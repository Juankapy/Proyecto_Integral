package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDao {

    /**
     * Crea un nuevo cliente en la base de datos.
     * El ID_CLIENTE es generado por una secuencia.
     * @param cliente Objeto Cliente con los datos a insertar.
     * @return El ID del cliente generado si la creación es exitosa, -1 en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public int crearCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO CLIENTE (NIF, NOMBRE, APELLIDOS, FECHA_NACIMIENTO, PROVINCIA, CIUDAD, CALLE, CP, TELEFONO, EMAIL, ID_USUARIO, RUTA_FOTO_PERFIL) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int nuevoIdCliente = -1;

        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Oracle necesita saber qué columna es la generada para devolverla
            pstmt = conn.prepareStatement(sql, new String[]{"ID_CLIENTE"});

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

            if (cliente.getIdUsuario() > 0) { // O alguna otra validación para ID_Usuario
                pstmt.setInt(11, cliente.getIdUsuario());
            } else {
                pstmt.setNull(11, Types.NUMERIC); // O manejarlo como un error si es obligatorio
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
            } else {
                // Esto puede pasar con Oracle y DEFAULT SEQ.NEXTVAL si el driver no lo soporta bien
                // Si affectedRows > 0, la inserción fue exitosa.
                // Si necesitas el ID de vuelta obligatoriamente, considera un SELECT SEQ_CLIENTE_ID.CURRVAL
                // o una cláusula RETURNING en el INSERT.
                System.out.println("Advertencia: Cliente insertado, pero no se pudo recuperar el ID generado directamente por getGeneratedKeys.");
                // Si el ID_CLIENTE se establece antes de llamar a crearCliente (ej. desde la secuencia),
                // podrías simplemente devolver ese valor si affectedRows > 0.
                // Por ahora, si no se obtiene aquí, devolvemos -1 o el ID si se asignó previamente.
                // Es más seguro confiar en la secuencia de la BD y obtener el CURRVAL si es necesario.
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
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) { /* ignore */ }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { /* ignore */ }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { /* ignore */ }
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
        } catch (SQLException e) {
            System.err.println("Error SQL al obtener cliente por ID: " + e.getMessage());
            throw e;
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
        } catch (SQLException e) {
            System.err.println("Error SQL al obtener cliente por ID de Usuario: " + e.getMessage());
            throw e;
        }
        return null;
    }

    public List<Cliente> obtenerTodosLosClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM CLIENTE ORDER BY APELLIDOS, NOMBRE"; // Usa nombres de columna de BD
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al obtener todos los clientes: " + e.getMessage());
            throw e;
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
        } catch (SQLException e) {
            System.err.println("Error SQL al actualizar cliente: " + e.getMessage());
            throw e;
        }
    }

    public boolean eliminarCliente(int idCliente) throws SQLException {
        String sql = "DELETE FROM CLIENTE WHERE ID_CLIENTE = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCliente);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al eliminar cliente: " + e.getMessage());
            throw e;
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
        cliente.setRutaFotoPerfil(rs.getString("RUTA_FOTO_PERFIL")); // Mapear nueva columna
        return cliente;
    }
}