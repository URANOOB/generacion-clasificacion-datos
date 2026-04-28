package co.edu.poligran.proyecto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Esta clase se encarga de generar los archivos de entrada del proyecto.
 *
 * Su trabajo principal es crear informacion de prueba para que luego
 * la otra clase principal del proyecto pueda leerla y generar reportes.
 *
 * Los archivos que genera son:
 * - products.txt
 * - salesmen_info.txt
 * - un archivo sales_*.txt por cada vendedor
 */
public class GenerateInfoFiles {

    /**
     * Nombre de la carpeta donde se guardan los archivos generados.
     */
    private static final String OUTPUT_FOLDER = "files";

    /**
     * Ruta completa del archivo de productos.
     */
    private static final String PRODUCTS_FILE = OUTPUT_FOLDER + File.separator + "products.txt";

    /**
     * Ruta completa del archivo de vendedores.
     */
    private static final String SALESMEN_FILE = OUTPUT_FOLDER + File.separator + "salesmen_info.txt";

    /**
     * Objeto que se usa para generar numeros aleatorios.
     */
    private static final Random RANDOM = new Random();

    /**
     * Formato usado para escribir precios con dos decimales.
     */
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    /**
     * Lista donde se guardan los productos que se van creando.
     *
     * Se usa despues cuando toca generar ventas aleatorias.
     */
    private static final List<Product> generatedProducts = new ArrayList<Product>();

    /**
     * Lista donde se guardan los vendedores generados.
     *
     * Se usa despues para crear un archivo de ventas por cada vendedor.
     */
    private static final List<Salesman> generatedSalesmen = new ArrayList<Salesman>();

