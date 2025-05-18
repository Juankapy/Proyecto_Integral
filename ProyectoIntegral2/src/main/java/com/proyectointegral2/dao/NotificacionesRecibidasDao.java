package com.proyectointegral2.dao;

import com.proyectointegral2.Model.*;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesRecibidasDao {

    public boolean asignarNotificacionAUsuario(int idUsuario, int idNotificacion) throws SQLException {
        String sql = "INSERT INTO Notificaciones_Recibidas (ID_Usuario, ID_Notificacion) VALUES (?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            pstmt.setInt(2, idNotificacion);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean desasignarNotificacionDeUsuario(int idUsuario, int idNotificacion) throws SQLException {
        String sql = "DELETE FROM Notificaciones_Recibidas WHERE ID_Usuario = ? AND ID_Notificacion = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            pstmt.setInt(2, idNotificacion);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Notificacion> obtenerNotificacionesPorUsuario(int idUsuario) throws SQLException {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT n.* FROM Notificacion n " +
                "JOIN Notificaciones_Recibidas nr ON n.ID_Notificacion = nr.ID_Notificacion " +
                "WHERE nr.ID_Usuario = ? ORDER BY n.Fecha DESC, n.ID_Notificacion DESC";
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

    public List<Notificacion> obtenerNotificacionesNoLeidasPorUsuario(int idUsuario) throws SQLException {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT n.* FROM Notificacion n " +
                "JOIN Notificaciones_Recibidas nr ON n.ID_Notificacion = nr.ID_Notificacion " +
                "WHERE nr.ID_Usuario = ? AND n.Estado = 'No Leída' " +
                "ORDER BY n.Fecha DESC, n.ID_Notificacion DESC";
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

    public boolean marcarComoLeida(int idUsuario, int idNotificacion) throws SQLException {
        String sqlCheck = "SELECT 1 FROM Notificaciones_Recibidas WHERE ID_Usuario = ? AND ID_Notificacion = ?";
        String sqlUpdate = "UPDATE Notificacion SET Estado = 'Leída' WHERE ID_Notificacion = ?";
        try (Connection conn = ConexionDB.getConnection()) {
            boolean recibida = false;
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                pstmtCheck.setInt(1, idUsuario);
                pstmtCheck.setInt(2, idNotificacion);
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next()) {
                        recibida = true;
                    }
                }
            }
            if (recibida) {
                try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    pstmtUpdate.setInt(1, idNotificacion);
                    return pstmtUpdate.executeUpdate() > 0;
                }
            } else {
                return false;
            }
        }
    }

    public boolean marcarTodasComoLeidasUsuario(int idUsuario) throws SQLException {
        String sql = "UPDATE Notificacion SET Estado = 'Leída' " +
                "WHERE ID_Notificacion IN (SELECT ID_Notificacion FROM Notificaciones_Recibidas WHERE ID_Usuario = ?) " +
                "AND Estado = 'No Leída'";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            pstmt.executeUpdate();
            return true;
        }
    }

    private Notificacion mapResultSetToNotificacion(ResultSet rs) throws SQLException {
        Notificacion notificacion = new Notificacion();
        notificacion.setIdNotificacion(rs.getInt("ID_Notificacion"));
        notificacion.setFecha(rs.getDate("Fecha").toLocalDate());
        notificacion.setDescripcion(rs.getString("Descripcion"));
        notificacion.setEstado(rs.getString("Estado"));
        return notificacion;
    }
}