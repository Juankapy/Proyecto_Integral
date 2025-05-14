package com.proyectointegral2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    private static Stage primaryStage;
    private static final double FIXED_VIEW_WIDTH = 815;
    private static final double FIXED_VIEW_HEIGHT = 550;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        String initialFxmlPath = "/com/proyectointegral2/Vista/Login.fxml";
        URL fxmlUrl = MainApp.class.getResource(initialFxmlPath);

        if (fxmlUrl == null) {
            System.err.println("¡ERROR CRÍTICO! No se pudo encontrar el FXML inicial en: " + initialFxmlPath);
            throw new IOException("No se pudo encontrar el recurso FXML inicial: " + initialFxmlPath);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Inicio de Sesión - Dogpuccino");

        configureStageForFixedView(stage, "Inicio de Sesión - Dogpuccino");

        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private static void configureStageForFixedView(Stage stage, String title) {
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    private static void configureStageForDynamicView(Stage stage, String title) {
        stage.setTitle(title);
        stage.setResizable(true);
        stage.setMaximized(true);

    }

    public static void changeScene(String fxmlFile, String title, boolean isDynamicSize) {
        if (primaryStage == null) {
            System.err.println("Error: PrimaryStage no ha sido inicializado. No se puede cambiar la escena.");
            return;
        }
        try {
            URL resourceUrl = MainApp.class.getResource(fxmlFile);
            if (resourceUrl == null) {
                System.err.println("Error: No se pudo encontrar el archivo FXML para cambiar escena: " + fxmlFile + ". Verifica la ruta.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            Scene currentScene = primaryStage.getScene();
            if (currentScene == null) {
                currentScene = new Scene(root);
                primaryStage.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }

            if (isDynamicSize) {
                configureStageForDynamicView(primaryStage, title);
            } else {
                configureStageForFixedView(primaryStage, title);
            }

        } catch (IOException e) {
            System.err.println("Error al cargar FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }

    public static void changeScene(String fxmlFile, String title) {
        changeScene(fxmlFile, title, false);
    }


    public static void main(String[] args) {
        launch(args);
    }
}