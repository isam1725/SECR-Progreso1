package estructuras;

import modelo.EventoConduccion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Índice de eventos de conducción ordenado por fecha.
 * Se apoya en {@link TreeMap} (árbol rojo-negro de la JCF, Guibas y Sedgewick, 1978):
 * <ul>
 *   <li>agregar / eliminar: O(log d), con d = fechas distintas.</li>
 *   <li>consultarRango(desde, hasta): O(log d + k), con k = eventos devueltos.</li>
 * </ul>
 */
public class IndiceTemporalEventos {

    private final TreeMap<LocalDate, List<EventoConduccion>> porFecha = new TreeMap<>();
    private int total;

    public void agregar(EventoConduccion e) {
        porFecha.computeIfAbsent(e.getFechaHora(), f -> new ArrayList<>()).add(e);
        total++;
    }

    public boolean eliminar(EventoConduccion e) {
        List<EventoConduccion> lista = porFecha.get(e.getFechaHora());
        if (lista == null || !lista.remove(e)) return false;
        if (lista.isEmpty()) porFecha.remove(e.getFechaHora());
        total--;
        return true;
    }

    /** Eventos entre dos fechas (ambas inclusive), en orden cronológico. */
    public List<EventoConduccion> consultarRango(LocalDate desde, LocalDate hasta) {
        List<EventoConduccion> resultado = new ArrayList<>();
        if (desde == null || hasta == null || desde.isAfter(hasta)) return resultado;
        NavigableMap<LocalDate, List<EventoConduccion>> sub = porFecha.subMap(desde, true, hasta, true);
        for (Collection<EventoConduccion> lista : sub.values()) resultado.addAll(lista);
        return resultado;
    }

    public int getTotal() { return total; }
}
