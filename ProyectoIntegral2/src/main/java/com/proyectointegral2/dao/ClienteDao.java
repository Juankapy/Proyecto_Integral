package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.Model.Direccion;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDao {

    public int crearCliente(Cliente cliente) throws SQLException {

        String sqlSelectId = "SELECT SEQ_CLIENTE_ID.NEXTVAL FROM DUAL";
        String sqlInsert = "INSERT INTO Cliente (ID_Cliente, NIF, Nombre, Apellidos, Fecha_Nacimiento, Provincia, Ciudad, Calle, CP, Telefono, Email, ID_Usuario) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int nuevoId = -1;

        try (Connection conn = ConexionDB.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtId = conn.prepareStatement(sqlSelectId);
                 ResultSet rsId = pstmtId.executeQuery()) {
                if (rsId.next()) {
                    nuevoId = rsId.getInt(1);
                } else {
                    conn.rollback();
                    throw new SQLException("No se pudo obtener el ID de la secuencia SEQ_CLIENTE_ID.");
                }
            }

            if (nuevoId != -1) {
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                    pstmt.setInt(1, nuevoId);
                    pstmt.setString(2, cliente.getNif());
                    pstmt.setString(3, cliente.getNombre());
                    pstmt.setString(4, cliente.getApellidos());
                    pstmt.setDate(5, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
                    pstmt.setString(6, cliente.getDireccion().getProvincia());
                    pstmt.setString(7, cliente.getDireccion().getCiudad());
                    pstmt.setString(8, cliente.getDireccion().getCalle());
                    pstmt.setString(9, cliente.getDireccion().getCodigoPostal());
                    pstmt.setString(10, cliente.getTelefono());
                    pstmt.setString(11, cliente.getEmail());
                    pstmt.setInt(12, cliente.getIdUsuario());

                    if (pstmt.executeUpdate() > 0) {
                        conn.commit();
                        return nuevoId;
                    }
                }
            }
            conn.rollback();
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Cliente obtenerClientePorId(int idCliente) throws SQLException {
        String sql = "SELECT * FROM Cliente WHERE ID_Cliente = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public Cliente obtenerClientePorIdUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM Cliente WHERE ID_Usuario = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public Cliente obtenerClientePorNIF(String nif) throws SQLException {
        String sql = "SELECT * FROM Cliente WHERE NIF = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nif);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public List<Cliente> obtenerTodosLosClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM Cliente ORDER BY Apellidos, Nombre";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return clientes;
    }

    public boolean actualizarCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE Cliente SET NIF = ?, Nombre = ?, Apellidos = ?, Fecha_Nacimiento = ?, " +
                "Provincia = ?, Ciudad = ?, Calle = ?, CP = ?, Telefono = ?, Email = ?, ID_Usuario = ? " +
                "WHERE ID_Cliente = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getNif());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getApellidos());
            pstmt.setDate(4, java.sql.Date.valueOf(cliente.getFechaNacimiento()));
            pstmt.setString(5, cliente.getDireccion().getProvincia());
            pstmt.setString(6, cliente.getDireccion().getCiudad());
            pstmt.setString(7, cliente.getDireccion().getCalle());
            pstmt.setString(8, cliente.getDireccion().getCodigoPostal());
            pstmt.setString(9, cliente.getTelefono());
            pstmt.setString(10, cliente.getEmail());
            pstmt.setInt(11, cliente.getIdUsuario());
            pstmt.setInt(12, cliente.getIdCliente());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean eliminarCliente(int idCliente) throws SQLException {
        String sql = "DELETE FROM Cliente WHERE ID_Cliente = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("ID_Cliente"));
        cliente.setNif(rs.getString("NIF"));
        cliente.setNombre(rs.getString("Nombre"));
        cliente.setApellidos(rs.getString("Apellidos"));
        cliente.setFechaNacimiento(rs.getDate("FechaNacimiento").toLocalDate());

        // Crear y setear la dirección
        Direccion direccion = new Direccion();
        direccion.setProvincia(rs.getString("Provincia"));
        direccion.setCiudad(rs.getString("Ciudad"));
        direccion.setCalle(rs.getString("Calle"));
        direccion.setCodigoPostal(rs.getString("CP"));
        cliente.setDireccion(direccion);

        cliente.setTelefono(rs.getString("Telefono"));
        cliente.setEmail(rs.getString("Email"));
        cliente.setIdUsuario(rs.getInt("ID_Usuario"));
        return cliente;
    }
}