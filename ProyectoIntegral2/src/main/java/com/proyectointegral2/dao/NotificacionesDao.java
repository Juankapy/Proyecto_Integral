package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Notificacion;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesDao {

    public boolean crearNotificacion(Notificacion notificacion) throws SQLException {
        String sql = "INSERT INTO Notificaciones (ID_Usuario, Mensaje, Fecha, Leida) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, notificacion.getIdUsuario());
            pstmt.setString(2, notificacion.getMensaje());
            pstmt.setTimestamp(3, Timestamp.valueOf(notificacion.getFecha()));
            pstmt.setBoolean(4, notificacion.isLeida());
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Notificacion> obtenerNotificacionesPorUsuario(int idUsuario) throws SQLException {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT * FROM Notificaciones WHERE ID_Usuario = ? ORDER BY Fecha DESC";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    notificaciones.add(mapResultSetToNotificacion(rs));
                }
            }
        }
        return notificaciones;
    }

    public boolean marcarComoLeida(int idNotificacion) throws SQLException {
        String sql = "UPDATE Notificaciones SET Leida = 1 WHERE ID_Notificacion = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idNotificacion);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Notificacion mapResultSetToNotificacion(ResultSet rs) throws SQLException {
        Notificacion notificacion = new Notificacion();
        notificacion.setIdNotificacion(rs.getInt("ID_Notificacion"));
        notificacion.setIdUsuario(rs.getInt("ID_Usuario"));
        notificacion.setMensaje(rs.getString("Mensaje"));
        notificacion.setFecha(rs.getTimestamp("Fecha").toLocalDateTime());
        notificacion.setLeida(rs.getBoolean("Leida"));
        return notificacion;
    }
}