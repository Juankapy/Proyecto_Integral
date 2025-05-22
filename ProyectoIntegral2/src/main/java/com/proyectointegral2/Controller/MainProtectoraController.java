package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.Model.RegistroAdopcionInfo;
import com.proyectointegral2.Model.RegistroPerroInfo;
import com.proyectointegral2.Model.SesionUsuario;
import com.proyectointegral2.dao.PerroDao;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.dao.PeticionAdopcionDao;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainProtectoraController {

    @FXML private BorderPane mainBorderPane;
    @FXML private ImageView logoImageView;
    @FXML private ImageView ImgIconBandeja;
    @FXML private ImageView ImgIconUsuario;
    @FXML private Button BtnNuevoPerro;
    @FXML private GridPane dogGrid;
    @FXML private Label lblRegistroTitulo;
    @FXML private Button BtnToggleRegistro;
    @FXML private StackPane tablasStackPane;

    @FXML private TableView<RegistroPerroInfo> TablaRegistroPerros;
    @FXML private TableColumn<RegistroPerroInfo, String> colPerroNombre;
    @FXML private TableColumn<RegistroPerroInfo, LocalDate> colPerroFecha;
    @FXML private TableColumn<RegistroPerroInfo, String> colPerroEstado;
    @FXML private TableColumn<RegistroPerroInfo, String> colPerroNotas;

    @FXML private TableView<RegistroAdopcionInfo> tablaRegistroAdopciones;
    @FXML private TableColumn<RegistroAdopcionInfo, String> colAdopNombrePerro;
    @FXML private TableColumn<RegistroAdopcionInfo, LocalDate> colAdopFecha;
    @FXML private TableColumn<RegistroAdopcionInfo, String> colAdopHora;
    @FXML private TableColumn<RegistroAdopcionInfo, String> colAdopCausante;
    @FXML private TableColumn<RegistroAdopcionInfo, String> colAdopContacto;

    @FXML private Label lblNoPerrosEnGrid;

    private static final String RUTA_BASE_IMAGENES_PERROS_RESOURCES = "/assets/Imagenes/Perros/";
    private final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.png";
    private static final double HEADER_HEIGHT_ESTIMADA = 70.0;
    private static final double TARJETA_IMG_AREA_WIDTH = 160.0;
    private static final double TARJETA_IMG_AREA_HEIGHT = 160.0;
    private static final double TARJETA_PREF_WIDTH = 190.0;
    private static final double CARD_HORIZONTAL_GAP = 20.0;
    private static final double CARD_INTERNAL_PADDING = 10.0;
    private static final double MIN_GRID_HEIGHT = 250.0;
    private static final double HBOX_TITULO_GRID_HEIGHT_ESTIMADA = 60.0;
    private static final double TABLAS_SECTION_HEIGHT_ESTIMADA = 350.0;

    private List<Perro> listaDePerrosDeLaProtectora;
    private PerroDao perroDao;
    private ProtectoraDao protectoraDao;
    private PeticionAdopcionDao peticionAdopcionDao;

    private Usuario usuarioCuentaLogueada;
    private int idProtectoraActual;
    private String nombreProtectoraActual;

    private boolean mostrandoRegistroPerros = true;

    @FXML
    public void initialize() {
        try {
            this.perroDao = new PerroDao();
            this.protectoraDao = new ProtectoraDao();
            this.peticionAdopcionDao = new PeticionAdopcionDao();
        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico DAO", "No se pudo inicializar el acceso a datos: " + e.getMessage());
            if(BtnNuevoPerro != null) BtnNuevoPerro.setDisable(true);
            return;
        }

        this.usuarioCuentaLogueada = SesionUsuario.getUsuarioLogueado();
        this.idProtectoraActual = SesionUsuario.getEntidadIdEspecifica();

        if (this.usuarioCuentaLogueada == null || this.idProtectoraActual <= 0 ||
                !"PROTECTORA".equalsIgnoreCase(this.usuarioCuentaLogueada.getRol())) {
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sesión", "No hay información de protectora válida. Volviendo al login.");
            UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/Login.fxml", "Inicio de Sesión", false);
            return;
        }

        try {
            Protectora protectora = protectoraDao.obtenerProtectoraPorId(this.idProtectoraActual);
            if (protectora != null) this.nombreProtectoraActual = protectora.getNombre();
            else {
                UtilidadesVentana.mostrarAlertaError("Error de Datos", "No se pudo cargar la información de la protectora ID: " + this.idProtectoraActual);
                UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/Login.fxml", "Inicio de Sesión", false);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Base de Datos", "No se pudo cargar info de protectora: " + e.getMessage());
            UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/Login.fxml", "Inicio de Sesión", false);
            return;
        }

        configurarColumnasTablas();
        cargarYMostrarPerrosDeProtectora();
        configurarVisibilidadInicialTablas();
        configurarListenersDeVentana();
        actualizarVisibilidadLabelNoPerros();
    }

    private void configurarVisibilidadInicialTablas() {
        if (lblRegistroTitulo != null) lblRegistroTitulo.setText("Registro de Perros");
        if (BtnToggleRegistro != null) BtnToggleRegistro.setText("Ver Registro de Adopciones");
        if (TablaRegistroPerros != null) {
            TablaRegistroPerros.setVisible(true);
            cargarDatosParaTablaRegistroPerros();
        }
        if (tablaRegistroAdopciones != null) tablaRegistroAdopciones.setVisible(false);
    }

    private void configurarColumnasTablas(){
        if (TablaRegistroPerros != null) {
            colPerroNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colPerroFecha.setCellValueFactory(new PropertyValueFactory<>("fechaIngreso"));
            colPerroEstado.setCellValueFactory(new PropertyValueFactory<>("estadoActual"));
            colPerroNotas.setCellValueFactory(new PropertyValueFactory<>("notasAdicionales"));
            TablaRegistroPerros.setPlaceholder(new Label("No hay perros registrados para mostrar."));
        }
        if (tablaRegistroAdopciones != null) {
            colAdopNombrePerro.setCellValueFactory(new PropertyValueFactory<>("nombrePerro"));
            colAdopFecha.setCellValueFactory(new PropertyValueFactory<>("fechaAdopcion"));
            colAdopHora.setCellValueFactory(new PropertyValueFactory<>("horaAdopcion"));
            colAdopCausante.setCellValueFactory(new PropertyValueFactory<>("nombreAdoptante"));
            colAdopContacto.setCellValueFactory(new PropertyValueFactory<>("contactoAdoptante"));
            tablaRegistroAdopciones.setPlaceholder(new Label("No hay adopciones registradas para mostrar."));
        }
    }

    private void cargarDatosParaTablaRegistroPerros() {
        if (TablaRegistroPerros == null || perroDao == null || idProtectoraActual <= 0) return;
        try {
            List<RegistroPerroInfo> datosPerros = perroDao.obtenerRegistrosPerrosParaTabla(idProtectoraActual);
            ObservableList<RegistroPerroInfo> observableDatosPerros = FXCollections.observableArrayList(datosPerros);
            TablaRegistroPerros.setItems(observableDatosPerros);
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error BD", "No se pudieron cargar los registros de perros: " + e.getMessage());
        }
    }

    private void cargarDatosParaTablaAdopciones() {
        if (tablaRegistroAdopciones == null || peticionAdopcionDao == null || idProtectoraActual <= 0) return;
        try {
            List<RegistroAdopcionInfo> datosAdopciones = peticionAdopcionDao.obtenerAdopcionesAceptadasParaTabla(idProtectoraActual);
            ObservableList<RegistroAdopcionInfo> observableDatosAdopciones = FXCollections.observableArrayList(datosAdopciones);
            tablaRegistroAdopciones.setItems(observableDatosAdopciones);
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error BD", "No se pudieron cargar los registros de adopciones: " + e.getMessage());
        }
    }

    private void cargarYMostrarPerrosDeProtectora() {
        this.listaDePerrosDeLaProtectora = new ArrayList<>();
        if (perroDao == null || idProtectoraActual <= 0) {
            if (dogGrid != null) dogGrid.getChildren().clear();
            actualizarVisibilidadLabelNoPerros();
            return;
        }
        try {
            this.listaDePerrosDeLaProtectora = perroDao.obtenerPerrosPorProtectora(this.idProtectoraActual);
            if (this.listaDePerrosDeLaProtectora == null) this.listaDePerrosDeLaProtectora = new ArrayList<>();
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error DB", "No se pudieron cargar los perros: " + e.getMessage());
        }
        actualizarVisibilidadLabelNoPerros();
        if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null && ((Stage)mainBorderPane.getScene().getWindow()).isShowing()) {
            Platform.runLater(() -> adaptarUIAlTamanoVentana((Stage) mainBorderPane.getScene().getWindow()));
        } else {
            popularGridDePerrosProtectora();
        }
    }

    private void actualizarVisibilidadLabelNoPerros() {
        if (lblNoPerrosEnGrid != null && dogGrid != null) {
            boolean hayPerros = listaDePerrosDeLaProtectora != null && !listaDePerrosDeLaProtectora.isEmpty();
            lblNoPerrosEnGrid.setVisible(!hayPerros);
            dogGrid.setVisible(hayPerros);
        }
    }

    private void configurarListenersDeVentana() {
        if (mainBorderPane != null && mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null) {
            Stage stage = (Stage) mainBorderPane.getScene().getWindow();
            Runnable adaptar = () -> Platform.runLater(() -> { if (stage.isShowing() && stage.getWidth() > 0 && !Double.isNaN(stage.getWidth())) adaptarUIAlTamanoVentana(stage); });
            if (stage.isShowing()) adaptar.run(); else stage.setOnShown(event -> adaptar.run());
            stage.widthProperty().addListener((obs, o, n) -> adaptar.run());
            stage.heightProperty().addListener((obs, o, n) -> adaptar.run());
            stage.maximizedProperty().addListener((obs, o, n) -> adaptar.run());
        } else if (mainBorderPane != null) {
            mainBorderPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> { if (newWindow instanceof Stage) configurarListenersDeVentanaEspecificos((Stage)newWindow); });
                    if (newScene.getWindow() instanceof Stage) configurarListenersDeVentanaEspecificos((Stage)newScene.getWindow());
                }
            });
        }
    }

    private void configurarListenersDeVentanaEspecificos(Stage stage) {
        Runnable adaptar = () -> Platform.runLater(() -> { if (stage.isShowing() && stage.getWidth() > 0 && !Double.isNaN(stage.getWidth())) adaptarUIAlTamanoVentana(stage); });
        if (stage.isShowing()) adaptar.run(); else stage.setOnShown(event -> adaptar.run());
        stage.widthProperty().addListener((obs, o, n) -> adaptar.run());
        stage.heightProperty().addListener((obs, o, n) -> adaptar.run());
        stage.maximizedProperty().addListener((obs, o, n) -> adaptar.run());
    }

    private void adaptarUIAlTamanoVentana(Stage stage) {
        if (stage == null || stage.getWidth() <= 0 || Double.isNaN(stage.getWidth()) || dogGrid == null) return;
        adaptarContenidoAlAnchoYPopular(stage.getWidth());
        adaptarContenidoALaAltura(stage.getHeight());
    }

    private int calcularColumnasSegunAncho(double anchoGridDisponible) {
        if (anchoGridDisponible <= 0) return 1;
        double hgap = (dogGrid.getHgap() > 0) ? dogGrid.getHgap() : CARD_HORIZONTAL_GAP;
        double anchoTarjetaEstimado = TARJETA_PREF_WIDTH + hgap;
        return Math.min(Math.max(1, (int) (anchoGridDisponible / anchoTarjetaEstimado)), 5);
    }

    private void adaptarContenidoAlAnchoYPopular(double anchoVentana) {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null || mainBorderPane == null) return;
        double paddingLateralTotalVBox = 0;
        if(mainBorderPane.getCenter() instanceof ScrollPane && ((ScrollPane) mainBorderPane.getCenter()).getContent() instanceof VBox) {
            VBox contentVBox = (VBox) ((ScrollPane) mainBorderPane.getCenter()).getContent();
            paddingLateralTotalVBox = contentVBox.getPadding().getLeft() + contentVBox.getPadding().getRight();
        }
        double paddingLateralGrid = dogGrid.getPadding().getLeft() + dogGrid.getPadding().getRight();
        double anchoDisponibleParaGrid = anchoVentana - paddingLateralTotalVBox - paddingLateralGrid - 40;
        int nuevasColumnas = calcularColumnasSegunAncho(anchoDisponibleParaGrid);

        boolean numColumnasCambio = dogGrid.getColumnConstraints().size() != nuevasColumnas;
        boolean gridVacioYHayDatos = dogGrid.getChildren().isEmpty() && !listaDePerrosDeLaProtectora.isEmpty();
        Integer hashListaActual = (dogGrid.getUserData() instanceof Integer) ? (Integer) dogGrid.getUserData() : null;
        boolean datosCambiaron = hashListaActual == null || !Objects.equals(hashListaActual, listaDePerrosDeLaProtectora.hashCode());

        if (numColumnasCambio || gridVacioYHayDatos || datosCambiaron ) {
            if (numColumnasCambio) {
                dogGrid.getColumnConstraints().clear();
                for (int i = 0; i < nuevasColumnas; i++) {
                    ColumnConstraints colConst = new ColumnConstraints();
                    colConst.setPercentWidth(100.0 / nuevasColumnas);
                    colConst.setHgrow(Priority.SOMETIMES);
                    dogGrid.getColumnConstraints().add(colConst);
                }
            }
            popularGridDePerrosProtectora();
            dogGrid.setUserData(listaDePerrosDeLaProtectora.hashCode());
        }
    }

    private void adaptarContenidoALaAltura(double nuevaAlturaVentana) {
        if (mainBorderPane.getCenter() instanceof ScrollPane) {
            ScrollPane mainScrollPane = (ScrollPane) mainBorderPane.getCenter();
            if (mainScrollPane.getContent() instanceof VBox) {
                VBox vBoxPrincipal = (VBox) mainScrollPane.getContent();
                if (vBoxPrincipal.getChildren().size() >= 2 && vBoxPrincipal.getChildren().get(0) instanceof VBox) {
                    VBox vBoxSeccionGrid = (VBox) vBoxPrincipal.getChildren().get(0);
                    double topHeight = (mainBorderPane.getTop() != null) ? mainBorderPane.getTop().getLayoutBounds().getHeight() : HEADER_HEIGHT_ESTIMADA;
                    double alturaSeccionTablas = (tablasStackPane != null && tablasStackPane.isVisible()) ? TABLAS_SECTION_HEIGHT_ESTIMADA : 0;
                    if (vBoxPrincipal.getChildren().size() > 1 && vBoxPrincipal.getChildren().get(1) instanceof VBox) {
                        VBox vBoxTablasContainer = (VBox) vBoxPrincipal.getChildren().get(1);
                        if (vBoxTablasContainer.getChildren().size() > 0 && vBoxTablasContainer.getChildren().get(0) instanceof HBox) {
                            alturaSeccionTablas += ((HBox)vBoxTablasContainer.getChildren().get(0)).getPrefHeight() > 0 ?
                                    ((HBox)vBoxTablasContainer.getChildren().get(0)).getPrefHeight() : 40;
                        }
                        alturaSeccionTablas += vBoxTablasContainer.getPadding().getTop() + vBoxTablasContainer.getPadding().getBottom() + vBoxTablasContainer.getSpacing();
                    }
                    double paddingVBoxPrincipal = vBoxPrincipal.getPadding().getTop() + vBoxPrincipal.getPadding().getBottom();
                    double spacingVBoxPrincipal = vBoxPrincipal.getSpacing();
                    double alturaDisponibleGrid = nuevaAlturaVentana - topHeight - alturaSeccionTablas - paddingVBoxPrincipal - spacingVBoxPrincipal - HBOX_TITULO_GRID_HEIGHT_ESTIMADA;
                    vBoxSeccionGrid.setPrefHeight(Math.max(MIN_GRID_HEIGHT, alturaDisponibleGrid));
                }
            }
        }
    }

    private void popularGridDePerrosProtectora() {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null) return;
        dogGrid.getChildren().clear();
        int numColumnas = Math.max(1, dogGrid.getColumnConstraints().size());
        actualizarVisibilidadLabelNoPerros();
        if (listaDePerrosDeLaProtectora.isEmpty()) return;
        int col = 0, row = 0;
        for (Perro perro : listaDePerrosDeLaProtectora) {
            dogGrid.add(crearTarjetaPerroProtectora(perro), col, row);
            col++; if (col >= numColumnas) { col = 0; row++; }
        }
    }

    private VBox crearTarjetaPerroProtectora(Perro perro) {
        VBox card = new VBox(5);
        card.setPrefWidth(TARJETA_PREF_WIDTH); card.setMaxWidth(TARJETA_PREF_WIDTH); card.setMinWidth(TARJETA_PREF_WIDTH);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(String.format("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-padding: %f;", CARD_INTERNAL_PADDING));
        card.setMinHeight(260);
        StackPane imageContainer = new StackPane(); imageContainer.setPrefSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);
        ImageView imgView = new ImageView(); String imagePath = perro.getFoto();
        try {
            Image loadedImage = null;
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                String fullResourcePath = imagePath.startsWith("/") ? imagePath : RUTA_BASE_IMAGENES_PERROS_RESOURCES + imagePath;
                fullResourcePath = fullResourcePath.replace("//", "/");
                try(InputStream stream = getClass().getResourceAsStream(fullResourcePath)){ if (stream != null) loadedImage = new Image(stream); }
            }
            if (loadedImage == null || loadedImage.isError()) { try(InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO)){ if(placeholderStream != null) loadedImage = new Image(placeholderStream);}}
            imgView.setImage(loadedImage);
        } catch (Exception e) {e.printStackTrace();}
        imgView.setPreserveRatio(true); imgView.setSmooth(true);
        imgView.setFitWidth(TARJETA_IMG_AREA_WIDTH); imgView.setFitHeight(TARJETA_IMG_AREA_HEIGHT);
        imageContainer.getChildren().add(imgView); VBox.setMargin(imageContainer, new Insets(0,0,8,0));
        Label lblNombrePerro = new Label(perro.getNombre());
        lblNombrePerro.setStyle("-fx-background-color: #A9D18E; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 5; -fx-background-radius: 3;");
        lblNombrePerro.setAlignment(Pos.CENTER); lblNombrePerro.setPrefWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setMaxWidth(TARJETA_IMG_AREA_WIDTH); lblNombrePerro.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        Label lblEstado = new Label("S".equalsIgnoreCase(perro.getAdoptado()) ? "Adoptado" : "En Adopción");
        lblEstado.setStyle("S".equalsIgnoreCase(perro.getAdoptado()) ? "-fx-text-fill: grey;" : "-fx-text-fill: green; -fx-font-weight:bold;");
        VBox.setMargin(lblEstado, new Insets(4,0,4,0));
        HBox botonesTarjeta = new HBox(10); botonesTarjeta.setAlignment(Pos.CENTER);
        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-background-color: transparent; -fx-border-color: #3498DB; -fx-text-fill: #3498DB; -fx-border-radius: 3; -fx-font-size:11px;");
        btnEditar.setOnAction(event -> handleEditarPerro(perro));
        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-border-color: #E74C3C; -fx-text-fill: #E74C3C; -fx-border-radius: 3; -fx-font-size:11px;");
        btnEliminar.setOnAction(event -> handleEliminarPerro(perro));
        botonesTarjeta.getChildren().addAll(btnEditar, btnEliminar);
        card.getChildren().addAll(imageContainer, lblNombrePerro, lblEstado, botonesTarjeta);
        card.setUserData(perro);
        return card;
    }

    private void handleEditarPerro(Perro perro) {
        if (this.usuarioCuentaLogueada == null || this.usuarioCuentaLogueada.getIdUsuario() <= 0) { UtilidadesVentana.mostrarAlertaError("Error Sesión", "No se puede editar sin identificar al usuario."); return; }
        String fxml = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
        Stage owner = (mainBorderPane.getScene() != null) ? (Stage) mainBorderPane.getScene().getWindow() : null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            FormularioPerroController formCtrl = loader.getController();
            if (formCtrl != null) formCtrl.initDataParaEdicion(perro, this.usuarioCuentaLogueada.getIdUsuario());
            else { UtilidadesVentana.mostrarAlertaError("Error", "Controlador FormularioPerro no hallado."); return; }
            UtilidadesVentana.mostrarVentanaComoDialogo(root, "Editar Perro: " + perro.getNombre(), owner);
            cargarYMostrarPerrosDeProtectora();
        } catch (IOException e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error", "No se pudo abrir formulario de edición."); }
    }

    private void handleEliminarPerro(Perro perro) {
        if (UtilidadesVentana.mostrarAlertaConfirmacion("Confirmar", "¿Eliminar a " + perro.getNombre() + "?")) {
            if (perroDao != null) {
                try {
                    if (perroDao.eliminarPerro(perro.getIdPerro())) {
                        UtilidadesVentana.mostrarAlertaInformacion("Éxito", perro.getNombre() + " eliminado.");
                        cargarYMostrarPerrosDeProtectora();
                    } else UtilidadesVentana.mostrarAlertaError("Error", "No se pudo eliminar perro de BD.");
                } catch (SQLException e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error BD", "No se pudo eliminar: " + e.getMessage()); }
            }
        }
    }

    @FXML void IrABandeja(MouseEvent event) { UtilidadesVentana.mostrarAlertaInformacion("Próximamente", "Bandeja no implementada."); }
    @FXML void IrAPerfilUsuario(MouseEvent event) {UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/FormularioUsuario.fxml", "Perfil de Usuario", true); }

    @FXML
    void RegistroAdopciones(ActionEvent event) {
        mostrandoRegistroPerros = !mostrandoRegistroPerros;
        if (mostrandoRegistroPerros) {
            if(lblRegistroTitulo != null) lblRegistroTitulo.setText("Registro de Perros");
            if(BtnToggleRegistro != null) BtnToggleRegistro.setText("Ver Registro de Adopciones");
            if (TablaRegistroPerros != null) { TablaRegistroPerros.setVisible(true); cargarDatosParaTablaRegistroPerros(); }
            if (tablaRegistroAdopciones != null) tablaRegistroAdopciones.setVisible(false);
        } else {
            if(lblRegistroTitulo != null) lblRegistroTitulo.setText("Registro de Adopciones");
            if(BtnToggleRegistro != null) BtnToggleRegistro.setText("Ver Registro de Perros");
            if (TablaRegistroPerros != null) TablaRegistroPerros.setVisible(false);
            if (tablaRegistroAdopciones != null) { tablaRegistroAdopciones.setVisible(true); cargarDatosParaTablaAdopciones(); }
        }
    }

    @FXML
    void NuevoPerro(ActionEvent actionEvent) {
    }
}