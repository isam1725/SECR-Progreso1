package estructuras.comparacion;

import modelo.EventoConduccion;

import java.time.LocalDate;
import java.util.List;

/** Contrato común para comparar el componente "historial de eventos por rango de fechas". */
public interface IndiceEventos {
    void agregar(EventoConduccion e);
    List<EventoConduccion> consultarRango(LocalDate desde, LocalDate hasta);
    int tamanio();
    String nombre();
}
