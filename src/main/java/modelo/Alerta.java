package modelo;

import java.time.LocalDate;

public class Alerta {
    private String nivel; // VERDE, AMARILLA, NARANJA, ROJA
    private String mensaje;
    private LocalDate fechaAlerta;

    public Alerta(String nivel, String mensaje) {
        this.nivel = nivel;
        this.mensaje = mensaje;
        this.fechaAlerta = LocalDate.now();
    }

    public static Alerta generarAlerta(Conductor c) {
        double puntaje = c.getPuntajeActual();
        String nivel;
        String mensaje;

        if (puntaje >= 80) {
            nivel = "VERDE";
            mensaje = "Conductor responsable. ¡Siga manteniendo un excelente nivel de conducción!";
        } else if (puntaje >= 60) {
            nivel = "AMARILLA";
            mensaje = "Atención: su puntaje está bajando. Conduzca con más cuidado.";
        } else if (puntaje >= 40) {
            nivel = "NARANJA";
            mensaje = "Riesgo moderado. Se recomienda realizar un curso de manejo defensivo.";
        } else {
            nivel = "ROJA";
            mensaje = "¡PELIGRO! Conductor de alto riesgo. Requiere intervención inmediata.";
        }

        Alerta alerta = new Alerta(nivel, mensaje);
        System.out.println(alerta);
        return alerta;
    }

    public String getNivel() {
        return nivel;
    }

    public String getMensaje() {
        return mensaje;
    }

    @Override
    public String toString() {
        return ">>> ALERTA [" + nivel + "] | " + fechaAlerta + "\n " + mensaje;
    }
}
