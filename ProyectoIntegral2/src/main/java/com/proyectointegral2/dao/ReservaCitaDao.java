package com.proyectointegral2.dao;

import com.proyectointegral2.Model.ReservaCita;
import com.proyectointegral2.utils.ConexionDB;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaCitaDao {

    public int crearReservaCita(ReservaCita reserva) throws SQLException {
        String sql = "INSERT INTO Reservas_Citas (Fecha, Hora, Motivo, ID_Cliente, ID_Protectora) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, Date.valueOf(reserva.getFecha()));
            pstmt.setTime(2, Time.valueOf(reserva.getHora()));
            pstmt.setString(3, reserva.getMotivo());
            pstmt.setInt(4, reserva.getIdCliente());
            pstmt.setInt(5, reserva.getIdProtectora());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("No se pudo crear la reserva.");
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado.");
                }
            }
        }
    }

    public ReservaCita obtenerReservaPorId(int idReserva) throws SQLException {
        String sql = "SELECT * FROM Reservas_Citas WHERE ID_RESERVA_CITA= ?";
        try (Connection conn = ConexionDB.getConnection() ;
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

    public List<ReservaCita> obtenerReservasPorCliente(int idCliente) throws SQLException {
        List<ReservaCita> reservas = new ArrayList<>();
        String sql = "SELECT * FROM Reservas_Citas WHERE ID_Cliente = ? ORDER BY Fecha DESC, Hora DESC";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservas.add(mapResultSetToReservaCita(rs));
                }
            }
        }
        return reservas;
    }

    public List<ReservaCita> obtenerReservasPorProtectora(int idProtectora) throws SQLException {
        List<ReservaCita> reservas = new ArrayList<>();
        String sql = "SELECT * FROM RESERVAS_CITAS WHERE ID_Protectora = ? ORDER BY Fecha DESC, Hora DESC";
        try (Connection conn = ConexionDB.getConnection() ;
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
        String sql = "UPDATE RESERVAS_CITAS SET Fecha = ?, Hora = ?, Motivo = ?, ID_Cliente = ?, ID_Protectora = ? WHERE ID_RESERVA_CITA = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(reserva.getFecha()));
            pstmt.setTime(2, Time.valueOf(reserva.getHora()));
            pstmt.setString(3, reserva.getMotivo());
            pstmt.setInt(4, reserva.getIdCliente());
            pstmt.setInt(5, reserva.getIdProtectora());
            pstmt.setInt(6, reserva.getIdReserva());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarReservaCita(int idReserva) throws SQLException {
        String sql = "DELETE FROM Reservas_Citas WHERE ID_RESERVA_CITA = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idReserva);
            return pstmt.executeUpdate() > 0;
        }
    }

    private ReservaCita mapResultSetToReservaCita(ResultSet rs) throws SQLException {
        ReservaCita reserva = new ReservaCita();
        reserva.setIdReserva(rs.getInt("ID_Reserva"));
        reserva.setFecha(rs.getDate("Fecha").toLocalDate());
        reserva.setHora(rs.getTime("Hora").toLocalTime());
        reserva.setMotivo(rs.getString("Motivo"));
        reserva.setIdCliente(rs.getInt("ID_Cliente"));
        reserva.setIdProtectora(rs.getInt("ID_Protectora"));
        return reserva;
    }

    public List<ReservaCita> obtenerCitasPorCliente(int idCliente) throws SQLException {
        List<ReservaCita> citas = new ArrayList<>();
        String sql = "SELECT * FROM RESERVAS_CITAS WHERE ID_Cliente = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ReservaCita cita = new ReservaCita();
                    cita.setIdReserva(rs.getInt("ID_Reserva"));
                    cita.setIdCliente(rs.getInt("ID_Cliente"));
                    cita.setIdPerro(rs.getInt("ID_Perro"));
                    cita.setFecha(rs.getDate("Fecha").toLocalDate());
                    cita.setHora(rs.getTime("Hora").toLocalTime());
                    cita.setMotivo(rs.getString("Motivo"));
                    // Agrega aquí otros campos si es necesario
                    citas.add(cita);
                }
            }
        }
        return citas;
    }
}