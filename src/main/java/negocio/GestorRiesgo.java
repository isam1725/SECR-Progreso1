package negocio;

import static modelo.Main.*;
import estructuras.comparacion.RankingRiesgoAVL;
import modelo.Conductor;
import modelo.NivelRiesgo;

import java.util.ArrayList;
import java.util.List;

/**
 * MÓDULO M3 — Puntaje y priorización de riesgo.
 * <p>
 * Mantiene a todos los conductores ordenados por riesgo (menor puntaje = mayor riesgo) en un
 * {@link RankingRiesgoAVL} (dos árboles AVL). Cada vez que M2 registra o deshace una infracción,
 * llama a {@link #reubicar(Conductor)} y el conductor cambia de lugar en O(log n).
 */
public class GestorRiesgo {

    public static final int K_MAXIMO = 100;

    private final RankingRiesgoAVL ranking = new RankingRiesgoAVL();

    /**
     * Sincroniza el ranking con el padrón de M1: agrega conductores nuevos, reubica los que
     * cambiaron de puntaje y quita los eliminados. O(n log n).
     */
    public void sincronizar(GestorConductores gestorC) {
        for (Conductor c : gestorC.listarTodos()) reubicar(c);
        for (String lic : ranking.licencias()) {
            if (gestorC.buscarPorLicencia(lic) == null) ranking.eliminar(lic);
        }
    }

    /** Ubica (o reubica) a un conductor según su puntaje actual. O(log n). */
    public void reubicar(Conductor c) {
        if (c != null) ranking.reubicar(c);
    }

    /** Los k conductores de mayor riesgo, en orden: recorrido en orden del AVL, O(log n + k). */
    public List<Conductor> topRiesgo(int k) throws SecrException {
        if (ranking.tamanio() == 0) throw new SecrException("No hay conductores registrados en el ranking.");
        if (k < 1 || k > K_MAXIMO)
            throw new SecrException("El valor de k debe estar entre 1 y " + K_MAXIMO + ".");
        return ranking.topRiesgo(Math.min(k, ranking.tamanio()));
    }

    /** Conductores de un nivel del semáforo, ordenados por riesgo: consulta de rango, O(log n + k). */
    public List<Conductor> porNivel(NivelRiesgo nivel) throws SecrException {
        if (nivel == null) throw new SecrException("Seleccione un nivel del semáforo.");
        return ranking.enRangoPuntaje(nivel.getMin(), nivel.getMax());
    }

    /** Posición del conductor en el ranking (1 = mayor riesgo): O(log n) gracias al tamaño de subárbol. */
    public int posicion(String licencia) throws SecrException {
        if (licencia == null || licencia.isBlank()) throw new SecrException("Ingrese un número de licencia.");
        int pos = ranking.posicion(licencia.trim().toUpperCase());
        if (pos < 0) throw new ConductorNoEncontradoException("No existe un conductor con la licencia " + licencia.trim().toUpperCase() + " en el ranking.");
        return pos;
    }

    /** Cantidad de conductores por nivel del semáforo, en el orden ROJO, NARANJA, AMARILLO, VERDE. */
    public int[] conteoPorNivel() {
        NivelRiesgo[] niveles = NivelRiesgo.values();
        int[] res = new int[niveles.length];
        for (int i = 0; i < niveles.length; i++) res[i] = ranking.enRangoPuntaje(niveles[i].getMin(), niveles[i].getMax()).size();
        return res;
    }

    /**
     * Aplica la bonificación por buen comportamiento (5 puntos por año sin infracciones) a todos
     * y reubica a cada conductor en el ranking. Devuelve los conductores que cambiaron de posición.
     */
    public List<String> aplicarBonificacionATodos(GestorConductores gestorC) throws SecrException {
        List<String> cambios = new ArrayList<>();
        for (Conductor c : gestorC.listarTodos()) {
            double antes = c.getPuntajeActual();
            gestorC.aplicarBonificacionBuenComportamiento(c.getLicencia().getNumero());
            reubicar(c);
            if (c.getPuntajeActual() != antes) cambios.add(c.getLicencia().getNumero());
        }
        return cambios;
    }

    public NivelRiesgo clasificar(Conductor c) { return NivelRiesgo.clasificar(c.getPuntajeActual()); }
    public int tamanio() { return ranking.tamanio(); }
}
