package com.proyectointegral2.Model;

import java.time.LocalDate;

public class Cliente {
    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private Direccion direccion;
    private String telefono;
    private String email;
    private String nif;
    private int idUsuario;

    // Constructor sin atributos
    public Cliente() {
        this.nombre = "";
        this.apellidos = "";
        this.fechaNacimiento = LocalDate.now();
        this.direccion = new Direccion("", "", "", "");
        this.telefono = "";
        this.email = "";
        this.nif = "";
        this.idUsuario = -1;
    }
    // Constructor con todos los atributos
    public Cliente(String nombre, String apellidos, LocalDate fechaNacimiento, Direccion direccion, String telefono, String email, String nif, int idUsuario) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.nif = nif;
        this.idUsuario = idUsuario;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

}
