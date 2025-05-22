package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.ReservaCita;
import com.proyectointegral2.dao.ReservaCitaDao;
import com.proyectointegral2.utils.UtilidadesVentana;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BandejaCitasController {

    @FXML private BorderPane mainContentPane;
    @FXML private ScrollPane citasScrollPane;
    @FXML private VBox citasContainerVBox;
    @FXML private Label lblNoCitas;
    @FXML private ImageView logoFooter;

    private ReservaCitaDao reservaCitaDao;

    private int idClienteActual;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");

    @FXML
    public void initialize() {
        this.reservaCitaDao = new ReservaCitaDao();

        lblNoCitas.setText("Cargando citas...");
        lblNoCitas.setVisible(true);
        citasContainerVBox.getChildren().clear();
    }

    public void initData(int idCliente) {
        this.idClienteActual = idCliente;
        cargarCitasDelCliente();
    }

    private void cargarCitasDelCliente() {
        if (idClienteActual <= 0) {
            lblNoCitas.setText("No se pudo identificar el usuario para cargar citas.");
            lblNoCitas.setVisible(true);
            citasContainerVBox.getChildren().clear();
            return;
        }

        List<ReservaCita> listaDeCitas;
        try {
             listaDeCitas = reservaCitaDao.obtenerCitasPorCliente(idClienteActual);

        } catch (Exception e) {
            e.printStackTrace();
            lblNoCitas.setText("Error al cargar las citas.");
            lblNoCitas.setVisible(true);
            citasContainerVBox.getChildren().clear();
            UtilidadesVentana.mostrarAlertaError("Error de Carga", "No se pudieron cargar las citas: " + e.getMessage());
            return;
        }

        citasContainerVBox.getChildren().clear();

        if (listaDeCitas == null || listaDeCitas.isEmpty()) {
            lblNoCitas.setText("No tienes citas programadas.");
            lblNoCitas.setVisible(true);
        } else {
            lblNoCitas.setVisible(false);
            for (ReservaCita cita : listaDeCitas) {
                VBox tarjeta = crearTarjetaCitaGrafica(cita);
                citasContainerVBox.getChildren().add(tarjeta);
            }
        }
    }

    private VBox crearTarjetaCitaGrafica(ReservaCita cita) {
        VBox tarjetaVBox = new VBox(10.0);
        tarjetaVBox.setPrefWidth(580.0);
        tarjetaVBox.setMaxWidth(VBox.USE_PREF_SIZE);
        tarjetaVBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-color: #D0D0D0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        String nombrePerroConRaza = "Perro Desconocido";
        String nombreProtectora = "Protectora Desconocida";

        Label lblNombrePerroRaza = new Label(nombrePerroConRaza);
        lblNombrePerroRaza.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 18px;");
        VBox.setMargin(lblNombrePerroRaza, new Insets(0, 0, 8, 0));

        HBox fechaHBox = crearFilaDetalle("Fecha:", cita.getFecha().format(dateFormatter));
        HBox horaHBox = crearFilaDetalle("Hora:", cita.getHora().format(timeFormatter));

        HBox protectoraHBox = null;
        if (!nombreProtectora.equals("Protectora Desconocida") && !nombreProtectora.isEmpty()) {
            protectoraHBox = crearFilaDetalle("Protectora:", nombreProtectora);
            VBox.setMargin(protectoraHBox, new Insets(0,0,10,0));
        }

        HBox botonesHBox = new HBox(10.0);
        botonesHBox.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(botonesHBox, new Insets(10,0,0,0));

        Button btnModificar = new Button("Modificar");
        btnModificar.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-background-radius: 5;");
        btnModificar.setOnAction(event -> handleModificarCita(cita));

        Button btnCancelarCita = new Button("Cancelar Cita");
        btnCancelarCita.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-background-radius: 5;");
        btnCancelarCita.setOnAction(event -> handleAccionCancelarCita(cita));

        botonesHBox.getChildren().addAll(btnModificar, btnCancelarCita);

        tarjetaVBox.getChildren().add(lblNombrePerroRaza);
        tarjetaVBox.getChildren().add(fechaHBox);
        if (protectoraHBox != null) {
            tarjetaVBox.getChildren().add(protectoraHBox);
        }
        tarjetaVBox.getChildren().add(botonesHBox);

        tarjetaVBox.setUserData(cita);
        return tarjetaVBox;
    }

    private HBox crearFilaDetalle(String etiqueta, String valor) {
        HBox filaHBox = new HBox(5.0);
        filaHBox.setAlignment(Pos.BASELINE_LEFT);
        Label lblEtiqueta = new Label(etiqueta);
        lblEtiqueta.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 14px;");
        Label lblValor = new Label(Objects.requireNonNullElse(valor, "N/D"));
        lblValor.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");
        filaHBox.getChildren().addAll(lblEtiqueta, lblValor);
        return filaHBox;
    }

    private void handleModificarCita(ReservaCita cita) {
        System.out.println("Modificar cita ID: " + cita.getIdReserva() + " con motivo: " + cita.getEstadoCita());
        // Lógica para abrir ventana de modificación
        UtilidadesVentana.mostrarAlertaInformacion("Modificar Cita", "Funcionalidad para modificar cita no implementada.");
    }

    private void handleAccionCancelarCita(ReservaCita cita) {
        System.out.println("Intentando cancelar cita ID: " + cita.getIdReserva());
        UtilidadesVentana.mostrarAlertaInformacion("Cancelar Cita", "Funcionalidad para cancelar cita no implementada.");
    }

}