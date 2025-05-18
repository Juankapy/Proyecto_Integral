package com.proyectointegral2.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Stack; // Para el historial de navegación

public class UtilidadesVentana {

    private static Stage primaryStageRef;

    // --- Constantes de Tamaño ---
    private static final double DEFAULT_FIXED_WIDTH = 814;
    private static final double DEFAULT_FIXED_HEIGHT = 550;
    private static final double MIN_DYNAMIC_WIDTH = 900;
    private static final double MIN_DYNAMIC_HEIGHT = 700;

    // En com.proyectointegral2.utils.UtilidadesVentana
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
            dialogStage.initModality(Modality.WINDOW_MODAL); // Bloquea la ventana dueña
            if (ownerStage != null) {
                dialogStage.initOwner(ownerStage);
            } else if (primaryStageRef != null) { // Fallback al primary stage si no hay owner específico
                dialogStage.initOwner(primaryStageRef);
            }

            Scene scene = new Scene(rootNode);
            dialogStage.setScene(scene);

            // Configurar para que no sea redimensionable y tenga un tamaño ajustado
            dialogStage.setResizable(false);
            // dialogStage.sizeToScene(); // Ajusta el tamaño al contenido
            // O puedes definir un tamaño fijo para los pop-ups
            // dialogStage.setWidth(400);
            // dialogStage.setHeight(300);


            // Añadir listeners si estas ventanas también necesitan el manejo de maximizado/fijo
            // Por ahora, la dejaremos simple, no redimensionable por defecto.
            // Si es una ventana de detalles y podría tener mucho contenido, considera hacerla redimensionable
            // y aplicar una lógica similar a configurarStageVentanaDinamica pero sin maximizarla.

