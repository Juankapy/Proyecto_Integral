package main.java.com.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class LoginController {

    @FXML
    private ImageView ImgUsuario;

    @FXML
    private ImageView ImgLateralLogin;

    @FXML
    private Button BtnConfirmar;

    @FXML
    private ImageView ImgIconoDog;

    @FXML
    private TextField TxtContra;

    @FXML
    private TextField TxtCorreo;

    @FXML
    private Hyperlink HyRegistrarse;

    @FXML
    private HBox HboxImg;

    @FXML
    void fafafa(ActionEvent event) {

    }

    @FXML
    private void IrAPagina2(ActionEvent event) {
        navigateTo("/main/resources/com/proyecto_integral/Vista/registro.fxml"); // Ajusta esta ruta
    }

    private void navigateTo(String fxmlPath) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newRoot = loader.load();
            Scene currentScene = HyRegistrarse.getScene(); // O cualquier otro nodo de la escena actual
            currentScene.setRoot(newRoot);

        } catch (IOException e) {
            System.err.println("Error al cargar el FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

}
