package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroValidacionException;

/**
 * Pruebas unitarias de las reglas de negocio de {@link ValidadorSeguro}.
 * No requieren base de datos.
 *
 * @author Alejandra Cano y Juan Rosero
 */
class ValidadorSeguroTest {

    // ---------------- Numero ----------------

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   ", "\t" })
    @DisplayName("El numero vacio o nulo es rechazado")
    void numero_vacio_lanzaExcepcion(String numero) {
        SeguroValidacionException ex = assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.validarNumero(numero));

        assertEquals("El numero del seguro es obligatorio.", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = { "---", "@#$", "..." })
    @DisplayName("El numero sin caracteres alfanumericos es rechazado")
    void numero_sinAlfanumericos_lanzaExcepcion(String numero) {
        assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.validarNumero(numero));
    }

    @Test
    @DisplayName("El numero de mas de 30 caracteres es rechazado")
    void numero_muyLargo_lanzaExcepcion() {
        String largo = "A".repeat(31);

        assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.validarNumero(largo));
    }

    @ParameterizedTest
    @ValueSource(strings = { "SEG-001", "12345", "A" })
    @DisplayName("Un numero valido no lanza excepcion")
    void numero_valido_noLanza(String numero) {
        assertDoesNotThrow(() -> ValidadorSeguro.validarNumero(numero));
    }

    // ---------------- Fecha ----------------

    @Test
    @DisplayName("La fecha nula es rechazada")
    void fecha_nula_lanzaExcepcion() {
        assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.validarFecha(null));
    }

    @Test
    @DisplayName("Una fecha futura es rechazada")
    void fecha_futura_lanzaExcepcion() {
        SeguroValidacionException ex = assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.validarFecha(LocalDate.now().plusDays(1)));

        assertEquals("La fecha de expedicion no puede ser futura.", ex.getMessage());
    }

    @Test
    @DisplayName("Hoy y fechas pasadas son validas")
    void fecha_hoyOPasada_noLanza() {
        assertDoesNotThrow(() -> ValidadorSeguro.validarFecha(LocalDate.now()));
        assertDoesNotThrow(() -> ValidadorSeguro.validarFecha(LocalDate.of(2020, 1, 15)));
    }

    // ---------------- Valor ----------------

    @ParameterizedTest
    @CsvSource({ "1000, 1000.00", "'1500,5', 1500.50", "' 2 ', 2.00", "0.999, 1.00" })
    @DisplayName("El valor valido se convierte a BigDecimal con 2 decimales")
    void valor_valido_seConvierte(String texto, String esperado) {
        assertEquals(new BigDecimal(esperado), ValidadorSeguro.parsearValor(texto));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    @DisplayName("El valor vacio o nulo es rechazado")
    void valor_vacio_lanzaExcepcion(String texto) {
        SeguroValidacionException ex = assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.parsearValor(texto));

        assertEquals("El valor es obligatorio.", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = { "0", "-1", "-0.5" })
    @DisplayName("El valor cero o negativo es rechazado")
    void valor_noPositivo_lanzaExcepcion(String texto) {
        SeguroValidacionException ex = assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.parsearValor(texto));

        assertEquals("El valor debe ser mayor que cero.", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = { "abc", "1.2.3", "mil", "NaN", "Infinity" })
    @DisplayName("El valor no numerico es rechazado")
    void valor_noNumerico_lanzaExcepcion(String texto) {
        SeguroValidacionException ex = assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.parsearValor(texto));

        assertEquals("El valor debe ser un numero valido.", ex.getMessage());
    }

    @Test
    @DisplayName("Un valor que excede la columna NUMERIC(14,2) es rechazado")
    void valor_demasiadoGrande_lanzaExcepcion() {
        assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.parsearValor("1000000000000"));
    }

    // ---------------- Estado y asegurado ----------------

    @Test
    @DisplayName("El estado nulo es rechazado")
    void estado_nulo_lanzaExcepcion() {
        assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.validarEstado(null));
    }

    @Test
    @DisplayName("El asegurado nulo es rechazado")
    void asegurado_nulo_lanzaExcepcion() {
        assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.validarAsegurado(null));
    }

    // ---------------- Beneficiario, parentesco, marca ----------------

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   ", "12345", "---" })
    @DisplayName("Un beneficiario vacio o sin letras es rechazado")
    void beneficiario_invalido_lanzaExcepcion(String nombre) {
        assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.validarBeneficiario(nombre));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Maria Perez", "Jose Nunez", "Ana" })
    @DisplayName("Un beneficiario valido no lanza excepcion")
    void beneficiario_valido_noLanza(String nombre) {
        assertDoesNotThrow(() -> ValidadorSeguro.validarBeneficiario(nombre));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "  ", "123" })
    @DisplayName("Un parentesco vacio o sin letras es rechazado")
    void parentesco_invalido_lanzaExcepcion(String parentesco) {
        assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.validarParentesco(parentesco));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "  ", "***" })
    @DisplayName("Una marca vacia o sin alfanumericos es rechazada")
    void marca_invalida_lanzaExcepcion(String marca) {
        assertThrows(SeguroValidacionException.class,
                () -> ValidadorSeguro.validarMarca(marca));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Mazda", "BMW", "Kia 2" })
    @DisplayName("Una marca valida no lanza excepcion")
    void marca_valida_noLanza(String marca) {
        assertDoesNotThrow(() -> ValidadorSeguro.validarMarca(marca));
    }

    // ---------------- Estructura ----------------

    @Test
    @DisplayName("ValidadorSeguro es una clase utilitaria: final y con constructor privado")
    void clase_esUtilitaria() {
        Constructor<?>[] constructores = ValidadorSeguro.class.getDeclaredConstructors();

        assertTrue(Modifier.isFinal(ValidadorSeguro.class.getModifiers()));
        assertEquals(1, constructores.length);
        assertTrue(Modifier.isPrivate(constructores[0].getModifiers()));
    }
}
