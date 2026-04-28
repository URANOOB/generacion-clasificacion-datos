package co.edu.poligran.proyecto;

/**
 * Esta clase representa un producto del proyecto.
 *
 * Un producto en este trabajo tiene tres datos basicos:
 * id, nombre y precio. Con esos datos se puede escribir el archivo
 * de productos y tambien se puede calcular el dinero de las ventas.
 */
public class Product {

    /**
     * Identificador del producto.
     * Ejemplo: P001.
     */
    private String id;

    /**
     * Nombre del producto.
     */
    private String name;

    /**
     * Precio del producto por unidad.
     */
    private double price;

    /**
     * Constructor de la clase Product.
     *
     * id identificador del producto
     * name nombre del producto
     * price precio por unidad
     */
    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    /**
     * Devuelve el id del producto.
     *
     * return id del producto
     */
    public String getId() {
        return id;
    }

    /**
     * Devuelve el nombre del producto.
     *
     * return nombre del producto
     */
    public String getName() {
        return name;
    }

    /**
     * Devuelve el precio del producto.
     *
     * return precio del producto
     */
    public double getPrice() {
        return price;
    }
}
