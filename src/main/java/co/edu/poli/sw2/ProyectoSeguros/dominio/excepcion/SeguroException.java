package co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion;

/**
 * Excepcion base utilizada por el dominio de seguros.
 *
 * @author Alejandra Cano y Juan Rosero
 */
public class SeguroException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una excepcion con mensaje.
     *
     * @param mensaje descripcion del error
     */
    public SeguroException(String mensaje) {
        super(mensaje);
    }

    /**
     * Construye una excepcion con mensaje y causa.
     *
     * @param mensaje descripcion del error
     * @param causa   causa original
     */
    public SeguroException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
