package co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion;

/**
 * Excepcion lanzada cuando un dato del seguro
 * no cumple las reglas de validacion.
 *
 * @author Alejandra Cano 
 */
public class SeguroValidacionException extends SeguroException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye la excepcion.
     *
     * @param mensaje regla que no fue cumplida
     */
    public SeguroValidacionException(String mensaje) {
        super(mensaje);
    }
}
