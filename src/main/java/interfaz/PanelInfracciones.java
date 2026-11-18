package interfaz;

import static modelo.Main.*;
import estructuras.ArbolAVL;
import estructuras.ListaDoble;
import modelo.Conductor;
import modelo.EventoConduccion;
import modelo.Infraccion;
import modelo.NivelRiesgo;
import modelo.TipoEvento;
import negocio.GestorConductores;
import negocio.GestorEventos;
import negocio.GestorRiesgo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

/**
 * MÓDULO M2 — Registro y análisis de infracciones (interfaz gráfica).
 * Entrada de datos → validaciones → procesamiento (GestorEventos) → resultados.
 * Estructuras visibles: árbol AVL por fecha (consulta por rango), pila (deshacer),
 * lista doblemente enlazada (historial navegable) y Merge Sort (ordenamiento).
 */
public class PanelInfracciones extends JPanel {

    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final GestorConductores gestorC;
    private final GestorEventos gestorE;
    private final GestorRiesgo gestorR;
    private final VentanaPrincipal ventana;

    // Formulario de registro (visibilidad de paquete: los usa CapturaDemo)
    final JTextField txtLicencia = ComponentesUI.campo(10);
    final JLabel lblConductor = new JLabel(" ");
    final JComboBox<String> cbxTipo = new JComboBox<>();
    final JTextField txtFecha = ComponentesUI.campo(10);
    final JTextField txtUbicacion = ComponentesUI.campo(18);
    final JSpinner spnGravedad = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
    final JTextField txtDescripcion = ComponentesUI.campo(18);
    final JButton btnRegistrar = ComponentesUI.boton("Registrar infracción", true);
    final JButton btnDeshacer = ComponentesUI.boton("Deshacer último (0)", false);
    final JTextArea txtResultado = new JTextArea(7, 26);

