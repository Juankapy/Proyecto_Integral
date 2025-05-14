package com.proyectointegral2.Controller;

import com.proyectointegral2.utils.UtilidadesVentana; // IMPORTANTE
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.InputStream;
import java.util.Objects; // Para Objects.requireNonNullElse

public class MainProtectoraController {

    @FXML private ImageView logoImageView;
    @FXML private ImageView cartIcon;
    @FXML private ImageView userIcon;

    @FXML private Button BtnNuevoPerro;
    @FXML private GridPane dogGrid;

    @FXML private Label lblRegistroTitulo;
    @FXML private Button btnToggleRegistro;
    @FXML private StackPane tablasStackPane;

    @FXML private TableView<RegistroPerroEntry> tablaRegistroPerros;
    @FXML private TableColumn<RegistroPerroEntry, String> colPerroNombre;
    @FXML private TableColumn<RegistroPerroEntry, String> colPerroFecha;
    @FXML private TableColumn<RegistroPerroEntry, String> colPerroEstado;
    @FXML private TableColumn<RegistroPerroEntry, String> colPerroNotas;

    @FXML private TableView<RegistroAdopcionEntry> tablaRegistroAdopciones;
    @FXML private TableColumn<RegistroAdopcionEntry, String> colAdopNombrePerro;
    @FXML private TableColumn<RegistroAdopcionEntry, String> colAdopFecha;
    @FXML private TableColumn<RegistroAdopcionEntry, String> colAdopHora;
    @FXML private TableColumn<RegistroAdopcionEntry, String> colAdopCausante;
    @FXML private TableColumn<RegistroAdopcionEntry, String> colAdopContacto;

    private boolean mostrandoRegistroPerros = true;

    private final ObservableList<RegistroPerroEntry> datosRegistroPerros = FXCollections.observableArrayList();
    private final ObservableList<RegistroAdopcionEntry> datosRegistroAdopciones = FXCollections.observableArrayList();

    private final String RUTA_ICONO_SWITCH = "/assets/Imagenes/iconos/switch_icon_placeholder.png";
    private final String RUTA_ICONO_PLACEHOLDER = "/assets/Imagenes/iconos/placeholder_image.png";


    @FXML
    public void initialize() {
        configurarIconoBotonToggle();
        cargarTarjetasPerroEjemplo();
        configurarTablas();
        cargarDatosEjemploTablas();
        asignarDatosALasTablas();
        actualizarVistaRegistros();
    }

    private void configurarIconoBotonToggle() {
        try {
            InputStream stream = getClass().getResourceAsStream(RUTA_ICONO_SWITCH);
            if (stream != null) {
                ImageView toggleIcon = new ImageView(new Image(stream));
                toggleIcon.setFitHeight(16.0);
                toggleIcon.setFitWidth(16.0);
                btnToggleRegistro.setGraphic(toggleIcon);
            } else {
                System.err.println("No se pudo cargar el icono para btnToggleRegistro desde: " + RUTA_ICONO_SWITCH);
            }
        } catch (Exception e) {
            System.err.println("Error cargando icono para btnToggleRegistro: " + e.getMessage());
        }
    }

    private void configurarTablas() {
        // Configurar Tabla Registro Perros
        colPerroNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPerroFecha.setCellValueFactory(new PropertyValueFactory<>("fechaIngreso"));
        colPerroEstado.setCellValueFactory(new PropertyValueFactory<>("estadoActual"));
        colPerroNotas.setCellValueFactory(new PropertyValueFactory<>("notasAdicionales"));
        tablaRegistroPerros.setPlaceholder(new Label("No hay registros de perros para mostrar."));

        // Configurar Tabla Registro Adopciones
        colAdopNombrePerro.setCellValueFactory(new PropertyValueFactory<>("nombrePerro"));
        colAdopFecha.setCellValueFactory(new PropertyValueFactory<>("fechaAdopcion"));
        colAdopHora.setCellValueFactory(new PropertyValueFactory<>("horaAdopcion"));
        colAdopCausante.setCellValueFactory(new PropertyValueFactory<>("nombreAdoptante"));
        colAdopContacto.setCellValueFactory(new PropertyValueFactory<>("contactoAdoptante"));
        tablaRegistroAdopciones.setPlaceholder(new Label("No hay registros de adopciones para mostrar."));
    }

    private void cargarDatosEjemploTablas() {
        datosRegistroPerros.addAll(
                new RegistroPerroEntry("Bobby", "01/01/2023", "En adopción", "Amigable, juguetón."),
                new RegistroPerroEntry("Luna", "15/02/2023", "En acogida", "Tímida al principio."),
                new RegistroPerroEntry("Max", "10/03/2023", "Adoptado", "Adoptado por la familia Pérez."),
                new RegistroPerroEntry("Rocky", "05/04/2024", "En adopción", "Energético.")
        );

        datosRegistroAdopciones.addAll(
                new RegistroAdopcionEntry("Max", "10/03/2023", "10:00", "Familia Pérez", "perez@example.com"),
                new RegistroAdopcionEntry("Bella", "20/04/2024", "15:30", "Juan García", "juan.g@example.com")
        );
    }

