package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Direccion;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.dao.UsuarioDao;

import com.proyectointegral2.utils.UtilidadesExcepciones;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.io.IOException;

public class RegistroClienteController implements Initializable {

    @FXML
    private ImageView ImgLateralLogin;
    @FXML
    private ImageView ImgUsuario; // Icono, no interactivo
    @FXML
    private TextField TxtNombre;
    @FXML
    private TextField TxtApellido;
    @FXML
    private TextField TxtNombreUsuario;
    @FXML
    private TextField TxtNIF;
    @FXML
    private TextField TxtDireccion; // Se mapeará a "Calle"
    @FXML
    private TextField TxtProvincia;
    @FXML
    private TextField TxtCP;
    @FXML
    private TextField TxtCiudad;
    @FXML
    private TextField TxtTel;
    @FXML
    private DatePicker DpFechaNacimiento;
    @FXML
    private TextField TxtCorreo;
    @FXML
    private PasswordField TxtContra;
    @FXML
    private PasswordField TxtConfirmarContra;
    @FXML
    private ImageView ImgIconoDog; // Icono, no interactivo
    @FXML
    private Button BtnConfirmar;
    @FXML
    private ImageView ImgIconoSalida;

    // Instancias de los DAO
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

        // --- VALIDACIONES ---
        if (nombre.isEmpty() || apellidos.isEmpty() || nombreUsuario.isEmpty() || nif.isEmpty() ||
                calle.isEmpty() || provincia.isEmpty() || cp.isEmpty() || ciudad.isEmpty() ||
                telefono.isEmpty() || fechaNacimientoLocal == null || email.isEmpty() ||
                contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            System.out.println("Error: Campos incompletos");
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            System.out.println("Error: Contraseñas no coinciden");
            TxtContra.clear();
            TxtConfirmarContra.clear();
            TxtContra.requestFocus();
            return;
        }

        if (!nif.matches("(?i)^[XYZ\\d]?\\d{7}[A-Z]$")) {
            System.out.println("Error: NIF/NIE inválido.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            System.out.println("Error: Email inválido");
            return;
        }

        if (!telefono.matches("\\d{9}")) {
            System.out.println("Error: Teléfono inválido, debe tener 9 dígitos.");
            return;
        }

        if (!cp.matches("\\d{5}")) {
            System.out.println("Error: Código Postal inválido, debe tener 5 dígitos.");
            return;
        }

        try {
            if (usuarioDAO.obtenerUsuarioPorNombreUsuario(nombreUsuario) != null) {
                System.out.println("Error: Nombre de usuario ya existe.");
                TxtNombreUsuario.requestFocus();
                return;
            }

            Cliente clienteExistentePorNIF = clienteDAO.obtenerClientePorNIF(nif);
            if (clienteExistentePorNIF != null) {
                System.out.println("Error: NIF ya registrado.");
                TxtNIF.requestFocus();
                return;
            }

            // --- CREACIÓN DE USUARIO ---
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nombreUsuario);
            nuevoUsuario.setContrasena(contrasena); // TODO: Hashear en producción

            int idUsuarioCreado = usuarioDAO.crearUsuario(nuevoUsuario);

            if (idUsuarioCreado != -1) {
                // Crear la dirección
                Direccion direccion = new Direccion();
                direccion.setProvincia(provincia);
                direccion.setCiudad(ciudad);
                direccion.setCalle(calle);
                direccion.setCodigoPostal(cp);

                // Crear el cliente y asignar la dirección
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setNif(nif);
                nuevoCliente.setNombre(nombre);
                nuevoCliente.setApellidos(apellidos);
                nuevoCliente.setFechaNacimiento(Date.valueOf(fechaNacimientoLocal));
                nuevoCliente.setDireccion(direccion);
                nuevoCliente.setTelefono(telefono);
                nuevoCliente.setEmail(email);
                nuevoCliente.setIdUsuario(idUsuarioCreado);

                int idClienteCreado = clienteDAO.crearCliente(nuevoCliente);

                if (idClienteCreado != -1) {
                    System.out.println("Cliente registrado correctamente. ID Cliente: " + idClienteCreado);
                    Volver(null);
                } else {
                    System.out.println("Error: No se pudo crear el perfil del cliente.");
                }
            } else {
                System.out.println("Error: No se pudo crear el usuario base.");
            }

        } catch (SQLException e) {
            System.err.println("Error SQL al registrar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    void Volver(MouseEvent event) {
        Stage stage = (Stage) ImgIconoSalida.getScene().getWindow();
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectointegral2/View/Login.fxml")); // Ruta a tu FXML de login
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Iniciar Sesión");
            stage.show();
         } catch (IOException e) {
            e.printStackTrace();
            UtilidadesExcepciones.mostrarError(e,"Error de Navegación", "No se pudo cargar la ventana de inicio de sesión.");
         }
        System.out.println("Volver a la pantalla anterior/login..."); // Placeholder
        stage.close(); // Cierra la ventana actual
    }
}