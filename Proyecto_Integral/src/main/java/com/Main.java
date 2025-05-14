package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/proyecto_integral/Vista/FormularioPerro.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root); // El tamaño se definirá en el FXML o por los min/max del Stage

        stage.setScene(scene);
        stage.setTitle("Inicio de Sesión - Dogpuccino");
        stage.setResizable(false); // Esto es lo principal para bloquear el redimensionamiento
        stage.show();
    }

    /**
     * Método estático para obtener el Stage principal.
     * Útil si necesitas pasar el Stage a diálogos o nuevas ventanas.
     * @return El Stage principal de la aplicación.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Cambia la vista (el nodo raíz) de la escena principal.
     * @param fxmlFile La ruta al archivo FXML a cargar (ej. "/com/paquete/vista.fxml").
     * @param title El nuevo título para la ventana.
     */
    public static void changeScene(String fxmlFile, String title) {
        if (primaryStage == null) {
            System.err.println("Error: PrimaryStage no ha sido inicializado. No se puede cambiar la escena.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
            Parent root = loader.load();
            primaryStage.setTitle(title);

            // Obtener la escena actual y cambiar su raíz
            Scene currentScene = primaryStage.getScene();
            if (currentScene == null) {
                // Si por alguna razón no hay escena (muy raro después del start), crear una nueva
                currentScene = new Scene(root);
                primaryStage.setScene(currentScene);
            } else {
                currentScene.setRoot(root);
            }




        } catch (IOException e) {
            System.err.println("Error al cargar FXML: " + fxmlFile);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Error: No se pudo encontrar el archivo FXML: " + fxmlFile + ". Verifica la ruta.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}