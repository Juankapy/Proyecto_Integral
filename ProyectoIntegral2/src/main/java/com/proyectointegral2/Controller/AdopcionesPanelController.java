package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.PerroDao;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.Model.SesionUsuario;
import com.proyectointegral2.utils.UtilidadesVentana;

import javafx.application.Platform;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdopcionesPanelController {

    @FXML private BorderPane adopcionesPanelPane;
    @FXML private ImageView logoImageView;
    @FXML private ImageView IconBandeja;
    @FXML private ImageView ImgIconUsuario;
    @FXML private ImageView imgIconoVolverAdopciones;

    @FXML private StackPane adopcionesContentStackPane;
    @FXML private ScrollPane perrosAdopcionScrollPane;
    @FXML private GridPane perrosAdopcionGrid;
    @FXML private Label lblNoPerrosParaAdoptar;

    private List<Perro> listaPerrosParaAdoptarPorCliente;
    private Usuario usuarioLogueado;
    private PerroDao perroDao;
    private ProtectoraDao protectoraDao;

    private final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.png";
    private static final double TARJETA_PREF_WIDTH = 200;
    private static final double TARJETA_IMG_AREA_WIDTH = 180;
    private static final double TARJETA_IMG_AREA_HEIGHT = 180;
    private static final double CARD_INTERNAL_PADDING = 10;


    @FXML
    public void initialize() {
        System.out.println("AdopcionesPanelController inicializado.");
        this.perroDao = new PerroDao();
        this.protectoraDao = new ProtectoraDao();

        this.usuarioLogueado = SesionUsuario.getUsuarioLogueado();
        if (this.usuarioLogueado == null || !"CLIENTE".equalsIgnoreCase(this.usuarioLogueado.getRol())) {
            UtilidadesVentana.mostrarAlertaError("Acceso Denegado", "Debe iniciar sesión como cliente para acceder a esta sección.");
            handleVolverAMainCliente(null);
            return;
        }

        cargarYMostrarPerrosDisponiblesParaAdopcion();
        configurarListenersDeVentana();
    }


    private void cargarYMostrarPerrosDisponiblesParaAdopcion() {
        this.listaPerrosParaAdoptarPorCliente = new ArrayList<>();
        if (perroDao == null || usuarioLogueado == null) {
            mostrarMensajeNoPerros("Error al inicializar servicios.");
            return;
        }


        try {
            boolean haTenidoCitas = true;

            if (haTenidoCitas) {
                List<Perro> todosLosPerrosNoAdoptados = perroDao.obtenerTodosLosPerrosNoAdoptados(); // Necesitarás este método en PerroDao
                if (todosLosPerrosNoAdoptados != null) {
                    this.listaPerrosParaAdoptarPorCliente.addAll(todosLosPerrosNoAdoptados);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error DB", "No se pudieron cargar los perros para adopción: " + e.getMessage());
        }


        if (listaPerrosParaAdoptarPorCliente.isEmpty()) {
            mostrarMensajeNoPerros("Actualmente no tienes perros disponibles para solicitar adopción. ¡Realiza una cita para conocerlos!");
        } else {
            lblNoPerrosParaAdoptar.setVisible(false);
            perrosAdopcionScrollPane.setVisible(true);
            if (adopcionesPanelPane.getScene() != null && adopcionesPanelPane.getScene().getWindow() != null &&
                    ((Stage)adopcionesPanelPane.getScene().getWindow()).isShowing()) {
                Platform.runLater(() -> adaptarUIAlTamanoVentana((Stage) adopcionesPanelPane.getScene().getWindow()));
            } else {
                popularGridPerrosAdopcion();
            }
        }
    }

    private void mostrarMensajeNoPerros(String mensaje) {
        if (lblNoPerrosParaAdoptar != null) {
            lblNoPerrosParaAdoptar.setText(mensaje);
            lblNoPerrosParaAdoptar.setVisible(true);
        }
        if (perrosAdopcionScrollPane != null) perrosAdopcionScrollPane.setVisible(false);
        if (perrosAdopcionGrid != null) perrosAdopcionGrid.getChildren().clear();
    }


    private VBox crearTarjetaPerroAdopcion(Perro perro) {
        VBox card = new VBox(8);
        card.setPrefWidth(TARJETA_PREF_WIDTH);
        card.setMaxWidth(TARJETA_PREF_WIDTH);
        card.setMinWidth(TARJETA_PREF_WIDTH);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(String.format("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2); -fx-padding: %f;", CARD_INTERNAL_PADDING));
        card.setMinHeight(320);

        ImageView imgView = new ImageView();
        String imagePath = perro.getFoto();
        try {
            Image loadedImage = null;
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                if (!imagePath.startsWith("/")) { imagePath = "\\" + imagePath; }
                try(InputStream stream = getClass().getResourceAsStream(imagePath)){
                    if (stream != null) loadedImage = new Image(stream);
                    else System.err.println("WARN: No se encontró imagen en " + imagePath);
                }
            }
            if (loadedImage == null || loadedImage.isError()) {
                try(InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO)){
                    if(placeholderStream != null) loadedImage = new Image(placeholderStream);
                }
            }
            imgView.setImage(loadedImage);
        } catch (Exception e) {System.err.println("Err img tarjeta adopcion: " + perro.getNombre() + " " + e.getMessage());}

        imgView.setFitHeight(TARJETA_IMG_AREA_HEIGHT);
        imgView.setFitWidth(TARJETA_IMG_AREA_WIDTH);
        imgView.setPreserveRatio(true);
        VBox.setMargin(imgView, new Insets(0,0,5,0));

        Label lblNombreEdad = new Label(perro.getNombre() + ", " + calcularEdadString(perro.getFechaNacimiento()));
        lblNombreEdad.setStyle("-fx-background-color: #A9D18E; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 5; -fx-background-radius: 3;");
        lblNombreEdad.setAlignment(Pos.CENTER);
        lblNombreEdad.setPrefWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombreEdad.setMaxWidth(TARJETA_IMG_AREA_WIDTH);

        VBox detallesBox = new VBox(3.0);
        detallesBox.setAlignment(Pos.CENTER_LEFT);
        detallesBox.setPrefWidth(TARJETA_IMG_AREA_WIDTH);

        Label lblRaza = new Label("Raza: " + (perro.getRaza() != null ? perro.getRaza().getNombreRaza() : "N/D"));
        lblRaza.setFont(new Font(12));
        // Label lblPatologia = new Label("Patología: " + obtenerPatologiasConcatenadas(perro.getIdPerro())); // Necesitarías lógica DAO
        Label lblPatologia = new Label("Patología: Ansiedad (Ejemplo)"); // Placeholder
        lblPatologia.setFont(new Font(12));
        // Label lblProtectora = new Label("Protectora: " + obtenerNombreProtectora(perro.getIdProtectora())); // Necesitarías lógica DAO
        Label lblProtectora = new Label("Protectora: Huellas Amistosas (Ejemplo)"); // Placeholder
        lblProtectora.setFont(new Font(12));

        detallesBox.getChildren().addAll(lblRaza, lblPatologia, lblProtectora);

        Button btnSolicitarAdopcion = new Button("Solicitar adopción");
        btnSolicitarAdopcion.setStyle("-fx-background-color: #8FBC8F; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-font-size:12px; -fx-padding: 5 10;");
        btnSolicitarAdopcion.setOnAction(event -> handleSolicitarAdopcion(perro));
        VBox.setMargin(btnSolicitarAdopcion, new Insets(8,0,0,0));

        card.getChildren().addAll(imgView, lblNombreEdad, detallesBox, btnSolicitarAdopcion);
        card.setUserData(perro);
        return card;
    }

    private String calcularEdadString(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return "Edad desc.";
        Period periodo = Period.between(fechaNacimiento, LocalDate.now());
        if (periodo.getYears() > 0) return periodo.getYears() + (periodo.getYears() == 1 ? " año" : " años");
        if (periodo.getMonths() > 0) return periodo.getMonths() + (periodo.getMonths() == 1 ? " mes" : " meses");
        return Math.max(0, periodo.getDays()) + (periodo.getDays() == 1 ? " día" : " días");
    }

    private void handleSolicitarAdopcion(Perro perro) {
        System.out.println("Solicitar adopción para: " + perro.getNombre());
        String formularioFxml = "/com/proyectointegral2/Vista/FormularioPeticionAdopcion.fxml";
        String titulo = "Solicitud de Adopción para " + perro.getNombre();
        Stage ownerStage = (Stage) adopcionesPanelPane.getScene().getWindow();

        FormularioSolicitudAdopcionController formController =
                UtilidadesVentana.mostrarVentanaPopup(formularioFxml, titulo, true, ownerStage);

//        if (formController != null) {
//            formController.initData(perro);
//        }
    }

    private void configurarListenersDeVentana() {  }
    private void adaptarUIAlTamanoVentana(Stage stage) { }
    private int calcularColumnasAdopcion(double anchoGridDisponible) {
        return 0;
    }
    private void adaptarContenidoAlAnchoYPopularAdopcion(double anchoVentana) {  }

    private void popularGridPerrosAdopcion() {
        if (perrosAdopcionGrid == null || listaPerrosParaAdoptarPorCliente == null) return;
        perrosAdopcionGrid.getChildren().clear();
        int numColumnas = Math.max(1, perrosAdopcionGrid.getColumnConstraints().size());
        if (numColumnas == 0) numColumnas = calcularColumnasAdopcion(perrosAdopcionGrid.getWidth() > 0 ? perrosAdopcionGrid.getWidth() : 800);
        if (perrosAdopcionGrid.getColumnConstraints().isEmpty()){
            for (int i = 0; i < numColumnas; i++) {
                ColumnConstraints colConst = new ColumnConstraints();
                colConst.setPercentWidth(100.0 / numColumnas);
                colConst.setHgrow(Priority.SOMETIMES);
                perrosAdopcionGrid.getColumnConstraints().add(colConst);
            }
        }


        int columnaActual = 0; int filaActual = 0;
        for (Perro perro : listaPerrosParaAdoptarPorCliente) {
            VBox tarjetaPerro = crearTarjetaPerroAdopcion(perro);
            perrosAdopcionGrid.add(tarjetaPerro, columnaActual, filaActual);
            columnaActual++;
            if (columnaActual >= numColumnas) {
                columnaActual = 0;
                filaActual++;
            }
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


    @FXML void handleIrABandeja(MouseEvent event) { }
    @FXML void handleIrAPerfilUsuario(MouseEvent event) { }
    @FXML void handleVolverAMainCliente(MouseEvent event) {
        UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/MainCliente.fxml", "Panel Cliente", true);
    }
}