package com.proyectointegral2.Model;

public class Usuario {

    private int id;
    private String NombreUsuario;
    private String Contrasena;

    // Constructor sin atributos
    public Usuario() {
        this.NombreUsuario = "";
        this.Contrasena = "";
        this.id = -1;
    }

    // Constructor con todos los atributos
    public Usuario(int id, String NombreUsuario, String Contrasena) {
        this.NombreUsuario = NombreUsuario;
        this.Contrasena = Contrasena;
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombreUsuario() {
        return NombreUsuario;
    }
    public void setNombreUsuario(String NombreUsuario) {
        this.NombreUsuario = NombreUsuario;
    }
    public String getContrasena() {
        return Contrasena;
    }
    public void setContrasena(String Contrasena) {
        this.Contrasena = Contrasena;
    }
}
