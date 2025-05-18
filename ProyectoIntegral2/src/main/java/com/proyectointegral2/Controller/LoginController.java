package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.utils.UtilidadesExcepciones;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import com.proyectointegral2.MainApp;

import java.net.URL;
import java.util.ResourceBundle;

import static java.sql.DriverManager.println;

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
    private TextField TxtContra;

    @FXML
    private TextField TxtNombreUsuario;

    @FXML
    private Hyperlink HyRegistrarse;

    @FXML
    private HBox HboxImg;

    @FXML
    void ConfirmarInicio(MouseEvent event) {
        String mainClienteFxmlFile = "/com/proyectointegral2/Vista/Main.fxml";
        String mainClienteTitle = "Panel Cliente - Dogpuccino";
        String nombreUsu = TxtNombreUsuario.getText();
        String contrasena = TxtContra.getText();

        if (nombreUsu.isEmpty() || contrasena.isEmpty()) {
            UtilidadesExcepciones.mostrarAdvertencia("Por favor, complete todos los campos.", "Campos vacíos", "Faltan datos");
            return;
        }

        try {
            UsuarioDao usuarioDao = new UsuarioDao();
            Usuario usuario = usuarioDao.verificarCredenciales(nombreUsu, contrasena);
            if (usuario != null) {
                println("Usuario verificado: " + usuario.getNombreUsuario());
                UtilidadesVentana.cambiarEscena(mainClienteFxmlFile, mainClienteTitle, true);
            } else {
                UtilidadesExcepciones.mostrarAdvertencia("El nombre de usuario o contraseña no son correctos.", "Error de inicio de sesión", "Fallo de credenciales");
                println("Error al iniciar sesión: Usuario o contraseña incorrectos.");
            }
        } catch (Exception e) {
            UtilidadesExcepciones.mostrarError(e, "Error de inicio de sesión", "Usuario o contraseña incorrectos.");
            println("Error al iniciar sesión: " + e.getMessage());
        }
    }

    @FXML
    void IrARegistro(ActionEvent event) {
        String choosingFxmlFile = "/com/proyectointegral2/Vista/InicioChoose.fxml";
        String choosingTitle = "Selección de Rol - Dogpuccino";
        UtilidadesVentana.cambiarEscena(choosingFxmlFile, choosingTitle, false);
    }
}
