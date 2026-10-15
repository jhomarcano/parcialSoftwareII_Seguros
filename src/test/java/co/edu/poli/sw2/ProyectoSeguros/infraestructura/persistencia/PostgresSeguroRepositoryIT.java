package co.edu.poli.sw2.ProyectoSeguros.infraestructura.persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroDuplicadoException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroNoEncontradoException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroValidacionException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Asegurado;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.EstadoSeguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVehiculo;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVida;

/**
 * Pruebas de integracion del adaptador de salida
 * {@link PostgresSeguroRepository} contra la base de datos real.
 *
 * <p>
 * Requiere PostgreSQL encendido, {@code sql/schema.sql} ejecutado y un
 * .env valido. Cada prueba usa numeros con prefijo {@code TEST-} y borra
 * lo que crea al terminar. Se ejecutan con {@code mvn test -Dtest=*IT}.
 * </p>
 *
 * @author Alejandra Cano y Juan Rosero
 */
class PostgresSeguroRepositoryIT {

    private final PostgresSeguroRepository repositorio = new PostgresSeguroRepository();
    private final List<Seguro> creados = new ArrayList<>();
    private Asegurado asegurado;

    @BeforeEach
    void elegirAsegurado() {
        List<Asegurado> asegurados = repositorio.listarAsegurados();
        assertFalse(asegurados.isEmpty(), "Ejecute sql/schema.sql: faltan asegurados");
        asegurado = asegurados.get(0);
    }

    @AfterEach
    void limpiar() {
        for (Seguro s : creados) {
            try {
                repositorio.eliminar(s);
            } catch (RuntimeException ignorada) {
                // La prueba pudo haberlo eliminado ya.
            }
        }
        creados.clear();
    }

    private String numeroUnico() {
        return "TEST-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private SeguroVida crearVida() {
        SeguroVida s = new SeguroVida(numeroUnico(), LocalDate.of(2025, 3, 10),
                new BigDecimal("1000.50"), EstadoSeguro.ACTIVO, asegurado, "Maria Perez", "Hija");
        repositorio.crear(s);
        creados.add(s);
        return s;
    }

    private SeguroVehiculo crearVehiculo() {
        SeguroVehiculo s = new SeguroVehiculo(numeroUnico(), LocalDate.of(2025, 4, 11),
                new BigDecimal("2000.00"), EstadoSeguro.ACTIVO, asegurado, "Mazda");
        repositorio.crear(s);
        creados.add(s);
        return s;
    }

    @Test
    @DisplayName("Hay asegurados predeterminados")
    void listarAsegurados_hayDatos() {
        assertFalse(repositorio.listarAsegurados().isEmpty());
    }

    @Test
    @DisplayName("Buscar recupera un seguro de vida con los datos de ambas tablas")
    void buscar_vida_recuperaDatos() {
        SeguroVida s = crearVida();

        Seguro leido = repositorio.buscarPorNumero(s.getNumero());

        assertTrue(leido instanceof SeguroVida);
        assertEquals(LocalDate.of(2025, 3, 10), leido.getFechaExpedicion());
        assertEquals(0, new BigDecimal("1000.50").compareTo(leido.getValor()));
        assertEquals(EstadoSeguro.ACTIVO, leido.getEstado());
        assertEquals(asegurado, leido.getAsegurado());
        assertEquals("Maria Perez", ((SeguroVida) leido).getNombreBeneficiario());
        assertEquals("Hija", ((SeguroVida) leido).getParentesco());
    }

    @Test
    @DisplayName("Buscar recupera un seguro de vehiculo con su marca")
    void buscar_vehiculo_recuperaMarca() {
        SeguroVehiculo s = crearVehiculo();

        Seguro leido = repositorio.buscarPorNumero(s.getNumero());

        assertTrue(leido instanceof SeguroVehiculo);
        assertEquals("Mazda", ((SeguroVehiculo) leido).getMarca());
    }

    @Test
    @DisplayName("Modificar actualiza la tabla padre y la tabla hija")
    void modificar_actualizaAmbasTablas() {
        SeguroVida s = crearVida();

        s.setValor(new BigDecimal("9999.99"));
        s.setEstado(EstadoSeguro.CANCELADO);
        s.setFechaExpedicion(LocalDate.of(2024, 12, 24));
        s.setNombreBeneficiario("Laura Gomez");
        s.setParentesco("Esposa");
        repositorio.modificar(s);

        SeguroVida leido = (SeguroVida) repositorio.buscarPorNumero(s.getNumero());
        assertEquals(0, new BigDecimal("9999.99").compareTo(leido.getValor()));
        assertEquals(EstadoSeguro.CANCELADO, leido.getEstado());
        assertEquals(LocalDate.of(2024, 12, 24), leido.getFechaExpedicion());
        assertEquals("Laura Gomez", leido.getNombreBeneficiario());
        assertEquals("Esposa", leido.getParentesco());
    }

