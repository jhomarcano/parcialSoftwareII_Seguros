package co.edu.poli.sw2.ProyectoSeguros.dominio.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad abstracta que representa un seguro dentro del dominio.
 * Contiene los atributos comunes a todos los tipos de seguro.
 * <p>
 * El numero identifica al seguro y no puede cambiarse una vez creado.
 * Cada seguro pertenece a un {@link Asegurado}.
 *
 * @author Alejandra Cano y Juan Rosero
 */
public abstract class Seguro implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String numero;
    private LocalDate fechaExpedicion;
    private BigDecimal valor;
    private EstadoSeguro estado;
    private Asegurado asegurado;

    /**
     * Construye un seguro con sus datos generales.
     *
     * @param numero          numero unico del seguro
     * @param fechaExpedicion fecha en que se expidio
     * @param valor           valor asegurado
     * @param estado          estado actual
     * @param asegurado       titular del seguro
     */
    protected Seguro(String numero, LocalDate fechaExpedicion, BigDecimal valor,
                     EstadoSeguro estado, Asegurado asegurado) {
        this.numero = numero;
        this.fechaExpedicion = fechaExpedicion;
        this.valor = valor;
        this.estado = estado;
        this.asegurado = asegurado;
    }

    /**
     * Obtiene el numero del seguro.
     *
     * @return numero unico
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Obtiene la fecha de expedicion.
     *
     * @return fecha de expedicion
     */
    public LocalDate getFechaExpedicion() {
        return fechaExpedicion;
    }

    /**
     * Modifica la fecha de expedicion.
     *
     * @param fechaExpedicion nueva fecha
     */
    public void setFechaExpedicion(LocalDate fechaExpedicion) {
        this.fechaExpedicion = fechaExpedicion;
    }

    /**
     * Obtiene el valor asegurado.
     *
     * @return valor del seguro
     */
    public BigDecimal getValor() {
        return valor;
    }

    /**
     * Modifica el valor asegurado.
     *
     * @param valor nuevo valor
     */
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    /**
     * Obtiene el estado.
     *
     * @return estado actual
     */
    public EstadoSeguro getEstado() {
        return estado;
    }

    /**
     * Modifica el estado.
     *
     * @param estado nuevo estado
     */
    public void setEstado(EstadoSeguro estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el titular del seguro.
     *
     * @return asegurado
     */
    public Asegurado getAsegurado() {
        return asegurado;
    }

    /**
     * Cambia el titular del seguro.
     *
     * @param asegurado nuevo asegurado
     */
    public void setAsegurado(Asegurado asegurado) {
        this.asegurado = asegurado;
    }

    /**
     * Devuelve el tipo concreto de seguro.
     *
     * @return nombre del tipo de seguro
     */
    public abstract String getTipo();
}
