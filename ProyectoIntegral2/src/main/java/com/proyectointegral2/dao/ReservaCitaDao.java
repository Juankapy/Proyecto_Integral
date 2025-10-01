package com.proyectointegral2.dao;

import com.proyectointegral2.Model.BandejaCita;
import com.proyectointegral2.Model.RegistroCitaInfo;
import com.proyectointegral2.Model.ReservaCita;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservaCitaDao {

    private static final String COL_ID_RESERVA_CITA = "ID_RESERVA_CITA";
    private static final String COL_FECHA = "Fecha";
    private static final String COL_HORA = "Hora";
    private static final String COL_ID_CLIENTE = "ID_Cliente";
    private static final String COL_ID_PERRO = "ID_Perro";
    private static final String COL_ID_PROTECTORA = "ID_Protectora";
    private static final String COL_ESTADO_CITA = "Estado_Cita";
    private static final String COL_DONACION = "Donacion";

    public int crearReservaCita(ReservaCita reserva) throws SQLException {
        String sql = "INSERT INTO Reservas_Citas (" +
                COL_FECHA + ", " + COL_HORA + ", " +
                COL_DONACION + ", " + COL_ID_CLIENTE + ", " +
                COL_ID_PERRO + ", " + COL_ID_PROTECTORA + ", " +
                COL_ESTADO_CITA + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{COL_ID_RESERVA_CITA})) {

            pstmt.setDate(1, Date.valueOf(reserva.getFecha()));
            pstmt.setString(2, reserva.getHora().format(DateTimeFormatter.ofPattern("HH:mm")));
            pstmt.setDouble(3, reserva.getDonacion());
            pstmt.setInt(4, reserva.getIdCliente());
            pstmt.setInt(5, reserva.getIdPerro());
            pstmt.setInt(6, reserva.getIdProtectora());
            pstmt.setString(7, reserva.getEstadoCita());

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

    public void eliminarCitasPorPerroEstadosFijos(int idPerro) throws SQLException {
        System.out.println("SQL: DELETE FROM Reservas_Citas WHERE ID_Perro = " + idPerro + " AND Estado_Cita IN ('Pendiente', 'Confirmada', 'Cancelada')");
        String sql = "DELETE FROM Reservas_Citas WHERE ID_Perro = ? AND Estado_Cita IN ('Pendiente', 'Confirmada', 'Cancelada')";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPerro);
            int filasAfectadas = stmt.executeUpdate();
            System.out.println("Filas eliminadas: " + filasAfectadas);
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

    private ReservaCita mapResultSetToReservaCita(ResultSet rs) throws SQLException {
        ReservaCita reserva = new ReservaCita();
        reserva.setIdReserva(rs.getInt(COL_ID_RESERVA_CITA));
        reserva.setFecha(rs.getDate(COL_FECHA).toLocalDate());

        String horaStr = rs.getString(COL_HORA);
        if (horaStr != null) {
            DateTimeFormatter formatter = horaStr.length() == 5
                    ? DateTimeFormatter.ofPattern("HH:mm")
                    : DateTimeFormatter.ofPattern("HH:mm:ss");
            reserva.setHora(LocalTime.parse(horaStr, formatter));
        } else {
            reserva.setHora(null);
        }

        reserva.setIdCliente(rs.getInt(COL_ID_CLIENTE));
        reserva.setIdPerro(rs.getInt(COL_ID_PERRO));
        reserva.setIdProtectora(rs.getInt(COL_ID_PROTECTORA));
        reserva.setEstadoCita(rs.getString(COL_ESTADO_CITA));

        try {
            reserva.getClass().getMethod("setNombrePerro", String.class)
                    .invoke(reserva, rs.getString("NombrePerro"));
        } catch (Exception ignored) {}
        return reserva;
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


    public List<LocalTime> obtenerHorasReservadasPorPerroYFecha(int idPerro, LocalDate fechaSeleccionada) {
        List<LocalTime> horasReservadas = new ArrayList<>();
        String sql = "SELECT Hora FROM Reservas_Citas WHERE ID_Perro = ? AND Fecha = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPerro);
            pstmt.setDate(2, Date.valueOf(fechaSeleccionada));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    horasReservadas.add(rs.getTime("Hora").toLocalTime());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return horasReservadas;
    }

    public List<RegistroCitaInfo> obtenerInfoCitasPorProtectora(int idProtectora) {
        List<RegistroCitaInfo> lista = new ArrayList<>();
        String sql = "SELECT rc.ID_RESERVA_CITA, rc.Fecha, rc.Hora, p.Nombre AS NombrePerro, " +
                "c.Nombre AS NombreCliente, rc.Estado_Cita " +
                "FROM Reservas_Citas rc " +
                "JOIN Perros p ON rc.ID_Perro = p.ID_Perro " +
                "JOIN Cliente c ON rc.ID_Cliente = c.ID_Cliente " +
                "WHERE rc.ID_Protectora = ? " +
                "ORDER BY rc.Fecha DESC, rc.Hora DESC";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProtectora);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RegistroCitaInfo info = new RegistroCitaInfo();
                    info.setIdReservaCita(rs.getInt("ID_RESERVA_CITA"));
                    info.setFechaCita(rs.getDate("Fecha").toLocalDate());
                    String horaStr = rs.getString("Hora");
                    if (horaStr != null) {
                        DateTimeFormatter formatter = horaStr.length() == 5
                                ? DateTimeFormatter.ofPattern("HH:mm")
                                : DateTimeFormatter.ofPattern("HH:mm:ss");
                        info.setHoraCita(LocalTime.parse(horaStr, formatter));
                    }
                    info.setNombrePerro(rs.getString("NombrePerro"));
                    info.setNombreCliente(rs.getString("NombreCliente"));
                    info.setEstadoCita(rs.getString("Estado_Cita"));
                    lista.add(info);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizarEstadoCita(int idReservaCita, String nuevoEstado) {
        String sql = "UPDATE RESERVAS_CITAS SET ESTADO_CITA = ? WHERE ID_RESERVA_CITA = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idReservaCita);
            int filasActualizadas = ps.executeUpdate();
            return filasActualizadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}