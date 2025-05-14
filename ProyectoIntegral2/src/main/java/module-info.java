module com.proyectointegral2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;

    opens com.proyectointegral2.Controller to javafx.fxml;
    exports com.proyectointegral2;
}