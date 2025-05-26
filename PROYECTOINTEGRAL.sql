SET SERVEROUTPUT ON;
SET VERIFY OFF; -- Para evitar que pregunte por variables de sustitución si se usa &

DECLARE
    -- Variables para guardar IDs generados y usarlos en pruebas posteriores
    id_usuario_prueba           USUARIO.ID_USUARIO%TYPE;
    id_cliente_prueba           CLIENTE.ID_CLIENTE%TYPE;
    id_protectora_prueba        PROTECTORA.ID_PROTECTORA%TYPE;
    id_raza_prueba              RAZA.ID_RAZA%TYPE;
    id_perro_prueba_1           PERROS.ID_PERRO%TYPE;
    id_perro_prueba_2           PERROS.ID_PERRO%TYPE;
    id_patologia_prueba         PATOLOGIA.ID_PATOLOGIA%TYPE;
    id_reserva_prueba           RESERVAS_CITAS.ID_RESERVA_CITA%TYPE;
    id_peticion_prueba          PETICIONES_ADOPCION.ID_PETICION%TYPE;
    id_notificacion_prueba      NOTIFICACION.ID_NOTIFICACION%TYPE;
    id_red_social_prueba        REDES_SOCIALES.ID_RED_SOCIAL%TYPE;

    -- Variables para verificar
    fecha_creacion_check        DATE;
    fecha_modificacion_check    DATE;
    conteo                      NUMBER;
    texto_notificacion          NOTIFICACION.MENSAJE%TYPE;
    id_usuario_funcion          USUARIO.ID_USUARIO%TYPE;

