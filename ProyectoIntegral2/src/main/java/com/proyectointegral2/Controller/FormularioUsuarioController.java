package com.proyectointegral2.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.dao.ClienteDao;

import java.io.File;
import java.sql.SQLException;

public class FormularioUsuarioController {

    @FXML private TextField TxtNombre;
    @FXML private TextField TxtApellidos;
    @FXML private TextField TxtEmail;
    @FXML private TextField TxtTelefono;
    @FXML private TextField TxtDireccion;
    @FXML private PasswordField TxtPassword;
    @FXML private PasswordField TxtConfirmPassword;
    @FXML private ImageView imgFotoPerfilEditable;
    @FXML private Button bBnGuardar;
    @FXML private Button BtnCancelar;

    private Usuario usuarioAEditar;
    private Cliente clienteAEditar;
    private String rutaFotoPerfil;

    public void setUsuarioAEditar(Usuario usuario) {
        this.usuarioAEditar = usuario;
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        if (usuarioAEditar != null) {
            TxtNombre.setText(usuarioAEditar.getNombreUsu());
            // Si tienes un modelo Cliente asociado:
            try {
                ClienteDao clienteDao = new ClienteDao();
                clienteAEditar = clienteDao.obtenerClientePorIdUsuario(usuarioAEditar.getIdUsuario());
                if (clienteAEditar != null) {
                    TxtApellidos.setText(clienteAEditar.getApellidos());
                    TxtEmail.setText(clienteAEditar.getEmail());
                    TxtTelefono.setText(clienteAEditar.getTelefono());
                    TxtDireccion.setText(clienteAEditar.getCalle());
                    rutaFotoPerfil = clienteAEditar.getRutaFotoPerfil();
                    if (rutaFotoPerfil != null && !rutaFotoPerfil.isEmpty()) {
                        imgFotoPerfilEditable.setImage(new Image("file:" + rutaFotoPerfil));
                    }
                }
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo cargar el cliente.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void CambiarFoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar foto de perfil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            rutaFotoPerfil = file.getAbsolutePath();
            imgFotoPerfilEditable.setImage(new Image("file:" + rutaFotoPerfil));
        }
    }

    @FXML
    private void Guardar() {
        if (!validarCampos()) return;

        try {
            // Actualizar usuario
            UsuarioDao usuarioDao = new UsuarioDao();
            usuarioAEditar.setNombreUsu(TxtNombre.getText());
            if (!TxtPassword.getText().isEmpty()) {
                usuarioAEditar.setContrasena(TxtPassword.getText());
            }
            usuarioDao.actualizarUsuario(usuarioAEditar);

            // Actualizar cliente
            if (clienteAEditar != null) {
                clienteAEditar.setApellidos(TxtApellidos.getText());
                clienteAEditar.setEmail(TxtEmail.getText());
                clienteAEditar.setTelefono(TxtTelefono.getText());
                clienteAEditar.setCalle(TxtDireccion.getText());
                clienteAEditar.setRutaFotoPerfil(rutaFotoPerfil);
                ClienteDao clienteDao = new ClienteDao();
                clienteDao.actualizarCliente(clienteAEditar);
            }

            mostrarAlerta("Éxito", "Datos actualizados correctamente.", Alert.AlertType.INFORMATION);
            cerrarVentana();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron guardar los cambios.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void Cancelar() {
        cerrarVentana();
    }

    @FXML
    private void Volver() {
        cerrarVentana();
    }

    private boolean validarCampos() {
        if (TxtNombre.getText().isEmpty() || TxtApellidos.getText().isEmpty() ||
                TxtEmail.getText().isEmpty() || TxtTelefono.getText().isEmpty() ||
                TxtDireccion.getText().isEmpty()) {
            mostrarAlerta("Campos obligatorios", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            return false;
        }
        if (!TxtPassword.getText().isEmpty() && !TxtPassword.getText().equals(TxtConfirmPassword.getText())) {
            mostrarAlerta("Contraseña", "Las contraseñas no coinciden.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) TxtNombre.getScene().getWindow();
        stage.close();
    }

    private Stage getStage() {
        return (Stage) TxtNombre.getScene().getWindow();
    }
}