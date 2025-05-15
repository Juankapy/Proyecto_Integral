package com.proyectointegral2.Controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class BandejaCitasController {

    @FXML
    private VBox tarjetaCitaEjemplo;

    @FXML
    private Label LblFechaCita;

    @FXML
    private Button BtnCancelar;

    @FXML
    private Label LblLugarCita;

    @FXML
    private ScrollPane citasScrollPane;

    @FXML
    private BorderPane mainContentPane;

    @FXML
    private VBox citasContainerVBox;

    @FXML
    private Label LblHoraCita;

    @FXML
    private Label lblNoCitas;

    @FXML
    private Label LblProtectoraCita;

    @FXML
    private ImageView logoFooter;

    @FXML
    private Label lblNombrePerroCita;

    @FXML
    void CancelarCita(ActionEvent event) {

    }

}

