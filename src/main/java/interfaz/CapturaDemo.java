package interfaz;

import util.SistemaSECR;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Genera las capturas de la demostración técnica ejecutando la aplicación real: abre la ventana,
 * llena los formularios de M2 y M3 como lo haría un usuario, pulsa los botones y guarda una imagen
 * de la ventana después de cada paso (docs/capturas/*.png).
 * Ejecutar: java -cp out interfaz.CapturaDemo [carpetaSalida]
 */
public class CapturaDemo {

    private static VentanaPrincipal v;
    private static File carpeta;

    public static void main(String[] args) throws Exception {
        carpeta = new File(args.length > 0 ? args[0] : "docs/capturas");
        carpeta.mkdirs();
        SwingUtilities.invokeAndWait(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ignored) { }
            java.io.PrintStream o = System.out;
            System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));
            SistemaSECR s = new SistemaSECR();
            s.cargarDatosPrueba();
            System.setOut(o);
            v = new VentanaPrincipal(s);
            v.setSize(1366, 800);
            v.setVisible(true);
        });
        PanelInfracciones m2 = v.getPanelInfracciones();
        PanelRiesgo m3 = v.getPanelRiesgo();

        // ------------------------------------------------ M2
        paso(() -> v.mostrarPanel("INFRACCIONES"));
        paso(() -> {
            m2.txtLicencia.setText("B-030405"); m2.buscarConductor();
            m2.cbxTipo.setSelectedIndex(1); m2.txtFecha.setText("10/11/2026");
            m2.txtUbicacion.setText("Av. Amazonas y Colón"); m2.spnGravedad.setValue(2);
            m2.txtDescripcion.setText("Uso del celular en semáforo");
            m2.registrar();
        });
        guardar("01_M2_registro_normal");
        paso(() -> { m2.txtLicencia.setText("Z-999999"); m2.registrar(); });
        guardar("02_M2_error_licencia_inexistente");
        paso(() -> { m2.txtLicencia.setText("B-050607"); m2.txtFecha.setText("31/02/2026"); m2.registrar(); });
        guardar("03_M2_error_fecha_invalida");
        paso(() -> { m2.txtFecha.setText("12/11/2026"); m2.txtUbicacion.setText("   "); m2.registrar(); });
        guardar("04_M2_error_ubicacion_vacia");
        paso(() -> {
            m2.txtDesde.setText("01/09/2026"); m2.txtHasta.setText("31/10/2026"); m2.txtFiltroLicencia.setText("");
            m2.cbxOrden.setSelectedIndex(1); m2.consultarRango();
        });
        guardar("05_M2_consulta_rango_ordenada");
        paso(() -> { m2.txtDesde.setText("31/10/2026"); m2.txtHasta.setText("01/09/2026"); m2.consultarRango(); });
        guardar("06_M2_error_rango_invertido");
        paso(() -> m2.deshacer());
        guardar("07_M2_deshacer_pila");
        paso(() -> m2.deshacer());
        guardar("08_M2_deshacer_pila_vacia");
        paso(() -> {
            m2.txtLicencia.setText("C-020304"); m2.buscarConductor();
            m2.cbxTipo.setSelectedIndex(5); m2.txtFecha.setText("12/11/2026");
            m2.txtUbicacion.setText("Av. Simón Bolívar"); m2.spnGravedad.setValue(4); m2.txtDescripcion.setText("");
            m2.registrar();
        });
        guardar("09_M2_limite_puntaje_cero");
        paso(() -> { m2.cbxTipo.setSelectedIndex(0); m2.spnGravedad.setValue(1); m2.registrar(); });
        guardar("10_M2_rechazo_conductor_inactivo");
        paso(() -> m2.deshacer());
        paso(() -> { m2.pestanas.setSelectedIndex(1); m2.txtHistLicencia.setText("E-040506"); m2.cargarHistorial(); m2.moverHistorial(true); });
        guardar("11_M2_historial_lista_doble");
        paso(() -> m2.pestanas.setSelectedIndex(0));

        // ------------------------------------------------ M3
        paso(() -> v.mostrarPanel("RIESGO"));
        guardar("12_M3_top5_semaforo");
        paso(() -> { m3.txtK.setText("0"); m3.verTop(); });
        guardar("13_M3_error_k_invalido");
        paso(() -> { m3.txtK.setText("50"); m3.verTop(); });
        guardar("14_M3_limite_k_mayor_que_n");
        paso(() -> { m3.cbxNivel.setSelectedIndex(0); m3.verNivel(); });
        guardar("15_M3_filtro_semaforo_rojo");
        paso(() -> { m3.txtLicencia.setText("b-050607"); m3.consultarPosicion(); });
        guardar("16_M3_posicion_conductor");
        paso(() -> { m3.txtLicencia.setText("B-999999"); m3.consultarPosicion(); });
        guardar("17_M3_error_licencia_inexistente");

        // ------------------------------------------------ Integración M2 → M3
        paso(() -> {
            v.mostrarPanel("INFRACCIONES");
            m2.txtLicencia.setText("B-050607"); m2.buscarConductor();
            m2.cbxTipo.setSelectedIndex(5); m2.txtFecha.setText("14/11/2026");
            m2.txtUbicacion.setText("Av. 10 de Agosto"); m2.spnGravedad.setValue(4); m2.txtDescripcion.setText("");
            m2.registrar();
        });
        guardar("18_M2_M3_integracion_registro");
        paso(() -> { v.mostrarPanel("RIESGO"); m3.txtK.setText("5"); m3.verTop(); });
        guardar("19_M3_ranking_actualizado");

        SwingUtilities.invokeAndWait(() -> v.dispose());
        System.out.println("Capturas guardadas en " + carpeta.getAbsolutePath());
        System.exit(0);
    }

    private static void paso(Runnable r) throws Exception {
        SwingUtilities.invokeAndWait(r);
        Thread.sleep(250);
    }

    private static void guardar(String nombre) throws Exception {
        BufferedImage[] img = new BufferedImage[1];
        SwingUtilities.invokeAndWait(() -> {
            JRootPane root = v.getRootPane();
            img[0] = new BufferedImage(root.getWidth(), root.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img[0].createGraphics();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            root.paint(g);
            g.dispose();
        });
        ImageIO.write(img[0], "png", new File(carpeta, nombre + ".png"));
    }
}
