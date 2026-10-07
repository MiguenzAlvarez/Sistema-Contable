# Sistema Contable

Aplicación de escritorio desarrollada con **Java Swing y MySQL** para gestionar cuentas, registrar asientos y consultar libros contables. Puede ejecutarse en el equipo o con Docker, accediendo a la interfaz de escritorio desde el navegador mediante noVNC.

## Funcionalidades

- **Plan de cuentas:** alta, consulta, modificación y eliminación de cuentas, organizadas por grupo y rubro.
- **Libro Diario:** registro y consulta de asientos, validación de partida doble y actualización de saldos.
- **Operaciones con IVA:** selección de asiento general sin IVA, compra o venta; datos de proveedor o cliente, comprobante, CUIT/DNI, alícuota y cálculo automático de IVA y total.
- **Libro Mayor:** consulta de movimientos y saldos por cuenta.
- **Libro IVA Compras y Ventas:** consulta por período, detalle de comprobantes, totales e impresión.
- **Navegación entre pantallas:** acceso a los módulos desde la aplicación.

El número generado para las operaciones con IVA es un identificador interno. No se incluye un servicio de emisión o autorización fiscal de comprobantes.

## Tecnologías

| Componente | Tecnología |
| --- | --- |
| Aplicación | Java 21 y Swing |
| Persistencia | MySQL y JDBC |
| Compilación | Maven |
| Entorno empaquetado | Docker Compose |
| Acceso a la interfaz en Docker | noVNC, Xvfb y Openbox |

## Inicio rápido con Docker

Requisitos: Git y Docker Desktop con Docker Compose habilitado.

```bash
git clone https://github.com/MiguenzAlvarez/Sistema-Contable.git
cd Sistema-Contable
docker compose up --build -d
```

Una vez iniciados los servicios, abrir [http://localhost:6080/vnc.html](http://localhost:6080/vnc.html) y seleccionar **Connect**. La aplicación Swing se muestra dentro del navegador; no es necesario instalar Java o MySQL por separado.

La primera ejecución crea la base de datos con el esquema y datos de demostración definidos en `docker/mysql/init/01_schema.sql`.

Para consultar el estado o los mensajes de los servicios:

```bash
docker compose ps
docker compose logs --tail=100
```

Para detenerlos:

```bash
docker compose down
```

Los datos permanecen en el volumen `mysql_data` al detener los contenedores con ese comando.

### Configuración de Docker

Copiar `.env.example` como `.env` y ajustar los valores antes del primer inicio:

| Variable | Uso |
| --- | --- |
| `MYSQL_DATABASE` | Nombre de la base de datos |
| `MYSQL_USER` | Usuario de la aplicación |
| `MYSQL_PASSWORD` | Contraseña del usuario de la aplicación |
| `MYSQL_ROOT_PASSWORD` | Contraseña de administración de MySQL |
| `APP_PORT` | Puerto de acceso desde el navegador; por defecto, `6080` |

El archivo `.env` está excluido de Git. Los valores incluidos en los archivos de ejemplo son para desarrollo. Cambiar estos valores no modifica las credenciales de una base ya inicializada en un volumen existente.

## Ejecución local con Java

Requisitos: **JDK 21 o superior**, **Maven** y un servidor **MySQL**.

1. Crear una base de datos y, si está vacía, cargar `docker/mysql/init/01_schema.sql` seleccionando esa base. Este esquema inicial incluye datos de demostración.
2. Configurar las variables de entorno `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` y `DB_PASSWORD` para la conexión local.
3. Compilar y ejecutar desde la raíz del proyecto.

Ejemplo en PowerShell:

```powershell
$env:DB_HOST = 'localhost'
$env:DB_PORT = '3306'
$env:DB_NAME = 'economia_db'
$env:DB_USER = 'tu_usuario'
$env:DB_PASSWORD = 'tu_contrasena_local'
mvn package
java -jar target/sistema-contable-1.0.0.jar
```

Fuera de Docker, el programa lee las variables del entorno; no carga automáticamente el archivo `.env`. En NetBeans se puede trabajar con el proyecto Maven definido en `pom.xml` y configurar la conexión en el entorno desde el que se ejecuta.

## Actualización de una base existente

Para incorporar las tablas y columnas de IVA, utilizar `sql/05_operaciones_iva.sql`. La migración está preparada para conservar los registros existentes y también agrega cuentas base si faltan.

Con MySQL local, ejecutar el archivo sobre la base correspondiente desde el cliente de MySQL. Si el nombre de la base es distinto de `economia_db`, ajustar la instrucción `USE` del script.

En Docker, se puede copiar el script al contenedor y ejecutarlo desde MySQL. Estos pasos funcionan también desde PowerShell:

```bash
docker compose cp sql/05_operaciones_iva.sql db:/tmp/05_operaciones_iva.sql
docker compose exec db mysql -ucontable -p economia_db
```

Ingresar la contraseña configurada y, en el cliente MySQL:

```sql
SOURCE /tmp/05_operaciones_iva.sql;
EXIT;
```

Sustituir el usuario y la base si se cambiaron en la configuración. Los scripts `00_reset_completo.sql` son de reinicialización: no utilizarlos para actualizar una base con datos que se quieran conservar.

## Pruebas

Prueba independiente de importes, separadores decimales, límites, alícuotas y redondeo:

```bash
javac -encoding UTF-8 -d build/test-classes src/models/ImportesIva.java test/IvaCalculosTest.java
java -cp build/test-classes IvaCalculosTest
```

`test/FlujoContableIntegracionTest.java` contiene una prueba del recorrido Diario–IVA–Mayor. Es una clase ejecutable que registra movimientos; usarla únicamente con una base de pruebas. Estas pruebas se ejecutan mediante sus métodos `main`, no automáticamente como pruebas JUnit al ejecutar Maven.

## Estructura

```text
src/
  conexion/       Conexión con MySQL
  dao/            Acceso a datos y transacciones
  models/         Modelos del sistema
  views/          Pantallas Swing
  main/           Punto de entrada
sql/              Scripts y migraciones
test/             Pruebas ejecutables
docker/           Entorno gráfico y esquema inicial
compose.yaml      Servicios de aplicación y base de datos
pom.xml           Compilación y empaquetado Maven
```

Para más detalles de la carga de comprobantes, consultar [Operaciones con IVA](OPERACIONES_IVA.md).

## Proyecto colaborativo

Este proyecto se desarrolla de forma colaborativa como parte de una aplicación para una materia del último año de la carrera. Integra los aportes del equipo para implementar un sistema contable y aplicar los conocimientos adquiridos durante la formación.
