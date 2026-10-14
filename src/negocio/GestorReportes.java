package negocio;

import modelo.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Gestor encargado de generar reportes del sistema.
 * Se movió la lógica de reportes desde modelo hacia negocio,
 * como observación de la docente.
 */
public class GestorReportes {
    private LocalDate fechaGeneracion;

    public GestorReportes() {
        this.fechaGeneracion = LocalDate.now();
    }

    public String generarReporteIndividual(Conductor conductor) {
        HistorialConduccion historial = conductor.getHistorialConduccion();
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================\n");
        sb.append(" REPORTE INDIVIDUAL DE CONDUCTOR \n");
        sb.append("========================================\n");
        sb.append("Generado el: ").append(fechaGeneracion).append("\n\n");
        sb.append(conductor.toString()).append("\n\n");

        sb.append("--- AVISO DE RENOVACIÓN DE LICENCIA ---\n");
        if (conductor.getLicencia().estaVencida()) {
            sb.append("La licencia está VENCIDA. Necesita renovar la licencia de forma inmediata.\n");
        } else if (conductor.getLicencia().estaCercaDeCaducar(30)) {
            sb.append("La licencia está próxima a vencer en 30 días o menos.\n");
        } else {
            sb.append("La licencia se encuentra vigente.\n");
        }

        sb.append("\n--- REGISTRO DE LICENCIAS ---\n");
        sb.append(conductor.getLicencia()).append("\n");
        sb.append("Cantidad de renovaciones registradas: ").append(conductor.getHistorialRenovaciones().size()).append("\n");
        if (conductor.getHistorialRenovaciones().isEmpty()) {
            sb.append("Sin renovaciones registradas.\n");
        } else {
            for (RenovacionLicencia r : conductor.getHistorialRenovaciones()) sb.append(r).append("\n");
        }

        List<EventoConduccion> eventos = historial.getEventos();
        sb.append("\n--- HISTORIAL DE EVENTOS / INFRACCIONES (").append(eventos.size()).append(" registros) ---\n");
        if (eventos.isEmpty()) sb.append("Sin eventos registrados.\n");
        else for (EventoConduccion e : eventos) sb.append(e).append("\n");

        sb.append("\n--- HISTORIAL DE CURSOS Y PRUEBAS RENDIDAS ---\n");
        sb.append("Cantidad de pruebas rendidas: ").append(conductor.getHistorialPruebas().size()).append("\n");
        if (conductor.getHistorialPruebas().isEmpty()) {
            sb.append("Sin pruebas rendidas.\n");
        } else {
            for (Prueba p : conductor.getHistorialPruebas()) {
                sb.append(p).append("\n");
                sb.append("  Año de rendición: ").append(p.getFecha().getYear()).append("\n");
                if (p.getEvaluacion() != null) sb.append("  Evaluación: ").append(p.getEvaluacion()).append("\n");
            }
        }

        sb.append("\n--- BONIFICACIÓN POR BUEN COMPORTAMIENTO ---\n");
        sb.append("Años sin infracciones: ").append(conductor.calcularAniosSinInfracciones()).append("\n");
        sb.append("Bonificación registrada: +")
                .append(String.format("%.1f", historial.getBonificacionBuenComportamiento())).append(" pts\n");

        double puntajeFinal = historial.calcularPuntajeFinal();
        sb.append("\n--- PUNTAJE FINAL: ").append(String.format("%.1f", puntajeFinal)).append(" / 100 ---\n");
        sb.append("Clasificación: ").append(clasificarRiesgo(puntajeFinal)).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    public String generarReporteGeneral(List<Conductor> conductores) {
        if (conductores.isEmpty()) return "No hay conductores registrados para generar estadísticas.";
        double sumaPuntajes = 0;
        double sumaEdades = 0;
        int activos = 0;
        int inactivos = 0;
        int totalInfracciones = 0;
        int totalPruebas = 0;
        int totalRenovaciones = 0;

        for (Conductor c : conductores) {
            sumaPuntajes += c.getPuntajeActual();
            sumaEdades += c.getEdad();
            if ("Activo".equalsIgnoreCase(c.getEstado())) activos++; else inactivos++;
            totalInfracciones += c.getInfracciones().size();
            totalPruebas += c.getHistorialPruebas().size();
            totalRenovaciones += c.getHistorialRenovaciones().size();
        }
        int total = conductores.size();
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================\n");
        sb.append(" REPORTE ESTADÍSTICO GENERAL \n");
        sb.append("========================================\n");
        sb.append("Total conductores: ").append(total).append("\n");
        sb.append("Activos: ").append(activos).append(" | Inactivos: ").append(inactivos).append("\n");
        sb.append("Puntaje promedio: ").append(String.format("%.1f", sumaPuntajes / total)).append(" pts\n");
        sb.append("Edad promedio: ").append(String.format("%.1f", sumaEdades / total)).append(" años\n");
        sb.append("Total infracciones: ").append(totalInfracciones).append("\n");
        sb.append("Total pruebas rendidas: ").append(totalPruebas).append("\n");
        sb.append("Total renovaciones de licencia: ").append(totalRenovaciones).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    private String clasificarRiesgo(double puntaje) {
        if (puntaje >= 80) return "VERDE - Conductor responsable";
        else if (puntaje >= 60) return "AMARILLA - Atención, puntaje en descenso";
        else if (puntaje >= 40) return "NARANJA - Riesgo moderado";
        else return "ROJA - Alto riesgo";
    }
}
