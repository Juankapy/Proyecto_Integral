package com.proyectointegral2.Model;

import java.time.LocalDate;

public class Cliente {
    private int idCliente;
    private String nif;
    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String provincia;
    private String ciudad;
    private String calle;
    private String codigoPostal;
    private String telefono;
    private String email;
    private int idUsuario;
    private String rutaFotoPerfil; // Añadido anteriormente

    public Cliente() {}

    // Constructor, Getters y Setters para TODOS los campos, incluyendo los de dirección
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getRutaFotoPerfil() { return rutaFotoPerfil; }
    public void setRutaFotoPerfil(String rutaFotoPerfil) { this.rutaFotoPerfil = rutaFotoPerfil; }
}