    // Consulta por rango
    final JTextField txtDesde = ComponentesUI.campo(8);
    final JTextField txtHasta = ComponentesUI.campo(8);
    final JTextField txtFiltroLicencia = ComponentesUI.campo(8);
    final JComboBox<GestorEventos.CriterioOrden> cbxOrden = new JComboBox<>(GestorEventos.CriterioOrden.values());
    final JButton btnConsultar = ComponentesUI.boton("Consultar rango", true);
    final JButton btnTodos = ComponentesUI.boton("Ver todos", false);
    final DefaultTableModel modeloEventos = new DefaultTableModel(
            new String[]{"ID", "Fecha", "Licencia", "Conductor", "Tipo de infracción", "Gravedad", "Pts", "Ubicación"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    final JTable tablaEventos = new JTable(modeloEventos);
    final JLabel lblResumen = ComponentesUI.etiqueta(" ");

    // Historial navegable (lista doble)
    final JTextField txtHistLicencia = ComponentesUI.campo(10);
    final JButton btnCargarHist = ComponentesUI.boton("Cargar historial", true);
    final JButton btnAnterior = ComponentesUI.boton("< Anterior", false);
    final JButton btnSiguiente = ComponentesUI.boton("Siguiente >", false);
    final JTextArea txtHistorial = new JTextArea(9, 40);
    final JLabel lblPosHist = ComponentesUI.etiqueta(" ");
    private ListaDoble<Infraccion>.Cursor cursor;
    private ListaDoble<Infraccion> listaHist;

    final JTabbedPane pestanas = new JTabbedPane();
    final ComponentesUI.BarraEstado estado = new ComponentesUI.BarraEstado();

    public PanelInfracciones(GestorConductores gestorC, GestorEventos gestorE, GestorRiesgo gestorR, VentanaPrincipal ventana) {
        this.gestorC = gestorC;
        this.gestorE = gestorE;
        this.gestorR = gestorR;
        this.ventana = ventana;
        construir();
        actualizarDatos();
    }

    // ------------------------------------------------------------------ construcción

    private void construir() {
        setLayout(new BorderLayout(8, 8));
        setBackground(ComponentesUI.FONDO);
        setBorder(new EmptyBorder(10, 12, 10, 12));
        add(ComponentesUI.encabezado("M2 · Registro y análisis de infracciones",
                "Árbol AVL por fecha (rango) · Pila (deshacer) · Lista doblemente enlazada (historial) · Merge Sort (ordenamiento)"),
                BorderLayout.NORTH);

        JPanel izquierda = new JPanel(new BorderLayout(0, 8));
        izquierda.setOpaque(false);
        izquierda.setPreferredSize(new Dimension(345, 0));
        izquierda.add(crearFormulario(), BorderLayout.NORTH);
        izquierda.add(crearResultado(), BorderLayout.CENTER);

        pestanas.setFont(new Font("Arial", Font.BOLD, 12));
        pestanas.addTab("Eventos · consulta por rango (AVL)", crearPestanaEventos());
        pestanas.addTab("Historial del conductor (lista doble)", crearPestanaHistorial());

        add(izquierda, BorderLayout.WEST);
        add(pestanas, BorderLayout.CENTER);
        add(estado, BorderLayout.SOUTH);
    }

    private JPanel crearFormulario() {
        JPanel p = ComponentesUI.tarjeta("Registrar infracción");
        p.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 2, 3, 2);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        int f = 0;
        JButton btnBuscar = ComponentesUI.boton("Buscar", false);
        JPanel filaLic = new JPanel(new BorderLayout(4, 0));
        filaLic.setOpaque(false);
        filaLic.add(txtLicencia, BorderLayout.CENTER);
        filaLic.add(btnBuscar, BorderLayout.EAST);
        fila(p, g, f++, "Licencia:", filaLic);
        lblConductor.setFont(new Font("Arial", Font.ITALIC, 11));
        lblConductor.setForeground(ComponentesUI.GRIS);
        fila(p, g, f++, "", lblConductor);
        EstilosInterfaz.configurarComboBox(cbxTipo);
        fila(p, g, f++, "Tipo:", cbxTipo);
        fila(p, g, f++, "Fecha (dd/MM/aaaa):", txtFecha);
        fila(p, g, f++, "Ubicación:", txtUbicacion);
        fila(p, g, f++, "Gravedad (1-5):", spnGravedad);
        fila(p, g, f++, "Descripción:", txtDescripcion);
        JPanel botones = new JPanel(new GridLayout(1, 2, 6, 0));
        botones.setOpaque(false);
        botones.add(btnRegistrar);
        botones.add(btnDeshacer);
        g.gridx = 0; g.gridy = f; g.gridwidth = 2; g.insets = new Insets(8, 2, 2, 2);
        p.add(botones, g);

        txtFecha.setText(LocalDate.now().format(FMT));
        // Si se cambia la licencia, el nombre mostrado deja de ser válido hasta volver a buscar.
        txtLicencia.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { lblConductor.setText(" "); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { lblConductor.setText(" "); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { }
        });
        btnBuscar.addActionListener(e -> buscarConductor());
        txtLicencia.addActionListener(e -> buscarConductor());
        btnRegistrar.addActionListener(e -> registrar());
        btnDeshacer.addActionListener(e -> deshacer());
        return p;
    }

    private void fila(JPanel p, GridBagConstraints g, int fila, String etiqueta, JComponent comp) {
        g.gridx = 0; g.gridy = fila; g.gridwidth = 1; g.weightx = 0;
        p.add(ComponentesUI.etiqueta(etiqueta), g);
        g.gridx = 1; g.weightx = 1;
        p.add(comp, g);
    }

    private JPanel crearResultado() {
        JPanel p = ComponentesUI.tarjeta("Resultado del procesamiento");
        p.setLayout(new BorderLayout());
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setText("Registre una infracción para ver la deducción,\nel nuevo puntaje y el cambio de posición\nen el ranking de riesgo (M3).");
        p.add(new JScrollPane(txtResultado), BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPestanaEventos() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));
        JPanel filtros = new JPanel(new GridLayout(2, 1, 0, 4));
        filtros.setOpaque(false);
        JPanel f1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JPanel f2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        f1.setOpaque(false);
        f2.setOpaque(false);
        f1.add(ComponentesUI.etiqueta("Desde:"));
        f1.add(txtDesde);
        f1.add(ComponentesUI.etiqueta("Hasta:"));
        f1.add(txtHasta);
        f1.add(ComponentesUI.etiqueta("Licencia (opcional):"));
        f1.add(txtFiltroLicencia);
        f2.add(ComponentesUI.etiqueta("Ordenar por:"));
        EstilosInterfaz.configurarComboBox(cbxOrden);
        f2.add(cbxOrden);
        f2.add(btnConsultar);
        f2.add(btnTodos);
        filtros.add(f1);
        filtros.add(f2);
        ComponentesUI.estilizarTabla(tablaEventos);
        int[] anchos = {58, 80, 72, 150, 140, 95, 40, 140};
        for (int i = 0; i < anchos.length; i++) tablaEventos.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        p.add(filtros, BorderLayout.NORTH);
        p.add(new JScrollPane(tablaEventos), BorderLayout.CENTER);
        p.add(lblResumen, BorderLayout.SOUTH);
        txtDesde.setText(LocalDate.now().minusMonths(3).format(FMT));
        txtHasta.setText(LocalDate.now().format(FMT));
        btnConsultar.addActionListener(e -> consultarRango());
        btnTodos.addActionListener(e -> mostrarTodos());
        cbxOrden.addActionListener(e -> mostrarTodos());
        return p;
    }

