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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import java.util.UUID;

public class FormularioPerroController {

    @FXML private ImageView imgIconoVolver;
    @FXML private TextField TxtNombrePerro;
    @FXML private DatePicker DatePickerFechaNacimiento;
    @FXML private TextField TxtRazaPerro;
    @FXML private ComboBox<String> CmbSexo;
    @FXML private TextArea TxtAreaPatologia;
    @FXML private ComboBox<String> CmbEstado;
    @FXML private ImageView ImgPreviewPerro;
    @FXML private Button btnSeleccionarImagen;
    @FXML private Button BtnAnadirPerro;
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
        try {
            perroDao = new PerroDao();
            razaDao = new RazaDao();
        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico DAO", "No se pudo inicializar el acceso a datos: " + e.getMessage());
            if (BtnAnadirPerro != null) BtnAnadirPerro.setDisable(true);
        }
        configurarComboBoxesIniciales();
        if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(null);
    }

    private void configurarComboBoxesIniciales() {
        if (CmbSexo != null) {
            CmbSexo.setItems(FXCollections.observableArrayList("Macho", "Hembra"));
        }
        if (CmbEstado != null) {
            CmbEstado.setItems(FXCollections.observableArrayList("En Adopción", "Adoptado", "En Acogida", "Reservado"));
            CmbEstado.setValue("En Adopción");
        }
    }

    public void initDataParaEdicion(Perro perro, int idProtectora) {
        this.perroAEditar = perro;
        this.idProtectoraDelPerro = idProtectora;

        TxtNombrePerro.setText(perro.getNombre());
        if (DatePickerFechaNacimiento != null) DatePickerFechaNacimiento.setValue(perro.getFechaNacimiento());
        CmbSexo.setValue(perro.getSexo());

        if (perro.getRaza() != null) {
            TxtRazaPerro.setText(perro.getRaza().getNombreRaza());
        } else {
            TxtRazaPerro.clear();
        }

        if (CmbEstado != null) {
            if (perro.isAdoptado()) CmbEstado.setValue("Adoptado");
            else CmbEstado.setValue(perro.getAdoptado().equalsIgnoreCase("R") ? "Reservado" : "En Adopción");
        }

        this.rutaFotoActualEnModelo = perro.getFoto();
        if (this.rutaFotoActualEnModelo != null && !this.rutaFotoActualEnModelo.isEmpty()) {
            try {
                String pathCompleto = this.rutaFotoActualEnModelo.startsWith("/") ? this.rutaFotoActualEnModelo : "/" + this.rutaFotoActualEnModelo;
                try (InputStream stream = getClass().getResourceAsStream(pathCompleto)) {
                    if (stream != null && ImgPreviewPerro != null) ImgPreviewPerro.setImage(new Image(stream));
                    else if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(null);
                }
            } catch (Exception e) { if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(null); }
        } else {
            if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(null);
        }
        this.archivoFotoSeleccionada = null;
        if (BtnAnadirPerro != null) BtnAnadirPerro.setText("Guardar Cambios");
    }

    public void initDataParaNuevoPerro(int idProtectora) {
        this.perroAEditar = null;
        this.idProtectoraDelPerro = idProtectora;
        limpiarCamposFormulario();
        if (BtnAnadirPerro != null) BtnAnadirPerro.setText("Añadir Perro");
    }

    private void limpiarCamposFormulario() {
        TxtNombrePerro.clear();
        if (DatePickerFechaNacimiento != null) DatePickerFechaNacimiento.setValue(null);
        if (CmbSexo != null) CmbSexo.getSelectionModel().clearSelection();
        TxtRazaPerro.clear();
        if (TxtAreaPatologia != null) TxtAreaPatologia.clear();
        if (CmbEstado != null) CmbEstado.setValue("En Adopción");
        if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(null);
        this.archivoFotoSeleccionada = null;
        this.rutaFotoActualEnModelo = null;
    }

    @FXML
    void handleSeleccionarFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto del Perro");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) btnSeleccionarImagen.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try (InputStream stream = new FileInputStream(selectedFile)) {
                Image image = new Image(stream);
                if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(image);
                this.archivoFotoSeleccionada = selectedFile;
                this.rutaFotoActualEnModelo = null;
            } catch (IOException e) {
                e.printStackTrace();
                UtilidadesVentana.mostrarAlertaError("Error de Imagen", "No se pudo cargar la imagen seleccionada.");
            }
        }
    }

    @FXML
    void AnadirPerro(ActionEvent event) {
        String nombre = TxtNombrePerro.getText();
        LocalDate fechaNac = null;
        if (DatePickerFechaNacimiento != null) fechaNac = DatePickerFechaNacimiento.getValue();
        String sexo = CmbSexo.getValue();
        String nombreRazaStr = TxtRazaPerro.getText();
        String estadoSeleccionado = CmbEstado.getValue();

        if (nombre == null || nombre.trim().isEmpty()) { UtilidadesVentana.mostrarAlertaError("Error", "El nombre es obligatorio."); return; }
        if (sexo == null) { UtilidadesVentana.mostrarAlertaError("Error", "El sexo es obligatorio."); return; }
        if (nombreRazaStr == null || nombreRazaStr.trim().isEmpty()) { UtilidadesVentana.mostrarAlertaError("Error", "La raza es obligatoria."); return; }
        if (fechaNac == null) { UtilidadesVentana.mostrarAlertaError("Error", "La fecha de nacimiento es obligatoria."); return; }
        if (fechaNac.isAfter(LocalDate.now())) { UtilidadesVentana.mostrarAlertaError("Error", "La fecha de nacimiento no puede ser futura."); return; }
        if (estadoSeleccionado == null) { UtilidadesVentana.mostrarAlertaError("Error", "El estado es obligatorio."); return; }
        if (this.idProtectoraDelPerro <= 0) { UtilidadesVentana.mostrarAlertaError("Error Interno", "Protectora no identificada."); return;}

        Raza razaObjeto = null;
        if (razaDao != null) {
            try {
                razaObjeto = razaDao.obtenerRazaPorNombre(nombreRazaStr.trim());
                if (razaObjeto == null) {
                    boolean crear = UtilidadesVentana.mostrarAlertaConfirmacion("Raza no encontrada",
                            "La raza '" + nombreRazaStr.trim() + "' no existe. ¿Desea crearla?");
                    if (crear) {
                        Raza nuevaRaza = new Raza(nombreRazaStr.trim());
                        int nuevoIdRaza = razaDao.crearRaza(nuevaRaza);
                        if (nuevoIdRaza > 0) {
                            nuevaRaza.setIdRaza(nuevoIdRaza);
                            razaObjeto = nuevaRaza;
                            UtilidadesVentana.mostrarAlertaInformacion("Raza Creada", "La raza '" + razaObjeto.getNombreRaza() + "' ha sido creada con ID: " + razaObjeto.getIdRaza());
                        } else { UtilidadesVentana.mostrarAlertaError("Error", "No se pudo crear la nueva raza."); return; }
                    } else { return; }
                }
            } catch (SQLException e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error BD", "Error al procesar la raza."); return; }
        } else { UtilidadesVentana.mostrarAlertaError("Error DAO", "Servicio de razas no disponible."); return; }

        if (razaObjeto == null || razaObjeto.getIdRaza() <= 0) {
            UtilidadesVentana.mostrarAlertaError("Error Raza", "No se pudo determinar la raza del perro. Operación cancelada.");
            return;
        }

        Perro perroParaGuardar;
        boolean esNuevo = (this.perroAEditar == null);

        if (esNuevo) {
            perroParaGuardar = new Perro();
            perroParaGuardar.setIdProtectora(this.idProtectoraDelPerro);
        } else {
            perroParaGuardar = this.perroAEditar;
        }

        perroParaGuardar.setNombre(nombre);
        perroParaGuardar.setFechaNacimiento(fechaNac);
        perroParaGuardar.setSexo(sexo);
        perroParaGuardar.setRaza(razaObjeto);

        if ("Adoptado".equalsIgnoreCase(estadoSeleccionado)) {
            perroParaGuardar.setAdoptado("S");
        } else {
            perroParaGuardar.setAdoptado("N");
        }

        String rutaDestinoFotoParaBD = null;
        if (archivoFotoSeleccionada != null) {
            try {
                String extension = obtenerExtensionArchivo(archivoFotoSeleccionada.getName());
                if (extension.isEmpty() || !(extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif"))) {
                    UtilidadesVentana.mostrarAlertaError("Error Foto", "La extensión de la imagen no es válida.");
                    return;
                }
                String nombreUnicoArchivo = UUID.randomUUID().toString() + "." + extension;
                Path destinoFileSystem = Paths.get(DIRECTORIO_IMAGENES_PERROS_FILESYSTEM + nombreUnicoArchivo);
                Files.createDirectories(destinoFileSystem.getParent());
                Files.copy(archivoFotoSeleccionada.toPath(), destinoFileSystem, StandardCopyOption.REPLACE_EXISTING);
                rutaDestinoFotoParaBD = RUTA_BASE_IMAGENES_PERROS_RESOURCES + nombreUnicoArchivo;
                rutaDestinoFotoParaBD = rutaDestinoFotoParaBD.replace("\\", "/");
            } catch (IOException e) { e.printStackTrace(); UtilidadesVentana.mostrarAlertaError("Error Foto", "No se pudo guardar la imagen."); return; }
        } else if (!esNuevo && rutaFotoActualEnModelo != null) {
            rutaDestinoFotoParaBD = rutaFotoActualEnModelo.replace("\\", "/");
        }
        perroParaGuardar.setFoto(rutaDestinoFotoParaBD);

        try {
            if (perroDao == null) { UtilidadesVentana.mostrarAlertaError("Error DAO", "Servicio de perros no disponible."); return; }
            if (esNuevo) {
                int nuevoId = perroDao.crearPerro(perroParaGuardar);
                if (nuevoId > 0) {
                    perroParaGuardar.setIdPerro(nuevoId);
                    UtilidadesVentana.mostrarAlertaInformacion("Éxito", "Perro '" + perroParaGuardar.getNombre() + "' añadido con ID: " + nuevoId);
                } else { UtilidadesVentana.mostrarAlertaError("Error Creación", "No se pudo crear el perro (ID no generado o error)."); return; }
            } else {
                boolean actualizado = perroDao.actualizarPerro(perroParaGuardar);
                if (actualizado) {
                    UtilidadesVentana.mostrarAlertaInformacion("Éxito", "Perro '" + perroParaGuardar.getNombre() + "' actualizado.");
                } else { UtilidadesVentana.mostrarAlertaError("Error Actualización", "No se pudo actualizar el perro."); return; }
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
    void Cancelar(MouseEvent event) {
        cerrarFormulario();
    }

    @FXML
    void Volver(MouseEvent event) {
        cerrarFormulario();
    }

    private void cerrarFormulario() {
        Node sourceNode = BtnCancelar != null ? BtnCancelar : BtnAnadirPerro;
        if(sourceNode != null && sourceNode.getScene() != null && sourceNode.getScene().getWindow() instanceof Stage) {
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.close();
        }
    }
}