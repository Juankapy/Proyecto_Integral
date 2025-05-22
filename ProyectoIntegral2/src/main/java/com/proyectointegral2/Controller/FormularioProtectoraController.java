package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.utils.UtilidadesExcepciones; // Si lo usas para manejo de excepciones
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Para volver al perfil
import javafx.fxml.Initializable; // Si se usa el initialize con parámetros
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Arrays; // Para deshabilitar campos
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Controlador para el formulario de edición del perfil de una Protectora.
 * Permite modificar los datos de la protectora (información de contacto, dirección)
 * y, opcionalmente, la contraseña de su cuenta de usuario asociada.
 * También gestiona la carga y actualización de la foto o logo de la protectora.
 */
public class FormularioProtectoraController implements Initializable {

    // --- Componentes FXML ---
    @FXML private ImageView imgIconoVolver;
    @FXML private Label lblTituloFormulario;
    @FXML private ImageView imgFotoProtectoraEditable;
    @FXML private Button btnCambiarFotoProtectora;

    // Campos para datos de la Protectora
    @FXML private TextField txtNombreProtectora;
    @FXML private TextField txtCIF;
    @FXML private TextField txtEmailProtectora;
    @FXML private TextField txtTelefonoProtectora;
    @FXML private TextField txtCalleProtectora;
    @FXML private TextField txtCiudadProtectora;
    @FXML private TextField txtProvinciaProtectora;
    @FXML private TextField txtCPProtectora;

    // Campos para la cuenta de Usuario asociada
    @FXML private PasswordField txtPasswordCuenta;
    @FXML private PasswordField txtConfirmPasswordCuenta;

    @FXML private Button btnCancelar;
    @FXML private Button btnGuardarCambios;

    // --- DAOs ---
    private ProtectoraDao protectoraDAO;
    private UsuarioDao usuarioDAO;

    // --- Estado del Controlador ---
    private Protectora protectoraAEditar;
    private Usuario cuentaUsuarioAsociada;
    private File nuevaFotoSeleccionada;

    // --- Constantes ---
    private static final String RUTA_PLACEHOLDER_LOGO_PROTECTORA = "/assets/Imagenes/iconos/placeholder_logo_protectora.png"; // O sinusuario.jpg
    private static final String DIRECTORIO_BASE_FOTOS_PROTECTORAS_FILESYSTEM = "src/main/resources/assets/Imagenes/logos_protectoras/";
    private static final String RUTA_BASE_FOTOS_PROTECTORAS_CLASSPATH = "/assets/Imagenes/logos_protectoras/";

    // Rutas para navegación
    private static final String RUTA_FXML_PERFIL_PROTECTORA = "/com/proyectointegral2/Vista/PerfilProtectora.fxml";
    private static final String RUTA_FXML_MAIN_PROTECTORA = "/com/proyectointegral2/Vista/MainProtectora.fxml"; // Fallback
    private static final String TITULO_PERFIL_PROTECTORA = "Perfil de Protectora";
    private static final String TITULO_MAIN_PROTECTORA = "Panel de Protectora";

