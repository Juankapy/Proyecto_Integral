package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Patologia;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatologiaDao  {

    public int crearPatologia(Patologia patologia) throws SQLException {
        String sqlInsert = "INSERT INTO Patologia (Nombre) VALUES (?)";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, patologia.getNombre());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear la patología.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado.");
                }
            }
        }
    }

    public Patologia obtenerPatologiaPorId(int idPatologia) throws SQLException {
        String sql = "SELECT * FROM Patologia WHERE ID_Patologia = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPatologia);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatologia(rs);
                }
            }
        }
        return null;
    }

    public Patologia obtenerPatologiaPorNombre(String nombrePatologia) throws SQLException {
        String sql = "SELECT * FROM Patologia WHERE Nombre = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombrePatologia);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatologia(rs);
                }
            }
        }
        return null;
    }

    public List<Patologia> obtenerTodasLasPatologias() throws SQLException {
        List<Patologia> patologias = new ArrayList<>();
        String sql = "SELECT * FROM Patologia ORDER BY Nombre";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                patologias.add(mapResultSetToPatologia(rs));
            }
        }
        return patologias;
    }

    public boolean actualizarPatologia(Patologia patologia) throws SQLException {
        String sql = "UPDATE Patologia SET Nombre = ? WHERE ID_Patologia = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patologia.getNombre());
            pstmt.setInt(2, patologia.getIdPatologia());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarPatologia(int idPatologia) throws SQLException {
        String sql = "DELETE FROM Patologia WHERE ID_Patologia = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPatologia);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Patologia mapResultSetToPatologia(ResultSet rs) throws SQLException {
        Patologia patologia = new Patologia();
        patologia.setIdPatologia(rs.getInt("ID_Patologia"));
        patologia.setNombre(rs.getString("Nombre"));
        return patologia;
    }
}