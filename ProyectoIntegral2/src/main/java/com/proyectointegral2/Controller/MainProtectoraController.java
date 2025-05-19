package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
// import com.proyectointegral2.dao.PerroDao; // Descomenta cuando lo uses
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
import javafx.scene.layout.Region; // Import faltante
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
    private static final double HBOX_TITULO_GRID_HEIGHT_ESTIMADA = 60.0; // Renombrado para claridad
    private static final double TABLAS_SECTION_HEIGHT_ESTIMADA = 350.0;
    private static final double GRID_VBOX_PADDING_Y_ESTIMADO = 20.0; // Padding del VBox que contiene el HBox título y dogGrid
    private static final double MAIN_SCROLL_VBOX_SPACING_ESTIMADO = 25.0; // Spacing del VBox principal dentro del ScrollPane
    private static final double MIN_GRID_HEIGHT = 250.0;
    private static final double PREFERRED_GRID_WIDTH_FALLBACK = 1000.0;
    private static final double TARJETA_IMG_AREA_WIDTH = 160.0;
    private static final double TARJETA_IMG_AREA_HEIGHT = 160.0;

    // --- Instance Variables ---
    private List<Perro> listaDePerrosDeLaProtectora;
    // private PerroDao perroDao;
    private int idProtectoraActual;
    private String nombreProtectoraActual;

    @FXML
    public void initialize() {
        System.out.println("MainProtectoraController inicializado. Esperando ID de Protectora...");
        // try { this.perroDao = new PerroDao(); } catch (Exception e) { e.printStackTrace(); /*...*/ }
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
        cargarPerrosSimuladosParaProtectora();
        if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null &&
                ((Stage)mainBorderPane.getScene().getWindow()).isShowing()) {
            Platform.runLater(() -> adaptarUIAlTamanoVentana((Stage) mainBorderPane.getScene().getWindow()));
        }
    }

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

    private void cargarPerrosSimuladosParaProtectora() {
        listaDePerrosDeLaProtectora = new ArrayList<>();
        Raza labrador = new Raza(1, "Labrador Retriever");
        Raza husky = new Raza(2, "Siberian Husky");
        Raza golden = new Raza(3, "Golden Retriever");

        if (this.idProtectoraActual == 1) {
            listaDePerrosDeLaProtectora.add(new Perro(101, "Buddy", "/assets/Imagenes/perros/buddy_labrador.jpg", LocalDate.of(2022, 3, 15), "Macho", "N", labrador, 1));
            listaDePerrosDeLaProtectora.add(new Perro(102, "Kira", "/assets/Imagenes/perros/kira_husky.jpg", LocalDate.of(2021, 11, 1), "Hembra", "N", husky, 1));
            listaDePerrosDeLaProtectora.add(new Perro(103, "Goldie", "/assets/Imagenes/perros/goldie_golden.jpg", LocalDate.of(2023, 1, 20), "Macho", "S", golden, 1));
        } else if (this.idProtectoraActual == 2) {
            listaDePerrosDeLaProtectora.add(new Perro(201, "Rex", "/assets/Imagenes/perros/perro_pastor.jpg", LocalDate.of(2022, 5, 5), "Macho", "N", new Raza(7,"Pastor Alemán"), 2));
        }
        System.out.println("Datos simulados cargados para Protectora ID " + idProtectoraActual + ": " + listaDePerrosDeLaProtectora.size() + " perros.");
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

    private void handleEditarPerro(Perro perro) {
        System.out.println("Editar perro: " + perro.getNombre());
        String formularioPerroFxml = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
        String titulo = "Editar Perro: " + perro.getNombre();
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
            UtilidadesVentana.cambiarEscenaConRoot(root, titulo, false);
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error", "No se pudo abrir el formulario de edición del perro.");
        }
    }

    private void handleEliminarPerro(Perro perro) {
        System.out.println("Eliminar perro: " + perro.getNombre());
        boolean confirmado = UtilidadesVentana.mostrarAlertaConfirmacion(
                "Confirmar Eliminación",
                "¿Estás seguro de que quieres eliminar a " + perro.getNombre() + "? Esta acción no se puede deshacer."
        );
        if (confirmado) {
            /*
            if(perroDao != null) {
                try {
                    perroDao.eliminarPerro(perro.getIdPerro()); // USA getIdPerro()
                    UtilidadesVentana.mostrarAlertaInformacion("Éxito", perro.getNombre() + " ha sido eliminado.");
                    cargarYMostrarPerrosDeProtectora();
                } catch (SQLException e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error DB", "No se pudo eliminar."); }
            }
            */
            if (listaDePerrosDeLaProtectora != null) {
                listaDePerrosDeLaProtectora.removeIf(p -> p.getIdPerro() == perro.getIdPerro()); // USA getIdPerro()
            }
            cargarYMostrarPerrosDeProtectora();
            System.out.println(perro.getNombre() + " eliminado (simulación).");
        }
    }

    private int calcularColumnasSegunAncho(double anchoVentana) {
        if (anchoVentana <= 0 || Double.isNaN(anchoVentana)) return 3;
        double hgap = (dogGrid != null && dogGrid.getHgap() > 0) ? dogGrid.getHgap() : 20;
        double anchoRealTarjetaConEspacio = 180 + hgap + 20;
        int numColumnas = Math.max(1, (int) (anchoVentana / anchoRealTarjetaConEspacio));
        return Math.min(numColumnas, 5);
    }

    private void adaptarContenidoAlAnchoYPopular(double nuevoAncho) {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null) return;
        System.out.println("Protectora - Adaptando al ancho: " + nuevoAncho);
        int nuevasColumnas = calcularColumnasSegunAncho(nuevoAncho);

        boolean necesitaReconfigurarColumnas = dogGrid.getColumnConstraints().size() != nuevasColumnas;
        boolean gridVacioConDatos = dogGrid.getChildren().isEmpty() && !listaDePerrosDeLaProtectora.isEmpty();
        boolean cantidadPerrosCambio = dogGrid.getChildren().size() != listaDePerrosDeLaProtectora.size();

        if (necesitaReconfigurarColumnas || gridVacioConDatos || cantidadPerrosCambio) {
            if (necesitaReconfigurarColumnas) {
                dogGrid.getColumnConstraints().clear();
                for (int i = 0; i < nuevasColumnas; i++) {
                    ColumnConstraints colConst = new ColumnConstraints();
                    colConst.setPercentWidth(100.0 / nuevasColumnas);
                    colConst.setHgrow(Priority.SOMETIMES);
                    dogGrid.getColumnConstraints().add(colConst);
                }
            }
            popularGridDePerrosProtectora();
            dogGrid.setUserData(listaDePerrosDeLaProtectora);
        }
    }

    private void adaptarContenidoALaAltura(double nuevaAlturaVentana) {
        System.out.println("Protectora - Adaptar contenido a la altura: " + nuevaAlturaVentana);

        ScrollPane mainScrollPane = null;
        if (mainBorderPane.getCenter() instanceof ScrollPane) {
            mainScrollPane = (ScrollPane) mainBorderPane.getCenter();
        }
        if (mainScrollPane == null || !(mainScrollPane.getContent() instanceof VBox)) {
            System.err.println("Error en adaptarContenidoALaAltura: La estructura FXML esperada no coincide.");
            return;
        }
        VBox vBoxPrincipalContenidoScroll = (VBox) mainScrollPane.getContent();
        if (vBoxPrincipalContenidoScroll.getChildren().isEmpty() || !(vBoxPrincipalContenidoScroll.getChildren().get(0) instanceof VBox)) {
            System.err.println("Error en adaptarContenidoALaAltura: No se encontró el VBox del grid.");
            return;
        }
        VBox vBoxContenedorGridYTitulo = (VBox) vBoxPrincipalContenidoScroll.getChildren().get(0);


        Node topNode = mainBorderPane.getTop();
        double topHeight = (topNode != null && topNode.getLayoutBounds().getHeight() > 0) ?
                topNode.getLayoutBounds().getHeight() : HEADER_HEIGHT_ESTIMADA;

        double alturaSeccionTablas = 0;
        if (tablasStackPane != null && tablasStackPane.getScene() != null && tablasStackPane.getParent() != null && tablasStackPane.getParent().isVisible()) {
            alturaSeccionTablas = tablasStackPane.getLayoutBounds().getHeight();
            if (alturaSeccionTablas <= 0 && tablasStackPane.getPrefHeight() > 0 && tablasStackPane.getPrefHeight() != Region.USE_COMPUTED_SIZE) {
                alturaSeccionTablas = tablasStackPane.getPrefHeight();
            } else if (alturaSeccionTablas <= 0) {
                alturaSeccionTablas = TABLAS_SECTION_HEIGHT_ESTIMADA;
            }
            if (vBoxPrincipalContenidoScroll.getChildren().size() > 1) {
                Node vBoxTablasNode = vBoxPrincipalContenidoScroll.getChildren().get(1);
                if (vBoxTablasNode instanceof VBox) {
                    alturaSeccionTablas += ((VBox)vBoxTablasNode).getPadding().getTop() + ((VBox)vBoxTablasNode).getPadding().getBottom();
                }
            }
        } else {
            alturaSeccionTablas = TABLAS_SECTION_HEIGHT_ESTIMADA;
        }

        double paddingVBoxPrincipal = vBoxPrincipalContenidoScroll.getPadding().getTop() + vBoxPrincipalContenidoScroll.getPadding().getBottom();
        double spacingVBoxPrincipal = vBoxPrincipalContenidoScroll.getSpacing();

        double alturaDisponibleParaSeccionGridYTitulo = nuevaAlturaVentana - topHeight - alturaSeccionTablas - paddingVBoxPrincipal - spacingVBoxPrincipal;

        // Aplicar la altura calculada al VBox que contiene el HBox del título del grid y el propio dogGrid
        if (vBoxContenedorGridYTitulo != null) {
            vBoxContenedorGridYTitulo.setPrefHeight(Math.max(MIN_GRID_HEIGHT + HBOX_TITULO_GRID_HEIGHT_ESTIMADA, alturaDisponibleParaSeccionGridYTitulo));
            System.out.println("Ajustando vBoxContenedorGridYTitulo.prefHeight a: " + vBoxContenedorGridYTitulo.getPrefHeight());
        }
        // El dogGrid dentro de este VBox debería usar Vgrow.ALWAYS si quieres que se expanda dentro de él.
        // O puedes calcular la altura específica para dogGrid también si es necesario:
        // double alturaHBoxTituloGrid = HBOX_TITULO_GRID_HEIGHT_ESTIMADA; // Usar la constante
        // double paddingVBoxContenedorGrid = vBoxContenedorGridYTitulo.getPadding().getTop() + vBoxContenedorGridYTitulo.getPadding().getBottom();
        // double spacingVBoxContenedorGrid = vBoxContenedorGridYTitulo.getSpacing();
        // double alturaCalculadaParaDogGrid = alturaDisponibleParaSeccionGridYTitulo - alturaHBoxTituloGrid - paddingVBoxContenedorGrid - spacingVBoxContenedorGrid;
        // if (dogGrid != null) {
        //    dogGrid.setPrefHeight(Math.max(MIN_GRID_HEIGHT, alturaCalculadaParaDogGrid));
        // }
    }

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

    @FXML
    void NuevoPerro(ActionEvent event) {
        System.out.println("Botón Añadir Nuevo Perro presionado.");
        String formularioPerroFxml = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
        String titulo = "Añadir Nuevo Perro";
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formularioPerroFxml));
            Parent root = loader.load();
            FormularioPerroController formController = loader.getController();
            if (formController != null) {
                formController.initDataParaNuevoPerro(this.idProtectoraActual);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error", "Controlador de FormularioPerro no encontrado.");
                return;
            }
            UtilidadesVentana.cambiarEscenaConRoot(root, titulo, false);
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error", "No se pudo abrir el formulario para añadir perro.");
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