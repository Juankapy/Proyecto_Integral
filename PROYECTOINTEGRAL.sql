-- Asegúrate de que la protectora y las razas existan

-- INSERT INTO Raza (ID_Raza, Nombre_Raza) VALUES (1, 'Labrador Retriever');
 INSERT INTO Raza (ID_Raza, Nombre_Raza) VALUES (2, 'Siberian Husky');
 INSERT INTO Raza (ID_Raza, Nombre_Raza) VALUES (3, 'Golden Retriever');

-- Perro 1
INSERT INTO Perros (ID_Perro, Nombre, Sexo, FechaNacimiento, Adoptado, Foto, ID_Protectora, ID_Raza)
VALUES (101, 'Buddy', 'Macho', TO_DATE('2022-03-15', 'YYYY-MM-DD'), 'N', '/assets/Imagenes/perros/buddy_labrador.jpg', 1, 1);

-- Perro 2
INSERT INTO Perros (ID_Perro, Nombre, Sexo, FechaNacimiento, Adoptado, Foto, ID_Protectora, ID_Raza)
VALUES (102, 'Kira', 'Hembra', TO_DATE('2021-11-01', 'YYYY-MM-DD'), 'N', '/assets/Imagenes/perros/kira_husky.png', 1, 2);

-- Perro 3
INSERT INTO Perros (ID_Perro, Nombre, Sexo, FechaNacimiento, Adoptado, Foto, ID_Protectora, ID_Raza)
VALUES (103, 'Goldie', 'Macho', TO_DATE('2023-01-20', 'YYYY-MM-DD'), 'N', '/assets/Imagenes/perros/goldie_golden.jpg', 1, 3);

COMMIT;

DESC Perros;
ALTER TABLE Perros MODIFY (Foto VARCHAR2(255));
ALTER TABLE Perros ADD (Foto_Ruta VARCHAR2(255));
-- Paso 3: Eliminar la columna BLOB original
ALTER TABLE Perros DROP COLUMN Foto;

-- Paso 4: Renombrar la nueva columna a 'Foto'
ALTER TABLE Perros RENAME COLUMN Foto_Ruta TO Foto;

-- Asumir que ya tienes insertados:
-- Protectora con ID_Protectora = 1 (ej. 'Amigos Peludos')
-- Raza con ID_Raza = 1 (ej. 'Labrador Retriever')
-- Patologia con ID_Patologia = 1 (ej. 'Alergia al Pollo')
-- Perro con ID_Perro = 101 (Buddy, el Labrador)

-- (1) Inserta una protectora si no existe una con ID=1
-- Comprueba primero si existe: SELECT * FROM Protectora WHERE ID_Protectora = 1;
-- Si no existe:
INSERT INTO Protectora (ID_Protectora, Nombre, Telefono, Email, Provincia, Ciudad, Calle, CP, ID_Usuario)
VALUES (1, 'Amigos Peludos', '911223344', 'info@amigospeludos.org', 'Madrid', 'Madrid', 'Calle de los Animales 10', '28010', NULL); -- Asume que no está ligada a un usuario específico de login

-- (2) Inserta una raza si no existe una con ID=1
-- Comprueba primero: SELECT * FROM Raza WHERE ID_Raza = 1;
-- Si no existe:
INSERT INTO Raza (ID_Raza, Nombre_Raza) VALUES (1, 'Labrador Retriever');

-- (3) Inserta una patología si no existe una con ID=1
-- Comprueba primero: SELECT * FROM Patologia WHERE ID_Patologia = 1;
-- Si no existe:
INSERT INTO Patologia (ID_Patologia, Nombre) VALUES (1, 'Alergia al Pollo');

-- (4) Inserta el perro Buddy (ID=101) si no lo has hecho con la Foto correcta
-- Si ya lo insertaste con la ruta, está bien. Si no:
DELETE FROM Perros WHERE ID_Perro = 101; -- Borrar si ya existe para reinsertar
INSERT INTO Perros (ID_Perro, Nombre, Sexo, FechaNacimiento, Adoptado, Foto, ID_Protectora, ID_Raza)
VALUES (101, 'Buddy', 'Macho', TO_DATE('2022-03-15', 'YYYY-MM-DD'), 'N', '/assets/Imagenes/perros/buddy_labrador.jpg', 1, 1);

-- (5) Asocia la patología 'Alergia al Pollo' (ID 1) al perro 'Buddy' (ID 101)
INSERT INTO Identificacion_Patologias (ID_Perro, ID_Patologia)
VALUES (101, 1);

COMMIT;


