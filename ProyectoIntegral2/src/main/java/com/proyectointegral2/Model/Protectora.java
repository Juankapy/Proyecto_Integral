package com.proyectointegral2.Model;

public class Protectora {
    private int idProtectora;
    private String cif;
    private String nombre;
    private String provincia;
    private String ciudad;
    private String calle;
    private String codigoPostal;
    private String telefono;
    private String email;
    private int idUsuario;
    private String rutaFotoPerfil;

    public Protectora() {}

    public int getIdProtectora() { return idProtectora; }
    public void setIdProtectora(int idProtectora) { this.idProtectora = idProtectora; }
    public String getCif() { return cif; }
    public void setCif(String cif) { this.cif = cif; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
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