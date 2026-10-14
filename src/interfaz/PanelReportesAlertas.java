package interfaz;

import static modelo.Main.*;
import modelo.Alerta;
import modelo.Conductor;
import negocio.GestorConductores;
import negocio.GestorEventos;
import negocio.GestorCursos;
import negocio.GestorReportes;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel para reportes, alertas y estadísticas.
 * Muestra información general, reportes individuales y alertas de riesgo.
 */
public class PanelReportesAlertas extends JPanel {

    private GestorConductores gestorConductores;
    private GestorEventos gestorEventos;
    private GestorCursos gestorCursos;
    private GestorReportes gestorReportes;
    private VentanaPrincipal ventanaPrincipal;

    // Componentes
    private JTextField txtBuscarLicencia;
    private JTextArea txtAreaResultado;
    private JButton btnReporteIndividual;
    private JButton btnReporteEstadistico;
    private JButton btnInfraccionMasComun;
    private JButton btnAlertasCaducidad;
    private JButton btnAlertaRiesgo;

    public PanelReportesAlertas(GestorConductores gestorConductores, GestorEventos gestorEventos,
                               GestorCursos gestorCursos, GestorReportes gestorReportes,
                               VentanaPrincipal ventanaPrincipal) {
        this.gestorConductores = gestorConductores;
        this.gestorEventos = gestorEventos;
        this.gestorCursos = gestorCursos;
        this.gestorReportes = gestorReportes;
        this.ventanaPrincipal = ventanaPrincipal;

        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Panel de controles
        JPanel panelControles = crearPanelControles();
        add(panelControles, BorderLayout.NORTH);

        // Panel de resultados
        JPanel panelResultados = crearPanelResultados();
        add(panelResultados, BorderLayout.CENTER);
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // Búsqueda para reportes individuales
        JPanel panelBusqueda = new JPanel(new BorderLayout(10, 10));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "BÚSQUEDA PARA REPORTES INDIVIDUALES", 0, 0, new Font("Arial", Font.BOLD, 12)));
        panelBusqueda.setBackground(Color.WHITE);

        JLabel lbl = new JLabel("Licencia:");
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setPreferredSize(new Dimension(80, 30));

        txtBuscarLicencia = new JTextField();
        txtBuscarLicencia.setFont(new Font("Arial", Font.PLAIN, 11));
        txtBuscarLicencia.setToolTipText("Ingrese la licencia completa. Ejemplo: B-001");

        JPanel panelEntrada = new JPanel(new BorderLayout(10, 0));
        panelEntrada.setBackground(Color.WHITE);
        panelEntrada.add(lbl, BorderLayout.WEST);
        panelEntrada.add(txtBuscarLicencia, BorderLayout.CENTER);
        panelBusqueda.add(panelEntrada, BorderLayout.CENTER);

        panel.add(panelBusqueda, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 15, 10));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "OPCIONES DE REPORTES Y ALERTAS", 0, 0, new Font("Arial", Font.BOLD, 12)));

        // Lado izquierdo: reportes individuales
        JPanel panelIndividuales = new JPanel(new GridLayout(4, 1, 8, 8));
        panelIndividuales.setBackground(Color.WHITE);
        panelIndividuales.setBorder(BorderFactory.createTitledBorder("Individuales"));

        btnReporteIndividual = crearBoton("Reporte Individual");
        btnReporteIndividual.addActionListener(e -> generarReporteIndividual());
        panelIndividuales.add(btnReporteIndividual);

        btnAlertaRiesgo = crearBoton("Alerta de Riesgo Individual");
        btnAlertaRiesgo.addActionListener(e -> generarAlertaRiesgo());
        panelIndividuales.add(btnAlertaRiesgo);

        JButton btnInfraccionIndividual = crearBoton("Infracción Más Común Individual");
        btnInfraccionIndividual.addActionListener(e -> mostrarInfraccionMasComunIndividual());
        panelIndividuales.add(btnInfraccionIndividual);

        JButton btnBonificacion = crearBoton("Aplicar +5 pts Individual");
        btnBonificacion.addActionListener(e -> aplicarBonificacion());
        panelIndividuales.add(btnBonificacion);

        // Lado derecho: reportes generales o grupales
        JPanel panelGenerales = new JPanel(new GridLayout(4, 1, 8, 8));
        panelGenerales.setBackground(Color.WHITE);
        panelGenerales.setBorder(BorderFactory.createTitledBorder("Generales / Grupales"));

        btnReporteEstadistico = crearBoton("Reporte Estadístico General");
        btnReporteEstadistico.addActionListener(e -> generarReporteEstadistico());
        panelGenerales.add(btnReporteEstadistico);

        btnInfraccionMasComun = crearBoton("Infracción Más Común General");
        btnInfraccionMasComun.addActionListener(e -> mostrarInfraccionMasComun());
        panelGenerales.add(btnInfraccionMasComun);

        btnAlertasCaducidad = crearBoton("Alertas de Caducidad General");
        btnAlertasCaducidad.addActionListener(e -> mostrarAlertasCaducidad());
        panelGenerales.add(btnAlertasCaducidad);

        JButton btnMejorCurso = crearBoton("Reporte Mejor Curso");
        btnMejorCurso.addActionListener(e -> mostrarReporteMejorCurso());
        panelGenerales.add(btnMejorCurso);

        panelBotones.add(panelIndividuales);
        panelBotones.add(panelGenerales);

        panel.add(panelBotones, BorderLayout.CENTER);

        JButton btnLimpiar = crearBoton("Limpiar Resultados");
        btnLimpiar.addActionListener(e -> limpiarCampos());
        panel.add(btnLimpiar, BorderLayout.SOUTH);

        return panel;
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.PLAIN, 11));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel crearPanelResultados() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "RESULTADOS", 0, 0, new Font("Arial", Font.BOLD, 12)));
        panel.setBackground(Color.WHITE);

        txtAreaResultado = new JTextArea();
        txtAreaResultado.setEditable(false);
        txtAreaResultado.setFont(new Font("Courier New", Font.PLAIN, 11));
        txtAreaResultado.setLineWrap(true);
        txtAreaResultado.setWrapStyleWord(true);
        txtAreaResultado.setMargin(new Insets(10, 10, 10, 10));
        txtAreaResultado.setBackground(new Color(245, 245, 245));
        txtAreaResultado.setForeground(new Color(40, 44, 52));

        JScrollPane scrollPane = new JScrollPane(txtAreaResultado);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void generarReporteIndividual() {
        try {
            String licencia = txtBuscarLicencia.getText().trim();
            if (licencia.isEmpty()) {
                mostrarError("Ingrese un número de licencia.");
                return;
            }

            Conductor c = gestorConductores.obtenerConductorObligatorio(licencia);
            String reporte = gestorReportes.generarReporteIndividual(c);

            txtAreaResultado.setText(reporte);

        } catch (ConductorNoEncontradoException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void generarReporteEstadistico() {
        try {
            List<Conductor> conductores = gestorConductores.listarTodos();
            String reporte = gestorReportes.generarReporteGeneral(conductores);

            txtAreaResultado.setText(reporte);

        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void mostrarInfraccionMasComun() {
        try {
            String resultado = gestorConductores.estadisticaInfraccionMasComun();
            txtAreaResultado.setText(resultado);

        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void mostrarAlertasCaducidad() {
        try {
            List<Conductor> alertas = gestorConductores.obtenerAlertasCaducidad(30);

            if (alertas.isEmpty()) {
                txtAreaResultado.setText("No hay alertas de caducidad de licencia en los próximos 30 días.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("========================================\n");
            sb.append(" ALERTAS DE CADUCIDAD (próximos 30 días) \n");
            sb.append("========================================\n\n");

            for (Conductor c : alertas) {
                sb.append("Conductor: ").append(c.getNombreCompleto()).append("\n");
                sb.append("Licencia: ").append(c.getLicencia().getNumero()).append("\n");
                sb.append("Categoría: ").append(c.getLicencia().getCategoria()).append("\n");
                sb.append("Vencimiento: ").append(c.getLicencia().getFechaVencimiento()).append("\n");

                if (c.getLicencia().estaVencida()) {
                    sb.append("Estado: VENCIDA\n");
                } else {
                    sb.append("Estado: PRÓXIMA A VENCER\n");
                }

                sb.append("───────────────────────────────\n\n");
            }

            sb.append("========================================\n");
            sb.append("Total de alertas: ").append(alertas.size()).append("\n");

            txtAreaResultado.setText(sb.toString());

        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void generarAlertaRiesgo() {
        try {
            String licencia = txtBuscarLicencia.getText().trim();
            if (licencia.isEmpty()) {
                mostrarError("Ingrese un número de licencia.");
                return;
            }

            Conductor c = gestorConductores.obtenerConductorObligatorio(licencia);
            Alerta alerta = Alerta.generarAlerta(c);

            StringBuilder sb = new StringBuilder();
            sb.append("========================================\n");
            sb.append(" ALERTA DE RIESGO \n");
            sb.append("========================================\n\n");
            sb.append("Conductor: ").append(c.getNombreCompleto()).append("\n");
            sb.append("Puntaje: ").append(String.format("%.1f", c.getPuntajeActual())).append(" pts\n\n");
            sb.append("Nivel de Alerta: ").append(alerta.getNivel()).append("\n");
            sb.append("Mensaje: ").append(alerta.getMensaje()).append("\n\n");
            sb.append("========================================\n");

            txtAreaResultado.setText(sb.toString());

        } catch (ConductorNoEncontradoException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }



    private void mostrarInfraccionMasComunIndividual() {
        try {
            String licencia = txtBuscarLicencia.getText().trim();
            if (licencia.isEmpty()) {
                mostrarError("Ingrese un número de licencia.");
                return;
            }
            Conductor c = gestorConductores.obtenerConductorObligatorio(licencia);
            if (c.getInfracciones().isEmpty()) {
                txtAreaResultado.setText("El conductor " + c.getNombreCompleto() + " no tiene infracciones registradas.");
                return;
            }

            java.util.Map<String, Integer> conteo = new java.util.HashMap<>();
            int total = 0;
            for (modelo.Infraccion inf : c.getInfracciones()) {
                String nombre = inf.getTipo().getNombre();
                conteo.put(nombre, conteo.getOrDefault(nombre, 0) + 1);
                total++;
            }
            String masComun = "";
            int max = 0;
            for (java.util.Map.Entry<String, Integer> entry : conteo.entrySet()) {
                if (entry.getValue() > max) {
                    masComun = entry.getKey();
                    max = entry.getValue();
                }
            }
            double porcentaje = max * 100.0 / total;
            txtAreaResultado.setText("INFRACCIÓN MÁS COMÚN INDIVIDUAL\n\n"
                    + "Conductor: " + c.getNombreCompleto() + "\n"
                    + "Licencia: " + c.getLicencia().getNumero() + "\n"
                    + "Infracción más común: " + masComun + "\n"
                    + "Cantidad: " + max + " veces\n"
                    + "Porcentaje dentro de su historial: " + String.format("%.1f", porcentaje) + "%");
        } catch (ConductorNoEncontradoException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void mostrarReporteMejorCurso() {
        try {
            txtAreaResultado.setText(gestorCursos.reporteMejorCurso(gestorConductores.listarTodos()));
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void aplicarBonificacion() {
        try {
            String licencia = txtBuscarLicencia.getText().trim();
            if (licencia.isEmpty()) {
                gestorConductores.aplicarBonificacionATodos();
                txtAreaResultado.setText("Bonificacion revisada para todos los conductores.\n"
                        + "Regla aplicada: 5 puntos por cada anio completo sin infracciones, sin pasar de 100 puntos.");
            } else {
                int puntos = gestorConductores.aplicarBonificacionBuenComportamiento(licencia);
                Conductor c = gestorConductores.obtenerConductorObligatorio(licencia);
                txtAreaResultado.setText("Bonificacion aplicada/revisada para: " + c.getNombreCompleto()
                        + "\nAnios sin infracciones: " + c.calcularAniosSinInfracciones()
                        + "\nBonificacion: +" + puntos + " pts"
                        + "\nPuntaje actual: " + String.format("%.1f", c.getPuntajeActual()) + " pts");
            }
            ventanaPrincipal.actualizarTodosPaneles();
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    public void limpiarCampos() {
        txtBuscarLicencia.setText("");
        txtAreaResultado.setText("");
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

