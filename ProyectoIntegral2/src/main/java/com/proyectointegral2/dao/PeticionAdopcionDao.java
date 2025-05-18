package com.proyectointegral2.dao;

import com.proyectointegral2.Model.PeticionAdopcion;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeticionAdopcionDao{

    public int crearPeticionAdopcion(PeticionAdopcion peticion) throws SQLException {
        String sqlInsert = "INSERT INTO Peticiones_Adopcion (Fecha, Estado, ID_Cliente, ID_Perro) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, peticion.getFecha());
            pstmt.setString(2, peticion.getEstado() != null ? peticion.getEstado() : "Pendiente");
            pstmt.setInt(3, peticion.getIdCliente());
            pstmt.setInt(4, peticion.getIdPerro());
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

    public PeticionAdopcion obtenerPeticionAdopcionPorId(int idPeticion) throws SQLException {
        String sql = "SELECT * FROM Peticiones_Adopcion WHERE ID_Peticion = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPeticion);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPeticionAdopcion(rs);
                }
            }
        }
        return null;
    }

    public List<PeticionAdopcion> obtenerPeticionesPorCliente(int idCliente) throws SQLException {
        List<PeticionAdopcion> peticiones = new ArrayList<>();
        String sql = "SELECT * FROM Peticiones_Adopcion WHERE ID_Cliente = ? ORDER BY Fecha DESC";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    peticiones.add(mapResultSetToPeticionAdopcion(rs));
                }
            }
        }
        return peticiones;
    }

    public List<PeticionAdopcion> obtenerPeticionesPorPerro(int idPerro) throws SQLException {
        List<PeticionAdopcion> peticiones = new ArrayList<>();
        String sql = "SELECT * FROM Peticiones_Adopcion WHERE ID_Perro = ? ORDER BY Fecha DESC";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPerro);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    peticiones.add(mapResultSetToPeticionAdopcion(rs));
                }
            }
        }
        return peticiones;
    }

    public List<PeticionAdopcion> obtenerPeticionesPorEstado(String estado) throws SQLException {
        List<PeticionAdopcion> peticiones = new ArrayList<>();
        String sql = "SELECT * FROM Peticiones_Adopcion WHERE Estado = ? ORDER BY Fecha DESC";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, estado);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    peticiones.add(mapResultSetToPeticionAdopcion(rs));
                }
            }
        }
        return peticiones;
    }

    @Override
    public List<PeticionAdopcion> obtenerPeticionesPorProtectora(int idProtectora) throws SQLException {
        List<PeticionAdopcion> peticiones = new ArrayList<>();
        String sql = "SELECT pa.* FROM Peticiones_Adopcion pa " +
                "JOIN Perros p ON pa.ID_Perro = p.ID_Perro " +
                "WHERE p.ID_Protectora = ? ORDER BY pa.Fecha DESC";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProtectora);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    peticiones.add(mapResultSetToPeticionAdopcion(rs));
                }
            }
        }
        return peticiones;
    }

    public boolean actualizarEstadoPeticion(int idPeticion, String nuevoEstado) throws SQLException {
        String sql = "UPDATE Peticiones_Adopcion SET Estado = ? WHERE ID_Peticion = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idPeticion);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarPeticionAdopcion(int idPeticion) throws SQLException {
        String sql = "DELETE FROM Peticiones_Adopcion WHERE ID_Peticion = ?";
        try (Connection conn = ConexionDB.getConnection() ;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPeticion);
            return pstmt.executeUpdate() > 0;
        }
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