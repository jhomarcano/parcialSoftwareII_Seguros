package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.DatosSeguro;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.ModificarSeguroUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.salida.SeguroRepository;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroValidacionException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;

/**
 * Servicio encargado de modificar seguros.
 *
 * <p>
 * El numero y el tipo del seguro existente se conservan. Los nuevos
 * datos se validan y se arma un seguro nuevo antes de tocar la
 * persistencia, de modo que un dato invalido nunca altera el original.
 * </p>
 *
 * @author Alejandra Cano 
 */
public class ModificarSeguroServicio implements ModificarSeguroUseCase {

    private final SeguroRepository seguroRepository;

    /**
     * Construye el servicio.
     *
     * @param seguroRepository repositorio utilizado por el servicio
     */
    public ModificarSeguroServicio(SeguroRepository seguroRepository) {
        this.seguroRepository = seguroRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Seguro modificar(Seguro existente, DatosSeguro datos) {

        if (existente == null) {
            throw new SeguroValidacionException(
                    "Debe seleccionar un seguro.");
        }

        DatosSeguro conIdentidad = datos == null
                ? null
                : datos.conIdentidad(existente.getNumero(), existente.getTipo());

        Seguro actualizado = ConstructorSeguro.construir(conIdentidad);

        return seguroRepository.modificar(actualizado);
    }
}
