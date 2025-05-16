package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Perro;
import com.proyectointegral2.Model.Raza;
// No Protectora model import needed if Perro only has idProtectora (int)
import com.proyectointegral2.utils.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PerroDao {

    public PerroDao() {
        // Constructor
    }

    public void insertarPerro(Perro perro) throws SQLException {
        String sql = "INSERT INTO Perros (ID_Perro, Nombre, Sexo, FechaNacimiento, Adoptado, Foto, ID_Protectora, ID_Raza) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, perro.getId());
            stmt.setString(2, perro.getNombre());
            stmt.setString(3, perro.getSexo());
            stmt.setDate(4, perro.getFechaNacimiento() != null ? Date.valueOf(perro.getFechaNacimiento()) : null);
            stmt.setString(5, perro.getAdoptadoChar()); // 'S' o 'N'
            stmt.setString(6, perro.getFoto()); // Asumiendo FOTO en BD es VARCHAR2 para la ruta

            if (perro.getIdProtectora() > 0) {
                stmt.setInt(7, perro.getIdProtectora());
            } else {
                stmt.setNull(7, java.sql.Types.NUMERIC);
            }

            if (perro.getRaza() != null && perro.getRaza().getId() > 0) {
                stmt.setInt(8, perro.getRaza().getId());
            } else {
                stmt.setNull(8, java.sql.Types.NUMERIC);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar perro: " + e.getMessage());
            throw e;
        }
    }

    public void actualizarPerro(Perro perro) throws SQLException {
        String sql = "UPDATE Perros SET Nombre=?, Foto=?, FechaNacimiento=?, Sexo=?, Adoptado=?, ID_Raza=?, ID_Protectora=? WHERE ID_Perro=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, perro.getNombre());
            stmt.setString(2, perro.getFoto()); // FOTO es String (ruta)
            stmt.setDate(3, perro.getFechaNacimiento() != null ? Date.valueOf(perro.getFechaNacimiento()) : null);
            stmt.setString(4, perro.getSexo());
            stmt.setString(5, perro.getAdoptadoChar()); // 'S' o 'N'

            if (perro.getRaza() != null && perro.getRaza().getId() > 0) {
                stmt.setInt(6, perro.getRaza().getId());
            } else {
                stmt.setNull(6, java.sql.Types.NUMERIC);
            }

            if (perro.getIdProtectora() > 0) {
                stmt.setInt(7, perro.getIdProtectora());
            } else {
                stmt.setNull(7, java.sql.Types.NUMERIC);
            }
            stmt.setInt(8, perro.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar perro: " + e.getMessage());
            throw e;
        }
    }

    public void eliminarPerro(int idPerro) throws SQLException {
        String sql = "DELETE FROM Perros WHERE ID_Perro=?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPerro);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar perro: " + e.getMessage());
            throw e;
        }
    }

    public List<Perro> obtenerTodosLosPerrosDisponibles() throws SQLException {
        List<Perro> perros = new ArrayList<>();
        // SQL para obtener perros con nombre de raza y nombre de protectora
        String sql = "SELECT p.ID_Perro, p.Nombre, p.Sexo, p.FechaNacimiento, p.Adoptado, p.Foto, " +
                "p.ID_Protectora, prot.Nombre AS Nombre_Protectora, " +
                "p.ID_Raza, r.Nombre_Raza " +
                "FROM Perros p " +
                "LEFT JOIN Raza r ON p.ID_Raza = r.ID_Raza " +
                "LEFT JOIN Protectora prot ON p.ID_Protectora = prot.ID_Protectora " +
                "WHERE p.Adoptado = 'N'"; // Solo los no adoptados

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Perro perro = new Perro();
                perro.setId(rs.getInt("ID_Perro"));
                perro.setNombre(rs.getString("Nombre"));
                perro.setSexo(rs.getString("Sexo"));
                Date fechaNacSQL = rs.getDate("FechaNacimiento");
                perro.setFechaNacimiento((fechaNacSQL != null) ? fechaNacSQL.toLocalDate() : null);
                perro.setAdoptadoChar(rs.getString("Adoptado"));
                perro.setFoto(rs.getString("Foto")); // Leer FOTO como String (ruta)

                Raza razaObj = null;
                int idRaza = rs.getInt("ID_Raza");
                if (!rs.wasNull()) { // Mejor forma de chequear si el ID_Raza era NULL en la BD
                    razaObj = new Raza(idRaza, rs.getString("Nombre_Raza"));
                }
                perro.setRaza(razaObj);

                int idProtectora = rs.getInt("ID_Protectora");
                if (!rs.wasNull()) {
                    perro.setIdProtectora(idProtectora);
                    // Si Perro tuviera un campo para nombreProtectora:
                    // perro.setNombreProtectora(rs.getString("Nombre_Protectora"));
                }


                perros.add(perro);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener perros: " + e.getMessage());
            throw e;
        }
        return perros;
    }
}