package estructuras.comparacion;

import estructuras.BusquedaBinaria;
import modelo.EventoConduccion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ALTERNATIVA A - Lista ordenada por fecha.
 * agregar O(n) (desplazamiento), consultarRango O(log n + k).
 */
public class IndiceEventosA implements IndiceEventos {

    private final List<EventoConduccion> eventos = new ArrayList<>();

    @Override
    public void agregar(EventoConduccion e) {
        // Se inserta después de los eventos con la misma fecha (estable).
        int pos = BusquedaBinaria.limiteInferior(eventos, e.getFechaHora().plusDays(1),
                EventoConduccion::getFechaHora, Comparator.naturalOrder());
        eventos.add(pos, e);
    }

    @Override
    public List<EventoConduccion> consultarRango(LocalDate desde, LocalDate hasta) {
        List<EventoConduccion> res = new ArrayList<>();
        if (desde == null || hasta == null || desde.isAfter(hasta)) return res;
        int i = BusquedaBinaria.limiteInferior(eventos, desde, EventoConduccion::getFechaHora, Comparator.naturalOrder());
        while (i < eventos.size() && !eventos.get(i).getFechaHora().isAfter(hasta)) res.add(eventos.get(i++));
        return res;
    }

    @Override public int tamanio() { return eventos.size(); }
    @Override public String nombre() { return "A (lista ordenada + búsqueda binaria)"; }
}
