package estructuras.comparacion;

import modelo.Conductor;

/** Utilidad interna: convierte la excepción verificada del dominio en una no verificada. */
final class Puntajes {
    private Puntajes() { }

    static void asignar(Conductor c, double puntaje) {
        try {
            c.setPuntajeActual(puntaje);
        } catch (modelo.Main.PuntajeNoValidoException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
