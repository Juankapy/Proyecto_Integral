package com.proyectointegral2.Model;

import java.time.LocalDate;

public class RegistroAdopcionInfo {
    private int idPeticion;
    private String nombrePerro;
    private LocalDate fechaPeticion;
    private String nombreAdoptante;
    private String estadoPeticion;
    private String numeroContacto;
    private int idPerro;

    public RegistroAdopcionInfo() {}

    public RegistroAdopcionInfo(int idPeticion, String nombrePerro, LocalDate fechaPeticion, String nombreAdoptante, String estadoPeticion, String numeroContacto) {
        this.idPeticion = idPeticion;
        this.nombrePerro = nombrePerro;
        this.fechaPeticion = fechaPeticion;
        this.nombreAdoptante = nombreAdoptante;
        this.estadoPeticion = estadoPeticion;
        this.numeroContacto = numeroContacto;
    }

    public int getIdPeticion() { return idPeticion; }
    public void setIdPeticion(int idPeticion) { this.idPeticion = idPeticion; }

    public String getNombrePerro() { return nombrePerro; }
    public void setNombrePerro(String nombrePerro) { this.nombrePerro = nombrePerro; }

    public LocalDate getFechaPeticion() { return fechaPeticion; }
    public void setFechaPeticion(LocalDate fechaPeticion) { this.fechaPeticion = fechaPeticion; }

    public String getNombreAdoptante() { return nombreAdoptante; }
    public void setNombreAdoptante(String nombreAdoptante) { this.nombreAdoptante = nombreAdoptante; }

    public String getEstadoPeticion() { return estadoPeticion; }
    public void setEstadoPeticion(String estadoPeticion) { this.estadoPeticion = estadoPeticion; }

    public String getNumeroContacto() { return numeroContacto; }
    public void setNumeroContacto(String numeroContacto) { this.numeroContacto = numeroContacto; }

    public int getIdPerro() {
        return idPerro;
    }

    public void setIdPerro(int idPerro) {
        this.idPerro = idPerro;
    }

}