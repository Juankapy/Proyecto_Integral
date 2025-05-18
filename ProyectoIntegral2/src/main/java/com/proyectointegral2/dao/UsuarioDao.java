package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;

public class UsuarioDao  {

    public int crearUsuario(Usuario usuario) throws SQLException {
        String sqlSelectId = "SELECT SEQ_USUARIO.NEXTVAL FROM DUAL";
        String sqlInsert = "INSERT INTO Usuario (ID_Usuario, Nombre_Usu, Contrasena) VALUES (?, ?, ?)";
        int nuevoId = -1;
        Connection conn = null;

        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            // 1. Obtener el próximo ID de la secuencia
            try (PreparedStatement pstmtId = conn.prepareStatement(sqlSelectId);
                 ResultSet rsId = pstmtId.executeQuery()) {
                if (rsId.next()) {
                    nuevoId = rsId.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la secuencia SEQ_USUARIO.");
                }
            }

            // 2. Insertar el usuario con el ID obtenido
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                pstmt.setInt(1, nuevoId);
                pstmt.setString(2, usuario.getNombreUsuario());
                pstmt.setString(3, usuario.getContrasena()); // ¡Hashear antes en producción!
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    conn.commit();
                    return nuevoId;
                } else {
                    conn.rollback();
                    return -1;
                }
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
            if (conn != null) conn.close();
        }
    }

    public Usuario obtenerUsuarioPorId(int idUsuario) throws SQLException {
        String sql = "SELECT ID_Usuario, Nombre_Usu, Contrasena FROM Usuario WHERE ID_Usuario = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }
        return null;
    }

    public Usuario obtenerUsuarioPorNombreUsuario(String nombreUsu) throws SQLException {
        String sql = "SELECT ID_Usuario, Nombre_Usu, Contrasena FROM Usuario WHERE Nombre_Usu = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreUsu);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }
        return null;
    }

    public Usuario verificarCredenciales(String nombreUsu, String contrasena) throws SQLException {
        String sql = "SELECT ID_Usuario, Nombre_Usu, Contrasena FROM Usuario WHERE Nombre_Usu = ? AND Contrasena = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreUsu);
            pstmt.setString(2, contrasena); // ¡Comparar hash en producción!
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        }
        return null;
    }

    public boolean actualizarContrasena(int idUsuario, String nuevaContrasena) throws SQLException {
        String sql = "UPDATE Usuario SET Contrasena = ? WHERE ID_Usuario = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevaContrasena); // ¡Hashear antes en producción!
            pstmt.setInt(2, idUsuario);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarUsuario(int idUsuario) throws SQLException {
        String sql = "DELETE FROM Usuario WHERE ID_Usuario = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("ID_Usuario"));
        usuario.setNombreUsuario(rs.getString("Nombre_Usu"));
        usuario.setContrasena(rs.getString("Contrasena"));
        return usuario;
    }
}