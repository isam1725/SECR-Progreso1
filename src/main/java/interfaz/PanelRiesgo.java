package interfaz;

import static modelo.Main.*;
import modelo.Conductor;
import modelo.NivelRiesgo;
import negocio.GestorConductores;
import negocio.GestorRiesgo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * MÓDULO M3 — Puntaje y priorización de riesgo (interfaz gráfica).
 * Entrada: k, nivel del semáforo o licencia → validaciones → procesamiento (GestorRiesgo,
 * árboles AVL) → resultados ordenados por riesgo.
 */
public class PanelRiesgo extends JPanel {

    static final Color[] COLORES = {new Color(200, 40, 50), new Color(230, 126, 34), new Color(241, 196, 15), new Color(39, 150, 80)};

    private final GestorConductores gestorC;
    private final GestorRiesgo gestorR;

    final JTextField txtK = ComponentesUI.campo(4);
    final JButton btnTop = ComponentesUI.boton("Ver top-k", true);
    final JComboBox<NivelRiesgo> cbxNivel = new JComboBox<>(NivelRiesgo.values());
    final JButton btnNivel = ComponentesUI.boton("Filtrar por nivel", false);
    final JButton btnTodos = ComponentesUI.boton("Ranking completo", false);
    final JTextField txtLicencia = ComponentesUI.campo(9);
    final JButton btnPosicion = ComponentesUI.boton("Consultar posición", true);
    final JButton btnBonificacion = ComponentesUI.boton("Aplicar bonificación", false);
    final JLabel[] lblConteo = new JLabel[4];
    final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Posición", "Licencia", "Conductor", "Puntaje", "Nivel del semáforo", "Infracciones", "Estado"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    final JTable tabla = new JTable(modelo);
    final JLabel lblTabla = ComponentesUI.etiqueta(" ");
    final JTextArea txtDetalle = new JTextArea(6, 30);
    final ComponentesUI.BarraEstado estado = new ComponentesUI.BarraEstado();

    public PanelRiesgo(GestorConductores gestorC, GestorRiesgo gestorR) {
        this.gestorC = gestorC;
        this.gestorR = gestorR;
        construir();
    }

