package estructuras;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Árbol binario de búsqueda autobalanceado AVL (Adelson-Velsky y Landis, 1962), implementado por el equipo.
 * <p>
 * Asocia claves ordenables con valores y mantiene, en cada nodo, |altura(izq) − altura(der)| ≤ 1
 * mediante rotaciones simples y dobles. Así la altura es O(log n) aun cuando las claves llegan
 * ordenadas (por ejemplo, licencias B-000001, B-000002…), caso en el que un ABB común degenera en lista.
 * <ul>
 *   <li>insertar, buscar, eliminar: O(log n)</li>
 *   <li>rango(desde, hasta): O(log n + k) — recorrido en orden podado</li>
 *   <li>primeros(k): O(log n + k)</li>
 *   <li>enOrden: O(n); espacio O(n)</li>
 * </ul>
 * Reemplaza a las tablas hash del proyecto anterior (LinkedHashMap/HashMap).
 */
public class ArbolAVL<K extends Comparable<? super K>, V> {

    private final class Nodo {
        K clave;
        V valor;
        Nodo izq, der;
        int altura = 1;
        Nodo(K clave, V valor) { this.clave = clave; this.valor = valor; }
    }

    private Nodo raiz;
    private int tamanio;

    // ------------------------------------------------------------------ consultas

    public int tamanio() { return tamanio; }
    public boolean estaVacio() { return tamanio == 0; }
    public int altura() { return h(raiz); }

    public V buscar(K clave) {
        Nodo n = raiz;
        while (n != null) {
            int c = clave.compareTo(n.clave);
            if (c == 0) return n.valor;
            n = c < 0 ? n.izq : n.der;
        }
        return null;
    }

    public boolean contiene(K clave) {
        Nodo n = raiz;
        while (n != null) {
            int c = clave.compareTo(n.clave);
            if (c == 0) return true;
            n = c < 0 ? n.izq : n.der;
        }
        return false;
    }

    /** Devuelve el valor asociado o inserta el que entrega el proveedor (útil para contadores). */
    public V obtenerOInsertar(K clave, Supplier<V> nuevo) {
        V v = buscar(clave);
        if (v == null) {
            v = nuevo.get();
            insertar(clave, v);
        }
        return v;
    }

    /** Recorrido en orden (in-order): valores ordenados por clave. Iterativo, con una Pila. */
    public List<V> valoresEnOrden() {
        List<V> res = new ArrayList<>(tamanio);
        recorrerEnOrden((k, v) -> res.add(v));
        return res;
    }

    public List<K> clavesEnOrden() {
        List<K> res = new ArrayList<>(tamanio);
        recorrerEnOrden((k, v) -> res.add(k));
        return res;
    }

    public void recorrerEnOrden(BiConsumer<K, V> visitar) {
        Pila<Nodo> pila = new Pila<>();
        Nodo actual = raiz;
        while (actual != null || !pila.estaVacia()) {
            while (actual != null) { pila.apilar(actual); actual = actual.izq; }
            actual = pila.desapilar();
            visitar.accept(actual.clave, actual.valor);
            actual = actual.der;
        }
    }

    /** Valores con clave en [desde, hasta], en orden. Poda los subárboles fuera del rango. */
    public List<V> rango(K desde, K hasta) {
        List<V> res = new ArrayList<>();
        if (desde == null || hasta == null || desde.compareTo(hasta) > 0) return res;
        rango(raiz, desde, hasta, res);
        return res;
    }

    private void rango(Nodo n, K desde, K hasta, List<V> res) {
        if (n == null) return;
        int cDesde = desde.compareTo(n.clave), cHasta = hasta.compareTo(n.clave);
        if (cDesde < 0) rango(n.izq, desde, hasta, res);
        if (cDesde <= 0 && cHasta >= 0) res.add(n.valor);
        if (cHasta > 0) rango(n.der, desde, hasta, res);
    }

