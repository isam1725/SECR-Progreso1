package estructuras.comparacion;

import estructuras.ArbolAVL;
import estructuras.MonticuloBinario;
import modelo.Conductor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ALTERNATIVA B — Cola de prioridad (montículo binario) con actualización perezosa + árbol AVL de licencias.
 * <p>
 * Un montículo binario no permite cambiar la prioridad de un elemento que está en medio del arreglo sin
 * saber su posición. En lugar de buscarlo, cada actualización INSERTA una entrada nueva con una versión
 * más alta; las entradas viejas quedan "obsoletas" y se ignoran al consultar ("lazy deletion").
 * Cuando las obsoletas superan a las vigentes, el montículo se reconstruye en O(n) (compactación).
 * <ul>
 *   <li>actualizarPuntaje: O(log m), m = entradas en el montículo (≤ 2n + c)</li>
 *   <li>topRiesgo(k): O((k + s) log(k + s)), s = obsoletas encontradas en el camino</li>
 *   <li>enRangoPuntaje: O(k + s), resultado SIN orden</li>
 *   <li>memoria: entre n y 2n entradas + 1 nodo AVL por conductor</li>
 * </ul>
 */
public class RankingRiesgoMonticulo implements RankingRiesgo {

    /** Estado vigente de cada conductor; la versión invalida entradas antiguas del montículo. */
    private static final class Registro {
        final Conductor conductor;
        int version;
        Registro(Conductor c) { this.conductor = c; }
    }

    private record Entrada(double puntaje, String licencia, Registro registro, int version) {
        boolean vigente() { return registro.version == version; }
    }

    private static final Comparator<Entrada> ORDEN =
            Comparator.comparingDouble(Entrada::puntaje).thenComparing(Entrada::licencia);

    private final ArbolAVL<String, Registro> porLicencia = new ArbolAVL<>();
    private final MonticuloBinario<Entrada> monticulo = new MonticuloBinario<>(ORDEN);
    private int compactaciones;

    private static String lic(Conductor c) { return c.getLicencia().getNumero(); }

    @Override
    public void registrar(Conductor c) {
        if (porLicencia.contiene(lic(c))) throw new IllegalArgumentException("Licencia duplicada: " + lic(c));
        Registro r = new Registro(c);
        porLicencia.insertar(lic(c), r);
        monticulo.insertar(new Entrada(c.getPuntajeActual(), lic(c), r, 0));
    }

    @Override
    public Conductor buscar(String licencia) {
        Registro r = porLicencia.buscar(licencia);
        return r == null ? null : r.conductor;
    }

    @Override
    public void actualizarPuntaje(String licencia, double nuevoPuntaje) {
        Registro r = porLicencia.buscar(licencia);
        if (r == null) throw new IllegalArgumentException("No existe " + licencia);
        Puntajes.asignar(r.conductor, nuevoPuntaje);
        r.version++;
        monticulo.insertar(new Entrada(nuevoPuntaje, licencia, r, r.version));
        if (monticulo.tamanio() > 2 * porLicencia.tamanio() + 64) compactar();
    }

    /** Reconstruye el montículo solo con entradas vigentes: O(n). */
    private void compactar() {
        List<Entrada> vigentes = new ArrayList<>(porLicencia.tamanio());
        porLicencia.recorrerEnOrden((k, r) -> vigentes.add(new Entrada(r.conductor.getPuntajeActual(), k, r, r.version)));
        monticulo.reconstruir(vigentes);
        compactaciones++;
    }

    @Override
    public List<Conductor> topRiesgo(int k) {
        List<Conductor> res = new ArrayList<>(k);
        for (Entrada e : monticulo.menores(k, Entrada::vigente)) res.add(e.registro().conductor);
        return res;
    }

    @Override
    public List<Conductor> enRangoPuntaje(double min, double max) {
        List<Conductor> res = new ArrayList<>();
        for (Entrada e : monticulo.hastaTope(e -> e.puntaje() <= max, e -> e.vigente() && e.puntaje() >= min))
            res.add(e.registro().conductor);
        return res;
    }

    public int entradasEnMonticulo() { return monticulo.tamanio(); }
    public int getCompactaciones() { return compactaciones; }

    @Override public int tamanio() { return porLicencia.tamanio(); }
    @Override public String nombre() { return "B (cola de prioridad perezosa)"; }
}
