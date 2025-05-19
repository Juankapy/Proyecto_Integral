package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.ReservaCita;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ReservaCitaDao;
import com.proyectointegral2.Model.SesionUsuario;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // IMPORTADO
import java.time.format.DateTimeParseException;

public class FormularioSolicitudCitaController {

    @FXML private ImageView imgIconoCalendario;
    @FXML private Label lblTituloFormulario;
    @FXML private Label lblNombrePerro;
    @FXML private DatePicker dpFechaCita;
    @FXML private ComboBox<String> cmbHoraCita;
    @FXML private TextField txtImporteDonacion;
    @FXML private Button btnCancelarSolicitud;
    @FXML private Button btnConfirmarSolicitud;

    private Perro perroParaCita;
    private Usuario clienteLogueado;
    private ReservaCitaDao reservaCitaDao;

    private final double DONACION_MINIMA = 3.00;

    // DECLARAR LOS FORMATEADORES COMO CAMPOS DE INSTANCIA
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm"); // Formato para parsear la hora del ComboBox

    @FXML
    public void initialize() {
        this.reservaCitaDao = new ReservaCitaDao();
        this.clienteLogueado = SesionUsuario.getUsuarioLogueado();

        if (this.clienteLogueado == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Sesión", "No se pudo identificar al usuario...");
            Platform.runLater(() -> {
                if (lblTituloFormulario != null && lblTituloFormulario.getScene() != null && lblTituloFormulario.getScene().getWindow() != null) {
                    ((Stage) lblTituloFormulario.getScene().getWindow()).close();
                }
            });
            return;
        }

        configurarDatePicker();
        poblarComboBoxHoras();
        txtImporteDonacion.setPromptText("Mínimo " + String.format("%.2f", DONACION_MINIMA) + " €");

    }

    public void initData(Perro perro) {
        System.out.println("FormularioSolicitudCitaController.initData() llamado.");
        this.perroParaCita = perro;

        if (this.perroParaCita != null && this.perroParaCita.getNombre() != null) {
            System.out.println("Perro recibido en FormularioSolicitudCita: ID=" + this.perroParaCita.getIdPerro() + ", Nombre=" + this.perroParaCita.getNombre());

            // Asegurarse de que los FXML Labels no sean null antes de usarlos
            if (lblTituloFormulario != null) {
                lblTituloFormulario.setText("Solicitar Cita con " + this.perroParaCita.getNombre());
            } else {
                System.err.println("FormularioSolicitudCitaController: ERROR - lblTituloFormulario es null.");
            }

            if (lblNombrePerro != null) {
                String nombreRazaInfo = "";
                if (this.perroParaCita.getRaza() != null && this.perroParaCita.getRaza().getNombreRaza() != null && !this.perroParaCita.getRaza().getNombreRaza().isEmpty()) {
                    nombreRazaInfo = " (" + this.perroParaCita.getRaza().getNombreRaza() + ")";
                }
                lblNombrePerro.setText(this.perroParaCita.getNombre() + nombreRazaInfo);
                System.out.println("FormularioSolicitudCitaController: Nombre de perro establecido en Label: " + lblNombrePerro.getText());
            } else {
                System.err.println("FormularioSolicitudCitaController: ERROR - lblNombrePerro es null.");
            }

            if (btnConfirmarSolicitud != null) btnConfirmarSolicitud.setDisable(false);

        } else {
            System.err.println("FormularioSolicitudCitaController: initData recibió un objeto Perro null o sin nombre.");
            UtilidadesVentana.mostrarAlertaError("Error de Datos", "No se pudo obtener la información del perro para la cita.");
            if (lblTituloFormulario != null) lblTituloFormulario.setText("Error Cargando Perro");
            if (lblNombrePerro != null) lblNombrePerro.setText("[Error: Perro no disponible]");
            if (btnConfirmarSolicitud != null) btnConfirmarSolicitud.setDisable(true);
        }
    }

