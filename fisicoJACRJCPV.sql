/*-- Tablas dependientes (relaciones)
DROP TABLE Notificaciones_Recibidas;
DROP TABLE Identificacion_Patologias;
DROP TABLE Peticiones_Adopcion;
DROP TABLE Reservas_Citas;
DROP TABLE Redes_Sociales;

-- Tablas principales
DROP TABLE Notificacion;
DROP TABLE Patologia;
DROP TABLE Perros;
DROP TABLE Raza;
DROP TABLE Protectora;
DROP TABLE Cliente;
DROP TABLE Usuario;
*/
-- Tabla Usuario
CREATE TABLE Usuario (
    ID_Usuario NUMBER PRIMARY KEY,
    Nombre_Usu VARCHAR2(25) NOT NULL,
    Contrasena VARCHAR2(20) NOT NULL
);

-- Tabla Cliente
CREATE TABLE Cliente (
    ID_Cliente NUMBER PRIMARY KEY,
    NIF VARCHAR2(9) UNIQUE,
    Nombre VARCHAR2(50) NOT NULL,
    Apellido1 VARCHAR2(50)NOT NULL,
    Apellido2 VARCHAR2(50)NOT NULL,
    Provincia VARCHAR2(20)NOT NULL,
    Ciudad VARCHAR2(100)NOT NULL,
    Calle VARCHAR2(100)NOT NULL,
    CP VARCHAR2(5)NOT NULL,
    Telefono VARCHAR2(9),
    Email VARCHAR2(100)NOT NULL,
    ID_Usuario NUMBER UNIQUE,
    FOREIGN KEY (ID_Usuario) REFERENCES Usuario(ID_Usuario)
);

-- Tabla Protectora
CREATE TABLE Protectora (
    ID_Protectora NUMBER PRIMARY KEY,
    Nombre VARCHAR2(100) NOT NULL,
    Telefono VARCHAR2(9)NOT NULL,
    Email VARCHAR2(100)NOT NULL,
    Provincia VARCHAR2(20)NOT NULL,
    Ciudad VARCHAR2(150)NOT NULL,
    Calle VARCHAR2(150)NOT NULL,
    CP VARCHAR2(5)NOT NULL,
    ID_Usuario NUMBER UNIQUE,
    FOREIGN KEY (ID_Usuario) REFERENCES Usuario(ID_Usuario)
);

-- Tabla Raza
CREATE TABLE Raza (
    ID_Raza NUMBER PRIMARY KEY,
    Nombre_Raza VARCHAR2(50) NOT NULL
);

-- Tabla Perros
CREATE TABLE Perros (
    ID_Perro NUMBER PRIMARY KEY,
    Nombre VARCHAR2(50) NOT NULL,
    Sexo VARCHAR2(6) NOT NULL,
    FechaNacimiento DATE,
    Adoptado CHAR(1) DEFAULT 'N' CHECK (Adoptado IN ('S','N')),
    Foto BLOB,
    ID_Protectora NUMBER,
    ID_Raza NUMBER,
    FOREIGN KEY (ID_Protectora) REFERENCES Protectora(ID_Protectora),
    FOREIGN KEY (ID_Raza) REFERENCES Raza(ID_Raza),
    CHECK (Sexo IN ('Macho', 'Hembra'))
);


-- Tabla Patología
CREATE TABLE Patologia (
    ID_Patologia NUMBER PRIMARY KEY,
    Nombre VARCHAR2(100)
);

-- Relación Perros y Patologías
CREATE TABLE Identificacion_Patologias (
    ID_Perro NUMBER,
    ID_Patologia NUMBER,
    PRIMARY KEY (ID_Perro, ID_Patologia),
    FOREIGN KEY (ID_Perro) REFERENCES Perros(ID_Perro),
    FOREIGN KEY (ID_Patologia) REFERENCES Patologia(ID_Patologia)
);

-- Tabla Reservas de Citas
CREATE TABLE Reservas_Citas (
    ID_Cita NUMBER PRIMARY KEY,
    Fecha DATE NOT NULL,
    Hora VARCHAR2(5) NOT NULL,
    Pago NUMBER CHECK (Pago >= 3),
    ID_Cliente NUMBER,
    ID_Perro NUMBER,
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID_Cliente),
    FOREIGN KEY (ID_Perro) REFERENCES Perros(ID_Perro)
);

