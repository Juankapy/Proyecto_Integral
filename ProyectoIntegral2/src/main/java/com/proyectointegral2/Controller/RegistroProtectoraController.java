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
import javafx.scene.control.PasswordField;

import java.io.IOException;

public class RegistroProtectoraController {

    @FXML
    private TextField TxtNombreProtectora;
    @FXML
    private TextField TxtCIF;
    @FXML
    private TextField TxtDireccionProtectora;
    @FXML
    private TextField TxtProvinciaProtectora;
    @FXML
    private TextField TxtCPProtectora;
    @FXML
    private TextField TxtCiudadProtectora;
    @FXML
    private TextField TxtTelProtectora;
    @FXML
    private TextField TxtNombreUsuarioCuenta;
    @FXML
    private TextField TxtCorreoCuenta;
    @FXML
    private PasswordField TxtContraCuenta;
    @FXML
    private PasswordField TxtConfirmarContraCuenta;

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
