package benchmark;

import estructuras.*;
import estructuras.comparacion.*;
import modelo.Conductor;
import util.SistemaSECR;

import java.util.*;

/**
 * Pruebas automáticas sin dependencias externas (no requieren JUnit).
 * Ejecutar: java -cp out benchmark.PruebasEstructuras
 */
public class PruebasEstructuras {

    private static int ok = 0, fallos = 0;

    private static void verificar(boolean condicion, String descripcion) {
        if (condicion) { ok++; System.out.println("  [OK]    " + descripcion); }
        else { fallos++; System.out.println("  [FALLA] " + descripcion); }
    }

    public static void main(String[] args) {
        Random r = new Random(7);

        System.out.println("== Árbol AVL ==");
        ArbolAVL<Integer, String> avl = new ArbolAVL<>();
        List<Integer> ref = new ArrayList<>();            // referencia: lista ordenada
        boolean coincide = true;
        for (int i = 0; i < 20000; i++) {
            int k = r.nextInt(5000);
            int pos = Collections.binarySearch(ref, k);
            if (r.nextInt(3) == 0) {
                boolean el = avl.eliminar(k);
                coincide &= el == (pos >= 0);
                if (pos >= 0) ref.remove(pos);
            } else {
                avl.insertar(k, "v" + k);
                if (pos < 0) ref.add(-pos - 1, k);
            }
        }
        verificar(coincide, "20 000 inserciones/eliminaciones aleatorias coinciden con la referencia");
        verificar(avl.clavesEnOrden().equals(ref), "recorrido en orden = claves ordenadas");
        verificar(avl.esAVLValido(), "propiedad AVL y orden de claves se mantienen");
        verificar(avl.tamanio() == ref.size(), "tamaño correcto");
        List<Integer> rangoRef = new ArrayList<>();
        for (int k : ref) if (k >= 1000 && k <= 1200) rangoRef.add(k);
        List<Integer> rangoAvl = new ArrayList<>();
        for (String v : avl.rango(1000, 1200)) rangoAvl.add(Integer.parseInt(v.substring(1)));
        verificar(rangoAvl.equals(rangoRef), "rango(1000, 1200) devuelve exactamente las claves del intervalo");
        verificar(avl.primeros(5).size() == 5 && avl.primeros(5).get(0).equals("v" + ref.get(0)), "primeros(5) empieza por la menor clave");
        ArbolAVL<Integer, Integer> secuencial = new ArbolAVL<>();
        for (int i = 0; i < 100000; i++) secuencial.insertar(i, i);
        verificar(secuencial.altura() <= 18, "100 000 claves en orden creciente: altura " + secuencial.altura() + " (un ABB común tendría 100 000)");

        System.out.println("== Pila y Cola ==");
        Pila<Integer> pila = new Pila<>();
        Cola<Integer> cola = new Cola<>();
        for (int i = 1; i <= 5; i++) { pila.apilar(i); cola.encolar(i); }
        verificar(pila.desapilar() == 5 && pila.cima() == 4 && pila.tamanio() == 4, "pila: último en entrar, primero en salir");
        verificar(cola.desencolar() == 1 && cola.verFrente() == 2 && cola.comoLista().equals(List.of(2, 3, 4, 5)), "cola: primero en entrar, primero en salir");

        System.out.println("== Montículo binario (cola de prioridad) ==");
        MonticuloBinario<Integer> mon = new MonticuloBinario<>(Comparator.naturalOrder());
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < 5000; i++) { int x = r.nextInt(100000); mon.insertar(x); nums.add(x); }
        Collections.sort(nums);
        verificar(mon.esMonticuloValido(), "propiedad de montículo tras 5 000 inserciones");
        verificar(mon.menores(20, x -> true).equals(nums.subList(0, 20)) && mon.tamanio() == 5000, "menores(20) correcto y no destructivo");
        List<Integer> pares = new ArrayList<>();
        for (int x : nums) if (x % 2 == 0) pares.add(x);
        verificar(mon.menores(10, x -> x % 2 == 0).equals(pares.subList(0, 10)), "menores(10) con filtro");
        List<Integer> bajo = mon.hastaTope(x -> x <= 1000, x -> true);
        Collections.sort(bajo);
        List<Integer> bajoRef = new ArrayList<>();
        for (int x : nums) if (x <= 1000) bajoRef.add(x);
        verificar(bajo.equals(bajoRef), "hastaTope(≤ 1000) poda y devuelve todos los elementos");
        List<Integer> extraidos = new ArrayList<>();
        while (!mon.estaVacio()) extraidos.add(mon.extraerMinimo());
        verificar(extraidos.equals(nums), "extraerMinimo sucesivo ordena (heapsort)");

