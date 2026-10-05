# Sistema Contable

Aplicación de escritorio Java Swing para administrar el plan de cuentas y el Libro Diario. Incluye MySQL y una interfaz gráfica accesible desde el navegador cuando se ejecuta con Docker.

## Ejecutar con Docker

Se necesita Docker Desktop (o Docker Engine con Docker Compose). Desde la raíz del proyecto:

```bash
docker compose up --build
```

Cuando los servicios estén listos, abrir [http://localhost:6080/vnc.html](http://localhost:6080/vnc.html) y seleccionar **Connect**. La ventana de la aplicación se muestra en el navegador; no hace falta instalar Java ni MySQL en el equipo anfitrión.

Para detenerlo:

```bash
docker compose down
```

Los datos se conservan en el volumen `mysql_data`. Para borrar también todos los datos y reinicializar la base:

```bash
docker compose down -v
```

## Configuración

Los valores de desarrollo están definidos en `compose.yaml`. Para cambiarlos, copiar `.env.example` a `.env` y editarlo antes de iniciar los contenedores. No subir `.env` al repositorio.

La aplicación obtiene su conexión de las variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` y `DB_PASSWORD`. Sin Docker mantiene valores locales compatibles (`localhost:3306`, base `economia_db`, usuario y clave `root`).

## Desarrollo local

El proyecto ahora usa Maven y Java 21 para evitar la dependencia de rutas locales de NetBeans:

```bash
mvn package
java -jar target/sistema-contable-1.0.0.jar
```

El esquema que inicializa Docker está en `docker/mysql/init/01_schema.sql`. Los scripts SQL originales se mantienen para uso manual.

## Libro IVA

La aplicación incluye Libro IVA Compras y Ventas. En instalaciones existentes,
ejecutar una única vez `sql/05_operaciones_iva.sql` para añadir las columnas del
módulo sin borrar los registros ya cargados. En una instalación Docker nueva el
esquema se crea completo automáticamente.

Si ya existe el volumen de Docker de una versión anterior, aplicar la migración
antes de reiniciar la aplicación:

```bash
docker compose exec -T db mysql -ucontable -pcontable_dev economia_db < sql/05_operaciones_iva.sql
docker compose up --build -d
```

Si se cambiaron las credenciales en `.env`, reemplazar el usuario, contraseña y
nombre de base del ejemplo por los valores configurados.
