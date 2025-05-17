package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;

import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class DetallesPerroController {

    @FXML private ImageView imgLogoPequeno;
    @FXML private Text TxtSexo;
    @FXML private Text TxtNombre;
    @FXML private Text TxtEdad;
    @FXML private Text TxtProtectora;
    @FXML private Button BtnReservarCita;
    @FXML private ImageView imgPerro; // ImageView principal para la foto del perro
    @FXML private Text TxtRaza;
    @FXML private Text TxtPatologia;

    private Perro perroActual;
    // Descomenta y usa cuando tengas los DAOs
    // private ProtectoraDao protectoraDao;
    // private IdentificacionPatologiasDao identificacionPatologiasDao;
    // private PatologiaDao patologiaDao;

    private final String RUTA_IMAGEN_PLACEHOLDER_DETALLES = "/assets/Imagenes/iconos/placeholder_dog_grande.png";

    @FXML
    public void initialize() {
        // try {
        //     protectoraDao = new ProtectoraDao();
        //     // Inicializa otros DAOs aquí
        // } catch (Exception e) {
        //     e.printStackTrace(); // Manejar error
        // }
        System.out.println("DetallesPerroController inicializado.");
    }

    public void initData(Perro perro) {
        this.perroActual = perro;
        if (perroActual == null) {
            TxtNombre.setText("Error: Perro no encontrado");
            // Ocultar otros campos o mostrar mensaje de error
            return;
        }

        TxtNombre.setText(Objects.requireNonNullElse(perroActual.getNombre(), "Nombre no disponible"));
        TxtSexo.setText(Objects.requireNonNullElse(perroActual.getSexo(), "No especificado"));

        if (perroActual.getFechaNacimiento() != null) {
            Period periodo = Period.between(perroActual.getFechaNacimiento(), LocalDate.now());
            String edadStr;
            if (periodo.getYears() > 0) {
                edadStr = periodo.getYears() + (periodo.getYears() == 1 ? " año" : " años");
                if (periodo.getMonths() > 0 && periodo.getYears() < 2) { // Añadir meses si es menos de 2 años
                    edadStr += " y " + periodo.getMonths() + (periodo.getMonths() == 1 ? " mes" : " meses");
                }
            } else {
                edadStr = periodo.getMonths() + (periodo.getMonths() == 1 ? " mes" : " meses");
            }
            TxtEdad.setText(edadStr);
        } else {
            TxtEdad.setText("Desconocida");
        }

        if (perroActual.getRaza() != null && perroActual.getRaza().getNombre() != null) {
            TxtRaza.setText(perroActual.getRaza().getNombre());
        } else {
            TxtRaza.setText("Raza no especificada");
        }

        // Cargar imagen principal del perro
        String imagePath = perroActual.getFoto();
        try {
            Image loadedImage = null;
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                if (!imagePath.startsWith("/")) { imagePath = "/" + imagePath; }
                InputStream stream = getClass().getResourceAsStream(imagePath);
                if (stream != null) {
                    loadedImage = new Image(stream);
                    stream.close();
                }
            }
            if (loadedImage == null || loadedImage.isError()) {
                try(InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_DETALLES)){
                    if(placeholderStream != null) loadedImage = new Image(placeholderStream);
                }
            }
            imgPerro.setImage(loadedImage);
        } catch (Exception e) {
            System.err.println("Error cargando imagen de detalle para " + perroActual.getNombre() + ": " + e.getMessage());
            try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_DETALLES)){
                if(placeholderStream != null) imgPerro.setImage(new Image(placeholderStream));
            } catch (Exception ex){}
        }


        // --- SIMULACIÓN NOMBRE PROTECTORA ---
        // Cuando tengas el DAO, reemplazarás esto
        if (perroActual.getIdProtectora() == 1) {
            TxtProtectora.setText("Amigos Peludos");
        } else if (perroActual.getIdProtectora() == 2) {
            TxtProtectora.setText("Huellas Felices");
        } else if (perroActual.getIdProtectora() > 0) {
            TxtProtectora.setText("Protectora ID: " + perroActual.getIdProtectora());
        } else {
            TxtProtectora.setText("No especificada");
        }
        // --- FIN SIMULACIÓN ---

        /* // DAO para Nombre Protectora:
        if (protectoraDao != null && perroActual.getIdProtectora() > 0) {
            try {
                Protectora p = protectoraDao.obtenerProtectoraPorId(perroActual.getIdProtectora());
                TxtProtectora.setText(p != null ? p.getNombre() : "Desconocida");
            } catch (SQLException e) { TxtProtectora.setText("Error DB"); }
        } else { TxtProtectora.setText("No especificada"); }
        */


        // --- SIMULACIÓN PATOLOGÍAS ---
        String patologiasTexto = "Ninguna conocida (Simulado)";
        if (perroActual.getId() == 101) { // Buddy
            patologiasTexto = "Alergia al Pollo (Simulado)";
        } else if (perroActual.getId() == 102) { // Kira
            patologiasTexto = "Leve displasia (Simulado)";
        }
        TxtPatologia.setText(patologiasTexto);
        // --- FIN SIMULACIÓN ---

        /* // DAO para Patologías:
        if (identificacionPatologiasDao != null && patologiaDao != null) {
            try {
                List<Patologia> patologiasDelPerro = identificacionPatologiasDao.obtenerPatologiasPorPerroId(perroActual.getId());
                if (patologiasDelPerro != null && !patologiasDelPerro.isEmpty()) {
                    TxtPatologia.setText(
                        patologiasDelPerro.stream().map(Patologia::getNombre).collect(Collectors.joining(", "))
                    );
                } else { TxtPatologia.setText("Ninguna conocida"); }
            } catch (SQLException e) { TxtPatologia.setText("Error DB Patol.");}
        } else { TxtPatologia.setText("Servicio Patol. no disp."); }
        */


        if (perroActual.isAdoptado()) {
            BtnReservarCita.setText("Adoptado");
            BtnReservarCita.setDisable(true);
            BtnReservarCita.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: #7f8c8d; -fx-font-weight: normal;");
        } else {
            BtnReservarCita.setText("Reservar cita");
            BtnReservarCita.setDisable(false);
            BtnReservarCita.setStyle("-fx-background-color: #D2691E; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }

    @FXML
    void ReservarCita(ActionEvent event) {
        if (perroActual == null || perroActual.isAdoptado()) {
            UtilidadesVentana.mostrarAlertaInformacion("No Disponible", "Este perrito no está disponible para reservar citas.");
            return;
        }
        System.out.println("Botón Reservar Cita presionado para: " + perroActual.getNombre());
        UtilidadesVentana.mostrarAlertaInformacion("Próximamente", "La funcionalidad de reservar cita aún no está implementada.");
        // Aquí navegarías al formulario de reserva de cita, pasando el ID del perro.
    }

    // Opcional: Si tienes un botón "Volver" o "Cerrar" en DetallesPerro.fxml
    @FXML
    void cerrarDetalles(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}