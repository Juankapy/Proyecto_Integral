package com.proyectointegral2.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class RegistroAdopcionInfo {
    private final StringProperty nombrePerro;
    private final ObjectProperty<LocalDate> fechaAdopcion;
    private final StringProperty horaAdopcion;
    private final StringProperty nombreAdoptante;
    private final StringProperty contactoAdoptante;
    private final int idPeticion;

    public RegistroAdopcionInfo(int idPeticion, String nombrePerro, LocalDate fechaAdopcion, String horaAdopcion, String nombreAdoptante, String contactoAdoptante) {
        this.idPeticion = idPeticion;
        this.nombrePerro = new SimpleStringProperty(nombrePerro);
        this.fechaAdopcion = new SimpleObjectProperty<>(fechaAdopcion);
        this.horaAdopcion = new SimpleStringProperty(horaAdopcion);
        this.nombreAdoptante = new SimpleStringProperty(nombreAdoptante);
        this.contactoAdoptante = new SimpleStringProperty(contactoAdoptante);
    }

    public StringProperty nombrePerroProperty() { return nombrePerro; }
    public ObjectProperty<LocalDate> fechaAdopcionProperty() { return fechaAdopcion; }
    public StringProperty horaAdopcionProperty() { return horaAdopcion; }
    public StringProperty nombreAdoptanteProperty() { return nombreAdoptante; }
    public StringProperty contactoAdoptanteProperty() { return contactoAdoptante; }

    public String getNombrePerro() { return nombrePerro.get(); }
    public LocalDate getFechaAdopcion() { return fechaAdopcion.get(); }
    public String getHoraAdopcion() { return horaAdopcion.get(); }
    public String getNombreAdoptante() { return nombreAdoptante.get(); }
    public String getContactoAdoptante() { return contactoAdoptante.get(); }
    public int getIdPeticion() { return idPeticion; }
}