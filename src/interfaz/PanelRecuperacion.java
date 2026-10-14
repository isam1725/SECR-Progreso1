package interfaz;

import static modelo.Main.*;
import modelo.*;
import negocio.GestorConductores;
import negocio.GestorCursos;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel para recuperación de puntos mediante cursos y pruebas.
 * Permite seleccionar un conductor, un curso, responder preguntas y ver resultados.
 */
public class PanelRecuperacion extends JPanel {

    private GestorConductores gestorConductores;
    private GestorCursos gestorCursos;
    private VentanaPrincipal ventanaPrincipal;

    // Componentes
    private JComboBox<String> cbxConductor;
    private JComboBox<String> cbxCurso;
    private JPanel panelPreguntas;
    private JScrollPane scrollPreguntas;
    private JButton btnIniciarPrueba;
    private JButton btnEnviarRespuestas;
    private JTextArea txtResultado;

    // Estado de la prueba
    private Conductor conductorSeleccionado;
    private Curso cursoSeleccionado;
    private List<Pregunta> preguntasActuales;
    private List<ButtonGroup> gruposRespuestas;

    public PanelRecuperacion(GestorConductores gestorConductores, GestorCursos gestorCursos, VentanaPrincipal ventanaPrincipal) {
        this.gestorConductores = gestorConductores;
        this.gestorCursos = gestorCursos;
        this.ventanaPrincipal = ventanaPrincipal;
        this.gruposRespuestas = new ArrayList<>();

        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Panel superior de configuración
        JPanel panelConfiguracion = crearPanelConfiguracion();
        add(panelConfiguracion, BorderLayout.NORTH);

        // Panel central con preguntas
        scrollPreguntas = crearPanelPreguntasScroll();
        add(scrollPreguntas, BorderLayout.CENTER);

        // Panel inferior con resultado
        JPanel panelResultado = crearPanelResultado();
        add(panelResultado, BorderLayout.SOUTH);
    }