    private void asignarDatosALasTablas() {
        tablaRegistroPerros.setItems(datosRegistroPerros);
        tablaRegistroAdopciones.setItems(datosRegistroAdopciones);
    }

    @FXML
    void toggleRegistroView(ActionEvent event) {
        mostrandoRegistroPerros = !mostrandoRegistroPerros;
        actualizarVistaRegistros();
    }

    private void actualizarVistaRegistros() {
        if (mostrandoRegistroPerros) {
            lblRegistroTitulo.setText("Registro de mis perros");
            btnToggleRegistro.setText("Registro de adopciones");
            tablaRegistroPerros.setVisible(true);
            tablaRegistroAdopciones.setVisible(false);
        } else {
            lblRegistroTitulo.setText("Registro de adopciones");
            btnToggleRegistro.setText("Registro de mis perros");
            tablaRegistroPerros.setVisible(false);
            tablaRegistroAdopciones.setVisible(true);
        }
    }

    @FXML
    void NuevoPerro(ActionEvent event) {
        // Navegar a la ventana de añadir perro (FormularioPerro.fxml)
        String formularioPerroFxml = "/com/proyectointegral2/Vista/FormularioPerro.fxml"; // RUTA CORREGIDA
        String titulo = "Añadir Nuevo Perro";
        // false indica que esta vista es de tamaño fijo.
        UtilidadesVentana.cambiarEscena(formularioPerroFxml, titulo, false);
        System.out.println("Botón Añadir Nuevo Perro presionado. Navegando a: " + formularioPerroFxml);
    }

    @FXML
    void Editar(MouseEvent event) {
        // Aquí necesitarías identificar qué perro se quiere editar.
        // Esto podría hacerse si el botón "Editar" está DENTRO de cada tarjeta
        // y tiene asociado el objeto Perro o su ID.
        // Por ahora, solo un placeholder:
        System.out.println("Acción Editar Perro (necesitas identificar cuál perro)");
        // Ejemplo de cómo podrías obtener datos si el evento viene de un nodo con UserData:
        // Node source = (Node) event.getSource();
        // Object userData = source.getUserData(); // O source.getParent().getUserData() etc.
        // if (userData instanceof TuClaseModeloPerro) {
        //    TuClaseModeloPerro perroAEditar = (TuClaseModeloPerro) userData;
        //    // UtilidadesVentana.cambiarEscena("/ruta/a/FormularioPerro.fxml?id=" + perroAEditar.getId(), "Editar Perro", false);
        // }
        UtilidadesVentana.mostrarAlertaInformacion("Editar Perro", "Funcionalidad de edición no implementada completamente.");
    }

    @FXML
    void Eliminar(MouseEvent event) {
        System.out.println("Acción Eliminar Perro (necesitas identificar cuál perro)");
        UtilidadesVentana.mostrarAlertaInformacion("Eliminar Perro", "Funcionalidad de eliminación no implementada completamente.");
    }

