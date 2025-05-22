package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.utils.UtilidadesExcepciones;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Controlador para la pantalla de registro de una nueva Protectora.
 * Gestiona la entrada de datos del formulario, valida la información,
 * y crea un nuevo usuario de tipo "Protectora" y su perfil de Protectora asociado
 * en la base de datos.
 */
public class RegistroProtectoraController implements Initializable {

    // --- Componentes FXML ---
    @FXML private TextField TxtNombreProtectora;
    @FXML private TextField TxtCIF;
    @FXML private TextField TxtDireccionProtectora;
    @FXML private TextField TxtProvinciaProtectora;
    @FXML private TextField TxtCPProtectora;
    @FXML private TextField TxtCiudadProtectora;
    @FXML private TextField TxtTelProtectora;
    @FXML private TextField TxtNombreUsuarioCuenta;
    @FXML private TextField TxtCorreoCuenta;
    @FXML private PasswordField TxtContraCuenta;
    @FXML private PasswordField TxtConfirmarContraCuenta;
    @FXML private ImageView ImgIconoSalida;
    @FXML private Button BtnConfirmar;

    // --- DAOs (Data Access Objects) ---
    private UsuarioDao usuarioDAO;
    private ProtectoraDao protectoraDAO;

    // --- Constantes ---
    private static final String ROL_USUARIO_PROTECTORA = "PROTECTORA";
    private static final String RUTA_FXML_LOGIN = "/com/proyectointegral2/Vista/Login.fxml";
    private static final String TITULO_VENTANA_LOGIN = "Inicio de Sesión - Dogpuccino";

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Método de inicialización llamado por JavaFX después de que los campos FXML han sido inyectados.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si desconocida.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            usuarioDAO = new UsuarioDao();
            protectoraDAO = new ProtectoraDao();
        } catch (Exception e) {
            System.err.println("Error crítico al inicializar DAOs en RegistroProtectoraController: " + e.getMessage());
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sistema", "No se pudo inicializar el acceso a la base de datos. El registro no funcionará.");
            if (BtnConfirmar != null) BtnConfirmar.setDisable(true);
        }
    }

    /**
     * Maneja el evento de clic del botón "Confirmar Registro".
     * Recolecta los datos del formulario, los valida, y si son correctos,
     * procede a crear el usuario y la protectora en la base de datos.
     * @param event El evento de acción que disparó este método.
     */
    @FXML
    public void ConfirmarRegistroProtectora(ActionEvent event) {
        // 1. Recolectar datos de los campos del formulario.
        String nombreProtectora = TxtNombreProtectora.getText().trim();
        String cif = TxtCIF.getText().trim();
        String calleProtectora = TxtDireccionProtectora.getText().trim();
        String provinciaProtectora = TxtProvinciaProtectora.getText().trim();
        String cpProtectora = TxtCPProtectora.getText().trim();
        String ciudadProtectora = TxtCiudadProtectora.getText().trim();
        String telefonoProtectora = TxtTelProtectora.getText().trim();
        String nombreUsuarioLogin = TxtNombreUsuarioCuenta.getText().trim();
        String emailCuenta = TxtCorreoCuenta.getText().trim();
        String contrasenaLogin = TxtContraCuenta.getText();
        String confirmarContrasena = TxtConfirmarContraCuenta.getText();

        // 2. Validar los datos recolectados.
        if (!validarEntradas(nombreProtectora, cif, calleProtectora, provinciaProtectora, cpProtectora,
                ciudadProtectora, telefonoProtectora, nombreUsuarioLogin, emailCuenta,
                contrasenaLogin, confirmarContrasena)) {
            return;
        }

        // 3. Lógica de creación de usuario y protectora.
        try {
            if (usuarioDAO.obtenerUsuarioPorNombreUsuario(nombreUsuarioLogin) != null) {
                UtilidadesVentana.mostrarAlertaError("Usuario Existente", "El nombre de usuario '" + nombreUsuarioLogin + "' ya está en uso. Por favor, elija otro.");
                TxtNombreUsuarioCuenta.requestFocus();
                return;
            }

            Usuario nuevaCuentaUsuario = new Usuario();
            nuevaCuentaUsuario.setNombreUsu(nombreUsuarioLogin);
            nuevaCuentaUsuario.setContrasena(contrasenaLogin);
            nuevaCuentaUsuario.setRol(ROL_USUARIO_PROTECTORA);

            int idUsuarioCreado = usuarioDAO.crearUsuario(nuevaCuentaUsuario);

            if (idUsuarioCreado != -1 && idUsuarioCreado > 0) {

                Protectora nuevaProtectora = new Protectora();
                nuevaProtectora.setNombre(nombreProtectora);
                nuevaProtectora.setCif(cif);
                nuevaProtectora.setEmail(emailCuenta);
                nuevaProtectora.setTelefono(telefonoProtectora);
                nuevaProtectora.setProvincia(provinciaProtectora);
                nuevaProtectora.setCiudad(ciudadProtectora);
                nuevaProtectora.setCalle(calleProtectora);
                nuevaProtectora.setCodigoPostal(cpProtectora);
                nuevaProtectora.setIdUsuario(idUsuarioCreado);

                int idProtectoraCreada = protectoraDAO.crearProtectora(nuevaProtectora);

                if (idProtectoraCreada != -1 && idProtectoraCreada > 0) {
                    UtilidadesVentana.mostrarAlertaInformacion("Registro Exitoso",
                            "¡La protectora '" + nombreProtectora + "' ha sido registrada correctamente!");
                    navegarAlLogin();
                } else {
                    UtilidadesVentana.mostrarAlertaError("Error de Registro", "No se pudo crear el perfil de la protectora. Por favor, inténtelo de nuevo o contacte a soporte.");
                }
            } else {
                UtilidadesVentana.mostrarAlertaError("Error de Registro", "No se pudo crear la cuenta de usuario. Por favor, inténtelo de nuevo.");
            }

        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("unique constraint") && e.getMessage().toLowerCase().contains("cif")) {
                UtilidadesVentana.mostrarAlertaError("CIF Duplicado", "El CIF '" + cif + "' ya está registrado.");
                TxtCIF.requestFocus();
            } else if (e.getMessage().toLowerCase().contains("unique constraint") && e.getMessage().toLowerCase().contains("email")) {
                UtilidadesVentana.mostrarAlertaError("Email Duplicado", "El email '" + emailCuenta + "' ya está registrado.");
                TxtCorreoCuenta.requestFocus();
            } else {
                UtilidadesExcepciones.mostrarError(e, "Error de Base de Datos",
                        "Ocurrió un error al intentar registrar la protectora: " + e.getMessage());
            }
            e.printStackTrace();
        } catch (Exception e) {
            UtilidadesExcepciones.mostrarError(e, "Error Inesperado", "Ocurrió un error general durante el proceso de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida los datos de entrada del formulario de registro de protectora.
     * Muestra alertas de error si la validación falla.
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private boolean validarEntradas(String nombreProtectora, String cif, String calle, String provincia, String cp, String ciudad, String telefono, String nombreUsuario, String email, String contrasena, String confirmarContrasena) {

        if (nombreProtectora.isEmpty() || cif.isEmpty() || calle.isEmpty() || provincia.isEmpty() ||
                cp.isEmpty() || ciudad.isEmpty() || telefono.isEmpty() || nombreUsuario.isEmpty() ||
                email.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campos Incompletos",
                    "Todos los campos son obligatorios. Por favor, complete toda la información.");
            return false;
        }

        if (!cif.matches("^[A-HJNP-SUVW][0-9]{7}[0-9A-J]$")) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El formato del CIF no es válido.");
            TxtCIF.requestFocus();
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El formato del correo electrónico no es válido.");
            TxtCorreoCuenta.requestFocus();
            return false;
        }

        if (!cp.matches("\\d{5}")) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El Código Postal debe tener 5 dígitos.");
            TxtCPProtectora.requestFocus();
            return false;
        }

        if (!telefono.matches("\\d{9}")) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El Teléfono debe tener 9 dígitos.");
            TxtTelProtectora.requestFocus();
            return false;
        }

        if (contrasena.length() < 6) {
            UtilidadesVentana.mostrarAlertaError("Contraseña Débil", "La contraseña debe tener al menos 6 caracteres.");
            TxtContraCuenta.requestFocus();
            return false;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            UtilidadesVentana.mostrarAlertaError("Error de Contraseña", "Las contraseñas no coinciden.");
            TxtContraCuenta.clear();
            TxtConfirmarContraCuenta.clear();
            TxtContraCuenta.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Maneja el evento de clic en el icono de "Volver" o "Salir".
     * Navega a la pantalla de login.
     * @param event El evento de ratón que disparó este método (si el icono es un ImageView).
     * Podría ser ActionEvent si es un Button.
     */
    @FXML
    void Volver(MouseEvent event) {
        navegarAlLogin();
    }

    /**
     * Navega el usuario a la pantalla de login.
     */
    private void navegarAlLogin() {
        UtilidadesVentana.cambiarEscena(RUTA_FXML_LOGIN, TITULO_VENTANA_LOGIN, false);
    }
}