    private JPanel crearPanelConfiguracion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "SELECCIONAR CURSO DE RECUPERACIÓN", 0, 0, new Font("Arial", Font.BOLD, 12)));
        panel.setBackground(Color.WHITE);

        // Fila: Conductor
        JPanel filaCondutor = new JPanel(new BorderLayout(10, 0));
        filaCondutor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        filaCondutor.setBackground(Color.WHITE);

        JLabel lblConductor = new JLabel("Conductor:");
        lblConductor.setFont(new Font("Arial", Font.PLAIN, 11));
        lblConductor.setPreferredSize(new Dimension(120, 30));

        cbxConductor = new JComboBox<>();
        cbxConductor.setFont(new Font("Arial", Font.PLAIN, 11));
        cbxConductor.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        EstilosInterfaz.configurarComboBox(cbxConductor);
        cargarConductores();

        filaCondutor.add(lblConductor, BorderLayout.WEST);
        filaCondutor.add(cbxConductor, BorderLayout.CENTER);
        panel.add(filaCondutor);

        panel.add(Box.createVerticalStrut(10));

        // Fila: Curso
        JPanel filaCurso = new JPanel(new BorderLayout(10, 0));
        filaCurso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        filaCurso.setBackground(Color.WHITE);

        JLabel lblCurso = new JLabel("Curso:");
        lblCurso.setFont(new Font("Arial", Font.PLAIN, 11));
        lblCurso.setPreferredSize(new Dimension(120, 30));

        cbxCurso = new JComboBox<>();
        cbxCurso.setFont(new Font("Arial", Font.PLAIN, 11));
        cbxCurso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        EstilosInterfaz.configurarComboBox(cbxCurso);
        cargarCursos();

        filaCurso.add(lblCurso, BorderLayout.WEST);
        filaCurso.add(cbxCurso, BorderLayout.CENTER);
        panel.add(filaCurso);

        panel.add(Box.createVerticalStrut(10));

        // Botón iniciar
        btnIniciarPrueba = new JButton("Iniciar Prueba");
        btnIniciarPrueba.setFont(new Font("Arial", Font.BOLD, 12));
        btnIniciarPrueba.setMaximumSize(new Dimension(200, 40));
        btnIniciarPrueba.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnIniciarPrueba.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIniciarPrueba.addActionListener(e -> iniciarPrueba());

        panel.add(btnIniciarPrueba);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JScrollPane crearPanelPreguntasScroll() {
        panelPreguntas = new JPanel();
        panelPreguntas.setLayout(new BoxLayout(panelPreguntas, BoxLayout.Y_AXIS));
        panelPreguntas.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "PREGUNTAS", 0, 0, new Font("Arial", Font.BOLD, 12)));
        panelPreguntas.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(panelPreguntas);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        return scroll;
    }

    private JPanel crearPanelResultado() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "RESULTADO", 0, 0, new Font("Arial", Font.BOLD, 12)));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 260));

        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setMargin(new Insets(10, 10, 10, 10));
        txtResultado.setBackground(new Color(245, 245, 245));
        txtResultado.setText("Seleccione un conductor y curso, luego haga clic en 'Iniciar Prueba'.");

        JScrollPane scrollResultado = new JScrollPane(txtResultado);
        panel.add(scrollResultado, BorderLayout.CENTER);

        // Botón enviar
        btnEnviarRespuestas = new JButton("Enviar Respuestas");
        btnEnviarRespuestas.setFont(new Font("Arial", Font.BOLD, 12));
        btnEnviarRespuestas.setPreferredSize(new Dimension(150, 35));
        btnEnviarRespuestas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEnviarRespuestas.setEnabled(false);
        btnEnviarRespuestas.addActionListener(e -> enviarRespuestas());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.add(btnEnviarRespuestas);

        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarConductores() {
        cbxConductor.removeAllItems();
        cbxConductor.addItem("--- Seleccione un conductor ---");

        List<Conductor> conductores = gestorConductores.listarTodos();
        for (Conductor c : conductores) {
            if (c.getPuntajeActual() < 100) {
                cbxConductor.addItem(c.getNombreCompleto() + " (" + String.format("%.1f", c.getPuntajeActual()) + " pts)");
            }
        }
    }

    private void cargarCursos() {
        cbxCurso.removeAllItems();
        cbxCurso.addItem("--- Seleccione un curso ---");
        for (Curso curso : gestorCursos.listarCursos()) {
            if (curso.estaActivo()) {
                cbxCurso.addItem(curso.getCodigo() + " - " + curso.getNombre()
                        + " (" + curso.getPuntosRecuperacion() + " puntos, mínimo "
                        + String.format("%.0f", curso.getPorcentajeMinimoAprobacion()) + "%)");
            }
        }
    }

    private void iniciarPrueba() {
        try {
            // Validar conductor
            int indiceConductor = cbxConductor.getSelectedIndex() - 1;
            if (indiceConductor < 0) {
                mostrarError("Seleccione un conductor válido.");
                return;
            }

            List<Conductor> conductores = gestorConductores.listarTodos();
            List<Conductor> conductoresConBajoPuntaje = new ArrayList<>();
            for (Conductor c : conductores) {
                if (c.getPuntajeActual() < 100) {
                    conductoresConBajoPuntaje.add(c);
                }
            }

            if (indiceConductor >= conductoresConBajoPuntaje.size()) {
                mostrarError("Conductor no válido.");
                return;
            }

            conductorSeleccionado = conductoresConBajoPuntaje.get(indiceConductor);

            // Validar curso
            int indiceCurso = cbxCurso.getSelectedIndex() - 1;
            if (indiceCurso < 0) {
                mostrarError("Seleccione un curso válido.");
                return;
            }

            List<Curso> cursosActivos = new ArrayList<>();
            for (Curso curso : gestorCursos.listarCursos()) {
                if (curso.estaActivo()) cursosActivos.add(curso);
            }
            if (indiceCurso >= cursosActivos.size()) {
                mostrarError("Curso no válido.");
                return;
            }
            cursoSeleccionado = cursosActivos.get(indiceCurso);

            // Cargar preguntas del curso seleccionado
            preguntasActuales = new ArrayList<>(cursoSeleccionado.getPreguntas());

            // Mostrar preguntas
            mostrarPreguntas();

            // Habilitar botón enviar
            btnEnviarRespuestas.setEnabled(true);
            cbxConductor.setEnabled(false);
            cbxCurso.setEnabled(false);
            btnIniciarPrueba.setEnabled(false);

        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void mostrarPreguntas() {
        panelPreguntas.removeAll();
        gruposRespuestas.clear();

        for (int i = 0; i < preguntasActuales.size(); i++) {
            Pregunta p = preguntasActuales.get(i);

            // Panel para cada pregunta
            JPanel panelPregunta = new JPanel();
            panelPregunta.setLayout(new BoxLayout(panelPregunta, BoxLayout.Y_AXIS));
            panelPregunta.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    "Pregunta " + (i + 1) + " de " + preguntasActuales.size(),
                    0, 0, new Font("Arial", Font.BOLD, 11)));
            panelPregunta.setBackground(Color.WHITE);
            panelPregunta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

            // Texto de la pregunta
            JLabel lblPregunta = new JLabel("<html>" + p.getTexto() + "</html>");
            lblPregunta.setFont(new Font("Arial", Font.BOLD, 12));
            lblPregunta.setForeground(new Color(40, 44, 52));
            panelPregunta.add(lblPregunta);
            panelPregunta.add(Box.createVerticalStrut(10));

            // Opciones con RadioButton
            ButtonGroup grupo = new ButtonGroup();
            gruposRespuestas.add(grupo);

            for (int j = 0; j < p.getOpciones().size(); j++) {
                JRadioButton rbtn = new JRadioButton(p.getOpciones().get(j));
                rbtn.setFont(new Font("Arial", Font.PLAIN, 11));
                rbtn.setBackground(Color.WHITE);
                rbtn.setForeground(Color.BLACK);
                rbtn.setActionCommand(String.valueOf(j));
                grupo.add(rbtn);
                panelPregunta.add(rbtn);
            }

            panelPreguntas.add(panelPregunta);
            panelPreguntas.add(Box.createVerticalStrut(10));
        }

        panelPreguntas.add(Box.createVerticalGlue());
        panelPreguntas.revalidate();
        panelPreguntas.repaint();
    }

    private void enviarRespuestas() {
        try {
            // Validar que todas las preguntas fueron respondidas
            List<Integer> respuestas = new ArrayList<>();

            for (int i = 0; i < gruposRespuestas.size(); i++) {
                ButtonGroup grupo = gruposRespuestas.get(i);

                if (grupo.getSelection() == null) {
                    mostrarError("Debe responder TODAS las preguntas. Falta la pregunta " + (i + 1) + ".");
                    return;
                }

                int respuesta = Integer.parseInt(grupo.getSelection().getActionCommand());
                respuestas.add(respuesta);
            }

            // Calcular cuántas respuestas fueron correctas antes de registrar la prueba
            int correctas = contarCorrectas(respuestas);

            // Realizar la prueba y guardarla en el historial del conductor
            Prueba prueba = gestorConductores.realizarPruebaRecuperacion(
                    conductorSeleccionado.getLicencia().getNumero(),
                    cursoSeleccionado,
                    preguntasActuales,
                    respuestas
            );

            // Mostrar resultado claro y conservarlo en pantalla
            mostrarResultado(prueba, correctas);

            // Resetear controles sin borrar el mensaje final
            resetearInterfaz(false);
            ventanaPrincipal.actualizarTodosPaneles();

        } catch (PruebaNoValidaException e) {
            mostrarError(e.getMessage());
        } catch (PuntajeNoValidoException e) {
            mostrarError(e.getMessage());
        } catch (ConductorNoEncontradoException e) {
            mostrarError(e.getMessage());
        } catch (SecrException e) {
            mostrarError("Error: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private int contarCorrectas(List<Integer> respuestas) {
        int correctas = 0;
        for (int i = 0; i < preguntasActuales.size() && i < respuestas.size(); i++) {
            if (preguntasActuales.get(i).esCorrecta(respuestas.get(i))) correctas++;
        }
        return correctas;
    }

    private void mostrarResultado(Prueba prueba, int correctas) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================\n");
        sb.append(" RESULTADO DE LA PRUEBA \n");
        sb.append("========================================\n\n");
        sb.append("Mensaje: El día de hoy ").append(conductorSeleccionado.getNombreCompleto())
                .append(" con licencia ").append(conductorSeleccionado.getLicencia().getNumero())
                .append(prueba.estaAprobada() ? " aprobó" : " desaprobó")
                .append(" el examen de recuperación del curso ").append(cursoSeleccionado.getNombre())
                .append(".\n\n");

        sb.append("Conductor: ").append(conductorSeleccionado.getNombreCompleto()).append("\n");
        sb.append("Licencia: ").append(conductorSeleccionado.getLicencia().getNumero()).append("\n");
        sb.append("Curso: ").append(cursoSeleccionado.getNombre()).append("\n");
        sb.append("Fecha de rendición: ").append(prueba.getFecha()).append("\n\n");

        sb.append("--- CALIFICACIÓN ---\n");
        sb.append("Preguntas correctas: ").append(correctas).append(" de ").append(preguntasActuales.size()).append("\n");
        sb.append("Calificación: ").append(String.format("%.1f", prueba.getCalificacion())).append("%\n");
        sb.append("Estado: ").append(prueba.estaAprobada() ? "APROBADA ✓" : "REPROBADA ✗").append("\n\n");

        sb.append("--- PUNTAJE ---\n");
        double puntajeAnterior = conductorSeleccionado.getPuntajeActual() -
                (prueba.estaAprobada() ? cursoSeleccionado.getPuntosRecuperacion() : 0);
        sb.append("Puntaje anterior: ").append(String.format("%.1f", puntajeAnterior)).append(" pts\n");

        if (prueba.estaAprobada()) {
            sb.append("Puntos recuperados: +").append(cursoSeleccionado.getPuntosRecuperacion()).append(" pts\n");
        } else {
            sb.append("Puntos recuperados: 0 pts\n");
        }

        sb.append("Puntaje nuevo: ").append(String.format("%.1f", conductorSeleccionado.getPuntajeActual())).append(" pts\n\n");

        sb.append("========================================\n");

        if (prueba.estaAprobada()) {
            sb.append("¡FELICIDADES! Ha recuperado puntos exitosamente.");
        } else {
            sb.append("Intente nuevamente para recuperar puntos.");
        }

        sb.append("\n========================================\n");

        txtResultado.setText(sb.toString());
    }

    private void resetearInterfaz(boolean limpiarResultado) {
        conductorSeleccionado = null;
        cursoSeleccionado = null;
        preguntasActuales = null;
        gruposRespuestas.clear();

        panelPreguntas.removeAll();
        panelPreguntas.revalidate();
        panelPreguntas.repaint();

        cbxConductor.setSelectedIndex(0);
        cbxCurso.setSelectedIndex(0);
        cbxConductor.setEnabled(true);
        cbxCurso.setEnabled(true);
        btnIniciarPrueba.setEnabled(true);
        btnEnviarRespuestas.setEnabled(false);

        cargarConductores();
        if (limpiarResultado) {
            txtResultado.setText("Seleccione un conductor y curso, luego haga clic en 'Iniciar Prueba'.");
        }
    }

    public void actualizarDatos() {
        cargarConductores();
        resetearInterfaz(true);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}

