package co.edu.poli.sw2.ProyectoSeguros.infraestructura.ui;

import java.net.URL;
import java.util.ResourceBundle;

import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.BuscarSeguroUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.CrearSeguroUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.DatosSeguro;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.EliminarSeguroUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.ListarSegurosUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.ModificarSeguroUseCase;
import co.edu.poli.sw2.ProyectoSeguros.dominio.excepcion.SeguroException;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Asegurado;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.EstadoSeguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.Seguro;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVehiculo;
import co.edu.poli.sw2.ProyectoSeguros.dominio.modelo.SeguroVida;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Adaptador de entrada JavaFX para la gestion de seguros.
 *
 * <p>
 * El controlador recibe los casos de uso de la aplicacion mediante
 * puertos de entrada y no conoce la implementacion de persistencia
 * ni la base de datos.
 * </p>
 *
 * <p>
 * Al editar un seguro existente el numero y el tipo quedan bloqueados,
 * porque identifican al registro. Pulse <b>Limpiar</b> para volver a
 * crear uno nuevo.
 * </p>
 *
 * @author Alejandra Cano y Juan Rosero
 */
public class ControlSeguro implements Initializable {

    private static final String TIPO_VIDA = SeguroVida.TIPO;
    private static final String TIPO_VEHICULO = SeguroVehiculo.TIPO;

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtNumero;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtValor;
    @FXML private ComboBox<EstadoSeguro> cmbEstado;
    @FXML private ComboBox<Asegurado> cmbAsegurado;

    @FXML private VBox boxVida;
    @FXML private TextField txtBeneficiario;
    @FXML private TextField txtParentesco;

    @FXML private VBox boxVehiculo;
    @FXML private TextField txtMarca;

    @FXML private TextField txtBuscarNumero;
    @FXML private Label lblMensaje;

    @FXML private TableView<Seguro> tablaSeguros;
    @FXML private TableColumn<Seguro, String> colNumero;
    @FXML private TableColumn<Seguro, String> colTipo;
    @FXML private TableColumn<Seguro, String> colAsegurado;
    @FXML private TableColumn<Seguro, String> colFecha;
    @FXML private TableColumn<Seguro, String> colValor;
    @FXML private TableColumn<Seguro, String> colEstado;
    @FXML private TableColumn<Seguro, String> colDetalle;

    private final CrearSeguroUseCase crearSeguroUseCase;
    private final ModificarSeguroUseCase modificarSeguroUseCase;
    private final EliminarSeguroUseCase eliminarSeguroUseCase;
    private final BuscarSeguroUseCase buscarSeguroUseCase;
    private final ListarSegurosUseCase listarSegurosUseCase;

    /** Seguro seleccionado actualmente en la tabla. */
    private Seguro seguroSeleccionado;

    /**
     * Construye el adaptador de entrada con todos los casos de uso
     * necesarios para gestionar seguros.
     *
     * @param crearSeguroUseCase     caso de uso de creacion
     * @param modificarSeguroUseCase caso de uso de modificacion
     * @param eliminarSeguroUseCase  caso de uso de eliminacion
     * @param buscarSeguroUseCase    caso de uso de busqueda
     * @param listarSegurosUseCase   caso de uso de listado
     */
    public ControlSeguro(
            CrearSeguroUseCase crearSeguroUseCase,
            ModificarSeguroUseCase modificarSeguroUseCase,
            EliminarSeguroUseCase eliminarSeguroUseCase,
            BuscarSeguroUseCase buscarSeguroUseCase,
            ListarSegurosUseCase listarSegurosUseCase) {

        this.crearSeguroUseCase = crearSeguroUseCase;
        this.modificarSeguroUseCase = modificarSeguroUseCase;
        this.eliminarSeguroUseCase = eliminarSeguroUseCase;
        this.buscarSeguroUseCase = buscarSeguroUseCase;
        this.listarSegurosUseCase = listarSegurosUseCase;
    }

