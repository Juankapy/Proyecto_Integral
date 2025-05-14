package com.proyectointegral2.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class UtilidadesVentana {

    private static Stage primaryStageRef;

    public static void setPrimaryStage(Stage stage) {
        primaryStageRef = stage;
    }

    private static final double DEFAULT_FIXED_WIDTH = 814;
    private static final double DEFAULT_FIXED_HEIGHT = 550;

    private static void configureStageForFixedView(String title) {
        if (primaryStageRef == null) return;
        primaryStageRef.setTitle(title);
        primaryStageRef.setResizable(false);
        primaryStageRef.setFullScreen(false);
        primaryStageRef.setMaximized(false);
        primaryStageRef.sizeToScene();
        primaryStageRef.centerOnScreen();
    }

    private static void configureStageForDynamicView(String title) {
        if (primaryStageRef == null) return;
        primaryStageRef.setTitle(title);
        primaryStageRef.setResizable(true);
        primaryStageRef.setFullScreen(true);
    }

    public static void cambiarEscena(String fxmlFile, String title, boolean isDynamicSize) {
        if (primaryStageRef == null) {
            System.err.println("Error en UtilidadesVentana: PrimaryStage no ha sido inicializado. Llama a setPrimaryStage() primero.");
            return;
        }
        try {
            URL resourceUrl = UtilidadesVentana.class.getResource(fxmlFile);
            if (resourceUrl == null) {
                System.err.println("Error: No se pudo encontrar el archivo FXML: " + fxmlFile + ". Verifica la ruta.");
                mostrarAlertaError("Error de Navegación", "No se pudo cargar la vista solicitada:\n" + fxmlFile);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            Scene currentScene = primaryStageRef.getScene();
            if (currentScene == null) {
                currentScene = new Scene(root);
                primaryStageRef.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }

            if (isDynamicSize) {
                configureStageForDynamicView(title);
            } else {
                configureStageForFixedView(title);
            }

        } catch (IOException e) {
            System.err.println("Error al cargar FXML: " + fxmlFile);
            e.printStackTrace();
            mostrarAlertaError("Error de Carga", "Ocurrió un error al intentar cargar la vista:\n" + fxmlFile);
        }
    }

    public static void cambiarEscena(String fxmlFile, String title) {
        cambiarEscena(fxmlFile, title, false); // Por defecto, las vistas son de tamaño fijo
    }


    public static void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void mostrarAlertaInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // En UtilidadesVentana.java (NUEVO MÉTODO DE EJEMPLO)
    public static void cambiarEscenaConRoot(Parent newRoot, String title, boolean isDynamicSize) {
        if (primaryStageRef == null) { /* ... error ... */ return; }

        Scene currentScene = primaryStageRef.getScene();
        if (currentScene == null) {
            currentScene = new Scene(newRoot);
            primaryStageRef.setScene(currentScene);
        } else {
            currentScene.setRoot(newRoot);
        }

        if (isDynamicSize) {
            configureStageForDynamicView(title); // Reusa tu método existente
        } else {
            configureStageForFixedView(title);  // Reusa tu método existente
        }
    }
    //  más tipos de alertas (CONFIRMATION, WARNING)
}