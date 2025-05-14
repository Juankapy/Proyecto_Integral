package com.proyectointegral2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/com/proyectointegral2/Vista/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("Inicio sesion - Dogpuccino");
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