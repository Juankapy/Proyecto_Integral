package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Patologia;
import com.proyectointegral2.Model.IdentificacionPatologia;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.dao.PatologiaDao;
import com.proyectointegral2.dao.IdentificacionPatologiaDao;
import com.proyectointegral2.utils.UtilidadesVentana;
import com.proyectointegral2.Controller.DetallesPerroController; // Asegúrate de que la ruta es correcta

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DetallesPerroController {

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

    private final String RUTA_IMAGEN_PLACEHOLDER_DETALLES = "/assets/Imagenes/iconos/placeholder_dog_grande.png";

    @FXML
    public void initialize() {
        this.protectoraDao = new ProtectoraDao();
        this.patologiaDao = new PatologiaDao();
        this.identificacionPatologiaDao = new IdentificacionPatologiaDao();
        System.out.println("DetallesPerroController inicializado. Esperando datos del perro...");
    }

    public void initData(Perro perro) {
        this.perroActual = perro;

        if (perroActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Datos", "No se recibió información del perro para mostrar.");
            TxtNombre.setText("Error");
            TxtEdad.setText("N/A");
            TxtRaza.setText("N/A");
            TxtSexo.setText("N/A");
            TxtProtectora.setText("N/A");
            TxtPatologia.setText("N/A");
            cargarImagenPlaceholder();
            BtnReservarCita.setDisable(true);
            return;
        }

        TxtNombre.setText(Objects.requireNonNullElse(perroActual.getNombre(), "Nombre no disponible"));
        TxtSexo.setText(Objects.requireNonNullElse(perroActual.getSexo(), "No especificado"));

        if (perroActual.getFechaNacimiento() != null) {
            Period periodo = Period.between(perroActual.getFechaNacimiento(), LocalDate.now());
            String edadStr;
            if (periodo.getYears() > 0) {
                edadStr = periodo.getYears() + (periodo.getYears() == 1 ? " año" : " años");
                if (periodo.getMonths() > 0 && periodo.getYears() < 2) {
                    edadStr += " y " + periodo.getMonths() + (periodo.getMonths() == 1 ? " mes" : " meses");
                }
            } else if (periodo.getMonths() > 0) {
                edadStr = periodo.getMonths() + (periodo.getMonths() == 1 ? " mes" : " meses");
            } else {
                edadStr = Math.max(0, periodo.getDays()) + (periodo.getDays() == 1 ? " día" : " días");
            }
            TxtEdad.setText(edadStr);
        } else {
            TxtEdad.setText("Desconocida");
        }

        if (perroActual.getRaza() != null && perroActual.getRaza().getNombreRaza() != null) {
            TxtRaza.setText(perroActual.getRaza().getNombreRaza());
        } else {
            TxtRaza.setText("Raza no especificada");
        }

        cargarImagenPerro();
        cargarDatosProtectora();
        cargarDatosPatologias();
        actualizarEstadoBotonReserva();
    }

    private void cargarImagenPerro() {
        String imagePath = perroActual.getFoto();
        try {
            Image loadedImage = null;
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                if (!imagePath.startsWith("/")) {
                    imagePath = "/" + imagePath.replace("\\", "/");
                }
                InputStream stream = getClass().getResourceAsStream(imagePath);
                if (stream != null) {
                    loadedImage = new Image(stream);
                    stream.close();
                } else {
                    System.err.println("WARN: Imagen no encontrada en classpath: " + imagePath + " para perro " + perroActual.getNombre());
                }
            }

            if (loadedImage == null || loadedImage.isError()) {
                if (loadedImage != null && loadedImage.isError()) {
                    System.err.println("Error al cargar imagen del perro '" + perroActual.getNombre() + "': " + loadedImage.getException().getMessage());
                }
                cargarImagenPlaceholder();
            } else {
                imgPerro.setImage(loadedImage);
            }
        } catch (Exception e) {
            System.err.println("Excepción general cargando imagen para " + perroActual.getNombre() + ": " + e.getMessage());
            e.printStackTrace();
            cargarImagenPlaceholder();
        }
    }

    private void cargarDatosProtectora() {
        // Elimina el uso de getNombreProtectora(), usa solo el ID
        if (perroActual.getIdProtectora() > 0) {
            try {
                Protectora protectora = protectoraDao.obtenerProtectoraPorId(perroActual.getIdProtectora());
                if (protectora != null && protectora.getNombre() != null) {
                    TxtProtectora.setText(protectora.getNombre());
                } else {
                    TxtProtectora.setText("No especificada");
                }
            } catch (SQLException e) {
                System.err.println("Error al obtener la protectora: " + e.getMessage());
                TxtProtectora.setText("Error al cargar");
            }
        } else {
            TxtProtectora.setText("No especificada");
        }
    }

    private void cargarDatosPatologias() {
        try {
            List<IdentificacionPatologia> identificaciones = identificacionPatologiaDao.obtenerPatologiasPorPerro(perroActual.getIdPerro());
            System.out.println();
            if (identificaciones != null && !identificaciones.isEmpty()) {
                List<String> nombresPatologias = identificaciones.stream()
                        .map(ident -> {
                            try {
                                Patologia pat = patologiaDao.obtenerPatologiaPorId(ident.getIdPatologia());
                                return pat != null ? pat.getNombre() : null;
                            } catch (SQLException e) {
                                System.err.println("Error al obtener detalle de patología ID " + ident.getIdPatologia() + ": " + e.getMessage());
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                if (!nombresPatologias.isEmpty()) {
                    TxtPatologia.setText(String.join(", ", nombresPatologias));
                } else {
                    TxtPatologia.setText("Ninguna conocida");
                }
            } else {
                TxtPatologia.setText("Ninguna conocida");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener identificaciones de patologías: " + e.getMessage());
            TxtPatologia.setText("Error al cargar");
        }
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

    private void cargarImagenPlaceholder() {
        try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_DETALLES)) {
            if (placeholderStream != null) {
                imgPerro.setImage(new Image(placeholderStream));
            } else {
                System.err.println("Error: No se pudo cargar la imagen placeholder de detalles desde: " + RUTA_IMAGEN_PLACEHOLDER_DETALLES);
                imgPerro.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Excepción crítica al cargar imagen placeholder de detalles: " + e.getMessage());
            imgPerro.setImage(null);
        }
    }

    @FXML
    void ReservarCita(ActionEvent event) {
        if (perroActual == null || perroActual.isAdoptado()) {
            UtilidadesVentana.mostrarAlertaInformacion("No Disponible", "Este perrito no está disponible para reservar citas.");
            return;
        }
        System.out.println("Botón Reservar Cita presionado para el perro ID: " + perroActual.getIdPerro() + ", Nombre: " + perroActual.getNombre()); // VERIFICA ESTE NOMBRE

        String formularioReservaFxml = "/com/proyectointegral2/Vista/FormularioSolicitarCita.fxml";
        String titulo = "Solicitar Cita con " + perroActual.getNombre();
        Stage ownerStage = (Stage) BtnReservarCita.getScene().getWindow();

        FormularioSolicitudCitaController formController =
                UtilidadesVentana.mostrarVentanaPopup(formularioReservaFxml, titulo, true, ownerStage);

        if (formController != null) {
            System.out.println("Pasando perro a FormularioSolicitudCita: " + perroActual.getNombre()); // DEBUG
            formController.initData(this.perroActual); // Pasar el objeto Perro
        } else {
            System.err.println("No se pudo obtener el controlador para FormularioSolicitudCita.");
            // El método mostrarVentanaPopup ya debería haber mostrado una alerta si el FXML no cargó.
        }
    }
}