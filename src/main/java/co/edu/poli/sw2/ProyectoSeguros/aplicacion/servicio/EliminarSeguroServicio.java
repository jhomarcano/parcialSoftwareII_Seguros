package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.EliminarSeguroUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.salida.SeguroRepository;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroValidacionException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;

/**
 * Servicio encargado de eliminar un seguro.
 *
 * @author Alejandra Cano 
 */
public class EliminarSeguroServicio implements EliminarSeguroUseCase {

    private final SeguroRepository seguroRepository;

    /**
     * Construye el servicio.
     *
     * @param seguroRepository repositorio utilizado
     */
    public EliminarSeguroServicio(SeguroRepository seguroRepository) {
        this.seguroRepository = seguroRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eliminar(Seguro seguro) {

        if (seguro == null) {
            throw new SeguroValidacionException(
                    "Debe seleccionar un seguro para eliminar.");
        }

        seguroRepository.eliminar(seguro);
    }
}
