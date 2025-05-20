package com.proyectointegral2.Controller;


import com.proyectointegral2.Model.Protectora;

import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.utils.UtilidadesVentana; // Para navegación si es necesario
import com.proyectointegral2.Model.SesionUsuario; // Para saber quién está logueado
import com.proyectointegral2.Model.Usuario;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventosPanelController {

    @FXML private BorderPane eventosPanelPane;
    @FXML private ImageView logoImageView;
    @FXML private ImageView IconBandeja;
    @FXML private ImageView ImgIconUsuario;
    @FXML private Label lblTituloEventos; // Podrías tener un título aquí también
    // @FXML private Button btnNuevoEvento; // Si las protectoras pueden crear eventos desde aquí

    @FXML private StackPane eventosContentStackPane;
    @FXML private ScrollPane eventosScrollPane;
    @FXML private VBox eventosContainerVBox; // Aquí irán las tarjetas de eventos
    @FXML private Label lblNoEventos;        // Label para "No hay eventos"


    private Usuario usuarioLogueado; // Para saber si es cliente o protectora

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {

        this.usuarioLogueado = SesionUsuario.getUsuarioLogueado();

        eventosScrollPane.setVisible(false);
        lblNoEventos.setVisible(true);
        lblNoEventos.setText("Cargando eventos..."); // Mensaje inicial

    }



    @FXML
    void handleIrABandeja(MouseEvent event) {
        if (usuarioLogueado == null) { UtilidadesVentana.mostrarAlertaError("Sesión Requerida", "Debe iniciar sesión para ver su bandeja."); return; }
        String bandejaFxml = "/com/proyectointegral2/Vista/BandejaCitas.fxml"; // O la vista de bandeja de notificaciones/peticiones
        String titulo = "Mi Bandeja";
        Stage owner = (Stage) ((Node)event.getSource()).getScene().getWindow();
        BandejaCitasController bcController = UtilidadesVentana.mostrarVentanaPopup(bandejaFxml, titulo, true, owner);
        if(bcController != null) bcController.initData(usuarioLogueado.getIdUsuario());
    }

    @FXML
    void handleIrAPerfil(MouseEvent event) {
        if (usuarioLogueado == null) { UtilidadesVentana.mostrarAlertaError("Sesión Requerida", "Debe iniciar sesión para ver su perfil."); return; }

        String perfilFxml;
        String titulo;
        if ("CLIENTE".equalsIgnoreCase(usuarioLogueado.getRol())) {
            perfilFxml = "/com/proyectointegral2/Vista/PerfilUsuario.fxml";
            titulo = "Mi Perfil (" + usuarioLogueado.getNombreUsu() + ")";
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(perfilFxml));
                Parent root = loader.load();
                PerfilUsuarioController controller = loader.getController();
                if (controller != null) controller.initData(usuarioLogueado.getIdUsuario(), usuarioLogueado.getNombreUsu());
                UtilidadesVentana.cambiarEscenaConRoot(root, titulo, false);
            } catch (Exception e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error", "No se pudo abrir el perfil."); }

        } else if ("PROTECTORA".equalsIgnoreCase(usuarioLogueado.getRol())) {
            perfilFxml = "/com/proyectointegral2/Vista/PerfilProtectoraView.fxml"; // Asume este FXML
            titulo = "Perfil Protectora";
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(perfilFxml));
                Parent root = loader.load();
                PerfilProtectoraController controller = loader.getController();
                if (controller != null) {
                    // PerfilProtectoraController necesita initData(Protectora p, Usuario u)
                    // Necesitas obtener el objeto Protectora aquí usando el id de entidad de SesionUsuario
                    ProtectoraDao pDao = new ProtectoraDao();
                    Protectora protectora = pDao.obtenerProtectoraPorId(SesionUsuario.getEntidadIdEspecifica());
                    if(protectora != null) controller.initData(protectora, usuarioLogueado);
                    else { UtilidadesVentana.mostrarAlertaError("Error", "No se encontraron datos de la protectora."); return; }
                }
                UtilidadesVentana.cambiarEscenaConRoot(root, titulo, false);
            } catch (Exception e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error", "No se pudo abrir el perfil."); }
        } else {
            UtilidadesVentana.mostrarAlertaError("Error Rol", "Rol de usuario no reconocido para ver perfil.");
        }
    }

    @FXML void Reservar(ActionEvent event) {
        System.out.println("Botón Reservar para volver a main perros presionado");
        String reservarFxml = "/com/proyectointegral2/Vista/Main.fxml";
        String reservartitulo = "Panel de Main";
        UtilidadesVentana.cambiarEscena(reservarFxml, reservartitulo, true);
    }
    @FXML void Adopciones(ActionEvent event) {
        System.out.println("Botón Adopciones presionado");
        String AdopcionesFxml = "/com/proyectointegral2/Vista/AdopcionesPanel.fxml";
        String Adopcionestitulo = "Panel de Adopciones";
        UtilidadesVentana.cambiarEscena(AdopcionesFxml, Adopcionestitulo, true);
    }

    @FXML void Eventos(ActionEvent event) {
        System.out.println("Botón Eventos presionado");
        String eventosFxml = "/com/proyectointegral2/Vista/EventosPanel.fxml";
        String eventostitulo = "Panel de Eventos";
        UtilidadesVentana.cambiarEscena(eventosFxml, eventostitulo, true);
    }
}