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
        URL fxmlUrl = MainApp.class.getResource(initialFxmlPath);

        if (fxmlUrl == null) {
            System.err.println("¡ERROR CRÍTICO! No se pudo encontrar el FXML inicial en: " + initialFxmlPath);
            UtilidadesVentana.mostrarAlertaError
                    ("Error Crítico", "No se pudo iniciar la aplicación." +
                    "\nArchivo FXML principal no encontrado.");
            throw new IOException("No se pudo encontrar el recurso FXML inicial: " + initialFxmlPath);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        UtilidadesVentana.cambiarEscena(initialFxmlPath, "Inicio de Sesión - Dogpuccino", false); // Carga inicial con config fija
        stage.show();
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