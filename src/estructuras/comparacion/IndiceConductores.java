package estructuras.comparacion;

import modelo.Conductor;

import java.util.List;

/**
 * Contrato común para comparar las Alternativas A y B del componente
 * "registro, búsqueda y priorización de conductores".
 */
public interface IndiceConductores {
    void registrar(Conductor c);
    Conductor buscarPorLicencia(String licencia);
    List<Conductor> buscarPorPrefijoNombre(String prefijo, int limite);
    void actualizarPuntaje(String licencia, double nuevoPuntaje);
    /** Los k conductores de mayor riesgo (menor puntaje). */
    List<Conductor> topRiesgo(int k);
    int tamanio();
    String nombre();
}
