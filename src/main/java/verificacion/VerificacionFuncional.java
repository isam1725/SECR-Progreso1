package verificacion;

import static modelo.Main.*;
import modelo.Conductor;
import modelo.EventoConduccion;
import modelo.Infraccion;
import modelo.NivelRiesgo;
import negocio.GestorConductores;
import negocio.GestorEventos;
import negocio.GestorRiesgo;
import estructuras.ListaDoble;
import util.SistemaSECR;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Registro de verificación funcional de los módulos M2 (infracciones) y M3 (riesgo).
 * Ejecuta cada caso VF sobre la misma lógica que usa la interfaz gráfica y escribe
 * docs/verificacion/resultados_verificacion.csv con el resultado obtenido.
 * Ejecutar: java -cp out verificacion.VerificacionFuncional
 */
public class VerificacionFuncional {

    private record Caso(String id, String modulo, String funcionalidad, String entrada, String esperado,
                        String obtenido, boolean cumple) { }

    private static final List<Caso> casos = new ArrayList<>();
    private static SistemaSECR sistema;
    private static GestorConductores gc;
    private static GestorEventos ge;
    private static GestorRiesgo gr;

    public static void main(String[] args) throws Exception {
        java.io.PrintStream original = System.out;
        System.setOut(new java.io.PrintStream(java.io.OutputStream.nullOutputStream()));   // silencia mensajes de carga
        sistema = new SistemaSECR();
        sistema.cargarDatosPrueba();
        System.setOut(original);
        gc = sistema.getGestorConductores();
        ge = sistema.getGestorEventos();
        gr = sistema.getGestorRiesgo();

        // ------------------------------------------------------------- M2
        Conductor gab = gc.buscarPorLicencia("B-030405");
        double antes = gab.getPuntajeActual();
        int eventosAntes = ge.totalEventos();
        Infraccion inf = ge.registrarEvento("B-030405", 1, LocalDate.of(2026, 11, 10), "Av. Amazonas y Colón", 2, "Prueba VF01", gc);
        caso("VF01", "M2", "Registrar infracción válida (cálculo de deducción, índice AVL y pila)",
                "B-030405 · Uso de celular (4 pts) · gravedad 2 · 10/11/2026",
                "Deducción 8 pts; puntaje " + fmt(antes) + " → " + fmt(antes - 8) + "; eventos +1; pila = 1",
                "Deducción " + inf.calcularPuntosDeduccion() + " pts; puntaje " + fmt(antes) + " → " + fmt(gab.getPuntajeActual())
                        + "; eventos " + eventosAntes + " → " + ge.totalEventos() + "; pila = " + ge.operacionesParaDeshacer(),
                inf.calcularPuntosDeduccion() == 8 && gab.getPuntajeActual() == Math.max(0, antes - 8)
                        && ge.totalEventos() == eventosAntes + 1 && ge.operacionesParaDeshacer() == 1);

        error("VF02", "M2", "Registrar infracción a una licencia inexistente", "Z-999999 · Exceso de velocidad",
                "Rechazo: no existe el conductor", () -> ge.registrarEvento("Z-999999", 0, LocalDate.of(2026, 11, 1), "Av. Colón", 1, "", gc),
                "No existe un conductor");
        error("VF03", "M2", "Registrar infracción con fecha futura", "B-050607 · fecha 01/01/2030",
                "Rechazo por fecha futura", () -> ge.registrarEvento("B-050607", 0, LocalDate.of(2030, 1, 1), "Av. Colón", 1, "", gc),
                "futura");
        error("VF04", "M2", "Registrar infracción con gravedad fuera de rango", "B-050607 · gravedad 6",
                "Rechazo: la gravedad debe estar entre 1 y 5", () -> ge.registrarEvento("B-050607", 0, LocalDate.of(2026, 11, 1), "Av. Colón", 6, "", gc),
                "gravedad");

        List<EventoConduccion> rango = ge.consultarPorRango(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 10, 31), null, gc);
        boolean cronologico = true;
        for (int i = 1; i < rango.size(); i++) cronologico &= !rango.get(i).getFechaHora().isBefore(rango.get(i - 1).getFechaHora());
        caso("VF05", "M2", "Consulta por rango de fechas (AVL por fecha)", "01/09/2026 a 31/10/2026, todos los conductores",
                "4 eventos (14/09, 02/10, 15/10, 28/10) en orden cronológico",
                rango.size() + " eventos: " + fechas(rango) + (cronologico ? ", en orden" : ", DESORDENADOS"),
                rango.size() == 4 && cronologico && fechas(rango).equals("14/09, 02/10, 15/10, 28/10"));

