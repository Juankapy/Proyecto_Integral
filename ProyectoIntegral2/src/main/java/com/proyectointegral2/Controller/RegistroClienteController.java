package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Direccion;
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.utils.UtilidadesExcepciones;
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
<<<<<<< HEAD
        try {
            String nombreUsu = TxtCorreo.getText();
            String contrasena = TxtContra.getText();
=======
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
>>>>>>> f8a27048f7bf2f1a2c0f24b670e5966714aaaff4

            // 1. Insertar usuario
            boolean usuarioInsertado = UsuarioDao.insertarUsuario(nombreUsu, contrasena);

            if (!usuarioInsertado) {
                UtilidadesExcepciones.mostrarAdvertencia("No se pudo registrar el usuario.", "Error de registro", "El usuario ya existe o hubo un problema con la base de datos.");
                return;
            }

            // 2. Recuperar el ID del usuario
            int idUsuario = UsuarioDao.obtenerIdUsuario(nombreUsu);

            // 3. Crear el objeto Cliente y Dirección
            Direccion direccion = new Direccion();
            direccion.setCalle(TxtDireccion.getText());
            direccion.setProvincia(TxtProvincia.getText());
            direccion.setCiudad(TxtCiudad.getText());
            direccion.setCodigoPostal(TxtCP.getText());

            Cliente cliente = new Cliente();
            cliente.setNombre(TxtNombre.getText());
            cliente.setApellidos(TxtApellido.getText());
            cliente.setTelefono(TxtTel.getText());
            cliente.setEmail(TxtCorreo.getText());
            cliente.setNif("");
            cliente.setIdUsuario(idUsuario);
            cliente.setDireccion(direccion);
            // cliente.setFechaNacimiento(...); // Si tienes el campo

            // 4. Insertar cliente
            boolean clienteInsertado = ClienteDao.insertarCliente(cliente);

            if (clienteInsertado) {
                String mainClienteFxmlFile = "/com/proyectointegral2/Vista/Main.fxml";
                String mainClienteTitle = "Panel Cliente - Dogpuccino";
                UtilidadesVentana.cambiarEscena(mainClienteFxmlFile, mainClienteTitle, true);
            } else {
                UtilidadesExcepciones.mostrarAdvertencia("No se pudo registrar el cliente.", "Error de registro", "Hubo un problema al guardar los datos del cliente."
                );
            }
        } catch (Exception e) {
            UtilidadesExcepciones.mostrarError(e,"Error de registro","Excepción no controlada");
            e.printStackTrace();
        }
    }

    @FXML
    private void Volver(MouseEvent event) {
        String loginFxml = "/com/proyectointegral2/Vista/Login.fxml";
        String loginTitle = "Inicio de Sesión - Dogpuccino";
        UtilidadesVentana.cambiarEscena(loginFxml, loginTitle, false);

    }

}
