package com.proyectointegral2.Model;

import java.sql.Date;

public class Cliente {
    private int IdCliente;
    private String nif;
    private String nombre;
    private String apellidos;
    private Date fechaNacimiento;
    private Direccion direccion;
    private String telefono;
    private String email;
    private int idUsuario;

    // Getters y setters para todos los campos
    public int getIdCliente() { return IdCliente; }
    public void setIdCliente(int id) { this.IdCliente = IdCliente; }

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Direccion getDireccion() { return direccion; }

    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}