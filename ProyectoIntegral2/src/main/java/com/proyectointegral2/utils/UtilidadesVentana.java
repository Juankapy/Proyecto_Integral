package com.proyectointegral2.utils;


import javafx.scene.control.Button;
import javafx.stage.Stage;

public class UtilidadesVentana {

    public void cerrarVentana(Button boton) {
        Stage stage = (Stage) boton.getScene().getWindow();
        stage.close();
    }

    private Stage ventanaAnterior;

    public void setVentanaAnterior(Stage stage) {
        this.ventanaAnterior = stage;
    }
    public void volverAVentanaAnterior(Button boton) {
        Stage ventanaActual = (Stage) boton.getScene().getWindow();
        ventanaActual.close();

        if (ventanaAnterior != null) {
            ventanaAnterior.show();
        }
    }
}
