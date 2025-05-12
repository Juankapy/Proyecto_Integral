-- Tablas dependientes (relaciones)
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