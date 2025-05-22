package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.SesionUsuario;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.PerroDao;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador principal para la vista del cliente.
 * Muestra una lista de perros disponibles, permite filtrarlos y navegar
 * a otras secciones como detalles del perro, perfil de usuario, bandeja de citas,
 * panel de adopciones y panel de eventos.
 */
public class MainClienteController {

    // --- Constantes ---
    private static final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.jpg";
    private static final String CRITERIO_BUSQUEDA_NOMBRE = "Nombre";
    private static final String CRITERIO_BUSQUEDA_RAZA = "Raza";

    // Rutas FXML
    private static final String FXML_LOGIN = "/com/proyectointegral2/Vista/Login.fxml";
    private static final String FXML_DETALLES_PERRO = "/com/proyectointegral2/Vista/DetallesPerro.fxml";
    private static final String FXML_ADOPCIONES_PANEL = "/com/proyectointegral2/Vista/AdopcionesPanel.fxml";
    private static final String FXML_EVENTOS_PANEL = "/com/proyectointegral2/Vista/EventosPanel.fxml";
    private static final String FXML_BANDEJA_CITAS = "/com/proyectointegral2/Vista/BandejaCitas.fxml";
    private static final String FXML_PERFIL_USUARIO = "/com/proyectointegral2/Vista/PerfilUsuario.fxml";
    private static final String FXML_MAIN_CLIENTE = "/com/proyectointegral2/Vista/Main.fxml";

    // Constantes para UI responsiva (estimaciones)
    private static final double HEADER_HEIGHT_ESTIMADA = 70.0;
    private static final double BOTTOM_BAR_HEIGHT_ESTIMADA = 80.0;
    private static final double MIN_SCROLLPANE_HEIGHT = 300.0;
    private static final double TARJETA_PREF_WIDTH = 190.0;
    private static final double TARJETA_IMG_AREA_WIDTH = 160.0;
    private static final double TARJETA_IMG_AREA_HEIGHT = 160.0;
    private static final double CARD_HORIZONTAL_GAP = 20.0;
    private static final double CARD_INTERNAL_PADDING = 10.0;
    private static final int MAX_COLUMNAS_GRID = 5;

    // --- Componentes FXML ---
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
    @FXML private ComboBox<String> comboCriterioBusqueda;

    // --- Listas de Datos ---
    private List<Perro> listaDePerrosOriginal;
    private List<Perro> perrosMostradosActuales;

    // --- DAOs ---
    private PerroDao perroDao;

