package modelo;

import static modelo.Main.*;
import java.time.LocalDate;

public class EventoConduccion {
    protected String idEvento;
    protected LocalDate fechaHora;
    protected String ubicacion;
    protected int gravedad; // 1 a 5
    protected String descripcion;
    protected TipoEvento tipo;

    public EventoConduccion(String idEvento, LocalDate fechaHora, String ubicacion, int gravedad,
                            String descripcion, TipoEvento tipo) throws EventoNoValidoException {
        if (idEvento == null || idEvento.trim().isEmpty()) throw new EventoNoValidoException("El ID del evento no puede estar vacío.");
        Validaciones.validarFechaEvento(fechaHora);
        Validaciones.validarUbicacion(ubicacion);
        if (tipo == null) throw new EventoNoValidoException("Debe seleccionar un tipo de evento válido.");
        if (gravedad < 1 || gravedad > 5) throw new EventoNoValidoException("La gravedad debe estar entre 1 y 5.");

        this.idEvento = idEvento;
        this.fechaHora = fechaHora;
        this.ubicacion = ubicacion.trim();
        this.gravedad = gravedad;
        this.descripcion = descripcion != null ? descripcion : "";
        this.tipo = tipo;
    }

    public int calcularPuntosDeduccion() { return tipo.getPuntosBase() * gravedad; }

    public String clasificarGravedad() {
        return switch (gravedad) {
            case 1 -> "Leve";
            case 2 -> "Moderado";
            case 3 -> "Grave";
            case 4, 5 -> "Muy Grave";
            default -> "Desconocido";
        };
    }

    public String getIdEvento() { return idEvento; }
    public LocalDate getFechaHora() { return fechaHora; }
    public String getUbicacion() { return ubicacion; }
    public int getGravedad() { return gravedad; }
    public String getDescripcion() { return descripcion; }
    public TipoEvento getTipo() { return tipo; }

    @Override
    public String toString() {
        return " [" + idEvento + "] " + tipo.getNombre()
                + " | Fecha: " + fechaHora
                + " | Gravedad: " + clasificarGravedad()
                + " | Deducción: -" + calcularPuntosDeduccion() + " pts"
                + " | Lugar: " + ubicacion;
    }
}
