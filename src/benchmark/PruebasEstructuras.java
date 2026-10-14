package benchmark;

import estructuras.MonticuloIndexado;
import estructuras.OrdenamientoMezcla;
import estructuras.Trie;
import estructuras.comparacion.*;
import modelo.Conductor;
import modelo.EventoConduccion;
import modelo.Infraccion;

import java.time.LocalDate;
import java.util.*;

/**
 * Pruebas automáticas sin dependencias externas.
 * Ejecutar: java -cp out benchmark.PruebasEstructuras
 * Verifica cada estructura y que las Alternativas A y B devuelvan exactamente los mismos resultados.
 */
public class PruebasEstructuras {

    private static int ok = 0, fallos = 0;

    private static void verificar(boolean condicion, String descripcion) {
        if (condicion) { ok++; System.out.println("  [OK]    " + descripcion); }
        else { fallos++; System.out.println("  [FALLA] " + descripcion); }
    }

    public static void main(String[] args) {
        System.out.println("== Trie ==");
        Trie t = new Trie();
        t.insertarNombre("Sofía Torres Vera", "B-000001");
        t.insertarNombre("Carlos Torreño Pinto", "B-000002");
        t.insertarNombre("Ana Suárez Mora", "C-000003");
        verificar(t.buscarPorPrefijo("torr", 10).equals(List.of("B-000002", "B-000001")), "prefijo 'torr' devuelve ambos en orden alfabético (torreño < torres)");
        verificar(t.buscarPorPrefijo("SUAR", 10).equals(List.of("C-000003")), "búsqueda insensible a mayúsculas y tildes");
        verificar(t.buscarPorPrefijo("xyz", 10).isEmpty(), "prefijo inexistente devuelve lista vacía");
        t.eliminarNombre("Sofía Torres Vera", "B-000001");
        verificar(t.buscarPorPrefijo("tor", 10).equals(List.of("B-000002")), "eliminar quita el valor y poda nodos");
        verificar(!t.contiene("sofia"), "la palabra eliminada ya no existe");

        System.out.println("== Montículo indexado ==");
        Random r = new Random(7);
        MonticuloIndexado h = new MonticuloIndexado();
        Map<String, Double> espejo = new HashMap<>();
        for (int i = 0; i < 2000; i++) {
            String k = "K" + i;
            double p = r.nextInt(101);
            h.insertar(k, p);
            espejo.put(k, p);
        }
        for (int i = 0; i < 3000; i++) {
            String k = "K" + r.nextInt(2000);
            if (!espejo.containsKey(k)) continue;
            if (r.nextInt(5) == 0) { h.eliminar(k); espejo.remove(k); }
            else { double p = r.nextInt(101); h.actualizar(k, p); espejo.put(k, p); }
        }
        List<String> esperado = new ArrayList<>(espejo.keySet());
        esperado.sort(Comparator.comparingDouble((String k) -> espejo.get(k)).thenComparing(k -> k));
        verificar(h.tamanio() == espejo.size(), "tamaño correcto tras inserciones, actualizaciones y eliminaciones");
        verificar(h.topK(25).equals(esperado.subList(0, 25)), "topK(25) coincide con el ordenamiento completo");
        verificar(h.tamanio() == espejo.size(), "topK no modifica el montículo");
        List<String> extraidos = new ArrayList<>();
        while (!h.estaVacio()) extraidos.add(h.extraerMinimo());
        verificar(extraidos.equals(esperado), "extraerMinimo sucesivo produce el orden total correcto");

        System.out.println("== Merge Sort ==");
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < 5000; i++) nums.add(r.nextInt(1000));
        List<Integer> ref = new ArrayList<>(nums);
        Collections.sort(ref);
        verificar(OrdenamientoMezcla.ordenar(nums, Comparator.naturalOrder()).equals(ref), "ordena igual que Collections.sort");

        System.out.println("== Equivalencia Alternativa A vs B (conductores) ==");
        List<Conductor> datosA = GeneradorDatos.conductores(3000, 99);
        List<Conductor> datosB = GeneradorDatos.conductores(3000, 99);
        IndiceConductores a = new IndiceConductoresA(), b = new IndiceConductoresB();
        datosA.forEach(a::registrar);
        datosB.forEach(b::registrar);
        boolean igualesBusqueda = true, igualesTop = true, igualesPrefijo = true;
        Random q = new Random(5);
        for (int i = 0; i < 500; i++) {
            Conductor c = datosA.get(q.nextInt(datosA.size()));
            String lic = c.getLicencia().getNumero();
            double nuevo = q.nextInt(101);
            a.actualizarPuntaje(lic, nuevo);
            b.actualizarPuntaje(lic, nuevo);
            igualesBusqueda &= b.buscarPorLicencia(lic).getCedula().equals(a.buscarPorLicencia(lic).getCedula());
            igualesTop &= licencias(a.topRiesgo(10)).equals(licencias(b.topRiesgo(10)));
            String pre = GeneradorDatos.prefijoAleatorio(q);
            igualesPrefijo &= new HashSet<>(licencias(a.buscarPorPrefijoNombre(pre, 100000)))
                    .equals(new HashSet<>(licencias(b.buscarPorPrefijoNombre(pre, 100000))));
        }
        verificar(igualesBusqueda, "búsqueda por licencia: mismos resultados");
        verificar(igualesTop, "top-10 de riesgo tras 500 actualizaciones: mismos resultados");
        verificar(igualesPrefijo, "búsqueda por prefijo de nombre: mismo conjunto de resultados");
        verificar(a.buscarPorLicencia("Z-999999") == null && b.buscarPorLicencia("Z-999999") == null, "licencia inexistente devuelve null");

        System.out.println("== Equivalencia Alternativa A vs B (eventos por rango) ==");
        List<Infraccion> evs = GeneradorDatos.eventos(5000, 3);
        IndiceEventos ea = new IndiceEventosA(), eb = new IndiceEventosB();
        evs.forEach(ea::agregar);
        evs.forEach(eb::agregar);
        boolean igualesRango = true;
        for (int i = 0; i < 300; i++) {
            LocalDate d = LocalDate.now().minusDays(q.nextInt(1800));
            LocalDate hasta = d.plusDays(q.nextInt(90));
            igualesRango &= ids(ea.consultarRango(d, hasta)).equals(ids(eb.consultarRango(d, hasta)));
        }
        verificar(igualesRango, "consultas por rango de fechas: mismos eventos y mismo orden");
        verificar(ea.consultarRango(LocalDate.now(), LocalDate.now().minusDays(1)).isEmpty(), "rango invertido devuelve vacío");

        System.out.println();
        System.out.println("Resultado: " + ok + " pruebas correctas, " + fallos + " fallidas.");
        if (fallos > 0) System.exit(1);
    }

    private static List<String> licencias(List<Conductor> l) {
        List<String> r = new ArrayList<>();
        for (Conductor c : l) r.add(c.getLicencia().getNumero());
        return r;
    }

    private static List<String> ids(List<EventoConduccion> l) {
        List<String> r = new ArrayList<>();
        for (EventoConduccion e : l) r.add(e.getIdEvento());
        return r;
    }
}
