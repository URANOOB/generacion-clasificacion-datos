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
 * Genera archivos planos pseudoaleatorios que sirven como entrada
 * para el proyecto principal.
 */
public class GenerateInfoFiles {

    private static final String OUTPUT_FOLDER = "files";
    private static final String PRODUCTS_FILE = OUTPUT_FOLDER + File.separator + "products.txt";
    private static final String SALESMEN_FILE = OUTPUT_FOLDER + File.separator + "salesmen_info.txt";

    private static final Random RANDOM = new Random();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private static final List<Product> generatedProducts = new ArrayList<Product>();
    private static final List<Salesman> generatedSalesmen = new ArrayList<Salesman>();

    public static void main(String[] args) {
        try {
            createOutputFolder();
            deletePreviousSalesFiles();
            createProductsFile(10);
            createSalesManInfoFile(5);

            for (Salesman salesman : generatedSalesmen) {
                createSalesMenFile(
                    5 + RANDOM.nextInt(6),
                    salesman.getFirstName(),
                    salesman.getDocumentNumber()
                );
            }

            System.out.println("Archivos generados correctamente en la carpeta: " + OUTPUT_FOLDER);
        } catch (Exception e) {
            System.out.println("Ocurrió un error al generar los archivos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea la carpeta de salida si no existe.
     */
    private static void createOutputFolder() {
        File folder = new File(OUTPUT_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Elimina archivos de ventas generados en ejecuciones anteriores.
     */
    private static void deletePreviousSalesFiles() {
        File folder = new File(OUTPUT_FOLDER);
        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().startsWith("sales_") && file.getName().endsWith(".txt")) {
                file.delete();
            }
        }
    }

    /**
     * Crea un archivo con información pseudoaleatoria de productos.
     *
     * @param productsCount cantidad de productos a generar
     * @throws IOException si ocurre un error de escritura
     */
    public static void createProductsFile(int productsCount) throws IOException {
        generatedProducts.clear();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            for (int i = 1; i <= productsCount; i++) {
                String productId = "P" + String.format("%03d", i);
                String productName = DataRepository.PRODUCT_NAMES[(i - 1) % DataRepository.PRODUCT_NAMES.length];
                double price = 10000 + (RANDOM.nextInt(190001));

                Product product = new Product(productId, productName, price);
                generatedProducts.add(product);

                writer.write(product.getId() + ";" + product.getName() + ";" + DECIMAL_FORMAT.format(product.getPrice()));
                writer.newLine();
            }
        }
    }

    /**
     * Crea un archivo con información pseudoaleatoria de vendedores.
     *
     * @param salesmanCount cantidad de vendedores a generar
     * @throws IOException si ocurre un error de escritura
     */
    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        generatedSalesmen.clear();

        Set<Long> usedIds = new HashSet<Long>();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALESMEN_FILE))) {
            for (int i = 0; i < salesmanCount; i++) {
                String documentType = DataRepository.DOCUMENT_TYPES[RANDOM.nextInt(DataRepository.DOCUMENT_TYPES.length)];
                long documentNumber = generateUniqueDocumentNumber(usedIds);
                String firstName = DataRepository.FIRST_NAMES[RANDOM.nextInt(DataRepository.FIRST_NAMES.length)];
                String lastName = DataRepository.LAST_NAMES[RANDOM.nextInt(DataRepository.LAST_NAMES.length)];

                Salesman salesman = new Salesman(documentType, documentNumber, firstName, lastName);
                generatedSalesmen.add(salesman);

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
     * Crea un archivo pseudoaleatorio de ventas de un vendedor.
     *
     * @param randomSalesCount cantidad de ventas aleatorias
     * @param name nombre del vendedor
     * @param id número de documento del vendedor
     * @throws IOException si ocurre un error de escritura
     */
    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws IOException {
        String fileName = OUTPUT_FOLDER + File.separator + "sales_" + name + "_" + id + ".txt";

        Salesman salesman = findSalesmanById(id);
        if (salesman == null) {
            throw new IOException("No existe vendedor con id: " + id);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(salesman.getDocumentType() + ";" + salesman.getDocumentNumber());
            writer.newLine();

            for (int i = 0; i < randomSalesCount; i++) {
                Product product = generatedProducts.get(RANDOM.nextInt(generatedProducts.size()));
                int quantity = 1 + RANDOM.nextInt(20);

                writer.write(product.getId() + ";" + quantity + ";");
                writer.newLine();
            }
        }
    }

    /**
     * Busca un vendedor por su número de documento.
     *
     * @param id número de documento
     * @return vendedor encontrado o null
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
     * Genera un número de documento único.
     *
     * @param usedIds conjunto de ids ya usados
     * @return número único
     */
    private static long generateUniqueDocumentNumber(Set<Long> usedIds) {
        long value;
        do {
            value = 10000000L + RANDOM.nextInt(90000000);
        } while (usedIds.contains(value));

        usedIds.add(value);
        return value;
    }
}
