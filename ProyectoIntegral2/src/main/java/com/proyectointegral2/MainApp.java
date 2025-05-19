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

    public void start(Stage stage) throws IOException {
        UtilidadesVentana.setPrimaryStage(stage);

        String initialFxmlPath = "/com/proyectointegral2/Vista/Login.fxml";
        String initialTitle = "Inicio de Sesión - Dogpuccino";

        try {
            URL resourceUrl = MainApp.class.getResource(initialFxmlPath);
            if (resourceUrl == null) {
                UtilidadesVentana.mostrarAlertaError("Error Crítico", "FXML inicial no encontrado: " + initialFxmlPath);
                throw new IOException("FXML inicial no encontrado: " + initialFxmlPath);
            }
            FXMLLoader fxmlLoader = new FXMLLoader(resourceUrl);
            Parent root = fxmlLoader.load();
            root.getProperties().put("fxmlLocation", initialFxmlPath);

            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.setTitle(initialTitle);
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.setMaximized(false);

            Parent sceneRoot = scene.getRoot();
            double prefW = sceneRoot.prefWidth(-1);
            double prefH = sceneRoot.prefHeight(-1);
            double targetWidth = (prefW > 0 && prefW != javafx.scene.layout.Region.USE_COMPUTED_SIZE)
                ? prefW : UtilidadesVentana.getDefaultFixedWidth();
            double targetHeight = (prefH > 0 && prefH != javafx.scene.layout.Region.USE_COMPUTED_SIZE)
                ? prefH : UtilidadesVentana.getDefaultFixedHeight();
            stage.setWidth(targetWidth);
            stage.setHeight(targetHeight);
            stage.centerOnScreen();

            stage.show();
        } catch (IOException e) {
            System.err.println("¡ERROR CRÍTICO! No se pudo cargar el FXML inicial en: " + initialFxmlPath);
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