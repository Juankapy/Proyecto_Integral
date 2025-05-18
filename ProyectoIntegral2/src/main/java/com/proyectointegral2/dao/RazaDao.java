package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.utils.ConexionDB;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RazaDao{

    public int crearRaza(Raza raza) throws SQLException {
        String sqlInsert = "INSERT INTO Raza (Nombre_Raza) VALUES (?)";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, raza.getNombreRaza());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("No se pudo crear la raza.");
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado.");
                }
            }
        }
    }

    public Raza obtenerRazaPorId(int idRaza) throws SQLException {
        String sql = "SELECT * FROM RAZA WHERE ID_RAZA = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idRaza);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Raza raza = new Raza();
                    raza.setIdRaza(rs.getInt("ID_RAZA"));
                    raza.setNombreRaza(rs.getString("NOMBRE_RAZA"));
                    return raza;
                }
            }
        }
        return null;
    }

    public Raza obtenerRazaPorNombre(String nombreRaza) throws SQLException {
        String sql = "SELECT * FROM Raza WHERE Nombre_Raza = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreRaza);
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
        String sql = "SELECT * FROM Raza ORDER BY Nombre_Raza";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                razas.add(mapResultSetToRaza(rs));
            }
        }
        return razas;
    }

    public boolean actualizarRaza(Raza raza) throws SQLException {
        String sql = "UPDATE Raza SET Nombre_Raza = ? WHERE ID_Raza = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, raza.getNombreRaza());
            pstmt.setInt(2, raza.getIdRaza());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarRaza(int idRaza) throws SQLException {
        String sql = "DELETE FROM Raza WHERE ID_Raza = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idRaza);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Raza mapResultSetToRaza(ResultSet rs) throws SQLException {
        Raza raza = new Raza();
        raza.setIdRaza(rs.getInt("ID_Raza"));
        raza.setNombreRaza(rs.getString("Nombre_Raza"));
        return raza;
    }
}