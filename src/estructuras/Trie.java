package estructuras;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Árbol de prefijos (Trie) para búsqueda incremental de conductores por nombre.
 * <p>
 * Cada palabra del nombre se indexa por separado, de modo que "torr" encuentra a
 * "Sofía Torres Vera". Las claves se normalizan (minúsculas y sin tildes).
 * <ul>
 *   <li>insertar / eliminar: O(L), con L = longitud de la clave.</li>
 *   <li>buscarPorPrefijo: O(P + R), con P = longitud del prefijo y R = nodos visitados del subárbol.</li>
 * </ul>
 * Referencia: Fredkin (1960), "Trie memory", Communications of the ACM 3(9).
 */
public class Trie {

    private static final class Nodo {
        // TreeMap mantiene a los hijos en orden alfabético: los resultados salen ordenados.
        private final Map<Character, Nodo> hijos = new TreeMap<>();
        private final Set<String> valores = new LinkedHashSet<>();
    }

    private final Nodo raiz = new Nodo();
    private int cantidadClaves;

    /** Normaliza un texto: minúsculas, sin tildes y sin espacios extremos. */
    public static String normalizar(String texto) {
        if (texto == null) return "";
        String sinTildes = Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.replaceAll("\\s+", " ");
    }

    /** Indexa todas las palabras de un nombre completo apuntando al valor indicado (p. ej. la licencia). */
    public void insertarNombre(String nombreCompleto, String valor) {
        for (String palabra : normalizar(nombreCompleto).split(" ")) {
            if (!palabra.isEmpty()) insertar(palabra, valor);
        }
    }

    /** Elimina el valor de todas las palabras del nombre. */
    public void eliminarNombre(String nombreCompleto, String valor) {
        for (String palabra : normalizar(nombreCompleto).split(" ")) {
            if (!palabra.isEmpty()) eliminar(palabra, valor);
        }
    }

    public void insertar(String clave, String valor) {
        String k = normalizar(clave);
        if (k.isEmpty() || valor == null) return;
        Nodo actual = raiz;
        for (char c : k.toCharArray()) {
            actual = actual.hijos.computeIfAbsent(c, x -> new Nodo());
        }
        if (actual.valores.add(valor)) cantidadClaves++;
    }

    public boolean eliminar(String clave, String valor) {
        String k = normalizar(clave);
        Nodo actual = raiz;
        Deque<Nodo> camino = new ArrayDeque<>();
        camino.push(raiz);
        for (char c : k.toCharArray()) {
            actual = actual.hijos.get(c);
            if (actual == null) return false;
            camino.push(actual);
        }
        boolean eliminado = actual.valores.remove(valor);
        if (!eliminado) return false;
        cantidadClaves--;
        // Poda de nodos que quedaron vacíos (de abajo hacia arriba).
        for (int i = k.length() - 1; i >= 0; i--) {
            Nodo hijo = camino.pop();
            Nodo padre = camino.peek();
            if (hijo.valores.isEmpty() && hijo.hijos.isEmpty()) padre.hijos.remove(k.charAt(i));
            else break;
        }
        return true;
    }

    /**
     * Devuelve hasta {@code limite} valores distintos cuyas claves empiezan con el prefijo.
     * Recorrido en profundidad (DFS) iterativo del subárbol del prefijo.
     */
    public List<String> buscarPorPrefijo(String prefijo, int limite) {
        List<String> resultado = new ArrayList<>();
        String p = normalizar(prefijo);
        if (p.isEmpty() || limite <= 0) return resultado;
        Nodo actual = raiz;
        for (char c : p.toCharArray()) {
            actual = actual.hijos.get(c);
            if (actual == null) return resultado;
        }
        Set<String> vistos = new LinkedHashSet<>();
        Deque<Nodo> pila = new ArrayDeque<>();
        pila.push(actual);
        while (!pila.isEmpty() && vistos.size() < limite) {
            Nodo n = pila.pop();
            for (String v : n.valores) {
                vistos.add(v);
                if (vistos.size() >= limite) break;
            }
            // Se apilan en orden inverso para visitar alfabéticamente.
            List<Nodo> hijos = new ArrayList<>(n.hijos.values());
            for (int i = hijos.size() - 1; i >= 0; i--) pila.push(hijos.get(i));
        }
        resultado.addAll(vistos);
        return resultado;
    }

    public boolean contiene(String clave) {
        Nodo actual = raiz;
        for (char c : normalizar(clave).toCharArray()) {
            actual = actual.hijos.get(c);
            if (actual == null) return false;
        }
        return !actual.valores.isEmpty();
    }

    public int getCantidadClaves() { return cantidadClaves; }
}
