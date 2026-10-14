package modelo;

import java.util.ArrayList;
import java.util.List;

public class Curso {
    private String codigo;
    private String nombre;
    private String descripcion;
    private int puntosRecuperacion;
    private int duracionHoras;
    private double porcentajeMinimoAprobacion;
    private String estado;
    private List<Pregunta> preguntas;

    public Curso(String codigo, String nombre, int puntos, int horas) {
        this(codigo, nombre, "Curso de recuperación de puntos", puntos, horas, 70.0, "Activo");
    }

    public Curso(String codigo, String nombre, String descripcion, int puntos, int horas,
                 double porcentajeMinimoAprobacion, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntosRecuperacion = puntos;
        this.duracionHoras = horas;
        this.porcentajeMinimoAprobacion = porcentajeMinimoAprobacion;
        this.estado = estado == null ? "Activo" : estado;
        this.preguntas = new ArrayList<>();
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getPuntosRecuperacion() { return puntosRecuperacion; }
    public int getDuracionHoras() { return duracionHoras; }
    public double getPorcentajeMinimoAprobacion() { return porcentajeMinimoAprobacion; }
    public String getEstado() { return estado; }
    public List<Pregunta> getPreguntas() { return preguntas; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPuntosRecuperacion(int puntosRecuperacion) { this.puntosRecuperacion = puntosRecuperacion; }
    public void setDuracionHoras(int duracionHoras) { this.duracionHoras = duracionHoras; }
    public void setPorcentajeMinimoAprobacion(double porcentajeMinimoAprobacion) { this.porcentajeMinimoAprobacion = porcentajeMinimoAprobacion; }
    public void setEstado(String estado) { this.estado = estado; }

    public void agregarPregunta(Pregunta pregunta) {
        if (pregunta != null) preguntas.add(pregunta);
    }

    public boolean estaActivo() {
        return "Activo".equalsIgnoreCase(estado);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre
                + " | Recupera: " + puntosRecuperacion + " pts"
                + " | Duración: " + duracionHoras + " horas"
                + " | Mínimo: " + String.format("%.1f", porcentajeMinimoAprobacion) + "%"
                + " | Estado: " + estado;
    }
}
