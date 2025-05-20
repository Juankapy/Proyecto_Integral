package com.proyectointegral2.Model;

import java.sql.Date;

public class PeticionAdopcion {
    private int idPeticion;
    private Date fecha;
    private String estado;
    private int idCliente;
    private int idPerro;
    private String mensajePeticion;

    public PeticionAdopcion(int idPeticion, Date fecha, String estado, int idCliente, int idPerro, String mensajePeticion) {
        this.idPeticion = idPeticion;
        this.fecha = fecha;
        this.estado = estado;
        this.idCliente = idCliente;
        this.idPerro = idPerro;
        this.mensajePeticion = mensajePeticion;
    }

    public PeticionAdopcion() {
        this.idPeticion = 0;
        this.fecha = null;
        this.estado = "";
        this.idCliente = 0;
        this.idPerro = 0;
        this.mensajePeticion = "";
    }

    public int getIdPeticion() { return idPeticion; }
    public void setIdPeticion(int idPeticion) { this.idPeticion = idPeticion; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdPerro() { return idPerro; }
    public void setIdPerro(int idPerro) { this.idPerro = idPerro; }

    public String getMensajePeticion() {
        return mensajePeticion;
    }

    public void setMensajePeticion(String mensajePeticion) {
        mensajePeticion = mensajePeticion;
    }
}