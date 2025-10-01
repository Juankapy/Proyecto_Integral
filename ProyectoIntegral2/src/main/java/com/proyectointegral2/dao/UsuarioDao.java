package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.utils.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDao {

    public Usuario verificacionUsuario(String nombreUsu, String contrasena) {

        String sql = "SELECT ID_USUARIO, NOMBRE_USU, CONTRASENA, ROL FROM USUARIO WHERE NOMBRE_USU = ? AND CONTRASENA = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombreUsu);
            stmt.setString(2, contrasena);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("ID_USUARIO"),
                            rs.getString("NOMBRE_USU"),
                            rs.getString("CONTRASENA"),
                            rs.getString("ROL")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int crearUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO USUARIO (NOMBRE_USU, CONTRASENA, ROL) VALUES (?, ?, ?) RETURNING ID_USUARIO INTO ?";

        try (Connection conn = ConexionDB.getConnection();

             PreparedStatement stmt = conn.prepareStatement("INSERT INTO USUARIO (NOMBRE_USU, CONTRASENA, ROL) VALUES (?, ?, ?)", new String[]{"ID_USUARIO"})) {

            stmt.setString(1, usuario.getNombreUsu());
            stmt.setString(2, usuario.getContrasena());
            stmt.setString(3, usuario.getRol());
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        usuario.setIdUsuario(generatedId);
                        return generatedId;
                    } else {
                        System.err.println("No se pudo obtener el ID generado para el usuario insertado.");
                        return -1;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            throw e;
        }
        return -1;
    }

    public Usuario obtenerUsuarioPorNombreUsuario(String nombreUsuario) throws SQLException {
        String sql = "SELECT ID_USUARIO, NOMBRE_USU, CONTRASENA, ROL FROM USUARIO WHERE NOMBRE_USU = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("ID_USUARIO"),
                            rs.getString("NOMBRE_USU"),
                            rs.getString("CONTRASENA"),
                            rs.getString("ROL")
                    );
                }
            }
        }
        return null;
    }

    public Usuario obtenerUsuarioPorId(int idUsuario) throws SQLException {
        String sql = "SELECT ID_USUARIO, NOMBRE_USU, CONTRASENA, ROL FROM USUARIO WHERE ID_USUARIO = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("ID_USUARIO"),
                            rs.getString("NOMBRE_USU"),
                            rs.getString("CONTRASENA"),
                            rs.getString("ROL")
                    );
                }
            }
        }
        return null;
    }

    public boolean actualizarUsuario(Usuario usuario) throws SQLException {
        String sql = "UPDATE USUARIO SET NOMBRE_USU = ?, CONTRASENA = ?, ROL = ? WHERE ID_USUARIO = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombreUsu());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setString(3, usuario.getRol());
            pstmt.setInt(4, usuario.getIdUsuario());
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        }
    }
}