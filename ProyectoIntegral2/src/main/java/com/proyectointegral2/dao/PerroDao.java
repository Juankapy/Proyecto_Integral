package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerroDao {

    private RazaDao razaDao;

    public PerroDao() {
        this.razaDao = new RazaDao(); // Asegúrate que RazaDao esté implementado y funcione
    }

    public int crearPerro(Perro perro) throws SQLException {
        String sqlInsert = "INSERT INTO PERROS (NOMBRE, SEXO, FECHA_NACIMIENTO, ADOPTADO, FOTO, ID_PROTECTORA, ID_RAZA) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        int nuevoIdPerro = -1;

        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sqlInsert, new String[]{"ID_PERRO"});

            pstmt.setString(1, perro.getNombre());
            pstmt.setString(2, perro.getSexo());
            if (perro.getFechaNacimiento() != null) {
                pstmt.setDate(3, java.sql.Date.valueOf(perro.getFechaNacimiento()));
            } else {
                pstmt.setNull(3, java.sql.Types.DATE);
            }
            pstmt.setString(4, perro.getAdoptado());
            pstmt.setString(5, perro.getFoto());
            pstmt.setInt(6, perro.getIdProtectora());

            if (perro.getRaza() != null && perro.getRaza().getIdRaza() > 0) {
                pstmt.setInt(7, perro.getRaza().getIdRaza());
            } else {
                pstmt.setNull(7, java.sql.Types.NUMERIC);
                System.err.println("Advertencia al crear perro: ID_Raza es nulo o inválido para el perro: " + perro.getNombre());
            }
            // ELIMINADO: pstmt.setString(8, perro.getDescripcionPerro());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rsKeys = pstmt.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        nuevoIdPerro = rsKeys.getInt(1);
                        perro.setIdPerro(nuevoIdPerro);
                    } else {
                        System.out.println("Advertencia: Perro insertado, pero no se recuperó ID con getGeneratedKeys.");
                    }
                }
            } else {
                conn.rollback();
                throw new SQLException("No se pudo crear el perro, ninguna fila afectada.");
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error SQL al crear perro: " + e.getMessage());
            throw e;
        } finally {
            // Cierre de PreparedStatement y Connection
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) { /* ignore */ }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { /* ignore */ }
        }
        return nuevoIdPerro;
    }

    public Perro obtenerPerroPorId(int idPerro) throws SQLException {
        // La query SELECT puede seguir trayendo DESCRIPCION_PERRO si existe en la BD,
        // pero mapResultSetToPerroConRaza ya no la usará.
        // Es mejor si la query es SELECT P.*, R.NOMBRE_RAZA ... para ser explícito.
        String sql = "SELECT P.ID_PERRO, P.NOMBRE, P.SEXO, P.FECHA_NACIMIENTO, P.ADOPTADO, P.FOTO, P.ID_PROTECTORA, P.ID_RAZA, R.NOMBRE_RAZA " +
                "FROM PERROS P JOIN RAZA R ON P.ID_RAZA = R.ID_RAZA WHERE P.ID_PERRO = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPerro);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPerroConRaza(rs);
                }
            }
        }
        return null;
    }

    public List<Perro> obtenerTodosLosPerros() throws SQLException {
        List<Perro> perros = new ArrayList<>();
        String sql = "SELECT P.ID_PERRO, P.NOMBRE, P.SEXO, P.FECHA_NACIMIENTO, P.ADOPTADO, P.FOTO, P.ID_PROTECTORA, P.ID_RAZA, R.NOMBRE_RAZA " +
                "FROM PERROS P JOIN RAZA R ON P.ID_RAZA = R.ID_RAZA ORDER BY P.NOMBRE";
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                perros.add(mapResultSetToPerroConRaza(rs));
            }
        }
        return perros;
    }

    public List<Perro> obtenerPerrosPorProtectora(int idProtectora) throws SQLException {
        List<Perro> perros = new ArrayList<>();
        String sql = "SELECT P.ID_PERRO, P.NOMBRE, P.SEXO, P.FECHA_NACIMIENTO, P.ADOPTADO, P.FOTO, P.ID_PROTECTORA, P.ID_RAZA, R.NOMBRE_RAZA " +
                "FROM PERROS P JOIN RAZA R ON P.ID_RAZA = R.ID_RAZA WHERE P.ID_PROTECTORA = ? ORDER BY P.NOMBRE";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProtectora);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    perros.add(mapResultSetToPerroConRaza(rs));
                }
            }
        }
        return perros;
    }

    public boolean actualizarPerro(Perro perro) throws SQLException {
        // ELIMINADO DESCRIPCION_PERRO de la query y de los parámetros
        String sql = "UPDATE PERROS SET NOMBRE = ?, SEXO = ?, FECHA_NACIMIENTO = ?, ADOPTADO = ?, FOTO = ?, ID_PROTECTORA = ?, ID_RAZA = ? WHERE ID_PERRO = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, perro.getNombre());
            pstmt.setString(2, perro.getSexo());
            if (perro.getFechaNacimiento() != null) {
                pstmt.setDate(3, java.sql.Date.valueOf(perro.getFechaNacimiento()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            pstmt.setString(4, perro.getAdoptado());
            pstmt.setString(5, perro.getFoto());
            pstmt.setInt(6, perro.getIdProtectora());
            if (perro.getRaza() != null && perro.getRaza().getIdRaza() > 0) {
                pstmt.setInt(7, perro.getRaza().getIdRaza());
            } else {
                pstmt.setNull(7, Types.NUMERIC); // O lanzar error si raza es obligatoria
                System.err.println("Advertencia al actualizar perro: ID_Raza es nulo o inválido para el perro ID: " + perro.getIdPerro());
            }
            // ELIMINADO: pstmt.setString(8, perro.getDescripcionPerro());
            pstmt.setInt(8, perro.getIdPerro()); // El índice ahora es 8 para ID_PERRO

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarPerro(int idPerro) throws SQLException {
        String sql = "DELETE FROM PERROS WHERE ID_PERRO = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPerro);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Perro mapResultSetToPerroConRaza(ResultSet rs) throws SQLException {
        Perro perro = new Perro();
        perro.setIdPerro(rs.getInt("ID_PERRO"));
        perro.setNombre(rs.getString("NOMBRE"));
        perro.setSexo(rs.getString("SEXO"));
        Date fechaNacSQL = rs.getDate("FECHA_NACIMIENTO");
        if (fechaNacSQL != null) {
            perro.setFechaNacimiento(fechaNacSQL.toLocalDate());
        }
        perro.setAdoptado(rs.getString("ADOPTADO"));
        perro.setFoto(rs.getString("FOTO"));
        perro.setIdProtectora(rs.getInt("ID_PROTECTORA"));
        // ELIMINADO: perro.setDescripcionPerro(rs.getString("DESCRIPCION_PERRO"));

        Raza raza = new Raza();
        raza.setIdRaza(rs.getInt("ID_RAZA"));
        // Si tu query SELECT ya hace JOIN y trae NOMBRE_RAZA (como las actualizadas arriba)
        raza.setNombreRaza(rs.getString("NOMBRE_RAZA"));
        perro.setRaza(raza);

        return perro;
    }
}