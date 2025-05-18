package com.proyectointegral2.Controller;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.Model.Patologia;
import com.proyectointegral2.Model.IdentificacionPatologia;
import com.proyectointegral2.dao.IdentificacionPatologiaDao;
import com.proyectointegral2.dao.PerroDao;
import com.proyectointegral2.dao.RazaDao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.ResourceBundle;

public class DetallesPerroController implements Initializable {

    @FXML
    private ImageView imgPerro;
    @FXML
    private Text TxtNombre;
    @FXML
    private Text TxtEdad;
    @FXML
    private Text TxtRaza;
    @FXML
    private Text TxtSexo;
    @FXML
    private Text TxtProtectora;
    @FXML
    private Text TxtPatologia;
    @FXML
    private ImageView imgLogoPequeno;
    @FXML
    private Button BtnReservarCita;

    private Perro perroActual;
    private int idPerroSeleccionado;

    private PerroDao perroDao;
    private RazaDao razaDao;
    private IdentificacionPatologiaDao identificacionPatologiaDao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        perroDao = new PerroDao();
        razaDao = new RazaDao();
        identificacionPatologiaDao = new IdentificacionPatologiaDao();
    }

    public void initData(int idPerro) {
        this.idPerroSeleccionado = idPerro;
        cargarDetallesPerro();
    }

    private void cargarDetallesPerro() {
        if (idPerroSeleccionado <= 0) {
            System.out.println("Error: ID de perro no válido para mostrar detalles.");
            return;
        }

        try {
            perroActual = perroDao.obtenerPerroPorId(idPerroSeleccionado);

            if (perroActual != null) {
                TxtNombre.setText(perroActual.getNombre());

                LocalDate fechaNac = perroActual.getFechaNacimiento();
                if (fechaNac != null) {
                    Period periodo = Period.between(fechaNac, LocalDate.now());
                    if (periodo.getYears() > 0) {
                        TxtEdad.setText(periodo.getYears() + (periodo.getYears() == 1 ? " año" : " años"));
                    } else if (periodo.getMonths() > 0) {
                        TxtEdad.setText(periodo.getMonths() + (periodo.getMonths() == 1 ? " mes" : " meses"));
                    } else {
                        TxtEdad.setText(periodo.getDays() + (periodo.getDays() == 1 ? " día" : " días"));
                    }
                } else {
                    TxtEdad.setText("Desconocida");
                }

                if (perroActual.getIdRaza() > 0) {
                    Raza raza = razaDao.obtenerRazaPorId(perroActual.getIdRaza());
                    TxtRaza.setText(raza != null ? raza.getNombreRaza() : "Desconocida");
                } else {
                    TxtRaza.setText("Mestizo/Desconocida");
                }

                TxtSexo.setText(perroActual.getSexo());

                // Si no tienes ProtectoraDao, puedes mostrar solo el ID o dejarlo como "Desconocida"
                if (perroActual.getIdProtectora() > 0) {
                    TxtProtectora.setText(String.valueOf(perroActual.getIdProtectora()));
                } else {
                    TxtProtectora.setText("Desconocida");
                }

                List<IdentificacionPatologia> identificaciones = identificacionPatologiaDao.obtenerPatologiasPorPerro(perroActual.getIdPerro());
                if (identificaciones != null && !identificaciones.isEmpty()) {
                    StringBuilder patologiasStr = new StringBuilder();
                    for(IdentificacionPatologia ip : identificaciones) {
                        Patologia pat = perroDao.obtenerPatologiaPorId(ip.getIdPatologia());
                        if (pat != null) {
                            patologiasStr.append(pat.getNombre());
                            if (ip.getDescripcion() != null && !ip.getDescripcion().isEmpty()) {
                                patologiasStr.append(" (").append(ip.getDescripcion()).append(")");
                            }
                            patologiasStr.append("; ");
                        }
                    }
                    if (patologiasStr.length() > 2) {
                        TxtPatologia.setText(patologiasStr.substring(0, patologiasStr.length() - 2));
                    } else {
                        TxtPatologia.setText("Ninguna conocida.");
                    }
                } else {
                    TxtPatologia.setText("Ninguna conocida.");
                }

                if (perroActual.getFoto() != null && perroActual.getFoto().length > 0) {
                    Image image = new Image(new ByteArrayInputStream(perroActual.getFoto()));
                    imgPerro.setImage(image);
                } else {
                    System.out.println("Perro sin foto.");
                }

                if ("S".equals(perroActual.getAdoptado())) {
                    BtnReservarCita.setDisable(true);
                    BtnReservarCita.setText("Adoptado");
                } else {
                    BtnReservarCita.setDisable(false);
                    BtnReservarCita.setText("Reservar cita");
                }

            } else {
                System.out.println("Error: Perro no encontrado en la BD.");
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al cargar detalles del perro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void ReservarCita(ActionEvent event) {
        if (perroActual != null && !"S".equals(perroActual.getAdoptado())) {
            System.out.println("Navegando a reservar cita para perro ID: " + perroActual.getIdPerro());
        } else {
            System.out.println("Error: El perro no está disponible para citas.");
        }
    }
}