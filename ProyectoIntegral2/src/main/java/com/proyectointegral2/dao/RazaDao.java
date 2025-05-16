package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Raza;
import com.proyectointegral2.utils.ConexionDB; // Usar la clase de conexión centralizada

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RazaDao {

    public RazaDao() {
        // Constructor vacío
    }

    public void insertarRaza(Raza raza) throws SQLException {
        // Nombres de tabla y columnas deben coincidir con tu CREATE TABLE
        // Tabla: Raza, Columnas: ID_Raza, Nombre_Raza
        String sql = "INSERT INTO Raza (ID_Raza, Nombre_Raza) VALUES (?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, raza.getId()); // Asumiendo que el ID se establece antes
            stmt.setString(2, raza.getNombre());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar raza: " + e.getMessage());
            throw e;
        }
    }

    public void actualizarRaza(Raza raza) throws SQLException {
        String sql = "UPDATE Raza SET Nombre_Raza=? WHERE ID_Raza=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, raza.getNombre());
            stmt.setInt(2, raza.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar raza: " + e.getMessage());
            throw e;
        }
    }

    public void eliminarRaza(int idRaza) throws SQLException {
        String sql = "DELETE FROM Raza WHERE ID_Raza=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idRaza);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar raza: " + e.getMessage());
            throw e;
        }
    }
    // Aquí irían métodos para leer razas
}