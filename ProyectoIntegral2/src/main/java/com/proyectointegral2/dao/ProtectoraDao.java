package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Direccion;
import com.proyectointegral2.utils.ConexionDB; // Usar la clase de conexión centralizada

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
// No necesitas Date aquí si no hay campo de fecha en la tabla Protectora,
// pero si tu modelo Protectora tiene algún campo de fecha que no está en la tabla, revísalo.

public class ProtectoraDao {

    public ProtectoraDao() {
        // Constructor vacío si ConexionDB es estática
    }

    public void insertarProtectora(Protectora protectora) throws SQLException {
        // Columnas según tu esquema: ID_Protectora, Nombre, Telefono, Email, Provincia, Ciudad, Calle, CP, ID_Usuario
        // El campo CIF no está en tu tabla Protectora, pero sí NIF en Cliente. Revisa esto.
        // La columna "FECHA_NACIMIENTO" que mencionaste en los comentarios NO está en tu CREATE TABLE de Protectora.
        // La tabla Protectora tiene ID_USUARIO.
        String sql = "INSERT INTO Protectora (ID_Protectora, Nombre, Telefono, Email, Provincia, Ciudad, Calle, CP, ID_Usuario) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Direccion dir = protectora.getDireccion();
            if (dir == null) {
                // Considera qué hacer si la dirección es nula. ¿Es permitida?
                // Por ahora, lanzaremos una excepción o pondremos valores nulos si la BD lo permite.
                // Esto dependerá de si los campos de dirección en la tabla Protectora son NOT NULL.
                // Tu esquema dice que Provincia, Ciudad, Calle, CP en Cliente son NOT NULL.
                // Para Protectora, tu esquema dice:
                // Provincia VARCHAR2(20)NOT NULL, Ciudad VARCHAR2(150)NOT NULL, Calle VARCHAR2(150)NOT NULL, CP VARCHAR2(5)NOT NULL
                // Así que la dirección NO PUEDE SER NULA. Deberías validar esto antes.
                throw new IllegalArgumentException("La dirección de la protectora no puede ser nula.");
            }

            stmt.setInt(1, protectora.getId()); // Asumiendo que el ID se establece antes
            stmt.setString(2, protectora.getNombre());
            stmt.setString(3, protectora.getTelefono());
            stmt.setString(4, protectora.getEmail());
            stmt.setString(5, dir.getProvincia());
            stmt.setString(6, dir.getCiudad());
            stmt.setString(7, dir.getCalle());
            stmt.setString(8, dir.getCodigoPostal());

            // Manejo de ID_Usuario (puede ser nulo según tu esquema)
            if (protectora.getIdUsuario() > 0) { // Asumiendo que Protectora tiene un getIdUsuario()
                stmt.setInt(9, protectora.getIdUsuario());
            } else {
                stmt.setNull(9, java.sql.Types.NUMERIC);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar protectora: " + e.getMessage());
            throw e;
        }
    }

    // Aquí irían métodos para actualizar, eliminar, buscar protectoras, etc.
}