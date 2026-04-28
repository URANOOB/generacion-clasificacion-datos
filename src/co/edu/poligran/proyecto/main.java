package co.edu.poligran.proyecto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Esta clase se encarga de leer los archivos del proyecto y generar
 * los reportes finales.
 *
 * En esta parte ya no se crean datos aleatorios. Aqui lo que se hace es:
 * - leer productos
 * - leer vendedores
 * - leer ventas
 * - calcular resultados
 * - escribir reportes
 */
public class main {

    /**
     * Carpeta donde se encuentran los archivos de trabajo.
     */
    private static final String INPUT_FOLDER = "files";

    /**
     * Ruta del archivo de productos.
     */
    private static final String PRODUCTS_FILE = INPUT_FOLDER + File.separator + "products.txt";

    /**
     * Ruta del archivo de vendedores.
     */
    private static final String SALESMEN_FILE = INPUT_FOLDER + File.separator + "salesmen_info.txt";

    /**
     * Ruta del archivo de salida del reporte de vendedores.
     */
    private static final String SALESMEN_REPORT_FILE = INPUT_FOLDER + File.separator + "salesmen_report.csv";

    /**
     * Ruta del archivo de salida del reporte de productos.
     */
    private static final String PRODUCTS_REPORT_FILE = INPUT_FOLDER + File.separator + "products_report.csv";

    /**
     * Formato usado para escribir dinero con dos decimales.
     */
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat(
        "0.00",
        DecimalFormatSymbols.getInstance(Locale.US)
    );

    /**
     * Metodo principal de esta clase.
     *
     * El flujo general de este metodo es:
     * 1. Leer productos.
     * 2. Leer vendedores.
     * 3. Preparar estructuras de apoyo.
     * 4. Procesar archivos de ventas.
     * 5. Crear reportes finales.
     *
     * args argumentos de consola no usados
     */
    public static void main(String[] args) {
        try {
            Map<String, Product> productsById = loadProducts();
            Map<Long, Salesman> salesmenByDocument = loadSalesmen();

            List<SalesmanSalesReport> salesmanReports = createInitialSalesmanReports(salesmenByDocument);
            Map<Long, SalesmanSalesReport> salesmanReportsByDocument = mapSalesmanReports(salesmanReports);
            Map<String, ProductSalesReport> productReportsById = createInitialProductReports(productsById);

            processSalesFiles(productsById, salesmenByDocument, salesmanReportsByDocument, productReportsById);
            writeSalesmenReport(salesmanReports);
            writeProductsReport(productReportsById);

            System.out.println("Reportes generados correctamente en la carpeta: " + INPUT_FOLDER);
        } catch (Exception e) {
            System.out.println("Ocurrio un error al generar los reportes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lee el archivo de productos y guarda la informacion en un mapa.
     *
     * La llave del mapa es el id del producto.
     * El valor es el objeto Product con todos sus datos.
     *
     * return mapa con los productos organizados por id
     * IOException si el archivo tiene errores o no se puede leer
     */
    private static Map<String, Product> loadProducts() throws IOException {
        Map<String, Product> productsById = new HashMap<String, Product>();

        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Si la linea viene vacia, no se procesa.
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Cada producto debe tener tres datos.
                String[] parts = line.split(";");
                if (parts.length != 3) {
                    throw new IOException("Formato invalido en " + PRODUCTS_FILE + ", linea " + lineNumber);
                }

                String id = parts[0].trim();
                String name = parts[1].trim();
                double price = parseMoney(parts[2].trim());

                // Se validan los campos importantes.
                if (id.isEmpty() || name.isEmpty() || price < 0) {
                    throw new IOException("Producto invalido en " + PRODUCTS_FILE + ", linea " + lineNumber);
                }

                productsById.put(id, new Product(id, name, price));
            }
        }

        if (productsById.isEmpty()) {
            throw new IOException("No se encontraron productos en " + PRODUCTS_FILE);
        }

        return productsById;
    }

    /**
     * Lee el archivo de vendedores y guarda la informacion en un mapa.
     *
     * La llave del mapa es el numero de documento.
     * El valor es el objeto Salesman con sus datos.
     *
     * return mapa con los vendedores organizados por documento
     * IOException si el archivo tiene errores o no se puede leer
     */
    private static Map<Long, Salesman> loadSalesmen() throws IOException {
        Map<Long, Salesman> salesmenByDocument = new HashMap<Long, Salesman>();

        try (BufferedReader reader = new BufferedReader(new FileReader(SALESMEN_FILE))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Si la linea esta vacia, se ignora.
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Cada vendedor debe tener cuatro datos.
                String[] parts = line.split(";");
                if (parts.length != 4) {
                    throw new IOException("Formato invalido en " + SALESMEN_FILE + ", linea " + lineNumber);
                }

                String documentType = parts[0].trim();
                long documentNumber = parseLong(parts[1].trim(), SALESMEN_FILE, lineNumber);
                String firstName = parts[2].trim();
                String lastName = parts[3].trim();

                // Se revisa que no falten datos importantes.
                if (documentType.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    throw new IOException("Vendedor invalido en " + SALESMEN_FILE + ", linea " + lineNumber);
                }

                salesmenByDocument.put(
                    documentNumber,
                    new Salesman(documentType, documentNumber, firstName, lastName)
                );
            }
        }

        if (salesmenByDocument.isEmpty()) {
            throw new IOException("No se encontraron vendedores en " + SALESMEN_FILE);
        }

        return salesmenByDocument;
    }

