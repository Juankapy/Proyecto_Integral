package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Patologia;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatologiaDao {

    public int crearPatologia(Patologia patologia) throws SQLException {
        String sqlGetId = "SELECT C##PRUEBABASE.SEQ_PATOLOGIA_ID.NEXTVAL FROM DUAL";
        // Incluir DESCRIPCION_PATOLOGIA si tu modelo y formulario la manejan
        String sqlInsert = "INSERT INTO Patologia (ID_PATOLOGIA, NOMBRE, DESCRIPCION_PATOLOGIA) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmtGetId = null;
        PreparedStatement pstmtInsert = null;
        ResultSet rsId = null;
        int nuevoIdPatologia = -1;

        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // Controlar la transacción

            // 1. Obtener el nuevo ID de la secuencia
            pstmtGetId = conn.prepareStatement(sqlGetId);
            rsId = pstmtGetId.executeQuery();
            if (rsId.next()) {
                nuevoIdPatologia = rsId.getInt(1);
            } else {
                conn.rollback();
                throw new SQLException("No se pudo obtener el siguiente ID de la secuencia SEQ_PATOLOGIA_ID.");
            }
            // Es buena práctica cerrar estos recursos intermedios inmediatamente
            rsId.close();
            pstmtGetId.close();

            // 2. Insertar la patología con el ID obtenido
            pstmtInsert = conn.prepareStatement(sqlInsert);
            pstmtInsert.setInt(1, nuevoIdPatologia);
            pstmtInsert.setString(2, patologia.getNombre());

            // Manejar la descripción
            if (patologia.getDescripcion() != null && !patologia.getDescripcion().trim().isEmpty()) {
                pstmtInsert.setString(3, patologia.getDescripcion());
            } else {
                pstmtInsert.setNull(3, Types.VARCHAR); // O no incluirla si la columna permite NULL y quieres dejarla vacía
            }

            int affectedRows = pstmtInsert.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                throw new SQLException("No se pudo crear la patología, ninguna fila afectada.");
            }

            conn.commit();
            patologia.setIdPatologia(nuevoIdPatologia); // Actualizar el objeto con el ID real
            return nuevoIdPatologia;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error SQL al crear patología: " + e.getMessage());
            throw e;
        } finally {
            if (rsId != null) try { rsId.close(); } catch (SQLException ex) { /* ignore */ }
            if (pstmtGetId != null) try { pstmtGetId.close(); } catch (SQLException ex) { /* ignore */ }
            if (pstmtInsert != null) try { pstmtInsert.close(); } catch (SQLException ex) { /* ignore */ }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }

    public Patologia obtenerPatologiaPorId(int idPatologia) throws SQLException {
        // Asegúrate que la columna de descripción sea DESCRIPCION_PATOLOGIA si así se llama
        String sql = "SELECT ID_PATOLOGIA, NOMBRE, DESCRIPCION_PATOLOGIA FROM Patologia WHERE ID_Patologia = ?";
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
             PreparedStatement pstmt = conn.prepareStatement(sql); // Usar PreparedStatement es más seguro incluso sin params
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
        // Considerar eliminar de IDENTIFICACION_PATOLOGIAS si no hay ON DELETE CASCADE
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
        patologia.setDescripcion(rs.getString("DESCRIPCION_PATOLOGIA")); // Usa la columna correcta de la tabla
        return patologia;
    }
}