package com.proyectointegral2.Model;
import com.proyectointegral2.Model.Usuario;

public class SesionUsuario {
    private static Usuario usuarioLogueado;
    private static int entidadIdEspecifica; // Para el ID de Cliente o Protectora

    private SesionUsuario() {}

    public static void iniciarSesion(Usuario usuario, int entidadId) {
        usuarioLogueado = usuario;
        entidadIdEspecifica = entidadId;
    }

    public static void setUsuarioLogueado(Usuario usuarioLogueado) {
        SesionUsuario.usuarioLogueado = usuarioLogueado;
    }

    public static void setEntidadIdEspecifica(int entidadIdEspecifica) {
        SesionUsuario.entidadIdEspecifica = entidadIdEspecifica;
    }

    public static Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public static int getEntidadIdEspecifica() { // ID de Cliente o Protectora
        return entidadIdEspecifica;
    }

    public static boolean haySesionActiva() {
        return usuarioLogueado != null;
    }

    public static void cerrarSesion() {
        usuarioLogueado = null;
        entidadIdEspecifica = 0;
    }
}