-- Tabla Peticiones de Adopción
CREATE TABLE Peticiones_Adopcion (
    ID_Peticion NUMBER PRIMARY KEY,
    Fecha DATE NOT NULL,
    Estado VARCHAR2(9) CHECK (Estado IN ('Pendiente', 'Aceptada', 'Rechazada')),
    ID_Cliente NUMBER,
    ID_Perro NUMBER,
    FOREIGN KEY (ID_Cliente) REFERENCES Cliente(ID_Cliente),
    FOREIGN KEY (ID_Perro) REFERENCES Perros(ID_Perro)
);

-- Tabla Notificaciones
CREATE TABLE Notificacion (
    ID_Notificacion NUMBER PRIMARY KEY,
    Fecha DATE,
    Descripcion VARCHAR2(255) NOT NULL,
    Estado VARCHAR2(10) DEFAULT 'No Leída' CHECK (Estado IN ('Leída', 'No Leída'))
);

-- Relación  Notificaciones y Usuarios
CREATE TABLE Notificaciones_Recibidas (
    ID_Usuario NUMBER,
    ID_Notificacion NUMBER,
    PRIMARY KEY (ID_Usuario, ID_Notificacion),
    FOREIGN KEY (ID_Usuario) REFERENCES Usuario(ID_Usuario),
    FOREIGN KEY (ID_Notificacion) REFERENCES Notificacion(ID_Notificacion)
);

-- Tabla Redes Sociales de Protectoras
CREATE TABLE Redes_Sociales (
    Plataforma VARCHAR2(30) NOT NULL,
    URL VARCHAR2(255) NOT NULL,
    ID_Protectora NUMBER NOT NULL,
    CONSTRAINT pk_red_social PRIMARY KEY (ID_Protectora, Plataforma),
    FOREIGN KEY (ID_Protectora) REFERENCES Protectora(ID_Protectora)
);

-- 1. Usuario
INSERT INTO Usuario (ID_Usuario, Nombre_Usu, Contrasena)
VALUES (2, '1', '1');

-- 2. Cliente
INSERT INTO Cliente (
    ID_Cliente, NIF, Nombre, Apellido1, Apellido2,
    Provincia, Ciudad, Calle, CP, Telefono, Email, ID_Usuario
)
VALUES (
    1, '12345678A', 'Juan', 'Pérez', 'Gómez',
    'Madrid', 'Madrid', 'Calle Falsa 123', '28080', '600123456', 'juan@example.com', 1
);

-- 3. Protectora
INSERT INTO Protectora (
    ID_Protectora, Nombre, Telefono, Email,
    Provincia, Ciudad, Calle, CP, ID_Usuario
)
VALUES (
    1, 'Protectora Amigos', '910123456', 'contacto@protectora.org',
    'Madrid', 'Madrid', 'Av. Libertad 45', '28080', NULL
);

-- 4. Raza
INSERT INTO Raza (ID_Raza, Nombre_Raza)
VALUES (1, 'Labrador');

-- 5. Perros
INSERT INTO Perros (
    ID_Perro, Nombre, Sexo, FechaNacimiento, Adoptado, Foto,
    ID_Protectora, ID_Raza
)
VALUES (
    1, 'Rocky', 'Macho', TO_DATE('2020-05-10', 'YYYY-MM-DD'), 'N', NULL,
    1, 1
);

-- 6. Patologia
INSERT INTO Patologia (ID_Patologia, Nombre)
VALUES (1, 'Displasia de cadera');

-- 7. Identificacion_Patologias
INSERT INTO Identificacion_Patologias (ID_Perro, ID_Patologia)
VALUES (1, 1);

-- 8. Reservas_Citas
INSERT INTO Reservas_Citas (
    ID_Cita, Fecha, Hora, Pago, ID_Cliente, ID_Perro
)
VALUES (
    1, TO_DATE('2025-06-15', 'YYYY-MM-DD'), '10:30', 10, 1, 1
);

-- 9. Peticiones_Adopcion
INSERT INTO Peticiones_Adopcion (
    ID_Peticion, Fecha, Estado, ID_Cliente, ID_Perro
)
VALUES (
    1, TO_DATE('2025-06-01', 'YYYY-MM-DD'), 'Pendiente', 1, 1
);

-- 10. Notificacion
INSERT INTO Notificacion (
    ID_Notificacion, Fecha, Descripcion, Estado
)
VALUES (
    1, TO_DATE('2025-05-10', 'YYYY-MM-DD'), 'Tiene una nueva cita programada.', 'No Leída'
);

-- 11. Notificaciones_Recibidas
INSERT INTO Notificaciones_Recibidas (ID_Usuario, ID_Notificacion)
VALUES (1, 1);

-- 12. Redes_Sociales
INSERT INTO Redes_Sociales (
    Plataforma, URL, ID_Protectora
)
VALUES (
    'Instagram', 'https://instagram.com/protectoraamigos', 1
);
