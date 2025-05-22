package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.utils.UtilidadesExcepciones;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Controlador para la pantalla de registro de un nuevo Cliente.
 * Gestiona la entrada de datos del formulario, valida la información,
 * y crea un nuevo usuario de tipo "Cliente" y su perfil de Cliente asociado
 * en la base de datos.
 */
public class RegistroClienteController implements Initializable {

    // --- Componentes FXML ---
    @FXML private TextField TxtNombre;
    @FXML private TextField TxtApellido;
    @FXML private TextField TxtNombreUsuario;
    @FXML private TextField TxtNIF;
    @FXML private TextField TxtDireccion;
    @FXML private TextField TxtProvincia;
    @FXML private TextField TxtCP;
    @FXML private TextField TxtCiudad;
    @FXML private TextField TxtTel;
    @FXML private DatePicker DpFechaNacimiento;
    @FXML private TextField TxtCorreo;
    @FXML private PasswordField TxtContra;
    @FXML private PasswordField TxtConfirmarContra;
    @FXML private ImageView ImgIconoSalida;
    @FXML private Button BtnConfirmar;

    // --- DAOs (Data Access Objects) ---
    private UsuarioDao usuarioDAO;
    private ClienteDao clienteDAO;

    // --- Constantes ---
    private static final String ROL_USUARIO_CLIENTE = "CLIENTE";
    private static final String RUTA_FXML_LOGIN = "/com/proyectointegral2/Vista/Login.fxml";
    private static final String TITULO_VENTANA_LOGIN = "Inicio de Sesión - Dogpuccino";

