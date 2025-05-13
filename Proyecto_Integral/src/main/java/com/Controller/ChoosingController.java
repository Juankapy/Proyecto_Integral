package main.java.com.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import main.java.com.Main;

public class ChoosingController {

    @FXML
    private ImageView imgLogoPrincipal;

    @FXML
    private ImageView ImgCliente;

    @FXML
    private ImageView ImgProtectora;

    @FXML
    void SeleccionarCliente(MouseEvent event) {
        System.out.println("Rol Usuario seleccionado.");
        String registroUsuarioFxml = "/main/resources/com/proyecto_integral/Vista/registro.fxml"; // O como se llame tu FXML de registro de usuario
        String titulo = "Registro de Usuario";

        if (Main.getPrimaryStage() != null) {
            Main.changeScene(registroUsuarioFxml, titulo);
        } else {
            System.err.println("Error en SeleccionRolController: PrimaryStage no inicializado.");
        }
    }

    @FXML
    void SeleccionarProtectora(MouseEvent event) {
        System.out.println("Rol Protectora seleccionado.");
        String registroProtectoraFxml = "/main/resources/com/proyecto_integral/Vista/registro_protectora.fxml"; // O como se llame tu FXML de registro de protectora
        String titulo = "Registro de Protectora";

        if (Main.getPrimaryStage() != null) {
            Main.changeScene(registroProtectoraFxml, titulo);
        } else {
            System.err.println("Error en SeleccionRolController: PrimaryStage no inicializado.");
        }
    }

}

