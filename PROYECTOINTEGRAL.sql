-- Asegúrate de que la protectora y las razas existan

-- INSERT INTO Raza (ID_Raza, Nombre_Raza) VALUES (1, 'Labrador Retriever');
 INSERT INTO Raza (ID_Raza, Nombre_Raza) VALUES (2, 'Siberian Husky');
 INSERT INTO Raza (ID_Raza, Nombre_Raza) VALUES (3, 'Golden Retriever');

-- Perro 1
INSERT INTO Perros (ID_Perro, Nombre, Sexo, FechaNacimiento, Adoptado, Foto, ID_Protectora, ID_Raza)
VALUES (101, 'Buddy', 'Macho', TO_DATE('2022-03-15', 'YYYY-MM-DD'), 'N', '/assets/Imagenes/perros/buddy_labrador.jpg', 1, 1);

-- Perro 2
INSERT INTO Perros (ID_Perro, Nombre, Sexo, FechaNacimiento, Adoptado, Foto, ID_Protectora, ID_Raza)
VALUES (102, 'Kira', 'Hembra', TO_DATE('2021-11-01', 'YYYY-MM-DD'), 'N', '/assets/Imagenes/perros/kira_husky.jpg', 1, 2);

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