BEGIN
    DBMS_OUTPUT.PUT_LINE('========== INICIO DE PRUEBAS DEL SCRIPT DE BD ==========');

    -- Limpieza inicial de datos de prueba si existieran (opcional, para ejecuciones repetidas)
    -- DELETE FROM NOTIFICACIONES_RECIBIDAS WHERE ID_USUARIO >= 100;
    -- DELETE FROM NOTIFICACION WHERE ID_NOTIFICACION >= 100;
    -- DELETE FROM PETICIONES_ADOPCION WHERE ID_CLIENTE >= 100;
    -- DELETE FROM RESERVAS_CITAS WHERE ID_CLIENTE >= 100;
    -- DELETE FROM IDENTIFICACION_PATOLOGIAS WHERE ID_PERRO >= 100;
    -- DELETE FROM PERROS WHERE ID_RAZA >= 100;
    -- DELETE FROM RAZA WHERE ID_RAZA >= 100;
    -- DELETE FROM PATOLOGIA WHERE ID_PATOLOGIA >= 100;
    -- DELETE FROM REDES_SOCIALES WHERE ID_PROTECTORA >= 100;
    -- DELETE FROM CLIENTE WHERE ID_USUARIO >= 100;
    -- DELETE FROM PROTECTORA WHERE ID_USUARIO >= 100;
    -- DELETE FROM USUARIO WHERE ID_USUARIO >= 100;
    -- COMMIT;

    -- ========= PRUEBA 1: Inserción en USUARIO y Secuencia =========
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Prueba 1: Inserción USUARIO ---');
    INSERT INTO USUARIO (ID_USUARIO, NOMBRE_USU, CONTRASENA, ROL)
    VALUES (100, 'usuario_test', 'testpass', 'CLIENTE') RETURNING ID_USUARIO INTO id_usuario_prueba;
    DBMS_OUTPUT.PUT_LINE('Usuario de prueba insertado con ID: ' || id_usuario_prueba);
    SELECT COUNT(*) INTO conteo FROM USUARIO WHERE ID_USUARIO = id_usuario_prueba;
    IF conteo = 1 THEN
        DBMS_OUTPUT.PUT_LINE('Prueba 1.1 (Inserción Usuario): OK');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Prueba 1.1 (Inserción Usuario): FALLO');
    END IF;

    -- ========= PRUEBA 2: Inserción en CLIENTE y Triggers de Fecha =========
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Prueba 2: Inserción CLIENTE y Triggers Fecha ---');
    INSERT INTO CLIENTE (ID_CLIENTE, NIF, NOMBRE, APELLIDOS, PROVINCIA, CIUDAD, CALLE, CP, EMAIL, ID_USUARIO)
    VALUES (100, '99999999Z', 'Cliente', 'DePrueba', 'TestProv', 'TestCity', 'Calle Falsa 123', '00000', 'test@cliente.com', id_usuario_prueba)
    RETURNING ID_CLIENTE, FECHA_CREACION, FECHA_MODIFICACION INTO id_cliente_prueba, fecha_creacion_check, fecha_modificacion_check;
    DBMS_OUTPUT.PUT_LINE('Cliente de prueba insertado con ID: ' || id_cliente_prueba);

    IF fecha_creacion_check IS NOT NULL AND fecha_modificacion_check IS NOT NULL AND fecha_creacion_check = fecha_modificacion_check THEN
        DBMS_OUTPUT.PUT_LINE('Prueba 2.1 (Trigger Creación Cliente): OK');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Prueba 2.1 (Trigger Creación Cliente): FALLO - Fechas: ' || fecha_creacion_check || ', ' || fecha_modificacion_check);
    END IF;

    UPDATE CLIENTE SET TELEFONO = '123456789' WHERE ID_CLIENTE = id_cliente_prueba RETURNING FECHA_MODIFICACION INTO fecha_modificacion_check;
    IF fecha_modificacion_check > fecha_creacion_check THEN
        DBMS_OUTPUT.PUT_LINE('Prueba 2.2 (Trigger Actualización Cliente): OK');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Prueba 2.2 (Trigger Actualización Cliente): FALLO');
    END IF;

    -- ========= PRUEBA 3: Inserción en PROTECTORA y Triggers de Fecha =========
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Prueba 3: Inserción PROTECTORA y Triggers Fecha ---');
    INSERT INTO USUARIO (ID_USUARIO, NOMBRE_USU, CONTRASENA, ROL)
    VALUES (101, 'protectora_test_user', 'prot_test_pass', 'PROTECTORA') RETURNING ID_USUARIO INTO id_usuario_prueba;

    INSERT INTO PROTECTORA (ID_PROTECTORA, CIF, NOMBRE, TELEFONO, EMAIL, PROVINCIA, CIUDAD, CALLE, CP, ID_USUARIO)
    VALUES (100, 'X99999990', 'Protectora Test', '900800700', 'info@prottest.com', 'ProvTest', 'CiuTest', 'Av Test 99', '11111', id_usuario_prueba)
    RETURNING ID_PROTECTORA, FECHA_CREACION, FECHA_MODIFICACION INTO id_protectora_prueba, fecha_creacion_check, fecha_modificacion_check;
    DBMS_OUTPUT.PUT_LINE('Protectora de prueba insertada con ID: ' || id_protectora_prueba);

    IF fecha_creacion_check IS NOT NULL AND fecha_modificacion_check IS NOT NULL AND fecha_creacion_check = fecha_modificacion_check THEN
        DBMS_OUTPUT.PUT_LINE('Prueba 3.1 (Trigger Creación Protectora): OK');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Prueba 3.1 (Trigger Creación Protectora): FALLO');
    END IF;
    
    UPDATE PROTECTORA SET EMAIL = 'contact@prottest.com' WHERE ID_PROTECTORA = id_protectora_prueba RETURNING FECHA_MODIFICACION INTO fecha_modificacion_check;
    IF fecha_modificacion_check > fecha_creacion_check THEN
        DBMS_OUTPUT.PUT_LINE('Prueba 3.2 (Trigger Actualización Protectora): OK');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Prueba 3.2 (Trigger Actualización Protectora): FALLO');
    END IF;

    -- ========= PRUEBA 4: Inserción RAZA, PERRO =========
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Prueba 4: Inserción RAZA y PERRO ---');
    INSERT INTO RAZA (ID_RAZA, NOMBRE_RAZA) VALUES (100, 'Raza de Prueba') RETURNING ID_RAZA INTO id_raza_prueba;
    INSERT INTO PERROS (ID_PERRO, NOMBRE, SEXO, FECHA_NACIMIENTO, ADOPTADO, ID_PROTECTORA, ID_RAZA)
    VALUES (100, 'PerroTest1', 'Macho', SYSDATE-365, 'N', id_protectora_prueba, id_raza_prueba) RETURNING ID_PERRO INTO id_perro_prueba_1;
    INSERT INTO PERROS (ID_PERRO, NOMBRE, SEXO, FECHA_NACIMIENTO, ADOPTADO, ID_PROTECTORA, ID_RAZA)
    VALUES (101, 'PerroTest2', 'Hembra', SYSDATE-730, 'N', id_protectora_prueba, id_raza_prueba) RETURNING ID_PERRO INTO id_perro_prueba_2;
    DBMS_OUTPUT.PUT_LINE('Raza de prueba ID: ' || id_raza_prueba || ', Perros de prueba IDs: ' || id_perro_prueba_1 || ', ' || id_perro_prueba_2);
    SELECT COUNT(*) INTO conteo FROM PERROS WHERE ID_PROTECTORA = id_protectora_prueba;
    IF conteo = 2 THEN
        DBMS_OUTPUT.PUT_LINE('Prueba 4.1 (Inserción Perros): OK');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Prueba 4.1 (Inserción Perros): FALLO');
    END IF;

    -- ========= PRUEBA 5: Función obtener_id_usuario_de_protectora_por_perro =========
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Prueba 5: Función obtener_id_usuario_de_protectora_por_perro ---');
    id_usuario_funcion := obtener_id_usuario_de_protectora_por_perro(id_perro_especifico => id_perro_prueba_1);
    IF id_usuario_funcion = id_usuario_prueba THEN -- id_usuario_prueba es el de la protectora_test_user (101)
        DBMS_OUTPUT.PUT_LINE('Prueba 5.1 (Función OK): OK - Usuario esperado: ' || id_usuario_prueba || ', Obtenido: ' || id_usuario_funcion);
    ELSE
        DBMS_OUTPUT.PUT_LINE('Prueba 5.1 (Función OK): FALLO - Usuario esperado: ' || id_usuario_prueba || ', Obtenido: ' || id_usuario_funcion);
    END IF;

    BEGIN
        id_usuario_funcion := obtener_id_usuario_de_protectora_por_perro(id_perro_especifico => 9999); -- Perro inexistente
        IF id_usuario_funcion IS NULL THEN
            DBMS_OUTPUT.PUT_LINE('Prueba 5.2 (Función Perro Inexistente): OK - Devuelve NULL como esperado.');
        ELSE
            DBMS_OUTPUT.PUT_LINE('Prueba 5.2 (Función Perro Inexistente): FALLO - No devolvió NULL.');
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
             DBMS_OUTPUT.PUT_LINE('Prueba 5.2 (Función Perro Inexistente): FALLO con excepción inesperada ' || SQLERRM);
    END;


    -- ========= PRUEBA 6: Procedimiento registrar_y_enviar_notificacion_a_usuario =========
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Prueba 6: Procedimiento registrar_y_enviar_notificacion_a_usuario ---');
    registrar_y_enviar_notificacion_a_usuario(
        texto_del_mensaje       => 'Notificación de prueba para usuario.',
        categoria_notificacion  => 'PRUEBA_PROCEDIMIENTO',
        id_elemento_relacionado => id_cliente_prueba,
        tabla_elemento_relacionado => 'CLIENTE',
        id_del_usuario_destino  => id_usuario_prueba -- Usuario 'protectora_test_user'
    );
    SELECT COUNT(*) INTO conteo FROM NOTIFICACIONES_RECIBIDAS nr JOIN NOTIFICACION n ON nr.id_notificacion = n.id_notificacion
    WHERE nr.ID_USUARIO = id_usuario_prueba AND n.TIPO_NOTIFICACION = 'PRUEBA_PROCEDIMIENTO';
    IF conteo = 1 THEN
        DBMS_OUTPUT.PUT_LINE('Prueba 6.1 (Procedimiento Notificación OK): OK');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Prueba 6.1 (Procedimiento Notificación OK): FALLO, conteo: ' || conteo);
    END IF;

    BEGIN
        registrar_y_enviar_notificacion_a_usuario('Test Fallo', 'FALLO_USER', 1, 'TABLA', 9999); -- Usuario inexistente
        DBMS_OUTPUT.PUT_LINE('Prueba 6.2 (Procedimiento Usuario Inexistente): FALLO - No lanzó excepción.');
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20003 THEN
                 DBMS_OUTPUT.PUT_LINE('Prueba 6.2 (Procedimiento Usuario Inexistente): OK - Excepción esperada ' || SQLERRM);
            ELSE
                 DBMS_OUTPUT.PUT_LINE('Prueba 6.2 (Procedimiento Usuario Inexistente): FALLO - Excepción inesperada ' || SQLERRM);
            END IF;
    END;

    -- ========= PRUEBA 7: Petición de Adopción y Trigger estado_peticion_adopcion_notif =========
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Prueba 7: Petición Adopción y Trigger Notificación ---');
    -- Cliente con ID_CLIENTE=100 (usuario_test, ID_USUARIO=100)
    -- Perro con ID_PERRO=101 (PerroTest2)
    INSERT INTO PETICIONES_ADOPCION (ID_PETICION, ESTADO, ID_CLIENTE, ID_PERRO, MENSAJE_PETICION)
    VALUES (100, 'Pendiente', id_cliente_prueba, id_perro_prueba_2, 'Quiero adoptar a PerroTest2')
    RETURNING ID_PETICION INTO id_peticion_prueba;
    DBMS_OUTPUT.PUT_LINE('Petición de prueba insertada con ID: ' || id_peticion_prueba);

    UPDATE PETICIONES_ADOPCION SET ESTADO = 'Aceptada', NOTAS_PROTECTORA = 'Parece un buen hogar.'
    WHERE ID_PETICION = id_peticion_prueba;

    SELECT COUNT(*) INTO conteo FROM NOTIFICACIONES_RECIBIDAS nr JOIN NOTIFICACION n ON nr.id_notificacion = n.id_notificacion
    WHERE nr.ID_USUARIO = (SELECT ID_USUARIO FROM CLIENTE WHERE ID_CLIENTE = id_cliente_prueba) -- Usuario 'usuario_test' ID 100
      AND n.TIPO_NOTIFICACION = 'CAMBIO_ESTADO_PETICION'
      AND n.ID_ENTIDAD_RELACIONADA = id_peticion_prueba;

    IF conteo = 1 THEN
        DBMS_OUTPUT.PUT_LINE('Prueba 7.1 (Trigger Petición Aceptada): OK - Notificación generada para cliente.');
        SELECT n.mensaje INTO texto_notificacion FROM NOTIFICACIONES_RECIBIDAS nr JOIN NOTIFICACION n ON nr.id_notificacion = n.id_notificacion
        WHERE nr.ID_USUARIO = (SELECT ID_USUARIO FROM CLIENTE WHERE ID_CLIENTE = id_cliente_prueba)
          AND n.TIPO_NOTIFICACION = 'CAMBIO_ESTADO_PETICION'
          AND n.ID_ENTIDAD_RELACIONADA = id_peticion_prueba;
        DBMS_OUTPUT.PUT_LINE('    Mensaje: ' || texto_notificacion);
    ELSE
        DBMS_OUTPUT.PUT_LINE('Prueba 7.1 (Trigger Petición Aceptada): FALLO - No se generó notificación. Conteo: ' || conteo);
    END IF;

    -- ========= PRUEBA 8: ON DELETE CASCADE (Usuario -> Cliente) =========
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Prueba 8: ON DELETE CASCADE (Usuario -> Cliente) ---');
    DELETE FROM USUARIO WHERE ID_USUARIO = (SELECT ID_USUARIO FROM CLIENTE WHERE ID_CLIENTE = id_cliente_prueba); -- Borra 'usuario_test' (ID 100)
    
    SELECT COUNT(*) INTO conteo FROM CLIENTE WHERE ID_CLIENTE = id_cliente_prueba;
    IF conteo = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Prueba 8.1 (DELETE CASCADE Usuario->Cliente): OK - Cliente borrado en cascada.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Prueba 8.1 (DELETE CASCADE Usuario->Cliente): FALLO - Cliente no borrado.');
    END IF;
    
    SELECT COUNT(*) INTO conteo FROM NOTIFICACIONES_RECIBIDAS WHERE ID_USUARIO = (SELECT ID_USUARIO FROM CLIENTE WHERE ID_CLIENTE = id_cliente_prueba);
     IF conteo = 0 THEN
        DBMS_OUTPUT.PUT_LINE('Prueba 8.2 (DELETE CASCADE Usuario->NotificacionesRecibidas): OK - Notificaciones del cliente borradas en cascada.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Prueba 8.2 (DELETE CASCADE Usuario->NotificacionesRecibidas): FALLO - Notificaciones del cliente no borradas.');
    END IF;

    -- ========= PRUEBA 9: Restricciones UNIQUE =========
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Prueba 9: Restricciones UNIQUE ---');
    BEGIN
        INSERT INTO USUARIO (ID_USUARIO, NOMBRE_USU, CONTRASENA, ROL) VALUES (102, 'protectora_happy', 'otra_pass', 'PROTECTORA'); -- Nombre_usu duplicado
        DBMS_OUTPUT.PUT_LINE('Prueba 9.1 (UNIQUE NOMBRE_USU): FALLO - Permitió duplicado.');
    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE('Prueba 9.1 (UNIQUE NOMBRE_USU): OK - Impidió duplicado.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Prueba 9.1 (UNIQUE NOMBRE_USU): FALLO - Error inesperado: ' || SQLERRM);
    END;

    BEGIN
        INSERT INTO CLIENTE (ID_CLIENTE, NIF, NOMBRE, APELLIDOS, PROVINCIA, CIUDAD, CALLE, CP, EMAIL, ID_USUARIO)
        VALUES (102, '12345678A', 'Otro', 'Cliente', 'Madrid', 'Madrid', 'Otra Calle', '28001', 'otro@email.com', 4); -- NIF duplicado de Ana
        DBMS_OUTPUT.PUT_LINE('Prueba 9.2 (UNIQUE NIF Cliente): FALLO - Permitió NIF duplicado.');
    EXCEPTION
        WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE('Prueba 9.2 (UNIQUE NIF Cliente): OK - Impidió NIF duplicado.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Prueba 9.2 (UNIQUE NIF Cliente): FALLO - Error inesperado: ' || SQLERRM);
    END;
    
    -- ========= PRUEBA 10: Restricciones CHECK =========
    DBMS_OUTPUT.PUT_LINE(CHR(10) || '--- Prueba 10: Restricciones CHECK ---');
    BEGIN
        INSERT INTO USUARIO (ID_USUARIO, NOMBRE_USU, CONTRASENA, ROL) VALUES (103, 'usuario_rol_invalido', 'pass', 'ADMIN');
        DBMS_OUTPUT.PUT_LINE('Prueba 10.1 (CHECK Rol Usuario): FALLO - Permitió rol inválido.');
    EXCEPTION
        WHEN OTHERS THEN -- ORA-02290: check constraint violated
            IF SQLCODE = -2290 THEN
                DBMS_OUTPUT.PUT_LINE('Prueba 10.1 (CHECK Rol Usuario): OK - Impidió rol inválido.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('Prueba 10.1 (CHECK Rol Usuario): FALLO - Error inesperado: ' || SQLERRM);
            END IF;
    END;

    BEGIN
        INSERT INTO PERROS (ID_PERRO, NOMBRE, SEXO, FECHA_NACIMIENTO, ADOPTADO, ID_PROTECTORA, ID_RAZA)
        VALUES (103, 'PerroSexoInvalido', 'Neutro', SYSDATE-100, 'N', 1, 1); -- id_protectora_prueba, id_raza_prueba ya no existen si se borró la protectora
        DBMS_OUTPUT.PUT_LINE('Prueba 10.2 (CHECK Sexo Perro): FALLO - Permitió sexo inválido.');
    EXCEPTION
        WHEN OTHERS THEN
             IF SQLCODE = -2290 THEN
                DBMS_OUTPUT.PUT_LINE('Prueba 10.2 (CHECK Sexo Perro): OK - Impidió sexo inválido.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('Prueba 10.2 (CHECK Sexo Perro): FALLO - Error inesperado: ' || SQLERRM);
            END IF;
    END;


    DBMS_OUTPUT.PUT_LINE(CHR(10) || '========== FIN DE PRUEBAS DEL SCRIPT DE BD ==========');
    ROLLBACK; -- Deshacer todos los cambios realizados durante las pruebas
    -- COMMIT; -- Si quieres guardar los datos de prueba (no recomendado para un script de testeo repetible)

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE(CHR(10) || '!!!!!!!!!! ERROR GENERAL DURANTE LAS PRUEBAS !!!!!!!!!!');
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLCODE || ' - ' || SQLERRM);
        DBMS_OUTPUT.PUT_LINE('Stack:');
        DBMS_OUTPUT.PUT_LINE(DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
        ROLLBACK;
END;
/