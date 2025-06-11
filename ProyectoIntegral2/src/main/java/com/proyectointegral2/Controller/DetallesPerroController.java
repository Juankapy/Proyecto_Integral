package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.*;
import com.proyectointegral2.dao.IdentificacionPatologiaDao;
import com.proyectointegral2.dao.PatologiaDao;
import com.proyectointegral2.dao.PeticionAdopcionDao;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.utils.UtilidadesExcepciones;
import com.proyectointegral2.utils.UtilidadesVentana;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class DetallesPerroController {

    private static final String RUTA_IMAGEN_PLACEHOLDER_DETALLES = "/assets/Imagenes/iconos/placeholder_dog.jpg";
    private static final String FXML_FORMULARIO_SOLICITAR_CITA = "/com/proyectointegral2/Vista/FormularioSolicitarCita.fxml";

    private static final String TEXTO_NO_DISPONIBLE = "No disponible";
    private static final String TEXTO_NO_ESPECIFICADO = "No especificado";
    private static final String TEXTO_EDAD_DESCONOCIDA = "Desconocida";
    private static final String TEXTO_RAZA_NO_ESPECIFICADA = "Raza no especificada";
    private static final String TEXTO_NINGUNA_PATOLOGIA = "Ninguna conocida";
    private static final String TEXTO_ERROR_CARGA = "Error al cargar";

    @FXML private ImageView imgPerro;
    @FXML private Text TxtNombre;
    @FXML private Text TxtEdad;
    @FXML private Text TxtRaza;
    @FXML private Text TxtSexo;
    @FXML private Text TxtProtectora;
    @FXML private Text TxtPatologia;
    @FXML private Button BtnReservarCita;
    @FXML private ImageView imgLogoPequeno;

    private Perro perroActual;

    private ProtectoraDao protectoraDao;
    private PatologiaDao patologiaDao;
    private IdentificacionPatologiaDao identificacionPatologiaDao;

    @FXML
    public void initialize() {
        try {
            this.protectoraDao = new ProtectoraDao();
            this.patologiaDao = new PatologiaDao();
            this.identificacionPatologiaDao = new IdentificacionPatologiaDao();
        } catch (Exception e) {
            System.err.println("Error al inicializar DAOs en DetallesPerroController: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("DetallesPerroController inicializado. Esperando datos del perro...");
    }

    public void initData(Perro perro) {
        this.perroActual = perro;

        if (this.perroActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Datos", "No se recibió información del perro para mostrar.");
            mostrarDatosPorDefectoEnError();
            return;
        }

        System.out.println("Mostrando detalles para el perro: " + perroActual.getNombre() + " (ID: " + perroActual.getIdPerro() + ")");
        poblarCamposConDatosDelPerro();
    }

    private void mostrarDatosPorDefectoEnError() {
        TxtNombre.setText("Error");
        TxtEdad.setText(TEXTO_NO_DISPONIBLE);
        TxtRaza.setText(TEXTO_NO_DISPONIBLE);
        TxtSexo.setText(TEXTO_NO_DISPONIBLE);
        TxtProtectora.setText(TEXTO_NO_DISPONIBLE);
        TxtPatologia.setText(TEXTO_NO_DISPONIBLE);
        cargarImagen(null);
        BtnReservarCita.setDisable(true);
        BtnReservarCita.setText("No disponible");
    }

    private void poblarCamposConDatosDelPerro() {
        TxtNombre.setText(Objects.requireNonNullElse(perroActual.getNombre(), TEXTO_NO_DISPONIBLE));
        TxtSexo.setText(Objects.requireNonNullElse(perroActual.getSexo(), TEXTO_NO_ESPECIFICADO));
        TxtEdad.setText(calcularYFormatearEdad(perroActual.getFechaNacimiento()));
        TxtRaza.setText(obtenerNombreRaza(perroActual));

        cargarImagen(perroActual.getFoto());
        cargarYMostrarDatosProtectora();
        cargarYMostrarDatosPatologias();
        actualizarEstadoBotonReserva();
    }

    private String calcularYFormatearEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) return TEXTO_EDAD_DESCONOCIDA;
        Period periodo = Period.between(fechaNacimiento, LocalDate.now());
        int anos = periodo.getYears();
        int meses = periodo.getMonths();
        if (anos > 0) {
            String edadStr = anos + (anos == 1 ? " año" : " años");
            if (meses > 0 && anos < 3) {
                edadStr += " y " + meses + (meses == 1 ? " mes" : " meses");
            }
            return edadStr;
        } else if (meses > 0) {
            return meses + (meses == 1 ? " mes" : " meses");
        } else {
            return Math.max(0, periodo.getDays()) + (periodo.getDays() == 1 ? " día" : " días");
        }
    }

    private String obtenerNombreRaza(Perro perro) {
        if (perro.getRaza() != null && perro.getRaza().getNombreRaza() != null && !perro.getRaza().getNombreRaza().trim().isEmpty()) {
            return perro.getRaza().getNombreRaza();
        }
        return TEXTO_RAZA_NO_ESPECIFICADA;
    }

    private void cargarImagen(String imagePath) {
        Image loadedImage = null;
        boolean errorAlCargarOriginal = false;
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            String normalizedPath = imagePath.startsWith("/") ? imagePath : "/" + imagePath.replace("\\", "/");
            try (InputStream stream = getClass().getResourceAsStream(normalizedPath)) {
                if (stream != null) loadedImage = new Image(stream);
                else errorAlCargarOriginal = true;
                if (loadedImage != null && loadedImage.isError()) {
                    System.err.println("Error al decodificar: " + normalizedPath + ". Ex: " + loadedImage.getException());
                    errorAlCargarOriginal = true;
                }
            } catch (Exception e) { errorAlCargarOriginal = true; System.err.println("Ex al cargar: " + normalizedPath + ": " + e.getMessage());}
        } else errorAlCargarOriginal = true;

        if (errorAlCargarOriginal || loadedImage == null) {
            try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_DETALLES)) {
                if (placeholderStream != null) imgPerro.setImage(new Image(placeholderStream));
                else imgPerro.setImage(null);
            } catch (Exception e) { imgPerro.setImage(null); System.err.println("Ex placeholder: " + e.getMessage());}
        } else imgPerro.setImage(loadedImage);
    }

    private void cargarYMostrarDatosProtectora() {
        if (protectoraDao == null) { TxtProtectora.setText("Servicio no disponible"); return; }
        if (perroActual.getIdProtectora() > 0) {
            try {
                Protectora protectora = protectoraDao.obtenerProtectoraPorId(perroActual.getIdProtectora());
                if (protectora != null && protectora.getNombre() != null && !protectora.getNombre().trim().isEmpty()) {
                    TxtProtectora.setText(protectora.getNombre());
                } else TxtProtectora.setText(TEXTO_NO_ESPECIFICADO);
            } catch (SQLException e) { TxtProtectora.setText(TEXTO_ERROR_CARGA); e.printStackTrace(); }
        } else TxtProtectora.setText(TEXTO_NO_ESPECIFICADO);
    }

    private void cargarYMostrarDatosPatologias() {
        if (identificacionPatologiaDao == null || patologiaDao == null) { TxtPatologia.setText("Servicio no disponible"); return; }
        try {
            List<IdentificacionPatologia> identificaciones = identificacionPatologiaDao.obtenerIdentificacionesPorPerro(perroActual.getIdPerro());

            if (identificaciones == null || identificaciones.isEmpty()) {
                TxtPatologia.setText(TEXTO_NINGUNA_PATOLOGIA);
                return;
            }
            List<String> nombresYNotasPatologias = new ArrayList<>();
            for (IdentificacionPatologia ip : identificaciones) {
                Patologia pat = patologiaDao.obtenerPatologiaPorId(ip.getIdPatologia());
                if (pat != null && pat.getNombre() != null && !pat.getNombre().trim().isEmpty()) {
                    String patologiaStr = pat.getNombre();
                    if (ip.getDescripcion() != null && !ip.getDescripcion().trim().isEmpty()){
                        patologiaStr += " (" + ip.getDescripcion() + ")";
                    }
                    nombresYNotasPatologias.add(patologiaStr);
                }
            }
            if (!nombresYNotasPatologias.isEmpty()) TxtPatologia.setText(String.join(", ", nombresYNotasPatologias));
            else TxtPatologia.setText(TEXTO_NINGUNA_PATOLOGIA);
        } catch (SQLException e) { TxtPatologia.setText(TEXTO_ERROR_CARGA); e.printStackTrace(); }
    }

    private void actualizarEstadoBotonReserva() {
        if (perroActual.isAdoptado()) {
            BtnReservarCita.setText("Adoptado");
            BtnReservarCita.setDisable(true);
            BtnReservarCita.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: #7f8c8d; -fx-font-weight: normal; -fx-background-radius: 20; -fx-font-size: 16px;");
        } else {
            BtnReservarCita.setText("Reservar cita");
            BtnReservarCita.setDisable(false);
            BtnReservarCita.setStyle("-fx-background-color: #D2691E; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }

    @FXML
    void ReservarCita(ActionEvent event) {
        if (perroActual == null || perroActual.isAdoptado()) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_FORMULARIO_SOLICITAR_CITA));
            Parent root = loader.load();
            FormularioSolicitudCitaController controller = loader.getController();
            controller.initData(perroActual);

            Stage stage = new Stage();
            stage.setTitle("Solicitar Cita con " + perroActual.getNombre());
            stage.setScene(new Scene(root));
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
        } catch (Exception e) {
            UtilidadesVentana.mostrarAlertaError("Error", "No se pudo abrir el formulario de solicitud de cita.");
            e.printStackTrace();
        }
    }

    public void activarModoAdopcion() {
        BtnReservarCita.setText("Pedir Adopción");
        BtnReservarCita.setDisable(false);
        BtnReservarCita.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-size: 16px; -fx-font-weight: bold;");
        BtnReservarCita.setOnAction(this::pedirAdopcion);
    }

    private void pedirAdopcion(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar adopción");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas solicitar la adopción de este perro?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                PeticionAdopcionDao peticionAdopcionDao = new PeticionAdopcionDao();
                int idCliente = SesionUsuario.getEntidadIdEspecifica(); // ID de CLIENTE, no de USUARIO
                PeticionAdopcion peticion = new PeticionAdopcion();
                peticion.setFecha(java.sql.Date.valueOf(LocalDate.now()));
                peticion.setEstado("Pendiente");
                peticion.setIdCliente(idCliente);
                peticion.setIdPerro(perroActual.getIdPerro());
                peticion.setIdProtectora(perroActual.getIdProtectora());
                peticionAdopcionDao.crearPeticionAdopcion(peticion);
                UtilidadesExcepciones.mostrarInformacion("Petición registrada", null, "Tu petición de adopción ha sido registrada.");
                BtnReservarCita.setDisable(true);
            } catch (Exception e) {
                UtilidadesVentana.mostrarAlertaError("Error", "No se pudo registrar la petición de adopción.");
                e.printStackTrace();
            }
        }
    }
}