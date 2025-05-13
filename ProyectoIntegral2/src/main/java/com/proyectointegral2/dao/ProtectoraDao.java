package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Protectora;
import com.proyectointegral2.Model.Direccion;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

public class ProtectoraDao {
    private String url;
    private String user;
    private String pass;

    public ProtectoraDao() throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("config.properties"));
        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        pass = props.getProperty("db.pass");
    }

    public void insertarProtectora(Protectora protectora) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "INSERT INTO protectoras (nombre, provincia, ciudad, calle, codigo_postal, telefono, email, nif, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);

        Direccion dir = protectora.getDireccion();

        stmt.setString(1, protectora.getNombre());
        stmt.setString(2, dir.getProvincia());
        stmt.setString(3, dir.getCiudad());
        stmt.setString(4, dir.getCalle());
        stmt.setString(5, dir.getCodigoPostal());
        stmt.setString(6, protectora.getTelefono());
        stmt.setString(7, protectora.getEmail());
        stmt.setString(8, protectora.getCif());

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void actualizarProtectora(Protectora protectora) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "UPDATE protectoras SET nombre=?, provincia=?, ciudad=?, calle=?, codigo_postal=?, telefono=?, email=? WHERE nif=?";
        PreparedStatement stmt = conn.prepareStatement(sql);

        Direccion dir = protectora.getDireccion();

        stmt.setString(1, protectora.getNombre());
        stmt.setString(2, dir.getProvincia());
        stmt.setString(3, dir.getCiudad());
        stmt.setString(4, dir.getCalle());
        stmt.setString(5, dir.getCodigoPostal());
        stmt.setString(6, protectora.getTelefono());
        stmt.setString(7, protectora.getEmail());
        stmt.setString(8, protectora.getCif());

        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }
}