package com.proyectointegral2.Model;

public class Direccion {
    private String provincia;
    private String ciudad;
    private String calle;
    private String codigoPostal;

    // Constructor con todos los atributos
    public Direccion(String provincia, String ciudad, String calle, String codigoPostal) {
        this.provincia = provincia;
        this.ciudad = ciudad;
        this.calle = calle;
        this.codigoPostal = codigoPostal;
    }
    // Constructor vacío
    public Direccion() {
        this.provincia = "";
        this.ciudad = "";
        this.calle = "";
        this.codigoPostal = "";
    }

    // Getters y Setters
    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
}