    // Patrones de validación
    private static final Pattern CIF_PATTERN = Pattern.compile("^[A-HJNP-SUVW][0-9]{7}[0-9A-J]$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CP_PATTERN = Pattern.compile("\\d{5}");
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("\\d{9}");


    /**
     * Método de inicialización llamado por JavaFX.
     * @param url La ubicación utilizada para resolver rutas relativas.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.protectoraDAO = new ProtectoraDao();
            this.usuarioDAO = new UsuarioDao();
        } catch (Exception e) {
            System.err.println("Error crítico al inicializar DAOs en FormularioProtectoraController: " + e.getMessage());
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sistema",
                    "No se pudo inicializar el acceso a la base de datos. El formulario no funcionará correctamente.");
            if (btnGuardarCambios != null) btnGuardarCambios.setDisable(true);
            if (btnCambiarFotoProtectora != null) btnCambiarFotoProtectora.setDisable(true);
        }
        if (lblTituloFormulario != null) lblTituloFormulario.setText("Editar Datos de Protectora");
        if (btnGuardarCambios != null) btnGuardarCambios.setText("Guardar Cambios");
        cargarImagenPlaceholder();
    }

    /**
     * Prepara el formulario con los datos de la protectora y su cuenta de usuario para edición.
     * @param protectora El objeto {@link Protectora} con los datos a editar.
     * @param cuentaUsuario El objeto {@link Usuario} asociado a la protectora.
     */
    public void initDataParaEdicion(Protectora protectora, Usuario cuentaUsuario) {
        this.protectoraAEditar = protectora;
        this.cuentaUsuarioAsociada = cuentaUsuario;

        if (this.protectoraAEditar == null || this.cuentaUsuarioAsociada == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Datos",
                    "No se recibió la información completa de la protectora para editar. El formulario se cerrará.");
            limpiarYDeshabilitarFormulario();
            if (btnGuardarCambios != null) btnGuardarCambios.setDisable(true);
            return;
        }

        if (lblTituloFormulario != null) {
            lblTituloFormulario.setText("Editar Datos de " + Objects.requireNonNullElse(this.protectoraAEditar.getNombre(), "Protectora"));
        }
        if (btnGuardarCambios != null) {
            btnGuardarCambios.setText("Guardar Cambios");
        }
        poblarCamposConDatosExistentes();
    }

    /**
     * Puebla los campos del formulario con los datos de la protectora que se está editando.
     * También configura los prompts para los campos de contraseña.
     */
    private void poblarCamposConDatosExistentes() {
        if (protectoraAEditar == null) {
            limpiarYDeshabilitarFormulario();
            return;
        }

        if (txtNombreProtectora != null) txtNombreProtectora.setText(Objects.requireNonNullElse(protectoraAEditar.getNombre(), ""));
        if (txtCIF != null) txtCIF.setText(Objects.requireNonNullElse(protectoraAEditar.getCif(), ""));
        if (txtEmailProtectora != null) txtEmailProtectora.setText(Objects.requireNonNullElse(protectoraAEditar.getEmail(), ""));
        if (txtTelefonoProtectora != null) txtTelefonoProtectora.setText(Objects.requireNonNullElse(protectoraAEditar.getTelefono(), ""));
        if (txtCalleProtectora != null) txtCalleProtectora.setText(Objects.requireNonNullElse(protectoraAEditar.getCalle(), ""));
        if (txtCiudadProtectora != null) txtCiudadProtectora.setText(Objects.requireNonNullElse(protectoraAEditar.getCiudad(), ""));
        if (txtProvinciaProtectora != null) txtProvinciaProtectora.setText(Objects.requireNonNullElse(protectoraAEditar.getProvincia(), ""));
        if (txtCPProtectora != null) txtCPProtectora.setText(Objects.requireNonNullElse(protectoraAEditar.getCodigoPostal(), ""));


        if (txtPasswordCuenta != null) txtPasswordCuenta.setPromptText("Dejar vacío para no cambiar");
        if (txtConfirmPasswordCuenta != null) txtConfirmPasswordCuenta.setPromptText("Confirmar nueva contraseña");

        cargarFotoProtectoraActual(protectoraAEditar.getRutaFotoPerfil());
    }

    /**
     * Limpia todos los campos de entrada del formulario y resetea la selección de foto.
     */
    private void limpiarCampos() {
        if(txtNombreProtectora != null) txtNombreProtectora.clear();
        if(txtCIF != null) txtCIF.clear();

        if(txtCPProtectora != null) txtCPProtectora.clear();
        if(txtPasswordCuenta != null) txtPasswordCuenta.clear();
        if(txtConfirmPasswordCuenta != null) txtConfirmPasswordCuenta.clear();
        this.nuevaFotoSeleccionada = null;
        cargarImagenPlaceholder();
    }

