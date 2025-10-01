package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.SesionUsuario;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.utils.UtilidadesExcepciones;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controlador para la pantalla de inicio de sesión.
 * Gestiona la autenticación de usuarios y la redirección basada en su rol.
 */
public class LoginController {

    private static final String ROL_CLIENTE = "CLIENTE";
    private static final String ROL_PROTECTORA = "PROTECTORA";

    private static final String FXML_MAIN_CLIENTE = "/com/proyectointegral2/Vista/Main.fxml";
    private static final String TITLE_MAIN_CLIENTE = "Panel Cliente - Dogpuccino";
    private static final String FXML_MAIN_PROTECTORA = "/com/proyectointegral2/Vista/MainProtectora.fxml";
    private static final String TITLE_MAIN_PROTECTORA = "Panel Protectora - Dogpuccino";
    private static final String FXML_INICIO_CHOOSE = "/com/proyectointegral2/Vista/InicioChoose.fxml";
    private static final String TITLE_REGISTRO = "Registro - Dogpuccino";

    @FXML private ImageView ImgUsuario;
    @FXML private ImageView ImgLateralLogin;
    @FXML private Button BtnConfirmar;
    @FXML private ImageView ImgIconoDog;
    @FXML private PasswordField TxtContra;
    @FXML private TextField TxtNombreUsuario;
    @FXML private Hyperlink HyRegistrarse;

    private UsuarioDao usuarioDao;
    private ClienteDao clienteDao;
    private ProtectoraDao protectoraDao;

    /**
     * Inicializa el controlador después de que sus elementos FXML han sido inyectados.
     * Crea instancias de los DAOs necesarios.
     */
    @FXML
    public void initialize() {
        this.usuarioDao = new UsuarioDao();
        this.clienteDao = new ClienteDao();
        this.protectoraDao = new ProtectoraDao();
    }

    /**
     * Maneja el evento de clic en el botón de confirmar inicio de sesión.
     * Llama al método principal de inicio de sesión.
     * @param event El evento de ratón que disparó la acción.
     */
    @FXML
    void ConfirmarInicio(MouseEvent event) {
        procesarInicioSesion();
    }


