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
    private File nuevaFotoSeleccionada;
    private String rutaRelativaNuevaFoto;
    private boolean modoEdicion = false;

    private final String RUTA_PLACEHOLDER_USUARIO = "/assets/Imagenes/iconos/sinusuario.jpg";
    private final String CARPETA_FOTOS_PERFIL_USUARIOS = "src/main/resources/assets/Imagenes/perfiles_usuarios/";


    @FXML
    public void initialize() {
        this.usuarioDAO = new UsuarioDao();
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

    }

    private void poblarCamposConDatosUsuario() {
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
            nuevaFotoSeleccionada = archivo;
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
        if (!email.contains("@") || !email.contains(".")) {
            UtilidadesVentana.mostrarAlertaError("Email Inválido", "Por favor, introduce un email válido.");
            return;
        }

        if (!modoEdicion || (modoEdicion && !password.isEmpty())) {
            if (password.isEmpty()) {
                UtilidadesVentana.mostrarAlertaError("Contraseña Vacía", "La contraseña es obligatoria.");
                return;
            }
            if (!password.equals(confirmPassword)) {
                UtilidadesVentana.mostrarAlertaError("Contraseña", "Las contraseñas no coinciden.");
                return;
            }
        }

        if (!password.isEmpty()) {
         }

        if (nuevaFotoSeleccionada != null) {
            try {
                rutaRelativaNuevaFoto = guardarNuevaFotoDePerfil(nuevaFotoSeleccionada, email);
                if (rutaRelativaNuevaFoto != null) {
                } else {
                }
            } catch (IOException e) {
                UtilidadesVentana.mostrarAlertaError("Error Foto", "Ocurrió un error al procesar la nueva foto de perfil.");
                e.printStackTrace();
                return;
            }
        }


        boolean exitoOperacion;
        if (modoEdicion) {
            exitoOperacion = true;
            if (exitoOperacion) {
                UtilidadesVentana.mostrarAlertaInformacion("Perfil Actualizado", "Tus datos se han guardado correctamente.");
                Volver(null);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error al Guardar", "No se pudieron guardar los cambios en la base de datos.");
            }
        } else {
            exitoOperacion = true;
            if (exitoOperacion) {
                UtilidadesVentana.mostrarAlertaInformacion("Registro Exitoso", "Usuario creado correctamente. Ahora puedes iniciar sesión.");
                UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/Login.fxml", "Inicio de Sesión", false);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error de Registro", "No se pudo crear el usuario en la base de datos.");
            }
        }
    }

    private String guardarNuevaFotoDePerfil(File foto, String identificadorUnico) throws IOException {
        if (foto == null) return null;

        File carpetaDestino = new File(CARPETA_FOTOS_PERFIL_USUARIOS);
        if (!carpetaDestino.exists()) {
            if (!carpetaDestino.mkdirs()) {
                System.err.println("No se pudo crear la carpeta de destino para fotos: " + carpetaDestino.getAbsolutePath());
                return null;
            }
        }

        String nombreOriginal = foto.getName();
        String extension = "";
        int i = nombreOriginal.lastIndexOf('.');
        if (i > 0) {
            extension = nombreOriginal.substring(i);
        }

        String identificadorLimpio = identificadorUnico.replaceAll("[^a-zA-Z0-9.-]", "_");
        String nombreArchivo = "perfil_" + identificadorLimpio + "_" + System.currentTimeMillis() + extension;

        Path pathDestino = Paths.get(carpetaDestino.getAbsolutePath(), nombreArchivo);

        try {
            Files.copy(foto.toPath(), pathDestino, StandardCopyOption.REPLACE_EXISTING);
            return "/assets/Imagenes/perfiles_usuarios/" + nombreArchivo;
        } catch (IOException e) {
            System.err.println("Error al guardar la nueva foto de perfil: " + e.getMessage());
            throw e;
        }
    }

    @FXML
    void Cancelar(ActionEvent event) {
        Volver(null);
    }

    @FXML
    void Volver(MouseEvent event) {
        if (modoEdicion) {
            UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/PerfilUsuario.fxml", "Perfil de Usuario", true);
        } else {
            UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/InicioChoose.fxml", "Selección de Rol", false);
        }
    }
}