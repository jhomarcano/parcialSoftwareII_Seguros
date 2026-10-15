-- =====================================================================
-- Esquema de la base de datos de seguros (PostgreSQL)
--
-- 1) Crear la base de datos (una sola vez, conectado como postgres):
--        CREATE DATABASE segurosdb;
-- 2) Conectarse a segurosdb y ejecutar el resto de este script.
--
-- Herencia por tablas: "seguro" es la tabla padre y cada subtipo
-- (vida / vehiculo) guarda sus datos propios en una tabla hija que
-- comparte la llave primaria y se borra en cascada.
-- =====================================================================

CREATE TABLE IF NOT EXISTS asegurado (
    identificacion     VARCHAR(20)  PRIMARY KEY,
    nombre             VARCHAR(100) NOT NULL,
    correo_electronico VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS seguro (
    numero           VARCHAR(30)   PRIMARY KEY,
    fecha_expedicion DATE          NOT NULL,
    valor            NUMERIC(14,2) NOT NULL CHECK (valor > 0),
    estado           VARCHAR(15)   NOT NULL
                     CHECK (estado IN ('ACTIVO', 'VENCIDO', 'CANCELADO')),
    tipo             VARCHAR(15)   NOT NULL
                     CHECK (tipo IN ('VIDA', 'VEHICULO')),
    id_asegurado     VARCHAR(20)   NOT NULL
                     REFERENCES asegurado (identificacion)
);

CREATE TABLE IF NOT EXISTS seguro_vida (
    numero_seguro       VARCHAR(30)  PRIMARY KEY
                        REFERENCES seguro (numero) ON DELETE CASCADE,
    nombre_beneficiario VARCHAR(100) NOT NULL,
    parentesco          VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS seguro_vehiculo (
    numero_seguro VARCHAR(30) PRIMARY KEY
                  REFERENCES seguro (numero) ON DELETE CASCADE,
    marca         VARCHAR(50) NOT NULL
);

-- Asegurados predeterminados (el CRUD solo aplica a seguros)
INSERT INTO asegurado (identificacion, nombre, correo_electronico) VALUES
    ('1001', 'Laura Gomez',     'laura.gomez@correo.com'),
    ('1002', 'Carlos Ramirez',  'carlos.ramirez@correo.com'),
    ('1003', 'Maria Fernandez', 'maria.fernandez@correo.com'),
    ('1004', 'Andres Lopez',    'andres.lopez@correo.com'),
    ('1005', 'Sofia Martinez',  'sofia.martinez@correo.com')
ON CONFLICT (identificacion) DO NOTHING;
