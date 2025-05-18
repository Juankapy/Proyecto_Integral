package com.proyectointegral2.Model;

public class Protectora {
    private int idProtectora;
    private String cif;
    private String nombre;
    private Direccion direccion;
    private String telefono;
    private String email;
    private String redesSociales;
    private int idUsuario;

    // Constructor con todos los atributos, incluyendo idUsuario
    public Protectora(int idProtectora, String cif, String nombre, Direccion direccion,
                      String telefono, String email, String redesSociales, int idUsuario) {
        this.idProtectora = idProtectora;
        this.cif = cif;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.redesSociales = redesSociales;
        this.idUsuario = idUsuario; // CAMBIO
    }

    // Constructor por defecto (buena práctica)
    public Protectora() {
    }

    // Getters y Setters

    public String getCif() {
        return cif;
    }
    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getRedesSociales() {
        return redesSociales;
    }
    public void setRedesSociales(String redesSociales) {
        this.redesSociales = redesSociales;
    }

    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setIdProtectora(int idProtectora) {
        this.idProtectora = idProtectora;
    }

    public int getIdProtectora() {
        return idProtectora;
    }
}