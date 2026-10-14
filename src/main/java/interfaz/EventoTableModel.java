package interfaz;

import modelo.Conductor;
import modelo.EventoConduccion;
import negocio.GestorConductores;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de tabla para mostrar eventos de conducción.
 * Implementa AbstractTableModel para sincronizar datos con JTable.
 */
public class EventoTableModel extends AbstractTableModel {

    private List<EventoConduccion> eventos;
    private GestorConductores gestorConductores;
    private final String[] nombreColumnas = {
            "ID", "Licencia", "Conductor", "Fecha", "Tipo",
            "Gravedad", "Clasificación", "Deducción", "Ubicación"
    };

    public EventoTableModel(GestorConductores gestorConductores) {
        this.gestorConductores = gestorConductores;
        this.eventos = new ArrayList<>();
        cargarTodosLosEventos();
    }

    private void cargarTodosLosEventos() {
        eventos.clear();
        for (Conductor c : gestorConductores.listarTodos()) {
            eventos.addAll(c.getInfracciones());
        }
    }

    @Override
    public int getRowCount() {
        return eventos.size();
    }

    @Override
    public int getColumnCount() {
        return nombreColumnas.length;
    }

    @Override
    public String getColumnName(int column) {
        return nombreColumnas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EventoConduccion e = eventos.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> e.getIdEvento();
            case 1 -> obtenerLicenciaDelEvento(e);
            case 2 -> obtenerNombreConductorDelEvento(e);
            case 3 -> e.getFechaHora();
            case 4 -> e.getTipo().getNombre();
            case 5 -> e.getGravedad() + "/5";
            case 6 -> e.clasificarGravedad();
            case 7 -> "-" + e.calcularPuntosDeduccion() + " pts";
            case 8 -> e.getUbicacion();
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Obtiene el evento de una fila específica
     */
    public EventoConduccion getEventoAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < eventos.size()) {
            return eventos.get(rowIndex);
        }
        return null;
    }

    /**
     * Actualiza la lista de eventos y notifica a la tabla
     */
    public void actualizarDatos() {
        cargarTodosLosEventos();
        fireTableDataChanged();
    }

    /**
     * Obtiene la lista actual de eventos
     */
    public List<EventoConduccion> getEventos() {
        return new ArrayList<>(eventos);
    }

    /**
     * Helper: obtiene licencia del conductor que cometió el evento
     */
    private String obtenerLicenciaDelEvento(EventoConduccion evento) {
        for (Conductor c : gestorConductores.listarTodos()) {
            if (c.getInfracciones().contains(evento)) {
                return c.getLicencia().getNumero();
            }
        }
        return "Desconocida";
    }

    /**
     * Helper: obtiene nombre del conductor que cometió el evento
     */
    private String obtenerNombreConductorDelEvento(EventoConduccion evento) {
        for (Conductor c : gestorConductores.listarTodos()) {
            if (c.getInfracciones().contains(evento)) {
                return c.getNombreCompleto();
            }
        }
        return "Desconocido";
    }
}

