package modelo;

/**
 * Semáforo de riesgo del conductor según su puntaje (0 a 100).
 * Cada nivel define el intervalo [min, max] de puntaje que le corresponde.
 */
public enum NivelRiesgo {
    ROJO("Rojo · riesgo alto", 0, 39.99),
    NARANJA("Naranja · riesgo medio-alto", 40, 59.99),
    AMARILLO("Amarillo · riesgo medio", 60, 79.99),
    VERDE("Verde · riesgo bajo", 80, 100);

    private final String etiqueta;
    private final double min, max;

    NivelRiesgo(String etiqueta, double min, double max) {
        this.etiqueta = etiqueta;
        this.min = min;
        this.max = max;
    }

    public String getEtiqueta() { return etiqueta; }
    public double getMin() { return min; }
    public double getMax() { return max; }

    public static NivelRiesgo clasificar(double puntaje) {
        if (puntaje < 40) return ROJO;
        if (puntaje < 60) return NARANJA;
        if (puntaje < 80) return AMARILLO;
        return VERDE;
    }

    @Override
    public String toString() { return etiqueta; }
}
