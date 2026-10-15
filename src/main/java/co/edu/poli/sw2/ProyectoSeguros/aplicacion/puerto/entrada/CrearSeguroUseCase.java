package co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada;

import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;

/**
 * Puerto de entrada encargado de crear seguros.
 *
 * @author Alejandra Cano y Juan Rosero
 */
public interface CrearSeguroUseCase {

    /**
     * Crea un nuevo seguro de vida o de vehiculo segun el tipo indicado.
     *
     * @param datos datos capturados del seguro
     * @return seguro creado
     */
    Seguro crear(DatosSeguro datos);
}
