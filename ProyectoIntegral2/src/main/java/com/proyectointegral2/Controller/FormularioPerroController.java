package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.IdentificacionPatologia;
import com.proyectointegral2.Model.Patologia;
import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.dao.IdentificacionPatologiaDao;
import com.proyectointegral2.dao.PatologiaDao;
import com.proyectointegral2.dao.PerroDao;
import com.proyectointegral2.dao.RazaDao;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FormularioPerroController {

    // --- FXML Injections (Asegúrate que coincidan con tu FormularioPerro.fxml) ---
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
    // @FXML private TextArea TxtAreaDescripcion; // Comentado/Eliminado si Perro no tiene descripción

    // --- Instance Variables ---
    private Perro perroAEditar;
    private int idProtectoraDelPerro;
    private File archivoFotoSeleccionada;
    private String rutaFotoActualEnModelo;

    private PerroDao perroDao;
    private RazaDao razaDao;
    private PatologiaDao patologiaDao;
    private IdentificacionPatologiaDao identificacionPatologiaDao;

    // --- Constants ---
    private final String RUTA_BASE_IMAGENES_PERROS_RESOURCES = "/assets/Imagenes/perros/"; // Ruta que se guarda en BD
    private String DIRECTORIO_IMAGENES_PERROS_FILESYSTEM; // Ruta física donde se copia el archivo

    @FXML
    public void initialize() {
        System.out.println("FormularioPerroController inicializado.");
        try {
            perroDao = new PerroDao();
            razaDao = new RazaDao();
            patologiaDao = new PatologiaDao();
            identificacionPatologiaDao = new IdentificacionPatologiaDao();

            URL resourceUrl = getClass().getResource(RUTA_BASE_IMAGENES_PERROS_RESOURCES);
            if (resourceUrl != null && "file".equals(resourceUrl.getProtocol())) {
                DIRECTORIO_IMAGENES_PERROS_FILESYSTEM = Paths.get(resourceUrl.toURI()).toString();
            } else {
                Path userHomeDir = Paths.get(System.getProperty("user.home"));
                Path appImageDir = userHomeDir.resolve("DogpuccinoAppImages").resolve("perros");
                DIRECTORIO_IMAGENES_PERROS_FILESYSTEM = appImageDir.toString();
                System.out.println("WARN: Imágenes se guardarán en directorio externo: " + DIRECTORIO_IMAGENES_PERROS_FILESYSTEM);
            }
            Files.createDirectories(Paths.get(DIRECTORIO_IMAGENES_PERROS_FILESYSTEM));
            System.out.println("Directorio para guardar imágenes físicas: " + Paths.get(DIRECTORIO_IMAGENES_PERROS_FILESYSTEM).toAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico DAO/Rutas", "No se pudo inicializar: " + e.getMessage());
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
            CmbEstado.setItems(FXCollections.observableArrayList("En Adopción", "Adoptado"));
            CmbEstado.setValue("En Adopción");
        }
    }

    public void initDataParaEdicion(Perro perro, int idProtectora) {
        this.perroAEditar = perro;
        this.idProtectoraDelPerro = idProtectora;
        System.out.println("INFO: Editando perro ID: " + perro.getIdPerro() + ", Nombre: " + perro.getNombre());

        TxtNombrePerro.setText(perro.getNombre());
        DatePickerFechaNacimiento.setValue(perro.getFechaNacimiento());
        CmbSexo.setValue(perro.getSexo());

        if (perro.getRaza() != null) {
            TxtRazaPerro.setText(perro.getRaza().getNombreRaza());
        } else {
            TxtRazaPerro.clear();
        }

        if (CmbEstado != null) {
            CmbEstado.setValue(perro.isAdoptado() ? "Adoptado" : "En Adopción");
        }

        this.rutaFotoActualEnModelo = perro.getFoto();
        cargarImagenPreview(this.rutaFotoActualEnModelo);
        this.archivoFotoSeleccionada = null;

        cargarYMostrarPatologiasAsociadas(perro.getIdPerro());

        if (BtnAnadirPerro != null) BtnAnadirPerro.setText("Guardar Cambios");
    }

    public void initDataParaNuevoPerro(int idProtectora) {
        this.perroAEditar = null;
        this.idProtectoraDelPerro = idProtectora;
        System.out.println("INFO: Abriendo formulario para añadir nuevo perro para protectora ID: " + this.idProtectoraDelPerro);
        limpiarCamposFormulario();
        if (BtnAnadirPerro != null) BtnAnadirPerro.setText("Añadir Perro");
    }

    private void limpiarCamposFormulario() {
        TxtNombrePerro.clear();
        DatePickerFechaNacimiento.setValue(null);
        if (CmbSexo != null) CmbSexo.getSelectionModel().clearSelection();
        TxtRazaPerro.clear();
        if (TxtAreaPatologia != null) TxtAreaPatologia.clear();
        if (CmbEstado != null) CmbEstado.setValue("En Adopción");
        if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(null);
        this.archivoFotoSeleccionada = null;
        this.rutaFotoActualEnModelo = null;
    }

    private void cargarImagenPreview(String rutaClasspath) {
        if (ImgPreviewPerro == null) return;
        Image image = null;
        if (rutaClasspath != null && !rutaClasspath.isEmpty()) {
            try {
                String path = rutaClasspath.startsWith("/") ? rutaClasspath : "/" + rutaClasspath;
                try (InputStream stream = getClass().getResourceAsStream(path)) {
                    if (stream != null) image = new Image(stream);
                    else System.err.println("WARN: No se pudo cargar imagen de preview desde classpath: " + path);
                }
            } catch (Exception e) { System.err.println("ERROR al cargar imagen de preview (ruta: "+rutaClasspath+"): " + e.getMessage());}
        }
        ImgPreviewPerro.setImage(image);
    }

    private void cargarYMostrarPatologiasAsociadas(int idPerro) {
        if (TxtAreaPatologia == null || identificacionPatologiaDao == null || patologiaDao == null) {
            if(TxtAreaPatologia != null) TxtAreaPatologia.setText("Servicio de patologías no disponible.");
            return;
        }
        try {
            List<IdentificacionPatologia> identificaciones = identificacionPatologiaDao.obtenerIdentificacionesPorPerro(idPerro);
            if (identificaciones != null && !identificaciones.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < identificaciones.size(); i++) {
                    IdentificacionPatologia ip = identificaciones.get(i);
                    Patologia patologia = patologiaDao.obtenerPatologiaPorId(ip.getIdPatologia());
                    if (patologia != null) {
                        sb.append(patologia.getNombre());
                        String notasEspecificas = ip.getDescripcion();
                        if (notasEspecificas != null && !notasEspecificas.trim().isEmpty()) {
                            sb.append(": ").append(notasEspecificas);
                        }
                        if (i < identificaciones.size() - 1) {
                            sb.append("\n");
                        }
                    }
                }
                TxtAreaPatologia.setText(sb.toString());
            } else {
                TxtAreaPatologia.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error BD", "No se pudieron cargar las patologías: " + e.getMessage());
            TxtAreaPatologia.setText("Error al cargar patologías");
        }
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
            String extension = obtenerExtensionArchivo(selectedFile.getName());
            if (!esExtensionImagenValida(extension)) {
                UtilidadesVentana.mostrarAlertaError("Extensión Inválida", "Por favor, seleccione un archivo PNG, JPG, JPEG o GIF.");
                this.archivoFotoSeleccionada = null;
                cargarImagenPreview(this.rutaFotoActualEnModelo); // Volver a mostrar la original si la nueva es inválida
                return;
            }
            try (InputStream stream = new FileInputStream(selectedFile)) {
                Image image = new Image(stream);
                if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(image);
                this.archivoFotoSeleccionada = selectedFile;
                this.rutaFotoActualEnModelo = null; // Indicar que la foto original ya no se usará si se guarda
            } catch (IOException e) {
                e.printStackTrace();
                UtilidadesVentana.mostrarAlertaError("Error de Imagen", "No se pudo cargar la imagen: " + e.getMessage());
            }
        }
    }

    private boolean validarCamposObligatorios(String nombre, LocalDate fechaNac, String sexo, String nombreRazaStr, String estado) {
        if (nombre == null || nombre.trim().isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campo Obligatorio", "El nombre del perro es obligatorio.");
            TxtNombrePerro.requestFocus(); return false;
        }
        if (sexo == null || sexo.trim().isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campo Obligatorio", "El sexo del perro es obligatorio.");
            CmbSexo.requestFocus(); return false;
        }
        if (nombreRazaStr == null || nombreRazaStr.trim().isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campo Obligatorio", "La raza del perro es obligatoria.");
            TxtRazaPerro.requestFocus(); return false;
        }
        if (fechaNac == null) {
            UtilidadesVentana.mostrarAlertaError("Campo Obligatorio", "La fecha de nacimiento es obligatoria.");
            DatePickerFechaNacimiento.requestFocus(); return false;
        }
        if (fechaNac.isAfter(LocalDate.now())) {
            UtilidadesVentana.mostrarAlertaError("Fecha Inválida", "La fecha de nacimiento no puede ser futura.");
            DatePickerFechaNacimiento.requestFocus(); return false;
        }
        if (estado == null || estado.trim().isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campo Obligatorio", "El estado del perro es obligatorio.");
            CmbEstado.requestFocus(); return false;
        }
        if (this.idProtectoraDelPerro <= 0) {
            UtilidadesVentana.mostrarAlertaError("Error Interno del Sistema", "La protectora para este perro no ha sido identificada. ID recibido: " + this.idProtectoraDelPerro);
            return false;
        }
        return true;
    }

    private Raza procesarRaza(String nombreRazaStr) {
        if (razaDao == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Sistema", "El servicio de gestión de razas no está disponible.");
            return null;
        }
        try {
            Raza razaExistente = razaDao.obtenerRazaPorNombre(nombreRazaStr.trim());
            if (razaExistente != null) {
                return razaExistente;
            } else {
                if (UtilidadesVentana.mostrarAlertaConfirmacion("Raza no Encontrada",
                        "La raza '" + nombreRazaStr.trim() + "' no existe. ¿Desea crearla?")) {
                    Raza nuevaRaza = new Raza();
                    nuevaRaza.setNombreRaza(nombreRazaStr.trim());
                    int nuevoIdRaza = razaDao.crearRaza(nuevaRaza);
                    if (nuevoIdRaza > 0) {
                        nuevaRaza.setIdRaza(nuevoIdRaza);
                        UtilidadesVentana.mostrarAlertaInformacion("Raza Creada", "La raza '" + nuevaRaza.getNombreRaza() + "' ha sido creada.");
                        return nuevaRaza;
                    } else {
                        UtilidadesVentana.mostrarAlertaError("Error Creación Raza", "No se pudo crear la nueva raza.");
                    }
                }
            }
        } catch (SQLException e) {
            UtilidadesVentana.mostrarAlertaError("Error de BD", "Ocurrió un error al procesar la raza: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private String guardarImagenYObtenerRutaBD(boolean esNuevo) throws IOException {
        if (archivoFotoSeleccionada != null) {
            String extension = obtenerExtensionArchivo(archivoFotoSeleccionada.getName());
            // La validación de extensión se hizo en handleSeleccionarFoto, pero no está de más aquí si la lógica cambia
            if (!esExtensionImagenValida(extension) && !extension.isEmpty()) { // Permitir sin extensión si es el caso
                UtilidadesVentana.mostrarAlertaError("Extensión Inválida", "La imagen seleccionada no es un formato válido (png, jpg, jpeg, gif).");
                this.archivoFotoSeleccionada = null;
                return esNuevo ? null : this.rutaFotoActualEnModelo;
            }

            String nombreUnicoArchivo = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
            Path directorioDestino = Paths.get(DIRECTORIO_IMAGENES_PERROS_FILESYSTEM);

            if (!Files.exists(directorioDestino)) {
                Files.createDirectories(directorioDestino);
            }
            Path rutaDestinoCompletaFileSystem = directorioDestino.resolve(nombreUnicoArchivo);

            Files.copy(archivoFotoSeleccionada.toPath(), rutaDestinoCompletaFileSystem, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Nueva foto copiada a: " + rutaDestinoCompletaFileSystem);
            return (RUTA_BASE_IMAGENES_PERROS_RESOURCES + nombreUnicoArchivo).replace("\\", "/");
        } else if (!esNuevo && rutaFotoActualEnModelo != null) {
            return rutaFotoActualEnModelo;
        }
        return null;
    }

    @FXML
    void AnadirPerro(ActionEvent event) {
        String nombre = TxtNombrePerro.getText();
        LocalDate fechaNac = DatePickerFechaNacimiento.getValue();
        String sexo = CmbSexo.getValue();
        String nombreRazaStr = TxtRazaPerro.getText();
        String estadoSeleccionado = CmbEstado.getValue();
        String patologiasTextoInput = (TxtAreaPatologia != null) ? TxtAreaPatologia.getText().trim() : "";

        if (!validarCamposObligatorios(nombre, fechaNac, sexo, nombreRazaStr, estadoSeleccionado)) {
            return;
        }

        Raza razaObjeto = procesarRaza(nombreRazaStr);
        if (razaObjeto == null && !(nombreRazaStr == null || nombreRazaStr.trim().isEmpty())) {
            return;
        }

        String rutaFotoParaBD;
        try {
            rutaFotoParaBD = guardarImagenYObtenerRutaBD(perroAEditar == null);
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Foto", "No se pudo procesar o guardar la imagen: " + e.getMessage());
            return;
        }

        Perro perroParaGuardar;
        boolean esNuevo = (this.perroAEditar == null);

        if (esNuevo) {
            perroParaGuardar = new Perro();
            // El ID del perro se asignará por el DAO o la BD
        } else {
            perroParaGuardar = this.perroAEditar;
        }

        perroParaGuardar.setNombre(nombre);
        perroParaGuardar.setFechaNacimiento(fechaNac);
        perroParaGuardar.setSexo(sexo);
        perroParaGuardar.setRaza(razaObjeto);
        perroParaGuardar.setIdProtectora(this.idProtectoraDelPerro); // ASIGNAR EL ID DE LA PROTECTORA

        if ("Adoptado".equalsIgnoreCase(estadoSeleccionado)) {
            perroParaGuardar.setAdoptado("S");
        } else {
            perroParaGuardar.setAdoptado("N");
        }
        perroParaGuardar.setFoto(rutaFotoParaBD);

        try {
            if (perroDao == null) { UtilidadesVentana.mostrarAlertaError("Error DAO", "Servicio de perros no disponible."); return; }
            int idPerroGuardado;
            if (esNuevo) {
                System.out.println("Creando perro para Protectora ID: " + perroParaGuardar.getIdProtectora());
                idPerroGuardado = perroDao.crearPerro(perroParaGuardar);
                if (idPerroGuardado > 0) {
                    perroParaGuardar.setIdPerro(idPerroGuardado);
                    UtilidadesVentana.mostrarAlertaInformacion("Éxito", "Perro '" + perroParaGuardar.getNombre() + "' añadido con ID: " + idPerroGuardado);
                } else { UtilidadesVentana.mostrarAlertaError("Error Creación", "No se pudo crear el perro."); return; }
            } else {
                boolean actualizado = perroDao.actualizarPerro(perroParaGuardar);
                if (actualizado) {
                    UtilidadesVentana.mostrarAlertaInformacion("Éxito", "Perro '" + perroParaGuardar.getNombre() + "' actualizado.");
                    idPerroGuardado = perroParaGuardar.getIdPerro();
                } else { UtilidadesVentana.mostrarAlertaError("Error Actualización", "No se pudo actualizar el perro."); return; }
            }

            procesarYGuardarPatologiasAsociadas(idPerroGuardado, patologiasTextoInput);
            cerrarFormulario();
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Base de Datos", "No se pudo guardar el perro: " + e.getMessage());
        }
    }

    private void procesarYGuardarPatologiasAsociadas(int idPerro, String textoPatologiasInput) {
        if (idPerro <= 0 || identificacionPatologiaDao == null || patologiaDao == null) {
            System.err.println("No se pueden procesar patologías: idPerro inválido o DAOs nulos.");
            return;
        }
        String textoPatologiasCompleto = (textoPatologiasInput != null) ? textoPatologiasInput.trim() : "";

        try {
            identificacionPatologiaDao.eliminarPatologiasPorPerro(idPerro);
            System.out.println("INFO: Patologías previas para perro ID: " + idPerro + " eliminadas antes de actualizar.");

            if (!textoPatologiasCompleto.isEmpty()) {
                String[] lineasPatologias = textoPatologiasCompleto.split("\\r?\\n");

                for (String linea : lineasPatologias) {
                    linea = linea.trim();
                    if (linea.isEmpty()) continue;

                    String nombrePatologiaLimpio;
                    String notasEspecificasParaEstaAsociacion = "";

                    int separadorDosPuntos = linea.indexOf(':');
                    int inicioParentesis = linea.indexOf('(');
                    int finParentesis = linea.lastIndexOf(')');

                    if (separadorDosPuntos != -1) {
                        nombrePatologiaLimpio = linea.substring(0, separadorDosPuntos).trim();
                        if (separadorDosPuntos < linea.length() - 1) {
                            notasEspecificasParaEstaAsociacion = linea.substring(separadorDosPuntos + 1).trim();
                        }
                    } else if (inicioParentesis != -1 && finParentesis > inicioParentesis && finParentesis == linea.length() - 1) {
                        nombrePatologiaLimpio = linea.substring(0, inicioParentesis).trim();
                        notasEspecificasParaEstaAsociacion = linea.substring(inicioParentesis + 1, finParentesis).trim();
                    } else {
                        nombrePatologiaLimpio = linea.trim();
                    }

                    if (nombrePatologiaLimpio.isEmpty()) continue;

                    Patologia patologiaExistente = patologiaDao.obtenerPatologiaPorNombre(nombrePatologiaLimpio);
                    int idPatologiaAGuardar;

                    if (patologiaExistente == null) {
                        boolean crear = UtilidadesVentana.mostrarAlertaConfirmacion("Patología no Encontrada",
                                "La patología '" + nombrePatologiaLimpio + "' no existe. ¿Desea crearla?");
                        if (crear) {
                            Dialog<String> dialogDescGeneral = new Dialog<>();
                            dialogDescGeneral.setTitle("Nueva Patología: " + nombrePatologiaLimpio);
                            dialogDescGeneral.setHeaderText("Proporcione una descripción general para la nueva patología '" + nombrePatologiaLimpio + "' (opcional).");
                            try (InputStream iconStream = getClass().getResourceAsStream("/assets/Imagenes/iconos/info_icon.png")) {
                                if (iconStream != null) ((Stage)dialogDescGeneral.getDialogPane().getScene().getWindow()).getIcons().add(new Image(iconStream));
                            } catch (Exception e) { System.err.println("No se pudo cargar icono para diálogo de patología.");}

                            ButtonType guardarButtonType = new ButtonType("Guardar Descripción", ButtonBar.ButtonData.OK_DONE);
                            dialogDescGeneral.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

                            VBox contentVBox = new VBox(10);
                            contentVBox.setPadding(new Insets(10));
                            TextArea txtDescGeneralDialog = new TextArea();
                            txtDescGeneralDialog.setPromptText("Ej: Enfermedad común en razas pequeñas...");
                            txtDescGeneralDialog.setWrapText(true);
                            txtDescGeneralDialog.setPrefRowCount(4);
                            contentVBox.getChildren().addAll(new Label("Descripción para '" + nombrePatologiaLimpio + "':"), txtDescGeneralDialog);
                            dialogDescGeneral.getDialogPane().setContent(contentVBox);
                            Platform.runLater(txtDescGeneralDialog::requestFocus);

                            dialogDescGeneral.setResultConverter(dialogButton -> {
                                if (dialogButton == guardarButtonType) return txtDescGeneralDialog.getText();
                                return null;
                            });
                            Optional<String> resultadoDescGeneral = dialogDescGeneral.showAndWait();
                            String descripcionPatologiaGeneral = resultadoDescGeneral.orElse("").trim();

                            Patologia nuevaPatologia = new Patologia(0, nombrePatologiaLimpio, descripcionPatologiaGeneral);
                            idPatologiaAGuardar = patologiaDao.crearPatologia(nuevaPatologia);
                            if (idPatologiaAGuardar <= 0) {
                                UtilidadesVentana.mostrarAlertaError("Error Creación", "No se pudo crear la patología: " + nombrePatologiaLimpio);
                                continue;
                            }
                            System.out.println("INFO: Patología '" + nombrePatologiaLimpio + "' creada con ID: " + idPatologiaAGuardar);
                        } else { continue; }
                    } else {
                        idPatologiaAGuardar = patologiaExistente.getIdPatologia();
                    }
                    identificacionPatologiaDao.asignarPatologiaAPerro(idPerro, idPatologiaAGuardar, notasEspecificasParaEstaAsociacion);
                    System.out.println("INFO: Patología '" + nombrePatologiaLimpio + "' (ID: " + idPatologiaAGuardar + ") con notas '" + notasEspecificasParaEstaAsociacion + "' asignada al perro ID: " + idPerro);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error BD (Patologías)", "No se pudieron guardar las patologías asociadas: " + e.getMessage());
        }
    }

    private String obtenerExtensionArchivo(String nombreArchivo) {
        if (nombreArchivo == null) return "";
        int ultimoPunto = nombreArchivo.lastIndexOf('.');
        if (ultimoPunto > 0 && ultimoPunto < nombreArchivo.length() - 1) {
            return nombreArchivo.substring(ultimoPunto + 1).toLowerCase();
        }
        return "";
    }

    private boolean esExtensionImagenValida(String extension) {
        if (extension == null) return false;
        String extLower = extension.toLowerCase();
        return extLower.equals("png") || extLower.equals("jpg") || extLower.equals("jpeg") || extLower.equals("gif");
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
        Node sourceNode = null;
        if (BtnCancelar != null && BtnCancelar.getScene() != null) sourceNode = BtnCancelar;
        else if (BtnAnadirPerro != null && BtnAnadirPerro.getScene() != null) sourceNode = BtnAnadirPerro;
        else if (imgIconoVolver != null && imgIconoVolver.getScene() != null) sourceNode = imgIconoVolver;

        if(sourceNode != null && sourceNode.getScene().getWindow() instanceof Stage) {
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.close();
        } else {
            System.err.println("Error: No se pudo obtener el Stage para cerrar el formulario.");
        }
    }
}