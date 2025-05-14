package com.proyectointegral2.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Direccion;

public class RegistroClienteController {
    @FXML private ImageView ImgLateralLogin;
    @FXML private TextField TxtTel;
    @FXML private TextField TxtNombre;
    @FXML private TextField TxtProvincia;
    @FXML private TextField TxtContra;
    @FXML private TextField TxtCP;
    @FXML private TextField TxtDireccion;
    @FXML private TextField TxtCiudad;
    @FXML private TextField TxtApellido;
    @FXML private Button BtnConfirmar;
    @FXML private TextField TxtCorreo;

    @FXML
    void ConfirmarRegistroCliente(ActionEvent event) throws Exception {
        String nombre = TxtNombre.getText();
        String apellido = TxtApellido.getText();
        String provincia = TxtProvincia.getText();
        String ciudad = TxtCiudad.getText();
        String calle = TxtDireccion.getText();
        String codigoPostal = TxtCP.getText();
        String telefono = TxtTel.getText();
        String email = TxtCorreo.getText();

        Direccion direccion = new Direccion(provincia, ciudad, calle, codigoPostal);
        // Se pasan los campos no disponibles como null
        Cliente cliente = new Cliente(nombre, apellido, null, null, direccion, telefono, email, null);

        ClienteDao clienteDAO = new ClienteDao();
        clienteDAO.insertarCliente(cliente);

        cerrarVentana();
    }

    @FXML
    void cerrarVentana() {
        Stage stage = (Stage) BtnConfirmar.getScene().getWindow();
        stage.close();
    }
}