package co.edu.poligran.proyecto;

/**
 * Esta clase guarda datos base que se usan para crear la informacion
 * de prueba del proyecto.
 *
 * La idea de esta clase es dejar en un solo lugar las listas de nombres,
 * apellidos, tipos de documento y productos. De esa manera el codigo
 * de generacion de archivos no queda lleno de arreglos grandes y es mas
 * facil entender de donde sale cada dato.
 */
public class DataRepository {

    /**
     * Lista de nombres que se usan para generar vendedores de prueba.
     */
    public static final String[] FIRST_NAMES = {
        "Carlos", "Laura", "Andres", "Valentina", "Camila",
        "Juan", "Sofia", "Mateo", "Daniela", "Sebastian",
        "Paula", "Felipe", "Natalia", "Julian", "Mariana"
    };

    /**
     * Lista de apellidos que se usan para generar vendedores de prueba.
     */
    public static final String[] LAST_NAMES = {
        "Gomez", "Rodriguez", "Perez", "Martinez", "Lopez",
        "Garcia", "Hernandez", "Ramirez", "Torres", "Castro",
        "Vargas", "Morales", "Rojas", "Diaz", "Suarez"
    };

    /**
     * Tipos de documento permitidos dentro de la informacion generada.
     */
    public static final String[] DOCUMENT_TYPES = {
        "CC", "CE", "TI"
    };

    /**
     * Nombres de productos que se usan para crear el archivo products.txt.
     */
    public static final String[] PRODUCT_NAMES = {
        "Teclado", "Mouse", "Monitor", "Portatil", "Impresora",
        "Audifonos", "Tablet", "Celular", "DiscoSSD", "MemoriaUSB",
        "CamaraWeb", "Microfono", "Router", "SillaOficina", "BasePortatil"
    };
}
