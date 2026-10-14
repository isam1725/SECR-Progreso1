package interfaz;

import static modelo.Main.*;
import modelo.Conductor;
import modelo.Licencia;
import negocio.GestorConductores;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Panel para gestión de licencias.
 * Permite búsqueda, visualización y renovación de licencias.
 */
public class PanelLicencias extends JPanel {

    private GestorConductores gestorConductores;
    private VentanaPrincipal ventanaPrincipal;

    // Componentes
    private JTextField txtBuscarLicencia;
    private JLabel lblNombre;
    private JLabel lblEstado;
    private JLabel lblPuntaje;
    private JLabel lblNumeroLicencia;
    private JLabel lblCategoria;
    private JLabel lblFechaEmision;
    private JLabel lblFechaVencimiento;
    private JLabel lblEstadoLicencia;
    private JLabel lblDiasRestantes;
    private JButton btnRenovar;
    private Conductor conductorActual;

    public PanelLicencias(GestorConductores gestorConductores, VentanaPrincipal ventanaPrincipal) {
        this.gestorConductores = gestorConductores;
        this.ventanaPrincipal = ventanaPrincipal;

        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // Panel superior de búsqueda
        JPanel panelBusqueda = crearPanelBusqueda();
        add(panelBusqueda, BorderLayout.NORTH);

        // Panel central con información
        JPanel panelInfo = crearPanelInformacion();
        add(panelInfo, BorderLayout.CENTER);

        // Panel inferior con botones
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "BÚSQUEDA DE CONDUCTOR", 0, 0, new Font("Arial", Font.BOLD, 12)));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 80));

        JLabel lbl = new JLabel("Número de Licencia:");
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));

        txtBuscarLicencia = new JTextField();
        txtBuscarLicencia.setFont(new Font("Arial", Font.PLAIN, 11));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Arial", Font.PLAIN, 11));
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.setPreferredSize(new Dimension(100, 30));
        btnBuscar.addActionListener(e -> buscarConductor());

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setFont(new Font("Arial", Font.PLAIN, 11));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.setPreferredSize(new Dimension(100, 30));
        btnLimpiar.addActionListener(e -> limpiarCampos());

        JPanel panelEntrada = new JPanel(new BorderLayout(5, 0));
        panelEntrada.setBackground(Color.WHITE);
        panelEntrada.add(lbl, BorderLayout.WEST);
        panelEntrada.add(txtBuscarLicencia, BorderLayout.CENTER);

        JPanel panelBotonesBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelBotonesBusqueda.setBackground(Color.WHITE);
        panelBotonesBusqueda.add(btnBuscar);
        panelBotonesBusqueda.add(btnLimpiar);

        panel.add(panelEntrada, BorderLayout.CENTER);
        panel.add(panelBotonesBusqueda, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelInformacion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "INFORMACIÓN DE LA LICENCIA", 0, 0, new Font("Arial", Font.BOLD, 12)));
        panel.setBackground(Color.WHITE);

        // Sección: Conductor
        panel.add(crearEtiqueta("DATOS DEL CONDUCTOR"));
        panel.add(crearFila("Nombre:", lblNombre = crearValor("---")));
        panel.add(crearFila("Estado:", lblEstado = crearValor("---")));
        panel.add(crearFila("Puntaje Actual:", lblPuntaje = crearValor("--- pts")));

        panel.add(Box.createVerticalStrut(15));

        // Sección: Licencia
        panel.add(crearEtiqueta("DATOS DE LA LICENCIA"));
        panel.add(crearFila("Número de Licencia:", lblNumeroLicencia = crearValor("---")));
        panel.add(crearFila("Categoría:", lblCategoria = crearValor("---")));
        panel.add(crearFila("Fecha de Emisión:", lblFechaEmision = crearValor("---")));
        panel.add(crearFila("Fecha de Vencimiento:", lblFechaVencimiento = crearValor("---")));
        panel.add(crearFila("Estado de Licencia:", lblEstadoLicencia = crearValor("---")));
        panel.add(crearFila("Días Restantes:", lblDiasRestantes = crearValor("---")));

        panel.add(Box.createVerticalGlue());

        JPanel panelConScroll = new JPanel(new BorderLayout());
        panelConScroll.setBackground(Color.WHITE);
        panelConScroll.add(new JScrollPane(panel), BorderLayout.CENTER);

        return panelConScroll;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(Color.WHITE);

        btnRenovar = new JButton("Renovar Licencia");
        btnRenovar.setFont(new Font("Arial", Font.BOLD, 12));
        btnRenovar.setPreferredSize(new Dimension(180, 40));
        btnRenovar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRenovar.setEnabled(false);
        btnRenovar.addActionListener(e -> renovarLicencia());

        panel.add(btnRenovar);

        return panel;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(new Color(40, 44, 52));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        return lbl;
    }

    private JLabel crearValor(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(60, 60, 60));
        return lbl;
    }

    private JPanel crearFila(String etiqueta, JLabel valor) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Arial", Font.BOLD, 11));
        lbl.setPreferredSize(new Dimension(180, 30));
        lbl.setForeground(new Color(40, 44, 52));

        panel.add(lbl, BorderLayout.WEST);
        panel.add(valor, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);

        return panel;
    }

    private void buscarConductor() {
        try {
            String licencia = txtBuscarLicencia.getText().trim();
            if (licencia.isEmpty()) {
                mostrarError("Ingrese un número de licencia.");
                return;
            }

            Conductor c = gestorConductores.obtenerConductorObligatorio(licencia);
            conductorActual = c;

            // Cargar información
            Licencia lic = c.getLicencia();

            lblNombre.setText(c.getNombreCompleto());
            lblEstado.setText(c.getEstado());
            lblPuntaje.setText(String.format("%.1f pts", c.getPuntajeActual()));
            lblNumeroLicencia.setText(lic.getNumero());
            lblCategoria.setText(lic.getCategoria());
            lblFechaEmision.setText(lic.getFechaEmision().toString());
            lblFechaVencimiento.setText(lic.getFechaVencimiento().toString());

            // Estado de licencia
            if (lic.estaVencida()) {
                lblEstadoLicencia.setText("VENCIDA");
                lblEstadoLicencia.setForeground(new Color(231, 76, 60));
            } else {
                lblEstadoLicencia.setText("VIGENTE");
                lblEstadoLicencia.setForeground(new Color(46, 204, 113));
            }

            // Días restantes
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), lic.getFechaVencimiento());
            if (diasRestantes > 0) {
                lblDiasRestantes.setText(diasRestantes + " días");
                lblDiasRestantes.setForeground(new Color(46, 204, 113));
            } else if (diasRestantes == 0) {
                lblDiasRestantes.setText("Vence hoy");
                lblDiasRestantes.setForeground(new Color(241, 196, 15));
            } else {
                lblDiasRestantes.setText("Vencida hace " + Math.abs(diasRestantes) + " días");
                lblDiasRestantes.setForeground(new Color(231, 76, 60));
            }

            // Habilitar botón renovar
            btnRenovar.setEnabled(c.puedeRenovarLicencia());

        } catch (ConductorNoEncontradoException e) {
            mostrarError(e.getMessage());
            limpiarCampos();
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void renovarLicencia() {
        try {
            if (conductorActual == null) {
                mostrarError("Seleccione un conductor primero.");
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Renovar licencia de " + conductorActual.getNombreCompleto() + "?",
                    "Confirmar renovación",
                    JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                gestorConductores.renovarLicencia(conductorActual.getLicencia().getNumero());

                mostrarMensaje("Licencia renovada exitosamente.\n" +
                        "Nueva fecha de vencimiento: " +
                        conductorActual.getLicencia().getFechaVencimiento());

                // Actualizar información
                buscarConductor();
                ventanaPrincipal.actualizarTodosPaneles();
            }

        } catch (EstadoInvalidoException | PuntajeNoValidoException e) {
            mostrarError(e.getMessage());
        } catch (SecrException e) {
            mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    public void limpiarCampos() {
        txtBuscarLicencia.setText("");
        lblNombre.setText("---");
        lblEstado.setText("---");
        lblPuntaje.setText("--- pts");
        lblNumeroLicencia.setText("---");
        lblCategoria.setText("---");
        lblFechaEmision.setText("---");
        lblFechaVencimiento.setText("---");
        lblEstadoLicencia.setText("---");
        lblDiasRestantes.setText("---");
        btnRenovar.setEnabled(false);
        conductorActual = null;
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

