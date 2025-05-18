package com.proyectointegral2.Model;

public class Patologia {
    private int idPatologia;
    private String nombre;
    private String descripcion;

    // Constructor vacio
    public Patologia() {
    }

    // Constructor con todos los atributos
    public Patologia(int idPatologia, String nombre, String descripcion) {
        this.idPatologia = idPatologia;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setIdPatologia(int idPatologia) {
    }

    public int getIdPatologia() {
        return idPatologia;
    }
}