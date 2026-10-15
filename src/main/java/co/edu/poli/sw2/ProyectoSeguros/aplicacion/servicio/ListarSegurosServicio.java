package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import java.util.List;

import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.ListarSegurosUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.salida.SeguroRepository;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Asegurado;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;

/**
 * Servicio encargado de listar seguros y asegurados.
 *
 * @author Alejandra Cano 
 */
public class ListarSegurosServicio implements ListarSegurosUseCase {

    private final SeguroRepository seguroRepository;

    /**
     * Construye el servicio.
     *
     * @param seguroRepository repositorio utilizado
     */
    public ListarSegurosServicio(SeguroRepository seguroRepository) {
        this.seguroRepository = seguroRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Seguro> listar() {
        return seguroRepository.listar();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Asegurado> listarAsegurados() {
        return seguroRepository.listarAsegurados();
    }
}
