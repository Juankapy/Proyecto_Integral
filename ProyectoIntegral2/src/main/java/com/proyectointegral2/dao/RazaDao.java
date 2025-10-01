package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Para obtenerTodasLasRazas si no tiene parámetros
import java.util.ArrayList;
import java.util.List;

public class RazaDao {

    public RazaDao() {
    }

    /**
     * Crea una nueva raza en la base de datos.
     * El ID_Raza se genera automáticamente por la secuencia definida en la tabla.
     * El método devuelve el ID generado.
     * @param nuevaRaza Objeto Raza con el nombre a insertar. El ID se ignorará y se actualizará.
     * @return El ID_Raza generado, o -1 si falla.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public int crearRaza(Raza nuevaRaza) throws SQLException {
        String sql = "INSERT INTO RAZA (NOMBRE_RAZA) VALUES (?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int nuevoIdRaza = -1;

        try {
            conn = ConexionDB.getConnection();
            pstmt = conn.prepareStatement(sql, new String[]{"ID_RAZA"});

            pstmt.setString(1, nuevaRaza.getNombreRaza());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    nuevoIdRaza = generatedKeys.getInt(1);
                    nuevaRaza.setIdRaza(nuevoIdRaza);

                } else {
                    // Esto podría pasar si la configuración de la secuencia/default no es la esperada
                    // o si el driver no puede recuperar la clave.
                    System.err.println("RazaDao.crearRaza: Raza insertada, pero no se pudo obtener el ID autogenerado por secuencia/default.");
                    // En este caso, podrías intentar obtener el último ID insertado de la secuencia si fuera necesario,
                    // pero getGeneratedKeys es el método estándar.
                    // Si la secuencia es C##PRUEBABASE.SEQ_RAZA_ID, podrías hacer:
                    // try (Statement stmtCurrval = conn.createStatement();
                    //      ResultSet rsCurrval = stmtCurrval.executeQuery("SELECT C##PRUEBABASE.SEQ_RAZA_ID.CURRVAL FROM DUAL")) {
                    //     if (rsCurrval.next()) {
                    //         nuevoIdRaza = rsCurrval.getInt(1);
                    //         nuevaRaza.setIdRaza(nuevoIdRaza);
                    //     }
                    // }
                    // Pero esto requiere que la secuencia haya sido usada en la misma sesión.
                    // Es mejor que getGeneratedKeys funcione.
                }
            } else {
                throw new SQLException("No se pudo crear la raza, ninguna fila afectada.");
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al crear raza: " + e.getMessage());
            System.err.println("SQL Intentado: " + sql);
            throw e;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException ex) { /* ignore */ }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) { /* ignore */ }
            if (conn != null) try { conn.close(); } catch (SQLException ex) { /* ignore */ }
        }
        return nuevoIdRaza;
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


    public List<Raza> obtenerTodasLasRazas() throws SQLException {
        List<Raza> razas = new ArrayList<>();
        String sql = "SELECT ID_RAZA, NOMBRE_RAZA FROM RAZA ORDER BY NOMBRE_RAZA";
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement(); // No hay parámetros, podemos usar Statement
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                razas.add(mapResultSetToRaza(rs));
            }
        }
        return razas;
    }


    private Raza mapResultSetToRaza(ResultSet rs) throws SQLException {
        Raza raza = new Raza();
        raza.setIdRaza(rs.getInt("ID_RAZA"));
        raza.setNombreRaza(rs.getString("NOMBRE_RAZA"));
        return raza;
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
}