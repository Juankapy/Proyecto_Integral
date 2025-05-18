package com.proyectointegral2.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaCita {
    private int idReserva;
    private LocalDate fecha;
    private LocalTime hora;
    private String motivo;
    private int idCliente;
    private int idProtectora;

    public ReservaCita() {}

    public ReservaCita(int idReserva, LocalDate fecha, LocalTime hora, String motivo, int idCliente, int idProtectora) {
        this.idReserva = idReserva;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.idCliente = idCliente;
        this.idProtectora = idProtectora;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdProtectora() {
        return idProtectora;
    }

    public void setIdProtectora(int idProtectora) {
        this.idProtectora = idProtectora;
    }
}