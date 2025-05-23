package com.proyectointegral2.Model; // O tu paquete correcto

public class SesionUsuario {
    private static Usuario usuarioLogueado;
    private static int entidadIdEspecifica; // ID_CLIENTE o ID_PROTECTORA

    public static void iniciarSesion(Usuario usuario, int idEntidad) {
        usuarioLogueado = usuario;
        entidadIdEspecifica = idEntidad;
        System.out.println("DEBUG SesionUsuario.iniciarSesion: Usuario: " +
                (usuario != null ? usuario.getNombreUsu() : "null") +
                ", Rol: " + (usuario != null ? usuario.getRol() : "null") +
                ", EntidadID guardada: " + idEntidad);
    }

    public static Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public static int getEntidadIdEspecifica() {
        System.out.println("DEBUG SesionUsuario.getEntidadIdEspecifica: Devolviendo EntidadID: " + entidadIdEspecifica);
        return entidadIdEspecifica;
    }

    public static void cerrarSesion() {
        System.out.println("DEBUG SesionUsuario.cerrarSesion: Cerrando sesión. Usuario anterior: " +
                (usuarioLogueado != null ? usuarioLogueado.getNombreUsu() : "null") +
                ", EntidadID anterior: " + entidadIdEspecifica);
        usuarioLogueado = null;
        entidadIdEspecifica = 0; // Importante resetear a 0
    }

    public static boolean haySesionActiva() {
        return usuarioLogueado != null;
    }
}