        error("VF06", "M2", "Consulta con rango invertido", "31/10/2026 a 01/09/2026",
                "Rechazo: fecha inicial posterior a la final", () -> ge.consultarPorRango(LocalDate.of(2026, 10, 31), LocalDate.of(2026, 9, 1), null, gc),
                "posterior");

        GestorEventos.OperacionRegistro op = ge.deshacerUltimoRegistro(gc);
        caso("VF07", "M2", "Deshacer el último registro (pila LIFO)", "Deshacer tras VF01",
                "Se revierte EVT de VF01; puntaje vuelve a " + fmt(antes) + "; eventos " + eventosAntes + "; pila = 0",
                "Revertido " + op.infraccion().getIdEvento() + "; puntaje " + fmt(gab.getPuntajeActual()) + "; eventos " + ge.totalEventos()
                        + "; pila = " + ge.operacionesParaDeshacer(),
                op.infraccion() == inf && gab.getPuntajeActual() == antes && ge.totalEventos() == eventosAntes && ge.operacionesParaDeshacer() == 0);

        error("VF08", "M2", "Deshacer con la pila vacía (caso límite)", "Deshacer sin registros pendientes",
                "Aviso: no hay registros para deshacer", () -> ge.deshacerUltimoRegistro(gc), "No hay registros");

        List<EventoConduccion> ord = ge.ordenar(ge.listarCronologico(), GestorEventos.CriterioOrden.GRAVEDAD);
        boolean desc = true;
        for (int i = 1; i < ord.size(); i++) desc &= ord.get(i - 1).getGravedad() >= ord.get(i).getGravedad();
        caso("VF09", "M2", "Ordenar eventos por gravedad (Merge Sort)", "Todos los eventos (" + ord.size() + ")",
                "Primero gravedad 5; secuencia no creciente",
                "Primero gravedad " + ord.get(0).getGravedad() + " (" + ord.get(0).getIdEvento() + "); " + (desc ? "no creciente" : "con errores"),
                ord.get(0).getGravedad() == 5 && desc);

        ListaDoble<Infraccion> hist = ge.historialNavegable("E-040506", gc);
        ListaDoble<Infraccion>.Cursor cur = hist.cursor();
        String primero = d(cur.actual().getFechaHora());
        boolean sinAnterior = !cur.hayAnterior();
        cur.avanzar(); cur.avanzar();
        String ultimo = d(cur.actual().getFechaHora());
        boolean sinSiguiente = !cur.haySiguiente();
        cur.retroceder();
        String medio = d(cur.actual().getFechaHora());
        caso("VF10", "M2", "Historial navegable (lista doblemente enlazada)", "E-040506: inicio → avanzar ×2 → retroceder",
                "3 eventos: 20/12/2025 → 28/10/2026 → 06/06/2026; sin anterior al inicio ni siguiente al final",
                hist.tamanio() + " eventos: " + primero + " → " + ultimo + " → " + medio + "; anterior al inicio: " + (sinAnterior ? "no" : "sí")
                        + "; siguiente al final: " + (sinSiguiente ? "no" : "sí"),
                hist.tamanio() == 3 && primero.equals("20/12/2025") && ultimo.equals("28/10/2026") && medio.equals("06/06/2026") && sinAnterior && sinSiguiente);

