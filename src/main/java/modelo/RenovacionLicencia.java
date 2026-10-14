package modelo;

import java.time.LocalDate;

public class RenovacionLicencia {
    private LocalDate fechaRenovacion;
    private String numeroLicencia;
    private LocalDate vencimientoAnterior;
    private LocalDate vencimientoNuevo;

    public RenovacionLicencia(String numeroLicencia, LocalDate vencimientoAnterior, LocalDate vencimientoNuevo) {
        this.fechaRenovacion = LocalDate.now();
        this.numeroLicencia = numeroLicencia;
        this.vencimientoAnterior = vencimientoAnterior;
        this.vencimientoNuevo = vencimientoNuevo;
    }

    public LocalDate getFechaRenovacion() { return fechaRenovacion; }
    public String getNumeroLicencia() { return numeroLicencia; }
    public LocalDate getVencimientoAnterior() { return vencimientoAnterior; }
    public LocalDate getVencimientoNuevo() { return vencimientoNuevo; }

    @Override
    public String toString() {
        return "Renovación: " + fechaRenovacion
                + " | Licencia: " + numeroLicencia
                + " | Vencía: " + vencimientoAnterior
                + " | Nuevo vencimiento: " + vencimientoNuevo;
    }
}
