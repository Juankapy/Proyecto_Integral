module main.java.com {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com to javafx.fxml;
    opens com.Controller to javafx.fxml;
}