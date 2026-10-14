package modelo;

import static modelo.Main.*;
import java.time.LocalDate;

public class Prueba {
    private String id;
    private LocalDate fecha;
    private Conductor conductor;
    private Curso curso;
    private double calificacion;
    private String motivo;
    private EvaluacionPrueba evaluacion;

    public Prueba(String id, Conductor conductor, Curso curso, String motivo) throws PruebaNoValidaException {
        if (conductor == null) throw new PruebaNoValidaException("No se puede crear la prueba sin conductor.");
        if (curso == null) throw new PruebaNoValidaException("Debe seleccionar un curso para rendir la prueba.");
        this.id = id;
        this.fecha = LocalDate.now();
        this.conductor = conductor;
        this.curso = curso;
        this.motivo = motivo;
        this.calificacion = 0.0;
    }

    public boolean estaAprobada() { return calificacion >= curso.getPorcentajeMinimoAprobacion(); }
    public String getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public Conductor getConductor() { return conductor; }
    public Curso getCurso() { return curso; }
    public double getCalificacion() { return calificacion; }

    public void setCalificacion(double calificacion) throws PruebaNoValidaException {
        if (calificacion < 0 || calificacion > 100) throw new PruebaNoValidaException("La calificación debe estar entre 0 y 100.");
        this.calificacion = calificacion;
        this.evaluacion = new EvaluacionPrueba(conductor, curso, fecha, calificacion, estaAprobada(),
                estaAprobada() ? curso.getPuntosRecuperacion() : 0);
    }

    public String getMotivo() { return motivo; }
    public EvaluacionPrueba getEvaluacion() { return evaluacion; }

    @Override
    public String toString() {
        return "Prueba[" + id + "] | " + fecha + " | " + curso.getNombre()
                + " | Nota: " + String.format("%.1f", calificacion) + "%"
                + " | Mínimo: " + String.format("%.1f", curso.getPorcentajeMinimoAprobacion()) + "%"
                + (estaAprobada() ? " (APROBADA)" : " (REPROBADA)");
    }
}