    /**
     * Método de inicialización del controlador. Se llama después de que los campos FXML han sido inyectados.
     * Configura la sesión, carga datos iniciales, y establece listeners para filtros y responsividad.
     */
    @FXML
    public void initialize() {
        System.out.println("MainClienteController inicializado.");
        if (!verificarSesionUsuario()) return;

        this.perroDao = new PerroDao();
        this.listaDePerrosOriginal = new ArrayList<>();
        this.perrosMostradosActuales = new ArrayList<>();

        cargarPerrosDesdeBaseDeDatos();
        actualizarPerrosMostrados(this.listaDePerrosOriginal);

        configurarFiltros();
        configurarListenersDeVentana();
        configurarPlaceholderSearchTextField();
        configurarAccionIconoBusqueda();

        Platform.runLater(() -> {
            if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null) {
                Stage stage = (Stage) mainBorderPane.getScene().getWindow();
                if (stage.isShowing() && stage.getWidth() > 0 && !Double.isNaN(stage.getWidth())) {
                    adaptarUIAlTamanoVentana(stage);
                } else if (!stage.isShowing()) {
                    stage.setOnShown(event -> Platform.runLater(() -> adaptarUIAlTamanoVentana(stage)));
                }
            } else {
                mainBorderPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null && newScene.getWindow() != null) {
                        Platform.runLater(() -> adaptarUIAlTamanoVentana((Stage)newScene.getWindow()));
                    }
                });
            }
        });
    }

    /**
     * Verifica si hay un usuario logueado en la sesión actual.
     * Si no hay usuario, muestra una alerta y redirige al login.
     * @return true si hay un usuario logueado, false en caso contrario.
     */
    private boolean verificarSesionUsuario() {
        Usuario usuarioActual = SesionUsuario.getUsuarioLogueado();
        if (usuarioActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sesión", "No hay usuario logueado. Volviendo al login.");
            UtilidadesVentana.cambiarEscena(FXML_LOGIN, "Inicio de Sesión", false);
            return false;
        }
        System.out.println("Bienvenido a MainCliente: " + usuarioActual.getNombreUsu());
        return true;
    }

    /**
     * Configura los listeners para el ComboBox de criterio de búsqueda y el TextField de búsqueda.
     * Cualquier cambio en estos campos disparará el filtrado y repopulación de perros.
     */
    private void configurarFiltros() {
        if (comboCriterioBusqueda != null) {
            comboCriterioBusqueda.getItems().setAll(CRITERIO_BUSQUEDA_NOMBRE, CRITERIO_BUSQUEDA_RAZA);
            comboCriterioBusqueda.getSelectionModel().selectFirst(); // Seleccionar "Nombre" por defecto
            comboCriterioBusqueda.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltrosActuales()
            );
        }
        if (searchTextField != null) {
            searchTextField.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltrosActuales()
            );
        }
    }

    /**
     * Carga la lista de todos los perros desde la base de datos
     * y la almacena en `listaDePerrosOriginal`.
     * Muestra una alerta en caso de error.
     */
    private void cargarPerrosDesdeBaseDeDatos() {
        try {
            List<Perro> perros = perroDao.obtenerTodosLosPerros();
            this.listaDePerrosOriginal = (perros != null) ? perros : new ArrayList<>();
            System.out.println("Perros cargados desde la base de datos: " + this.listaDePerrosOriginal.size());
        } catch (SQLException e) {
            System.err.println("Error al cargar perros desde la base de datos: " + e.getMessage());
            e.printStackTrace();
            this.listaDePerrosOriginal = new ArrayList<>();
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "No se pudieron cargar los perros: " + e.getMessage());
        }
    }

    /**
     * Actualiza la lista de `perrosMostradosActuales` con una nueva lista
     * y repopula el grid de perros.
     * @param nuevaListaDePerros La nueva lista de perros a mostrar.
     */
    private void actualizarPerrosMostrados(List<Perro> nuevaListaDePerros) {
        this.perrosMostradosActuales = (nuevaListaDePerros != null) ? new ArrayList<>(nuevaListaDePerros) : new ArrayList<>();
        if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null && mainBorderPane.getScene().getWindow().getWidth() > 0) {
            adaptarContenidoAlAnchoYPopular(mainBorderPane.getScene().getWindow().getWidth());
        } else {
            popularGridDePerros();
        }
    }

    /**
     * Filtra `listaDePerrosOriginal` basado en el texto de búsqueda y el criterio seleccionado,
     * actualizando `perrosMostradosActuales` y repopulando el grid.
     */
    private void aplicarFiltrosActuales() {
        if (listaDePerrosOriginal == null) {
            UtilidadesVentana.mostrarAlertaError("Error Datos", "No hay datos de perros para filtrar.");
            return;
        }

        String textoBusqueda = (searchTextField != null) ? searchTextField.getText() : "";
        String textoBusquedaTrim = textoBusqueda.trim().toLowerCase();
        String criterio = (comboCriterioBusqueda != null && comboCriterioBusqueda.getValue() != null) ? comboCriterioBusqueda.getValue() : CRITERIO_BUSQUEDA_NOMBRE;

        List<Perro> perrosFiltrados;
        if (textoBusquedaTrim.isEmpty()) {
            perrosFiltrados = new ArrayList<>(this.listaDePerrosOriginal);
        } else {
            perrosFiltrados = this.listaDePerrosOriginal.stream().filter(perro -> {
                if (perro == null) return false;
                switch (criterio) {
                    case CRITERIO_BUSQUEDA_RAZA:
                        return perro.getRaza() != null && perro.getRaza().getNombreRaza() != null && perro.getRaza().getNombreRaza().toLowerCase().startsWith(textoBusquedaTrim);
                    case CRITERIO_BUSQUEDA_NOMBRE:
                    default:
                        return perro.getNombre() != null && perro.getNombre().toLowerCase().startsWith(textoBusquedaTrim);
                }
            }).collect(Collectors.toList());
        }
        actualizarPerrosMostrados(perrosFiltrados);
    }


    // --- Métodos para UI Responsiva y Popular Grid ---

    /**
     * Configura listeners en la ventana para adaptar la UI cuando cambia su tamaño o estado.
     */
    private void configurarListenersDeVentana() {
        if (mainBorderPane == null) return;

        mainBorderPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow instanceof Stage) {
                        Stage stage = (Stage) newWindow;

                        if (stage.isShowing()) {
                            Platform.runLater(() -> adaptarUIAlTamanoVentana(stage));
                        } else {

                            stage.setOnShown(event -> Platform.runLater(() -> adaptarUIAlTamanoVentana(stage)));
                        }

                        stage.widthProperty().addListener((obs, o, n) -> Platform.runLater(() -> adaptarUIAlTamanoVentana(stage)));
                        stage.heightProperty().addListener((obs, o, n) -> Platform.runLater(() -> adaptarUIAlTamanoVentana(stage)));
                        stage.maximizedProperty().addListener((obs, o, n) -> Platform.runLater(() -> adaptarUIAlTamanoVentana(stage)));
                    }
                });
            }
        });
    }

    /**
     * Adapta la UI (columnas del grid, altura del scrollpane) al tamaño actual de la ventana.
     * @param stage La ventana principal (Stage).
     */
    private void adaptarUIAlTamanoVentana(Stage stage) {
        if (stage == null || stage.getWidth() <= 0 || Double.isNaN(stage.getWidth()) || Double.isNaN(stage.getHeight())) {
            return;
        }
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();

        adaptarContenidoAlAnchoYPopular(currentWidth);
        adaptarContenidoALaAltura(currentHeight);
    }

    /**
     * Adapta el número de columnas del GridPane y repopula con tarjetas de perros
     * basándose en el ancho disponible de la ventana.
     * @param anchoVentana El ancho actual de la ventana.
     */
    private void adaptarContenidoAlAnchoYPopular(double anchoVentana) {
        if (dogGrid == null || mainBorderPane == null) return;

        double paddingLateralTotalVBox = 0;
        if (mainBorderPane.getCenter() instanceof VBox) {
            VBox centerVBox = (VBox) mainBorderPane.getCenter();
            paddingLateralTotalVBox = centerVBox.getPadding().getLeft() + centerVBox.getPadding().getRight();
        } else {
            paddingLateralTotalVBox = mainBorderPane.getPadding().getLeft() + mainBorderPane.getPadding().getRight();
        }

        double paddingLateralScrollPane = (dogScrollPane != null && dogScrollPane.getPadding() != null)
                ? dogScrollPane.getPadding().getLeft() + dogScrollPane.getPadding().getRight() : 0;
        double paddingLateralGrid = (dogGrid.getPadding() != null) ? dogGrid.getPadding().getLeft() + dogGrid.getPadding().getRight() : 0;

        double espacioAdicionalConsiderado = 20.0;

        double anchoDisponibleParaGrid = anchoVentana - paddingLateralTotalVBox - paddingLateralScrollPane - paddingLateralGrid - espacioAdicionalConsiderado;
        int nuevasColumnas = calcularColumnasSegunAncho(anchoDisponibleParaGrid);

        boolean necesitaRepopularPorCambioLista = (perrosMostradosActuales != null && dogGrid.getChildren().size() != perrosMostradosActuales.size());
        boolean necesitaRepopularPorGridVacio = dogGrid.getChildren().isEmpty() && perrosMostradosActuales != null && !perrosMostradosActuales.isEmpty();

        if (dogGrid.getColumnConstraints().size() != nuevasColumnas || necesitaRepopularPorCambioLista || necesitaRepopularPorGridVacio) {
            if (dogGrid.getColumnConstraints().size() != nuevasColumnas) {
                dogGrid.getColumnConstraints().clear();
                for (int i = 0; i < nuevasColumnas; i++) {
                    ColumnConstraints colConst = new ColumnConstraints();
                    colConst.setPercentWidth(100.0 / nuevasColumnas);
                    colConst.setHgrow(Priority.SOMETIMES);
                    dogGrid.getColumnConstraints().add(colConst);
                }
            }
            popularGridDePerros();
        }
    }

    /**
     * Calcula el número óptimo de columnas para el GridPane basado en el ancho disponible.
     * @param anchoGridDisponible El ancho neto disponible para el contenido del GridPane.
     * @return El número de columnas, entre 1 y MAX_COLUMNAS_GRID.
     */
    private int calcularColumnasSegunAncho(double anchoGridDisponible) {
        if (anchoGridDisponible <= 0) return 1;
        double anchoTarjetaEstimado = TARJETA_PREF_WIDTH + CARD_HORIZONTAL_GAP;
        int numColumnas = Math.max(1, (int) (anchoGridDisponible / anchoTarjetaEstimado));
        return Math.min(numColumnas, MAX_COLUMNAS_GRID);
    }

    /**
     * Adapta la altura del ScrollPane que contiene el grid de perros.
     * @param nuevaAlturaVentana La altura actual de la ventana.
     */
    private void adaptarContenidoALaAltura(double nuevaAlturaVentana) {
        if (dogScrollPane == null || mainBorderPane == null || !(mainBorderPane.getCenter() instanceof VBox)) {
            return;
        }

        Node topNode = mainBorderPane.getTop();
        VBox centerVBox = (VBox) mainBorderPane.getCenter();
        Node bottomNode = mainBorderPane.getBottom();

        double topHeight = (topNode != null && topNode.getBoundsInParent().getHeight() > 0)
                ? topNode.getBoundsInParent().getHeight() : HEADER_HEIGHT_ESTIMADA;
        double bottomHeight = (bottomNode != null && bottomNode.getBoundsInParent().getHeight() > 0)
                ? bottomNode.getBoundsInParent().getHeight() : BOTTOM_BAR_HEIGHT_ESTIMADA;

        double vBoxPaddingVertical = centerVBox.getPadding().getTop() + centerVBox.getPadding().getBottom();
        double vBoxSpacing = centerVBox.getSpacing();

        double alturaOtrosElementosEnVBox = 0;
        for (Node child : centerVBox.getChildren()) {
            if (child != dogScrollPane && child.isVisible()) {
                alturaOtrosElementosEnVBox += child.getBoundsInParent().getHeight();
                if (centerVBox.getChildren().indexOf(child) < centerVBox.getChildren().size() -1) {
                    alturaOtrosElementosEnVBox += vBoxSpacing;
                }
            }
        }

        double margenAdicional = 20.0;

        double alturaDisponibleParaScroll = nuevaAlturaVentana - topHeight - bottomHeight - vBoxPaddingVertical - alturaOtrosElementosEnVBox - margenAdicional;
        dogScrollPane.setPrefHeight(Math.max(MIN_SCROLLPANE_HEIGHT, alturaDisponibleParaScroll));
    }

    /**
     * Limpia el GridPane y lo llena con tarjetas de perro creadas a partir de `perrosMostradosActuales`.
     */
    private void popularGridDePerros() {
        if (dogGrid == null || perrosMostradosActuales == null) {
            System.err.println("Error: dogGrid o perrosMostradosActuales es null en popularGridDePerros.");
            return;
        }
        dogGrid.getChildren().clear();
        int numColumnas = Math.max(1, dogGrid.getColumnConstraints().size());
        int columnaActual = 0;
        int filaActual = 0;

        for (Perro perro : perrosMostradosActuales) {
            VBox tarjetaPerro = crearTarjetaPerro(perro);
            dogGrid.add(tarjetaPerro, columnaActual, filaActual);
            columnaActual++;
            if (columnaActual >= numColumnas) {
                columnaActual = 0;
                filaActual++;
            }
        }
    }

    /**
     * Crea un VBox (tarjeta) visualmente representando un perro.
     * Incluye imagen, nombre y un botón de acción ("Ver Más" o "Adoptado").
     * @param perro El objeto Perro para el cual crear la tarjeta.
     * @return Un VBox configurado como una tarjeta de perro.
     */
    private VBox crearTarjetaPerro(Perro perro) {
        VBox card = new VBox(5);
        card.setPrefWidth(TARJETA_PREF_WIDTH);
        card.setMaxWidth(TARJETA_PREF_WIDTH);
        card.setMinWidth(TARJETA_PREF_WIDTH);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(String.format(
                "-fx-background-color: white; -fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 8; -fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2); " +
                        "-fx-padding: %f;", CARD_INTERNAL_PADDING
        ));
        card.setMinHeight(240);

        // Contenedor para la imagen
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);
        imageContainer.setMinSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);
        imageContainer.setMaxSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);

        ImageView imgView = new ImageView();
        cargarImagenPerroConPlaceholder(imgView, perro);

        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        imgView.setCache(true);

        imgView.setFitWidth(TARJETA_IMG_AREA_WIDTH);
        imgView.setFitHeight(TARJETA_IMG_AREA_HEIGHT);
        imgView.fitWidthProperty().bind(imageContainer.widthProperty());
        imgView.fitHeightProperty().bind(imageContainer.heightProperty());

        imageContainer.getChildren().add(imgView);
        VBox.setMargin(imageContainer, new Insets(0, 0, 8, 0));

        // Nombre del perro
        Label lblNombrePerro = new Label(perro.getNombre() != null ? perro.getNombre() : "Sin Nombre");
        lblNombrePerro.setStyle("-fx-background-color: #C8E6C9; -fx-padding: 5 10 5 10; " + "-fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 14px;"
        );
        lblNombrePerro.setAlignment(Pos.CENTER);
        lblNombrePerro.setWrapText(false); // Evitar múltiples líneas
        lblNombrePerro.setPrefWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setMaxWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setTextOverrun(OverrunStyle.ELLIPSIS);

        // Botón de acción
        Button btnAccionTarjeta = new Button();
        VBox.setMargin(btnAccionTarjeta, new Insets(8, 0, 0, 0));

        if (perro.isAdoptado()) {
            btnAccionTarjeta.setText("Adoptado");
            btnAccionTarjeta.setStyle(
                    "-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; " +
                            "-fx-background-radius: 5; -fx-font-size:12px; -fx-padding: 5 10;"
            );
            btnAccionTarjeta.setDisable(true);
        } else {
            btnAccionTarjeta.setText("Ver Más");
            btnAccionTarjeta.setStyle(
                    "-fx-background-color: #E67E22; -fx-text-fill: white; -fx-font-weight: bold; " +
                            "-fx-background-radius: 5; -fx-font-size:12px; -fx-padding: 5 10;"
            );
            btnAccionTarjeta.setOnAction(event -> handleVerMas(perro));
        }
        btnAccionTarjeta.setPrefWidth(TARJETA_IMG_AREA_WIDTH * 0.8);

        card.getChildren().addAll(imageContainer, lblNombrePerro, btnAccionTarjeta);
        card.setUserData(perro);

        // Hacer toda la tarjeta (excepto el botón) clickeable si el perro no está adoptado
        if (!perro.isAdoptado()) {
            card.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    Node clickedNode = event.getPickResult().getIntersectedNode();
                    if (!isNodeOrItsParentAButton(clickedNode, btnAccionTarjeta)) {
                        handleVerMas(perro);
                    }
                }
            });
            card.setStyle(card.getStyle() + "; -fx-cursor: hand;");
        }
        return card;
    }

    /**
     * Carga la imagen del perro en el ImageView. Si la imagen no se encuentra o hay un error,
     * carga una imagen de placeholder.
     * @param imageView El ImageView donde cargar la imagen.
     * @param perro El perro del cual obtener la ruta de la imagen.
     */
    private void cargarImagenPerroConPlaceholder(ImageView imageView, Perro perro) {
        String imagePath = (perro != null) ? perro.getFoto() : null;
        Image loadedImage = null;

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            if (!imagePath.startsWith("/")) {
                imagePath = "/" + imagePath;
            }
            try (InputStream stream = getClass().getResourceAsStream(imagePath)) {
                if (stream != null) {
                    loadedImage = new Image(stream);
                } else {
                    System.err.println("WARN: Imagen no encontrada en la ruta: " + imagePath + " para perro: " + (perro != null ? perro.getNombre() : "Desconocido"));
                }
            } catch (IOException e) {
                System.err.println("ERROR IO al cargar imagen " + imagePath + ": " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.err.println("ERROR: Ruta de imagen inválida " + imagePath + ": " + e.getMessage());
            }
        }

        // Si la imagen no se cargó o hubo un error, usar placeholder
        if (loadedImage == null || loadedImage.isError()) {
            if (loadedImage != null && loadedImage.getException() != null) {
                System.err.println("ERROR al cargar imagen para " + (perro != null ? perro.getNombre() : "Desconocido") + " (" + imagePath + "): " + loadedImage.getException().getMessage());
            }
            try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO)) {
                if (placeholderStream != null) {
                    loadedImage = new Image(placeholderStream);
                } else {
                    System.err.println("ERROR CRITICO: Imagen de placeholder principal no encontrada: " + RUTA_IMAGEN_PLACEHOLDER_PERRO);
                }
            } catch (IOException e) {
                System.err.println("ERROR IO al cargar placeholder " + RUTA_IMAGEN_PLACEHOLDER_PERRO + ": " + e.getMessage());
            }
        }
        imageView.setImage(loadedImage);
    }

    /**
     * Verifica si un nodo o alguno de sus ancestros es el botón especificado.
     * Útil para evitar doble acción si se hace clic en un botón dentro de un panel clickeable.
     * @param node El nodo que fue clickeado.
     * @param button El botón a verificar.
     * @return true si el nodo o uno de sus padres es el botón, false en caso contrario.
     */
    private boolean isNodeOrItsParentAButton(Node node, Button button) {
        if (node == null) return false;
        if (node.equals(button)) return true;
        Node parent = node.getParent();
        while (parent != null) {
            if (parent.equals(button)) return true;
            parent = parent.getParent();
        }
        return false;
    }

    /**
     * Maneja la acción "Ver Más" para un perro. Abre una ventana de diálogo/popup
     * con los detalles del perro.
     * @param perro El perro cuyos detalles se mostrarán.
     */
    private void handleVerMas(Perro perro) {
        if (perro == null) return;
        System.out.println("Mostrando detalles (pop-up) para: " + perro.getNombre());
        String titulo = "Detalles de " + perro.getNombre();
        Stage ownerStage = (mainBorderPane != null && mainBorderPane.getScene() != null)
                ? (Stage) mainBorderPane.getScene().getWindow()
                : UtilidadesVentana.getPrimaryStage(); // Fallback

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_DETALLES_PERRO));
            Parent root = loader.load();
            DetallesPerroController controller = loader.getController();
            if (controller != null) {
                controller.initData(perro); // Pasar el perro al controlador de detalles
                UtilidadesVentana.mostrarVentanaComoDialogo(root, titulo, ownerStage);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Interno", "No se pudo cargar el controlador de detalles del perro.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Loggear la excepción completa
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir la vista de detalles: " + e.getMessage());
        }
    }


    // --- Métodos de Navegación y Acciones de Botones ---

    /**
     * Maneja la acción del botón "Reservar".
     * Según el requerimiento original, este botón debería "mostrar todos los perros".
     * Actualmente, el FXML podría estar configurado para recargar Main.fxml.
     * Una forma más eficiente es restablecer los filtros y repopular.
     * Aquí, por simplicidad y si el FXML lo hace, se mantiene la recarga.
     * Opcionalmente, se podría cambiar a:
     *   this.searchTextField.clear();
     *   this.comboCriterioBusqueda.getSelectionModel().selectFirst(); // o el default
     *   actualizarPerrosMostrados(new ArrayList<>(this.listaDePerrosOriginal));
     */
    @FXML
    void Reservar(ActionEvent event) {
        System.out.println("Botón 'Reservar' (Mostrar todos los perros) presionado.");
        // Opción 1: Recargar la escena (como podría estar actualmente)
        // UtilidadesVentana.cambiarEscena(FXML_MAIN_CLIENTE, "Panel Principal - Dogpuccino", true);

        // Opción 2: Restablecer filtros y repopular (más eficiente)
        if (searchTextField != null) searchTextField.clear();
        if (comboCriterioBusqueda != null) comboCriterioBusqueda.getSelectionModel().selectFirst();
        // No es necesario recargar desde BD a menos que haya habido cambios externos.
        // aplicarFiltrosActuales() se encargará de usar listaDePerrosOriginal.
        aplicarFiltrosActuales();
        System.out.println("Vista de perros restablecida para mostrar todos.");
    }

    /**
     * Maneja la acción del botón "Adopciones".
     * Requerimiento: "modificar la tabla para mostrar solo los perros con los cuales hayas tenido ya una cita anterior".
     * Implementación actual: Navega a AdopcionesPanel.fxml.
     * Cambiar esto a un filtro requeriría lógica DAO adicional. Por ahora, se mantiene la navegación.
     */
    @FXML
    void Adopciones(ActionEvent event) {
        System.out.println("Botón Adopciones presionado. Navegando a Panel de Adopciones.");
        UtilidadesVentana.cambiarEscena(FXML_ADOPCIONES_PANEL, "Panel de Adopciones", true);
        // Para implementar el filtro de "perros con citas":
        // 1. Obtener idCliente de SesionUsuario.
        // 2. Crear método en CitaDao/PerroDao: List<Perro> obtenerPerrosConCitasPrevias(int idCliente).
        // 3. List<Perro> perrosConCitas = dao.obtenerPerrosConCitasPrevias(idCliente);
        // 4. actualizarPerrosMostrados(perrosConCitas);
    }

    /**
     * Maneja la acción del botón "Eventos".
     * Navega a la vista del panel de eventos.
     */
    @FXML
    void Eventos(ActionEvent event) {
        System.out.println("Botón Eventos presionado. Navegando a Panel de Eventos.");
        UtilidadesVentana.cambiarEscena(FXML_EVENTOS_PANEL, "Panel de Eventos", true);
    }

    /**
     * Maneja el clic en el icono de la bandeja de entrada (citas).
     * Muestra la bandeja de citas del usuario como una ventana emergente (pop-up).
     */
    @FXML
    void Bandeja(MouseEvent event) {
        System.out.println("Abriendo bandeja de citas como pop-up...");
        Usuario usuarioActual = SesionUsuario.getUsuarioLogueado();
        if (usuarioActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Sesión", "No se pudo identificar el usuario para ver sus citas.");
            return;
        }
        int idCliente = SesionUsuario.getEntidadIdEspecifica();
        if (idCliente == 0 && "CLIENTE".equalsIgnoreCase(usuarioActual.getRol())) {
            idCliente = usuarioActual.getIdUsuario();
            System.out.println("WARN: Usando IdUsuario como IdCliente para bandeja de citas, EntidadId era 0.");
        }
        if (idCliente == 0) {
            UtilidadesVentana.mostrarAlertaError("Error de Datos", "ID de cliente no disponible para mostrar citas.");
            return;
        }


        String titulo = "Mis Citas Programadas";
        Stage ownerStage = (event.getSource() instanceof Node)
                ? (Stage) ((Node) event.getSource()).getScene().getWindow()
                : UtilidadesVentana.getPrimaryStage();
        BandejaCitasController bandejaController = UtilidadesVentana.mostrarVentanaPopup(
                FXML_BANDEJA_CITAS, titulo, true, ownerStage
        );

        if (bandejaController != null) {
            bandejaController.initData(idCliente);
            System.out.println("Pop-up de bandeja de citas mostrado para cliente ID: " + idCliente);
        } else {
            System.err.println("Error: No se pudo obtener el controlador de BandejaCitasController o mostrar el pop-up.");
        }
    }

    /**
     * Maneja el clic en el icono de usuario.
     * Navega a la pantalla de perfil del usuario.
     */
    @FXML
    void DetallesUsuario(MouseEvent event) {
        System.out.println("Icono Usuario presionado - Navegando a Perfil de Usuario.");
        Usuario usuarioActual = SesionUsuario.getUsuarioLogueado();
        if (usuarioActual == null || usuarioActual.getIdUsuario() <= 0) {
            UtilidadesVentana.mostrarAlertaError("Error de Sesión", "No se pudo identificar el usuario actual o ID de usuario no válido.");
            return;
        }

        int idUsuario = usuarioActual.getIdUsuario();
        String nombreUsuario = usuarioActual.getNombreUsu();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PERFIL_USUARIO));
            Parent root = loader.load();
            root.getProperties().put("fxmlLocation", FXML_PERFIL_USUARIO);

            PerfilUsuarioController perfilController = loader.getController();
            if (perfilController != null) {
                perfilController.initData(idUsuario, nombreUsuario);
                UtilidadesVentana.cambiarEscenaConRoot(root, "Mi Perfil (" + nombreUsuario + ")", false);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Interno", "No se pudo cargar el controlador del perfil de usuario.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir la vista del perfil: " + e.getMessage());
        } catch (Exception e) { // Captura más genérica
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Inesperado", "Ocurrió un error al intentar abrir el perfil: " + e.getMessage());
        }
    }

    /**
     * Maneja el clic en el icono de búsqueda (si existe y está configurado).
     * Aplica los filtros actuales.
     */
    @FXML
    void onSearchIconClicked(MouseEvent event) {
        System.out.println("Icono de búsqueda clickeado.");
        aplicarFiltrosActuales();
    }

    /**
     * Maneja la acción de presionar Enter en el TextField de búsqueda.
     * Aplica los filtros actuales.
     */
    @FXML
    void onSearchTextFieldAction(ActionEvent event) {
        System.out.println("Enter presionado en campo de búsqueda.");
        aplicarFiltrosActuales();
    }

    // --- Métodos Auxiliares de Configuración (pueden ser redundantes si FXML ya lo hace) ---

    /**
     * Configura el texto de placeholder para el campo de búsqueda.
     * (Nota: Esto a menudo se establece directamente en el FXML).
     */
    private void configurarPlaceholderSearchTextField() {
        if (searchTextField != null && (searchTextField.getPromptText() == null || searchTextField.getPromptText().isEmpty())) {
            searchTextField.setPromptText("Buscar por " + CRITERIO_BUSQUEDA_NOMBRE.toLowerCase() + " o " + CRITERIO_BUSQUEDA_RAZA.toLowerCase() + "...");
        }
    }

    /**
     * Configura la acción para el icono de búsqueda si está presente.
     * (Nota: Asegúrate que `ImgIconBuscar` tenga el `fx:id` correcto en el FXML).
     */
    private void configurarAccionIconoBusqueda() {
        if (ImgIconBuscar != null) {
            ImgIconBuscar.setOnMouseClicked(this::onSearchIconClicked);
            ImgIconBuscar.setStyle(ImgIconBuscar.getStyle() + "; -fx-cursor: hand;");
        }
    }
}