package com.proyectointegral2.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import com.proyectointegral2.Model.Direccion;
import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.dao.ProtectoraDao;

public class RegistroProtectoraController {
    @FXML private ImageView ImgLateralLogin;
    @FXML private TextField TxtNombre;
    @FXML private TextField TxtApellido; // No se usa en Protectora, pero está en el FXML
    @FXML private TextField TxtDireccion;
    @FXML private TextField TxtProvincia;
    @FXML private TextField TxtCiudad;
    @FXML private TextField TxtCP;
    @FXML private TextField TxtTel;
    @FXML private TextField TxtCorreo;
    @FXML private TextField TxtContra; // No se usa en Protectora, pero está en el FXML
    @FXML private TextField TxtCIF;
    @FXML private Button BtnConfirmar;

    @FXML
    void ConfirmarRegistroProtectora(ActionEvent event) throws Exception {
        String nombre = TxtNombre.getText();
        String cif = TxtCIF.getText();
        String provincia = TxtProvincia.getText();
        String ciudad = TxtCiudad.getText();
        String calle = TxtDireccion.getText();
        String codigoPostal = TxtCP.getText();
        String telefono = TxtTel.getText();
        String email = TxtCorreo.getText();
        // Puedes añadir redes sociales si tienes un campo para ello
        String redesSociales = null;

        Direccion direccion = new Direccion(provincia, ciudad, calle, codigoPostal);
        Protectora protectora = new Protectora(0, cif, nombre, direccion, telefono, email, redesSociales);

        ProtectoraDao protectoraDao = new ProtectoraDao();
        protectoraDao.insertarProtectora(protectora);

        CerrarVentana();
    }

    @FXML
    public void CerrarVentana(javafx.scene.input.MouseEvent event) {
        // Cierra la ventana actual
        Stage stage = (Stage) ImgLateralLogin.getScene().getWindow();
        stage.close();

        // Carga la ventana anterior
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ruta/a/ventana_anterior.fxml"));
            javafx.scene.Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new javafx.scene.Scene(root));
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}