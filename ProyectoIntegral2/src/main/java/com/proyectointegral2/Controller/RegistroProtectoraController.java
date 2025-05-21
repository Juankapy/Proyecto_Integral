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

public class RegistroProtectoraController implements Initializable {

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


    private UsuarioDao usuarioDAO;
    private ProtectoraDao protectoraDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usuarioDAO = new UsuarioDao();
        protectoraDAO = new ProtectoraDao();
    }

    @FXML
    public void ConfirmarRegistroProtectora(ActionEvent event) {
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

        if (nombreProtectora.isEmpty() || cif.isEmpty() || nombreUsuarioLogin.isEmpty() || emailCuenta.isEmpty() || contrasenaLogin.isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campos Incompletos", "Rellena los campos obligatorios.");
            return;
        }
        if (!contrasenaLogin.equals(confirmarContrasena)) {
            UtilidadesVentana.mostrarAlertaError("Error de Contraseña", "Las contraseñas no coinciden.");
            return;
        }

        try {
            if (usuarioDAO.obtenerUsuarioPorNombreUsuario(nombreUsuarioLogin) != null) {
                UtilidadesVentana.mostrarAlertaError("Usuario Existente", "El nombre de usuario '" + nombreUsuarioLogin + "' ya está en uso.");
                TxtNombreUsuarioCuenta.requestFocus();
                return;
            }

            Usuario nuevaCuentaUsuario = new Usuario();
            nuevaCuentaUsuario.setNombreUsu(nombreUsuarioLogin);
            nuevaCuentaUsuario.setContrasena(contrasenaLogin);
            nuevaCuentaUsuario.setRol("PROTECTORA");

            int idUsuarioCreado = usuarioDAO.crearUsuario(nuevaCuentaUsuario);

            if (idUsuarioCreado != -1) {
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

                if (idProtectoraCreada != -1) {
                    UtilidadesVentana.mostrarAlertaInformacion("Registro Exitoso", "¡Protectora registrada correctamente!");
                    Volver(null);
                } else {
                    UtilidadesVentana.mostrarAlertaError("Error de Registro", "No se pudo crear el perfil de la protectora.");
                }
            } else {
                UtilidadesVentana.mostrarAlertaError("Error de Registro", "No se pudo crear la cuenta de usuario.");
            }

        } catch (SQLException e) {
            UtilidadesExcepciones.mostrarError(e, "Error de Base de Datos", "Ocurrió un error al registrar la protectora.");
            e.printStackTrace();
        } catch (Exception e) {
            UtilidadesExcepciones.mostrarError(e, "Error Inesperado", "Ocurrió un error general durante el registro.");
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