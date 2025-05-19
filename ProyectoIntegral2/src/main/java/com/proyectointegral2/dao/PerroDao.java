package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza; // Necesario si vas a setear el objeto Raza
import com.proyectointegral2.Model.Patologia; // Para tu método obtenerPatologiaPorId
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerroDao {

    private RazaDao razaDao;

    public PerroDao() {
        this.razaDao = new RazaDao();
    }

    public int crearPerro(Perro perro) throws SQLException {

        String sqlInsert = "INSERT INTO PERROS (NOMBRE, SEXO, FECHA_NACIMIENTO, ADOPTADO, FOTO, ID_PROTECTORA, ID_RAZA, DESCRIPCION_PERRO) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
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
                pstmt.setNull(3, Types.DATE);
            }
            pstmt.setString(4, perro.getAdoptado());
            pstmt.setString(5, perro.getFoto());
            pstmt.setInt(6, perro.getIdProtectora());
            pstmt.setInt(7, perro.getRaza() != null ? perro.getRaza().getIdRaza() : 0);


            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                throw new SQLException("No se pudo crear el perro, ninguna fila afectada.");
            }

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                nuevoIdPerro = generatedKeys.getInt(1);
                perro.setIdPerro(nuevoIdPerro);
            } else {
                System.out.println("Advertencia: Perro insertado, pero no se recuperó ID con getGeneratedKeys.");
            }
            conn.commit();
            return nuevoIdPerro;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Error SQL al crear perro: " + e.getMessage());
            throw e;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException ex) {  }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {  }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {  }
        }
    }

    public Perro obtenerPerroPorId(int idPerro) throws SQLException {
        String sql = "SELECT * FROM PERROS WHERE ID_PERRO = ?";
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
        String sql = "SELECT * FROM PERROS ORDER BY NOMBRE";
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                perros.add(mapResultSetToPerro(rs));
            }
        }
        return perros;
    }

    public List<Perro> obtenerPerrosPorProtectora(int idProtectora) throws SQLException {
        List<Perro> perros = new ArrayList<>();
        String sql = "SELECT * FROM PERROS WHERE ID_PROTECTORA = ? ORDER BY NOMBRE";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProtectora);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    perros.add(mapResultSetToPerro(rs));
                }
            }
        }
        return perros;
    }


    public boolean actualizarPerro(Perro perro) throws SQLException {
        String sql = "UPDATE PERROS SET NOMBRE = ?, SEXO = ?, FECHA_NACIMIENTO = ?, ADOPTADO = ?, FOTO = ?, ID_PROTECTORA = ?, ID_RAZA = ?, DESCRIPCION_PERRO = ? WHERE ID_PERRO = ?";
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
            pstmt.setInt(7, perro.getRaza() != null ? perro.getRaza().getIdRaza() : 0);
            pstmt.setString(8, perro.getDescripcionPerro());
            pstmt.setInt(9, perro.getIdPerro());
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

    private Perro mapResultSetToPerro(ResultSet rs) throws SQLException {
        Perro perro = new Perro();
        perro.setIdPerro(rs.getInt("ID_PERRO"));
        perro.setNombre(rs.getString("NOMBRE"));
        perro.setSexo(rs.getString("SEXO"));
        Date fechaNacSQL = rs.getDate("FECHA_NACIMIENTO");
        if (fechaNacSQL != null) {
            perro.setFechaNacimiento(fechaNacSQL.toLocalDate());
        }
        perro.setAdoptado(rs.getString("ADOPTADO"));
        perro.setFoto(rs.getString("FOTO")); // CORREGIDO: getString
        perro.setIdProtectora(rs.getInt("ID_PROTECTORA"));
        perro.setDescripcionPerro(rs.getString("DESCRIPCION_PERRO"));

        // Obtener y setear el objeto Raza
        int idRaza = rs.getInt("ID_RAZA");
        if (idRaza > 0 && razaDao != null) {
            Raza raza = razaDao.obtenerRazaPorId(idRaza);
            perro.setRaza(raza);
        } else if (idRaza > 0) {

            Raza razaConId = new Raza();
            razaConId.setIdRaza(idRaza);
            perro.setRaza(razaConId);
            System.err.println("Advertencia: RazaDao no inicializado en PerroDao. No se pudo cargar el objeto Raza completo para perro ID: " + perro.getIdPerro());
        }
        return perro;
    }

    public Patologia obtenerPatologiaPorId(int idPatologia) throws SQLException {
        String sql = "SELECT * FROM PATOLOGIA WHERE ID_PATOLOGIA = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPatologia);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Patologia patologia = new Patologia();
                    patologia.setIdPatologia(rs.getInt("ID_PATOLOGIA"));
                    patologia.setNombre(rs.getString("NOMBRE"));
                    return patologia;
                }
            }
        }
        return null;
    }
}