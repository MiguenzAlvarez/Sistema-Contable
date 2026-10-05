USE economia_db;

-- ============================================================
-- ASIENTOS Y ASIENTO_DETALLE
-- Nombres y columnas alineados EXACTAMENTE con AsientoDAO.java
-- (numero, fecha, concepto / asiento_id, cuenta_codigo, debe,
-- haber, orden). No cambies estos nombres sin avisar al equipo.
-- ============================================================

CREATE TABLE IF NOT EXISTS asientos (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    numero    INT NOT NULL,
    fecha     DATE NOT NULL,
    concepto  VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS asiento_detalle (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    asiento_id     INT NOT NULL,
    cuenta_codigo  VARCHAR(12) NOT NULL,
    debe           DECIMAL(11,2) NOT NULL DEFAULT 0,
    haber          DECIMAL(11,2) NOT NULL DEFAULT 0,
    orden          INT NOT NULL,
    FOREIGN KEY (asiento_id) REFERENCES asientos(id),
    FOREIGN KEY (cuenta_codigo) REFERENCES cuentas(codigo)
);

-- ============================================================
-- REGISTRO AUXILIAR DE IVA (Compras y Ventas)
-- Formato estándar: Fecha, N° Comprobante, Tipo, CUIT,
-- Razón Social, Neto Gravado, IVA, Total.
-- Cada fila se vincula al asiento contable que la originó.
-- ============================================================

CREATE TABLE IF NOT EXISTS iva_compras (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    asiento_id        INT NOT NULL,
    fecha             DATE NOT NULL,
    nro_comprobante   VARCHAR(20) NOT NULL,
    tipo_comprobante  VARCHAR(5) NOT NULL,
    cuit              VARCHAR(13) NOT NULL,
    razon_social      VARCHAR(100) NOT NULL,   -- proveedor
    neto_gravado      DECIMAL(11,2) NOT NULL,
    iva               DECIMAL(11,2) NOT NULL,
    total             DECIMAL(11,2) NOT NULL,
    FOREIGN KEY (asiento_id) REFERENCES asientos(id)
);

CREATE TABLE IF NOT EXISTS iva_ventas (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    asiento_id        INT NOT NULL,
    fecha             DATE NOT NULL,
    nro_comprobante   VARCHAR(20) NOT NULL,
    tipo_comprobante  VARCHAR(5) NOT NULL,
    cuit              VARCHAR(13) NOT NULL,
    razon_social      VARCHAR(100) NOT NULL,   -- cliente
    neto_gravado      DECIMAL(11,2) NOT NULL,
    iva               DECIMAL(11,2) NOT NULL,
    total             DECIMAL(11,2) NOT NULL,
    FOREIGN KEY (asiento_id) REFERENCES asientos(id)
);

-- ============================================================
-- DATOS DE PRUEBA
-- Simplificados solo para poder probar Mayor e IVA ya mismo.
-- El saldo de cada cuenta se deja cargado directo con el valor
-- final que resultaría de estos movimientos (no pasa por
-- AsientoDAO, así que no dispara su lógica de actualización de
-- saldo — por eso lo dejamos precalculado acá).
-- ============================================================

-- Cuentas de prueba (si ya las creaste desde la app con otro
-- código, borrá estos INSERT y usá los códigos reales)
INSERT INTO cuentas (codigo, grupo_id, tipo, rubro_id, numero_cuenta, nombre, saldo, tipo_saldo) VALUES
('1.1.01.01', 1, 1, 1, 1, 'Caja',        9100.00, 'D'),
('2.1.05.01', 2, 1, 5, 1, 'Proveedores', 3050.00, 'A'),
('4.0.09.01', 4, 0, 9, 1, 'Ventas',      12100.00, 'A'),
('5.0.10.01', 5, 0, 10, 1, 'Compras',    6050.00, 'D');

-- Asiento 1: venta al contado
INSERT INTO asientos (numero, fecha, concepto) VALUES
(1, '2026-09-05', 'Venta según factura A-0001');
SET @asiento1 = LAST_INSERT_ID();

INSERT INTO asiento_detalle (asiento_id, cuenta_codigo, debe, haber, orden) VALUES
(@asiento1, '1.1.01.01', 12100.00, 0, 1),
(@asiento1, '4.0.09.01', 0, 12100.00, 2);

INSERT INTO iva_ventas (asiento_id, fecha, nro_comprobante, tipo_comprobante, cuit, razon_social, neto_gravado, iva, total) VALUES
(@asiento1, '2026-09-05', 'A-0001', 'A', '20-12345678-9', 'Cliente S.A.', 10000.00, 2100.00, 12100.00);

-- Asiento 2: compra a crédito
INSERT INTO asientos (numero, fecha, concepto) VALUES
(2, '2026-09-10', 'Compra según factura B-0045');
SET @asiento2 = LAST_INSERT_ID();

INSERT INTO asiento_detalle (asiento_id, cuenta_codigo, debe, haber, orden) VALUES
(@asiento2, '5.0.10.01', 6050.00, 0, 1),
(@asiento2, '2.1.05.01', 0, 6050.00, 2);

INSERT INTO iva_compras (asiento_id, fecha, nro_comprobante, tipo_comprobante, cuit, razon_social, neto_gravado, iva, total) VALUES
(@asiento2, '2026-09-10', 'B-0045', 'B', '30-98765432-1', 'Proveedor S.R.L.', 5000.00, 1050.00, 6050.00);

-- Asiento 3: pago parcial a proveedor
INSERT INTO asientos (numero, fecha, concepto) VALUES
(3, '2026-09-15', 'Pago parcial a proveedor');
SET @asiento3 = LAST_INSERT_ID();

INSERT INTO asiento_detalle (asiento_id, cuenta_codigo, debe, haber, orden) VALUES
(@asiento3, '2.1.05.01', 3000.00, 0, 1),
(@asiento3, '1.1.01.01', 0, 3000.00, 2);

-- Verificación
SELECT * FROM asientos;
SELECT * FROM asiento_detalle;
SELECT * FROM iva_ventas;
SELECT * FROM iva_compras;
SELECT codigo, nombre, saldo, tipo_saldo FROM cuentas;
