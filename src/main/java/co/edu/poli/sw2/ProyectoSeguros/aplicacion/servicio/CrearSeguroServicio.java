package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.CrearSeguroUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.DatosSeguro;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.salida.SeguroRepository;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;

/**
 * Servicio de aplicacion encargado de crear seguros
 * de vida y de vehiculo.
 *
 * @author Alejandra Cano 
 */
public class CrearSeguroServicio implements CrearSeguroUseCase {

    private final SeguroRepository seguroRepository;

    /**
     * Construye el servicio.
     *
     * @param seguroRepository puerto de salida para persistencia
     */
    public CrearSeguroServicio(SeguroRepository seguroRepository) {
        this.seguroRepository = seguroRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Seguro crear(DatosSeguro datos) {

        Seguro seguro = ConstructorSeguro.construir(datos);

        return seguroRepository.crear(seguro);
    }
}
