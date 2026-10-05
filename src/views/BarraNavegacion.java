package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

// Barra de botones que permite saltar entre las 4 pantallas del sistema
// (Plan de Cuentas, Libro Diario, Libro Mayor, Libro de IVA) desde
// cualquiera de ellas. Cada pantalla la agrega a su propio header
// pasando su propia clase, así no se muestra un botón para "volver"
// a la misma ventana en la que ya estás parado.
public class BarraNavegacion {

    /** Barra completa: navegación entre módulos y controles de la ventana real. */
    public static JPanel crearConControles(Class<?> pantallaActual) {
        JPanel area = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        area.setOpaque(false);
        area.add(crear(pantallaActual));
        area.add(crearControl("−", "Minimizar", BarraNavegacion::minimizar));
        area.add(crearControl("□", "Maximizar o restaurar", BarraNavegacion::alternarMaximizado));
        area.add(crearControl("×", "Cerrar", BarraNavegacion::cerrar));
        return area;
    }

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
        btn.addActionListener(e -> abrir(SwingUtilities.getWindowAncestor(btn), destino));
        barra.add(btn);
    }

    /**
     * Construye el módulo elegido y reemplaza el contenido de la ventana que
     * ya está abierta. Así nunca se acumulan ventanas al navegar.
     */
    private static void abrir(Window origen, Class<?> destino) {
        SwingUtilities.invokeLater(() -> {
            if (!(origen instanceof JFrame actual)) return;

            JFrame siguiente;
            if (destino == GestionCuentasPanel.class) siguiente = new GestionCuentasPanel();
            else if (destino == LibroDiarioPanel.class) siguiente = new LibroDiarioPanel();
            else if (destino == LibroMayorPanel.class) siguiente = new LibroMayorPanel();
            else if (destino == LibroIvaPanel.class) siguiente = new LibroIvaPanel();
            else return;

            // Los constructores actuales muestran el JFrame. Se lo oculta de
            // inmediato y se transfiere su contenido a la ventana original.
            siguiente.setVisible(false);
            Container contenido = siguiente.getContentPane();
            siguiente.setContentPane(new JPanel());

            actual.setContentPane(contenido);
            actual.setTitle(siguiente.getTitle());
            actual.setMinimumSize(siguiente.getMinimumSize());
            actual.setSize(siguiente.getSize());
            actual.revalidate();
            actual.repaint();
            siguiente.dispose();
            actual.toFront();
        });
    }

    private static JButton crearControl(String texto, String descripcion, java.util.function.Consumer<Window> accion) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setToolTipText(descripcion);
        btn.setPreferredSize(new Dimension(38, 28));
        btn.setOpaque(false); btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> accion.accept(SwingUtilities.getWindowAncestor(btn)));
        return btn;
    }

    private static void minimizar(Window ventana) {
        if (ventana instanceof Frame frame) frame.setState(Frame.ICONIFIED);
    }

    private static void alternarMaximizado(Window ventana) {
        if (!(ventana instanceof Frame frame)) return;
        int estado = frame.getExtendedState();
        frame.setExtendedState((estado & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH
                ? Frame.NORMAL : estado | Frame.MAXIMIZED_BOTH);
    }

    private static void cerrar(Window ventana) {
        if (ventana != null) ventana.dispatchEvent(new WindowEvent(ventana, WindowEvent.WINDOW_CLOSING));
    }
}
