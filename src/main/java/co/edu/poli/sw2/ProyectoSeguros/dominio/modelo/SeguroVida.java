package co.edu.poli.sw2.ProyectoSeguros.dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seguro de vida: indica quien recibe la indemnizacion y su parentesco
 * con el asegurado. Sus datos propios se guardan en la tabla
 * {@code seguro_vida}.
 *
 * @author Alejandra Cano y Juan Rosero
 * @see Seguro
 */
public class SeguroVida extends Seguro {

    private static final long serialVersionUID = 1L;

    /** Valor de {@link #getTipo()} y de la columna {@code tipo}. */
    public static final String TIPO = "VIDA";

    private String nombreBeneficiario;
    private String parentesco;

    /**
     * Crea un seguro de vida.
     *
     * @param numero             numero unico del seguro
     * @param fechaExpedicion    fecha de expedicion
     * @param valor              valor asegurado
     * @param estado             estado actual
     * @param asegurado          titular
     * @param nombreBeneficiario nombre de quien recibe la indemnizacion
     * @param parentesco         relacion del beneficiario con el asegurado
     */
    public SeguroVida(String numero, LocalDate fechaExpedicion, BigDecimal valor,
                      EstadoSeguro estado, Asegurado asegurado,
                      String nombreBeneficiario, String parentesco) {
        super(numero, fechaExpedicion, valor, estado, asegurado);
        this.nombreBeneficiario = nombreBeneficiario;
        this.parentesco = parentesco;
    }

    /**
     * Obtiene el nombre del beneficiario.
     *
     * @return nombre del beneficiario
     */
    public String getNombreBeneficiario() {
        return nombreBeneficiario;
    }

    /**
     * Modifica el nombre del beneficiario.
     *
     * @param nombre nuevo nombre
     */
    public void setNombreBeneficiario(String nombre) {
        this.nombreBeneficiario = nombre;
    }

    /**
     * Obtiene el parentesco.
     *
     * @return parentesco con el asegurado
     */
    public String getParentesco() {
        return parentesco;
    }

    /**
     * Modifica el parentesco.
     *
     * @param parentesco nuevo parentesco
     */
    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    /**
     * {@inheritDoc}
     *
     * @return siempre {@code "VIDA"}
     */
    @Override
    public String getTipo() {
        return TIPO;
    }
}
