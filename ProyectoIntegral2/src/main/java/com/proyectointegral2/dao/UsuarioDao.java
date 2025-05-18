package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Usuario;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsuarioDao {

    public Usuario verificacionUsuario(String nombreUsu, String contrasena) {
        String sql = "SELECT ID_USUARIO, NOMBRE_USU, CONTRASENA, ROL FROM USUARIO WHERE NOMBRE_USU = ? AND CONTRASENA = ?"; // Asumiendo que añades ROL
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
                            rs.getString("ROL") // Obtener ROL si existe en la tabla
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int insertarUsuario(String nombreUsu, String contrasena, String rol) { // Añadido rol
        String sql = "INSERT INTO USUARIO (NOMBRE_USU, CONTRASENA, ROL) VALUES (?, ?, ?)"; // Añadido ROL
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"ID_USUARIO"})) {

            stmt.setString(1, nombreUsu);
            stmt.setString(2, contrasena); // Debería ser un hash
            stmt.setString(3, rol);         // Establecer rol

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // Sobrecarga por si no se especifica rol al insertar (se podría poner un default en BD o aquí)
    public int insertarUsuario(String nombreUsu, String contrasena) {
        return insertarUsuario(nombreUsu, contrasena, "CLIENTE"); // O un rol por defecto
    }


    public int obtenerIdUsuario(String nombreUsu) {
        String sql = "SELECT ID_USUARIO FROM USUARIO WHERE NOMBRE_USU = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsu);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID_USUARIO");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ID de usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Obtiene un usuario por su ID.
     * @param idUsuario El ID del usuario a buscar.
     * @return El objeto Usuario si se encuentra, null en caso contrario.
     */
    public Usuario obtenerUsuarioPorId(int idUsuario) {
        String sql = "SELECT ID_USUARIO, NOMBRE_USU, CONTRASENA, ROL FROM USUARIO WHERE ID_USUARIO = ?"; // Asumiendo ROL
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("ID_USUARIO"),
                            rs.getString("NOMBRE_USU"),
                            rs.getString("CONTRASENA"),
                            rs.getString("ROL") // Obtener ROL
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Actualiza los datos de un usuario existente (excepto su ID).
     * @param usuario El objeto Usuario con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE USUARIO SET NOMBRE_USU = ?, CONTRASENA = ?, ROL = ? WHERE ID_USUARIO = ?"; // Asumiendo ROL
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombreUsu());
            pstmt.setString(2, usuario.getContrasena()); // Si se cambia, debería ser el nuevo hash
            pstmt.setString(3, usuario.getRol());        // Actualizar ROL
            pstmt.setInt(4, usuario.getIdUsuario());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Elimina un usuario de la base de datos por su ID.
     * @param idUsuario El ID del usuario a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminarUsuario(int idUsuario) {
        // Considerar las restricciones de clave foránea (ON DELETE CASCADE en Cliente y Protectora
        // se encargarían de eliminar los registros dependientes).
        String sql = "DELETE FROM USUARIO WHERE ID_USUARIO = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}