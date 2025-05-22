package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Usuario; // Para el objeto que se pasa a FormularioUsuarioController
import com.proyectointegral2.Model.ReservaCita;
import com.proyectointegral2.dao.ClienteDao;
import com.proyectointegral2.dao.ReservaCitaDao; // DAO para el historial de citas
import com.proyectointegral2.dao.UsuarioDao;
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class PerfilUsuarioController {

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

    private ClienteDao clienteDAO;
    private ReservaCitaDao reservaCitaDao;
    private Cliente clienteActual;
    private int idUsuarioDelPerfil;

    private final String RUTA_PLACEHOLDER_PERFIL = "/assets/Imagenes/iconos/sinusuario.jpg";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    @FXML
    public void initialize() {
        this.clienteDAO = new ClienteDao();
        this.reservaCitaDao = new ReservaCitaDao();
        limpiarYMostrarCargando();
    }

    public void initData(int idUsuario, String nombreUsuarioLogin) {
        this.idUsuarioDelPerfil = idUsuario;
        System.out.println("PerfilUsuarioController: initData para Usuario ID: " + idUsuario);
        cargarDatosDelCliente();
    }

    private void limpiarYMostrarCargando() {
        TxtNombre.setText("Cargando...");
        TxtEmail.setText("...");
        TxtTelefono.setText("...");
        TxtDireccion.setText("...");
        listViewHistorial.getItems().clear();
        listViewHistorial.setPlaceholder(new Label("Cargando historial..."));
        cargarImagenPlaceholder();
        BtnEditarDatos.setDisable(true);
    }

    private void cargarDatosDelCliente() {
        if (idUsuarioDelPerfil <= 0) {
            UtilidadesVentana.mostrarAlertaError("Error de Perfil", "ID de usuario no válido.");
            mostrarDatosDeErrorEnUI();
            return;
        }

        try {
            this.clienteActual = clienteDAO.obtenerClientePorIdUsuario(idUsuarioDelPerfil);

            if (clienteActual != null) {
                TxtNombre.setText(
                        Objects.requireNonNullElse(clienteActual.getNombre(), "").trim() + " " +
                                Objects.requireNonNullElse(clienteActual.getApellidos(), "").trim()
                );
                TxtEmail.setText(Objects.requireNonNullElse(clienteActual.getEmail(), "No disponible"));
                TxtTelefono.setText(Objects.requireNonNullElse(clienteActual.getTelefono(), "No disponible"));

                String direccionCompleta = construirDireccion(clienteActual);
                TxtDireccion.setText(direccionCompleta.isEmpty() ? "No disponible" : direccionCompleta);

                cargarFotoDePerfil(clienteActual.getRutaFotoPerfil());
                cargarHistorialCitasDelCliente(clienteActual.getIdCliente());

                BtnEditarDatos.setDisable(false);
            } else {
                UtilidadesVentana.mostrarAlertaError("Perfil no Encontrado", "No se encontró perfil para el usuario ID: " + idUsuarioDelPerfil);
                mostrarDatosDeErrorEnUI();
            }
        } catch (SQLException e) {
            UtilidadesVentana.mostrarAlertaError("Error de Base de Datos", "No se pudieron cargar los datos del perfil: " + e.getMessage());
            e.printStackTrace();
            mostrarDatosDeErrorEnUI();
        } catch (Exception e) {
            UtilidadesVentana.mostrarAlertaError("Error Inesperado", "Ocurrió un error al cargar el perfil: " + e.getMessage());
            e.printStackTrace();
            mostrarDatosDeErrorEnUI();
        }
    }

    private String construirDireccion(Cliente cliente) {
        List<String> partes = new ArrayList<>();
        if (cliente.getCalle() != null && !cliente.getCalle().trim().isEmpty()) partes.add(cliente.getCalle().trim());
        if (cliente.getCiudad() != null && !cliente.getCiudad().trim().isEmpty()) partes.add(cliente.getCiudad().trim());
        if (cliente.getProvincia() != null && !cliente.getProvincia().trim().isEmpty()) partes.add(cliente.getProvincia().trim());
        if (cliente.getCodigoPostal() != null && !cliente.getCodigoPostal().trim().isEmpty()) partes.add(cliente.getCodigoPostal().trim());
        return String.join(", ", partes);
    }

    private void cargarFotoDePerfil(String rutaImagenRelativaAlClasspath) {
        if (rutaImagenRelativaAlClasspath != null && !rutaImagenRelativaAlClasspath.trim().isEmpty()) {
            String pathCorregido = rutaImagenRelativaAlClasspath;
            if (!pathCorregido.startsWith("/")) { pathCorregido = "\\" + pathCorregido; }
            try (InputStream stream = getClass().getResourceAsStream(pathCorregido)) {
                if (stream != null) {
                    imgFotoPerfil.setImage(new Image(stream));
                    return;
                } else {
                    System.err.println("WARN: Imagen de perfil no encontrada en classpath: " + pathCorregido);
                }
            } catch (Exception e) {
                System.err.println("Excepción al cargar la imagen de perfil desde '" + pathCorregido + "': " + e.getMessage());
            }
        }
        cargarImagenPlaceholder();
    }

    private void cargarHistorialCitasDelCliente(int idCliente) {
        if (idCliente <= 0) {
            listViewHistorial.setPlaceholder(new Label("No se pudo identificar el cliente para el historial."));
            return;
        }
        try {
            List<ReservaCita> citas = reservaCitaDao.obtenerReservasPorCliente(idCliente);
            ObservableList<String> historialItems = FXCollections.observableArrayList();
            if (citas != null && !citas.isEmpty()) {
                for (ReservaCita rc : citas) {

                    String perroInfo = "Perro ID: " + rc.getIdPerro() + " - " +
                            (rc.getEstadoCita().equals("CANCELADA") ? "Cita Cancelada" : "Cita Programada");

                    historialItems.add(rc.getFecha().format(dateFormatter) + " - " + perroInfo);
                }
                listViewHistorial.setItems(historialItems);
                listViewHistorial.setPlaceholder(null);
            } else {
                listViewHistorial.getItems().clear();
                listViewHistorial.setPlaceholder(new Label("No hay historial de citas o eventos."));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            listViewHistorial.getItems().clear();
            listViewHistorial.setPlaceholder(new Label("Error al cargar historial de citas."));
            UtilidadesVentana.mostrarAlertaError("Error Historial", "No se pudo cargar el historial: " + e.getMessage());
        }
    }

    private void mostrarDatosDeErrorEnUI() {
        TxtNombre.setText("Error al cargar datos");
        TxtEmail.setText("No disponible");
        TxtTelefono.setText("No disponible");
        TxtDireccion.setText("No disponible");
        cargarImagenPlaceholder();
        listViewHistorial.setItems(FXCollections.observableArrayList());
        listViewHistorial.setPlaceholder(new Label("No hay información disponible."));
        BtnEditarDatos.setDisable(true);
    }

    private void cargarImagenPlaceholder() {
        try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_PERFIL)) {
            if (placeholderStream != null) {
                imgFotoPerfil.setImage(new Image(placeholderStream));
            } else {
                System.err.println("Error Crítico: Placeholder de perfil no encontrado: " + RUTA_PLACEHOLDER_PERFIL);
                imgFotoPerfil.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Excepción crítica cargando placeholder de perfil: " + e.getMessage());
        }
    }

    @FXML
    void IrAFormularioUsuario(MouseEvent event) {
        if (clienteActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error", "No hay datos de cliente cargados para editar.");
            return;
        }
        Usuario cuentaDelCliente = null;
        if (this.idUsuarioDelPerfil > 0) {
            UsuarioDao tempUsuarioDao = new UsuarioDao();
            try {
                cuentaDelCliente = tempUsuarioDao.obtenerUsuarioPorId(this.idUsuarioDelPerfil);
            } catch (SQLException e) {
                e.printStackTrace();
                UtilidadesVentana.mostrarAlertaError("Error DB", "No se pudo obtener la cuenta de usuario para editar.");
                return;
            }
        }
        if (cuentaDelCliente == null) {
            UtilidadesVentana.mostrarAlertaError("Error", "No se pudo obtener la información de la cuenta para editar.");
            return;
        }

        System.out.println("Botón Editar Datos Personales presionado para Cliente ID: " + clienteActual.getIdCliente());
        String formularioUsuarioFxml = "/com/proyectointegral2/Vista/FormularioUsuario.fxml";
        String titulo = "Editar Perfil de " + clienteActual.getNombre();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formularioUsuarioFxml));
            Parent root = loader.load();
            root.getProperties().put("fxmlLocation", formularioUsuarioFxml);
            FormularioUsuarioController formularioController = loader.getController();

            if (formularioController != null) {
                formularioController.initDataParaEdicion(this.clienteActual, cuentaDelCliente);
                UtilidadesVentana.cambiarEscenaConRoot(root, titulo, false);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Interno", "No se pudo abrir el formulario de edición.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir el formulario de edición: " + e.getMessage());
        }
    }

    @FXML
    void handleVolver(MouseEvent event) {
        System.out.println("Icono Volver presionado en PerfilUsuario.");
        String MainFxml = "/com/proyectointegral2/Vista/Main.fxml";
        String MainTitle = "Inicio de Sesión - Dogpuccino";
        UtilidadesVentana.cambiarEscena(MainFxml, MainTitle, false);
    }
}