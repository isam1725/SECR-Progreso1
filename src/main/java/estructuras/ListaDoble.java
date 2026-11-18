package estructuras;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Lista doblemente enlazada, implementada por el equipo (tema de la semana 7).
 * Cada nodo conoce a su anterior y a su siguiente, por lo que se puede recorrer
 * el historial de un conductor hacia adelante y hacia atrás sin volver a buscar.
 * <ul>
 *   <li>insertarOrdenado: O(n) — recorre hasta encontrar la posición</li>
 *   <li>insertarAlFinal, eliminarPrimero/Último: O(1)</li>
 *   <li>avanzar / retroceder del cursor: O(1)</li>
 * </ul>
 */
public class ListaDoble<T> {

    private static final class Nodo<T> {
        final T dato;
        Nodo<T> anterior, siguiente;
        Nodo(T dato) { this.dato = dato; }
    }

    private Nodo<T> cabeza, cola;
    private int tamanio;

    public int tamanio() { return tamanio; }
    public boolean estaVacia() { return tamanio == 0; }

    public void insertarAlFinal(T dato) {
        Nodo<T> n = new Nodo<>(dato);
        if (cola == null) cabeza = cola = n;
        else { cola.siguiente = n; n.anterior = cola; cola = n; }
        tamanio++;
    }

    /** Inserta manteniendo el orden dado por el comparador (estable: después de los iguales). */
    public void insertarOrdenado(T dato, Comparator<? super T> cmp) {
        Nodo<T> actual = cabeza;
        while (actual != null && cmp.compare(actual.dato, dato) <= 0) actual = actual.siguiente;
        if (actual == null) { insertarAlFinal(dato); return; }
        Nodo<T> n = new Nodo<>(dato);
        n.siguiente = actual;
        n.anterior = actual.anterior;
        if (actual.anterior == null) cabeza = n; else actual.anterior.siguiente = n;
        actual.anterior = n;
        tamanio++;
    }

    public T primero() { if (cabeza == null) throw new NoSuchElementException("Lista vacía."); return cabeza.dato; }
    public T ultimo() { if (cola == null) throw new NoSuchElementException("Lista vacía."); return cola.dato; }

    /** Recorrido de la cabeza a la cola. */
    public List<T> aLista() {
        List<T> res = new ArrayList<>(tamanio);
        for (Nodo<T> n = cabeza; n != null; n = n.siguiente) res.add(n.dato);
        return res;
    }

    /** Recorrido de la cola a la cabeza (usa los enlaces "anterior"). */
    public List<T> aListaInversa() {
        List<T> res = new ArrayList<>(tamanio);
        for (Nodo<T> n = cola; n != null; n = n.anterior) res.add(n.dato);
        return res;
    }

    /** Cursor para navegar la lista elemento por elemento en ambos sentidos. */
    public Cursor cursor() { return new Cursor(); }

    public final class Cursor {
        private Nodo<T> actual = cabeza;
        private int indice = cabeza == null ? 0 : 1;

        public T actual() { return actual == null ? null : actual.dato; }
        public int indice() { return indice; }
        public boolean haySiguiente() { return actual != null && actual.siguiente != null; }
        public boolean hayAnterior() { return actual != null && actual.anterior != null; }

        public T avanzar() {
            if (!haySiguiente()) throw new NoSuchElementException("No hay un elemento siguiente.");
            actual = actual.siguiente; indice++;
            return actual.dato;
        }

        public T retroceder() {
            if (!hayAnterior()) throw new NoSuchElementException("No hay un elemento anterior.");
            actual = actual.anterior; indice--;
            return actual.dato;
        }
    }
}
