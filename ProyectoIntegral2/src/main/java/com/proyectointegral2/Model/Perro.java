package com.proyectointegral2.Model;

import com.proyectointegral2.Model.Raza;

import java.time.LocalDate;
// En Perro.java
// ...
public class Perro {
    private int id;
    private String nombre;
    private String foto; // ESTO YA ES STRING, ¡Perfecto!
    private LocalDate fechaNacimiento;
    private String sexo;
    private boolean adoptado;
    private Raza raza;
    // Asegúrate de tener el campo idProtectora y su getter/setter
    private int idProtectora;


    public Perro(int id, String nombre, String foto, LocalDate fechaNacimiento, String sexo, boolean adoptado, Raza raza, int idProtectora) {
        this.id = id;
        this.nombre = nombre;
        this.foto = foto; // String
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.adoptado = adoptado;
        this.raza = raza;
        this.idProtectora = idProtectora; // Asegúrate que esto esté
    }
    public Perro(){} // Constructor por defecto

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

    public String getFoto() { // Devuelve String
        return foto;
    }
    public void setFoto(String foto) { // Acepta String
        this.foto = foto;
    }

    public int getIdProtectora() { return idProtectora; }
    public void setIdProtectora(int idProtectora) { this.idProtectora = idProtectora; }

    public void setAdoptadoChar(String adoptadoChar) { this.adoptado = "S".equalsIgnoreCase(adoptadoChar); }
    public String getAdoptadoChar() { return this.adoptado ? "S" : "N"; }
}