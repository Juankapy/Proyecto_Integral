package com.proyectointegral2.Controller;


import com.proyectointegral2.Model.Direccion;
import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.utils.UtilidadesExcepciones;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.control.PasswordField;

import java.io.IOException;

public class RegistroProtectoraController {

    @FXML
    private TextField TxtNombreProtectora;
    @FXML
    private TextField TxtCIF;
    @FXML
    private TextField TxtDireccionProtectora;
    @FXML
    private TextField TxtProvinciaProtectora;
    @FXML
    private TextField TxtCPProtectora;
    @FXML
    private TextField TxtCiudadProtectora;
    @FXML
    private TextField TxtTelProtectora;
    @FXML
    private TextField TxtNombreUsuarioCuenta;
    @FXML
    private TextField TxtCorreoCuenta;
    @FXML
    private PasswordField TxtContraCuenta;
    @FXML
    private PasswordField TxtConfirmarContraCuenta;

    @FXML
    void ConfirmarRegistroProtectora(ActionEvent event) {
        try {
            String nombreUsu = TxtCorreo.getText();
            String contrasena = TxtContra.getText();

            // 1. Insertar usuario
            boolean usuarioInsertado = UsuarioDao.insertarUsuario(nombreUsu, contrasena);

            if (!usuarioInsertado) {
                UtilidadesExcepciones.mostrarAdvertencia(
                        "No se pudo registrar el usuario.",
                        "Error de registro",
                        "El usuario ya existe o hubo un problema con la base de datos."
                );
                return;
            }

            // 2. Recuperar el ID del usuario
            int idUsuario = UsuarioDao.obtenerIdUsuario(nombreUsu);

            // 3. Crear la dirección
            Direccion direccion = new Direccion();
            direccion.setCalle(TxtDireccion.getText());
            direccion.setProvincia(TxtProvincia.getText());
            direccion.setCiudad(TxtCiudad.getText());
            direccion.setCodigoPostal(TxtCP.getText());

            // 4. Crear la protectora
            Protectora protectora = new Protectora();
            protectora.setNombre(TxtNombre.getText());
            protectora.setCif(TxtCIF.getText());
            protectora.setTelefono(TxtTel.getText());
            protectora.setEmail(TxtCorreo.getText());
            protectora.setDireccion(direccion);
            protectora.setIdUsuario(idUsuario);
            // protectora.setRedesSociales(...); // Si tienes el campo

            // 5. Insertar protectora
            ProtectoraDao protectoraDao = new ProtectoraDao();
            try {
                protectoraDao.insertarProtectora(protectora);
                String mainProtectoraFxmlFile = "/com/proyectointegral2/Vista/Main.fxml";
                String mainProtectoraTitle = "Panel Protectora - Dogpuccino";
                UtilidadesVentana.cambiarEscena(mainProtectoraFxmlFile, mainProtectoraTitle, true);
            } catch (Exception e) {
                UtilidadesExcepciones.mostrarAdvertencia(
                        "No se pudo registrar la protectora.",
                        "Error de registro",
                        "Hubo un problema al guardar los datos de la protectora."
                );
            }
        } catch (Exception e) {
            UtilidadesExcepciones.mostrarError(
                    e,
                    "Error de registro",
                    "Excepción no controlada"
            );
            e.printStackTrace();
        }
    }

    @FXML
    void Volver(MouseEvent event) {
        String loginFxml = "/com/proyectointegral2/Vista/Login.fxml";
        String loginTitle = "Inicio de Sesión - Dogpuccino";
        UtilidadesVentana.cambiarEscena(loginFxml, loginTitle, false);

    }

}
