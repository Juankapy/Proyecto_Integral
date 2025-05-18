package com.proyectointegral2.Model;

public class RedSocial {
    private String plataforma;
    private String url;
    private int idProtectora;

    public RedSocial() {}

    public RedSocial(String plataforma, String url, int idProtectora) {
        this.plataforma = plataforma;
        this.url = url;
        this.idProtectora = idProtectora;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIdProtectora() {
        return idProtectora;
    }

    public void setIdProtectora(int idProtectora) {
        this.idProtectora = idProtectora;
    }
}