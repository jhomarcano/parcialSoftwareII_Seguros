package co.edu.poli.sw2.ProyectoSeguros.infraestructura.persistencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.salida.SeguroRepository;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.ConexionBDException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroDuplicadoException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroNoEncontradoException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroValidacionException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Asegurado;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.EstadoSeguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVehiculo;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVida;

/**
 * Adaptador de salida que implementa la persistencia
 * de seguros utilizando PostgreSQL y JDBC.
 *
 * Maneja las tablas {@code seguro} (padre), {@code seguro_vida} y
 * {@code seguro_vehiculo} (hijas) y consulta {@code asegurado}.
 *
 * @author Alejandra Cano y Juan Rosero
 */
public class PostgresSeguroRepository implements SeguroRepository {

    private static final String SQLSTATE_UNIQUE = "23505";
    private static final String SQLSTATE_FK = "23503";
    private static final String SQLSTATE_CHECK = "23514";
    private static final String SQLSTATE_NOT_NULL = "23502";
    private static final String SQLSTATE_TOO_LONG = "22001";
    private static final String SQLSTATE_OUT_OF_RANGE = "22003";

    private static final String SELECT_BASE =
            "SELECT s.numero, s.fecha_expedicion, s.valor, s.estado, s.tipo, "
          + "a.identificacion, a.nombre, a.correo_electronico, "
          + "v.nombre_beneficiario, v.parentesco, h.marca "
          + "FROM seguro s "
          + "INNER JOIN asegurado a ON a.identificacion = s.id_asegurado "
          + "LEFT JOIN seguro_vida v ON v.numero_seguro = s.numero "
          + "LEFT JOIN seguro_vehiculo h ON h.numero_seguro = s.numero ";

