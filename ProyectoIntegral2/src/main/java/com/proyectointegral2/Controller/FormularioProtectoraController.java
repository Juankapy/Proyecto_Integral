package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.dao.UsuarioDao;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Objects;

public class FormularioProtectoraController {

    @FXML private ImageView imgIconoVolver;
    @FXML private Label lblTituloFormulario;
    @FXML private ImageView imgFotoProtectoraEditable; // Declarado correctamente
    @FXML private Button btnCambiarFotoProtectora;
    @FXML private TextField txtNombreProtectora;
    @FXML private TextField txtCIF;
    @FXML private TextField txtEmailProtectora;
    @FXML private TextField txtTelefonoProtectora;
    @FXML private TextField txtCalleProtectora;
    @FXML private TextField txtCiudadProtectora;
    @FXML private TextField txtProvinciaProtectora;
    @FXML private TextField txtCPProtectora;
    @FXML private PasswordField txtPasswordCuenta;
    @FXML private PasswordField txtConfirmPasswordCuenta;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardarCambios;

    private ProtectoraDao protectoraDAO;
    private UsuarioDao usuarioDAO;
    private Protectora protectoraAEditar;
    private Usuario cuentaUsuarioAsociada;
    private File nuevaFotoSeleccionada;

    private final String RUTA_PLACEHOLDER_LOGO = "/assets/Imagenes/iconos/sinusuario.jpg";
    private final String CARPETA_FOTOS_PROTECTORAS = "src/main/resources/assets/Imagenes/logos_protectoras/";


    @FXML
    public void initialize() {
        this.protectoraDAO = new ProtectoraDao();
        this.usuarioDAO = new UsuarioDao();
        // La imagen inicial se carga en initData o si es un nuevo formulario (no implementado aquí)
        // Si este formulario también se usa para "Crear Nueva Protectora", necesitarías
        // una lógica para cargar el placeholder si protectoraAEditar es null al inicio.
        // Por ahora, asumimos que siempre se llama a initDataParaEdicion.
        // Si no, podrías llamar a cargarImagenPlaceholder() aquí.
        // cargarImagenPlaceholder(); // Podría ser una opción si el formulario puede empezar vacío.
    }

    public void initDataParaEdicion(Protectora protectora, Usuario cuentaUsuario) {
        this.protectoraAEditar = protectora;
        this.cuentaUsuarioAsociada = cuentaUsuario;
        if (lblTituloFormulario != null) {
            lblTituloFormulario.setText("Editar Datos de " + Objects.requireNonNullElse(protectora.getNombre(), "Protectora"));
        }
        if (btnGuardarCambios != null) {
            btnGuardarCambios.setText("Guardar Cambios");
        }
        poblarCamposConDatosProtectora();
    }

    private void poblarCamposConDatosProtectora() {
        if (protectoraAEditar != null) {
            if(txtNombreProtectora != null) txtNombreProtectora.setText(protectoraAEditar.getNombre());
            if(txtCIF != null) txtCIF.setText(protectoraAEditar.getCif());
            if(txtEmailProtectora != null) txtEmailProtectora.setText(protectoraAEditar.getEmail());
            if(txtTelefonoProtectora != null) txtTelefonoProtectora.setText(protectoraAEditar.getTelefono());
            if(txtCalleProtectora != null) txtCalleProtectora.setText(protectoraAEditar.getCalle());
            if(txtCiudadProtectora != null) txtCiudadProtectora.setText(protectoraAEditar.getCiudad());
            if(txtProvinciaProtectora != null) txtProvinciaProtectora.setText(protectoraAEditar.getProvincia());
            if(txtCPProtectora != null) txtCPProtectora.setText(protectoraAEditar.getCodigoPostal());

            if(txtPasswordCuenta != null) txtPasswordCuenta.setPromptText("Dejar en blanco para no cambiar");
            if(txtConfirmPasswordCuenta != null) txtConfirmPasswordCuenta.setPromptText("Dejar en blanco para no cambiar");

            String rutaFoto = protectoraAEditar.getRutaFotoPerfil();
            if (rutaFoto != null && !rutaFoto.isEmpty()) {
                cargarFotoActual(rutaFoto);
            } else {
                cargarImagenPlaceholder();
            }
        } else {
            // Limpiar campos si no hay protectora para editar (aunque initData debería asegurar esto)
            limpiarCampos();
        }
    }

