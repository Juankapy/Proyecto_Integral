package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.dao.PerroDao;
import com.proyectointegral2.dao.RazaDao;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FormularioPerroController {

    @FXML private Label LblTituloFormulario;
    @FXML private TextField TxtNombrePerro;
    @FXML private DatePicker DatePickerFechaNacimiento;
    @FXML private ComboBox<String> ComboBoxSexo;
    @FXML private ComboBox<Raza> ComboBoxRaza;
    @FXML private CheckBox CheckBoxAdoptado;
    @FXML private ImageView ImgPreviewFoto;
    @FXML private Button BtnSeleccionarFoto;
    @FXML private TextArea TxtAreaDescripcion;
    @FXML private Button BtnGuardarPerro;
    @FXML private Button BtnCancelar;

    private Perro perroAEditar;
    private int idProtectoraDelPerro;
    private File archivoFotoSeleccionada;
    private String rutaFotoActualEnModelo;

    private PerroDao perroDao;
    private RazaDao razaDao;

    private final String RUTA_BASE_IMAGENES_PERROS_RESOURCES = "/assets/Imagenes/perros/";
    private final String DIRECTORIO_IMAGENES_PERROS_FILESYSTEM = "src/main/resources/assets/Imagenes/perros/";

    @FXML
    public void initialize() {
        System.out.println("FormularioPerroController inicializado.");
        try {
            perroDao = new PerroDao();
            razaDao = new RazaDao();
        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico DAO", "No se pudo inicializar el acceso a datos: " + e.getMessage());
            if (BtnGuardarPerro != null) BtnGuardarPerro.setDisable(true);
        }
        configurarComboBoxes();
        ImgPreviewFoto.setImage(null);
    }

    private void configurarComboBoxes() {
        if (ComboBoxSexo != null) {
            ComboBoxSexo.setItems(FXCollections.observableArrayList("Macho", "Hembra"));
        }
        if (ComboBoxRaza != null && razaDao != null) {
            try {
                List<Raza> razas = razaDao.obtenerTodasLasRazas(); // Necesitas este método en RazaDao
                if (razas != null) {
                    ComboBoxRaza.setItems(FXCollections.observableArrayList(razas));
                    ComboBoxRaza.setConverter(new javafx.util.StringConverter<Raza>() {
                        @Override public String toString(Raza raza) { return raza == null ? null : raza.getNombreRaza(); } // Usa getNombreRaza()
                        @Override public Raza fromString(String string) {
                            return ComboBoxRaza.getItems().stream().filter(r -> r.getNombreRaza().equals(string)).findFirst().orElse(null); // Usa getNombreRaza()
                        }
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
                UtilidadesVentana.mostrarAlertaError("Error BD", "No se pudieron cargar las razas.");
            }
        }
    }

    public void initDataParaEdicion(Perro perro, int idProtectora) {
        this.perroAEditar = perro;
        this.idProtectoraDelPerro = idProtectora;
        LblTituloFormulario.setText("Editar Perro: " + Objects.requireNonNullElse(perro.getNombre(), "Desconocido"));

        TxtNombrePerro.setText(perro.getNombre());
        DatePickerFechaNacimiento.setValue(perro.getFechaNacimiento());
        ComboBoxSexo.setValue(perro.getSexo());
        CheckBoxAdoptado.setSelected(perro.isAdoptado());

        if (perro.getRaza() != null && ComboBoxRaza.getItems() != null) {
            ComboBoxRaza.getItems().stream()
                    .filter(r -> r.getIdRaza() == perro.getRaza().getIdRaza()) // Usa getIdRaza()
                    .findFirst().ifPresent(ComboBoxRaza::setValue);
        }

        this.rutaFotoActualEnModelo = perro.getFoto();
        if (this.rutaFotoActualEnModelo != null && !this.rutaFotoActualEnModelo.isEmpty()) {
            try {
                String pathCompleto = this.rutaFotoActualEnModelo.startsWith("/") ? this.rutaFotoActualEnModelo : "/" + this.rutaFotoActualEnModelo;
                try (InputStream stream = getClass().getResourceAsStream(pathCompleto)) {
                    if (stream != null) ImgPreviewFoto.setImage(new Image(stream));
                    else ImgPreviewFoto.setImage(null);
                }
            } catch (Exception e) { ImgPreviewFoto.setImage(null); }
        } else {
            ImgPreviewFoto.setImage(null);
        }
        this.archivoFotoSeleccionada = null;
    }

    public void initDataParaNuevoPerro(int idProtectora) {
        this.perroAEditar = null;
        this.idProtectoraDelPerro = idProtectora;
        LblTituloFormulario.setText("Añadir Nuevo Perro");
        limpiarCamposFormulario();
    }

    private void limpiarCamposFormulario() {
        TxtNombrePerro.clear();
        DatePickerFechaNacimiento.setValue(null);
        ComboBoxSexo.getSelectionModel().clearSelection();
        ComboBoxRaza.getSelectionModel().clearSelection();
        CheckBoxAdoptado.setSelected(false);
        if (TxtAreaDescripcion != null) TxtAreaDescripcion.clear();
        ImgPreviewFoto.setImage(null);
        this.archivoFotoSeleccionada = null;
        this.rutaFotoActualEnModelo = null;
    }

    @FXML
    void handleSeleccionarFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto del Perro");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        Stage stage = (Stage) BtnSeleccionarFoto.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try (InputStream stream = new FileInputStream(selectedFile)) {
                Image image = new Image(stream);
                ImgPreviewFoto.setImage(image);
                this.archivoFotoSeleccionada = selectedFile;
                this.rutaFotoActualEnModelo = null;
            } catch (IOException e) {
                e.printStackTrace();
                UtilidadesVentana.mostrarAlertaError("Error de Imagen", "No se pudo cargar la imagen seleccionada.");
            }
        }
    }

    @FXML
    void handleGuardarPerro(ActionEvent event) {
        String nombre = TxtNombrePerro.getText();
        LocalDate fechaNac = DatePickerFechaNacimiento.getValue();
        String sexo = ComboBoxSexo.getValue();
        Raza raza = ComboBoxRaza.getValue();
        String descripcion = (TxtAreaDescripcion != null) ? TxtAreaDescripcion.getText() : null;

        if (nombre == null || nombre.trim().isEmpty()) { UtilidadesVentana.mostrarAlertaError("Error", "El nombre es obligatorio."); return; }
        if (sexo == null) { UtilidadesVentana.mostrarAlertaError("Error", "El sexo es obligatorio."); return; }
        if (raza == null) { UtilidadesVentana.mostrarAlertaError("Error", "La raza es obligatoria."); return; }
        if (fechaNac == null) { UtilidadesVentana.mostrarAlertaError("Error", "La fecha de nacimiento es obligatoria."); return; }
        if (fechaNac.isAfter(LocalDate.now())) { UtilidadesVentana.mostrarAlertaError("Error", "La fecha de nacimiento no puede ser futura."); return; }

        Perro perroParaGuardar;
        boolean esNuevo = (this.perroAEditar == null);

        if (esNuevo) {
            perroParaGuardar = new Perro();
            // ID se asignará por la BD o por el DAO.crearPerro
        } else {
            perroParaGuardar = this.perroAEditar;
        }

        perroParaGuardar.setNombre(nombre);
        perroParaGuardar.setFechaNacimiento(fechaNac);
        perroParaGuardar.setSexo(sexo);
        perroParaGuardar.setRaza(raza);
        boolean esAdoptado = CheckBoxAdoptado.isSelected();
        perroParaGuardar.setAdoptado(esAdoptado ? "S" : "N"); // Pasa "S" o "N"
        perroParaGuardar.setIdProtectora(this.idProtectoraDelPerro);

        String rutaDestinoFotoParaBD = null;
        if (archivoFotoSeleccionada != null) {
            try {
                String extension = obtenerExtensionArchivo(archivoFotoSeleccionada.getName());
                String nombreUnicoArchivo = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
                Path destinoFileSystem = Paths.get(DIRECTORIO_IMAGENES_PERROS_FILESYSTEM + nombreUnicoArchivo);
                Files.createDirectories(destinoFileSystem.getParent());
                Files.copy(archivoFotoSeleccionada.toPath(), destinoFileSystem, StandardCopyOption.REPLACE_EXISTING);
                rutaDestinoFotoParaBD = RUTA_BASE_IMAGENES_PERROS_RESOURCES + nombreUnicoArchivo;
            } catch (IOException e) {
                e.printStackTrace();
                UtilidadesVentana.mostrarAlertaError("Error Guardar Foto", "No se pudo procesar la nueva imagen.");
                return;
            }
        } else if (!esNuevo && rutaFotoActualEnModelo != null) {
            rutaDestinoFotoParaBD = rutaFotoActualEnModelo;
        }
        perroParaGuardar.setFoto(rutaDestinoFotoParaBD);

        try {
            if (perroDao == null) throw new SQLException("PerroDao no está inicializado.");
            if (esNuevo) {
                // ----- CORRECCIÓN AQUÍ -----
                int nuevoId = perroDao.crearPerro(perroParaGuardar); // Llama al método correcto
                if (nuevoId > 0) {
                    perroParaGuardar.setIdPerro(nuevoId); // Actualizar el objeto con el ID generado
                    UtilidadesVentana.mostrarAlertaInformacion("Éxito", "Perro '" + perroParaGuardar.getNombre() + "' añadido con ID: " + nuevoId);
                } else {
                    UtilidadesVentana.mostrarAlertaError("Error", "No se pudo crear el perro (ID no generado).");
                    return; // No cerrar si falló
                }
                // ----- FIN CORRECCIÓN -----
            } else {
                boolean actualizado = perroDao.actualizarPerro(perroParaGuardar); // actualizarPerro ahora devuelve boolean
                if (actualizado) {
                    UtilidadesVentana.mostrarAlertaInformacion("Éxito", "Perro '" + perroParaGuardar.getNombre() + "' actualizado.");
                } else {
                    UtilidadesVentana.mostrarAlertaError("Error", "No se pudo actualizar el perro.");
                    return; // No cerrar si falló
                }
            }
            cerrarFormulario();
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Base de Datos", "No se pudo guardar el perro: " + e.getMessage());
        }
    }

    private String obtenerExtensionArchivo(String nombreArchivo) {
        int ultimoPunto = nombreArchivo.lastIndexOf('.');
        if (ultimoPunto > 0 && ultimoPunto < nombreArchivo.length() - 1) {
            return nombreArchivo.substring(ultimoPunto + 1).toLowerCase();
        }
        return "";
    }

    @FXML
    void handleCancelar(ActionEvent event) {
        cerrarFormulario();
    }

    private void cerrarFormulario() {
        Stage stage = (Stage) BtnCancelar.getScene().getWindow();
        stage.close();
        // Podrías necesitar notificar a MainProtectoraController para que refresque
    }
}