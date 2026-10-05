import dao.AsientoDAO;
import dao.LibroIvaDAO;
import dao.LibroMayorDAO;
import models.Asiento;
import models.AsientoDetalle;
import models.RegistroIva;

import java.sql.Date;
import java.util.List;

/**
 * Prueba ejecutable contra una base MySQL de prueba/desarrollo.
 * Comprueba el recorrido completo: Diario, registro de IVA y Libro Mayor.
 */
public class FlujoContableIntegracionTest {
    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }

    private static RegistroIva comprobante(String razonSocial, String cuit,
                                            double neto, double iva, int puntoVenta) {
        RegistroIva registro = new RegistroIva(Date.valueOf("2026-10-05"), "", "Factura A", cuit,
                razonSocial, "Responsable Inscripto", neto, 0, 0, 21, iva, neto + iva);
        registro.setPuntoVenta(puntoVenta);
        return registro;
    }

    private static boolean yaExiste(AsientoDAO diario, String concepto) {
        return diario.buscarPorConcepto(concepto).stream()
                .anyMatch(a -> concepto.equals(a.getConcepto()));
    }

    private static void registrarCompra(AsientoDAO diario) {
        if (yaExiste(diario, "Compra de prueba con IVA")) return;
        Asiento compra = new Asiento("2026-10-05", "Compra de prueba con IVA", List.of(
                new AsientoDetalle("5.0.10.01", 1000, 0),
                new AsientoDetalle("1.1.02.02", 210, 0),
                new AsientoDetalle("2.1.05.01", 0, 1210)));
        RegistroIva iva = comprobante("Proveedor de prueba S.A.", "30-12345678-9", 1000, 210, 1);
        verificar("OK".equals(diario.crearAsiento(compra, iva, true)), diario.getUltimoError());
        verificar(iva.getNroComprobante().matches("00001-\\d{8}"), "No se generó el número interno de compra.");
    }

    private static void registrarVenta(AsientoDAO diario) {
        if (yaExiste(diario, "Venta de prueba con IVA")) return;
        Asiento venta = new Asiento("2026-10-05", "Venta de prueba con IVA", List.of(
                new AsientoDetalle("1.1.01.01", 2420, 0),
                new AsientoDetalle("4.0.09.01", 0, 2000),
                new AsientoDetalle("2.1.05.02", 0, 420)));
        RegistroIva iva = comprobante("Cliente de prueba S.R.L.", "30-87654321-0", 2000, 420, 2);
        verificar("OK".equals(diario.crearAsiento(venta, iva, false)), diario.getUltimoError());
        verificar(iva.getNroComprobante().matches("00002-\\d{8}"), "No se generó el número interno de venta.");
    }

    private static void registrarMovimientosGenerales(AsientoDAO diario) {
        if (!yaExiste(diario, "Transferencia de Caja a Bancos")) {
        Asiento transferencia = new Asiento("2026-10-06", "Transferencia de Caja a Bancos", List.of(
                new AsientoDetalle("1.1.01.02", 800, 0),
                new AsientoDetalle("1.1.01.01", 0, 800)));
        verificar("OK".equals(diario.crearAsiento(transferencia)), diario.getUltimoError());
        }

        if (!yaExiste(diario, "Pago parcial a proveedores")) {
        Asiento pagoProveedor = new Asiento("2026-10-07", "Pago parcial a proveedores", List.of(
                new AsientoDetalle("2.1.05.01", 350, 0),
                new AsientoDetalle("1.1.01.02", 0, 350)));
        verificar("OK".equals(diario.crearAsiento(pagoProveedor)), diario.getUltimoError());
        }
    }

    public static void main(String[] args) {
        AsientoDAO diario = new AsientoDAO();
        registrarCompra(diario);
        registrarVenta(diario);
        registrarMovimientosGenerales(diario);

        LibroIvaDAO libroIva = new LibroIvaDAO();
        verificar(libroIva.listarCompras(10, 2026).stream()
                        .anyMatch(r -> "Proveedor de prueba S.A.".equals(r.getRazonSocial())),
                "La compra no apareció en Libro IVA.");
        verificar(libroIva.listarVentas(10, 2026).stream()
                        .anyMatch(r -> "Cliente de prueba S.R.L.".equals(r.getRazonSocial())),
                "La venta no apareció en Libro IVA.");
        verificar(new LibroMayorDAO().generarMayor("4.0.09.01").stream()
                        .anyMatch(f -> "Venta de prueba con IVA".equals(f.getDetalle())
                                && f.getHaber() == 2000 && "A".equals(f.getNaturalezaSaldo())),
                "La venta no apareció en el Libro Mayor.");
        verificar(new LibroMayorDAO().generarMayor("1.1.01.02").size() >= 2,
                "Los movimientos generales no aparecieron en el Libro Mayor.");
        System.out.println("OK: compra, venta, IVA, saldos y Libro Mayor verificados.");
    }
}
