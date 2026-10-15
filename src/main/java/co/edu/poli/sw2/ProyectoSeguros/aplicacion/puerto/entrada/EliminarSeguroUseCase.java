package co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada;

import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;

/**
 * Puerto de entrada encargado de eliminar seguros.
 *
 * @author Alejandra Cano 
 */
public interface EliminarSeguroUseCase {

    /**
     * Elimina un seguro.
     *
     * @param seguro seguro que se desea eliminar
     */
    void eliminar(Seguro seguro);
}
