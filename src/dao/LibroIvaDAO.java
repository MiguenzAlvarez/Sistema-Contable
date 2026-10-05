package dao;

import conexion.Conexion;
import models.RegistroIva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LibroIvaDAO {
    /** La conexión pertenece a la transacción del asiento. */
    public String registrar(Connection conn, int asientoId, RegistroIva r, boolean compra) throws SQLException {
        String tabla = compra ? "iva_compras" : "iva_ventas";
        String numero = String.format(java.util.Locale.ROOT, "%05d-%08d", r.getPuntoVenta(), asientoId);
        String sql = "INSERT INTO " + tabla + " (asiento_id, fecha, nro_comprobante, tipo_comprobante, cuit, "
            + "razon_social, condicion_iva, neto_gravado, neto_no_gravado, exento, alicuota, iva, total, "
            + "punto_venta, dni, otros_percepciones, horas) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, asientoId); pst.setDate(2, r.getFecha()); pst.setString(3, numero);
            pst.setString(4, r.getTipoComprobante()); pst.setString(5, r.getCuit());
            pst.setString(6, r.getRazonSocial()); pst.setString(7, r.getCondicionIva());
            pst.setBigDecimal(8, BigDecimal.valueOf(r.getNetoGravado()));
            pst.setBigDecimal(9, BigDecimal.valueOf(r.getNetoNoGravado()));
            pst.setBigDecimal(10, BigDecimal.valueOf(r.getExento()));
            pst.setBigDecimal(11, BigDecimal.valueOf(r.getAlicuota()));
            pst.setBigDecimal(12, BigDecimal.valueOf(r.getIva()));
            pst.setBigDecimal(13, BigDecimal.valueOf(r.getTotal()));
            pst.setInt(14, r.getPuntoVenta()); pst.setString(15, r.getDni());
            pst.setBigDecimal(16, BigDecimal.valueOf(r.getOtrosPercepciones()));
            pst.setBigDecimal(17, BigDecimal.valueOf(r.getHoras()));
            pst.executeUpdate();
        }
        return numero;
    }

    public List<RegistroIva> listarCompras(int mes, int anio) {
        return listar("iva_compras", mes, anio);
    }

    public List<RegistroIva> listarVentas(int mes, int anio) {
        return listar("iva_ventas", mes, anio);
    }

    private List<RegistroIva> listar(String tabla, int mes, int anio) {

        List<RegistroIva> lista = new ArrayList<>();

        // El nombre de tabla viene fijo desde este mismo archivo (no desde
        // afuera), así que no hay riesgo de inyección SQL al concatenarlo.
        String sql = "SELECT fecha, nro_comprobante, tipo_comprobante, cuit, razon_social, " +
                     "       condicion_iva, neto_gravado, neto_no_gravado, exento, alicuota, iva, total, punto_venta, dni, otros_percepciones, horas " +
                     "FROM " + tabla + " " +
                     "WHERE MONTH(fecha) = ? AND YEAR(fecha) = ? " +
                     "ORDER BY fecha ASC, nro_comprobante ASC";

        try (
                Connection conn = Conexion.conectar();
                PreparedStatement pst = conn.prepareStatement(sql)
        ) {
            pst.setInt(1, mes);
            pst.setInt(2, anio);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    RegistroIva registro = new RegistroIva(
                            rs.getDate("fecha"),
                            rs.getString("nro_comprobante"),
                            rs.getString("tipo_comprobante"),
                            rs.getString("cuit"),
                            rs.getString("razon_social"),
                            rs.getString("condicion_iva"),
                            rs.getDouble("neto_gravado"),
                            rs.getDouble("neto_no_gravado"),
                            rs.getDouble("exento"),
                            rs.getDouble("alicuota"),
                            rs.getDouble("iva"),
                            rs.getDouble("total")
                    );
                    registro.setPuntoVenta(rs.getInt("punto_venta"));
                    registro.setDni(rs.getString("dni"));
                    registro.setOtrosPercepciones(rs.getDouble("otros_percepciones"));
                    registro.setHoras(rs.getDouble("horas"));
                    lista.add(registro);
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException("No se pudo consultar el Libro IVA. Verificá la conexión y la migración 05_operaciones_iva.sql.", e);
        }

        return lista;
    }
}
