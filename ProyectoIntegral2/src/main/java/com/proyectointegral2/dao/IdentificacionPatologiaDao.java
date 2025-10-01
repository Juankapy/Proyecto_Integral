package com.proyectointegral2.dao;

import com.proyectointegral2.Model.IdentificacionPatologia;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IdentificacionPatologiaDao {

    public boolean asignarPatologiaAPerro(int idPerro, int idPatologia, String notasEspecificas) throws SQLException {
        String sql = "INSERT INTO Identificacion_Patologias (ID_Perro, ID_Patologia, NOTAS_ESPECIFICAS) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPerro);
            pstmt.setInt(2, idPatologia);
            if (notasEspecificas != null && !notasEspecificas.trim().isEmpty()) {
                pstmt.setString(3, notasEspecificas);
            } else {
                pstmt.setNull(3, Types.VARCHAR);
            }
            return pstmt.executeUpdate() > 0;
        }
    }


    public boolean desasignarPatologiaDePerro(int idPerro, int idPatologia) throws SQLException {
        String sql = "DELETE FROM Identificacion_Patologias WHERE ID_Perro = ? AND ID_Patologia = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPerro);
            pstmt.setInt(2, idPatologia);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean actualizarNotasPatologiaPerro(int idPerro, int idPatologia, String nuevasNotas) throws SQLException {
        String sql = "UPDATE Identificacion_Patologias SET NOTAS_ESPECIFICAS = ? WHERE ID_Perro = ? AND ID_Patologia = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevasNotas);
            pstmt.setInt(2, idPerro);
            pstmt.setInt(3, idPatologia);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<IdentificacionPatologia> obtenerIdentificacionesPorPerro(int idPerro) throws SQLException {
        List<IdentificacionPatologia> identificaciones = new ArrayList<>();
        String sql = "SELECT ID_Perro, ID_Patologia, NOTAS_ESPECIFICAS FROM Identificacion_Patologias WHERE ID_Perro = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPerro);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    identificaciones.add(mapResultSetToIdentificacionPatologia(rs));
                }
            }
        }
        return identificaciones;
    }


    public List<IdentificacionPatologia> obtenerPerrosPorPatologia(int idPatologia) throws SQLException {
        List<IdentificacionPatologia> identificaciones = new ArrayList<>();
        String sql = "SELECT * FROM Identificacion_Patologias WHERE ID_Patologia = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPatologia);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    identificaciones.add(mapResultSetToIdentificacionPatologia(rs));
                }
            }
        }
        return identificaciones;
    }

    private IdentificacionPatologia mapResultSetToIdentificacionPatologia(ResultSet rs) throws SQLException {
        IdentificacionPatologia ip = new IdentificacionPatologia();
        ip.setIdPerro(rs.getInt("ID_PERRO"));
        ip.setIdPatologia(rs.getInt("ID_PATOLOGIA"));
        ip.setDescripcion(rs.getString("NOTAS_ESPECIFICAS"));
        return ip;
    }

    public void eliminarPatologiasPorPerro(int idPerro) throws SQLException {
        String sql = "DELETE FROM Identificacion_Patologias WHERE ID_Perro = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPerro);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar patologías asociadas al perro ID " + idPerro + ": " + e.getMessage());
            throw e;
        }
    }
}