        Conductor diego = gc.buscarPorLicencia("C-020304");
        double pd = diego.getPuntajeActual();
        ge.registrarEvento("C-020304", 5, LocalDate.of(2026, 11, 12), "Av. Simón Bolívar", 4, "Prueba VF11", gc);
        String estadoTras = diego.getEstado();
        String rechazo = capturar(() -> ge.registrarEvento("C-020304", 0, LocalDate.of(2026, 11, 13), "Av. Simón Bolívar", 1, "", gc));
        ge.deshacerUltimoRegistro(gc);
        caso("VF11", "M2", "Puntaje llega a 0: inactivación y rechazo de nuevos registros (límite)",
                "C-020304 (" + fmt(pd) + " pts) + Conducción bajo efectos, gravedad 4 (−40)",
                "Puntaje 0; estado Inactivo; nuevo registro rechazado; al deshacer vuelve a Activo con " + fmt(pd),
                "Puntaje 0; estado " + estadoTras + "; nuevo registro: " + (rechazo == null ? "aceptado" : "rechazado") + "; tras deshacer: "
                        + diego.getEstado() + " con " + fmt(diego.getPuntajeActual()),
                "Inactivo".equals(estadoTras) && rechazo != null && "Activo".equals(diego.getEstado()) && diego.getPuntajeActual() == pd);

        // ------------------------------------------------------------- M3
        List<Conductor> oraculo = oraculo();
        List<Conductor> top3 = gr.topRiesgo(3);
        caso("VF12", "M3", "Top-k de mayor riesgo (recorrido en orden del AVL)", "k = 3",
                "Los 3 menores puntajes: " + lics(oraculo.subList(0, 3)), lics(top3), lics(top3).equals(lics(oraculo.subList(0, 3))));

        List<Conductor> todos = gr.topRiesgo(50);
        caso("VF13", "M3", "Top-k con k mayor que el número de conductores (límite)", "k = 50 con " + gc.listarTodos().size() + " conductores",
                "Devuelve los " + gc.listarTodos().size() + " conductores, ordenados", todos.size() + " conductores; " + (lics(todos).equals(lics(oraculo)) ? "orden correcto" : "orden incorrecto"),
                todos.size() == gc.listarTodos().size() && lics(todos).equals(lics(oraculo)));

        error("VF14", "M3", "Top-k con valor inválido", "k = 0", "Rechazo: k debe estar entre 1 y " + GestorRiesgo.K_MAXIMO,
                () -> gr.topRiesgo(0), "entre 1 y");

        int esperadoPos = indice(oraculo, "B-050607") + 1;
        int pos = gr.posicion("b-050607");
        caso("VF15", "M3", "Posición de un conductor en el ranking (AVL con tamaño de subárbol)", "b-050607 (en minúsculas)",
                "Posición " + esperadoPos + " de " + oraculo.size(), "Posición " + pos + " de " + gr.tamanio(), pos == esperadoPos);

        error("VF16", "M3", "Posición de una licencia inexistente", "B-999999", "Rechazo: no existe en el ranking",
                () -> gr.posicion("B-999999"), "No existe");

        List<Conductor> rojos = gr.porNivel(NivelRiesgo.ROJO);
        List<Conductor> rojosEsp = new ArrayList<>();
        for (Conductor c : oraculo) if (c.getPuntajeActual() < 40) rojosEsp.add(c);
        caso("VF17", "M3", "Filtro por semáforo rojo (consulta de rango en el AVL)", "Nivel ROJO (puntaje < 40)",
                rojosEsp.size() + " conductores: " + lics(rojosEsp), rojos.size() + " conductores: " + lics(rojos), lics(rojos).equals(lics(rojosEsp)));

        int posAntes = gr.posicion("B-050607");
        ge.registrarEvento("B-050607", 5, LocalDate.of(2026, 11, 14), "Av. 10 de Agosto", 4, "Prueba VF18", gc);
        int posDespues = gr.posicion("B-050607");
        int posEsperada = indice(oraculo(), "B-050607") + 1;
        ge.deshacerUltimoRegistro(gc);
        int posFinal = gr.posicion("B-050607");
        caso("VF18", "M3", "Reubicación automática al registrar y al deshacer (integración M2 → M3)",
                "B-050607 recibe Conducción bajo efectos, gravedad 4 (−40); luego se deshace",
                "Sube de la posición " + posAntes + " a la " + posEsperada + "; al deshacer vuelve a la " + posAntes,
                "Posición " + posAntes + " → " + posDespues + " → " + posFinal, posDespues == posEsperada && posFinal == posAntes && posDespues < posAntes);

