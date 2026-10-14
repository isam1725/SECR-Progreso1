package modelo;

import java.time.LocalDate;

public class Licencia {
    public static final int DURACION_ANIOS = 5;

    private String numero;
    private String categoria;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;

    /**
     * Constructor recomendado: calcula automáticamente la fecha de vencimiento.
     * La licencia dura 5 años desde la fecha de emisión.
     */
    public Licencia(String numero, String categoria, LocalDate emision) {
        this.numero = numero;
        this.categoria = categoria;
        renovarDesde(emision);
    }

    /**
     * Constructor anterior: se mantiene para no dañar compatibilidad con datos de prueba.
     */
    public Licencia(String numero, String categoria, LocalDate emision, LocalDate vencimiento) {
        this.numero = numero;
        this.categoria = categoria;
        this.fechaEmision = emision;
        this.fechaVencimiento = vencimiento;
    }

    public void renovarDesde(LocalDate nuevaFechaEmision) {
        this.fechaEmision = nuevaFechaEmision;
        this.fechaVencimiento = nuevaFechaEmision.plusYears(DURACION_ANIOS);
    }

    public boolean estaVencida() {
        return LocalDate.now().isAfter(fechaVencimiento);
    }

    public boolean estaCercaDeCaducar(int diasAnticipacion) {
        return LocalDate.now().plusDays(diasAnticipacion).isAfter(fechaVencimiento);
    }

    public String getNumero() {
        return numero;
    }

    public String getCategoria() {
        return categoria;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fecha) {
        this.fechaVencimiento = fecha;
    }

    @Override
    public String toString() {
        String estado = estaVencida() ? "VENCIDA" : "VIGENTE";
        return "Licencia: " + numero
                + " | Categoría: " + categoria
                + " | Emisión: " + fechaEmision
                + " | Vence: " + fechaVencimiento
                + " [" + estado + "]";
    }
}
