package main.java.com.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import main.java.com.Main;

public class LoginController {

    @FXML
    private ImageView ImgUsuario;

    @FXML
    private ImageView ImgLateralLogin;

    @FXML
    private Button BtnConfirmar;

    @FXML
    private ImageView ImgIconoDog;

    @FXML
    private TextField TxtContra;

    @FXML
    private TextField TxtCorreo;

    @FXML
    private Hyperlink HyRegistrarse;

    @FXML
    private HBox HboxImg;



    @FXML
    void IrARegistro(ActionEvent event) {
        // Asume que RegistroView.fxml está en src/main/resources/com/proyecto_integral/Vista/
        String choosingFxmlFile = "/main/resources/com/proyecto_integral/Vista/inicio_choose.fxml";

        // Título para la ventana de Registro
        String choosingTitle = "Selecciona rol de Nuevo Usuario - Dogpuccino";

        // Llama al método estático para cambiar la escena
        if (Main.getPrimaryStage() != null) {
            Main.changeScene(choosingFxmlFile, choosingTitle);

            // Opcional: Si la ventana de choosing debe tener un tamaño fijo y no ser redimensionable
            // podrías querer re-aplicar esas configuraciones aquí, similar a como se hizo
            // para la ventana de login en la clase Main.
            // Main.getPrimaryStage().setResizable(false);
            // Main.getPrimaryStage().setWidth(650); // Ancho de tu RegistroView.fxml
            // Main.getPrimaryStage().setHeight(487); // Alto de tu RegistroView.fxml
            // etc.
        } else {
            System.err.println("Error en LoginController: PrimaryStage en Main no está inicializado. No se puede cambiar a la escena de registro.");
        }
    }

}