        int[] cn = gr.conteoPorNivel();
        int suma = cn[0] + cn[1] + cn[2] + cn[3];
        caso("VF19", "M3", "Conteo por nivel del semáforo", "Todos los conductores",
                "Suma de niveles = " + gc.listarTodos().size(), "Rojo " + cn[0] + ", naranja " + cn[1] + ", amarillo " + cn[2] + ", verde " + cn[3] + " (suma " + suma + ")",
                suma == gc.listarTodos().size());

        // ------------------------------------------------------------- salida
        int ok = 0;
        System.out.println("REGISTRO DE VERIFICACIÓN FUNCIONAL · SECR Progreso 2 · " + LocalDate.now());
        System.out.println("=".repeat(100));
        for (Caso c : casos) {
            System.out.printf("%s [%s] %s%n   Entrada:   %s%n   Esperado:  %s%n   Obtenido:  %s%n   Cumple:    %s%n%n",
                    c.id(), c.modulo(), c.funcionalidad(), c.entrada(), c.esperado(), c.obtenido(), c.cumple() ? "Sí" : "No");
            if (c.cumple()) ok++;
        }
        System.out.println("Resultado: " + ok + " de " + casos.size() + " casos cumplen.");
        Path p = Path.of(args.length > 0 ? args[0] : "docs/verificacion/resultados_verificacion.csv");
        if (p.getParent() != null) Files.createDirectories(p.getParent());
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(p))) {
            w.println("caso;modulo;funcionalidad;entrada;esperado;obtenido;cumple");
            for (Caso c : casos) w.println(String.join(";", c.id(), c.modulo(), c.funcionalidad(), c.entrada(), c.esperado(), c.obtenido(), c.cumple() ? "Sí" : "No"));
        }
        if (ok < casos.size()) System.exit(1);
    }

    // ------------------------------------------------------------- utilidades

    private interface Accion { Object ejecutar() throws Exception; }

    private static void caso(String id, String m, String f, String in, String esp, String obt, boolean cumple) {
        casos.add(new Caso(id, m, f, in, esp, obt, cumple));
    }

    /** Ejecuta una acción que DEBE fallar con un mensaje que contenga {@code fragmento}. */
    private static void error(String id, String m, String f, String in, String esp, Accion a, String fragmento) {
        String msg = capturar(a);
        caso(id, m, f, in, esp, msg == null ? "Se aceptó (no hubo error)" : "Rechazado: «" + msg + "»",
                msg != null && msg.toLowerCase().contains(fragmento.toLowerCase()));
    }

    private static String capturar(Accion a) {
        try { a.ejecutar(); return null; } catch (Exception e) { return e.getMessage(); }
    }

    /** Orden esperado calculado por fuerza bruta (ordenamiento completo), independiente del AVL. */
    private static List<Conductor> oraculo() {
        List<Conductor> l = new ArrayList<>(gc.listarTodos());
        l.sort(Comparator.comparingDouble(Conductor::getPuntajeActual).thenComparing(c -> c.getLicencia().getNumero()));
        return l;
    }

    private static int indice(List<Conductor> l, String lic) {
        for (int i = 0; i < l.size(); i++) if (l.get(i).getLicencia().getNumero().equals(lic)) return i;
        return -1;
    }

    private static String lics(List<Conductor> l) {
        List<String> r = new ArrayList<>();
        for (Conductor c : l) r.add(c.getLicencia().getNumero());
        return String.join(", ", r);
    }

    private static String fechas(List<EventoConduccion> l) {
        List<String> r = new ArrayList<>();
        for (EventoConduccion e : l) r.add(String.format("%02d/%02d", e.getFechaHora().getDayOfMonth(), e.getFechaHora().getMonthValue()));
        return String.join(", ", r);
    }

    private static String d(LocalDate f) { return String.format("%02d/%02d/%d", f.getDayOfMonth(), f.getMonthValue(), f.getYear()); }
    private static String fmt(double x) { return String.format(java.util.Locale.US, "%.1f", x); }
}
