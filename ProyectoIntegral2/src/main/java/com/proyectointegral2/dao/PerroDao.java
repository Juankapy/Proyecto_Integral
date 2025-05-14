package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.Properties;

public class PerroDao {
    private String url;
    private String user;
    private String pass;

    public PerroDao() throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream("config.properties"));
        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        pass = props.getProperty("db.pass");
    }

    public void insertarPerro(Perro perro) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "INSERT INTO perros (id, nombre, foto, fecha_nacimiento, sexo, adoptado, raza_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, perro.getId());
        stmt.setString(2, perro.getNombre());
        stmt.setString(3, perro.getFoto());
        stmt.setDate(4, Date.valueOf(perro.getFechaNacimiento()));
        stmt.setString(5, perro.getSexo());
        stmt.setBoolean(6, perro.isAdoptado());
        stmt.setInt(7, perro.getRaza().getId());
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void actualizarPerro(Perro perro) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "UPDATE perros SET nombre=?, foto=?, fecha_nacimiento=?, sexo=?, adoptado=?, raza_id=? WHERE id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, perro.getNombre());
        stmt.setString(2, perro.getFoto());
        stmt.setDate(3, Date.valueOf(perro.getFechaNacimiento()));
        stmt.setString(4, perro.getSexo());
        stmt.setBoolean(5, perro.isAdoptado());
        stmt.setInt(6, perro.getRaza().getId());
        stmt.setInt(7, perro.getId());
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }

    public void eliminarPerro(int id) throws Exception {
        Connection conn = DriverManager.getConnection(url, user, pass);
        String sql = "DELETE FROM perros WHERE id=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    }
}