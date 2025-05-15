package com.proyectointegral2.Controller;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class MainProtectoraController {

    @FXML
    private TableColumn<?, ?> colAdopHora;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label lblRegistroTitulo;

    @FXML
    private TableColumn<?, ?> colPerroFecha;

    @FXML
    private GridPane dogGrid;

    @FXML
    private StackPane tablasStackPane;

    @FXML
    private TableColumn<?, ?> colAdopCausante;

    @FXML
    private ImageView cartIcon;

    @FXML
    private TableView<?> TablaRegistroPerros;

    @FXML
    private TableView<?> tablaRegistroAdopciones;

    @FXML
    private ImageView logoImageView;

    @FXML
    private Button BtnToggleRegistro;

    @FXML
    private TableColumn<?, ?> colAdopFecha;

    @FXML
    private TableColumn<?, ?> colPerroEstado;

    @FXML
    private Button BtnNuevoPerro;

    @FXML
    private TableColumn<?, ?> colPerroNombre;

    @FXML
    private ImageView userIcon;

    @FXML
    private TableColumn<?, ?> colPerroNotas;

    @FXML
    private TableColumn<?, ?> colAdopNombrePerro;

    @FXML
    private TableColumn<?, ?> colAdopContacto;

    @FXML
    void NuevoPerro(ActionEvent event) {
        String formularioPerroFxmlFile = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
        String formularioPerroTitle = "Panel Cliente - Dogpuccino";
        UtilidadesVentana.cambiarEscena(formularioPerroFxmlFile, formularioPerroTitle, true);
    }

    @FXML
    void Editar(ActionEvent event) {
        String formularioPerroFxmlFile = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
        String formularioPerroTitle = "Panel Cliente - Dogpuccino";
        UtilidadesVentana.cambiarEscena(formularioPerroFxmlFile, formularioPerroTitle, true);
    }

    @FXML
    void Eliminar(ActionEvent event) {

    }

    @FXML
    void RegistroAdopciones(ActionEvent event) {

    }

}
