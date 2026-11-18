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
 * posicion(licencia): O(log n) con el tamaño de subárbol del AVL.
 * Memoria: un número fijo de nodos por conductor, sin importar cuántas actualizaciones haya.
 */
public class RankingRiesgoAVL implements RankingRiesgo {

    private final ArbolAVL<String, Conductor> porLicencia = new ArbolAVL<>();
    private final ArbolAVL<ClaveRiesgo, Conductor> porRiesgo = new ArbolAVL<>();
    /** Puntaje con el que cada conductor está indexado en porRiesgo (para poder ubicar su clave vieja). */
    private final ArbolAVL<String, Double> puntajeIndexado = new ArbolAVL<>();

    private static String lic(Conductor c) { return c.getLicencia().getNumero(); }

    @Override
    public void registrar(Conductor c) {
        if (porLicencia.contiene(lic(c))) throw new IllegalArgumentException("Licencia duplicada: " + lic(c));
        porLicencia.insertar(lic(c), c);
        porRiesgo.insertar(new ClaveRiesgo(c.getPuntajeActual(), lic(c)), c);
        puntajeIndexado.insertar(lic(c), c.getPuntajeActual());
    }

    public boolean contiene(String licencia) { return porLicencia.contiene(licencia); }

    /**
     * Reubica a un conductor cuyo puntaje ya cambió en el modelo (p. ej. tras registrar o deshacer
     * una infracción): elimina la clave indexada anterior e inserta la nueva. O(log n).
     */
    public void reubicar(Conductor c) {
        String l = lic(c);
        Double anterior = puntajeIndexado.buscar(l);
        if (anterior == null) { registrar(c); return; }
        if (anterior == c.getPuntajeActual()) return;
        porRiesgo.eliminar(new ClaveRiesgo(anterior, l));
        porRiesgo.insertar(new ClaveRiesgo(c.getPuntajeActual(), l), c);
        puntajeIndexado.insertar(l, c.getPuntajeActual());
    }

    /** Quita a un conductor del ranking. O(log n). */
    public boolean eliminar(String licencia) {
        Double p = puntajeIndexado.buscar(licencia);
        if (p == null) return false;
        porRiesgo.eliminar(new ClaveRiesgo(p, licencia));
        porLicencia.eliminar(licencia);
        puntajeIndexado.eliminar(licencia);
        return true;
    }

    /** Posición en el ranking (1 = mayor riesgo) usando el tamaño de subárbol del AVL: O(log n). -1 si no está. */
    public int posicion(String licencia) {
        Double p = puntajeIndexado.buscar(licencia);
        return p == null ? -1 : porRiesgo.posicion(new ClaveRiesgo(p, licencia));
    }

    /** Licencias registradas, en orden alfabético (recorrido en orden). */
    public List<String> licencias() { return porLicencia.clavesEnOrden(); }

    @Override
    public Conductor buscar(String licencia) { return porLicencia.buscar(licencia); }

    @Override
    public void actualizarPuntaje(String licencia, double nuevoPuntaje) {
        Conductor c = porLicencia.buscar(licencia);
        if (c == null) throw new IllegalArgumentException("No existe " + licencia);
        porRiesgo.eliminar(new ClaveRiesgo(puntajeIndexado.buscar(licencia), licencia));
        Puntajes.asignar(c, nuevoPuntaje);
        porRiesgo.insertar(new ClaveRiesgo(nuevoPuntaje, licencia), c);
        puntajeIndexado.insertar(licencia, nuevoPuntaje);
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