    private void cargarTarjetasPerroEjemplo() {
        dogGrid.getChildren().clear();

        Object[][] perrosData = {
                {"Rex", "2 años", "En adopción", "Pastor Alemán", "Juguetón y leal", "/assets/Imagenes/perros/perro1.jpg"},
                {"Nina", "5 meses", "En acogida", "Mestiza", "Muy cariñosa", "/assets/Imagenes/perros/perro2_placeholder.jpg"},
                {"Coco", "3 años", "Adoptado", "Labrador", "Ideal para familias", "/assets/Imagenes/perros/perro3_placeholder.jpg"},
                {"Thor", "1 año", "En adopción", "Husky", "Energético", "/assets/Imagenes/perros/perro4_placeholder.jpg"},
                {"Lola", "4 años", "En adopción", "Beagle", "Curiosa y amigable", "/assets/Imagenes/perros/perro5_placeholder.jpg"}
        };

        int column = 0;
        int row = 0;
        int maxColumns = dogGrid.getColumnConstraints().size();

        for (Object[] data : perrosData) {
            VBox dogCard = createDogCard(
                    (String) data[0], (String) data[1], (String) data[2],
                    (String) data[3], (String) data[4], (String) data[5]
                    // Aquí podrías pasar un objeto Perro completo si lo tienes
            );
            // dogCard.setUserData(objetoPerroCompleto); // Para usar en Editar/Eliminar
            dogGrid.add(dogCard, column, row);
            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createDogCard(String nombre, String edad, String estado, String raza, String descripcion, String imagePath) {
        VBox cardVBox = new VBox(0);
        cardVBox.setPrefWidth(220.0);
        cardVBox.setAlignment(Pos.TOP_LEFT);
        cardVBox.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        ImageView dogImageView = new ImageView();
        try {
            InputStream stream = getClass().getResourceAsStream(imagePath);
            Image img = (stream != null) ? new Image(stream) : new Image(Objects.requireNonNull(getClass().getResourceAsStream(RUTA_ICONO_PLACEHOLDER)));
            dogImageView.setImage(img);
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + imagePath + " o placeholder. " + e.getMessage());
            // Intentar cargar un placeholder de emergencia si todo falla
            try {
                dogImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(RUTA_ICONO_PLACEHOLDER))));
            } catch (Exception ex) { /* No hacer nada si ni el placeholder carga */ }
        }

        dogImageView.setFitHeight(180.0);
        dogImageView.setFitWidth(220.0);
        dogImageView.setPickOnBounds(true);

        Label nameAgeLabel = new Label(nombre + ", " + edad);
        nameAgeLabel.setAlignment(Pos.CENTER);
        nameAgeLabel.setPrefHeight(40.0);
        nameAgeLabel.setPrefWidth(220.0);
        nameAgeLabel.setStyle("-fx-background-color: #A9D18E; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        VBox detailsVBox = new VBox(4.0);
        detailsVBox.setStyle("-fx-padding: 10;");

        HBox statusHBox = new HBox(5.0);
        statusHBox.setAlignment(Pos.CENTER_LEFT);
        Text statusLabelText = new Text("Estado:");
        statusLabelText.setStyle("-fx-font-weight: bold;");
        TextFlow statusTextFlow = new TextFlow();
        Text statusValueText = new Text(estado);
        if ("En adopción".equalsIgnoreCase(estado)) statusValueText.setStyle("-fx-fill: #27AE60;");
        else if ("Adoptado".equalsIgnoreCase(estado)) statusValueText.setStyle("-fx-fill: #3498DB;");
        else if ("En acogida".equalsIgnoreCase(estado)) statusValueText.setStyle("-fx-fill: #F39C12;");
        statusTextFlow.getChildren().add(statusValueText);
        statusHBox.getChildren().addAll(statusLabelText, statusTextFlow);

        Label breedLabel = new Label("Raza: " + raza);
        Label descriptionLabel = new Label("Descripción: " + descripcion);
        descriptionLabel.setWrapText(true);

        detailsVBox.getChildren().addAll(statusHBox, breedLabel, descriptionLabel);

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox buttonsHBox = new HBox(10.0);
        buttonsHBox.setAlignment(Pos.CENTER_RIGHT);
        buttonsHBox.setStyle("-fx-padding: 0 10 10 10;");

        Button editButton = new Button("Editar");
        editButton.setStyle("-fx-background-color: transparent; -fx-border-color: #3498DB; -fx-text-fill: #3498DB; -fx-border-radius: 3; -fx-border-width: 1.5px;");
        editButton.setOnMouseClicked(this::Editar);

        Button deleteButton = new Button("Eliminar");
        deleteButton.setStyle("-fx-background-color: transparent; -fx-border-color: #E74C3C; -fx-text-fill: #E74C3C; -fx-border-radius: 3; -fx-border-width: 1.5px;");
        deleteButton.setOnMouseClicked(this::Eliminar);

        buttonsHBox.getChildren().addAll(editButton, deleteButton);

        cardVBox.getChildren().addAll(dogImageView, nameAgeLabel, detailsVBox, spacer, buttonsHBox);
        return cardVBox;
    }

    // --- Clases Modelo de Ejemplo ---
    public static class RegistroPerroEntry {
        private final SimpleStringProperty nombre;
        private final SimpleStringProperty fechaIngreso;
        private final SimpleStringProperty estadoActual;
        private final SimpleStringProperty notasAdicionales;

        public RegistroPerroEntry(String nombre, String fechaIngreso, String estadoActual, String notasAdicionales) {
            this.nombre = new SimpleStringProperty(nombre);
            this.fechaIngreso = new SimpleStringProperty(fechaIngreso);
            this.estadoActual = new SimpleStringProperty(estadoActual);
            this.notasAdicionales = new SimpleStringProperty(notasAdicionales);
        }
        public String getNombre() { return nombre.get(); }
        public String getFechaIngreso() { return fechaIngreso.get(); }
        public String getEstadoActual() { return estadoActual.get(); }
        public String getNotasAdicionales() { return notasAdicionales.get(); }
    }

    public static class RegistroAdopcionEntry {
        private final SimpleStringProperty nombrePerro;
        private final SimpleStringProperty fechaAdopcion;
        private final SimpleStringProperty horaAdopcion;
        private final SimpleStringProperty nombreAdoptante;
        private final SimpleStringProperty contactoAdoptante;

        public RegistroAdopcionEntry(String nombrePerro, String fechaAdopcion, String horaAdopcion, String nombreAdoptante, String contactoAdoptante) {
            this.nombrePerro = new SimpleStringProperty(nombrePerro);
            this.fechaAdopcion = new SimpleStringProperty(fechaAdopcion);
            this.horaAdopcion = new SimpleStringProperty(horaAdopcion);
            this.nombreAdoptante = new SimpleStringProperty(nombreAdoptante);
            this.contactoAdoptante = new SimpleStringProperty(contactoAdoptante);
        }
        public String getNombrePerro() { return nombrePerro.get(); }
        public String getFechaAdopcion() { return fechaAdopcion.get(); }
        public String getHoraAdopcion() { return horaAdopcion.get(); }
        public String getNombreAdoptante() { return nombreAdoptante.get(); }
        public String getContactoAdoptante() { return contactoAdoptante.get(); }
    }
}