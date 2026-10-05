# Operaciones con IVA

En Libro Diario, seleccionar **Asiento general sin IVA**, **Compra** o **Venta**.
La fecha inicial es la del día. Compra y Venta muestran los datos adicionales
del comprobante; el asiento general conserva el formulario de cuentas.

Para Compra o Venta:

1. Completar tipo de comprobante, punto de venta, condición IVA y proveedor o cliente.
2. Informar CUIT o DNI y los importes. El IVA se calcula sobre el neto gravado,
   redondeando a dos decimales. El total incluye neto gravado, IVA, exento,
   no gravado y otros/percepciones.
3. Completar las cuentas del asiento. Debe y Haber deben coincidir con el total
   del comprobante. Las cuentas se eligen explícitamente desde el plan existente.
4. Guardar. El asiento, los saldos y el registro IVA se confirman en una sola
   transacción. La confirmación muestra el número interno generado.

El número es un identificador **interno**, formado por punto de venta e ID del
asiento; no es una integración de emisión o autorización fiscal.
«Horas» se conserva literalmente como una cantidad informativa y no suma dinero.

Libro IVA permite consultar Compras o Ventas, elegir mes y año, ver los registros
guardados y sus totales e imprimir en orientación horizontal. La tabla permite
desplazamiento horizontal para mantener legibles todas las columnas.

## Base de datos

En otra instalación, ejecutar `sql/05_operaciones_iva.sql` sobre `economia_db`
antes de abrir la nueva versión. Es repetible: agrega las columnas faltantes y
amplía el tipo de comprobante, conservando los registros anteriores. Requiere
las tablas existentes de los scripts anteriores. No ejecutar el script de reset
para actualizar una base con datos.

## Prueba de cálculos

```powershell
javac -encoding UTF-8 -d build/test-classes src/models/ImportesIva.java test/IvaCalculosTest.java
java -cp build/test-classes IvaCalculosTest
```
