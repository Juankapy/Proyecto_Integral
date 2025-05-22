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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controlador para el formulario de gestión de perros (creación y edición).
 * Permite al usuario ingresar y modificar información detallada sobre un perro,
 * incluyendo su nombre, fecha de nacimiento, sexo, raza, estado de adopción,
 * foto y patologías asociadas.
 * Interactúa con varios DAOs para persistir los datos en la base de datos.
 */
public class FormularioPerroController {

    // --- Componentes FXML ---
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

    // --- Estado del Controlador ---
    private Perro perroAEditar;
    private int idProtectoraDelPerro;
    private File archivoFotoSeleccionada;
    private String rutaFotoActualEnModelo;

    // --- DAOs (Data Access Objects) ---
    private PerroDao perroDao;
    private RazaDao razaDao;
    private PatologiaDao patologiaDao;
    private IdentificacionPatologiaDao identificacionPatologiaDao;

    // --- Constantes ---

    private static final String RUTA_BASE_IMAGENES_PERROS_RESOURCES = "/assets/Imagenes/perros/";
    private static final String DIRECTORIO_IMAGENES_PERROS_FILESYSTEM = "src/main/resources/assets/Imagenes/perros/";

    private static final String ESTADO_EN_ADOPCION = "En Adopción";
    private static final String ESTADO_ADOPTADO = "Adoptado";
    private static final String ESTADO_EN_ACOGIDA = "En Acogida";
    private static final String ESTADO_RESERVADO = "Reservado";
    private static final List<String> OPCIONES_ESTADO = Arrays.asList(
            ESTADO_EN_ADOPCION, ESTADO_ADOPTADO, ESTADO_EN_ACOGIDA, ESTADO_RESERVADO
    );
    private static final List<String> OPCIONES_SEXO = Arrays.asList("Macho", "Hembra");


