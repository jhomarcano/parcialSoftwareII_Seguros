package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroNoEncontradoException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroValidacionException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.EstadoSeguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVehiculo;

/**
 * Pruebas del caso de uso de eliminacion ({@link EliminarSeguroServicio}).
 * Usa {@link RepositorioEnMemoria}, por lo que no requiere base de datos.
 *
 * @author Alejandra Cano 
 */
class EliminarSeguroServicioTest {

    private RepositorioEnMemoria repositorio;
    private EliminarSeguroServicio servicio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioEnMemoria();
        servicio = new EliminarSeguroServicio(repositorio);
    }

    private Seguro nuevo(String numero) {
        return repositorio.crear(new SeguroVehiculo(numero, LocalDate.of(2025, 1, 1),
                new BigDecimal("100.00"), EstadoSeguro.ACTIVO,
                RepositorioEnMemoria.ANA, "Kia"));
    }

    @Test
    @DisplayName("Eliminar quita el seguro del repositorio")
    void eliminar_quitaSeguro() {
        Seguro s = nuevo("SEG-001");

        servicio.eliminar(s);

        assertTrue(repositorio.listar().isEmpty());
        assertThrows(SeguroNoEncontradoException.class,
                () -> repositorio.buscarPorNumero("SEG-001"));
    }

    @Test
    @DisplayName("Eliminar solo borra el seguro indicado")
    void eliminar_soloBorraElIndicado() {
        Seguro a = nuevo("SEG-A");
        nuevo("SEG-B");

        servicio.eliminar(a);

        assertEquals(1, repositorio.listar().size());
        assertEquals("SEG-B", repositorio.listar().get(0).getNumero());
    }

    @Test
    @DisplayName("Eliminar sin seleccion lanza excepcion y no llama al repositorio")
    void eliminar_sinSeleccion_lanzaExcepcion() {
        SeguroValidacionException ex = assertThrows(SeguroValidacionException.class,
                () -> servicio.eliminar(null));

        assertEquals("Debe seleccionar un seguro para eliminar.", ex.getMessage());
        assertEquals(0, repositorio.llamadasEliminar);
    }

    @Test
    @DisplayName("Eliminar un seguro inexistente lanza SeguroNoEncontradoException")
    void eliminar_inexistente_lanzaExcepcion() {
        Seguro fantasma = new SeguroVehiculo("NO-EXISTE", LocalDate.of(2025, 1, 1),
                new BigDecimal("1.00"), EstadoSeguro.ACTIVO,
                RepositorioEnMemoria.ANA, "Kia");

        assertThrows(SeguroNoEncontradoException.class, () -> servicio.eliminar(fantasma));
    }
}
