package interfaz;

import util.SistemaSECR;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;

/**
 * Clase principal para la interfaz gráfica de SECR.
 * Punto de entrada para la aplicación con GUI.
 * Ejecutar: interfaz.Interfaz
 */
public class Interfaz {

    public static void main(String[] args) {
        // Ejecutar en el Event Dispatch Thread de Swing
        SwingUtilities.invokeLater(() -> {
            // Aplicar Look and Feel del sistema operativo
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si falla, usar el Look and Feel por defecto
                System.err.println("No se pudo aplicar el Look and Feel del sistema.");
            }

            // Mejorar contraste visual de listas, combos y selecciones
            configurarContraste();

            // Crear instancia única de SistemaSECR
            SistemaSECR sistema = new SistemaSECR();
            sistema.cargarDatosPrueba(); // Carga conductores y eventos preañadidos para demostrar el sistema.

            // Crear y mostrar la ventana principal
            VentanaPrincipal ventana = new VentanaPrincipal(sistema);
            ventana.setVisible(true);
        });
    }
    private static void configurarContraste() {
        UIManager.put("ComboBox.background", Color.WHITE);
        UIManager.put("ComboBox.foreground", Color.BLACK);
        UIManager.put("ComboBox.selectionBackground", new Color(52, 152, 219));
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("List.background", Color.WHITE);
        UIManager.put("List.foreground", Color.BLACK);
        UIManager.put("List.selectionBackground", new Color(52, 152, 219));
        UIManager.put("List.selectionForeground", Color.WHITE);
        UIManager.put("Table.selectionBackground", new Color(52, 152, 219));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", Color.BLACK);
    }

}

