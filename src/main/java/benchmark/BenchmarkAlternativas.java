package benchmark;

import estructuras.comparacion.*;
import modelo.Conductor;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

/**
 * Micro-benchmark: Alternativa A (árboles AVL) vs Alternativa B (cola de prioridad perezosa)
 * para el componente de priorización de riesgo (M3).
 * Ejecutar: java -Xmx3g -cp out benchmark.BenchmarkAlternativas [archivoCsv]
 * Cada medición se repite y se informa la mediana en milisegundos.
 */
public class BenchmarkAlternativas {

    private static final int REPETICIONES = 5;
    private static final int[] TAMANIOS = {1_000, 10_000, 100_000};
    private static final List<String> filas = new ArrayList<>();

    private interface Operacion { void ejecutar(RankingRiesgo r, List<Conductor> datos); }

    public static void main(String[] args) throws IOException {
        String salida = args.length > 0 ? args[0] : "docs/benchmark/resultados.csv";
        System.out.println("Calentando la JVM...");
        for (int i = 0; i < 3; i++) ronda(5_000, false);
        for (int n : TAMANIOS) ronda(n, true);
        Path p = Path.of(salida);
        if (p.getParent() != null) Files.createDirectories(p.getParent());
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(p))) {
            w.println("n,operacion,alternativa,valor");
            filas.forEach(w::println);
        }
        System.out.println("\nResultados guardados en " + p.toAbsolutePath());
    }

    private static void ronda(int n, boolean reg) {
        if (reg) System.out.printf("%n=== n = %,d ===%n", n);
        medir(n, "Registro de n conductores", reg, false, (r, d) -> d.forEach(r::registrar));
        medir(n, "500 x (actualizar puntaje + top-10)", reg, true, (r, d) -> {
            Random q = new Random(3);
            for (int i = 0; i < 500; i++) {
                r.actualizarPuntaje(d.get(q.nextInt(d.size())).getLicencia().getNumero(), q.nextInt(101));
                r.topRiesgo(10);
            }
        });
        medir(n, "20 000 actualizaciones de puntaje", reg, true, (r, d) -> {
            Random q = new Random(4);
            for (int i = 0; i < 20_000; i++) r.actualizarPuntaje(d.get(q.nextInt(d.size())).getLicencia().getNumero(), q.nextInt(101));
        });
        medir(n, "1 000 consultas top-10 (sin cambios)", reg, true, (r, d) -> {
            for (int i = 0; i < 1_000; i++) r.topRiesgo(10);
        });
        medir(n, "50 consultas semáforo rojo (< 40)", reg, true, (r, d) -> {
            Random q = new Random(6);
            for (int i = 0; i < 5 * d.size() / 10; i++) r.actualizarPuntaje(d.get(q.nextInt(d.size())).getLicencia().getNumero(), q.nextInt(101));
        }, (r, d) -> {
            for (int i = 0; i < 50; i++) r.enRangoPuntaje(0, 39.99);
        });
        medir(n, "10 000 búsquedas por licencia", reg, true, (r, d) -> {
            Random q = new Random(5);
            for (int i = 0; i < 10_000; i++) r.buscar(d.get(q.nextInt(d.size())).getLicencia().getNumero());
        });
        if (reg) memoria(n);
    }

    private static void medir(int n, String op, boolean reg, boolean precargar, Operacion o) {
        medir(n, op, reg, precargar, null, o);
    }

    /** {@code preparar} (si existe) se ejecuta antes de medir y no cuenta en el tiempo. */
    private static void medir(int n, String op, boolean reg, boolean precargar, Operacion preparar, Operacion o) {
        double ta = mediana(() -> correr(n, new RankingRiesgoAVL(), precargar, preparar, o));
        double tb = mediana(() -> correr(n, new RankingRiesgoMonticulo(), precargar, preparar, o));
        if (!reg) return;
        System.out.printf("%-42s A: %10.2f ms | B: %10.2f ms | A/B = %5.2f%n", op, ta, tb, ta / Math.max(tb, 1e-6));
        filas.add(n + ",\"" + op + "\",A," + String.format(Locale.US, "%.3f", ta));
        filas.add(n + ",\"" + op + "\",B," + String.format(Locale.US, "%.3f", tb));
    }

    private static double correr(int n, RankingRiesgo r, boolean precargar, Operacion preparar, Operacion o) {
        List<Conductor> d = GeneradorDatos.conductores(n, 42);
        if (precargar) d.forEach(r::registrar);
        if (preparar != null) preparar.ejecutar(r, d);
        long t0 = System.nanoTime();
        o.ejecutar(r, d);
        return (System.nanoTime() - t0) / 1e6;
    }

    /** Memoria (MB) de cada estructura tras registrar n conductores y aplicar 2n actualizaciones. */
    private static void memoria(int n) {
        double ma = memoria(n, RankingRiesgoAVL::new), mb = memoria(n, RankingRiesgoMonticulo::new);
        System.out.printf("%-42s A: %10.2f MB | B: %10.2f MB%n", "Memoria tras n registros y 2n cambios", ma, mb);
        filas.add(n + ",\"Memoria tras n registros y 2n cambios (MB)\",A," + String.format(Locale.US, "%.3f", ma));
        filas.add(n + ",\"Memoria tras n registros y 2n cambios (MB)\",B," + String.format(Locale.US, "%.3f", mb));
    }

    private static double memoria(int n, Supplier<RankingRiesgo> f) {
        Runtime rt = Runtime.getRuntime();
        double[] v = new double[3];
        for (int k = 0; k < 3; k++) {
            List<Conductor> d = GeneradorDatos.conductores(n, 42);
            limpiar();
            long antes = rt.totalMemory() - rt.freeMemory();
            RankingRiesgo r = f.get();
            d.forEach(r::registrar);
            Random q = new Random(8);
            for (int i = 0; i < 2 * n; i++) r.actualizarPuntaje(d.get(q.nextInt(n)).getLicencia().getNumero(), q.nextInt(101));
            limpiar();
            long despues = rt.totalMemory() - rt.freeMemory();
            v[k] = (despues - antes) / (1024.0 * 1024.0);
            if (r.tamanio() < 0 || d.isEmpty()) System.out.print("");
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

    private static double mediana(Supplier<Double> s) {
        double[] v = new double[REPETICIONES];
        for (int i = 0; i < REPETICIONES; i++) v[i] = s.get();
        Arrays.sort(v);
        return v[REPETICIONES / 2];
    }
}
