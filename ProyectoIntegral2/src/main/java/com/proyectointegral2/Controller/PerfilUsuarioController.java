package com.proyectointegral2.Controller;

import com.proyectointegral2.dao.ClienteDao; // Asumiendo que existe y está en este paquete
import com.proyectointegral2.Model.Usuario;   // Asumiendo que existe y está en este paquete
import com.proyectointegral2.utils.UtilidadesVentana;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PerfilUsuarioController {

    @FXML private ImageView imgIconoVolver; // Para el botón de volver/salir
    @FXML private ImageView imgIconoUsuarioGrande;
    @FXML private Label TxtNombre;
    @FXML private Label TxtEmail;
    @FXML private Label TxtTelefono;
    @FXML private Label TxtDireccion;
    @FXML private Button BtnEditarDatos; // El fx:id en tu FXML es BtnEditarDatos, no btnEditarDatos
    @FXML private ImageView imgFotoPerfil;
    @FXML private ListView<String> listViewHistorial;
    @FXML private ImageView imgLogoDogpuccino;

    private ClienteDao clienteDAO; // Cambiado de UsuarioDao a ClienteDao
    private Usuario usuarioActual;
    private int idUsuarioLogueado; // Necesitarás una forma de establecer este ID

    private final String RUTA_PLACEHOLDER_PERFIL = "/assets/Imagenes/iconos/sinusuario.jpg";


    @FXML
    public void initialize() {
        //this.clienteDAO = new ClienteDao(); // Instanciar tu DAO específico para Clientes/Usuarios
        // La carga de datos se hará a través de initData()
        // cargarDatosEjemplo(); // Puedes llamar a esto si initData no se llama inmediatamente para tener algo visual
    }

    /**
     * Método para ser llamado desde el controlador anterior para pasar el ID del usuario.
     * Este método cargará los datos del perfil.
     * @param idUsuario El ID del usuario (cliente) cuyos detalles se van a mostrar.
     */
    public void initData(int idUsuario) {
        this.idUsuarioLogueado = idUsuario;
        // Si clienteDAO no se inicializa en initialize() o es específico por usuario, hazlo aquí.
        // this.clienteDAO = new ClienteDao();
        cargarDatosPerfil();
    }

    private void cargarDatosPerfil() {
//        if (clienteDAO == null) { // Comprobación si no se inicializó en constructor o initialize
//            this.clienteDAO = new ClienteDao();
//        }
//        if (idUsuarioLogueado <= 0) {
//            System.err.println("ID de usuario no válido para cargar perfil.");
//            UtilidadesVentana.mostrarAlertaError("Error de Perfil", "No se pudo identificar el usuario.");
//            cargarDatosEjemplo(); // Fallback
//            return;
//        }

        // --- LÓGICA DAO PARA OBTENER USUARIO (CLIENTE) ---
        // this.usuarioActual = clienteDAO.obtenerClientePorId(idUsuarioLogueado); // Asume que tienes un método así
        // -------------------------------------------------

        // SIMULACIÓN HASTA QUE EL DAO ESTÉ LISTO
//        if (this.usuarioActual == null) { // Si el DAO no lo pudo cargar o está comentado
//            System.out.println("ADVERTENCIA: No se pudo cargar usuario desde DAO o DAO no implementado. Usando datos de ejemplo para perfil.");
//            // Crear un usuario de ejemplo si no se cargó nada
//            this.usuarioActual = new Usuario(); // Necesitas un constructor en tu clase Usuario
//            this.usuarioActual.setNombre("Juan (Ejemplo)");
//            this.usuarioActual.setApellidos("Pérez (Ejemplo)");
//            this.usuarioActual.setEmail("juan.ejemplo@dao.com");
//            this.usuarioActual.setTelefono("+34 111222333");
//            this.usuarioActual.setDireccion("Calle Ficticia 456, Ciudad DAO");
//            this.usuarioActual.setRutaFotoPerfil(RUTA_PLACEHOLDER_PERFIL); // Usar placeholder
//        }
        // --- FIN SIMULACIÓN ---


//        if (usuarioActual != null) {
//            TxtNombre.setText(Objects.requireNonNullElse(usuarioActual.getNombre() + " " + usuarioActual.getApellidos(), "No disponible"));
//            TxtEmail.setText(Objects.requireNonNullElse(usuarioActual.getEmail(), "No disponible"));
//            TxtTelefono.setText(Objects.requireNonNullElse(usuarioActual.getTelefono(), "No disponible"));
//            TxtDireccion.setText(Objects.requireNonNullElse(usuarioActual.getDireccion(), "No disponible"));
//
//            String rutaImagen = usuarioActual.getRutaFotoPerfil();
//            if (rutaImagen != null && !rutaImagen.isEmpty()) {
//                try {
//                    InputStream stream = getClass().getResourceAsStream(rutaImagen);
//                    if (stream != null) {
//                        imgFotoPerfil.setImage(new Image(stream));
//                    } else {
//                        System.err.println("No se encontró la imagen de perfil en: " + rutaImagen);
//                        cargarImagenPlaceholder();
//                    }
//                } catch (Exception e) {
//                    System.err.println("Excepción al cargar la imagen de perfil del usuario: " + e.getMessage());
//                    cargarImagenPlaceholder();
//                }
//            } else {
//                cargarImagenPlaceholder();
//            }

            // --- LÓGICA DAO PARA OBTENER HISTORIAL ---
            // List<String> historial = clienteDAO.obtenerHistorialCitasEventos(idUsuarioLogueado); // Asume este método en ClienteDao
            // ----------------------------------------

            // SIMULACIÓN HISTORIAL
            List<String> historial = new ArrayList<>();
            historial.add("15/05/2024 - Cita con Rocky (Ejemplo DAO)");
            historial.add("01/05/2024 - Evento: Puertas Abiertas (Ejemplo DAO)");
            // --- FIN SIMULACIÓN HISTORIAL ---

//            if (historial != null && !historial.isEmpty()) {
//                listViewHistorial.setItems(FXCollections.observableArrayList(historial));
//            } else {
//                listViewHistorial.setPlaceholder(new Label("No hay historial de citas o eventos."));
//                listViewHistorial.getItems().clear(); // Asegurarse de que esté vacío
//            }
//
//        } else {
//            UtilidadesVentana.mostrarAlertaError("Error de Perfil", "No se encontró el usuario con ID: " + idUsuarioLogueado);
//            cargarDatosEjemplo(); // Fallback a datos de ejemplo si el usuario es nulo después de intentar cargar
//        }
    }

    private void cargarDatosEjemplo() { // Este método puede ser un fallback si el DAO falla
        TxtNombre.setText("Usuario Ejemplo");
        TxtEmail.setText("ejemplo@dominio.com");
        TxtTelefono.setText("+00 000 000 000");
        TxtDireccion.setText("Dirección de Ejemplo, 123");
        cargarImagenPlaceholder();

        ObservableList<String> historialItems = FXCollections.observableArrayList(
                "Dato de ejemplo 1 para historial",
                "Dato de ejemplo 2 para historial"
        );
        listViewHistorial.setItems(historialItems);
    }

    private void cargarImagenPlaceholder() {
        try {
            InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_PERFIL);
            if (placeholderStream != null) {
                imgFotoPerfil.setImage(new Image(placeholderStream));
            } else {
                System.err.println("No se pudo cargar la imagen placeholder por defecto desde: " + RUTA_PLACEHOLDER_PERFIL);
                imgFotoPerfil.setImage(null);
                imgFotoPerfil.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 8;");
            }
        } catch (Exception e) {
            System.err.println("Error crítico cargando imagen placeholder: " + e.getMessage());
        }
    }

    @FXML
    void IrAFormularioUsuario(MouseEvent event) {
        System.out.println("Botón Editar Datos Personales presionado.");
        String formularioUsuarioFxml = "/com/proyectointegral2/Vista/FormularioUsuario.fxml";
        String titulo = "Editar Perfil de Usuario";

        // ANTES de cambiar de escena, necesitas obtener el controlador del formulario
        // para pasarle el objeto 'usuarioActual' o su 'idUsuarioLogueado'.
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formularioUsuarioFxml));
            Parent root = loader.load();

            FormularioUsuarioController formularioController = loader.getController();
            if (usuarioActual != null) { // Preferible pasar el objeto si ya lo tienes
                formularioController.initDataParaEdicion(usuarioActual);
            } else if (idUsuarioLogueado > 0) { // Como fallback, si solo tienes el ID
                // Necesitarías un initData(int id) en FormularioUsuarioController
                // o cargar el usuario aquí y luego pasarlo. Por ahora, asumimos que tienes usuarioActual.
                // Si no, FormularioUsuarioController necesitaría cargar el usuario por ID.
                UtilidadesVentana.mostrarAlertaError("Error Edición", "No hay datos de usuario para editar.");
                return;
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Edición", "No se pudo identificar el usuario para editar.");
                return;
            }

            // Ahora que el controlador del formulario tiene los datos, cambia la escena.
            // Usamos un nuevo método en UtilidadesVentana para pasar el root ya cargado.
            UtilidadesVentana.cambiarEscenaConRoot(root, titulo, false); // 'false' para tamaño fijo

        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir el formulario de edición.");
        }
    }

    @FXML
    void handleVolver(MouseEvent event) {
        System.out.println("Icono Volver presionado en PerfilUsuario. Navegando a MainCliente...");
        // Asume que la pantalla principal del cliente es MainCliente.fxml y es dinámica
        String mainClienteFxml = "/com/proyectointegral2/Vista/MainCliente.fxml"; // VERIFICA ESTA RUTA
        String titulo = "Panel Principal Cliente";

        // true porque MainCliente es dinámica (pantalla completa/redimensionable)
        UtilidadesVentana.cambiarEscena(mainClienteFxml, titulo, true);
    }

    // Placeholder para un método que obtendría el ID del usuario logueado.
    // En una aplicación real, esto vendría de un gestor de sesión o se pasaría al controlador.
    // private int obtenerIdUsuarioLogueadoDesdeSesion() {
    //     // Lógica para obtener el ID del usuario actual.
    //     // Por ahora, devolvemos un ID de ejemplo.
    //     System.out.println("ADVERTENCIA: Usando ID de usuario logueado de ejemplo (1). Implementar lógica real.");
    //     return 1; // EJEMPLO
    // }
}