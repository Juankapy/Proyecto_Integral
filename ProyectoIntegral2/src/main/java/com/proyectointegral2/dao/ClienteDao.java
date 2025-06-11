package com.proyectointegral2.dao;

import com.proyectointegral2.Model.Cliente;
import com.proyectointegral2.utils.ConexionDB; // Asumiendo que esta es tu clase de conexión
import com.proyectointegral2.utils.UtilidadesExcepciones;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para gestionar las operaciones CRUD de la entidad Cliente
 * en la base de datos.
 */
public class ClienteDao {

    // Nombres de columnas para consistencia y evitar errores tipográficos.
    // Asegúrate de que coincidan EXACTAMENTE con tu esquema de BD.
    private static final String TABLA_CLIENTE = "CLIENTE";
    private static final String COL_ID_CLIENTE = "ID_CLIENTE";
    private static final String COL_NIF = "NIF";
    private static final String COL_NOMBRE = "NOMBRE";
    private static final String COL_APELLIDOS = "APELLIDOS";
    private static final String COL_FECHA_NACIMIENTO = "FECHA_NACIMIENTO";
    private static final String COL_PROVINCIA = "PROVINCIA";
    private static final String COL_CIUDAD = "CIUDAD";
    private static final String COL_CALLE = "CALLE";
    private static final String COL_CP = "CP";
    private static final String COL_TELEFONO = "TELEFONO";
    private static final String COL_EMAIL = "EMAIL";
    private static final String COL_ID_USUARIO_FK = "ID_USUARIO"; // Columna FK a la tabla USUARIO
    private static final String COL_RUTA_FOTO_PERFIL = "RUTA_FOTO_PERFIL";


    /**
     * Crea un nuevo registro de cliente en la base de datos.
     * Utiliza una transacción para asegurar la atomicidad de la operación.
     *
     * @param cliente El objeto Cliente con los datos a insertar.
     * @return El ID generado para el nuevo cliente (ID_CLIENTE), o -1 si la creación falla
     *         o no se puede recuperar el ID generado.
     * @throws SQLException Si ocurre un error de base de datos durante la operación.
     */
    public int crearCliente(Cliente cliente) throws SQLException {
        String sqlInsert = "INSERT INTO " + TABLA_CLIENTE + " (" +
                COL_NIF + ", " + COL_NOMBRE + ", " + COL_APELLIDOS + ", " + COL_FECHA_NACIMIENTO + ", " +
                COL_PROVINCIA + ", " + COL_CIUDAD + ", " + COL_CALLE + ", " + COL_CP + ", " +
                COL_TELEFONO + ", " + COL_EMAIL + ", " + COL_ID_USUARIO_FK + ", " + COL_RUTA_FOTO_PERFIL + ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null; // Declarar fuera del try para acceso en catch y finally
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int nuevoIdCliente = -1; // Valor por defecto si no se obtiene el ID

        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Especificar que queremos recuperar la columna ID_CLIENTE generada.
            pstmt = conn.prepareStatement(sqlInsert, new String[]{COL_ID_CLIENTE});

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

            // ID_USUARIO es una FK, puede ser null si la relación lo permite, o debe ser > 0.
            // Tu DDL dice "ID_USUARIO NUMBER UNIQUE" y luego "REFERENCES USUARIO(ID_USUARIO) ON DELETE CASCADE",
            // lo que implica que es opcional (UNIQUE permite NULLs una vez, pero no múltiples NULLs si también es UNIQUE).
            // Sin embargo, para un cliente funcional, normalmente el ID_USUARIO no sería null.
            if (cliente.getIdUsuario() > 0) {
                pstmt.setInt(11, cliente.getIdUsuario());
            } else {
                // Si ID_USUARIO es mandatorio y no puede ser null, aquí deberías lanzar un error
                // o asegurar que siempre tenga un valor antes de llegar al DAO.
                // Por ahora, si es 0 o negativo, se intentará insertar NULL si la BD lo permite.
                pstmt.setNull(11, Types.NUMERIC);
                System.out.println("WARN: Intentando crear cliente con ID_USUARIO no válido o no establecido: " + cliente.getIdUsuario());
            }
            pstmt.setString(12, cliente.getRutaFotoPerfil());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                // No se insertó ninguna fila, esto es un error.
                conn.rollback(); // Revertir la transacción
                throw new SQLException("La creación del cliente falló, ninguna fila fue afectada.");
            }

