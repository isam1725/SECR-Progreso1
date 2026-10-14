package estructuras.comparacion;

import estructuras.BusquedaBinaria;
import estructuras.Trie;
import modelo.Conductor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ALTERNATIVA A - Arreglos dinámicos ordenados + búsqueda binaria + ordenamiento por mezcla (TimSort).
 * <ul>
 *   <li>registrar: búsqueda binaria de la posición + desplazamiento: O(n).</li>
 *   <li>buscarPorLicencia: búsqueda binaria O(log n).</li>
 *   <li>buscarPorPrefijoNombre: lista ordenada por palabra del nombre, límite inferior + barrido: O(log n + k).</li>
 *   <li>actualizarPuntaje: O(log n) (el orden por riesgo no se mantiene).</li>
 *   <li>topRiesgo: copia + ordenamiento por mezcla (TimSort) por puntaje: O(n log n) en cada consulta.</li>
 * </ul>
 */
public class IndiceConductoresA implements IndiceConductores {

    private static final Comparator<String> TEXTO = Comparator.naturalOrder();

    /** Entrada del índice de nombres: palabra normalizada → conductor. */
    private record EntradaNombre(String palabra, Conductor conductor) { }

    private final List<Conductor> porLicencia = new ArrayList<>();
    private final List<EntradaNombre> porNombre = new ArrayList<>();

    // El número de licencia ya se almacena normalizado en mayúsculas (Validaciones.normalizarNumeroLicencia).
    private static String lic(Conductor c) { return c.getLicencia().getNumero(); }

    @Override
    public void registrar(Conductor c) {
        int pos = BusquedaBinaria.limiteInferior(porLicencia, lic(c), IndiceConductoresA::lic, TEXTO);
        if (pos < porLicencia.size() && lic(porLicencia.get(pos)).equals(lic(c)))
            throw new IllegalArgumentException("Licencia duplicada: " + lic(c));
        porLicencia.add(pos, c);
        for (String palabra : Trie.normalizar(c.getNombreCompleto()).split(" ")) {
            int p = BusquedaBinaria.limiteInferior(porNombre, palabra, EntradaNombre::palabra, TEXTO);
            porNombre.add(p, new EntradaNombre(palabra, c));
        }
    }

    @Override
    public Conductor buscarPorLicencia(String licencia) {
        String k = licencia.trim().toUpperCase();
        int pos = BusquedaBinaria.limiteInferior(porLicencia, k, IndiceConductoresA::lic, TEXTO);
        return pos < porLicencia.size() && lic(porLicencia.get(pos)).equals(k) ? porLicencia.get(pos) : null;
    }

    @Override
    public List<Conductor> buscarPorPrefijoNombre(String prefijo, int limite) {
        String p = Trie.normalizar(prefijo);
        List<Conductor> res = new ArrayList<>();
        if (p.isEmpty()) return res;
        int i = BusquedaBinaria.limiteInferior(porNombre, p, EntradaNombre::palabra, TEXTO);
        while (i < porNombre.size() && res.size() < limite && porNombre.get(i).palabra().startsWith(p)) {
            Conductor c = porNombre.get(i).conductor();
            if (!res.contains(c)) res.add(c);
            i++;
        }
        return res;
    }

    @Override
    public void actualizarPuntaje(String licencia, double nuevoPuntaje) {
        Conductor c = buscarPorLicencia(licencia);
        if (c == null) throw new IllegalArgumentException("No existe " + licencia);
        Puntajes.asignar(c, nuevoPuntaje);
    }

    @Override
    public List<Conductor> topRiesgo(int k) {
        // List.sort usa TimSort (variante adaptativa y estable de Merge Sort): O(n log n).
        // Se usa la implementación optimizada del JDK para que la comparación con B sea justa.
        List<Conductor> ordenados = new ArrayList<>(porLicencia);
        ordenados.sort(Comparator.comparingDouble(Conductor::getPuntajeActual).thenComparing(IndiceConductoresA::lic));
        return new ArrayList<>(ordenados.subList(0, Math.min(k, ordenados.size())));
    }

    @Override public int tamanio() { return porLicencia.size(); }
    @Override public String nombre() { return "A (arreglos ordenados + búsqueda binaria + TimSort)"; }
}
