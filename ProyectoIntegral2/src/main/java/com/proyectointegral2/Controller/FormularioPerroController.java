package com.proyectointegral2.Controller;


import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class FormularioPerroController {
    @FXML
    private Button BtnCancelar;

    @FXML
    private TextField TxtNombrePerro;

    @FXML
    private ImageView ImgPreviewPerro;

    @FXML
    private DatePicker DateFechaNacimiento;

    @FXML
    private ComboBox<?> CmbEstado;

    @FXML
    private ImageView imgIconoVolver;

    @FXML
    private TextField TxtRazaPerro;

    @FXML
    private Button btnSeleccionarImagen;

    @FXML
    private ComboBox<?> CmbSexo;

    @FXML
    private TextArea TxtAreaPatologia;

    @FXML
    private Button BtnAnadirPerro;

    @FXML
    private TextArea TxtAreaDescripcion;

    @FXML
    void Cancelar(ActionEvent event) {

    }

    @FXML
    void AnadirPerro(ActionEvent event) {

    }


    @FXML
    private void Volver(MouseEvent event) {
        String mainProtectoraFxml = "/com/proyectointegral2/Vista/MainProtectora.fxml";
        String mainProtectoraTitle = "Inicio de Sesión - Dogpuccino";
        UtilidadesVentana.cambiarEscena(mainProtectoraFxml, mainProtectoraTitle, false);

    }
}