    private void limpiarCampos(){
        if(txtNombreProtectora != null) txtNombreProtectora.clear();
        if(txtCIF != null) txtCIF.clear();
        if(txtEmailProtectora != null) txtEmailProtectora.clear();
        if(txtTelefonoProtectora != null) txtTelefonoProtectora.clear();
        if(txtCalleProtectora != null) txtCalleProtectora.clear();
        if(txtCiudadProtectora != null) txtCiudadProtectora.clear();
        if(txtProvinciaProtectora != null) txtProvinciaProtectora.clear();
        if(txtCPProtectora != null) txtCPProtectora.clear();
        if(txtPasswordCuenta != null) txtPasswordCuenta.clear();
        if(txtConfirmPasswordCuenta != null) txtConfirmPasswordCuenta.clear();
        nuevaFotoSeleccionada = null;
        cargarImagenPlaceholder();
    }

    private void cargarFotoActual(String rutaFotoRelativa) {
        if (imgFotoProtectoraEditable == null) return; // Comprobación de nulidad
        if (rutaFotoRelativa != null && !rutaFotoRelativa.trim().isEmpty()) {
            String pathCorregido = rutaFotoRelativa;
            if (!pathCorregido.startsWith("/")) pathCorregido = "/" + pathCorregido;
            try (InputStream stream = getClass().getResourceAsStream(pathCorregido)) {
                if (stream != null) {
                    imgFotoProtectoraEditable.setImage(new Image(stream));
                    return;
                } else System.err.println("WARN: Logo/Foto de protectora no encontrada en classpath: " + pathCorregido);
            } catch (Exception e) {
                System.err.println("Excepción al cargar logo/foto de protectora: " + e.getMessage());
            }
        }
        cargarImagenPlaceholder();
    }

