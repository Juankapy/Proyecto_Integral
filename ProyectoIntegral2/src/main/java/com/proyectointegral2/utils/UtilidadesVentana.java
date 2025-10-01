package com.proyectointegral2.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Stack; // Para el historial de navegación

public class UtilidadesVentana {

    private static Stage primaryStageRef;

    private static final double DEFAULT_FIXED_WIDTH = 814;
    private static final double DEFAULT_FIXED_HEIGHT = 550;
    private static final double MIN_DYNAMIC_WIDTH = 900;
    private static final double MIN_DYNAMIC_HEIGHT = 700;

    public static double getDefaultFixedWidth() {
        return DEFAULT_FIXED_WIDTH;
    }

    public static double getDefaultFixedHeight() {
        return DEFAULT_FIXED_HEIGHT;
    }

    public static void mostrarVentanaComoDialogo(Parent rootNode, String title, Stage ownerStage) {
        if (rootNode == null) {
            System.err.println("Error: Nodo raíz nulo para mostrar como diálogo.");
            mostrarAlertaError("Error de Ventana", "No se pudo mostrar la ventana de diálogo.");
            return;
        }
        try {
            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            if (ownerStage != null) {
                dialogStage.initOwner(ownerStage);
            } else if (primaryStageRef != null) {
                dialogStage.initOwner(primaryStageRef);
            }

            Scene scene = new Scene(rootNode);
            dialogStage.setScene(scene);

            dialogStage.setResizable(false);

            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error de Diálogo", "Ocurrió un error al mostrar la ventana de diálogo.");
        }
    }

    private static class VistaAnterior {
        String fxmlFile;
        String title;
        boolean esDinamicaYMaximizada;

        VistaAnterior(String fxmlFile, String title, boolean esDinamicaYMaximizada) {
            this.fxmlFile = fxmlFile;
            this.title = title;
            this.esDinamicaYMaximizada = esDinamicaYMaximizada;
        }
    }
    private static Stack<VistaAnterior> historialNavegacion = new Stack<>();
    private static String fxmlActual = null;


    public static void setPrimaryStage(Stage stage) {
        primaryStageRef = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStageRef;
    }

    private static void configurarStageParaVentanaFixed(String title) {
        if (primaryStageRef == null) return;
        primaryStageRef.setTitle(title);
        primaryStageRef.setResizable(false);
        primaryStageRef.setFullScreen(false);
        primaryStageRef.setMaximized(false);

        Scene scene = primaryStageRef.getScene();
        if (scene != null && scene.getRoot() != null) {
            Parent root = scene.getRoot();
            double prefW = root.prefWidth(-1);
            double prefH = root.prefHeight(-1);
            if (prefW > 0 && prefW != javafx.scene.layout.Region.USE_COMPUTED_SIZE &&
                    prefH > 0 && prefH != javafx.scene.layout.Region.USE_COMPUTED_SIZE) {
                primaryStageRef.setWidth(prefW);
                primaryStageRef.setHeight(prefH);
            } else {
                primaryStageRef.setWidth(DEFAULT_FIXED_WIDTH);
                primaryStageRef.setHeight(DEFAULT_FIXED_HEIGHT);
            }
        } else {
            primaryStageRef.setWidth(DEFAULT_FIXED_WIDTH);
            primaryStageRef.setHeight(DEFAULT_FIXED_HEIGHT);
        }
        primaryStageRef.centerOnScreen();
    }

    private static void configurarStageVentanaDinamicaMaximizada(String title) {
        if (primaryStageRef == null) return;
        primaryStageRef.setTitle(title);
        primaryStageRef.setResizable(true);
        primaryStageRef.setFullScreen(false);
        primaryStageRef.setMinWidth(MIN_DYNAMIC_WIDTH);
        primaryStageRef.setMinHeight(MIN_DYNAMIC_HEIGHT);
        primaryStageRef.setMaximized(true);
    }

    public static void cambiarEscena(String fxmlFile, String title, boolean esDinamicaYMaximizada) {
        if (primaryStageRef == null) { return; }

        if (fxmlActual != null && !fxmlActual.equals(fxmlFile)) {

            boolean actualEsDinamica = primaryStageRef.isMaximized() || primaryStageRef.isFullScreen();

            if(fxmlActual != null) {
                historialNavegacion.push(new VistaAnterior(fxmlActual, primaryStageRef.getTitle(), actualEsDinamica));
                System.out.println("Historial: Añadido " + fxmlActual);
            }
        }


        try {
            URL resourceUrl = UtilidadesVentana.class.getResource(fxmlFile);
            if (resourceUrl == null) { return; }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            Scene currentScene = primaryStageRef.getScene();
            if (currentScene == null) {
                currentScene = new Scene(root);
                primaryStageRef.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }

            if (esDinamicaYMaximizada) {
                configurarStageVentanaDinamicaMaximizada(title);
            } else {
                configurarStageParaVentanaFixed(title);
            }
            fxmlActual = fxmlFile;

        } catch (IOException e) {  }
    }


