package com.proyectointegral2.Controller;

import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;

import java.io.IOException;

public class RegistroClienteController {
    @FXML
    private ImageView ImgLateralLogin;

    @FXML
    private TextField TxtTel;

    @FXML
    private TextField TxtNombre;

    @FXML
    private TextField TxtProvincia;

    @FXML
    private ImageView ImgIconoDog;

    @FXML
    private PasswordField TxtContra;

    @FXML
    private TextField TxtCP;

    @FXML
    private TextField TxtDireccion;

    @FXML
    private TextField TxtCiudad;

    @FXML
    private ImageView ImgUsuario;

    @FXML
    private TextField TxtApellido;

    @FXML
    private Button BtnConfirmar;

    @FXML
    private ImageView ImgIconoSalida;

    @FXML
    private TextField TxtCorreo;

    @FXML
    private TextField TxtNombreUsuario;

    @FXML
    private TextField TxtNIF;

    @FXML
    private DatePicker DpFechaNacimiento;

    @FXML
    private PasswordField TxtConfirmarContra;

    @FXML
    void ConfirmarRegistro(ActionEvent event) {
        String mainClienteFxmlFile = "/com/proyectointegral2/Vista/Main.fxml";
        String mainClienteTitle = "Panel Cliente - Dogpuccino";
        UtilidadesVentana.cambiarEscena(mainClienteFxmlFile, mainClienteTitle, true);
    }
//    @FXML
//    void ConfirmarRegistro(ActionEvent event) {
//        // Cuando implementes esto, necesitarás obtener los valores de los nuevos campos:
//        String nombreUsuario = TxtNombreUsuario.getText();
//        String nif = TxtNIF.getText();
//        java.time.LocalDate fechaNacimiento = DpFechaNacimiento.getValue();
//        String confirmarContra = TxtConfirmarContra.getText();
//
//        // ... tu lógica de validación y guardado ...
//        if (fechaNacimiento != null) {
//            System.out.println("Fecha de Nacimiento seleccionada: " + fechaNacimiento.toString());
//        }
//    }


    @Deprecated
    void fafafa(ActionEvent event) {

    }
    @FXML
    private void Volver(MouseEvent event) {
        String loginFxml = "/com/proyectointegral2/Vista/Login.fxml";
        String loginTitle = "Inicio de Sesión - Dogpuccino";
        UtilidadesVentana.cambiarEscena(loginFxml, loginTitle, false);

    }

}
