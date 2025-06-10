package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.Model.RegistroAdopcionInfo;
import com.proyectointegral2.Model.RegistroCitaInfo;
import com.proyectointegral2.Model.SesionUsuario;
import com.proyectointegral2.dao.PerroDao;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.dao.PeticionAdopcionDao;
import com.proyectointegral2.dao.ReservaCitaDao;
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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainProtectoraController {

    // --- @FXML Injections ---
    @FXML private BorderPane mainBorderPane;
    @FXML private ImageView logoImageView;
    @FXML private ImageView ImgIconBandeja;
    @FXML private ImageView ImgIconUsuario;
    @FXML private Button BtnNuevoPerro;
    @FXML private GridPane dogGrid;
    @FXML private Label lblNoPerrosEnGrid;
    @FXML private Label lblRegistroTitulo;
    @FXML private Button BtnToggleRegistro;
    @FXML private StackPane tablasStackPane;
    @FXML private ScrollPane mainScrollPane;
    @FXML private VBox vBoxPrincipalContenidoScroll;
    @FXML private VBox vBoxSeccionGrid;

    @FXML private TableView<RegistroCitaInfo> TablaRegistroCitas;
    @FXML private TableColumn<RegistroCitaInfo, String> ColumNombrePerro;
    @FXML private TableColumn<RegistroCitaInfo, LocalDate> ColumDiaCita;
    @FXML private TableColumn<RegistroCitaInfo, LocalTime> ColumHoraCita;
    @FXML private TableColumn<RegistroCitaInfo, String> ColumNombreClienteCita;
    @FXML private TableColumn<RegistroCitaInfo, String> ColumEstadoCita;

    @FXML private TableView<RegistroAdopcionInfo> TablaRegistroAdopciones;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumNombrePerroAdopcion;
    @FXML private TableColumn<RegistroAdopcionInfo, LocalDate> ColumFechaAdopcion;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumHoraAdopcion;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumNombreClienteAdopcion;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumEstadoAdopcion;

    // --- Constants ---
    private static final String RUTA_BASE_IMAGENES_PERROS_RESOURCES = "/assets/Imagenes/perros/";
    private static final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.png";
    private static final String RUTA_FXML_LOGIN = "/com/proyectointegral2/Vista/Login.fxml";
    private static final String RUTA_FXML_FORMULARIO_PERRO = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
    private static final String RUTA_FXML_PERFIL_PROTECTORA = "/com/proyectointegral2/Vista/PerfilProtectora.fxml";

    private static final double HEADER_HEIGHT_ESTIMADA = 70.0;
    private static final double TARJETA_IMG_AREA_WIDTH = 150.0;
    private static final double TARJETA_IMG_AREA_HEIGHT = 140.0;
    private static final double TARJETA_PREF_WIDTH = 180.0;
    private static final double TARJETA_PREF_HEIGHT = 250.0;
    private static final double CARD_HORIZONTAL_GAP = 15.0;
    private static final double CARD_INTERNAL_PADDING = 8.0;
    private static final double HBOX_TITULO_GRID_HEIGHT_ESTIMADA = 50.0;
    private static final double TABLAS_SECTION_CONTAINER_HEIGHT_ESTIMADA = 300.0;
    private static final double MIN_GRID_CONTAINER_HEIGHT = TARJETA_PREF_HEIGHT + 20;
    private static final double PREFERRED_GRID_WIDTH_FALLBACK = 1000.0;
    private static final DecimalFormat df = new DecimalFormat("#.##");

    // --- Instance Variables ---
    private List<Perro> listaDePerrosDeLaProtectora;
    private Usuario usuarioCuentaLogueada;
    private Protectora protectoraInfoActual;
    private int idProtectoraActual;
    private String nombreProtectoraActual;
    private boolean mostrandoRegistroCitas = true;

    private PerroDao perroDao;
    private ProtectoraDao protectoraDao;
    private PeticionAdopcionDao peticionAdopcionDao;
    private ReservaCitaDao reservaCitaDao;

    @FXML
    public void initialize() {
        System.out.println("MainProtectoraController inicializado. Validando sesión...");
        try {
            this.perroDao = new PerroDao();
            this.protectoraDao = new ProtectoraDao();
            this.peticionAdopcionDao = new PeticionAdopcionDao();
            this.reservaCitaDao = new ReservaCitaDao();
        } catch (Exception e) { handleDaoInitializationError(e); return; }

        if (!validarYConfigurarSesionProtectora()) return;

        configurarColumnasDeTablas();
        cargarYMostrarPerrosEnGrid();
        configurarVisibilidadInicialDeTablas();
        configurarListenersDeVentanaParaResponsividad();
        actualizarVisibilidadDelLabelNoPerros();
    }

    private void handleDaoInitializationError(Exception e) {
        System.err.println("Error crítico al inicializar DAOs: " + e.getMessage());
        e.printStackTrace();
        UtilidadesVentana.mostrarAlertaError("Error Crítico de Sistema", "No se pudo inicializar el acceso a datos.");
        if (BtnNuevoPerro != null) BtnNuevoPerro.setDisable(true);
    }

    private boolean validarYConfigurarSesionProtectora() {
        this.usuarioCuentaLogueada = SesionUsuario.getUsuarioLogueado();
        this.idProtectoraActual = SesionUsuario.getEntidadIdEspecifica();

        System.out.println("MainProtectoraController.validarYConfigurarSesion: idProtectoraActual desde SesionUsuario: " + this.idProtectoraActual);

        if (this.usuarioCuentaLogueada == null || this.idProtectoraActual <= 0 ||
                !"PROTECTORA".equalsIgnoreCase(this.usuarioCuentaLogueada.getRol())) {
            UtilidadesVentana.mostrarAlertaError("Error Sesión", "Info de protectora inválida. Redirigiendo al login.");
            Platform.runLater(() -> {
                if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null) {
                    UtilidadesVentana.cambiarEscena(RUTA_FXML_LOGIN, "Login", false);
                }
            });
            return false;
        }
        try {
            this.protectoraInfoActual = protectoraDao.obtenerProtectoraPorId(this.idProtectoraActual);
            if (this.protectoraInfoActual != null) {
                this.nombreProtectoraActual = this.protectoraInfoActual.getNombre();
                System.out.println("Protectora: " + this.nombreProtectoraActual + " (ID: " + this.idProtectoraActual + ") cargada.");
                return true;
            } else {
                throw new SQLException("No se encontró info para protectora ID: " + this.idProtectoraActual);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Datos", "No se pudo cargar info de protectora: " + e.getMessage());
            Platform.runLater(() -> UtilidadesVentana.cambiarEscena(RUTA_FXML_LOGIN, "Login", false));
            return false;
        }
    }

    private void configurarColumnasDeTablas() {
        if (TablaRegistroCitas != null && ColumNombrePerro != null) {
            ColumNombrePerro.setCellValueFactory(new PropertyValueFactory<>("nombrePerro"));
            ColumDiaCita.setCellValueFactory(new PropertyValueFactory<>("fechaCita"));
            ColumHoraCita.setCellValueFactory(new PropertyValueFactory<>("horaCita"));
            ColumNombreClienteCita.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
            ColumEstadoCita.setCellValueFactory(new PropertyValueFactory<>("estadoCita"));
            TablaRegistroCitas.setPlaceholder(new Label("No hay citas programadas para mostrar."));
        }
        if (TablaRegistroAdopciones != null && ColumNombrePerroAdopcion != null) {
            ColumNombrePerroAdopcion.setCellValueFactory(new PropertyValueFactory<>("nombrePerro"));
            ColumFechaAdopcion.setCellValueFactory(new PropertyValueFactory<>("fechaPeticion"));
            ColumHoraAdopcion.setCellValueFactory(new PropertyValueFactory<>("horaPeticion"));
            ColumNombreClienteAdopcion.setCellValueFactory(new PropertyValueFactory<>("nombreAdoptante"));
            ColumEstadoAdopcion.setCellValueFactory(new PropertyValueFactory<>("estadoPeticion"));
            TablaRegistroAdopciones.setPlaceholder(new Label("No hay registros de adopción para mostrar."));
        }
    }

    private void configurarVisibilidadInicialDeTablas() {
        mostrandoRegistroCitas = true;
        actualizarContenidoSeccionRegistros();
    }

    private void cargarYMostrarPerrosEnGrid() {
        this.listaDePerrosDeLaProtectora = new ArrayList<>();
        if (perroDao == null || idProtectoraActual <= 0) {
            handlePerroDaoError("PerroDao no inicializado o ID Protectora inválido en cargarYMostrarPerrosEnGrid. ID_Protectora: " + idProtectoraActual);
            return;
        }
        try {
            this.listaDePerrosDeLaProtectora = perroDao.obtenerPerrosPorProtectora(this.idProtectoraActual);
            if (this.listaDePerrosDeLaProtectora == null) this.listaDePerrosDeLaProtectora = new ArrayList<>();
        } catch (SQLException e) { handlePerroDaoError("No se pudieron cargar los perros de la protectora: " + e.getMessage()); }
        finalizeUIPerrosUpdate();
    }

    private void handlePerroDaoError(String errorMessage) {
        System.err.println(errorMessage);
        UtilidadesVentana.mostrarAlertaError("Error de Datos (Perros)", errorMessage);
        if (dogGrid != null) dogGrid.getChildren().clear();
        actualizarVisibilidadDelLabelNoPerros();
    }

    private void finalizeUIPerrosUpdate() {
        actualizarVisibilidadDelLabelNoPerros();
        if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null &&
                ((Stage)mainBorderPane.getScene().getWindow()).isShowing()) {
            Platform.runLater(() -> adaptarUIAlTamanoDeVentana((Stage) mainBorderPane.getScene().getWindow()));
        } else {
            Platform.runLater(this::popularGridConPerrosDeProtectora);
        }
    }

    private void actualizarVisibilidadDelLabelNoPerros() {
        if (lblNoPerrosEnGrid != null && dogGrid != null) {
            boolean hayPerros = this.listaDePerrosDeLaProtectora != null && !this.listaDePerrosDeLaProtectora.isEmpty();
            lblNoPerrosEnGrid.setVisible(!hayPerros);
            dogGrid.setVisible(hayPerros);
        }
    }

    private void configurarListenersDeVentanaParaResponsividad() {
        Runnable setupListeners = () -> {
            if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() instanceof Stage) {
                Stage stage = (Stage) mainBorderPane.getScene().getWindow();
                Runnable adaptarUI = () -> Platform.runLater(() -> {
                    if (stage.isShowing() && stage.getWidth() > 0 && !Double.isNaN(stage.getWidth())) {
                        adaptarUIAlTamanoDeVentana(stage);
                    }
                });
                if (stage.isShowing()) adaptarUI.run();
                else stage.setOnShown(event -> adaptarUI.run());
                stage.widthProperty().addListener((obs, oldVal, newVal) -> adaptarUI.run());
                stage.heightProperty().addListener((obs, oldVal, newVal) -> adaptarUI.run());
                stage.maximizedProperty().addListener((obs, oldVal, newVal) -> adaptarUI.run());
            }
        };
        if (mainBorderPane.getScene() != null) {
            if (mainBorderPane.getScene().getWindow() != null) setupListeners.run();
            else mainBorderPane.getScene().windowProperty().addListener((obs, oldW, newW) -> { if (newW != null) setupListeners.run(); });
        } else {
            mainBorderPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
                if (newScene != null) {
                    if (newScene.getWindow() != null) setupListeners.run();
                    else newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> { if (newWindow != null) setupListeners.run(); });
                }
            });
        }
    }

    private void adaptarUIAlTamanoDeVentana(Stage stage) {
        if (stage == null || stage.getWidth() <= 0 || Double.isNaN(stage.getWidth()) || dogGrid == null) return;
        adaptarAnchoDelGridYRepopular(stage.getWidth());
        adaptarAlturaDelContenedorDelGrid(stage.getHeight());
    }

    private int calcularNumeroDeColumnasSegunAncho(double anchoGridDisponible) {
        if (anchoGridDisponible <= 0) return 1;
        double hgap = (dogGrid != null && !Double.isNaN(dogGrid.getHgap())) ? dogGrid.getHgap() : CARD_HORIZONTAL_GAP;
        double anchoEstimadoTarjetaConEspacio = TARJETA_PREF_WIDTH + hgap;
        if (anchoEstimadoTarjetaConEspacio <= 0) return 1;
        int numColumnasCalculadas = Math.max(1, (int) Math.floor(anchoGridDisponible / anchoEstimadoTarjetaConEspacio));
        return Math.min(numColumnasCalculadas, 5);
    }

    private void adaptarAnchoDelGridYRepopular(double anchoVentana) {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null || mainBorderPane == null || mainScrollPane == null) return;
        double paddingLateralesScrollPane = mainScrollPane.getPadding().getLeft() + mainScrollPane.getPadding().getRight();
        double paddingLateralesVBoxPrincipal = 0;
        double paddingLateralesVBoxGrid = 0;
        if (vBoxPrincipalContenidoScroll != null) {
            paddingLateralesVBoxPrincipal = vBoxPrincipalContenidoScroll.getPadding().getLeft() + vBoxPrincipalContenidoScroll.getPadding().getRight();
            if (vBoxSeccionGrid != null) {
                paddingLateralesVBoxGrid = vBoxSeccionGrid.getPadding().getLeft() + vBoxSeccionGrid.getPadding().getRight();
            }
        }
        double paddingLateralesGridPane = dogGrid.getPadding().getLeft() + dogGrid.getPadding().getRight();
        double espacioEstimadoScrollbarVertical = 20.0;
        double anchoDisponibleParaGrid = anchoVentana - paddingLateralesScrollPane - paddingLateralesVBoxPrincipal - paddingLateralesVBoxGrid - paddingLateralesGridPane - espacioEstimadoScrollbarVertical;
        int nuevasColumnas = calcularNumeroDeColumnasSegunAncho(anchoDisponibleParaGrid);
        boolean necesitaCambioDeColumnas = dogGrid.getColumnConstraints().size() != nuevasColumnas;
        boolean gridVacioPeroHayDatos = dogGrid.getChildren().isEmpty() && !listaDePerrosDeLaProtectora.isEmpty();
        Integer hashListaGuardado = (dogGrid.getUserData() instanceof Integer) ? (Integer) dogGrid.getUserData() : null;
        boolean listaDePerrosHaCambiado = hashListaGuardado == null || !Objects.equals(hashListaGuardado, listaDePerrosDeLaProtectora.hashCode());

        if (necesitaCambioDeColumnas || gridVacioPeroHayDatos || listaDePerrosHaCambiado) {
            dogGrid.getColumnConstraints().clear();
            for (int i = 0; i < nuevasColumnas; i++) {
                ColumnConstraints colConst = new ColumnConstraints();
                if (nuevasColumnas == 1 && anchoDisponibleParaGrid < TARJETA_PREF_WIDTH + CARD_HORIZONTAL_GAP) {
                    colConst.setHgrow(Priority.NEVER);
                    colConst.setMinWidth(TARJETA_PREF_WIDTH);
                    colConst.setPrefWidth(TARJETA_PREF_WIDTH);
                } else {
                    colConst.setPercentWidth(100.0 / nuevasColumnas);
                    colConst.setHgrow(Priority.SOMETIMES);
                }
                dogGrid.getColumnConstraints().add(colConst);
            }
            popularGridConPerrosDeProtectora();
            dogGrid.setUserData(listaDePerrosDeLaProtectora.hashCode());
        }
    }

    private void adaptarAlturaDelContenedorDelGrid(double alturaVentana) {
        if (vBoxPrincipalContenidoScroll == null || vBoxSeccionGrid == null || mainBorderPane.getTop() == null) return;

        Node topNode = mainBorderPane.getTop();
        double topHeight = topNode.getLayoutBounds().getHeight() > 0 ? topNode.getLayoutBounds().getHeight() : HEADER_HEIGHT_ESTIMADA;
        double alturaSeccionTablas = 0;
        Node vBoxTablasNode = null;
        if (vBoxPrincipalContenidoScroll.getChildren().size() > 1 && vBoxPrincipalContenidoScroll.getChildren().get(1) instanceof VBox) {
            vBoxTablasNode = vBoxPrincipalContenidoScroll.getChildren().get(1);
            alturaSeccionTablas = ((VBox)vBoxTablasNode).getPrefHeight();
            if (alturaSeccionTablas <=0 || alturaSeccionTablas == Region.USE_COMPUTED_SIZE) {
                alturaSeccionTablas = TABLAS_SECTION_CONTAINER_HEIGHT_ESTIMADA;
            }
            alturaSeccionTablas += ((VBox)vBoxTablasNode).getPadding().getTop() + ((VBox)vBoxTablasNode).getPadding().getBottom();
        }

        double paddingVerticalVBoxPrincipal = vBoxPrincipalContenidoScroll.getPadding().getTop() + vBoxPrincipalContenidoScroll.getPadding().getBottom();
        double spacingVBoxPrincipal = vBoxPrincipalContenidoScroll.getSpacing();
        double alturaDisponibleParaVBoxGrid = alturaVentana - topHeight - alturaSeccionTablas - paddingVerticalVBoxPrincipal - spacingVBoxPrincipal;
        vBoxSeccionGrid.setPrefHeight(Math.max(MIN_GRID_CONTAINER_HEIGHT, alturaDisponibleParaVBoxGrid));
    }

    private void popularGridConPerrosDeProtectora() {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null) {
            actualizarVisibilidadDelLabelNoPerros(); return;
        }
        dogGrid.getChildren().clear();
        int numColumnas = Math.max(1, dogGrid.getColumnConstraints().size());
        if (dogGrid.getColumnConstraints().isEmpty()){
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setMinWidth(TARJETA_PREF_WIDTH);
            colConst.setPrefWidth(Region.USE_COMPUTED_SIZE);
            colConst.setHgrow(Priority.SOMETIMES);
            dogGrid.getColumnConstraints().add(colConst);
            numColumnas = 1;
        }
        int columna = 0; int fila = 0;
        for (Perro perro : listaDePerrosDeLaProtectora) {
            VBox tarjetaPerro = crearTarjetaPerroParaProtectora(perro);
            dogGrid.add(tarjetaPerro, columna, fila);
            GridPane.setMargin(tarjetaPerro, new Insets(0,0,CARD_HORIZONTAL_GAP,0));
            columna++;
            if (columna >= numColumnas) { columna = 0; fila++; }
        }
        actualizarVisibilidadDelLabelNoPerros();
    }

    private VBox crearTarjetaPerroParaProtectora(Perro perro) {
        VBox card = new VBox(4);
        card.setPrefWidth(TARJETA_PREF_WIDTH);
        card.setMaxWidth(TARJETA_PREF_WIDTH);
        card.setMinWidth(TARJETA_PREF_WIDTH);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(String.format(
                "-fx-background-color: white; -fx-border-color: #E0E0E0; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0, 0, 1); -fx-padding: %f;",
                CARD_INTERNAL_PADDING
        ));
        card.setPrefHeight(TARJETA_PREF_HEIGHT);
        card.setMaxHeight(TARJETA_PREF_HEIGHT);

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);
        imageContainer.setMinSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);
        imageContainer.setMaxSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);

        ImageView imgView = new ImageView();
        cargarImagenPerroEnTarjeta(imgView, perro.getFoto());

        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        imgView.setFitWidth(TARJETA_IMG_AREA_WIDTH);
        imgView.setFitHeight(TARJETA_IMG_AREA_HEIGHT);
        StackPane.setAlignment(imgView, Pos.CENTER);
        imageContainer.getChildren().add(imgView);

        Label lblNombrePerro = new Label(Objects.requireNonNullElse(perro.getNombre(), "Sin Nombre"));
        lblNombrePerro.setStyle("-fx-background-color: #A9D18E; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 4px 6px; -fx-background-radius: 3;");
        lblNombrePerro.setAlignment(Pos.CENTER);
        lblNombrePerro.setPrefWidth(TARJETA_IMG_AREA_WIDTH - 10);
        lblNombrePerro.setMaxWidth(TARJETA_IMG_AREA_WIDTH - 10);
        lblNombrePerro.setTextOverrun(OverrunStyle.ELLIPSIS);

        Label lblEstado = new Label(perro.isAdoptado() ? "Adoptado" : "En Adopción");
        String styleEstado = "-fx-font-size: 11px; ";
        if (perro.isAdoptado()) styleEstado += "-fx-text-fill: grey;";
        else styleEstado += "-fx-text-fill: green; -fx-font-weight:bold;";
        lblEstado.setStyle(styleEstado);

        HBox botonesTarjeta = new HBox(5);
        botonesTarjeta.setAlignment(Pos.CENTER);
        botonesTarjeta.setMaxWidth(Double.MAX_VALUE);

        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-background-color: transparent; -fx-border-color: #3498DB; -fx-text-fill: #3498DB; -fx-border-radius: 3; -fx-font-size:10px; -fx-padding: 2px 5px;");
        btnEditar.setOnAction(event -> handleEditarPerro(perro));
        HBox.setHgrow(btnEditar, Priority.ALWAYS);
        btnEditar.setMaxWidth(Double.MAX_VALUE);

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-border-color: #E74C3C; -fx-text-fill: #E74C3C; -fx-border-radius: 3; -fx-font-size:10px; -fx-padding: 2px 5px;");
        btnEliminar.setOnAction(event -> handleEliminarPerro(perro));
        HBox.setHgrow(btnEliminar, Priority.ALWAYS);
        btnEliminar.setMaxWidth(Double.MAX_VALUE);

        botonesTarjeta.getChildren().addAll(btnEditar, btnEliminar);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(imageContainer, lblNombrePerro, lblEstado, spacer, botonesTarjeta);
        VBox.setMargin(imageContainer, new Insets(0,0,4,0));
        VBox.setMargin(lblNombrePerro, new Insets(0,0,2,0));
        VBox.setMargin(lblEstado, new Insets(0,0,4,0));
        VBox.setMargin(botonesTarjeta, new Insets(0,0,0,0));
        card.setUserData(perro);
        return card;
    }

    private void cargarImagenPerroEnTarjeta(ImageView imageView, String rutaFotoRelativa) {
        Image imagenCargada = null;
        if (rutaFotoRelativa != null && !rutaFotoRelativa.trim().isEmpty()) {
            String rutaCompletaRecurso = rutaFotoRelativa.startsWith("/") ? rutaFotoRelativa : RUTA_BASE_IMAGENES_PERROS_RESOURCES + rutaFotoRelativa;
            rutaCompletaRecurso = rutaCompletaRecurso.replace("//", "/");
            try (InputStream stream = getClass().getResourceAsStream(rutaCompletaRecurso)) {
                if (stream != null) imagenCargada = new Image(stream);
                else System.err.println("WARN: Imagen de perro no encontrada en classpath: " + rutaCompletaRecurso);
            } catch (Exception e) { System.err.println("ERROR: Excepción al cargar imagen '" + rutaCompletaRecurso + "': " + e.getMessage());}
        }
        if (imagenCargada == null || imagenCargada.isError()) {
            try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO)) {
                if (placeholderStream != null) imagenCargada = new Image(placeholderStream);
                else System.err.println("Error Crítico: Placeholder no encontrado: " + RUTA_IMAGEN_PLACEHOLDER_PERRO);
            } catch (Exception e) { System.err.println("Excepción crítica al cargar placeholder: " + e.getMessage());}
        }
        imageView.setImage(imagenCargada);
    }

    private void handleEditarPerro(Perro perro) {
        if (perro == null || this.idProtectoraActual <= 0) return;
        System.out.println("INFO: Editando perro ID: " + perro.getIdPerro());
        Stage owner = (mainBorderPane.getScene() != null) ? (Stage) mainBorderPane.getScene().getWindow() : null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_FXML_FORMULARIO_PERRO));
            Parent root = loader.load();
            FormularioPerroController formController = loader.getController();
            if (formController != null) {
                formController.initDataParaEdicion(perro, this.idProtectoraActual);
                UtilidadesVentana.mostrarVentanaComoDialogo(root, "Editar Perro: " + perro.getNombre(), owner);
                cargarYMostrarPerrosEnGrid();
            } else { UtilidadesVentana.mostrarAlertaError("Error Interno", "No se pudo cargar controlador de formulario."); }
        } catch (IOException e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error Navegación", "No se pudo abrir formulario: " + e.getMessage()); }
    }

    private void handleEliminarPerro(Perro perro) {
        if (perro == null) return;
        if (UtilidadesVentana.mostrarAlertaConfirmacion("Confirmar", "¿Eliminar a " + perro.getNombre() + "?")) {
            if (perroDao != null) {
                try {
                    if (perroDao.eliminarPerro(perro.getIdPerro())) {
                        UtilidadesVentana.mostrarAlertaInformacion("Éxito", perro.getNombre() + " eliminado.");
                        cargarYMostrarPerrosEnGrid();
                    } else { UtilidadesVentana.mostrarAlertaError("Error", "No se pudo eliminar el perro de la BD."); }
                } catch (SQLException e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error BD", "Error al eliminar perro: " + e.getMessage()); }
            }
        }
    }

    @FXML
    void NuevoPerro(ActionEvent event) {
        System.out.println("INFO: Abriendo formulario para añadir nuevo perro para protectora ID: " + this.idProtectoraActual);
        Stage ownerStage = (mainBorderPane.getScene() != null) ? (Stage) mainBorderPane.getScene().getWindow() : null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_FXML_FORMULARIO_PERRO));
            Parent root = loader.load();
            FormularioPerroController formularioController = loader.getController();
            if (formularioController != null) {
                formularioController.initDataParaNuevoPerro(this.idProtectoraActual);
                UtilidadesVentana.mostrarVentanaComoDialogo(root, "Añadir Nuevo Perro", ownerStage);
                cargarYMostrarPerrosEnGrid();
            } else { UtilidadesVentana.mostrarAlertaError("Error Interno", "No se pudo cargar controlador de formulario."); }
        } catch (IOException e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error Navegación", "No se pudo abrir formulario: " + e.getMessage()); }
    }

    @FXML void IrABandeja(MouseEvent event) { UtilidadesVentana.mostrarAlertaInformacion("Próximamente", "Bandeja de protectora no implementada."); }
    @FXML void IrAPerfilUsuario(MouseEvent event) {
        if (this.protectoraInfoActual == null || this.usuarioCuentaLogueada == null) {
            UtilidadesVentana.mostrarAlertaError("Error Datos", "No se puede mostrar perfil. Info no disponible.");
            return;
        }
        System.out.println("INFO: Navegando al perfil de la protectora: " + this.nombreProtectoraActual);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_FXML_PERFIL_PROTECTORA));
            Parent root = loader.load();
            PerfilProtectoraController perfilController = loader.getController();
            if (perfilController != null) {
                perfilController.initData(this.protectoraInfoActual, this.usuarioCuentaLogueada);
                UtilidadesVentana.cambiarEscenaConRoot(root, "Perfil de " + this.nombreProtectoraActual, false);
            } else { UtilidadesVentana.mostrarAlertaError("Error Interno", "No se pudo cargar el controlador del perfil de protectora."); }
        } catch (IOException e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error Navegación", "No se pudo abrir el perfil de la protectora: " + e.getMessage()); }
    }

    @FXML
    void RegistroAdopciones(ActionEvent event) {
        mostrandoRegistroCitas = !mostrandoRegistroCitas;
        actualizarContenidoSeccionRegistros();
    }

    private void actualizarContenidoSeccionRegistros() {
        if (mostrandoRegistroCitas) {
            if (lblRegistroTitulo != null) lblRegistroTitulo.setText("Registro de Citas Programadas");
            if (BtnToggleRegistro != null) BtnToggleRegistro.setText("Ver Adopciones");
            if (TablaRegistroCitas != null) { TablaRegistroCitas.setVisible(true); cargarDatosParaTablaCitas(); }
            if (TablaRegistroAdopciones != null) TablaRegistroAdopciones.setVisible(false);
        } else {
            if (lblRegistroTitulo != null) lblRegistroTitulo.setText("Registro de Adopciones");
            if (BtnToggleRegistro != null) BtnToggleRegistro.setText("Ver Citas");
            if (TablaRegistroCitas != null) TablaRegistroCitas.setVisible(false);
            if (TablaRegistroAdopciones != null) { TablaRegistroAdopciones.setVisible(true); cargarDatosParaTablaAdopciones(); }
        }
    }

    private void cargarDatosParaTablaCitas() {
        if (TablaRegistroCitas == null || reservaCitaDao == null || idProtectoraActual <= 0) {
            if (TablaRegistroCitas != null) TablaRegistroCitas.getItems().clear(); return;
        }
        try {
            // TODO: Implementar reservaCitaDao.obtenerInfoCitasPorProtectora(idProtectoraActual)
            List<RegistroCitaInfo> datosCitas = new ArrayList<>();
            ObservableList<RegistroCitaInfo> obsDatosCitas = FXCollections.observableArrayList(datosCitas);
            TablaRegistroCitas.setItems(obsDatosCitas);
        } catch (Exception e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error BD", "No se pudieron cargar citas."); }
    }

    private void cargarDatosParaTablaAdopciones() {
        if (TablaRegistroAdopciones == null || peticionAdopcionDao == null || idProtectoraActual <= 0) {
            if (TablaRegistroAdopciones != null) TablaRegistroAdopciones.getItems().clear(); return;
        }
        try {
            // TODO: Implementar peticionAdopcionDao.obtenerInfoAdopcionesPorProtectora(idProtectoraActual)
            List<RegistroAdopcionInfo> datosAdopciones = new ArrayList<>();
            ObservableList<RegistroAdopcionInfo> obsDatosAdopciones = FXCollections.observableArrayList(datosAdopciones);
            TablaRegistroAdopciones.setItems(obsDatosAdopciones);
        } catch (Exception e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error BD", "No se pudieron cargar adopciones.");}
    }

    @FXML public void Cancelar(ActionEvent event) { System.out.println("Botón Cancelar (general) presionado - Sin acción definida aquí.");}
    @FXML public void Aceptar(ActionEvent event) { System.out.println("Botón Aceptar (general) presionado - Sin acción definida aquí.");}
}