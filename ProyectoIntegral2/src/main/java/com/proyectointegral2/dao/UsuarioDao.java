package com.proyectointegral2.dao;

import com.proyectointegral2.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDao {

    public boolean verificarCredenciales(String correo, String contrasena) {
        String query = "SELECT * FROM usuarios WHERE correo = ? AND contrasena = ?";
        try (Connection connection = ConexionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, correo);
            statement.setString(2, contrasena);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
