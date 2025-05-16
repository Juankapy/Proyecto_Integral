package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Patologia;
import com.proyectointegral2.utils.ConexionDB; // Usar la clase de conexión centralizada

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PatologiaDao {

    public PatologiaDao() {
        // Constructor vacío
    }

    public void insertarPatologia(Patologia patologia) throws SQLException {
        String sql = "INSERT INTO Patologia (ID_Patologia, Nombre) VALUES (?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patologia.getId());
            stmt.setString(2, patologia.getNombre());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar patología: " + e.getMessage());
            throw e;
        }
    }

    public void actualizarPatologia(Patologia patologia) throws SQLException {
        // Asumiendo que solo se actualiza el nombre
        String sql = "UPDATE Patologia SET Nombre=? WHERE ID_Patologia=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patologia.getNombre());
            stmt.setInt(2, patologia.getId());
            // Si añades descripción a la tabla: stmt.setString(2, patologia.getDescripcion()); y ajusta el SQL
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar patología: " + e.getMessage());
            throw e;
        }
    }

    public void eliminarPatologia(int idPatologia) throws SQLException {
        String sql = "DELETE FROM Patologia WHERE ID_Patologia=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Tu PreparedStatement original tenía una 'A' al final de la línea SQL, la he quitado.
            stmt.setInt(1, idPatologia);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar patología: " + e.getMessage());
            throw e;
        }
    }
}