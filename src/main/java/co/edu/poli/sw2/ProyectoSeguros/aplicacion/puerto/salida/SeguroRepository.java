package co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.salida;

import java.util.List;

import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Asegurado;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;

/**
 * Puerto de salida que define las operaciones necesarias
 * para almacenar y recuperar seguros.
 *
 * La aplicacion conoce esta interfaz, pero no conoce
 * PostgreSQL ni JDBC.
 *
 * @author Alejandra Cano 
 */
public interface SeguroRepository {

    /**
     * Guarda un nuevo seguro.
     *
     * @param seguro seguro a almacenar
     * @return seguro almacenado
     */
    Seguro crear(Seguro seguro);

    /**
     * Actualiza un seguro existente.
     *
     * @param seguro seguro modificado
     * @return seguro actualizado
     */
    Seguro modificar(Seguro seguro);

    /**
     * Elimina un seguro.
     *
     * @param seguro seguro a eliminar
     */
    void eliminar(Seguro seguro);

    /**
     * Busca un seguro por numero.
     *
     * @param numero numero del seguro
     * @return seguro encontrado
     */
    Seguro buscarPorNumero(String numero);

    /**
     * Obtiene todos los seguros almacenados.
     *
     * @return lista de seguros
     */
    List<Seguro> listar();

    /**
     * Obtiene los asegurados predeterminados.
     *
     * @return lista de asegurados
     */
    List<Asegurado> listarAsegurados();
}
