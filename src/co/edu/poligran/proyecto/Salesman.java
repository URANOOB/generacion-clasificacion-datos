package co.edu.poligran.proyecto;

/**
 * Esta clase representa un vendedor.
 *
 * Aqui se guardan los datos personales basicos que necesita el proyecto
 * para identificar a cada vendedor dentro de los archivos de entrada
 * y tambien dentro de los reportes.
 */
public class Salesman {

    /**
     * Tipo de documento del vendedor.
     */
    private String documentType;

    /**
     * Numero de documento del vendedor.
     */
    private long documentNumber;

    /**
     * Nombre del vendedor.
     */
    private String firstName;

    /**
     * Apellido del vendedor.
     */
    private String lastName;

    /**
     * Constructor de la clase Salesman.
     *
     * documentType tipo de documento
     * documentNumber numero de documento
     * firstName nombre del vendedor
     * lastName apellido del vendedor
     */
    public Salesman(String documentType, long documentNumber, String firstName, String lastName) {
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Devuelve el tipo de documento del vendedor.
     *
     * return tipo de documento
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Devuelve el numero de documento del vendedor.
     *
     * return numero de documento
     */
    public long getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Devuelve el nombre del vendedor.
     *
     * return nombre del vendedor
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Devuelve el apellido del vendedor.
     *
     * return apellido del vendedor
     */
    public String getLastName() {
        return lastName;
    }
}
