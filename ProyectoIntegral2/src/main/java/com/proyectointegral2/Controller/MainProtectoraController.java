package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.dao.PerroDao; // <<--- DESCOMENTA ESTO
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
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
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
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
    @FXML private Label lblRegistroTitulo;
    @FXML private Button BtnToggleRegistro;
    @FXML private StackPane tablasStackPane;
    @FXML private TableView<?> TablaRegistroPerros;
    @FXML private TableView<?> tablaRegistroAdopciones;

    // --- Constants ---
    private final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.png";
    private static final double HEADER_HEIGHT_ESTIMADA = 70.0;
    private static final double HBOX_TITULO_GRID_HEIGHT_ESTIMADA = 60.0;
    private static final double TABLAS_SECTION_HEIGHT_ESTIMADA = 350.0;
    private static final double GRID_VBOX_PADDING_Y_ESTIMADO = 20.0;
    private static final double MAIN_SCROLL_VBOX_SPACING_ESTIMADO = 25.0;
    private static final double MIN_GRID_HEIGHT = 250.0;
    private static final double PREFERRED_GRID_WIDTH_FALLBACK = 1000.0;
    private static final double TARJETA_IMG_AREA_WIDTH = 160.0;
    private static final double TARJETA_IMG_AREA_HEIGHT = 160.0;

    // --- Instance Variables ---
    private List<Perro> listaDePerrosDeLaProtectora;
    private PerroDao perroDao; // <<--- DESCOMENTADO
    private int idProtectoraActual;
    private String nombreProtectoraActual;

    @FXML
    public void initialize() {
        System.out.println("MainProtectoraController inicializado. Esperando ID de Protectora...");
        try {
            this.perroDao = new PerroDao(); // <<--- INSTANCIAR DAO
        } catch (Exception e) { // El constructor de PerroDao podría lanzar Exception (ej. si carga config.properties)
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico DAO", "No se pudo inicializar el acceso a datos de perros: " + e.getMessage());
            // Podrías deshabilitar funcionalidad si el DAO falla
        }
        if (TablaRegistroPerros != null) TablaRegistroPerros.setVisible(true);
        if (tablaRegistroAdopciones != null) tablaRegistroAdopciones.setVisible(false);
    }

    public void initData(int idProtectora, String nombreProtectora) {
        this.idProtectoraActual = idProtectora;
        this.nombreProtectoraActual = nombreProtectora;
        System.out.println("Protectora ID: " + idProtectoraActual + ", Nombre: " + nombreProtectoraActual + " ha iniciado sesión.");
        cargarYMostrarPerrosDeProtectora();
        configurarListenersDeVentana();
    }

    private void cargarYMostrarPerrosDeProtectora() {
        this.listaDePerrosDeLaProtectora = new ArrayList<>(); // Inicializar para evitar NullPointer

        if (perroDao != null && idProtectoraActual > 0) {
            try {
                this.listaDePerrosDeLaProtectora = perroDao.obtenerPerrosPorProtectora(this.idProtectoraActual);
                if (this.listaDePerrosDeLaProtectora == null) { // El DAO podría devolver null si no hay perros o error
                    this.listaDePerrosDeLaProtectora = new ArrayList<>();
                }
                System.out.println("Perros cargados desde DAO para protectora " + idProtectoraActual + ": " + this.listaDePerrosDeLaProtectora.size());
            } catch (SQLException e) {
                e.printStackTrace();
                UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "No se pudieron cargar los perros de la protectora: " + e.getMessage());

            }
        } else {
            if (idProtectoraActual <= 0) System.err.println("ID Protectora ("+idProtectoraActual+") inválido para cargar perros.");
            else if (perroDao == null) System.err.println("PerroDao no inicializado, no se pueden cargar perros.");
        }

        if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null &&
                ((Stage)mainBorderPane.getScene().getWindow()).isShowing()) {
            Platform.runLater(() -> adaptarUIAlTamanoVentana((Stage) mainBorderPane.getScene().getWindow()));
        }
    }

    // configurarListenersDeVentana se mantiene igual
    private void configurarListenersDeVentana() {
        if (mainBorderPane != null) {
            mainBorderPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                        if (newWindow != null) {
                            Stage stage = (Stage) newWindow;
                            Runnable adaptarAlMostrar = () -> {
                                Platform.runLater(() -> {
                                    if (stage.getWidth() > 0 && !Double.isNaN(stage.getWidth())) {
                                        adaptarUIAlTamanoVentana(stage);
                                    }
                                });
                            };
                            if (stage.isShowing()) adaptarAlMostrar.run();
                            else stage.setOnShown(event -> adaptarAlMostrar.run());
                            stage.widthProperty().addListener((obs, o, n) -> adaptarAlMostrar.run());
                            stage.heightProperty().addListener((obs, o, n) -> adaptarAlMostrar.run());
                            stage.maximizedProperty().addListener((obs, o, n) -> adaptarAlMostrar.run());
                        }
                    });
                }
            });
        }
    }

    private void adaptarUIAlTamanoVentana(Stage stage) {
        if (stage == null || stage.getWidth() <= 0 || Double.isNaN(stage.getWidth())) return;
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        System.out.println("Protectora - Adaptando UI a: " + currentWidth + "x" + currentHeight + ", Maximized: " + stage.isMaximized());
        adaptarContenidoAlAnchoYPopular(currentWidth);
        adaptarContenidoALaAltura(currentHeight);
    }

    private VBox crearTarjetaPerroProtectora(Perro perro) {
        VBox card = new VBox(5);
        card.setPrefWidth(180);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-padding: 10;");
        card.setMinHeight(260);

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);
        imageContainer.setMinSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);
        imageContainer.setMaxSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);

        ImageView imgView = new ImageView();
        String imagePath = perro.getFoto();
        try {
            Image loadedImage = null;
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                if (!imagePath.startsWith("/")) { imagePath = "/" + imagePath; }
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
        } catch (Exception e) {System.err.println("Err img: " + perro.getNombre() + " " + e.getMessage());}

        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        imgView.setFitWidth(TARJETA_IMG_AREA_WIDTH);
        imgView.setFitHeight(TARJETA_IMG_AREA_HEIGHT);
        imageContainer.getChildren().add(imgView);
        VBox.setMargin(imageContainer, new Insets(0,0,8,0));

        Label lblNombrePerro = new Label(perro.getNombre());
        lblNombrePerro.setStyle("-fx-background-color: #A9D18E; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 5; -fx-background-radius: 3;");
        lblNombrePerro.setAlignment(Pos.CENTER);
        lblNombrePerro.setPrefWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setMaxWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);

        Label lblEstado = new Label(perro.isAdoptado() ? "Adoptado" : "En Adopción");
        lblEstado.setStyle(perro.isAdoptado() ? "-fx-text-fill: grey;" : "-fx-text-fill: green; -fx-font-weight:bold;");
        VBox.setMargin(lblEstado, new Insets(4,0,4,0));

        HBox botonesTarjeta = new HBox(10);
        botonesTarjeta.setAlignment(Pos.CENTER);
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

    // handleEditarPerro se mantiene igual
    private void handleEditarPerro(Perro perro) {
        System.out.println("Editar perro: " + perro.getNombre());
        String formularioPerroFxml = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
        String titulo = "Editar Perro: " + perro.getNombre();
        Stage owner = (mainBorderPane != null && mainBorderPane.getScene() != null) ? (Stage) mainBorderPane.getScene().getWindow() : null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formularioPerroFxml));
            Parent root = loader.load();
            FormularioPerroController formController = loader.getController();
            if (formController != null) {
                formController.initDataParaEdicion(perro, this.idProtectoraActual);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error", "Controlador de FormularioPerro no encontrado.");
                return;
            }
            UtilidadesVentana.mostrarVentanaComoDialogo(root, titulo, owner);
            cargarYMostrarPerrosDeProtectora(); // Refrescar después de cerrar el diálogo
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error", "No se pudo abrir el formulario de edición del perro.");
        }
    }

    // handleEliminarPerro se mantiene igual, pero la lógica DAO está comentada
    private void handleEliminarPerro(Perro perro) {
        System.out.println("Eliminar perro: " + perro.getNombre());
        boolean confirmado = UtilidadesVentana.mostrarAlertaConfirmacion(
                "Confirmar Eliminación",
                "¿Estás seguro de que quieres eliminar a " + perro.getNombre() + "? Esta acción no se puede deshacer."
        );
        if (confirmado) {
            if (perroDao != null) { // Solo si el DAO está inicializado
                try {
                    boolean eliminado = perroDao.eliminarPerro(perro.getIdPerro());
                    if (eliminado) {
                        UtilidadesVentana.mostrarAlertaInformacion("Éxito", perro.getNombre() + " ha sido eliminado.");
                        // Quitar de la lista local y repopular
                        if (listaDePerrosDeLaProtectora != null) {
                            listaDePerrosDeLaProtectora.removeIf(p -> p.getIdPerro() == perro.getIdPerro());
                        }
                        // Llamar a adaptar para que repopule el grid
                        if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null) {
                            adaptarUIAlTamanoVentana((Stage)mainBorderPane.getScene().getWindow());
                        } else {
                            popularGridDePerrosProtectora(); // Fallback
                        }
                    } else {
                        UtilidadesVentana.mostrarAlertaError("Error", "No se pudo eliminar el perro de la base de datos.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    UtilidadesVentana.mostrarAlertaError("Error Base de Datos", "No se pudo eliminar el perro: " + e.getMessage());
                }
            } else { // SIMULACIÓN si perroDao es null
                if (listaDePerrosDeLaProtectora != null) {
                    listaDePerrosDeLaProtectora.removeIf(p -> p.getIdPerro() == perro.getIdPerro());
                }
                cargarYMostrarPerrosDeProtectora(); // Recargar y repopular desde la lista modificada (simulación)
                System.out.println(perro.getNombre() + " eliminado (simulación).");
            }
        }
    }


    // calcularColumnasSegunAncho se mantiene igual
    private int calcularColumnasSegunAncho(double anchoVentana) {
        if (anchoVentana <= 0 || Double.isNaN(anchoVentana)) return 3;
        double hgap = (dogGrid != null && dogGrid.getHgap() > 0) ? dogGrid.getHgap() : 20;
        double anchoRealTarjetaConEspacio = 180 + hgap + 20;
        int numColumnas = Math.max(1, (int) (anchoVentana / anchoRealTarjetaConEspacio));
        return Math.min(numColumnas, 5);
    }

    // adaptarContenidoAlAnchoYPopular se mantiene igual
    private void adaptarContenidoAlAnchoYPopular(double nuevoAncho) {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null) {
            System.err.println("Error: dogGrid o listaDePerrosDeLaProtectora es null en adaptarContenidoAlAnchoYPopular.");
            return;
        }
        System.out.println("Protectora - Adaptando al ancho: " + nuevoAncho);
        int nuevasColumnas = calcularColumnasSegunAncho(nuevoAncho);

        boolean necesitaReconfigurarColumnas = dogGrid.getColumnConstraints().size() != nuevasColumnas;
        boolean gridVacioConDatos = dogGrid.getChildren().isEmpty() && !listaDePerrosDeLaProtectora.isEmpty();
        boolean listaCambio = dogGrid.getUserData() != listaDePerrosDeLaProtectora ||
                dogGrid.getChildren().size() != listaDePerrosDeLaProtectora.size();

        if (necesitaReconfigurarColumnas || gridVacioConDatos || listaCambio) {
            if (necesitaReconfigurarColumnas) {
                dogGrid.getColumnConstraints().clear();
                for (int i = 0; i < nuevasColumnas; i++) {
                    ColumnConstraints colConst = new ColumnConstraints();
                    colConst.setPercentWidth(100.0 / nuevasColumnas);
                    colConst.setHgrow(Priority.SOMETIMES);
                    dogGrid.getColumnConstraints().add(colConst);
                }
                System.out.println("Columnas de dogGrid reconfiguradas a: " + nuevasColumnas);
            }
            popularGridDePerrosProtectora();
            dogGrid.setUserData(listaDePerrosDeLaProtectora);
        } else {
            System.out.println("No se necesita repopular/reconfigurar columnas para dogGrid.");
        }
    }

    // adaptarContenidoALaAltura se mantiene igual
    private void adaptarContenidoALaAltura(double nuevaAlturaVentana) {
        System.out.println("Protectora - Adaptar contenido a la altura: " + nuevaAlturaVentana);
        if (mainBorderPane.getCenter() instanceof ScrollPane) {
            ScrollPane mainScrollPane = (ScrollPane) mainBorderPane.getCenter();
            if (mainScrollPane.getContent() instanceof VBox) {
                VBox vBoxPrincipalContenidoScroll = (VBox) mainScrollPane.getContent();
                if (vBoxPrincipalContenidoScroll.getChildren().size() >= 2 &&
                        vBoxPrincipalContenidoScroll.getChildren().get(0) instanceof VBox &&
                        vBoxPrincipalContenidoScroll.getChildren().get(1) instanceof VBox) {
                    VBox vBoxSeccionGrid = (VBox) vBoxPrincipalContenidoScroll.getChildren().get(0);
                    Node topNode = mainBorderPane.getTop();
                    double topHeight = (topNode != null && topNode.getLayoutBounds().getHeight() > 0) ?
                            topNode.getLayoutBounds().getHeight() : HEADER_HEIGHT_ESTIMADA;
                    double alturaSeccionTablas = 0;
                    if (tablasStackPane != null && tablasStackPane.getScene() != null && tablasStackPane.getParent() != null && tablasStackPane.getParent().isVisible()) {
                        alturaSeccionTablas = tablasStackPane.getLayoutBounds().getHeight();
                        if (alturaSeccionTablas <= 0 && tablasStackPane.getPrefHeight() > 0 && tablasStackPane.getPrefHeight() != Region.USE_COMPUTED_SIZE) {
                            alturaSeccionTablas = tablasStackPane.getPrefHeight();
                        } else if (alturaSeccionTablas <= 0) { alturaSeccionTablas = TABLAS_SECTION_HEIGHT_ESTIMADA; }
                        if (vBoxPrincipalContenidoScroll.getChildren().size() > 1) {
                            Node vBoxTablasNode = vBoxPrincipalContenidoScroll.getChildren().get(1);
                            if (vBoxTablasNode instanceof VBox) {
                                alturaSeccionTablas += ((VBox)vBoxTablasNode).getPadding().getTop() + ((VBox)vBoxTablasNode).getPadding().getBottom();
                            }
                        }
                    } else { alturaSeccionTablas = TABLAS_SECTION_HEIGHT_ESTIMADA; }
                    double paddingVBoxPrincipal = vBoxPrincipalContenidoScroll.getPadding().getTop() + vBoxPrincipalContenidoScroll.getPadding().getBottom();
                    double spacingVBoxPrincipal = vBoxPrincipalContenidoScroll.getSpacing();
                    double alturaDisponibleParaSeccionGridYTitulo = nuevaAlturaVentana - topHeight - alturaSeccionTablas - paddingVBoxPrincipal - spacingVBoxPrincipal;
                    vBoxSeccionGrid.setPrefHeight(Math.max(MIN_GRID_HEIGHT + HBOX_TITULO_GRID_HEIGHT_ESTIMADA, alturaDisponibleParaSeccionGridYTitulo));
                }
            }
        }
    }

    // popularGridDePerrosProtectora se mantiene igual
    private void popularGridDePerrosProtectora() {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null) return;
        dogGrid.getChildren().clear();
        int numColumnas = dogGrid.getColumnConstraints().size();
        if (numColumnas == 0) {
            double currentWidth = (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null) ?
                    mainBorderPane.getScene().getWindow().getWidth() : PREFERRED_GRID_WIDTH_FALLBACK;
            adaptarContenidoAlAnchoYPopular(currentWidth);
            return;
        }
        System.out.println("Populando dogGrid Protectora con " + listaDePerrosDeLaProtectora.size() + " perros en " + numColumnas + " columnas.");
        int columnaActual = 0; int filaActual = 0;
        for (Perro perro : listaDePerrosDeLaProtectora) {
            VBox tarjetaPerro = crearTarjetaPerroProtectora(perro);
            dogGrid.add(tarjetaPerro, columnaActual, filaActual);
            columnaActual++;
            if (columnaActual >= numColumnas) { columnaActual = 0; filaActual++; }
        }
    }


    // --- ACCIONES DE BOTONES DEL MENÚ SUPERIOR DE PROTECTORA ---
    @FXML
    void NuevoPerro(ActionEvent event) {
        System.out.println("Botón Añadir Nuevo Perro presionado.");
        String formularioPerroFxml = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
        String titulo = "Añadir Nuevo Perro";
        Stage ownerStage = (Stage) mainBorderPane.getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formularioPerroFxml));
            if (loader.getLocation() == null) {
                UtilidadesVentana.mostrarAlertaError("Error", "FXML no encontrado: " + formularioPerroFxml);
                return;
            }
            Parent root = loader.load();
            FormularioPerroController formController = loader.getController();

            if (formController != null) {
                System.out.println("MainProtectoraController - Pasando idProtectoraActual (" + this.idProtectoraActual + ") a FormularioPerroController");
                formController.initDataParaNuevoPerro(this.idProtectoraActual);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error", "Controlador de FormularioPerro no encontrado.");
                return;
            }
            UtilidadesVentana.mostrarVentanaComoDialogo(root, titulo, ownerStage);
            System.out.println("Pop-up de FormularioPerro cerrado. Recargando perros...");
            cargarYMostrarPerrosDeProtectora(); // Refrescar la lista después de cerrar el diálogo

        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir el formulario para añadir perro: " + e.getMessage());
        }
    }

    @FXML
    void IrABandeja(MouseEvent event) {
        System.out.println("Protectora - Icono Bandeja presionado");
        UtilidadesVentana.mostrarAlertaInformacion("Próximamente", "Bandeja de entrada de protectora no implementada.");
    }

    @FXML
    void IrAPerfilUsuario(MouseEvent event) {
        System.out.println("Protectora - Icono Usuario (Perfil Protectora) presionado");
        UtilidadesVentana.mostrarAlertaInformacion("Próximamente", "Perfil de protectora no implementado.");
    }

    @FXML
    void RegistroAdopciones(ActionEvent event) {
        boolean mostrarAdopciones = tablaRegistroAdopciones == null || !tablaRegistroAdopciones.isVisible();
        if (mostrarAdopciones) {
            lblRegistroTitulo.setText("Registro de adopciones");
            BtnToggleRegistro.setText("Registro de mis perros");
            if (TablaRegistroPerros != null) TablaRegistroPerros.setVisible(false);
            if (tablaRegistroAdopciones != null) tablaRegistroAdopciones.setVisible(true);
            System.out.println("Mostrando tabla de registro de adopciones.");
        } else {
            lblRegistroTitulo.setText("Registro de mis perros");
            BtnToggleRegistro.setText("Registro de adopciones");
            if (TablaRegistroPerros != null) TablaRegistroPerros.setVisible(true);
            if (tablaRegistroAdopciones != null) tablaRegistroAdopciones.setVisible(false);
            System.out.println("Mostrando tabla de registro de perros.");
        }
    }
}