package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.RedSocial;
import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.dao.ProtectoraDao; // Aunque no lo usemos para cargar, podría ser para otras ops
import com.proyectointegral2.dao.RedesSocialesDao;
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
import javafx.stage.Stage; // Para el owner en IrAFormularioEdicionProtectora

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    // No necesitamos ProtectoraDao aquí si los datos vienen por initData
    // private ProtectoraDao protectoraDao;
    private RedesSocialesDao redesSocialesDao;
    private Protectora protectoraActual;
    private Usuario cuentaUsuarioAsociada;

    private final String RUTA_PLACEHOLDER_LOGO_PROT = "/assets/Imagenes/iconos/sinusuario.jpg"; // Asegúrate que esta imagen exista

    @FXML
    public void initialize() {
        this.redesSocialesDao = new RedesSocialesDao();
        limpiarYMostrarCargando(); // Prepara la UI para recibir datos
    }


    public void initData(Protectora protectora, Usuario cuentaUsuario) {
        this.protectoraActual = protectora;
        this.cuentaUsuarioAsociada = cuentaUsuario;

        if (this.protectoraActual == null) {
            UtilidadesVentana.mostrarAlertaError("Error de Datos", "No se recibió información de la protectora.");
            mostrarDatosDeErrorEnUI();
            return;
        }
        System.out.println("PerfilProtectoraController: Mostrando datos para Protectora ID: " + protectoraActual.getIdProtectora() + ", Nombre: " + protectoraActual.getNombre());
        poblarDatosProtectoraEnUI();
    }

    private void limpiarYMostrarCargando() {
        if (lblNombreProtectoraTitulo != null) lblNombreProtectoraTitulo.setText("Perfil de Protectora");
        if (TxtNombreProtectora != null) TxtNombreProtectora.setText("Cargando...");
        if (TxtCIF != null) TxtCIF.setText("...");
        if (TxtEmailProtectora != null) TxtEmailProtectora.setText("...");
        if (TxtTelefonoProtectora != null) TxtTelefonoProtectora.setText("...");
        if (TxtDireccionProtectora != null) TxtDireccionProtectora.setText("...");
        if (listViewRedesSociales != null) {
            listViewRedesSociales.getItems().clear();
            listViewRedesSociales.setPlaceholder(new Label("Cargando redes sociales..."));
        }
        cargarImagenPlaceholder();
        if (BtnEditarDatosProtectora != null) BtnEditarDatosProtectora.setDisable(true);
    }

    private void poblarDatosProtectoraEnUI() {
        if (protectoraActual == null) {
            mostrarDatosDeErrorEnUI();
            return;
        }

        lblNombreProtectoraTitulo.setText("Perfil de " + Objects.requireNonNullElse(protectoraActual.getNombre(), "Protectora"));
        TxtNombreProtectora.setText(Objects.requireNonNullElse(protectoraActual.getNombre(), "No disponible"));
        TxtCIF.setText(Objects.requireNonNullElse(protectoraActual.getCif(), "No disponible"));
        TxtEmailProtectora.setText(Objects.requireNonNullElse(protectoraActual.getEmail(), "No disponible"));
        TxtTelefonoProtectora.setText(Objects.requireNonNullElse(protectoraActual.getTelefono(), "No disponible"));

        String direccionCompleta = construirDireccion(protectoraActual);
        TxtDireccionProtectora.setText(direccionCompleta.isEmpty() ? "No disponible" : direccionCompleta);

        cargarLogoProtectora(protectoraActual.getRutaFotoPerfil());
        cargarRedesSocialesDeLaProtectora(protectoraActual.getIdProtectora());

        BtnEditarDatosProtectora.setDisable(false); // Habilitar botón de editar
    }

    private String construirDireccion(Protectora p) {
        List<String> partes = new ArrayList<>();
        if (p.getCalle() != null && !p.getCalle().trim().isEmpty()) partes.add(p.getCalle().trim());
        if (p.getCiudad() != null && !p.getCiudad().trim().isEmpty()) partes.add(p.getCiudad().trim());
        if (p.getProvincia() != null && !p.getProvincia().trim().isEmpty()) partes.add(p.getProvincia().trim());
        if (p.getCodigoPostal() != null && !p.getCodigoPostal().trim().isEmpty()) partes.add(p.getCodigoPostal().trim());
        return String.join(", ", partes);
    }

    private void cargarLogoProtectora(String rutaLogoRelativa) {
        if (rutaLogoRelativa != null && !rutaLogoRelativa.trim().isEmpty()) {
            String pathCorregido = rutaLogoRelativa;
            if (!pathCorregido.startsWith("/")) pathCorregido = "/" + pathCorregido;
            try (InputStream stream = getClass().getResourceAsStream(pathCorregido)) {
                if (stream != null) {
                    imgLogoOFotoProtectora.setImage(new Image(stream));
                    return;
                } else System.err.println("WARN: Logo/Foto de protectora no encontrada en classpath: " + pathCorregido);
            } catch (Exception e) {
                System.err.println("Excepción al cargar logo/foto de protectora: " + e.getMessage());
            }
        }
        cargarImagenPlaceholder();
    }

    private void cargarImagenPlaceholder() {
        try (InputStream placeholderStream = getClass().getResourceAsStream(RUTA_PLACEHOLDER_LOGO_PROT)) {
            if (placeholderStream != null) {
                imgLogoOFotoProtectora.setImage(new Image(placeholderStream));
            } else {
                System.err.println("Error: No se pudo cargar placeholder de logo: " + RUTA_PLACEHOLDER_LOGO_PROT);
                imgLogoOFotoProtectora.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("Excepción crítica cargando placeholder de logo protectora: " + e.getMessage());
        }
    }

    private void cargarRedesSocialesDeLaProtectora(int idProtectora) {
        if (idProtectora <= 0 || redesSocialesDao == null) {
            listViewRedesSociales.setPlaceholder(new Label("No se pudo cargar información de redes sociales."));
            return;
        }
        try {
            List<RedSocial> redes = redesSocialesDao.obtenerRedesSocialesPorProtectora(idProtectora);
            ObservableList<String> itemsRedes = FXCollections.observableArrayList();
            if (redes != null && !redes.isEmpty()) {
                for (RedSocial rs : redes) {
                    itemsRedes.add(rs.getPlataforma() + ": " + rs.getUrl());
                }
                listViewRedesSociales.setItems(itemsRedes);
                listViewRedesSociales.setPlaceholder(null); // Quitar placeholder si hay items
            } else {
                listViewRedesSociales.getItems().clear();
                listViewRedesSociales.setPlaceholder(new Label("No hay redes sociales registradas."));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            listViewRedesSociales.getItems().clear();
            listViewRedesSociales.setPlaceholder(new Label("Error al cargar redes sociales."));
            UtilidadesVentana.mostrarAlertaError("Error Redes Sociales", "No se pudieron cargar las redes: " + e.getMessage());
        }
    }

    private void mostrarDatosDeErrorEnUI() {
        lblNombreProtectoraTitulo.setText("Perfil de Protectora");
        TxtNombreProtectora.setText("Error al cargar");
        TxtCIF.setText("N/D");
        TxtEmailProtectora.setText("N/D");
        TxtTelefonoProtectora.setText("N/D");
        TxtDireccionProtectora.setText("N/D");
        cargarImagenPlaceholder();
        listViewRedesSociales.setItems(FXCollections.observableArrayList());
        listViewRedesSociales.setPlaceholder(new Label("No hay información disponible."));
        BtnEditarDatosProtectora.setDisable(true);
    }


    @FXML
    void IrAFormularioEdicionProtectora(MouseEvent event) {
        if (protectoraActual == null || cuentaUsuarioAsociada == null) {
            UtilidadesVentana.mostrarAlertaError("Error", "No hay datos de protectora cargados para editar. Intente recargar el perfil.");
            return;
        }
        System.out.println("Botón Editar Datos Protectora presionado para ID: " + protectoraActual.getIdProtectora());
        String formularioFxml = "/com/proyectointegral2/Vista/FormularioProtectora.fxml";
        String titulo = "Editar Datos de " + protectoraActual.getNombre();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(formularioFxml));
            Parent root = loader.load();
            FormularioProtectoraController formController = loader.getController();

            if (formController != null) {
                formController.initDataParaEdicion(this.protectoraActual, this.cuentaUsuarioAsociada);
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
    void handleVolverAMainProtectora(MouseEvent event) {
        System.out.println("Volviendo a MainProtectora desde PerfilProtectora...");
        UtilidadesVentana.cambiarEscena("/com/proyectointegral2/Vista/MainProtectora.fxml", "Panel Protectora", true);
    }
}