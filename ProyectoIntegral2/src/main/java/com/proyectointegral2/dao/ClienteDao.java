package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDao {

    public int crearCliente(Cliente cliente) throws SQLException {
        String sqlInsert = "INSERT INTO CLIENTE (NIF, NOMBRE, APELLIDOS, FECHA_NACIMIENTO, PROVINCIA, CIUDAD, CALLE, CP, TELEFONO, EMAIL, ID_USUARIO, RUTA_FOTO_PERFIL) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int nuevoIdCliente = -1;

        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sqlInsert, new String[]{"ID_CLIENTE"});

            pstmt.setString(1, cliente.getNif());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getApellidos());
            if (cliente.getFechaNacimiento() != null) {
                pstmt.setDate(4, Date.valueOf(cliente.getFechaNacimiento()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            pstmt.setString(5, cliente.getProvincia());
            pstmt.setString(6, cliente.getCiudad());
            pstmt.setString(7, cliente.getCalle());
            pstmt.setString(8, cliente.getCodigoPostal());
            pstmt.setString(9, cliente.getTelefono());
            pstmt.setString(10, cliente.getEmail());

            if (cliente.getIdUsuario() > 0) {
                pstmt.setInt(11, cliente.getIdUsuario());
            } else {
                pstmt.setNull(11, Types.NUMERIC);
            }
            pstmt.setString(12, cliente.getRutaFotoPerfil());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                conn.rollback();
                throw new SQLException("No se pudo crear el cliente, ninguna fila afectada.");
            }

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                nuevoIdCliente = generatedKeys.getInt(1);
                cliente.setIdCliente(nuevoIdCliente);
            } else {
                System.out.println("Advertencia: Cliente insertado, pero no se recuperó ID con getGeneratedKeys.");
            }
            conn.commit();
            return nuevoIdCliente;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error haciendo rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error SQL al crear cliente: " + e.getMessage());
            throw e;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException ex) { }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) {  }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {  }
        }
    }

    public Cliente obtenerClientePorId(int idCliente) throws SQLException {
        String sql = "SELECT * FROM CLIENTE WHERE ID_CLIENTE = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        }
        return null;
    }

    public Cliente obtenerClientePorIdUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM CLIENTE WHERE ID_USUARIO = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        }
        return null;
    }

    public Cliente obtenerClientePorNIF(String nif) throws SQLException {
        String sql = "SELECT * FROM CLIENTE WHERE NIF = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nif);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        }
        return null;
    }


    public List<Cliente> obtenerTodosLosClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM CLIENTE ORDER BY APELLIDOS, NOMBRE";
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        }
        return clientes;
    }

    public boolean actualizarCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE CLIENTE SET NIF = ?, NOMBRE = ?, APELLIDOS = ?, FECHA_NACIMIENTO = ?, " +
                "PROVINCIA = ?, CIUDAD = ?, CALLE = ?, CP = ?, TELEFONO = ?, EMAIL = ?, ID_USUARIO = ?, RUTA_FOTO_PERFIL = ? " +
                "WHERE ID_CLIENTE = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getNif());
            pstmt.setString(2, cliente.getNombre());
            pstmt.setString(3, cliente.getApellidos());
            if (cliente.getFechaNacimiento() != null) {
                pstmt.setDate(4, Date.valueOf(cliente.getFechaNacimiento()));
            } else {
                pstmt.setNull(4, Types.DATE);
            }
            pstmt.setString(5, cliente.getProvincia());
            pstmt.setString(6, cliente.getCiudad());
            pstmt.setString(7, cliente.getCalle());
            pstmt.setString(8, cliente.getCodigoPostal());
            pstmt.setString(9, cliente.getTelefono());
            pstmt.setString(10, cliente.getEmail());
            if (cliente.getIdUsuario() > 0) {
                pstmt.setInt(11, cliente.getIdUsuario());
            } else {
                pstmt.setNull(11, Types.NUMERIC);
            }
            pstmt.setString(12, cliente.getRutaFotoPerfil());
            pstmt.setInt(13, cliente.getIdCliente());
            return pstmt.executeUpdate() > 0;
        }
    }

    public Cliente obtenerClientePorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM CLIENTE WHERE EMAIL = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        }
        return null;
    }


    public boolean eliminarCliente(int idCliente) throws SQLException {
        String sql = "DELETE FROM CLIENTE WHERE ID_CLIENTE = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("ID_CLIENTE"));
        cliente.setNif(rs.getString("NIF"));
        cliente.setNombre(rs.getString("NOMBRE"));
        cliente.setApellidos(rs.getString("APELLIDOS"));
        Date fechaNacSQL = rs.getDate("FECHA_NACIMIENTO");
        if (fechaNacSQL != null) {
            cliente.setFechaNacimiento(fechaNacSQL.toLocalDate());
        }
        cliente.setProvincia(rs.getString("PROVINCIA"));
        cliente.setCiudad(rs.getString("CIUDAD"));
        cliente.setCalle(rs.getString("CALLE"));
        cliente.setCodigoPostal(rs.getString("CP"));

        cliente.setTelefono(rs.getString("TELEFONO"));
        cliente.setEmail(rs.getString("EMAIL"));
        cliente.setIdUsuario(rs.getInt("ID_USUARIO"));
        cliente.setRutaFotoPerfil(rs.getString("RUTA_FOTO_PERFIL"));
        return cliente;
    }
}