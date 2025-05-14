package com.proyectointegral2.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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
    void IrARegistro(ActionEvent event) {
        String choosingFxmlFile = "/com/proyectointegral2/Vista/InicioChoose.fxml";

        String choosingTitle = "Selecciona rol de Nuevo Usuario - Dogpuccino";

        if (MainApp.getPrimaryStage() != null) {
            MainApp.changeScene(choosingFxmlFile, choosingTitle);

        } else {
            System.err.println("Error en LoginController: PrimaryStage en Main no está inicializado. No se puede cambiar a la escena de registro.");
        }
    }

}
