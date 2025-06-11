package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.ReservaCita;
import com.proyectointegral2.Model.Usuario; // Para pasar al formulario de edición
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.dao.PerroDao;
import com.proyectointegral2.dao.ReservaCitaDao;
import com.proyectointegral2.dao.UsuarioDao; // Necesario para obtener el objeto Usuario completo
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
// import javafx.fxml.Initializable; // No es estrictamente necesario si solo usas @FXML initialize()
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controlador para la pantalla de perfil del usuario cliente.
 * Muestra la información personal del cliente, su foto de perfil (si existe),
 * y un historial de sus citas o eventos.
 * Permite al usuario navegar a un formulario para editar sus datos.
 */
public class PerfilUsuarioController {

    // --- Componentes FXML ---
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

    // --- DAOs (Data Access Objects) ---
    private ClienteDao clienteDAO;
    private ReservaCitaDao reservaCitaDao;
    private UsuarioDao usuarioDao;
    private PerroDao perroDao;

    // --- Estado del Controlador ---
    private Cliente clienteActual;
    private int idUsuarioDelPerfil;

    // --- Constantes ---
    private static final String RUTA_PLACEHOLDER_FOTO_PERFIL = "/assets/Imagenes/iconos/sinusuario.jpg";
    private static final DateTimeFormatter FORMATO_FECHA_HISTORIAL = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String RUTA_FXML_FORMULARIO_USUARIO = "/com/proyectointegral2/Vista/FormularioUsuario.fxml";
    private static final String RUTA_FXML_MAIN_CLIENTE = "/com/proyectointegral2/Vista/Main.fxml";
    private static final String TITULO_VENTANA_MAIN_CLIENTE = "Panel Principal - Dogpuccino";

    private static final String TEXTO_CARGANDO = "Cargando...";
    private static final String TEXTO_NO_DISPONIBLE = "No disponible";
    private static final String TEXTO_ERROR_CARGA = "Error al cargar datos";
    private static final String PLACEHOLDER_HISTORIAL_CARGANDO = "Cargando historial...";
    private static final String PLACEHOLDER_HISTORIAL_VACIO = "No hay historial de citas o eventos.";
    private static final String PLACEHOLDER_HISTORIAL_ERROR = "Error al cargar historial.";
    private static final String PLACEHOLDER_HISTORIAL_NO_IDENTIFICADO = "No se pudo identificar el cliente para el historial.";
    private static final String PLACEHOLDER_HISTORIAL_NO_INFO = "No hay información disponible.";


    /**
     * Método de inicialización del controlador. Se llama automáticamente después de que
     * los campos FXML han sido inyectados.
     * Inicializa los DAOs y establece un estado de carga inicial en la UI.
     */
    @FXML
    public void initialize() {
        try {
            this.clienteDAO = new ClienteDao();
            this.reservaCitaDao = new ReservaCitaDao();
            this.usuarioDao = new UsuarioDao();
            this.perroDao = new PerroDao();
        } catch (Exception e) {
            System.err.println("Error crítico al inicializar DAOs en PerfilUsuarioController: " + e.getMessage());
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sistema", "No se pudo inicializar el acceso a la base de datos. El perfil no se cargará correctamente.");
        }
        configurarUIEnEstadoCarga();
    }

    /**
     * Inicializa el controlador con el ID del usuario cuyo perfil se va a mostrar.
     * Este método debe ser llamado desde el controlador que navega a esta pantalla.
     * @param idUsuario El ID del Usuario (de la tabla USUARIO) asociado al perfil del Cliente.
     * @param nombreUsuarioLogin El nombre de usuario (login), podría usarse para el título o logs (actualmente no se usa aquí directamente).
     */
    public void initData(int idUsuario, String nombreUsuarioLogin) {
        this.idUsuarioDelPerfil = idUsuario;
        System.out.println("PerfilUsuarioController: initData para Usuario ID: " + idUsuario + ", Nombre Login: " + nombreUsuarioLogin);
        cargarDatosDelClienteYActualizarUI();
    }

