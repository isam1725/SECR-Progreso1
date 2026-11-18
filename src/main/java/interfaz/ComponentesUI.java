package interfaz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Componentes visuales comunes de los módulos M2 y M3 (título, botones, barra de estado). */
final class ComponentesUI {

    static final Color VINO = new Color(140, 29, 64);
    static final Color AZUL = new Color(31, 78, 140);
    static final Color FONDO = new Color(250, 247, 245);
    static final Color TEXTO = new Color(29, 26, 36);
    static final Color GRIS = new Color(95, 90, 100);
    static final Color OK_FONDO = new Color(226, 242, 230), OK_TEXTO = new Color(25, 100, 50);
    static final Color ERROR_FONDO = new Color(252, 228, 228), ERROR_TEXTO = new Color(150, 20, 30);
    static final Color INFO_FONDO = new Color(232, 240, 251), INFO_TEXTO = AZUL;

    private ComponentesUI() { }

    static JPanel encabezado(String titulo, String subtitulo) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setBackground(FONDO);
        p.setBorder(new EmptyBorder(4, 4, 8, 4));
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Arial", Font.BOLD, 18));
        t.setForeground(VINO);
        JLabel s = new JLabel(subtitulo);
        s.setFont(new Font("Arial", Font.PLAIN, 12));
        s.setForeground(GRIS);
        p.add(t, BorderLayout.NORTH);
        p.add(s, BorderLayout.SOUTH);
        return p;
    }

    static JButton boton(String texto, boolean principal) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setBackground(principal ? VINO : new Color(232, 226, 222));
        b.setForeground(principal ? Color.WHITE : TEXTO);
        b.setBorder(new EmptyBorder(7, 12, 7, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        l.setForeground(TEXTO);
        return l;
    }

    static JTextField campo(int columnas) {
        JTextField f = new JTextField(columnas);
        f.setFont(new Font("Arial", Font.PLAIN, 12));
        return f;
    }

    static JPanel tarjeta(String titulo) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220, 212, 206)), titulo,
                        0, 0, new Font("Arial", Font.BOLD, 12), VINO),
                new EmptyBorder(6, 8, 8, 8)));
        return p;
    }

    /** Barra de estado: muestra el resultado de la última operación o el error con su motivo. */
    static final class BarraEstado extends JLabel {
        BarraEstado() {
            setOpaque(true);
            setFont(new Font("Arial", Font.PLAIN, 12));
            setBorder(new EmptyBorder(8, 10, 8, 10));
            info("Listo.");
        }
        void ok(String m) { pintar("Correcto: " + m, OK_FONDO, OK_TEXTO); }
        void error(String m) { pintar("Error: " + m, ERROR_FONDO, ERROR_TEXTO); }
        void info(String m) { pintar(m, INFO_FONDO, INFO_TEXTO); }
        private void pintar(String m, Color f, Color t) { setText(m); setBackground(f); setForeground(t); }
    }

    static void estilizarTabla(JTable tabla) {
        EstilosInterfaz.configurarTabla(tabla);
        tabla.setRowHeight(24);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(false);
    }
}
