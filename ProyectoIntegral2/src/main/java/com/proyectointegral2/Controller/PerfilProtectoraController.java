package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.RedSocial;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ProtectoraDao;
import com.proyectointegral2.dao.RedesSocialesDao;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controlador para la pantalla de perfil de una Protectora.
 * Muestra la información de la protectora, su logo o foto (si existe),
 * y una lista de sus redes sociales registradas.
 * Permite al usuario navegar a un formulario para editar los datos de la protectora.
 */
public class PerfilProtectoraController {

    @FXML private ImageView imgIconoVolver;
    @FXML private ImageView imgIconoProtectoraGrande;
    @FXML private Label lblNombreProtectoraTitulo;
    @FXML private Label TxtNombreProtectora;
    @FXML private Label TxtCIF;
    @FXML private Label TxtEmailProtectora;
    @FXML private Label TxtTelefonoProtectora;
    @FXML private Label TxtDireccionProtectora;
    @FXML private Button BtnEditarDatosProtectora;
    @FXML private ImageView imgLogoOFotoProtectora;
    @FXML private ListView<String> listViewRedesSociales;
    @FXML private ImageView imgLogoDogpuccino;

    private RedesSocialesDao redesSocialesDao;
     private ProtectoraDao protectoraDao;

    private Protectora protectoraActual;
    private Usuario cuentaUsuarioAsociada;

    private static final String RUTA_PLACEHOLDER_LOGO_PROTECTORA = "/assets/Imagenes/iconos/sinusuario.jpg";
    private static final String RUTA_FXML_FORMULARIO_PROTECTORA = "/com/proyectointegral2/Vista/FormularioProtectora.fxml";
    private static final String RUTA_FXML_MAIN_PROTECTORA = "/com/proyectointegral2/Vista/MainProtectora.fxml";
    private static final String TITULO_VENTANA_MAIN_PROTECTORA = "Panel de Protectora - Dogpuccino";

    private static final String TEXTO_CARGANDO = "Cargando...";
    private static final String TEXTO_NO_DISPONIBLE = "No disponible";
    private static final String TEXTO_ERROR_CARGA = "Error al cargar datos";
    private static final String PLACEHOLDER_REDES_CARGANDO = "Cargando redes sociales...";
    private static final String PLACEHOLDER_REDES_VACIO = "No hay redes sociales registradas.";
    private static final String PLACEHOLDER_REDES_ERROR = "Error al cargar redes sociales.";
    private static final String PLACEHOLDER_REDES_NO_INFO = "No hay información de redes disponible.";


