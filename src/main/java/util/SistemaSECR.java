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
    private GestorRiesgo gestorRiesgo;

    public SistemaSECR() {
        gestorConductores = new GestorConductores();
        gestorEventos = new GestorEventos();
        gestorCursos = new GestorCursos();
        gestorReportes = new GestorReportes();
        gestorRiesgo = new GestorRiesgo();
        gestorEventos.setGestorRiesgo(gestorRiesgo);   // M2 avisa a M3 cada cambio de puntaje
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

            cargarConductoresAdicionales();
            cargarEventosPrueba();
            gestorConductores.aplicarBonificacionATodos();
            gestorRiesgo.sincronizar(gestorConductores);   // M3: ranking inicial
            gestorEventos.limpiarHistorialDeshacer();      // los datos de prueba no se pueden deshacer
            System.out.println("✅ Datos de prueba cargados correctamente.\n");
        } catch (SecrException e) {
            System.out.println("❌ Error al cargar datos de prueba: " + e.getMessage());
        }
    }

    /** Conductores adicionales para que el ranking de riesgo (M3) tenga casos de todos los niveles. */
    private void cargarConductoresAdicionales() throws SecrException {
        Object[][] datos = {
            {"B-010203", "B", LocalDate.of(2022, 5, 10), "1703456788", "Valentina Andrade Cevallos", LocalDate.of(1995, 4, 2)},
            {"C-020304", "C", LocalDate.of(2021, 2, 14), "1712345600", "Diego Herrera Salazar", LocalDate.of(1987, 10, 21)},
            {"B-030405", "B", LocalDate.of(2024, 1, 8), "1723456719", "Gabriela Paredes Moreno", LocalDate.of(2000, 12, 3)},
            {"E-040506", "E", LocalDate.of(2020, 8, 22), "0908765431", "Jorge Espinoza Pazmiño", LocalDate.of(1975, 6, 30)},
            {"B-050607", "B", LocalDate.of(2023, 6, 1), "1304567892", "Lucía Chávez Aguirre", LocalDate.of(1992, 3, 17)},
            {"C-060708", "C", LocalDate.of(2022, 11, 11), "1109876548", "Andrés Guerrero Loor", LocalDate.of(1984, 8, 9)},
            {"B-070809", "B", LocalDate.of(2025, 2, 2), "1711223345", "Paula Moreno Zambrano", LocalDate.of(2003, 1, 25)},
        };
        for (Object[] d : datos) {
            LocalDate emision = (LocalDate) d[2];
            Licencia lic = new Licencia((String) d[0], (String) d[1], emision, emision.plusYears(5));
            gestorConductores.registrarConductor(new Conductor((String) d[3], (String) d[4], (LocalDate) d[5], lic));
        }
    }

    private void cargarEventosPrueba() throws SecrException {
        // Índices de tipo: 0 velocidad (5), 1 celular (4), 2 frenada (3), 3 carril (3), 4 señal (6), 5 bajo efectos (10)
        Object[][] ev = {
            {"B-010203", 1, LocalDate.of(2026, 9, 14), "Av. Amazonas y Naciones Unidas", 3},
            {"B-010203", 4, LocalDate.of(2026, 10, 2), "Av. 6 de Diciembre", 2},
            {"C-020304", 0, LocalDate.of(2026, 3, 3), "Panamericana Norte km 5", 4},
            {"C-020304", 5, LocalDate.of(2026, 7, 19), "Av. Simón Bolívar", 3},
            {"C-020304", 3, LocalDate.of(2026, 8, 30), "Av. Simón Bolívar", 4},
            {"B-030405", 2, LocalDate.of(2026, 5, 11), "Redondel Norte", 1},
            {"E-040506", 0, LocalDate.of(2025, 12, 20), "Av. Mariscal Sucre", 5},
            {"E-040506", 4, LocalDate.of(2026, 6, 6), "Av. 10 de Agosto", 5},
            {"E-040506", 1, LocalDate.of(2026, 10, 28), "Av. Eloy Alfaro", 3},
            {"C-060708", 3, LocalDate.of(2026, 4, 4), "Av. Occidental", 3},
            {"C-060708", 0, LocalDate.of(2026, 11, 2), "Av. Occidental", 2},
            {"B-070809", 1, LocalDate.of(2026, 10, 15), "Av. Amazonas y Colón", 4},
        };
        for (Object[] e : ev) {
            gestorEventos.registrarEvento((String) e[0], (Integer) e[1], (LocalDate) e[2], (String) e[3],
                    (Integer) e[4], "", gestorConductores);
        }
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
    public GestorRiesgo getGestorRiesgo() { return gestorRiesgo; }

    public String mostrarColeccion(List<?> lista) {
        if (lista.isEmpty()) return "No hay registros para mostrar.\n";
        StringBuilder sb = new StringBuilder();
        for (Object obj : lista) sb.append(obj).append("\n");
        return sb.toString();
    }
}
