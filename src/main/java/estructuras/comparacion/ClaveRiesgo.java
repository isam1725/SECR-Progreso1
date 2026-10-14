package estructuras.comparacion;

/** Clave compuesta para ordenar por riesgo: menor puntaje primero; en empate, por licencia (determinista). */
public record ClaveRiesgo(double puntaje, String licencia) implements Comparable<ClaveRiesgo> {
    @Override
    public int compareTo(ClaveRiesgo o) {
        int c = Double.compare(puntaje, o.puntaje);
        return c != 0 ? c : licencia.compareTo(o.licencia);
    }
}