    /**
     * Método de inicialización del controlador. Se llama automáticamente después de que
     * los campos FXML han sido inyectados.
     * Inicializa los DAOs necesarios y configura la UI en un estado de carga inicial.
     */
    @FXML
    public void initialize() {
        try {
            this.redesSocialesDao = new RedesSocialesDao();
            this.protectoraDao = new ProtectoraDao();
        } catch (Exception e) {
            System.err.println("Error crítico al inicializar DAOs en PerfilProtectoraController: " + e.getMessage());
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error Crítico de Sistema",
                    "No se pudo inicializar el acceso a la base de datos. El perfil no se cargará correctamente.");
        }
        configurarUIEnEstadoCarga();
    }

    /**
     * Inicializa el controlador con los datos de la Protectora y su cuenta de Usuario asociada.
     * Este método debe ser llamado desde el controlador que navega a esta pantalla de perfil.
     * @param protectora El objeto {@link Protectora} con los datos a mostrar.
     * @param cuentaUsuario El objeto {@link Usuario} asociado a esta protectora (para la edición).
     */
    public void initData(Protectora protectora, Usuario cuentaUsuario) {
        this.protectoraActual = protectora;
        this.cuentaUsuarioAsociada = cuentaUsuario;

        if (this.protectoraActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Datos", "No se recibió información de la protectora para mostrar el perfil. El formulario se cerrará.");
            actualizarUIConDatosDeError();
            return;
        }
        if (this.cuentaUsuarioAsociada == null) {
            System.err.println("WARN: No se recibió la cuenta de Usuario asociada a la protectora. La edición podría no funcionar completamente.");
            UtilidadesVentana.mostrarAlertaAdvertencia("Datos Incompletos","Datos Incompletos", "No se recibió la información completa de la cuenta de usuario. Algunas funciones de edición podrían estar limitadas.");
        }

        System.out.println("PerfilProtectoraController: Mostrando datos para Protectora ID: " +
                protectoraActual.getIdProtectora() + ", Nombre: " + protectoraActual.getNombre());
        poblarDatosProtectoraEnUI();
    }

    /**
     * Configura la interfaz de usuario para mostrar un estado de "cargando"
     * mientras se recuperan o procesan los datos del perfil.
     */
    private void configurarUIEnEstadoCarga() {
        if (lblNombreProtectoraTitulo != null) lblNombreProtectoraTitulo.setText("Perfil de Protectora");
        if (TxtNombreProtectora != null) TxtNombreProtectora.setText(TEXTO_CARGANDO);
        if (TxtCIF != null) TxtCIF.setText("...");
        if (TxtEmailProtectora != null) TxtEmailProtectora.setText("...");
        if (TxtTelefonoProtectora != null) TxtTelefonoProtectora.setText("...");
        if (TxtDireccionProtectora != null) TxtDireccionProtectora.setText("...");
        if (listViewRedesSociales != null) {
            listViewRedesSociales.getItems().clear();
            listViewRedesSociales.setPlaceholder(new Label(PLACEHOLDER_REDES_CARGANDO));
        }
        cargarImagenPlaceholder();
        if (BtnEditarDatosProtectora != null) BtnEditarDatosProtectora.setDisable(true);
    }

    /**
     * Puebla los campos de la interfaz de usuario con los datos de la `protectoraActual`.
     * Si `protectoraActual` es null, actualiza la UI para mostrar un estado de error.
     */
    private void poblarDatosProtectoraEnUI() {
        if (protectoraActual == null) {
            actualizarUIConDatosDeError();
            return;
        }

        String nombreProt = Objects.requireNonNullElse(protectoraActual.getNombre(), "Protectora sin nombre");
        if (lblNombreProtectoraTitulo != null) lblNombreProtectoraTitulo.setText("Perfil de " + nombreProt);
        if (TxtNombreProtectora != null) TxtNombreProtectora.setText(nombreProt);
        if (TxtCIF != null) TxtCIF.setText(Objects.requireNonNullElse(protectoraActual.getCif(), TEXTO_NO_DISPONIBLE));
        if (TxtEmailProtectora != null) TxtEmailProtectora.setText(Objects.requireNonNullElse(protectoraActual.getEmail(), TEXTO_NO_DISPONIBLE));
        if (TxtTelefonoProtectora != null) TxtTelefonoProtectora.setText(Objects.requireNonNullElse(protectoraActual.getTelefono(), TEXTO_NO_DISPONIBLE));

        if (TxtDireccionProtectora != null) {
            String direccionCompleta = construirDireccionFormateada(protectoraActual);
            TxtDireccionProtectora.setText(direccionCompleta.isEmpty() ? TEXTO_NO_DISPONIBLE : direccionCompleta);
        }

        cargarLogoOImagenProtectora(protectoraActual.getRutaFotoPerfil());
        cargarRedesSociales(protectoraActual.getIdProtectora());

        if (BtnEditarDatosProtectora != null) BtnEditarDatosProtectora.setDisable(false);
    }

    /**
     * Construye una cadena de dirección formateada a partir de las partes de la dirección de la protectora.
     * @param protectora El objeto Protectora.
     * @return Una cadena con la dirección formateada.
     */
    private String construirDireccionFormateada(Protectora protectora) {
        if (protectora == null) return "";
        List<String> partesDireccion = new ArrayList<>();
        if (protectora.getCalle() != null && !protectora.getCalle().trim().isEmpty()) partesDireccion.add(protectora.getCalle().trim());
        if (protectora.getCiudad() != null && !protectora.getCiudad().trim().isEmpty()) partesDireccion.add(protectora.getCiudad().trim());
        if (protectora.getProvincia() != null && !protectora.getProvincia().trim().isEmpty()) partesDireccion.add(protectora.getProvincia().trim());
        if (protectora.getCodigoPostal() != null && !protectora.getCodigoPostal().trim().isEmpty()) partesDireccion.add(protectora.getCodigoPostal().trim());
        return String.join(", ", partesDireccion);
    }

    /**
     * Carga el logo o foto de la protectora en el ImageView.
     * Si no hay imagen o hay un error, carga una imagen placeholder.
     * @param rutaImagenRelativaAlClasspath La ruta de la imagen, DEBE comenzar con "/"
     *                                     si es desde la raíz de 'resources'
     *                                     (ej: "/assets/Imagenes/Protectoras/logo.png").
     */
    private void cargarLogoOImagenProtectora(String rutaImagenRelativaAlClasspath) {
        if (imgLogoOFotoProtectora == null) {
            System.err.println("ERROR: ImageViews para logo/foto de protectora no inyectados.");
            return;
        }

        Image imagenParaMostrar = null;
        String pathIntentarCargar = null;

        if (rutaImagenRelativaAlClasspath != null && !rutaImagenRelativaAlClasspath.trim().isEmpty()) {
            pathIntentarCargar = rutaImagenRelativaAlClasspath.trim().replace("\\", "/");
            if (!pathIntentarCargar.startsWith("/")) {
                pathIntentarCargar = "/" + pathIntentarCargar;
            }

            System.out.println("PerfilProtectora: Intentando cargar imagen desde classpath: " + pathIntentarCargar);
            try (InputStream stream = getClass().getResourceAsStream(pathIntentarCargar)) {
                if (stream != null) {
                    imagenParaMostrar = new Image(stream);
                    if (imagenParaMostrar.isError()) {
                        System.err.println("WARN: Error al decodificar imagen: " + pathIntentarCargar + ". Ex: " + (imagenParaMostrar.getException() != null ? imagenParaMostrar.getException().getMessage() : "Desconocida"));
                        imagenParaMostrar = null;
                    } else {
                        System.out.println("INFO: Imagen de protectora cargada exitosamente desde: " + pathIntentarCargar);
                    }
                } else {
                    System.err.println("WARN: Logo/Foto no encontrada en classpath: " + pathIntentarCargar);
                }
            } catch (Exception e) {
                System.err.println("ERROR: Excepción general al cargar imagen desde '" + pathIntentarCargar + "': " + e.getMessage());
            }
        } else {
            System.out.println("INFO: Ruta de imagen de protectora no proporcionada o vacía.");
        }

        if (imagenParaMostrar == null) {
            System.out.println("Cargando imagen placeholder para protectora...");
            try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_LOGO_PROTECTORA)) {
                if (placeholderStream != null) {
                    imagenParaMostrar = new Image(placeholderStream);
                    if (imagenParaMostrar.isError()) {
                        System.err.println("ERROR CRITICO: Error al decodificar la imagen placeholder: " + RUTA_PLACEHOLDER_LOGO_PROTECTORA +
                                ". Excepción: " + (imagenParaMostrar.getException() != null ? imagenParaMostrar.getException().getMessage() : "Desconocida"));
                        imagenParaMostrar = null;
                    }
                } else {
                    System.err.println("Error Crítico: Placeholder de logo de protectora no encontrado en: " + RUTA_PLACEHOLDER_LOGO_PROTECTORA);
                }
            } catch (Exception e) {
                System.err.println("Excepción crítica al cargar imagen placeholder de logo de protectora: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (imgLogoOFotoProtectora != null) imgLogoOFotoProtectora.setImage(imagenParaMostrar);
    }

    /**
     * Carga la imagen de placeholder en el ImageView del logo/foto de la protectora.
     */
    private void cargarImagenPlaceholder() {
        if (imgLogoOFotoProtectora == null) return;
        try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_LOGO_PROTECTORA)) {
            if (placeholderStream != null) {
                imgLogoOFotoProtectora.setImage(new Image(placeholderStream));
            } else {
                System.err.println("Error Crítico: Placeholder de logo de protectora no encontrado en: " + RUTA_PLACEHOLDER_LOGO_PROTECTORA);
                imgLogoOFotoProtectora.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Excepción crítica al cargar imagen placeholder de logo de protectora: " + e.getMessage());
            e.printStackTrace();
            imgLogoOFotoProtectora.setImage(null);
        }
    }

    /**
     * Carga la lista de redes sociales de la protectora en el ListView.
     * @param idProtectora El ID de la Protectora.
     */
    private void cargarRedesSociales(int idProtectora) {
        if (listViewRedesSociales == null) return;
        if (redesSocialesDao == null) {
            listViewRedesSociales.setPlaceholder(new Label("Servicio de redes sociales no disponible."));
            return;
        }
        if (idProtectora <= 0) {
            listViewRedesSociales.setPlaceholder(new Label("No se pudo identificar la protectora para cargar redes sociales."));
            return;
        }

        try {
            List<RedSocial> redes = redesSocialesDao.obtenerRedesSocialesPorProtectora(idProtectora);
            ObservableList<String> itemsParaListView = FXCollections.observableArrayList();

            if (redes != null && !redes.isEmpty()) {
                for (RedSocial red : redes) {
                    itemsParaListView.add(String.format("%s: %s",
                            Objects.requireNonNullElse(red.getPlataforma(), "Plataforma desconocida"),
                            Objects.requireNonNullElse(red.getUrl(), "URL no disponible")
                    ));
                }
                listViewRedesSociales.setItems(itemsParaListView);
                listViewRedesSociales.setPlaceholder(null);
            } else {
                listViewRedesSociales.getItems().clear();
                listViewRedesSociales.setPlaceholder(new Label(PLACEHOLDER_REDES_VACIO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            listViewRedesSociales.getItems().clear();
            listViewRedesSociales.setPlaceholder(new Label(PLACEHOLDER_REDES_ERROR));
            UtilidadesVentana.mostrarAlertaError("Error al Cargar Redes Sociales",
                    "No se pudieron cargar las redes sociales: " + e.getMessage());
        }
    }

    /**
     * Actualiza la interfaz de usuario para reflejar un estado de error
     * (ej. datos no encontrados o error de base de datos).
     */
    private void actualizarUIConDatosDeError() {
        if (lblNombreProtectoraTitulo != null) lblNombreProtectoraTitulo.setText("Perfil de Protectora");
        if (TxtNombreProtectora != null) TxtNombreProtectora.setText(TEXTO_ERROR_CARGA);
        if (TxtCIF != null) TxtCIF.setText(TEXTO_NO_DISPONIBLE);
        if (TxtEmailProtectora != null) TxtEmailProtectora.setText(TEXTO_NO_DISPONIBLE);
        if (TxtTelefonoProtectora != null) TxtTelefonoProtectora.setText(TEXTO_NO_DISPONIBLE);
        if (TxtDireccionProtectora != null) TxtDireccionProtectora.setText(TEXTO_NO_DISPONIBLE);
        cargarImagenPlaceholder();
        if (listViewRedesSociales != null) {
            listViewRedesSociales.setItems(FXCollections.observableArrayList());
            listViewRedesSociales.setPlaceholder(new Label(PLACEHOLDER_REDES_NO_INFO));
        }
        if (BtnEditarDatosProtectora != null) BtnEditarDatosProtectora.setDisable(true);
    }


    /**
     * Maneja el evento de clic en el botón "Editar Datos".
     * Navega al formulario de edición de protectora, pasando los datos de la protectora actual
     * y su cuenta de usuario asociada.
     * @param event El evento de ratón (si el botón usa onMouseClicked) o ActionEvent (si usa onAction).
     */
    @FXML
    void IrAFormularioEdicionProtectora(MouseEvent event) { // O ActionEvent
        if (this.protectoraActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Datos", "No hay datos de protectora cargados para editar. Intente recargar el perfil.");
            return;
        }
        if (this.cuentaUsuarioAsociada == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Cuenta", "No se dispone de la información de la cuenta de usuario asociada. No se puede proceder a la edición completa.");
            return;
        }

        System.out.println("INFO: Navegando a editar perfil para Protectora ID: " + protectoraActual.getIdProtectora() +
                ", Usuario ID: " + cuentaUsuarioAsociada.getIdUsuario());
        String tituloFormularioEdicion = "Editar Datos de " + Objects.requireNonNullElse(protectoraActual.getNombre(), "Protectora");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RUTA_FXML_FORMULARIO_PROTECTORA));
            Parent root = loader.load();

            FormularioProtectoraController formularioEdicionController = loader.getController();
            if (formularioEdicionController != null) {
                formularioEdicionController.initDataParaEdicion(this.protectoraActual, this.cuentaUsuarioAsociada);
                UtilidadesVentana.cambiarEscenaConRoot(root, tituloFormularioEdicion, false);
            } else {
                UtilidadesVentana.mostrarAlertaError("Error Interno del Sistema", "No se pudo cargar el controlador del formulario de edición de protectora.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            UtilidadesVentana.mostrarAlertaError("Error de Navegación", "No se pudo abrir el formulario de edición de protectora: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de clic en el icono "Volver".
     * Navega a la pantalla principal de la protectora.
     * @param event El evento de ratón.
     */
    @FXML
    void handleVolverAMainProtectora(MouseEvent event) {
        System.out.println("INFO: Volviendo al panel principal de la protectora desde PerfilProtectora.");
        UtilidadesVentana.cambiarEscena(RUTA_FXML_MAIN_PROTECTORA, TITULO_VENTANA_MAIN_PROTECTORA, true);
    }
}