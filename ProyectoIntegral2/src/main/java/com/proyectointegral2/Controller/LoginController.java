package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Cliente; // Importar Cliente
import com.proyectointegral2.Model.Protectora; // Importar Protectora
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ClienteDao; // Importar ClienteDao
import com.proyectointegral2.dao.ProtectoraDao; // Importar ProtectoraDao
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.Model.SesionUsuario;
import com.proyectointegral2.utils.UtilidadesExcepciones;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;

public class LoginController {

    @FXML private ImageView ImgUsuario;
    @FXML private ImageView ImgLateralLogin;
    @FXML private Button BtnConfirmar;
    @FXML private ImageView ImgIconoDog;
    @FXML private PasswordField TxtContra;
    @FXML private TextField TxtNombreUsuario;
    @FXML private Hyperlink HyRegistrarse;

    private UsuarioDao usuarioDao;
    private ClienteDao clienteDao;         // Añadir instancia
    private ProtectoraDao protectoraDao;   // Añadir instancia

    @FXML
    public void initialize() {
        this.usuarioDao = new UsuarioDao();
        this.clienteDao = new ClienteDao();         // Inicializar
        this.protectoraDao = new ProtectoraDao();   // Inicializar
    }

    @FXML
    void ConfirmarInicio(MouseEvent event) { // O ActionEvent
        iniciarSesion();
    }

    private void iniciarSesion() {
        String nombreUsu = TxtNombreUsuario.getText().trim();
        String contrasena = TxtContra.getText();

        if (nombreUsu.isEmpty() || contrasena.isEmpty()) {
            UtilidadesExcepciones.mostrarAdvertencia("Por favor, complete todos los campos.", "Campos vacíos", "Faltan datos");
            return;
        }

        try {
            Usuario usuario = usuarioDao.verificacionUsuario(nombreUsu, contrasena);

            if (usuario != null) {
                int entidadIdEspecifica = 0;

                if ("CLIENTE".equalsIgnoreCase(usuario.getRol())) {
                    Cliente cliente = clienteDao.obtenerClientePorIdUsuario(usuario.getIdUsuario());
                    if (cliente != null) {
                        entidadIdEspecifica = cliente.getIdCliente();
                    }
                } else if ("PROTECTORA".equalsIgnoreCase(usuario.getRol())) {
                    Protectora protectora = protectoraDao.obtenerProtectoraPorIdUsuario(usuario.getIdUsuario());
                    if (protectora != null) {
                        entidadIdEspecifica = protectora.getIdProtectora();
                    }
                }

                if (entidadIdEspecifica == 0 && ("CLIENTE".equalsIgnoreCase(usuario.getRol()) || "PROTECTORA".equalsIgnoreCase(usuario.getRol()))) {
                    UtilidadesExcepciones.mostrarAdvertencia("Error de configuración de cuenta.", "Error de datos", "No se pudo encontrar el perfil asociado al usuario (Cliente/Protectora). Verifique que los datos de Cliente/Protectora estén correctamente creados y vinculados al ID de Usuario " + usuario.getIdUsuario() + ".");
                    System.err.println("Error: No se encontró ID de cliente/protectora para el usuario ID: " + usuario.getIdUsuario() + " con rol: " + usuario.getRol());
                    return;
                }

                SesionUsuario.iniciarSesion(usuario, entidadIdEspecifica);
                System.out.println("Usuario verificado: " + usuario.getNombreUsu() + ", Rol: " + usuario.getRol() + ", EntidadID (Cliente/Protectora): " + entidadIdEspecifica);

                String vistaARedirigir;
                String tituloVista;

                if ("CLIENTE".equalsIgnoreCase(usuario.getRol())) {
                    vistaARedirigir = "/com/proyectointegral2/Vista/Main.fxml";
                    tituloVista = "Panel Cliente - Dogpuccino";
                } else if ("PROTECTORA".equalsIgnoreCase(usuario.getRol())) {
                    vistaARedirigir = "/com/proyectointegral2/Vista/MainProtectora.fxml";
                    tituloVista = "Panel Protectora - Dogpuccino";
                } else {
                    UtilidadesExcepciones.mostrarAdvertencia("Rol de usuario no reconocido.", "Error de rol", "El rol '" + usuario.getRol() + "' no es válido.");
                    SesionUsuario.cerrarSesion();
                    return;
                }
                UtilidadesVentana.cambiarEscena(vistaARedirigir, tituloVista, true);

            } else {
                UtilidadesExcepciones.mostrarAdvertencia("El nombre de usuario o contraseña no son correctos.", "Error de inicio de sesión", "Fallo de credenciales");
                System.out.println("Error al iniciar sesión: Usuario o contraseña incorrectos.");
            }
        } catch (SQLException e) {
            UtilidadesExcepciones.mostrarError(e, "Error de Base de Datos", "Ocurrió un problema al intentar iniciar sesión.");
            System.err.println("Error de SQL al iniciar sesión: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) { // Captura más genérica por si acaso
            UtilidadesExcepciones.mostrarError(e, "Error Inesperado", "Ocurrió un error inesperado durante el inicio de sesión.");
            System.err.println("Error general al iniciar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void IrARegistro(ActionEvent event) {
        String choosingFxmlFile = "/com/proyectointegral2/Vista/InicioChoose.fxml";
        String choosingTitle = "Registro - Dogpuccino";
        UtilidadesVentana.cambiarEscena(choosingFxmlFile, choosingTitle, false);
    }
}