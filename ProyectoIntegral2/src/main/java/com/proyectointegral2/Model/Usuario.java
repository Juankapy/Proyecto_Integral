package com.proyectointegral2.Model;

public class Usuario {
    private int idUsuario;
    private String nombreUsu;
    private String contrasena;
    private String rol;

    public Usuario() {}

    public Usuario(int idUsuario, String nombreUsu, String contrasena, String rol) {
        this.idUsuario = idUsuario;
        this.nombreUsu = nombreUsu;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsu() { return nombreUsu; }
    public void setNombreUsu(String nombreUsu) { this.nombreUsu = nombreUsu; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}