    /**
     * Configura la interfaz de usuario para mostrar un estado de "cargando"
     * mientras se recuperan los datos del perfil.
     */
    private void configurarUIEnEstadoCarga() {
        if (TxtNombre != null) TxtNombre.setText(TEXTO_CARGANDO);
        if (TxtEmail != null) TxtEmail.setText("...");
        if (TxtTelefono != null) TxtTelefono.setText("...");
        if (TxtDireccion != null) TxtDireccion.setText("...");
        if (listViewHistorial != null) {
            listViewHistorial.getItems().clear();
            listViewHistorial.setPlaceholder(new Label(PLACEHOLDER_HISTORIAL_CARGANDO));
        }
        cargarImagenPlaceholder();
        if (BtnEditarDatos != null) BtnEditarDatos.setDisable(true);
    }

    /**
     * Carga los datos del cliente asociado al `idUsuarioDelPerfil` y actualiza la UI.
     * Maneja errores durante la carga y actualiza la UI en consecuencia.
     */
    private void cargarDatosDelClienteYActualizarUI() {
        if (idUsuarioDelPerfil <= 0) {
            UtilidadesVentana.mostrarAlertaError("Error de Perfil", "ID de usuario no válido para cargar el perfil.");
            actualizarUIConDatosDeError();
            return;
        }
        if (clienteDAO == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Sistema", "El servicio de datos de cliente no está disponible.");
            actualizarUIConDatosDeError();
            return;
        }

        try {
            this.clienteActual = clienteDAO.obtenerClientePorIdUsuario(idUsuarioDelPerfil);

            if (this.clienteActual != null) {
                actualizarCamposDeTextoConCliente(this.clienteActual);
                cargarFotoDePerfil(this.clienteActual.getRutaFotoPerfil());
                cargarHistorialCitas(this.clienteActual.getIdCliente());

                if (BtnEditarDatos != null) BtnEditarDatos.setDisable(false);
            } else {
                UtilidadesVentana.mostrarAlertaInformacion("Perfil no Encontrado",
                        "No se encontró un perfil de cliente asociado al usuario ID: " + idUsuarioDelPerfil +
                                ". Puede que necesite completar su registro de cliente.");
                actualizarUIConDatosDeError();
            }
        } catch (SQLException e) {
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos",
                    "No se pudieron cargar los datos del perfil del cliente: " + e.getMessage());
            e.printStackTrace();
            actualizarUIConDatosDeError();
        } catch (Exception e) {
            UtilidadesVentana.mostrarAlertaError("Error Inesperado",
                    "Ocurrió un error inesperado al cargar el perfil: " + e.getMessage());
            e.printStackTrace();
            actualizarUIConDatosDeError();
        }
    }

    /**
     * Actualiza los campos de texto de la UI con la información del cliente.
     * @param cliente El objeto Cliente con los datos.
     */
    private void actualizarCamposDeTextoConCliente(Cliente cliente) {
        if (TxtNombre != null) {
            String nombreCompleto = String.join(" ",
                    Objects.requireNonNullElse(cliente.getNombre(), "").trim(),
                    Objects.requireNonNullElse(cliente.getApellidos(), "").trim()
            ).trim();
            TxtNombre.setText(nombreCompleto.isEmpty() ? TEXTO_NO_DISPONIBLE : nombreCompleto);
        }

        if (TxtEmail != null) TxtEmail.setText(Objects.requireNonNullElse(cliente.getEmail(), TEXTO_NO_DISPONIBLE));
        if (TxtTelefono != null) TxtTelefono.setText(Objects.requireNonNullElse(cliente.getTelefono(), TEXTO_NO_DISPONIBLE));

        if (TxtDireccion != null) {
            String direccionCompleta = construirDireccionFormateada(cliente);
            TxtDireccion.setText(direccionCompleta.isEmpty() ? TEXTO_NO_DISPONIBLE : direccionCompleta);
        }
    }

    /**
     * Construye una cadena de dirección formateada a partir de las partes de la dirección del cliente.
     * Omite las partes que son nulas o vacías.
     * @param cliente El objeto Cliente.
     * @return Una cadena con la dirección formateada (ej: "Calle Falsa 123, Ciudad, Provincia, 12345").
     */
    private String construirDireccionFormateada(Cliente cliente) {
        if (cliente == null) return "";
        List<String> partesDireccion = new ArrayList<>();
        if (cliente.getCalle() != null && !cliente.getCalle().trim().isEmpty()) partesDireccion.add(cliente.getCalle().trim());
        if (cliente.getCiudad() != null && !cliente.getCiudad().trim().isEmpty()) partesDireccion.add(cliente.getCiudad().trim());
        if (cliente.getProvincia() != null && !cliente.getProvincia().trim().isEmpty()) partesDireccion.add(cliente.getProvincia().trim());
        if (cliente.getCodigoPostal() != null && !cliente.getCodigoPostal().trim().isEmpty()) partesDireccion.add(cliente.getCodigoPostal().trim());
        return String.join(", ", partesDireccion);
    }

    /**
     * Carga la foto de perfil del cliente en el ImageView.
     * Si no hay foto o hay un error, carga una imagen placeholder.
     * @param rutaImagenRelativa La ruta de la imagen, relativa a la carpeta 'resources' del classpath.
     */
    private void cargarFotoDePerfil(String rutaImagenRelativa) {
        if (imgFotoPerfil == null) return;
        String pathNormalizado = null;

        if (rutaImagenRelativa != null && !rutaImagenRelativa.trim().isEmpty()) {
            try {

                pathNormalizado = rutaImagenRelativa.startsWith("/") ? rutaImagenRelativa : "/" + rutaImagenRelativa.replace("\\", "/");

                try (InputStream stream = getClass().getResourceAsStream(pathNormalizado)) {
                    if (stream != null) {
                        imgFotoPerfil.setImage(new Image(stream));
                        return;
                    } else {
                        System.err.println("WARN: Imagen de perfil no encontrada en classpath: " + pathNormalizado);
                    }
                }
            } catch (Exception e) {
                System.err.println("ERROR: Excepción al cargar la imagen de perfil desde '" +
                        (pathNormalizado != null ? pathNormalizado : rutaImagenRelativa) + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
        cargarImagenPlaceholder();
    }

    /**
     * Carga el historial de citas del cliente en el ListView.
     * Para cada cita, obtiene el nombre del perro asociado realizando una consulta adicional.
     * @param idCliente El ID del Cliente (de la tabla CLIENTE) cuyo historial se va a cargar.
     */
    private void cargarHistorialCitas(int idCliente) {
        if (listViewHistorial == null) {
            System.err.println("Error: ListView para historial es null.");
            return;
        }

        if (reservaCitaDao == null || perroDao == null) {
            listViewHistorial.setPlaceholder(new Label("Servicio de historial o datos de perros no disponible."));
            System.err.println("Error: ReservaCitaDao o PerroDao no están inicializados.");
            return;
        }

        if (idCliente <= 0) {
            listViewHistorial.setPlaceholder(new Label(PLACEHOLDER_HISTORIAL_NO_IDENTIFICADO));
            System.err.println("Error: idCliente no válido para cargar historial: " + idCliente);
            return;
        }

        ObservableList<String> itemsHistorial = FXCollections.observableArrayList();
        listViewHistorial.setItems(itemsHistorial);

        try {
            List<ReservaCita> citas = reservaCitaDao.obtenerReservasPorCliente(idCliente);

            System.out.println("DEBUG: Se encontraron " + (citas != null ? citas.size() : 0) + " citas para el cliente ID: " + idCliente);
            if (citas != null) {
                for (ReservaCita c : citas) {
                    System.out.println("DEBUG: Cita -> id: " + c.getIdReserva() + ", fecha: " + c.getFecha() + ", idPerro: " + c.getIdPerro());
                }
            }

            if (citas != null && !citas.isEmpty()) {
                for (ReservaCita cita : citas) {
                    if (cita == null || cita.getFecha() == null) {
                        System.err.println("WARN: Se encontró una cita null o con fecha null en el historial para cliente ID: " + idCliente);
                        continue;
                    }

                    String nombreDelPerro = "[Perro no encontrado]";
                    if (cita.getIdPerro() > 0) {
                        try {
                            Perro perroDeLaCita = perroDao.obtenerPerroPorId(cita.getIdPerro());
                            if (perroDeLaCita != null && perroDeLaCita.getNombre() != null && !perroDeLaCita.getNombre().trim().isEmpty()) {
                                nombreDelPerro = perroDeLaCita.getNombre();
                            } else if (perroDeLaCita != null && (perroDeLaCita.getNombre() == null || perroDeLaCita.getNombre().trim().isEmpty())) {
                                nombreDelPerro = "[Nombre de perro no disponible (ID: " + cita.getIdPerro() + ")]";
                                System.err.println("WARN: Perro con ID " + cita.getIdPerro() + " encontrado pero no tiene nombre.");
                            } else {
                                System.err.println("WARN: No se encontró perro con ID: " + cita.getIdPerro() + " para la cita del " + cita.getFecha().format(FORMATO_FECHA_HISTORIAL));
                                nombreDelPerro = "[Perro ID: " + cita.getIdPerro() + " no encontrado]";
                            }
                        } catch (Exception e) {
                            System.err.println("ERROR: SQLException al obtener perro con ID " + cita.getIdPerro() + ": " + e.getMessage());
                            e.printStackTrace();
                            nombreDelPerro = "[Error al cargar datos del perro]";
                        }
                    } else {
                        nombreDelPerro = "[ID de perro no especificado en cita]";
                    }

                    String descripcionCita = String.format("%s - Cita con %s (%s)",
                            cita.getFecha().format(FORMATO_FECHA_HISTORIAL),
                            nombreDelPerro,
                            Objects.requireNonNullElse(cita.getEstadoCita(), "Estado desconocido").trim()
                    );
                    itemsHistorial.add(descripcionCita);
                }

                if (itemsHistorial.isEmpty()) {
                    listViewHistorial.setPlaceholder(new Label(PLACEHOLDER_HISTORIAL_VACIO));
                    if(!citas.isEmpty()){
                        System.out.println("INFO: Historial de citas procesado, pero la lista de visualización está vacía. Verifique los logs para WARN/ERROR.");
                    }
                } else {
                    listViewHistorial.setPlaceholder(null);
                }

            } else {
                listViewHistorial.setPlaceholder(new Label(PLACEHOLDER_HISTORIAL_VACIO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            listViewHistorial.setPlaceholder(new Label(PLACEHOLDER_HISTORIAL_ERROR));
            UtilidadesVentana.mostrarAlertaError("Error al Cargar Historial",
                    "No se pudo cargar el historial de citas debido a un error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            listViewHistorial.setPlaceholder(new Label(PLACEHOLDER_HISTORIAL_ERROR));
            UtilidadesVentana.mostrarAlertaError("Error Inesperado", "Ocurrió un error inesperado al cargar el historial: " + e.getMessage());
        }
    }

    /**
     * Actualiza la interfaz de usuario para reflejar un estado de error
     * (ej. datos no encontrados o error de base de datos).
     */
    private void actualizarUIConDatosDeError() {
        if (TxtNombre != null) TxtNombre.setText(TEXTO_ERROR_CARGA);
        if (TxtEmail != null) TxtEmail.setText(TEXTO_NO_DISPONIBLE);
        if (TxtTelefono != null) TxtTelefono.setText(TEXTO_NO_DISPONIBLE);
        if (TxtDireccion != null) TxtDireccion.setText(TEXTO_NO_DISPONIBLE);
        cargarImagenPlaceholder();
        if (listViewHistorial != null) {
            listViewHistorial.setItems(FXCollections.observableArrayList());
            listViewHistorial.setPlaceholder(new Label(PLACEHOLDER_HISTORIAL_NO_INFO));
        }
        if (BtnEditarDatos != null) BtnEditarDatos.setDisable(true);
    }

    /**
     * Carga la imagen de placeholder en el ImageView de la foto de perfil.
     */
    private void cargarImagenPlaceholder() {
        if (imgFotoPerfil == null) return;
        try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_FOTO_PERFIL)) {
            if (placeholderStream != null) {
                imgFotoPerfil.setImage(new Image(placeholderStream));
            } else {
                System.err.println("Error Crítico: Placeholder de foto de perfil no encontrado en: " + RUTA_PLACEHOLDER_FOTO_PERFIL);
                imgFotoPerfil.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Excepción crítica al cargar imagen placeholder de perfil: " + e.getMessage());
            e.printStackTrace();
            imgFotoPerfil.setImage(null);
        }
    }

    /**
     * Maneja el evento de clic en el botón "Editar Datos".
     * Navega al formulario de edición de usuario, pasando los datos del cliente actual
     * y su cuenta de usuario asociada.
     * @param event El evento de ratón (si el botón usa onMouseClicked) o ActionEvent (si usa onAction).
     */
    @FXML
    void IrAFormularioUsuario(MouseEvent event) {
        if (this.clienteActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Datos", "No hay datos de cliente cargados para editar. Por favor, recargue el perfil.");
            return;
        }
        if (this.idUsuarioDelPerfil <= 0 || this.usuarioDao == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Sistema", "No se puede obtener la información de la cuenta de usuario asociada. DAO no disponible o ID de usuario inválido.");
            return;
        }

        Usuario cuentaDelClienteAsociada;
        try {
            cuentaDelClienteAsociada = usuarioDao.obtenerUsuarioPorId(this.idUsuarioDelPerfil);
        } catch (SQLException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "No se pudo obtener la información de la cuenta de usuario para editar: " + e.getMessage());
            return;
        }

        if (cuentaDelClienteAsociada == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Cuenta", "No se encontró la cuenta de usuario asociada a este perfil de cliente. No se puede editar.");
            return;
        }

        System.out.println("INFO: Navegando a editar perfil para Cliente ID: " + clienteActual.getIdCliente() +
                ", Usuario ID: " + cuentaDelClienteAsociada.getIdUsuario());
        String tituloFormularioEdicion = "Editar Perfil de " + clienteActual.getNombre();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_FXML_FORMULARIO_USUARIO));
            Parent root = loader.load();
            root.getProperties().put("fxmlLocation", RUTA_FXML_FORMULARIO_USUARIO);

            FormularioUsuarioController formularioEdicionController = loader.getController();
            if (formularioEdicionController != null) {
                formularioEdicionController.initDataParaEdicion(this.clienteActual, cuentaDelClienteAsociada);
                UtilidadesVentana.cambiarEscenaConRoot(root, tituloFormularioEdicion, false);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Interno del Sistema", "No se pudo cargar el controlador del formulario de edición de perfil.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir el formulario de edición de perfil: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de clic en el icono "Volver".
     * Navega a la pantalla principal del cliente.
     * @param event El evento de ratón.
     */
    @FXML
    void handleVolver(MouseEvent event) {
        System.out.println("INFO: Volviendo a la pantalla principal del cliente desde PerfilUsuario.");
        UtilidadesVentana.cambiarEscena(RUTA_FXML_MAIN_CLIENTE, TITULO_VENTANA_MAIN_CLIENTE, false);
    }
}