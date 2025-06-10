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
        this.razaDao = new RazaDao();
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

    /**
     * Obtiene una lista de perros con los que un cliente específico ha tenido una cita
     * (confirmada o completada) y que actualmente no están adoptados.
     *
     * @param idCliente El ID del cliente.
     * @return Una lista de objetos Perro.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<Perro> obtenerPerrosConCitasPreviasPorCliente(int idCliente) throws SQLException {
        List<Perro> perrosConCitas = new ArrayList<>();
        String sql = "SELECT DISTINCT P.*, R.NOMBRE_RAZA " +
                "FROM PERROS P " +
                "JOIN RAZA R ON P.ID_RAZA = R.ID_RAZA " +
                "JOIN RESERVAS_CITAS RC ON P.ID_PERRO = RC.ID_PERRO " +
                "WHERE RC.ID_CLIENTE = ? " +
                "AND RC.ESTADO_CITA = 'Completada' " +
                "AND (P.ADOPTADO = 'N' OR P.ADOPTADO = 'R' OR P.ADOPTADO = 'A')";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    perrosConCitas.add(mapResultSetToPerroConRaza(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al obtener perros con citas previas para cliente ID " + idCliente + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return perrosConCitas;
    }


    private Perro mapResultSetToPerroConRaza(ResultSet rs) throws SQLException {
        Perro perro = new Perro();
        perro.setIdPerro(rs.getInt("ID_PERRO"));
        perro.setNombre(rs.getString("NOMBRE"));
        perro.setSexo(rs.getString("SEXO"));
        Date fechaNacimientoDB = rs.getDate("FECHA_NACIMIENTO");
        if (fechaNacimientoDB != null) {
            perro.setFechaNacimiento(fechaNacimientoDB.toLocalDate());
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

    public boolean actualizarCampoAdoptado(int idPerro, String valor) {
        String sql = "UPDATE PERROS SET ADOPTADO = ? WHERE ID_PERRO = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, valor);
            stmt.setInt(2, idPerro);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}