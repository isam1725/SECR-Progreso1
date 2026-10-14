package util;

import static modelo.Main.*;
import modelo.*;
import negocio.*;
import java.time.LocalDate;
import java.util.List;

public class SistemaSECR {
    private GestorConductores gestorConductores;
    private GestorEventos gestorEventos;
    private GestorCursos gestorCursos;
    private GestorReportes gestorReportes;

    public SistemaSECR() {
        gestorConductores = new GestorConductores();
        gestorEventos = new GestorEventos();
        gestorCursos = new GestorCursos();
        gestorReportes = new GestorReportes();
    }

    public void cargarDatosPrueba() {
        System.out.println("Cargando datos de prueba...");
        try {
            Licencia lic1 = new Licencia("B-001234", "B", LocalDate.of(2020, 3, 10), LocalDate.of(2026, 3, 10));
            Conductor c1 = new Conductor("0901234567", "Carlos Mendoza Pinto", LocalDate.of(1989, 5, 15), lic1);
            gestorConductores.registrarConductor(c1);

            Licencia lic2 = new Licencia("B-005678", "B", LocalDate.of(2021, 7, 15), LocalDate.of(2027, 7, 15));
            Conductor c2 = new Conductor("0912345675", "Sofía Torres Vera", LocalDate.of(1998, 2, 20), lic2);
            gestorConductores.registrarConductor(c2);

            Licencia lic3 = new Licencia("C-009012", "C", LocalDate.of(2019, 1, 5), LocalDate.of(2024, 12, 31));
            Conductor c3 = new Conductor("0923456784", "Luis Ramírez Calle", LocalDate.of(1982, 11, 8), lic3);
            gestorConductores.registrarConductor(c3);

            Licencia lic4 = new Licencia("B-003456", "B", LocalDate.of(2023, 9, 20), LocalDate.of(2029, 9, 20));
            Conductor c4 = new Conductor("0934567892", "Ana Suárez Mora", LocalDate.of(2002, 7, 12), lic4);
            gestorConductores.registrarConductor(c4);

            Licencia lic5 = new Licencia("E-007890", "E", LocalDate.of(2018, 4, 12), LocalDate.of(2026, 4, 12));
            Conductor c5 = new Conductor("0945678902", "Pedro Gutiérrez Loor", LocalDate.of(1970, 9, 25), lic5);
            gestorConductores.registrarConductor(c5);

            cargarEventosPrueba();
            gestorConductores.aplicarBonificacionATodos();
            System.out.println("✅ Datos de prueba cargados correctamente.\n");
        } catch (SecrException e) {
            System.out.println("❌ Error al cargar datos de prueba: " + e.getMessage());
        }
    }

    private void cargarEventosPrueba() throws SecrException {
        gestorEventos.registrarEvento("B-001234", 0, LocalDate.of(2025, 11, 5),
                "Av. Quito y 10 de Agosto", 2, "", gestorConductores);
        gestorEventos.registrarEvento("B-001234", 2, LocalDate.of(2026, 1, 18),
                "Redondel Norte", 1, "", gestorConductores);
        gestorEventos.registrarEvento("B-005678", 0, LocalDate.of(2025, 9, 3),
                "Panamericana Sur km 12", 3, "", gestorConductores);
        gestorEventos.registrarEvento("C-009012", 5, LocalDate.of(2025, 8, 10),
                "Av. Simón Bolívar", 4, "", gestorConductores);
    }

    public GestorConductores getGestorConductores() { return gestorConductores; }
    public GestorEventos getGestorEventos() { return gestorEventos; }
    public GestorCursos getGestorCursos() { return gestorCursos; }
    public GestorReportes getGestorReportes() { return gestorReportes; }

    public String mostrarColeccion(List<?> lista) {
        if (lista.isEmpty()) return "No hay registros para mostrar.\n";
        StringBuilder sb = new StringBuilder();
        for (Object obj : lista) sb.append(obj).append("\n");
        return sb.toString();
    }
}
