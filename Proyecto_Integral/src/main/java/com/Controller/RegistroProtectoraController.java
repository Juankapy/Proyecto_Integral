package com.Controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class RegistroProtectoraController {

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
    private HBox HboxImg;

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
    private TextField TxtCIF;

    @FXML
    private TextField TxtCorreo;

    @FXML
    void ConfirmarRegistroProtectora(ActionEvent event) {

    }

    @FXML
    void Volver(MouseEvent event) {
        String loginFxmlFile = "/com/proyecto_integral/Vista/login.fxml";
        String loginTitle = "Inicio de Sesión - Dogpuccino";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(loginFxmlFile));
            Parent root = loader.load();
            Scene scene = ImgIconoSalida.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            System.err.println("Error al cargar: " + loginFxmlFile);
            e.printStackTrace();
        }
    }

}
