package com.Model;

import java.time.LocalDate;

public class Perro {
    private int id;
    private String nombre;
    private String foto;
    private LocalDate fechaNacimiento; // Usar LocalDate si prefieres trabajar con fechas
    private String sexo;
    private boolean adoptado;
    private Raza raza;

    // Constructor vacío
    public Perro() {
    }

    // Constructor con todos los atributos
    public Perro(int id, String nombre, String foto, LocalDate fechaNacimiento, String sexo, boolean adoptado, Raza raza) {
        this.id = id;
        this.nombre = nombre;
        this.foto = foto;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.adoptado = adoptado;
        this.raza = raza;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public boolean isAdoptado() {
        return adoptado;
    }

    public void setAdoptado(boolean adoptado) {
        this.adoptado = adoptado;
    }

    public Raza getRaza() {
        return raza;
    }

    public void setRaza(Raza raza) {
        this.raza = raza;
    }
}
