package com.proyectointegral2.Controller;

import com.proyectointegral2.dao.ClienteDao; // Asumiendo que existe y está en este paquete
import com.proyectointegral2.Model.Usuario;   // Asumiendo que existe y está en este paquete
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
// No necesitas ActionEvent si todos tus botones/iconos usan onMouseClicked
// import javafx.event.ActionEvent;


import java.io.IOException;
import java.io.InputStream;


public class PerfilUsuarioController {

    @FXML private ImageView imgIconoVolver;
    @FXML private ImageView imgIconoUsuarioGrande;
    @FXML private Label TxtNombre;
    @FXML private Label TxtEmail;
    @FXML private Label TxtTelefono;
    @FXML private Label TxtDireccion;
    @FXML private Button BtnEditarDatos;
    @FXML private ImageView imgFotoPerfil;
    @FXML private ListView<String> listViewHistorial;
    @FXML private ImageView imgLogoDogpuccino;

    private ClienteDao clienteDAO;
    private Usuario usuarioActual;
    private int idUsuarioLogueado;

    private final String RUTA_PLACEHOLDER_PERFIL = "/assets/Imagenes/iconos/sinusuario.jpg";

    public void initData(int idUsuario, String nombreUsuarioLogin) {
        this.idUsuarioLogueado = idUsuario;
        cargarDatosPerfil();
    }

    private void cargarDatosPerfil() {
    }

    private void cargarDatosEjemplo() {
        TxtNombre.setText("Usuario Ejemplo");
        TxtEmail.setText("ejemplo@dominio.com");
        TxtTelefono.setText("+00 000 000 000");
        TxtDireccion.setText("Dirección de Ejemplo, 123");
        cargarImagenPlaceholder();

        ObservableList<String> historialItems = FXCollections.observableArrayList(
                "Dato de ejemplo 1 para historial",
                "Dato de ejemplo 2 para historial"
        );
        listViewHistorial.setItems(historialItems);
    }

    private void cargarImagenPlaceholder() {
        try {
            InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_PERFIL);
            if (placeholderStream != null) {
                imgFotoPerfil.setImage(new Image(placeholderStream));
            } else {
                System.err.println("No se pudo cargar la imagen placeholder por defecto desde: " + RUTA_PLACEHOLDER_PERFIL);
                imgFotoPerfil.setImage(null);
                imgFotoPerfil.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 8;");
            }
        } catch (Exception e) {
            System.err.println("Error crítico cargando imagen placeholder: " + e.getMessage());
        }
    }

    @FXML
    void IrAFormularioUsuario(MouseEvent event) {
        System.out.println("Botón Editar Datos Personales presionado.");
        String formularioUsuarioFxml = "/com/proyectointegral2/Vista/FormularioUsuario.fxml";
        String titulo = "Editar Perfil de Usuario";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formularioUsuarioFxml));
            Parent root = loader.load();

            FormularioUsuarioController formularioController = loader.getController();
            if (usuarioActual != null) {
                formularioController.initDataParaEdicion(usuarioActual);
            } else if (idUsuarioLogueado > 0) {
                UtilidadesVentana.mostrarAlertaError("Error Edición", "No hay datos de usuario para editar.");
                return;
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Edición", "No se pudo identificar el usuario para editar.");
                return;
            }

            UtilidadesVentana.cambiarEscenaConRoot(root, titulo, false); // 'false' para tamaño fijo

        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir el formulario de edición.");
        }
    }

    @FXML
    void Volver(MouseEvent event) {
        System.out.println("Volviendo a la pantalla anterior...");
        UtilidadesVentana.volverAEscenaAnterior();
    }

}