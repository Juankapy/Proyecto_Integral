package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
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
import javafx.scene.Scene;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controlador principal para la interfaz de usuario de una Protectora.
 * Muestra los perros registrados por la protectora en tarjetas, permite añadir nuevos perros,
 * y visualiza registros de citas y adopciones en tablas intercambiables.
 * También gestiona la navegación al perfil de la protectora y a una bandeja de entrada (futura).
 */
public class MainProtectoraController {

    // --- Componentes FXML ---
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

    // Tabla para Citas
    @FXML private TableView<RegistroCitaInfo> TablaRegistroCitas;
    @FXML private TableColumn<RegistroCitaInfo, String> ColumNombrePerro;
    @FXML private TableColumn<RegistroCitaInfo, LocalDate> ColumDiaCita;
    @FXML private TableColumn<RegistroCitaInfo, LocalTime> ColumHoraCita;
    @FXML private TableColumn<RegistroCitaInfo, String> ColumNombreClienteCita;
    @FXML private TableColumn<RegistroCitaInfo, String> ColumEstadoCita;

    // Tabla para Adopciones
    @FXML private TableView<RegistroAdopcionInfo> TablaRegistroAdopciones;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumNombrePerroAdopcion;
    @FXML private TableColumn<RegistroAdopcionInfo, LocalDate> ColumFechaAdopcion;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumHoraAdopcion;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumNombreClienteAdopcion;
    @FXML private TableColumn<RegistroAdopcionInfo, String> ColumEstadoAdopcion;


    // --- Constantes de UI y Rutas ---
    private static final String RUTA_BASE_IMAGENES_PERROS_RESOURCES = "/assets/Imagenes/Perros/";
    private static final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.png";
    private static final String RUTA_FXML_LOGIN = "/com/proyectointegral2/Vista/Login.fxml";
    private static final String RUTA_FXML_FORMULARIO_PERRO = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
    private static final String RUTA_FXML_PERFIL_PROTECTORA = "/com/proyectointegral2/Vista/PerfilProtectora.fxml";

    // Constantes para diseño responsivo de tarjetas de perro
    private static final double HEADER_HEIGHT_ESTIMADA = 70.0;
    private static final double TARJETA_IMG_AREA_WIDTH = 160.0;
    private static final double TARJETA_IMG_AREA_HEIGHT = 160.0;
    private static final double TARJETA_PREF_WIDTH = 190.0;
    private static final double CARD_HORIZONTAL_GAP = 20.0;
    private static final double CARD_VERTICAL_GAP = 20.0;
    private static final double CARD_INTERNAL_PADDING = 10.0;
    private static final double MIN_GRID_CONTAINER_HEIGHT = 250.0;
    private static final double HBOX_TITULO_GRID_HEIGHT_ESTIMADA = 60.0;
    private static final double TABLAS_SECTION_CONTAINER_HEIGHT_ESTIMADA = 350.0;

    // --- Estado del Controlador ---
    private List<Perro> listaDePerrosDeLaProtectora;
    private Usuario usuarioCuentaLogueada;
    private Protectora protectoraInfoActual;
    private int idProtectoraActual;
    private String nombreProtectoraActual;

    private boolean mostrandoRegistroCitas = true;

    // --- DAOs ---
    private PerroDao perroDao;
    private ProtectoraDao protectoraDao;
    private PeticionAdopcionDao peticionAdopcionDao;
    private ReservaCitaDao reservaCitaDao;


