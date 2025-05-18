package com.proyectointegral2.Model;

import com.proyectointegral2.Model.Direccion;
import java.time.LocalDate;

public class Cliente {
    private int IdCliente;
    private String nif;
    private String nombre;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private Direccion direccion;
    private String telefono;
    private String email;
    private int idUsuario;

    // Constructor con todos los atributos
    public Cliente(int idCliente, String nif, String nombre, String apellidos, LocalDate fechaNacimiento,
                   Direccion direccion, String telefono, String email, int idUsuario) {
        this.IdCliente = idCliente;
        this.nif = nif;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.idUsuario = idUsuario;
    }
    // Constructor vacío
    public Cliente() {
        this.IdCliente = 0;
        this.nif = "";
        this.nombre = "";
        this.apellidos = "";
        this.fechaNacimiento = null;
        this.direccion = new Direccion();
        this.telefono = "";
        this.email = "";
        this.idUsuario = 0;
    }
    // Getters y setters para todos los campos
    public int getIdCliente() { return IdCliente; }
    public void setIdCliente(int id) { this.IdCliente = id; }

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
}