    /**
     * Método de inicialización del controlador. Se llama automáticamente después de que
     * los campos FXML han sido inyectados.
     * Inicializa los DAOs y configura los componentes UI iniciales.
     */
    @FXML
    public void initialize() {
        try {
            perroDao = new PerroDao();
            razaDao = new RazaDao();
            patologiaDao = new PatologiaDao();
            identificacionPatologiaDao = new IdentificacionPatologiaDao();
        } catch (Exception e) {
            System.err.println("Error crítico al inicializar DAOs: " + e.getMessage());
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sistema",
                    "No se pudo inicializar el acceso a la base de datos: " + e.getMessage() + "\nEl formulario no funcionará correctamente. Por favor, contacte a soporte.");
            if (BtnAnadirPerro != null) BtnAnadirPerro.setDisable(true);
        }
        configurarComboBoxesIniciales();
        if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(null);
    }

    /**
     * Configura los ComboBoxes del formulario (Sexo y Estado) con sus respectivas opciones.
     */
    private void configurarComboBoxesIniciales() {
        if (CmbSexo != null) {
            CmbSexo.setItems(FXCollections.observableArrayList(OPCIONES_SEXO));
        }
        if (CmbEstado != null) {
            CmbEstado.setItems(FXCollections.observableArrayList(OPCIONES_ESTADO));
            CmbEstado.setValue(ESTADO_EN_ADOPCION);
        }
    }

    /**
     * Prepara el formulario para la edición de un perro existente.
     * Carga los datos del perro en los campos correspondientes.
     * @param perro El objeto Perro con los datos a editar.
     * @param idProtectora El ID de la protectora asociada al perro.
     */
    public void initDataParaEdicion(Perro perro, int idProtectora) {
        this.perroAEditar = perro;
        this.idProtectoraDelPerro = idProtectora;

        if (perro == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Datos", "No se recibió información del perro para editar. El formulario se cerrará.");
            cerrarFormulario();
            return;
        }

        TxtNombrePerro.setText(perro.getNombre());
        if (DatePickerFechaNacimiento != null) DatePickerFechaNacimiento.setValue(perro.getFechaNacimiento());
        if (CmbSexo != null) CmbSexo.setValue(perro.getSexo());

        if (perro.getRaza() != null && perro.getRaza().getNombreRaza() != null) {
            TxtRazaPerro.setText(perro.getRaza().getNombreRaza());
        } else {
            TxtRazaPerro.clear();
        }

        cargarYMostrarPatologiasAsociadas(perro.getIdPerro());

        if (CmbEstado != null) {
            String estadoModelo = perro.getAdoptado();
            if ("S".equalsIgnoreCase(estadoModelo)) CmbEstado.setValue(ESTADO_ADOPTADO);
            else if ("R".equalsIgnoreCase(estadoModelo)) CmbEstado.setValue(ESTADO_RESERVADO);
            else if ("A".equalsIgnoreCase(estadoModelo)) CmbEstado.setValue(ESTADO_EN_ACOGIDA);
            else CmbEstado.setValue(ESTADO_EN_ADOPCION);
        }

        this.rutaFotoActualEnModelo = perro.getFoto();
        cargarImagenPreview(this.rutaFotoActualEnModelo);

        this.archivoFotoSeleccionada = null;
        if (BtnAnadirPerro != null) BtnAnadirPerro.setText("Guardar Cambios");
    }

    /**
     * Prepara el formulario para añadir un nuevo perro.
     * Limpia los campos y establece el texto del botón principal.
     * @param idProtectora El ID de la protectora a la que se asociará el nuevo perro.
     */
    public void initDataParaNuevoPerro(int idProtectora) {
        this.perroAEditar = null;
        this.idProtectoraDelPerro = idProtectora;
        limpiarCamposFormulario();
        if (BtnAnadirPerro != null) BtnAnadirPerro.setText("Añadir Perro");
    }

    /**
     * Restablece todos los campos del formulario a su estado inicial o vacío.
     */
    private void limpiarCamposFormulario() {
        if (TxtNombrePerro != null) TxtNombrePerro.clear();
        if (DatePickerFechaNacimiento != null) DatePickerFechaNacimiento.setValue(null);
        if (CmbSexo != null) CmbSexo.getSelectionModel().clearSelection();
        if (TxtRazaPerro != null) TxtRazaPerro.clear();
        if (TxtAreaPatologia != null) TxtAreaPatologia.clear();
        if (CmbEstado != null) CmbEstado.setValue(ESTADO_EN_ADOPCION);
        if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(null);
        this.archivoFotoSeleccionada = null;
        this.rutaFotoActualEnModelo = null;
    }

    /**
     * Carga y muestra una imagen en el ImageView de previsualización.
     * Intenta cargar la imagen desde el classpath (recursos de la aplicación).
     * @param rutaImagenRelativaAResources
     * La ruta de la imagen, relativa a la carpeta 'resources'.
     * Debe comenzar con '/' si es desde la raíz de resources.
     */
    private void cargarImagenPreview(String rutaImagenRelativaAResources) {
        if (ImgPreviewPerro == null) return;
        String pathNormalizado = null;

        if (rutaImagenRelativaAResources != null && !rutaImagenRelativaAResources.isEmpty()) {
            try {
                pathNormalizado = rutaImagenRelativaAResources.startsWith("/")
                        ? rutaImagenRelativaAResources
                        : "/" + rutaImagenRelativaAResources.replace("\\", "/");

                try (InputStream stream = getClass().getResourceAsStream(pathNormalizado)) {
                    if (stream != null) {
                        ImgPreviewPerro.setImage(new Image(stream));
                    } else {
                        System.err.println("WARN: No se pudo cargar imagen de preview desde classpath: " + pathNormalizado);
                        ImgPreviewPerro.setImage(null);
                    }
                }
            } catch (Exception e) {
                ImgPreviewPerro.setImage(null);
                System.err.println("ERROR: Cargando imagen de preview: " +
                        (pathNormalizado != null ? pathNormalizado : rutaImagenRelativaAResources) +
                        " - " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            ImgPreviewPerro.setImage(null);
        }
    }

    /**
     * Carga las patologías asociadas a un perro desde la base de datos y las muestra
     * en el TextArea, separadas por comas.
     * @param idPerro El ID del perro cuyas patologías se van a cargar.
     */
    private void cargarYMostrarPatologiasAsociadas(int idPerro) {
        if (TxtAreaPatologia == null || identificacionPatologiaDao == null || patologiaDao == null) {
            System.err.println("Error: Componentes para patologías no están inicializados (TxtAreaPatologia o DAOs). No se pueden cargar.");
            return;
        }
        try {
            List<IdentificacionPatologia> identificaciones = identificacionPatologiaDao.obtenerPatologiasPorPerro(idPerro);
            List<String> nombresDePatologias = new ArrayList<>();

            if (identificaciones != null) {
                for (IdentificacionPatologia ident : identificaciones) {
                    if (ident == null) continue; // Seguridad extra

                    int idPatologiaActual = ident.getIdPatologia();
                    if (idPatologiaActual > 0) {

                        Patologia patologia = patologiaDao.obtenerPatologiaPorId(idPatologiaActual);

                        if (patologia != null && patologia.getNombre() != null && !patologia.getNombre().trim().isEmpty()) {
                            nombresDePatologias.add(patologia.getNombre());
                        } else {
                            System.err.println("WARN: No se encontró el nombre para la patología con ID: " + idPatologiaActual +
                                    " o el nombre es vacío, para el perro ID: " + idPerro);
                        }
                    } else {
                        System.err.println("WARN: Se encontró una identificación de patología con ID de patología inválido (<=0) para el perro ID: " + idPerro);
                    }
                }
            }
            TxtAreaPatologia.setText(String.join(", ", nombresDePatologias));

        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error al Cargar Patologías",
                    "No se pudieron cargar las patologías existentes del perro: " + e.getMessage());
            if (TxtAreaPatologia != null) TxtAreaPatologia.setText("Error al cargar patologías");
        }
    }


    /**
     * Abre un FileChooser para que el usuario seleccione una imagen para el perro.
     * Muestra una vista previa de la imagen seleccionada.
     * @param event El evento de acción que disparó este método.
     */
    @FXML
    void handleSeleccionarFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto del Perro");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Node sourceNode = (Node) event.getSource();
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try (InputStream stream = new FileInputStream(selectedFile)) {
                if (ImgPreviewPerro != null) ImgPreviewPerro.setImage(new Image(stream));
                this.archivoFotoSeleccionada = selectedFile;
                this.rutaFotoActualEnModelo = null;
            } catch (IOException e) {
                UtilidadesVentana.mostrarAlertaError("Error al Cargar Imagen",
                        "No se pudo cargar la imagen seleccionada: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Valida los campos obligatorios del formulario antes de intentar guardar.
     * @param nombre El nombre del perro.
     * @param fechaNac La fecha de nacimiento del perro.
     * @param sexo El sexo del perro.
     * @param nombreRazaStr El nombre de la raza ingresado.
     * @param estado El estado de adopción seleccionado.
     * @return true si todos los campos obligatorios son válidos, false en caso contrario.
     *         Muestra alertas de error si la validación falla.
     */
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
            UtilidadesVentana.mostrarAlertaError("Error Interno del Sistema", "La protectora para este perro no ha sido identificada. Por favor, contacte a soporte.");
            return false;
        }
        return true;
    }

    /**
     * Procesa la raza ingresada por el usuario.
     * Intenta obtener la raza de la base de datos. Si no existe, pregunta al usuario si desea crearla.
     * @param nombreRazaStr El nombre de la raza ingresado en el TextField.
     * @return Un objeto Raza si se procesa correctamente (existente o recién creada), o null si hay un error o el usuario cancela la creación.
     */
    private Raza procesarRaza(String nombreRazaStr) {
        if (razaDao == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Sistema", "El servicio de gestión de razas no está disponible.");
            return null;
        }
        try {
            Raza razaExistente = razaDao.obtenerRazaPorNombre(nombreRazaStr.trim());
            if (razaExistente != null) {
                return razaExistente; // La raza ya existe, usarla.
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

    /**
     * Guarda la imagen seleccionada (si hay una) en el sistema de archivos y devuelve
     * la ruta relativa a 'resources' para ser almacenada en la base de datos.
     * Si no se seleccionó una nueva imagen, devuelve la ruta de la imagen actual del modelo (si existe).
     * @return La ruta de la imagen para la base de datos, o null si no hay imagen o si ocurre un error.
     * @throws IOException Si ocurre un error de E/S al copiar el archivo de imagen.
     */
    private String guardarImagenYObtenerRutaBD() throws IOException {
        if (archivoFotoSeleccionada == null) {
            return (this.rutaFotoActualEnModelo != null) ? this.rutaFotoActualEnModelo.replace("\\", "/") : null;
        }


        String extension = obtenerExtensionArchivo(archivoFotoSeleccionada.getName());
        if (!esExtensionImagenValida(extension)) {
            UtilidadesVentana.mostrarAlertaError("Extensión de Imagen Inválida",
                    "El archivo seleccionado no es una imagen válida. Extensiones permitidas: png, jpg, jpeg, gif.");
            return null;
        }

        String nombreUnicoArchivo = UUID.randomUUID().toString() + "." + extension;
        Path directorioDestinoFileSystem = Paths.get(DIRECTORIO_IMAGENES_PERROS_FILESYSTEM);

        if (!Files.exists(directorioDestinoFileSystem)) {
            Files.createDirectories(directorioDestinoFileSystem);
        }
        Path rutaDestinoCompletaFileSystem = directorioDestinoFileSystem.resolve(nombreUnicoArchivo);

        Files.copy(archivoFotoSeleccionada.toPath(), rutaDestinoCompletaFileSystem, StandardCopyOption.REPLACE_EXISTING);

        return (RUTA_BASE_IMAGENES_PERROS_RESOURCES + nombreUnicoArchivo).replace("\\", "/");
    }

    /**
     * Maneja el evento del botón "Añadir Perro" o "Guardar Cambios".
     * Recolecta datos, valida, procesa raza e imagen, y guarda el perro en la BD.
     * También gestiona las patologías asociadas.
     * @param event El evento de acción.
     */
    @FXML
    void AnadirPerro(ActionEvent event) {
        // 1. Recolectar datos de la UI.
        String nombre = TxtNombrePerro.getText();
        LocalDate fechaNac = (DatePickerFechaNacimiento != null) ? DatePickerFechaNacimiento.getValue() : null;
        String sexo = (CmbSexo != null) ? CmbSexo.getValue() : null;
        String nombreRazaStr = TxtRazaPerro.getText();
        String estadoSeleccionado = (CmbEstado != null) ? CmbEstado.getValue() : null;
        String patologiasStr = (TxtAreaPatologia != null) ? TxtAreaPatologia.getText() : "";

        // 2. Validar campos obligatorios.
        if (!validarCamposObligatorios(nombre, fechaNac, sexo, nombreRazaStr, estadoSeleccionado)) {
            return;
        }

        // 3. Procesar Raza (obtener existente o crear nueva).
        Raza razaObjeto = procesarRaza(nombreRazaStr);
        if (razaObjeto == null || razaObjeto.getIdRaza() <= 0) {
            return;
        }

        // 4. Preparar el objeto Perro para guardar.
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

        if (ESTADO_ADOPTADO.equalsIgnoreCase(estadoSeleccionado)) perroParaGuardar.setAdoptado("S");
        else if (ESTADO_RESERVADO.equalsIgnoreCase(estadoSeleccionado)) perroParaGuardar.setAdoptado("R");
        else if (ESTADO_EN_ACOGIDA.equalsIgnoreCase(estadoSeleccionado)) perroParaGuardar.setAdoptado("A");
        else perroParaGuardar.setAdoptado("N");

        // 5. Procesar y guardar la imagen del perro.
        try {
            String rutaFotoParaBD = guardarImagenYObtenerRutaBD();
            if (rutaFotoParaBD == null && archivoFotoSeleccionada != null) {
                return;
            }
            perroParaGuardar.setFoto(rutaFotoParaBD);
        } catch (IOException e) {
            UtilidadesVentana.mostrarAlertaError("Error al Guardar Imagen", "No se pudo guardar la imagen del perro: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 6. Guardar el perro (y sus patologías) en la base de datos.
        if (perroDao == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Sistema", "El servicio de gestión de perros no está disponible.");
            return;
        }

        try {
            if (esNuevo) {
                int nuevoIdPerro = perroDao.crearPerro(perroParaGuardar);
                if (nuevoIdPerro > 0) {
                    perroParaGuardar.setIdPerro(nuevoIdPerro);
                    procesarYGuardarPatologiasAsociadas(nuevoIdPerro, patologiasStr, false);
                    UtilidadesVentana.mostrarAlertaInformacion("Operación Exitosa", "Perro '" + perroParaGuardar.getNombre() + "' añadido correctamente con ID: " + nuevoIdPerro);
                } else {
                    UtilidadesVentana.mostrarAlertaError("Error de Creación", "No se pudo crear el perro en la base de datos (ID no generado o error).");
                    return;
                }
            } else {
                boolean actualizado = perroDao.actualizarPerro(perroParaGuardar);
                if (actualizado) {
                    procesarYGuardarPatologiasAsociadas(perroParaGuardar.getIdPerro(), patologiasStr, true);
                    UtilidadesVentana.mostrarAlertaInformacion("Operación Exitosa", "Perro '" + perroParaGuardar.getNombre() + "' actualizado correctamente.");
                } else {
                    UtilidadesVentana.mostrarAlertaError("Error de Actualización", "No se pudo actualizar el perro en la base de datos.");
                    return;
                }
            }
            cerrarFormulario();
        } catch (SQLException e) {
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "Ocurrió un error al guardar el perro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Procesa la cadena de texto de patologías, busca o crea cada patología en la BD,
     * y las asigna al perro especificado.
     * Si es una edición, primero elimina las asignaciones de patologías anteriores del perro.
     * @param idPerro El ID del perro al que se asignarán las patologías.
     * @param patologiasInputStr La cadena de texto con nombres de patologías, separadas por comas.
     * @param esModoEdicion True si se está editando un perro (lo que implica borrar asignaciones previas).
     */
    private void procesarYGuardarPatologiasAsociadas(int idPerro, String patologiasInputStr, boolean esModoEdicion) {
        if (patologiaDao == null || identificacionPatologiaDao == null || idPerro <= 0) {
            System.err.println("Error: DAOs de patología no inicializados o ID de perro inválido ("+idPerro+"). No se procesarán patologías.");
            return;
        }

        if (esModoEdicion) {
            identificacionPatologiaDao.eliminarPatologiasPorPerro(idPerro);
            System.out.println("INFO: Patologías previas eliminadas para el perro ID: " + idPerro);
        }

        if (patologiasInputStr == null || patologiasInputStr.trim().isEmpty()) {
            System.out.println("INFO: No se especificaron patologías para el perro ID: " + idPerro);
            return;
        }

        String[] nombresPatologiasArray = patologiasInputStr.split(",");
        for (String nombrePatologiaCrudo : nombresPatologiasArray) {
            String nombrePatologiaLimpio = nombrePatologiaCrudo.trim();
            if (nombrePatologiaLimpio.isEmpty()) {
                continue;
            }

            try {
                Patologia patologiaExistente = patologiaDao.obtenerPatologiaPorNombre(nombrePatologiaLimpio);
                int idPatologiaParaAsignar;

                if (patologiaExistente != null) {
                    idPatologiaParaAsignar = patologiaExistente.getIdPatologia();
                } else {
                    Patologia nuevaPatologia = new Patologia();
                    nuevaPatologia.setNombre(nombrePatologiaLimpio);

                    idPatologiaParaAsignar = patologiaDao.crearPatologia(nuevaPatologia);
                    if (idPatologiaParaAsignar <= 0) {
                        System.err.println("WARN: No se pudo crear la patología: '" + nombrePatologiaLimpio +
                                "'. ID devuelto: " + idPatologiaParaAsignar + ". Esta patología no será asignada.");

                        UtilidadesVentana.mostrarAlertaAdvertencia("Creación Fallida", "No se pudo crear la patología: ", "" + nombrePatologiaLimpio + ". No se asignará al perro.");
                        continue;
                    }
                    System.out.println("INFO: Patología '" + nombrePatologiaLimpio + "' creada con ID: " + idPatologiaParaAsignar);
                }

                identificacionPatologiaDao.asignarPatologiaAPerro(idPerro, idPatologiaParaAsignar, null);
                System.out.println("INFO: Patología '" + nombrePatologiaLimpio + "' (ID: " + idPatologiaParaAsignar +
                        ") asignada al perro ID: " + idPerro);

            } catch (SQLException e) {
                UtilidadesVentana.mostrarAlertaError("Error de Base de Datos al Procesar Patología",
                        "Ocurrió un error al procesar la patología '" + nombrePatologiaLimpio + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Obtiene la extensión de un archivo a partir de su nombre.
     * @param nombreArchivo El nombre del archivo.
     * @return La extensión en minúsculas, o una cadena vacía si no tiene o es inválida.
     */
    private String obtenerExtensionArchivo(String nombreArchivo) {
        if (nombreArchivo == null) return "";
        int ultimoPunto = nombreArchivo.lastIndexOf('.');
        if (ultimoPunto > 0 && ultimoPunto < nombreArchivo.length() - 1) {
            return nombreArchivo.substring(ultimoPunto + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Verifica si una extensión de archivo corresponde a un tipo de imagen comúnmente aceptado.
     * @param extension La extensión del archivo (ej. "png", "jpg") en minúsculas.
     * @return true si la extensión es de una imagen válida, false en caso contrario.
     */
    private boolean esExtensionImagenValida(String extension) {
        return extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif");
    }

    /**
     * Maneja el evento de clic en el botón "Cancelar". Cierra el formulario.
     * @param event El evento del ratón (si el botón es un ImageView o similar con onMouseClicked).
     *              Si es un Button con onAction, el parámetro sería ActionEvent.
     */
    @FXML
    void Cancelar(MouseEvent event) {
        cerrarFormulario();
    }

    /**
     * Maneja el evento de clic en el icono "Volver". Cierra el formulario.
     * @param event El evento del ratón.
     */
    @FXML
    void Volver(MouseEvent event) {
        cerrarFormulario();
    }

    /**
     * Cierra la ventana actual del formulario.
     * Intenta obtener el Stage (ventana) a partir de varios nodos FXML conocidos.
     */
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