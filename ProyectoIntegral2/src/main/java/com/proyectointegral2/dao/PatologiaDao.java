package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Patologia;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatologiaDao {

    public int crearPatologia(Patologia patologia) throws SQLException {
        String sqlGetId = "SELECT C##PRUEBABASE.SEQ_PATOLOGIA_ID.NEXTVAL FROM DUAL";
        String sqlInsert = "INSERT INTO Patologia (ID_PATOLOGIA, NOMBRE, DESCRIPCION_PATOLOGIA) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmtGetId = null;
        PreparedStatement pstmtInsert = null;
        ResultSet rsId = null;
        int nuevoIdPatologia = -1;

        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            pstmtGetId = conn.prepareStatement(sqlGetId);
            rsId = pstmtGetId.executeQuery();
            if (rsId.next()) {
                nuevoIdPatologia = rsId.getInt(1);
            } else {
                conn.rollback();
                throw new SQLException("No se pudo obtener el siguiente ID de la secuencia SEQ_ID_PATOLOGIA.");
            }
            rsId.close();
            pstmtGetId.close();

            pstmtInsert = conn.prepareStatement(sqlInsert);
            pstmtInsert.setInt(1, nuevoIdPatologia);
            pstmtInsert.setString(2, patologia.getNombre());
            if (patologia.getDescripcion() != null && !patologia.getDescripcion().trim().isEmpty()) {
                pstmtInsert.setString(3, patologia.getDescripcion());
            } else {
                pstmtInsert.setNull(3, Types.VARCHAR);
            }

            int affectedRows = pstmtInsert.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                throw new SQLException("No se pudo crear la patología, ninguna fila afectada.");
            }

            conn.commit();
            patologia.setIdPatologia(nuevoIdPatologia);
            return nuevoIdPatologia;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error SQL al crear patología: " + e.getMessage());
            throw e;
        } finally {
            if (rsId != null) try { rsId.close(); } catch (SQLException ex) {  }
            if (pstmtGetId != null) try { pstmtGetId.close(); } catch (SQLException ex) {  }
            if (pstmtInsert != null) try { pstmtInsert.close(); } catch (SQLException ex) {  }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {  }
        }
    }

    public Patologia obtenerPatologiaPorId(int idPatologia) throws SQLException {
        String sql = "SELECT ID_PATOLOGIA, NOMBRE, DESCRIPCION_PATOLOGIA FROM Patologia WHERE ID_PATOLOGIA = ?";
        try (Connection conn = ConexionDB.getConnection();
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
        String sql = "SELECT ID_PATOLOGIA, NOMBRE, DESCRIPCION_PATOLOGIA FROM Patologia WHERE UPPER(NOMBRE) = UPPER(?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombrePatologia.trim());
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
        String sql = "SELECT ID_PATOLOGIA, NOMBRE, DESCRIPCION_PATOLOGIA FROM Patologia ORDER BY Nombre";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                patologias.add(mapResultSetToPatologia(rs));
            }
        }
        return patologias;
    }

    public boolean actualizarPatologia(Patologia patologia) throws SQLException {
        String sql = "UPDATE Patologia SET Nombre = ?, DESCRIPCION_PATOLOGIA = ? WHERE ID_Patologia = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patologia.getNombre());
            pstmt.setString(2, patologia.getDescripcion());
            pstmt.setInt(3, patologia.getIdPatologia());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarPatologia(int idPatologia) throws SQLException {
        String sql = "DELETE FROM Patologia WHERE ID_Patologia = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPatologia);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Patologia mapResultSetToPatologia(ResultSet rs) throws SQLException {
        Patologia patologia = new Patologia();
        patologia.setIdPatologia(rs.getInt("ID_PATOLOGIA"));
        patologia.setNombre(rs.getString("NOMBRE"));
        patologia.setDescripcion(rs.getString("DESCRIPCION_PATOLOGIA"));
        return patologia;
    }
}