package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza; // Necesario si vas a setear el objeto Raza
import com.proyectointegral2.Model.Patologia; // Para tu método obtenerPatologiaPorId
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerroDao {

    private RazaDao razaDao; // Para obtener el objeto Raza

    public PerroDao() {
        this.razaDao = new RazaDao(); // Instanciar el DAO de Raza
    }

    public int crearPerro(Perro perro) throws SQLException {
        // ID_PERRO se genera por secuencia DEFAULT
        String sqlInsert = "INSERT INTO PERROS (NOMBRE, SEXO, FECHANACIMIENTO, ADOPTADO, FOTO, ID_PROTECTORA, ID_RAZA) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
            pstmt.setString(4, perro.getAdoptado()); // 'S' o 'N'
            pstmt.setString(5, perro.getFoto());     // CORREGIDO: setString para la ruta de la foto
            pstmt.setInt(6, perro.getIdProtectora());
            pstmt.setInt(7, perro.getRaza() != null ? perro.getRaza().getIdRaza() : 0); // Obtener ID del objeto Raza, o 0/null si no hay raza


            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                conn.rollback();
                throw new SQLException("No se pudo crear el perro, ninguna fila afectada.");
            }

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                nuevoIdPerro = generatedKeys.getInt(1);
                perro.setIdPerro(nuevoIdPerro); // Actualizar el objeto perro
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
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException ex) { /* ignore */ }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) { /* ignore */ }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { /* ignore */ }
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
             Statement stmt = conn.createStatement(); // Usar Statement si no hay parámetros
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
        String sql = "UPDATE PERROS SET NOMBRE = ?, SEXO = ?, FECHANACIMIENTO = ?, " +
                "ADOPTADO = ?, FOTO = ?, ID_PROTECTORA = ?, ID_RAZA = ?";
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
            pstmt.setString(5, perro.getFoto()); // CORREGIDO: setString
            pstmt.setInt(6, perro.getIdProtectora());
            pstmt.setInt(7, perro.getRaza() != null ? perro.getRaza().getIdRaza() : 0); // Obtener ID del objeto Raza
            pstmt.setInt(8, perro.getIdPerro());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarPerro(int idPerro) throws SQLException {
        // Considerar eliminar también las entradas en IDENTIFICACION_PATOLOGIAS, RESERVAS_CITAS, PETICIONES_ADOPCION
        // si no tienes ON DELETE CASCADE en esas tablas referenciando a PERROS.
        // O manejarlo en una capa de servicio.
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

        // Obtener y setear el objeto Raza
        int idRaza = rs.getInt("ID_RAZA");
        if (idRaza > 0 && razaDao != null) { // Comprobar que razaDao esté inicializado
            Raza raza = razaDao.obtenerRazaPorId(idRaza); // Asume que tienes este método en RazaDao
            perro.setRaza(raza);
        } else if (idRaza > 0) {
            // Si razaDao es null, al menos guardar el ID si se quiere manejar después
            // O crear un objeto Raza solo con el ID
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