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
 * Procesa los archivos planos del proyecto y genera reportes CSV.
 */
public class main {

    private static final String INPUT_FOLDER = "files";
    private static final String PRODUCTS_FILE = INPUT_FOLDER + File.separator + "products.txt";
    private static final String SALESMEN_FILE = INPUT_FOLDER + File.separator + "salesmen_info.txt";
    private static final String SALESMEN_REPORT_FILE = INPUT_FOLDER + File.separator + "salesmen_report.csv";
    private static final String PRODUCTS_REPORT_FILE = INPUT_FOLDER + File.separator + "products_report.csv";

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat(
        "0.00",
        DecimalFormatSymbols.getInstance(Locale.US)
    );

    /**
     * Punto de entrada para crear los reportes solicitados.
     *
     * @param args argumentos de consola no usados
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
     * Carga los productos disponibles desde el archivo plano.
     *
     * @return productos organizados por identificador
     * @throws IOException si el archivo no existe o tiene formato invalido
     */
    private static Map<String, Product> loadProducts() throws IOException {
        Map<String, Product> productsById = new HashMap<String, Product>();

        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length != 3) {
                    throw new IOException("Formato invalido en " + PRODUCTS_FILE + ", linea " + lineNumber);
                }

                String id = parts[0].trim();
                String name = parts[1].trim();
                double price = parseMoney(parts[2].trim());

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
     * Carga los vendedores desde el archivo plano.
     *
     * @return vendedores organizados por numero de documento
     * @throws IOException si el archivo no existe o tiene formato invalido
     */
    private static Map<Long, Salesman> loadSalesmen() throws IOException {
        Map<Long, Salesman> salesmenByDocument = new HashMap<Long, Salesman>();

        try (BufferedReader reader = new BufferedReader(new FileReader(SALESMEN_FILE))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length != 4) {
                    throw new IOException("Formato invalido en " + SALESMEN_FILE + ", linea " + lineNumber);
                }

                String documentType = parts[0].trim();
                long documentNumber = parseLong(parts[1].trim(), SALESMEN_FILE, lineNumber);
                String firstName = parts[2].trim();
                String lastName = parts[3].trim();

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
     * Construye los reportes base de vendedores con ventas en cero.
     *
     * @param salesmenByDocument vendedores cargados
     * @return lista de reportes por vendedor
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
     * Organiza los reportes de vendedores por numero de documento.
     *
     * @param reports reportes existentes
     * @return reportes indexados por numero de documento
     */
    private static Map<Long, SalesmanSalesReport> mapSalesmanReports(List<SalesmanSalesReport> reports) {
        Map<Long, SalesmanSalesReport> reportsByDocument = new HashMap<Long, SalesmanSalesReport>();

        for (SalesmanSalesReport report : reports) {
            reportsByDocument.put(report.getSalesman().getDocumentNumber(), report);
        }

        return reportsByDocument;
    }

    /**
     * Construye los reportes base de productos con cantidad vendida en cero.
     *
     * @param productsById productos cargados
     * @return reportes indexados por identificador de producto
     */
    private static Map<String, ProductSalesReport> createInitialProductReports(Map<String, Product> productsById) {
        Map<String, ProductSalesReport> reportsById = new HashMap<String, ProductSalesReport>();

        for (Product product : productsById.values()) {
            reportsById.put(product.getId(), new ProductSalesReport(product));
        }

        return reportsById;
    }

    /**
     * Lee todos los archivos de venta y acumula totales.
     *
     * @param productsById productos disponibles
     * @param salesmenByDocument vendedores disponibles
     * @param salesmanReportsByDocument reportes de vendedores
     * @param productReportsById reportes de productos
     * @throws IOException si falta informacion o existe formato invalido
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
            if (isSalesFile(file)) {
                processSalesFile(file, productsById, salesmenByDocument, salesmanReportsByDocument, productReportsById);
            }
        }
    }

    /**
     * Procesa un archivo de ventas de un vendedor.
     *
     * @param file archivo de ventas
     * @param productsById productos disponibles
     * @param salesmenByDocument vendedores disponibles
     * @param salesmanReportsByDocument reportes de vendedores
     * @param productReportsById reportes de productos
     * @throws IOException si el archivo tiene datos invalidos
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
            if (header == null || header.trim().isEmpty()) {
                throw new IOException("Archivo de ventas vacio: " + file.getName());
            }

            String[] headerParts = header.split(";");
            if (headerParts.length != 2) {
                throw new IOException("Encabezado invalido en " + file.getName());
            }

            long documentNumber = parseLong(headerParts[1].trim(), file.getName(), 1);
            Salesman salesman = salesmenByDocument.get(documentNumber);
            if (salesman == null) {
                throw new IOException("Vendedor no registrado en " + SALESMEN_FILE + ": " + documentNumber);
            }

            SalesmanSalesReport salesmanReport = salesmanReportsByDocument.get(documentNumber);
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

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

                salesmanReport.addSale(product.getPrice() * quantity);
                productReportsById.get(productId).addQuantity(quantity);
            }
        }
    }

    /**
     * Escribe el reporte de vendedores ordenado de mayor a menor recaudo.
     *
     * @param reports reportes de vendedores
     * @throws IOException si ocurre un error de escritura
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
                writer.write(
                    salesman.getFirstName() + " " + salesman.getLastName() + ";" +
                    MONEY_FORMAT.format(report.getTotalSales())
                );
                writer.newLine();
            }
        }
    }

    /**
     * Escribe el reporte de productos vendidos ordenado por cantidad.
     *
     * @param reportsById reportes de productos
     * @throws IOException si ocurre un error de escritura
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
     * Indica si un archivo corresponde a ventas de un vendedor.
     *
     * @param file archivo a validar
     * @return true si es archivo de ventas
     */
    private static boolean isSalesFile(File file) {
        return file.isFile() && file.getName().startsWith("sales_") && file.getName().endsWith(".txt");
    }

    /**
     * Convierte un valor monetario aceptando coma o punto decimal.
     *
     * @param value valor textual
     * @return valor numerico
     * @throws IOException si el valor no es numerico
     */
    private static double parseMoney(String value) throws IOException {
        try {
            return Double.parseDouble(value.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IOException("Valor monetario invalido: " + value, e);
        }
    }

    /**
     * Convierte un valor textual a entero.
     *
     * @param value valor textual
     * @param fileName nombre del archivo
     * @param lineNumber numero de linea
     * @return valor entero
     * @throws IOException si el valor no es entero
     */
    private static int parseInt(String value, String fileName, int lineNumber) throws IOException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IOException("Numero invalido en " + fileName + ", linea " + lineNumber + ": " + value, e);
        }
    }

