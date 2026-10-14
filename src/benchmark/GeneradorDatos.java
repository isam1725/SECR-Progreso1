package benchmark;

import modelo.Conductor;
import modelo.Infraccion;
import modelo.Licencia;
import modelo.TipoEvento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Genera datos sintéticos reproducibles (semilla fija) para pruebas y benchmark. */
public final class GeneradorDatos {

    private static final String[] NOMBRES = {"Sofía", "Mateo", "Valentina", "Martín", "Camila", "Alex",
            "Isabella", "Israel", "Daniela", "Amelio", "Lucía", "Sebastián", "Gabriela", "Andrés", "María",
            "José", "Paula", "Diego", "Emilia", "Carlos"};
    private static final String[] APELLIDOS = {"Torres", "Vásquez", "Palma", "Jurado", "Macas", "Zurita",
            "Mendoza", "Ramírez", "Suárez", "Gutiérrez", "Andrade", "Cevallos", "Chávez", "Herrera", "Paredes",
            "Salazar", "Vera", "Moreno", "Loor", "Calle", "Guerrero", "Espinoza", "Pazmiño", "Aguirre"};
    private static final String[] CATEGORIAS = {"B", "C", "E"};
    private static final String[] UBICACIONES = {"Av. 10 de Agosto", "Av. Simón Bolívar", "Panamericana Sur",
            "Av. Mariscal Sucre", "Av. Amazonas", "Av. Occidental", "Redondel Norte", "Av. Eloy Alfaro"};

    private GeneradorDatos() { }

    public static List<Conductor> conductores(int n, long semilla) {
        Random r = new Random(semilla);
        List<Integer> numeros = new ArrayList<>();
        for (int i = 0; i < Math.max(n, 1) * 2; i++) numeros.add(i);
        Collections.shuffle(numeros, r);
        List<Conductor> lista = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String cat = CATEGORIAS[r.nextInt(CATEGORIAS.length)];
            String numero = cat + "-" + String.format("%06d", numeros.get(i));
            String nombre = NOMBRES[r.nextInt(NOMBRES.length)] + " " + APELLIDOS[r.nextInt(APELLIDOS.length)]
                    + " " + APELLIDOS[r.nextInt(APELLIDOS.length)];
            LocalDate emision = LocalDate.now().minusDays(r.nextInt(1800));
            Licencia lic = new Licencia(numero, cat, emision);
            Conductor c = new Conductor(String.format("%010d", i), nombre, LocalDate.of(1970 + r.nextInt(30), 1 + r.nextInt(12), 1 + r.nextInt(28)), lic);
            try {
                c.setPuntajeActual(r.nextInt(101));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            lista.add(c);
        }
        return lista;
    }

    public static List<Infraccion> eventos(int m, long semilla) {
        Random r = new Random(semilla);
        TipoEvento[] tipos = {
                new TipoEvento("INF-001", "Exceso de velocidad", "", 5, "Activo"),
                new TipoEvento("INF-002", "Uso de celular", "", 4, "Activo"),
                new TipoEvento("INF-005", "No respetar señal", "", 6, "Activo")};
        List<Infraccion> lista = new ArrayList<>(m);
        LocalDate hoy = LocalDate.now();
        for (int i = 0; i < m; i++) {
            try {
                lista.add(new Infraccion(String.format("EVT-%06d", i), hoy.minusDays(r.nextInt(1800)),
                        UBICACIONES[r.nextInt(UBICACIONES.length)], 1 + r.nextInt(5), "",
                        tipos[r.nextInt(tipos.length)]));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return lista;
    }

    public static String prefijoAleatorio(Random r) {
        String base = r.nextBoolean() ? NOMBRES[r.nextInt(NOMBRES.length)] : APELLIDOS[r.nextInt(APELLIDOS.length)];
        return base.substring(0, Math.min(3, base.length()));
    }
}
