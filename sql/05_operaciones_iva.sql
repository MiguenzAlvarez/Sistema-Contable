-- Crea o amplía los libros IVA sin borrar registros. Ejecutar una vez antes de
-- usar la nueva versión. Requiere que ya existan asientos y cuentas.
USE economia_db;

-- Las instalaciones anteriores al módulo IVA no tienen estas tablas. Se crean
-- vacías aquí; si ya existen, CREATE TABLE IF NOT EXISTS no modifica datos.
CREATE TABLE IF NOT EXISTS iva_compras (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    asiento_id         INT NOT NULL,
    fecha              DATE NOT NULL,
    nro_comprobante    VARCHAR(20) NOT NULL,
    tipo_comprobante   VARCHAR(40) NOT NULL,
    cuit               VARCHAR(13) NOT NULL,
    razon_social       VARCHAR(100) NOT NULL,
    condicion_iva      VARCHAR(30) NOT NULL DEFAULT '',
    neto_gravado       DECIMAL(11,2) NOT NULL,
    neto_no_gravado    DECIMAL(14,2) NOT NULL DEFAULT 0,
    exento             DECIMAL(14,2) NOT NULL DEFAULT 0,
    alicuota           DECIMAL(5,2) NOT NULL DEFAULT 0,
    iva                DECIMAL(11,2) NOT NULL,
    total              DECIMAL(11,2) NOT NULL,
    punto_venta        INT NOT NULL DEFAULT 0,
    dni                VARCHAR(8) NOT NULL DEFAULT '',
    otros_percepciones DECIMAL(11,2) NOT NULL DEFAULT 0,
    horas              DECIMAL(11,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (asiento_id) REFERENCES asientos(id)
);

CREATE TABLE IF NOT EXISTS iva_ventas (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    asiento_id         INT NOT NULL,
    fecha              DATE NOT NULL,
    nro_comprobante    VARCHAR(20) NOT NULL,
    tipo_comprobante   VARCHAR(40) NOT NULL,
    cuit               VARCHAR(13) NOT NULL,
    razon_social       VARCHAR(100) NOT NULL,
    condicion_iva      VARCHAR(30) NOT NULL DEFAULT '',
    neto_gravado       DECIMAL(11,2) NOT NULL,
    neto_no_gravado    DECIMAL(14,2) NOT NULL DEFAULT 0,
    exento             DECIMAL(14,2) NOT NULL DEFAULT 0,
    alicuota           DECIMAL(5,2) NOT NULL DEFAULT 0,
    iva                DECIMAL(11,2) NOT NULL,
    total              DECIMAL(11,2) NOT NULL,
    punto_venta        INT NOT NULL DEFAULT 0,
    dni                VARCHAR(8) NOT NULL DEFAULT '',
    otros_percepciones DECIMAL(11,2) NOT NULL DEFAULT 0,
    horas              DECIMAL(11,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (asiento_id) REFERENCES asientos(id)
);
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_compras ADD COLUMN condicion_iva VARCHAR(30) NOT NULL DEFAULT ''''', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_compras' AND column_name = 'condicion_iva');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_compras ADD COLUMN neto_no_gravado DECIMAL(14,2) NOT NULL DEFAULT 0', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_compras' AND column_name = 'neto_no_gravado');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_compras ADD COLUMN exento DECIMAL(14,2) NOT NULL DEFAULT 0', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_compras' AND column_name = 'exento');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_compras ADD COLUMN alicuota DECIMAL(5,2) NOT NULL DEFAULT 0', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_compras' AND column_name = 'alicuota');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_compras ADD COLUMN punto_venta INT NOT NULL DEFAULT 0', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_compras' AND column_name = 'punto_venta');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_compras ADD COLUMN dni VARCHAR(8) NOT NULL DEFAULT ''''', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_compras' AND column_name = 'dni');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_compras ADD COLUMN otros_percepciones DECIMAL(11,2) NOT NULL DEFAULT 0', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_compras' AND column_name = 'otros_percepciones');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_compras ADD COLUMN horas DECIMAL(11,2) NOT NULL DEFAULT 0', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_compras' AND column_name = 'horas');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
ALTER TABLE iva_compras MODIFY COLUMN tipo_comprobante VARCHAR(40) NOT NULL;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_ventas ADD COLUMN condicion_iva VARCHAR(30) NOT NULL DEFAULT ''''', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_ventas' AND column_name = 'condicion_iva');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_ventas ADD COLUMN neto_no_gravado DECIMAL(14,2) NOT NULL DEFAULT 0', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_ventas' AND column_name = 'neto_no_gravado');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_ventas ADD COLUMN exento DECIMAL(14,2) NOT NULL DEFAULT 0', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_ventas' AND column_name = 'exento');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_ventas ADD COLUMN alicuota DECIMAL(5,2) NOT NULL DEFAULT 0', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_ventas' AND column_name = 'alicuota');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_ventas ADD COLUMN punto_venta INT NOT NULL DEFAULT 0', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_ventas' AND column_name = 'punto_venta');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_ventas ADD COLUMN dni VARCHAR(8) NOT NULL DEFAULT ''''', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_ventas' AND column_name = 'dni');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_ventas ADD COLUMN otros_percepciones DECIMAL(11,2) NOT NULL DEFAULT 0', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_ventas' AND column_name = 'otros_percepciones');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
SET @ddl_iva = (SELECT IF(COUNT(*) = 0, 'ALTER TABLE iva_ventas ADD COLUMN horas DECIMAL(11,2) NOT NULL DEFAULT 0', 'SELECT 1') FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'iva_ventas' AND column_name = 'horas');
PREPARE migracion_iva FROM @ddl_iva;
EXECUTE migracion_iva;
DEALLOCATE PREPARE migracion_iva;
ALTER TABLE iva_ventas MODIFY COLUMN tipo_comprobante VARCHAR(40) NOT NULL;
