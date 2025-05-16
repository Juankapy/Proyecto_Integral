package com.proyectointegral2.Model;

public class Protectora {
    private int id;
    private String cif;
    private String nombre;
    private Direccion direccion; // Reutilizando la clase Direccion
    private String telefono;
    private String email;
    private String redesSociales;
    private int idUsuario; // CAMBIO: Añadir campo para el ID del Usuario asociado

    // Constructor con todos los atributos, incluyendo idUsuario
    public Protectora(int id, String cif, String nombre, Direccion direccion,
                      String telefono, String email, String redesSociales, int idUsuario) {
        this.id = id;
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
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

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

    // *** GETTER Y SETTER AÑADIDOS PARA idUsuario ***
    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    // *** FIN GETTER Y SETTER AÑADIDOS ***
}