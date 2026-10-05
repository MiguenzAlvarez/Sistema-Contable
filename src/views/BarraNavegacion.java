package views;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// Barra de botones que permite saltar entre las 4 pantallas del sistema
// (Plan de Cuentas, Libro Diario, Libro Mayor, Libro de IVA) desde
// cualquiera de ellas. Cada pantalla la agrega a su propio header
// pasando su propia clase, así no se muestra un botón para "volver"
// a la misma ventana en la que ya estás parado.
public class BarraNavegacion {

    public static JPanel crear(Class<?> pantallaActual) {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        barra.setOpaque(false);

        agregarBoton(barra, pantallaActual, GestionCuentasPanel.class, "Plan de Cuentas");
        agregarBoton(barra, pantallaActual, LibroDiarioPanel.class,    "Libro Diario");
        agregarBoton(barra, pantallaActual, LibroMayorPanel.class,     "Libro Mayor");
        agregarBoton(barra, pantallaActual, LibroIvaPanel.class,       "Libro de IVA");

        return barra;
    }

    private static void agregarBoton(JPanel barra, Class<?> actual, Class<?> destino, String texto) {
        if (actual == destino) return; // no mostrar botón para volver a la pantalla actual

        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(255, 255, 255, 55) : new Color(255, 255, 255, 30));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(120, 32));
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> abrir(destino));
        barra.add(btn);
    }

    private static void abrir(Class<?> destino) {
        SwingUtilities.invokeLater(() -> {
            if (destino == GestionCuentasPanel.class) new GestionCuentasPanel();
            else if (destino == LibroDiarioPanel.class) new LibroDiarioPanel();
            else if (destino == LibroMayorPanel.class) new LibroMayorPanel();
            else if (destino == LibroIvaPanel.class) new LibroIvaPanel();
        });
    }
}
