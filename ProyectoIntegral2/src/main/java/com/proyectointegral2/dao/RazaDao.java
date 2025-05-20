package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RazaDao {

    public int crearRaza(Raza raza) throws SQLException {
        String sql = "BEGIN INSERT INTO RAZA (NOMBRE_RAZA) VALUES (?) RETURNING ID_RAZA INTO ?; END;";
        Connection conn = null;
        CallableStatement cstmt = null;
        int nuevoIdRaza = -1;

        try {
            conn = ConexionDB.getConnection();

            cstmt = conn.prepareCall(sql);
            cstmt.setString(1, raza.getNombreRaza());

            cstmt.registerOutParameter(2, Types.NUMERIC);

            cstmt.execute();

            nuevoIdRaza = cstmt.getInt(2);

            if (nuevoIdRaza > 0) {
                raza.setIdRaza(nuevoIdRaza);
            } else {
                throw new SQLException("No se pudo crear la raza u obtener el ID generado (RETURNING falló).");
            }
            return nuevoIdRaza;

        } catch (SQLException e) {
            System.err.println("Error SQL al crear raza: " + e.getMessage());
            throw e;
        } finally {
                if (cstmt != null) try { cstmt.close(); } catch (SQLException ex) { }
            if (conn != null) try { conn.close(); } catch (SQLException ex) { }
        }
    }

    public Raza obtenerRazaPorId(int idRaza) throws SQLException {
        String sql = "SELECT ID_RAZA, NOMBRE_RAZA FROM RAZA WHERE ID_RAZA = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idRaza);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRaza(rs);
                }
            }
        }
        return null;
    }

    public Raza obtenerRazaPorNombre(String nombreRaza) throws SQLException {
        String sql = "SELECT ID_RAZA, NOMBRE_RAZA FROM RAZA WHERE UPPER(NOMBRE_RAZA) = UPPER(?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreRaza.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRaza(rs);
                }
            }
        }
        return null;
    }

    public List<Raza> obtenerTodasLasRazas() throws SQLException {
        List<Raza> razas = new ArrayList<>();
        String sql = "SELECT ID_RAZA, NOMBRE_RAZA FROM RAZA ORDER BY NOMBRE_RAZA";
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                razas.add(mapResultSetToRaza(rs));
            }
        }
        return razas;
    }

    public boolean actualizarRaza(Raza raza) throws SQLException {
        String sql = "UPDATE RAZA SET NOMBRE_RAZA = ? WHERE ID_RAZA = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, raza.getNombreRaza());
            pstmt.setInt(2, raza.getIdRaza());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarRaza(int idRaza) throws SQLException {
        String sql = "DELETE FROM RAZA WHERE ID_RAZA = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idRaza);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Raza mapResultSetToRaza(ResultSet rs) throws SQLException {
        Raza raza = new Raza();
        raza.setIdRaza(rs.getInt("ID_RAZA"));
        raza.setNombreRaza(rs.getString("NOMBRE_RAZA"));
        return raza;
    }
}