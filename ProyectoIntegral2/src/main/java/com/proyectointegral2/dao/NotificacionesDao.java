package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Notificacion; // Asegúrate que Notificacion.java esté actualizado
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesDao {

    /**
     * Crea una nueva notificación genérica en la tabla NOTIFICACION.
     * El ID_NOTIFICACION se genera automáticamente.
     * La asignación a un usuario específico se haría a través de NotificacionesRecibidasDao.
     * @param notificacion Objeto Notificacion con los datos a insertar (mensaje, tipo, etc.).
     * @return El ID de la notificación generada si la creación es exitosa, -1 en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public int crearNotificacion(Notificacion notificacion) throws SQLException {
        // Columnas según el DDL corregido para la tabla NOTIFICACION
        String sql = "INSERT INTO NOTIFICACION (MENSAJE, TIPO_NOTIFICACION, ID_ENTIDAD_RELACIONADA, ENTIDAD_TIPO, FECHA_GENERACION) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int nuevoIdNotificacion = -1;

        try {
            conn = ConexionDB.getConnection();
            // Pedir que devuelva la clave generada "ID_NOTIFICACION"
            pstmt = conn.prepareStatement(sql, new String[]{"ID_NOTIFICACION"});

            pstmt.setString(1, notificacion.getMensaje());
            pstmt.setString(2, notificacion.getTipoNotificacion());

            if (notificacion.getIdEntidadRelacionada() != null) {
                pstmt.setInt(3, notificacion.getIdEntidadRelacionada());
            } else {
                pstmt.setNull(3, Types.NUMERIC);
            }
            pstmt.setString(4, notificacion.getEntidadTipo());
            pstmt.setTimestamp(5, Timestamp.valueOf(notificacion.getFechaGeneracion())); // Usa fechaGeneracion

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear la notificación, ninguna fila afectada.");
            }

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                nuevoIdNotificacion = generatedKeys.getInt(1);
                notificacion.setIdNotificacion(nuevoIdNotificacion); // Actualizar el objeto
            } else {
                System.out.println("Advertencia: Notificación insertada, pero no se pudo recuperar el ID generado.");
                // Si la inserción tuvo éxito pero no se recuperó el ID, podría ser un problema de configuración
                // o del driver JDBC con Oracle para secuencias en DEFAULT.
            }
            return nuevoIdNotificacion;

        } catch (SQLException e) {
            System.err.println("Error SQL al crear notificación: " + e.getMessage());
            throw e;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { /* ignore */ }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { /* ignore */ }
            if (conn != null) try { conn.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    /**
     * Obtiene una notificación por su ID.
     * @param idNotificacion El ID de la notificación.
     * @return El objeto Notificacion si se encuentra, null en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
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


    // Este método ya no tiene sentido en ESTE DAO porque la tabla NOTIFICACION
    // no tiene ID_USUARIO ni LEIDA directamente. Esos están en NOTIFICACIONES_RECIBIDAS.
    // public List<Notificacion> obtenerNotificacionesPorUsuario(int idUsuario) throws SQLException { ... }

    // Este método tampoco tiene sentido aquí. Se marcaría como leída en NOTIFICACIONES_RECIBIDAS.
    // public boolean marcarComoLeida(int idNotificacion) throws SQLException { ... }


    /**
     * Mapea un ResultSet a un objeto Notificacion.
     * Solo mapea los campos de la tabla NOTIFICACION.
     * Los campos como 'leida' o 'idUsuarioDestino' vendrían de un JOIN con NOTIFICACIONES_RECIBIDAS.
     */
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

        // Los campos leida, fechaLeida, idUsuarioDestino NO se mapean aquí
        // porque no están en la tabla NOTIFICACION.
        return notificacion;
    }
}