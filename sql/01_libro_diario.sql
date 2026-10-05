-- ============================================================
-- ALTA INCREMENTAL - Módulo Libro Diario (economia_db)
-- Usar este script SOLO si ya tenés la base creada y no querés
-- perder los datos existentes (grupos, rubros, cuentas).
-- Si preferís partir de cero, usá 00_reset_completo.sql
-- (que ya incluye estas mismas tablas).
-- ============================================================

USE economia_db;

CREATE TABLE IF NOT EXISTS asientos (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    numero    INT NOT NULL,
    fecha     DATE NOT NULL,
    concepto  VARCHAR(200) NOT NULL,
    total_debe DECIMAL(11,2) NOT NULL DEFAULT 0,
    total_haber DECIMAL(11,2) NOT NULL DEFAULT 0,
    UNIQUE KEY uq_asiento_numero (numero)
);

CREATE TABLE IF NOT EXISTS asiento_detalle (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    asiento_id     INT NOT NULL,
    cuenta_codigo  VARCHAR(12) NOT NULL,
    debe           DECIMAL(11,2) NOT NULL DEFAULT 0,
    haber          DECIMAL(11,2) NOT NULL DEFAULT 0,
    orden          INT NOT NULL,
    FOREIGN KEY (asiento_id) REFERENCES asientos(id) ON DELETE CASCADE,
    FOREIGN KEY (cuenta_codigo) REFERENCES cuentas(codigo)
);

SHOW TABLES;
