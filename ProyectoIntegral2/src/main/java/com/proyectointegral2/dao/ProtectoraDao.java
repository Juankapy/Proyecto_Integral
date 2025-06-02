package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.RedSocial;
import com.proyectointegral2.utils.ConexionDB;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProtectoraDao{

    public int crearProtectora(Protectora protectora) throws SQLException {
        String sqlInsert = "INSERT INTO PROTECTORA (CIF, NOMBRE, TELEFONO, EMAIL, PROVINCIA, CIUDAD, CALLE, CP, ID_USUARIO) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert, new String[]{"ID_PROTECTORA"})) {

            pstmt.setString(1, protectora.getCif());
            pstmt.setString(2, protectora.getNombre());
            pstmt.setString(3, protectora.getTelefono());
            pstmt.setString(4, protectora.getEmail());
            pstmt.setString(5, protectora.getProvincia());
            pstmt.setString(6, protectora.getCiudad());
            pstmt.setString(7, protectora.getCalle());
            pstmt.setString(8, protectora.getCodigoPostal());
            pstmt.setInt(9, protectora.getIdUsuario());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear la protectora.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    System.out.println("Advertencia: Protectora insertada, pero ID no recuperado por getGeneratedKeys.");
                    return -1;
                }
            }
        }
    }

    public Protectora obtenerProtectoraPorId(int idProtectora) throws SQLException {
        String sql = "SELECT * FROM Protectora WHERE ID_Protectora = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProtectora);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProtectora(rs);
                }
            }
        }
        return null;
    }

    public Protectora obtenerProtectoraPorIdUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM Protectora WHERE ID_Usuario = ?"; // Busca en la columna ID_USUARIO de la tabla Protectora
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    return mapResultSetToProtectora(rs);
                }
            }
        }
        return null;
    }

    public boolean actualizarProtectora(Protectora protectora) throws SQLException {
        String sql = "UPDATE Protectora SET Nombre = ?, Telefono = ?, Email = ?, Provincia = ?, Ciudad = ?, Calle = ?, CP = ?, ID_Usuario = ? " +
                "WHERE ID_Protectora = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, protectora.getNombre());
            pstmt.setString(2, protectora.getTelefono());
            pstmt.setString(3, protectora.getEmail());
            pstmt.setString(4, protectora.getProvincia());
            pstmt.setString(5, protectora.getCiudad());
            pstmt.setString(6, protectora.getCalle());
            pstmt.setString(7, protectora.getCodigoPostal());
            pstmt.setInt(8, protectora.getIdUsuario());
            pstmt.setInt(9, protectora.getIdProtectora());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarProtectora(int idProtectora) throws SQLException {
        String sqlDeleteRedes = "DELETE FROM Redes_Sociales WHERE ID_Protectora = ?";
        String sqlDeleteProtectora = "DELETE FROM Protectora WHERE ID_Protectora = ?";
        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtRedes = conn.prepareStatement(sqlDeleteRedes)) {
                pstmtRedes.setInt(1, idProtectora);
                pstmtRedes.executeUpdate();
            }
            try (PreparedStatement pstmtProtectora = conn.prepareStatement(sqlDeleteProtectora)) {
                pstmtProtectora.setInt(1, idProtectora);
                boolean exito = pstmtProtectora.executeUpdate() > 0;
                if (exito) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        }
    }

    public boolean agregarRedSocial(int idProtectora, RedSocial redSocial) throws SQLException {
        String sql = "INSERT INTO Redes_Sociales (Plataforma, URL, ID_Protectora) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, redSocial.getPlataforma());
            pstmt.setString(2, redSocial.getUrl());
            pstmt.setInt(3, idProtectora);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarRedSocial(int idProtectora, String plataforma) throws SQLException {
        String sql = "DELETE FROM Redes_Sociales WHERE ID_Protectora = ? AND Plataforma = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProtectora);
            pstmt.setString(2, plataforma);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean actualizarRedSocial(RedSocial redSocial) throws SQLException {
        String sql = "UPDATE Redes_Sociales SET URL = ? WHERE ID_Protectora = ? AND Plataforma = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, redSocial.getUrl());
            pstmt.setInt(2, redSocial.getIdProtectora());
            pstmt.setString(3, redSocial.getPlataforma());
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<RedSocial> obtenerRedesSocialesPorProtectora(int idProtectora) throws SQLException {
        List<RedSocial> redes = new ArrayList<>();
        String sql = "SELECT Plataforma, URL, ID_Protectora FROM Redes_Sociales WHERE ID_Protectora = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProtectora);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RedSocial red = new RedSocial();
                    red.setPlataforma(rs.getString("Plataforma"));
                    red.setUrl(rs.getString("URL"));
                    red.setIdProtectora(rs.getInt("ID_Protectora"));
                    redes.add(red);
                }
            }
        }
        return redes;
    }

    private Protectora mapResultSetToProtectora(ResultSet rs) throws SQLException {
        Protectora protectora = new Protectora();
        protectora.setIdProtectora(rs.getInt("ID_PROTECTORA"));
        protectora.setCif(rs.getString("CIF"));
        protectora.setNombre(rs.getString("NOMBRE"));
        protectora.setTelefono(rs.getString("TELEFONO"));
        protectora.setEmail(rs.getString("EMAIL"));
        // Mapeo directo de los campos de dirección
        protectora.setProvincia(rs.getString("PROVINCIA"));
        protectora.setCiudad(rs.getString("CIUDAD"));
        protectora.setCalle(rs.getString("CALLE"));
        protectora.setCodigoPostal(rs.getString("CP"));
        protectora.setIdUsuario(rs.getInt("ID_USUARIO"));
        return protectora;
    }
}