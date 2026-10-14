package modelo;

import static modelo.Main.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Conductor {
    private String cedula;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private Licencia licencia;
    private double puntajeActual;
    private String estado; // Activo / Inactivo
    private List<Infraccion> infracciones;
    private List<Prueba> historialPruebas;
    private List<RenovacionLicencia> historialRenovaciones;
    private HistorialConduccion historialConduccion;

    public Conductor(String cedula, String nombreCompleto, LocalDate fechaNacimiento, Licencia licencia) {
        this.cedula = cedula;
        this.nombreCompleto = nombreCompleto;
        this.fechaNacimiento = fechaNacimiento;
        this.licencia = licencia;
        this.puntajeActual = 100.0;
        this.estado = "Activo";
        this.infracciones = new ArrayList<>();
        this.historialPruebas = new ArrayList<>();
        this.historialRenovaciones = new ArrayList<>();
        this.historialConduccion = new HistorialConduccion();
    }

    public int getEdad() { return Period.between(fechaNacimiento, LocalDate.now()).getYears(); }

    public boolean puedeRenovarLicencia() {
        return getPuntajeActual() >= 50 && "Activo".equalsIgnoreCase(estado) && licencia.estaVencida();
    }

    public void cambiarEstado(String nuevoEstado) throws EstadoInvalidoException {
        if (nuevoEstado == null) {
            throw new EstadoInvalidoException("El estado no puede estar vacío. Use Activo o Inactivo.");
        }
        if ("Activo".equalsIgnoreCase(nuevoEstado) || "Inactivo".equalsIgnoreCase(nuevoEstado)) {
            this.estado = nuevoEstado.substring(0, 1).toUpperCase() + nuevoEstado.substring(1).toLowerCase();
            System.out.println("✅ Estado cambiado exitosamente a: " + this.estado);
        } else {
            throw new EstadoInvalidoException("Estado inválido. Use 'Activo' o 'Inactivo'.");
        }
    }

    public void agregarInfraccion(Infraccion infraccion) {
        infracciones.add(infraccion);
        historialConduccion.agregarEvento(infraccion);
    }

    public void agregarPrueba(Prueba prueba) {
        historialPruebas.add(prueba);
        if (prueba.estaAprobada()) {
            int ptsRec = prueba.getCurso().getPuntosRecuperacion();
            historialConduccion.agregarRecuperacion(ptsRec);
            puntajeActual = Math.min(100.0, puntajeActual + ptsRec);
        }
    }

    public void agregarRenovacion(RenovacionLicencia renovacion) { historialRenovaciones.add(renovacion); }

    public boolean tieneHistorial() {
        return !infracciones.isEmpty() || !historialPruebas.isEmpty() || !historialRenovaciones.isEmpty();
    }

    public long calcularAniosSinInfracciones() {
        if (infracciones.isEmpty()) {
            return Math.max(0, ChronoUnit.YEARS.between(licencia.getFechaEmision(), LocalDate.now()));
        }
        LocalDate ultima = infracciones.get(0).getFechaHora();
        for (Infraccion inf : infracciones) {
            if (inf.getFechaHora().isAfter(ultima)) ultima = inf.getFechaHora();
        }
        return Math.max(0, ChronoUnit.YEARS.between(ultima, LocalDate.now()));
    }

    public String getCedula() { return cedula; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombre) { this.nombreCompleto = nombre; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate f) { this.fechaNacimiento = f; }
    public Licencia getLicencia() { return licencia; }
    public double getPuntajeActual() { return puntajeActual; }

    public void setPuntajeActual(double puntaje) throws PuntajeNoValidoException {
        if (puntaje < 0 || puntaje > 100) {
            throw new PuntajeNoValidoException("El puntaje debe estar entre 0 y 100.");
        }
        this.puntajeActual = puntaje;
    }

    public String getEstado() { return estado; }
    public List<Infraccion> getInfracciones() { return infracciones; }
    public List<Prueba> getHistorialPruebas() { return historialPruebas; }
    public List<RenovacionLicencia> getHistorialRenovaciones() { return historialRenovaciones; }
    public HistorialConduccion getHistorialConduccion() { return historialConduccion; }

    @Override
    public String toString() {
        return "=== CONDUCTOR ===\n"
                + "Nombre: " + nombreCompleto + "\n"
                + "Cédula: " + cedula + "\n"
                + "Fecha Nacimiento: " + fechaNacimiento + " (" + getEdad() + " años)\n"
                + licencia + "\n"
                + "Puntaje Actual: " + String.format("%.1f", puntajeActual) + " pts\n"
                + "Estado: " + estado;
    }
}
