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
    private static final String RUTA_BASE_IMAGENES_PERROS_RESOURCES = "/assets/Imagenes/perros";
    private static final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.jpg";
    private static final String RUTA_FXML_LOGIN = "/com/proyectointegral2/Vista/Login.fxml";
    private static final String RUTA_FXML_FORMULARIO_PERRO = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
    private static final String RUTA_FXML_PERFIL_PROTECTORA = "/com/proyectointegral2/Vista/PerfilProtectora.fxml";

    private static final double HEADER_HEIGHT_ESTIMADA = 70.0;
    private static final double TARJETA_IMG_AREA_WIDTH = 160.0;
    private static final double TARJETA_IMG_AREA_HEIGHT = 160.0;
    private static final double TARJETA_PREF_WIDTH = 190.0;
    private static final double CARD_HORIZONTAL_GAP = 20.0;
    private static final double CARD_INTERNAL_PADDING = 10.0;
    private static final double HBOX_TITULO_GRID_HEIGHT_ESTIMADA = 60.0;
    private static final double TABLAS_SECTION_CONTAINER_HEIGHT_ESTIMADA = 350.0;
    private static final double MIN_GRID_CONTAINER_HEIGHT = 250.0;
    private static final double PREFERRED_GRID_WIDTH_FALLBACK = 1000.0;


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
        System.out.println("Intentando cargar placeholder: " + RUTA_IMAGEN_PLACEHOLDER_PERRO);
        try (InputStream testStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO)) {
            if (testStream != null) {
                System.out.println("Placeholder ENCONTRADO!");
            } else {
                System.err.println("Placeholder NO ENCONTRADO en classpath con la ruta: " + RUTA_IMAGEN_PLACEHOLDER_PERRO);
            }
        } catch (Exception e) {
            System.err.println("Excepción al intentar cargar placeholder: " + e.getMessage());
        }
        System.out.println("MainProtectoraController inicializado. Validando sesión...");
        try {
            this.perroDao = new PerroDao();
            this.protectoraDao = new ProtectoraDao();
            this.peticionAdopcionDao = new PeticionAdopcionDao();
            this.reservaCitaDao = new ReservaCitaDao();
        } catch (Exception e) {
            handleDaoInitializationError(e);
            return;
        }

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
        UtilidadesVentana.mostrarAlertaError("Error Crítico de Sistema", "No se pudo inicializar el acceso a datos. La aplicación no funcionará correctamente.");
        if (BtnNuevoPerro != null) BtnNuevoPerro.setDisable(true);
    }

    private boolean validarYConfigurarSesionProtectora() {
        this.usuarioCuentaLogueada = SesionUsuario.getUsuarioLogueado();
        this.idProtectoraActual = SesionUsuario.getEntidadIdEspecifica();

        if (this.usuarioCuentaLogueada == null || this.idProtectoraActual <= 0 ||
                !"PROTECTORA".equalsIgnoreCase(this.usuarioCuentaLogueada.getRol())) {
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sesión", "No hay información de protectora válida. Será redirigido al inicio de sesión.");
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
                throw new SQLException("No se encontró información para la protectora con ID: " + this.idProtectoraActual);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Carga de Datos", "No se pudo cargar la información de la protectora: " + e.getMessage() + "\nSerá redirigido.");
            Platform.runLater(() -> UtilidadesVentana.cambiarEscena(RUTA_FXML_LOGIN, "Login", false));
            return false;
        }
    }

    private void configurarColumnasDeTablas() {
        if (TablaRegistroCitas != null) {
            ColumNombrePerro.setCellValueFactory(new PropertyValueFactory<>("nombrePerro"));
            ColumDiaCita.setCellValueFactory(new PropertyValueFactory<>("fechaCita"));
            ColumHoraCita.setCellValueFactory(new PropertyValueFactory<>("horaCita"));
            ColumNombreClienteCita.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
            ColumEstadoCita.setCellValueFactory(new PropertyValueFactory<>("estadoCita"));
            TablaRegistroCitas.setPlaceholder(new Label("No hay citas programadas para mostrar."));
        }
        if (TablaRegistroAdopciones != null) {
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
            handlePerroDaoError("PerroDao no inicializado o ID Protectora inválido.");
            return;
        }
        try {
            this.listaDePerrosDeLaProtectora = perroDao.obtenerPerrosPorProtectora(this.idProtectoraActual);
            if (this.listaDePerrosDeLaProtectora == null) {
                this.listaDePerrosDeLaProtectora = new ArrayList<>();
            }
        } catch (SQLException e) {
            handlePerroDaoError("No se pudieron cargar los perros de la protectora: " + e.getMessage());
        }
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
            if (listaDePerrosDeLaProtectora != null && !listaDePerrosDeLaProtectora.isEmpty()) {
                popularGridConPerrosDeProtectora();
            }
        }
    }

    private void actualizarVisibilidadDelLabelNoPerros() {
        if (lblNoPerrosEnGrid != null && dogGrid != null) {
            boolean hayPerros = this.listaDePerrosDeLaProtectora != null && !this.listaDePerrosDeLaProtectora.isEmpty();
            lblNoPerrosEnGrid.setVisible(!hayPerros);
            dogGrid.setVisible(hayPerros);
            if (!hayPerros) {
                System.out.println("No hay perros para mostrar para la protectora ID: " + idProtectoraActual);
            }
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

                if (stage.isShowing()) {
                    adaptarUI.run();
                } else {
                    stage.setOnShown(event -> adaptarUI.run());
                }
                stage.widthProperty().addListener((obs, oldVal, newVal) -> adaptarUI.run());
                stage.heightProperty().addListener((obs, oldVal, newVal) -> adaptarUI.run());
                stage.maximizedProperty().addListener((obs, oldVal, newVal) -> adaptarUI.run());
            }
        };
        if (mainBorderPane.getScene() != null) {
            if (mainBorderPane.getScene().getWindow() != null) {
                setupListeners.run();
            } else {
                mainBorderPane.getScene().windowProperty().addListener((obs, oldW, newW) -> {
                    if (newW != null) setupListeners.run();
                });
            }
        } else {
            mainBorderPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
                if (newScene != null) {
                    if (newScene.getWindow() != null) {
                        setupListeners.run();
                    } else {
                        newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                            if (newWindow != null) setupListeners.run();
                        });
                    }
                }
            });
        }
    }

    private void adaptarUIAlTamanoDeVentana(Stage stage) {
        if (stage == null || stage.getWidth() <= 0 || Double.isNaN(stage.getWidth()) || dogGrid == null) {
            return;
        }
        adaptarAnchoDelGridYRepopular(stage.getWidth());
        adaptarAlturaDelContenedorDelGrid(stage.getHeight());
    }

    private int calcularNumeroDeColumnasSegunAncho(double anchoGridDisponible) {
        if (anchoGridDisponible <= 0 || Double.isNaN(anchoGridDisponible)) {
            System.out.println("WARN: Ancho de grid disponible inválido (" + anchoGridDisponible + ") para calcular columnas, usando 3 por defecto.");
            return 3; // Un valor por defecto razonable si el ancho no es válido
        }

        // Obtener el hgap del GridPane si está disponible, sino usar una constante
        double hgap = (dogGrid != null && dogGrid.getHgap() > 0 && !Double.isNaN(dogGrid.getHgap())) ?
                dogGrid.getHgap() : CARD_HORIZONTAL_GAP;

        // Ancho estimado de una tarjeta (TARJETA_PREF_WIDTH) más su espaciado horizontal (hgap)
        double anchoEstimadoTarjetaConEspacio = TARJETA_PREF_WIDTH + hgap;

        if (anchoEstimadoTarjetaConEspacio <= 0) { // Evitar división por cero o negativo
            System.err.println("ERROR: Ancho estimado de tarjeta con espacio es inválido (" + anchoEstimadoTarjetaConEspacio + "). Usando 3 columnas.");
            return 3;
        }

        int numColumnasCalculadas = Math.max(1, (int) Math.floor(anchoGridDisponible / anchoEstimadoTarjetaConEspacio));

        // Limitar el número máximo de columnas (puedes ajustar este límite)
        int maxColumnas = 8; // Puedes hacerlo una constante de clase si prefieres
        int columnasFinales = Math.min(numColumnasCalculadas, maxColumnas);

        // System.out.println("Ancho disponible para grid: " + anchoGridDisponible +
        //                    ", Ancho Tarjeta+Gap Est.: " + anchoEstimadoTarjetaConEspacio +
        //                    ", Columnas calculadas: " + numColumnasCalculadas +
        //                    ", Columnas finales: " + columnasFinales);
        return columnasFinales;
    }


    private void adaptarAnchoDelGridYRepopular(double anchoVentana) {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null || mainBorderPane == null) return;
        double paddingLateralesVBoxContenedor = 0;
        if (mainBorderPane.getCenter() instanceof ScrollPane) {
            ScrollPane scrollPane = (ScrollPane) mainBorderPane.getCenter();
            if (scrollPane.getContent() instanceof VBox) {
                VBox vBoxPrincipal = (VBox) scrollPane.getContent();
                paddingLateralesVBoxContenedor = vBoxPrincipal.getPadding().getLeft() + vBoxPrincipal.getPadding().getRight();
            }
        }
        double paddingLateralesGridPane = (dogGrid.getPadding() != null) ? dogGrid.getPadding().getLeft() + dogGrid.getPadding().getRight() : 0;
        double espacioConsideradoParaScrollbar = 20.0;
        double anchoDisponibleParaContenidoGrid = anchoVentana - paddingLateralesVBoxContenedor - paddingLateralesGridPane - espacioConsideradoParaScrollbar;
        int nuevasColumnas = calcularNumeroDeColumnasSegunAncho(anchoDisponibleParaContenidoGrid);
        boolean necesitaCambioDeColumnas = dogGrid.getColumnConstraints().size() != nuevasColumnas;
        boolean gridVacioPeroHayDatos = dogGrid.getChildren().isEmpty() && !listaDePerrosDeLaProtectora.isEmpty();
        Integer hashListaGuardado = (dogGrid.getUserData() instanceof Integer) ? (Integer) dogGrid.getUserData() : null;
        boolean listaDePerrosHaCambiado = hashListaGuardado == null || !Objects.equals(hashListaGuardado, listaDePerrosDeLaProtectora.hashCode());

        if (necesitaCambioDeColumnas || gridVacioPeroHayDatos || listaDePerrosHaCambiado) {
            if (necesitaCambioDeColumnas) {
                dogGrid.getColumnConstraints().clear();
                for (int i = 0; i < nuevasColumnas; i++) {
                    ColumnConstraints colConst = new ColumnConstraints();
                    colConst.setPercentWidth(100.0 / nuevasColumnas);
                    colConst.setHgrow(Priority.SOMETIMES);
                    dogGrid.getColumnConstraints().add(colConst);
                }
            }
            popularGridConPerrosDeProtectora();
            dogGrid.setUserData(listaDePerrosDeLaProtectora.hashCode());
        }
    }

    private void adaptarAlturaDelContenedorDelGrid(double alturaVentana) {
        if (mainBorderPane.getCenter() instanceof ScrollPane) {
            ScrollPane mainScrollPane = (ScrollPane) mainBorderPane.getCenter();
            if (mainScrollPane.getContent() instanceof VBox) {
                VBox vBoxPrincipal = (VBox) mainScrollPane.getContent();
                if (!vBoxPrincipal.getChildren().isEmpty() && vBoxPrincipal.getChildren().get(0) instanceof VBox) {
                    VBox vBoxContenedorDelGrid = (VBox) vBoxPrincipal.getChildren().get(0);
                    Node topNode = mainBorderPane.getTop();
                    double topHeight = (topNode != null) ? topNode.getLayoutBounds().getHeight() : HEADER_HEIGHT_ESTIMADA;
                    double alturaSeccionTablas = 0;
                    if (vBoxPrincipal.getChildren().size() > 1 && vBoxPrincipal.getChildren().get(1) instanceof VBox) {
                        VBox vBoxSeccionTablas = (VBox) vBoxPrincipal.getChildren().get(1);
                        alturaSeccionTablas = vBoxSeccionTablas.getPrefHeight() > 0 && vBoxSeccionTablas.getPrefHeight() != Region.USE_COMPUTED_SIZE ?
                                vBoxSeccionTablas.getPrefHeight() : TABLAS_SECTION_CONTAINER_HEIGHT_ESTIMADA;
                        alturaSeccionTablas += vBoxSeccionTablas.getPadding().getTop() + vBoxSeccionTablas.getPadding().getBottom();
                    }
                    double paddingVerticalVBoxPrincipal = vBoxPrincipal.getPadding().getTop() + vBoxPrincipal.getPadding().getBottom();
                    double spacingVBoxPrincipal = vBoxPrincipal.getSpacing();
                    double alturaDisponibleParaVBoxGrid = alturaVentana - topHeight - alturaSeccionTablas - paddingVerticalVBoxPrincipal - spacingVBoxPrincipal;
                    vBoxContenedorDelGrid.setPrefHeight(Math.max(MIN_GRID_CONTAINER_HEIGHT + HBOX_TITULO_GRID_HEIGHT_ESTIMADA, alturaDisponibleParaVBoxGrid));
                }
            }
        }
    }

    private void popularGridConPerrosDeProtectora() {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null) {
            actualizarVisibilidadDelLabelNoPerros();
            return;
        }
        dogGrid.getChildren().clear();
        int numColumnas = Math.max(1, dogGrid.getColumnConstraints().size());
        if (dogGrid.getColumnConstraints().isEmpty()){
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0);
            dogGrid.getColumnConstraints().add(colConst);
            numColumnas = 1;
        }
        int columna = 0; int fila = 0;
        for (Perro perro : listaDePerrosDeLaProtectora) {
            VBox tarjetaPerro = crearTarjetaPerroParaProtectora(perro);
            dogGrid.add(tarjetaPerro, columna, fila);
            columna++;
            if (columna >= numColumnas) { columna = 0; fila++; }
        }
        actualizarVisibilidadDelLabelNoPerros();
    }

    private VBox crearTarjetaPerroParaProtectora(Perro perro) {
        VBox card = new VBox(5);
        card.setPrefWidth(TARJETA_PREF_WIDTH);
        card.setMaxWidth(TARJETA_PREF_WIDTH);
        card.setMinWidth(TARJETA_PREF_WIDTH);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(String.format(
                "-fx-background-color: white; -fx-border-color: #E0E0E0; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-padding: %f;",
                CARD_INTERNAL_PADDING
        ));
        card.setMinHeight(260);

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
        imageContainer.getChildren().add(imgView);
        VBox.setMargin(imageContainer, new Insets(0, 0, 8, 0));

        Label lblNombrePerro = new Label(Objects.requireNonNullElse(perro.getNombre(), "Sin Nombre"));
        lblNombrePerro.setStyle("-fx-background-color: #A9D18E; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 5; -fx-background-radius: 3;");
        lblNombrePerro.setAlignment(Pos.CENTER);
        lblNombrePerro.setPrefWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setMaxWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);

        String estadoPerro = "N/D";
        if (perro.getAdoptado() != null) {
            estadoPerro = perro.isAdoptado() ? "Adoptado" : "En Adopción";
        }
        Label lblEstado = new Label(estadoPerro);
        String styleEstado = "-fx-font-size: 12px; ";
        if ("Adoptado".equals(estadoPerro)) styleEstado += "-fx-text-fill: grey;";
        else styleEstado += "-fx-text-fill: green; -fx-font-weight:bold;";
        lblEstado.setStyle(styleEstado);
        VBox.setMargin(lblEstado, new Insets(4, 0, 4, 0));

        HBox botonesTarjeta = new HBox(10);
        botonesTarjeta.setAlignment(Pos.CENTER);
        Button btnEditar = new Button("Editar");
        btnEditar.setStyle("-fx-background-color: transparent; -fx-border-color: #3498DB; -fx-text-fill: #3498DB; -fx-border-radius: 3; -fx-font-size:11px; -fx-padding: 3 8;");
        btnEditar.setOnAction(event -> handleEditarPerro(perro));
        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-border-color: #E74C3C; -fx-text-fill: #E74C3C; -fx-border-radius: 3; -fx-font-size:11px; -fx-padding: 3 8;");
        btnEliminar.setOnAction(event -> handleEliminarPerro(perro));
        botonesTarjeta.getChildren().addAll(btnEditar, btnEliminar);

        card.getChildren().addAll(imageContainer, lblNombrePerro, lblEstado, botonesTarjeta);
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
            UtilidadesVentana.mostrarAlertaError("Error Datos", "No se puede mostrar perfil. Info de protectora o cuenta no disponible.");
            return;
        }
        System.out.println("INFO: Navegando al perfil de la protectora: " + this.nombreProtectoraActual);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_FXML_PERFIL_PROTECTORA));
            Parent root = loader.load();
            PerfilProtectoraController perfilController = loader.getController(); // Asume que existe
            if (perfilController != null) {
                perfilController.initData(this.protectoraInfoActual, this.usuarioCuentaLogueada); // Asume este initData
                UtilidadesVentana.cambiarEscenaConRoot(root, "Perfil de " + this.nombreProtectoraActual, false); // Perfil es fijo
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
            // List<RegistroCitaInfo> datosCitas = reservaCitaDao.obtenerInfoCitasPorProtectora(idProtectoraActual);
            List<RegistroCitaInfo> datosCitas = new ArrayList<>(); // Simulación
            ObservableList<RegistroCitaInfo> obsDatosCitas = FXCollections.observableArrayList(datosCitas);
            TablaRegistroCitas.setItems(obsDatosCitas);
        } catch (Exception e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error BD", "No se pudieron cargar citas."); }
    }

    private void cargarDatosParaTablaAdopciones() {
        if (TablaRegistroAdopciones == null || peticionAdopcionDao == null || idProtectoraActual <= 0) {
            if (TablaRegistroAdopciones != null) TablaRegistroAdopciones.getItems().clear(); return;
        }
        try {
            // List<RegistroAdopcionInfo> datosAdopciones = peticionAdopcionDao.obtenerInfoAdopcionesPorProtectora(idProtectoraActual);
            List<RegistroAdopcionInfo> datosAdopciones = new ArrayList<>(); // Simulación
            ObservableList<RegistroAdopcionInfo> obsDatosAdopciones = FXCollections.observableArrayList(datosAdopciones);
            TablaRegistroAdopciones.setItems(obsDatosAdopciones);
        } catch (Exception e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error BD", "No se pudieron cargar adopciones.");}
    }

    @FXML
    public void Cancelar(ActionEvent event) {

    }
    @FXML
    public void Aceptar(ActionEvent event) {

    }
}