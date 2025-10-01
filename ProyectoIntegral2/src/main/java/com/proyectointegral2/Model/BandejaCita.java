package com.proyectointegral2.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class BandejaCita {
    private String nombreCliente;
    private String nombrePerro;
    private LocalDate fecha;
    private LocalTime hora;

    public BandejaCita(String nombreCliente, String nombrePerro, LocalDate fecha, LocalTime hora) {
        this.nombreCliente = nombreCliente;
        this.nombrePerro = nombrePerro;
        this.fecha = fecha;
        this.hora = hora;
    }

    public BandejaCita() {

    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombrePerro() {
        return nombrePerro;
    }

    public void setNombrePerro(String nombrePerro) {
        this.nombrePerro = nombrePerro;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
}