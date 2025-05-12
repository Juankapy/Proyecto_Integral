module com.proyectointegral2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.proyectointegral2.Controller to javafx.fxml;
    exports com.proyectointegral2;
}