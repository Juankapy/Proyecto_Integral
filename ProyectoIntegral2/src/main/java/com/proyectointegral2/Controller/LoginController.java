package com.proyectointegral2.Controller;

import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import com.proyectointegral2.MainApp;

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
    void ConfirmarInicio(MouseEvent event) {
        String mainClienteFxmlFile = "/com/proyectointegral2/Vista/Main.fxml";
        String mainClienteTitle = "Panel Cliente - Dogpuccino";
        UtilidadesVentana.cambiarEscena(mainClienteFxmlFile, mainClienteTitle, true);
    }

    @FXML
    void IrARegistro(ActionEvent event) {
        String choosingFxmlFile = "/com/proyectointegral2/Vista/InicioChoose.fxml";
        String choosingTitle = "Selección de Rol - Dogpuccino";
        UtilidadesVentana.cambiarEscena(choosingFxmlFile, choosingTitle, false);
    }

    public void ConfirmarRegistroProtectora(ActionEvent event) {
        String mainProtectoraFxmlFile = "/com/proyectointegral2/Vista/MainProtectora.fxml";
        String mainProtectoraTitle = "Panel Protectora - Dogpuccino";
        UtilidadesVentana.cambiarEscena(mainProtectoraFxmlFile, mainProtectoraTitle, true);
    }
}
