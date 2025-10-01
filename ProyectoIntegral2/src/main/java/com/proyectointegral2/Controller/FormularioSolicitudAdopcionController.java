package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.PeticionAdopcion;
import com.proyectointegral2.Model.Protectora; // Necesario si queremos mostrar el nombre de la protectora
import com.proyectointegral2.Model.SesionUsuario;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.dao.PeticionAdopcionDao;
import com.proyectointegral2.dao.ProtectoraDao; // Para obtener el nombre de la protectora
import com.proyectointegral2.utils.UtilidadesVentana;
// import javafx.application.Platform; // No se usa directamente aquí, pero UtilidadesVentana podría.
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
// import javafx.fxml.Initializable; // No es estrictamente necesario si solo usas @FXML initialize()
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

// import java.sql.Date; // java.sql.Date para la BD
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controlador para el formulario de solicitud de adopción de un perro.
 * Muestra información del cliente solicitante (obtenida de la sesión) y del perro seleccionado.
 * Permite al cliente ingresar una motivación y enviar la solicitud, que se registra
 * como una nueva petición de adopción en la base de datos.
 */
public class FormularioSolicitudAdopcionController {

    // --- Componentes FXML ---
    // Estos campos deben tener un fx:id coincidente en el archivo FXML asociado.
    @FXML private ImageView imgIconoAdopcion; // Icono decorativo.
    @FXML private Label lblTituloFormulario;

    // Labels para mostrar datos del Cliente solicitante.
    @FXML private Label lblNombreCliente;
    @FXML private Label lblNifCliente;
    @FXML private Label lblEmailCliente;
    @FXML private Label lblTelefonoCliente;
    @FXML private Label lblDireccionCliente;

    // Labels para mostrar datos del Perro a adoptar.
    @FXML private Label lblNombrePerroAdopcion;
    @FXML private Label lblEdadPerroAdopcion;
    @FXML private Label lblRazaPerroAdopcion;
    @FXML private Label lblNombreProtectoraAdopcion; // Nombre de la protectora del perro.

    @FXML private TextArea txtAreaMotivacion; // Para que el cliente escriba su motivación.
    @FXML private Button btnCancelarSolicitud;
    @FXML private Button btnEnviarSolicitud;

    // --- Estado del Controlador ---
    private Cliente clienteSolicitante; // Cliente logueado que realiza la solicitud.
    private Perro perroAAdoptar; // Perro seleccionado para la adopción.
    private Protectora protectoraDelPerro; // Protectora a la que pertenece el perro.

    // --- DAOs (Data Access Objects) ---
    private ClienteDao clienteDao;
    private ProtectoraDao protectoraDao;
    private PeticionAdopcionDao peticionAdopcionDao;

    // --- Constantes ---
    private static final String ROL_USUARIO_CLIENTE = "Cliente";
    private static final String ESTADO_PETICION_PENDIENTE = "Pendiente";
    private static final String TEXTO_NO_DISPONIBLE = "No disponible";
    private static final String TEXTO_ERROR_CARGA = "Error al cargar datos";
    private static final String TEXTO_PROTECTORA_NO_ENCONTRADA = "Protectora no encontrada";
    private static final String TEXTO_PROTECTORA_NO_ESPECIFICADA = "Protectora no especificada";
    private static final String TITULO_ERROR_DATOS = "Error de Datos";


    /**
     * Método de inicialización del controlador. Se llama automáticamente después de que
     * los campos FXML han sido inyectados.
     * Inicializa los DAOs y carga los datos del cliente logueado.
     */
    @FXML
    public void initialize() {
        try {
            this.clienteDao = new ClienteDao();
            this.protectoraDao = new ProtectoraDao();
            this.peticionAdopcionDao = new PeticionAdopcionDao();
        } catch (Exception e) {
            System.err.println("Error crítico al inicializar DAOs en FormularioSolicitudAdopcionController: " + e.getMessage());
            e.printStackTrace();
            handleErrorCargaInicial("Error de sistema al inicializar componentes. El formulario no funcionará.");
            return;
        }

        cargarDatosClienteDesdeSesion();
    }

