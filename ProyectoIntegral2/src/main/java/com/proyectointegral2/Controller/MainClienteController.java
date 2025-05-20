package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.Model.SesionUsuario;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.PerroDao;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
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
    @FXML private ComboBox<String> comboCriterioBusqueda;

    private final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.jpg";
    private List<Perro> listaDePerrosOriginal;
    private List<Perro> perrosMostradosActuales;
    private Usuario usuarioLogueado;

    private PerroDao perroDao;

    private static final double HEADER_HEIGHT_ESTIMADA = 70;
    private static final double BOTTOM_BAR_HEIGHT_ESTIMADA = 80;
    private static final double SCROLLPANE_PADDING_VERTICAL = 40;
    private static final double MIN_SCROLLPANE_HEIGHT = 300;
    private static final double TARJETA_PREF_WIDTH = 190;
    private static final double TARJETA_IMG_AREA_WIDTH = 160;
    private static final double TARJETA_IMG_AREA_HEIGHT = 160;
    private static final double CARD_HORIZONTAL_GAP = 20;
    private static final double CARD_INTERNAL_PADDING = 10;


    @FXML
    public void initialize() {
        System.out.println("MainClienteController inicializado.");
        Usuario usuarioActual = SesionUsuario.getUsuarioLogueado();

        if (usuarioActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sesión", "No hay usuario logueado. Volviendo al login.");
            UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/Login.fxml", "Inicio de Sesión", false);
            return;
        }
        System.out.println("Bienvenido a MainCliente: " + usuarioActual.getNombreUsu());

        this.perroDao = new PerroDao();
        cargarPerrosDesdeBaseDeDatos();
        if (this.listaDePerrosOriginal != null) {
            this.perrosMostradosActuales = new ArrayList<>(this.listaDePerrosOriginal);
        } else {
            this.listaDePerrosOriginal = new ArrayList<>();
            this.perrosMostradosActuales = new ArrayList<>();
        }

        if (comboCriterioBusqueda != null) {
            comboCriterioBusqueda.getItems().setAll("Nombre", "Raza");
            comboCriterioBusqueda.getSelectionModel().selectFirst();
            comboCriterioBusqueda.valueProperty().addListener((obs, oldVal, newVal) ->
                    filtrarYRepopularPerros(searchTextField.getText())
            );
        }
        if (searchTextField != null) {
            searchTextField.textProperty().addListener((obs, oldVal, newVal) ->
                    filtrarYRepopularPerros(newVal)
            );
        }

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
            }
        });
    }

    private void filtrarYRepopularPerros(String textoBusqueda) {
        if (listaDePerrosOriginal == null) {
            cargarPerrosDesdeBaseDeDatos();
            if (listaDePerrosOriginal == null) {
                UtilidadesVentana.mostrarAlertaError("Error Datos", "No se pudieron cargar los datos de los perros para filtrar.");
                return;
            }
        }
        String textoBusquedaTrim = (textoBusqueda == null) ? "" : textoBusqueda.trim().toLowerCase();
        String criterio = (comboCriterioBusqueda != null && comboCriterioBusqueda.getValue() != null) ? comboCriterioBusqueda.getValue() : "Nombre";

        if (textoBusquedaTrim.isEmpty()) {
            this.perrosMostradosActuales = new ArrayList<>(this.listaDePerrosOriginal);
        } else {
            this.perrosMostradosActuales = this.listaDePerrosOriginal.stream().filter(perro -> {
                switch (criterio) {
                    case "Raza":
                        return perro.getRaza() != null && perro.getRaza().getNombreRaza() != null &&
                                perro.getRaza().getNombreRaza().toLowerCase().startsWith(textoBusquedaTrim);
                    case "Nombre":
                    default:
                        return perro.getNombre() != null &&
                                perro.getNombre().toLowerCase().startsWith(textoBusquedaTrim);
                }
            }).collect(Collectors.toList());
        }

        if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null && mainBorderPane.getScene().getWindow().getWidth() > 0) {
            adaptarContenidoAlAnchoYPopular(mainBorderPane.getScene().getWindow().getWidth());
        } else {
            popularGridDePerros();
        }
    }

    private void cargarPerrosDesdeBaseDeDatos() {
        try {
            this.listaDePerrosOriginal = perroDao.obtenerTodosLosPerros();
            if (this.listaDePerrosOriginal == null) {
                this.listaDePerrosOriginal = new ArrayList<>();
            }
            System.out.println("Perros cargados desde la base de datos: " + this.listaDePerrosOriginal.size());
        } catch (SQLException e) {
            System.err.println("Error al cargar perros desde la base de datos: " + e.getMessage());
            e.printStackTrace();
            this.listaDePerrosOriginal = new ArrayList<>();
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "No se pudieron cargar los perros: " + e.getMessage());
        }
    }

    private void configurarListenersDeVentana() {
        if (mainBorderPane != null) {
            mainBorderPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                        if (newWindow != null) {
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
    }

    private void adaptarUIAlTamanoVentana(Stage stage) {
        if (stage == null || stage.getWidth() <= 0 || Double.isNaN(stage.getWidth())) return;
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        adaptarContenidoAlAnchoYPopular(currentWidth);
        adaptarContenidoALaAltura(currentHeight);
    }

    private void configurarPlaceholderSearchTextField() {
        if (searchTextField != null) {
            searchTextField.setPromptText("Buscar por nombre o raza...");
        }
    }

    private void configurarAccionIconoBusqueda() {
        if (ImgIconBuscar != null) {
            ImgIconBuscar.setOnMouseClicked(this::onSearchIconClicked);
        }
    }

    private VBox crearTarjetaPerro(Perro perro) {
        VBox card = new VBox(5);
        card.setPrefWidth(TARJETA_PREF_WIDTH);
        card.setMaxWidth(TARJETA_PREF_WIDTH);
        card.setMinWidth(TARJETA_PREF_WIDTH);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(String.format("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2); -fx-padding: %f;", CARD_INTERNAL_PADDING));
        card.setMinHeight(240);

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
                InputStream stream = getClass().getResourceAsStream(imagePath);
                if (stream != null) {
                    loadedImage = new Image(stream);
                    stream.close();
                } else {
                    System.err.println("WARN: No se encontró imagen en " + imagePath + " para " + perro.getNombre());
                }
            }

            if (loadedImage == null || loadedImage.isError()) {
                try(InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO)){
                    if(placeholderStream != null) loadedImage = new Image(placeholderStream);
                    else System.err.println("ERROR CRITICO: Placeholder principal no encontrado: " + RUTA_IMAGEN_PLACEHOLDER_PERRO);
                }
            }
            imgView.setImage(loadedImage);

        } catch (Exception e) {
            System.err.println("ERROR cargando imagen para " + perro.getNombre() + ": " + imagePath + ". " + e.getMessage());
            try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO)) {
                if (placeholderStream != null) imgView.setImage(new Image(placeholderStream));
            } catch (Exception ex) {  }
        }

        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        imgView.setCache(true);
        imgView.setFitWidth(TARJETA_IMG_AREA_WIDTH);
        imgView.setFitHeight(TARJETA_IMG_AREA_HEIGHT);
        imageContainer.getChildren().add(imgView);
        VBox.setMargin(imageContainer, new Insets(0,0,8,0));

        Label lblNombrePerro = new Label(perro.getNombre());
        lblNombrePerro.setStyle("-fx-background-color: #C8E6C9; -fx-padding: 5 10 5 10; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 14px;");
        lblNombrePerro.setAlignment(Pos.CENTER);
        lblNombrePerro.setWrapText(false);
        lblNombrePerro.setPrefWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setMaxWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);

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

        card.getChildren().addAll(imageContainer, lblNombrePerro, btnAccionTarjeta);
        card.setUserData(perro);
        if (!perro.isAdoptado()) {
            card.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    Node clickedNode = event.getPickResult().getIntersectedNode();
                    if (!(clickedNode instanceof Button) && !isNodeInsideButton(clickedNode, btnAccionTarjeta)) {
                        handleVerMas(perro);
                    }
                }
            });
            card.setStyle(card.getStyle() + "; -fx-cursor: hand;");
        }
        return card;
    }

    private boolean isNodeInsideButton(Node node, Button button) {
        if (node == null) return false;
        if (node.equals(button)) return true;
        return isNodeInsideButton(node.getParent(), button);
    }

    private void handleVerMas(Perro perro) {
        System.out.println("Ver más sobre (pop-up): " + perro.getNombre());
        String detallesFxml = "/com/proyectointegral2/Vista/DetallesPerro.fxml";
        String titulo = "Detalles de " + perro.getNombre();
        Stage ownerStage = (mainBorderPane != null && mainBorderPane.getScene() != null) ? (Stage) mainBorderPane.getScene().getWindow() : UtilidadesVentana.getPrimaryStage();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(detallesFxml));
            Parent root = loader.load();
            DetallesPerroController controller = loader.getController();
            if (controller != null) {
                controller.initData(perro);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Interno", "Controlador de detalles no encontrado."); return;
            }
            UtilidadesVentana.mostrarVentanaComoDialogo(root, titulo, ownerStage);
        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir la vista de detalles: " + e.getMessage());
        }
    }

    private int calcularColumnasSegunAncho(double anchoGridDisponible) {
        if (anchoGridDisponible <= 0) return 1;
        double anchoTarjetaEstimado = TARJETA_PREF_WIDTH + CARD_HORIZONTAL_GAP;
        int numColumnas = Math.max(1, (int) (anchoGridDisponible / anchoTarjetaEstimado));
        return Math.min(numColumnas, 5);
    }

    private void adaptarContenidoAlAnchoYPopular(double anchoVentana) {
        if (dogGrid == null || listaDePerrosOriginal == null || mainBorderPane == null) return;
        double paddingLateralTotalVBox = mainBorderPane.getPadding().getLeft() + mainBorderPane.getPadding().getRight();
        if (mainBorderPane.getCenter() instanceof VBox) {
            VBox centerVBox = (VBox) mainBorderPane.getCenter();
            paddingLateralTotalVBox += centerVBox.getPadding().getLeft() + centerVBox.getPadding().getRight();
        }
        double paddingLateralScrollPane = (dogScrollPane != null && dogScrollPane.getPadding() != null) ? dogScrollPane.getPadding().getLeft() + dogScrollPane.getPadding().getRight() : 0;
        double paddingLateralGrid = dogGrid.getPadding().getLeft() + dogGrid.getPadding().getRight();
        double anchoDisponibleParaGrid = anchoVentana - paddingLateralTotalVBox - paddingLateralScrollPane - paddingLateralGrid - 20;
        int nuevasColumnas = calcularColumnasSegunAncho(anchoDisponibleParaGrid);
        if (dogGrid.getColumnConstraints().size() != nuevasColumnas ||
                (dogGrid.getChildren().isEmpty() && perrosMostradosActuales != null && !perrosMostradosActuales.isEmpty()) ||
                (perrosMostradosActuales != null && dogGrid.getChildren().size() != perrosMostradosActuales.size()) ) {
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

    private void adaptarContenidoALaAltura(double nuevaAltura) {
        if (dogScrollPane != null && mainBorderPane != null && mainBorderPane.getCenter() instanceof VBox) {
            Node topNode = mainBorderPane.getTop();
            VBox centerVBox = (VBox) mainBorderPane.getCenter();
            Node bottomNode = mainBorderPane.getBottom();
            double topHeight = (topNode != null) ? topNode.getBoundsInParent().getHeight() : HEADER_HEIGHT_ESTIMADA;
            double bottomHeight = (bottomNode != null) ? bottomNode.getBoundsInParent().getHeight() : BOTTOM_BAR_HEIGHT_ESTIMADA;
            double vBoxPaddingVertical = centerVBox.getPadding().getTop() + centerVBox.getPadding().getBottom();
            double hBoxBotonesFiltroHeight = 0;
            if (!centerVBox.getChildren().isEmpty() && centerVBox.getChildren().get(0) instanceof HBox) {
                HBox filterButtonHBox = (HBox) centerVBox.getChildren().get(0);
                hBoxBotonesFiltroHeight = filterButtonHBox.getHeight() > 0 ? filterButtonHBox.getHeight() : 40;
            }
            double alturaDisponibleParaScroll = nuevaAltura - topHeight - hBoxBotonesFiltroHeight - vBoxPaddingVertical - bottomHeight - 20;
            dogScrollPane.setPrefHeight(Math.max(MIN_SCROLLPANE_HEIGHT, alturaDisponibleParaScroll));
        }
    }

    private void popularGridDePerros() {
        if (dogGrid == null || perrosMostradosActuales == null) return;
        dogGrid.getChildren().clear();
        int numColumnas = Math.max(1, dogGrid.getColumnConstraints().size());
        int columnaActual = 0; int filaActual = 0;
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

    @FXML void Reservar(ActionEvent event) {
        System.out.println("Botón Reservar para volver a main perros presionado");
        String reservarFxml = "/com/proyectointegral2/Vista/Main.fxml";
        String reservartitulo = "Panel de Main";
        UtilidadesVentana.cambiarEscena(reservarFxml, reservartitulo, true);
         }
    @FXML void Adopciones(ActionEvent event) {
        System.out.println("Botón Adopciones presionado");
        String AdopcionesFxml = "/com/proyectointegral2/Vista/AdopcionesPanel.fxml";
        String Adopcionestitulo = "Panel de Adopciones";
        UtilidadesVentana.cambiarEscena(AdopcionesFxml, Adopcionestitulo, true);
    }

    @FXML void Eventos(ActionEvent event) {
            System.out.println("Botón Eventos presionado");
            String eventosFxml = "/com/proyectointegral2/Vista/EventosPanel.fxml";
            String eventostitulo = "Panel de Eventos";
            UtilidadesVentana.cambiarEscena(eventosFxml, eventostitulo, true);
    }

    @FXML
    void Bandeja(MouseEvent event) {
        System.out.println("Abriendo bandeja de citas como pop-up...");
        Usuario usuarioActual = SesionUsuario.getUsuarioLogueado(); // Obtener de la sesión
        if (usuarioActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Sesión", "No se pudo identificar el usuario para ver sus citas.");
            return;
        }
        int idCliente = usuarioActual.getIdUsuario();

        String bandejaFxml = "/com/proyectointegral2/Vista/BandejaCitas.fxml";
        String titulo = "Mis Citas Programadas";
        Stage ownerStage = (event.getSource() instanceof Node) ? (Stage) ((Node) event.getSource()).getScene().getWindow() : UtilidadesVentana.getPrimaryStage();

        BandejaCitasController bandejaController = UtilidadesVentana.mostrarVentanaPopup(bandejaFxml, titulo, true, ownerStage);
        if (bandejaController != null) {
            bandejaController.initData(idCliente);
            System.out.println("Pop-up de bandeja de citas mostrado.");
        } else {
            System.err.println("No se pudo mostrar el pop-up de bandeja de citas.");
        }
    }

    @FXML
    void DetallesUsuario(MouseEvent event) {
        System.out.println("Icono Usuario presionado - Navegando a Perfil");
        Usuario usuarioActual = SesionUsuario.getUsuarioLogueado();

        if (usuarioActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Sesión", "No se pudo identificar el usuario actual.");
            return;
        }

        int idUsuario = usuarioActual.getIdUsuario();
        String nombreUsuario = usuarioActual.getNombreUsu();

        if (idUsuario > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectointegral2/Vista/PerfilUsuario.fxml"));
                Parent root = loader.load();
                root.getProperties().put("fxmlLocation", "/com/proyectointegral2/Vista/PerfilUsuario.fxml");
                PerfilUsuarioController perfilController = loader.getController();
                if (perfilController != null) {
                    perfilController.initData(idUsuario, nombreUsuario);
                    UtilidadesVentana.cambiarEscenaConRoot(root, "Mi Perfil (" + nombreUsuario + ")", false);
                } else { UtilidadesVentana.mostrarAlertaError("Error Interno", "No se pudo cargar el controlador del perfil.");}
            } catch (Exception e) {
                e.printStackTrace();
                UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir la vista del perfil: " + e.getMessage());
            }
        } else { UtilidadesVentana.mostrarAlertaError("Error de Sesión", "ID de usuario no válido.");}
    }

    @FXML
    void onSearchIconClicked(MouseEvent event) {
        filtrarYRepopularPerros(searchTextField.getText());
    }
    @FXML void onSearchTextFieldAction(ActionEvent event) {
        filtrarYRepopularPerros(searchTextField.getText());
    }
}