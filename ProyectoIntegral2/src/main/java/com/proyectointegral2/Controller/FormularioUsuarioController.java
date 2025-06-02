package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.Model.SesionUsuario;
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.utils.UtilidadesExcepciones;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.regex.Pattern;

public class FormularioUsuarioController implements Initializable {

    @FXML private ImageView imgIconoVolver;
    @FXML private Label lblTituloFormulario;
    @FXML private ImageView imgFotoPerfilEditable;
    @FXML private Button btnCambiarFoto;

    @FXML private TextField TxtNombre;
    @FXML private TextField TxtApellido;
    @FXML private TextField TxtNIF;
    @FXML private DatePicker DpFechaNacimiento;
    @FXML private TextField TxtDireccion;
    @FXML private TextField TxtProvincia;
    @FXML private TextField TxtCiudad;
    @FXML private TextField TxtCP;
    @FXML private TextField TxtTelefono;
    @FXML private TextField TxtEmail;

    @FXML private TextField TxtNombreUsuario;
    @FXML private PasswordField TxtPassword;
    @FXML private PasswordField TxtConfirmPassword;

    @FXML private Button BtnCancelar;
    @FXML private Button BtnGuardar;

    private ClienteDao clienteDAO;
    private UsuarioDao usuarioDAO;

    private Cliente clienteAEditar;
    private Usuario cuentaUsuarioAEditar;
    private File nuevaFotoSeleccionada;

    private static final String RUTA_PLACEHOLDER_FOTO_PERFIL_USUARIO = "/assets/Imagenes/iconos/sinusuario.jpg";
    private static final String DIRECTORIO_BASE_FOTOS_PERFIL_FILESYSTEM = "src/main/resources/assets/Imagenes/perfiles_usuarios/";
    private static final String RUTA_BASE_FOTOS_PERFIL_CLASSPATH = "/assets/Imagenes/perfiles_usuarios/";

    private static final String RUTA_FXML_PERFIL_USUARIO = "/com/proyectointegral2/Vista/PerfilUsuario.fxml";
    private static final String RUTA_FXML_MAIN_CLIENTE = "/com/proyectointegral2/Vista/Main.fxml";
    private static final String TITULO_PERFIL_USUARIO = "Mi Perfil";
    private static final String TITULO_MAIN_CLIENTE = "Panel Principal";