            dialogStage.showAndWait(); // Muestra y espera a que se cierre

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error de Diálogo", "Ocurrió un error al mostrar la ventana de diálogo.");
        }
    }

    // --- Pila para el Historial de Navegación ---
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
    private static String fxmlActual = null; // Para saber cuál es la vista actual


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
        // Intentar que el FXML defina su tamaño primero
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

    /**
     * Cambia la escena actual del primaryStage, guardando la actual en el historial.
     */
    public static void cambiarEscena(String fxmlFile, String title, boolean esDinamicaYMaximizada) {
        if (primaryStageRef == null) { /* ... error ... */ return; }

        // Guardar la vista actual en el historial ANTES de cambiar,
        // pero solo si no es la misma que la que vamos a cargar (evita duplicados al refrescar)
        // y si fxmlActual no es null (primera carga).
        if (fxmlActual != null && !fxmlActual.equals(fxmlFile)) {
            // Necesitamos saber si la vista ACTUAL era dinámica o no.
            // Esto es un poco más complejo de determinar sin pasar ese estado.
            // Por ahora, asumiremos que podemos obtener el estado del stage actual.
            // O, más simple, el que llama a "volver" debe saber a qué tipo de vista vuelve.
            // Para un "volver" simple, la información guardada es la de la vista anterior.
            boolean actualEsDinamica = primaryStageRef.isMaximized() || primaryStageRef.isFullScreen();
            // No necesitamos guardar la actual si estamos haciendo "volver",
            // eso se maneja en `volverAEscenaAnterior`.
            // Este método `cambiarEscena` es para navegación "hacia adelante".
            if(fxmlActual != null) { // Solo guardar si hay una vista actual definida
                // Para determinar `actualEsDinamica` correctamente, necesitaríamos haberla guardado
                // o inferirla. La forma más simple es que la VistaAnterior guarde su propio estado.
                // Cuando llamamos a cambiarEscena, el fxmlActual y su configuración son lo que
                // se convierte en la "vista anterior".
                // De momento, no lo guardamos explícitamente si la siguiente vista es la misma.
                // Esto necesita refinamiento para un historial robusto.
                // Por simplicidad ahora, cada vez que se cambia de escena, guardamos la *anterior*
                // (el `fxmlActual` que está a punto de ser reemplazado).

                // Al navegar a una NUEVA escena (no al volver)
                // guardamos la información de la escena que estamos dejando.
                // El `esDinamicaYMaximizada` del `fxmlActual` es un poco un truco aquí.
                // Lo ideal sería que VistaAnterior guarde este flag.
                // Vamos a asumir que el `fxmlActual` tenía la configuración `actualEsDinamica`.
                historialNavegacion.push(new VistaAnterior(fxmlActual, primaryStageRef.getTitle(), actualEsDinamica));
                System.out.println("Historial: Añadido " + fxmlActual);
            }
        }


        try {
            URL resourceUrl = UtilidadesVentana.class.getResource(fxmlFile);
            if (resourceUrl == null) { /* ... error ... */ return; }
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
            fxmlActual = fxmlFile; // Actualizar la vista actual

        } catch (IOException e) { /* ... error ... */ }
    }

    public static void cambiarEscena(String fxmlFile, String title) {
        cambiarEscena(fxmlFile, title, false);
    }


    /**
     * Vuelve a la escena anterior guardada en el historial.
     */
    public static void volverAEscenaAnterior() {
        if (primaryStageRef == null) {
            System.err.println("Error: PrimaryStage no inicializado.");
            return;
        }
        if (historialNavegacion.isEmpty()) {
            System.out.println("No hay escena anterior en el historial.");
            // Opcional: ir a una pantalla principal por defecto o no hacer nada.
            // cambiarEscena("/com/proyectointegral2/Vista/Login.fxml", "Login", false); // Ejemplo
            return;
        }

        VistaAnterior vista = historialNavegacion.pop();
        System.out.println("Historial: Volviendo a " + vista.fxmlFile);

        try {
            URL resourceUrl = UtilidadesVentana.class.getResource(vista.fxmlFile);
            if (resourceUrl == null) {
                System.err.println("Error: No se pudo encontrar FXML anterior: " + vista.fxmlFile);
                // Intentar con la siguiente en el historial si existe, o mostrar error.
                if (!historialNavegacion.isEmpty()) volverAEscenaAnterior();
                else mostrarAlertaError("Error de Navegación", "No se pudo volver a la vista anterior.");
                return;
            }
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            Scene currentScene = primaryStageRef.getScene();
            currentScene.setRoot(root); // No necesitamos crear nueva Scene

            if (vista.esDinamicaYMaximizada) {
                configurarStageVentanaDinamicaMaximizada(vista.title);
            } else {
                configurarStageParaVentanaFixed(vista.title);
            }
            fxmlActual = vista.fxmlFile; // Actualizar la vista actual

        } catch (IOException e) {
            System.err.println("Error al cargar FXML anterior: " + vista.fxmlFile);
            e.printStackTrace();
            mostrarAlertaError("Error de Carga", "Ocurrió un error al volver a la vista anterior.");
        }
    }


    // --- Tus otros métodos (cambiarEscenaConRoot, mostrarVentanaPopup, alertas) ---
    // El método cambiarEscenaConRoot también necesitaría lógica de historial similar a cambiarEscena
    public static void cambiarEscenaConRoot(Parent newRoot, String title, boolean esDinamicaYMaximizada) {
        if (primaryStageRef == null) { /* ... error ... */ return; }

        // Guardar la vista actual en el historial ANTES de cambiar
        if (fxmlActual != null) { // Solo si hay una vista actual que guardar
            boolean actualEsDinamica = primaryStageRef.isMaximized() || primaryStageRef.isFullScreen();
            historialNavegacion.push(new VistaAnterior(fxmlActual, primaryStageRef.getTitle(), actualEsDinamica));
            System.out.println("Historial (con root): Añadido " + fxmlActual);
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
        // Aquí no podemos saber el fxmlFile del newRoot fácilmente, así que fxmlActual
        // no se actualiza de forma tan directa. Esto es una limitación si `volver` depende de fxmlActual.
        // Una solución sería que cambiarEscenaConRoot también reciba el fxmlFile del newRoot.
        // O que el "volver" no dependa de fxmlActual y siempre use la pila.
        // Por ahora, fxmlActual no se actualiza aquí para evitar inconsistencias.
        // Lo ideal es que `cambiarEscenaConRoot` se use menos para navegación principal
        // y más para casos donde el `root` ya está preparado (ej. después de initData).
    }

    // ... (mostrarVentanaPopup, mostrarAlertaError, mostrarAlertaInformacion) ...
    // (El código de estos métodos se mantiene como en tu versión)
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

    public static void setFxmlActual(String fxmlPath) {
        fxmlActual = fxmlPath;
    }
}