    private void construir() {
        setLayout(new BorderLayout(8, 8));
        setBackground(ComponentesUI.FONDO);
        setBorder(new EmptyBorder(10, 12, 10, 12));
        add(ComponentesUI.encabezado("M3 · Puntaje y priorización de riesgo",
                "Ranking sobre dos árboles AVL: top-k en O(log n + k) · filtro por semáforo con consulta de rango · posición en O(log n) con tamaño de subárbol"),
                BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(crearControles(), BorderLayout.NORTH);

        JPanel tablaPanel = ComponentesUI.tarjeta("Conductores ordenados por riesgo (menor puntaje primero)");
        tablaPanel.setLayout(new BorderLayout(0, 4));
        ComponentesUI.estilizarTabla(tabla);
        tabla.getColumnModel().getColumn(4).setCellRenderer(new RenderNivel());
        int[] anchos = {65, 80, 200, 70, 190, 85, 75};
        for (int i = 0; i < anchos.length; i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        tablaPanel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        tablaPanel.add(lblTabla, BorderLayout.SOUTH);
        centro.add(tablaPanel, BorderLayout.CENTER);

        JPanel detalle = ComponentesUI.tarjeta("Detalle de la consulta");
        detalle.setLayout(new BorderLayout());
        detalle.setPreferredSize(new Dimension(300, 0));
        txtDetalle.setEditable(false);
        txtDetalle.setLineWrap(true);
        txtDetalle.setWrapStyleWord(true);
        txtDetalle.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtDetalle.setText("Consulte la posición de un conductor para ver su puntaje, su nivel y cuántos conductores tienen más riesgo que él.");
        detalle.add(new JScrollPane(txtDetalle), BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);
        add(detalle, BorderLayout.EAST);
        add(estado, BorderLayout.SOUTH);
    }

    private JPanel crearControles() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);

        JPanel semaforo = new JPanel(new GridLayout(1, 4, 8, 0));
        semaforo.setOpaque(false);
        NivelRiesgo[] niveles = NivelRiesgo.values();
        for (int i = 0; i < 4; i++) {
            lblConteo[i] = new JLabel("", SwingConstants.CENTER);
            lblConteo[i].setOpaque(true);
            lblConteo[i].setBackground(COLORES[i]);
            lblConteo[i].setForeground(i == 2 ? ComponentesUI.TEXTO : Color.WHITE);
            lblConteo[i].setFont(new Font("Arial", Font.BOLD, 13));
            lblConteo[i].setBorder(new EmptyBorder(8, 6, 8, 6));
            lblConteo[i].setToolTipText(niveles[i].getEtiqueta());
            semaforo.add(lblConteo[i]);
        }

        JPanel acciones = ComponentesUI.tarjeta("Consultas");
        acciones.setLayout(new GridLayout(2, 1, 0, 4));
        JPanel f1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JPanel f2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        f1.setOpaque(false);
        f2.setOpaque(false);
        f1.add(ComponentesUI.etiqueta("Top-k, k ="));
        txtK.setText("5");
        f1.add(txtK);
        f1.add(btnTop);
        f1.add(Box.createHorizontalStrut(12));
        f1.add(ComponentesUI.etiqueta("Semáforo:"));
        EstilosInterfaz.configurarComboBox(cbxNivel);
        f1.add(cbxNivel);
        f1.add(btnNivel);
        f2.add(ComponentesUI.etiqueta("Licencia:"));
        f2.add(txtLicencia);
        f2.add(btnPosicion);
        f2.add(Box.createHorizontalStrut(12));
        f2.add(btnTodos);
        f2.add(btnBonificacion);
        acciones.add(f1);
        acciones.add(f2);

        btnTop.addActionListener(e -> verTop());
        txtK.addActionListener(e -> verTop());
        btnNivel.addActionListener(e -> verNivel());
        btnTodos.addActionListener(e -> verTodos());
        btnPosicion.addActionListener(e -> consultarPosicion());
        txtLicencia.addActionListener(e -> consultarPosicion());
        btnBonificacion.addActionListener(e -> aplicarBonificacion());

        p.add(semaforo, BorderLayout.NORTH);
        p.add(acciones, BorderLayout.CENTER);
        return p;
    }

    /** Se llama al abrir el panel: sincroniza el ranking con los conductores de M1 y muestra el top-5. */
    public void actualizarDatos() {
        gestorR.sincronizar(gestorC);
        actualizarSemaforo();
        verTop();
    }

    void verTop() {
        try {
            String t = txtK.getText().trim();
            int k;
            try {
                k = Integer.parseInt(t);
            } catch (NumberFormatException ex) {
                throw new SecrException("El valor de k «" + t + "» no es un número entero.");
            }
            List<Conductor> top = gestorR.topRiesgo(k);
            llenar(top, 1);
            lblTabla.setText("Top-" + k + " de mayor riesgo" + (k > top.size() ? " (solo hay " + top.size() + " conductores)" : "")
                    + " · recorrido en orden del árbol AVL desde el menor puntaje.");
            estado.ok("Se obtuvieron los " + top.size() + " conductores de mayor riesgo.");
        } catch (SecrException ex) {
            estado.error(ex.getMessage());
        }
    }

    void verNivel() {
        try {
            NivelRiesgo n = (NivelRiesgo) cbxNivel.getSelectedItem();
            List<Conductor> lista = gestorR.porNivel(n);
            modelo.setRowCount(0);
            for (Conductor c : lista) agregarFila(gestorR.posicion(c.getLicencia().getNumero()), c);
            lblTabla.setText(lista.size() + " conductores en " + n.getEtiqueta() + " (puntaje " + (int) n.getMin() + " a "
                    + (n == NivelRiesgo.VERDE ? "100" : String.valueOf((int) n.getMax())) + ") · consulta de rango en el árbol AVL.");
            if (lista.isEmpty()) estado.info("No hay conductores en el nivel " + n.getEtiqueta() + ".");
            else estado.ok("Filtro por semáforo: " + lista.size() + " conductores.");
        } catch (SecrException ex) {
            estado.error(ex.getMessage());
        }
    }

