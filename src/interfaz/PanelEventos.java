package interfaz;

import static modelo.Main.*;
import modelo.Conductor;
import modelo.EventoConduccion;
import negocio.GestorConductores;
import negocio.GestorEventos;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Panel para gestión de eventos e infracciones (CRUD).
 * Permite registrar eventos y listar todos los registrados.
 */
public class PanelEventos extends JPanel {

    private GestorConductores gestorConductores;
    private GestorEventos gestorEventos;
    private VentanaPrincipal ventanaPrincipal;
    private EventoTableModel modeloTabla;

    // Componentes del formulario
    private JComboBox<String> cbxConductor;
    private JComboBox<String> cbxTipoEvento;
    private JSpinner spnFecha;
    private JTextField txtUbicacion;
    private JSpinner spnGravedad;
    private JTextArea txtDescripcion;
    private JTable tablaEventos;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PanelEventos(GestorConductores gestorConductores, GestorEventos gestorEventos,
                       VentanaPrincipal ventanaPrincipal) {
        this.gestorConductores = gestorConductores;
        this.gestorEventos = gestorEventos;
        this.ventanaPrincipal = ventanaPrincipal;

        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Panel izquierdo con formulario
        JPanel panelFormulario = crearPanelFormulario();

        // Panel derecho con tabla
        JPanel panelTabla = crearPanelTabla();

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelFormulario, panelTabla);
        splitPane.setDividerLocation(420);
        splitPane.setResizeWeight(0.4);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "REGISTRO DE EVENTO / INFRACCIÓN", 0, 0, new Font("Arial", Font.BOLD, 12)));
        panel.setBackground(Color.WHITE);

        // Conductor
        panel.add(crearEtiqueta("DATOS DEL EVENTO"));
        panel.add(crearCampo("Conductor:", "cbx_conductor"));

        // Tipo de evento
        panel.add(crearCampo("Tipo de Evento:", "cbx_tipo"));

        // Fecha
        panel.add(crearCampoFecha("Fecha del Evento (dd/MM/yyyy):", "spn_fecha"));

        // Ubicación
        panel.add(crearCampoTexto("Ubicación:", "txt_ubicacion"));

        // Gravedad
        panel.add(crearCampoGravedad("Gravedad (1-5):", "spn_gravedad"));

        // Descripción
        panel.add(crearEtiquetaCampo("Descripción:"));
        txtDescripcion = new JTextArea(4, 20);
        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 11));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.add(scrollDesc);

        panel.add(Box.createVerticalStrut(15));

        // Botones
        JPanel panelBotones = crearPanelBotones();
        panel.add(panelBotones);

        panel.add(Box.createVerticalGlue());

        JPanel panelConScroll = new JPanel(new BorderLayout());
        panelConScroll.setBackground(Color.WHITE);
        panelConScroll.add(new JScrollPane(panel), BorderLayout.CENTER);

        return panelConScroll;
    }

    private JPanel crearCampo(String etiqueta, String id) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setPreferredSize(new Dimension(140, 30));
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));

        JComboBox<String> cbx = new JComboBox<>();
        EstilosInterfaz.configurarComboBox(cbx);

        if (id.equals("cbx_conductor")) {
            cbxConductor = cbx;
            cargarConductores();
        } else if (id.equals("cbx_tipo")) {
            cbxTipoEvento = cbx;
            cargarTiposEvento();
        }

        panel.add(lbl, BorderLayout.WEST);
        panel.add(cbx, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearCampoTexto(String etiqueta, String id) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setPreferredSize(new Dimension(140, 30));
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));

        JTextField txt = new JTextField();
        txt.setFont(new Font("Arial", Font.PLAIN, 11));

        if (id.equals("txt_ubicacion")) txtUbicacion = txt;

        panel.add(lbl, BorderLayout.WEST);
        panel.add(txt, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearCampoFecha(String etiqueta, String id) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setPreferredSize(new Dimension(140, 30));
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));

        JSpinner spn = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spn, "dd/MM/yyyy");
        spn.setEditor(editor);

        if (id.equals("spn_fecha")) spnFecha = spn;

        panel.add(lbl, BorderLayout.WEST);
        panel.add(spn, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearCampoGravedad(String etiqueta, String id) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setPreferredSize(new Dimension(140, 30));
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));

        JSpinner spn = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        if (id.equals("spn_gravedad")) spnGravedad = spn;

        panel.add(lbl, BorderLayout.WEST);
        panel.add(spn, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);

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

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        return lbl;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 10, 10));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.setBackground(Color.WHITE);

        JButton btnRegistrar = crearBoton("Registrar Evento");
        JButton btnLimpiar = crearBoton("Limpiar Formulario");

        btnRegistrar.addActionListener(e -> registrarEvento());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panel.add(btnRegistrar);
        panel.add(btnLimpiar);

        return panel;
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.PLAIN, 11));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "LISTA DE EVENTOS REGISTRADOS", 0, 0, new Font("Arial", Font.BOLD, 12)));
        panel.setBackground(Color.WHITE);

        // Tabla
        modeloTabla = new EventoTableModel(gestorConductores);
        tablaEventos = new JTable(modeloTabla);
        tablaEventos.setFont(new Font("Arial", Font.PLAIN, 9));
        tablaEventos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 10));
        tablaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEventos.setRowHeight(20);
        tablaEventos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        EstilosInterfaz.configurarTabla(tablaEventos);

        JScrollPane scrollPane = new JScrollPane(tablaEventos);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botón para actualizar
        JPanel panelBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelBot.setBackground(Color.WHITE);

        JButton btnActualizar = new JButton("Actualizar Tabla");
        btnActualizar.setFont(new Font("Arial", Font.PLAIN, 11));
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> modeloTabla.actualizarDatos());

        panelBot.add(btnActualizar);
        panel.add(panelBot, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarConductores() {
        cbxConductor.removeAllItems();
        cbxConductor.addItem("--- Seleccione un conductor ---");

        List<Conductor> conductores = gestorConductores.listarTodos();
        for (Conductor c : conductores) {
            cbxConductor.addItem(c.getNombreCompleto() + " (" + c.getLicencia().getNumero() + ")");
        }
    }

    private void cargarTiposEvento() {
        cbxTipoEvento.removeAllItems();
        cbxTipoEvento.addItem("--- Seleccione un tipo ---");

        List<?> tipos = gestorEventos.getTiposEventoActivos();
        for (Object tipo : tipos) {
            cbxTipoEvento.addItem(tipo.toString());
        }
    }

    private void registrarEvento() {
        try {
            // Validar conductor
            int indiceConductor = cbxConductor.getSelectedIndex() - 1;
            if (indiceConductor < 0) {
                mostrarError("Seleccione un conductor válido.");
                return;
            }

            List<Conductor> conductores = gestorConductores.listarTodos();
            if (indiceConductor >= conductores.size()) {
                mostrarError("Conductor no válido.");
                return;
            }

            Conductor c = conductores.get(indiceConductor);

            // Validar tipo de evento
            int indiceTipo = cbxTipoEvento.getSelectedIndex() - 1;
            if (indiceTipo < 0) {
                mostrarError("Seleccione un tipo de evento válido.");
                return;
            }

            // Validar fecha
            LocalDate fecha = obtenerFechaSpinner(spnFecha);
            if (fecha.isAfter(LocalDate.now())) {
                mostrarError("La fecha no puede ser en el futuro.");
                return;
            }

            // Validar ubicación
            String ubicacion = txtUbicacion.getText().trim();

            // Obtener gravedad
            int gravedad = (Integer) spnGravedad.getValue();

            // Obtener descripción
            String descripcion = txtDescripcion.getText().trim();

            // Registrar evento
            gestorEventos.registrarEvento(
                    c.getLicencia().getNumero(),
                    indiceTipo,
                    fecha,
                    ubicacion,
                    gravedad,
                    descripcion,
                    gestorConductores
            );

            // Mostrar confirmación clara en ventana emergente
            EventoConduccion ultimoEvento = c.getInfracciones().get(c.getInfracciones().size() - 1);
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("Evento registrado correctamente.\n")
                    .append("Conductor: ").append(c.getNombreCompleto()).append("\n")
                    .append("Tipo: ").append(ultimoEvento.getTipo().getNombre()).append("\n")
                    .append("Puntaje actual: ").append(String.format("%.1f", c.getPuntajeActual())).append(" pts");

            if (c.getLicencia().estaVencida()) {
                mensaje.append("\n\n⚠ ATENCIÓN: el conductor cuenta con una licencia ya vencida.")
                        .append("\nDebe renovar la licencia pronto y realizar un curso de recuperación/refuerzo.");
            }

            JOptionPane.showMessageDialog(this,
                    mensaje.toString(),
                    "Evento registrado", JOptionPane.INFORMATION_MESSAGE);

            // Actualizar tabla
            modeloTabla.actualizarDatos();
            limpiarCampos();
            ventanaPrincipal.actualizarTodosPaneles();

        } catch (SecrException e) {
            mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private LocalDate obtenerFechaSpinner(JSpinner spn) throws DateTimeParseException {
        Object valor = spn.getValue();
        if (valor instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) valor;
            return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }
        throw new DateTimeParseException("Formato de fecha inválido.", "", 0);
    }

    public void actualizarDatos() {
        modeloTabla.actualizarDatos();
        cargarConductores();
        cargarTiposEvento();
    }

    public void limpiarCampos() {
        cbxConductor.setSelectedIndex(0);
        cbxTipoEvento.setSelectedIndex(0);
        txtUbicacion.setText("");
        spnGravedad.setValue(1);
        txtDescripcion.setText("");
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

