/**
 * Modulo principal de la aplicacion de gestion de seguros.
 *
 * @author Alejandra Cano 
 */
module co.edu.poli.sw2.seguros {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires org.postgresql.jdbc;

    /*
     * JavaFX necesita acceso reflexivo al
     * adaptador de entrada.
     */
    opens co.edu.poli.sw2.ProyectoSeguros.infraestructura.ui
            to javafx.fxml;

    /*
     * TableView utiliza reflexion para leer
     * propiedades como numero, tipo, valor y estado.
     */
    opens co.edu.poli.sw2.ProyectoSeguros.dominio.modelo
            to javafx.base, javafx.fxml;

    exports co.edu.poli.sw2.ProyectoSeguros.infraestructura.web;
    exports co.edu.poli.sw2.ProyectoSeguros.dominio.modelo;
}
