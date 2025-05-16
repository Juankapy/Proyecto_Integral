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


    private final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.jpg";
    private List<Perro> listaDePerrosOriginal;
    private List<Perro> perrosMostradosActuales;


    private static final double HEADER_HEIGHT_ESTIMADA = 70;
    private static final double BOTTOM_BAR_HEIGHT_ESTIMADA = 80;
    private static final double SCROLLPANE_PADDING_VERTICAL = 40;
    private static final double MIN_SCROLLPANE_HEIGHT = 300;
    private static final double PREFERRED_GRID_WIDTH_FALLBACK = 1200;

    private static final double TARJETA_IMG_AREA_WIDTH = 160;
    private static final double TARJETA_IMG_AREA_HEIGHT = 160;

    @FXML
    public void initialize() {
        System.out.println("MainClienteController inicializado.");
        cargarDatosDePerrosOriginalesSimulados();
        this.perrosMostradosActuales = new ArrayList<>(this.listaDePerrosOriginal);

        configurarListenersDeVentana();
        configurarPlaceholderSearchTextField();
        configurarListenersDeVentana();

        Platform.runLater(() -> {
            if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null) {
                Stage stage = (Stage) mainBorderPane.getScene().getWindow();
                if (stage.getWidth() > 0 && !Double.isNaN(stage.getWidth())) {
                    System.out.println("Forzando adaptación inicial en initialize() con Platform.runLater. Ancho: " + stage.getWidth());
                    adaptarUIAlTamanoVentana(stage);
                } else {
                    System.out.println("WARN: Ancho de stage no disponible inmediatamente en initialize(). Esperando a setOnShown/listeners.");
                }
            } else {
                System.out.println("WARN: Escena o ventana no disponible en initialize() para forzar adaptación.");
            }
        });
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



    private VBox crearTarjetaPerro(Perro perro) {
        VBox card = new VBox(5); // Espaciado entre elementos de la tarjeta
        card.setPrefWidth(180); // Ancho preferido de la tarjeta (permite algo de padding alrededor de la imagen de 160)
        card.setMaxWidth(Double.MAX_VALUE);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2); -fx-padding: 10;");
        card.setMinHeight(230); // Para mantener una altura consistente

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
                InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO);
                if (placeholderStream != null) {
                    loadedImage = new Image(placeholderStream);
                    placeholderStream.close();
                } else {
                    System.err.println("ERROR CRITICO: No se pudo cargar ni el placeholder: " + RUTA_IMAGEN_PLACEHOLDER_PERRO);
                }
            }
            imgView.setImage(loadedImage);

        } catch (Exception e) {
            System.err.println("ERROR cargando imagen para " + perro.getNombre() + ": " + imagePath + ". " + e.getMessage());
            try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO)) {
                if (placeholderStream != null) imgView.setImage(new Image(placeholderStream));
            } catch (Exception ex) { /* Falló hasta el placeholder */ }
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
        lblNombrePerro.setWrapText(false); // Para que no haga wrap si el nombre es largo y el fondo es fijo
        lblNombrePerro.setPrefWidth(TARJETA_IMG_AREA_WIDTH); // Mismo ancho que el contenedor de imagen
        lblNombrePerro.setMaxWidth(TARJETA_IMG_AREA_WIDTH);
        lblNombrePerro.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS); // Poner puntos suspensivos si es muy largo


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
                if (event.getClickCount() == 1) { // Para que no se dispare también por el botón "Ver Más"
                    Node clickedNode = event.getPickResult().getIntersectedNode();
                    // Asegurarse de que el clic no fue directamente en el botón
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


    private void handleVerMas(Perro perro) { // Ahora usa tu modelo Perro
        System.out.println("Ver más sobre (pop-up): " + perro.getNombre());
        String detallesFxml = "/com/proyectointegral2/Vista/DetallesPerro.fxml";
        String titulo = "Detalles de " + perro.getNombre();

        Stage ownerStage = null;

        if (mainBorderPane != null && mainBorderPane.getScene() != null) {
            ownerStage = (Stage) mainBorderPane.getScene().getWindow();
        } else {
            ownerStage = UtilidadesVentana.getPrimaryStage(); // Fallback
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(detallesFxml));
            if (loader.getLocation() == null) {
                System.err.println("Error FXML no encontrado para DetallesPerro: " + detallesFxml);
                UtilidadesVentana.mostrarAlertaError("Error", "No se pudo encontrar la vista de detalles.");
                return;
            }
            Parent root = loader.load();

            DetallesPerroController controller = loader.getController();
            if (controller != null) {
                controller.initData(perro);
            } else {
                System.err.println("Error: Controlador para DetallesPerro.fxml no encontrado.");
                UtilidadesVentana.mostrarAlertaError("Error", "No se pudo preparar la vista de detalles.");
                return;
            }
            UtilidadesVentana.mostrarVentanaComoDialogo(root, titulo, ownerStage);

        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir la vista de detalles del perro: " + e.getMessage());
        }
    }

    private int calcularColumnasSegunAncho(double anchoVentana) {
        if (anchoVentana <= 0 || Double.isNaN(anchoVentana)) return 3;
        double anchoRealTarjetaConEspacio = 180 + (dogGrid.getHgap() > 0 ? dogGrid.getHgap() : 20) + 20; // 180 es el prefWidth del VBox de la tarjeta (o del imageContainer)
        int numColumnas = Math.max(1, (int) (anchoVentana / anchoRealTarjetaConEspacio));
        return Math.min(numColumnas, 5);
    }

    private void adaptarContenidoAlAnchoYPopular(double nuevoAncho) {
        if (dogGrid == null || listaDePerrosOriginal == null) {
            System.err.println("ERROR: dogGrid o listaDePerrosOriginal es null en adaptarContenidoAlAnchoYPopular.");
            return;
        }
        System.out.println("Adaptando contenido al ancho: " + nuevoAncho);
        int nuevasColumnas = calcularColumnasSegunAncho(nuevoAncho);

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
            popularGridDePerros();
        } else {
            System.out.println("No se necesita repopular/reconfigurar columnas.");
        }
    }

    private void adaptarContenidoALaAltura(double nuevaAltura) {
        System.out.println("Adaptar contenido a la altura: " + nuevaAltura);
        if (dogScrollPane != null && mainBorderPane != null) {
            Node topNode = mainBorderPane.getTop();
            Node centerVBoxNode = mainBorderPane.getCenter(); // Este es el VBox que contiene el HBox de botones y el ScrollPane
            Node bottomNode = mainBorderPane.getBottom();

            double topHeight = (topNode != null && topNode.getLayoutBounds().getHeight() > 0) ? topNode.getLayoutBounds().getHeight() : HEADER_HEIGHT_ESTIMADA;
            double bottomHeight = (bottomNode != null && bottomNode.getLayoutBounds().getHeight() > 0) ? bottomNode.getLayoutBounds().getHeight() : BOTTOM_BAR_HEIGHT_ESTIMADA;

            double vBoxPadding = 0;
            double hBoxBotonesHeight = 0;

            if (centerVBoxNode instanceof VBox) {
                VBox centerVBox = (VBox) centerVBoxNode;
                vBoxPadding = centerVBox.getPadding().getTop() + centerVBox.getPadding().getBottom();
                if (!centerVBox.getChildren().isEmpty() && centerVBox.getChildren().get(0) instanceof javafx.scene.layout.HBox) {
                    // Asumiendo que el primer hijo del VBox central es el HBox de botones de filtro
                    javafx.scene.layout.HBox filterButtonHBox = (javafx.scene.layout.HBox) centerVBox.getChildren().get(0);
                    hBoxBotonesHeight = filterButtonHBox.getLayoutBounds().getHeight() > 0 ? filterButtonHBox.getLayoutBounds().getHeight() : 40; // Fallback
                }
            }

            double alturaDisponibleParaScroll = nuevaAltura - topHeight - hBoxBotonesHeight - vBoxPadding - bottomHeight - SCROLLPANE_PADDING_VERTICAL;
            dogScrollPane.setPrefHeight(Math.max(MIN_SCROLLPANE_HEIGHT, alturaDisponibleParaScroll));
        }
    }

    private void popularGridDePerros() {
        if (dogGrid == null || perrosMostradosActuales == null) { return; }
        dogGrid.getChildren().clear();
        int numColumnas = dogGrid.getColumnConstraints().size();
        if (numColumnas == 0) {
            double currentWidth = (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null) ?
                    mainBorderPane.getScene().getWindow().getWidth() : PREFERRED_GRID_WIDTH_FALLBACK;
            adaptarContenidoAlAnchoYPopular(currentWidth);
            return;
        }
        System.out.println("Populando dogGrid con " + perrosMostradosActuales.size() + " perros en " + numColumnas + " columnas.");
        int columnaActual = 0; int filaActual = 0;
        for (Perro perro : perrosMostradosActuales) {
            VBox tarjetaPerro = crearTarjetaPerro(perro);
            dogGrid.add(tarjetaPerro, columnaActual, filaActual);
            columnaActual++;
            if (columnaActual >= numColumnas) { columnaActual = 0; filaActual++; }
        }
    }

    @FXML void Reservar(ActionEvent event) { System.out.println("Botón Reservar presionado"); }
    @FXML void Adopciones(ActionEvent event) { System.out.println("Botón Adopciones presionado"); }
    @FXML void Eventos(ActionEvent event) { System.out.println("Botón Eventos presionado"); }

    @FXML
    void Bandeja(MouseEvent event) {
        System.out.println("Abriendo bandeja de citas como pop-up...");

        String bandejaFxml = "/com/proyectointegral2/Vista/BandejasCitas.fxml";
        String titulo = "Mis Citas Programadas";

        Stage ownerStage = null;
        if (event.getSource() instanceof Node) {
            ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        } else {
            ownerStage = UtilidadesVentana.getPrimaryStage();
        }
        BandejaCitasController bandejaController = UtilidadesVentana.mostrarVentanaPopup(bandejaFxml, titulo, true, ownerStage);

        if (bandejaController != null) {
            System.out.println("Pop-up de bandeja de citas mostrado.");
        } else {
            System.err.println("No se pudo mostrar el pop-up de bandeja de citas.");
        }
    }

    @FXML
    void DetallesUsuario(MouseEvent event) {
        System.out.println("Icono Usuario presionado - Navegando a Perfil");
        int idUsuarioActual = obtenerIdSimuladoDelUsuario();
        String nombreUsuarioLogin = obtenerNombreUsuarioSimuladoDelLogin();
        if (idUsuarioActual > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectointegral2/Vista/PerfilUsuario.fxml"));
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
    private String obtenerNombreUsuarioSimuladoDelLogin() { return "cliente_test"; }

    @FXML
    void onSearchIconClicked(MouseEvent event) {
        filtrarYRepopularPerros(searchTextField.getText());
    }
    @FXML void onSearchTextFieldAction(ActionEvent event) {
        filtrarYRepopularPerros(searchTextField.getText());
    }

    private void filtrarYRepopularPerros(String textoBusqueda) {
        if (listaDePerrosOriginal == null) { cargarDatosDePerrosOriginalesSimulados(); }
        if (textoBusqueda == null || textoBusqueda.trim().isEmpty()) {
            this.perrosMostradosActuales = new ArrayList<>(this.listaDePerrosOriginal);
        } else {
            String textoBusquedaLower = textoBusqueda.toLowerCase().trim();
            this.perrosMostradosActuales = this.listaDePerrosOriginal.stream()
                    .filter(perro -> perro.getNombre().toLowerCase().contains(textoBusquedaLower) ||
                            (perro.getRaza() != null && perro.getRaza().getNombre().toLowerCase().contains(textoBusquedaLower)))
                    .collect(Collectors.toList());
        }
        if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null) {
            adaptarContenidoAlAnchoYPopular(mainBorderPane.getScene().getWindow().getWidth());
        } else { popularGridDePerros(); }
    }
}