    /**
     * Crea una lista de reportes base para vendedores.
     *
     * Cada vendedor empieza con total vendido en cero.
     *
     * salesmenByDocument mapa de vendedores cargados
     * return lista inicial de reportes de vendedores
     */
    private static List<SalesmanSalesReport> createInitialSalesmanReports(
        Map<Long, Salesman> salesmenByDocument
    ) {
        List<SalesmanSalesReport> reports = new ArrayList<SalesmanSalesReport>();

        for (Salesman salesman : salesmenByDocument.values()) {
            reports.add(new SalesmanSalesReport(salesman));
        }

        return reports;
    }

    /**
     * Convierte la lista de reportes de vendedores en un mapa.
     *
     * Esto permite encontrar mas rapido el reporte correcto cuando
     * se esta procesando cada archivo de ventas.
     *
     * reports lista de reportes
     * return mapa de reportes por numero de documento
     */
    private static Map<Long, SalesmanSalesReport> mapSalesmanReports(List<SalesmanSalesReport> reports) {
        Map<Long, SalesmanSalesReport> reportsByDocument = new HashMap<Long, SalesmanSalesReport>();

        for (SalesmanSalesReport report : reports) {
            reportsByDocument.put(report.getSalesman().getDocumentNumber(), report);
        }

        return reportsByDocument;
    }

    /**
     * Crea una estructura inicial para los reportes de productos.
     *
     * Cada producto empieza con cantidad vendida en cero.
     *
     * productsById mapa de productos cargados
     * return mapa de reportes por id de producto
     */
    private static Map<String, ProductSalesReport> createInitialProductReports(Map<String, Product> productsById) {
        Map<String, ProductSalesReport> reportsById = new HashMap<String, ProductSalesReport>();

        for (Product product : productsById.values()) {
            reportsById.put(product.getId(), new ProductSalesReport(product));
        }

        return reportsById;
    }

    /**
     * Recorre la carpeta files y procesa todos los archivos de ventas.
     *
     * productsById productos disponibles
     * salesmenByDocument vendedores disponibles
     * salesmanReportsByDocument reportes de vendedores
     * productReportsById reportes de productos
     * IOException si ocurre algun problema al leer archivos
     */
    private static void processSalesFiles(
        Map<String, Product> productsById,
        Map<Long, Salesman> salesmenByDocument,
        Map<Long, SalesmanSalesReport> salesmanReportsByDocument,
        Map<String, ProductSalesReport> productReportsById
    ) throws IOException {
        File folder = new File(INPUT_FOLDER);
        File[] files = folder.listFiles();

        if (files == null) {
            throw new IOException("No se pudo leer la carpeta " + INPUT_FOLDER);
        }

        for (File file : files) {
            // Solo se toman en cuenta archivos de ventas.
            if (isSalesFile(file)) {
                processSalesFile(file, productsById, salesmenByDocument, salesmanReportsByDocument, productReportsById);
            }
        }
    }

