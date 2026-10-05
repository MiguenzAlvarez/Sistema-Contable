package models;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Importes monetarios con redondeo decimal, sin aritmética binaria. */
public final class ImportesIva {
    private ImportesIva() {}

    public static BigDecimal leer(String texto) {
        String valor = texto.trim();
        if (valor.isEmpty()) return BigDecimal.ZERO.setScale(2);
        // Decimal con coma (y miles con punto) o decimal con punto sin miles.
        if (!valor.matches("(?:\\d+(?:[.,]\\d{1,2})?|\\d{1,3}(?:\\.\\d{3})+(?:,\\d{1,2})?)")) {
            throw new IllegalArgumentException("Ingresá un importe positivo válido, por ejemplo 1234,56.");
        }
        if (valor.contains(",")) valor = valor.replace(".", "").replace(',', '.');
        else if (valor.matches("\\d{1,3}(?:\\.\\d{3})+")) valor = valor.replace(".", "");
        BigDecimal importe = new BigDecimal(valor).setScale(2, RoundingMode.HALF_UP);
        if (importe.compareTo(new BigDecimal("999999999.99")) > 0) {
            throw new IllegalArgumentException("El importe supera el máximo permitido.");
        }
        return importe;
    }

    public static BigDecimal calcular(BigDecimal neto, BigDecimal alicuota) {
        return neto.multiply(alicuota).movePointLeft(2).setScale(2, RoundingMode.HALF_UP);
    }
}
