package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.*;
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.dao.ReservaCitaDao;
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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Controlador para el formulario de solicitud de cita con un perro.
 * Permite al usuario seleccionar una fecha, hora y especificar una donación.
 * Incluye restricciones de fecha y límite de citas por usuario.
 */
public class FormularioSolicitudCitaController {

    // --- Constantes ---
    private static final double DONACION_MINIMA_REQUERIDA = 3.00;
    private static final String ESTADO_CITA_POR_DEFECTO = "Pendiente";
    private static final DateTimeFormatter FORMATO_FECHA_USUARIO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA_USUARIO = DateTimeFormatter.ofPattern("HH:mm");
    private static final int MAX_CITAS_POR_USUARIO = 3;
    private static final int MAX_DIAS_ANTICIPACION_CITA = 14;

    // --- Componentes FXML ---
    @FXML private Label lblTituloFormulario;
    @FXML private Label lblNombrePerro;
    @FXML private DatePicker dpFechaCita;
    @FXML private ComboBox<String> cmbHoraCita;
    @FXML private TextField txtImporteDonacion;
    @FXML private Button btnCancelarSolicitud;
    @FXML private Button btnConfirmarSolicitud;

    // --- Estado del Controlador ---
    private Perro perroParaCita;
    private Usuario clienteLogueado;

    // --- DAOs ---
    private ReservaCitaDao reservaCitaDao;
    private ClienteDao clienteDao = new ClienteDao();

    /**
     * Método de inicialización del controlador.
     */
    @FXML
    public void initialize() {
        this.reservaCitaDao = new ReservaCitaDao();
        this.clienteLogueado = SesionUsuario.getUsuarioLogueado();

        if (this.clienteLogueado == null) {
            manejarErrorSesion();
            return;
        }

        configurarDatePicker();
        poblarComboBoxHoras();
        txtImporteDonacion.setPromptText(String.format(Locale.US, "Mínimo %.2f €", DONACION_MINIMA_REQUERIDA));

        if (lblTituloFormulario == null) System.err.println("ERROR FXML: lblTituloFormulario es null en initialize()");
        if (lblNombrePerro == null) System.err.println("ERROR FXML: lblNombrePerro es null en initialize()");

        dpFechaCita.valueProperty().addListener((obs, oldDate, newDate) -> poblarComboBoxHoras());
    }

    /**
     * Maneja la situación donde no hay un usuario logueado.
     */
    private void manejarErrorSesion() {
        UtilidadesVentana.mostrarAlertaError("Error de Sesión",
                "No se pudo identificar al usuario. Por favor, inicie sesión nuevamente.");
        cerrarVentanaAsync();
    }

    /**
     * Cierra la ventana del formulario de forma asíncrona.
     */
    private void cerrarVentanaAsync() {
        Platform.runLater(this::cerrarVentana);
    }

    /**
     * Inicializa la vista con los datos del perro.
     * @param perro El objeto Perro.
     */
    public void initData(Perro perro) {
        System.out.println("FormularioSolicitudCitaController.initData() llamado con perro: " + (perro != null ? perro.getNombre() : "null"));
        this.perroParaCita = perro;

        if (this.perroParaCita == null || this.perroParaCita.getNombre() == null || this.perroParaCita.getNombre().trim().isEmpty()) {
            manejarErrorDatosPerro();
            return;
        }

        System.out.println("Perro recibido en FormularioSolicitudCita: ID=" + this.perroParaCita.getIdPerro() +
                ", Nombre=" + this.perroParaCita.getNombre());

        actualizarEtiquetasConDatosPerro();
        if (btnConfirmarSolicitud != null) {
            btnConfirmarSolicitud.setDisable(false);
        }

        poblarComboBoxHoras();
    }

    /**
     * Maneja errores si los datos del perro no son válidos.
     */
    private void manejarErrorDatosPerro() {
        System.err.println("FormularioSolicitudCitaController: initData recibió un objeto Perro null o sin nombre válido.");
        UtilidadesVentana.mostrarAlertaError("Error de Datos",
                "No se pudo obtener la información del perro para la cita.");
        if (lblTituloFormulario != null) {
            lblTituloFormulario.setText("Error Cargando Perro");
        } else {
            System.err.println("ERROR: lblTituloFormulario es null en manejarErrorDatosPerro");
        }
        if (lblNombrePerro != null) {
            lblNombrePerro.setText("[Perro no disponible]");
        } else {
            System.err.println("ERROR: lblNombrePerro es null en manejarErrorDatosPerro");
        }
        if (btnConfirmarSolicitud != null) {
            btnConfirmarSolicitud.setDisable(true);
        }
    }