    private void cargarImagenPlaceholder() {
        if (imgFotoProtectoraEditable == null) return; // Comprobación de nulidad
        try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_LOGO)) {
            if (placeholderStream != null) {
                imgFotoProtectoraEditable.setImage(new Image(placeholderStream));
            } else {
                System.err.println("Error: No se pudo cargar placeholder de logo: " + RUTA_PLACEHOLDER_LOGO);
                imgFotoProtectoraEditable.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Excepción crítica cargando placeholder de logo protectora: " + e.getMessage());
        }
    }

    @FXML
    void handleCambiarFoto(ActionEvent event) {
        if (imgFotoProtectoraEditable == null || btnCambiarFotoProtectora == null) return; // Comprobación

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen/Logo de Protectora");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Todos los Archivos", "*.*")
        );
        Stage stage = (Stage) btnCambiarFotoProtectora.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);

        if (archivo != null) {
            this.nuevaFotoSeleccionada = archivo;
            try {
                String imageUrl = archivo.toURI().toURL().toString();
                Image image = new Image(imageUrl);
                imgFotoProtectoraEditable.setImage(image); // Actualiza la previsualización
                System.out.println("Nueva foto previsualizada: " + archivo.getAbsolutePath());
            } catch (MalformedURLException e) {
                UtilidadesVentana.mostrarAlertaError("Error de Imagen", "No se pudo cargar la imagen seleccionada: " + e.getMessage());
            }
        }
    }

    @FXML
    void handleGuardarCambios(ActionEvent event) {
        if (protectoraAEditar == null) {
            UtilidadesVentana.mostrarAlertaError("Error", "No hay datos de protectora para guardar.");
            return;
        }

        String nombre = txtNombreProtectora.getText().trim();
        String cif = txtCIF.getText().trim();
        String email = txtEmailProtectora.getText().trim();
        String telefono = txtTelefonoProtectora.getText().trim();
        String calle = txtCalleProtectora.getText().trim();
        String ciudad = txtCiudadProtectora.getText().trim();
        String provincia = txtProvinciaProtectora.getText().trim();
        String cp = txtCPProtectora.getText().trim();
        String password = txtPasswordCuenta.getText();
        String confirmPassword = txtConfirmPasswordCuenta.getText();

        if (nombre.isEmpty() || email.isEmpty() /* ... más validaciones ... */) {
            UtilidadesVentana.mostrarAlertaError("Campos Vacíos", "Nombre y email son obligatorios.");
            return;
        }
        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            UtilidadesVentana.mostrarAlertaError("Contraseña", "Las nuevas contraseñas no coinciden.");
            return;
        }

        protectoraAEditar.setNombre(nombre);
        protectoraAEditar.setEmail(email);
        protectoraAEditar.setTelefono(telefono);
        protectoraAEditar.setCalle(calle);
        protectoraAEditar.setCiudad(ciudad);
        protectoraAEditar.setProvincia(provincia);
        protectoraAEditar.setCodigoPostal(cp);
        // CIF no se suele editar, pero si lo permites, aquí iría protectoraAEditar.setCif(cif);


        if (nuevaFotoSeleccionada != null) {
            try {
                String nuevaRutaRelativa = guardarNuevaFotoProtectora(nuevaFotoSeleccionada, protectoraAEditar.getCif());
                if (nuevaRutaRelativa != null) {
                    protectoraAEditar.setRutaFotoPerfil(nuevaRutaRelativa);
                }
            } catch (IOException e) {
                UtilidadesVentana.mostrarAlertaError("Error Foto", "Ocurrió un error al procesar la nueva imagen.");
                e.printStackTrace();
                return;
            }
        }

        boolean exitoPassword = true;
        if (cuentaUsuarioAsociada != null && !password.isEmpty()) {
            cuentaUsuarioAsociada.setContrasena(password); // TODO: Encriptar!
            try {
                if (!usuarioDAO.actualizarUsuario(cuentaUsuarioAsociada)) {
                    exitoPassword = false;
                }
            } catch (SQLException e) {
                exitoPassword = false;
                e.printStackTrace();
                UtilidadesVentana.mostrarAlertaError("Error Contraseña Cuenta", "No se pudo actualizar la contraseña: " + e.getMessage());
            }
        }

        boolean exitoProtectora = false;
        try {
            exitoProtectora = protectoraDAO.actualizarProtectora(protectoraAEditar);
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Base de Datos", "No se pudieron guardar los cambios de la protectora: " + e.getMessage());
        }

        if (exitoProtectora && exitoPassword) {
            UtilidadesVentana.mostrarAlertaInformacion("Datos Actualizados", "Los datos se han guardado correctamente.");
            handleVolver(null);
        } else if (exitoProtectora && !exitoPassword && !password.isEmpty()){
            UtilidadesVentana.mostrarAlertaAdvertencia("Actualización Parcial", "Datos de protectora guardados, pero error al actualizar contraseña.", null);
        } else if (!exitoProtectora) {
            // El error ya se mostró
        }
    }

    private String guardarNuevaFotoProtectora(File foto, String identificadorUnico) throws IOException {
        if (foto == null) return null;
        File carpetaDestino = new File(CARPETA_FOTOS_PROTECTORAS);
        if (!carpetaDestino.exists()) {
            if (!carpetaDestino.mkdirs()) {
                System.err.println("No se pudo crear la carpeta de destino: " + carpetaDestino.getAbsolutePath());
                return null;
            }
        }
        String nombreOriginal = foto.getName();
        String extension = "";
        int i = nombreOriginal.lastIndexOf('.');
        if (i > 0) extension = nombreOriginal.substring(i);
        String identificadorLimpio = identificadorUnico.replaceAll("[^a-zA-Z0-9.-]", "_");
        String nombreArchivo = "logo_" + identificadorLimpio + "_" + System.currentTimeMillis() + extension;
        Path pathDestino = Paths.get(carpetaDestino.getAbsolutePath(), nombreArchivo);
        try {
            Files.copy(foto.toPath(), pathDestino, StandardCopyOption.REPLACE_EXISTING);
            return "/assets/Imagenes/logos_protectoras/" + nombreArchivo;
        } catch (IOException e) {
            System.err.println("Error al guardar la nueva imagen: " + e.getMessage());
            throw e;
        }
    }

    @FXML
    void handleCancelar(ActionEvent event) {
        handleVolver(null);
    }

    @FXML
    void handleVolver(MouseEvent event) { // Acepta MouseEvent por el icono, o null si es llamado por otro botón
        UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/MainProtectora.fxml", "Panel Protectora", true);
    }
}