-- Esquema inicial para el contenedor MySQL. Docker crea la base indicada por
-- MYSQL_DATABASE antes de ejecutar este archivo.

CREATE TABLE grupos (
    id      INT PRIMARY KEY,
    nombre  VARCHAR(50) NOT NULL
);

CREATE TABLE rubros (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    grupo_id  INT NOT NULL,
    tipo      INT NOT NULL,
    codigo    VARCHAR(2) NOT NULL,
    nombre    VARCHAR(100) NOT NULL,
    FOREIGN KEY (grupo_id) REFERENCES grupos(id),
    UNIQUE KEY uq_rubro_codigo (codigo)
);

CREATE TABLE cuentas (
    codigo         VARCHAR(12) PRIMARY KEY,
    grupo_id       INT NOT NULL,
    tipo           INT NOT NULL,
    rubro_id       INT NOT NULL,
    numero_cuenta  INT NOT NULL,
    nombre         VARCHAR(40) NOT NULL,
    saldo          DECIMAL(11,2) NOT NULL DEFAULT 0,
    tipo_saldo     CHAR(1) NOT NULL,
    FOREIGN KEY (grupo_id) REFERENCES grupos(id),
    FOREIGN KEY (rubro_id) REFERENCES rubros(id)
);

CREATE TABLE asientos (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    numero    INT NOT NULL,
    fecha     DATE NOT NULL,
    concepto  VARCHAR(200) NOT NULL,
    UNIQUE KEY uq_asiento_numero (numero)
);

CREATE TABLE asiento_detalle (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    asiento_id     INT NOT NULL,
    cuenta_codigo  VARCHAR(12) NOT NULL,
    debe           DECIMAL(11,2) NOT NULL DEFAULT 0,
    haber          DECIMAL(11,2) NOT NULL DEFAULT 0,
    orden          INT NOT NULL,
    FOREIGN KEY (asiento_id) REFERENCES asientos(id) ON DELETE CASCADE,
    FOREIGN KEY (cuenta_codigo) REFERENCES cuentas(codigo)
);

INSERT INTO grupos (id, nombre) VALUES
    (1, 'Activo'), (2, 'Pasivo'), (3, 'Patrimonio Neto'),
    (4, 'Ingresos'), (5, 'Egresos');

INSERT INTO rubros (grupo_id, tipo, codigo, nombre) VALUES
    (1, 1, '01', 'Caja y Bancos'),
    (1, 1, '02', 'Créditos por Ventas'),
    (1, 1, '03', 'Bienes de Cambio'),
    (1, 2, '04', 'Bienes de Uso'),
    (2, 1, '05', 'Deudas Comerciales'),
    (2, 2, '06', 'Deudas a Largo Plazo'),
    (3, 0, '07', 'Patrimonio Neto'),
    (3, 0, '08', 'Resultados Acumulados'),
    (4, 0, '09', 'Ventas'),
    (5, 0, '10', 'Gastos');
