package interfaz;

import util.SistemaSECR;
import negocio.GestorConductores;
import negocio.GestorEventos;
import modelo.Conductor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel de inicio/bienvenida del sistema SECR.
 * Muestra estadísticas generales y indicadores de estado.
 */
public class PanelInicio extends JPanel {

    private SistemaSECR sistema;
    private GestorConductores gestorConductores;
    private GestorEventos gestorEventos;
    private VentanaPrincipal ventanaPrincipal;

    private JLabel lblTotalConductores;
    private JLabel lblConductoresActivos;
    private JLabel lblConductoresInactivos;
    private JLabel lblConductoresConEventos;
    private JLabel lblPromedioPuntaje;
    private JLabel lblTotalEventos;

    public PanelInicio(SistemaSECR sistema, GestorConductores gestorConductores,
                       GestorEventos gestorEventos, VentanaPrincipal ventanaPrincipal) {
        this.sistema = sistema;
        this.gestorConductores = gestorConductores;
        this.gestorEventos = gestorEventos;
        this.ventanaPrincipal = ventanaPrincipal;

        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Panel superior con título
        JPanel panelTitulo = crearPanelTitulo();
        add(panelTitulo, BorderLayout.NORTH);

        // Panel central con estadísticas
        JPanel panelEstadisticas = crearPanelEstadisticas();
        add(panelEstadisticas, BorderLayout.CENTER);

        // Panel inferior con botón
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);

        // Actualizar datos iniciales
        actualizarDatos();
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("BIENVENIDO AL SISTEMA SECR");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(new Color(40, 44, 52));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Sistema de Evaluación de Conducción Responsable");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(120, 120, 120));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitulo);
        panel.add(Box.createVerticalStrut(10));

        JLabel descripcion = new JLabel("<html><center>Este sistema le permite gestionar conductores, registrar eventos de conducción, " +
                "emitir y renovar licencias, así como realizar cursos de recuperación de puntos.</center></html>");
        descripcion.setFont(new Font("Arial", Font.PLAIN, 12));
        descripcion.setForeground(Color.GRAY);
        descripcion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(descripcion);

        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 20, 20));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "INDICADORES GENERALES", 0, 0, new Font("Arial", Font.BOLD, 14)));
        panel.setBackground(Color.WHITE);

        // Total de conductores
        JPanel pnl1 = crearIndicador("Total de Conductores:", "0", new Color(52, 152, 219));
        lblTotalConductores = obtenerLabelDelIndicador(pnl1);
        panel.add(pnl1);

        // Conductores activos
        JPanel pnl2 = crearIndicador("Conductores Activos:", "0", new Color(46, 204, 113));
        lblConductoresActivos = obtenerLabelDelIndicador(pnl2);
        panel.add(pnl2);

        // Conductores inactivos
        JPanel pnl3 = crearIndicador("Conductores Inactivos:", "0", new Color(231, 76, 60));
        lblConductoresInactivos = obtenerLabelDelIndicador(pnl3);
        panel.add(pnl3);

        // Conductores con eventos
        JPanel pnl4 = crearIndicador("Conductores con Eventos:", "0", new Color(241, 196, 15));
        lblConductoresConEventos = obtenerLabelDelIndicador(pnl4);
        panel.add(pnl4);

        // Promedio de puntaje
        JPanel pnl5 = crearIndicador("Promedio de Puntaje:", "0.0", new Color(155, 89, 182));
        lblPromedioPuntaje = obtenerLabelDelIndicador(pnl5);
        panel.add(pnl5);

        // Total de eventos
        JPanel pnl6 = crearIndicador("Total de Eventos Registrados:", "0", new Color(52, 73, 94));
        lblTotalEventos = obtenerLabelDelIndicador(pnl6);
        panel.add(pnl6);

        return panel;
    }

    private JLabel obtenerLabelDelIndicador(JPanel panel) {
        // El segundo componente del panel indicador es el JLabel con el valor
        for (int i = 0; i < panel.getComponentCount(); i++) {
            if (panel.getComponent(i) instanceof JLabel && i > 0) {
                return (JLabel) panel.getComponent(i);
            }
        }
        return null;
    }

    private JPanel crearIndicador(String etiqueta, String valor, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createLineBorder(color, 2));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Arial", Font.PLAIN, 12));
        lblEtiqueta.setForeground(Color.DARK_GRAY);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.BOLD, 24));
        lblValor.setForeground(color);

        panel.add(lblEtiqueta);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblValor);

        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);

        JButton btnActualizar = new JButton("Actualizar Indicadores");
        btnActualizar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnActualizar.setPreferredSize(new Dimension(180, 40));
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnActualizar.addActionListener(e -> actualizarDatos());

        panel.add(btnActualizar);
        return panel;
    }

    public void actualizarDatos() {
        List<Conductor> conductores = gestorConductores.listarTodos();

        // Total de conductores
        lblTotalConductores.setText(String.valueOf(conductores.size()));

        // Conductores activos
        long activos = conductores.stream().filter(c -> "Activo".equalsIgnoreCase(c.getEstado())).count();
        lblConductoresActivos.setText(String.valueOf(activos));

        // Conductores inactivos
        long inactivos = conductores.stream().filter(c -> "Inactivo".equalsIgnoreCase(c.getEstado())).count();
        lblConductoresInactivos.setText(String.valueOf(inactivos));

        // Conductores con eventos
        long conEventos = gestorEventos.obtenerConductoresConEventos(gestorConductores).size();
        lblConductoresConEventos.setText(String.valueOf(conEventos));

        // Promedio de puntaje
        if (conductores.isEmpty()) {
            lblPromedioPuntaje.setText("0.0");
        } else {
            double promedio = conductores.stream().mapToDouble(Conductor::getPuntajeActual).average().orElse(0.0);
            lblPromedioPuntaje.setText(String.format("%.1f", promedio));
        }

        // Total de eventos
        int totalEventos = (int) conductores.stream().mapToLong(c -> c.getInfracciones().size()).sum();
        lblTotalEventos.setText(String.valueOf(totalEventos));
    }
}

