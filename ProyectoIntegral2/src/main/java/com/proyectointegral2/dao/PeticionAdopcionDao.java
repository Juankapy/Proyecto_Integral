package com.proyectointegral2.dao;

import com.proyectointegral2.Model.PeticionAdopcion;
import com.proyectointegral2.Model.RegistroAdopcionInfo;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeticionAdopcionDao {

    public int crearPeticionAdopcion(PeticionAdopcion peticion) throws SQLException {
        String sqlInsert = "INSERT INTO PETICIONES_ADOPCION (FECHA, ESTADO_ADOPCION, ID_CLIENTE, ID_PERRO, ID_PROTECTORA) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, peticion.getFecha());
            pstmt.setString(2, peticion.getEstado() != null ? peticion.getEstado() : "Pendiente");
            pstmt.setInt(3, peticion.getIdCliente());
            pstmt.setInt(4, peticion.getIdPerro());
            pstmt.setInt(5, peticion.getIdProtectora());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) throw new SQLException("No se pudo crear la petición.");
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado.");
                }
            }
        }
    }

    public List<RegistroAdopcionInfo> obtenerInfoAdopcionesPorProtectora(int idProtectora) {
        List<RegistroAdopcionInfo> lista = new ArrayList<>();
        String sql = "SELECT pa.ID_PETICION, pa.FECHA, p.NOMBRE AS NombrePerro, c.NOMBRE AS NombreAdoptante, c.TELEFONO AS NumeroContacto, pa.ESTADO_ADOPCION " +
                "FROM PETICIONES_ADOPCION pa " +
                "JOIN PERROS p ON pa.ID_PERRO = p.ID_PERRO " +
                "JOIN CLIENTE c ON pa.ID_CLIENTE = c.ID_CLIENTE " +
                "WHERE pa.ID_PROTECTORA = ? " +
                "ORDER BY pa.FECHA DESC, pa.ESTADO_ADOPCION DESC";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProtectora);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RegistroAdopcionInfo info = new RegistroAdopcionInfo();
                    info.setIdPeticion(rs.getInt("ID_PETICION"));
                    java.sql.Date fechaSql = rs.getDate("FECHA");
                    info.setFechaPeticion(fechaSql != null ? fechaSql.toLocalDate() : null);
                    info.setNombrePerro(rs.getString("NombrePerro"));
                    info.setNombreAdoptante(rs.getString("NombreAdoptante"));
                    info.setNumeroContacto(rs.getString("NumeroContacto"));
                    info.setEstadoPeticion(rs.getString("ESTADO_ADOPCION"));
                    lista.add(info);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}