    /**
     * Convierte un valor textual a numero largo.
     *
     * @param value valor textual
     * @param fileName nombre del archivo
     * @param lineNumber numero de linea
     * @return valor largo
     * @throws IOException si el valor no es numerico
     */
    private static long parseLong(String value, String fileName, int lineNumber) throws IOException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IOException("Numero invalido en " + fileName + ", linea " + lineNumber + ": " + value, e);
        }
    }

    /**
     * Reporte acumulado de ventas por vendedor.
     */
    private static class SalesmanSalesReport {

        private Salesman salesman;
        private double totalSales;

        SalesmanSalesReport(Salesman salesman) {
            this.salesman = salesman;
            this.totalSales = 0;
        }

        Salesman getSalesman() {
            return salesman;
        }

        double getTotalSales() {
            return totalSales;
        }

        void addSale(double saleValue) {
            totalSales += saleValue;
        }
    }

    /**
     * Reporte acumulado de cantidad vendida por producto.
     */
    private static class ProductSalesReport {

        private Product product;
        private int quantitySold;

        ProductSalesReport(Product product) {
            this.product = product;
            this.quantitySold = 0;
        }

        Product getProduct() {
            return product;
        }

        int getQuantitySold() {
            return quantitySold;
        }

        void addQuantity(int quantity) {
            quantitySold += quantity;
        }
    }
}
