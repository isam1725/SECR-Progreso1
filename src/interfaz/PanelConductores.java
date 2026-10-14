package interfaz;

import static modelo.Main.*;
import modelo.Conductor;
import modelo.Licencia;
import modelo.Validaciones;
import negocio.GestorConductores;
import negocio.GestorEventos;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Panel para gestión de conductores (CRUD).
 * Permite registrar, consultar, actualizar, cambiar estado y eliminar conductores.
 */
public class PanelConductores extends JPanel {

    private GestorConductores gestorConductores;
    private GestorEventos gestorEventos;
    private VentanaPrincipal ventanaPrincipal;
    private ConductorTableModel modeloTabla;

    // Componentes del formulario
    private JTextField txtCedula;
    private JTextField txtNombre;
    private JSpinner spnFechaNacimiento;
    private JTextField txtNumeroLicencia;
    private JComboBox<String> cbxCategoria;
    private JSpinner spnFechaEmision;
    private JTable tablaControl;
    private JLabel lblPuntaje;
    private JLabel lblEstado;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Conductor conductorSeleccionado;

    public PanelConductores(GestorConductores gestorConductores, GestorEventos gestorEventos,
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

        // Split pane para dividir formulario y tabla
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelFormulario, panelTabla);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.4);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelFormulario() {
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "REGISTRO Y ACTUALIZACIÓN DE CONDUCTORES", 0, 0, new Font("Arial", Font.BOLD, 12)));
        formulario.setBackground(Color.WHITE);

        // Sección: Datos Personales
        formulario.add(crearEtiqueta("DATOS PERSONALES"));
        formulario.add(crearCampoTexto("Cédula (10 números):", "txt_cedula"));
        formulario.add(crearCampoTexto("Nombre Completo:", "txt_nombre"));
        formulario.add(crearCampoFecha("Fecha de Nacimiento (dd/MM/yyyy):", "spn_nacimiento"));

        formulario.add(Box.createVerticalStrut(15));

        // Sección: Datos de Licencia
        formulario.add(crearEtiqueta("DATOS DE LICENCIA"));
        formulario.add(crearCampoTexto("Número de Licencia (solo números, ej: 001):", "txt_licencia"));
        
        formulario.add(crearEtiquetaCampo("Categoría:"));
        cbxCategoria = new JComboBox<>(new String[]{"B", "C", "E"});
        cbxCategoria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        EstilosInterfaz.configurarComboBox(cbxCategoria);
        formulario.add(cbxCategoria);
        formulario.add(Box.createVerticalStrut(10));

        formulario.add(crearCampoFecha("Fecha de Emisión de la Licencia (dd/MM/yyyy):", "spn_emision"));
        JLabel guiaEmision = crearEtiquetaCampo("Guía: fecha en que se emitió/entregó la licencia. Debe ser cuando el conductor ya tenga mínimo 18 años.");
        guiaEmision.setForeground(new Color(90, 90, 90));
        formulario.add(guiaEmision);

        formulario.add(Box.createVerticalStrut(15));

        // Sección: Información Actual
        formulario.add(crearEtiqueta("INFORMACIÓN ACTUAL"));
        
        JPanel panelInfo = new JPanel(new GridLayout(2, 2, 10, 10));
        panelInfo.setBackground(Color.WHITE);
        
        panelInfo.add(crearEtiquetaCampo("Puntaje:"));
        lblPuntaje = new JLabel("---");
        lblPuntaje.setFont(new Font("Arial", Font.BOLD, 12));
        lblPuntaje.setForeground(new Color(52, 152, 219));
        panelInfo.add(lblPuntaje);
        
        panelInfo.add(crearEtiquetaCampo("Estado:"));
        lblEstado = new JLabel("---");
        lblEstado.setFont(new Font("Arial", Font.BOLD, 12));
        lblEstado.setForeground(new Color(46, 204, 113));
        panelInfo.add(lblEstado);
        
        formulario.add(panelInfo);

        formulario.add(Box.createVerticalStrut(15));

        // Botones de acción
        JPanel panelBotones = crearPanelBotones();
        formulario.add(panelBotones);

        formulario.add(Box.createVerticalGlue());

        JPanel panelConScroll = new JPanel(new BorderLayout());
        panelConScroll.setBackground(Color.WHITE);
        panelConScroll.add(new JScrollPane(formulario), BorderLayout.CENTER);
        
        return panelConScroll;
    }

    private JPanel crearCampoTexto(String etiqueta, String id) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setPreferredSize(new Dimension(180, 30));
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));

        JTextField txt = new JTextField();
        txt.setFont(new Font("Arial", Font.PLAIN, 11));

        if (id.equals("txt_cedula")) txtCedula = txt;
        else if (id.equals("txt_nombre")) txtNombre = txt;
        else if (id.equals("txt_licencia")) txtNumeroLicencia = txt;

        panel.add(lbl, BorderLayout.WEST);
        panel.add(txt, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearCampoFecha(String etiqueta, String id) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setPreferredSize(new Dimension(180, 30));
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));

        JSpinner spn = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spn, "dd/MM/yyyy");
        spn.setEditor(editor);

        if (id.equals("spn_nacimiento")) spnFechaNacimiento = spn;
        else if (id.equals("spn_emision")) spnFechaEmision = spn;

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
        return lbl;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panel.setBackground(Color.WHITE);

        JButton btnRegistrar = crearBoton("Registrar Conductor", "REGISTRAR");
        JButton btnBuscar = crearBoton("Buscar por Licencia", "BUSCAR");
        JButton btnActualizar = crearBoton("Actualizar Nombre", "ACTUALIZAR_NOMBRE");
        JButton btnCambiarEstado = crearBoton("Cambiar Estado", "CAMBIAR_ESTADO");
        JButton btnEliminar = crearBoton("Eliminar", "ELIMINAR");

        panel.add(btnRegistrar);
        panel.add(btnBuscar);
        panel.add(btnActualizar);
        panel.add(btnCambiarEstado);
        panel.add(btnEliminar);

        btnRegistrar.addActionListener(e -> registrarConductor());
        btnBuscar.addActionListener(e -> buscarConductor());
        btnActualizar.addActionListener(e -> actualizarNombre());
        btnCambiarEstado.addActionListener(e -> cambiarEstado());
        btnEliminar.addActionListener(e -> eliminarConductor());

        return panel;
    }

    private JButton crearBoton(String texto, String accion) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.PLAIN, 11));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "LISTA DE CONDUCTORES", 0, 0, new Font("Arial", Font.BOLD, 12)));
        panel.setBackground(Color.WHITE);

        // Tabla
        modeloTabla = new ConductorTableModel(gestorConductores.listarTodos());
        tablaControl = new JTable(modeloTabla);
        tablaControl.setFont(new Font("Arial", Font.PLAIN, 10));
        tablaControl.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tablaControl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaControl.setRowHeight(25);
        EstilosInterfaz.configurarTabla(tablaControl);

        // Listener para seleccionar fila
        tablaControl.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaControl.getSelectedRow() >= 0) {
                cargarDatosConductor();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaControl);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botón para actualizar tabla
        JPanel panelBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelBot.setBackground(Color.WHITE);

        JButton btnActualizarTabla = new JButton("Actualizar Tabla");
        btnActualizarTabla.setFont(new Font("Arial", Font.PLAIN, 11));
        btnActualizarTabla.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizarTabla.addActionListener(e -> actualizarTabla());

        panelBot.add(btnActualizarTabla);
        panel.add(panelBot, BorderLayout.SOUTH);

        return panel;
    }

    private void registrarConductor() {
        try {
            String cedula = validarYObtener(txtCedula, "Cédula");
            String nombre = validarYObtener(txtNombre, "Nombre Completo");
            LocalDate fechaNac = obtenerFechaSpinner(spnFechaNacimiento);
            String categoria = (String) cbxCategoria.getSelectedItem();
            String numeroSolo = validarYObtener(txtNumeroLicencia, "Número de Licencia");
            if (!numeroSolo.matches("\\d{3,6}")) {
                throw new LicenciaNoValidaException("Ingrese solo el número de licencia, sin letras ni guion. Ejemplo: 001. El sistema agregará automáticamente " + categoria + "-.");
            }
            String numLic = Validaciones.normalizarNumeroLicencia(numeroSolo, categoria);
            LocalDate emision = obtenerFechaSpinner(spnFechaEmision);

            // Validaciones solicitadas por la docente
            Validaciones.validarCedula(cedula);
            Validaciones.validarNombre(nombre);
            Validaciones.validarEdadMayorEdad(fechaNac);
            Validaciones.validarNumeroLicencia(numLic);
            Validaciones.validarCategoria(categoria);
            Validaciones.validarFechaEmision(emision, fechaNac);

            // Crear licencia y conductor
            Licencia lic = new Licencia(numLic, categoria, emision);
            Conductor c = new Conductor(cedula, nombre, fechaNac, lic);

            // Registrar
            gestorConductores.registrarConductor(c);

            mostrarMensaje("Conductor registrado exitosamente.\n" +
                    "Edad: " + c.getEdad() + " años\n" +
                    "Licencia vence: " + lic.getFechaVencimiento());

            limpiarCampos();
            actualizarTabla();
            ventanaPrincipal.actualizarTodosPaneles();

        } catch (EdadNoValidaException | LicenciaDuplicadaException | LicenciaNoValidaException e) {
            mostrarError(e.getMessage());
        } catch (SecrException e) {
            mostrarError("Error: " + e.getMessage());
        } catch (DateTimeParseException e) {
            mostrarError("Formato de fecha inválido. Use: dd/MM/yyyy");
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private void buscarConductor() {
        try {
            String lic = JOptionPane.showInputDialog(this, "Ingrese número de licencia:");
            if (lic == null || lic.trim().isEmpty()) return;

            Conductor c = gestorConductores.obtenerConductorObligatorio(lic.trim());

            // Cargar datos en el formulario
            txtCedula.setText(c.getCedula());
            txtNombre.setText(c.getNombreCompleto());
            setFechaSpinner(spnFechaNacimiento, c.getFechaNacimiento());
            txtNumeroLicencia.setText(c.getLicencia().getNumero().replaceFirst("^[BCE]-", ""));
            cbxCategoria.setSelectedItem(c.getLicencia().getCategoria());
            setFechaSpinner(spnFechaEmision, c.getLicencia().getFechaEmision());

            lblPuntaje.setText(String.format("%.1f pts", c.getPuntajeActual()));
            lblEstado.setText(c.getEstado());

            conductorSeleccionado = c;

            // Seleccionar en tabla
            List<Conductor> todos = gestorConductores.listarTodos();
            for (int i = 0; i < todos.size(); i++) {
                if (todos.get(i).getLicencia().getNumero().equals(lic.trim())) {
                    tablaControl.setRowSelectionInterval(i, i);
                    break;
                }
            }

        } catch (ConductorNoEncontradoException e) {
            mostrarError(e.getMessage());
        }
    }

    private void actualizarNombre() {
        try {
            if (conductorSeleccionado == null) {
                mostrarError("Seleccione un conductor de la tabla primero.");
                return;
            }

            String nuevoNombre = validarYObtener(txtNombre, "Nombre Completo");
            gestorConductores.actualizarConductor(
                    conductorSeleccionado.getLicencia().getNumero(),
                    nuevoNombre,
                    null,
                    null
            );

            mostrarMensaje("Nombre actualizado exitosamente.");
            actualizarTabla();
            ventanaPrincipal.actualizarTodosPaneles();

        } catch (SecrException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void cambiarEstado() {
        try {
            if (conductorSeleccionado == null) {
                mostrarError("Seleccione un conductor de la tabla primero.");
                return;
            }

            String estadoActual = conductorSeleccionado.getEstado();
            String nuevoEstado = "Activo".equalsIgnoreCase(estadoActual) ? "Inactivo" : "Activo";

            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Cambiar estado a " + nuevoEstado + "?",
                    "Confirmar cambio de estado",
                    JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                conductorSeleccionado.cambiarEstado(nuevoEstado);
                lblEstado.setText(conductorSeleccionado.getEstado());

                mostrarMensaje("Estado cambiado a: " + nuevoEstado);
                actualizarTabla();
                ventanaPrincipal.actualizarTodosPaneles();
            }

        } catch (EstadoInvalidoException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void eliminarConductor() {
        try {
            if (conductorSeleccionado == null) {
                mostrarError("Seleccione un conductor de la tabla primero.");
                return;
            }

            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea eliminar a " + conductorSeleccionado.getNombreCompleto() + "?\n" +
                    "Esta acción no se puede deshacer.",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (respuesta == JOptionPane.YES_OPTION) {
                gestorConductores.eliminarConductor(conductorSeleccionado.getLicencia().getNumero());

                mostrarMensaje("Conductor eliminado exitosamente.");
                limpiarCampos();
                actualizarTabla();
                ventanaPrincipal.actualizarTodosPaneles();
            }

        } catch (ConductorNoEncontradoException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void cargarDatosConductor() {
        int fila = tablaControl.getSelectedRow();
        if (fila >= 0) {
            Conductor c = modeloTabla.getConductorAt(fila);
            if (c != null) {
                txtCedula.setText(c.getCedula());
                txtNombre.setText(c.getNombreCompleto());
                setFechaSpinner(spnFechaNacimiento, c.getFechaNacimiento());
                txtNumeroLicencia.setText(c.getLicencia().getNumero().replaceFirst("^[BCE]-", ""));
                cbxCategoria.setSelectedItem(c.getLicencia().getCategoria());
                setFechaSpinner(spnFechaEmision, c.getLicencia().getFechaEmision());

                lblPuntaje.setText(String.format("%.1f pts", c.getPuntajeActual()));
                lblEstado.setText(c.getEstado());

                conductorSeleccionado = c;
            }
        }
    }

    public void actualizarTabla() {
        modeloTabla.actualizarDatos(gestorConductores.listarTodos());
    }

    public void actualizarDatos() {
        actualizarTabla();
        limpiarCampos();
    }

    private void limpiarCampos() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtNumeroLicencia.setText("");
        cbxCategoria.setSelectedIndex(0);
        lblPuntaje.setText("---");
        lblEstado.setText("---");
        conductorSeleccionado = null;
        tablaControl.clearSelection();
    }

    private String validarYObtener(JTextField txt, String nombre) throws IllegalArgumentException {
        String valor = txt.getText().trim();
        if (valor.isEmpty()) {
            throw new IllegalArgumentException(nombre + " no puede estar vacío.");
        }
        return valor;
    }

    private LocalDate obtenerFechaSpinner(JSpinner spn) throws DateTimeParseException {
        Object valor = spn.getValue();
        if (valor instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) valor;
            return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }
        throw new DateTimeParseException("Formato de fecha inválido.", "", 0);
    }

    private void setFechaSpinner(JSpinner spn, LocalDate fecha) {
        java.util.Date date = java.sql.Date.valueOf(fecha);
        spn.setValue(date);
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}