    /**
     * {@inheritDoc}
     */
    @Override
    public Seguro crear(Seguro seguro) {

        validarTipo(seguro);

        String sqlSeguro =
                "INSERT INTO seguro "
              + "(numero, fecha_expedicion, valor, estado, tipo, id_asegurado) "
              + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conexion = null;

        try {

            conexion = ConexionBD.getInstancia().obtenerConexion();

            conexion.setAutoCommit(false);

            try (PreparedStatement ps = conexion.prepareStatement(sqlSeguro)) {

                ps.setString(1, seguro.getNumero());
                ps.setDate(2, Date.valueOf(seguro.getFechaExpedicion()));
                ps.setBigDecimal(3, seguro.getValor());
                ps.setString(4, seguro.getEstado().name());
                ps.setString(5, seguro.getTipo());
                ps.setString(6, seguro.getAsegurado().getIdentificacion());

                ps.executeUpdate();
            }

            insertarDetalle(conexion, seguro);

            conexion.commit();

            return seguro;

        } catch (SQLException e) {

            rollback(conexion);

            throw traducirExcepcion(e, seguro.getNumero());

        } catch (RuntimeException e) {

            rollback(conexion);

            throw e;

        } finally {

            cerrar(conexion);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Seguro modificar(Seguro seguro) {

        validarTipo(seguro);

        String sqlSeguro =
                "UPDATE seguro "
              + "SET fecha_expedicion=?, valor=?, estado=?, id_asegurado=? "
              + "WHERE numero=? AND tipo=?";

        Connection conexion = null;

        try {

            conexion = ConexionBD.getInstancia().obtenerConexion();

            conexion.setAutoCommit(false);

            try (PreparedStatement ps = conexion.prepareStatement(sqlSeguro)) {

                ps.setDate(1, Date.valueOf(seguro.getFechaExpedicion()));
                ps.setBigDecimal(2, seguro.getValor());
                ps.setString(3, seguro.getEstado().name());
                ps.setString(4, seguro.getAsegurado().getIdentificacion());
                ps.setString(5, seguro.getNumero());
                ps.setString(6, seguro.getTipo());

                if (ps.executeUpdate() == 0) {
                    throw new SeguroNoEncontradoException(seguro.getNumero());
                }
            }

            actualizarDetalle(conexion, seguro);

            conexion.commit();

            return seguro;

        } catch (SQLException e) {

            rollback(conexion);

            throw traducirExcepcion(e, seguro.getNumero());

        } catch (RuntimeException e) {

            rollback(conexion);

            throw e;

        } finally {

            cerrar(conexion);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eliminar(Seguro seguro) {

        // La fila hija se borra por ON DELETE CASCADE.
        String sql = "DELETE FROM seguro WHERE numero=?";

        try (Connection conexion = ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, seguro.getNumero());

            if (ps.executeUpdate() == 0) {
                throw new SeguroNoEncontradoException(seguro.getNumero());
            }

        } catch (SQLException e) {

            throw new ConexionBDException(
                    "No fue posible eliminar el seguro.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Seguro buscarPorNumero(String numero) {

        String sql = SELECT_BASE + "WHERE s.numero=?";

        try (Connection conexion = ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, numero);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SeguroNoEncontradoException(numero);
                }

                return mapearSeguro(rs);
            }

        } catch (SQLException e) {

            throw new ConexionBDException(
                    "No fue posible buscar el seguro.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Seguro> listar() {

        String sql = SELECT_BASE + "ORDER BY s.numero";

        List<Seguro> seguros = new ArrayList<>();

        try (Connection conexion = ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                seguros.add(mapearSeguro(rs));
            }

            return seguros;

        } catch (SQLException e) {

            throw new ConexionBDException(
                    "No fue posible listar los seguros.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Asegurado> listarAsegurados() {

        String sql =
                "SELECT identificacion, nombre, correo_electronico "
              + "FROM asegurado ORDER BY nombre";

        List<Asegurado> asegurados = new ArrayList<>();

        try (Connection conexion = ConexionBD.getInstancia().obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                asegurados.add(new Asegurado(
                        rs.getString("identificacion"),
                        rs.getString("nombre"),
                        rs.getString("correo_electronico")));
            }

            return asegurados;

        } catch (SQLException e) {

            throw new ConexionBDException(
                    "No fue posible listar los asegurados.", e);
        }
    }

    /**
     * Inserta la fila hija segun el tipo de seguro.
     */
    private void insertarDetalle(Connection conexion, Seguro seguro)
            throws SQLException {

        if (seguro instanceof SeguroVida) {

            SeguroVida vida = (SeguroVida) seguro;

            try (PreparedStatement ps = conexion.prepareStatement(
                    "INSERT INTO seguro_vida "
                  + "(numero_seguro, nombre_beneficiario, parentesco) "
                  + "VALUES (?, ?, ?)")) {

                ps.setString(1, vida.getNumero());
                ps.setString(2, vida.getNombreBeneficiario());
                ps.setString(3, vida.getParentesco());
                ps.executeUpdate();
            }

        } else {

            SeguroVehiculo vehiculo = (SeguroVehiculo) seguro;

            try (PreparedStatement ps = conexion.prepareStatement(
                    "INSERT INTO seguro_vehiculo (numero_seguro, marca) "
                  + "VALUES (?, ?)")) {

                ps.setString(1, vehiculo.getNumero());
                ps.setString(2, vehiculo.getMarca());
                ps.executeUpdate();
            }
        }
    }

    /**
     * Actualiza la fila hija segun el tipo de seguro.
     */
    private void actualizarDetalle(Connection conexion, Seguro seguro)
            throws SQLException {

        if (seguro instanceof SeguroVida) {

            SeguroVida vida = (SeguroVida) seguro;

            try (PreparedStatement ps = conexion.prepareStatement(
                    "UPDATE seguro_vida "
                  + "SET nombre_beneficiario=?, parentesco=? "
                  + "WHERE numero_seguro=?")) {

                ps.setString(1, vida.getNombreBeneficiario());
                ps.setString(2, vida.getParentesco());
                ps.setString(3, vida.getNumero());
                ps.executeUpdate();
            }

        } else {

            SeguroVehiculo vehiculo = (SeguroVehiculo) seguro;

            try (PreparedStatement ps = conexion.prepareStatement(
                    "UPDATE seguro_vehiculo SET marca=? "
                  + "WHERE numero_seguro=?")) {

                ps.setString(1, vehiculo.getMarca());
                ps.setString(2, vehiculo.getNumero());
                ps.executeUpdate();
            }
        }
    }

    /**
     * Convierte una fila de PostgreSQL en el seguro de su tipo.
     *
     * @param rs resultado SQL
     * @return seguro construido
     * @throws SQLException si ocurre un error de lectura
     */
    private Seguro mapearSeguro(ResultSet rs) throws SQLException {

        Asegurado asegurado = new Asegurado(
                rs.getString("identificacion"),
                rs.getString("nombre"),
                rs.getString("correo_electronico"));

        String tipo = rs.getString("tipo");

        if (SeguroVida.TIPO.equals(tipo)) {

            return new SeguroVida(
                    rs.getString("numero"),
                    rs.getDate("fecha_expedicion").toLocalDate(),
                    rs.getBigDecimal("valor"),
                    EstadoSeguro.valueOf(rs.getString("estado")),
                    asegurado,
                    rs.getString("nombre_beneficiario"),
                    rs.getString("parentesco"));
        }

        if (SeguroVehiculo.TIPO.equals(tipo)) {

            return new SeguroVehiculo(
                    rs.getString("numero"),
                    rs.getDate("fecha_expedicion").toLocalDate(),
                    rs.getBigDecimal("valor"),
                    EstadoSeguro.valueOf(rs.getString("estado")),
                    asegurado,
                    rs.getString("marca"));
        }

        throw new ConexionBDException(
                "Tipo de seguro desconocido en la base de datos: " + tipo + ".");
    }

    /**
     * Verifica que el seguro sea de un tipo soportado.
     *
     * @param seguro seguro recibido
     */
    private void validarTipo(Seguro seguro) {

        if (!(seguro instanceof SeguroVida)
                && !(seguro instanceof SeguroVehiculo)) {

            throw new SeguroValidacionException(
                    "Solo se admiten seguros de vida y de vehiculo.");
        }
    }

    /**
     * Traduce errores PostgreSQL a excepciones de dominio.
     *
     * @param e      excepcion SQL
     * @param numero numero del seguro involucrado
     * @return excepcion correspondiente
     */
    private RuntimeException traducirExcepcion(SQLException e, String numero) {

        String estado = e.getSQLState();

        if (SQLSTATE_UNIQUE.equals(estado)) {
            return new SeguroDuplicadoException(numero);
        }

        if (SQLSTATE_FK.equals(estado)) {
            return new SeguroValidacionException(
                    "El asegurado seleccionado no existe.");
        }

        if (SQLSTATE_CHECK.equals(estado)
                || SQLSTATE_NOT_NULL.equals(estado)
                || SQLSTATE_TOO_LONG.equals(estado)
                || SQLSTATE_OUT_OF_RANGE.equals(estado)) {

            return new SeguroValidacionException(
                    "Los datos no cumplen las restricciones de la base de datos.");
        }

        return new ConexionBDException(
                "No fue posible guardar el seguro.", e);
    }

    /**
     * Revierte una transaccion.
     *
     * @param conexion conexion activa
     */
    private void rollback(Connection conexion) {

        if (conexion != null) {

            try {
                conexion.rollback();
            } catch (SQLException ignored) {
                // Nada mas que hacer: la conexion se cierra enseguida.
            }
        }
    }

    /**
     * Cierra una conexion.
     *
     * @param conexion conexion activa
     */
    private void cerrar(Connection conexion) {

        if (conexion != null) {

            try {
                conexion.setAutoCommit(true);
                conexion.close();
            } catch (SQLException ignored) {
                // Nada mas que hacer.
            }
        }
    }
}