    /**
     * Limpia y deshabilita todos los campos del formulario.
     * Se usa si ocurre un error crítico al inicializar los datos.
     */
    private void limpiarYDeshabilitarFormulario() {
        limpiarCampos();
        for (TextField tf : Arrays.asList(txtNombreProtectora, txtCIF, txtEmailProtectora, txtTelefonoProtectora,
                txtCalleProtectora, txtCiudadProtectora, txtProvinciaProtectora, txtCPProtectora)) {
            if (tf != null) tf.setDisable(true);
        }
        if (txtPasswordCuenta != null) { txtPasswordCuenta.clear(); txtPasswordCuenta.setDisable(true); }
        if (txtConfirmPasswordCuenta != null) { txtConfirmPasswordCuenta.clear(); txtConfirmPasswordCuenta.setDisable(true); }
        if (btnCambiarFotoProtectora != null) btnCambiarFotoProtectora.setDisable(true);
    }


    /**
     * Carga la foto/logo actual de la protectora en el ImageView.
     * Si no hay foto o hay un error, carga una imagen placeholder.
     * @param rutaFotoRelativaAlClasspath La ruta de la foto, relativa a la carpeta 'resources'.
     */
    private void cargarFotoProtectoraActual(String rutaFotoRelativaAlClasspath) {
        if (imgFotoProtectoraEditable == null) return;
        String pathNormalizado = null;

        if (rutaFotoRelativaAlClasspath != null && !rutaFotoRelativaAlClasspath.trim().isEmpty()) {
            try {
                pathNormalizado = rutaFotoRelativaAlClasspath.startsWith("/")
                        ? rutaFotoRelativaAlClasspath
                        : "/" + rutaFotoRelativaAlClasspath.replace("\\", "/");

                try (InputStream stream = getClass().getResourceAsStream(pathNormalizado)) {
                    if (stream != null) {
                        imgFotoProtectoraEditable.setImage(new Image(stream));
                        return;
                    } else {
                        System.err.println("WARN: Foto/Logo de protectora no encontrada en classpath: " + pathNormalizado);
                    }
                }
            } catch (Exception e) {
                System.err.println("ERROR: Excepción al cargar foto/logo de protectora desde '" +
                        (pathNormalizado != null ? pathNormalizado : rutaFotoRelativaAlClasspath) + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
        cargarImagenPlaceholder();
    }

    /**
     * Carga la imagen de placeholder en el ImageView de la foto/logo.
     */
    private void cargarImagenPlaceholder() {
        if (imgFotoProtectoraEditable == null) return;
        try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_LOGO_PROTECTORA)) {
            if (placeholderStream != null) {
                imgFotoProtectoraEditable.setImage(new Image(placeholderStream));
            } else {
                System.err.println("Error Crítico: Placeholder de logo de protectora no encontrado en: " + RUTA_PLACEHOLDER_LOGO_PROTECTORA);
                imgFotoProtectoraEditable.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Excepción crítica al cargar imagen placeholder de logo de protectora: " + e.getMessage());
            e.printStackTrace();
            imgFotoProtectoraEditable.setImage(null);
        }
    }

    /**
     * Maneja el evento del botón "Cambiar Foto".
     * Abre un FileChooser para que el usuario seleccione una nueva imagen/logo.
     * @param event El evento de acción.
     */
    @FXML
    void handleCambiarFoto(ActionEvent event) {
        if (imgFotoProtectoraEditable == null || btnCambiarFotoProtectora == null) {
            System.err.println("Error: Componentes UI para cambiar foto de protectora no disponibles.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Nueva Imagen/Logo de Protectora");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Node sourceNode = (Node) event.getSource();
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        File archivoSeleccionado = fileChooser.showOpenDialog(stage);

        if (archivoSeleccionado != null) {
            this.nuevaFotoSeleccionada = archivoSeleccionado;
            try {
                Image image = new Image(archivoSeleccionado.toURI().toURL().toString());
                imgFotoProtectoraEditable.setImage(image);
                System.out.println("INFO: Nueva foto/logo previsualizada: " + archivoSeleccionado.getAbsolutePath());
            } catch (MalformedURLException e) {
                UtilidadesVentana.mostrarAlertaError("Error al Previsualizar Imagen",
                        "No se pudo previsualizar la imagen seleccionada: " + e.getMessage());
                e.printStackTrace();
                this.nuevaFotoSeleccionada = null;
            }
        }
    }

    /**
     * Valida los datos de entrada del formulario de edición de protectora.
     * @return true si los datos son válidos, false en caso contrario.
     */
    private boolean validarEntradasFormulario(String nombre, String cif, String email, String telefono, String calle, String ciudad, String provincia, String cp, String nuevaPassword, String confirmarNuevaPassword) {

        if (nombre.isEmpty() || cif.isEmpty() || email.isEmpty() || telefono.isEmpty() ||
                calle.isEmpty() || ciudad.isEmpty() || provincia.isEmpty() || cp.isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campos Obligatorios", "Todos los campos de información de la protectora son obligatorios.");
            return false;
        }
        // Validar CIF
        if (!CIF_PATTERN.matcher(cif.toUpperCase()).matches()) { // Convertir a mayúsculas para validar
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El formato del CIF no es válido.");
            txtCIF.requestFocus();
            return false;
        }
        // Validar Email
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El formato del correo electrónico no es válido.");
            txtEmailProtectora.requestFocus();
            return false;
        }
        // Validar Código Postal
        if (!CP_PATTERN.matcher(cp).matches()) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El Código Postal debe tener 5 dígitos.");
            txtCPProtectora.requestFocus();
            return false;
        }
        // Validar Teléfono
        if (!TELEFONO_PATTERN.matcher(telefono).matches()) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El Teléfono debe tener 9 dígitos.");
            txtTelefonoProtectora.requestFocus();
            return false;
        }

        // Validaciones de Contraseña
        if (!nuevaPassword.isEmpty()) {
            if (nuevaPassword.length() < 6) { // Mínimo 6 caracteres
                UtilidadesVentana.mostrarAlertaError("Contraseña Débil", "La nueva contraseña debe tener al menos 6 caracteres.");
                txtPasswordCuenta.requestFocus();
                return false;
            }
            if (!nuevaPassword.equals(confirmarNuevaPassword)) {
                UtilidadesVentana.mostrarAlertaError("Error de Contraseña", "Las nuevas contraseñas no coinciden.");
                txtPasswordCuenta.clear();
                txtConfirmPasswordCuenta.clear();
                txtPasswordCuenta.requestFocus();
                return false;
            }
        }
        return true;
    }