    private static final Pattern NIF_PATTERN = Pattern.compile("^[0-9]{8}[A-Z]$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CP_PATTERN = Pattern.compile("\\d{5}");
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("\\d{9}");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.clienteDAO = new ClienteDao();
            this.usuarioDAO = new UsuarioDao();
        } catch (Exception e) {
            System.err.println("Error crítico al inicializar DAOs en FormularioUsuarioController: " + e.getMessage());
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sistema",
                    "No se pudo inicializar el acceso a la base de datos. El formulario no funcionará correctamente.");
            if (BtnGuardar != null) BtnGuardar.setDisable(true);
            if (btnCambiarFoto != null) btnCambiarFoto.setDisable(true);
        }
        if (lblTituloFormulario != null) lblTituloFormulario.setText("Editar Perfil de Usuario");
        if (BtnGuardar != null) BtnGuardar.setText("Guardar Cambios");
        cargarImagenPlaceholder();
    }

    /**
     * Prepara el formulario con los datos del cliente y usuario que se van a editar.
     * Este método debe ser llamado desde el controlador que abre este formulario.
     * @param cliente El objeto {@link Cliente} con los datos personales a editar.
     * @param cuentaUsuario El objeto {@link Usuario} con los datos de la cuenta a editar.
     */
    public void initDataParaEdicion(Cliente cliente, Usuario cuentaUsuario) {
        this.clienteAEditar = cliente;
        this.cuentaUsuarioAEditar = cuentaUsuario;

        if (this.clienteAEditar == null || this.cuentaUsuarioAEditar == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Datos",
                    "No se recibió la información completa del usuario para editar. El formulario se cerrará.");
            limpiarYDeshabilitarFormulario();
            if (BtnGuardar != null) BtnGuardar.setDisable(true);
            return;
        }

        if (lblTituloFormulario != null) {
            lblTituloFormulario.setText("Editar Perfil de " +
                    Objects.requireNonNullElse(this.clienteAEditar.getNombre(), "Usuario"));
        }
        if (BtnGuardar != null) {
            BtnGuardar.setText("Guardar Cambios");
        }
        poblarCamposConDatosExistentes();
    }

    private void poblarCamposConDatosExistentes() {
        if (clienteAEditar != null) {
            TxtNombre.setText(Objects.requireNonNullElse(clienteAEditar.getNombre(), ""));
            TxtApellido.setText(Objects.requireNonNullElse(clienteAEditar.getApellidos(), ""));
            TxtNIF.setText(Objects.requireNonNullElse(clienteAEditar.getNif(), ""));
            if (DpFechaNacimiento != null) DpFechaNacimiento.setValue(clienteAEditar.getFechaNacimiento());
            TxtDireccion.setText(Objects.requireNonNullElse(clienteAEditar.getCalle(), ""));
            TxtProvincia.setText(Objects.requireNonNullElse(clienteAEditar.getProvincia(), ""));
            TxtCiudad.setText(Objects.requireNonNullElse(clienteAEditar.getCiudad(), ""));
            TxtCP.setText(Objects.requireNonNullElse(clienteAEditar.getCodigoPostal(), ""));
            TxtTelefono.setText(Objects.requireNonNullElse(clienteAEditar.getTelefono(), ""));
            TxtEmail.setText(Objects.requireNonNullElse(clienteAEditar.getEmail(), ""));

            cargarFotoPerfilActual(clienteAEditar.getRutaFotoPerfil());
        } else {
            limpiarCamposCliente();
            cargarImagenPlaceholder();
        }

        if (cuentaUsuarioAEditar != null) {
            TxtNombreUsuario.setText(Objects.requireNonNullElse(cuentaUsuarioAEditar.getNombreUsu(), ""));
        } else {
            if(TxtNombreUsuario != null) TxtNombreUsuario.clear();
        }

        if(TxtPassword != null) TxtPassword.setPromptText("Dejar vacío para no cambiar");
        if(TxtConfirmPassword != null) TxtConfirmPassword.setPromptText("Confirmar nueva contraseña");
    }

    /**
     * Limpia los campos relacionados con los datos del Cliente.
     */
    private void limpiarCamposCliente() {
        if(TxtNombre != null) TxtNombre.clear();
        if(TxtApellido != null) TxtApellido.clear();
        if(TxtEmail != null) TxtEmail.clear();
    }

    /**
     * Limpia todos los campos del formulario y los deshabilita.
     * Usado si hay un error crítico al iniciar con datos.
     */
    private void limpiarYDeshabilitarFormulario() {
        limpiarCamposCliente();
        if(TxtNombreUsuario != null) TxtNombreUsuario.clear();
        if(TxtPassword != null) { TxtPassword.clear(); TxtPassword.setDisable(true); }
        if(TxtConfirmPassword != null) { TxtConfirmPassword.clear(); TxtConfirmPassword.setDisable(true); }
        nuevaFotoSeleccionada = null;
        cargarImagenPlaceholder();
        if(btnCambiarFoto != null) btnCambiarFoto.setDisable(true);
        for (TextField tf : Arrays.asList(TxtNombre, TxtApellido, TxtNIF, TxtDireccion, TxtProvincia, TxtCiudad, TxtCP, TxtTelefono, TxtEmail, TxtNombreUsuario)) {
            if (tf != null) tf.setDisable(true);
        }
        if (DpFechaNacimiento != null) DpFechaNacimiento.setDisable(true);
    }


    /**
     * Carga la foto de perfil actual del usuario en el ImageView.
     * Si no hay foto o hay un error, carga una imagen placeholder.
     * @param rutaFotoRelativaAlClasspath La ruta de la foto, relativa a la carpeta 'resources'.
     */
    private void cargarFotoPerfilActual(String rutaFotoRelativaAlClasspath) {
        if (imgFotoPerfilEditable == null) return;
        String pathNormalizado = null;

        if (rutaFotoRelativaAlClasspath != null && !rutaFotoRelativaAlClasspath.trim().isEmpty()) {
            try {
                pathNormalizado = rutaFotoRelativaAlClasspath.startsWith("/")
                        ? rutaFotoRelativaAlClasspath
                        : "/" + rutaFotoRelativaAlClasspath.replace("\\", "/");

                try (InputStream stream = getClass().getResourceAsStream(pathNormalizado)) {
                    if (stream != null) {
                        imgFotoPerfilEditable.setImage(new Image(stream));
                        return;
                    } else {
                        System.err.println("WARN: Foto de perfil no encontrada en classpath: " + pathNormalizado);
                    }
                }
            } catch (Exception e) {
                System.err.println("ERROR: Excepción al cargar foto de perfil desde '" +
                        (pathNormalizado != null ? pathNormalizado : rutaFotoRelativaAlClasspath) + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
        cargarImagenPlaceholder();
    }

    /**
     * Carga la imagen de placeholder en el ImageView de la foto de perfil.
     */
    private void cargarImagenPlaceholder() {
        if (imgFotoPerfilEditable == null) return;
        try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_FOTO_PERFIL_USUARIO)) {
            if (placeholderStream != null) {
                imgFotoPerfilEditable.setImage(new Image(placeholderStream));
            } else {
                System.err.println("Error Crítico: Placeholder de foto de perfil de usuario no encontrado en: " + RUTA_PLACEHOLDER_FOTO_PERFIL_USUARIO);
                imgFotoPerfilEditable.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Excepción crítica al cargar imagen placeholder de usuario: " + e.getMessage());
            e.printStackTrace();
            imgFotoPerfilEditable.setImage(null);
        }
    }

    /**
     * Maneja el evento del botón "Cambiar Foto".
     * Abre un FileChooser para que el usuario seleccione una nueva imagen de perfil.
     * Muestra una vista previa de la imagen seleccionada.
     * @param event El evento de acción.
     */
    @FXML
    void CambiarFoto(ActionEvent event) {
        if (imgFotoPerfilEditable == null || btnCambiarFoto == null) {
            System.err.println("Error: Componentes UI para cambiar foto no disponibles.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Nueva Foto de Perfil");
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
                imgFotoPerfilEditable.setImage(image);
            } catch (MalformedURLException e) {
                UtilidadesVentana.mostrarAlertaError("Error al Previsualizar Imagen",
                        "No se pudo previsualizar la imagen seleccionada: " + e.getMessage());
                e.printStackTrace();
                this.nuevaFotoSeleccionada = null;
            }
        }
    }

    /**
     * Valida los datos de entrada del formulario de edición de perfil.
     * @return true si los datos son válidos, false en caso contrario.
     */
    private boolean validarEntradasFormulario(String nombre, String apellidos, String nif, LocalDate fechaNac, String email, String nombreUsuario, String nuevaPassword, String confirmarNuevaPassword) {

        if (nombre.isEmpty() || apellidos.isEmpty() || nif.isEmpty() || email.isEmpty() || nombreUsuario.isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campos Obligatorios",
                    "Nombre, apellidos, NIF, email del cliente y nombre de usuario son obligatorios.");
            return false;
        }
        if (!NIF_PATTERN.matcher(nif.toUpperCase()).matches()) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El formato del NIF no es válido (ej: 12345678A).");
            TxtNIF.requestFocus();
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El formato del correo electrónico no es válido.");
            TxtEmail.requestFocus();
            return false;
        }
        if (fechaNac != null && fechaNac.isAfter(LocalDate.now())) {
            UtilidadesVentana.mostrarAlertaError("Fecha Inválida", "La fecha de nacimiento no puede ser futura.");
            DpFechaNacimiento.requestFocus();
            return false;
        }
        String cp = TxtCP.getText().trim();
        if (!cp.isEmpty() && !CP_PATTERN.matcher(cp).matches()) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El Código Postal debe tener 5 dígitos si se especifica.");
            TxtCP.requestFocus();
            return false;
        }
        String telefono = TxtTelefono.getText().trim();
        if (!telefono.isEmpty() && !TELEFONO_PATTERN.matcher(telefono).matches()) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El Teléfono debe tener 9 dígitos si se especifica.");
            TxtTelefono.requestFocus();
            return false;
        }


        if (!nuevaPassword.isEmpty()) {
            if (nuevaPassword.length() < 6) {
                UtilidadesVentana.mostrarAlertaError("Contraseña Débil", "La nueva contraseña debe tener al menos 6 caracteres.");
                TxtPassword.requestFocus();
                return false;
            }
            if (!nuevaPassword.equals(confirmarNuevaPassword)) {
                UtilidadesVentana.mostrarAlertaError("Error de Contraseña", "Las nuevas contraseñas no coinciden.");
                TxtPassword.clear();
                TxtConfirmPassword.clear();
                TxtPassword.requestFocus();
                return false;
            }
        }
        return true;
    }


    /**
     * Maneja el evento del botón "Guardar Cambios".
     * Recolecta, valida y guarda los datos actualizados del cliente y su cuenta de usuario.
     * @param event El evento de acción.
     */
    @FXML
    void Guardar(ActionEvent event) {
        if (clienteAEditar == null || cuentaUsuarioAEditar == null) {
            UtilidadesVentana.mostrarAlertaError("Error Interno del Sistema",
                    "No hay datos de usuario o cliente cargados para guardar. Por favor, recargue el perfil.");
            return;
        }

        String nombre = TxtNombre.getText().trim();
        String apellidos = TxtApellido.getText().trim();
        String nif = TxtNIF.getText().trim().toUpperCase();
        LocalDate fechaNacimiento = (DpFechaNacimiento != null) ? DpFechaNacimiento.getValue() : null;
        String calle = TxtDireccion.getText().trim();
        String provincia = TxtProvincia.getText().trim();
        String ciudad = TxtCiudad.getText().trim();
        String cp = TxtCP.getText().trim();
        String telefono = TxtTelefono.getText().trim();
        String emailCliente = TxtEmail.getText().trim();

        String nombreUsuarioLogin = TxtNombreUsuario.getText().trim();
        String passwordNueva = TxtPassword.getText();
        String confirmPasswordNueva = TxtConfirmPassword.getText();

        if (!validarEntradasFormulario(nombre, apellidos, nif, fechaNacimiento, emailCliente,
                nombreUsuarioLogin, passwordNueva, confirmPasswordNueva)) {
            return;
        }

        clienteAEditar.setApellidos(apellidos);
        clienteAEditar.setNif(nif);
        clienteAEditar.setFechaNacimiento(fechaNacimiento);
        clienteAEditar.setCalle(calle);
        clienteAEditar.setProvincia(provincia);
        clienteAEditar.setCiudad(ciudad);
        clienteAEditar.setCodigoPostal(cp);
        clienteAEditar.setTelefono(telefono);
        clienteAEditar.setEmail(emailCliente);

        boolean esNecesarioActualizarUsuario = false;
        if (!cuentaUsuarioAEditar.getNombreUsu().equals(nombreUsuarioLogin)) {
            cuentaUsuarioAEditar.setNombreUsu(nombreUsuarioLogin);
            esNecesarioActualizarUsuario = true;
        }
        if (!passwordNueva.isEmpty()) {
            cuentaUsuarioAEditar.setContrasena(passwordNueva);
            esNecesarioActualizarUsuario = true;
        }

        if (nuevaFotoSeleccionada != null) {
            try {

                String identificadorParaFoto = clienteAEditar.getNif() != null ? clienteAEditar.getNif() : clienteAEditar.getEmail();
                String rutaRelativaFotoGuardada = guardarNuevaFotoDePerfilYObtenerRuta(nuevaFotoSeleccionada, identificadorParaFoto);
                if (rutaRelativaFotoGuardada != null) {
                    clienteAEditar.setRutaFotoPerfil(rutaRelativaFotoGuardada);
                } else {
                    return;
                }
            } catch (IOException e) {
                UtilidadesVentana.mostrarAlertaError("Error al Procesar Foto",
                        "Ocurrió un error al procesar la nueva foto de perfil: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        try {
            boolean exitoActualizacionCliente = clienteDAO.actualizarCliente(clienteAEditar);
            boolean exitoActualizacionUsuario = true;

            if (esNecesarioActualizarUsuario) {
                if (!cuentaUsuarioAEditar.getNombreUsu().equals(nombreUsuarioLogin) &&
                        usuarioDAO.obtenerUsuarioPorNombreUsuario(nombreUsuarioLogin) != null) {
                    UtilidadesVentana.mostrarAlertaError("Usuario Existente", "El nuevo nombre de usuario '" + nombreUsuarioLogin + "' ya está en uso.");
                    TxtNombreUsuario.requestFocus();
                    return;
                }
                exitoActualizacionUsuario = usuarioDAO.actualizarUsuario(cuentaUsuarioAEditar);
            }

            if (exitoActualizacionCliente && exitoActualizacionUsuario) {
                UtilidadesVentana.mostrarAlertaInformacion("Perfil Actualizado", "Tus datos se han guardado correctamente.");

                Usuario usuarioSesion = SesionUsuario.getUsuarioLogueado();
                if (usuarioSesion != null && esNecesarioActualizarUsuario &&
                        !usuarioSesion.getNombreUsu().equals(cuentaUsuarioAEditar.getNombreUsu())) {
                    usuarioSesion.setNombreUsu(cuentaUsuarioAEditar.getNombreUsu());
                }
                navegarAlPerfilUsuario();
            } else {
                StringBuilder errorMsg = new StringBuilder();
                if (!exitoActualizacionCliente) errorMsg.append("No se pudieron guardar los datos del perfil del cliente. ");
                if (!exitoActualizacionUsuario && esNecesarioActualizarUsuario) errorMsg.append("No se pudieron guardar los cambios en la cuenta de usuario.");
                UtilidadesVentana.mostrarAlertaError("Error al Guardar",
                        errorMsg.toString().trim().isEmpty() ? "Ocurrió un error desconocido al guardar." : errorMsg.toString().trim());
            }
        } catch (SQLException e) {
            UtilidadesExcepciones.mostrarError(e, "Error de Base de Datos",
                    "Ocurrió un error al intentar guardar los cambios en la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Guarda la nueva foto de perfil seleccionada en el sistema de archivos (tanto en 'src/main/resources'
     * como en 'target/classes' si es posible) y devuelve la ruta relativa al classpath para la base de datos.
     * @param fotoArchivo El archivo de la foto seleccionada.
     * @param identificadorUnico Un identificador para ayudar a nombrar el archivo (ej. NIF o email).
     * @return La ruta relativa al classpath para la base de datos, o null si hay un error.
     * @throws IOException Si ocurre un error de E/S durante la copia del archivo.
     */
    private String guardarNuevaFotoDePerfilYObtenerRuta(File fotoArchivo, String identificadorUnico) throws IOException {
        if (fotoArchivo == null) return null;

        String nombreOriginal = fotoArchivo.getName();
        String extension = "";
        int i = nombreOriginal.lastIndexOf('.');
        if (i > 0 && i < nombreOriginal.length() - 1) {
            extension = nombreOriginal.substring(i);
        } else {
            throw new IOException("El archivo de imagen no tiene una extensión válida.");
        }

        String identificadorLimpio = (identificadorUnico != null ? identificadorUnico : "usuario").replaceAll("[^a-zA-Z0-9.-]", "_").replace("@", "_at_");

        String nombreArchivoGenerado = "perfil_" + identificadorLimpio + "_" + System.currentTimeMillis() + extension;

        Path rutaDestinoEnSrc = Paths.get(DIRECTORIO_BASE_FOTOS_PERFIL_FILESYSTEM, nombreArchivoGenerado);
        File carpetaDestinoEnSrc = rutaDestinoEnSrc.getParent().toFile();
        if (!carpetaDestinoEnSrc.exists()) {
            if (!carpetaDestinoEnSrc.mkdirs()) {
                System.err.println("WARN: No se pudo crear la carpeta de destino en 'src': " + carpetaDestinoEnSrc.getAbsolutePath());
            }
        }
        if (carpetaDestinoEnSrc.exists()) {
            Files.copy(fotoArchivo.toPath(), rutaDestinoEnSrc, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("INFO: Foto de perfil guardada en (fuentes): " + rutaDestinoEnSrc.toString());
        }

        Path rutaDestinoEnTarget = null;
        try {
            URL urlRaizResources = getClass().getResource("/");
            if (urlRaizResources != null) {
                Path pathRaizResources = Paths.get(urlRaizResources.toURI());
                Path pathCarpetaDestinoTarget = pathRaizResources.resolve(RUTA_BASE_FOTOS_PERFIL_CLASSPATH.substring(1));

                if (!Files.exists(pathCarpetaDestinoTarget)) {
                    Files.createDirectories(pathCarpetaDestinoTarget);
                }
                rutaDestinoEnTarget = pathCarpetaDestinoTarget.resolve(nombreArchivoGenerado);
                Files.copy(fotoArchivo.toPath(), rutaDestinoEnTarget, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("INFO: Foto de perfil copiada a (target/classes): " + rutaDestinoEnTarget.toString());
            } else {
                System.err.println("WARN: No se pudo obtener la URL raíz de resources. La imagen podría no ser visible inmediatamente en el 'target'.");
            }
        } catch (Exception e) {
            System.err.println("WARN: No se pudo copiar la imagen a 'target/classes'. " + "La imagen se guardó en 'src' pero podría no ser visible inmediatamente: " + e.getMessage());
        }

        return RUTA_BASE_FOTOS_PERFIL_CLASSPATH + nombreArchivoGenerado;
    }


    /**
     * Maneja el evento del botón "Cancelar". Vuelve a la pantalla de perfil del usuario.
     * @param event El evento de acción.
     */
    @FXML
    void Cancelar(ActionEvent event) {
        navegarAlPerfilUsuario();
    }

    /**
     * Maneja el evento del icono "Volver". Vuelve a la pantalla de perfil del usuario.
     * @param event El evento del ratón.
     */
    @FXML
    void Volver(MouseEvent event) {
        navegarAlPerfilUsuario();
    }

    /**
     * Navega de vuelta a la pantalla de Perfil de Usuario, recargando sus datos.
     * Requiere que `cuentaUsuarioAEditar` (el objeto Usuario) no sea null.
     */
    private void navegarAlPerfilUsuario() {
        if (cuentaUsuarioAEditar == null || cuentaUsuarioAEditar.getIdUsuario() <= 0) {
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se puede volver al perfil porque la información del usuario no está disponible.");
            UtilidadesVentana.cambiarEscena(RUTA_FXML_MAIN_CLIENTE, TITULO_MAIN_CLIENTE, true);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_FXML_PERFIL_USUARIO));
            Parent root = loader.load();
            PerfilUsuarioController perfilControllerDestino = loader.getController();

            if (perfilControllerDestino != null) {
                perfilControllerDestino.initData(cuentaUsuarioAEditar.getIdUsuario(), cuentaUsuarioAEditar.getNombreUsu());
            }
            UtilidadesVentana.cambiarEscenaConRoot(root, TITULO_PERFIL_USUARIO, false);
        } catch (Exception e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo volver a la pantalla de perfil: " + e.getMessage() + "\nSerás redirigido a la pantalla principal.");
            UtilidadesVentana.cambiarEscena(RUTA_FXML_MAIN_CLIENTE, TITULO_MAIN_CLIENTE, true);
        }
    }
}