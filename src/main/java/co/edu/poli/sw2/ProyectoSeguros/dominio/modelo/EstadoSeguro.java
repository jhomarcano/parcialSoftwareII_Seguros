package co.edu.poli.sw2.ProyectoSeguros.dominio.modelo;

/**
 * Estados posibles de un seguro.
 *
 * @author Alejandra Cano y Juan Rosero
 */
public enum EstadoSeguro {

    /** El seguro esta vigente. */
    ACTIVO,

    /** El seguro cumplio su periodo de cobertura. */
    VENCIDO,

    /** El seguro fue cancelado antes de su vencimiento. */
    CANCELADO
}
