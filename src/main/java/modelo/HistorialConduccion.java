package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Historial de conducción del conductor.
 * Registra infracciones, recuperaciones y bonificaciones por buen comportamiento.
 */
public class HistorialConduccion {
    private List<EventoConduccion> eventos;
    private double puntajeInicial;
    private double puntosRecuperacion;
    private double bonificacionBuenComportamiento;

    public HistorialConduccion() {
        this.eventos = new ArrayList<>();
        this.puntajeInicial = 100.0;
        this.puntosRecuperacion = 0.0;
        this.bonificacionBuenComportamiento = 0.0;
    }

    public void agregarEvento(EventoConduccion e) { eventos.add(e); }
    public void agregarRecuperacion(double puntos) { puntosRecuperacion += puntos; }
    public void setBonificacionBuenComportamiento(double puntos) { bonificacionBuenComportamiento = Math.max(0, puntos); }

    public double calcularPuntajeFinal() {
        double deduccion = 0;
        for (EventoConduccion e : eventos) {
            deduccion += e.calcularPuntosDeduccion();
        }
        return Math.max(0, Math.min(100, puntajeInicial - deduccion + puntosRecuperacion + bonificacionBuenComportamiento));
    }

    public List<EventoConduccion> getEventos() { return eventos; }
    public int getCantidadEventos() { return eventos.size(); }
    public double getPuntosRecuperacion() { return puntosRecuperacion; }
    public double getBonificacionBuenComportamiento() { return bonificacionBuenComportamiento; }
}
