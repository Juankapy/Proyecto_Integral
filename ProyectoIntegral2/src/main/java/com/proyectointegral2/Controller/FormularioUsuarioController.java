package com.proyectointegral2.Controller;

import com.proyectointegral2.dao.UsuarioDao; // Asegúrate que este paquete sea correcto
import com.proyectointegral2.Model.Usuario; // Asegúrate que este paquete sea correcto
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import java.io.IOException; // Para la firma de guardarNuevaFotoDePerfil
import java.net.MalformedURLException;
import java.io.InputStream;
import java.nio.file.Files; // Para copiar archivos
import java.nio.file.Path;  // Para rutas de archivos
import java.nio.file.Paths; // Para rutas de archivos
import java.nio.file.StandardCopyOption; // Para reemplazar archivos existentes
import java.util.Objects;

public class FormularioUsuarioController {

    @FXML private ImageView imgIconoVolver;
    @FXML private Label lblTituloFormulario;
    @FXML private ImageView imgFotoPerfilEditable;
    @FXML private Button btnCambiarFoto;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDireccion;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private UsuarioDao usuarioDAO;
    private Usuario usuarioAEditar;
    private File nuevaFotoSeleccionada; // Almacena el archivo de la nueva foto
    private String rutaRelativaNuevaFoto; // Almacena la ruta relativa para la BD
    private boolean modoEdicion = false;

    private final String RUTA_PLACEHOLDER_USUARIO = "/assets/Imagenes/iconos/sinusuario.jpg";
    // Define una carpeta base dentro de tus recursos para guardar las fotos de perfil de los usuarios
    // Esto es un ejemplo, ajusta según tu proyecto. La idea es que sea accesible por el classpath.
    private final String CARPETA_FOTOS_PERFIL_USUARIOS = "src/main/resources/assets/Imagenes/perfiles_usuarios/";


    @FXML
    public void initialize() {
        this.usuarioDAO = new UsuarioDao(); // Instanciar tu DAO
        cargarImagenPlaceholder();
    }

    public void initDataParaEdicion(Usuario usuario) {
        this.usuarioAEditar = usuario;
        this.modoEdicion = true;
        lblTituloFormulario.setText("Editar Perfil");
        btnGuardar.setText("Guardar Cambios");
        poblarCamposConDatosUsuario();
    }

    public void initDataParaNuevoUsuario() {
//        this.usuarioAEditar = new Usuario();
//        this.modoEdicion = false;
//        lblTituloFormulario.setText("Crear Nuevo Usuario");
//        btnGuardar.setText("Crear Usuario");
//        limpiarCampos();
//        cargarImagenPlaceholder();
    }

    private void poblarCamposConDatosUsuario() {
//        if (usuarioAEditar != null) {
//            txtNombre.setText(Objects.requireNonNullElse(usuarioAEditar.getNombre(), ""));
//            txtApellidos.setText(Objects.requireNonNullElse(usuarioAEditar.getApellidos(), ""));
//            txtEmail.setText(Objects.requireNonNullElse(usuarioAEditar.getEmail(), ""));
//            txtTelefono.setText(Objects.requireNonNullElse(usuarioAEditar.getTelefono(), ""));
//            txtDireccion.setText(Objects.requireNonNullElse(usuarioAEditar.getDireccion(), ""));
//
//            txtPassword.setPromptText("Dejar en blanco para no cambiar");
//            txtConfirmPassword.setPromptText("Dejar en blanco para no cambiar");
//
//            String rutaFoto = usuarioAEditar.getRutaFotoPerfil();
//            if (rutaFoto != null && !rutaFoto.isEmpty()) {
//                try {
//                    // Asumimos que la ruta guardada en la BD es relativa al classpath
//                    InputStream stream = getClass().getResourceAsStream(rutaFoto);
//                    if (stream != null) {
//                        imgFotoPerfilEditable.setImage(new Image(stream));
//                    } else {
//                        System.err.println("No se encontró la imagen de perfil en: " + rutaFoto);
//                        cargarImagenPlaceholder();
//                    }
//                } catch (Exception e) {
//                    cargarImagenPlaceholder();
//                    System.err.println("Error al cargar foto de perfil del usuario: " + e.getMessage());
//                }
//            } else {
//                cargarImagenPlaceholder();
//            }
//        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtApellidos.clear();
        txtEmail.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
        nuevaFotoSeleccionada = null;
        rutaRelativaNuevaFoto = null;
        cargarImagenPlaceholder();
    }

