package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Notificacion; // Asegúrate que Notificacion.java esté actualizado
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesDao {

    public int crearNotificacion(Notificacion notificacion) throws SQLException {
        String sql = "INSERT INTO NOTIFICACION (MENSAJE, TIPO_NOTIFICACION, ID_ENTIDAD_RELACIONADA, ENTIDAD_TIPO, FECHA_GENERACION) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int nuevoIdNotificacion = -1;

        try {
            conn = ConexionDB.getConnection();
            pstmt = conn.prepareStatement(sql, new String[]{"ID_NOTIFICACION"});

            pstmt.setString(1, notificacion.getMensaje());
            pstmt.setString(2, notificacion.getTipoNotificacion());

            if (notificacion.getIdEntidadRelacionada() != null) {
                pstmt.setInt(3, notificacion.getIdEntidadRelacionada());
            } else {
                pstmt.setNull(3, Types.NUMERIC);
            }
            pstmt.setString(4, notificacion.getEntidadTipo());
            pstmt.setTimestamp(5, Timestamp.valueOf(notificacion.getFechaGeneracion()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear la notificación, ninguna fila afectada.");
            }

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                nuevoIdNotificacion = generatedKeys.getInt(1);
                notificacion.setIdNotificacion(nuevoIdNotificacion);
            } else {
                System.out.println("Advertencia: Notificación insertada, pero no se pudo recuperar el ID generado.");
            }
            return nuevoIdNotificacion;

        } catch (SQLException e) {
            System.err.println("Error SQL al crear notificación: " + e.getMessage());
            throw e;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { }
            if (conn != null) try { conn.close(); } catch (SQLException e) { }
        }
    }

    public Notificacion obtenerNotificacionPorId(int idNotificacion) throws SQLException {
        String sql = "SELECT * FROM NOTIFICACION WHERE ID_NOTIFICACION = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idNotificacion);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNotificacion(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al obtener notificación por ID: " + e.getMessage());
            throw e;
        }
        return null;
    }

    private Notificacion mapResultSetToNotificacion(ResultSet rs) throws SQLException {
        Notificacion notificacion = new Notificacion();
        notificacion.setIdNotificacion(rs.getInt("ID_NOTIFICACION"));
        notificacion.setFechaGeneracion(rs.getTimestamp("FECHA_GENERACION").toLocalDateTime());
        notificacion.setMensaje(rs.getString("MENSAJE"));
        notificacion.setTipoNotificacion(rs.getString("TIPO_NOTIFICACION"));
        // ID_ENTIDAD_RELACIONADA puede ser NULL
        int idEntidad = rs.getInt("ID_ENTIDAD_RELACIONADA");
        if (!rs.wasNull()) {
            notificacion.setIdEntidadRelacionada(idEntidad);
        }
        notificacion.setEntidadTipo(rs.getString("ENTIDAD_TIPO"));

        return notificacion;
    }
}