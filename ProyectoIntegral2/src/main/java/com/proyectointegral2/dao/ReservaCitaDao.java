package com.proyectointegral2.dao;

import com.proyectointegral2.Model.BandejaCita;
import com.proyectointegral2.Model.RegistroCitaInfo;
import com.proyectointegral2.Model.ReservaCita;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaCitaDao {

    // Nombres de columnas
    private static final String COL_ID_RESERVA_CITA = "ID_RESERVA_CITA";
    private static final String COL_FECHA = "Fecha";
    private static final String COL_HORA = "Hora";
    private static final String COL_ID_CLIENTE = "ID_Cliente";
    private static final String COL_ID_PERRO = "ID_Perro";
    private static final String COL_ID_PROTECTORA = "ID_Protectora";
    private static final String COL_ESTADO_CITA = "EstadoCita";

    public int crearReservaCita(ReservaCita reserva) throws SQLException {
        String sql = "INSERT INTO Reservas_Citas (" +
                COL_FECHA + ", " + COL_HORA + ", " +
                COL_ID_CLIENTE + ", " + COL_ID_PERRO + ", " + COL_ID_PROTECTORA + ", " +
                COL_ESTADO_CITA + ") VALUES (?, ?, ?, ?, ?, ?)";
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
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado para la reserva.");
                }
            }
        }
    }


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
                    reservas.add(reserva);
                }
            }
        }
        return reservas;
    }

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

    public boolean actualizarReservaCita(ReservaCita reserva) throws SQLException {
        String sql = "UPDATE Reservas_Citas SET " +
                COL_FECHA + " = ?, " + COL_HORA + " = ?, " +
                COL_ID_CLIENTE + " = ?, " + COL_ID_PERRO + " = ?, " +
                COL_ID_PROTECTORA + " = ?, " + COL_ESTADO_CITA + " = ? " +
                "WHERE " + COL_ID_RESERVA_CITA + " = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(reserva.getFecha()));
            pstmt.setTime(2, Time.valueOf(reserva.getHora()));
            pstmt.setInt(3, reserva.getIdCliente());
            pstmt.setInt(4, reserva.getIdPerro());
            pstmt.setInt(5, reserva.getIdProtectora());
            pstmt.setString(6, reserva.getEstadoCita());
            pstmt.setInt(7, reserva.getIdReserva());

            return pstmt.executeUpdate() > 0;
        }
    }

    // Método para mapear ResultSet a ReservaCita
    private ReservaCita mapResultSetToReservaCita(ResultSet rs) throws SQLException {
        ReservaCita reserva = new ReservaCita();
        reserva.setIdReserva(rs.getInt(COL_ID_RESERVA_CITA));
        reserva.setFecha(rs.getDate(COL_FECHA).toLocalDate());
        reserva.setHora(rs.getTime(COL_HORA).toLocalTime());
        reserva.setIdCliente(rs.getInt(COL_ID_CLIENTE));
        reserva.setIdPerro(rs.getInt(COL_ID_PERRO));
        reserva.setIdProtectora(rs.getInt(COL_ID_PROTECTORA));
        reserva.setEstadoCita(rs.getString(COL_ESTADO_CITA));
        // Si tienes el campo nombrePerro en ReservaCita, puedes setearlo aquí:
        try {
            reserva.getClass().getMethod("setNombrePerro", String.class)
                    .invoke(reserva, rs.getString("NombrePerro"));
        } catch (Exception ignored) {}
        return reserva;
    }

    // Ya implementado, no modificar
    private BandejaCita mapResultSetToBandejaCita(ResultSet rs) throws SQLException {
        BandejaCita cita = new BandejaCita();
        cita.setNombreCliente(rs.getString("nombreCliente"));
        cita.setNombrePerro(rs.getString("nombrePerro"));
        cita.setFecha(rs.getDate("Fecha").toLocalDate());
        cita.setHora(rs.getTime("Hora").toLocalTime());
        return cita;
    }

    public List<ReservaCita> obtenerCitasPorCliente(int idClienteActual) {
            List<ReservaCita> reservas = new ArrayList<>();
            String sql = "SELECT rc.*, p.Nombre AS NombrePerro " +
                    "FROM Reservas_Citas rc " +
                    "JOIN Perros p ON rc.ID_Perro = p.ID_Perro " +
                    "WHERE rc.ID_Cliente = ? " +
                    "ORDER BY rc.Fecha DESC, rc.Hora DESC";
            try (Connection conn = ConexionDB.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, idClienteActual);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        ReservaCita reserva = mapResultSetToReservaCita(rs);
                        reservas.add(reserva);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return reservas;
        }

    public List<RegistroCitaInfo> obtenerCitasParaTablaProtectora(int idProtectoraActual) {
        List<RegistroCitaInfo> citas = new ArrayList<>();
        String sql = "SELECT rc.*, p.Nombre AS NombrePerro, c.Nombre AS NombreCliente " +
                "FROM Reservas_Citas rc " +
                "JOIN Perros p ON rc.ID_Perro = p.ID_Perro " +
                "JOIN Cliente c ON rc.ID_Cliente = c.ID_Cliente " +
                "WHERE rc.ID_Protectora = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idProtectoraActual);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RegistroCitaInfo cita = new RegistroCitaInfo(
                            rs.getString("NombreCliente"),
                            rs.getString("NombrePerro"),
                            rs.getDate(COL_FECHA).toString(),
                            rs.getTime(COL_HORA).toString(),
                            rs.getString("TipoServicio"),
                            rs.getString("Veterinario")
                    );
                    citas.add(cita);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }
}