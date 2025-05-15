package com.proyectointegral2.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class UtilidadesVentana {

    private static Stage primaryStageRef;

    // Tamaños fijos por defecto para ventanas no dinámicas
    private static final double DEFAULT_FIXED_WIDTH = 814;
    private static final double DEFAULT_FIXED_HEIGHT = 550;

    // Tamaños mínimos para ventanas dinámicas (cuando el usuario las redimensiona manualmente)
    private static final double MIN_DYNAMIC_WIDTH = 900;
    private static final double MIN_DYNAMIC_HEIGHT = 700;


    public static void setPrimaryStage(Stage stage) {
        primaryStageRef = stage;
    }

    /**
     * Configura el stage para ventanas de tamaño fijo, no redimensionables.
     * Se centra en pantalla.
     */
    private static void configurarStageParaVentanaFixed(String title) {
        if (primaryStageRef == null) return;

        primaryStageRef.setTitle(title);
        primaryStageRef.setResizable(false);    // No redimensionable
        primaryStageRef.setFullScreen(false);   // Asegurar que no esté en pantalla completa
        primaryStageRef.setMaximized(false);    // Asegurar que no esté maximizada

        Scene scene = primaryStageRef.getScene();
        double targetWidth = DEFAULT_FIXED_WIDTH;
        double targetHeight = DEFAULT_FIXED_HEIGHT;

        if (scene != null && scene.getRoot() != null) {
            Parent root = scene.getRoot();
            double prefW = root.prefWidth(-1); // -1 para obtener el preferido
            double prefH = root.prefHeight(-1);

            if (prefW > 0 && prefW != javafx.scene.layout.Region.USE_COMPUTED_SIZE) {
                targetWidth = prefW;
            }
            if (prefH > 0 && prefH != javafx.scene.layout.Region.USE_COMPUTED_SIZE) {
                targetHeight = prefH;
            }
        }

        primaryStageRef.setWidth(targetWidth);
        primaryStageRef.setHeight(targetHeight);
        primaryStageRef.centerOnScreen();
        System.out.println("Ventana Fija Configurada: " + title + " a " + targetWidth + "x" + targetHeight);
    }

    /**
     * Configura el stage para ventanas dinámicas.
     * Se abre MAXIMIZADA (con bordes, no pantalla completa) y es redimensionable.
     */
    private static void configurarStageVentanaDinamicaMaximizada(String title) {
        if (primaryStageRef == null) return;

        primaryStageRef.setTitle(title);
        primaryStageRef.setResizable(true);     // Redimensionable
        primaryStageRef.setFullScreen(false);   // NO pantalla completa

        // Establecer tamaños mínimos para cuando el usuario la desmaximice o redimensione
        primaryStageRef.setMinWidth(MIN_DYNAMIC_WIDTH);
        primaryStageRef.setMinHeight(MIN_DYNAMIC_HEIGHT);

        // Maximizar la ventana
        primaryStageRef.setMaximized(true);
        System.out.println("Ventana Dinámica Maximizada Configurada: " + title);
    }

    /**
     * Cambia la escena actual del primaryStage.
     * @param fxmlFile Ruta al archivo FXML.
     * @param title Título de la nueva ventana.
     * @param esDinamicaYMaximizada Si es true, la ventana será redimensionable y se abrirá maximizada.
     *                             Si es false, será de tamaño fijo.
     */
    public static void cambiarEscena(String fxmlFile, String title, boolean esDinamicaYMaximizada) {
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
                // Definir un tamaño inicial antes de maximizar o fijar,
                // esto ayuda a que el primer layout ocurra con dimensiones más predecibles.
                double initialWidth = esDinamicaYMaximizada ? MIN_DYNAMIC_WIDTH : DEFAULT_FIXED_WIDTH;
                double initialHeight = esDinamicaYMaximizada ? MIN_DYNAMIC_HEIGHT : DEFAULT_FIXED_HEIGHT;
                currentScene = new Scene(root, initialWidth, initialHeight);
                primaryStageRef.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }

            if (esDinamicaYMaximizada) {
                configurarStageVentanaDinamicaMaximizada(title);
            } else {
                configurarStageParaVentanaFixed(title);
            }
            primaryStageRef.show(); // Asegurar que se muestre y apliquen los cambios

        } catch (IOException e) {
            System.err.println("Error al cargar FXML: " + fxmlFile);
            e.printStackTrace();
            mostrarAlertaError("Error de Carga", "Ocurrió un error al intentar cargar la vista:\n" + fxmlFile);
        }
    }

    // Sobrecarga para mantener compatibilidad, asume tamaño fijo por defecto
    public static void cambiarEscena(String fxmlFile, String title) {
        cambiarEscena(fxmlFile, title, false);
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

    public static void cambiarEscenaConRoot(Parent newRoot, String title, boolean esDinamicaYMaximizada) {
        if (primaryStageRef == null) {
            System.err.println("Error en UtilidadesVentana: PrimaryStage no ha sido inicializado.");
            return;
        }

        Scene currentScene = primaryStageRef.getScene();
        if (currentScene == null) {
            double initialWidth = esDinamicaYMaximizada ? MIN_DYNAMIC_WIDTH : DEFAULT_FIXED_WIDTH;
            double initialHeight = esDinamicaYMaximizada ? MIN_DYNAMIC_HEIGHT : DEFAULT_FIXED_HEIGHT;
            currentScene = new Scene(newRoot, initialWidth, initialHeight);
            primaryStageRef.setScene(currentScene);
        } else {
            currentScene.setRoot(newRoot);
        }

        if (esDinamicaYMaximizada) {
            configurarStageVentanaDinamicaMaximizada(title);
        } else {
            configurarStageParaVentanaFixed(title);
        }
        primaryStageRef.show();
    }
}