    /**
     * Método de inicialización del controlador.
     * Configura DAOs, valida la sesión de la protectora, carga datos iniciales y configura listeners.
     */
    @FXML
    public void initialize() {
        // 1. Inicializar DAOs
        try {
            this.perroDao = new PerroDao();
            this.protectoraDao = new ProtectoraDao();
            this.peticionAdopcionDao = new PeticionAdopcionDao();
            this.reservaCitaDao = new ReservaCitaDao();
        } catch (Exception e) {
            System.err.println("Error crítico al inicializar DAOs en MainProtectoraController: " + e.getMessage());
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sistema", "No se pudo inicializar el acceso a datos: " + e.getMessage() + "\nLa aplicación no funcionará correctamente.");
            if(BtnNuevoPerro != null) BtnNuevoPerro.setDisable(true);
            return;
        }

        // 2. Validar Sesión de Usuario y Obtener ID de Protectora
        this.usuarioCuentaLogueada = SesionUsuario.getUsuarioLogueado();
        this.idProtectoraActual = SesionUsuario.getEntidadIdEspecifica();

        if (this.usuarioCuentaLogueada == null || this.idProtectoraActual <= 0 ||
                !"PROTECTORA".equalsIgnoreCase(this.usuarioCuentaLogueada.getRol())) {
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sesión", "No hay información de protectora válida en la sesión actual. Será redirigido al inicio de sesión.");
            UtilidadesVentana.cambiarEscena(RUTA_FXML_LOGIN, "Inicio de Sesión", false);
            return;
        }

        // 3. Cargar Información de la Protectora
        try {
            this.protectoraInfoActual = protectoraDao.obtenerProtectoraPorId(this.idProtectoraActual);
            if (this.protectoraInfoActual != null) {
                this.nombreProtectoraActual = this.protectoraInfoActual.getNombre();
            } else {
                throw new SQLException("No se encontró información para la protectora con ID: " + this.idProtectoraActual);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Carga de Datos", "No se pudo cargar la información esencial de la protectora: " + e.getMessage() + "\nSerá redirigido al inicio de sesión.");
            UtilidadesVentana.cambiarEscena(RUTA_FXML_LOGIN, "Inicio de Sesión", false);
            return;
        }

        // 4. Configurar UI
        configurarColumnasDeTablas();
        cargarYMostrarPerrosEnGrid();
        configurarVisibilidadInicialDeTablas();
        configurarListenersDeVentanaParaResponsividad();
        actualizarVisibilidadDelLabelNoPerros();
    }

    /**
     * Configura la visibilidad inicial de las tablas de registro (Citas y Adopciones).
     * Por defecto, muestra la tabla de Citas.
     */
    private void configurarVisibilidadInicialDeTablas() {
        mostrandoRegistroCitas = true;
        actualizarContenidoSeccionRegistros();
    }

    /**
     * Configura las columnas para ambas TableView (Citas y Adopciones),
     * estableciendo qué propiedad de los modelos `RegistroCitaInfo` y `RegistroAdopcionInfo`
     * se mostrará en cada columna.
     * También establece los placeholders para cuando las tablas están vacías.
     */
    private void configurarColumnasDeTablas() {
        // Configuración Tabla de Citas
        if (TablaRegistroCitas != null) {
            ColumNombrePerro.setCellValueFactory(new PropertyValueFactory<>("nombrePerro"));
            ColumDiaCita.setCellValueFactory(new PropertyValueFactory<>("fechaCita"));
            ColumHoraCita.setCellValueFactory(new PropertyValueFactory<>("horaCita"));
            ColumNombreClienteCita.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
            ColumEstadoCita.setCellValueFactory(new PropertyValueFactory<>("estadoCita"));
            TablaRegistroCitas.setPlaceholder(new Label("No hay citas registradas para mostrar."));
        }

        // Configuración Tabla de Adopciones
        if (TablaRegistroAdopciones != null) {
            ColumNombrePerroAdopcion.setCellValueFactory(new PropertyValueFactory<>("nombrePerro"));
            ColumFechaAdopcion.setCellValueFactory(new PropertyValueFactory<>("fechaPeticion"));
            ColumHoraAdopcion.setCellValueFactory(new PropertyValueFactory<>("horaPeticion"));
            ColumNombreClienteAdopcion.setCellValueFactory(new PropertyValueFactory<>("nombreAdoptante"));
            ColumEstadoAdopcion.setCellValueFactory(new PropertyValueFactory<>("estadoPeticion"));
            TablaRegistroAdopciones.setPlaceholder(new Label("No hay adopciones (o peticiones aceptadas) para mostrar."));
        }
    }

    /**
     * Carga los datos para la tabla de registro de citas de la protectora.
     * Requiere un método en `ReservaCitaDao` que devuelva `List<RegistroCitaInfo>`.
     */
    private void cargarDatosParaTablaCitas() {
        if (TablaRegistroCitas == null || reservaCitaDao == null || idProtectoraActual <= 0) {
            if (TablaRegistroCitas != null) TablaRegistroCitas.getItems().clear();
            return;
        }
        try {
            List<RegistroCitaInfo> datosCitas = reservaCitaDao.obtenerCitasParaTablaProtectora(idProtectoraActual);
            ObservableList<RegistroCitaInfo> observableDatosCitas = FXCollections.observableArrayList(datosCitas);
            TablaRegistroCitas.setItems(observableDatosCitas);
        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "No se pudieron cargar los registros de citas: " + e.getMessage());
        }
    }

