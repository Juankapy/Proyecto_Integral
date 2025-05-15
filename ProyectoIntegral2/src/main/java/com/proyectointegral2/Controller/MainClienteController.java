package com.proyectointegral2.Controller;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;


public class MainClienteController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label nameAgeLabel_r0_c1;

    @FXML
    private GridPane dogGrid;

    @FXML
    private ImageView IconBandeja;

    @FXML
    private ImageView ImgIconUsuario;

    @FXML
    private Button BtnReservar;

    @FXML
    private TextField searchTextField;

    @FXML
    private Label nameAgeLabel_r0_c11;

    @FXML
    private ScrollPane dogScrollPane;

    @FXML
    private Label nameAgeLabel_r0_c11111;

    @FXML
    private Label nameAgeLabel_r0_c111111;

    @FXML
    private ImageView ImgPerro5;

    @FXML
    private ImageView ImgPerro6;

    @FXML
    private Label nameAgeLabel_r0_c11111111;

    @FXML
    private ImageView ImgPerro3;

    @FXML
    private ImageView ImgPerro4;

    @FXML
    private ImageView ImgPerro1;

    @FXML
    private ImageView ImgPerro2;

    @FXML
    private Label nameAgeLabel_r0_c1111;

    @FXML
    private Label nameAgeLabel_r0_c0;

    @FXML
    private Label nameAgeLabel_r0_c111;

    @FXML
    private ImageView ImgPerro10;

    @FXML
    private Button BtnAdopciones;

    @FXML
    private ImageView ImgPerro9;

    @FXML
    private ImageView ImgPerro7;

    @FXML
    private ImageView ImgPerro8;

    @FXML
    private ImageView logoImageView;

    @FXML
    private ImageView ImgIconBuscar;

    @FXML
    private Label nameAgeLabel_r0_c1111111;

    @FXML
    private Label nameAgeLabel_r1_c0;

    @FXML
    private Button BtnEventos;


    @FXML
    void Reservar(ActionEvent event) {

    }

    @FXML
    void Adopciones(ActionEvent event) {

    }

    @FXML
    void Eventos(ActionEvent event) {

    }

    @FXML
    void Bandeja(MouseEvent event) {
        String bandejaCitaFxmlFile = "/com/proyectointegral2/Vista/BandejasCitas.fxml";
        String bandejaCitaTitle = "Panel Cliente - Dogpuccino";
        UtilidadesVentana.cambiarEscena(bandejaCitaFxmlFile, bandejaCitaTitle, true);
    }

    @FXML
    void DetallesUsuario(MouseEvent event) {
        String perfilUsuarioFxmlFile = "/com/proyectointegral2/Vista/PerfilUsuario.fxml";
        String perfilUsuarioTitle = "Panel Cliente - Dogpuccino";
        UtilidadesVentana.cambiarEscena(perfilUsuarioFxmlFile, perfilUsuarioTitle, true);
    }

    public void IraDetallesPerro(MouseEvent mouseEvent) {
        String detallesPerroFxmlFile = "/com/proyectointegral2/Vista/DetallesPerro.fxml";
        String detallesPerroTitle = "Panel Cliente - Dogpuccino";
        UtilidadesVentana.cambiarEscena(detallesPerroFxmlFile, detallesPerroTitle, true);
    }
}
