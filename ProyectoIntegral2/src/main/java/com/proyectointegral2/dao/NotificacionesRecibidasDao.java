package com.proyectointegral2.dao;

    import com.proyectointegral2.Model.Notificacion;
    import com.proyectointegral2.utils.ConexionDB;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class NotificacionesRecibidasDao {

        public boolean asignarNotificacionAUsuario(int idUsuario, int idNotificacion) throws SQLException {
            String sql = "INSERT INTO NOTIFICACIONES_RECIBIDAS (ID_USUARIO, ID_NOTIFICACION, LEIDA) VALUES (?, ?, 'N')";
            try (Connection conn = ConexionDB.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idUsuario);
                pstmt.setInt(2, idNotificacion);
                return pstmt.executeUpdate() > 0;
            }
        }

        public boolean desasignarNotificacionDeUsuario(int idUsuario, int idNotificacion) throws SQLException {
            String sql = "DELETE FROM NOTIFICACIONES_RECIBIDAS WHERE ID_USUARIO = ? AND ID_NOTIFICACION = ?";
            try (Connection conn = ConexionDB.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idUsuario);
                pstmt.setInt(2, idNotificacion);
                return pstmt.executeUpdate() > 0;
            }
        }

        public List<Notificacion> obtenerNotificacionesPorUsuario(int idUsuario) throws SQLException {
            List<Notificacion> notificaciones = new ArrayList<>();
            String sql = "SELECT n.ID_NOTIFICACION, n.FECHA_GENERACION, n.MENSAJE, n.TIPO_NOTIFICACION, " +
                    "n.ID_ENTIDAD_RELACIONADA, n.ENTIDAD_TIPO, " +
                    "nr.ID_USUARIO, nr.LEIDA, nr.FECHA_LEIDA " +
                    "FROM NOTIFICACION n " +
                    "JOIN NOTIFICACIONES_RECIBIDAS nr ON n.ID_NOTIFICACION = nr.ID_NOTIFICACION " +
                    "WHERE nr.ID_USUARIO = ? " +
                    "ORDER BY n.FECHA_GENERACION DESC, n.ID_NOTIFICACION DESC";

            try (Connection conn = ConexionDB.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, idUsuario);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        notificaciones.add(mapResultSetToNotificacionConInfoRecibida(rs));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error al obtener notificaciones por usuario: " + e.getMessage());
                throw e;
            }
            return notificaciones;
        }

        public List<Notificacion> obtenerNotificacionesNoLeidasPorUsuario(int idUsuario) throws SQLException {
            List<Notificacion> notificaciones = new ArrayList<>();
            String sql = "SELECT n.ID_NOTIFICACION, n.FECHA_GENERACION, n.MENSAJE, n.TIPO_NOTIFICACION, " +
                    "n.ID_ENTIDAD_RELACIONADA, n.ENTIDAD_TIPO, " +
                    "nr.ID_USUARIO, nr.LEIDA, nr.FECHA_LEIDA " +
                    "FROM NOTIFICACION n " +
                    "JOIN NOTIFICACIONES_RECIBIDAS nr ON n.ID_NOTIFICACION = nr.ID_NOTIFICACION " +
                    "WHERE nr.ID_USUARIO = ? AND nr.LEIDA = 'N' " +
                    "ORDER BY n.FECHA_GENERACION DESC, n.ID_NOTIFICACION DESC";
            try (Connection conn = ConexionDB.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idUsuario);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        notificaciones.add(mapResultSetToNotificacionConInfoRecibida(rs));
                    }
                }
            }
            return notificaciones;
        }

        public boolean marcarComoLeida(int idUsuario, int idNotificacion) throws SQLException {
            String sqlUpdate = "UPDATE NOTIFICACIONES_RECIBIDAS SET LEIDA = 'S', FECHA_LEIDA = SYSTIMESTAMP " +
                    "WHERE ID_USUARIO = ? AND ID_NOTIFICACION = ?";
            try (Connection conn = ConexionDB.getConnection();
                 PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setInt(1, idUsuario);
                pstmtUpdate.setInt(2, idNotificacion);
                return pstmtUpdate.executeUpdate() > 0;
            }
        }

        public boolean marcarTodasComoLeidasUsuario(int idUsuario) throws SQLException {
            String sql = "UPDATE NOTIFICACIONES_RECIBIDAS SET LEIDA = 'S', FECHA_LEIDA = SYSTIMESTAMP " +
                    "WHERE ID_USUARIO = ? AND LEIDA = 'N'";
            try (Connection conn = ConexionDB.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idUsuario);
                pstmt.executeUpdate();
                return true;
            }
        }

        private Notificacion mapResultSetToNotificacionConInfoRecibida(ResultSet rs) throws SQLException {
            Notificacion notificacion = new Notificacion();

            notificacion.setIdNotificacion(rs.getInt("ID_NOTIFICACION"));
            Timestamp fechaGeneracionTS = rs.getTimestamp("FECHA_GENERACION");
            if (fechaGeneracionTS != null) {
                notificacion.setFechaGeneracion(fechaGeneracionTS.toLocalDateTime());
            }
            notificacion.setMensaje(rs.getString("MENSAJE"));
            notificacion.setTipoNotificacion(rs.getString("TIPO_NOTIFICACION"));
            int idEntidad = rs.getInt("ID_ENTIDAD_RELACIONADA");
            if (!rs.wasNull()) {
                notificacion.setIdEntidadRelacionada(idEntidad);
            } else {
                notificacion.setIdEntidadRelacionada(null);
            }
            notificacion.setEntidadTipo(rs.getString("ENTIDAD_TIPO"));

            notificacion.setIdUsuarioDestino(rs.getInt("ID_USUARIO"));
            String leidaChar = rs.getString("LEIDA");
            notificacion.setLeida(leidaChar != null && leidaChar.equals("S"));

            Timestamp fechaLeidaTS = rs.getTimestamp("FECHA_LEIDA");
            if (fechaLeidaTS != null) {
                notificacion.setFechaLeida(fechaLeidaTS.toLocalDateTime());
            } else {
                notificacion.setFechaLeida(null);
            }
            return notificacion;
        }



    }