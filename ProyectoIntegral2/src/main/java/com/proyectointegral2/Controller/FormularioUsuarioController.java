package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.Model.SesionUsuario; // Para actualizar sesión si cambia nombre de usuario
import com.proyectointegral2.utils.UtilidadesVentana;
import com.proyectointegral2.utils.UtilidadesExcepciones; // Si lo usas para manejo de excepciones
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable; // Si necesitas initialize con URL y ResourceBundle
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker; // Para Fecha de Nacimiento del cliente
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL; // Para Initializable
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

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

    private final String RUTA_PLACEHOLDER_USUARIO = "/assets/Imagenes/iconos/sinusuario.jpg";
    private final String CARPETA_FOTOS_PERFIL_USUARIOS = "src/main/resources/assets/Imagenes/perfiles_usuarios/";


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.clienteDAO = new ClienteDao();
        this.usuarioDAO = new UsuarioDao();
        if (lblTituloFormulario != null) lblTituloFormulario.setText("Editar Perfil");
        if (BtnGuardar != null) BtnGuardar.setText("Guardar Cambios");
        cargarImagenPlaceholder();
    }

    public void initDataParaEdicion(Cliente cliente, Usuario cuentaUsuario) {
        this.clienteAEditar = cliente;
        this.cuentaUsuarioAEditar = cuentaUsuario;

        if (lblTituloFormulario != null && cliente != null) {
            lblTituloFormulario.setText("Editar Perfil de " + Objects.requireNonNullElse(cliente.getNombre(), "Usuario"));
        }
        if (BtnGuardar != null) {
            BtnGuardar.setText("Guardar Cambios");
        }
        poblarCamposConDatos();
    }


    private void poblarCamposConDatos() {
        if (clienteAEditar != null) {
            TxtNombre.setText(Objects.requireNonNullElse(clienteAEditar.getNombre(), ""));
            TxtApellido.setText(Objects.requireNonNullElse(clienteAEditar.getApellidos(), ""));
            TxtNIF.setText(Objects.requireNonNullElse(clienteAEditar.getNif(), ""));
            DpFechaNacimiento.setValue(clienteAEditar.getFechaNacimiento()); // Asume que Cliente tiene getFechaNacimiento() que devuelve LocalDate
            TxtDireccion.setText(Objects.requireNonNullElse(clienteAEditar.getCalle(), ""));
            TxtProvincia.setText(Objects.requireNonNullElse(clienteAEditar.getProvincia(), ""));
            TxtCiudad.setText(Objects.requireNonNullElse(clienteAEditar.getCiudad(), ""));
            TxtCP.setText(Objects.requireNonNullElse(clienteAEditar.getCodigoPostal(), ""));
            TxtTelefono.setText(Objects.requireNonNullElse(clienteAEditar.getTelefono(), ""));
            TxtEmail.setText(Objects.requireNonNullElse(clienteAEditar.getEmail(), ""));

            String rutaFoto = clienteAEditar.getRutaFotoPerfil();
            cargarFotoActual(rutaFoto);
        } else {
            limpiarCampos();
            cargarImagenPlaceholder();
        }

        if (cuentaUsuarioAEditar != null) {
            TxtNombreUsuario.setText(Objects.requireNonNullElse(cuentaUsuarioAEditar.getNombreUsu(), ""));
        } else {
            if(TxtNombreUsuario != null) TxtNombreUsuario.clear();
        }
        if(TxtPassword != null) TxtPassword.setPromptText("Dejar en blanco para no cambiar");
        if(TxtConfirmPassword != null) TxtConfirmPassword.setPromptText("Confirmar si cambia");
    }

    private void limpiarCampos() {
        if(TxtNombre != null) TxtNombre.clear();
        if(TxtApellido != null) TxtApellido.clear();
        if(TxtNIF != null) TxtNIF.clear();
        if(DpFechaNacimiento != null) DpFechaNacimiento.setValue(null);
        if(TxtDireccion != null) TxtDireccion.clear();
        if(TxtProvincia != null) TxtProvincia.clear();
        if(TxtCiudad != null) TxtCiudad.clear();
        if(TxtCP != null) TxtCP.clear();
        if(TxtTelefono != null) TxtTelefono.clear();
        if(TxtEmail != null) TxtEmail.clear();
        if(TxtNombreUsuario != null) TxtNombreUsuario.clear();
        if(TxtPassword != null) TxtPassword.clear();
        if(TxtConfirmPassword != null) TxtConfirmPassword.clear();
        nuevaFotoSeleccionada = null;
        cargarImagenPlaceholder();
    }

    private void cargarFotoActual(String rutaFotoRelativa) {
        if (imgFotoPerfilEditable == null) return;
        if (rutaFotoRelativa != null && !rutaFotoRelativa.trim().isEmpty()) {
            String pathCorregido = rutaFotoRelativa;
            if (!pathCorregido.startsWith("\\")) pathCorregido = "\\" + pathCorregido;
            try (InputStream stream = getClass().getResourceAsStream(pathCorregido)) {
                if (stream != null) {
                    imgFotoPerfilEditable.setImage(new Image(stream));
                    return;
                } else System.err.println("WARN: Foto de perfil no encontrada en classpath: " + pathCorregido);
            } catch (Exception e) {
                System.err.println("Excepción al cargar foto de perfil: " + e.getMessage());
            }
        }
        cargarImagenPlaceholder();
    }

    private void cargarImagenPlaceholder() {
        if (imgFotoPerfilEditable == null) return;
        try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_USUARIO)) {
            if (placeholderStream != null) {
                imgFotoPerfilEditable.setImage(new Image(placeholderStream));
            } else {
                System.err.println("Error: Placeholder de usuario no encontrado: " + RUTA_PLACEHOLDER_USUARIO);
                imgFotoPerfilEditable.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Excepción crítica cargando placeholder de usuario: " + e.getMessage());
        }
    }

    @FXML
    void CambiarFoto(ActionEvent event) {
        if (imgFotoPerfilEditable == null || btnCambiarFoto == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto de Perfil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) btnCambiarFoto.getScene().getWindow();
        File archivo = fileChooser.showOpenDialog(stage);
        if (archivo != null) {
            this.nuevaFotoSeleccionada = archivo;
            try {
                Image image = new Image(archivo.toURI().toURL().toString());
                imgFotoPerfilEditable.setImage(image);
            } catch (MalformedURLException e) {
                UtilidadesVentana.mostrarAlertaError("Error de Imagen", "No se pudo previsualizar la imagen: " + e.getMessage());
            }
        }
    }

    @FXML
    void Guardar(ActionEvent event) {
        if (clienteAEditar == null || cuentaUsuarioAEditar == null) {
            UtilidadesVentana.mostrarAlertaError("Error Interno", "No hay datos de usuario o cliente cargados para guardar.");
            return;
        }

        // Recoger datos del formulario
        String nombre = TxtNombre.getText().trim();
        String apellidos = TxtApellido.getText().trim();
        String nif = TxtNIF.getText().trim();
        LocalDate fechaNacimiento = DpFechaNacimiento.getValue();
        String calle = TxtDireccion.getText().trim();
        String provincia = TxtProvincia.getText().trim();
        String ciudad = TxtCiudad.getText().trim();
        String cp = TxtCP.getText().trim();
        String telefono = TxtTelefono.getText().trim();
        String emailCliente = TxtEmail.getText().trim();

        String nombreUsuarioLogin = TxtNombreUsuario.getText().trim();
        String passwordNueva = TxtPassword.getText();
        String confirmPasswordNueva = TxtConfirmPassword.getText();

        if (nombre.isEmpty() || apellidos.isEmpty() || emailCliente.isEmpty() || nombreUsuarioLogin.isEmpty() || nif.isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campos Vacíos", "Nombre, apellidos, NIF, email del cliente y nombre de usuario de cuenta son obligatorios.");
            return;
        }
        if (!passwordNueva.isEmpty() && !passwordNueva.equals(confirmPasswordNueva)) {
            UtilidadesVentana.mostrarAlertaError("Contraseña", "Las nuevas contraseñas no coinciden.");
            return;
        }
        clienteAEditar.setNombre(nombre);
        clienteAEditar.setApellidos(apellidos);
        clienteAEditar.setNif(nif);
        clienteAEditar.setFechaNacimiento(fechaNacimiento);
        clienteAEditar.setCalle(calle);
        clienteAEditar.setProvincia(provincia);
        clienteAEditar.setCiudad(ciudad);
        clienteAEditar.setCodigoPostal(cp);
        clienteAEditar.setTelefono(telefono);
        clienteAEditar.setEmail(emailCliente);

        boolean cuentaActualizada = false;
        if (!cuentaUsuarioAEditar.getNombreUsu().equals(nombreUsuarioLogin) || !passwordNueva.isEmpty()) {
            cuentaUsuarioAEditar.setNombreUsu(nombreUsuarioLogin);
            if (!passwordNueva.isEmpty()) {
                cuentaUsuarioAEditar.setContrasena(passwordNueva);
            }
            cuentaActualizada = true;
        }

        if (nuevaFotoSeleccionada != null) {
            try {
                String rutaRelativaFotoGuardada = guardarNuevaFotoDePerfil(nuevaFotoSeleccionada, clienteAEditar.getEmail());
                if (rutaRelativaFotoGuardada != null) {
                    clienteAEditar.setRutaFotoPerfil(rutaRelativaFotoGuardada);
                }
            } catch (IOException e) {
                UtilidadesVentana.mostrarAlertaError("Error Foto", "Ocurrió un error al procesar la nueva foto de perfil.");
                e.printStackTrace();
                return;
            }
        }

        try {
            boolean exitoCliente = clienteDAO.actualizarCliente(clienteAEditar);
            boolean exitoUsuario = true;

            if (cuentaActualizada) {
                exitoUsuario = usuarioDAO.actualizarUsuario(cuentaUsuarioAEditar);
            }

            if (exitoCliente && exitoUsuario) {
                UtilidadesVentana.mostrarAlertaInformacion("Perfil Actualizado", "Tus datos se han guardado correctamente.");
                if (SesionUsuario.getUsuarioLogueado() != null &&
                        !SesionUsuario.getUsuarioLogueado().getNombreUsu().equals(cuentaUsuarioAEditar.getNombreUsu())) {
                    SesionUsuario.getUsuarioLogueado().setNombreUsu(cuentaUsuarioAEditar.getNombreUsu());
                }
                Volver(null);
            } else {
                String errorMsg = "";
                if (!exitoCliente) errorMsg += "No se pudieron guardar los datos del perfil del cliente. ";
                if (!exitoUsuario && cuentaActualizada) errorMsg += "No se pudieron guardar los cambios en la cuenta de usuario.";
                UtilidadesVentana.mostrarAlertaError("Error al Guardar", errorMsg.trim().isEmpty() ? "Ocurrió un error desconocido." : errorMsg.trim());
            }
        } catch (SQLException e) {
            UtilidadesExcepciones.mostrarError(e, "Error de Base de Datos", "Ocurrió un error al guardar los cambios.");
            e.printStackTrace();
        }
    }

    private String guardarNuevaFotoDePerfil(File foto, String identificadorUnico) throws IOException {
        if (foto == null) return null;

        String nombreOriginal = foto.getName();
        String extension = "";
        int i = nombreOriginal.lastIndexOf('.');
        if (i > 0) extension = nombreOriginal.substring(i);
        String identificadorLimpio = identificadorUnico.replaceAll("[^a-zA-Z0-9.-]", "_").replace("@", "_at_");
        String nombreArchivoRelativo = "perfil_" + identificadorLimpio + "_" + System.currentTimeMillis() + extension;
        String rutaRelativaClasspath = "/assets/Imagenes/perfiles_usuarios/" + nombreArchivoRelativo;

        Path pathFuente = Paths.get("src", "main", "resources", "assets", "Imagenes", "perfiles_usuarios", nombreArchivoRelativo);
        File carpetaDestinoFuente = pathFuente.getParent().toFile();
        if (!carpetaDestinoFuente.exists()) {
            if (!carpetaDestinoFuente.mkdirs()) {
                System.err.println("No se pudo crear la carpeta de destino en fuentes: " + carpetaDestinoFuente.getAbsolutePath());
            }
        }

        Path pathTarget = null;
        try {
            URL resourcesUrl = getClass().getResource("/");
            if (resourcesUrl != null) {
                File targetResourcesDir = new File(resourcesUrl.toURI());
                File targetAssetsDir = new File(targetResourcesDir, "assets/Imagenes/perfiles_usuarios");
                if (!targetAssetsDir.exists()) {
                    targetAssetsDir.mkdirs();
                }
                pathTarget = Paths.get(targetAssetsDir.getAbsolutePath(), nombreArchivoRelativo);
            }
        } catch (Exception e) {
            System.err.println("WARN: No se pudo determinar la ruta a target/classes/assets. La imagen podría no ser visible inmediatamente. " + e.getMessage());
        }


        try {
            Files.copy(foto.toPath(), pathFuente, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Foto guardada en (fuentes): " + pathFuente.toString());

            if (pathTarget != null) {
                Files.copy(foto.toPath(), pathTarget, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Foto copiada a (target): " + pathTarget.toString());
            }

            return rutaRelativaClasspath;

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
        String perfilUsuarioFxml = "/com/proyectointegral2/Vista/PerfilUsuario.fxml";
        String titulo = "Mi Perfil";

        if (cuentaUsuarioAEditar != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(perfilUsuarioFxml));
                Parent root = loader.load();
                PerfilUsuarioController perfilController = loader.getController();
                if (perfilController != null) {
                    perfilController.initData(cuentaUsuarioAEditar.getIdUsuario(), cuentaUsuarioAEditar.getNombreUsu());
                }
                UtilidadesVentana.cambiarEscenaConRoot(root, titulo, false);
            } catch (Exception e) {
                e.printStackTrace();
                UtilidadesVentana.mostrarAlertaError("Error", "No se pudo volver al perfil: " + e.getMessage());
                UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/Main.fxml", "Panel Cliente", true);
            }
        }
    }
}