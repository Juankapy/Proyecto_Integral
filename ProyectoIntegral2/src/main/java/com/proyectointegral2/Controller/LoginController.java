package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.UsuarioDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {
    @FXML
    private Button BtnConfirmar;

    @FXML
    private TextField TxtContra;

    @FXML
    private TextField TxtCorreo;

    @FXML
    private Hyperlink HlnkRegistrarse;

    @FXML
    private void onLogin(ActionEvent event) {
        String correo = TxtCorreo.getText();
        String contrasena = TxtContra.getText();

        Usuario usuario = UsuarioDao.buscarPorCorreoYContrasena(correo, contrasena);
        if (usuario != null) {
            // Login exitoso
        } else {
            mostrarAlerta("Error", "Correo o contraseña incorrectos.");
        }
    }


    @FXML
    public void PulsarRegistrarse(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectointegral2/Vista/inicio_choose.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Tipo de Usuario");
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la ventana de registro.");
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
