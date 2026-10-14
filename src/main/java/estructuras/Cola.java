package estructuras;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/** Cola (FIFO) enlazada con referencias al frente y al final, implementada por el equipo. Operaciones O(1). */
public class Cola<T> {

    private static final class Nodo<T> {
        final T dato;
        Nodo<T> siguiente;
        Nodo(T dato) { this.dato = dato; }
    }

    private Nodo<T> frente, fin;
    private int tamanio;

    public void encolar(T dato) {
        Nodo<T> n = new Nodo<>(dato);
        if (fin == null) frente = fin = n;
        else { fin.siguiente = n; fin = n; }
        tamanio++;
    }

    public T desencolar() {
        if (frente == null) throw new NoSuchElementException("La cola está vacía.");
        T d = frente.dato;
        frente = frente.siguiente;
        if (frente == null) fin = null;
        tamanio--;
        return d;
    }

    public T verFrente() {
        if (frente == null) throw new NoSuchElementException("La cola está vacía.");
        return frente.dato;
    }

    public boolean estaVacia() { return frente == null; }
    public int tamanio() { return tamanio; }

    /** Copia de los elementos en orden de llegada (no modifica la cola). O(n). */
    public List<T> comoLista() {
        List<T> res = new ArrayList<>(tamanio);
        for (Nodo<T> n = frente; n != null; n = n.siguiente) res.add(n.dato);
        return res;
    }
}
