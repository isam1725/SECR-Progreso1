package interfaz;

import modelo.Conductor;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de tabla para mostrar conductores.
 * Implementa AbstractTableModel para sincronizar datos con JTable.
 */
public class ConductorTableModel extends AbstractTableModel {

    private List<Conductor> conductores;
    private final String[] nombreColumnas = {
            "Cédula", "Nombre", "Edad", "Licencia", "Categoría",
            "Emisión", "Vencimiento", "Puntaje", "Estado"
    };

    public ConductorTableModel(List<Conductor> conductores) {
        this.conductores = new ArrayList<>(conductores);
    }

    @Override
    public int getRowCount() {
        return conductores.size();
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
        Conductor c = conductores.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> c.getCedula();
            case 1 -> c.getNombreCompleto();
            case 2 -> c.getEdad();
            case 3 -> c.getLicencia().getNumero();
            case 4 -> c.getLicencia().getCategoria();
            case 5 -> c.getLicencia().getFechaEmision();
            case 6 -> c.getLicencia().getFechaVencimiento();
            case 7 -> String.format("%.1f", c.getPuntajeActual());
            case 8 -> c.getEstado();
            default -> "";
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Las celdas no son editables directamente desde la tabla
    }

    /**
     * Obtiene el conductor de una fila específica
     */
    public Conductor getConductorAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < conductores.size()) {
            return conductores.get(rowIndex);
        }
        return null;
    }

    /**
     * Actualiza la lista de conductores y notifica a la tabla
     */
    public void actualizarDatos(List<Conductor> nuevosConductores) {
        this.conductores = new ArrayList<>(nuevosConductores);
        fireTableDataChanged();
    }

    /**
     * Obtiene la lista actual de conductores
     */
    public List<Conductor> getConductores() {
        return new ArrayList<>(conductores);
    }
}