    public static void volverAEscenaAnterior() {
        if (primaryStageRef == null) {
            System.err.println("Error: PrimaryStage no inicializado.");
            return;
        }
        if (historialNavegacion.isEmpty()) {
            System.out.println("No hay escena anterior en el historial.");
            return;
        }

        VistaAnterior vista = historialNavegacion.pop();
        System.out.println("Historial: Volviendo a " + vista.fxmlFile);

        try {
            URL resourceUrl = UtilidadesVentana.class.getResource(vista.fxmlFile);
            if (resourceUrl == null) {
                System.err.println("Error: No se pudo encontrar FXML anterior: " + vista.fxmlFile);
                if (!historialNavegacion.isEmpty()) volverAEscenaAnterior();
                else mostrarAlertaError("Error de Navegación", "No se pudo volver a la vista anterior.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            Scene currentScene = primaryStageRef.getScene();
            currentScene.setRoot(root);

            if (vista.esDinamicaYMaximizada) {
                configurarStageVentanaDinamicaMaximizada(vista.title);
            } else {
                configurarStageParaVentanaFixed(vista.title);
            }
            fxmlActual = vista.fxmlFile;

        } catch (IOException e) {
            System.err.println("Error al cargar FXML anterior: " + vista.fxmlFile);
            e.printStackTrace();
            mostrarAlertaError("Error de Carga", "Ocurrió un error al volver a la vista anterior.");
        }
    }

    public static void cambiarEscenaConRoot(Parent newRoot, String title, boolean esDinamicaYMaximizada) {
        if (primaryStageRef == null) { return; }
        String fxmlAnterior = null;
        boolean actualEraDinamica = false;
        if (primaryStageRef.getScene() != null && primaryStageRef.getScene().getRoot() != null &&
                primaryStageRef.getScene().getRoot().getProperties().containsKey("fxmlLocation")) { // (A)
            fxmlAnterior = (String) primaryStageRef.getScene().getRoot().getProperties().get("fxmlLocation");
            actualEraDinamica = primaryStageRef.isMaximized() || primaryStageRef.isFullScreen();
        }

        if (fxmlAnterior != null) {
            historialNavegacion.push(new VistaAnterior(fxmlAnterior, primaryStageRef.getTitle(), actualEraDinamica));
            System.out.println("Historial (con root): Añadido '" + fxmlAnterior + "' (era dinámica: " + actualEraDinamica + ")");
        }

        Scene currentScene = primaryStageRef.getScene();
        if (currentScene == null) {
            currentScene = new Scene(newRoot);
            primaryStageRef.setScene(currentScene);
        } else {
            currentScene.setRoot(newRoot);
        }

        if (esDinamicaYMaximizada) {
            configurarStageVentanaDinamicaMaximizada(title);
        } else {
            configurarStageParaVentanaFixed(title);
        }
    }

    public static <T> T mostrarVentanaPopup(String fxmlFile, String titulo, boolean esModal, Stage owner) {
        try {
            URL resourceUrl = UtilidadesVentana.class.getResource(fxmlFile);
            if (resourceUrl == null) {
                System.err.println("Error: No se pudo encontrar el archivo FXML para el pop-up: " + fxmlFile);
                mostrarAlertaError("Error de Pop-up", "Vista no encontrada: " + fxmlFile);
                return null;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle(titulo);

            Scene popupScene = new Scene(root);
            popupStage.setScene(popupScene);
            popupStage.setResizable(false);

            if (esModal) {
                popupStage.initModality(Modality.WINDOW_MODAL);
                if (owner != null) {
                    popupStage.initOwner(owner);
                } else if (primaryStageRef != null) {
                    popupStage.initOwner(primaryStageRef);
                }
            }
            popupStage.showAndWait();

            return loader.getController();

        } catch (IOException e) {
            System.err.println("Error al cargar FXML para el pop-up: " + fxmlFile);
            e.printStackTrace();
            mostrarAlertaError("Error de Carga", "Ocurrió un error al cargar ventana emergente:\n" + fxmlFile);
            return null;
        }
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
    public static boolean mostrarAlertaConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void mostrarAlertaAdvertencia(String titulo, String cabecera, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    public static void setFxmlActual(String fxmlPath) {
        fxmlActual = fxmlPath;
    }
}