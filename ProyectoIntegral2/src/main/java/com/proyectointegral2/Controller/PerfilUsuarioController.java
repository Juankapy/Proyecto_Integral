package com.proyectointegral2.Controller;

// Ya no necesitamos importar Usuario si no lo usamos para los datos del perfil aquí
// import com.proyectointegral2.Model.Usuario;
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

    @FXML private ImageView imgIconoVolver;
    @FXML private ImageView imgIconoUsuarioGrande;
    @FXML private Label TxtNombre; // Mostrará nombre + apellidos simulados
    @FXML private Label TxtEmail;  // Mostrará email simulado
    @FXML private Label TxtTelefono; // Mostrará teléfono simulado
    @FXML private Label TxtDireccion; // Mostrará dirección simulada
    @FXML private Button BtnEditarDatos;
    @FXML private ImageView imgFotoPerfil;
    @FXML private ListView<String> listViewHistorial;
    @FXML private ImageView imgLogoDogpuccino;

    // Variables para almacenar los datos de perfil simulados
    // Estos NO vienen del objeto Usuario (que solo tiene id, NombreUsuario, Contrasena)
    private String perfilNombreSimulado;
    private String perfilApellidosSimulados;
    private String perfilEmailSimulado;
    private String perfilTelefonoSimulado;
    private String perfilDireccionSimulada;
    private String perfilRutaFotoSimulada;

    private int idUsuarioAutenticado; // ID del usuario que inició sesión
    private String nombreUsuarioAutenticado; // Nombre de usuario del login

    private final String RUTA_PLACEHOLDER_PERFIL = "/assets/Imagenes/iconos/sinusuario.jpg";

    @FXML
    public void initialize() {
        System.out.println("PerfilUsuarioController inicializado. Esperando datos del usuario (simulados)...");
        cargarImagenPlaceholder();
        listViewHistorial.setPlaceholder(new Label("Cargando historial..."));
    }

    /**
     * Método para ser llamado DESPUÉS de cargar este FXML.
     * Recibe el ID y el NombreUsuario del usuario autenticado.
     * Los datos de perfil adicionales se simularán basados en el ID.
     *
     * @param idUsuario El ID del usuario autenticado.
     * @param nombreUsuarioLogin El NombreUsuario del login.
     */
    public void initData(int idUsuario, String nombreUsuarioLogin) {
        if (idUsuario <= 0) {
            System.err.println("Error: ID de usuario inválido recibido en initData: " + idUsuario);
            UtilidadesVentana.mostrarAlertaError("Error de Perfil", "No se pudo identificar el usuario para cargar el perfil.");
            cargarDatosDeEjemploPorDefecto();
            return;
        }
        this.idUsuarioAutenticado = idUsuario;
        this.nombreUsuarioAutenticado = nombreUsuarioLogin; // Guardamos el nombre de usuario del login
        System.out.println("initData llamado para Usuario ID: " + this.idUsuarioAutenticado + ", NombreUsuario: " + this.nombreUsuarioAutenticado);
        cargarDatosDelPerfilSimulado();
    }

    private void cargarDatosDelPerfilSimulado() {
        System.out.println("Cargando datos de perfil simulados para Usuario ID: " + idUsuarioAutenticado);

        // Simulación de datos de perfil basada en idUsuarioAutenticado
        // Estos datos son independientes del objeto Usuario de login
        if (idUsuarioAutenticado == 1) { // Asumamos que este ID corresponde a "ana_g"
            perfilNombreSimulado = "Ana";
            perfilApellidosSimulados = "García López";
            perfilEmailSimulado = "ana.garcia.sim@example.com"; // Puede ser diferente al nombreUsuario de login
            perfilTelefonoSimulado = "600111222 (Sim)";
            perfilDireccionSimulada = "Calle Sol Simulada, 1, Madrid";
            perfilRutaFotoSimulada = "/assets/Imagenes/usuarios/ana_perfil_sim.png";
        } else if (idUsuarioAutenticado == 2) { // Asumamos que este ID corresponde a "carlos_m"
            perfilNombreSimulado = "Carlos";
            perfilApellidosSimulados = "Martínez Ruiz";
            perfilEmailSimulado = "carlos.m.sim@example.net";
            perfilTelefonoSimulado = "600333444 (Sim)";
            perfilDireccionSimulada = "Avenida Luna Simulada, 20, Barcelona";
            perfilRutaFotoSimulada = null; // Para probar el placeholder
        } else {
            perfilNombreSimulado = "Usuario (" + Objects.requireNonNullElse(nombreUsuarioAutenticado, "ID: "+idUsuarioAutenticado) + ")";
            perfilApellidosSimulados = "Simulado";
            perfilEmailSimulado = "perfil.simulado@example.com";
            perfilTelefonoSimulado = "N/A (Sim)";
            perfilDireccionSimulada = "Dirección Simulada";
            perfilRutaFotoSimulada = RUTA_PLACEHOLDER_PERFIL;
        }

        // Mostrar datos de perfil simulados
        TxtNombre.setText(perfilNombreSimulado + " " + perfilApellidosSimulados);
        TxtEmail.setText(perfilEmailSimulado);
        TxtTelefono.setText(perfilTelefonoSimulado);
        TxtDireccion.setText(perfilDireccionSimulada);

        if (perfilRutaFotoSimulada != null && !perfilRutaFotoSimulada.trim().isEmpty()) {
            try {
                String rutaCorregida = perfilRutaFotoSimulada.startsWith("/") ? perfilRutaFotoSimulada : "/" + perfilRutaFotoSimulada;
                InputStream stream = getClass().getResourceAsStream(rutaCorregida);
                if (stream != null) {
                    imgFotoPerfil.setImage(new Image(stream));
                    if (imgIconoUsuarioGrande != null) imgIconoUsuarioGrande.setImage(imgFotoPerfil.getImage());
                } else {
                    System.err.println("WARN (Sim): No se encontró la imagen de perfil en: " + rutaCorregida + ". Usando placeholder.");
                    cargarImagenPlaceholder();
                }
            } catch (Exception e) {
                System.err.println("ERROR (Sim): Excepción al cargar la imagen de perfil (" + perfilRutaFotoSimulada + "): " + e.getMessage());
                cargarImagenPlaceholder();
            }
        } else {
            System.out.println("INFO (Sim): Ruta de imagen de perfil no especificada. Usando placeholder.");
            cargarImagenPlaceholder();
        }

        // Simulación Historial
        List<String> historialSimulado = new ArrayList<>();
        historialSimulado.add("20/05/2024 - Cita con 'Bobby' (Simulado para " + nombreUsuarioAutenticado + ")");
        historialSimulado.add("10/05/2024 - Evento: 'Feria de Adopción' (Simulado para " + nombreUsuarioAutenticado + ")");
        // ... (más lógica de historial si es necesario) ...

        if (!historialSimulado.isEmpty()) {
            listViewHistorial.setItems(FXCollections.observableArrayList(historialSimulado));
        } else {
            listViewHistorial.setPlaceholder(new Label("No hay historial de citas o eventos (simulado)."));
            listViewHistorial.getItems().clear();
        }
    }

    private void cargarDatosDeEjemploPorDefecto() {
        // Datos para cuando no se puede cargar un perfil específico
        TxtNombre.setText("Usuario Ejemplo Fallback");
        TxtEmail.setText("ejemplo@fallback.com");
        TxtTelefono.setText("+00 000 000 000");
        TxtDireccion.setText("Dirección Fallback, 123");
        cargarImagenPlaceholder();

        ObservableList<String> historialItems = FXCollections.observableArrayList(
                "Historial de ejemplo 1 (Fallback)",
                "Historial de ejemplo 2 (Fallback)"
        );
        listViewHistorial.setItems(historialItems);
        listViewHistorial.setPlaceholder(new Label("No hay historial disponible."));
    }

    private void cargarImagenPlaceholder() {
        try {
            InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_PERFIL);
            if (placeholderStream != null) {
                Image placeholderImage = new Image(placeholderStream);
                imgFotoPerfil.setImage(placeholderImage);
                if (imgIconoUsuarioGrande != null) imgIconoUsuarioGrande.setImage(placeholderImage);
            } else {
                System.err.println("ERROR CRÍTICO: No se pudo cargar la imagen placeholder por defecto desde: " + RUTA_PLACEHOLDER_PERFIL);
                imgFotoPerfil.setImage(null);
                if (imgIconoUsuarioGrande != null) imgIconoUsuarioGrande.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Error crítico cargando imagen placeholder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void IrAFormularioUsuario(MouseEvent event) {
        System.out.println("Botón Editar Datos Personales presionado (simulación).");
        if (idUsuarioAutenticado <= 0 && (perfilNombreSimulado == null || perfilNombreSimulado.equals("Usuario Ejemplo Fallback"))) {
            UtilidadesVentana.mostrarAlertaError("Error de Edición", "No hay datos de usuario cargados para editar.");
            return;
        }

        String formularioUsuarioFxml = "/com/proyectointegral2/Vista/FormularioUsuario.fxml";
        String titulo = "Editar Perfil de Usuario";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formularioUsuarioFxml));
            Parent root = loader.load();

            FormularioUsuarioController formularioController = loader.getController();
            if (formularioController != null) {
                // Pasar los datos simulados al controlador del formulario.
                // FormularioUsuarioController necesitará un método para aceptar estos datos.
                // Ejemplo: formularioController.initDataParaEdicionSimulada(
                // idUsuarioAutenticado, nombreUsuarioAutenticado, perfilNombreSimulado, perfilApellidosSimulados,
                // perfilEmailSimulado, perfilTelefonoSimulado, perfilDireccionSimulada, perfilRutaFotoSimulada);

                // O, si FormularioUsuarioController va a usar tu clase Usuario original (solo con id, NombreUsuario, Contrasena)
                // Y el formulario de edición solo permite cambiar, por ejemplo, NombreUsuario y Contrasena:
                com.proyectointegral2.Model.Usuario usuarioDeLogin = new com.proyectointegral2.Model.Usuario(
                        idUsuarioAutenticado,
                        nombreUsuarioAutenticado,
                        "" // Contraseña actual no se pasa o se maneja de forma segura
                );
                // Y FormularioUsuarioController tendría un método:
                // formularioController.initDataParaEdicionLogin(usuarioDeLogin);
                // Y el formulario de edición manejaría los campos de perfil adicionales internamente o los obtendría de otra forma.

                // Por ahora, como simplificación, supongamos que el FormularioUsuarioController
                // puede tomar los datos de perfil simulados (tendrás que crear ese método en FormularioUsuarioController):
                System.out.println("Pasando datos simulados al formulario de edición...");
                // formularioController.initDataParaEdicionConDatosSimulados(idUsuarioAutenticado, perfilNombreSimulado, perfilApellidosSimulados, ...etc );


                // ----- INICIO BLOQUE MODIFICADO PARA PASAR DATOS SIMULADOS -----
                // Asumimos que FormularioUsuarioController tiene un método para tomar datos básicos
                // y que los campos de perfil se cargarán/editarán allí.
                // Por ahora, vamos a pasar el ID y el nombre de usuario de login.
                // Y el FormularioUsuarioController deberá simular la carga del resto de datos de perfil para edición.
                if (idUsuarioAutenticado > 0) {
                    // Necesitas un método en FormularioUsuarioController como:
                    // public void initDataParaEdicion(int idUsuario, String nombreUsuarioLogin)
                    // O si tu FormularioUsuarioController tiene un método que acepta un objeto Usuario (el de login)
                    com.proyectointegral2.Model.Usuario usuarioLogin = new com.proyectointegral2.Model.Usuario(idUsuarioAutenticado, nombreUsuarioAutenticado, "contraseña_actual_no_visible");
                    // Y en FormularioUsuarioController: public void initDataParaEdicion(Usuario usuarioLogin)
                    // Este es un ejemplo, ajusta según tu FormularioUsuarioController
                    // formularioController.initDataConUsuarioLogin(usuarioLogin);

                    UtilidadesVentana.mostrarAlertaInformacion("Edición Simulada",
                            "Abriendo formulario de edición para el usuario (simulado) con ID: " + idUsuarioAutenticado +
                                    "\nNombre de Login: " + nombreUsuarioAutenticado +
                                    "\nNombre Perfil: " + perfilNombreSimulado + " " + perfilApellidosSimulados +
                                    "\nEmail Perfil: " + perfilEmailSimulado +
                                    "\nTeléfono Perfil: " + perfilTelefonoSimulado +
                                    "\nDirección Perfil: " + perfilDireccionSimulada
                    );
                    // Por ahora, no pasamos datos explícitamente, el FormularioUsuarioController deberá tener su propia simulación
                    // si necesita mostrar datos para editar.
                } else {
                    UtilidadesVentana.mostrarAlertaError("Error Edición", "ID de usuario no válido para edición.");
                    return;
                }
                // ----- FIN BLOQUE MODIFICADO -----

            } else {
                System.err.println("Error: No se pudo obtener el controlador para " + formularioUsuarioFxml);
                UtilidadesVentana.mostrarAlertaError("Error de Navegación", "Fallo al preparar el formulario de edición.");
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir el formulario de edición.");
        }
    }

    @FXML
    void Volver(MouseEvent event) {
        System.out.println("Icono Volver presionado en PerfilUsuario (simulación).");
        String pantallaPrincipalFxml = "/com/proyectointegral2/Vista/Main.fxml";
        String titulo = "Panel Principal";
        System.out.println("Navegando a: " + pantallaPrincipalFxml);
        UtilidadesVentana.cambiarEscena(pantallaPrincipalFxml, titulo, true);
    }


    // Este es el método que llamarías desde el controlador anterior
    // para pasar el ID y el NombreUsuario (del login)
    //
    // Ejemplo en el controlador anterior (ej. MainClienteController):
    /*
    @FXML
    void irAPerfilDeUsuario(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyectointegral2/Vista/PerfilUsuarioView.fxml"));
            Parent root = loader.load();

            PerfilUsuarioController perfilController = loader.getController();
            if (perfilController != null) {
                // Asume que tienes estos datos del usuario que ha iniciado sesión
                int idDelUsuarioLogueado = obtenerIdActualDelUsuario(); // Implementa esto
                String nombreUsuarioDelLogin = obtenerNombreUsuarioActualDelLogin(); // Implementa esto

                if (idDelUsuarioLogueado > 0 && nombreUsuarioDelLogin != null) {
                    perfilController.initData(idDelUsuarioLogueado, nombreUsuarioDelLogin); // <--- PASAR DATOS
                    UtilidadesVentana.cambiarEscenaConRoot(root, "Mi Perfil", false); // false para tamaño fijo
                } else {
                     UtilidadesVentana.mostrarAlertaError("Error de Sesión", "No se pudo identificar el usuario actual.");
                }
            } // ... else manejo de error
        } catch (IOException e) { // ... manejo de error }
    }

    private int obtenerIdActualDelUsuario() { return 1; // EJEMPLO }
    private String obtenerNombreUsuarioActualDelLogin() { return "ana_g"; // EJEMPLO }
    */
}