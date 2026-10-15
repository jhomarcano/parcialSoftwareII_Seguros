package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.DatosSeguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroDuplicadoException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroValidacionException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.EstadoSeguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVehiculo;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVida;

/**
 * Pruebas del caso de uso de creacion ({@link CrearSeguroServicio}).
 * Usa {@link RepositorioEnMemoria}, por lo que no requiere base de datos.
 *
 * @author Alejandra Cano 
 */
class CrearSeguroServicioTest {

    private RepositorioEnMemoria repositorio;
    private CrearSeguroServicio servicio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioEnMemoria();
        servicio = new CrearSeguroServicio(repositorio);
    }

    private DatosSeguro vida(String numero, String valor, String beneficiario, String parentesco) {
        return new DatosSeguro("VIDA", numero, LocalDate.of(2025, 3, 10), valor,
                EstadoSeguro.ACTIVO, RepositorioEnMemoria.ANA,
                beneficiario, parentesco, null);
    }

    private DatosSeguro vehiculo(String numero, String valor, String marca) {
        return new DatosSeguro("VEHICULO", numero, LocalDate.of(2025, 3, 10), valor,
                EstadoSeguro.ACTIVO, RepositorioEnMemoria.LUIS,
                null, null, marca);
    }

    @Test
    @DisplayName("Crear un seguro de vida guarda todos sus datos")
    void crear_vida_guardaDatos() {
        Seguro creado = servicio.crear(vida("SEG-001", "50000000", "Maria Perez", "Hija"));

        assertTrue(creado instanceof SeguroVida);
        assertEquals("VIDA", creado.getTipo());
        assertEquals("SEG-001", creado.getNumero());
        assertEquals(LocalDate.of(2025, 3, 10), creado.getFechaExpedicion());
        assertEquals(new BigDecimal("50000000.00"), creado.getValor());
        assertEquals(EstadoSeguro.ACTIVO, creado.getEstado());
        assertEquals(RepositorioEnMemoria.ANA, creado.getAsegurado());
        assertEquals("Maria Perez", ((SeguroVida) creado).getNombreBeneficiario());
        assertEquals("Hija", ((SeguroVida) creado).getParentesco());
        assertEquals(1, repositorio.llamadasCrear);
    }

    @Test
    @DisplayName("Crear un seguro de vehiculo guarda la marca")
    void crear_vehiculo_guardaMarca() {
        Seguro creado = servicio.crear(vehiculo("SEG-002", "30000000", "Mazda"));

        assertTrue(creado instanceof SeguroVehiculo);
        assertEquals("VEHICULO", creado.getTipo());
        assertEquals("Mazda", ((SeguroVehiculo) creado).getMarca());
    }

    @Test
    @DisplayName("Crear elimina los espacios sobrantes de los textos")
    void crear_recortaEspacios() {
        Seguro creado = servicio.crear(vida("  SEG-003  ", "100", " Maria  ", " Hija "));

        assertEquals("SEG-003", creado.getNumero());
        assertEquals("Maria", ((SeguroVida) creado).getNombreBeneficiario());
        assertEquals("Hija", ((SeguroVida) creado).getParentesco());
    }

    @Test
    @DisplayName("Crear acepta el valor con coma decimal")
    void crear_aceptaValorConComa() {
        Seguro creado = servicio.crear(vehiculo("SEG-004", "1500,5", "Kia"));

        assertEquals(new BigDecimal("1500.50"), creado.getValor());
    }

    @Test
    @DisplayName("Un tipo no soportado o ausente no llega al repositorio")
    void crear_tipoInvalido_noLlamaRepositorio() {
        DatosSeguro desconocido = new DatosSeguro("MASCOTA", "SEG-005",
                LocalDate.now(), "100", EstadoSeguro.ACTIVO, RepositorioEnMemoria.ANA,
                null, null, null);
        DatosSeguro sinTipo = new DatosSeguro(null, "SEG-005",
                LocalDate.now(), "100", EstadoSeguro.ACTIVO, RepositorioEnMemoria.ANA,
                null, null, null);

        assertThrows(SeguroValidacionException.class, () -> servicio.crear(desconocido));
        assertThrows(SeguroValidacionException.class, () -> servicio.crear(sinTipo));
        assertThrows(SeguroValidacionException.class, () -> servicio.crear(null));
        assertEquals(0, repositorio.llamadasCrear);
    }

    @Test
    @DisplayName("Un numero invalido no llega al repositorio")
    void crear_numeroInvalido_noLlamaRepositorio() {
        assertThrows(SeguroValidacionException.class,
                () -> servicio.crear(vida("", "100", "Maria", "Hija")));

        assertEquals(0, repositorio.llamadasCrear);
    }

    @Test
    @DisplayName("Un valor invalido no llega al repositorio")
    void crear_valorInvalido_noLlamaRepositorio() {
        assertThrows(SeguroValidacionException.class,
                () -> servicio.crear(vida("SEG-006", "-3", "Maria", "Hija")));

        assertEquals(0, repositorio.llamadasCrear);
    }

    @Test
    @DisplayName("Sin asegurado, estado o fecha no se crea el seguro")
    void crear_sinAseguradoEstadoOFecha_lanzaExcepcion() {
        DatosSeguro sinAsegurado = new DatosSeguro("VEHICULO", "SEG-007", LocalDate.now(),
                "100", EstadoSeguro.ACTIVO, null, null, null, "Kia");
        DatosSeguro sinEstado = new DatosSeguro("VEHICULO", "SEG-007", LocalDate.now(),
                "100", null, RepositorioEnMemoria.ANA, null, null, "Kia");
        DatosSeguro sinFecha = new DatosSeguro("VEHICULO", "SEG-007", null,
                "100", EstadoSeguro.ACTIVO, RepositorioEnMemoria.ANA, null, null, "Kia");

        assertThrows(SeguroValidacionException.class, () -> servicio.crear(sinAsegurado));
        assertThrows(SeguroValidacionException.class, () -> servicio.crear(sinEstado));
        assertThrows(SeguroValidacionException.class, () -> servicio.crear(sinFecha));
        assertEquals(0, repositorio.llamadasCrear);
    }

    @Test
    @DisplayName("Un seguro de vida sin beneficiario o un vehiculo sin marca es rechazado")
    void crear_faltanDatosDelSubtipo_lanzaExcepcion() {
        assertThrows(SeguroValidacionException.class,
                () -> servicio.crear(vida("SEG-008", "100", "", "Hija")));
        assertThrows(SeguroValidacionException.class,
                () -> servicio.crear(vida("SEG-008", "100", "Maria", " ")));
        assertThrows(SeguroValidacionException.class,
                () -> servicio.crear(vehiculo("SEG-008", "100", "")));

        assertEquals(0, repositorio.llamadasCrear);
    }

    @Test
    @DisplayName("Crear con un numero repetido lanza SeguroDuplicadoException")
    void crear_numeroDuplicado_lanzaExcepcion() {
        servicio.crear(vehiculo("SEG-009", "100", "Kia"));

        assertThrows(SeguroDuplicadoException.class,
                () -> servicio.crear(vida("SEG-009", "200", "Maria", "Hija")));
    }
}
