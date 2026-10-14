package estructuras.comparacion;

import estructuras.ArbolAVL;
import modelo.Conductor;

import java.util.List;

/**
 * ALTERNATIVA A — Dos árboles AVL.
 * <ul>
 *   <li>Árbol 1: licencia → conductor (búsqueda O(log n)).</li>
 *   <li>Árbol 2: (puntaje, licencia) → conductor, ordenado por riesgo.</li>
 * </ul>
 * actualizarPuntaje = eliminar la clave vieja + insertar la nueva: O(log n).
 * topRiesgo(k): recorrido en orden desde el mínimo, O(log n + k).
 * enRangoPuntaje: recorrido en orden podado, O(log n + k), resultado ordenado.
 * Memoria: exactamente 2 nodos por conductor, sin importar cuántas actualizaciones haya.
 */
public class RankingRiesgoAVL implements RankingRiesgo {

    private final ArbolAVL<String, Conductor> porLicencia = new ArbolAVL<>();
    private final ArbolAVL<ClaveRiesgo, Conductor> porRiesgo = new ArbolAVL<>();

    private static String lic(Conductor c) { return c.getLicencia().getNumero(); }

    @Override
    public void registrar(Conductor c) {
        if (porLicencia.contiene(lic(c))) throw new IllegalArgumentException("Licencia duplicada: " + lic(c));
        porLicencia.insertar(lic(c), c);
        porRiesgo.insertar(new ClaveRiesgo(c.getPuntajeActual(), lic(c)), c);
    }

    @Override
    public Conductor buscar(String licencia) { return porLicencia.buscar(licencia); }

    @Override
    public void actualizarPuntaje(String licencia, double nuevoPuntaje) {
        Conductor c = porLicencia.buscar(licencia);
        if (c == null) throw new IllegalArgumentException("No existe " + licencia);
        porRiesgo.eliminar(new ClaveRiesgo(c.getPuntajeActual(), licencia));
        Puntajes.asignar(c, nuevoPuntaje);
        porRiesgo.insertar(new ClaveRiesgo(nuevoPuntaje, licencia), c);
    }

    @Override
    public List<Conductor> topRiesgo(int k) { return porRiesgo.primeros(k); }

    @Override
    public List<Conductor> enRangoPuntaje(double min, double max) {
        return porRiesgo.rango(new ClaveRiesgo(min, ""), new ClaveRiesgo(max, "￿"));
    }

    @Override public int tamanio() { return porLicencia.tamanio(); }
    @Override public String nombre() { return "A (árboles AVL)"; }
}