    /**
     * Procesa un archivo individual de ventas.
     *
     * file archivo de ventas
     * productsById productos disponibles
     * salesmenByDocument vendedores disponibles
     * salesmanReportsByDocument reportes de vendedores
     * productReportsById reportes de productos
     * IOException si el archivo tiene datos invalidos
     */
    private static void processSalesFile(
        File file,
        Map<String, Product> productsById,
        Map<Long, Salesman> salesmenByDocument,
        Map<Long, SalesmanSalesReport> salesmanReportsByDocument,
        Map<String, ProductSalesReport> productReportsById
    ) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();

            // Si no hay primera linea, el archivo esta vacio.
            if (header == null || header.trim().isEmpty()) {
                throw new IOException("Archivo de ventas vacio: " + file.getName());
            }

            // La primera linea debe tener tipo y numero de documento.
            String[] headerParts = header.split(";");
            if (headerParts.length != 2) {
                throw new IOException("Encabezado invalido en " + file.getName());
            }

            long documentNumber = parseLong(headerParts[1].trim(), file.getName(), 1);
            Salesman salesman = salesmenByDocument.get(documentNumber);

            // Si el documento no aparece en salesmen_info.txt, hay inconsistencia.
            if (salesman == null) {
                throw new IOException("Vendedor no registrado en " + SALESMEN_FILE + ": " + documentNumber);
            }

            SalesmanSalesReport salesmanReport = salesmanReportsByDocument.get(documentNumber);
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Las lineas vacias se ignoran.
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Cada venta debe tener por lo menos producto y cantidad.
                String[] parts = line.split(";");
                if (parts.length < 2) {
                    throw new IOException("Venta invalida en " + file.getName() + ", linea " + lineNumber);
                }

                String productId = parts[0].trim();
                int quantity = parseInt(parts[1].trim(), file.getName(), lineNumber);
                Product product = productsById.get(productId);

                if (product == null) {
                    throw new IOException("Producto no registrado en " + file.getName() + ", linea " + lineNumber);
                }

                if (quantity < 0) {
                    throw new IOException("Cantidad negativa en " + file.getName() + ", linea " + lineNumber);
                }

                // Se suma el dinero vendido por el vendedor.
                salesmanReport.addSale(product.getPrice() * quantity);