    /**
     * Maneja el evento del botón "Guardar Cambios".
     * Recolecta, valida y guarda los datos actualizados de la protectora y, opcionalmente,
     * la contraseña de su cuenta de usuario.
     * @param event El evento de acción.
     */
    @FXML
    void handleGuardarCambios(ActionEvent event) {
        if (protectoraAEditar == null || cuentaUsuarioAsociada == null) {
            UtilidadesVentana.mostrarAlertaError("Error Interno del Sistema",
                    "No hay datos de protectora o cuenta de usuario cargados para guardar. Por favor, recargue el perfil.");
            return;
        }

        // 1. Recolectar datos del formulario.
        String nombre = txtNombreProtectora.getText().trim();
        String cif = txtCIF.getText().trim().toUpperCase(); // CIF a mayúsculas
        String email = txtEmailProtectora.getText().trim();
        String telefono = txtTelefonoProtectora.getText().trim();
        String calle = txtCalleProtectora.getText().trim();
        String ciudad = txtCiudadProtectora.getText().trim();
        String provincia = txtProvinciaProtectora.getText().trim();
        String cp = txtCPProtectora.getText().trim();
        String passwordNueva = txtPasswordCuenta.getText(); // No hacer trim
        String confirmPasswordNueva = txtConfirmPasswordCuenta.getText(); // No hacer trim

        // 2. Validar los datos.
        if (!validarEntradasFormulario(nombre, cif, email, telefono, calle, ciudad, provincia, cp,
                passwordNueva, confirmPasswordNueva)) {
            return;
        }

        protectoraAEditar.setNombre(nombre);
        protectoraAEditar.setEmail(email);
        protectoraAEditar.setTelefono(telefono);
        protectoraAEditar.setCalle(calle);
        protectoraAEditar.setCiudad(ciudad);
        protectoraAEditar.setProvincia(provincia);
        protectoraAEditar.setCodigoPostal(cp);

        // 4. Determinar si la contraseña del Usuario necesita actualización.
        boolean esNecesarioActualizarPasswordUsuario = !passwordNueva.isEmpty();
        if (esNecesarioActualizarPasswordUsuario) {
            // IMPORTANTE: Hashear la nueva contraseña antes de setearla.
            // cuentaUsuarioAsociada.setContrasena(hashUtils.hashPassword(passwordNueva));
            cuentaUsuarioAsociada.setContrasena(passwordNueva);
        }

        // 5. Procesar nueva foto/logo si se seleccionó una.
        if (nuevaFotoSeleccionada != null) {
            try {
                String identificadorParaFoto = protectoraAEditar.getCif() != null ? protectoraAEditar.getCif() : "protectora";
                String rutaRelativaFotoGuardada = guardarNuevaFotoProtectoraYObtenerRuta(nuevaFotoSeleccionada, identificadorParaFoto);
                if (rutaRelativaFotoGuardada != null) {
                    protectoraAEditar.setRutaFotoPerfil(rutaRelativaFotoGuardada);
                } else {
                    return;
                }
            } catch (IOException e) {
                UtilidadesVentana.mostrarAlertaError("Error al Procesar Imagen", "Ocurrió un error al procesar la nueva imagen/logo: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        // 6. Guardar los cambios en la base de datos.
        boolean exitoActualizacionProtectora = false;
        boolean exitoActualizacionPassword = true;

        try {
            if (protectoraDAO != null) {
                exitoActualizacionProtectora = protectoraDAO.actualizarProtectora(protectoraAEditar);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error de Sistema", "Servicio de datos de protectora no disponible.");
                return;
            }

            if (esNecesarioActualizarPasswordUsuario) {
                if (usuarioDAO != null) {
                    exitoActualizacionPassword = usuarioDAO.actualizarUsuario(cuentaUsuarioAsociada);
                } else {
                    UtilidadesVentana.mostrarAlertaError("Error de Sistema", "Servicio de datos de usuario no disponible.");
                    exitoActualizacionPassword = false;
                }
            }

            if (exitoActualizacionProtectora && exitoActualizacionPassword) {
                UtilidadesVentana.mostrarAlertaInformacion("Datos Actualizados", "Los datos de la protectora se han guardado correctamente.");
                navegarAlPerfilProtectora();
            } else if (exitoActualizacionProtectora && !exitoActualizacionPassword && esNecesarioActualizarPasswordUsuario) {
                UtilidadesVentana.mostrarAlertaAdvertencia("Actualizacion Parcial","Actualización Parcial", "Los datos de la protectora se guardaron, pero hubo un error al actualizar la contraseña de la cuenta.");
                navegarAlPerfilProtectora();
            } else if (!exitoActualizacionProtectora) {
                UtilidadesVentana.mostrarAlertaError("Error al Guardar", "No se pudieron guardar los cambios en los datos de la protectora.");
            } else {
                UtilidadesVentana.mostrarAlertaError("Error al Guardar", "Ocurrió un error desconocido al intentar guardar los cambios.");
            }

        } catch (SQLException e) {
            String mensajeErrorBD = "Ocurrió un error al intentar guardar los cambios en la base de datos: " + e.getMessage();
            UtilidadesExcepciones.mostrarError(e, "Error de Base de Datos", mensajeErrorBD);
            e.printStackTrace();
        }
    }

    /**
     * Guarda la nueva foto/logo seleccionada y devuelve su ruta relativa al classpath.
     * @param fotoArchivo El archivo de la foto/logo.
     * @param identificadorUnico Identificador para el nombre del archivo (ej. CIF).
     * @return Ruta relativa para la BD, o null si hay error.
     * @throws IOException Si hay error de E/S.
     */
    private String guardarNuevaFotoProtectoraYObtenerRuta(File fotoArchivo, String identificadorUnico) throws IOException {

        if (fotoArchivo == null) return null;

        String nombreOriginal = fotoArchivo.getName();
        String extension = "";
        int i = nombreOriginal.lastIndexOf('.');
        if (i > 0 && i < nombreOriginal.length() - 1) {
            extension = nombreOriginal.substring(i);
        } else {
            throw new IOException("El archivo de imagen no tiene una extensión válida.");
        }

        String identificadorLimpio = (identificadorUnico != null ? identificadorUnico : "protectora")
                .replaceAll("[^a-zA-Z0-9.-]", "_");
        String nombreArchivoGenerado = "logo_" + identificadorLimpio + "_" + System.currentTimeMillis() + extension;

        Path rutaDestinoEnSrc = Paths.get(DIRECTORIO_BASE_FOTOS_PROTECTORAS_FILESYSTEM, nombreArchivoGenerado);
        File carpetaDestinoEnSrc = rutaDestinoEnSrc.getParent().toFile();
        if (!carpetaDestinoEnSrc.exists() && !carpetaDestinoEnSrc.mkdirs()) {
            System.err.println("WARN: No se pudo crear la carpeta de destino en 'src': " + carpetaDestinoEnSrc.getAbsolutePath());
        }
        if (carpetaDestinoEnSrc.exists()) {
            Files.copy(fotoArchivo.toPath(), rutaDestinoEnSrc, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("INFO: Foto/Logo de protectora guardada en (fuentes): " + rutaDestinoEnSrc.toString());
        }

        try {
            URL urlRaizResources = getClass().getResource("/");
            if (urlRaizResources != null) {
                Path pathRaizResources = Paths.get(urlRaizResources.toURI());
                Path pathCarpetaDestinoTarget = pathRaizResources.resolve(RUTA_BASE_FOTOS_PROTECTORAS_CLASSPATH.substring(1));
                if (!Files.exists(pathCarpetaDestinoTarget)) Files.createDirectories(pathCarpetaDestinoTarget);
                Path rutaDestinoEnTarget = pathCarpetaDestinoTarget.resolve(nombreArchivoGenerado);
                Files.copy(fotoArchivo.toPath(), rutaDestinoEnTarget, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("INFO: Foto/Logo de protectora copiada a (target/classes): " + rutaDestinoEnTarget.toString());
            }
        } catch (Exception e) {
            System.err.println("WARN: No se pudo copiar la imagen a 'target/classes' para protectora: " + e.getMessage());
        }
        return RUTA_BASE_FOTOS_PROTECTORAS_CLASSPATH + nombreArchivoGenerado;
    }


    /**
     * Maneja el evento del botón "Cancelar". Vuelve a la pantalla de perfil de la protectora.
     * @param event El evento de acción.
     */
    @FXML
    void handleCancelar(ActionEvent event) {
        navegarAlPerfilProtectora();
    }

    /**
     * Maneja el evento del icono "Volver". Vuelve a la pantalla de perfil de la protectora.
     * @param event El evento del ratón.
     */
    @FXML
    void handleVolver(MouseEvent event) {
        navegarAlPerfilProtectora();
    }

    /**
     * Navega de vuelta a la pantalla de Perfil de Protectora, recargando sus datos.
     * Requiere que `protectoraAEditar` y `cuentaUsuarioAsociada` no sean null.
     */
    private void navegarAlPerfilProtectora() {
        if (protectoraAEditar == null || cuentaUsuarioAsociada == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Navegación",
                    "No se puede volver al perfil porque la información de la protectora o usuario no está disponible.");
            UtilidadesVentana.cambiarEscena(RUTA_FXML_MAIN_PROTECTORA, TITULO_MAIN_PROTECTORA, true);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_FXML_PERFIL_PROTECTORA));
            Parent root = loader.load();
            PerfilProtectoraController perfilControllerDestino = loader.getController();

            if (perfilControllerDestino != null) {
                perfilControllerDestino.initData(this.protectoraAEditar, this.cuentaUsuarioAsociada);
            }
            UtilidadesVentana.cambiarEscenaConRoot(root, TITULO_PERFIL_PROTECTORA + " - " + protectoraAEditar.getNombre(), false);
        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación",
                    "No se pudo volver a la pantalla de perfil de la protectora: " + e.getMessage() +
                            "\nSerás redirigido al panel principal de la protectora.");
            UtilidadesVentana.cambiarEscena(RUTA_FXML_MAIN_PROTECTORA, TITULO_MAIN_PROTECTORA, true);
        }
    }
}