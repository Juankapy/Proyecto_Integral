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
import java.util.ResourceBundle;

public class RegistroClienteController implements Initializable {

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

    private UsuarioDao usuarioDAO;
    private ClienteDao clienteDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usuarioDAO = new UsuarioDao();
        clienteDAO = new ClienteDao();
    }

    @FXML
    public void ConfirmarRegistro(ActionEvent event) {
        String nombre = TxtNombre.getText().trim();
        String apellidos = TxtApellido.getText().trim();
        String nombreUsuario = TxtNombreUsuario.getText().trim();
        String nif = TxtNIF.getText().trim();
        String calle = TxtDireccion.getText().trim();
        String provincia = TxtProvincia.getText().trim();
        String cp = TxtCP.getText().trim();
        String ciudad = TxtCiudad.getText().trim();
        String telefono = TxtTel.getText().trim();
        LocalDate fechaNacimientoLocal = DpFechaNacimiento.getValue();
        String email = TxtCorreo.getText().trim();
        String contrasena = TxtContra.getText();
        String confirmarContrasena = TxtConfirmarContra.getText();

        if (nombre.isEmpty() || apellidos.isEmpty() || nombreUsuario.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campos Incompletos", "Rellena los campos obligatorios.");
            return;
        }
        if (!contrasena.equals(confirmarContrasena)) {
            UtilidadesVentana.mostrarAlertaError("Error de Contraseña", "Las contraseñas no coinciden.");
            return;
        }

        try {
            if (usuarioDAO.obtenerUsuarioPorNombreUsuario(nombreUsuario) != null) {
                UtilidadesVentana.mostrarAlertaError("Usuario Existente", "El nombre de usuario '" + nombreUsuario + "' ya está en uso.");
                TxtNombreUsuario.requestFocus();
                return;
            }

            Usuario nuevoUsuarioParaLogin = new Usuario();
            nuevoUsuarioParaLogin.setNombreUsu(nombreUsuario);
            nuevoUsuarioParaLogin.setContrasena(contrasena);
            nuevoUsuarioParaLogin.setRol("CLIENTE");

            int idUsuarioCreado = usuarioDAO.crearUsuario(nuevoUsuarioParaLogin);

            if (idUsuarioCreado != -1) {
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setNif(nif);
                nuevoCliente.setNombre(nombre);
                nuevoCliente.setApellidos(apellidos);
                if (fechaNacimientoLocal != null) {
                    nuevoCliente.setFechaNacimiento(fechaNacimientoLocal);
                }
                nuevoCliente.setProvincia(provincia);
                nuevoCliente.setCiudad(ciudad);
                nuevoCliente.setCalle(calle);
                nuevoCliente.setCodigoPostal(cp);
                nuevoCliente.setTelefono(telefono);
                nuevoCliente.setEmail(email);
                nuevoCliente.setIdUsuario(idUsuarioCreado);

                int idClienteCreado = clienteDAO.crearCliente(nuevoCliente);

                if (idClienteCreado != -1) {
                    UtilidadesVentana.mostrarAlertaInformacion("Registro Exitoso", "¡Cliente registrado correctamente!");
                    Volver(null);
                } else {
                    UtilidadesVentana.mostrarAlertaError("Error de Registro", "No se pudo crear el perfil del cliente.");
                }
            } else {
                UtilidadesVentana.mostrarAlertaError("Error de Registro", "No se pudo crear la cuenta de usuario.");
            }

        } catch (SQLException e) {
            UtilidadesExcepciones.mostrarError(e, "Error de Base de Datos", "Ocurrió un error al registrar el cliente.");
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