    void verTodos() {
        try {
            List<Conductor> todos = gestorR.topRiesgo(Math.max(1, Math.min(GestorRiesgo.K_MAXIMO, gestorR.tamanio())));
            llenar(todos, 1);
            lblTabla.setText("Ranking completo: " + todos.size() + " conductores.");
            estado.ok("Ranking completo mostrado.");
        } catch (SecrException ex) {
            estado.error(ex.getMessage());
        }
    }

    void consultarPosicion() {
        try {
            String lic = txtLicencia.getText().trim();
            int pos = gestorR.posicion(lic);
            Conductor c = gestorC.buscarPorLicencia(lic);
            NivelRiesgo n = NivelRiesgo.clasificar(c.getPuntajeActual());
            txtDetalle.setText(
                    "Licencia:   " + c.getLicencia().getNumero() + "\n"
                  + "Conductor:  " + c.getNombreCompleto() + "\n"
                  + "Puntaje:    " + PanelInfracciones.fmt(c.getPuntajeActual()) + " / 100\n"
                  + "Nivel:      " + n.getEtiqueta() + "\n"
                  + "Posición:   " + pos + " de " + gestorR.tamanio() + "\n"
                  + "Con más riesgo que él: " + (pos - 1) + "\n"
                  + "Infracciones: " + c.getInfracciones().size() + "\n\n"
                  + "Cálculo: búsqueda en el AVL sumando el tamaño de los subárboles izquierdos (O(log n)).");
            // Resalta su fila en el ranking completo
            verTodos();
            tabla.setRowSelectionInterval(pos - 1, pos - 1);
            tabla.scrollRectToVisible(tabla.getCellRect(pos - 1, 0, true));
            estado.ok(c.getNombreCompleto() + " está en la posición " + pos + " de " + gestorR.tamanio() + ".");
        } catch (SecrException ex) {
            txtDetalle.setText("Sin resultado: " + ex.getMessage());
            estado.error(ex.getMessage());
        }
    }

    void aplicarBonificacion() {
        try {
            List<String> cambios = gestorR.aplicarBonificacionATodos(gestorC);
            actualizarSemaforo();
            verTop();
            estado.ok("Bonificación por buen comportamiento aplicada (5 pts por año sin infracciones). "
                    + (cambios.isEmpty() ? "Ningún puntaje cambió." : cambios.size() + " conductores cambiaron de puntaje: " + String.join(", ", cambios)));
        } catch (SecrException ex) {
            estado.error(ex.getMessage());
        }
    }

    private void actualizarSemaforo() {
        int[] cn = gestorR.conteoPorNivel();
        String[] nombres = {"ROJO", "NARANJA", "AMARILLO", "VERDE"};
        String[] rangos = {"< 40", "40 – 59", "60 – 79", "≥ 80"};
        for (int i = 0; i < 4; i++) lblConteo[i].setText(nombres[i] + " (" + rangos[i] + "): " + cn[i]);
    }

    private void llenar(List<Conductor> lista, int desde) {
        modelo.setRowCount(0);
        int pos = desde;
        for (Conductor c : lista) agregarFila(pos++, c);
        actualizarSemaforo();
    }

    private void agregarFila(int pos, Conductor c) {
        modelo.addRow(new Object[]{pos, c.getLicencia().getNumero(), c.getNombreCompleto(),
                PanelInfracciones.fmt(c.getPuntajeActual()), NivelRiesgo.clasificar(c.getPuntajeActual()),
                c.getInfracciones().size(), c.getEstado()});
    }

    /** Pinta la celda del nivel con el color del semáforo (el texto también dice el nivel). */
    private static final class RenderNivel extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            if (v instanceof NivelRiesgo n && !sel) {
                comp.setBackground(COLORES[n.ordinal()]);
                comp.setForeground(n == NivelRiesgo.AMARILLO ? ComponentesUI.TEXTO : Color.WHITE);
            }
            setFont(getFont().deriveFont(Font.BOLD));
            return comp;
        }
    }
}
