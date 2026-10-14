package estructuras.comparacion;

import modelo.Conductor;

import java.util.List;

/**
 * Contrato común del componente crítico "priorización de conductores por riesgo" (módulo M3),
 * para comparar las Alternativas A y B con exactamente las mismas operaciones.
 */
public interface RankingRiesgo {
    void registrar(Conductor c);
    Conductor buscar(String licencia);
    /** Cambia el puntaje (tras una infracción o un curso) y reubica al conductor en el orden de riesgo. */
    void actualizarPuntaje(String licencia, double nuevoPuntaje);
    /** Los k conductores de mayor riesgo (menor puntaje), en orden. */
    List<Conductor> topRiesgo(int k);
    /** Conductores con puntaje en [min, max] (p. ej. semáforo rojo: [0, 40)). */
    List<Conductor> enRangoPuntaje(double min, double max);
    int tamanio();
    String nombre();
}