    /**
     * Orquesta el proceso completo de inicio de sesión.
     * Valida entradas, autentica al usuario, gestiona la sesión y redirige.
     */
    private void procesarInicioSesion() {
        String nombreUsu = TxtNombreUsuario.getText().trim();
        String contrasena = TxtContra.getText();

        if (!validarCampos(nombreUsu, contrasena)) {
            return;
        }

        try {
            Usuario usuario = autenticarUsuario(nombreUsu, contrasena);
            if (usuario == null) {
                UtilidadesExcepciones.mostrarAdvertencia("El nombre de usuario o contraseña no son correctos.", "Error de inicio de sesión", "Fallo de credenciales");
                System.out.println("Error al iniciar sesión: Usuario o contraseña incorrectos.");
                return;
            }

            int entidadId = obtenerEntidadIdPorRol(usuario);
            if (entidadId == 0 && esRolConEntidadAsociada(usuario.getRol())) {
                String mensajeError = String.format(
                        "No se pudo encontrar el perfil asociado al usuario (%s/%s). Verifique que los datos estén correctamente creados y vinculados al ID de Usuario %d.",
                        ROL_CLIENTE, ROL_PROTECTORA, usuario.getIdUsuario()
                );
                UtilidadesExcepciones.mostrarAdvertencia("Error de configuración de cuenta.", "Error de datos", mensajeError);
                System.err.println("Error: No se encontró ID de cliente/protectora para el usuario ID: " + usuario.getIdUsuario() + " con rol: " + usuario.getRol());
                return;
            }

            SesionUsuario.iniciarSesion(usuario, entidadId);
            System.out.println("Usuario verificado: " + usuario.getNombreUsu() + ", Rol: " + usuario.getRol() + ", EntidadID (Cliente/Protectora): " + entidadId);

            redirigirSegunRol(usuario.getRol());

        } catch (SQLException e) {
            UtilidadesExcepciones.mostrarError(e, "Error de Base de Datos", "Ocurrió un problema al intentar iniciar sesión.");
            System.err.println("Error de SQL al iniciar sesión: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            UtilidadesExcepciones.mostrarError(e, "Error Inesperado", "Ocurrió un error inesperado durante el inicio de sesión.");
            System.err.println("Error general al iniciar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida que los campos de nombre de usuario y contraseña no estén vacíos.
     * Muestra una advertencia si alguno está vacío.
     * @param nombreUsu El nombre de usuario ingresado.
     * @param contrasena La contraseña ingresada.
     * @return true si los campos son válidos, false en caso contrario.
     */
    private boolean validarCampos(String nombreUsu, String contrasena) {
        if (nombreUsu.isEmpty() || contrasena.isEmpty()) {
            UtilidadesExcepciones.mostrarAdvertencia("Por favor, complete todos los campos.", "Campos vacíos", "Faltan datos");
            return false;
        }
        return true;
    }

    /**
     * Autentica al usuario contra la base de datos.
     * @param nombreUsu El nombre de usuario.
     * @param contrasena La contraseña.
     * @return El objeto Usuario si la autenticación es exitosa, null en caso contrario.
     * @throws SQLException Si ocurre un error durante la consulta a la base de datos.
     */
    private Usuario autenticarUsuario(String nombreUsu, String contrasena) throws SQLException {
        return usuarioDao.verificacionUsuario(nombreUsu, contrasena);
    }

    /**
     * Obtiene el ID de la entidad específica (Cliente o Protectora) asociado al usuario.
     * @param usuario El objeto Usuario autenticado.
     * @return El ID de la entidad (Cliente ID o Protectora ID), o 0 si no se encuentra o no aplica.
     * @throws SQLException Si ocurre un error durante la consulta a la base de datos.
     */
    private int obtenerEntidadIdPorRol(Usuario usuario) throws SQLException {
        String rol = usuario.getRol() != null ? usuario.getRol().trim() : null;
        if (rol == null) {
            System.err.println("ERROR LoginController: Rol de usuario es null en obtenerEntidadIdPorRol.");
            return 0;
        }

        System.out.println("DEBUG LoginController.obtenerEntidadIdPorRol: Procesando rol: '" + rol + "' para Usuario ID: " + usuario.getIdUsuario());

        if (ROL_CLIENTE.equalsIgnoreCase(rol)) {
            Cliente cliente = clienteDao.obtenerClientePorIdUsuario(usuario.getIdUsuario());
            if (cliente != null) {
                System.out.println("DEBUG LoginController.obtenerEntidadIdPorRol: ClienteDao devolvió cliente con ID_CLIENTE: " + cliente.getIdCliente());
                return cliente.getIdCliente();
            } else {
                System.err.println("ERROR LoginController.obtenerEntidadIdPorRol: clienteDao.obtenerClientePorIdUsuario(" + usuario.getIdUsuario() + ") devolvió NULL.");
                return 0;
            }
        } else if (ROL_PROTECTORA.equalsIgnoreCase(rol)) {
            Protectora protectora = protectoraDao.obtenerProtectoraPorIdUsuario(usuario.getIdUsuario());
            if (protectora != null) {
                System.out.println("DEBUG LoginController.obtenerEntidadIdPorRol: ProtectoraDao devolvió protectora con ID_PROTECTORA: " + protectora.getIdProtectora());
                return protectora.getIdProtectora();
            } else {
                System.err.println("ERROR LoginController.obtenerEntidadIdPorRol: protectoraDao.obtenerProtectoraPorIdUsuario(" + usuario.getIdUsuario() + ") devolvió NULL.");
                return 0;
            }
        }
        System.err.println("WARN LoginController.obtenerEntidadIdPorRol: Rol no reconocido: '" + rol + "'");
        return 0;
    }

    /**
     * Verifica si el rol del usuario es uno que debería tener una entidad (Cliente/Protectora) asociada.
     * @param rol El rol del usuario.
     * @return true si el rol es CLIENTE o PROTECTORA, false en caso contrario.
     */
    private boolean esRolConEntidadAsociada(String rol) {
        if (rol == null) return false;
        return ROL_CLIENTE.equalsIgnoreCase(rol) || ROL_PROTECTORA.equalsIgnoreCase(rol);
    }

    /**
     * Redirige al usuario a la vista principal correspondiente según su rol.
     * Muestra un error si el rol no es reconocido.
     * @param rol El rol del usuario.
     */
    private void redirigirSegunRol(String rol) {
        if (rol == null) {
            UtilidadesExcepciones.mostrarAdvertencia("Rol de usuario no definido.", "Error de rol", "El usuario no tiene un rol asignado.");
            SesionUsuario.cerrarSesion();
            return;
        }

        String fxmlFile;
        String tituloVista;
        boolean esDinamica = true;

        Usuario usuarioLogueado = SesionUsuario.getUsuarioLogueado();

        if (usuarioLogueado == null) {
            UtilidadesExcepciones.mostrarAdvertencia("Error de Sesión", "No se pudo recuperar la información del usuario logueado.","");
            return;
        }

        if (ROL_CLIENTE.equalsIgnoreCase(rol.trim())) {
            fxmlFile = FXML_MAIN_CLIENTE;
            tituloVista = TITLE_MAIN_CLIENTE;
            System.out.println("Redirigiendo a la vista de Cliente...");
        } else if (ROL_PROTECTORA.equalsIgnoreCase(rol.trim())) {
            fxmlFile = FXML_MAIN_PROTECTORA;
            tituloVista = TITLE_MAIN_PROTECTORA;
            System.out.println("Redirigiendo a la vista de Protectora...");
        } else {
            UtilidadesExcepciones.mostrarAdvertencia("Rol de usuario no reconocido.", "Error de rol", "El rol '" + rol + "' no es válido.");
            SesionUsuario.cerrarSesion();
            System.err.println("Rol no reconocido para redirección: '" + rol.trim() + "'");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof MainClienteController && ROL_CLIENTE.equalsIgnoreCase(rol.trim())) {
                System.out.println("DEBUG LoginController: MainClienteController cargado.");
            } else if (controller instanceof MainProtectoraController && ROL_PROTECTORA.equalsIgnoreCase(rol.trim())) {
                System.out.println("DEBUG LoginController: MainProtectoraController cargado. Su initialize() usará SesionUsuario.");
            }

            UtilidadesVentana.cambiarEscenaConRoot(root, tituloVista, esDinamica);

        } catch (IOException e) {
            UtilidadesExcepciones.mostrarError(e, "Error de Navegación", "No se pudo cargar la vista: " + fxmlFile);
            System.err.println("Error al cambiar de escena: " + e.getMessage());
            e.printStackTrace();
            SesionUsuario.cerrarSesion();
        }
    }

    /**
     * Maneja el evento de clic en el hyperlink "Registrarse".
     * Cambia la escena a la vista de elección de tipo de registro.
     * @param event El evento de acción que disparó la navegación.
     */
    @FXML
    void IrARegistro(ActionEvent event) {
        UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/InicioChoose.fxml", "Pantalla de Registro", false);
    }
}