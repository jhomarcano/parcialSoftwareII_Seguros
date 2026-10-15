package co.edu.poli.sw2.ProyectoSeguros.dominio.modelo;

import java.io.Serializable;

/**
 * Entidad que representa a la persona titular de un seguro.
 * <p>
 * Los asegurados estan predeterminados en la base de datos: el sistema
 * solo los consulta para asociarlos a un seguro.
 *
 * @author Alejandra Cano 
 */
public class Asegurado implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String identificacion;
    private final String nombre;
    private final String correoElectronico;

    /**
     * Crea un asegurado con todos sus atributos.
     *
     * @param identificacion    documento de identidad (unico)
     * @param nombre            nombre completo
     * @param correoElectronico correo de contacto
     */
    public Asegurado(String identificacion, String nombre, String correoElectronico) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.correoElectronico = correoElectronico;
    }

    /**
     * Devuelve la identificacion.
     *
     * @return documento de identidad
     */
    public String getIdentificacion() {
        return identificacion;
    }

    /**
     * Devuelve el nombre.
     *
     * @return nombre completo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el correo electronico.
     *
     * @return correo de contacto
     */
    public String getCorreoElectronico() {
        return correoElectronico;
    }

    /**
     * Descripcion legible usada en los controles de seleccion.
     *
     * @return nombre seguido de la identificacion
     */
    @Override
    public String toString() {
        return nombre + " (" + identificacion + ")";
    }

    /**
     * Dos asegurados son iguales si comparten la identificacion.
     *
     * @param o objeto a comparar
     * @return {@code true} si es un Asegurado con la misma identificacion
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asegurado)) return false;
        Asegurado otro = (Asegurado) o;
        return identificacion != null && identificacion.equals(otro.identificacion);
    }

    /**
     * Hash coherente con {@link #equals(Object)}.
     *
     * @return hash derivado de la identificacion
     */
    @Override
    public int hashCode() {
        return identificacion == null ? 0 : identificacion.hashCode();
    }
}
