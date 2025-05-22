package com.proyectointegral2.dao;

import com.proyectointegral2.Model.ReservaCita;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar las operaciones CRUD de Reservas de Citas en la base de datos.
 * Esta versión no incluye el campo 'Motivo'.
 */
public class ReservaCitaDao {

    // Nombres de columnas (constantes para evitar errores tipográficos y facilitar refactorización)
    private static final String COL_ID_RESERVA_CITA = "ID_RESERVA_CITA";
    private static final String COL_FECHA = "Fecha";
    private static final String COL_HORA = "Hora";

    private static final String COL_ID_CLIENTE = "ID_Cliente";
    private static final String COL_ID_PERRO = "ID_Perro";
    private static final String COL_ID_PROTECTORA = "ID_Protectora";
    private static final String COL_ESTADO_CITA = "EstadoCita";

    /**
     * Crea una nueva reserva de cita en la base de datos.
     * @param reserva El objeto ReservaCita con los datos a insertar.
     * @return El ID generado para la nueva reserva, o -1 si falla la creación.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public int crearReservaCita(ReservaCita reserva) throws SQLException {

        String sql = "INSERT INTO Reservas_Citas (" +
                COL_FECHA + ", " + COL_HORA + ", " +
                COL_ID_CLIENTE + ", " + COL_ID_PERRO + ", " + COL_ID_PROTECTORA + ", " +
                COL_ESTADO_CITA + ") VALUES (?, ?, ?, ?, ?, ?)"; // 6 placeholders

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDate(1, Date.valueOf(reserva.getFecha()));
            pstmt.setTime(2, Time.valueOf(reserva.getHora()));
            pstmt.setInt(3, reserva.getIdCliente());
            pstmt.setInt(4, reserva.getIdPerro());
            pstmt.setInt(5, reserva.getIdProtectora());
            pstmt.setString(6, reserva.getEstadoCita());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear la reserva, ninguna fila afectada.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Devuelve el ID generado
                } else {
                    throw new SQLException("No se pudo obtener el ID generado para la reserva.");
                }
            }
        }
    }

    /**
     * Obtiene una reserva de cita por su ID.
     * @param idReserva El ID de la reserva a buscar.
     * @return El objeto ReservaCita si se encuentra, o null si no.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public ReservaCita obtenerReservaPorId(int idReserva) throws SQLException {
        String sql = "SELECT * FROM Reservas_Citas WHERE " + COL_ID_RESERVA_CITA + " = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReserva);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservaCita(rs);
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todas las reservas de citas para un cliente específico.
     * @param idCliente El ID del cliente.
     * @return Una lista de objetos ReservaCita.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<ReservaCita> obtenerReservasPorCliente(int idCliente) throws SQLException {
        List<ReservaCita> reservas = new ArrayList<>();

        String sql = "SELECT rc.*, p.Nombre AS NombrePerro " +
                "FROM Reservas_Citas rc " +
                "JOIN Perros p ON rc." + COL_ID_PERRO + " = p.ID_Perro " +
                "WHERE rc." + COL_ID_CLIENTE + " = ? " +
                "ORDER BY rc." + COL_FECHA + " DESC, rc." + COL_HORA + " DESC";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ReservaCita reserva = mapResultSetToReservaCita(rs);

                    if (reserva != null && rs.getString("NombrePerro") != null) {

                    }
                    reservas.add(reserva);
                }
            }
        }
        return reservas;
    }

    /**
     * Obtiene todas las reservas de citas para una protectora específica.
     * @param idProtectora El ID de la protectora.
     * @return Una lista de objetos ReservaCita.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<ReservaCita> obtenerReservasPorProtectora(int idProtectora) throws SQLException {
        List<ReservaCita> reservas = new ArrayList<>();
        String sql = "SELECT * FROM Reservas_Citas WHERE " + COL_ID_PROTECTORA + " = ? ORDER BY " +
                COL_FECHA + " DESC, " + COL_HORA + " DESC";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProtectora);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapResultSetToReservaCita(rs));
                }
            }
        }
        return reservas;
    }

    /**
     * Actualiza una reserva de cita existente en la base de datos.
     * @param reserva El objeto ReservaCita con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public boolean actualizarReservaCita(ReservaCita reserva) throws SQLException {
        // Motivo ha sido eliminado de la actualización
        String sql = "UPDATE Reservas_Citas SET " +
                COL_FECHA + " = ?, " + COL_HORA + " = ?, " +
                COL_ID_CLIENTE + " = ?, " + COL_ID_PERRO + " = ?, " +
                COL_ID_PROTECTORA + " = ?, " + COL_ESTADO_CITA + " = ? " + // 6 campos a actualizar
                "WHERE " + COL_ID_RESERVA_CITA + " = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(reserva.getFecha()));
            pstmt.setTime(2, Time.valueOf(reserva.getHora()));
            // Índice 3 ahora es ID_Cliente
            pstmt.setInt(3, reserva.getIdCliente());
            pstmt.setInt(4, reserva.getIdPerro());
            pstmt.setInt(5, reserva.getIdProtectora());
            pstmt.setString(6, reserva.getEstadoCita());
            pstmt.setInt(7, reserva.getIdReserva());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina una reserva de cita de la base de datos por su ID.
     * @param idReserva El ID de la reserva a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public boolean eliminarReservaCita(int idReserva) throws SQLException {
        String sql = "DELETE FROM Reservas_Citas WHERE " + COL_ID_RESERVA_CITA + " = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReserva);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Mapea una fila de un ResultSet a un objeto ReservaCita.
     * @param rs El ResultSet del cual leer los datos.
     * @return Un objeto ReservaCita poblado.
     * @throws SQLException Si ocurre un error al acceder a los datos del ResultSet.
     */
    private ReservaCita mapResultSetToReservaCita(ResultSet rs) throws SQLException {
        ReservaCita reserva = new ReservaCita();
        reserva.setIdReserva(rs.getInt(COL_ID_RESERVA_CITA));
        reserva.setFecha(rs.getDate(COL_FECHA).toLocalDate());
        reserva.setHora(rs.getTime(COL_HORA).toLocalTime());
        // MOTIVO ELIMINADO del mapeo
        reserva.setIdCliente(rs.getInt(COL_ID_CLIENTE));
        reserva.setIdPerro(rs.getInt(COL_ID_PERRO));
        reserva.setIdProtectora(rs.getInt(COL_ID_PROTECTORA));
        reserva.setEstadoCita(rs.getString(COL_ESTADO_CITA));
        return reserva;
    }

    public List<ReservaCita> obtenerCitasPorCliente(int idClienteActual) {
        // Implementación de ejemplo, debe ser reemplazada por la lógica real
        return new ArrayList<>();
    }
}