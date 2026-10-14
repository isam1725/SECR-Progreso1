package estructuras;

import java.util.NoSuchElementException;

/** Pila (LIFO) enlazada, implementada por el equipo. apilar, desapilar y cima: O(1). */
public class Pila<T> {

    private static final class Nodo<T> {
        final T dato;
        final Nodo<T> siguiente;
        Nodo(T dato, Nodo<T> siguiente) { this.dato = dato; this.siguiente = siguiente; }
    }

    private Nodo<T> tope;
    private int tamanio;

    public void apilar(T dato) { tope = new Nodo<>(dato, tope); tamanio++; }

    public T desapilar() {
        if (tope == null) throw new NoSuchElementException("La pila está vacía.");
        T d = tope.dato;
        tope = tope.siguiente;
        tamanio--;
        return d;
    }

    public T cima() {
        if (tope == null) throw new NoSuchElementException("La pila está vacía.");
        return tope.dato;
    }

    public boolean estaVacia() { return tope == null; }
    public int tamanio() { return tamanio; }
}
