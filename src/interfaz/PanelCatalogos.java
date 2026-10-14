package interfaz;

import static modelo.Main.*;
import modelo.*;
import negocio.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Catálogos del sistema: permite crear y administrar cursos, preguntas y tipos de evento.
 */
public class PanelCatalogos extends JPanel {
    private GestorCursos gestorCursos;
    private GestorEventos gestorEventos;
    private GestorConductores gestorConductores;
    private VentanaPrincipal ventanaPrincipal;

    private JTextArea txtSalida;

    public PanelCatalogos(GestorCursos gestorCursos, GestorEventos gestorEventos,
                          GestorConductores gestorConductores, VentanaPrincipal ventanaPrincipal) {
        this.gestorCursos = gestorCursos;
        this.gestorEventos = gestorEventos;
        this.gestorConductores = gestorConductores;
        this.ventanaPrincipal = ventanaPrincipal;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        JLabel titulo = new JLabel("CATÁLOGOS PARA CREAR CURSOS, PREGUNTAS Y TIPOS DE EVENTO");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setForeground(new Color(40, 44, 52));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(titulo, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Cursos", crearPanelCursos());
        tabs.addTab("Preguntas", crearPanelPreguntas());
        tabs.addTab("Tipos de Evento", crearPanelTiposEvento());
        add(tabs, BorderLayout.CENTER);

        txtSalida = new JTextArea(8, 40);
        txtSalida.setEditable(false);
        txtSalida.setLineWrap(true);
        txtSalida.setWrapStyleWord(true);
        txtSalida.setFont(new Font("Courier New", Font.PLAIN, 11));
        txtSalida.setBackground(new Color(245, 245, 245));
        txtSalida.setForeground(Color.BLACK);
        add(new JScrollPane(txtSalida), BorderLayout.SOUTH);

        listarCursos();
    }

    private JLabel guia(String texto) {
        JLabel lbl = new JLabel("<html><span style='color:#666666;'><i>Guía: " + texto + "</i></span></html>");
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel crearContenedorFormulario(String titulo) {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(BorderFactory.createTitledBorder(titulo));
        return contenido;
    }

    private JPanel crearSeccionCampo(String etiqueta, String textoGuia, JComponent campo) {
        JPanel fila = new JPanel();
        fila.setLayout(new BoxLayout(fila, BoxLayout.Y_AXIS));
        fila.setBackground(Color.WHITE);
        fila.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        fila.add(lbl);
        fila.add(Box.createVerticalStrut(2));
        fila.add(guia(textoGuia));
        fila.add(Box.createVerticalStrut(4));
        fila.add(campo);

        return fila;
    }

    private JPanel crearSeccionAcciones(JButton... botones) {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBackground(Color.WHITE);
        contenedor.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        JLabel lbl = new JLabel("Acciones:");
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        contenedor.add(lbl);
        contenedor.add(Box.createVerticalStrut(2));
        contenedor.add(guia("Use Crear para registrar, Actualizar para modificar, Eliminar para quitar y Listar para mostrar la información existente."));
        contenedor.add(Box.createVerticalStrut(6));

        JPanel filaBotones = new JPanel(new GridLayout(1, botones.length, 8, 0));
        filaBotones.setBackground(Color.WHITE);
        for (JButton b : botones) {
            b.setForeground(Color.BLACK);
            filaBotones.add(b);
        }
        filaBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        filaBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenedor.add(filaBotones);

        return contenedor;
    }

    private JScrollPane envolverEnScroll(JPanel panel) {
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JComponent crearPanelCursos() {
        JPanel panel = crearContenedorFormulario("Catálogo de cursos");

        JTextField txtCodigo = new JTextField("101");
        JTextField txtNombre = new JTextField("Manejo defensivo");
        JTextField txtDescripcion = new JTextField("Curso para recuperación de puntos");
        JTextField txtPuntos = new JTextField("20");
        JTextField txtHoras = new JTextField("8");
        JTextField txtMinimo = new JTextField("70");
        JComboBox<String> cbxEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        EstilosInterfaz.configurarComboBox(cbxEstado);

        panel.add(crearSeccionCampo("Número de curso:",
                "Escriba solo el número. Ejemplo: 101. El sistema lo convertirá en CUR-101 automáticamente. No se permiten códigos repetidos.",
                txtCodigo));
        panel.add(crearSeccionCampo("Nombre del curso:",
                "Ingrese el nombre del curso usando texto claro. Ejemplo: Manejo defensivo básico.",
                txtNombre));
        panel.add(crearSeccionCampo("Descripción:",
                "Explique brevemente de qué trata el curso. Ejemplo: Curso para recuperar puntos y mejorar la conducción.",
                txtDescripcion));
        panel.add(crearSeccionCampo("Puntos que recupera:",
                "Ingrese solo números enteros. Ejemplo: 20.",
                txtPuntos));
        panel.add(crearSeccionCampo("Duración en horas:",
                "Ingrese solo números enteros. Ejemplo: 8.",
                txtHoras));
        panel.add(crearSeccionCampo("% mínimo de aprobación:",
                "Ingrese el porcentaje mínimo para aprobar. Ejemplo: 70.",
                txtMinimo));
        panel.add(crearSeccionCampo("Estado:",
                "Seleccione si el curso estará Activo o Inactivo.",
                cbxEstado));

        JButton btnCrear = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnListar = new JButton("Listar");
        panel.add(crearSeccionAcciones(btnCrear, btnActualizar, btnEliminar, btnListar));

        btnCrear.addActionListener(e -> {
            try {
                String codigo = armarCodigoCurso(txtCodigo.getText());
                Curso c = new Curso(codigo, txtNombre.getText().trim(), txtDescripcion.getText().trim(),
                        Integer.parseInt(txtPuntos.getText().trim()), Integer.parseInt(txtHoras.getText().trim()),
                        Double.parseDouble(txtMinimo.getText().trim()), cbxEstado.getSelectedItem().toString());
                gestorCursos.crearCurso(c);
                mostrar("Curso creado correctamente: " + codigo + "\n");
                listarCursos();
                ventanaPrincipal.actualizarTodosPaneles();
            } catch (Exception ex) { mostrarError(ex.getMessage()); }
        });

        btnActualizar.addActionListener(e -> {
            try {
                String codigo = armarCodigoCurso(txtCodigo.getText());
                gestorCursos.actualizarCurso(codigo, txtNombre.getText().trim(), txtDescripcion.getText().trim(),
                        Integer.parseInt(txtPuntos.getText().trim()), Integer.parseInt(txtHoras.getText().trim()),
                        Double.parseDouble(txtMinimo.getText().trim()), cbxEstado.getSelectedItem().toString());
                mostrar("Curso actualizado correctamente: " + codigo + "\n");
                listarCursos();
                ventanaPrincipal.actualizarTodosPaneles();
            } catch (Exception ex) { mostrarError(ex.getMessage()); }
        });

        btnEliminar.addActionListener(e -> {
            try {
                String codigo = armarCodigoCurso(txtCodigo.getText());
                gestorCursos.eliminarCurso(codigo);
                mostrar("Curso eliminado correctamente: " + codigo + "\n");
                listarCursos();
                ventanaPrincipal.actualizarTodosPaneles();
            } catch (Exception ex) { mostrarError(ex.getMessage()); }
        });

        btnListar.addActionListener(e -> listarCursos());

        return envolverEnScroll(panel);
    }

    private JComponent crearPanelPreguntas() {
        JPanel panel = crearContenedorFormulario("Catálogo de preguntas por curso");

        JTextField txtCurso = new JTextField("101");
        JTextField txtId = new JTextField("001");
        JTextField txtTexto = new JTextField("¿Qué significa una luz roja en el semáforo?");
        JTextField txtOpciones = new JTextField("Detenerse;Precaución;Avanzar");
        JTextField txtCorrecta = new JTextField("1");

        panel.add(crearSeccionCampo("Número de curso:",
                "Escriba solo el número del curso. Ejemplo: 101. El sistema usará CUR-101.",
                txtCurso));
        panel.add(crearSeccionCampo("Número/ID de pregunta:",
                "Escriba solo el número. Ejemplo: 001. El sistema usará PREG-001. No se permiten códigos repetidos.",
                txtId));
        panel.add(crearSeccionCampo("Texto de la pregunta:",
                "Escriba la pregunta completa. Ejemplo: ¿Qué significa una luz roja en el semáforo?",
                txtTexto));
        panel.add(crearSeccionCampo("Opciones separadas por ;",
                "Escriba mínimo 2 opciones separadas por punto y coma (;). Ejemplo: Detenerse;Precaución;Avanzar.",
                txtOpciones));
        panel.add(crearSeccionCampo("Respuesta correcta (1,2,3...):",
                "Ingrese el número de la opción correcta. Ejemplo: 1 si la primera opción es la correcta.",
                txtCorrecta));

        JButton btnCrear = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnListar = new JButton("Listar");
        panel.add(crearSeccionAcciones(btnCrear, btnActualizar, btnEliminar, btnListar));

        btnCrear.addActionListener(e -> {
            try {
                String codigoCurso = armarCodigoCurso(txtCurso.getText());
                String codigoPregunta = gestorCursos.normalizarCodigoPregunta(txtId.getText());
                gestorCursos.crearPregunta(codigoCurso, codigoPregunta, txtTexto.getText().trim(),
                        obtenerOpciones(txtOpciones.getText()), Integer.parseInt(txtCorrecta.getText().trim()) - 1);
                mostrar("Pregunta creada correctamente: " + codigoPregunta + " en " + codigoCurso + ".\n");
                listarPreguntas(codigoCurso);
                ventanaPrincipal.actualizarTodosPaneles();
            } catch (Exception ex) { mostrarError(ex.getMessage()); }
        });

        btnActualizar.addActionListener(e -> {
            try {
                String codigoCurso = armarCodigoCurso(txtCurso.getText());
                String codigoPregunta = gestorCursos.normalizarCodigoPregunta(txtId.getText());
                gestorCursos.actualizarPregunta(codigoCurso, codigoPregunta, txtTexto.getText().trim(),
                        obtenerOpciones(txtOpciones.getText()), Integer.parseInt(txtCorrecta.getText().trim()) - 1);
                mostrar("Pregunta actualizada correctamente.\n");
                listarPreguntas(codigoCurso);
                ventanaPrincipal.actualizarTodosPaneles();
            } catch (Exception ex) { mostrarError(ex.getMessage()); }
        });

        btnEliminar.addActionListener(e -> {
            try {
                String codigoCurso = armarCodigoCurso(txtCurso.getText());
                String codigoPregunta = gestorCursos.normalizarCodigoPregunta(txtId.getText());
                gestorCursos.eliminarPregunta(codigoCurso, codigoPregunta);
                mostrar("Pregunta eliminada correctamente.\n");
                listarPreguntas(codigoCurso);
                ventanaPrincipal.actualizarTodosPaneles();
            } catch (Exception ex) { mostrarError(ex.getMessage()); }
        });

        btnListar.addActionListener(e -> {
            try { listarPreguntas(armarCodigoCurso(txtCurso.getText())); }
            catch (Exception ex) { mostrarError(ex.getMessage()); }
        });

        return envolverEnScroll(panel);
    }

    private JComponent crearPanelTiposEvento() {
        JPanel panel = crearContenedorFormulario("Catálogo de tipos de evento / infracción");

        JTextField txtCodigo = new JTextField("007");
        JTextField txtNombre = new JTextField();
        JTextField txtDescripcion = new JTextField();
        JTextField txtPuntos = new JTextField("5");
        JComboBox<String> cbxEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        EstilosInterfaz.configurarComboBox(cbxEstado);

        panel.add(crearSeccionCampo("Número de tipo de evento:",
                "Escriba solo el número. Ejemplo: 007. El sistema agregará INF-007 automáticamente. No se permiten códigos repetidos.",
                txtCodigo));
        panel.add(crearSeccionCampo("Nombre:",
                "Escriba el nombre del tipo de infracción. Ejemplo: Estacionamiento prohibido.",
                txtNombre));
        panel.add(crearSeccionCampo("Descripción:",
                "Ingrese una descripción breve del tipo de evento o infracción.",
                txtDescripcion));
        panel.add(crearSeccionCampo("Puntos:",
                "Ingrese solo números enteros 0 o mayores. Ejemplo: 5. No se permiten puntos negativos.",
                txtPuntos));
        panel.add(crearSeccionCampo("Estado:",
                "Seleccione si el tipo de evento estará Activo o Inactivo.",
                cbxEstado));

        JButton btnCrear = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnListar = new JButton("Listar");
        panel.add(crearSeccionAcciones(btnCrear, btnActualizar, btnEliminar, btnListar));

        btnCrear.addActionListener(e -> {
            try {
                String codigo = armarCodigoTipoEvento(txtCodigo.getText());
                gestorEventos.crearTipoEvento(codigo, txtNombre.getText().trim(),
                        txtDescripcion.getText().trim(), Integer.parseInt(txtPuntos.getText().trim()));
                mostrar("Tipo de evento creado correctamente: " + codigo + "\n");
                listarTiposEvento();
                ventanaPrincipal.actualizarTodosPaneles();
            } catch (Exception ex) { mostrarError(ex.getMessage()); }
        });

        btnActualizar.addActionListener(e -> {
            try {
                String codigo = armarCodigoTipoEvento(txtCodigo.getText());
                gestorEventos.actualizarTipoEvento(codigo, txtNombre.getText().trim(),
                        txtDescripcion.getText().trim(), Integer.parseInt(txtPuntos.getText().trim()), cbxEstado.getSelectedItem().toString());
                mostrar("Tipo de evento actualizado correctamente: " + codigo + "\n");
                listarTiposEvento();
                ventanaPrincipal.actualizarTodosPaneles();
            } catch (Exception ex) { mostrarError(ex.getMessage()); }
        });

        btnEliminar.addActionListener(e -> {
            try {
                String codigo = armarCodigoTipoEvento(txtCodigo.getText());
                gestorEventos.eliminarTipoEvento(codigo, gestorConductores);
                mostrar("Tipo de evento eliminado correctamente: " + codigo + "\n");
                listarTiposEvento();
                ventanaPrincipal.actualizarTodosPaneles();
            } catch (Exception ex) { mostrarError(ex.getMessage()); }
        });

        btnListar.addActionListener(e -> listarTiposEvento());

        return envolverEnScroll(panel);
    }

    private String armarCodigoCurso(String numero) throws PruebaNoValidaException {
        String valor = numero == null ? "" : numero.trim();
        if (valor.toUpperCase().startsWith("CUR-")) valor = valor.substring(4);
        if (!valor.matches("\\d{1,6}")) {
            throw new PruebaNoValidaException("Ingrese solo el número del curso. Ejemplo: 101. El sistema agregará CUR-.");
        }
        return "CUR-" + valor;
    }

    private String armarCodigoTipoEvento(String numero) throws EventoNoValidoException {
        String valor = numero == null ? "" : numero.trim();
        if (valor.toUpperCase().startsWith("INF-")) valor = valor.substring(4);
        if (!valor.matches("\\d{1,6}")) {
            throw new EventoNoValidoException("Ingrese solo el número del tipo de evento. Ejemplo: 007. El sistema agregará INF-.");
        }
        return "INF-" + String.format("%03d", Integer.parseInt(valor));
    }

    private List<String> obtenerOpciones(String texto) {
        List<String> opciones = new ArrayList<>();
        for (String op : texto.split(";")) {
            if (!op.trim().isEmpty()) opciones.add(op.trim());
        }
        return opciones;
    }

    private void listarCursos() {
        StringBuilder sb = new StringBuilder("CURSOS REGISTRADOS\n\n");
        for (Curso c : gestorCursos.listarCursos()) {
            sb.append(c).append(" | Preguntas: ").append(c.getPreguntas().size()).append("\n");
        }
        mostrar(sb.toString());
    }

    private void listarPreguntas(String codigoCurso) {
        try {
            StringBuilder sb = new StringBuilder("PREGUNTAS DEL CURSO " + codigoCurso + "\n\n");
            for (Pregunta p : gestorCursos.listarPreguntas(codigoCurso)) {
                sb.append(p).append("\n");
                int i = 1;
                for (String op : p.getOpciones()) {
                    sb.append("   ").append(i++).append(". ").append(op).append("\n");
                }
                sb.append("   Correcta: ").append(p.getRespuestaCorrecta() + 1).append("\n\n");
            }
            mostrar(sb.toString());
        } catch (Exception e) { mostrarError(e.getMessage()); }
    }

    private void listarTiposEvento() {
        StringBuilder sb = new StringBuilder("TIPOS DE EVENTO REGISTRADOS\n\n");
        for (TipoEvento t : gestorEventos.getTiposEvento()) {
            sb.append(t).append(" | ").append(t.getDescripcion()).append("\n");
        }
        mostrar(sb.toString());
    }

    private void mostrar(String texto) { txtSalida.setText(texto); }
    private void mostrarError(String mensaje) { JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE); }
}
