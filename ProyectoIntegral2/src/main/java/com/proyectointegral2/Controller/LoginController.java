package com.proyectointegral2.Controller;

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
        String nombreUsu = TxtNombreUsuario.getText();
        String contrasena = TxtContra.getText();

        var usuario = com.proyectointegral2.dao.UsuarioDao.verificacionUsuario(nombreUsu, contrasena);

        if (usuario != null) {
            String mainClienteFxmlFile = "/com/proyectointegral2/Vista/Main.fxml";
            String mainClienteTitle = "Panel Cliente - Dogpuccino";
            UtilidadesVentana.cambiarEscena(mainClienteFxmlFile, mainClienteTitle, true);
        } else {
            UtilidadesVentana.mostrarAlertaError(
                    "Credenciales incorrectas",
                    "El correo o la contraseña son incorrectos. Intenta nuevamente."
            );
        }
    }

    @FXML
    void IrARegistro(ActionEvent event) {
        String choosingFxmlFile = "/com/proyectointegral2/Vista/InicioChoose.fxml";
        String choosingTitle = "Selección de Rol - Dogpuccino";
        UtilidadesVentana.cambiarEscena(choosingFxmlFile, choosingTitle, false);
    }
}
