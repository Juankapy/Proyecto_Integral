package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.dao.UsuarioDao;

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

    void ConfirmarRegistro(ActionEvent event) {
<<<<<<< Updated upstream
        String nombre = TxtNombre.getText().trim();
        String apellidos = TxtApellido.getText().trim();
        String nombreUsuario = TxtNombreUsuario.getText().trim();
        String nif = TxtNIF.getText().trim();
        String calle = TxtDireccion.getText().trim(); // Mapeado de TxtDireccion
        String provincia = TxtProvincia.getText().trim();
        String cp = TxtCP.getText().trim();
        String ciudad = TxtCiudad.getText().trim();
        String telefono = TxtTel.getText().trim();
        LocalDate fechaNacimientoLocal = DpFechaNacimiento.getValue();
        String email = TxtCorreo.getText().trim();
        String contrasena = TxtContra.getText(); // No trim(), las contraseñas pueden tener espacios
        String confirmarContrasena = TxtConfirmarContra.getText();

        // --- VALIDACIONES ---
        if (nombre.isEmpty() || apellidos.isEmpty() || nombreUsuario.isEmpty() || nif.isEmpty() ||
                calle.isEmpty() || provincia.isEmpty() || cp.isEmpty() || ciudad.isEmpty() ||
                telefono.isEmpty() || fechaNacimientoLocal == null || email.isEmpty() ||
                contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            // alertas.mostrarError("Campos incompletos", "Por favor, rellene todos los campos.");
            System.out.println("Error: Campos incompletos"); // Placeholder
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            // alertas.mostrarError("Contraseñas no coinciden", "Las contraseñas ingresadas no coinciden.");
            System.out.println("Error: Contraseñas no coinciden"); // Placeholder
            TxtContra.clear();
            TxtConfirmarContra.clear();
            TxtContra.requestFocus();
            return;
        }

        // Validación de NIF (ejemplo básico: 8 números y 1 letra, o letra y 7 números y letra para NIE)
        if (!nif.matches("(?i)^[XYZ\\d]?\\d{7}[A-Z]$")) { // Permite NIF y NIE básicos
            System.out.println("Error: NIF/NIE inválido."); // Placeholder
            // alertas.mostrarError("NIF inválido", "El formato del NIF/NIE no es válido.");
            return;
        }

        // Validación de email (ejemplo)
        // if (!validaciones.esEmailValido(email)) { ... }
        if (!email.contains("@") || !email.contains(".")) { // Validación muy básica
            System.out.println("Error: Email inválido"); // Placeholder
            // alertas.mostrarError("Email inválido", "El formato del correo electrónico no es válido.");
            return;
        }

        // Validación de teléfono (ejemplo, 9 dígitos numéricos)
        if (!telefono.matches("\\d{9}")) {
            System.out.println("Error: Teléfono inválido, debe tener 9 dígitos."); // Placeholder
            // alertas.mostrarError("Teléfono inválido", "El teléfono debe contener 9 dígitos.");
            return;
        }

        // Validación de CP (ejemplo, 5 dígitos numéricos)
        if (!cp.matches("\\d{5}")) {
            System.out.println("Error: Código Postal inválido, debe tener 5 dígitos."); // Placeholder
            // alertas.mostrarError("CP inválido", "El Código Postal debe contener 5 dígitos.");
            return;
        }


        // --- LÓGICA DE REGISTRO ---
        try {
            // 1. Verificar si el nombre de usuario o email ya existen
            if (usuarioDAO.obtenerUsuarioPorNombreUsuario(nombreUsuario) != null) {
                // alertas.mostrarError("Registro Fallido", "El nombre de usuario ya está en uso.");
                System.out.println("Error: Nombre de usuario ya existe."); // Placeholder
                TxtNombreUsuario.requestFocus();
=======
        // Cuando implementes esto, necesitarás obtener los valores de los nuevos campos:
        String nombreUsuario = TxtNombreUsuario.getText();
        String nif = TxtNIF.getText();
        java.time.LocalDate fechaNacimiento = DpFechaNacimiento.getValue();
        String confirmarContra = TxtConfirmarContra.getText();

        // ... tu lógica de validación y guardado ...
        if (fechaNacimiento != null) {
            System.out.println("Fecha de Nacimiento seleccionada: " + fechaNacimiento.toString());
        }
    }

            // 1. Insertar usuario
            boolean usuarioInsertado = UsuarioDao.insertarUsuario(nombreUsu, contrasena);

            if (!usuarioInsertado) {
                UtilidadesExcepciones.mostrarAdvertencia("No se pudo registrar el usuario.", "Error de registro", "El usuario ya existe o hubo un problema con la base de datos.");
>>>>>>> Stashed changes
                return;
            }

            Cliente clienteExistentePorNIF = clienteDAO.obtenerClientePorNIF(nif);
            if (clienteExistentePorNIF != null) {
                // alertas.mostrarError("Registro Fallido", "El NIF ingresado ya está registrado.");
                System.out.println("Error: NIF ya registrado."); // Placeholder
                TxtNIF.requestFocus();
                return;
            }
            // Podrías añadir una verificación si el email ya existe en la tabla Cliente
            // Cliente clienteExistentePorEmail = clienteDAO.obtenerClientePorEmail(email); // Necesitarías este método en ClienteDAO
            // if (clienteExistentePorEmail != null) { ... }


            // 2. Crear el Usuario base
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nombreUsuario);

            // ¡IMPORTANTE! Hashear la contraseña antes de guardarla
            // String contrasenaHasheada = miUtilidadDeHashing.hash(contrasena);
            // nuevoUsuario.setContrasena(contrasenaHasheada);
            nuevoUsuario.setContrasena(contrasena); // ¡Temporalmente sin hash!

            int idUsuarioCreado = usuarioDAO.crearUsuario(nuevoUsuario);

            if (idUsuarioCreado != -1) {
                // 3. Crear el perfil del Cliente
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setNif(nif);
                nuevoCliente.setNombre(nombre);
                nuevoCliente.setApellidos(apellidos);
                nuevoCliente.setFechaNacimiento(Date.valueOf(fechaNacimientoLocal)); // Convertir LocalDate a java.sql.Date
                nuevoCliente.setProvincia(provincia);
                nuevoCliente.setCiudad(ciudad);
                nuevoCliente.setCalle(calle); // Usamos el valor de TxtDireccion
                nuevoCliente.setCp(cp);
                nuevoCliente.setTelefono(telefono);
                nuevoCliente.setEmail(email);
                nuevoCliente.setIdUsuario(idUsuarioCreado);

                int idClienteCreado = clienteDAO.crearCliente(nuevoCliente);

                if (idClienteCreado != -1) {
                    // alertas.mostrarInformacion("Registro Exitoso", "Cliente registrado correctamente. Ahora puede iniciar sesión.");
                    System.out.println("Cliente registrado con ID: " + idClienteCreado + " y Usuario ID: " + idUsuarioCreado); // Placeholder

                    // Navegar a la pantalla de login o a la principal del cliente
                    // Stage stage = (Stage) BtnConfirmar.getScene().getWindow();
                    // navegacionVentanas.cargarVentanaLogin(stage); // Ejemplo
                    Volver(null); // Volver a la pantalla anterior (asumo que es login)
                } else {
                    // Error al crear cliente, considerar rollback del usuario
                    // usuarioDAO.eliminarUsuario(idUsuarioCreado); // ¡Cuidado con esto!
                    // alertas.mostrarError("Registro Fallido", "No se pudo crear el perfil del cliente.");
                    System.out.println("Error: No se pudo crear el perfil del cliente."); // Placeholder
                }
            } else {
                // alertas.mostrarError("Registro Fallido", "No se pudo crear el usuario base.");
                System.out.println("Error: No se pudo crear el usuario base."); // Placeholder
            }

        } catch (SQLException e) {
            // alertas.mostrarError("Error de Base de Datos", "Ocurrió un error al intentar registrar: " + e.getMessage());
            System.err.println("Error SQL al registrar cliente: " + e.getMessage()); // Placeholder
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
            // alertas.mostrarError("Error de Navegación", "No se pudo cargar la ventana de inicio de sesión.");
         }
        System.out.println("Volver a la pantalla anterior/login..."); // Placeholder
        stage.close(); // Cierra la ventana actual
    }
}