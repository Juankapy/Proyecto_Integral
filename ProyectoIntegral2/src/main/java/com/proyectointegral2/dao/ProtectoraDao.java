package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Direccion;
import com.proyectointegral2.utils.ConexionDB;
import com.proyectointegral2.utils.UtilidadesExcepciones;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class ProtectoraDao {
    private String url;
    private String user;
    private String pass;

    public static boolean insertarProtectora(Protectora protectora) {
        String sql = "INSERT INTO Protectora (CIF, Nombre, Provincia, Ciudad, Calle, CP, Telefono, Email, ID_Usuario) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Datos de la protectora
            stmt.setString(1, protectora.getCif());
            stmt.setString(2, protectora.getNombre());

            // Dirección (usando protectora.getDireccion())
            Direccion direccion = protectora.getDireccion();
            stmt.setString(3, direccion.getProvincia());
            stmt.setString(4, direccion.getCiudad());
            stmt.setString(5, direccion.getCalle());
            stmt.setString(6, direccion.getCodigoPostal());

            // Contacto
            stmt.setString(7, protectora.getTelefono());
            stmt.setString(8, protectora.getEmail());
            stmt.setInt(9, protectora.getIdUsuario());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            UtilidadesExcepciones.mostrarError(e, "Error al insertar protectora", "No se pudo registrar la protectora en la base de datos.");
            return false;
        }
    }

}