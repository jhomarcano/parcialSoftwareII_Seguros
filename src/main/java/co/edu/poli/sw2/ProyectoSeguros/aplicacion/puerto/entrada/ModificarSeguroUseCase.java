package co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada;

import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;

/**
 * Puerto de entrada encargado de modificar un seguro.
 *
 * @author Alejandra Cano 
 */
public interface ModificarSeguroUseCase {

    /**
     * Modifica un seguro existente. El numero y el tipo no cambian.
     *
     * @param existente seguro que se desea modificar
     * @param datos     nuevos datos capturados
     * @return seguro modificado
     */
    Seguro modificar(Seguro existente, DatosSeguro datos);
}
