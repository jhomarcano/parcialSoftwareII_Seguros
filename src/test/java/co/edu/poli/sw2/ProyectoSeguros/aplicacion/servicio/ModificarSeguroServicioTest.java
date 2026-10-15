package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.DatosSeguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroNoEncontradoException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroValidacionException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.EstadoSeguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVehiculo;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVida;

/**
 * Pruebas del caso de uso de modificacion ({@link ModificarSeguroServicio}).
 * Usa {@link RepositorioEnMemoria}, por lo que no requiere base de datos.
 *
 * @author Alejandra Cano y Juan Rosero
 */
class ModificarSeguroServicioTest {

    private RepositorioEnMemoria repositorio;
    private ModificarSeguroServicio servicio;
    private Seguro vida;
    private Seguro vehiculo;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioEnMemoria();
        servicio = new ModificarSeguroServicio(repositorio);

        vida = repositorio.crear(new SeguroVida("VID-001", LocalDate.of(2025, 1, 5),
                new BigDecimal("1000.00"), EstadoSeguro.ACTIVO,
                RepositorioEnMemoria.ANA, "Maria Perez", "Hija"));

        vehiculo = repositorio.crear(new SeguroVehiculo("VEH-001", LocalDate.of(2025, 2, 6),
                new BigDecimal("2000.00"), EstadoSeguro.ACTIVO,
                RepositorioEnMemoria.LUIS, "Mazda"));
    }

    private DatosSeguro datosVida(String valor, String beneficiario, EstadoSeguro estado) {
        return new DatosSeguro("VIDA", "IGNORADO", LocalDate.of(2025, 6, 1), valor,
                estado, RepositorioEnMemoria.LUIS, beneficiario, "Esposa", null);
    }

    @Test
    @DisplayName("Modificar un seguro de vida actualiza sus datos")
    void modificar_vida_actualizaDatos() {
        Seguro modificado = servicio.modificar(vida,
                datosVida("5000,50", "Laura Gomez", EstadoSeguro.CANCELADO));

        assertTrue(modificado instanceof SeguroVida);
        assertEquals(new BigDecimal("5000.50"), modificado.getValor());
        assertEquals(EstadoSeguro.CANCELADO, modificado.getEstado());
        assertEquals(LocalDate.of(2025, 6, 1), modificado.getFechaExpedicion());
        assertEquals(RepositorioEnMemoria.LUIS, modificado.getAsegurado());
        assertEquals("Laura Gomez", ((SeguroVida) modificado).getNombreBeneficiario());
        assertEquals("Esposa", ((SeguroVida) modificado).getParentesco());
        assertEquals(1, repositorio.llamadasModificar);
    }

    @Test
    @DisplayName("El numero y el tipo del seguro no cambian al modificar")
    void modificar_conservaNumeroYTipo() {
        // Los datos traen otro numero y otro tipo, pero se ignoran.
        DatosSeguro datos = new DatosSeguro("VEHICULO", "OTRO-NUMERO",
                LocalDate.of(2025, 6, 1), "100", EstadoSeguro.ACTIVO,
                RepositorioEnMemoria.ANA, "Maria", "Hija", "Kia");

        Seguro modificado = servicio.modificar(vida, datos);

        assertEquals("VID-001", modificado.getNumero());
        assertEquals("VIDA", modificado.getTipo());
        assertTrue(modificado instanceof SeguroVida);
        assertEquals(1, repositorio.listar().stream()
                .filter(s -> s.getNumero().equals("VID-001")).count());
    }

    @Test
    @DisplayName("Modificar un seguro de vehiculo actualiza la marca")
    void modificar_vehiculo_actualizaMarca() {
        DatosSeguro datos = new DatosSeguro("VEHICULO", "VEH-001",
                LocalDate.of(2025, 2, 6), "2500", EstadoSeguro.VENCIDO,
                RepositorioEnMemoria.LUIS, null, null, "Toyota");

        Seguro modificado = servicio.modificar(vehiculo, datos);

        assertEquals("Toyota", ((SeguroVehiculo) modificado).getMarca());
        assertEquals(EstadoSeguro.VENCIDO, modificado.getEstado());
    }

    @Test
    @DisplayName("Modificar sin seleccion lanza excepcion y no llama al repositorio")
    void modificar_sinSeleccion_lanzaExcepcion() {
        SeguroValidacionException ex = assertThrows(SeguroValidacionException.class,
                () -> servicio.modificar(null,
                        datosVida("100", "Maria", EstadoSeguro.ACTIVO)));

        assertEquals("Debe seleccionar un seguro.", ex.getMessage());
        assertEquals(0, repositorio.llamadasModificar);
    }

    @Test
    @DisplayName("Con datos nulos lanza excepcion de validacion")
    void modificar_datosNulos_lanzaExcepcion() {
        assertThrows(SeguroValidacionException.class,
                () -> servicio.modificar(vida, null));

        assertEquals(0, repositorio.llamadasModificar);
    }

    @Test
    @DisplayName("Si un dato es invalido, el seguro original no se altera")
    void modificar_datoInvalido_noAlteraOriginal() {
        assertThrows(SeguroValidacionException.class,
                () -> servicio.modificar(vida,
                        datosVida("abc", "Laura Gomez", EstadoSeguro.CANCELADO)));

        assertEquals(new BigDecimal("1000.00"), vida.getValor());
        assertEquals(EstadoSeguro.ACTIVO, vida.getEstado());
        assertEquals("Maria Perez", ((SeguroVida) vida).getNombreBeneficiario());
        assertEquals(0, repositorio.llamadasModificar);
    }

    @Test
    @DisplayName("Modificar un seguro inexistente lanza SeguroNoEncontradoException")
    void modificar_inexistente_lanzaExcepcion() {
        Seguro fantasma = new SeguroVida("NO-EXISTE", LocalDate.of(2025, 1, 1),
                new BigDecimal("10.00"), EstadoSeguro.ACTIVO,
                RepositorioEnMemoria.ANA, "Maria", "Hija");

        assertThrows(SeguroNoEncontradoException.class,
                () -> servicio.modificar(fantasma,
                        datosVida("100", "Maria", EstadoSeguro.ACTIVO)));
    }
}
