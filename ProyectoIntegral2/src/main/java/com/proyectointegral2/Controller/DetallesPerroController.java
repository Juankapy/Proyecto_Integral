package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Patologia;
import com.proyectointegral2.Model.IdentificacionPatologia;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.dao.PatologiaDao;
import com.proyectointegral2.dao.IdentificacionPatologiaDao;
import com.proyectointegral2.utils.UtilidadesVentana;
// import com.proyectointegral2.Controller.DetallesPerroController; // No es necesario importarse a sí mismo

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
// import javafx.fxml.FXMLLoader; // No se usa directamente para cargar FXML aquí
// import javafx.scene.Node; // No se usa
// import javafx.scene.Parent; // No se usa
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
// import javafx.scene.input.MouseEvent; // No se usa
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controlador para la ventana emergente (pop-up) que muestra los detalles de un perro.
 * Permite al usuario ver información completa del perro y, si está disponible,
 * iniciar el proceso para reservar una cita.
 */
public class DetallesPerroController {

    // --- Constantes ---
    private static final String RUTA_IMAGEN_PLACEHOLDER_DETALLES = "/assets/Imagenes/iconos/placeholder_dog_grande.png";
    private static final String FXML_FORMULARIO_SOLICITAR_CITA = "/com/proyectointegral2/Vista/FormularioSolicitarCita.fxml";

    private static final String TEXTO_NO_DISPONIBLE = "No disponible";
    private static final String TEXTO_NO_ESPECIFICADO = "No especificado";
    private static final String TEXTO_EDAD_DESCONOCIDA = "Desconocida";
    private static final String TEXTO_RAZA_NO_ESPECIFICADA = "Raza no especificada";
    private static final String TEXTO_NINGUNA_PATOLOGIA = "Ninguna conocida";
    private static final String TEXTO_ERROR_CARGA = "Error al cargar";

    // --- Componentes FXML ---
    @FXML private ImageView imgPerro;
    @FXML private Text TxtNombre;
    @FXML private Text TxtEdad;
    @FXML private Text TxtRaza;
    @FXML private Text TxtSexo;
    @FXML private Text TxtProtectora;
    @FXML private Text TxtPatologia;
    @FXML private Button BtnReservarCita;
    @FXML private ImageView imgLogoPequeno;

    // --- Estado del Controlador ---
    private Perro perroActual;

    // --- DAOs ---
    private ProtectoraDao protectoraDao;
    private PatologiaDao patologiaDao;
    private IdentificacionPatologiaDao identificacionPatologiaDao;

    /**
     * Método de inicialización del controlador. Se llama después de que los campos FXML han sido inyectados.
     * Crea instancias de los DAOs necesarios.
     */
    @FXML
    public void initialize() {
        this.protectoraDao = new ProtectoraDao();
        this.patologiaDao = new PatologiaDao();
        this.identificacionPatologiaDao = new IdentificacionPatologiaDao();
        System.out.println("DetallesPerroController inicializado. Esperando datos del perro...");
    }

    /**
     * Inicializa la vista con los datos del perro proporcionado.
     * Este método debe ser llamado desde el controlador que abre esta ventana de detalles.
     * @param perro El objeto Perro cuyos detalles se mostrarán.
     */
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

    /**
     * Muestra valores por defecto en los campos de texto cuando no se pueden cargar los datos del perro.
     * Deshabilita el botón de reserva.
     */
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

    /**
     * Llena todos los campos de la interfaz de usuario con la información del `perroActual`.
     */
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

