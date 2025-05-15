// src/main/java/com/proyectointegral2/dao/ClienteDao.java
package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Direccion;
import com.proyectointegral2.utils.ConexionDB;
import com.proyectointegral2.utils.UtilidadesExcepciones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClienteDao {

    public static boolean insertarCliente(Cliente cliente) {
        String sql = "INSERT INTO Cliente (NIF, Nombre, Apellido1, Apellido2, Provincia, Ciudad, Calle, CP, Telefono, Email, ID_Usuario) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Datos personales
            stmt.setString(1, cliente.getNif());
            stmt.setString(2, cliente.getNombre());
            stmt.setString(3, cliente.getApellido1());
            stmt.setString(4, cliente.getApellido2());

            // Dirección (usando cliente.getDireccion())
            stmt.setString(5, cliente.getDireccion().getProvincia());
            stmt.setString(6, cliente.getDireccion().getCiudad());
            stmt.setString(7, cliente.getDireccion().getCalle());
            stmt.setString(8, cliente.getDireccion().getCodigoPostal());

            // Contacto
            stmt.setString(9, cliente.getTelefono());
            stmt.setString(10, cliente.getEmail());

            // Relación con Usuario
            stmt.setInt(11, cliente.getIdUsuario());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            UtilidadesExcepciones.mostrarError(e, "Error al insertar cliente", "No se pudo registrar el cliente en la base de datos.");
            return false;
        }
    }


}