    // Patrones de validación
    private static final Pattern NIF_PATTERN = Pattern.compile("^[0-9]{8}[A-Z]$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CP_PATTERN = Pattern.compile("\\d{5}");
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("\\d{9}");

    /**
     * Método de inicialización llamado por JavaFX después de que los campos FXML han sido inyectados.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si desconocida.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            usuarioDAO = new UsuarioDao();
            clienteDAO = new ClienteDao();
        } catch (Exception e) {
            System.err.println("Error crítico al inicializar DAOs en RegistroClienteController: " + e.getMessage());
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sistema", "No se pudo inicializar el acceso a la base de datos. El registro no funcionará.");
            if (BtnConfirmar != null) BtnConfirmar.setDisable(true);
        }
    }

    /**
     * Maneja el evento de clic del botón "Confirmar Registro".
     * Recolecta los datos del formulario, los valida, y si son correctos,
     * procede a crear el usuario y el cliente en la base de datos.
     * @param event El evento de acción que disparó este método.
     */
    @FXML
    public void ConfirmarRegistro(ActionEvent event) {
        // 1. Recolectar datos de los campos del formulario.
        String nombre = TxtNombre.getText().trim();
        String apellidos = TxtApellido.getText().trim();
        String nombreUsuario = TxtNombreUsuario.getText().trim();
        String nif = TxtNIF.getText().trim().toUpperCase();
        String calle = TxtDireccion.getText().trim();
        String provincia = TxtProvincia.getText().trim();
        String cp = TxtCP.getText().trim();
        String ciudad = TxtCiudad.getText().trim();
        String telefono = TxtTel.getText().trim();
        LocalDate fechaNacimiento = (DpFechaNacimiento != null) ? DpFechaNacimiento.getValue() : null;
        String emailCliente = TxtCorreo.getText().trim();
        String contrasena = TxtContra.getText();
        String confirmarContrasena = TxtConfirmarContra.getText();

        // 2. Validar los datos recolectados.
        if (!validarEntradas(nombre, apellidos, nombreUsuario, nif, calle, provincia, cp, ciudad,
                telefono, fechaNacimiento, emailCliente, contrasena, confirmarContrasena)) {
            return;
        }

        // 3. Lógica de creación de usuario y cliente.
        try {
            // Verificar si el nombre de usuario ya existe.
            if (usuarioDAO.obtenerUsuarioPorNombreUsuario(nombreUsuario) != null) {
                UtilidadesVentana.mostrarAlertaError("Usuario Existente",
                        "El nombre de usuario '" + nombreUsuario + "' ya está en uso. Por favor, elija otro.");
                TxtNombreUsuario.requestFocus();
                return;
            }

            // Verificar si el NIF ya está registrado para otro cliente.
            if (clienteDAO.obtenerClientePorNIF(nif) != null) {
                UtilidadesVentana.mostrarAlertaError("NIF Existente", "El NIF '" + nif + "' ya está registrado para otro cliente.");
                TxtNIF.requestFocus();
                return;
            }

            // Verificar si el email ya está registrado para otro cliente.
            if (clienteDAO.obtenerClientePorEmail(emailCliente) != null) {
                UtilidadesVentana.mostrarAlertaError("Email Existente", "El email '" + emailCliente + "' ya está registrado para otro cliente.");
                TxtCorreo.requestFocus();
                return;
            }

            Usuario nuevoUsuarioParaLogin = new Usuario();
            nuevoUsuarioParaLogin.setNombreUsu(nombreUsuario);
            nuevoUsuarioParaLogin.setContrasena(contrasena);
            nuevoUsuarioParaLogin.setRol(ROL_USUARIO_CLIENTE);

            int idUsuarioCreado = usuarioDAO.crearUsuario(nuevoUsuarioParaLogin);

            if (idUsuarioCreado > 0) {

                Cliente nuevoCliente = new Cliente();

                nuevoCliente.setNif(nif);
                nuevoCliente.setNombre(nombre);
                nuevoCliente.setApellidos(apellidos);
                nuevoCliente.setFechaNacimiento(fechaNacimiento);
                nuevoCliente.setProvincia(provincia);
                nuevoCliente.setCiudad(ciudad);
                nuevoCliente.setCalle(calle);
                nuevoCliente.setCodigoPostal(cp);
                nuevoCliente.setTelefono(telefono);
                nuevoCliente.setEmail(emailCliente);
                nuevoCliente.setIdUsuario(idUsuarioCreado);

                int idClienteCreado = clienteDAO.crearCliente(nuevoCliente);

                if (idClienteCreado > 0) {
                    UtilidadesVentana.mostrarAlertaInformacion("Registro Exitoso",
                            "¡Te has registrado correctamente como cliente! Ahora puedes iniciar sesión.");
                    navegarAlLogin();
                } else {
                    UtilidadesVentana.mostrarAlertaError("Error de Registro (Cliente)",
                            "No se pudo crear el perfil del cliente en la base de datos. " +
                                    "Por favor, inténtelo de nuevo o contacte a soporte.");
                }
            } else {
                UtilidadesVentana.mostrarAlertaError("Error de Registro (Usuario)", "No se pudo crear la cuenta de usuario. " + "El nombre de usuario podría ya estar en uso o hubo un error interno.");
            }

        } catch (SQLException e) {
            String mensajeErrorBD = "Ocurrió un error al procesar el registro: " + e.getMessage();
            if (e.getMessage().toLowerCase().contains("unique constraint")) {
                mensajeErrorBD = "Error: Ya existe un registro con alguno de los datos únicos proporcionados (NIF, Email, Nombre de Usuario).";
            }
            UtilidadesExcepciones.mostrarError(e, "Error de Base de Datos", mensajeErrorBD);
            e.printStackTrace();
        } catch (Exception e) {
            UtilidadesExcepciones.mostrarError(e, "Error Inesperado",
                    "Ocurrió un error general durante el proceso de registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida los datos de entrada del formulario de registro de cliente.
     * Muestra alertas de error si la validación falla.
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private boolean validarEntradas(String nombre, String apellidos, String nombreUsuario, String nif,
                                    String calle, String provincia, String cp, String ciudad, String telefono,
                                    LocalDate fechaNacimiento, String email,
                                    String contrasena, String confirmarContrasena) {

        // Validar campos obligatorios básicos.
        if (nombre.isEmpty() || apellidos.isEmpty() || nombreUsuario.isEmpty() || email.isEmpty() ||
                nif.isEmpty() || calle.isEmpty() || provincia.isEmpty() || ciudad.isEmpty() || cp.isEmpty() ||
                telefono.isEmpty() || fechaNacimiento == null || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campos Incompletos", "Todos los campos son obligatorios. Por favor, complete toda la información.");
            return false;
        }

        // Validar NIF
        if (!NIF_PATTERN.matcher(nif).matches()) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El formato del NIF no es válido (ej: 12345678A).");
            TxtNIF.requestFocus();
            return false;
        }

        // Validar letra del NIF
        if (!validarLetraNIF(nif)) {
            UtilidadesVentana.mostrarAlertaError("NIF Inválido", "La letra del NIF no es correcta.");
            TxtNIF.requestFocus();
            return false;
        }

        // Validar Email
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El formato del correo electrónico no es válido.");
            TxtCorreo.requestFocus();
            return false;
        }

        // Validar Código Postal
        if (!CP_PATTERN.matcher(cp).matches()) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El Código Postal debe tener 5 dígitos.");
            TxtCP.requestFocus();
            return false;
        }

        // Validar Teléfono
        if (!TELEFONO_PATTERN.matcher(telefono).matches()) {
            UtilidadesVentana.mostrarAlertaError("Formato Inválido", "El Teléfono debe tener 9 dígitos.");
            TxtTel.requestFocus();
            return false;
        }

        // Validar Fecha de Nacimiento
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            UtilidadesVentana.mostrarAlertaError("Fecha Inválida", "La fecha de nacimiento no puede ser futura.");
            DpFechaNacimiento.requestFocus();
            return false;
        }

        // Validar Edad (mayor de 18 años)
        if (Period.between(fechaNacimiento, LocalDate.now()).getYears() < 18) {
             UtilidadesVentana.mostrarAlertaError("Edad Inválida", "Debes ser mayor de 18 años para registrarte.");
             DpFechaNacimiento.requestFocus();
             return false;
        }

        // Validar que la contraseña tenga al menos 6 caracteres
        if (contrasena.length() < 6) {
            UtilidadesVentana.mostrarAlertaError("Contraseña Débil", "La contraseña debe tener al menos 6 caracteres.");
            TxtContra.requestFocus();
            return false;
        }

        // Validar que las contraseñas coincidan
        if (!contrasena.equals(confirmarContrasena)) {
            UtilidadesVentana.mostrarAlertaError("Error de Contraseña", "Las contraseñas no coinciden.");
            TxtContra.clear();
            TxtConfirmarContra.clear();
            TxtContra.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Valida la letra del NIF según el número proporcionado.
     * @param nif El NIF a validar.
     * @return true si la letra es correcta, false en caso contrario.
     */
    private boolean validarLetraNIF(String nif) {
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        try {
            int numero = Integer.parseInt(nif.substring(0, 8));
            char letraCalculada = letras.charAt(numero % 23);
            char letraIntroducida = nif.charAt(8);
            return letraCalculada == letraIntroducida;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Maneja el evento de clic en el icono de "Volver" o "Salir".
     * Navega a la pantalla de login.
     * @param event El evento de ratón que disparó este método.
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