package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Patologia;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

public class PatologiaDao {
    private String url;
    private String user;
    private String pass;

    public PatologiaDao() throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("config.properties"));
        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        pass = props.getProperty("db.pass");
    }

    public void insertarPatologia(Patologia patologia) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "INSERT INTO patologias (id, nombre, descripcion) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, patologia.getId());
        stmt.setString(2, patologia.getNombre());
        stmt.setString(3, patologia.getDescripcion());
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void actualizarPatologia(Patologia patologia) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "UPDATE patologias SET nombre=?, descripcion=? WHERE id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, patologia.getNombre());
        stmt.setString(2, patologia.getDescripcion());
        stmt.setInt(3, patologia.getId());
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void eliminarPatologia(int id) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "DELETE FROM patologias WHERE id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }
}