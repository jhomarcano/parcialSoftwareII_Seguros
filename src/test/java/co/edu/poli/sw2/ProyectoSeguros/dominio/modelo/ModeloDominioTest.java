package co.edu.poli.sw2.ProyectoSeguros.dominio.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de las entidades de dominio. No requieren base de datos.
 *
 * @author Alejandra Cano y Juan Rosero
 */
class ModeloDominioTest {

    private final Asegurado ana = new Asegurado("1001", "Ana Torres", "ana@correo.com");

    @Test
    @DisplayName("SeguroVida asigna atributos y su tipo es VIDA")
    void seguroVida_atributosYTipo() {
        SeguroVida v = new SeguroVida("VID-1", LocalDate.of(2025, 1, 2),
                new BigDecimal("10.00"), EstadoSeguro.ACTIVO, ana, "Maria", "Hija");

        assertEquals("VID-1", v.getNumero());
        assertEquals(LocalDate.of(2025, 1, 2), v.getFechaExpedicion());
        assertEquals(new BigDecimal("10.00"), v.getValor());
        assertEquals(EstadoSeguro.ACTIVO, v.getEstado());
        assertEquals(ana, v.getAsegurado());
        assertEquals("Maria", v.getNombreBeneficiario());
        assertEquals("Hija", v.getParentesco());
        assertEquals("VIDA", v.getTipo());
    }

    @Test
    @DisplayName("SeguroVehiculo asigna atributos y su tipo es VEHICULO")
    void seguroVehiculo_atributosYTipo() {
        SeguroVehiculo v = new SeguroVehiculo("VEH-1", LocalDate.of(2025, 1, 2),
                new BigDecimal("20.00"), EstadoSeguro.VENCIDO, ana, "Mazda");

        assertEquals("Mazda", v.getMarca());
        assertEquals("VEHICULO", v.getTipo());
    }

    @Test
    @DisplayName("Los setters modifican los atributos modificables")
    void setters_modificanAtributos() {
        SeguroVida v = new SeguroVida("VID-1", LocalDate.of(2025, 1, 2),
                new BigDecimal("10.00"), EstadoSeguro.ACTIVO, ana, "Maria", "Hija");
        Asegurado otro = new Asegurado("1002", "Luis", "luis@correo.com");

        v.setFechaExpedicion(LocalDate.of(2024, 5, 5));
        v.setValor(new BigDecimal("99.00"));
        v.setEstado(EstadoSeguro.CANCELADO);
        v.setAsegurado(otro);
        v.setNombreBeneficiario("Laura");
        v.setParentesco("Esposa");

        assertEquals(LocalDate.of(2024, 5, 5), v.getFechaExpedicion());
        assertEquals(new BigDecimal("99.00"), v.getValor());
        assertEquals(EstadoSeguro.CANCELADO, v.getEstado());
        assertEquals(otro, v.getAsegurado());
        assertEquals("Laura", v.getNombreBeneficiario());
        assertEquals("Esposa", v.getParentesco());
        assertEquals("VID-1", v.getNumero());
    }

    @Test
    @DisplayName("Los seguros son serializables y heredan de Seguro")
    void seguros_sonSerializables() {
        SeguroVehiculo v = new SeguroVehiculo("VEH-1", LocalDate.now(),
                BigDecimal.ONE, EstadoSeguro.ACTIVO, ana, "Kia");

        assertTrue(v instanceof Seguro);
        assertTrue(v instanceof Serializable);
    }

    @Test
    @DisplayName("Asegurado se compara por identificacion")
    void asegurado_equalsPorIdentificacion() {
        Asegurado copia = new Asegurado("1001", "Otro nombre", "otro@correo.com");

        assertEquals(ana, copia);
        assertEquals(ana.hashCode(), copia.hashCode());
        assertNotEquals(ana, new Asegurado("9999", "Ana Torres", "ana@correo.com"));
    }

    @Test
    @DisplayName("EstadoSeguro tiene los tres estados esperados")
    void estadoSeguro_valores() {
        assertEquals(3, EstadoSeguro.values().length);
        assertNotNull(EstadoSeguro.valueOf("ACTIVO"));
        assertNotNull(EstadoSeguro.valueOf("VENCIDO"));
        assertNotNull(EstadoSeguro.valueOf("CANCELADO"));
    }
}