    /**
     * Actualiza las etiquetas de la UI con el nombre y raza del perro.
     */
    private void actualizarEtiquetasConDatosPerro() {
        if (this.perroParaCita == null || this.perroParaCita.getNombre() == null) {
            System.err.println("ERROR: Intento de actualizar etiquetas con perroParaCita null o sin nombre.");
            return;
        }

        if (lblTituloFormulario != null) {
            lblTituloFormulario.setText("Solicitar Cita con " + this.perroParaCita.getNombre());
        } else {
            System.err.println("ERROR FXML: lblTituloFormulario sigue siendo null al actualizar etiquetas.");
        }

        if (lblNombrePerro != null) {
            String infoRaza = "";
            if (this.perroParaCita.getRaza() != null &&
                    this.perroParaCita.getRaza().getNombreRaza() != null &&
                    !this.perroParaCita.getRaza().getNombreRaza().trim().isEmpty()) {
                infoRaza = " (" + this.perroParaCita.getRaza().getNombreRaza() + ")";
            }

            lblNombrePerro.setText(this.perroParaCita.getNombre() + infoRaza);
            System.out.println("FormularioSolicitudCitaController: Nombre de perro establecido en Label: " + lblNombrePerro.getText());
        } else {
            System.err.println("ERROR FXML: lblNombrePerro sigue siendo null al actualizar etiquetas.");
        }
    }