    /**
     * Carga los datos del cliente que ha iniciado sesión.
     * Si no hay cliente o hay un error, actualiza la UI para reflejarlo.
     */
    private void cargarDatosClienteDesdeSesion() {
        Usuario usuarioCuenta = SesionUsuario.getUsuarioLogueado();
        if (usuarioCuenta != null && ROL_USUARIO_CLIENTE.equalsIgnoreCase(usuarioCuenta.getRol())) {
            try {
                this.clienteSolicitante = clienteDao.obtenerClientePorIdUsuario(usuarioCuenta.getIdUsuario());
                if (this.clienteSolicitante != null) {
                    poblarCamposClienteUI();
                } else {
                    handleErrorCargaInicial("No se encontró el perfil del cliente asociado a esta cuenta de usuario.");
                }
            } catch (SQLException e) {
                handleErrorCargaInicial("Error al cargar los datos del cliente desde la base de datos: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            handleErrorCargaInicial("No hay un cliente logueado o el rol del usuario no es 'Cliente'. No se puede procesar la solicitud.");
        }
    }

    /**
     * Inicializa el formulario con el perro seleccionado para adopción.
     * Este método debe ser llamado desde el controlador que abre este formulario,
     * pasándole el perro que el usuario desea adoptar.
     * @param perro El objeto {@link Perro} seleccionado para la adopción.
     */
    public void initDataPerro(Perro perro) {
        this.perroAAdoptar = perro;

        if (lblTituloFormulario == null || perroAAdoptar == null || perroAAdoptar.getNombre() == null) {
            handleErrorCargaInicial("No se especificó un perro válido para la adopción o el título del formulario no está disponible.");
            if (perroAAdoptar != null && perroAAdoptar.getNombre() != null && lblTituloFormulario != null) {
                lblTituloFormulario.setText("Solicitud de Adopción");
            }
            return;
        }

        lblTituloFormulario.setText("Solicitud de Adopción para " + perroAAdoptar.getNombre());
        poblarCamposPerroUI();
        cargarYMostrarNombreProtectora();
    }

    /**
     * Puebla los campos de la UI relacionados con el cliente solicitante.
     */
    private void poblarCamposClienteUI() {
        if (clienteSolicitante == null) {
            System.err.println("Error: Intentando poblar datos de cliente, pero clienteSolicitante es null.");
            if (lblNombreCliente != null) lblNombreCliente.setText(TEXTO_ERROR_CARGA);
            return;
        }

        if (lblNombreCliente != null) lblNombreCliente.setText(
                Objects.requireNonNullElse(clienteSolicitante.getNombre(), "").trim() + " " + Objects.requireNonNullElse(clienteSolicitante.getApellidos(), "").trim()
        );
        if (lblNifCliente != null) lblNifCliente.setText(Objects.requireNonNullElse(clienteSolicitante.getNif(), TEXTO_NO_DISPONIBLE));
        if (lblEmailCliente != null) lblEmailCliente.setText(Objects.requireNonNullElse(clienteSolicitante.getEmail(), TEXTO_NO_DISPONIBLE));
        if (lblTelefonoCliente != null) lblTelefonoCliente.setText(Objects.requireNonNullElse(clienteSolicitante.getTelefono(), TEXTO_NO_DISPONIBLE));

        if (lblDireccionCliente != null) {
            String dirCompleta = construirDireccionFormateadaCliente(clienteSolicitante);
            lblDireccionCliente.setText(dirCompleta.isEmpty() ? TEXTO_NO_DISPONIBLE : dirCompleta);
        }
    }

    /**
     * Construye una cadena de dirección formateada para el cliente.
     * @param cliente El objeto Cliente.
     * @return Dirección formateada.
     */
    private String construirDireccionFormateadaCliente(Cliente cliente) {
        if (cliente == null) return "";
        List<String> partes = new ArrayList<>();
        if (cliente.getCalle() != null && !cliente.getCalle().trim().isEmpty()) partes.add(cliente.getCalle().trim());
        if (cliente.getCiudad() != null && !cliente.getCiudad().trim().isEmpty()) partes.add(cliente.getCiudad().trim());
        if (cliente.getProvincia() != null && !cliente.getProvincia().trim().isEmpty()) partes.add(cliente.getProvincia().trim());
        if (cliente.getCodigoPostal() != null && !cliente.getCodigoPostal().trim().isEmpty()) partes.add(cliente.getCodigoPostal().trim());
        return String.join(", ", partes);
    }

    /**
     * Puebla los campos de la UI relacionados con el perro a adoptar.
     */
    private void poblarCamposPerroUI() {
        if (perroAAdoptar == null) {
            System.err.println("Error: Intentando poblar datos del perro, pero perroAAdoptar es null.");
            if (lblNombrePerroAdopcion != null) lblNombrePerroAdopcion.setText(TEXTO_ERROR_CARGA);
            return;
        }

        if (lblNombrePerroAdopcion != null) lblNombrePerroAdopcion.setText(Objects.requireNonNullElse(perroAAdoptar.getNombre(), TEXTO_NO_DISPONIBLE));

        if (lblEdadPerroAdopcion != null) {
            if (perroAAdoptar.getFechaNacimiento() != null) {
                Period periodo = Period.between(perroAAdoptar.getFechaNacimiento(), LocalDate.now());
                String edadStr;
                if (periodo.getYears() > 0) edadStr = periodo.getYears() + (periodo.getYears() == 1 ? " año" : " años");
                else if (periodo.getMonths() > 0) edadStr = periodo.getMonths() + (periodo.getMonths() == 1 ? " mes" : " meses");
                else edadStr = Math.max(0, periodo.getDays()) + (periodo.getDays() == 1 ? " día" : " días");
                lblEdadPerroAdopcion.setText(edadStr);
            } else {
                lblEdadPerroAdopcion.setText("Desconocida");
            }
        }

        if (lblRazaPerroAdopcion != null) {
            if (perroAAdoptar.getRaza() != null && perroAAdoptar.getRaza().getNombreRaza() != null) {
                lblRazaPerroAdopcion.setText(perroAAdoptar.getRaza().getNombreRaza());
            } else {
                lblRazaPerroAdopcion.setText("No especificada");
            }
        }
    }

    /**
     * Carga el nombre de la protectora del perro y lo muestra en la UI.
     */
    private void cargarYMostrarNombreProtectora() {
        if (lblNombreProtectoraAdopcion == null || perroAAdoptar == null || protectoraDao == null) {
            if (lblNombreProtectoraAdopcion != null) lblNombreProtectoraAdopcion.setText(TEXTO_NO_DISPONIBLE);
            return;
        }

        if (perroAAdoptar.getIdProtectora() > 0) {
            try {
                this.protectoraDelPerro = protectoraDao.obtenerProtectoraPorId(perroAAdoptar.getIdProtectora());
                if (this.protectoraDelPerro != null && this.protectoraDelPerro.getNombre() != null) {
                    lblNombreProtectoraAdopcion.setText(this.protectoraDelPerro.getNombre());
                } else {
                    lblNombreProtectoraAdopcion.setText(TEXTO_PROTECTORA_NO_ENCONTRADA);
                }
            } catch (SQLException e) {
                lblNombreProtectoraAdopcion.setText("Error al cargar (" + TEXTO_PROTECTORA_NO_ENCONTRADA + ")");
                e.printStackTrace();
            }
        } else {
            lblNombreProtectoraAdopcion.setText(TEXTO_PROTECTORA_NO_ESPECIFICADA);
        }
    }

    /**
     * Maneja errores que ocurren durante la carga inicial de datos (cliente o perro).
     * Muestra una alerta al usuario y deshabilita el botón de enviar solicitud.
     * @param mensaje El mensaje de error específico a mostrar.
     */
    private void handleErrorCargaInicial(String mensaje) {
        UtilidadesVentana.mostrarAlertaError(TITULO_ERROR_DATOS, mensaje);
        if (lblTituloFormulario != null) lblTituloFormulario.setText("Error en Solicitud de Adopción");
        if (txtAreaMotivacion != null) txtAreaMotivacion.setDisable(true);
        if (btnEnviarSolicitud != null) btnEnviarSolicitud.setDisable(true);
    }

    /**
     * Maneja el evento de clic del botón "Enviar Solicitud".
     * Valida la motivación y, si es válida, crea una nueva petición de adopción.
     * @param event El evento de acción.
     */
    @FXML
    void handleEnviarSolicitud(ActionEvent event) {
        if (clienteSolicitante == null || perroAAdoptar == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Datos Crítico", "No se pueden procesar los datos. Faltan datos esenciales del solicitante o del perro. " + "Por favor, cierre y vuelva a intentar.");
            return;
        }
        if (peticionAdopcionDao == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Sistema", "El servicio de peticiones de adopción no está disponible.");
            return;
        }

        String motivacion = (txtAreaMotivacion != null) ? txtAreaMotivacion.getText().trim() : "";
        if (motivacion.isEmpty()) {
            UtilidadesVentana.mostrarAlertaError("Campo Requerido", "Por favor, escribe tu motivación para la adopción.");
            if (txtAreaMotivacion != null) txtAreaMotivacion.requestFocus();
            return;
        }
        if (motivacion.length() > 500) {
            UtilidadesVentana.mostrarAlertaError("Texto Excesivo", "Tu motivación no debe exceder los 500 caracteres.");
            if (txtAreaMotivacion != null) txtAreaMotivacion.requestFocus();
            return;
        }


        PeticionAdopcion nuevaPeticion = new PeticionAdopcion();
        nuevaPeticion.setFecha(java.sql.Date.valueOf(LocalDate.now()));
        nuevaPeticion.setEstado(ESTADO_PETICION_PENDIENTE);
        nuevaPeticion.setIdCliente(clienteSolicitante.getIdCliente());
        nuevaPeticion.setIdPerro(perroAAdoptar.getIdPerro());


        try {
            int idPeticionCreada = peticionAdopcionDao.crearPeticionAdopcion(nuevaPeticion);
            if (idPeticionCreada > 0) {
                UtilidadesVentana.mostrarAlertaInformacion("Solicitud Enviada Exitosamente", "Tu solicitud de adopción para " + perroAAdoptar.getNombre() + " ha sido enviada correctamente. La protectora revisará tu caso y se pondrá en contacto contigo pronto.");
                cerrarVentana();
            } else {
                UtilidadesVentana.mostrarAlertaError("Error al Enviar Solicitud",
                        "No se pudo registrar tu solicitud de adopción en la base de datos. Inténtalo más tarde.");
            }
        } catch (SQLException e) {
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos",
                    "Ocurrió un error al procesar tu solicitud de adopción: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maneja el evento de clic del botón "Cancelar". Cierra el formulario.
     * @param event El evento de acción.
     */
    @FXML
    void handleCancelar(ActionEvent event) {
        cerrarVentana();
    }

    /**
     * Cierra la ventana actual del formulario.
     */
    private void cerrarVentana() {
        Node sourceNode = btnCancelarSolicitud;
        if (sourceNode == null && btnEnviarSolicitud != null) sourceNode = btnEnviarSolicitud;

        if (sourceNode != null && sourceNode.getScene() != null && sourceNode.getScene().getWindow() instanceof Stage) {
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.close();
        } else {
            System.err.println("Error: No se pudo obtener el Stage para cerrar el formulario de solicitud de adopción.");
        }
    }

}