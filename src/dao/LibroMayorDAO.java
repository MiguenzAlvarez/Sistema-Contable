package dao;

import conexion.Conexion;
import models.FilaMayor;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LibroMayorDAO {

    // ══════════════════════════════════════════════════════════════
    //  GENERAR EL LIBRO MAYOR DE UNA CUENTA
    //  Devuelve cada movimiento con su saldo corrido ya calculado
    //  (saldo = acumulado de Debe - acumulado de Haber; el signo
    //  define si en esa fila el saldo es Deudor o Acreedor).
    // ══════════════════════════════════════════════════════════════
    public List<FilaMayor> generarMayor(String codigoCuenta) {

        List<FilaMayor> filas = new ArrayList<>();

        String sql = "SELECT a.fecha, a.numero, a.concepto, d.debe, d.haber " +
                     "FROM asiento_detalle d " +
                     "JOIN asientos a ON a.id = d.asiento_id " +
                     "WHERE d.cuenta_codigo = ? " +
                     "ORDER BY a.fecha ASC, a.numero ASC, d.orden ASC";

        try (
                Connection conn = Conexion.conectar();
                PreparedStatement pst = conn.prepareStatement(sql)
        ) {
            pst.setString(1, codigoCuenta);

            double acumulado = 0; // positivo = saldo deudor, negativo = saldo acreedor

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Date fecha = rs.getDate("fecha");
                    int numeroAsiento = rs.getInt("numero");
                    String concepto = rs.getString("concepto");
                    double debe = rs.getDouble("debe");
                    double haber = rs.getDouble("haber");

                    acumulado += debe - haber;
                    acumulado = Math.round(acumulado * 100) / 100.0;

                    String naturaleza = acumulado >= 0 ? "D" : "A";
                    double saldoMostrado = Math.abs(acumulado);

                    filas.add(new FilaMayor(fecha, numeroAsiento, concepto, debe, haber,
                            saldoMostrado, naturaleza));
                }
            }

        } catch (Exception e) {
            System.out.println("Error al generar el Libro Mayor");
            System.out.println(e.getMessage());
        }

        return filas;
    }
}
