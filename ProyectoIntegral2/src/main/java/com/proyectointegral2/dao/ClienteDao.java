package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Direccion;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

public class ClienteDao {
    private String url;
    private String user;
    private String pass;

    public ClienteDao() throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("config.properties"));
        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        pass = props.getProperty("db.pass");
    }

    public void insertarCliente(Cliente cliente) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "INSERT INTO clientes (nombre, apellido1, apellido2, fecha_nacimiento, provincia, ciudad, calle, codigo_postal, telefono, email, nif) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);

        Direccion dir = cliente.getDireccion();

        stmt.setString(1, cliente.getNombre());
        stmt.setString(2, cliente.getApellido1());
        stmt.setString(3, cliente.getApellido2());
        stmt.setDate(4, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
        stmt.setString(5, dir.getProvincia());
        stmt.setString(6, dir.getCiudad());
        stmt.setString(7, dir.getCalle());
        stmt.setString(8, dir.getCodigoPostal());
        stmt.setString(9, cliente.getTelefono());
        stmt.setString(10, cliente.getEmail());
        stmt.setString(11, cliente.getNif());

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void actualizarCliente(Cliente cliente) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "UPDATE clientes SET nombre=?, apellido1=?, apellido2=?, fecha_nacimiento=?, provincia=?, ciudad=?, calle=?, codigo_postal=?, telefono=?, email=? WHERE nif=?";
        PreparedStatement stmt = conn.prepareStatement(sql);

        Direccion dir = cliente.getDireccion();

        stmt.setString(1, cliente.getNombre());
        stmt.setString(2, cliente.getApellido1());
        stmt.setString(3, cliente.getApellido2());
        stmt.setDate(4, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
        stmt.setString(5, dir.getProvincia());
        stmt.setString(6, dir.getCiudad());
        stmt.setString(7, dir.getCalle());
        stmt.setString(8, dir.getCodigoPostal());
        stmt.setString(9, cliente.getTelefono());
        stmt.setString(10, cliente.getEmail());
        stmt.setString(11, cliente.getNif());

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }
}