                // Se suma la cantidad vendida del producto.
                productReportsById.get(productId).addQuantity(quantity);
            }
        }
    }

    /**
     * Ordena y escribe el reporte de vendedores.
     *
     * Primero se ordena por dinero recaudado de mayor a menor.
     * Si dos vendedores empatan, se usa el nombre completo para desempatar.
     *
     * reports lista de reportes de vendedores
     * IOException si ocurre un error al escribir el archivo
     */
    private static void writeSalesmenReport(List<SalesmanSalesReport> reports) throws IOException {
        Collections.sort(reports, new Comparator<SalesmanSalesReport>() {
            public int compare(SalesmanSalesReport first, SalesmanSalesReport second) {
                int totalSalesComparison = Double.compare(second.getTotalSales(), first.getTotalSales());
                if (totalSalesComparison != 0) {
                    return totalSalesComparison;
                }

                String firstFullName = first.getSalesman().getFirstName() + " " + first.getSalesman().getLastName();
                String secondFullName = second.getSalesman().getFirstName() + " " + second.getSalesman().getLastName();
                return firstFullName.compareTo(secondFullName);
            }
        });

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SALESMEN_REPORT_FILE))) {
            for (SalesmanSalesReport report : reports) {
                Salesman salesman = report.getSalesman();

                // Se escribe nombre completo y total recaudado.
                writer.write(
                    salesman.getFirstName() + " " + salesman.getLastName() + ";" +
                    MONEY_FORMAT.format(report.getTotalSales())
                );
                writer.newLine();
            }
        }
    }

    /**
     * Ordena y escribe el reporte de productos.
     *
     * Primero se ordena por cantidad vendida de mayor a menor.
     * Si dos productos empatan, se ordenan por nombre.
     *
     * reportsById mapa de reportes de productos
     * IOException si ocurre un error al escribir el archivo
     */
    private static void writeProductsReport(Map<String, ProductSalesReport> reportsById) throws IOException {
        List<ProductSalesReport> reports = new ArrayList<ProductSalesReport>(reportsById.values());
        Collections.sort(reports, new Comparator<ProductSalesReport>() {
            public int compare(ProductSalesReport first, ProductSalesReport second) {
                int quantityComparison = Integer.compare(second.getQuantitySold(), first.getQuantitySold());
                if (quantityComparison != 0) {
                    return quantityComparison;
                }

                return first.getProduct().getName().compareTo(second.getProduct().getName());
            }
        });

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_REPORT_FILE))) {
            for (ProductSalesReport report : reports) {
                // Solo se escriben productos que realmente tuvieron ventas.
                if (report.getQuantitySold() > 0) {
                    Product product = report.getProduct();

                    writer.write(
                        product.getName() + ";" +
                        MONEY_FORMAT.format(product.getPrice()) + ";" +
                        report.getQuantitySold()
                    );
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Revisa si un archivo corresponde al formato de ventas.
     *
     * file archivo a revisar
     * return true si el archivo parece ser de ventas
     */
    private static boolean isSalesFile(File file) {
        return file.isFile() && file.getName().startsWith("sales_") && file.getName().endsWith(".txt");
    }

    /**
     * Convierte un texto a numero decimal.
     *
     * Se acepta coma o punto como separador decimal.
     *
     * value valor en texto
     * return valor convertido a double
     * IOException si el texto no se puede convertir
     */
    private static double parseMoney(String value) throws IOException {
        try {
            return Double.parseDouble(value.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IOException("Valor monetario invalido: " + value, e);
        }
    }

    /**
     * Convierte un texto a entero.
     *
     * value valor en texto
     * fileName nombre del archivo donde se encontro el valor
     * lineNumber linea donde se encontro el valor
     * return numero entero convertido
     * IOException si el texto no es un numero entero valido
     */
    private static int parseInt(String value, String fileName, int lineNumber) throws IOException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IOException("Numero invalido en " + fileName + ", linea " + lineNumber + ": " + value, e);
        }
    }

    /**
     * Convierte un texto a numero largo.
     *
     * value valor en texto
     * fileName nombre del archivo donde se encontro el valor
     * lineNumber linea donde se encontro el valor
     * return numero largo convertido
     * IOException si el texto no es un numero valido
     */
    private static long parseLong(String value, String fileName, int lineNumber) throws IOException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IOException("Numero invalido en " + fileName + ", linea " + lineNumber + ": " + value, e);
        }
    }

    /**
     * Clase interna para guardar la informacion acumulada de ventas
     * por vendedor.
     */
    private static class SalesmanSalesReport {

        /**
         * Vendedor al que pertenece este reporte.
         */
        private Salesman salesman;

        /**
         * Total de dinero vendido por ese vendedor.
         */
        private double totalSales;

        /**
         * Constructor de la clase interna.
         *
         * salesman vendedor del reporte
         */
        SalesmanSalesReport(Salesman salesman) {
            this.salesman = salesman;
            this.totalSales = 0;
        }

        /**
         * Devuelve el vendedor asociado al reporte.
         *
         * return vendedor del reporte
         */
        Salesman getSalesman() {
            return salesman;
        }

        /**
         * Devuelve el total vendido.
         *
         * return total de ventas del vendedor
         */
        double getTotalSales() {
            return totalSales;
        }

        /**
         * Suma una venta al total acumulado.
         *
         * saleValue valor de la venta
         */
        void addSale(double saleValue) {
            totalSales += saleValue;
        }
    }

    /**
     * Clase interna para guardar la cantidad vendida de cada producto.
     */
    private static class ProductSalesReport {

        /**
         * Producto al que pertenece este reporte.
         */
        private Product product;

        /**
         * Cantidad total vendida del producto.
         */
        private int quantitySold;

        /**
         * Constructor de la clase interna.
         *
         * product producto del reporte
         */
        ProductSalesReport(Product product) {
            this.product = product;
            this.quantitySold = 0;
        }

        /**
         * Devuelve el producto asociado.
         *
         * return producto del reporte
         */
        Product getProduct() {
            return product;
        }

        /**
         * Devuelve la cantidad vendida.
         *
         * return cantidad total vendida
         */
        int getQuantitySold() {
            return quantitySold;
        }

        /**
         * Suma una cantidad al total acumulado.
         *
         * param quantity cantidad que se desea agregar
         */
        void addQuantity(int quantity) {
            quantitySold += quantity;
        }
    }
}