    private void configurarDatePicker() {
        final Callback<DatePicker, DateCell> dayCellFactory = datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now().plusDays(1))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        };
        dpFechaCita.setDayCellFactory(dayCellFactory);
        dpFechaCita.setValue(LocalDate.now().plusDays(1));
    }

    private void poblarComboBoxHoras() {
        ObservableList<String> horas = FXCollections.observableArrayList();
        for (int h = 9; h <= 17; h++) {
            horas.add(String.format("%02d:00", h));
            if (h < 17) {
                horas.add(String.format("%02d:30", h));
            }
        }
        cmbHoraCita.setItems(horas);
    }

    @FXML
    void handleConfirmarSolicitud(ActionEvent event) {
        if (perroParaCita == null || clienteLogueado == null) {
            UtilidadesVentana.mostrarAlertaError("Error", "Faltan datos del perro o del cliente.");
            return;
        }

        LocalDate fechaSeleccionada = dpFechaCita.getValue();
        String horaSeleccionadaStr = cmbHoraCita.getValue();
        String importeStr = txtImporteDonacion.getText().trim().replace(",", ".");

        if (fechaSeleccionada == null) {
            UtilidadesVentana.mostrarAlertaError("Campo Requerido", "Por favor, seleccione una fecha para la cita.");
            return;
        }
        if (horaSeleccionadaStr == null || horaSeleccionadaStr.isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campo Requerido", "Por favor, seleccione una hora para la cita.");
            return;
        }
        double importeDonacion;
        try {
            importeDonacion = Double.parseDouble(importeStr);
            if (importeDonacion < DONACION_MINIMA) {
                UtilidadesVentana.mostrarAlertaError("Importe Inválido", "La donación mínima es de " + String.format("%.2f", DONACION_MINIMA) + " €.");
                return;
            }
        } catch (NumberFormatException e) {
            UtilidadesVentana.mostrarAlertaError("Importe Inválido", "Por favor, ingrese un número válido para el importe.");
            return;
        }

        LocalTime horaSeleccionada;
        try {
            // Usar el timeFormatter declarado en la clase
            horaSeleccionada = LocalTime.parse(horaSeleccionadaStr, this.timeFormatter);
        } catch (DateTimeParseException e) {
            UtilidadesVentana.mostrarAlertaError("Hora Inválida", "El formato de hora seleccionado no es válido (" + horaSeleccionadaStr + ").");
            return;
        }

        ReservaCita nuevaReserva = new ReservaCita();
        nuevaReserva.setFecha(fechaSeleccionada);
        nuevaReserva.setHora(horaSeleccionada);
        nuevaReserva.setIdCliente(clienteLogueado.getIdUsuario());
        nuevaReserva.setIdPerro(perroParaCita.getIdPerro());
        nuevaReserva.setIdProtectora(perroParaCita.getIdProtectora());
        // nuevaReserva.setEstadoCita("Pendiente"); // El DDL ya tiene DEFAULT 'Pendiente'

        try {
            int idReservaCreada = reservaCitaDao.crearReservaCita(nuevaReserva);
            if (idReservaCreada != -1) {
                UtilidadesVentana.mostrarAlertaInformacion("Solicitud Enviada",
                        "Tu solicitud de cita para " + perroParaCita.getNombre() + " el " +
                                fechaSeleccionada.format(this.dateFormatter) + // Usar el dateFormatter declarado
                                " a las " + horaSeleccionada.format(this.timeFormatter) + // Formatear la hora también
                                " ha sido enviada. La protectora se pondrá en contacto contigo.");
                cerrarVentana();
            } else {
                UtilidadesVentana.mostrarAlertaError("Error al Solicitar", "No se pudo registrar la solicitud de cita.");
            }
        } catch (SQLException e) {
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "Ocurrió un error al procesar tu solicitud: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        if (btnCancelarSolicitud != null && btnCancelarSolicitud.getScene() != null && btnCancelarSolicitud.getScene().getWindow() != null) {
            Stage stage = (Stage) btnCancelarSolicitud.getScene().getWindow();
            stage.close();
        }
    }
}