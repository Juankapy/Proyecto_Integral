package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Patologia;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerroDao {

    public int crearPerro(Perro perro) throws SQLException {
        String sqlInsert = "INSERT INTO Perros (Nombre, Sexo, FECHA_NACIMIENTO, Adoptado, Foto, ID_Protectora, ID_Raza) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, perro.getNombre());
            pstmt.setString(2, perro.getSexo());
            pstmt.setDate(3, java.sql.Date.valueOf(perro.getFechaNacimiento()));
            pstmt.setString(4, perro.getAdoptado());
            if (perro.getFoto() != null) {
                pstmt.setBytes(5, perro.getFoto());
            } else {
                pstmt.setNull(5, Types.BLOB);
            }
            pstmt.setInt(6, perro.getIdProtectora());
            pstmt.setInt(7, perro.getIdRaza());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("No se pudo crear el perro.");
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado.");
                }
            }
        }
    }

    public Perro obtenerPerroPorId(int idPerro) throws SQLException {
        String sql = "SELECT * FROM Perros WHERE ID_Perro = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPerro);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPerro(rs);
                }
            }
        }
        return null;
    }

    public List<Perro> obtenerTodosLosPerros() throws SQLException {
        List<Perro> perros = new ArrayList<>();
        String sql = "SELECT * FROM Perros ORDER BY Nombre";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                perros.add(mapResultSetToPerro(rs));
            }
        }
        return perros;
    }

    public boolean actualizarPerro(Perro perro) throws SQLException {
        String sql = "UPDATE Perros SET Nombre = ?, Sexo = ?, FECHA_NACIMIENTO = ?, Adoptado = ?, Foto = ?, ID_Protectora = ?, ID_Raza = ? WHERE ID_Perro = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, perro.getNombre());
            pstmt.setString(2, perro.getSexo());
            pstmt.setDate(3, java.sql.Date.valueOf(perro.getFechaNacimiento()));
            pstmt.setString(4, perro.getAdoptado());
            if (perro.getFoto() != null) {
                pstmt.setBytes(5, perro.getFoto());
            } else {
                pstmt.setNull(5, Types.BLOB);
            }
            pstmt.setInt(6, perro.getIdProtectora());
            pstmt.setInt(7, perro.getIdRaza());
            pstmt.setInt(8, perro.getIdPerro());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarPerro(int idPerro) throws SQLException {
        String sql = "DELETE FROM Perros WHERE ID_Perro = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPerro);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Perro mapResultSetToPerro(ResultSet rs) throws SQLException {
        Perro perro = new Perro();
        perro.setIdPerro(rs.getInt("ID_Perro"));
        perro.setNombre(rs.getString("Nombre"));
        perro.setSexo(rs.getString("Sexo"));
        perro.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO").toLocalDate());
        perro.setAdoptado(rs.getString("Adoptado"));
        perro.setFoto(rs.getBytes("Foto"));
        perro.setIdProtectora(rs.getInt("ID_Protectora"));
        perro.setIdRaza(rs.getInt("ID_Raza"));
        return perro;
    }

    public Patologia obtenerPatologiaPorId(int idPatologia) {
        String sql = "SELECT * FROM Patologia WHERE ID_Patologia = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPatologia);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Patologia patologia = new Patologia();
                    patologia.setIdPatologia(rs.getInt("ID_Patologia"));
                    patologia.setNombre(rs.getString("Nombre"));
                    return patologia;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}