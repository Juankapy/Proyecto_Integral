package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Raza;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

public class RazaDao {
    private String url;
    private String user;
    private String pass;

    public RazaDao() throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("config.properties"));
        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        pass = props.getProperty("db.pass");
    }

    public void insertarRaza(Raza raza) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "INSERT INTO razas (id, nombre) VALUES (?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, raza.getId());
        stmt.setString(2, raza.getNombre());
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void actualizarRaza(Raza raza) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "UPDATE razas SET nombre=? WHERE id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, raza.getNombre());
        stmt.setInt(2, raza.getId());
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void eliminarRaza(int id) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "DELETE FROM razas WHERE id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }
}