package interfaz;

import javax.swing.*;
import java.awt.*;

/**
 * Utilidades de estilo para asegurar buen contraste en la interfaz gráfica.
 */
public final class EstilosInterfaz {
    private EstilosInterfaz() {
    }

    public static void configurarComboBox(JComboBox<?> combo) {
        combo.setBackground(Color.WHITE);
        combo.setForeground(Color.BLACK);
        combo.setOpaque(true);
        combo.setFont(new Font("Arial", Font.PLAIN, 11));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    c.setBackground(new Color(52, 152, 219));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                setOpaque(true);
                return c;
            }
        });
    }

    public static void configurarTabla(JTable tabla) {
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(Color.BLACK);
        tabla.setSelectionBackground(new Color(52, 152, 219));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.getTableHeader().setBackground(new Color(240, 240, 240));
        tabla.getTableHeader().setForeground(Color.BLACK);
    }
}
