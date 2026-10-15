package co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion;

/**
 * Excepcion lanzada cuando ya existe un seguro
 * con el mismo numero.
 *
 * @author Alejandra Cano 
 */
public class SeguroDuplicadoException extends SeguroException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye la excepcion.
     *
     * @param numero numero duplicado
     */
    public SeguroDuplicadoException(String numero) {
        super("Ya existe un seguro registrado con el numero '" + numero + "'.");
    }
}
