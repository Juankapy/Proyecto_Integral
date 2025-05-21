package com.proyectointegral2.dao;

import com.proyectointegral2.Model.PeticionAdopcion;
import com.proyectointegral2.Model.RegistroAdopcionInfo;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeticionAdopcionDao{

    public int crearPeticionAdopcion(PeticionAdopcion peticion) throws SQLException {
        String sqlInsert = "INSERT INTO Peticiones_Adopcion (Fecha, Estado, ID_Cliente, ID_Perro,MENSAJE_PETICION) VALUES (?, ?, ?, ?,?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, peticion.getFecha());
            pstmt.setString(2, peticion.getEstado() != null ? peticion.getEstado() : "Pendiente");
            pstmt.setInt(3, peticion.getIdCliente());
            pstmt.setInt(4, peticion.getIdPerro());
            pstmt.setString(5, peticion.getMensajePeticion());
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

    public List<RegistroAdopcionInfo> obtenerAdopcionesAceptadasParaTabla(int idProtectora) throws SQLException {
        List<RegistroAdopcionInfo> registros = new ArrayList<>();
        String sql = "SELECT pa.ID_PETICION, p.NOMBRE AS NOMBRE_PERRO, pa.FECHA AS FECHA_ADOPCION, " +
                "c.NOMBRE || ' ' || c.APELLIDOS AS NOMBRE_ADOPTANTE, c.EMAIL AS CONTACTO_ADOPTANTE " +
                // " 'N/A' AS HORA_ADOPCION " + // Si no tienes hora específica de adopción
                "FROM PETICIONES_ADOPCION pa " +
                "JOIN PERROS p ON pa.ID_PERRO = p.ID_PERRO " +
                "JOIN CLIENTE c ON pa.ID_CLIENTE = c.ID_CLIENTE " +
                "WHERE p.ID_PROTECTORA = ? AND pa.ESTADO = 'Aceptada' " + // Solo las aceptadas
                "ORDER BY pa.FECHA DESC, p.NOMBRE ASC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ConexionDB.getConnection(); // Tu clase de conexión
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idProtectora);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int idPeticion = rs.getInt("ID_PETICION");
                String nombrePerro = rs.getString("NOMBRE_PERRO");
                LocalDate fechaAdopcion = rs.getDate("FECHA_ADOPCION").toLocalDate();
                String horaAdopcion = "N/A"; // Si tuvieras una columna de hora en PETICIONES_ADOPCION, la leerías aquí
                String nombreAdoptante = rs.getString("NOMBRE_ADOPTANTE");
                String contactoAdoptante = rs.getString("CONTACTO_ADOPTANTE"); // Podrías preferir el teléfono: c.TELEFONO

                registros.add(new RegistroAdopcionInfo(idPeticion, nombrePerro, fechaAdopcion, horaAdopcion, nombreAdoptante, contactoAdoptante));
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return registros;
    }

    private PeticionAdopcion mapResultSetToPeticionAdopcion(ResultSet rs) throws SQLException {
        PeticionAdopcion peticion = new PeticionAdopcion();
        peticion.setIdPeticion(rs.getInt("ID_Peticion"));
        peticion.setFecha(rs.getDate("Fecha"));
        peticion.setEstado(rs.getString("Estado"));
        peticion.setIdCliente(rs.getInt("ID_Cliente"));
        peticion.setIdPerro(rs.getInt("ID_Perro"));
        return peticion;
    }
}