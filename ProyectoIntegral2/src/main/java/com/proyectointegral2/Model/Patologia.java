package com.proyectointegral2.Model;

public class Patologia {
    private int idPatologia;
    private String nombre;
    private String descripcion;

    public Patologia() {
    }

    public Patologia(int idPatologia, String nombre, String descripcion) {
        this.idPatologia = idPatologia;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdPatologia() { return idPatologia; }
    public void setIdPatologia(int idPatologia) {
        this.idPatologia = idPatologia;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}