package com.proyectointegral2.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import com.proyectointegral2.MainApp;

public class InicioChooseController {

    @FXML
    private ImageView imgLogoPrincipal;

    @FXML
    private ImageView ImgCliente;

    @FXML
    private ImageView ImgProtectora;

    @FXML
    void SeleccionarCliente(MouseEvent event) {
        System.out.println("Rol Usuario seleccionado.");
        String registroUsuarioFxml = "/com/proyectointegral2/Vista/RegistroCliente.fxml"; // O como se llame tu FXML de registro de usuario
        String titulo = "Registro de Usuario";

        if (MainApp.getPrimaryStage() != null) {
            MainApp.changeScene(registroUsuarioFxml, titulo);
        } else {
            System.err.println("Error en SeleccionRolController: PrimaryStage no inicializado.");
        }
        MainApp.changeScene(registroUsuarioFxml, titulo, false);
    }

    @FXML
    void SeleccionarProtectora(MouseEvent event) {
        System.out.println("Rol Protectora seleccionado.");
        String registroProtectoraFxml = "/com/proyectointegral2/Vista/RegistroProtectora.fxml"; // O como se llame tu FXML de registro de protectora
        String titulo = "Registro de Protectora";

        if (MainApp.getPrimaryStage() != null) {
            MainApp.changeScene(registroProtectoraFxml, titulo);
        } else {
            System.err.println("Error en SeleccionRolController: PrimaryStage no inicializado.");
        }

        MainApp.changeScene(registroProtectoraFxml, titulo, false);
    }

}

