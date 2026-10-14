package estructuras;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Ordenamiento por mezcla (Merge Sort) estable, implementado por el equipo.
 * Tiempo O(n log n) en todos los casos; espacio auxiliar O(n).
 * Se usa en M2 (eventos por gravedad y fecha) y en M5
 * para ordenar reportes por varios criterios (la estabilidad conserva el orden previo).
 */
public final class OrdenamientoMezcla {

    private OrdenamientoMezcla() { }

    public static <T> List<T> ordenar(List<T> lista, Comparator<? super T> cmp) {
        List<T> datos = new ArrayList<>(lista);
        if (datos.size() < 2) return datos;
        List<T> aux = new ArrayList<>(datos);
        ordenar(datos, aux, 0, datos.size() - 1, cmp);
        return datos;
    }

    private static <T> void ordenar(List<T> a, List<T> aux, int ini, int fin, Comparator<? super T> cmp) {
        if (ini >= fin) return;
        int medio = (ini + fin) >>> 1;
        ordenar(a, aux, ini, medio, cmp);
        ordenar(a, aux, medio + 1, fin, cmp);
        if (cmp.compare(a.get(medio), a.get(medio + 1)) <= 0) return; // ya ordenado
        for (int k = ini; k <= fin; k++) aux.set(k, a.get(k));
        int i = ini, j = medio + 1;
        for (int k = ini; k <= fin; k++) {
            if (i > medio) a.set(k, aux.get(j++));
            else if (j > fin) a.set(k, aux.get(i++));
            else if (cmp.compare(aux.get(j), aux.get(i)) < 0) a.set(k, aux.get(j++));
            else a.set(k, aux.get(i++));
        }
    }
}
