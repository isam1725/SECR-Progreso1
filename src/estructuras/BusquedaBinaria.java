package estructuras;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * Búsqueda binaria genérica sobre listas ordenadas. O(log n).
 */
public final class BusquedaBinaria {

    private BusquedaBinaria() { }

    /**
     * Primera posición i tal que clave(lista[i]) >= objetivo (lower bound).
     * Si todos son menores, devuelve lista.size().
     */
    public static <T, K> int limiteInferior(List<T> lista, K objetivo, Function<T, K> clave,
                                            Comparator<? super K> cmp) {
        int lo = 0, hi = lista.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (cmp.compare(clave.apply(lista.get(mid)), objetivo) < 0) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }
}
