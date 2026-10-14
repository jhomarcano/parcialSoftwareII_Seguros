package co.edu.poli.sw2.ProyectoSeguros.infraestructura.web;

import java.io.IOException;

import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.BuscarSeguroUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.CrearSeguroUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.EliminarSeguroUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.ListarSegurosUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.entrada.ModificarSeguroUseCase;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.puerto.salida.SeguroRepository;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio.BuscarSeguroServicio;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio.CrearSeguroServicio;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio.EliminarSeguroServicio;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio.ListarSegurosServicio;
import co.edu.poli.sw2.ProyectoSeguros.aplicacion.servicio.ModificarSeguroServicio;
import co.edu.poli.sw2.ProyectoSeguros.infraestructura.persistencia.PostgresSeguroRepository;
import co.edu.poli.sw2.ProyectoSeguros.infraestructura.ui.ControlSeguro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de entrada principal de la aplicacion.
 *
 * <p>
 * Esta clase realiza el wiring de la arquitectura hexagonal.
 * Construye el adaptador de salida, los servicios de aplicacion
 * y finalmente el adaptador de entrada JavaFX.
 * </p>
 *
 * @author Alejandra Cano y Juan Rosero
 */
public class App extends Application {

    /**
     * Construye las dependencias de la aplicacion y carga
     * la interfaz JavaFX.
     *
     * @param stage escenario principal de JavaFX
     * @throws IOException si no es posible cargar el archivo FXML
     */
    @Override
    public void start(Stage stage) throws IOException {

        // ADAPTADOR DE SALIDA
        SeguroRepository seguroRepository = new PostgresSeguroRepository();

        // SERVICIOS / CASOS DE USO
        CrearSeguroUseCase crearSeguro = new CrearSeguroServicio(seguroRepository);
        ModificarSeguroUseCase modificarSeguro = new ModificarSeguroServicio(seguroRepository);
        EliminarSeguroUseCase eliminarSeguro = new EliminarSeguroServicio(seguroRepository);
        BuscarSeguroUseCase buscarSeguro = new BuscarSeguroServicio(seguroRepository);
        ListarSegurosUseCase listarSeguros = new ListarSegurosServicio(seguroRepository);

        // CARGADOR FXML
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/co/edu/poli/sw2/seguros/vista/seguro.fxml"));

        // INYECCION DE LOS PUERTOS EN EL ADAPTADOR DE ENTRADA
        loader.setControllerFactory(controllerClass -> {

            if (controllerClass == ControlSeguro.class) {

                return new ControlSeguro(
                        crearSeguro,
                        modificarSeguro,
                        eliminarSeguro,
                        buscarSeguro,
                        listarSeguros);
            }

            try {

                return controllerClass.getDeclaredConstructor().newInstance();

            } catch (Exception e) {

                throw new RuntimeException(
                        "No fue posible crear el controlador.", e);
            }
        });

        Parent root = loader.load();

        Scene scene = new Scene(root, 1180, 720);

        stage.setTitle("Gestion de Seguros - Arquitectura Hexagonal");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Inicia la aplicacion JavaFX.
     *
     * @param args argumentos de linea de comandos
     */
    public static void main(String[] args) {

        launch(args);
    }
}