    /** Los k valores de menor clave, en orden: O(log n + k). */
    public List<V> primeros(int k) {
        List<V> res = new ArrayList<>(Math.max(0, Math.min(k, tamanio)));
        if (k <= 0) return res;
        Pila<Nodo> pila = new Pila<>();
        Nodo actual = raiz;
        while ((actual != null || !pila.estaVacia()) && res.size() < k) {
            while (actual != null) { pila.apilar(actual); actual = actual.izq; }
            actual = pila.desapilar();
            res.add(actual.valor);
            actual = actual.der;
        }
        return res;
    }

    public K minimo() {
        if (raiz == null) return null;
        Nodo n = raiz;
        while (n.izq != null) n = n.izq;
        return n.clave;
    }

    // ------------------------------------------------------------------ modificaciones

    /** Inserta o reemplaza el valor de la clave. */
    public void insertar(K clave, V valor) {
        if (clave == null) throw new IllegalArgumentException("La clave no puede ser nula.");
        raiz = insertar(raiz, clave, valor);
    }

    private Nodo insertar(Nodo n, K clave, V valor) {
        if (n == null) { tamanio++; return new Nodo(clave, valor); }
        int c = clave.compareTo(n.clave);
        if (c < 0) n.izq = insertar(n.izq, clave, valor);
        else if (c > 0) n.der = insertar(n.der, clave, valor);
        else { n.valor = valor; return n; }
        return balancear(n);
    }

    /** Elimina la clave; devuelve true si existía. */
    public boolean eliminar(K clave) {
        int antes = tamanio;
        raiz = eliminar(raiz, clave);
        return tamanio < antes;
    }

    private Nodo eliminar(Nodo n, K clave) {
        if (n == null) return null;
        int c = clave.compareTo(n.clave);
        if (c < 0) n.izq = eliminar(n.izq, clave);
        else if (c > 0) n.der = eliminar(n.der, clave);
        else {
            if (n.izq == null || n.der == null) { tamanio--; return n.izq != null ? n.izq : n.der; }
            Nodo sucesor = n.der;
            while (sucesor.izq != null) sucesor = sucesor.izq;
            n.clave = sucesor.clave;
            n.valor = sucesor.valor;
            n.der = eliminar(n.der, sucesor.clave);
        }
        return balancear(n);
    }

    // ------------------------------------------------------------------ balanceo

    private int h(Nodo n) { return n == null ? 0 : n.altura; }
    private void actualizar(Nodo n) { n.altura = 1 + Math.max(h(n.izq), h(n.der)); }
    private int factor(Nodo n) { return h(n.izq) - h(n.der); }

    private Nodo rotarDerecha(Nodo y) {
        Nodo x = y.izq;
        y.izq = x.der;
        x.der = y;
        actualizar(y);
        actualizar(x);
        return x;
    }

    private Nodo rotarIzquierda(Nodo x) {
        Nodo y = x.der;
        x.der = y.izq;
        y.izq = x;
        actualizar(x);
        actualizar(y);
        return y;
    }

    private Nodo balancear(Nodo n) {
        actualizar(n);
        int f = factor(n);
        if (f > 1) {                       // cargado a la izquierda
            if (factor(n.izq) < 0) n.izq = rotarIzquierda(n.izq);   // caso izquierda-derecha
            return rotarDerecha(n);
        }
        if (f < -1) {                      // cargado a la derecha
            if (factor(n.der) > 0) n.der = rotarDerecha(n.der);     // caso derecha-izquierda
            return rotarIzquierda(n);
        }
        return n;
    }

    /** Verifica la propiedad AVL y el orden de las claves (para pruebas). */
    public boolean esAVLValido() { return valido(raiz, null, null) >= 0; }

    private int valido(Nodo n, K min, K max) {
        if (n == null) return 0;
        if ((min != null && n.clave.compareTo(min) <= 0) || (max != null && n.clave.compareTo(max) >= 0)) return -1;
        int hi = valido(n.izq, min, n.clave), hd = valido(n.der, n.clave, max);
        if (hi < 0 || hd < 0 || Math.abs(hi - hd) > 1 || n.altura != 1 + Math.max(hi, hd)) return -1;
        return n.altura;
    }
}
