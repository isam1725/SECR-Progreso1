package estructuras;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * Cola de prioridad implementada como montículo binario mínimo (Williams, 1964): un árbol binario
 * completo almacenado en un arreglo, donde cada padre es menor o igual que sus hijos.
 * <ul>
 *   <li>insertar, extraerMinimo: O(log n) — flotar / hundir</li>
 *   <li>verMinimo: O(1)</li>
 *   <li>construir (heapify) desde una lista: O(n)</li>
 *   <li>menores(k, filtro): O((k + s) log(k + s)) sin modificar el montículo, s = elementos descartados por el filtro</li>
 * </ul>
 */
public class MonticuloBinario<T> {

    private final List<T> datos = new ArrayList<>();
    private final Comparator<? super T> cmp;

    public MonticuloBinario(Comparator<? super T> cmp) { this.cmp = cmp; }

    public int tamanio() { return datos.size(); }
    public boolean estaVacio() { return datos.isEmpty(); }

    public void insertar(T x) {
        datos.add(x);
        flotar(datos.size() - 1);
    }

    public T verMinimo() {
        if (datos.isEmpty()) throw new NoSuchElementException("El montículo está vacío.");
        return datos.get(0);
    }

    public T extraerMinimo() {
        T min = verMinimo();
        T ultimo = datos.remove(datos.size() - 1);
        if (!datos.isEmpty()) {
            datos.set(0, ultimo);
            hundir(0);
        }
        return min;
    }

    /** Reemplaza todo el contenido y reconstruye el montículo en O(n) (Floyd). */
    public void reconstruir(List<T> elementos) {
        datos.clear();
        datos.addAll(elementos);
        for (int i = datos.size() / 2 - 1; i >= 0; i--) hundir(i);
    }

    public void vaciar() { datos.clear(); }

    /**
     * Los k menores elementos que cumplen el filtro, en orden, SIN modificar el montículo.
     * Recorre el árbol desde la raíz con una cola de prioridad auxiliar de índices ("frontera").
     */
    public List<T> menores(int k, Predicate<T> filtro) {
        List<T> res = new ArrayList<>();
        if (k <= 0 || datos.isEmpty()) return res;
        MonticuloBinario<Integer> frontera = new MonticuloBinario<>((a, b) -> cmp.compare(datos.get(a), datos.get(b)));
        frontera.insertar(0);
        while (!frontera.estaVacio() && res.size() < k) {
            int i = frontera.extraerMinimo();
            T x = datos.get(i);
            if (filtro.test(x)) res.add(x);
            int izq = 2 * i + 1, der = izq + 1;
            if (izq < datos.size()) frontera.insertar(izq);
            if (der < datos.size()) frontera.insertar(der);
        }
        return res;
    }

    /** Todos los elementos con valor ≤ tope (según el comparador), podando subárboles: O(k + s). */
    public List<T> hastaTope(Predicate<T> dentroDelTope, Predicate<T> filtro) {
        List<T> res = new ArrayList<>();
        if (datos.isEmpty()) return res;
        Pila<Integer> pila = new Pila<>();
        pila.apilar(0);
        while (!pila.estaVacia()) {
            int i = pila.desapilar();
            T x = datos.get(i);
            if (!dentroDelTope.test(x)) continue;        // todo su subárbol es mayor: se poda
            if (filtro.test(x)) res.add(x);
            int izq = 2 * i + 1, der = izq + 1;
            if (izq < datos.size()) pila.apilar(izq);
            if (der < datos.size()) pila.apilar(der);
        }
        return res;
    }

    /** Verifica la propiedad de montículo (para pruebas). */
    public boolean esMonticuloValido() {
        for (int i = 1; i < datos.size(); i++) if (cmp.compare(datos.get((i - 1) / 2), datos.get(i)) > 0) return false;
        return true;
    }

    private void flotar(int i) {
        T x = datos.get(i);
        while (i > 0) {
            int p = (i - 1) / 2;
            if (cmp.compare(x, datos.get(p)) >= 0) break;
            datos.set(i, datos.get(p));
            i = p;
        }
        datos.set(i, x);
    }

    private void hundir(int i) {
        int n = datos.size();
        T x = datos.get(i);
        while (true) {
            int hijo = 2 * i + 1;
            if (hijo >= n) break;
            if (hijo + 1 < n && cmp.compare(datos.get(hijo + 1), datos.get(hijo)) < 0) hijo++;
            if (cmp.compare(datos.get(hijo), x) >= 0) break;
            datos.set(i, datos.get(hijo));
            i = hijo;
        }
        datos.set(i, x);
    }
}