    @Test
    @DisplayName("Listar incluye vida y vehiculo recien creados")
    void listar_incluyeAmbosTipos() {
        SeguroVida v = crearVida();
        SeguroVehiculo h = crearVehiculo();

        List<Seguro> todos = repositorio.listar();

        assertTrue(todos.stream().anyMatch(x -> x.getNumero().equals(v.getNumero())));
        assertTrue(todos.stream().anyMatch(x -> x.getNumero().equals(h.getNumero())));
    }

    @Test
    @DisplayName("Eliminar borra el seguro (y su fila hija por cascada)")
    void eliminar_borraSeguro() {
        SeguroVehiculo s = crearVehiculo();

        repositorio.eliminar(s);

        assertThrows(SeguroNoEncontradoException.class,
                () -> repositorio.buscarPorNumero(s.getNumero()));
    }

    @Test
    @DisplayName("Crear con un numero repetido lanza SeguroDuplicadoException")
    void crear_numeroDuplicado_lanzaExcepcion() {
        SeguroVida original = crearVida();
        SeguroVehiculo repetido = new SeguroVehiculo(original.getNumero(),
                LocalDate.now(), BigDecimal.TEN, EstadoSeguro.ACTIVO, asegurado, "Kia");

        assertThrows(SeguroDuplicadoException.class, () -> repositorio.crear(repetido));
    }

    @Test
    @DisplayName("Un numero duplicado no deja registros huerfanos (rollback)")
    void crear_duplicado_haceRollback() {
        SeguroVida original = crearVida();
        int antes = repositorio.listar().size();

        assertThrows(SeguroDuplicadoException.class, () -> repositorio.crear(
                new SeguroVehiculo(original.getNumero(), LocalDate.now(),
                        BigDecimal.TEN, EstadoSeguro.ACTIVO, asegurado, "Kia")));

        assertEquals(antes, repositorio.listar().size());
        assertTrue(repositorio.buscarPorNumero(original.getNumero()) instanceof SeguroVida);
    }

    @Test
    @DisplayName("Un asegurado inexistente lanza SeguroValidacionException")
    void crear_aseguradoInexistente_lanzaExcepcion() {
        Asegurado fantasma = new Asegurado("NO-EXISTE", "Fantasma", "f@correo.com");

        assertThrows(SeguroValidacionException.class, () -> repositorio.crear(
                new SeguroVehiculo(numeroUnico(), LocalDate.now(),
                        BigDecimal.TEN, EstadoSeguro.ACTIVO, fantasma, "Kia")));
    }

    @Test
    @DisplayName("Buscar un numero inexistente lanza SeguroNoEncontradoException")
    void buscar_inexistente_lanzaExcepcion() {
        assertThrows(SeguroNoEncontradoException.class,
                () -> repositorio.buscarPorNumero("NO-EXISTE"));
    }

    @Test
    @DisplayName("Modificar un numero inexistente lanza SeguroNoEncontradoException")
    void modificar_inexistente_lanzaExcepcion() {
        SeguroVehiculo fantasma = new SeguroVehiculo("NO-EXISTE", LocalDate.now(),
                BigDecimal.TEN, EstadoSeguro.ACTIVO, asegurado, "Kia");

        assertThrows(SeguroNoEncontradoException.class,
                () -> repositorio.modificar(fantasma));
    }

    @Test
    @DisplayName("Eliminar un numero inexistente lanza SeguroNoEncontradoException")
    void eliminar_inexistente_lanzaExcepcion() {
        SeguroVehiculo fantasma = new SeguroVehiculo("NO-EXISTE", LocalDate.now(),
                BigDecimal.TEN, EstadoSeguro.ACTIVO, asegurado, "Kia");

        assertThrows(SeguroNoEncontradoException.class,
                () -> repositorio.eliminar(fantasma));
    }

    @Test
    @DisplayName("El repositorio rechaza tipos de seguro no soportados")
    void crear_tipoNoSoportado_lanzaExcepcion() {
        Seguro otroTipo = new Seguro(numeroUnico(), LocalDate.now(), BigDecimal.TEN,
                EstadoSeguro.ACTIVO, asegurado) {
            private static final long serialVersionUID = 1L;

            @Override
            public String getTipo() {
                return "MASCOTA";
            }
        };

        assertThrows(SeguroValidacionException.class, () -> repositorio.crear(otroTipo));
    }
}
