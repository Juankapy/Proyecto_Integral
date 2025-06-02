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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private PatologiaDao patologiaDao;
    private IdentificacionPatologiaDao identificacionPatologiaDao;

    private final String RUTA_BASE_IMAGENES_PERROS_RESOURCES = "/assets/Imagenes/perros/";
    private String DIRECTORIO_IMAGENES_PERROS_FILESYSTEM;

    @FXML
    public void initialize() {
        System.out.println("FormularioPerroController inicializado.");
        try {
            perroDao = new PerroDao();
            razaDao = new RazaDao();
            patologiaDao = new PatologiaDao();
            identificacionPatologiaDao = new IdentificacionPatologiaDao();

            URL resourceUrl = getClass().getResource(RUTA_BASE_IMAGENES_PERROS_RESOURCES);
            if (resourceUrl != null) {
                if ("file".equals(resourceUrl.getProtocol())) {
                    DIRECTORIO_IMAGENES_PERROS_FILESYSTEM = Paths.get(resourceUrl.toURI()).toString();
                } else {
                    Path userHomeDir = Paths.get(System.getProperty("user.home"));
                    Path appImageDir = userHomeDir.resolve("DogpuccinoAppImages").resolve("perros");
                    DIRECTORIO_IMAGENES_PERROS_FILESYSTEM = appImageDir.toString();
                    System.out.println("WARN: Ejecutando desde JAR o protocolo no 'file'. Imágenes se guardarán en: " + DIRECTORIO_IMAGENES_PERROS_FILESYSTEM);
                }
                Files.createDirectories(Paths.get(DIRECTORIO_IMAGENES_PERROS_FILESYSTEM));
                System.out.println("Directorio para guardar imágenes: " + Paths.get(DIRECTORIO_IMAGENES_PERROS_FILESYSTEM).toAbsolutePath());

            } else {
                System.err.println("ERROR CRÍTICO: No se pudo determinar la ruta base para guardar imágenes en resources.");
                DIRECTORIO_IMAGENES_PERROS_FILESYSTEM = "temp_perro_images/";
                Files.createDirectories(Paths.get(DIRECTORIO_IMAGENES_PERROS_FILESYSTEM));
            }

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
        if (this.rutaFotoActualEnModelo != null && !this.rutaFotoActualEnModelo.isEmpty()) {
            try {
                String pathCompleto = this.rutaFotoActualEnModelo;
                if (!pathCompleto.startsWith("/")) pathCompleto = "/" + pathCompleto;
                try (InputStream stream = getClass().getResourceAsStream(pathCompleto)) {
                    if (stream != null && ImgPreviewPerro != null) ImgPreviewPerro.setImage(new Image(stream));
                    else if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(null);
                }
            } catch (Exception e) { if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(null); }
        } else {
            if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(null);
        }
        this.archivoFotoSeleccionada = null;

        cargarYMostrarPatologiasAsociadas(perro.getIdPerro());

        if (BtnAnadirPerro != null) BtnAnadirPerro.setText("Guardar Cambios");
    }


    public void initDataParaNuevoPerro(int idProtectora) {
        this.perroAEditar = null;
        this.idProtectoraDelPerro = idProtectora;
        System.out.println("Formulario para nuevo perro de protectora ID: " + this.idProtectoraDelPerro);
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

    private void cargarYMostrarPatologiasAsociadas(int idPerro) {
        if (TxtAreaPatologia == null || identificacionPatologiaDao == null || patologiaDao == null) {
            System.err.println("Componentes para patologías no listos.");
            if(TxtAreaPatologia != null) TxtAreaPatologia.setText("No se pudieron cargar patologías.");
            return;
        }
        try {
            List<IdentificacionPatologia> identificaciones = identificacionPatologiaDao.obtenerIdentificacionesPorPerro(idPerro);
            if (identificaciones != null && !identificaciones.isEmpty()) {
                String textoPatologias = identificaciones.stream()
                        .map(ip -> {
                            try {
                                Patologia p = patologiaDao.obtenerPatologiaPorId(ip.getIdPatologia());
                                if (p != null) {
                                    String nombrePat = p.getNombre();
                                    String notas = ip.getDescripcion();
                                    return nombrePat + (notas != null && !notas.trim().isEmpty() ? " (" + notas + ")" : "");
                                }
                            } catch (SQLException e) { e.printStackTrace(); }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(", "));
                TxtAreaPatologia.setText(textoPatologias);
            } else {
                TxtAreaPatologia.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error BD", "No se pudieron cargar las patologías del perro: " + e.getMessage());
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
                return;
            }
            try (InputStream stream = new FileInputStream(selectedFile)) {
                Image image = new Image(stream);
                if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(image);
                this.archivoFotoSeleccionada = selectedFile;
                this.rutaFotoActualEnModelo = null;
            } catch (IOException e) {
                e.printStackTrace();
                UtilidadesVentana.mostrarAlertaError("Error de Imagen", "No se pudo cargar la imagen seleccionada: " + e.getMessage());
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
                        "La raza '" + nombreRazaStr.trim() + "' no existe en la base de datos. ¿Desea crearla ahora?")) {

                    Raza nuevaRaza = new Raza();
                    nuevaRaza.setNombreRaza(nombreRazaStr.trim());

                    int nuevoIdRaza = razaDao.crearRaza(nuevaRaza);
                    if (nuevoIdRaza > 0) {
                        nuevaRaza.setIdRaza(nuevoIdRaza);
                        UtilidadesVentana.mostrarAlertaInformacion("Raza Creada", "La raza '" + nuevaRaza.getNombreRaza() + "' ha sido creada.");
                        return nuevaRaza;
                    } else {
                        UtilidadesVentana.mostrarAlertaError("Error Creación Raza", "No se pudo crear la nueva raza en la base de datos.");
                    }
                }
            }
        } catch (SQLException e) {
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "Ocurrió un error al procesar la raza: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private String guardarImagenYObtenerRutaBD(boolean esNuevo) throws IOException {
        if (archivoFotoSeleccionada != null) {
            String extension = obtenerExtensionArchivo(archivoFotoSeleccionada.getName());
            String nombreUnicoArchivo = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
            Path directorioDestino = Paths.get(DIRECTORIO_IMAGENES_PERROS_FILESYSTEM);

            if (!Files.exists(directorioDestino)) {
                Files.createDirectories(directorioDestino);
                System.out.println("Directorio de imágenes creado: " + directorioDestino.toAbsolutePath());
            }
            Path rutaDestinoCompleta = directorioDestino.resolve(nombreUnicoArchivo);

            Files.copy(archivoFotoSeleccionada.toPath(), rutaDestinoCompleta, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Nueva foto copiada a: " + rutaDestinoCompleta);
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
        String patologiasTexto = (TxtAreaPatologia != null) ? TxtAreaPatologia.getText().trim() : "";

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

            procesarYGuardarPatologiasAsociadas(idPerroGuardado, patologiasTexto);
            cerrarFormulario();
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Base de Datos", "No se pudo guardar el perro: " + e.getMessage());
        }
    }




    private void procesarYGuardarPatologiasAsociadas(int idPerro, String textoPatologiasInput) {
        if (idPerro <= 0 || identificacionPatologiaDao == null || patologiaDao == null || TxtAreaPatologia == null) {
            System.err.println("No se pueden procesar patologías: idPerro inválido o DAOs/TextArea nulos.");
            return;
        }
        String textoPatologias = textoPatologiasInput;

        try {
            identificacionPatologiaDao.eliminarPatologiasPorPerro(idPerro);
            System.out.println("INFO: Patologías previas eliminadas para el perro ID: " + idPerro);

            if (textoPatologias != null && !textoPatologias.trim().isEmpty()) {
                String[] patologiasEntradas = textoPatologias.split("\\s*[,;]\\s*");

                for (String entrada : patologiasEntradas) {
                    entrada = entrada.trim();
                    if (entrada.isEmpty()) continue;

                    String nombrePatologiaLimpio;
                    String notasEspecificas = "";

                    int inicioParentesis = entrada.indexOf('(');
                    int finParentesis = entrada.lastIndexOf(')');

                    if (inicioParentesis != -1 && finParentesis > inicioParentesis && finParentesis == entrada.length() - 1) {
                        nombrePatologiaLimpio = entrada.substring(0, inicioParentesis).trim();
                        notasEspecificas = entrada.substring(inicioParentesis + 1, finParentesis).trim();
                    } else {
                        nombrePatologiaLimpio = entrada.trim();
                    }

                    if (nombrePatologiaLimpio.isEmpty()) continue;

                    Patologia patologiaExistente = patologiaDao.obtenerPatologiaPorNombre(nombrePatologiaLimpio);
                    int idPatologiaAGuardar;

                    if (patologiaExistente == null) {
                        boolean crear = UtilidadesVentana.mostrarAlertaConfirmacion("Patología no encontrada",
                                "La patología '" + nombrePatologiaLimpio + "' no existe. ¿Desea crearla?");
                        if (crear) {
                            Patologia nuevaPatologia = new Patologia(0, nombrePatologiaLimpio, "");
                            idPatologiaAGuardar = patologiaDao.crearPatologia(nuevaPatologia);
                            if (idPatologiaAGuardar <= 0) {
                                System.err.println("ERROR: No se pudo crear la patología: " + nombrePatologiaLimpio);
                                continue;
                            }
                            System.out.println("INFO: Patología '" + nombrePatologiaLimpio + "' creada con ID: " + idPatologiaAGuardar);
                        } else { continue; }
                    } else {
                        idPatologiaAGuardar = patologiaExistente.getIdPatologia();
                    }
                    identificacionPatologiaDao.asignarPatologiaAPerro(idPerro, idPatologiaAGuardar, notasEspecificas);
                    System.out.println("INFO: Patología '" + nombrePatologiaLimpio + "' (ID: " + idPatologiaAGuardar + ") con notas '" + notasEspecificas + "' asignada al perro ID: " + idPerro);
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
        return extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif");
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
        if (BtnCancelar != null && BtnCancelar.getScene() != null) {
            sourceNode = BtnCancelar;
        } else if (BtnAnadirPerro != null && BtnAnadirPerro.getScene() != null) {
            sourceNode = BtnAnadirPerro;
        } else if (imgIconoVolver != null && imgIconoVolver.getScene() != null) {
            sourceNode = imgIconoVolver;
        }

        if (sourceNode != null && sourceNode.getScene().getWindow() instanceof Stage) {
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.close();
        } else {
            System.err.println("Error: No se pudo obtener el Stage para cerrar el formulario. " +
                    "El nodo de referencia puede no estar en la escena o la escena no tiene una ventana asociada.");
        }
    }
}