    /**
     * Calcula y formatea la edad del perro a partir de su fecha de nacimiento.
     * @param fechaNacimiento La fecha de nacimiento del perro.
     * @return Una cadena formateada representando la edad (e.g., "2 años", "5 meses", "10 días"),
     *         o {@link #TEXTO_EDAD_DESCONOCIDA} si la fecha es nula.
     */
    private String calcularYFormatearEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return TEXTO_EDAD_DESCONOCIDA;
        }

        Period periodo = Period.between(fechaNacimiento, LocalDate.now());
        int anos = periodo.getYears();
        int meses = periodo.getMonths();
        int dias = periodo.getDays();

        if (anos > 0) {
            String edadStr = anos + (anos == 1 ? " año" : " años");
            if (meses > 0 && anos < 2) {
                edadStr += " y " + meses + (meses == 1 ? " mes" : " meses");
            }
            return edadStr;
        } else if (meses > 0) {
            return meses + (meses == 1 ? " mes" : " meses");
        } else {
            return Math.max(0, dias) + (dias == 1 ? " día" : " días");
        }
    }

    /**
     * Obtiene el nombre de la raza del perro.
     * @param perro El objeto Perro.
     * @return El nombre de la raza, o {@link #TEXTO_RAZA_NO_ESPECIFICADA} si no está disponible.
     */
    private String obtenerNombreRaza(Perro perro) {
        if (perro.getRaza() != null && perro.getRaza().getNombreRaza() != null && !perro.getRaza().getNombreRaza().trim().isEmpty()) {
            return perro.getRaza().getNombreRaza();
        }
        return TEXTO_RAZA_NO_ESPECIFICADA;
    }

    /**
     * Carga la imagen del perro en el ImageView. Si la imagen especificada no se encuentra
     * o hay un error, carga una imagen de placeholder.
     * @param imagePath La ruta de la imagen del perro. Puede ser null o vacía.
     */
    private void cargarImagen(String imagePath) {
        Image loadedImage = null;
        boolean errorAlCargarOriginal = false;

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            String normalizedPath = imagePath.startsWith("/") ? imagePath : "/" + imagePath.replace("\\", "/");
            try (InputStream stream = getClass().getResourceAsStream(normalizedPath)) {
                if (stream != null) {
                    loadedImage = new Image(stream);
                    if (loadedImage.isError()) {
                        System.err.println("Error al decodificar imagen desde: " + normalizedPath + ". Excepción: " + loadedImage.getException().getMessage());
                        errorAlCargarOriginal = true;
                    }
                } else {
                    System.err.println("WARN: Imagen no encontrada en classpath: " + normalizedPath +
                            (perroActual != null ? " para perro " + perroActual.getNombre() : ""));
                    errorAlCargarOriginal = true;
                }
            } catch (IOException e) {
                System.err.println("IOException al intentar cargar imagen " + normalizedPath + ": " + e.getMessage());
                errorAlCargarOriginal = true;
            } catch (IllegalArgumentException e) {
                System.err.println("Ruta de imagen inválida (IllegalArgumentException) " + normalizedPath + ": " + e.getMessage());
                errorAlCargarOriginal = true;
            }
        } else {
            errorAlCargarOriginal = true;
        }

        if (errorAlCargarOriginal || loadedImage == null) {
            try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_IMAGEN_PLACEHOLDER_DETALLES)) {
                if (placeholderStream != null) {
                    imgPerro.setImage(new Image(placeholderStream));
                } else {
                    System.err.println("ERROR CRITICO: Placeholder de detalles no encontrado: " + RUTA_IMAGEN_PLACEHOLDER_DETALLES);
                    imgPerro.setImage(null); // Sin imagen
                }
            } catch (IOException e) {
                System.err.println("IOException al cargar placeholder de detalles: " + e.getMessage());
                imgPerro.setImage(null);
            }
        } else {
            imgPerro.setImage(loadedImage);
        }
    }


    /**
     * Carga y muestra el nombre de la protectora del perro.
     * Utiliza el `idProtectora` del `perroActual`.
     */
    private void cargarYMostrarDatosProtectora() {
        if (perroActual.getIdProtectora() > 0) {
            try {
                Protectora protectora = protectoraDao.obtenerProtectoraPorId(perroActual.getIdProtectora());
                if (protectora != null && protectora.getNombre() != null && !protectora.getNombre().trim().isEmpty()) {
                    TxtProtectora.setText(protectora.getNombre());
                } else {
                    TxtProtectora.setText(TEXTO_NO_ESPECIFICADO);
                    System.out.println("Protectora encontrada para ID " + perroActual.getIdProtectora() + " pero sin nombre, o vacía.");
                }
            } catch (SQLException e) {
                System.err.println("Error SQL al obtener la protectora con ID " + perroActual.getIdProtectora() + ": " + e.getMessage());
                TxtProtectora.setText(TEXTO_ERROR_CARGA);
            }
        } else {
            TxtProtectora.setText(TEXTO_NO_ESPECIFICADO);
        }
    }

    /**
     * Carga y muestra las patologías identificadas para el perro.
     * Concatena los nombres de las patologías separadas por comas.
     */
    private void cargarYMostrarDatosPatologias() {
        try {
            List<IdentificacionPatologia> identificaciones = identificacionPatologiaDao.obtenerPatologiasPorPerro(perroActual.getIdPerro());

            if (identificaciones == null || identificaciones.isEmpty()) {
                TxtPatologia.setText(TEXTO_NINGUNA_PATOLOGIA);
                return;
            }

            List<String> nombresPatologias = identificaciones.stream()
                    .map(ident -> {
                        try {
                            Patologia pat = patologiaDao.obtenerPatologiaPorId(ident.getIdPatologia());

                            return (pat != null && pat.getNombre() != null && !pat.getNombre().trim().isEmpty()) ? pat.getNombre() : null;
                        } catch (SQLException e) {
                            System.err.println("Error SQL al obtener detalle de patología ID " + ident.getIdPatologia() + ": " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!nombresPatologias.isEmpty()) {
                TxtPatologia.setText(String.join(", ", nombresPatologias));
            } else {
                TxtPatologia.setText(TEXTO_NINGUNA_PATOLOGIA);
            }

        } catch (SQLException e) {
            System.err.println("Error SQL al obtener identificaciones de patologías para perro ID " + perroActual.getIdPerro() + ": " + e.getMessage());
            TxtPatologia.setText(TEXTO_ERROR_CARGA);
        }
    }

    /**
     * Actualiza el estado y texto del botón de reserva ("Reservar cita" o "Adoptado").
     * Deshabilita el botón si el perro ya está adoptado.
     */
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

    /**
     * Maneja el evento de clic en el botón "Reservar Cita".
     * Si el perro está disponible, abre la ventana emergente del formulario para solicitar una cita,
     * pasando los datos del `perroActual`.
     * @param event El evento de acción que disparó este método.
     */
    @FXML
    void ReservarCita(ActionEvent event) {
        if (perroActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error", "No hay información del perro para procesar la reserva.");
            return;
        }
        if (perroActual.isAdoptado()) {
            UtilidadesVentana.mostrarAlertaInformacion("No Disponible", "Este perrito ya ha sido adoptado y no está disponible para reservar citas.");
            return;
        }

        System.out.println("Botón Reservar Cita presionado para el perro: " + perroActual.getNombre() + " (ID: " + perroActual.getIdPerro() + ")");

        String titulo = "Solicitar Cita con " + perroActual.getNombre();

        Stage ownerStage = (Stage) BtnReservarCita.getScene().getWindow();

        FormularioSolicitudCitaController formController =
                UtilidadesVentana.mostrarVentanaPopup(FXML_FORMULARIO_SOLICITAR_CITA, titulo, true, ownerStage);

        if (formController != null) {
            System.out.println("Pasando perro al FormularioSolicitudCitaController: " + perroActual.getNombre());
            formController.initData(this.perroActual);
        } else {
            System.err.println("Error: No se pudo obtener el controlador para FormularioSolicitudCita o el pop-up no se mostró.");
        }
    }
}