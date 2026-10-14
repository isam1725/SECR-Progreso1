package interfaz;

import util.SistemaSECR;
import negocio.GestorConductores;
import negocio.GestorEventos;
import negocio.GestorCursos;
import negocio.GestorReportes;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de la aplicación SECR.
 * Utiliza CardLayout para cambiar entre paneles.
 * Proporciona navegación mediante menú lateral o superior.
 */
public class VentanaPrincipal extends JFrame {

    private SistemaSECR sistema;
    private GestorConductores gestorConductores;
    private GestorEventos gestorEventos;
    private GestorCursos gestorCursos;
    private GestorReportes gestorReportes;

    private CardLayout cardLayout;
    private JPanel panelCentral;

    private PanelInicio panelInicio;
    private PanelConductores panelConductores;
    private PanelEventos panelEventos;
    private PanelLicencias panelLicencias;
    private PanelReportesAlertas panelReportes;
    private PanelRecuperacion panelRecuperacion;
    private PanelCatalogos panelCatalogos;

    public VentanaPrincipal(SistemaSECR sistema) {
        this.sistema = sistema;
        this.gestorConductores = sistema.getGestorConductores();
        this.gestorEventos = sistema.getGestorEventos();
        this.gestorCursos = sistema.getGestorCursos();
        this.gestorReportes = sistema.getGestorReportes();

        // Configuración de la ventana
        setTitle("SECR - Sistema de Evaluación de Conducción Responsable");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // Crear interfaz
        construirInterfaz();
    }

    private void construirInterfaz() {
        // Panel superior con título
        JPanel panelSuperior = crearPanelSuperior();

        // Panel izquierdo con menú de navegación
        JPanel panelIzquierdo = crearMenuNavegacion();

        // Panel central con CardLayout
        cardLayout = new CardLayout();
        panelCentral = new JPanel(cardLayout);

        // Crear todos los paneles
        panelInicio = new PanelInicio(sistema, gestorConductores, gestorEventos, this);
        panelConductores = new PanelConductores(gestorConductores, gestorEventos, this);
        panelEventos = new PanelEventos(gestorConductores, gestorEventos, this);
        panelLicencias = new PanelLicencias(gestorConductores, this);
        panelReportes = new PanelReportesAlertas(gestorConductores, gestorEventos, gestorCursos, gestorReportes, this);
        panelRecuperacion = new PanelRecuperacion(gestorConductores, gestorCursos, this);
        panelCatalogos = new PanelCatalogos(gestorCursos, gestorEventos, gestorConductores, this);

        // Agregar paneles al CardLayout
        panelCentral.add(panelInicio, "INICIO");
        panelCentral.add(panelConductores, "CONDUCTORES");
        panelCentral.add(panelEventos, "EVENTOS");
        panelCentral.add(panelLicencias, "LICENCIAS");
        panelCentral.add(panelReportes, "REPORTES");
        panelCentral.add(panelRecuperacion, "RECUPERACION");
        panelCentral.add(panelCatalogos, "CATALOGOS");

        // Configurar layout principal
        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(panelIzquierdo, BorderLayout.WEST);
        add(panelCentral, BorderLayout.CENTER);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 44, 52));
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(BorderFactory.createEtchedBorder());

        JLabel titulo = new JLabel("SECR - Sistema de Evaluación de Conducción Responsable");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        panel.add(titulo, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearMenuNavegacion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(60, 63, 65));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(BorderFactory.createEtchedBorder());

        // Crear botones del menú
        String[] opciones = {"Inicio", "Conductores", "Eventos", "Licencias", "Reportes y Alertas", "Recuperación de Puntos", "Catálogos", "Salir"};
        String[] paneles = {"INICIO", "CONDUCTORES", "EVENTOS", "LICENCIAS", "REPORTES", "RECUPERACION", "CATALOGOS"};

        for (int i = 0; i < opciones.length; i++) {
            JButton btn = crearBotonMenu(opciones[i]);
            int indice = i;

            btn.addActionListener(e -> {
                if (indice == opciones.length - 1) {
                    // Botón Salir
                    System.exit(0);
                } else {
                    // Cambiar panel
                    cardLayout.show(panelCentral, paneles[indice]);

                    // Actualizar datos del panel si es necesario
                    switch (indice) {
                        case 0 -> panelInicio.actualizarDatos();
                        case 1 -> panelConductores.actualizarDatos();
                        case 2 -> panelEventos.actualizarDatos();
                        case 3 -> panelLicencias.limpiarCampos();
                        case 4 -> panelReportes.limpiarCampos();
                        case 5 -> panelRecuperacion.actualizarDatos();
                        case 6 -> { }
                    }
                }
            });

            panel.add(btn);
            panel.add(Box.createVerticalStrut(10));
        }

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setBackground(new Color(245, 245, 245));
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(220, 235, 250));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(245, 245, 245));
            }
        });

        return btn;
    }

    /**
     * Método para actualizar datos en paneles específicos desde otros paneles
     */
    public void actualizarTodosPaneles() {
        panelInicio.actualizarDatos();
        panelConductores.actualizarDatos();
        panelEventos.actualizarDatos();
        // Recuperación se actualiza al abrir su pestaña para no borrar resultados recién generados.
    }
}

