package com.proyectointegral2.Model;

import java.time.LocalDateTime;

public class Notificacion {
    private int idNotificacion;
    private LocalDateTime fechaGeneracion;
    private String mensaje;
    private String tipoNotificacion;
    private Integer idEntidadRelacionada;
    private String entidadTipo;

    private int idUsuarioDestino;
    private boolean leida;
    private LocalDateTime fechaLeida;

    public Notificacion() {}

    public int getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(int idNotificacion) { this.idNotificacion = idNotificacion; }

    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getTipoNotificacion() { return tipoNotificacion; }
    public void setTipoNotificacion(String tipoNotificacion) { this.tipoNotificacion = tipoNotificacion; }

    public Integer getIdEntidadRelacionada() { return idEntidadRelacionada; }
    public void setIdEntidadRelacionada(Integer idEntidadRelacionada) { this.idEntidadRelacionada = idEntidadRelacionada; }

    public String getEntidadTipo() { return entidadTipo; }
    public void setEntidadTipo(String entidadTipo) { this.entidadTipo = entidadTipo; }

    public int getIdUsuarioDestino() { return idUsuarioDestino; }
    public void setIdUsuarioDestino(int idUsuarioDestino) { this.idUsuarioDestino = idUsuarioDestino; }

    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }

    public LocalDateTime getFechaLeida() { return fechaLeida; }
    public void setFechaLeida(LocalDateTime fechaLeida) { this.fechaLeida = fechaLeida; }
}