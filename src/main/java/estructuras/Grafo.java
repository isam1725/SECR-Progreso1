package estructuras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Grafo no dirigido y ponderado con listas de adyacencia, implementado por el equipo.
 * En SECR los vértices son zonas o intersecciones viales y las aristas, tramos con su distancia en km.
 * Los nombres de los vértices se indexan con un {@link ArbolAVL} (sin tablas hash).
 * <ul>
 *   <li>agregarVertice: O(log V); conectar: O(log V)</li>
 *   <li>zonasCercanas (BFS con {@link Cola}, hasta k saltos): O(V + E)</li>
 *   <li>rutaMasCorta (Dijkstra con {@link MonticuloBinario}): O((V + E) log V)</li>
 *   <li>componentesConexas (DFS iterativo con {@link Pila}): O(V + E)</li>
 *   <li>espacio: O(V + E)</li>
 * </ul>
 */
public class Grafo {

    /** Arista hacia un vértice vecino con su peso (distancia en km). */
    public record Arista(int destino, double peso) { }

    /** Resultado de Dijkstra: distancia total y vértices del camino (vacío si no hay ruta). */
    public record Ruta(double distancia, List<String> camino) {
        public boolean existe() { return !camino.isEmpty(); }
    }

    private final ArbolAVL<String, Integer> indice = new ArbolAVL<>();
    private final List<String> nombres = new ArrayList<>();
    private final List<List<Arista>> adyacencia = new ArrayList<>();
    private int aristas;

    public int cantidadVertices() { return nombres.size(); }
    public int cantidadAristas() { return aristas; }
    public boolean existeVertice(String nombre) { return indice.contiene(nombre); }

    public int agregarVertice(String nombre) {
        Integer i = indice.buscar(nombre);
        if (i != null) return i;
        nombres.add(nombre);
        adyacencia.add(new ArrayList<>());
        indice.insertar(nombre, nombres.size() - 1);
        return nombres.size() - 1;
    }

    public void conectar(String a, String b, double km) {
        if (km <= 0) throw new IllegalArgumentException("La distancia debe ser positiva.");
        if (a.equals(b)) throw new IllegalArgumentException("No se permiten lazos.");
        int u = agregarVertice(a), v = agregarVertice(b);
        adyacencia.get(u).add(new Arista(v, km));
        adyacencia.get(v).add(new Arista(u, km));
        aristas++;
    }

    public List<String> vecinos(String nombre) {
        List<String> res = new ArrayList<>();
        Integer u = indice.buscar(nombre);
        if (u == null) return res;
        for (Arista a : adyacencia.get(u)) res.add(nombres.get(a.destino()));
        return res;
    }

    /** BFS: zonas a lo sumo a {@code maxSaltos} tramos del origen (sin incluirlo), por cercanía en saltos. */
    public List<String> zonasCercanas(String origen, int maxSaltos) {
        List<String> res = new ArrayList<>();
        Integer s = indice.buscar(origen);
        if (s == null || maxSaltos <= 0) return res;
        int[] nivel = new int[nombres.size()];
        java.util.Arrays.fill(nivel, -1);
        Cola<Integer> cola = new Cola<>();
        nivel[s] = 0;
        cola.encolar(s);
        while (!cola.estaVacia()) {
            int u = cola.desencolar();
            if (nivel[u] == maxSaltos) continue;
            for (Arista a : adyacencia.get(u)) {
                if (nivel[a.destino()] == -1) {
                    nivel[a.destino()] = nivel[u] + 1;
                    res.add(nombres.get(a.destino()));
                    cola.encolar(a.destino());
                }
            }
        }
        return res;
    }

    /** Dijkstra (1959): ruta de menor distancia entre dos zonas. Pesos no negativos. */
    public Ruta rutaMasCorta(String origen, String destino) {
        Integer s = indice.buscar(origen), t = indice.buscar(destino);
        if (s == null || t == null) return new Ruta(Double.POSITIVE_INFINITY, List.of());
        int n = nombres.size();
        double[] dist = new double[n];
        int[] previo = new int[n];
        boolean[] cerrado = new boolean[n];
        java.util.Arrays.fill(dist, Double.POSITIVE_INFINITY);
        java.util.Arrays.fill(previo, -1);
        dist[s] = 0;
        // Cola de prioridad con entradas (distancia, vértice); las obsoletas se descartan al extraerlas.
        MonticuloBinario<double[]> pq = new MonticuloBinario<>((x, y) -> Double.compare(x[0], y[0]));
        pq.insertar(new double[]{0, s});
        while (!pq.estaVacio()) {
            double[] e = pq.extraerMinimo();
            int u = (int) e[1];
            if (cerrado[u]) continue;
            cerrado[u] = true;
            if (u == t) break;
            for (Arista a : adyacencia.get(u)) {
                double nd = dist[u] + a.peso();
                if (nd < dist[a.destino()]) {
                    dist[a.destino()] = nd;
                    previo[a.destino()] = u;
                    pq.insertar(new double[]{nd, a.destino()});
                }
            }
        }
        if (dist[t] == Double.POSITIVE_INFINITY) return new Ruta(Double.POSITIVE_INFINITY, List.of());
        List<String> camino = new ArrayList<>();
        for (int v = t; v != -1; v = previo[v]) camino.add(nombres.get(v));
        Collections.reverse(camino);
        return new Ruta(dist[t], camino);
    }

    /** DFS iterativo con pila: grupos de zonas conectadas entre sí (detecta zonas aisladas del mapa). */
    public List<List<String>> componentesConexas() {
        List<List<String>> res = new ArrayList<>();
        boolean[] visto = new boolean[nombres.size()];
        for (int i = 0; i < nombres.size(); i++) {
            if (visto[i]) continue;
            List<String> comp = new ArrayList<>();
            Pila<Integer> pila = new Pila<>();
            pila.apilar(i);
            visto[i] = true;
            while (!pila.estaVacia()) {
                int u = pila.desapilar();
                comp.add(nombres.get(u));
                for (Arista a : adyacencia.get(u)) {
                    if (!visto[a.destino()]) { visto[a.destino()] = true; pila.apilar(a.destino()); }
                }
            }
            res.add(comp);
        }
        return res;
    }
}
