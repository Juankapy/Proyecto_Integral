package com.proyectointegral2.Controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import com.proyectointegral2.utils.UtilidadesVentana;

public class InicioChooseController {

    @FXML
    private ImageView imgLogoPrincipal;

    @FXML
    private ImageView ImgCliente;

    @FXML
    private ImageView ImgProtectora;

    @FXML
    void SeleccionarCliente(MouseEvent event) {
        System.out.println("Rol Usuario/Cliente seleccionado.");
        String registroClienteFxml = "/com/proyectointegral2/Vista/RegistroCliente.fxml";
        String titulo = "Registro de Cliente";

        UtilidadesVentana.cambiarEscena(registroClienteFxml, titulo, false);
    }

    @FXML
    void SeleccionarProtectora(MouseEvent event) {
        System.out.println("Rol Protectora seleccionado.");
        String registroProtectoraFxml = "/com/proyectointegral2/Vista/RegistroProtectora.fxml";
        String titulo = "Registro de Protectora";

        UtilidadesVentana.cambiarEscena(registroProtectoraFxml, titulo, false);
    }
}