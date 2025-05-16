package com.proyectointegral2.Controller;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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

    public void IrABandeja(MouseEvent mouseEvent) {
        System.out.println("Abriendo bandeja de citas como pop-up...");

        String bandejaFxml = "/com/proyectointegral2/Vista/BandejasCitas.fxml";
        String titulo = "Mis Citas Programadas";

        Stage ownerStage = null;
        if (mouseEvent.getSource() instanceof Node) {
            ownerStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        } else {
            ownerStage = UtilidadesVentana.getPrimaryStage();
        }
        BandejaCitasController bandejaController = UtilidadesVentana.mostrarVentanaPopup(bandejaFxml, titulo, true, ownerStage);

        if (bandejaController != null) {
            System.out.println("Pop-up de bandeja de citas mostrado.");
        } else {
            System.err.println("No se pudo mostrar el pop-up de bandeja de citas.");
        }
    }

    public void IrAPerfilUsuario(MouseEvent mouseEvent) {
        String perfilUsuarioFxmlFile = "/com/proyectointegral2/Vista/PerfilUsuario.fxml";
        String perfilUsuarioTitle = "Panel Cliente - Dogpuccino";
        UtilidadesVentana.cambiarEscena(perfilUsuarioFxmlFile, perfilUsuarioTitle, true);
    }

    public void IrAFormularioPerro(ActionEvent actionEvent) {
        String formularioPerroFxmlFile = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
        String formularioPerroTitle = "Panel Cliente - Dogpuccino";
        UtilidadesVentana.cambiarEscena(formularioPerroFxmlFile, formularioPerroTitle, true);
    }
}
