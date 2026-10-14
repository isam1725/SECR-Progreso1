package estructuras.comparacion;

import estructuras.MonticuloIndexado;
import estructuras.Trie;
import modelo.Conductor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ALTERNATIVA B (seleccionada) - Tabla hash + Trie + Montículo indexado.
 * <ul>
 *   <li>registrar: O(1) promedio (hash) + O(L) (Trie) + O(log n) (montículo).</li>
 *   <li>buscarPorLicencia: O(1) promedio.</li>
 *   <li>buscarPorPrefijoNombre: O(P + R) recorriendo solo el subárbol del prefijo.</li>
 *   <li>actualizarPuntaje: O(log n) y el orden por riesgo se mantiene siempre.</li>
 *   <li>topRiesgo: O(k log k) sin reordenar toda la colección.</li>
 * </ul>
 */
public class IndiceConductoresB implements IndiceConductores {

    private final Map<String, Conductor> porLicencia = new HashMap<>();
    private final Trie nombres = new Trie();
    private final MonticuloIndexado riesgo = new MonticuloIndexado();

    // El número de licencia ya se almacena normalizado en mayúsculas (Validaciones.normalizarNumeroLicencia).
    private static String lic(Conductor c) { return c.getLicencia().getNumero(); }

    @Override
    public void registrar(Conductor c) {
        String k = lic(c);
        if (porLicencia.containsKey(k)) throw new IllegalArgumentException("Licencia duplicada: " + k);
        porLicencia.put(k, c);
        nombres.insertarNombre(c.getNombreCompleto(), k);
        riesgo.insertar(k, c.getPuntajeActual());
    }

    @Override
    public Conductor buscarPorLicencia(String licencia) {
        return porLicencia.get(licencia.trim().toUpperCase());
    }

    @Override
    public List<Conductor> buscarPorPrefijoNombre(String prefijo, int limite) {
        List<Conductor> res = new ArrayList<>();
        for (String k : nombres.buscarPorPrefijo(prefijo, limite)) res.add(porLicencia.get(k));
        return res;
    }

    @Override
    public void actualizarPuntaje(String licencia, double nuevoPuntaje) {
        Conductor c = buscarPorLicencia(licencia);
        if (c == null) throw new IllegalArgumentException("No existe " + licencia);
        Puntajes.asignar(c, nuevoPuntaje);
        riesgo.actualizar(lic(c), nuevoPuntaje);
    }

    @Override
    public List<Conductor> topRiesgo(int k) {
        List<Conductor> res = new ArrayList<>();
        for (String clave : riesgo.topK(k)) res.add(porLicencia.get(clave));
        return res;
    }

    @Override public int tamanio() { return porLicencia.size(); }
    @Override public String nombre() { return "B (hash + Trie + montículo indexado)"; }
}
