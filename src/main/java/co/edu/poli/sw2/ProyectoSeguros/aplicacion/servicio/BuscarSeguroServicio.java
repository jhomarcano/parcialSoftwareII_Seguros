package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.BuscarSeguroUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.salida.SeguroRepository;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroValidacionException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;

/**
 * Servicio encargado de buscar seguros.
 *
 * @author Alejandra Cano 
 */
public class BuscarSeguroServicio implements BuscarSeguroUseCase {

    private final SeguroRepository seguroRepository;

    /**
     * Construye el servicio.
     *
     * @param seguroRepository repositorio utilizado
     */
    public BuscarSeguroServicio(SeguroRepository seguroRepository) {
        this.seguroRepository = seguroRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Seguro buscarPorNumero(String numero) {

        if (numero == null || numero.isBlank()) {
            throw new SeguroValidacionException(
                    "Ingrese el numero del seguro que desea buscar.");
        }

        return seguroRepository.buscarPorNumero(numero.trim());
    }
}
