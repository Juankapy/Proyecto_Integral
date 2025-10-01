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

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controlador principal para la vista del cliente (`Main.fxml`).
 * (Resto del Javadoc sin cambios)
 */
public class MainClienteController {

    private enum ModoVistaPerros {
        PARA_CITAS,
        PARA_ADOPCION_CON_CITA,
        EVENTOS
    }

    // --- Constantes ---
    private static final String ROL_ESPERADO_CLIENTE = "CLIENTE";
    private static final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.jpg";
    private static final String CRITERIO_BUSQUEDA_NOMBRE = "Nombre";
    private static final String CRITERIO_BUSQUEDA_RAZA = "Raza";
    private static final String FXML_LOGIN = "/com/proyectointegral2/Vista/Login.fxml";
    private static final String FXML_DETALLES_PERRO = "/com/proyectointegral2/Vista/DetallesPerro.fxml";
    private static final String FXML_EVENTOS_PANEL = "/com/proyectointegral2/Vista/EventosPanel.fxml";
    private static final String FXML_BANDEJA_CITAS = "/com/proyectointegral2/Vista/BandejasCitas.fxml";
    private static final String FXML_PERFIL_USUARIO = "/com/proyectointegral2/Vista/PerfilUsuario.fxml";
    private static final double HEADER_HEIGHT_ESTIMADA = 70.0;
    private static final double BOTTOM_BAR_HEIGHT_ESTIMADA = 80.0;
    private static final double MIN_SCROLLPANE_HEIGHT = 300.0;
    private static final double TARJETA_PREF_WIDTH = 190.0;
    private static final double TARJETA_IMG_AREA_WIDTH = 160.0;
    private static final double TARJETA_IMG_AREA_HEIGHT = 160.0;
    private static final double CARD_HORIZONTAL_GAP = 20.0;
    public static final double CARD_INTERNAL_PADDING = 10.0;
    private static final int MAX_COLUMNAS_GRID = 5;

    // --- Componentes FXML (sin cambios) ---
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
    private List<Perro> listaCompletaDePerrosParaCitas;
    private List<Perro> listaPerrosConCitaPreviaParaAdopcion;
    private List<Perro> perrosFiltradosParaMostrar;
    private List<Perro> listaBase = new ArrayList<>();

    // --- DAOs ---
    private PerroDao perroDao;

    // --- Estado del Controlador ---
    private Usuario usuarioLogueado;
    private ModoVistaPerros modoVistaActual = ModoVistaPerros.PARA_CITAS;

    @FXML
    public void initialize() {
        System.out.println("INFO: MainClienteController.initialize() - Iniciando controlador.");
        if (!verificarYEstablecerSesionUsuario()) {
            if(mainBorderPane != null) mainBorderPane.setCenter(new Label("Error de sesión. Por favor, reinicie o inicie sesión."));
            return;
        }

        try {
            this.perroDao = new PerroDao();
        } catch (Exception e) {
            System.err.println("CRITICAL: Error al inicializar PerroDao: " + e.getMessage());
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sistema", "No se pudo conectar con la base de datos de perros.");
            deshabilitarFuncionalidadPrincipal();
            return;
        }

        this.listaCompletaDePerrosParaCitas = new ArrayList<>();
        this.listaPerrosConCitaPreviaParaAdopcion = new ArrayList<>();
        this.perrosFiltradosParaMostrar = new ArrayList<>();

        cargarDatosInicialesPerrosParaCitas();
        actualizarListaDePerrosSegunModoVista();
        actualizarEstilosBotonesFiltroPrincipal();

        configurarFiltrosDeTexto();
        configurarListenersDeVentanaParaResponsividad();
        configurarPlaceholderDelSearchTextField();
        configurarAccionDelIconoBusqueda();

        Platform.runLater(this::adaptarUIAEscenaLista);
    }

    private void deshabilitarFuncionalidadPrincipal() {
    }