            // Intentar obtener el ID_CLIENTE generado por la secuencia.
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                nuevoIdCliente = generatedKeys.getInt(1);
                cliente.setIdCliente(nuevoIdCliente); // Actualizar el objeto cliente con el ID real
                System.out.println("INFO: Cliente creado con ID: " + nuevoIdCliente);
            } else {
                // Esto es problemático. Si la inserción tuvo éxito (affectedRows > 0) pero no podemos
                // obtener el ID, la aplicación podría no funcionar correctamente después.
                // En Oracle, si usas una secuencia para el DEFAULT y pides getGeneratedKeys con el nombre de la columna,
                // debería funcionar. Si no, podrías tener que hacer un SELECT SEQ_CLIENTE_ID.CURRVAL.
                System.err.println("ADVERTENCIA: Cliente insertado, pero no se pudo recuperar el ID_CLIENTE con getGeneratedKeys(). Afectadas: " + affectedRows);
                // Podrías considerar hacer rollback aquí también si el ID es crucial para el flujo posterior.
                // conn.rollback();
                // throw new SQLException("Cliente creado, pero no se pudo obtener el ID generado.");
            }

            conn.commit(); // Confirmar la transacción si todo fue bien

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    System.err.println("ERROR SQL en crearCliente. Intentando rollback...");
                    conn.rollback(); // Intentar revertir en caso de cualquier SQLException
                } catch (SQLException exRollback) {
                    System.err.println("Error CRÍTICO al intentar hacer rollback: " + exRollback.getMessage());
                    exRollback.printStackTrace();
                }
            }
            System.err.println("Error SQL al crear el cliente: " + e.getMessage());
            e.printStackTrace();
            throw e; // Relanzar la excepción original para que la capa superior la maneje
        } finally {
            // Cerrar recursos en el orden inverso a su apertura
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            if (conn != null) {
                try {
                    if (!conn.getAutoCommit()) { // Solo si manejamos explícitamente el autocommit
                        conn.setAutoCommit(true); // Devolver al estado por defecto
                    }
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return nuevoIdCliente; // Puede ser -1 si no se obtuvo el ID
    }



    /**
     * Obtiene un cliente de la base de datos basado en el ID de su cuenta de Usuario asociada.
     * **Este es el método clave para tu problema de login.**
     *
     * @param idUsuario El ID del usuario (de la tabla USUARIO) asociado al cliente.
     * @return Un objeto Cliente si se encuentra, o null si no existe un cliente asociado.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public Cliente obtenerClientePorIdUsuario(int idUsuario) throws SQLException {
        Cliente cliente = null;
        String sql = "SELECT * FROM " + TABLA_CLIENTE + " WHERE " + COL_ID_USUARIO_FK + " = ?";
        System.out.println("DEBUG ClienteDao.obtenerClientePorIdUsuario: Buscando cliente con ID_USUARIO = " + idUsuario); // <<< LOG

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("DEBUG ClienteDao.obtenerClientePorIdUsuario: Fila encontrada para ID_USUARIO = " + idUsuario); // <<< LOG
                    cliente = mapResultSetToCliente(rs);
                    if (cliente != null) {
                        System.out.println("DEBUG ClienteDao.obtenerClientePorIdUsuario: Cliente mapeado. ID_CLIENTE = " + cliente.getIdCliente()); // <<< LOG
                    } else {
                        System.err.println("ERROR ClienteDao.obtenerClientePorIdUsuario: mapResultSetToCliente devolvió NULL."); // <<< LOG
                    }
                } else {
                    System.out.println("DEBUG ClienteDao.obtenerClientePorIdUsuario: NO se encontró fila en CLIENTE para ID_USUARIO = " + idUsuario); // <<< LOG
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR SQL al obtener cliente por ID_USUARIO " + idUsuario + ": " + e.getMessage()); // <<< LOG
            UtilidadesExcepciones.mostrarError(e, "Error al obtener cliente", "No se pudo recuperar el cliente asociado al usuario.");
            throw e;
        }
        return cliente;
    }

    /**
     * Obtiene un cliente por su NIF.
     * @param nif El NIF del cliente.
     * @return Cliente o null.
     * @throws SQLException Error de BD.
     */
    public Cliente obtenerClientePorNIF(String nif) throws SQLException {
        String sql = "SELECT * FROM " + TABLA_CLIENTE + " WHERE " + COL_NIF + " = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nif);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al obtener cliente por NIF " + nif + ": " + e.getMessage());
            throw e;
        }
        return null;
    }

    /**
     * Actualiza los datos de un cliente existente en la base de datos.
     * @param cliente El objeto Cliente con los datos actualizados.
     * @return true si la actualización fue exitosa (al menos una fila afectada), false en caso contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public boolean actualizarCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE " + TABLA_CLIENTE + " SET " +
                COL_NIF + " = ?, " + COL_NOMBRE + " = ?, " + COL_APELLIDOS + " = ?, " + COL_FECHA_NACIMIENTO + " = ?, " +
                COL_PROVINCIA + " = ?, " + COL_CIUDAD + " = ?, " + COL_CALLE + " = ?, " + COL_CP + " = ?, " +
                COL_TELEFONO + " = ?, " + COL_EMAIL + " = ?, " + COL_ID_USUARIO_FK + " = ?, " + COL_RUTA_FOTO_PERFIL + " = ? " +
                "WHERE " + COL_ID_CLIENTE + " = ?";
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
                pstmt.setNull(11, Types.NUMERIC); // Asumiendo que ID_USUARIO puede ser null en la BD
            }
            pstmt.setString(12, cliente.getRutaFotoPerfil());
            pstmt.setInt(13, cliente.getIdCliente()); // Para la cláusula WHERE

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al actualizar cliente ID " + cliente.getIdCliente() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene un cliente por su dirección de email.
     * @param email El email del cliente.
     * @return Cliente o null.
     * @throws SQLException Error de BD.
     */
    public Cliente obtenerClientePorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM " + TABLA_CLIENTE + " WHERE " + COL_EMAIL + " = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCliente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al obtener cliente por email " + email + ": " + e.getMessage());
            throw e;
        }
        return null;
    }


    /**
     * Mapea una fila de un ResultSet a un objeto Cliente.
     * Este es un método auxiliar privado, crucial para todos los métodos SELECT.
     *
     * @param rs El ResultSet posicionado en la fila actual que contiene los datos del cliente.
     * @return Un objeto {@link Cliente} poblado con los datos.
     * @throws SQLException Si ocurre un error al acceder a las columnas del ResultSet.
     */
    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt(COL_ID_CLIENTE)); // Importante: obtener el ID_CLIENTE
        cliente.setNif(rs.getString(COL_NIF));
        cliente.setNombre(rs.getString(COL_NOMBRE));
        cliente.setApellidos(rs.getString(COL_APELLIDOS));

        Date fechaNacSQL = rs.getDate(COL_FECHA_NACIMIENTO);
        if (fechaNacSQL != null) {
            cliente.setFechaNacimiento(fechaNacSQL.toLocalDate());
        } else {
            cliente.setFechaNacimiento(null); // Asegurar que sea null si la BD es null
        }

        cliente.setProvincia(rs.getString(COL_PROVINCIA));
        cliente.setCiudad(rs.getString(COL_CIUDAD));
        cliente.setCalle(rs.getString(COL_CALLE));
        // ASUNCIÓN: Tu modelo Cliente tiene setCodigoPostal(String)
        cliente.setCodigoPostal(rs.getString(COL_CP));
        cliente.setTelefono(rs.getString(COL_TELEFONO));
        cliente.setEmail(rs.getString(COL_EMAIL));
        cliente.setIdUsuario(rs.getInt(COL_ID_USUARIO_FK)); // El ID del usuario asociado
        // ASUNCIÓN: Tu modelo Cliente tiene setRutaFotoPerfil(String)
        cliente.setRutaFotoPerfil(rs.getString(COL_RUTA_FOTO_PERFIL));

        return cliente;
    }
}