    private void cargarImagenPlaceholder() {
        try {
            InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_USUARIO);
            imgFotoPerfilEditable.setImage(new Image(Objects.requireNonNull(placeholderStream)));
        } catch (Exception e) {
            System.err.println("Error cargando imagen placeholder de usuario: " + e.getMessage());
            imgFotoPerfilEditable.setImage(null);
            imgFotoPerfilEditable.setStyle("-fx-background-color: #E0E0E0; -fx-border-color: #A1887F; -fx-border-radius: 65; -fx-background-radius: 65;");
        }
    }

    @FXML
    void CambiarFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto de Perfil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Todos los Archivos", "*.*")
        );
        Stage stage = (Stage) btnCambiarFoto.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo != null) {
            nuevaFotoSeleccionada = archivo; // Guardar el File para el proceso de guardado
            try {
                Image image = new Image(archivo.toURI().toURL().toString());
                imgFotoPerfilEditable.setImage(image);
            } catch (MalformedURLException e) {
                UtilidadesVentana.mostrarAlertaError("Error de Imagen", "No se pudo cargar la imagen seleccionada.");
                System.err.println("Error al cargar la imagen seleccionada: " + e.getMessage());
            }
        }
    }

    @FXML
    void Guardar(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campos Vacíos", "Nombre, apellidos y email son obligatorios.");
            return;
        }
        // Validar email (simple, puedes usar regex más complejo)
        if (!email.contains("@") || !email.contains(".")) {
            UtilidadesVentana.mostrarAlertaError("Email Inválido", "Por favor, introduce un email válido.");
            return;
        }

        // Si se está creando un nuevo usuario O si se está editando y se ha escrito una nueva contraseña
        if (!modoEdicion || (modoEdicion && !password.isEmpty())) {
            if (password.isEmpty()) {
                UtilidadesVentana.mostrarAlertaError("Contraseña Vacía", "La contraseña es obligatoria.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                UtilidadesVentana.mostrarAlertaError("Contraseña", "Las contraseñas no coinciden.");
                return;
            }
            // Aquí deberías llamar a un método para encriptar 'password' antes de asignarlo al objeto Usuario
            // String passwordEncriptada = EncriptadorUtil.encriptar(password);
            // usuarioAEditar.setPassword(passwordEncriptada); // O nuevoUsuario.setPassword(passwordEncriptada)
        }


        // Asignar valores al objeto Usuario
//        usuarioAEditar.setNombre(nombre);
//        usuarioAEditar.setApellidos(apellidos);
//        usuarioAEditar.setEmail(email);
//        usuarioAEditar.setTelefono(telefono);
//        usuarioAEditar.setDireccion(direccion);

        // Solo actualiza la contraseña en el objeto si se proporcionó una nueva
        if (!password.isEmpty()) {
            // IDEALMENTE: Encriptar contraseña antes de setearla
            // String passwordEncriptada = miMetodoDeEncriptacion(password);
            // usuarioAEditar.setPassword(passwordEncriptada);
//            usuarioAEditar.setPassword(password); // Placeholder sin encriptación
        }


        // Manejar la foto de perfil
        if (nuevaFotoSeleccionada != null) {
            try {
                rutaRelativaNuevaFoto = guardarNuevaFotoDePerfil(nuevaFotoSeleccionada, email); // Pasar email para nombre único
                if (rutaRelativaNuevaFoto != null) {
//                    usuarioAEditar.setRutaFotoPerfil(rutaRelativaNuevaFoto);
                } else {
                    // Error al guardar la foto, no actualizamos la ruta en el objeto
                    // UtilidadesVentana.mostrarAlertaError("Error Foto", "No se pudo guardar la nueva foto de perfil.");
                    // Decidir si continuar sin la nueva foto o detenerse. Por ahora, continuamos.
                }
            } catch (IOException e) {
                UtilidadesVentana.mostrarAlertaError("Error Foto", "Ocurrió un error al procesar la nueva foto de perfil.");
                e.printStackTrace();
                // No continuar si hay error de IO con la foto.
                return;
            }
        }


        boolean exitoOperacion;
        if (modoEdicion) {
            // --- LLAMADA AL DAO PARA ACTUALIZAR ---
//            System.out.println("Intentando actualizar usuario: " + usuarioAEditar.getNombre());
            // exitoOperacion = usuarioDAO.actualizarUsuario(usuarioAEditar); // DESCOMENTAR CUANDO DAO ESTÉ LISTO
            exitoOperacion = true; // SIMULACIÓN DAO
            // ------------------------------------

            if (exitoOperacion) {
                UtilidadesVentana.mostrarAlertaInformacion("Perfil Actualizado", "Tus datos se han guardado correctamente.");
                Volver(null);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error al Guardar", "No se pudieron guardar los cambios en la base de datos.");
            }
        } else {
            // Modo Creación Nuevo Usuario
            // --- LLAMADA AL DAO PARA CREAR ---
//            System.out.println("Intentando crear nuevo usuario: " + usuarioAEditar.getNombre());
            // exitoOperacion = usuarioDAO.crearUsuario(usuarioAEditar); // DESCOMENTAR CUANDO DAO ESTÉ LISTO
            exitoOperacion = true; // SIMULACIÓN DAO
            // ---------------------------------

            if (exitoOperacion) {
                UtilidadesVentana.mostrarAlertaInformacion("Registro Exitoso", "Usuario creado correctamente. Ahora puedes iniciar sesión.");
                UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/Login.fxml", "Inicio de Sesión", false);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error de Registro", "No se pudo crear el usuario en la base de datos.");
            }
        }
    }

    /**
     * Guarda la foto seleccionada en una ubicación permanente y devuelve la ruta relativa.
     * @param foto El archivo de la foto seleccionada.
     * @param identificadorUnico Un identificador para hacer el nombre del archivo único (ej. email o ID de usuario).
     * @return La ruta relativa para guardar en la BD (ej. "/assets/Imagenes/perfiles_usuarios/...") o null si falla.
     * @throws IOException Si ocurre un error al copiar el archivo.
     */
    private String guardarNuevaFotoDePerfil(File foto, String identificadorUnico) throws IOException {
        if (foto == null) return null;

        File carpetaDestino = new File(CARPETA_FOTOS_PERFIL_USUARIOS);
        if (!carpetaDestino.exists()) {
            if (!carpetaDestino.mkdirs()) {
                System.err.println("No se pudo crear la carpeta de destino para fotos: " + carpetaDestino.getAbsolutePath());
                return null;
            }
        }

        // Crear un nombre de archivo único para evitar colisiones y problemas con espacios/caracteres especiales
        String nombreOriginal = foto.getName();
        String extension = "";
        int i = nombreOriginal.lastIndexOf('.');
        if (i > 0) {
            extension = nombreOriginal.substring(i); // Incluye el punto, ej. ".jpg"
        }
        // Limpiar el identificador para usarlo en el nombre del archivo
        String identificadorLimpio = identificadorUnico.replaceAll("[^a-zA-Z0-9.-]", "_");
        String nombreArchivo = "perfil_" + identificadorLimpio + "_" + System.currentTimeMillis() + extension;

        Path pathDestino = Paths.get(carpetaDestino.getAbsolutePath(), nombreArchivo);

        try {
            Files.copy(foto.toPath(), pathDestino, StandardCopyOption.REPLACE_EXISTING);
            // Devolver la ruta relativa al classpath para usar en FXML y guardar en BD
            return "/assets/Imagenes/perfiles_usuarios/" + nombreArchivo;
        } catch (IOException e) {
            System.err.println("Error al guardar la nueva foto de perfil: " + e.getMessage());
            throw e; // Relanzar para que handleGuardar pueda manejarlo
        }
    }

    @FXML
    void Cancelar(ActionEvent event) {
        Volver(null);
    }

    @FXML
    void Volver(MouseEvent event) {
        if (modoEdicion) {
            // Asumimos que la vista de perfil es dinámica (pantalla completa/redimensionable)
            UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/PerfilUsuario.fxml", "Perfil de Usuario", true);
        } else {
            // Si es nuevo registro y cancela, volver a la selección de rol
            UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/InicioChoose.fxml", "Selección de Rol", false);
        }
    }
}