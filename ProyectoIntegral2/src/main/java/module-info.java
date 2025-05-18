module com.proyectointegral2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires javafx.base;
    requires java.desktop;
    requires jdk.compiler;

    opens com.proyectointegral2.Controller to javafx.fxml;
    opens com.proyectointegral2.Model to javafx.base, javafx.fxml;
    exports com.proyectointegral2 ;
}