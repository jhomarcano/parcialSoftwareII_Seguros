package co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion;

/**
 * Excepcion lanzada cuando el seguro solicitado
 * no existe en persistencia.
 *
 * @author Alejandra Cano 
 */
public class SeguroNoEncontradoException extends SeguroException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye la excepcion.
     *
     * @param numero numero buscado
     */
    public SeguroNoEncontradoException(String numero) {
        super("No se encontro un seguro con el numero '" + numero + "'.");
    }
}
