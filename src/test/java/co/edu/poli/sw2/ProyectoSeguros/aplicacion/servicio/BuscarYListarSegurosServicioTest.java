package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroNoEncontradoException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroValidacionException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Asegurado;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.EstadoSeguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVehiculo;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVida;

/**
 * Pruebas de los casos de uso de consulta:
 * {@link BuscarSeguroServicio} y {@link ListarSegurosServicio}.
 * Usa {@link RepositorioEnMemoria}, por lo que no requiere base de datos.
 *
 * @author Alejandra Cano 
 */
class BuscarYListarSegurosServicioTest {

    private RepositorioEnMemoria repositorio;
    private BuscarSeguroServicio buscar;
    private ListarSegurosServicio listar;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioEnMemoria();
        buscar = new BuscarSeguroServicio(repositorio);
        listar = new ListarSegurosServicio(repositorio);
    }

    private Seguro vehiculo(String numero) {
        return repositorio.crear(new SeguroVehiculo(numero, LocalDate.of(2025, 1, 1),
                new BigDecimal("100.00"), EstadoSeguro.ACTIVO,
                RepositorioEnMemoria.ANA, "Kia"));
    }

    @Test
    @DisplayName("Buscar por numero devuelve el seguro con sus datos")
    void buscar_existente_devuelveSeguro() {
        repositorio.crear(new SeguroVida("VID-001", LocalDate.of(2025, 1, 1),
                new BigDecimal("500.00"), EstadoSeguro.ACTIVO,
                RepositorioEnMemoria.LUIS, "Maria", "Hija"));

        Seguro encontrado = buscar.buscarPorNumero("VID-001");

        assertEquals("VID-001", encontrado.getNumero());
        assertEquals("Maria", ((SeguroVida) encontrado).getNombreBeneficiario());
    }

    @Test
    @DisplayName("Buscar recorta los espacios del numero")
    void buscar_recortaEspacios() {
        vehiculo("SEG-001");

        assertEquals("SEG-001", buscar.buscarPorNumero("  SEG-001 ").getNumero());
    }

    @Test
    @DisplayName("Buscar un numero inexistente lanza excepcion con el numero en el mensaje")
    void buscar_inexistente_lanzaExcepcion() {
        SeguroNoEncontradoException ex = assertThrows(SeguroNoEncontradoException.class,
                () -> buscar.buscarPorNumero("NO-77"));

        assertTrue(ex.getMessage().contains("NO-77"));
    }

    @Test
    @DisplayName("Buscar con numero vacio o nulo lanza excepcion de validacion")
    void buscar_vacio_lanzaExcepcion() {
        assertThrows(SeguroValidacionException.class, () -> buscar.buscarPorNumero(""));
        assertThrows(SeguroValidacionException.class, () -> buscar.buscarPorNumero("   "));
        assertThrows(SeguroValidacionException.class, () -> buscar.buscarPorNumero(null));
    }

    @Test
    @DisplayName("Listar sin registros devuelve una lista vacia, no null")
    void listar_vacio_devuelveListaVacia() {
        List<Seguro> seguros = listar.listar();

        assertNotNull(seguros);
        assertTrue(seguros.isEmpty());
    }

    @Test
    @DisplayName("Listar devuelve todos los seguros en orden de creacion")
    void listar_devuelveTodosEnOrden() {
        vehiculo("SEG-A");
        vehiculo("SEG-B");
        vehiculo("SEG-C");

        List<Seguro> seguros = listar.listar();

        assertEquals(3, seguros.size());
        assertEquals("SEG-A", seguros.get(0).getNumero());
        assertEquals("SEG-C", seguros.get(2).getNumero());
    }

    @Test
    @DisplayName("Listar asegurados devuelve los asegurados predeterminados")
    void listarAsegurados_devuelvePredeterminados() {
        List<Asegurado> asegurados = listar.listarAsegurados();

        assertEquals(2, asegurados.size());
        assertTrue(asegurados.contains(RepositorioEnMemoria.ANA));
    }
}
