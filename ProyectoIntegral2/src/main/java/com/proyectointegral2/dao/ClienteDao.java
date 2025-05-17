package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Direccion;
import com.proyectointegral2.utils.ConexionDB; // Usar la clase de conexión centralizada

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date; // Usar java.sql.Date

public class ClienteDao {

    public static boolean insertarCliente(Cliente cliente) {
        String sql = "INSERT INTO Cliente (NOMBRE, APELLIDOs, FECHANACIMIENTO, PROVINCIA, CIUDAD, CALLE, CP, TELEFONO, EMAIL, NIF, ID_USUARIO) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getApellidos());
            stmt.setDate(3, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
            stmt.setString(4, cliente.getDireccion().getProvincia());
            stmt.setString(5, cliente.getDireccion().getCiudad());
            stmt.setString(6, cliente.getDireccion().getCalle());
            stmt.setString(7, cliente.getDireccion().getCodigoPostal());
            stmt.setString(8, cliente.getTelefono());
            stmt.setString(9, cliente.getEmail());
            stmt.setString(10, cliente.getNif());
            stmt.setInt(11, cliente.getIdUsuario());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}