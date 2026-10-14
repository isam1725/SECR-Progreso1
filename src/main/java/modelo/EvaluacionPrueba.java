package modelo;

import java.time.LocalDate;

/**
 * Clase para guardar la información resumida de una prueba rendida.
 * Permite reportar conductor, curso, fecha, nota, aprobación y puntos recuperados.
 */
public class EvaluacionPrueba {
    private Conductor conductor;
    private Curso curso;
    private LocalDate fecha;
    private double nota;
    private boolean aprobado;
    private int puntosRecuperados;

    public EvaluacionPrueba(Conductor conductor, Curso curso, LocalDate fecha, double nota, boolean aprobado, int puntosRecuperados) {
        this.conductor = conductor;
        this.curso = curso;
        this.fecha = fecha;
        this.nota = nota;
        this.aprobado = aprobado;
        this.puntosRecuperados = puntosRecuperados;
    }

    public Conductor getConductor() { return conductor; }
    public Curso getCurso() { return curso; }
    public LocalDate getFecha() { return fecha; }
    public double getNota() { return nota; }
    public boolean isAprobado() { return aprobado; }
    public int getPuntosRecuperados() { return puntosRecuperados; }

    @Override
    public String toString() {
        return fecha + " | " + conductor.getNombreCompleto() + " | " + curso.getNombre()
                + " | Nota: " + String.format("%.1f", nota) + "%"
                + " | " + (aprobado ? "APROBADA" : "REPROBADA")
                + " | Puntos: +" + puntosRecuperados;
    }
}
