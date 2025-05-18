package com.proyectointegral2.Model;

import java.time.LocalDate;

public class Perro {
    private int idPerro;
    private String nombre;
    private String sexo;
    private LocalDate fechaNacimiento;
    private String adoptado;
    private byte[] foto;
    private int idProtectora;
    private int idRaza;

    public Perro() {}

    public Perro(int idPerro, String nombre, String sexo, LocalDate fechaNacimiento, String adoptado, byte[] foto, int idProtectora, int idRaza) {
        this.idPerro = idPerro;
        this.nombre = nombre;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.adoptado = adoptado;
        this.foto = foto;
        this.idProtectora = idProtectora;
        this.idRaza = idRaza;
    }

    public int getIdPerro() {
        return idPerro;
    }

    public void setIdPerro(int idPerro) {
        this.idPerro = idPerro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getAdoptado() {
        return adoptado;
    }

    public void setAdoptado(String adoptado) {
        this.adoptado = adoptado;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public int getIdProtectora() {
        return idProtectora;
    }

    public void setIdProtectora(int idProtectora) {
        this.idProtectora = idProtectora;
    }

    public int getIdRaza() {
        return idRaza;
    }

    public void setIdRaza(int idRaza) {
        this.idRaza = idRaza;
    }
}