    /**
     * Carga los datos para la tabla de adopciones (peticiones aceptadas) de la protectora.
     * Requiere un método en `PeticionAdopcionDao` que devuelva `List<RegistroAdopcionInfo>`.
     */
    private void cargarDatosParaTablaAdopciones() {
        if (TablaRegistroAdopciones == null || peticionAdopcionDao == null || idProtectoraActual <= 0) {
            if (TablaRegistroAdopciones != null) TablaRegistroAdopciones.getItems().clear();
            return;
        }
        try {
            List<RegistroAdopcionInfo> datosAdopciones = peticionAdopcionDao.obtenerAdopcionesAceptadasParaTabla(idProtectoraActual);
            ObservableList<RegistroAdopcionInfo> observableDatosAdopciones = FXCollections.observableArrayList(datosAdopciones);
            TablaRegistroAdopciones.setItems(observableDatosAdopciones);
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "No se pudieron cargar los registros de adopciones: " + e.getMessage());
        }
    }

    /**
     * Carga la lista de perros de la protectora actual desde la base de datos
     * y actualiza el GridPane que los muestra.
     * También actualiza la visibilidad del mensaje "No hay perros".
     */
    private void cargarYMostrarPerrosEnGrid() {
        this.listaDePerrosDeLaProtectora = new ArrayList<>();
        if (perroDao == null || idProtectoraActual <= 0) {
            if (dogGrid != null) dogGrid.getChildren().clear();
            actualizarVisibilidadDelLabelNoPerros();
            return;
        }

        try {
            this.listaDePerrosDeLaProtectora = perroDao.obtenerPerrosPorProtectora(this.idProtectoraActual);
            if (this.listaDePerrosDeLaProtectora == null) {
                this.listaDePerrosDeLaProtectora = new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "No se pudieron cargar los perros de la protectora: " + e.getMessage());
        }

        actualizarVisibilidadDelLabelNoPerros();
        if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null &&
                ((Stage)mainBorderPane.getScene().getWindow()).isShowing()) {
            Platform.runLater(() -> adaptarUIAlTamanoDeVentana((Stage) mainBorderPane.getScene().getWindow()));
        } else {
            popularGridConPerrosDeProtectora();
        }
    }

    /**
     * Actualiza la visibilidad del Label que indica si hay o no perros registrados,
     * basándose en el contenido de `listaDePerrosDeLaProtectora`.
     * También controla la visibilidad del GridPane.
     */
    private void actualizarVisibilidadDelLabelNoPerros() {
        if (lblNoPerrosEnGrid != null && dogGrid != null) {
            boolean hayPerros = this.listaDePerrosDeLaProtectora != null && !this.listaDePerrosDeLaProtectora.isEmpty();
            lblNoPerrosEnGrid.setVisible(!hayPerros);
            dogGrid.setVisible(hayPerros);
        }
    }

    /**
     * Configura listeners en la ventana para adaptar la UI (GridPane de perros)
     * cuando cambia su tamaño o estado (maximizada).
     */
    private void configurarListenersDeVentanaParaResponsividad() {
        Runnable setupListeners = () -> {
            if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() instanceof Stage) {
                Stage stage = (Stage) mainBorderPane.getScene().getWindow();
                Runnable adaptarUI = () -> Platform.runLater(() -> {
                    if (stage.isShowing() && stage.getWidth() > 0 && !Double.isNaN(stage.getWidth())) {
                        adaptarUIAlTamanoDeVentana(stage);
                    }
                });

                // Adaptar UI si la ventana ya se muestra.
                if (stage.isShowing()) {
                    adaptarUI.run();
                } else {
                    stage.setOnShown(event -> adaptarUI.run());
                }
                // Añadir listeners para cambios de tamaño y estado.
                stage.widthProperty().addListener((obs, oldVal, newVal) -> adaptarUI.run());
                stage.heightProperty().addListener((obs, oldVal, newVal) -> adaptarUI.run());
                stage.maximizedProperty().addListener((obs, oldVal, newVal) -> adaptarUI.run());
            }
        };

        if (mainBorderPane.getScene() != null) {
            setupListeners.run();
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

    /**
     * Adapta la UI (GridPane de perros y su contenedor) al tamaño actual de la ventana.
     * @param stage La ventana principal (Stage).
     */
    private void adaptarUIAlTamanoDeVentana(Stage stage) {
        if (stage == null || stage.getWidth() <= 0 || Double.isNaN(stage.getWidth()) || dogGrid == null) {
            return;
        }
        adaptarAnchoDelGridYRepopular(stage.getWidth());
        adaptarAlturaDelContenedorDelGrid(stage.getHeight());
    }

    /**
     * Calcula el número óptimo de columnas para el GridPane de perros
     * basado en el ancho disponible.
     * @param anchoGridDisponible El ancho neto disponible para el contenido del GridPane.
     * @return El número de columnas, limitado entre 1 y 5.
     */
    private int calcularNumeroDeColumnasSegunAncho(double anchoGridDisponible) {
        if (anchoGridDisponible <= 0) return 1;
        double hgap = (dogGrid.getHgap() > 0 && !Double.isNaN(dogGrid.getHgap())) ? dogGrid.getHgap() : CARD_HORIZONTAL_GAP;
        double anchoEstimadoTarjetaConEspacio = TARJETA_PREF_WIDTH + hgap;
        int numColumnasCalculadas = Math.max(1, (int) (anchoGridDisponible / anchoEstimadoTarjetaConEspacio));
        return Math.min(numColumnasCalculadas, 8);
    }

    /**
     * Adapta el número de columnas del GridPane y repopula con tarjetas de perros
     * basándose en el ancho disponible de la ventana.
     * @param anchoVentana El ancho actual de la ventana.
     */
    private void adaptarAnchoDelGridYRepopular(double anchoVentana) {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null || mainBorderPane == null) return;

        // Calcular el ancho disponible real para el GridPane.
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

        // Verificar si es necesario reconfigurar columnas o repopular.
        boolean necesitaCambioDeColumnas = dogGrid.getColumnConstraints().size() != nuevasColumnas;
        boolean gridVacioPeroHayDatos = dogGrid.getChildren().isEmpty() && (listaDePerrosDeLaProtectora != null && !listaDePerrosDeLaProtectora.isEmpty());
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

    /**
     * Adapta la altura del contenedor del GridPane de perros.
     * La altura se calcula restando la altura de otros elementos (cabecera, sección de tablas)
     * de la altura total de la ventana.
     * @param alturaVentana La altura actual de la ventana.
     */
    private void adaptarAlturaDelContenedorDelGrid(double alturaVentana) {

        if (mainBorderPane.getCenter() instanceof ScrollPane) {
            ScrollPane mainScrollPane = (ScrollPane) mainBorderPane.getCenter();
            if (mainScrollPane.getContent() instanceof VBox) {
                VBox vBoxPrincipal = (VBox) mainScrollPane.getContent();

                if (!vBoxPrincipal.getChildren().isEmpty() && vBoxPrincipal.getChildren().get(0) instanceof VBox) {
                    VBox vBoxContenedorDelGrid = (VBox) vBoxPrincipal.getChildren().get(0);

                    double alturaCabecera = (mainBorderPane.getTop() != null) ? mainBorderPane.getTop().getLayoutBounds().getHeight() : HEADER_HEIGHT_ESTIMADA;

                    double alturaSeccionTablas = 0;
                    if (vBoxPrincipal.getChildren().size() > 1 && vBoxPrincipal.getChildren().get(1) instanceof VBox) {
                        VBox vBoxContenedorTablas = (VBox) vBoxPrincipal.getChildren().get(1);
                        alturaSeccionTablas = vBoxContenedorTablas.getLayoutBounds().getHeight();
                        if (alturaSeccionTablas <=0) alturaSeccionTablas = TABLAS_SECTION_CONTAINER_HEIGHT_ESTIMADA;
                    }

                    double paddingVerticalVBoxPrincipal = vBoxPrincipal.getPadding().getTop() + vBoxPrincipal.getPadding().getBottom();
                    double spacingVBoxPrincipal = vBoxPrincipal.getSpacing();

                    double alturaDisponibleParaContenedorGrid = alturaVentana - alturaCabecera - alturaSeccionTablas - paddingVerticalVBoxPrincipal - spacingVBoxPrincipal - 20;
                    vBoxContenedorDelGrid.setPrefHeight(Math.max(MIN_GRID_CONTAINER_HEIGHT, alturaDisponibleParaContenedorGrid));
                }
            }
        }
    }


    /**
     * Limpia el GridPane y lo llena con tarjetas de perro creadas a partir de
     * `listaDePerrosDeLaProtectora`.
     */
    private void popularGridConPerrosDeProtectora() {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null) {
            System.err.println("Error: dogGrid o listaDePerrosDeLaProtectora es null en popularGrid.");
            return;
        }
        dogGrid.getChildren().clear();
        int numColumnasActual = Math.max(1, dogGrid.getColumnConstraints().size());
        actualizarVisibilidadDelLabelNoPerros();

        if (listaDePerrosDeLaProtectora.isEmpty()) {
            return;
        }

        int columna = 0;
        int fila = 0;
        for (Perro perro : listaDePerrosDeLaProtectora) {
            VBox tarjeta = crearTarjetaPerroParaProtectora(perro);
            dogGrid.add(tarjeta, columna, fila);
            columna++;
            if (columna >= numColumnasActual) {columna = 0;
                fila++;
            }
        }
    }

    /**
     * Crea un VBox (tarjeta) visualmente representando un perro para la vista de la protectora.
     * Incluye imagen, nombre, estado y botones de acción (Editar, Eliminar).
     * @param perro El objeto Perro para el cual crear la tarjeta.
     * @return Un VBox configurado como una tarjeta de perro.
     */
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

        // Contenedor para la imagen
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

        // Nombre del perro
        Label lblNombrePerro = new Label(Objects.requireNonNullElse(perro.getNombre(), "Sin Nombre"));
        lblNombrePerro.setStyle("-fx-background-color: #A9D18E; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 5; -fx-background-radius: 3;");
        lblNombrePerro.setAlignment(Pos.CENTER);
        lblNombrePerro.setPrefWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setMaxWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);

        // Estado del perro (Adoptado / En Adopción)
        String estadoPerro = "N/D";
        if (perro.getAdoptado() != null) {
            estadoPerro = "S".equalsIgnoreCase(perro.getAdoptado()) ? "Adoptado" : ("R".equalsIgnoreCase(perro.getAdoptado()) ? "Reservado" : ("A".equalsIgnoreCase(perro.getAdoptado()) ? "En Acogida" : "En Adopción"));
        }
        Label lblEstado = new Label(estadoPerro);
        String styleEstado = "-fx-font-size: 12px; ";
        if ("Adoptado".equals(estadoPerro)) styleEstado += "-fx-text-fill: grey;";
        else if ("Reservado".equals(estadoPerro)) styleEstado += "-fx-text-fill: orange; -fx-font-weight:bold;";
        else if ("En Acogida".equals(estadoPerro)) styleEstado += "-fx-text-fill: #3498DB; -fx-font-weight:bold;";
        else styleEstado += "-fx-text-fill: green; -fx-font-weight:bold;"; // En Adopción
        lblEstado.setStyle(styleEstado);
        VBox.setMargin(lblEstado, new Insets(4, 0, 4, 0));

        // Botones de acción para la tarjeta
        HBox botonesTarjeta = new HBox(10); // Espaciado entre botones
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

    /**
     * Carga la imagen del perro en el ImageView proporcionado.
     * Utiliza un placeholder si la imagen no se encuentra o hay un error.
     * @param imageView El ImageView donde se cargará la imagen.
     * @param rutaFotoRelativa La ruta de la foto, relativa a RUTA_BASE_IMAGENES_PERROS_RESOURCES.
     */
    private void cargarImagenPerroEnTarjeta(ImageView imageView, String rutaFotoRelativa) {
        Image imagenCargada = null;
        if (rutaFotoRelativa != null && !rutaFotoRelativa.trim().isEmpty()) {

            String rutaCompletaRecurso = rutaFotoRelativa.startsWith("/") ? rutaFotoRelativa : RUTA_BASE_IMAGENES_PERROS_RESOURCES + rutaFotoRelativa;
            rutaCompletaRecurso = rutaCompletaRecurso.replace("//", "/");

            try (InputStream stream = getClass().getResourceAsStream(rutaCompletaRecurso)) {
                if (stream != null) {
                    imagenCargada = new Image(stream);
                } else {
                    System.err.println("WARN: Imagen de perro no encontrada en classpath: " + rutaCompletaRecurso);
                }
            } catch (Exception e) {
                System.err.println("ERROR: Excepción al cargar imagen de perro desde '" + rutaCompletaRecurso + "': " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (imagenCargada == null || imagenCargada.isError()) {
            try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO)) {
                if (placeholderStream != null) {
                    imagenCargada = new Image(placeholderStream);
                } else {
                    System.err.println("Error Crítico: Placeholder de perro no encontrado: " + RUTA_IMAGEN_PLACEHOLDER_PERRO);
                }
            } catch (Exception e) {
                System.err.println("Excepción crítica al cargar imagen placeholder de perro: " + e.getMessage());
                e.printStackTrace();
            }
        }
        imageView.setImage(imagenCargada);
    }


    /**
     * Maneja la acción de editar un perro. Abre el formulario de perro en modo edición.
     * @param perro El perro a editar.
     */
    private void handleEditarPerro(Perro perro) {
        if (perro == null) {
            UtilidadesVentana.mostrarAlertaError("Error", "No se ha seleccionado un perro para editar.");
            return;
        }

        if (this.idProtectoraActual <= 0) {
            UtilidadesVentana.mostrarAlertaError("Error de Sesión", "No se puede editar el perro sin identificar la protectora actual.");
            return;
        }

        System.out.println("INFO: Editando perro ID: " + perro.getIdPerro() + ", Nombre: " + perro.getNombre());
        Stage ownerStage = (mainBorderPane.getScene() != null) ? (Stage) mainBorderPane.getScene().getWindow() : null;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_FXML_FORMULARIO_PERRO));
            Parent root = loader.load();
            FormularioPerroController formularioController = loader.getController();

            if (formularioController != null) {

                formularioController.initDataParaEdicion(perro, this.idProtectoraActual);

                UtilidadesVentana.mostrarVentanaComoDialogo(root, "Editar Perro: " + perro.getNombre(), ownerStage);

                cargarYMostrarPerrosEnGrid();
                actualizarVisibilidadDelLabelNoPerros();
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Interno", "No se pudo cargar el controlador del formulario de edición de perros.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir el formulario de edición de perros: " + e.getMessage());
        }
    }

    /**
     * Maneja la acción de eliminar un perro. Pide confirmación antes de proceder.
     * @param perro El perro a eliminar.
     */
    private void handleEliminarPerro(Perro perro) {
        if (perro == null) return;

        if (UtilidadesVentana.mostrarAlertaConfirmacion("Confirmar Eliminación",
                "¿Está seguro de que desea eliminar permanentemente a '" + perro.getNombre() + "'? Esta acción no se puede deshacer.")) {
            if (perroDao != null) {
                try {
                    if (perroDao.eliminarPerro(perro.getIdPerro())) {
                        UtilidadesVentana.mostrarAlertaInformacion("Eliminación Exitosa",
                                "El perro '" + perro.getNombre() + "' ha sido eliminado.");
                        cargarYMostrarPerrosEnGrid();
                        actualizarVisibilidadDelLabelNoPerros();
                    } else {
                        UtilidadesVentana.mostrarAlertaError("Error de Eliminación",
                                "No se pudo eliminar el perro de la base de datos. Puede que aún tenga citas o peticiones asociadas.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "Ocurrió un error al intentar eliminar el perro: " + e.getMessage() + "\nEs posible que el perro tenga registros asociados (citas, adopciones) que impiden su eliminación directa.");
                }
            }
        }
    }

    /**
     * Maneja el evento del ImageView para ir a la bandeja de entrada (funcionalidad futura).
     */
    @FXML
    void IrABandeja(MouseEvent event) {
        UtilidadesVentana.mostrarAlertaInformacion("Próximamente", "La bandeja de entrada para protectoras aún no está implementada.");
    }

    /**
     * Maneja el evento del ImageView para ir al perfil de la protectora (para edición).
     */
    @FXML
    void IrAPerfilUsuario(MouseEvent event) {
        if (this.protectoraInfoActual == null || this.usuarioCuentaLogueada == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Datos", "No se puede mostrar el perfil. Información de protectora o cuenta no disponible.");
            return;
        }
        System.out.println("INFO: Navegando al perfil de la protectora: " + this.nombreProtectoraActual);
        String titulo = "Perfil de " + this.nombreProtectoraActual;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_FXML_PERFIL_PROTECTORA));
            Parent root = loader.load();
            PerfilProtectoraController perfilController = loader.getController();
            if (perfilController != null) {
                perfilController.initData(this.protectoraInfoActual, this.usuarioCuentaLogueada);
                Stage stage = (Stage) mainBorderPane.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle(titulo);
                stage.show();
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Interno", "No se pudo cargar el controlador del perfil de protectora.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir el perfil de la protectora: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento del botón para alternar entre la tabla de Citas y la de Adopciones.
     * @param event El evento de acción.
     */
    @FXML
    void RegistroAdopciones(ActionEvent event) {
        mostrandoRegistroCitas = !mostrandoRegistroCitas;
        actualizarContenidoSeccionRegistros();
    }

    /**
     * Actualiza la visibilidad de las tablas y el texto del título/botón
     * según el valor de `mostrandoRegistroCitas`.
     * También carga los datos correspondientes en la tabla visible.
     */
    private void actualizarContenidoSeccionRegistros() {
        if (mostrandoRegistroCitas) {
            if (lblRegistroTitulo != null) lblRegistroTitulo.setText("Registro de Citas Programadas");
            if (BtnToggleRegistro != null) BtnToggleRegistro.setText("Ver Registro de Adopciones");
            if (TablaRegistroCitas != null) {
                TablaRegistroCitas.setVisible(true);
                cargarDatosParaTablaCitas();
            }
            if (TablaRegistroAdopciones != null) TablaRegistroAdopciones.setVisible(false);
        } else {
            if (lblRegistroTitulo != null) lblRegistroTitulo.setText("Registro de Adopciones Realizadas");
            if (BtnToggleRegistro != null) BtnToggleRegistro.setText("Ver Registro de Citas");
            if (TablaRegistroCitas != null) TablaRegistroCitas.setVisible(false);
            if (TablaRegistroAdopciones != null) {
                TablaRegistroAdopciones.setVisible(true);
                cargarDatosParaTablaAdopciones();
            }
        }
    }

    /**
     * Maneja el evento del botón "Añadir nuevo perro".
     * Abre el formulario para crear un nuevo perro, asociándolo a la protectora actual.
     * @param event El evento de acción.
     */
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
                actualizarVisibilidadDelLabelNoPerros();
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Interno", "No se pudo cargar el controlador del formulario para añadir perros.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir el formulario para añadir un nuevo perro: " + e.getMessage());
        }
    }
}