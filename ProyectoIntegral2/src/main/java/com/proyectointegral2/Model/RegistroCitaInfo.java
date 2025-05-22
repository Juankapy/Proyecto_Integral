package com.proyectointegral2.Model;

public class RegistroCitaInfo {
    private String nombreCliente;
    private String nombreMascota;
    private String fechaCita;
    private String horaCita;
    private String tipoServicio;
    private String veterinario;

    public RegistroCitaInfo(String nombreCliente, String nombreMascota, String fechaCita, String horaCita, String tipoServicio, String veterinario) {
        this.nombreCliente = nombreCliente;
        this.nombreMascota = nombreMascota;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.tipoServicio = tipoServicio;
        this.veterinario = veterinario;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(String fechaCita) {
        this.fechaCita = fechaCita;
    }

    public String getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(String horaCita) {
        this.horaCita = horaCita;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }
}
