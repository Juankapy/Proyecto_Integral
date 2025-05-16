package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class DetallesPerroController {

    @FXML
    private ImageView imgLogoPequeno;

    @FXML
    private Text TxtSexo;

    @FXML
    private Text TxtNombre;

    @FXML
    private Text TxtEdad;

    @FXML
    private Text TxtProtectora;

    @FXML
    private Button BtnReservarCita;

    @FXML
    private ImageView imgPerro;

    @FXML
    private Text TxtRaza;

    @FXML
    private Text TxtPatologia;

    @FXML private Perro perroActual;

    @FXML
    void ReservarCita(ActionEvent event) {

    }
    public void initData(Perro perro) {
        this.perroActual = perro;
        // Aquí cargas los datos de 'perroActual' en los labels e imageview de tu FXML de detalles
        // ej: nombreLabel.setText(perro.getNombre());
        //     razaLabel.setText(perro.getRaza().getNombre());
        //     // Cargar imagen
    }

}
