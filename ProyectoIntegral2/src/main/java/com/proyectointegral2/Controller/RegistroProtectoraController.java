package com.proyectointegral2.Controller;


import com.proyectointegral2.utils.UtilidadesVentana;
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
        String mainProtectoraFxmlFile = "/com/proyectointegral2/Vista/MainProtectora.fxml";
        String mainProtectoraTitle = "Panel Protectora - Dogpuccino";
        UtilidadesVentana.cambiarEscena(mainProtectoraFxmlFile, mainProtectoraTitle, true);
    }

    @FXML
    void Volver(MouseEvent event) {
        String loginFxml = "/com/proyectointegral2/Vista/Login.fxml";
        String loginTitle = "Inicio de Sesión - Dogpuccino";
        UtilidadesVentana.cambiarEscena(loginFxml, loginTitle, false);

    }

}
