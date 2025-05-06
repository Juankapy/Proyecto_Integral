package main.java.com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main/resources/com/proyecto_integral/Vista/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 814, 550);
        stage.setScene(scene);
        stage.setTitle("Inicio de Sesión - Dogpuccino");
        stage.setResizable(false);
        stage.setMinWidth(814);
        stage.setMinHeight(550);
        stage.setMaxWidth(814);
        stage.setMaxHeight(550);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}