    /**
     * Inicializa los componentes JavaFX despues de cargar el FXML.
     *
     * @param location  ubicacion del recurso
     * @param resources recursos de internacionalizacion
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cmbTipo.setItems(FXCollections.observableArrayList(TIPO_VIDA, TIPO_VEHICULO));
        cmbTipo.valueProperty().addListener(
                (obs, anterior, nuevo) -> actualizarCamposPorTipo(nuevo));
        cmbTipo.setValue(TIPO_VIDA);

        cmbEstado.setItems(FXCollections.observableArrayList(EstadoSeguro.values()));
        cmbEstado.setValue(EstadoSeguro.ACTIVO);

        cargarAsegurados();
        configurarTabla();
        listarSeguros();

        tablaSeguros.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, nuevo) -> {

                    if (nuevo != null) {
                        seguroSeleccionado = nuevo;
                        cargarFormulario(nuevo);
                    }
                });
    }

    private void cargarAsegurados() {

        try {

            cmbAsegurado.setItems(FXCollections.observableArrayList(
                    listarSegurosUseCase.listarAsegurados()));

        } catch (SeguroException e) {

            mostrarMensaje(e.getMessage(), true);

        } catch (Exception e) {

            mostrarErrorInesperado(e);
        }
    }

    private void configurarTabla() {

        colNumero.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNumero()));

        colTipo.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTipo()));

        colAsegurado.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getAsegurado().getNombre()));

        colFecha.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.valueOf(d.getValue().getFechaExpedicion())));

        colValor.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getValor().toPlainString()));

        colEstado.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEstado().name()));

        colDetalle.setCellValueFactory(d -> {

            Seguro seguro = d.getValue();

            if (seguro instanceof SeguroVida) {
                SeguroVida vida = (SeguroVida) seguro;
                return new SimpleStringProperty(
                        vida.getNombreBeneficiario() + " (" + vida.getParentesco() + ")");
            }

            if (seguro instanceof SeguroVehiculo) {
                return new SimpleStringProperty(
                        ((SeguroVehiculo) seguro).getMarca());
            }

            return new SimpleStringProperty("");
        });
    }

    /** Ejecuta el caso de uso de creacion con los datos del formulario. */
    @FXML
    private void crearSeguro() {

        try {

            if (seguroSeleccionado != null) {

                mostrarMensaje(
                        "Pulse Limpiar antes de crear un seguro nuevo.", true);

                return;
            }

            crearSeguroUseCase.crear(leerFormulario());

            listarSeguros();
            limpiarFormulario();

            mostrarMensaje("Seguro creado correctamente.", false);

        } catch (SeguroException e) {

            mostrarMensaje(e.getMessage(), true);

        } catch (Exception e) {

            mostrarErrorInesperado(e);
        }
    }

    /** Ejecuta el caso de uso de modificacion para el seguro seleccionado. */
    @FXML
    private void modificarSeguro() {

        try {

            if (seguroSeleccionado == null) {

                mostrarMensaje("Seleccione un seguro para modificar.", true);

                return;
            }

            modificarSeguroUseCase.modificar(seguroSeleccionado, leerFormulario());

            listarSeguros();
            limpiarFormulario();

            mostrarMensaje("Seguro modificado correctamente.", false);

        } catch (SeguroException e) {

            mostrarMensaje(e.getMessage(), true);

        } catch (Exception e) {

            mostrarErrorInesperado(e);
        }
    }

    /** Ejecuta el caso de uso de eliminacion para el seguro seleccionado. */
    @FXML
    private void eliminarSeguro() {

        try {

            if (seguroSeleccionado == null) {

                mostrarMensaje("Seleccione un seguro para eliminar.", true);

                return;
            }

            eliminarSeguroUseCase.eliminar(seguroSeleccionado);

            listarSeguros();
            limpiarFormulario();

            mostrarMensaje("Seguro eliminado correctamente.", false);

        } catch (SeguroException e) {

            mostrarMensaje(e.getMessage(), true);

        } catch (Exception e) {

            mostrarErrorInesperado(e);
        }
    }

