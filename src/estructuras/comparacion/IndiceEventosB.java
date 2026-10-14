package estructuras.comparacion;

import estructuras.IndiceTemporalEventos;
import modelo.EventoConduccion;

import java.time.LocalDate;
import java.util.List;

/** ALTERNATIVA B - Árbol rojo-negro (TreeMap) indexado por fecha. agregar O(log d), rango O(log d + k). */
public class IndiceEventosB implements IndiceEventos {

    private final IndiceTemporalEventos indice = new IndiceTemporalEventos();

    @Override public void agregar(EventoConduccion e) { indice.agregar(e); }
    @Override public List<EventoConduccion> consultarRango(LocalDate d, LocalDate h) { return indice.consultarRango(d, h); }
    @Override public int tamanio() { return indice.getTotal(); }
    @Override public String nombre() { return "B (árbol rojo-negro / TreeMap)"; }
}