    /**
     * Metodo principal de la clase.
     *
     * Aqui se ejecuta toda la secuencia de generacion:
     * 1. Crear carpeta files si no existe.
     * 2. Borrar archivos viejos de ventas.
     * 3. Crear el archivo de productos.
     * 4. Crear el archivo de vendedores.
     * 5. Crear los archivos de ventas.
     *
     */
    public static void main(String[] args) {
        try {
            // Se asegura que exista la carpeta donde se guardan los archivos.
            createOutputFolder();

            // Se eliminan ventas anteriores para no mezclar resultados.
            deletePreviousSalesFiles();

            // Se crean los archivos principales del proyecto.
            createProductsFile(10);
            createSalesManInfoFile(5);

            // Se crea un archivo de ventas para cada vendedor.
            for (Salesman salesman : generatedSalesmen) {
                createSalesMenFile(
                    5 + RANDOM.nextInt(6),
                    salesman.getFirstName(),
                    salesman.getDocumentNumber()
                );
            }

            System.out.println("Archivos generados correctamente en la carpeta: " + OUTPUT_FOLDER);
        } catch (Exception e) {
            System.out.println("Ocurrio un error al generar los archivos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea la carpeta files si todavia no existe.
     */
    private static void createOutputFolder() {
        File folder = new File(OUTPUT_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Elimina archivos de ventas de ejecuciones anteriores.
     *
     * Esto se hace para evitar que queden archivos viejos que ya no
     * correspondan con los nuevos vendedores generados.
     */
    private static void deletePreviousSalesFiles() {
        File folder = new File(OUTPUT_FOLDER);
        File[] files = folder.listFiles();

        // Si no se puede leer la carpeta, simplemente se termina el metodo.
        if (files == null) {
            return;
        }

        for (File file : files) {
            // Solo se borran archivos de ventas.
            if (file.isFile() && file.getName().startsWith("sales_") && file.getName().endsWith(".txt")) {
                file.delete();
            }
        }
    }

    /**
     * Crea el archivo de productos con datos pseudoaleatorios.
     *
     * Cada linea del archivo queda con este formato:
     * IDProducto;NombreProducto;Precio
     *
     * productsCount cantidad de productos que se desean generar
     * IOException si ocurre un error al escribir el archivo
     */
    public static void createProductsFile(int productsCount) throws IOException {
        // Se limpia la lista por si el metodo ya se habia usado antes.
        generatedProducts.clear();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            for (int i = 1; i <= productsCount; i++) {
                // El id se crea con formato P001, P002, P003...
                String productId = "P" + String.format("%03d", i);

                // El nombre sale del repositorio de datos.
                String productName = DataRepository.PRODUCT_NAMES[(i - 1) % DataRepository.PRODUCT_NAMES.length];

                // El precio se genera en un rango aleatorio.
                double price = 10000 + (RANDOM.nextInt(190001));

                Product product = new Product(productId, productName, price);
                generatedProducts.add(product);

                // Se escribe el producto en una linea del archivo.
                writer.write(product.getId() + ";" + product.getName() + ";" + DECIMAL_FORMAT.format(product.getPrice()));
                writer.newLine();
            }
        }
    }

    /**
     * Crea el archivo de vendedores con datos pseudoaleatorios.
     *
     * Cada linea del archivo queda con este formato:
     * TipoDocumento;NumeroDocumento;Nombre;Apellido
     *
     * salesmanCount cantidad de vendedores que se desean generar
     * IOException si ocurre un error al escribir el archivo
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        // Se limpia la lista por si ya existian vendedores guardados.
        generatedSalesmen.clear();

        // Este conjunto ayuda a que los documentos no se repitan.
        Set<Long> usedIds = new HashSet<Long>();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALESMEN_FILE))) {
            for (int i = 0; i < salesmanCount; i++) {
                // Se seleccionan datos aleatorios desde los arreglos base.
                String documentType = DataRepository.DOCUMENT_TYPES[RANDOM.nextInt(DataRepository.DOCUMENT_TYPES.length)];
                long documentNumber = generateUniqueDocumentNumber(usedIds);
                String firstName = DataRepository.FIRST_NAMES[RANDOM.nextInt(DataRepository.FIRST_NAMES.length)];
                String lastName = DataRepository.LAST_NAMES[RANDOM.nextInt(DataRepository.LAST_NAMES.length)];

                Salesman salesman = new Salesman(documentType, documentNumber, firstName, lastName);
                generatedSalesmen.add(salesman);

                // Se escribe una linea por vendedor.
                writer.write(
                    salesman.getDocumentType() + ";" +
                    salesman.getDocumentNumber() + ";" +
                    salesman.getFirstName() + ";" +
                    salesman.getLastName()
                );
                writer.newLine();
            }
        }
    }

    /**
     * Crea un archivo de ventas para un vendedor especifico.
     *
     * La primera linea guarda la identificacion del vendedor.
     * Las siguientes lineas guardan ventas aleatorias con este formato:
     * IDProducto;CantidadVendida;
     *
     * randomSalesCount cantidad de ventas que se quieren generar
     * name nombre del vendedor
     * id numero de documento del vendedor
     * IOException si ocurre un error de escritura o si el vendedor no existe
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws IOException {
        String fileName = OUTPUT_FOLDER + File.separator + "sales_" + name + "_" + id + ".txt";

        Salesman salesman = findSalesmanById(id);
        if (salesman == null) {
            throw new IOException("No existe vendedor con id: " + id);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // La primera linea identifica a quien pertenece el archivo.
            writer.write(salesman.getDocumentType() + ";" + salesman.getDocumentNumber());
            writer.newLine();

            for (int i = 0; i < randomSalesCount; i++) {
                // Se escoge un producto aleatorio de la lista ya generada.
                Product product = generatedProducts.get(RANDOM.nextInt(generatedProducts.size()));

                // La cantidad se genera entre 1 y 20.
                int quantity = 1 + RANDOM.nextInt(20);

                // Cada linea representa una venta.
                writer.write(product.getId() + ";" + quantity + ";");
                writer.newLine();
            }
        }
    }

    /**
     * Busca un vendedor en la lista de vendedores generados.
     *
     * id numero de documento del vendedor
     * vendedor encontrado o null si no existe
     */
    private static Salesman findSalesmanById(long id) {
        for (Salesman salesman : generatedSalesmen) {
            if (salesman.getDocumentNumber() == id) {
                return salesman;
            }
        }
        return null;
    }

    /**
     * Genera un numero de documento que no se repita.
     *
     * usedIds conjunto de documentos ya usados
     * return numero de documento unico
     */
    private static long generateUniqueDocumentNumber(Set<Long> usedIds) {
        long value;

        // Se sigue intentando mientras el numero ya exista.
        do {
            value = 10000000L + RANDOM.nextInt(90000000);
        } while (usedIds.contains(value));

        // Cuando el numero ya es valido, se marca como usado.
        usedIds.add(value);
        return value;
    }
}