    private JPanel crearPestanaHistorial() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        arriba.setOpaque(false);
        arriba.add(ComponentesUI.etiqueta("Licencia:"));
        arriba.add(txtHistLicencia);
        arriba.add(btnCargarHist);
        txtHistorial.setEditable(false);
        txtHistorial.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        txtHistorial.setText("Cargue el historial de un conductor y recórralo con los botones Anterior / Siguiente.\n"
                + "Cada evento es un nodo de una lista doblemente enlazada ordenada por fecha.");
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
        nav.setOpaque(false);
        nav.add(btnAnterior);
        nav.add(lblPosHist);
        nav.add(btnSiguiente);
        btnAnterior.setEnabled(false);
        btnSiguiente.setEnabled(false);
        p.add(arriba, BorderLayout.NORTH);
        p.add(new JScrollPane(txtHistorial), BorderLayout.CENTER);
        p.add(nav, BorderLayout.SOUTH);
        btnCargarHist.addActionListener(e -> cargarHistorial());
        txtHistLicencia.addActionListener(e -> cargarHistorial());
        btnAnterior.addActionListener(e -> moverHistorial(false));
        btnSiguiente.addActionListener(e -> moverHistorial(true));
        return p;
    }

    // ------------------------------------------------------------------ acciones

    public void actualizarDatos() {
        cbxTipo.removeAllItems();
        for (TipoEvento t : gestorE.getTiposEventoActivos())
            cbxTipo.addItem(t.getNombre() + " (" + t.getPuntosBase() + " pts base)");
        actualizarBotonDeshacer();
        mostrarTodos();
    }

    void buscarConductor() {
        String lic = txtLicencia.getText().trim();
        if (lic.isEmpty()) { estado.error("Ingrese un número de licencia (por ejemplo B-030405)."); return; }
        Conductor c = gestorC.buscarPorLicencia(lic);       // búsqueda en el árbol AVL: O(log n)
        if (c == null) {
            lblConductor.setText("No existe un conductor con esa licencia.");
            estado.error("No existe un conductor con la licencia " + lic.toUpperCase() + ".");
            return;
        }
        lblConductor.setText(c.getNombreCompleto() + " · " + fmt(c.getPuntajeActual()) + " pts · " + c.getEstado());
        estado.info("Conductor encontrado en el índice AVL de licencias.");
    }

    void registrar() {
        try {
            String lic = txtLicencia.getText().trim();
            if (lic.isEmpty()) throw new EventoNoValidoException("Ingrese el número de licencia del conductor.");
            if (cbxTipo.getSelectedIndex() < 0) throw new EventoNoValidoException("Seleccione un tipo de infracción.");
            LocalDate fecha = leerFecha(txtFecha, "del evento");
            Conductor c = gestorC.obtenerConductorObligatorio(lic);
            lblConductor.setText(c.getNombreCompleto() + " · " + fmt(c.getPuntajeActual()) + " pts · " + c.getEstado());
            gestorR.reubicar(c);                       // por si el conductor se registró recién en M1
            double antes = c.getPuntajeActual();
            int posAntes = gestorR.posicion(lic);
            Infraccion inf = gestorE.registrarEvento(lic, cbxTipo.getSelectedIndex(), fecha, txtUbicacion.getText(),
                    (Integer) spnGravedad.getValue(), txtDescripcion.getText().trim(), gestorC);
            int posDespues = gestorR.posicion(lic);
            txtResultado.setText(
                    "Evento:     " + inf.getIdEvento() + " · " + inf.getTipo().getNombre() + "\n"
                  + "Gravedad:   " + inf.getGravedad() + " (" + inf.clasificarGravedad() + ")\n"
                  + "Deducción:  " + inf.getTipo().getPuntosBase() + " × " + inf.getGravedad() + " = −" + inf.calcularPuntosDeduccion() + " pts\n"
                  + "Puntaje:    " + fmt(antes) + " → " + fmt(c.getPuntajeActual()) + "\n"
                  + "Semáforo:   " + NivelRiesgo.clasificar(c.getPuntajeActual()).getEtiqueta() + "\n"
                  + "Ranking M3: posición " + posAntes + " → " + posDespues + " de " + gestorR.tamanio() + "\n"
                  + "Estado:     " + c.getEstado()
                  + (c.getEstado().equals("Inactivo") ? "  (puntaje en 0: no admite nuevos registros)" : ""));
            estado.ok("Infracción " + inf.getIdEvento() + " registrada para " + c.getNombreCompleto()
                    + ". Indexada por fecha y apilada para poder deshacerla.");
            actualizarBotonDeshacer();
            mostrarTodos();
            lblConductor.setText(c.getNombreCompleto() + " · " + fmt(c.getPuntajeActual()) + " pts · " + c.getEstado());
            if (ventana != null) ventana.actualizarTodosPaneles();
        } catch (SecrException ex) {
            estado.error(ex.getMessage());
        } catch (RuntimeException ex) {
            estado.error("Datos no válidos: " + ex.getMessage());
        }
    }

    void deshacer() {
        try {
            GestorEventos.OperacionRegistro op = gestorE.deshacerUltimoRegistro(gestorC);
            Conductor c = op.conductor();
            txtResultado.setText(
                    "DESHACER (pila LIFO)\n"
                  + "Evento revertido: " + op.infraccion().getIdEvento() + " · " + op.infraccion().getTipo().getNombre() + "\n"
                  + "Conductor:        " + c.getLicencia().getNumero() + " · " + c.getNombreCompleto() + "\n"
                  + "Puntaje restaurado: " + fmt(c.getPuntajeActual()) + " · " + c.getEstado() + "\n"
                  + "Ranking M3:       posición " + gestorR.posicion(c.getLicencia().getNumero()) + " de " + gestorR.tamanio() + "\n"
                  + "Quedan en la pila: " + gestorE.operacionesParaDeshacer());
            estado.ok("Se deshizo " + op.infraccion().getIdEvento() + ". Se quitó del historial y del índice por fecha.");
            actualizarBotonDeshacer();
            mostrarTodos();
            if (ventana != null) ventana.actualizarTodosPaneles();
        } catch (SecrException ex) {
            estado.error(ex.getMessage());
        }
    }

    void consultarRango() {
        try {
            LocalDate desde = leerFecha(txtDesde, "inicial");
            LocalDate hasta = leerFecha(txtHasta, "final");
            List<EventoConduccion> res = gestorE.consultarPorRango(desde, hasta, txtFiltroLicencia.getText().trim(), gestorC);
            res = gestorE.ordenar(res, (GestorEventos.CriterioOrden) cbxOrden.getSelectedItem());
            llenarTabla(res);
            lblResumen.setText(res.size() + " eventos entre " + desde.format(FMT) + " y " + hasta.format(FMT)
                    + " · más frecuente: " + gestorE.infraccionMasFrecuente(res)
                    + " · ordenados por " + cbxOrden.getSelectedItem());
            if (res.isEmpty()) estado.info("No hay eventos en ese rango de fechas.");
            else estado.ok("Consulta de rango sobre el árbol AVL por fecha: " + res.size() + " eventos.");
        } catch (SecrException ex) {
            estado.error(ex.getMessage());
        }
    }

    void mostrarTodos() {
        List<EventoConduccion> todos = gestorE.ordenar(gestorE.listarCronologico(), (GestorEventos.CriterioOrden) cbxOrden.getSelectedItem());
        llenarTabla(todos);
        lblResumen.setText(todos.size() + " eventos registrados · más frecuente: " + gestorE.infraccionMasFrecuente(todos)
                + " · ordenados por " + cbxOrden.getSelectedItem() + " (Merge Sort)");
    }

    void cargarHistorial() {
        try {
            String lic = txtHistLicencia.getText().trim();
            if (lic.isEmpty()) throw new SecrException("Ingrese la licencia del conductor.");
            listaHist = gestorE.historialNavegable(lic, gestorC);
            Conductor c = gestorC.buscarPorLicencia(lic);
            if (listaHist.estaVacia()) {
                cursor = null;
                txtHistorial.setText(c.getNombreCompleto() + " no tiene infracciones registradas.");
                lblPosHist.setText("0 de 0");
                btnAnterior.setEnabled(false);
                btnSiguiente.setEnabled(false);
                estado.info("El conductor no tiene infracciones (lista vacía).");
                return;
            }
            cursor = listaHist.cursor();
            mostrarNodo(c);
            estado.ok("Historial de " + c.getNombreCompleto() + " cargado en una lista doble de " + listaHist.tamanio() + " nodos.");
        } catch (SecrException ex) {
            estado.error(ex.getMessage());
        }
    }

    void moverHistorial(boolean adelante) {
        if (cursor == null) return;
        if (adelante) cursor.avanzar(); else cursor.retroceder();
        mostrarNodo(gestorC.buscarPorLicencia(txtHistLicencia.getText().trim()));
    }

    private void mostrarNodo(Conductor c) {
        Infraccion e = cursor.actual();
        txtHistorial.setText(
                "Conductor:   " + c.getLicencia().getNumero() + " · " + c.getNombreCompleto() + "\n"
              + "────────────────────────────────────────────\n"
              + "Evento:      " + e.getIdEvento() + "\n"
              + "Fecha:       " + e.getFechaHora().format(FMT) + "\n"
              + "Tipo:        " + e.getTipo().getNombre() + "\n"
              + "Gravedad:    " + e.getGravedad() + " (" + e.clasificarGravedad() + ")\n"
              + "Deducción:   −" + e.calcularPuntosDeduccion() + " pts\n"
              + "Ubicación:   " + e.getUbicacion() + "\n"
              + "────────────────────────────────────────────\n"
              + "Anterior: " + (cursor.hayAnterior() ? "sí" : "no (primer nodo)")
              + "   ·   Siguiente: " + (cursor.haySiguiente() ? "sí" : "no (último nodo)"));
        lblPosHist.setText(cursor.indice() + " de " + listaHist.tamanio());
        btnAnterior.setEnabled(cursor.hayAnterior());
        btnSiguiente.setEnabled(cursor.haySiguiente());
    }

    // ------------------------------------------------------------------ utilidades

    private void llenarTabla(List<EventoConduccion> eventos) {
        // Índice auxiliar idEvento → conductor sobre un árbol AVL para mostrar el titular de cada evento.
        ArbolAVL<String, Conductor> titular = new ArbolAVL<>();
        for (Conductor c : gestorC.listarTodos()) for (Infraccion i : c.getInfracciones()) titular.insertar(i.getIdEvento(), c);
        modeloEventos.setRowCount(0);
        for (EventoConduccion e : eventos) {
            Conductor c = titular.buscar(e.getIdEvento());
            modeloEventos.addRow(new Object[]{e.getIdEvento(), e.getFechaHora().format(FMT),
                    c == null ? "—" : c.getLicencia().getNumero(), c == null ? "—" : c.getNombreCompleto(),
                    e.getTipo().getNombre(), e.getGravedad() + " · " + e.clasificarGravedad(),
                    "−" + e.calcularPuntosDeduccion(), e.getUbicacion()});
        }
    }

    private void actualizarBotonDeshacer() {
        int n = gestorE.operacionesParaDeshacer();
        btnDeshacer.setText("Deshacer último (" + n + ")");
        btnDeshacer.setEnabled(true);
    }

    static LocalDate leerFecha(JTextField campo, String cual) throws EventoNoValidoException {
        String t = campo.getText().trim();
        if (t.isEmpty()) throw new EventoNoValidoException("Ingrese la fecha " + cual + " con formato dd/MM/aaaa.");
        try {
            return LocalDate.parse(t, FMT);
        } catch (DateTimeParseException ex) {
            throw new EventoNoValidoException("La fecha " + cual + " «" + t + "» no es válida. Use el formato dd/MM/aaaa (por ejemplo 15/11/2026).");
        }
    }

    static String fmt(double x) { return String.format("%.1f", x); }
}
