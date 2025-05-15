package com.proyectointegral2;

import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        UtilidadesVentana.setPrimaryStage(stage);

        String initialFxmlPath = "/com/proyectointegral2/Vista/Login.fxml";
        String initialTitle = "Inicio de Sesión - Dogpuccino";

        try {
            URL resourceUrl = MainApp.class.getResource(initialFxmlPath);
            if (resourceUrl == null) { /* ... error ... */
                throw new IOException(/*...*/);
            }
            FXMLLoader fxmlLoader = new FXMLLoader(resourceUrl);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.setTitle(initialTitle);
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.setMaximized(false);
            stage.sizeToScene();
            stage.centerOnScreen();

            UtilidadesVentana.setFxmlActual(initialFxmlPath);

            stage.show();
        } catch (IOException e) {
            System.err.println("¡ERROR CRÍTICO! No se pudo encontrar o cargar el FXML inicial en: " + initialFxmlPath);
            UtilidadesVentana.mostrarAlertaError("Error Crítico", "No se pudo iniciar la aplicación.");
            throw e;
        }
    }


    private static void configureStageForFixedView(Stage stage, String title) {
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.setMaximized(false);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}