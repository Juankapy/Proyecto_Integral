package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.utils.UtilidadesExcepciones;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private ImageView ImgUsuario;
    @FXML
    private ImageView ImgLateralLogin;
    @FXML
    private Button BtnConfirmar;
    @FXML
    private ImageView ImgIconoDog;
    @FXML
    private PasswordField TxtContra;
    @FXML
    private TextField TxtNombreUsuario;
    @FXML
    private Hyperlink HyRegistrarse;
    @FXML
    private HBox HboxImg;

    private UsuarioDao usuarioDao;

    private static Usuario usuarioLogueado;
    private static int entidadIdLogueada;

    public static Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public static int getEntidadIdLogueada() {
        return entidadIdLogueada;
    }

    public static void cerrarSesionEstatica() {
        usuarioLogueado = null;
        entidadIdLogueada = 0;
    }


    @FXML
    public void initialize() {
        this.usuarioDao = new UsuarioDao();
    }

    @FXML
    void ConfirmarInicio(MouseEvent event) {
        iniciarSesion();
    }

    private void iniciarSesion() {
        String nombreUsu = TxtNombreUsuario.getText();
        String contrasena = TxtContra.getText();

        if (nombreUsu.isEmpty() || contrasena.isEmpty()) {
            UtilidadesExcepciones.mostrarAdvertencia("Por favor, complete todos los campos.", "Campos vacíos", "Faltan datos");
            return;
        }

        try {
            Usuario usuario = usuarioDao.verificacionUsuario(nombreUsu, contrasena);

            if (usuario != null) {
                System.out.println("Usuario verificado: " + usuario.getNombreUsu() + ", Rol: " + usuario.getRol());

                int entidadIdEspecifica = usuarioDao.obtenerIdEntidadEspecifica(usuario.getIdUsuario(), usuario.getRol());

                if (entidadIdEspecifica == 0 && ("Cliente".equalsIgnoreCase(usuario.getRol()) || "Protectora".equalsIgnoreCase(usuario.getRol()))) {
                    UtilidadesExcepciones.mostrarAdvertencia("Error de configuración de cuenta.", "Error de datos", "No se pudo encontrar el perfil asociado al usuario (Cliente/Protectora).");
                    System.err.println("Error: No se encontró ID de cliente/protectora para el usuario ID: " + usuario.getIdUsuario() + " con rol: " + usuario.getRol());
                    return;
                }

                LoginController.usuarioLogueado = usuario;
                LoginController.entidadIdLogueada = entidadIdEspecifica;

                String vistaARedirigir;
                String tituloVista;
                Object controllerDestino = null;

                if ("Cliente".equalsIgnoreCase(usuario.getRol())) {
                    vistaARedirigir = "/com/proyectointegral2/Vista/Main.fxml";
                    tituloVista = "Panel Cliente - Dogpuccino";
                    System.out.println("Redirigiendo a la vista de Cliente...");
                } else if ("Protectora".equalsIgnoreCase(usuario.getRol())) {
                    vistaARedirigir = "/com/proyectointegral2/Vista/MainProtectora.fxml";
                    tituloVista = "Panel Protectora - Dogpuccino";
                    System.out.println("Redirigiendo a la vista de Protectora...");
                } else {
                    UtilidadesExcepciones.mostrarAdvertencia("Rol de usuario no reconocido.", "Error de rol", "El rol '" + usuario.getRol() + "' no es válido para acceder.");
                    System.err.println("Rol no reconocido para el usuario: " + usuario.getRol());
                    LoginController.cerrarSesionEstatica();
                    return;
                }

                cambiarEscenaConDatos(vistaARedirigir, tituloVista, usuario, entidadIdEspecifica);


            } else {
                UtilidadesExcepciones.mostrarAdvertencia("El nombre de usuario o contraseña no son correctos.", "Error de inicio de sesión", "Fallo de credenciales");
                System.out.println("Error al iniciar sesión: Usuario o contraseña incorrectos.");
            }
        } catch (SQLException e) {
            UtilidadesExcepciones.mostrarError(e, "Error de Base de Datos", "Ocurrió un problema al intentar iniciar sesión. Por favor, inténtelo más tarde.");
            System.err.println("Error de SQL al iniciar sesión: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            UtilidadesExcepciones.mostrarError(e, "Error Inesperado", "Ocurrió un error inesperado durante el inicio de sesión.");
            System.err.println("Error general al iniciar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cambiarEscenaConDatos(String fxmlFile, String title, Usuario usuario, int entidadId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof MainClienteController && "Cliente".equalsIgnoreCase(usuario.getRol())) {
                // ((MainClienteController) controller).initData(usuario, entidadId); // Asumiendo que MainClienteController tiene initData
                System.out.println("Datos pasados a MainClienteController (simulado)");
            } else if (controller instanceof MainProtectoraController && "Protectora".equalsIgnoreCase(usuario.getRol())) {
                // ((MainProtectoraController) controller).initData(usuario, entidadId); // Asumiendo que MainProtectoraController tiene initData
                System.out.println("Datos pasados a MainProtectoraController (simulado)");
            }


            Stage stage = (Stage) BtnConfirmar.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            UtilidadesExcepciones.mostrarError(e, "Error de Navegación", "No se pudo cargar la vista: " + fxmlFile);
            System.err.println("Error al cambiar de escena: " + e.getMessage());
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