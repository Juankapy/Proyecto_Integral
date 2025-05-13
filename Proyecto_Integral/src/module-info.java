module main.java.com {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens main.java.com.Controller to javafx.fxml;
    opens main.java.com to javafx.graphics;
}