package com.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import main.java.com.Main;

import java.io.IOException;

public class RegistroController {
    @FXML
    private ImageView ImgLateralLogin;

    @FXML
    private TextField TxtTel;

    @FXML
    private TextField TxtNombre;

    @FXML
    private TextField TxtProvincia;

    @FXML
    private ImageView ImgIconoDog;

    @FXML
    private TextField TxtContra;

    @FXML
    private TextField TxtCP;

    @FXML
    private TextField TxtDireccion;

    @FXML
    private TextField TxtCiudad;

    @FXML
    private ImageView ImgUsuario;

    @FXML
    private TextField TxtApellido;

    @FXML
    private Button BtnConfirmar;

    @FXML
    private ImageView ImgIconoSalida;

    @FXML
    private TextField TxtCorreo;

    @FXML
    void ConfirmarRegistro(ActionEvent event) {

    }
    @Deprecated
    void fafafa(ActionEvent event) {

    }
    @FXML
    private void Volver(MouseEvent event) {
        String loginFxmlFile = "/main/resources/com/proyecto_integral/Vista/login.fxml";
        String loginTitle = "Inicio de Sesión - Dogpuccino";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(loginFxmlFile));
            Parent root = loader.load();
            Scene scene = ImgIconoSalida.getScene(); // Necesitarías inyectar ImgIconoSalida con @FXML
            scene.setRoot(root);
            // ((Stage) scene.getWindow()).setTitle(loginTitle); // Opcional para cambiar el título
        } catch (IOException e) {
            System.err.println("Error al cargar: " + loginFxmlFile);
            e.printStackTrace();
        }
    }

}
