package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Direccion;
import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

public class RegistroProtectoraController {

    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final ProtectoraDao protectoraDao = new ProtectoraDao();

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

    @FXML
    public void ConfirmarRegistroProtectora(ActionEvent event) {
        String nombre = TxtNombreProtectora.getText().trim();
        String cif = TxtCIF.getText().trim();
        String nombreUsuario = TxtNombreUsuarioCuenta.getText().trim();
        String calle = TxtDireccionProtectora.getText().trim();
        String provincia = TxtProvinciaProtectora.getText().trim();
        String cp = TxtCPProtectora.getText().trim();
        String ciudad = TxtCiudadProtectora.getText().trim();
        String telefono = TxtTelProtectora.getText().trim();
        String email = TxtCorreoCuenta.getText().trim();
        String contrasena = TxtContraCuenta.getText();
        String confirmarContrasena = TxtConfirmarContraCuenta.getText();

        // Validaciones (igual que antes)
        if (nombre.isEmpty() || cif.isEmpty() || nombreUsuario.isEmpty() ||
                calle.isEmpty() || provincia.isEmpty() || cp.isEmpty() || ciudad.isEmpty() ||
                telefono.isEmpty() || email.isEmpty() ||
                contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            System.out.println("Error: Campos incompletos");
            return;
        }
        if (!contrasena.equals(confirmarContrasena)) {
            System.out.println("Error: Contraseñas no coinciden");
            TxtContraCuenta.clear();
            TxtConfirmarContraCuenta.clear();
            TxtContraCuenta.requestFocus();
            return;
        }
        if (!cif.matches("^[A-Z]\\d{8}$")) {
            System.out.println("Error: CIF inválido.");
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
            if (usuarioDao.obtenerUsuarioPorNombreUsuario(nombreUsuario) != null) {
                System.out.println("Error: Nombre de usuario ya existe.");
                TxtNombreUsuarioCuenta.requestFocus();
                return;
            }

            Protectora protectoraExistentePorCIF = protectoraDao.obtenerProtectoraPorCIF(cif);
            if (protectoraExistentePorCIF != null) {
                System.out.println("Error: CIF ya registrado.");
                TxtCIF.requestFocus();
                return;
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nombreUsuario);
            nuevoUsuario.setContrasena(contrasena);

            int idUsuarioCreado = usuarioDao.crearUsuario(nuevoUsuario);

            if (idUsuarioCreado != -1) {
                Direccion direccion = new Direccion();
                direccion.setProvincia(provincia);
                direccion.setCiudad(ciudad);
                direccion.setCalle(calle);
                direccion.setCodigoPostal(cp);

                Protectora nuevaProtectora = new Protectora();
                nuevaProtectora.setNombre(nombre);
                nuevaProtectora.setCif(cif);
                nuevaProtectora.setDireccion(direccion);
                nuevaProtectora.setTelefono(telefono);
                nuevaProtectora.setEmail(email);
                nuevaProtectora.setIdUsuario(idUsuarioCreado);

                int idProtectoraCreada = protectoraDao.crearProtectora(nuevaProtectora);
                if (idProtectoraCreada != -1) {
                    System.out.println("Protectora registrada correctamente. ID Protectora: " + idProtectoraCreada);
                    Volver(null);
                } else {
                    System.out.println("Error: No se pudo crear el perfil de la protectora.");
                }
            } else {
                System.out.println("Error: No se pudo crear el usuario base.");
            }

        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL al registrar protectora: " + e.getMessage());
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