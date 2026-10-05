package views;

import models.ImportesIva;
import models.RegistroIva;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Datos auxiliares que se guardan junto al asiento, nunca por separado. */
public class DatosIvaPanel extends JPanel {
    private final JLabel titulo = new JLabel();
    private final JLabel terceroLabel = new JLabel();
    private final JComboBox<String> comprobante = new JComboBox<>(new String[]{
        "Factura A", "Factura B", "Factura C", "Factura M"});
    private final JComboBox<String> condicion = new JComboBox<>(new String[]{
        "Seleccioná...", "Responsable inscripto", "Monotributista", "Exento", "Consumidor final", "No responsable"});
    private final JComboBox<String> alicuota = new JComboBox<>(new String[]{"0", "2,5", "5", "10,5", "21", "27"});
    private final JTextField puntoVenta = new JTextField();
    private final JTextField numero = new JTextField("Se asigna al registrar");
    private final JTextField tercero = new JTextField();
    private final JTextField cuit = new JTextField();
    private final JTextField dni = new JTextField();
    private final JTextField neto = new JTextField("0,00");
    private final JTextField exento = new JTextField("0,00");
    private final JTextField noGravado = new JTextField("0,00");
    private final JTextField otros = new JTextField("0,00");
    private final JTextField horas = new JTextField("0,00");
    private final JTextField iva = new JTextField("0,00");
    private final JTextField total = new JTextField("0,00");
    private final JLabel estado = new JLabel(" ");

