package co.edu.poli.sw2.ProyectoSeguros.dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seguro de vehiculo: registra la marca del vehiculo asegurado. Sus datos
 * propios se guardan en la tabla {@code seguro_vehiculo}.
 *
 * @author Alejandra Cano 
 * @see Seguro
 */
public class SeguroVehiculo extends Seguro {

    private static final long serialVersionUID = 1L;

    /** Valor de {@link #getTipo()} y de la columna {@code tipo}. */
    public static final String TIPO = "VEHICULO";

    private String marca;

    /**
     * Crea un seguro de vehiculo.
     *
     * @param numero          numero unico del seguro
     * @param fechaExpedicion fecha de expedicion
     * @param valor           valor asegurado
     * @param estado          estado actual
     * @param asegurado       titular
     * @param marca           marca del vehiculo
     */
    public SeguroVehiculo(String numero, LocalDate fechaExpedicion, BigDecimal valor,
                          EstadoSeguro estado, Asegurado asegurado, String marca) {
        super(numero, fechaExpedicion, valor, estado, asegurado);
        this.marca = marca;
    }

    /**
     * Obtiene la marca del vehiculo.
     *
     * @return marca
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Modifica la marca del vehiculo.
     *
     * @param marca nueva marca
     */
    public void setMarca(String marca) {
        this.marca = marca;
    }

    /**
     * {@inheritDoc}
     *
     * @return siempre {@code "VEHICULO"}
     */
    @Override
    public String getTipo() {
        return TIPO;
    }
}
