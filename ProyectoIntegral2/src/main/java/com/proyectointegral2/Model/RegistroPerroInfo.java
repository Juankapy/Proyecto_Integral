package com.proyectointegral2.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class RegistroPerroInfo {
    private final StringProperty nombre;
    private final ObjectProperty<LocalDate> fechaIngreso; // Usaremos FechaNacimiento como placeholder
    private final StringProperty estadoActual;
    private final StringProperty notasAdicionales;
    private final int idPerro;

    public RegistroPerroInfo(int idPerro, String nombre, LocalDate fechaIngreso, String estadoActual, String notasAdicionales) {
        this.idPerro = idPerro;
        this.nombre = new SimpleStringProperty(nombre);
        this.fechaIngreso = new SimpleObjectProperty<>(fechaIngreso);
        this.estadoActual = new SimpleStringProperty(estadoActual);
        this.notasAdicionales = new SimpleStringProperty(notasAdicionales);
    }

    public StringProperty nombreProperty() { return nombre; }
    public ObjectProperty<LocalDate> fechaIngresoProperty() { return fechaIngreso; }
    public StringProperty estadoActualProperty() { return estadoActual; }
    public StringProperty notasAdicionalesProperty() { return notasAdicionales; }

    public String getNombre() { return nombre.get(); }
    public LocalDate getFechaIngreso() { return fechaIngreso.get(); }
    public String getEstadoActual() { return estadoActual.get(); }
    public String getNotasAdicionales() { return notasAdicionales.get(); }
    public int getIdPerro() { return idPerro; }
}