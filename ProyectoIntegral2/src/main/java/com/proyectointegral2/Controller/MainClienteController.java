package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.dao.PerroDao;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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

public class MainClienteController {

    @FXML private BorderPane mainBorderPane;
    @FXML private ImageView logoImageView;
    @FXML private TextField searchTextField;
    @FXML private ImageView ImgIconBuscar;
    @FXML private ImageView IconBandeja;
    @FXML private ImageView ImgIconUsuario;
    @FXML private ScrollPane dogScrollPane;
    @FXML private GridPane dogGrid;
    @FXML private Button BtnAdopciones;
    @FXML private Button BtnEventos;
    @FXML private Button BtnReservar;

    private final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.png";
    private List<Perro> listaDePerrosOriginal;
    private List<Perro> perrosMostradosActuales;

    // private PerroDao perroDao; // Descomenta cuando integres el DAO

    private static final double HEADER_HEIGHT_ESTIMADA = 70;
    private static final double SEARCH_BAR_HEIGHT_ESTIMADA = 50; // No se usa en el cálculo de altura actual
    private static final double BOTTOM_BAR_HEIGHT_ESTIMADA = 0;
    private static final double SCROLLPANE_PADDING_VERTICAL = 40;
    private static final double MIN_SCROLLPANE_HEIGHT = 300;
    private static final double PREFERRED_GRID_WIDTH_FALLBACK = 1200;


