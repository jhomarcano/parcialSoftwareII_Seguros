package co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada;

import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;

/**
 * Puerto de entrada encargado de buscar un seguro.
 *
 * @author Alejandra Cano 
 */
public interface BuscarSeguroUseCase {

    /**
     * Busca un seguro utilizando su numero.
     *
     * @param numero numero del seguro
     * @return seguro encontrado
     */
    Seguro buscarPorNumero(String numero);
}
