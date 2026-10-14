package benchmark;

import estructuras.comparacion.*;
import modelo.Conductor;
import modelo.Infraccion;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;

/**
 * Micro-benchmark que compara las Alternativas A y B con datos sintéticos.
 * Ejecutar: java -cp out benchmark.BenchmarkAlternativas [archivoCsv]
 * Cada medición se repite y se reporta la mediana en milisegundos.
 * Los valores absolutos dependen del equipo; lo importante es la tendencia al crecer n.
 */
public class BenchmarkAlternativas {

    private static final int REPETICIONES = 5;
    private static final int[] TAMANIOS = {1_000, 10_000, 100_000};
    private static final List<String> filas = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        String salida = args.length > 0 ? args[0] : "docs/benchmark/resultados.csv";
        System.out.println("Calentando la JVM...");
        for (int i = 0; i < 3; i++) ejecutarRonda(5_000, false);
        for (int n : TAMANIOS) ejecutarRonda(n, true);
        Path p = Path.of(salida);
        if (p.getParent() != null) Files.createDirectories(p.getParent());
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(p))) {
            w.println("n,operacion,alternativa,mediana_ms");
            filas.forEach(w::println);
        }
        System.out.println("\nResultados guardados en " + p.toAbsolutePath());
    }

    private static void ejecutarRonda(int n, boolean registrar) {
        if (registrar) System.out.printf("%n=== n = %,d ===%n", n);
        medir(n, "Registro de n conductores", registrar, IndiceConductoresA::new, IndiceConductoresB::new, (idx, datos) -> {
            datos.forEach(idx::registrar);
        });
        medir(n, "10.000 búsquedas por licencia", registrar, IndiceConductoresA::new, IndiceConductoresB::new, (idx, datos) -> {
            Random r = new Random(1);
            for (int i = 0; i < 10_000; i++) idx.buscarPorLicencia(datos.get(r.nextInt(datos.size())).getLicencia().getNumero());
        }, true);
        medir(n, "2.000 búsquedas por prefijo (10 resultados)", registrar, IndiceConductoresA::new, IndiceConductoresB::new, (idx, datos) -> {
            Random r = new Random(2);
            for (int i = 0; i < 2_000; i++) idx.buscarPorPrefijoNombre(GeneradorDatos.prefijoAleatorio(r), 10);
        }, true);
        medir(n, "500 x (actualizar puntaje + top-10 riesgo)", registrar, IndiceConductoresA::new, IndiceConductoresB::new, (idx, datos) -> {
            Random r = new Random(3);
            for (int i = 0; i < 500; i++) {
                idx.actualizarPuntaje(datos.get(r.nextInt(datos.size())).getLicencia().getNumero(), r.nextInt(101));
                idx.topRiesgo(10);
            }
        }, true);
        medirEventos(n, registrar);
        if (registrar) medirMemoria(n);
    }

    /** Memoria aproximada (MB) que ocupa cada índice de conductores, sin contar los objetos Conductor. */
    private static void medirMemoria(int n) {
        List<Conductor> d = GeneradorDatos.conductores(n, 42);
        double ma = memoria(() -> { IndiceConductores i = new IndiceConductoresA(); d.forEach(i::registrar); return i; });
        double mb = memoria(() -> { IndiceConductores i = new IndiceConductoresB(); d.forEach(i::registrar); return i; });
        System.out.printf("%-46s A: %10.2f MB | B: %10.2f MB | B/A = %6.1fx%n", "Memoria aproximada del índice", ma, mb, mb / Math.max(ma, 1e-6));
        filas.add(n + ",\"Memoria del índice (MB)\",A," + String.format(Locale.US, "%.3f", ma));
        filas.add(n + ",\"Memoria del índice (MB)\",B," + String.format(Locale.US, "%.3f", mb));
    }

    private static double memoria(Supplier<Object> constructor) {
        Runtime rt = Runtime.getRuntime();
        double[] v = new double[3];
        for (int k = 0; k < 3; k++) {
            limpiar();
            long antes = rt.totalMemory() - rt.freeMemory();
            Object indice = constructor.get();
            limpiar();
            long despues = rt.totalMemory() - rt.freeMemory();
            v[k] = (despues - antes) / (1024.0 * 1024.0);
            if (indice.hashCode() == 42) System.out.print(""); // mantiene viva la referencia
        }
        Arrays.sort(v);
        return v[1];
    }

    private static void limpiar() {
        for (int i = 0; i < 4; i++) {
            System.gc();
            try { Thread.sleep(40); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    private interface Operacion { void ejecutar(IndiceConductores idx, List<Conductor> datos); }

    private static void medir(int n, String op, boolean reg, Supplier<IndiceConductores> fa,
                              Supplier<IndiceConductores> fb, Operacion o) {
        medir(n, op, reg, fa, fb, o, false);
    }

    /** Si {@code precargar} es true, el registro no se incluye en el tiempo medido. */
    private static void medir(int n, String op, boolean reg, Supplier<IndiceConductores> fa,
                              Supplier<IndiceConductores> fb, Operacion o, boolean precargar) {
        double ta = mediana(() -> {
            List<Conductor> d = GeneradorDatos.conductores(n, 42);
            IndiceConductores idx = fa.get();
            if (precargar) d.forEach(idx::registrar);
            long t0 = System.nanoTime();
            o.ejecutar(idx, d);
            return (System.nanoTime() - t0) / 1e6;
        });
        double tb = mediana(() -> {
            List<Conductor> d = GeneradorDatos.conductores(n, 42);
            IndiceConductores idx = fb.get();
            if (precargar) d.forEach(idx::registrar);
            long t0 = System.nanoTime();
            o.ejecutar(idx, d);
            return (System.nanoTime() - t0) / 1e6;
        });
        reportar(n, op, ta, tb, reg);
    }

    private static void medirEventos(int n, boolean reg) {
        List<Infraccion> evs = GeneradorDatos.eventos(n, 11);
        double insA = mediana(() -> { IndiceEventos e = new IndiceEventosA(); long t0 = System.nanoTime(); evs.forEach(e::agregar); return (System.nanoTime() - t0) / 1e6; });
        double insB = mediana(() -> { IndiceEventos e = new IndiceEventosB(); long t0 = System.nanoTime(); evs.forEach(e::agregar); return (System.nanoTime() - t0) / 1e6; });
        reportar(n, "Registro de n eventos", insA, insB, reg);
        IndiceEventos a = new IndiceEventosA(), b = new IndiceEventosB();
        evs.forEach(a::agregar);
        evs.forEach(b::agregar);
        double rA = mediana(() -> consultas(a));
        double rB = mediana(() -> consultas(b));
        reportar(n, "1.000 consultas por rango de 30 días", rA, rB, reg);
    }

    private static double consultas(IndiceEventos idx) {
        Random r = new Random(4);
        long t0 = System.nanoTime();
        for (int i = 0; i < 1_000; i++) {
            LocalDate d = LocalDate.now().minusDays(r.nextInt(1800));
            idx.consultarRango(d, d.plusDays(30));
        }
        return (System.nanoTime() - t0) / 1e6;
    }

    private static double mediana(Supplier<Double> s) {
        double[] v = new double[REPETICIONES];
        for (int i = 0; i < REPETICIONES; i++) v[i] = s.get();
        Arrays.sort(v);
        return v[REPETICIONES / 2];
    }

    private static void reportar(int n, String op, double a, double b, boolean reg) {
        if (!reg) return;
        System.out.printf("%-46s A: %10.2f ms | B: %10.2f ms | A/B = %6.1fx%n", op, a, b, a / Math.max(b, 1e-6));
        filas.add(n + ",\"" + op + "\",A," + String.format(Locale.US, "%.3f", a));
        filas.add(n + ",\"" + op + "\",B," + String.format(Locale.US, "%.3f", b));
    }
}