    /**
     * Configura el DatePicker.
     */
    private void configurarDatePicker() {
        if (dpFechaCita == null) return;
        final LocalDate hoy = LocalDate.now();
        final LocalDate fechaMinima = hoy.plusDays(1);
        final LocalDate fechaMaxima = hoy.plusDays(MAX_DIAS_ANTICIPACION_CITA);

        final Callback<DatePicker, DateCell> dayCellFactory = datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(fechaMinima) || item.isAfter(fechaMaxima)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        };
        dpFechaCita.setDayCellFactory(dayCellFactory);
        dpFechaCita.setValue(fechaMinima);
    }

    /**
     * Puebla el ComboBox de horas.
     */
    private void poblarComboBoxHoras() {
        if (cmbHoraCita == null || perroParaCita == null || dpFechaCita == null) return;
        ObservableList<String> horasDisponibles = FXCollections.observableArrayList();
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(20, 0);

        LocalDate fechaSeleccionada = dpFechaCita.getValue();
        LocalDate hoy = LocalDate.now();

        LocalDate fechaMinima = hoy.plusDays(1);
        LocalDate fechaMaxima = hoy.plusDays(MAX_DIAS_ANTICIPACION_CITA);

        if (fechaSeleccionada == null || fechaSeleccionada.isBefore(fechaMinima) || fechaSeleccionada.isAfter(fechaMaxima)) {
            cmbHoraCita.setItems(FXCollections.observableArrayList("No hay horas disponibles"));
            cmbHoraCita.getSelectionModel().selectFirst();
            return;
        }

        List<LocalTime> horasOcupadas = reservaCitaDao.obtenerHorasReservadasPorPerroYFecha(perroParaCita.getIdPerro(), fechaSeleccionada);

        LocalTime ahora = LocalTime.now();
        LocalTime horaActual = horaInicio;
        while (!horaActual.isAfter(horaFin)) {
            boolean esHoraOcupada = horasOcupadas.contains(horaActual);
            boolean esHoraFutura = true;
            if (fechaSeleccionada.isEqual(hoy)) {
                esHoraFutura = horaActual.isAfter(ahora);
            }
            if (!esHoraOcupada && esHoraFutura) {
                horasDisponibles.add(horaActual.format(FORMATO_HORA_USUARIO));
            }
            horaActual = horaActual.plusMinutes(30);
        }

        if (horasDisponibles.isEmpty()) {
            horasDisponibles.add("No hay horas disponibles");
        }
        cmbHoraCita.setItems(horasDisponibles);
        cmbHoraCita.getSelectionModel().selectFirst();
    }

    /**
     * Valida los campos del formulario.
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private boolean validarCampos(LocalDate fecha, String horaStr, String importeStr) {
        if (fecha == null) {
            UtilidadesVentana.mostrarAlertaError("Campo Requerido", "Por favor, seleccione una fecha para la cita.");
            dpFechaCita.requestFocus();
            return false;
        }

        if (fecha.isAfter(LocalDate.now().plusDays(MAX_DIAS_ANTICIPACION_CITA))) {
            UtilidadesVentana.mostrarAlertaError("Fecha Inválida",
                    "No puedes solicitar citas con más de " + MAX_DIAS_ANTICIPACION_CITA + " días de anticipación.");
            dpFechaCita.requestFocus();
            return false;
        }


        if (horaStr == null || horaStr.trim().isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campo Requerido", "Por favor, seleccione una hora para la cita.");
            cmbHoraCita.requestFocus();
            return false;
        }

        try {
            double importeDonacion = Double.parseDouble(importeStr.replace(',', '.'));
            if (importeDonacion < DONACION_MINIMA_REQUERIDA) {
                UtilidadesVentana.mostrarAlertaError("Importe Inválido",
                        String.format(Locale.US, "La donación mínima es de %.2f €.", DONACION_MINIMA_REQUERIDA));
                txtImporteDonacion.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            UtilidadesVentana.mostrarAlertaError("Importe Inválido",
                    "Por favor, ingrese un número válido para la donación (ej: 3.50).");
            txtImporteDonacion.requestFocus();
            return false;
        }

        try {
            LocalTime.parse(horaStr, FORMATO_HORA_USUARIO);
        } catch (DateTimeParseException e) {
            UtilidadesVentana.mostrarAlertaError("Hora Inválida",
                    "El formato de hora seleccionado ('" + horaStr + "') no es válido.");
            cmbHoraCita.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Verifica si el cliente ha alcanzado el límite máximo de citas activas.
     * @return true si el cliente puede solicitar una nueva cita, false si ha alcanzado el límite.
     */
    private boolean verificarLimiteDeCitas() {
        if (clienteLogueado == null) return false;
        try {

            List<ReservaCita> citasDelCliente = reservaCitaDao.obtenerReservasPorCliente(clienteLogueado.getIdUsuario());

            long citasActivas = citasDelCliente.stream()
                    .filter(cita -> "Pendiente".equalsIgnoreCase(cita.getEstadoCita()) ||
                            "Confirmada".equalsIgnoreCase(cita.getEstadoCita()))
                    .count();

            if (citasActivas >= MAX_CITAS_POR_USUARIO) {
                UtilidadesVentana.mostrarAlertaError("Límite Alcanzado",
                        "Has alcanzado el máximo de " + MAX_CITAS_POR_USUARIO + " citas activas. " +
                                "Por favor, espera a que alguna de tus citas actuales se complete o cancélala si es necesario.");
                return false;
            }
        } catch (SQLException e) {
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos",
                    "No se pudo verificar el número de citas existentes: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Maneja el evento de clic en el botón "Confirmar Solicitud".
     */
    @FXML
    void handleConfirmarSolicitud(ActionEvent event) {
        if (perroParaCita == null || clienteLogueado == null) {
            UtilidadesVentana.mostrarAlertaError("Error Interno",
                    "Faltan datos del perro o del cliente. No se puede procesar la solicitud.");
            return;
        }

        // Verificar límite de citas
        if (!verificarLimiteDeCitas()) {
            return;
        }

        // Obtener y validar campos
        LocalDate fechaSeleccionada = dpFechaCita.getValue();
        String horaSeleccionadaStr = cmbHoraCita.getValue();
        String importeStr = txtImporteDonacion.getText().trim();

        if (!validarCampos(fechaSeleccionada, horaSeleccionadaStr, importeStr)) {
            return;
        }

        // Obtener el idCliente real
        Cliente cliente;
        try {
            cliente = clienteDao.obtenerClientePorIdUsuario(clienteLogueado.getIdUsuario());
            if (cliente == null) {
                UtilidadesVentana.mostrarAlertaError("Error", "No se encontró el cliente asociado al usuario.");
                return;
            }
        } catch (SQLException e) {
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "No se pudo obtener el cliente: " + e.getMessage());
            return;
        }

        // Parsear datos validados
        LocalTime horaSeleccionada = LocalTime.parse(horaSeleccionadaStr, FORMATO_HORA_USUARIO);
        double importeDonacion = Double.parseDouble(importeStr.replace(',', '.'));

        // Crear objeto ReservaCita
        ReservaCita nuevaReserva = new ReservaCita();
        nuevaReserva.setFecha(fechaSeleccionada);
        nuevaReserva.setHora(horaSeleccionada);
        nuevaReserva.setIdCliente(cliente.getIdCliente());
        nuevaReserva.setIdPerro(perroParaCita.getIdPerro());
        nuevaReserva.setIdProtectora(perroParaCita.getIdProtectora());
        nuevaReserva.setEstadoCita(ESTADO_CITA_POR_DEFECTO);
        nuevaReserva.setDonacion(importeDonacion);

        try {
            int idReservaCreada = reservaCitaDao.crearReservaCita(nuevaReserva);
            if (idReservaCreada != -1) {
                String mensajeExito = String.format(
                        "Tu solicitud de cita para %s el %s a las %s ha sido enviada. " +
                                "La protectora se pondrá en contacto contigo. Donación registrada: %.2f €.",
                        perroParaCita.getNombre(),
                        fechaSeleccionada.format(FORMATO_FECHA_USUARIO),
                        horaSeleccionada.format(FORMATO_HORA_USUARIO),
                        importeDonacion);
                UtilidadesVentana.mostrarAlertaInformacion("Solicitud Enviada", mensajeExito);
                cerrarVentana();
            } else {
                UtilidadesVentana.mostrarAlertaError("Error al Solicitar",
                        "No se pudo registrar la solicitud de cita en la base de datos.");
            }
        } catch (SQLException e) {
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos",
                    "Ocurrió un problema al procesar tu solicitud: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maneja el evento de clic en el botón "Cancelar".
     */
    @FXML
    void handleCancelar(ActionEvent event) {
        cerrarVentana();
    }

    /**
     * Cierra la ventana actual del formulario.
     */
    private void cerrarVentana() {
        if (btnCancelarSolicitud != null && btnCancelarSolicitud.getScene() != null && btnCancelarSolicitud.getScene().getWindow() != null) {
            Stage stage = (Stage) btnCancelarSolicitud.getScene().getWindow();
            stage.close();
        } else {
            System.err.println("No se pudo obtener la referencia a la ventana para cerrarla (posiblemente el nodo no está en la escena).");
        }
    }
}