package dao;

import conexion.Conexion;
import models.Asiento;
import models.AsientoDetalle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AsientoDAO {

    // ══════════════════════════════════════════════════════════════
    //  CREAR ASIENTO (partida doble obligatoria)
    // ══════════════════════════════════════════════════════════════
    // Devuelve:
    //   "OK"             -> se guardó correctamente
    //   "DESBALANCEADO"  -> la suma del Debe no coincide con la del Haber
    //   "SIN_LINEAS"     -> no hay líneas de detalle o los importes están en 0
    //   "ERROR"          -> error de base de datos (se hace rollback)
    public String crearAsiento(Asiento asiento) {

        List<AsientoDetalle> detalles = asiento.getDetalles();
        if (detalles == null || detalles.isEmpty()) {
            return "SIN_LINEAS";
        }

        double sumaDebe = 0, sumaHaber = 0;
        for (AsientoDetalle d : detalles) {
            sumaDebe  += d.getDebe();
            sumaHaber += d.getHaber();
        }
        sumaDebe  = Math.round(sumaDebe  * 100) / 100.0;
        sumaHaber = Math.round(sumaHaber * 100) / 100.0;

        if (sumaDebe == 0 && sumaHaber == 0) {
            return "SIN_LINEAS";
        }

        // ── VALIDACIÓN CLAVE DE PARTIDA DOBLE ──────────────────────
        // El sistema NUNCA permite guardar un asiento si Debe != Haber.
        if (Math.abs(sumaDebe - sumaHaber) >= 0.005) {
            return "DESBALANCEADO";
        }

        String sqlAsiento = "INSERT INTO asientos(numero, fecha, concepto) VALUES(?,?,?)";
        String sqlDetalle = "INSERT INTO asiento_detalle(asiento_id, cuenta_codigo, debe, haber, orden) " +
                             "VALUES(?,?,?,?,?)";
        String sqlSaldoDeudor   = "UPDATE cuentas SET saldo = saldo + (? - ?) WHERE codigo = ? AND tipo_saldo = 'D'";
        String sqlSaldoAcreedor = "UPDATE cuentas SET saldo = saldo + (? - ?) WHERE codigo = ? AND tipo_saldo = 'A'";

        Connection conn = null;
        try {
            conn = Conexion.conectar();
            if (conn == null) return "ERROR";
            conn.setAutoCommit(false);

            synchronized (AsientoDAO.class) {

                int siguienteNumero = obtenerSiguienteNumero(conn);

                int asientoId;
                try (PreparedStatement pst = conn.prepareStatement(sqlAsiento, Statement.RETURN_GENERATED_KEYS)) {
                    pst.setInt(1, siguienteNumero);
                    pst.setString(2, asiento.getFecha());
                    pst.setString(3, asiento.getConcepto());
                    pst.executeUpdate();

                    try (ResultSet rs = pst.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("No se pudo generar el ID del asiento");
                        asientoId = rs.getInt(1);
                    }
                }

                int orden = 1;
                for (AsientoDetalle d : detalles) {
                    if (d.getDebe() == 0 && d.getHaber() == 0) continue;

                    try (PreparedStatement pst = conn.prepareStatement(sqlDetalle)) {
                        pst.setInt(1, asientoId);
                        pst.setString(2, d.getCuentaCodigo());
                        pst.setDouble(3, d.getDebe());
                        pst.setDouble(4, d.getHaber());
                        pst.setInt(5, orden++);
                        pst.executeUpdate();
                    }

                    // Actualiza el saldo de la cuenta según su naturaleza:
                    // cuentas Deudoras aumentan con el Debe, disminuyen con el Haber.
                    // cuentas Acreedoras aumentan con el Haber, disminuyen con el Debe.
                    try (PreparedStatement pstD = conn.prepareStatement(sqlSaldoDeudor)) {
                        pstD.setDouble(1, d.getDebe());
                        pstD.setDouble(2, d.getHaber());
                        pstD.setString(3, d.getCuentaCodigo());
                        pstD.executeUpdate();
                    }
                    try (PreparedStatement pstA = conn.prepareStatement(sqlSaldoAcreedor)) {
                        pstA.setDouble(1, d.getHaber());
                        pstA.setDouble(2, d.getDebe());
                        pstA.setString(3, d.getCuentaCodigo());
                        pstA.executeUpdate();
                    }
                }

                conn.commit();
                asiento.setNumero(siguienteNumero);
                System.out.println("Asiento N° " + siguienteNumero + " guardado correctamente");
                return "OK";
            }

        } catch (Exception e) {
            System.out.println("Error al crear asiento");
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
            return "ERROR";
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException ignored) {}
        }
    }

    private int obtenerSiguienteNumero(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(numero), 0) + 1 AS siguiente FROM asientos";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt("siguiente");
        }
        return 1;
    }

    // ══════════════════════════════════════════════════════════════
    //  LISTAR ASIENTOS (cabecera + totales, orden cronológico)
    // ══════════════════════════════════════════════════════════════
    public List<Asiento> listarAsientos() {

        List<Asiento> lista = new ArrayList<>();
        String sql = "SELECT a.id, a.numero, a.fecha, a.concepto, " +
                     "       COALESCE(SUM(d.debe), 0)  AS total_debe, " +
                     "       COALESCE(SUM(d.haber), 0) AS total_haber " +
                     "FROM asientos a " +
                     "LEFT JOIN asiento_detalle d ON d.asiento_id = a.id " +
                     "GROUP BY a.id, a.numero, a.fecha, a.concepto " +
                     "ORDER BY a.fecha ASC, a.numero ASC";

        try (
                Connection conn = Conexion.conectar();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)
        ) {
            while (rs.next()) {
                lista.add(mapearAsiento(rs));
            }
        } catch (Exception e) {
            System.out.println("Error al listar asientos");
            System.out.println(e.getMessage());
        }

        return lista;
    }

    // BUSCAR ASIENTOS POR CONCEPTO (para la barra de búsqueda)
    public List<Asiento> buscarPorConcepto(String texto) {

        List<Asiento> lista = new ArrayList<>();
        String sql = "SELECT a.id, a.numero, a.fecha, a.concepto, " +
                     "       COALESCE(SUM(d.debe), 0)  AS total_debe, " +
                     "       COALESCE(SUM(d.haber), 0) AS total_haber " +
                     "FROM asientos a " +
                     "LEFT JOIN asiento_detalle d ON d.asiento_id = a.id " +
                     "WHERE a.concepto LIKE ? " +
                     "GROUP BY a.id, a.numero, a.fecha, a.concepto " +
                     "ORDER BY a.fecha ASC, a.numero ASC";

        try (
                Connection conn = Conexion.conectar();
                PreparedStatement pst = conn.prepareStatement(sql)
        ) {
            pst.setString(1, "%" + texto + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAsiento(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar asientos");
            System.out.println(e.getMessage());
        }

        return lista;
    }

    private Asiento mapearAsiento(ResultSet rs) throws SQLException {
        return new Asiento(
                rs.getInt("id"),
                rs.getInt("numero"),
                rs.getString("fecha"),
                rs.getString("concepto"),
                rs.getDouble("total_debe"),
                rs.getDouble("total_haber")
        );
    }

    // ══════════════════════════════════════════════════════════════
    //  LISTAR EL DETALLE (líneas Debe/Haber) DE UN ASIENTO
    // ══════════════════════════════════════════════════════════════
    public List<AsientoDetalle> listarDetalle(int asientoId) {

        List<AsientoDetalle> lista = new ArrayList<>();
        String sql = "SELECT d.id, d.asiento_id, d.cuenta_codigo, c.nombre AS cuenta_nombre, " +
                     "       d.debe, d.haber, d.orden " +
                     "FROM asiento_detalle d " +
                     "JOIN cuentas c ON c.codigo = d.cuenta_codigo " +
                     "WHERE d.asiento_id = ? " +
                     "ORDER BY d.orden ASC";

        try (
                Connection conn = Conexion.conectar();
                PreparedStatement pst = conn.prepareStatement(sql)
        ) {
            pst.setInt(1, asientoId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(new AsientoDetalle(
                            rs.getInt("id"),
                            rs.getInt("asiento_id"),
                            rs.getString("cuenta_codigo"),
                            rs.getString("cuenta_nombre"),
                            rs.getDouble("debe"),
                            rs.getDouble("haber"),
                            rs.getInt("orden")
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("Error al listar el detalle del asiento");
            System.out.println(e.getMessage());
        }

        return lista;
    }

    // ══════════════════════════════════════════════════════════════
    //  ELIMINAR ASIENTO (revierte el efecto en los saldos de cuentas)
    // ══════════════════════════════════════════════════════════════
    public String eliminarAsiento(int asientoId) {

        String sqlDetalle = "SELECT cuenta_codigo, debe, haber FROM asiento_detalle WHERE asiento_id = ?";
        String sqlSaldoDeudor   = "UPDATE cuentas SET saldo = saldo - (? - ?) WHERE codigo = ? AND tipo_saldo = 'D'";
        String sqlSaldoAcreedor = "UPDATE cuentas SET saldo = saldo - (? - ?) WHERE codigo = ? AND tipo_saldo = 'A'";
        String sqlEliminarDetalle = "DELETE FROM asiento_detalle WHERE asiento_id = ?";
        String sqlEliminarAsiento = "DELETE FROM asientos WHERE id = ?";

        Connection conn = null;
        try {
            conn = Conexion.conectar();
            if (conn == null) return "ERROR";
            conn.setAutoCommit(false);

            List<Object[]> lineas = new ArrayList<>();
            try (PreparedStatement pst = conn.prepareStatement(sqlDetalle)) {
                pst.setInt(1, asientoId);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        lineas.add(new Object[]{
                                rs.getString("cuenta_codigo"),
                                rs.getDouble("debe"),
                                rs.getDouble("haber")
                        });
                    }
                }
            }

            if (lineas.isEmpty()) {
                conn.rollback();
                return "NO_EXISTE";
            }

            for (Object[] linea : lineas) {
                String codigo = (String) linea[0];
                double debe  = (Double) linea[1];
                double haber = (Double) linea[2];

                try (PreparedStatement pstD = conn.prepareStatement(sqlSaldoDeudor)) {
                    pstD.setDouble(1, debe);
                    pstD.setDouble(2, haber);
                    pstD.setString(3, codigo);
                    pstD.executeUpdate();
                }
                try (PreparedStatement pstA = conn.prepareStatement(sqlSaldoAcreedor)) {
                    pstA.setDouble(1, haber);
                    pstA.setDouble(2, debe);
                    pstA.setString(3, codigo);
                    pstA.executeUpdate();
                }
            }

            try (PreparedStatement pst = conn.prepareStatement(sqlEliminarDetalle)) {
                pst.setInt(1, asientoId);
                pst.executeUpdate();
            }
            try (PreparedStatement pst = conn.prepareStatement(sqlEliminarAsiento)) {
                pst.setInt(1, asientoId);
                pst.executeUpdate();
            }

            conn.commit();
            System.out.println("Asiento eliminado correctamente");
            return "OK";

        } catch (Exception e) {
            System.out.println("Error al eliminar asiento");
            System.out.println(e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
            return "ERROR";
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException ignored) {}
        }
    }
}