    public DatosIvaPanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(14, 14, 14, 14));
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(titulo, BorderLayout.NORTH);
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        campo(campos, "Tipo de comprobante", comprobante, 0, 0);
        campo(campos, "Punto de venta", puntoVenta, 0, 1);
        campo(campos, "Número interno", numero, 0, 2);
        campo(campos, "Condición frente al IVA", condicion, 1, 0);
        agregar(campos, terceroLabel, tercero, 1, 1);
        campo(campos, "CUIT", cuit, 1, 2);
        campo(campos, "DNI", dni, 2, 0);
        campo(campos, "Neto gravado", neto, 2, 1);
        campo(campos, "Alícuota (%)", alicuota, 2, 2);
        campo(campos, "Exento", exento, 3, 0);
        campo(campos, "No gravado", noGravado, 3, 1);
        campo(campos, "Otros / percepciones", otros, 3, 2);
        campo(campos, "Horas", horas, 4, 0);
        campo(campos, "IVA calculado", iva, 4, 1);
        campo(campos, "Total", total, 4, 2);
        add(campos, BorderLayout.CENTER);
        JPanel pie = new JPanel(new GridLayout(0, 1, 0, 3));
        pie.setOpaque(false);
        pie.add(new JLabel("Completá debajo las cuentas y los importes del asiento por el total de la operación."));
        pie.add(estado);
        add(pie, BorderLayout.SOUTH);
        numero.setEditable(false);
        numero.setToolTipText("Identificador interno automático: punto de venta y número asignado al guardar.");
        iva.setEditable(false);
        total.setEditable(false);
        total.setFont(new Font("Segoe UI", Font.BOLD, 14));
        horas.setToolTipText("Cantidad informativa; no se suma a los importes.");
        DocumentListener listener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizar(); }
            public void removeUpdate(DocumentEvent e) { actualizar(); }
            public void changedUpdate(DocumentEvent e) { actualizar(); }
        };
        for (JTextField campo : new JTextField[]{neto, exento, noGravado, otros}) campo.getDocument().addDocumentListener(listener);
        alicuota.addActionListener(e -> actualizar());
        alicuota.setSelectedItem("21");
        setCompra(true);
    }

    private void campo(JPanel panel, String texto, JComponent componente, int fila, int columna) {
        agregar(panel, new JLabel(texto), componente, fila, columna);
    }

    private void agregar(JPanel panel, JLabel label, JComponent componente, int fila, int columna) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setLabelFor(componente);
        componente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        componente.setPreferredSize(new Dimension(220, 32));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = columna; c.gridy = fila * 2; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL; c.insets = new Insets(4, 0, 3, 12);
        panel.add(label, c);
        c.gridy++; c.insets = new Insets(0, 0, 6, 12);
        panel.add(componente, c);
    }

    public void setCompra(boolean compra) {
        titulo.setText("Datos para Libro IVA " + (compra ? "Compras" : "Ventas"));
        terceroLabel.setText(compra ? "Proveedor / razón social" : "Cliente / razón social");
    }

    private BigDecimal tasa() {
        return new BigDecimal(alicuota.getSelectedItem().toString().replace(',', '.'));
    }

    private BigDecimal total(BigDecimal base, BigDecimal impuesto) {
        return base.add(impuesto).add(ImportesIva.leer(exento.getText()))
            .add(ImportesIva.leer(noGravado.getText())).add(ImportesIva.leer(otros.getText()));
    }

    private void actualizar() {
        try {
            BigDecimal base = ImportesIva.leer(neto.getText());
            BigDecimal impuesto = ImportesIva.calcular(base, tasa());
            iva.setText(formato(impuesto));
            total.setText(formato(total(base, impuesto)));
            estado.setText(" ");
        } catch (IllegalArgumentException ex) {
            iva.setText("—"); total.setText("—");
            estado.setText(ex.getMessage());
            estado.setForeground(new Color(155, 28, 48));
        }
    }

    public RegistroIva leer(Date fecha) {
        String pv = puntoVenta.getText().trim();
        if (!pv.matches("\\d{1,5}") || Integer.parseInt(pv) == 0)
            throw new IllegalArgumentException("El punto de venta debe ser un número de 1 a 99999.");
        String razon = tercero.getText().trim();
        if (razon.isEmpty() || razon.length() > 100)
            throw new IllegalArgumentException("Ingresá la razón social (hasta 100 caracteres).");
        if (condicion.getSelectedIndex() == 0)
            throw new IllegalArgumentException("Seleccioná la condición frente al IVA.");
        String documento = cuit.getText().trim().replace("-", "");
        String documentoDni = dni.getText().trim().replace(".", "");
        if (!documento.isEmpty() && !documento.matches("\\d{11}"))
            throw new IllegalArgumentException("El CUIT debe contener 11 dígitos.");
        if (!documentoDni.isEmpty() && !documentoDni.matches("\\d{7,8}"))
            throw new IllegalArgumentException("El DNI debe contener 7 u 8 dígitos.");
        if (documento.isEmpty() && documentoDni.isEmpty())
            throw new IllegalArgumentException("Ingresá el CUIT o el DNI.");
        BigDecimal base = ImportesIva.leer(neto.getText());
        BigDecimal impuesto = ImportesIva.calcular(base, tasa());
        BigDecimal importeTotal = total(base, impuesto);
        if (importeTotal.signum() <= 0 || importeTotal.compareTo(new BigDecimal("999999999.99")) > 0)
            throw new IllegalArgumentException("El total debe ser mayor a cero y no superar 999.999.999,99.");
        RegistroIva registro = new RegistroIva(fecha, "", comprobante.getSelectedItem().toString(), documento,
            razon, condicion.getSelectedItem().toString(), base.doubleValue(),
            ImportesIva.leer(noGravado.getText()).doubleValue(), ImportesIva.leer(exento.getText()).doubleValue(),
            tasa().doubleValue(), impuesto.doubleValue(), importeTotal.doubleValue());
        registro.setPuntoVenta(Integer.parseInt(pv));
        registro.setDni(documentoDni);
        registro.setOtrosPercepciones(ImportesIva.leer(otros.getText()).doubleValue());
        registro.setHoras(ImportesIva.leer(horas.getText()).doubleValue());
        return registro;
    }

    public void limpiar() {
        puntoVenta.setText(""); tercero.setText(""); cuit.setText(""); dni.setText("");
        comprobante.setSelectedIndex(0); condicion.setSelectedIndex(0); alicuota.setSelectedItem("21");
        for (JTextField campo : new JTextField[]{neto, exento, noGravado, otros, horas}) campo.setText("0,00");
        actualizar();
    }

    private String formato(BigDecimal valor) {
        return String.format(Locale.forLanguageTag("es-AR"), "%,.2f", valor);
    }
}
