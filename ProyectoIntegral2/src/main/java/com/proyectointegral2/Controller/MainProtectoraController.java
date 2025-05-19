package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Raza; // Asumiendo que Perro tiene un objeto Raza
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.PerroDao;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.Model.SesionUsuario; // Usaremos este para obtener el usuario logueado
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainProtectoraController {

    @FXML private BorderPane mainBorderPane;
    @FXML private ImageView logoImageView;
    @FXML private ImageView ImgIconBandeja;
    @FXML private ImageView ImgIconUsuario;
    @FXML private Button BtnNuevoPerro;
    @FXML private GridPane dogGrid;
    @FXML private Label lblRegistroTitulo;
    @FXML private Button BtnToggleRegistro;
    @FXML private StackPane tablasStackPane;

    // Para las tablas, asegúrate de que los tipos genéricos coincidan con tus clases modelo
    // y que los fx:id estén en tu MainProtectora.fxml
    @FXML private TableView<Object> TablaRegistroPerros; // TODO: Cambiar Object a tu clase modelo (ej. RegistroPerroEntry)
    @FXML private TableColumn<Object, String> colPerroNombre;
    @FXML private TableColumn<Object, String> colPerroFecha;
    @FXML private TableColumn<Object, String> colPerroEstado;
    @FXML private TableColumn<Object, String> colPerroNotas;

    @FXML private TableView<Object> tablaRegistroAdopciones; // TODO: Cambiar Object a tu clase modelo (ej. RegistroAdopcionEntry)
    @FXML private TableColumn<Object, String> colAdopNombrePerro;
    @FXML private TableColumn<Object, String> colAdopFecha;
    @FXML private TableColumn<Object, String> colAdopHora;
    @FXML private TableColumn<Object, String> colAdopCausante;
    @FXML private TableColumn<Object, String> colAdopContacto;

    // Si tienes un Label específico para "no hay perros en el grid" en tu FXML
    // @FXML private Label lblNoPerrosEnGrid; // Descomenta si lo añades al FXML

    private final String RUTA_IMAGEN_PLACEHOLDER_PERRO = "/assets/Imagenes/iconos/placeholder_dog.png";
    private static final double HEADER_HEIGHT_ESTIMADA = 70.0;
    private static final double TARJETA_IMG_AREA_WIDTH = 160.0;
    private static final double TARJETA_IMG_AREA_HEIGHT = 160.0;
    private static final double TARJETA_PREF_WIDTH = 190.0; // Ancho de la tarjeta
    private static final double CARD_HORIZONTAL_GAP = 20.0;
    private static final double CARD_INTERNAL_PADDING = 10.0;
    private static final double MIN_GRID_HEIGHT = 250.0;


    private List<Perro> listaDePerrosDeLaProtectora;
    private PerroDao perroDao;
    private ProtectoraDao protectoraDao;

    private Usuario usuarioCuentaLogueada; // Objeto Usuario de la cuenta de la protectora
    private int idProtectoraActual;       // ID de la Protectora (de la tabla PROTECTORA)
    private String nombreProtectoraActual;

    private boolean mostrandoRegistroPerros = true; // VARIABLE DECLARADA E INICIALIZADA

    @FXML
    public void initialize() {
        System.out.println("MainProtectoraController inicializado.");
        this.perroDao = new PerroDao();
        this.protectoraDao = new ProtectoraDao();

        // Obtener información de la sesión
        this.usuarioCuentaLogueada = SesionUsuario.getUsuarioLogueado();
        this.idProtectoraActual = SesionUsuario.getEntidadIdEspecifica();

        if (this.usuarioCuentaLogueada == null || this.idProtectoraActual <= 0 ||
                !"PROTECTORA".equalsIgnoreCase(this.usuarioCuentaLogueada.getRol())) {
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sesión", "No hay información de protectora válida. Volviendo al login.");
            UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/Login.fxml", "Inicio de Sesión", false);
            return;
        }

        try {
            Protectora protectora = protectoraDao.obtenerProtectoraPorId(this.idProtectoraActual);
            if (protectora != null) {
                this.nombreProtectoraActual = protectora.getNombre();
                System.out.println("Protectora: " + this.nombreProtectoraActual + " (ID: " + this.idProtectoraActual + ") - Cuenta Usuario: " + this.usuarioCuentaLogueada.getNombreUsu());
            } else {
                UtilidadesVentana.mostrarAlertaError("Error de Datos", "No se pudo cargar la información de la protectora ID: " + this.idProtectoraActual);
                UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/Login.fxml", "Inicio de Sesión", false); // Volver al login
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Base de Datos", "No se pudo cargar info de protectora: " + e.getMessage());
            UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/Login.fxml", "Inicio de Sesión", false); // Volver al login
            return;
        }

        cargarYMostrarPerrosDeProtectora();
        configurarTablasDeRegistros();
        configurarVisibilidadInicialTablas();
        configurarListenersDeVentana(); // Llamar después de que los datos iniciales y la UI estén listos
    }


    private void configurarVisibilidadInicialTablas() {
        if (lblRegistroTitulo != null) lblRegistroTitulo.setText("Registro de Perros");
        if (BtnToggleRegistro != null) BtnToggleRegistro.setText("Ver Registro de Adopciones");
        if (TablaRegistroPerros != null) TablaRegistroPerros.setVisible(true);
        if (tablaRegistroAdopciones != null) tablaRegistroAdopciones.setVisible(false);
    }

    private void configurarTablasDeRegistros(){
        // TODO: Reemplaza 'Object' con tus clases modelo (ej. RegistroPerroEntry, RegistroAdopcionEntry)
        // y asegúrate que esas clases tengan getters para los PropertyValueFactory.
        if (TablaRegistroPerros != null) {
            // colPerroNombre.setCellValueFactory(new PropertyValueFactory<>("nombreDelPerro")); // Ejemplo
            // ... configurar otras columnas de TablaRegistroPerros ...
            TablaRegistroPerros.setPlaceholder(new Label("No hay registros de perros para mostrar en tabla."));
            // cargarDatosParaTablaRegistroPerros();
        }
        if (tablaRegistroAdopciones != null) {
            // colAdopNombrePerro.setCellValueFactory(new PropertyValueFactory<>("nombrePerroAdop")); // Ejemplo
            // ... configurar otras columnas de tablaRegistroAdopciones ...
            tablaRegistroAdopciones.setPlaceholder(new Label("No hay registros de adopciones para mostrar."));
            // cargarDatosParaTablaAdopciones();
        }
    }

    private void cargarYMostrarPerrosDeProtectora() {
        this.listaDePerrosDeLaProtectora = new ArrayList<>();
        if (perroDao == null || idProtectoraActual <= 0) {
            System.err.println("PerroDao no inicializado o ID Protectora inválido.");
            if (dogGrid != null) dogGrid.getChildren().clear();
            // if (lblNoPerrosEnGrid != null) lblNoPerrosEnGrid.setVisible(true); // Si tienes este label
            return;
        }

        try {
            this.listaDePerrosDeLaProtectora = perroDao.obtenerPerrosPorProtectora(this.idProtectoraActual);
            if (this.listaDePerrosDeLaProtectora == null) {
                this.listaDePerrosDeLaProtectora = new ArrayList<>();
            }
            System.out.println("Perros cargados para protectora " + nombreProtectoraActual + ": " + this.listaDePerrosDeLaProtectora.size());
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error DB", "No se pudieron cargar los perros: " + e.getMessage());
        }

        if (dogGrid != null) {
            if (mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null &&
                    ((Stage)mainBorderPane.getScene().getWindow()).isShowing()) {
                Platform.runLater(() -> adaptarUIAlTamanoVentana((Stage) mainBorderPane.getScene().getWindow()));
            } else {
                popularGridDePerrosProtectora();
            }
        }
    }

    private void configurarListenersDeVentana() {
        if (mainBorderPane != null && mainBorderPane.getScene() != null && mainBorderPane.getScene().getWindow() != null) {
            Stage stage = (Stage) mainBorderPane.getScene().getWindow();
            Runnable adaptar = () -> Platform.runLater(() -> {
                if (stage.isShowing() && stage.getWidth() > 0 && !Double.isNaN(stage.getWidth())) {
                    adaptarUIAlTamanoVentana(stage);
                }
            });
            if (stage.isShowing()) adaptar.run();
            else stage.setOnShown(event -> adaptar.run());
            stage.widthProperty().addListener((obs, o, n) -> adaptar.run());
            stage.heightProperty().addListener((obs, o, n) -> adaptar.run());
            stage.maximizedProperty().addListener((obs, o, n) -> adaptar.run());
        } else if (mainBorderPane != null) {
            mainBorderPane.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                        if (newWindow instanceof Stage) configurarListenersDeVentanaEspecificos((Stage)newWindow);
                    });
                    if (newScene.getWindow() instanceof Stage) configurarListenersDeVentanaEspecificos((Stage)newScene.getWindow());
                }
            });
        }
    }

    private void configurarListenersDeVentanaEspecificos(Stage stage) {
        Runnable adaptar = () -> Platform.runLater(() -> {
            if (stage.isShowing() && stage.getWidth() > 0 && !Double.isNaN(stage.getWidth())) {
                adaptarUIAlTamanoVentana(stage);
            }
        });
        if (stage.isShowing()) adaptar.run();
        else stage.setOnShown(event -> adaptar.run());
        stage.widthProperty().addListener((obs, o, n) -> adaptar.run());
        stage.heightProperty().addListener((obs, o, n) -> adaptar.run());
        stage.maximizedProperty().addListener((obs, o, n) -> adaptar.run());
    }

    private void adaptarUIAlTamanoVentana(Stage stage) {
        if (stage == null || stage.getWidth() <= 0 || Double.isNaN(stage.getWidth()) || dogGrid == null) return;
        double currentWidth = stage.getWidth();
        // double currentHeight = stage.getHeight(); // No se usa directamente para adaptar el grid
        System.out.println("Protectora - Adaptando UI al ancho: " + currentWidth);
        adaptarContenidoAlAnchoYPopular(currentWidth);
        // La altura del ScrollPane principal se maneja por el VBox contenedor y VGrow.
    }

    private int calcularColumnasSegunAncho(double anchoGridDisponible) {
        if (anchoGridDisponible <= 0) return 1;
        double hgap = (dogGrid.getHgap() > 0) ? dogGrid.getHgap() : CARD_HORIZONTAL_GAP;
        double anchoTarjetaEstimado = TARJETA_PREF_WIDTH + hgap;
        int numColumnas = Math.max(1, (int) (anchoGridDisponible / anchoTarjetaEstimado));
        return Math.min(numColumnas, 5); // Máximo 5 columnas
    }

    private void adaptarContenidoAlAnchoYPopular(double anchoVentana) {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null || mainBorderPane == null) return;

        double paddingLateralTotalVBox = 0;
        Node centerNode = mainBorderPane.getCenter(); // El ScrollPane principal
        if(centerNode instanceof ScrollPane) {
            Node scrollContent = ((ScrollPane) centerNode).getContent(); // El VBox dentro del ScrollPane
            if (scrollContent instanceof VBox) {
                VBox contentVBox = (VBox) scrollContent;
                paddingLateralTotalVBox = contentVBox.getPadding().getLeft() + contentVBox.getPadding().getRight();
            }
        }

        double paddingLateralGrid = dogGrid.getPadding().getLeft() + dogGrid.getPadding().getRight();
        double anchoDisponibleParaGrid = anchoVentana - paddingLateralTotalVBox - paddingLateralGrid - 40; // Margen extra/scrollbars

        int nuevasColumnas = calcularColumnasSegunAncho(anchoDisponibleParaGrid);

        // Comprobar si es necesario repopular
        boolean numColumnasCambio = dogGrid.getColumnConstraints().size() != nuevasColumnas;
        boolean gridVacioYHayDatos = dogGrid.getChildren().isEmpty() && !listaDePerrosDeLaProtectora.isEmpty();
        boolean datosCambiaron = dogGrid.getUserData() == null || !Objects.equals(dogGrid.getUserData(), listaDePerrosDeLaProtectora.hashCode()) || dogGrid.getChildren().size() != listaDePerrosDeLaProtectora.size();


        if (numColumnasCambio || gridVacioYHayDatos || datosCambiaron ) {
            if (numColumnasCambio) {
                dogGrid.getColumnConstraints().clear();
                for (int i = 0; i < nuevasColumnas; i++) {
                    ColumnConstraints colConst = new ColumnConstraints();
                    colConst.setPercentWidth(100.0 / nuevasColumnas);
                    colConst.setHgrow(Priority.SOMETIMES);
                    dogGrid.getColumnConstraints().add(colConst);
                }
                System.out.println("Protectora - Columnas dogGrid reconfiguradas a: " + nuevasColumnas);
            }
            popularGridDePerrosProtectora();
            dogGrid.setUserData(listaDePerrosDeLaProtectora.hashCode()); // Guardar un hash para detectar cambios en la lista
        }
    }

    private void popularGridDePerrosProtectora() {
        if (dogGrid == null || listaDePerrosDeLaProtectora == null) return;
        dogGrid.getChildren().clear();
        int numColumnas = Math.max(1, dogGrid.getColumnConstraints().size());

        if (listaDePerrosDeLaProtectora.isEmpty()) {
            // if (lblNoPerrosEnGrid != null) lblNoPerrosEnGrid.setVisible(true); // Si tienes este label
            System.out.println("No hay perros para mostrar para esta protectora.");
            return;
        }
        // if (lblNoPerrosEnGrid != null) lblNoPerrosEnGrid.setVisible(false);

        int columnaActual = 0; int filaActual = 0;
        for (Perro perro : listaDePerrosDeLaProtectora) {
            VBox tarjetaPerro = crearTarjetaPerroProtectora(perro);
            dogGrid.add(tarjetaPerro, columnaActual, filaActual);
            columnaActual++;
            if (columnaActual >= numColumnas) {
                columnaActual = 0;
                filaActual++;
            }
        }
    }

    private VBox crearTarjetaPerroProtectora(Perro perro) {
        VBox card = new VBox(5);
        card.setPrefWidth(TARJETA_PREF_WIDTH);
        card.setMaxWidth(TARJETA_PREF_WIDTH);
        card.setMinWidth(TARJETA_PREF_WIDTH);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(String.format("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-padding: %f;", CARD_INTERNAL_PADDING));
        card.setMinHeight(260); // Altura para acomodar botones

        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(TARJETA_IMG_AREA_WIDTH, TARJETA_IMG_AREA_HEIGHT);

        ImageView imgView = new ImageView();
        String imagePath = perro.getFoto();
        try {
            Image loadedImage = null;
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                if (!imagePath.startsWith("/")) { imagePath = "/" + imagePath; }
                try(InputStream stream = getClass().getResourceAsStream(imagePath)){
                    if (stream != null) loadedImage = new Image(stream);
                    else System.err.println("WARN: No se encontró imagen en " + imagePath + " para perro " + perro.getNombre());
                }
            }
            if (loadedImage == null || loadedImage.isError()) {
                try(InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_PERRO)){
                    if(placeholderStream != null) loadedImage = new Image(placeholderStream);
                    else System.err.println("ERROR CRITICO: Placeholder no encontrado: " + RUTA_IMAGEN_PLACEHOLDER_PERRO);
                }
            }
            imgView.setImage(loadedImage);
        } catch (Exception e) {System.err.println("Error img tarjeta protectora: " + perro.getNombre() + " " + e.getMessage());}

        imgView.setPreserveRatio(true); imgView.setSmooth(true);
        imgView.setFitWidth(TARJETA_IMG_AREA_WIDTH); imgView.setFitHeight(TARJETA_IMG_AREA_HEIGHT);
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
        card.setUserData(perro); // Guardar el objeto perro para referencia
        return card;
    }

    private void handleEditarPerro(Perro perro) {
        System.out.println("Editar perro ID: " + perro.getIdPerro() + ", Nombre: " + perro.getNombre());
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
                UtilidadesVentana.mostrarAlertaError("Error Interno", "Controlador de FormularioPerro no encontrado."); return;
            }
            UtilidadesVentana.mostrarVentanaComoDialogo(root, titulo, owner);
            cargarYMostrarPerrosDeProtectora(); // Refrescar la lista
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir el formulario de edición.");
        }
    }

    private void handleEliminarPerro(Perro perro) {
        System.out.println("Eliminar perro ID: " + perro.getIdPerro() + ", Nombre: " + perro.getNombre());
        boolean confirmado = UtilidadesVentana.mostrarAlertaConfirmacion(
                "Confirmar Eliminación",
                "¿Estás seguro de que quieres eliminar a " + perro.getNombre() + "? Esta acción también eliminará las citas y peticiones asociadas."
        );
        if (confirmado) {
            if (perroDao != null) {
                try {
                    boolean eliminado = perroDao.eliminarPerro(perro.getIdPerro()); // Asume que el DAO maneja eliminaciones en cascada o las haces aquí
                    if (eliminado) {
                        UtilidadesVentana.mostrarAlertaInformacion("Éxito", perro.getNombre() + " ha sido eliminado.");
                        cargarYMostrarPerrosDeProtectora(); // Recargar y repopular
                    } else { UtilidadesVentana.mostrarAlertaError("Error", "No se pudo eliminar el perro de la base de datos."); }
                } catch (SQLException e) {
                    e.printStackTrace();
                    UtilidadesVentana.mostrarAlertaError("Error Base de Datos", "No se pudo eliminar el perro: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    void NuevoPerro(ActionEvent event) {
        System.out.println("Botón Añadir Nuevo Perro presionado por Protectora: " + this.nombreProtectoraActual);
        String formularioPerroFxml = "/com/proyectointegral2/Vista/FormularioPerro.fxml";
        String titulo = "Añadir Nuevo Perro a " + this.nombreProtectoraActual;
        Stage ownerStage = (Stage) mainBorderPane.getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formularioPerroFxml));
            Parent root = loader.load();
            FormularioPerroController formController = loader.getController();
            if (formController != null) {
                formController.initDataParaNuevoPerro(this.idProtectoraActual);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Interno", "Controlador de FormularioPerro no encontrado."); return;
            }
            UtilidadesVentana.mostrarVentanaComoDialogo(root, titulo, ownerStage);
            cargarYMostrarPerrosDeProtectora();
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir el formulario para añadir perro.");
        }
    }

    @FXML
    void IrABandeja(MouseEvent event) {
        System.out.println("Protectora - Icono Bandeja presionado. Mostrando peticiones/citas.");
        // Aquí deberías abrir una nueva ventana (pop-up) o cambiar de vista para la bandeja de la protectora
        // String bandejaProtectoraFxml = "/com/proyectointegral2/Vista/BandejaProtectora.fxml";
        // String titulo = "Bandeja de " + this.nombreProtectoraActual;
        // Stage owner = (Stage) mainBorderPane.getScene().getWindow();
        // BandejaProtectoraController bpController = UtilidadesVentana.mostrarVentanaPopup(bandejaProtectoraFxml, titulo, true, owner);
        // if (bpController != null) bpController.initData(this.idProtectoraActual);
        UtilidadesVentana.mostrarAlertaInformacion("Próximamente", "Bandeja de peticiones de la protectora no implementada.");
    }

    @FXML
    void IrAPerfilUsuario(MouseEvent event) { // Este es el perfil de la CUENTA de la protectora
        System.out.println("Protectora - Icono Usuario (Perfil Cuenta) presionado.");
        if (usuarioCuentaLogueada == null) {
            UtilidadesVentana.mostrarAlertaError("Error Sesión", "No se pudo identificar la cuenta de la protectora.");
            return;
        }
        // String perfilCuentaFxml = "/com/proyectointegral2/Vista/PerfilCuentaProtectora.fxml"; // Un FXML para editar la cuenta
        // String titulo = "Editar Cuenta de " + this.nombreProtectoraActual;
        // FormularioUsuarioController formController = UtilidadesVentana.cambiarEscenaYObtenerController(perfilCuentaFxml, titulo, false);
        // if(formController != null) formController.initDataParaEdicion(this.usuarioCuentaLogueada);
        UtilidadesVentana.mostrarAlertaInformacion("Perfil Cuenta", "Funcionalidad para editar la cuenta de la protectora (ID Usuario: " + usuarioCuentaLogueada.getIdUsuario() + ") no implementada.");
    }

    @FXML
    void RegistroAdopciones(ActionEvent event) { // Renombrado de toggleRegistroView
        mostrandoRegistroPerros = !mostrandoRegistroPerros;
        if (mostrandoRegistroPerros) {
            lblRegistroTitulo.setText("Registro de Perros de la Protectora");
            BtnToggleRegistro.setText("Ver Registro de Adopciones");
            if (TablaRegistroPerros != null) TablaRegistroPerros.setVisible(true);
            if (tablaRegistroAdopciones != null) tablaRegistroAdopciones.setVisible(false);
            // cargarDatosParaTablaRegistroPerros();
        } else {
            lblRegistroTitulo.setText("Registro de Adopciones");
            BtnToggleRegistro.setText("Ver Registro de Perros");
            if (TablaRegistroPerros != null) TablaRegistroPerros.setVisible(false);
            if (tablaRegistroAdopciones != null) tablaRegistroAdopciones.setVisible(true);
            // cargarDatosParaTablaAdopciones();
        }
    }
}