package com.proyectointegral2.Model;

public class Usuario {

    private int IdUsuario;
    private String NombreUsuario;
    private String Contrasena;

    // Constructor sin atributos
    public Usuario() {
        this.NombreUsuario = "";
        this.Contrasena = "";
        this.IdUsuario = -1;
    }

    // Constructor con todos los atributos
    public Usuario(int IdUsuario, String NombreUsuario, String Contrasena) {
        this.NombreUsuario = NombreUsuario;
        this.Contrasena = Contrasena;
        this.IdUsuario = IdUsuario;
    }

    public int getIdUsuario() {
        return IdUsuario;
    }
    public void setIdUsuario(int IdUsuario) {
        this.IdUsuario = IdUsuario;
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
