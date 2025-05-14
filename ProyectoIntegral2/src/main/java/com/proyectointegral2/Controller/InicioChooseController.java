package com.proyectointegral2.Controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;

public class InicioChooseController {



    @FXML
    private ImageView ImgCliente;

    @FXML
    private ImageView ImgProtectora;

    @FXML
    private void SeleccionarCliente(Event event) {
        abrirVentanaRegistro("/com/proyectointegral2/Vista/registro_cliente.fxml", "Registro Cliente");
    }


    @FXML
    private void SeleccionarProtectora(Event event) {
        abrirVentanaRegistro("/com/proyectointegral2/Vista/registro_protectora.fxml", "Registro Protectora");
    }

    private void abrirVentanaRegistro(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}