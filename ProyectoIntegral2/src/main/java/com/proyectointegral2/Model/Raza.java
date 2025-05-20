package com.proyectointegral2.Model;

public class Raza {
    private int idRaza;
    private String nombreRaza;

    public Raza(String nombreRaza) {
        this.nombreRaza = nombreRaza;
    }

    public Raza(int idRaza, String nombreRaza) {
        this.idRaza = idRaza;
        this.nombreRaza = nombreRaza;
    }

    // Constructor vacío (buena práctica)
    public Raza() {}

    // Getters y Setters
    public int getIdRaza() {
        return idRaza;
    }

    public void setIdRaza(int idRaza) {
        this.idRaza = idRaza;
    }

    public String getNombreRaza() {
        return nombreRaza;
    }

    public void setNombreRaza(String nombreRaza) {
        this.nombreRaza = nombreRaza;
    }
}