    /** Busca un seguro por el numero escrito por el usuario. */
    @FXML
    private void buscarSeguro() {

        try {

            Seguro seguro = buscarSeguroUseCase.buscarPorNumero(
                    txtBuscarNumero.getText());

            tablaSeguros.setItems(FXCollections.observableArrayList(seguro));
            tablaSeguros.getSelectionModel().select(seguro);

            seguroSeleccionado = seguro;
            cargarFormulario(seguro);

            mostrarMensaje("Seguro encontrado.", false);

        } catch (SeguroException e) {

            mostrarMensaje(e.getMessage(), true);

        } catch (Exception e) {

            mostrarErrorInesperado(e);
        }
    }

    /** Obtiene nuevamente todos los seguros registrados. */
    @FXML
    private void listarSeguros() {

        try {

            tablaSeguros.setItems(FXCollections.observableArrayList(
                    listarSegurosUseCase.listar()));

        } catch (SeguroException e) {

            mostrarMensaje(e.getMessage(), true);

        } catch (Exception e) {

            mostrarErrorInesperado(e);
        }
    }

    /** Limpia el formulario y vuelve al modo de creacion. */
    @FXML
    private void limpiarFormulario() {

        txtNumero.clear();
        dpFecha.setValue(null);
        txtValor.clear();
        txtBeneficiario.clear();
        txtParentesco.clear();
        txtMarca.clear();
        txtBuscarNumero.clear();

        cmbEstado.setValue(EstadoSeguro.ACTIVO);
        cmbAsegurado.setValue(null);
        cmbTipo.setValue(TIPO_VIDA);

        txtNumero.setDisable(false);
        cmbTipo.setDisable(false);

        tablaSeguros.getSelectionModel().clearSelection();

        seguroSeleccionado = null;
    }

    /** Reune los datos escritos en el formulario. */
    private DatosSeguro leerFormulario() {

        return new DatosSeguro(
                cmbTipo.getValue(),
                txtNumero.getText(),
                dpFecha.getValue(),
                txtValor.getText(),
                cmbEstado.getValue(),
                cmbAsegurado.getValue(),
                txtBeneficiario.getText(),
                txtParentesco.getText(),
                txtMarca.getText());
    }

    /** Carga en el formulario los datos de un seguro seleccionado. */
    private void cargarFormulario(Seguro seguro) {

        cmbTipo.setValue(seguro.getTipo());

        txtNumero.setText(seguro.getNumero());
        dpFecha.setValue(seguro.getFechaExpedicion());
        txtValor.setText(seguro.getValor().toPlainString());
        cmbEstado.setValue(seguro.getEstado());
        cmbAsegurado.setValue(seguro.getAsegurado());

        if (seguro instanceof SeguroVida) {

            SeguroVida vida = (SeguroVida) seguro;

            txtBeneficiario.setText(vida.getNombreBeneficiario());
            txtParentesco.setText(vida.getParentesco());
            txtMarca.clear();

        } else if (seguro instanceof SeguroVehiculo) {

            txtMarca.setText(((SeguroVehiculo) seguro).getMarca());
            txtBeneficiario.clear();
            txtParentesco.clear();
        }

        // El numero y el tipo identifican al registro: no se editan.
        txtNumero.setDisable(true);
        cmbTipo.setDisable(true);
    }

    /** Muestra solo los campos propios del tipo elegido. */
    private void actualizarCamposPorTipo(String tipo) {

        boolean vida = TIPO_VIDA.equals(tipo);

        boxVida.setVisible(vida);
        boxVida.setManaged(vida);

        boxVehiculo.setVisible(!vida);
        boxVehiculo.setManaged(!vida);
    }

    private void mostrarMensaje(String mensaje, boolean error) {

        lblMensaje.setText(mensaje);

        lblMensaje.getStyleClass().removeAll("mensaje-ok", "mensaje-error");

        lblMensaje.getStyleClass().add(error ? "mensaje-error" : "mensaje-ok");
    }

    private void mostrarErrorInesperado(Exception excepcion) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("Ocurrio un error inesperado");
        alert.setContentText(excepcion.getMessage());

        alert.showAndWait();
    }
}
