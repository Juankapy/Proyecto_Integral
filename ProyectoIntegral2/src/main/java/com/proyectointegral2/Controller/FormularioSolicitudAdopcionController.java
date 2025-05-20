package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.*;
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.dao.PeticionAdopcionDao;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView; // Si usas el icono del título
import javafx.stage.Stage;

import java.sql.Date; // Para la fecha de la petición
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FormularioSolicitudAdopcionController {

    @FXML private ImageView imgIconoAdopcion;
    @FXML private Label lblTituloFormulario;

    // Labels para datos del Cliente
    @FXML private Label lblNombreCliente;
    @FXML private Label lblNifCliente;
    @FXML private Label lblEmailCliente;
    @FXML private Label lblTelefonoCliente;
    @FXML private Label lblDireccionCliente;

    // Labels para datos del Perro
    @FXML private Label lblNombrePerroAdopcion;
    @FXML private Label lblEdadPerroAdopcion;
    @FXML private Label lblRazaPerroAdopcion;
    @FXML private Label lblNombreProtectoraAdopcion;

    @FXML private TextArea txtAreaMotivacion;
    @FXML private Button btnCancelarSolicitud;
    @FXML private Button btnEnviarSolicitud;

    private Cliente clienteSolicitante;
    private Perro perroAAdoptar;
    private Protectora protectoraDelPerro; // Para el nombre de la protectora

    private ClienteDao clienteDao;
    private ProtectoraDao protectoraDao;
    private PeticionAdopcionDao peticionAdopcionDao;

    @FXML
    public void initialize() {
        this.clienteDao = new ClienteDao();
        this.protectoraDao = new ProtectoraDao();
        this.peticionAdopcionDao = new PeticionAdopcionDao();

        Usuario usuarioCuenta = SesionUsuario.getUsuarioLogueado();
        if (usuarioCuenta != null && "CLIENTE".equalsIgnoreCase(usuarioCuenta.getRol())) {
            try {
                this.clienteSolicitante = clienteDao.obtenerClientePorIdUsuario(usuarioCuenta.getIdUsuario());
                if (this.clienteSolicitante != null) {
                    poblarDatosCliente();
                } else {
                    handleErrorCarga("No se encontró el perfil del cliente asociado a esta cuenta.");
                }
            } catch (SQLException e) {
                handleErrorCarga("Error al cargar datos del cliente: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            handleErrorCarga("No hay un cliente logueado o el rol no es correcto.");
        }
    }

    /**
     * Inicializa el formulario con el perro seleccionado para adopción.
     * @param perro El objeto Perro a adoptar.
     */
    public void initData(Perro perro) {
        this.perroAAdoptar = perro;
        if (perroAAdoptar != null) {
            lblTituloFormulario.setText("Solicitud de Adopción para " + perro.getNombre());
            poblarDatosPerro();
            // Obtener y mostrar el nombre de la protectora
            if (perroAAdoptar.getIdProtectora() > 0) {
                try {
                    this.protectoraDelPerro = protectoraDao.obtenerProtectoraPorId(perroAAdoptar.getIdProtectora());
                    if (protectoraDelPerro != null) {
                        lblNombreProtectoraAdopcion.setText(Objects.requireNonNullElse(protectoraDelPerro.getNombre(), "No disponible"));
                    } else {
                        lblNombreProtectoraAdopcion.setText("Protectora no encontrada");
                    }
                } catch (SQLException e) {
                    lblNombreProtectoraAdopcion.setText("Error al cargar protectora");
                    e.printStackTrace();
                }
            } else {
                lblNombreProtectoraAdopcion.setText("No especificada");
            }

        } else {
            handleErrorCarga("No se especificó un perro para la adopción.");
        }
    }

    private void poblarDatosCliente() {
        if (clienteSolicitante == null) return;
        lblNombreCliente.setText(clienteSolicitante.getNombre() + " " + clienteSolicitante.getApellidos());
        lblNifCliente.setText(Objects.requireNonNullElse(clienteSolicitante.getNif(), "N/D"));
        lblEmailCliente.setText(Objects.requireNonNullElse(clienteSolicitante.getEmail(), "N/D"));
        lblTelefonoCliente.setText(Objects.requireNonNullElse(clienteSolicitante.getTelefono(), "N/D"));
        String dirCompleta = construirDireccionCliente(clienteSolicitante);
        lblDireccionCliente.setText(dirCompleta.isEmpty() ? "No disponible" : dirCompleta);
    }

    private String construirDireccionCliente(Cliente c) {
        List<String> partes = new ArrayList<>();
        if (c.getCalle() != null && !c.getCalle().trim().isEmpty()) partes.add(c.getCalle().trim());
        if (c.getCiudad() != null && !c.getCiudad().trim().isEmpty()) partes.add(c.getCiudad().trim());
        if (c.getProvincia() != null && !c.getProvincia().trim().isEmpty()) partes.add(c.getProvincia().trim());
        if (c.getCodigoPostal() != null && !c.getCodigoPostal().trim().isEmpty()) partes.add(c.getCodigoPostal().trim());
        return String.join(", ", partes);
    }


    private void poblarDatosPerro() {
        if (perroAAdoptar == null) return;
        lblNombrePerroAdopcion.setText(Objects.requireNonNullElse(perroAAdoptar.getNombre(), "N/D"));
        if (perroAAdoptar.getFechaNacimiento() != null) {
            Period periodo = Period.between(perroAAdoptar.getFechaNacimiento(), LocalDate.now());
            String edadStr = "";
            if (periodo.getYears() > 0) edadStr = periodo.getYears() + (periodo.getYears() == 1 ? " año" : " años");
            else if (periodo.getMonths() > 0) edadStr = periodo.getMonths() + (periodo.getMonths() == 1 ? " mes" : " meses");
            else edadStr = Math.max(0,periodo.getDays()) + (periodo.getDays() == 1 ? " día" : " días");
            lblEdadPerroAdopcion.setText(edadStr);
        } else {
            lblEdadPerroAdopcion.setText("Desconocida");
        }
        if (perroAAdoptar.getRaza() != null && perroAAdoptar.getRaza().getNombreRaza() != null) {
            lblRazaPerroAdopcion.setText(perroAAdoptar.getRaza().getNombreRaza());
        } else {
            lblRazaPerroAdopcion.setText("No especificada");
        }
    }

    private void handleErrorCarga(String mensaje) {
        UtilidadesVentana.mostrarAlertaError("Error de Datos", mensaje);
        if (lblTituloFormulario != null) lblTituloFormulario.setText("Error en Solicitud");
        if (btnEnviarSolicitud != null) btnEnviarSolicitud.setDisable(true);
    }

    @FXML
    void handleEnviarSolicitud(ActionEvent event) {
        if (clienteSolicitante == null || perroAAdoptar == null) {
            UtilidadesVentana.mostrarAlertaError("Error", "Faltan datos del solicitante o del perro.");
            return;
        }
        String motivacion = txtAreaMotivacion.getText().trim();
        if (motivacion.isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campo Requerido", "Por favor, escribe tu motivación para adoptar.");
            txtAreaMotivacion.requestFocus();
            return;
        }

        PeticionAdopcion nuevaPeticion = new PeticionAdopcion();
        nuevaPeticion.setFecha(Date.valueOf(LocalDate.now()));
        nuevaPeticion.setEstado("Pendiente");
        nuevaPeticion.setIdCliente(clienteSolicitante.getIdCliente());
        nuevaPeticion.setIdPerro(perroAAdoptar.getIdPerro());
        nuevaPeticion.setMensajePeticion(motivacion);

        try {
            int idPeticionCreada = peticionAdopcionDao.crearPeticionAdopcion(nuevaPeticion);
            if (idPeticionCreada != -1) {
                UtilidadesVentana.mostrarAlertaInformacion("Solicitud Enviada",
                        "Tu solicitud de adopción para " + perroAAdoptar.getNombre() +
                                " ha sido enviada. La protectora revisará tu caso y se pondrá en contacto.");
                cerrarVentana();
            } else {
                UtilidadesVentana.mostrarAlertaError("Error al Enviar", "No se pudo registrar tu solicitud de adopción.");
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
        Stage stage = (Stage) btnCancelarSolicitud.getScene().getWindow();
        stage.close();
    }
}