package co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada;

import java.time.LocalDate;

import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Asegurado;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.EstadoSeguro;

/**
 * Datos crudos de un seguro tal como los captura el adaptador de entrada.
 *
 * <p>
 * Agrupa los campos del formulario para que los puertos de entrada no
 * reciban una lista larga de parametros. Los textos llegan sin validar:
 * la validacion es responsabilidad de la capa de aplicacion.
 * Segun el tipo se usan los campos del beneficiario y parentesco (VIDA)
 * o la marca (VEHICULO); los demas se ignoran.
 * </p>
 *
 * @author Alejandra Cano 
 */
public final class DatosSeguro {

    private final String tipo;
    private final String numero;
    private final LocalDate fechaExpedicion;
    private final String valorTexto;
    private final EstadoSeguro estado;
    private final Asegurado asegurado;
    private final String nombreBeneficiario;
    private final String parentesco;
    private final String marca;

    /**
     * Crea el paquete de datos.
     *
     * @param tipo               tipo de seguro: {@code VIDA} o {@code VEHICULO}
     * @param numero             numero del seguro
     * @param fechaExpedicion    fecha de expedicion
     * @param valorTexto         valor capturado como texto
     * @param estado             estado del seguro
     * @param asegurado          titular del seguro
     * @param nombreBeneficiario beneficiario (solo seguro de vida)
     * @param parentesco         parentesco (solo seguro de vida)
     * @param marca              marca (solo seguro de vehiculo)
     */
    public DatosSeguro(String tipo, String numero, LocalDate fechaExpedicion,
                       String valorTexto, EstadoSeguro estado, Asegurado asegurado,
                       String nombreBeneficiario, String parentesco, String marca) {
        this.tipo = tipo;
        this.numero = numero;
        this.fechaExpedicion = fechaExpedicion;
        this.valorTexto = valorTexto;
        this.estado = estado;
        this.asegurado = asegurado;
        this.nombreBeneficiario = nombreBeneficiario;
        this.parentesco = parentesco;
        this.marca = marca;
    }

    /**
     * Devuelve una copia con otro numero y tipo.
     * Se usa al modificar: el numero y el tipo del seguro no cambian.
     *
     * @param nuevoNumero numero del seguro existente
     * @param nuevoTipo   tipo del seguro existente
     * @return copia con la identidad indicada
     */
    public DatosSeguro conIdentidad(String nuevoNumero, String nuevoTipo) {
        return new DatosSeguro(nuevoTipo, nuevoNumero, fechaExpedicion, valorTexto,
                estado, asegurado, nombreBeneficiario, parentesco, marca);
    }

    /** @return tipo de seguro */
    public String getTipo() { return tipo; }

    /** @return numero del seguro */
    public String getNumero() { return numero; }

    /** @return fecha de expedicion */
    public LocalDate getFechaExpedicion() { return fechaExpedicion; }

    /** @return valor como texto */
    public String getValorTexto() { return valorTexto; }

    /** @return estado */
    public EstadoSeguro getEstado() { return estado; }

    /** @return titular */
    public Asegurado getAsegurado() { return asegurado; }

    /** @return nombre del beneficiario */
    public String getNombreBeneficiario() { return nombreBeneficiario; }

    /** @return parentesco */
    public String getParentesco() { return parentesco; }

    /** @return marca del vehiculo */
    public String getMarca() { return marca; }
}