    @FXML
    public void initialize() {
        System.out.println("MainClienteController inicializado.");
        cargarDatosDePerrosOriginalesSimulados();
        this.perrosMostradosActuales = new ArrayList<>(this.listaDePerrosOriginal); // Copia a la lista que se mostrará

        configurarListenersDeVentana();
        configurarPlaceholderSearchTextField();
        configurarListenersDeVentana();

        // Forzar una adaptación inicial después de que el layout se haya completado
        // Esto es CLAVE para la carga inicial.
        Platform.runLater(() -> {
            if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null) {
                Stage stage = (Stage) mainBorderPane.getScene().getWindow();
                if (stage.getWidth() > 0 && !Double.isNaN(stage.getWidth())) {
                    System.out.println("Forzando adaptación inicial en initialize() con Platform.runLater. Ancho: " + stage.getWidth());
                    adaptarUIAlTamanoVentana(stage);
                } else {
                    // Si el ancho aún no está listo, el listener de setOnShown debería tomarlo.
                    // O podemos añadir un listener a showingProperty.
                    System.out.println("WARN: Ancho de stage no disponible inmediatamente en initialize(). Esperando a setOnShown/listeners.");
                }
            } else {
                System.out.println("WARN: Escena o ventana no disponible en initialize() para forzar adaptación.");
            }
        });
    }


    private void cargarYMostrarPerros() {
        // --- SIMULACIÓN POR AHORA ---
        cargarDatosDePerrosOriginalesSimulados();
        // --- FIN SIMULACIÓN ---

        // --- CUANDO USES EL DAO ---
        /*
        if (perroDao != null) {
            try {
                this.listaDePerrosOriginal = perroDao.obtenerTodosLosPerrosDisponibles();
                if (this.listaDePerrosOriginal == null) {
                    this.listaDePerrosOriginal = new ArrayList<>();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "No se pudieron cargar los perros.");
                this.listaDePerrosOriginal = new ArrayList<>();
            }
        } else {
             this.listaDePerrosOriginal = new ArrayList<>(); // Si perroDao no se pudo inicializar
             System.err.println("PerroDao no inicializado, cargando lista vacía.");
        }
        */

        this.perrosMostradosActuales = new ArrayList<>(this.listaDePerrosOriginal);
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

                            if (!stage.isShowing()) { // Solo añadir setOnShown si no se está mostrando ya
                                stage.setOnShown(event -> {
                                    Platform.runLater(() -> {
                                        System.out.println("Stage.setOnShown, adaptando UI. Ancho: " + stage.getWidth());
                                        adaptarUIAlTamanoVentana(stage);
                                    });
                                });
                            } else {
                                Platform.runLater(() -> {
                                    System.out.println("Stage ya mostrado al añadir listener, adaptando UI. Ancho: " + stage.getWidth());
                                    adaptarUIAlTamanoVentana(stage);
                                });
                            }


                            stage.widthProperty().addListener((obs, o, n) -> Platform.runLater(() -> {
                                System.out.println("Listener Ancho: " + stage.getWidth());
                                adaptarUIAlTamanoVentana(stage);
                            }));
                            stage.heightProperty().addListener((obs, o, n) -> Platform.runLater(() -> {
                                System.out.println("Listener Alto: " + stage.getHeight());
                                adaptarUIAlTamanoVentana(stage);
                            }));
                            stage.maximizedProperty().addListener((obs, o, n) -> Platform.runLater(() -> {
                                System.out.println("Listener Maximized: " + stage.isMaximized() + ", Ancho: " + stage.getWidth());
                                adaptarUIAlTamanoVentana(stage);
                            }));
                        }
                    });
                }
            });
        }
    }

    private void adaptarUIAlTamanoVentana(Stage stage) {
        if (stage == null || stage.getWidth() <= 0 || Double.isNaN(stage.getWidth())) {
            System.out.println("WARN: adaptarUIAlTamanoVentana llamado con stage nulo o ancho inválido.");
            return;
        }
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        System.out.println("Adaptando UI a: " + currentWidth + "x" + currentHeight + ", Maximized: " + stage.isMaximized());

        adaptarContenidoAlAnchoYPopular(currentWidth); // Esto configura columnas y llama a popularGridDePerros
        adaptarContenidoALaAltura(currentHeight);      // Esto ajusta la altura del scrollpane
    }


    private void configurarPlaceholderSearchTextField() {
        if (searchTextField != null) {
            searchTextField.setPromptText("Buscar por nombre o raza...");
        }
    }

    private void configurarAccionIconoBusqueda() {
        if (ImgIconBuscar != null) {
            ImgIconBuscar.setOnMouseClicked(event -> onSearchIconClicked(event));
        }
    }

    private void cargarDatosDePerrosOriginalesSimulados() {
        listaDePerrosOriginal = new ArrayList<>();
        Raza labrador = new Raza(1, "Labrador Retriever");
        Raza husky = new Raza(2, "Siberian Husky");
        Raza golden = new Raza(3, "Golden Retriever");
        // Añade el idProtectora (8º argumento)
        listaDePerrosOriginal.add(new Perro(101, "Buddy", "/assets/Imagenes/perros/buddy_labrador.jpg", LocalDate.of(2022, 3, 15), "Macho", false, labrador, 1));
        listaDePerrosOriginal.add(new Perro(102, "Kira", "/assets/Imagenes/perros/kira_husky.png", LocalDate.of(2021, 11, 1), "Hembra", false, husky, 1));
        listaDePerrosOriginal.add(new Perro(103, "Goldie", "/assets/Imagenes/perros/goldie_golden.jpg", LocalDate.of(2023, 1, 20), "Macho", false, golden, 2));
        System.out.println("Datos simulados cargados. Total perros originales: " + listaDePerrosOriginal.size());
    }



    // He copiado y adaptado los métodos relevantes de la respuesta anterior:
    private VBox crearTarjetaPerro(Perro perro) { // Ahora usa tu modelo Perro
        VBox card = new VBox(5);
        card.setPrefWidth(200);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2); -fx-padding: 10;");
        card.setMinHeight(240);


        // --- Contenedor para la Imagen con Tamaño Fijo/Preferido ---
        // Usaremos un StackPane para centrar la imagen si es más pequeña que el área
        StackPane imageContainer = new StackPane();
        double imageContainerWidth = 180;  // Ancho del área para la imagen (un poco menos que la tarjeta)
        double imageContainerHeight = 140; // Altura del área para la imagen
        imageContainer.setPrefSize(imageContainerWidth, imageContainerHeight);
        imageContainer.setMaxSize(imageContainerWidth, imageContainerHeight);
        imageContainer.setMinSize(imageContainerWidth, imageContainerHeight);
        // Opcional: un color de fondo para ver el área de la imagen
        // imageContainer.setStyle("-fx-background-color: lightgrey;");

        ImageView imgView = new ImageView();
        String imagePath = perro.getFoto();
        try {
            Image loadedImage = null;
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                if (!imagePath.startsWith("/")) { imagePath = "/" + imagePath; }
                InputStream stream = getClass().getResourceAsStream(imagePath);
                if (stream != null) {
                    loadedImage = new Image(stream);
                } else {
                    System.err.println("WARN: No se encontró imagen en " + imagePath + " para " + perro.getNombre() + ". Usando placeholder.");
                }
            }
            if (loadedImage == null || loadedImage.isError()) { // Si falló la carga o no había ruta
            imgView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO))));
        } else {
            imgView.setImage(loadedImage);
        }

    } catch (Exception e) {
        System.err.println("ERROR cargando imagen para " + perro.getNombre() + ": " + imagePath + ". " + e.getMessage());
        try {
            imgView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO))));
        } catch (Exception ex) { /* Falló placeholder */ }
    }
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        imgView.setCache(true);
        imgView.setFitWidth(imageContainerWidth);
        imgView.setFitHeight(imageContainerHeight);
        imageContainer.getChildren().add(imgView); // Añadir ImageView al contenedor
        VBox.setMargin(imageContainer, new Insets(0,0,8,0)); // Margen inferior para el contenedor de imagen

        Label lblNombrePerro = new Label(perro.getNombre());
        lblNombrePerro.setStyle("-fx-background-color: #C8E6C9; -fx-padding: 5 10 5 10; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 14px;");
        lblNombrePerro.setAlignment(Pos.CENTER);
        lblNombrePerro.setWrapText(true);
        lblNombrePerro.setPrefWidth(180);
        lblNombrePerro.setMaxWidth(180);

        Button btnAccionTarjeta = new Button();
        VBox.setMargin(btnAccionTarjeta, new Insets(8,0,0,0));
        if (perro.isAdoptado()) {
            btnAccionTarjeta.setText("Adoptado");
            btnAccionTarjeta.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-font-size:12px; -fx-padding: 5 10;");
            btnAccionTarjeta.setDisable(true);
        } else {
            btnAccionTarjeta.setText("Ver Más");
            btnAccionTarjeta.setStyle("-fx-background-color: #E67E22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-font-size:12px; -fx-padding: 5 10;");
            btnAccionTarjeta.setOnAction(event -> handleVerMas(perro));
        }

        card.getChildren().addAll(imageContainer, lblNombrePerro, btnAccionTarjeta); // Añadir imageContainer en lugar de imgView directamente
        card.setUserData(perro);
        if (!perro.isAdoptado()) {
            card.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    handleVerMas(perro);
                }
            });
            card.setStyle(card.getStyle() + "; -fx-cursor: hand;");
        }
        return card;
    }


    private void handleVerMas(Perro perro) { // Ahora usa tu modelo Perro
        System.out.println("Ver más sobre (pop-up): " + perro.getNombre());
        String detallesFxml = "/com/proyectointegral2/Vista/DetallesPerro.fxml";
        String titulo = "Detalles de " + perro.getNombre();
        Stage ownerStage = (Stage) mainBorderPane.getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(detallesFxml));
            Parent root = loader.load();
            DetallesPerroController controller = loader.getController();
            if (controller != null) {
                controller.initData(perro); // Pasa el objeto Perro
            } else { /* ... error ... */ return; }
            UtilidadesVentana.mostrarVentanaComoDialogo(root, titulo, ownerStage);
        } catch (IOException e) { /* ... error ... */ e.printStackTrace(); }
    }

    private int calcularColumnasSegunAncho(double anchoVentana) {
        if (anchoVentana <= 0 || Double.isNaN(anchoVentana)) return 3;
        double anchoTarjetaConEspacio = 180 + (dogGrid.getHgap() > 0 ? dogGrid.getHgap() : 20) + 20;
        int numColumnas = Math.max(1, (int) (anchoVentana / anchoTarjetaConEspacio));
        return Math.min(numColumnas, 5);
    }

    private void adaptarContenidoAlAnchoYPopular(double nuevoAncho) {
        if (dogGrid == null || listaDePerrosOriginal == null) { // Chequea listaDePerrosOriginal también
            System.err.println("ERROR: dogGrid o listaDePerrosOriginal es null en adaptarContenidoAlAnchoYPopular.");
            return;
        }
        System.out.println("Adaptando contenido al ancho: " + nuevoAncho);

        int nuevasColumnas = calcularColumnasSegunAncho(nuevoAncho);

        // Solo reconfigurar columnas y repopular si el número de columnas cambió,
        // o si el grid está vacío y hay perros para mostrar,
        // o si la cantidad de perros a mostrar cambió (ej. por filtro).
        boolean necesitaReconfigurarColumnas = dogGrid.getColumnConstraints().size() != nuevasColumnas;
        boolean gridVacioConDatos = dogGrid.getChildren().isEmpty() && (perrosMostradosActuales != null && !perrosMostradosActuales.isEmpty());
        boolean cantidadPerrosCambio = perrosMostradosActuales != null && dogGrid.getChildren().size() != perrosMostradosActuales.size();

        if (necesitaReconfigurarColumnas || gridVacioConDatos || cantidadPerrosCambio) {
            if (necesitaReconfigurarColumnas) {
                dogGrid.getColumnConstraints().clear();
                for (int i = 0; i < nuevasColumnas; i++) {
                    javafx.scene.layout.ColumnConstraints colConst = new javafx.scene.layout.ColumnConstraints();
                    colConst.setPercentWidth(100.0 / nuevasColumnas);
                    colConst.setHgrow(javafx.scene.layout.Priority.ALWAYS);
                    dogGrid.getColumnConstraints().add(colConst);
                }
                System.out.println("Columnas reconfiguradas a: " + nuevasColumnas);
            }
            popularGridDePerros(); // Llama a popular siempre que haya un cambio que lo requiera
        } else {
            System.out.println("No se necesita repopular/reconfigurar columnas. Columnas Actuales: " + dogGrid.getColumnConstraints().size() + ", Perros en Grid: " + dogGrid.getChildren().size() + ", Perros a Mostrar: " + (perrosMostradosActuales != null ? perrosMostradosActuales.size() : "null"));
        }
    }

    private void adaptarContenidoALaAltura(double nuevaAltura) {
        System.out.println("Adaptar contenido a la altura: " + nuevaAltura);
        if (dogScrollPane != null && mainBorderPane != null) {
            Node topNode = mainBorderPane.getTop();
            double topHeight = (topNode != null && topNode.getLayoutBounds().getHeight() > 0) ? topNode.getLayoutBounds().getHeight() : HEADER_HEIGHT_ESTIMADA;
            Node centerNode = mainBorderPane.getCenter(); // El VBox que contiene HBox de botones y ScrollPane
            double vBoxPadding = 0;
            double hBoxBotonesHeight = 0;
            if(centerNode instanceof VBox){
                VBox centerVBox = (VBox) centerNode;
                vBoxPadding = centerVBox.getPadding().getTop() + centerVBox.getPadding().getBottom();
                if(!centerVBox.getChildren().isEmpty() && centerVBox.getChildren().get(0) instanceof javafx.scene.layout.HBox){
                    hBoxBotonesHeight = ((javafx.scene.layout.HBox)centerVBox.getChildren().get(0)).getHeight();
                    if (hBoxBotonesHeight <=0 ) hBoxBotonesHeight = 40; // Fallback si no está renderizado
                }
            }

            double alturaDisponibleParaScroll = nuevaAltura - topHeight - hBoxBotonesHeight - vBoxPadding - BOTTOM_BAR_HEIGHT_ESTIMADA - SCROLLPANE_PADDING_VERTICAL;
            dogScrollPane.setPrefHeight(Math.max(MIN_SCROLLPANE_HEIGHT, alturaDisponibleParaScroll));
        }
    }

    private void popularGridDePerros() {
        if (dogGrid == null || perrosMostradosActuales == null) { // CAMBIO: Chequea perrosMostradosActuales
            System.err.println("dogGrid o perrosMostradosActuales es null. No se puede popular.");
            return;
        }
        dogGrid.getChildren().clear();
        int numColumnas = dogGrid.getColumnConstraints().size();
        if (numColumnas == 0) {
            System.out.println("WARN: popularGridDePerros llamado sin columnas. Aplicando 3 por defecto.");
            if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null) {
                Stage stage = (Stage) mainBorderPane.getScene().getWindow();
                if (stage.getProperties().get("FIRST_ADAPT_ATTEMPTED") == null) {
                    stage.getProperties().put("FIRST_ADAPT_ATTEMPTED", true);
                    adaptarContenidoAlAnchoYPopular(stage.getWidth() > 0 ? stage.getWidth() : PREFERRED_GRID_WIDTH_FALLBACK);
                } else {
                    System.err.println("ERROR: Falló el intento de adaptar columnas. Grid no se poblará.");
                }
            }
            return;
        }

        System.out.println("Populando dogGrid con " + perrosMostradosActuales.size() + " perros en " + numColumnas + " columnas.");
        int columnaActual = 0;
        int filaActual = 0;
        for (Perro perro : perrosMostradosActuales) { // Usa perrosMostradosActuales
            VBox tarjetaPerro = crearTarjetaPerro(perro);
            dogGrid.add(tarjetaPerro, columnaActual, filaActual);
            columnaActual++;
            if (columnaActual >= numColumnas) {
                columnaActual = 0;
                filaActual++;
            }
        }
    }

    @FXML void Reservar(ActionEvent event) { System.out.println("Botón Reservar presionado"); }
    @FXML void Adopciones(ActionEvent event) { System.out.println("Botón Adopciones presionado"); }
    @FXML void Eventos(ActionEvent event) { System.out.println("Botón Eventos presionado"); }

    @FXML
    void Bandeja(MouseEvent event) {
        // ... (igual) ...
        System.out.println("Icono Bandeja presionado");
        UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/BandejaCitasView.fxml", "Bandeja de Citas", true);
    }

    @FXML
    void DetallesUsuario(MouseEvent event) {
        // ... (igual) ...
        System.out.println("Icono Usuario presionado - Navegando a Perfil");
        int idUsuarioActual = obtenerIdSimuladoDelUsuario();
        String nombreUsuarioLogin = obtenerNombreUsuarioSimuladoDelLogin();

        if (idUsuarioActual > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectointegral2/Vista/PerfilUsuarioView.fxml"));
                Parent root = loader.load();
                PerfilUsuarioController perfilController = loader.getController();
                if (perfilController != null) {
                    perfilController.initData(idUsuarioActual, nombreUsuarioLogin);
                    UtilidadesVentana.cambiarEscenaConRoot(root, "Mi Perfil", false);
                } else { UtilidadesVentana.mostrarAlertaError("Error", "No se pudo cargar el controlador del perfil.");}
            } catch (IOException e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir la vista del perfil.");}
        } else { UtilidadesVentana.mostrarAlertaError("Error de Sesión", "No se pudo identificar el usuario actual.");}
    }

    private int obtenerIdSimuladoDelUsuario() { return 1; }
    private String obtenerNombreUsuarioSimuladoDelLogin() { return "usuario_cliente_test"; }

    @FXML
    void onSearchIconClicked(MouseEvent event) {
        filtrarYRepopularPerros(searchTextField.getText());
    }

    @FXML
    void onSearchTextFieldAction(ActionEvent event) {
        filtrarYRepopularPerros(searchTextField.getText());
    }

    private void filtrarYRepopularPerros(String textoBusqueda) {
        if (listaDePerrosOriginal == null) {
            cargarDatosDePerrosOriginalesSimulados(); // Asegurar que la lista original exista
        }
        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            this.perrosMostradosActuales = new ArrayList<>(this.listaDePerrosOriginal);
        } else {
            String textoBusquedaLower = textoBusqueda.toLowerCase().trim();
            this.perrosMostradosActuales = this.listaDePerrosOriginal.stream()
                    .filter(perro -> perro.getNombre().toLowerCase().contains(textoBusquedaLower) ||
                            (perro.getRaza() != null && perro.getRaza().getNombre().toLowerCase().contains(textoBusquedaLower)))
                    .collect(Collectors.toList());
        }
        // Forzar la adaptación del grid que a su vez llamará a popularGridDePerros
        if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null) {
            adaptarContenidoAlAnchoYPopular(mainBorderPane.getScene().getWindow().getWidth());
        } else {
            // Si la ventana no está lista, es más difícil saber el ancho correcto
            // Podríamos intentar popular con un número de columnas por defecto.
            popularGridDePerros(); // Esto podría usar un número de columnas incorrecto si no se han seteado
            System.out.println("WARN: Filtrando perros pero la ventana no está completamente lista para determinar ancho de columnas.");
        }
    }
}