    /**
     * Verifica si hay un usuario logueado y si su rol es "Cliente".
     * Si no, muestra una alerta y redirige al login.
     * @return `true` si la sesión es válida para un cliente, `false` en caso contrario.
     */
    private boolean verificarYEstablecerSesionUsuario() {
        this.usuarioLogueado = SesionUsuario.getUsuarioLogueado();

        System.out.println("DEBUG MainClienteController: Verificando sesión...");
        if (this.usuarioLogueado != null) {
            System.out.println("DEBUG MainClienteController: Usuario obtenido de SesionUsuario: " + this.usuarioLogueado.getNombreUsu());
            System.out.println("DEBUG MainClienteController: Rol obtenido de SesionUsuario: '" + this.usuarioLogueado.getRol() + "'");
        } else {
            System.out.println("DEBUG MainClienteController: No hay usuario en SesionUsuario.");
        }

        String rolEnSesion = null;
        if (this.usuarioLogueado != null && this.usuarioLogueado.getRol() != null) {
            rolEnSesion = this.usuarioLogueado.getRol().trim(); // Aplicar trim() para robustez
        }

        // CORRECCIÓN: Usar la constante ROL_ESPERADO_CLIENTE para la comparación
        if (this.usuarioLogueado == null || !ROL_ESPERADO_CLIENTE.equalsIgnoreCase(rolEnSesion)) {
            String mensajeError = (this.usuarioLogueado == null)
                    ? "No hay usuario logueado."
                    : "El rol del usuario ('" + rolEnSesion + "') no es válido para esta sección.";
            UtilidadesVentana.mostrarAlertaError("Error de Sesión", mensajeError + " Será redirigido al inicio de sesión.");
            UtilidadesVentana.cambiarEscena(FXML_LOGIN, "Inicio de Sesión", false);
            return false;
        }

        System.out.println("INFO: Bienvenido a MainCliente: " + this.usuarioLogueado.getNombreUsu() + " con rol: " + rolEnSesion);
        return true;
    }

