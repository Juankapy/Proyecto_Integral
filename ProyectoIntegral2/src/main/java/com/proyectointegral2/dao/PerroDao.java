package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.Model.RegistroPerroInfo;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.time.LocalDate;
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

    // En tu PerroDaoImpl.java o como se llame tu implementación
// ... (otros imports y métodos) ...

    // Añade este método o asegúrate que tu implementación existente haga esto:
    public List<RegistroPerroInfo> obtenerRegistrosPerrosParaTabla(int idProtectora) throws SQLException {
        List<RegistroPerroInfo> registros = new ArrayList<>();
        String sql = "SELECT ID_PERRO, NOMBRE, FECHA_NACIMIENTO, ADOPTADO, DESCRIPCION_PERRO " +
                "FROM PERROS WHERE ID_PROTECTORA = ? ORDER BY NOMBRE";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConexionDB.getConnection(); // Tu clase de conexión
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idProtectora);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int idPerro = rs.getInt("ID_PERRO");
                String nombre = rs.getString("NOMBRE");
                LocalDate fechaIngreso = null; // Usaremos FECHA_NACIMIENTO como placeholder
                if (rs.getDate("FECHA_NACIMIENTO") != null) {
                    fechaIngreso = rs.getDate("FECHA_NACIMIENTO").toLocalDate();
                }
                String estado = "S".equalsIgnoreCase(rs.getString("ADOPTADO")) ? "Adoptado" : "En Adopción";
                String notas = rs.getString("DESCRIPCION_PERRO");
                if (notas == null || notas.trim().isEmpty()) {
                    notas = "Sin notas específicas.";
                }
                registros.add(new RegistroPerroInfo(idPerro, nombre, fechaIngreso, estado, notas));
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return registros;
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
                pstmt.setNull(7, Types.NUMERIC);
                System.err.println("Advertencia al actualizar perro: ID_Raza es nulo o inválido para el perro ID: " + perro.getIdPerro());
            }
            pstmt.setInt(8, perro.getIdPerro());

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

        Raza raza = new Raza();
        raza.setIdRaza(rs.getInt("ID_RAZA"));
        raza.setNombreRaza(rs.getString("NOMBRE_RAZA"));
        perro.setRaza(raza);

        return perro;
    }

    public List<Perro> obtenerTodosLosPerrosNoAdoptados() throws SQLException {
        List<Perro> perrosNoAdoptados = new ArrayList<>();
        String sql = "SELECT P.*, R.NOMBRE_RAZA " +
                "FROM PERROS P " +
                "JOIN RAZA R ON P.ID_RAZA = R.ID_RAZA " +
                "WHERE P.ADOPTADO = 'N' " +
                "ORDER BY P.NOMBRE";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                perrosNoAdoptados.add(mapResultSetToPerroConRaza(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al obtener todos los perros no adoptados: " + e.getMessage());
            throw e;
        }
        return perrosNoAdoptados;
    }

    public Perro obtenerPerroPorId(int idPerro) {
        Perro perro = null;
        String sql = "SELECT P.*, R.NOMBRE_RAZA " +
                "FROM PERROS P " +
                "JOIN RAZA R ON P.ID_RAZA = R.ID_RAZA " +
                "WHERE P.ID_PERRO = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPerro);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    perro = mapResultSetToPerroConRaza(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al obtener perro por ID: " + e.getMessage());
        }
        return perro;
    }
}