package com.proyectointegral2.dao;

import com.proyectointegral2.Model.RedSocial;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RedesSocialesDao {

    public boolean agregarRedSocial(RedSocial redSocial) throws SQLException {
        String sql = "INSERT INTO Redes_Sociales (Plataforma, URL, ID_Protectora) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, redSocial.getPlataforma());
            pstmt.setString(2, redSocial.getUrl());
            pstmt.setInt(3, redSocial.getIdProtectora());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarRedSocial(int idProtectora, String plataforma) throws SQLException {
        String sql = "DELETE FROM Redes_Sociales WHERE ID_Protectora = ? AND Plataforma = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProtectora);
            pstmt.setString(2, plataforma);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean actualizarRedSocial(RedSocial redSocial) throws SQLException {
        String sql = "UPDATE Redes_Sociales SET URL = ? WHERE ID_Protectora = ? AND Plataforma = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, redSocial.getUrl());
            pstmt.setInt(2, redSocial.getIdProtectora());
            pstmt.setString(3, redSocial.getPlataforma());
            return pstmt.executeUpdate() > 0;
        }
    }

    public RedSocial obtenerRedSocial(int idProtectora, String plataforma) throws SQLException {
        String sql = "SELECT * FROM Redes_Sociales WHERE ID_Protectora = ? AND Plataforma = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProtectora);
            pstmt.setString(2, plataforma);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRedSocial(rs);
                }
            }
        }
        return null;
    }

    public List<RedSocial> obtenerRedesSocialesPorProtectora(int idProtectora) throws SQLException {
        List<RedSocial> redes = new ArrayList<>();
        String sql = "SELECT * FROM Redes_Sociales WHERE ID_Protectora = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProtectora);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    redes.add(mapResultSetToRedSocial(rs));
                }
            }
        }
        return redes;
    }

    private RedSocial mapResultSetToRedSocial(ResultSet rs) throws SQLException {
        RedSocial red = new RedSocial();
        red.setPlataforma(rs.getString("Plataforma"));
        red.setUrl(rs.getString("URL"));
        red.setIdProtectora(rs.getInt("ID_Protectora"));
        return red;
    }
}