    private void cargarDatosInicialesPerrosParaCitas() {
        if (perroDao == null) {
            System.err.println("ERROR: PerroDao no inicializado en cargarDatosInicialesPerrosParaCitas.");
            this.listaCompletaDePerrosParaCitas = new ArrayList<>();
            return;
        }
        try {
            List<Perro> todosLosPerrosObtenidos = perroDao.obtenerTodosLosPerros();
            if (todosLosPerrosObtenidos != null) {
                this.listaCompletaDePerrosParaCitas = todosLosPerrosObtenidos.stream()
                        .filter(p -> p != null && (
                                "N".equalsIgnoreCase(p.getAdoptado()) ||
                                        "R".equalsIgnoreCase(p.getAdoptado()) ||
                                        "A".equalsIgnoreCase(p.getAdoptado()) ||
                                        p.getAdoptado() == null
                        ))
                        .collect(Collectors.toList());
            } else {
                this.listaCompletaDePerrosParaCitas = new ArrayList<>();
            }
            System.out.println("INFO: Perros disponibles para citas cargados inicialmente: " + this.listaCompletaDePerrosParaCitas.size());
        } catch (SQLException e) {
            System.err.println("ERROR SQL al cargar perros para citas: " + e.getMessage());
            e.printStackTrace();
            this.listaCompletaDePerrosParaCitas = new ArrayList<>();
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "No se pudieron cargar los perros disponibles: " + e.getMessage());
        }
    }

    private void cargarPerrosConCitaPreviaParaAdopcion() {
        int idCliente = SesionUsuario.getEntidadIdEspecifica();
        try {
            listaBase = perroDao.obtenerPerrosConCitasPreviasPorCliente(idCliente);
            if (listaBase == null) {
                listaBase = new ArrayList<>();
            }
            System.out.println("DEBUG: Perros con cita previa obtenidos: " + listaBase.size());
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "No se pudieron obtener los perros con cita previa.");
            listaBase = new ArrayList<>();
        }
        // No llamar aquí a popularGridConPerros()
    }

    private void actualizarListaDePerrosSegunModoVista() {
        if (modoVistaActual == ModoVistaPerros.PARA_ADOPCION_CON_CITA) {
            cargarPerrosConCitaPreviaParaAdopcion();
        } else {
            if (this.listaCompletaDePerrosParaCitas.isEmpty() && perroDao != null) {
                cargarDatosInicialesPerrosParaCitas();
            }
            listaBase = new ArrayList<>(this.listaCompletaDePerrosParaCitas);
        }
        listaBase = listaBase.stream()
                .filter(p -> !"S".equalsIgnoreCase(p.getAdoptado()))
                .collect(Collectors.toList());
        aplicarFiltrosDeTextoSobreListaBase(listaBase);
    }

    private void configurarFiltrosDeTexto() {
        if (comboCriterioBusqueda != null) {
            comboCriterioBusqueda.getItems().setAll(CRITERIO_BUSQUEDA_NOMBRE, CRITERIO_BUSQUEDA_RAZA);
            comboCriterioBusqueda.getSelectionModel().selectFirst();
            comboCriterioBusqueda.valueProperty().addListener((obs, oldVal, newVal) -> actualizarListaDePerrosSegunModoVista());
        }
        if (searchTextField != null) {
            searchTextField.textProperty().addListener((obs, oldVal, newVal) -> actualizarListaDePerrosSegunModoVista());
        }
    }

    private void aplicarFiltrosDeTextoSobreListaBase(List<Perro> listaBase) {
        if (listaBase == null) {
            this.perrosFiltradosParaMostrar = new ArrayList<>();
        } else {
            String textoBusqueda = (searchTextField != null) ? searchTextField.getText() : "";
            String textoBusquedaTrim = textoBusqueda.trim().toLowerCase();
            String criterioSeleccionado = (comboCriterioBusqueda != null && comboCriterioBusqueda.getValue() != null)
                    ? comboCriterioBusqueda.getValue()
                    : CRITERIO_BUSQUEDA_NOMBRE;
            if (textoBusquedaTrim.isEmpty()) {
                this.perrosFiltradosParaMostrar = new ArrayList<>(listaBase);
            } else {
                this.perrosFiltradosParaMostrar = listaBase.stream().filter(perro -> {
                    if (perro == null) return false;
                    switch (criterioSeleccionado) {
                        case CRITERIO_BUSQUEDA_RAZA:
                            return perro.getRaza() != null && perro.getRaza().getNombreRaza() != null &&
                                    perro.getRaza().getNombreRaza().toLowerCase().startsWith(textoBusquedaTrim);
                        case CRITERIO_BUSQUEDA_NOMBRE:
                        default:
                            return perro.getNombre() != null &&
                                    perro.getNombre().toLowerCase().startsWith(textoBusquedaTrim);
                    }
                }).collect(Collectors.toList());
            }
        }
        popularGridConPerros();
    }

    private void configurarListenersDeVentanaParaResponsividad() {
        if (mainBorderPane == null) return;
        Runnable setupStageListeners = () -> {
            if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() instanceof Stage) {
                Stage stage = (Stage) mainBorderPane.getScene().getWindow();
                Runnable adaptarUI = () -> Platform.runLater(() -> {
                    if (stage.isShowing() && stage.getWidth() > 0 && !Double.isNaN(stage.getWidth()) && !Double.isNaN(stage.getHeight())) {
                        adaptarUIAlTamanoDeVentana(stage);
                    }
                });
                if (stage.isShowing()) adaptarUI.run(); else stage.setOnShown(event -> adaptarUI.run());
                stage.widthProperty().addListener((obs, oldVal, newVal) -> adaptarUI.run());
                stage.heightProperty().addListener((obs, oldVal, newVal) -> adaptarUI.run());
                stage.maximizedProperty().addListener((obs, oldVal, newVal) -> adaptarUI.run());
            }
        };
        if (mainBorderPane.getScene() != null) {
            if (mainBorderPane.getScene().getWindow() != null) {
                setupStageListeners.run();
            } else {
                mainBorderPane.getScene().windowProperty().addListener((obs, oW, nW) -> { if (nW != null) setupStageListeners.run(); });
            }
        } else {
            mainBorderPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    if (newScene.getWindow() != null) {
                        setupStageListeners.run();
                    } else {
                        newScene.windowProperty().addListener((obsW, oW, nW) -> { if (nW != null) setupStageListeners.run(); });
                    }
                }
            });
        }
    }

    private void adaptarUIAEscenaLista() {
        if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() instanceof Stage) {
            Stage stage = (Stage) mainBorderPane.getScene().getWindow();
            if (stage.isShowing() && stage.getWidth() > 0 && !Double.isNaN(stage.getWidth()) && !Double.isNaN(stage.getHeight())) {
                adaptarUIAlTamanoDeVentana(stage);
            } else if (!stage.isShowing()) {
                stage.setOnShown(event -> Platform.runLater(() -> adaptarUIAlTamanoDeVentana(stage)));
            }
        }
    }

    private void adaptarUIAlTamanoDeVentana(Stage stage) {
        if (stage == null || stage.getWidth() <= 0 || Double.isNaN(stage.getWidth()) || Double.isNaN(stage.getHeight()) || dogGrid == null) {
            System.err.println("WARN: No se puede adaptar UI, stage o dogGrid inválido o dimensiones no listas.");
            return;
        }
        adaptarAnchoDelGridYRepopular(stage.getWidth());
        adaptarAlturaDelScrollPane(stage.getHeight());
    }

    private void adaptarAnchoDelGridYRepopular(double anchoVentana) {
        if (dogGrid == null || mainBorderPane == null) return;
        VBox centerVBox = (mainBorderPane.getCenter() instanceof VBox) ? (VBox) mainBorderPane.getCenter() : null;
        if (centerVBox == null) { System.err.println("WARN: El centro del BorderPane no es un VBox, no se puede calcular padding."); return; }
        double paddingLateralTotal = centerVBox.getPadding().getLeft() + centerVBox.getPadding().getRight();
        paddingLateralTotal += (dogScrollPane != null && dogScrollPane.getPadding() != null) ? dogScrollPane.getPadding().getLeft() + dogScrollPane.getPadding().getRight() : 0;
        paddingLateralTotal += (dogGrid.getPadding() != null) ? dogGrid.getPadding().getLeft() + dogGrid.getPadding().getRight() : 0;
        double espacioParaScrollbar = (dogScrollPane != null && dogScrollPane.getVbarPolicy() != ScrollPane.ScrollBarPolicy.NEVER && dogScrollPane.isVisible()) ? 20.0 : 0.0;
        double anchoDisponibleParaGrid = anchoVentana - paddingLateralTotal - espacioParaScrollbar;
        int nuevasColumnas = calcularColumnasSegunAncho(anchoDisponibleParaGrid);
        boolean numColumnasNecesitaCambio = dogGrid.getColumnConstraints().size() != nuevasColumnas;
        boolean gridVacioPeroHayPerrosParaMostrar = dogGrid.getChildren().isEmpty() && (perrosFiltradosParaMostrar != null && !perrosFiltradosParaMostrar.isEmpty());
        boolean listaDePerrosCambio = !Objects.equals(dogGrid.getUserData(), perrosFiltradosParaMostrar);
        if (numColumnasNecesitaCambio || gridVacioPeroHayPerrosParaMostrar || listaDePerrosCambio ) {
            if (numColumnasNecesitaCambio) {
                dogGrid.getColumnConstraints().clear();
                for (int i = 0; i < nuevasColumnas; i++) {
                    ColumnConstraints colConst = new ColumnConstraints();
                    colConst.setPercentWidth(100.0 / nuevasColumnas);
                    colConst.setHgrow(Priority.SOMETIMES);
                    dogGrid.getColumnConstraints().add(colConst);
                }
            }
            popularGridConPerros();
            dogGrid.setUserData(perrosFiltradosParaMostrar != null ? new ArrayList<>(perrosFiltradosParaMostrar) : new ArrayList<>());
        }
    }

    private int calcularColumnasSegunAncho(double anchoGridDisponible) {
        if (anchoGridDisponible <= 0) return 1;
        double hgap = (dogGrid != null && dogGrid.getHgap() > 0 && !Double.isNaN(dogGrid.getHgap())) ? dogGrid.getHgap() : CARD_HORIZONTAL_GAP;
        double anchoEstimadoTarjetaConEspacio = TARJETA_PREF_WIDTH + hgap;
        int numColumnas = Math.max(1, (int) (anchoGridDisponible / anchoEstimadoTarjetaConEspacio));
        return Math.min(numColumnas, MAX_COLUMNAS_GRID);
    }

    private void adaptarAlturaDelScrollPane(double nuevaAlturaVentana) {
        if (dogScrollPane == null || mainBorderPane == null || !(mainBorderPane.getCenter() instanceof VBox)) return;
        VBox centerVBox = (VBox) mainBorderPane.getCenter();
        Node topNode = mainBorderPane.getTop();
        Node bottomNode = mainBorderPane.getBottom();
        double alturaCabecera = (topNode != null && topNode.getLayoutBounds().getHeight() > 0) ? topNode.getLayoutBounds().getHeight() : HEADER_HEIGHT_ESTIMADA;
        double alturaPie = (bottomNode != null && bottomNode.getLayoutBounds().getHeight() > 0) ? bottomNode.getLayoutBounds().getHeight() : BOTTOM_BAR_HEIGHT_ESTIMADA;
        double paddingVerticalVBoxCentro = centerVBox.getPadding().getTop() + centerVBox.getPadding().getBottom();
        double alturaOtrosNodosEnVBoxCentro = 0;
        for (Node child : centerVBox.getChildren()) {
            if (child != dogScrollPane && child.isVisible() && child.isManaged()) {
                alturaOtrosNodosEnVBoxCentro += child.getLayoutBounds().getHeight();
                if (centerVBox.getChildren().indexOf(child) < centerVBox.getChildren().indexOf(dogScrollPane)) {
                    alturaOtrosNodosEnVBoxCentro += centerVBox.getSpacing();
                }
            }
        }
        double margenInferiorConsiderado = 20.0;
        double alturaDisponibleParaScrollPane = nuevaAlturaVentana - alturaCabecera - alturaPie - paddingVerticalVBoxCentro - alturaOtrosNodosEnVBoxCentro - margenInferiorConsiderado;
        dogScrollPane.setPrefHeight(Math.max(MIN_SCROLLPANE_HEIGHT, alturaDisponibleParaScrollPane));
    }

    private void popularGridConPerros() {
        if (dogScrollPane.getContent() != dogGrid) {
            dogScrollPane.setContent(dogGrid);
        }
        System.out.println("DEBUG: Repoblando grid con " + perrosFiltradosParaMostrar.size() + " perros.");
        dogGrid.getChildren().clear();
        int columna = 0;
        int fila = 0;
        for (Perro perro : perrosFiltradosParaMostrar) {
            try {
                VBox tarjeta = crearTarjetaPerro(perro);
                dogGrid.add(tarjeta, columna, fila);
                System.out.println("DEBUG: Tarjeta añadida para perro: " + perro.getNombre());
                columna++;
                if (columna == 5) {
                    columna = 0;
                    fila++;
                }
            } catch (Exception e) {
                System.err.println("ERROR al crear tarjeta para perro: " + perro.getNombre() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("DEBUG: Tarjetas añadidas al grid.");
    }


    private VBox crearTarjetaPerro(Perro perro) {
        VBox card = new VBox(5);
        card.setPrefWidth(TARJETA_PREF_WIDTH); card.setMaxWidth(TARJETA_PREF_WIDTH); card.setMinWidth(TARJETA_PREF_WIDTH);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(String.format("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2); -fx-padding: %f;", CARD_INTERNAL_PADDING));
        card.setMinHeight(240);
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);
        ImageView imgView = new ImageView();
        cargarImagenPerroConPlaceholder(imgView, perro);
        imgView.setPreserveRatio(true); imgView.setSmooth(true); imgView.setCache(true);
        imgView.setFitWidth(TARJETA_IMG_AREA_WIDTH); imgView.setFitHeight(TARJETA_IMG_AREA_HEIGHT);
        imageContainer.getChildren().add(imgView);
        VBox.setMargin(imageContainer, new Insets(0, 0, 8, 0));
        Label lblNombrePerro = new Label(Objects.requireNonNullElse(perro.getNombre(), "Sin Nombre"));
        lblNombrePerro.setStyle("-fx-background-color: #C8E6C9; -fx-padding: 5 10; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 14px;");
        lblNombrePerro.setAlignment(Pos.CENTER); lblNombrePerro.setWrapText(false);
        lblNombrePerro.setPrefWidth(TARJETA_IMG_AREA_WIDTH); lblNombrePerro.setMaxWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setTextOverrun(OverrunStyle.ELLIPSIS);
        Button btnAccionTarjeta = new Button();
        VBox.setMargin(btnAccionTarjeta, new Insets(8, 0, 0, 0));
        boolean isAdoptado = "S".equalsIgnoreCase(perro.getAdoptado());
        if (isAdoptado) {
            btnAccionTarjeta.setText("Adoptado");
            btnAccionTarjeta.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-font-size:12px; -fx-padding: 5 10;");
            btnAccionTarjeta.setDisable(true);
        } else {
            btnAccionTarjeta.setText("Ver Detalles");
            btnAccionTarjeta.setStyle("-fx-background-color: #E67E22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-font-size:12px; -fx-padding: 5 10;");
            btnAccionTarjeta.setOnAction(event -> handleVerMasOSolicitar(perro));
        }
        btnAccionTarjeta.setPrefWidth(TARJETA_IMG_AREA_WIDTH * 0.9);
        card.getChildren().addAll(imageContainer, lblNombrePerro, btnAccionTarjeta);
        card.setUserData(perro);
        if (!isAdoptado) {
            card.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    Node clickedNode = event.getPickResult().getIntersectedNode();
                    if (!isNodeOrItsParentAButton(clickedNode, btnAccionTarjeta)) {
                        handleVerMasOSolicitar(perro);
                    }
                }
            });
            if (!card.getStyleClass().contains("tarjeta-perro-clickable")) {
                card.getStyleClass().add("tarjeta-perro-clickable");
            }
        }
        return card;
    }

    private void cargarImagenPerroConPlaceholder(ImageView imageView, Perro perro) {
        String imagePath = (perro != null) ? perro.getFoto() : null; Image loadedImage = null;
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            if (!imagePath.startsWith("/")) imagePath = "/" + imagePath;
            try (InputStream stream = getClass().getResourceAsStream(imagePath)) {
                if (stream != null) loadedImage = new Image(stream);
                else System.err.println("WARN: Imagen no encontrada: " + imagePath + " para perro: " + (perro != null ? perro.getNombre() : "Desconocido"));
            } catch (Exception e) { System.err.println("ERROR al cargar imagen " + imagePath + ": " + e.getMessage());}
        }
        if (loadedImage == null || loadedImage.isError()) {
            if (loadedImage != null && loadedImage.getException() != null) System.err.println("ERROR img para " + (perro != null ? perro.getNombre() : "Desconocido") + " (" + imagePath + "): " + loadedImage.getException().getMessage());
            try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO)) {
                if (placeholderStream != null) loadedImage = new Image(placeholderStream);
                else System.err.println("ERROR CRITICO: Placeholder no encontrado: " + RUTA_IMAGEN_PLACEHOLDER_PERRO);
            } catch (Exception e) { System.err.println("ERROR IO al cargar placeholder " + RUTA_IMAGEN_PLACEHOLDER_PERRO + ": " + e.getMessage());}
        }
        imageView.setImage(loadedImage);
    }

    private boolean isNodeOrItsParentAButton(Node node, Button button) {
        if (node == null || button == null) return false; if (node.equals(button)) return true; Node parent = node.getParent();
        while (parent != null) { if (parent.equals(button)) return true; parent = parent.getParent(); } return false;
    }

    private void handleVerMasOSolicitar(Perro perro) {
        abrirDetallesPerro(perro);
    }

    private void abrirDetallesPerro(Perro perro) {
        if (perro == null) return;
        System.out.println("INFO: Mostrando detalles (pop-up) para: " + perro.getNombre());
        String titulo = "Detalles de " + perro.getNombre();
        Stage ownerStage = (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() instanceof Stage)
                ? (Stage) mainBorderPane.getScene().getWindow() : UtilidadesVentana.getPrimaryStage();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_DETALLES_PERRO));
            Parent root = loader.load();
            DetallesPerroController controller = loader.getController();
            if (controller != null) {
                controller.initData(perro);
                // Llama a activarModoAdopcion si estás en modo adopciones
                if (modoVistaActual == ModoVistaPerros.PARA_ADOPCION_CON_CITA) {
                    controller.activarModoAdopcion();
                }
                UtilidadesVentana.mostrarVentanaComoDialogo(root, titulo, ownerStage);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Interno", "No se pudo cargar el controlador de detalles del perro.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir la vista de detalles del perro: " + e.getMessage());
        }
    }

    @FXML
    void Reservar(ActionEvent event) {
        System.out.println("INFO: Botón 'Reservar cita' presionado. Mostrando perros para citas.");
        this.modoVistaActual = ModoVistaPerros.PARA_CITAS;
        if (searchTextField != null) searchTextField.clear();
        actualizarListaDePerrosSegunModoVista();
        actualizarEstilosBotonesFiltroPrincipal();
    }

    @FXML
    void Adopciones(ActionEvent event) {
        System.out.println("INFO: Botón 'Adopciones' presionado. Mostrando perros con cita previa para adopción.");
        this.modoVistaActual = ModoVistaPerros.PARA_ADOPCION_CON_CITA;
        if (searchTextField != null) searchTextField.clear();
        actualizarListaDePerrosSegunModoVista();
        actualizarEstilosBotonesFiltroPrincipal();
    }

    @FXML
    void Eventos(ActionEvent event) {
        System.out.println("INFO: Botón Eventos presionado. Mostrando mensaje de no disponibilidad.");
        this.modoVistaActual = ModoVistaPerros.EVENTOS;
        mostrarEventos();
        actualizarEstilosBotonesFiltroPrincipal();
    }

    private void mostrarEventos() {
        if (dogScrollPane != null) {
            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.setPrefHeight(300);
            Label label = new Label("Los eventos aún no están disponibles");
            label.setStyle("-fx-font-size: 18px; -fx-text-fill: #888; -fx-font-weight: bold;");
            vbox.getChildren().add(label);
            dogScrollPane.setContent(vbox);
        }
    }

    private void actualizarEstilosBotonesFiltroPrincipal() {
        if (modoVistaActual == ModoVistaPerros.EVENTOS) {
            mostrarEventos();
            return;
        }

        String estiloActivo = "-fx-background-color: #FF8C00; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold;";
        String estiloInactivoReservar = "-fx-background-color: #D2691E; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold;";
        String estiloInactivoAdopciones = "-fx-background-color: #8FBC8F; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold;";
        if (BtnReservar == null || BtnAdopciones == null) return;
        if (modoVistaActual == ModoVistaPerros.PARA_CITAS) {
            BtnReservar.setStyle(estiloActivo); BtnAdopciones.setStyle(estiloInactivoAdopciones);
        } else {
            BtnReservar.setStyle(estiloInactivoReservar); BtnAdopciones.setStyle(estiloActivo);
        }
    }

    @FXML
    void Bandeja(MouseEvent event) {
        System.out.println("INFO: Abriendo bandeja de citas como pop-up...");
        if (usuarioLogueado == null) { UtilidadesVentana.mostrarAlertaError("Error de Sesión", "No se pudo identificar el usuario para ver sus citas."); return; }
        int idCliente = SesionUsuario.getEntidadIdEspecifica();
        if (idCliente == 0 && ROL_ESPERADO_CLIENTE.equalsIgnoreCase(usuarioLogueado.getRol())) { // Usar constante
            idCliente = usuarioLogueado.getIdUsuario();
        }
        if (idCliente == 0) { UtilidadesVentana.mostrarAlertaError("Error de Datos", "ID de cliente no disponible para mostrar citas."); return; }
        Stage ownerStage = (event.getSource() instanceof Node) ? (Stage)((Node)event.getSource()).getScene().getWindow() : UtilidadesVentana.getPrimaryStage();
        BandejaCitasController bandejaCtrl = UtilidadesVentana.mostrarVentanaPopup(FXML_BANDEJA_CITAS, "Mis Citas Programadas", true, ownerStage);
        if (bandejaCtrl != null) {
            bandejaCtrl.initData(idCliente);
            System.out.println("INFO: Pop-up de bandeja de citas mostrado para cliente ID: " + idCliente);
        } else { System.err.println("ERROR: No se pudo obtener el controlador de BandejaCitasController o mostrar el pop-up."); }
    }

    @FXML
    void DetallesUsuario(MouseEvent event) {
        System.out.println("INFO: Icono Usuario presionado - Navegando a Perfil de Usuario.");
        if (usuarioLogueado == null || usuarioLogueado.getIdUsuario() <= 0) { UtilidadesVentana.mostrarAlertaError("Error de Sesión", "No se pudo identificar el usuario actual o ID de usuario no válido."); return; }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PERFIL_USUARIO));
            Parent root = loader.load();
            PerfilUsuarioController perfilCtrl = loader.getController();
            if (perfilCtrl != null) {
                perfilCtrl.initData(usuarioLogueado.getIdUsuario(), usuarioLogueado.getNombreUsu());
                UtilidadesVentana.cambiarEscenaConRoot(root, "Mi Perfil (" + usuarioLogueado.getNombreUsu() + ")", false);
            } else { UtilidadesVentana.mostrarAlertaError("Error Interno", "No se pudo cargar el controlador del perfil de usuario.");}
        } catch (Exception e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir la vista del perfil: " + e.getMessage());}
    }

    @FXML void onSearchIconClicked(MouseEvent event) { actualizarListaDePerrosSegunModoVista(); }
    @FXML void onSearchTextFieldAction(ActionEvent event) { actualizarListaDePerrosSegunModoVista(); }

    private void configurarPlaceholderDelSearchTextField() {
        if (searchTextField != null && (searchTextField.getPromptText() == null || searchTextField.getPromptText().isEmpty())) {
            searchTextField.setPromptText("Buscar por " + CRITERIO_BUSQUEDA_NOMBRE.toLowerCase() + " o " + CRITERIO_BUSQUEDA_RAZA.toLowerCase() + "...");
        }
    }
    private void configurarAccionDelIconoBusqueda() {
        if (ImgIconBuscar != null) {
            ImgIconBuscar.setOnMouseClicked(this::onSearchIconClicked);
            if (!ImgIconBuscar.getStyleClass().contains("clickable-icon")) {
                ImgIconBuscar.getStyleClass().add("clickable-icon");
            }
        }
    }
}