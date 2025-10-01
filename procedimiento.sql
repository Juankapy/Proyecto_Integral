SELECT ip.notas_especificas, p.descripcion_patologia
FROM identificacion_patologias ip
JOIN patologia p ON p.id_patologia = ip.id_patologia
WHERE p.id_patologia = 10;  


CREATE OR REPLACE PROCEDURE obtenerdescripciones(
    id_patologia_especifica IN NUMBER 
    ) AS
BEGIN
    FOR fila IN (
        SELECT ip.notas_especificas, p.descripcion_patologia
        FROM identificacion_patologias ip
        JOIN patologia p ON p.id_patologia = ip.id_patologia
        WHERE p.id_patologia = id_patologia_especifica
    ) LOOP
        DBMS_OUTPUT.PUT_LINE('Notas: ' || fila.notas_especificas || ' | Descripción: ' || fila.descripcion_patologia);
    END LOOP;
END;
/

