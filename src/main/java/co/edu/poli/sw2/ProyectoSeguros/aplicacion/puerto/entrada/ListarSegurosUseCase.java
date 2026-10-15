package co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada;

import java.util.List;

import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Asegurado;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;

/**
 * Puerto de entrada encargado de listar seguros.
 *
 * @author Alejandra Cano 
 */
public interface ListarSegurosUseCase {

    /**
     * Obtiene todos los seguros registrados.
     *
     * @return lista de seguros
     */
    List<Seguro> listar();

    /**
     * Obtiene los asegurados predeterminados, necesarios para elegir
     * el titular al crear o modificar un seguro.
     *
     * @return lista de asegurados
     */
    List<Asegurado> listarAsegurados();
}