        System.out.println("== Grafo vial (BFS, Dijkstra, DFS) ==");
        Grafo g = new Grafo();
        g.conectar("Base", "A", 2); g.conectar("A", "B", 2); g.conectar("Base", "C", 1);
        g.conectar("C", "B", 5); g.conectar("B", "D", 1); g.conectar("X", "Y", 3);
        Grafo.Ruta ruta = g.rutaMasCorta("Base", "D");
        verificar(ruta.existe() && ruta.distancia() == 5 && ruta.camino().equals(List.of("Base", "A", "B", "D")), "Dijkstra: Base→A→B→D = 5 km");
        verificar(!g.rutaMasCorta("Base", "X").existe(), "Dijkstra: sin ruta entre componentes distintas");
        verificar(new SinOrden(g.zonasCercanas("Base", 1)).igual(List.of("A", "C")), "BFS a 1 salto: A y C");
        verificar(g.zonasCercanas("Base", 2).size() == 3, "BFS a 2 saltos: A, C y B");
        verificar(g.componentesConexas().size() == 2, "DFS con pila: 2 componentes conexas");

        System.out.println("== Alternativa A vs B (ranking de riesgo) ==");
        List<Conductor> da = GeneradorDatos.conductores(3000, 99), db = GeneradorDatos.conductores(3000, 99);
        RankingRiesgoAVL a = new RankingRiesgoAVL();
        RankingRiesgoMonticulo b = new RankingRiesgoMonticulo();
        da.forEach(a::registrar);
        db.forEach(b::registrar);
        boolean igualesTop = true, igualesRango = true, igualesBusq = true;
        Random q = new Random(5);
        for (int i = 0; i < 4000; i++) {
            String lic = da.get(q.nextInt(da.size())).getLicencia().getNumero();
            double p = q.nextInt(101);
            a.actualizarPuntaje(lic, p);
            b.actualizarPuntaje(lic, p);
            if (i % 10 == 0) {
                igualesTop &= licencias(a.topRiesgo(10)).equals(licencias(b.topRiesgo(10)));
                List<String> ra = licencias(a.enRangoPuntaje(0, 39.99)), rb = licencias(b.enRangoPuntaje(0, 39.99));
                Collections.sort(ra);
                Collections.sort(rb);
                igualesRango &= ra.equals(rb);
                igualesBusq &= a.buscar(lic).getCedula().equals(b.buscar(lic).getCedula());
            }
        }
        verificar(igualesTop, "top-10 idéntico en A y B durante 4 000 actualizaciones");
        verificar(igualesRango, "semáforo rojo (puntaje < 40): mismo conjunto en A y B");
        verificar(igualesBusq, "búsqueda por licencia: mismos resultados");
        verificar(b.getCompactaciones() > 0 && b.entradasEnMonticulo() <= 2 * b.tamanio() + 64, "B compacta su montículo (" + b.getCompactaciones() + " veces) y acota la memoria");
        List<Conductor> top = a.topRiesgo(50);
        boolean ordenado = true;
        for (int i = 1; i < top.size(); i++) ordenado &= top.get(i - 1).getPuntajeActual() <= top.get(i).getPuntajeActual();
        verificar(ordenado, "el ranking sale ordenado de menor a mayor puntaje");

        System.out.println("== Gestores del sistema (código heredado sin tablas hash) ==");
        SistemaSECR sistema = new SistemaSECR();
        sistema.cargarDatosPrueba();
        verificar(sistema.getGestorConductores().listarTodos().size() == 5, "carga de datos de prueba: 5 conductores");
        verificar(sistema.getGestorConductores().buscarPorLicencia("b-005678") != null, "búsqueda por licencia sobre ArbolAVL (insensible a mayúsculas)");
        verificar(sistema.getGestorConductores().estadisticaInfraccionMasComun().startsWith("Infracción más cometida: Exceso de velocidad"), "infracción más común con conteo en ArbolAVL");
        verificar(sistema.getGestorCursos().listarCursos().size() == 2, "catálogo de cursos en ArbolAVL");
        verificar(sistema.getGestorCursos().reporteMejorCurso(sistema.getGestorConductores().listarTodos()).contains("Mejor curso"), "reporte de mejor curso");

        System.out.println();
        System.out.println("Resultado: " + ok + " pruebas correctas, " + fallos + " fallidas.");
        if (fallos > 0) System.exit(1);
    }

    /** Comparación de listas sin importar el orden. */
    private record SinOrden(List<String> l) {
        boolean igual(List<String> otra) {
            List<String> x = new ArrayList<>(l), y = new ArrayList<>(otra);
            Collections.sort(x);
            Collections.sort(y);
            return x.equals(y);
        }
    }

    private static List<String> licencias(List<Conductor> l) {
        List<String> r = new ArrayList<>();
        for (Conductor c : l) r.add(c.getLicencia().getNumero());
        return r;
    }
}
