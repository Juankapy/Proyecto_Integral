package com.proyectointegral2.Model;

import java.time.LocalDate;

public class Perro {
    private int idPerro;
    private String nombre;
    private String sexo;
    private LocalDate fechaNacimiento;
    private String adoptado;
    private String foto;
    private int idProtectora;
    private Raza raza;
    private String protectora;


    public Perro() {}

    public Perro(int idPerro, String nombre, String foto, LocalDate fechaNacimiento, String sexo,
                 String adoptado, Raza raza, int idProtectora) {
        this.idPerro = idPerro;
        this.nombre = nombre;
        this.foto = foto;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.adoptado = adoptado;
        this.raza = raza;
        this.idProtectora = idProtectora;
    }


    public int getIdPerro() { return idPerro; }
    public void setIdPerro(int idPerro) { this.idPerro = idPerro; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getAdoptado() { return adoptado; }
    public void setAdoptado(String adoptado) { this.adoptado = adoptado; }

    public boolean isAdoptado() {
        return "S".equalsIgnoreCase(this.adoptado);
    }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public int getIdProtectora() { return idProtectora; }
    public void setIdProtectora(int idProtectora) { this.idProtectora = idProtectora; }

    public Raza getRaza() { return raza; }
    public void setRaza(Raza raza) { this.raza = raza; }

    public int getIdRaza() {
        return (this.raza != null) ? this.raza.getIdRaza() : 0;
    }

    public String getProtectora() {
        return protectora;
    }

    public void setProtectora(String protectora) {
        this.protectora = protectora;
    }
}