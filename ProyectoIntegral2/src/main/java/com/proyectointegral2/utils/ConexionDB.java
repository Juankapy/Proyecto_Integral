package com.proyectointegral2.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConexionDB {

    public static Connection getConnection() {
        Properties prop = new Properties();

        try {
            prop.load(ConexionDB.class.getClassLoader().getResourceAsStream("config.properties"));
            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");

            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
