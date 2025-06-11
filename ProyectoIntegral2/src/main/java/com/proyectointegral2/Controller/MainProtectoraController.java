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
import javafx.scene.control.*;
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
    @FXML private TableColumn<RegistroCitaInfo, String> ColumAceptarCancelarCita;

    @FXML private TableView<RegistroAdopcionInfo> TablaRegistroAdopciones;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumNombrePerroAdopcion;
    @FXML private TableColumn<RegistroAdopcionInfo, LocalDate> ColumFechaAdopcion;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumNombreClienteAdopcion;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumNumeroDeContacto;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumEstadoAdopcion;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumAceptarCancelarAdopcion;

    // --- Constants ---
    private static final String RUTA_BASE_IMAGENES_PERROS_RESOURCES = "/assets/Imagenes/perros/";
    private static final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.jpg";
    private static final String RUTA_FXML_LOGIN = "/com/proyectointegral2/Vista/Login.fxml";
    private static final String RUTA_FXML_FORMULARIO_PERRO = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
    private static final String RUTA_FXML_PERFIL_PROTECTORA = "/com/proyectointegral2/Vista/PerfilProtectora.fxml";

    private static final double HEADER_HEIGHT_ESTIMADA = 70.0;
    private static final double TARJETA_IMG_AREA_WIDTH = 150.0;
    private static final double TARJETA_IMG_AREA_HEIGHT = 150.0;
    private static final double TARJETA_PREF_WIDTH = 180.0;
    private static final double CARD_HORIZONTAL_GAP = 15.0;
    private static final double CARD_INTERNAL_PADDING = 8.0;
    private static final double HBOX_TITULO_GRID_HEIGHT_ESTIMADA = 60.0;
    private static final double TABLAS_SECTION_CONTAINER_HEIGHT_ESTIMADA = 350.0;
    private static final double MIN_GRID_CONTAINER_HEIGHT = 250.0;


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
        if (TablaRegistroAdopciones != null) {
            ColumNombrePerroAdopcion.setCellValueFactory(new PropertyValueFactory<>("nombrePerro"));
            ColumFechaAdopcion.setCellValueFactory(new PropertyValueFactory<>("fechaPeticion"));
            ColumNombreClienteAdopcion.setCellValueFactory(new PropertyValueFactory<>("nombreAdoptante"));
            ColumEstadoAdopcion.setCellValueFactory(new PropertyValueFactory<>("estadoPeticion"));
            ColumNumeroDeContacto.setCellValueFactory(new PropertyValueFactory<>("numeroContacto"));
            TablaRegistroAdopciones.setPlaceholder(new Label("No hay registros de adopción para mostrar."));

            ColumEstadoAdopcion.setCellFactory(col -> new TableCell<RegistroAdopcionInfo, String>() {
                @Override
                protected void updateItem(String estado, boolean empty) {
                    super.updateItem(estado, empty);
                    setGraphic(null);
                    setText(empty || estado == null ? "" : estado);
                }
            });

            ColumAceptarCancelarAdopcion.setCellFactory(col -> new TableCell<RegistroAdopcionInfo, String>() {
                private final Button btnAceptar = new Button("Aceptar");
                private final Button btnCancelar = new Button("Cancelar");
                private final HBox hbox = new HBox(5, btnAceptar, btnCancelar);

                {
                    btnAceptar.setOnAction(event -> {
                        RegistroAdopcionInfo info = getTableView().getItems().get(getIndex());
                        if (peticionAdopcionDao.actualizarEstadoAdopcion(info.getIdPeticion(), "Aceptada")) {
                            if (perroDao != null) {
                                perroDao.actualizarCampoAdoptado(info.getIdPerro(), "S");
                            }
                            if (reservaCitaDao != null) {
                                try {
                                    reservaCitaDao.eliminarCitasPorPerroEstadosFijos(info.getIdPerro());
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (peticionAdopcionDao != null) {
                                try {
                                    peticionAdopcionDao.cancelarOtrasPeticionesDeAdopcion(info.getIdPerro(), info.getIdPeticion());
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            cargarDatosParaTablaAdopciones();
                            cargarYMostrarPerrosEnGrid();
                            cargarDatosParaTablaCitas();
                        }
                    });
                    btnCancelar.setOnAction(event -> {
                        RegistroAdopcionInfo info = getTableView().getItems().get(getIndex());
                        if (peticionAdopcionDao.actualizarEstadoAdopcion(info.getIdPeticion(), "Cancelada")) {
                            if (perroDao != null) {
                                perroDao.actualizarCampoAdoptado(info.getIdPerro(), "N");
                            }
                            cargarDatosParaTablaAdopciones();
                            cargarYMostrarPerrosEnGrid();
                        }
                    });
                }

                @Override
                protected void updateItem(String estado, boolean empty) {
                    super.updateItem(estado, empty);
                    RegistroAdopcionInfo info = empty ? null : getTableView().getItems().get(getIndex());
                    if (empty || info == null || info.getEstadoPeticion() == null) {
                        setGraphic(null);
                    } else if (!"Completada".equalsIgnoreCase(info.getEstadoPeticion())
                            && !"Aceptada".equalsIgnoreCase(info.getEstadoPeticion())) {
                        setGraphic(hbox);
                    } else {
                        setGraphic(null);
                    }
                }
            });
        }

        if (TablaRegistroCitas != null) {
            ColumNombrePerro.setCellValueFactory(new PropertyValueFactory<>("nombrePerro"));
            ColumDiaCita.setCellValueFactory(new PropertyValueFactory<>("fechaCita"));
            ColumHoraCita.setCellValueFactory(new PropertyValueFactory<>("horaCita"));
            ColumNombreClienteCita.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
            ColumEstadoCita.setCellValueFactory(new PropertyValueFactory<>("estadoCita"));
            TablaRegistroCitas.setPlaceholder(new Label("No hay citas programadas para mostrar."));

            ColumEstadoCita.setCellFactory(col -> new TableCell<RegistroCitaInfo, String>() {
                @Override
                protected void updateItem(String estado, boolean empty) {
                    super.updateItem(estado, empty);
                    setGraphic(null);
                    setText(empty || estado == null ? "" : estado);
                }
            });

            ColumAceptarCancelarCita.setCellFactory(col -> new TableCell<RegistroCitaInfo, String>() {
                private final Button btnAceptar = new Button("Aceptar");
                private final Button btnCancelar = new Button("Cancelar");
                private final HBox hbox = new HBox(5, btnAceptar, btnCancelar);

                {
                    btnAceptar.setOnAction(event -> {
                        RegistroCitaInfo info = getTableView().getItems().get(getIndex());
                        if (reservaCitaDao.actualizarEstadoCita(info.getIdReservaCita(), "Confirmada")) {
                            cargarDatosParaTablaCitas();
                        }
                    });
                    btnCancelar.setOnAction(event -> {
                        RegistroCitaInfo info = getTableView().getItems().get(getIndex());
                        if (reservaCitaDao.actualizarEstadoCita(info.getIdReservaCita(), "Cancelada")) {
                            cargarDatosParaTablaCitas();
                        }
                    });
                }

                @Override
                protected void updateItem(String estado, boolean empty) {
                    super.updateItem(estado, empty);
                    RegistroCitaInfo info = empty ? null : getTableView().getItems().get(getIndex());
                    if (empty || info == null || info.getEstadoCita() == null) {
                        setGraphic(null);
                    } else if (!"Completada".equalsIgnoreCase(info.getEstadoCita())
                            && !"Aceptada".equalsIgnoreCase(info.getEstadoCita())) {
                        setGraphic(hbox);
                    } else {
                        setGraphic(null);
                    }
                }
            });
        }
    }

    private void configurarVisibilidadInicialDeTablas() {
        mostrandoRegistroCitas = true;
        actualizarContenidoSeccionRegistros();
    }

    private void cargarYMostrarPerrosEnGrid() {
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

        popularGridConPerrosDeProtectora();
        actualizarVisibilidadDelLabelNoPerros();
    }

    private void handlePerroDaoError(String errorMessage) {
        System.err.println(errorMessage);
        UtilidadesVentana.mostrarAlertaError("Error de Datos (Perros)", errorMessage);
        if (dogGrid != null) dogGrid.getChildren().clear();
        actualizarVisibilidadDelLabelNoPerros();
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
            return 3;
        }

        double hgap = (dogGrid != null && dogGrid.getHgap() > 0 && !Double.isNaN(dogGrid.getHgap())) ?
                dogGrid.getHgap() : CARD_HORIZONTAL_GAP;

        double anchoEstimadoTarjetaConEspacio = TARJETA_PREF_WIDTH + hgap;

        if (anchoEstimadoTarjetaConEspacio <= 0) {
            System.err.println("ERROR: Ancho estimado de tarjeta con espacio es inválido (" + anchoEstimadoTarjetaConEspacio + "). Usando 3 columnas.");
            return 3;
        }

        int numColumnasCalculadas = Math.max(1, (int) Math.floor(anchoGridDisponible / anchoEstimadoTarjetaConEspacio));
        int maxColumnasPermitidas = 6;
        int columnasFinales = Math.min(numColumnasCalculadas, maxColumnasPermitidas);

        System.out.println("Ancho grid disponible: " + anchoGridDisponible + ", Ancho Tarjeta+Gap: " + anchoEstimadoTarjetaConEspacio + ", Col. Calculadas: " + numColumnasCalculadas + ", Col. Finales: " + columnasFinales);
        return columnasFinales;
    }


    private void adaptarAnchoDelGridYRepopular(double anchoVentana) {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null || mainBorderPane == null) return;

        // Calcular el ancho disponible real para el contenido del GridPane
        Node centerNode = mainBorderPane.getCenter();
        double anchoDisponibleParaContenidoGrid = anchoVentana; // Valor inicial
        if (centerNode instanceof ScrollPane) {
            ScrollPane scrollPane = (ScrollPane) centerNode;
            // Restar paddings del ScrollPane y su contenido VBox si los tiene
            Insets scrollPanePadding = scrollPane.getPadding();
            anchoDisponibleParaContenidoGrid -= (scrollPanePadding.getLeft() + scrollPanePadding.getRight());
            if (scrollPane.getContent() instanceof VBox) {
                VBox vBoxPrincipal = (VBox) scrollPane.getContent();
                Insets vBoxPadding = vBoxPrincipal.getPadding();
                anchoDisponibleParaContenidoGrid -= (vBoxPadding.getLeft() + vBoxPadding.getRight());

                if (vBoxPrincipal.getChildren().size() > 0 && vBoxPrincipal.getChildren().get(0) instanceof VBox) {
                    VBox vBoxGridContainer = (VBox) vBoxPrincipal.getChildren().get(0);
                    Insets gridContainerPadding = vBoxGridContainer.getPadding();
                    anchoDisponibleParaContenidoGrid -= (gridContainerPadding.getLeft() + gridContainerPadding.getRight());
                }
            }
        }
        // Restar padding del propio GridPane
        double paddingLateralesGridPane = (dogGrid.getPadding() != null) ? dogGrid.getPadding().getLeft() + dogGrid.getPadding().getRight() : 0;
        anchoDisponibleParaContenidoGrid -= paddingLateralesGridPane;
        anchoDisponibleParaContenidoGrid -= 20; // Espacio estimado para la barra de scroll vertical si aparece

        System.out.println("Ancho Ventana: " + anchoVentana + ", Ancho Disponible para Contenido Grid: " + anchoDisponibleParaContenidoGrid);


        int nuevasColumnas = calcularNumeroDeColumnasSegunAncho(anchoDisponibleParaContenidoGrid);

        // Si el ancho disponible es muy pequeño para una sola tarjeta + gaps, forzar 1 columna
        // y el ScrollPane horizontal deberá actuar.
        double minWidthParaUnaTarjeta = TARJETA_PREF_WIDTH + (dogGrid.getHgap() > 0 ? dogGrid.getHgap() : CARD_HORIZONTAL_GAP);
        if (anchoDisponibleParaContenidoGrid < minWidthParaUnaTarjeta) {
            nuevasColumnas = 1;
            System.out.println("Ancho muy pequeño, forzando 1 columna.");
        }


        boolean necesitaCambioDeColumnas = dogGrid.getColumnConstraints().size() != nuevasColumnas;
        boolean gridVacioPeroHayDatos = dogGrid.getChildren().isEmpty() && !listaDePerrosDeLaProtectora.isEmpty();
        Integer hashListaGuardado = (dogGrid.getUserData() instanceof Integer) ? (Integer) dogGrid.getUserData() : null;
        boolean listaDePerrosHaCambiado = hashListaGuardado == null || !Objects.equals(hashListaGuardado, listaDePerrosDeLaProtectora.hashCode());

        if (necesitaCambioDeColumnas || gridVacioPeroHayDatos || listaDePerrosHaCambiado) {
            dogGrid.getColumnConstraints().clear();
            if (anchoDisponibleParaContenidoGrid >= minWidthParaUnaTarjeta) { // Solo usar percentWidth si cabe al menos una
                for (int i = 0; i < nuevasColumnas; i++) {
                    ColumnConstraints colConst = new ColumnConstraints();
                    colConst.setPercentWidth(100.0 / nuevasColumnas);
                    colConst.setHgrow(Priority.SOMETIMES);
                    dogGrid.getColumnConstraints().add(colConst);
                }
                System.out.println("Columnas reconfiguradas a (percentWidth): " + nuevasColumnas);
            } else { // Si no cabe ni una, no usar percentWidth, dejar que el GridPane determine su ancho
                ColumnConstraints colConst = new ColumnConstraints();
                colConst.setHgrow(Priority.NEVER); // No permitir que crezca más allá de su contenido
                colConst.setMinWidth(Region.USE_PREF_SIZE);
                dogGrid.getColumnConstraints().add(colConst); // Solo una columna
                System.out.println("Columnas reconfiguradas a 1 (pref size) debido a ancho insuficiente.");
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
        if (dogGrid == null || listaDePerrosDeLaProtectora == null) { /* ... */ return; }
        dogGrid.getChildren().clear();

        // Asegurarse de que haya al menos una ColumnConstraint si el grid es visible y tiene perros
        if (dogGrid.getColumnConstraints().isEmpty() && !listaDePerrosDeLaProtectora.isEmpty()) {
            System.out.println("WARN: popularGrid llamado sin ColumnConstraints. Añadiendo una por defecto.");
            ColumnConstraints colConst = new ColumnConstraints();
            // Si el ancho es muy pequeño, no usar percentWidth, sino dejar que el contenido determine
            // colConst.setPercentWidth(100.0);
            colConst.setHgrow(Priority.SOMETIMES); // O NEVER si quieres que se ajuste al contenido de la tarjeta
            colConst.setMinWidth(TARJETA_PREF_WIDTH); // Darle un mínimo
            dogGrid.getColumnConstraints().add(colConst);
        }

        int numColumnas = Math.max(1, dogGrid.getColumnConstraints().size());
        actualizarVisibilidadDelLabelNoPerros();
        if (listaDePerrosDeLaProtectora.isEmpty()) return;

        System.out.println("Populando dogGrid Protectora con " + listaDePerrosDeLaProtectora.size() + " perros en " + numColumnas + " columnas.");
        int columna = 0; int fila = 0;
        for (Perro perro : listaDePerrosDeLaProtectora) {
            VBox tarjetaPerro = crearTarjetaPerroParaProtectora(perro);
            dogGrid.add(tarjetaPerro, columna, fila);
            columna++;
            if (columna >= numColumnas) { columna = 0; fila++; }
        }
    }

    private VBox crearTarjetaPerroParaProtectora(Perro perro) {
        VBox card = new VBox(5); // Espaciado vertical interno de la tarjeta
        card.setPrefWidth(TARJETA_PREF_WIDTH);
        card.setMaxWidth(TARJETA_PREF_WIDTH); // Importante para que no se estire más de lo debido
        card.setMinWidth(Region.USE_PREF_SIZE); // Permitir que se encoja si es necesario
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(String.format(
                "-fx-background-color: white; -fx-border-color: #E0E0E0; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 5, 0, 0, 2); -fx-padding: %f;",
                CARD_INTERNAL_PADDING
        ));
        card.setMinHeight(250); // Altura mínima de la tarjeta, ajustar según contenido

        // Contenedor para la imagen
        StackPane imageContainer = new StackPane();
        // Usar las constantes para el área de la imagen
        imageContainer.setPrefSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);
        imageContainer.setMinSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT); // Para que no se encoja demasiado
        imageContainer.setMaxSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT); // Para que no se agrande demasiado
        // imageContainer.setStyle("-fx-background-color: lightblue;"); // Para depurar

        ImageView imgView = new ImageView();
        cargarImagenPerroEnTarjeta(imgView, perro.getFoto()); // Tu método de carga

        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        imgView.setFitWidth(TARJETA_IMG_AREA_WIDTH);   // Ajustar al ancho del contenedor
        imgView.setFitHeight(TARJETA_IMG_AREA_HEIGHT); // Ajustar a la altura del contenedor
        StackPane.setAlignment(imgView, Pos.CENTER); // Centrar la imagen en el StackPane
        imageContainer.getChildren().add(imgView);
        VBox.setMargin(imageContainer, new Insets(0, 0, 5, 0)); // Menos margen inferior

        // Nombre del perro
        Label lblNombrePerro = new Label(Objects.requireNonNullElse(perro.getNombre(), "Sin Nombre"));
        lblNombrePerro.setStyle("-fx-background-color: #A9D18E; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 4px 8px; -fx-background-radius: 3;");
        lblNombrePerro.setAlignment(Pos.CENTER);
        lblNombrePerro.setPrefWidth(TARJETA_IMG_AREA_WIDTH); // Que el label no exceda el ancho de la imagen
        lblNombrePerro.setMaxWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setTextOverrun(OverrunStyle.ELLIPSIS);
        VBox.setMargin(lblNombrePerro, new Insets(0, 0, 3, 0));


        // Estado del perro
        String estadoPerro = perro.isAdoptado() ? "Adoptado" : "En Adopción"; // Simplificado
        Label lblEstado = new Label(estadoPerro);
        String styleEstado = "-fx-font-size: 11px; ";
        if ("Adoptado".equals(estadoPerro)) styleEstado += "-fx-text-fill: grey;";
        else styleEstado += "-fx-text-fill: green; -fx-font-weight:bold;";
        lblEstado.setStyle(styleEstado);
        VBox.setMargin(lblEstado, new Insets(0, 0, 5, 0));

        // Botones de acción en un HBox
        HBox botonesTarjeta = new HBox(5); // Reducir espaciado entre botones
        botonesTarjeta.setAlignment(Pos.CENTER); // Centrar HBox de botones
        // HBox.setHgrow(botonesTarjeta, Priority.ALWAYS); // Permitir que el HBox se expanda si es necesario

        Button btnEditar = new Button("Editar");
        // Reducir padding y tamaño de fuente de botones para que quepan mejor
        btnEditar.setStyle("-fx-background-color: transparent; -fx-border-color: #3498DB; -fx-text-fill: #3498DB; -fx-border-radius: 3; -fx-font-size:10px; -fx-padding: 2px 6px;");
        btnEditar.setOnAction(event -> handleEditarPerro(perro));
        // HBox.setHgrow(btnEditar, Priority.ALWAYS); // Hacer que los botones se expandan equitativamente

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-border-color: #E74C3C; -fx-text-fill: #E74C3C; -fx-border-radius: 3; -fx-font-size:10px; -fx-padding: 2px 6px;");
        btnEliminar.setOnAction(event -> handleEliminarPerro(perro));
        // HBox.setHgrow(btnEliminar, Priority.ALWAYS);

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
            if (TablaRegistroCitas != null) {
                TablaRegistroCitas.setVisible(true);
                TablaRegistroCitas.toFront();
                cargarDatosParaTablaCitas();
            }
            if (TablaRegistroAdopciones != null) {
                TablaRegistroAdopciones.setVisible(false);
                TablaRegistroAdopciones.getItems().clear();
            }
        } else {
            if (lblRegistroTitulo != null) lblRegistroTitulo.setText("Registro de Adopciones");
            if (BtnToggleRegistro != null) BtnToggleRegistro.setText("Ver Citas");
            if (TablaRegistroCitas != null) {
                TablaRegistroCitas.setVisible(false);
                TablaRegistroCitas.getItems().clear();
            }
            if (TablaRegistroAdopciones != null) {
                TablaRegistroAdopciones.setVisible(true);
                TablaRegistroAdopciones.toFront();
                cargarDatosParaTablaAdopciones();
            }
        }
    }

    private void cargarDatosParaTablaCitas() {
        if (TablaRegistroCitas == null || reservaCitaDao == null || idProtectoraActual <= 0) {
            if (TablaRegistroCitas != null) TablaRegistroCitas.getItems().clear();
            return;
        }
        try {
            List<RegistroCitaInfo> datosCitas = reservaCitaDao.obtenerInfoCitasPorProtectora(idProtectoraActual);
            LocalDate hoy = LocalDate.now();
            for (RegistroCitaInfo info : datosCitas) {
                if (("Confirmada").equalsIgnoreCase(info.getEstadoCita())
                    && info.getFechaCita() != null
                    && info.getFechaCita().isBefore(hoy)) {
                    reservaCitaDao.actualizarEstadoCita(info.getIdReservaCita(), "Completada");
                    info.setEstadoCita("Completada");
                }
            }
            List<RegistroCitaInfo> filtradas = datosCitas.stream()
                .filter(info ->
                    !("Completada".equalsIgnoreCase(info.getEstadoCita()) &&
                      info.getFechaCita() != null &&
                      info.getFechaCita().isBefore(hoy.minusDays(14)))
                )
                .collect(Collectors.toList());

            ObservableList<RegistroCitaInfo> obsDatosCitas = FXCollections.observableArrayList(filtradas);
            TablaRegistroCitas.setItems(obsDatosCitas);
        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error BD", "No se pudieron cargar citas.");
        }
    }

    private void cargarDatosParaTablaAdopciones() {
        if (TablaRegistroAdopciones == null || peticionAdopcionDao == null || idProtectoraActual <= 0) {
            if (TablaRegistroAdopciones != null) TablaRegistroAdopciones.getItems().clear();
            return;
        }
        try {
            System.out.println("ID protectora usado: " + idProtectoraActual);
            List<RegistroAdopcionInfo> datosAdopciones = peticionAdopcionDao.obtenerInfoAdopcionesPorProtectora(idProtectoraActual);
            LocalDate hoy = LocalDate.now();
            for (RegistroAdopcionInfo info : datosAdopciones) {
                if ("Pendiente".equalsIgnoreCase(info.getEstadoPeticion())
                        && info.getFechaPeticion() != null
                        && info.getFechaPeticion().plusDays(14).isBefore(hoy)) {
                    peticionAdopcionDao.actualizarEstadoAdopcion(info.getIdPeticion(), "Cancelada");
                    info.setEstadoPeticion("Cancelada");
                    if (perroDao != null) {
                        perroDao.actualizarCampoAdoptado(info.getIdPerro(), "N");
                    }
                }
            }
            List<RegistroAdopcionInfo> filtradas = datosAdopciones.stream()
                    .filter(info ->
                            !("Aceptada".equalsIgnoreCase(info.getEstadoPeticion()) &&
                                    info.getFechaPeticion() != null &&
                                    info.getFechaPeticion().isBefore(hoy.minusDays(14)))
                    )
                    .collect(Collectors.toList());

            ObservableList<RegistroAdopcionInfo> obsDatosAdopciones = FXCollections.observableArrayList(filtradas);
            TablaRegistroAdopciones.setItems(obsDatosAdopciones);

        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error BD", "No se pudieron cargar adopciones.");
        }
    }

}