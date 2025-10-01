package com.proyectointegral2.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaCita {
    private int idReserva;
    private LocalDate fecha;
    private LocalTime hora;
    private String estadoCita;
    private int idCliente;
    private int idPerro;
    private int idProtectora;
    private double donacion;

    public ReservaCita() {}

    public ReservaCita(int idReserva, LocalDate fecha, LocalTime hora,double donacion, String estadoCita, int idCliente,int idPerro, int idProtectora) {
        this.idReserva = idReserva;
        this.fecha = fecha;
        this.hora = hora;
        this.donacion = donacion;
        this.estadoCita = estadoCita;
        this.idCliente = idCliente;
        this.idPerro = idPerro;
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

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdPerro() {
        return idPerro;
    }

    public void setIdPerro(int idPerro) {
        this.idPerro = idPerro;
    }

    public int getIdProtectora() {
        return idProtectora;
    }

    public void setIdProtectora(int idProtectora) {
        this.idProtectora = idProtectora;
    }

    public void setEstadoCita(String estadoCita) {
        this.estadoCita = estadoCita;
    }

    public String getEstadoCita() {
        return estadoCita;
    }

    public double getDonacion() {
        return donacion;
    }

    public void setDonacion(double donacion) {
        this.donacion = donacion;
    }
}