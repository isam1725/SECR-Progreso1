package estructuras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

/**
 * Montículo binario mínimo indexado (indexed min-priority queue).
 * <p>
 * Mantiene claves (p. ej. números de licencia) ordenadas por prioridad (p. ej. puntaje):
 * el menor puntaje = mayor riesgo queda siempre en la raíz. Un mapa clave → posición
 * permite cambiar la prioridad de un conductor cuando se registra una infracción o
 * aprueba un curso sin reconstruir la estructura.
 * <ul>
 *   <li>insertar, actualizar, eliminar, extraerMinimo: O(log n)</li>
 *   <li>verMinimo, contiene, prioridad: O(1)</li>
 *   <li>topK (no destructivo): O(k log k)</li>
 *   <li>espacio: O(n)</li>
 * </ul>
 * En empates de prioridad se ordena por clave para que los resultados sean deterministas.
 * Referencias: Williams (1964); Sedgewick y Wayne (2011), sección 2.4.
 */
public class MonticuloIndexado {

    private final List<String> claves = new ArrayList<>();
    private final List<Double> prioridades = new ArrayList<>();
    private final Map<String, Integer> posicion = new HashMap<>();

    public int tamanio() { return claves.size(); }
    public boolean estaVacio() { return claves.isEmpty(); }
    public boolean contiene(String clave) { return posicion.containsKey(clave); }

    public double prioridad(String clave) {
        Integer i = posicion.get(clave);
        if (i == null) throw new NoSuchElementException("No existe la clave " + clave);
        return prioridades.get(i);
    }

    public void insertar(String clave, double prioridad) {
        if (clave == null) throw new IllegalArgumentException("La clave no puede ser nula.");
        if (contiene(clave)) throw new IllegalArgumentException("La clave ya existe: " + clave);
        claves.add(clave);
        prioridades.add(prioridad);
        posicion.put(clave, claves.size() - 1);
        subir(claves.size() - 1);
    }

    /** Cambia la prioridad de una clave existente (sube o baja según corresponda). */
    public void actualizar(String clave, double nuevaPrioridad) {
        Integer i = posicion.get(clave);
        if (i == null) throw new NoSuchElementException("No existe la clave " + clave);
        double anterior = prioridades.get(i);
        prioridades.set(i, nuevaPrioridad);
        if (nuevaPrioridad < anterior) subir(i); else bajar(i);
    }

    /** Inserta la clave o actualiza su prioridad si ya existe. */
    public void insertarOActualizar(String clave, double prioridad) {
        if (contiene(clave)) actualizar(clave, prioridad); else insertar(clave, prioridad);
    }

    public void eliminar(String clave) {
        Integer i = posicion.get(clave);
        if (i == null) throw new NoSuchElementException("No existe la clave " + clave);
        int ultimo = claves.size() - 1;
        intercambiar(i, ultimo);
        claves.remove(ultimo);
        prioridades.remove(ultimo);
        posicion.remove(clave);
        if (i < claves.size()) {
            subir(i);
            bajar(i);
        }
    }

    public String verMinimo() {
        if (estaVacio()) throw new NoSuchElementException("El montículo está vacío.");
        return claves.get(0);
    }

    public String extraerMinimo() {
        String min = verMinimo();
        eliminar(min);
        return min;
    }

    /**
     * Devuelve las k claves de menor prioridad sin modificar el montículo.
     * Usa una cola auxiliar con los índices "frontera" del árbol: O(k log k).
     */
    public List<String> topK(int k) {
        List<String> resultado = new ArrayList<>();
        if (k <= 0 || estaVacio()) return resultado;
        PriorityQueue<Integer> frontera = new PriorityQueue<>((a, b) -> comparar(a, b));
        frontera.add(0);
        while (!frontera.isEmpty() && resultado.size() < k) {
            int i = frontera.poll();
            resultado.add(claves.get(i));
            int izq = 2 * i + 1, der = 2 * i + 2;
            if (izq < claves.size()) frontera.add(izq);
            if (der < claves.size()) frontera.add(der);
        }
        return resultado;
    }

    // ---------------------------------------------------------------- internos

    private int comparar(int a, int b) {
        int c = Double.compare(prioridades.get(a), prioridades.get(b));
        return c != 0 ? c : claves.get(a).compareTo(claves.get(b));
    }

    private void subir(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (comparar(i, padre) >= 0) break;
            intercambiar(i, padre);
            i = padre;
        }
    }

    private void bajar(int i) {
        int n = claves.size();
        while (true) {
            int izq = 2 * i + 1, der = izq + 1, menor = i;
            if (izq < n && comparar(izq, menor) < 0) menor = izq;
            if (der < n && comparar(der, menor) < 0) menor = der;
            if (menor == i) return;
            intercambiar(i, menor);
            i = menor;
        }
    }

    private void intercambiar(int a, int b) {
        if (a == b) return;
        String ka = claves.get(a), kb = claves.get(b);
        Double pa = prioridades.get(a), pb = prioridades.get(b);
        claves.set(a, kb);
        claves.set(b, ka);
        prioridades.set(a, pb);
        prioridades.set(b, pa);
        posicion.put(kb, a);
        posicion.put(ka, b);
    }
}
