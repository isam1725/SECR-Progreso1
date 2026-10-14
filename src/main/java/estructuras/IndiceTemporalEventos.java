package estructuras;

import modelo.EventoConduccion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Índice de eventos de conducción ordenado por fecha sobre un {@link ArbolAVL}
 * (fecha → lista de eventos de ese día).
 * <ul>
 *   <li>agregar / eliminar: O(log d), con d = fechas distintas</li>
 *   <li>consultarRango(desde, hasta): O(log d + k), con k = eventos devueltos</li>
 * </ul>
 */
public class IndiceTemporalEventos {

    private final ArbolAVL<LocalDate, List<EventoConduccion>> porFecha = new ArbolAVL<>();
    private int total;

    public void agregar(EventoConduccion e) {
        porFecha.obtenerOInsertar(e.getFechaHora(), ArrayList::new).add(e);
        total++;
    }

    public boolean eliminar(EventoConduccion e) {
        List<EventoConduccion> lista = porFecha.buscar(e.getFechaHora());
        if (lista == null || !lista.remove(e)) return false;
        if (lista.isEmpty()) porFecha.eliminar(e.getFechaHora());
        total--;
        return true;
    }

    /** Eventos entre dos fechas (ambas inclusive), en orden cronológico. */
    public List<EventoConduccion> consultarRango(LocalDate desde, LocalDate hasta) {
        List<EventoConduccion> res = new ArrayList<>();
        for (List<EventoConduccion> dia : porFecha.rango(desde, hasta)) res.addAll(dia);
        return res;
    }

    public int getTotal() { return total; }
}
