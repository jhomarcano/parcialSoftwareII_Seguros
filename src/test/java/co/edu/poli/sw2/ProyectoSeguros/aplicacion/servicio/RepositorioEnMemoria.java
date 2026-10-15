package co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.salida.SeguroRepository;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroDuplicadoException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroNoEncontradoException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Asegurado;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;

/**
 * Implementacion en memoria del puerto de salida {@link SeguroRepository},
 * usada solo en pruebas.
 *
 * <p>
 * Permite probar los casos de uso sin PostgreSQL. Imita las reglas de la
 * base de datos real: rechaza numeros duplicados y lanza
 * {@link SeguroNoEncontradoException} si el numero no existe. Cuenta
 * cuantas veces se llamo cada operacion, para verificar que un dato
 * invalido nunca llegue a la persistencia.
 * </p>
 *
 * @author Alejandra Cano y Juan Rosero
 */
class RepositorioEnMemoria implements SeguroRepository {

    static final Asegurado ANA = new Asegurado("1001", "Ana Torres", "ana@correo.com");
    static final Asegurado LUIS = new Asegurado("1002", "Luis Perez", "luis@correo.com");

    private final Map<String, Seguro> datos = new LinkedHashMap<>();

    int llamadasCrear;
    int llamadasModificar;
    int llamadasEliminar;

    @Override
    public Seguro crear(Seguro seguro) {
        llamadasCrear++;
        if (datos.containsKey(seguro.getNumero())) {
            throw new SeguroDuplicadoException(seguro.getNumero());
        }
        datos.put(seguro.getNumero(), seguro);
        return seguro;
    }

    @Override
    public Seguro modificar(Seguro seguro) {
        llamadasModificar++;
        if (!datos.containsKey(seguro.getNumero())) {
            throw new SeguroNoEncontradoException(seguro.getNumero());
        }
        datos.put(seguro.getNumero(), seguro);
        return seguro;
    }

    @Override
    public void eliminar(Seguro seguro) {
        llamadasEliminar++;
        if (datos.remove(seguro.getNumero()) == null) {
            throw new SeguroNoEncontradoException(seguro.getNumero());
        }
    }

    @Override
    public Seguro buscarPorNumero(String numero) {
        Seguro seguro = datos.get(numero);
        if (seguro == null) {
            throw new SeguroNoEncontradoException(numero);
        }
        return seguro;
    }

    @Override
    public List<Seguro> listar() {
        return new ArrayList<>(datos.values());
    }

    @Override
    public List<Asegurado> listarAsegurados() {
        return Arrays.asList(ANA, LUIS);
    }
}
