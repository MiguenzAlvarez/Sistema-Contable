import java.math.BigDecimal;
import models.ImportesIva;

/** Ejecutable con java -cp build/test-classes IvaCalculosTest. */
public class IvaCalculosTest {
    private static void igual(String esperado, BigDecimal valor) {
        if (new BigDecimal(esperado).compareTo(valor) != 0)
            throw new AssertionError("Esperado " + esperado + "; recibido " + valor);
    }

    public static void main(String[] args) {
        igual("1234.56", ImportesIva.leer("1.234,56"));
        igual("1234.56", ImportesIva.leer("1234.56"));
        igual("1234", ImportesIva.leer("1.234"));
        igual("0", ImportesIva.leer(""));
        igual("210", ImportesIva.calcular(new BigDecimal("1000"), new BigDecimal("21")));
        igual("105", ImportesIva.calcular(new BigDecimal("1000"), new BigDecimal("10.5")));
        igual("0.01", ImportesIva.calcular(new BigDecimal("0.05"), new BigDecimal("21")));
        igual("0", ImportesIva.calcular(new BigDecimal("1000"), BigDecimal.ZERO));
        for (String invalido : new String[]{"-1", "NaN", "Infinity", "1,2,3", "1,234", "1000000000"}) {
            try {
                ImportesIva.leer(invalido);
                throw new AssertionError("No se rechazó: " + invalido);
            } catch (IllegalArgumentException esperado) {}
        }
        System.out.println("OK: importes, separadores, límites, alícuotas y redondeo.");
    }
}
