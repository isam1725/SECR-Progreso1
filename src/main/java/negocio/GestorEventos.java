package negocio;

import static modelo.Main.*;
import modelo.*;
import estructuras.ArbolAVL;
import estructuras.IndiceTemporalEventos;
import estructuras.ListaDoble;
import estructuras.OrdenamientoMezcla;
import estructuras.Pila;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * MÓDULO M2 — Registro y análisis de eventos e infracciones.
 * <ul>
 *   <li>{@link IndiceTemporalEventos} (árbol AVL por fecha): consultas por rango de fechas en O(log d + k).</li>
 *   <li>{@link Pila} de operaciones: deshacer el último registro (LIFO) en O(1) + O(log n).</li>
 *   <li>{@link ListaDoble}: historial cronológico de un conductor, navegable hacia adelante y atrás.</li>
 *   <li>{@link OrdenamientoMezcla} (Merge Sort recursivo y estable): ordenar resultados por varios criterios.</li>
 * </ul>
 */
public class GestorEventos {

    /** Registro de una operación para poder revertirla (elemento de la pila de deshacer). */
    public record OperacionRegistro(Infraccion infraccion, Conductor conductor, String estadoAnterior,
                                    double puntajeAnterior) { }

    /** Criterios de ordenamiento disponibles para los listados de eventos. */
    public enum CriterioOrden {
        FECHA("Fecha (más reciente primero)"),
        GRAVEDAD("Gravedad (mayor primero)"),
        DEDUCCION("Puntos descontados (mayor primero)");
        private final String etiqueta;
        CriterioOrden(String e) { etiqueta = e; }
        @Override public String toString() { return etiqueta; }
    }

    private List<TipoEvento> tiposEvento = new ArrayList<>();
    private int contadorEvento = 1;
    private int contadorTipo = 1;
    private final IndiceTemporalEventos indiceFechas = new IndiceTemporalEventos();
    private final Pila<OperacionRegistro> pilaDeshacer = new Pila<>();
    private GestorRiesgo gestorRiesgo;      // M3: se notifica cada cambio de puntaje

    public GestorEventos() { cargarTiposEvento(); }

    public void setGestorRiesgo(GestorRiesgo gestorRiesgo) { this.gestorRiesgo = gestorRiesgo; }

    private void cargarTiposEvento() {
        crearTipoEventoBase("INF-001", "Exceso de velocidad", "Conducir sobre el límite permitido", 5);
        crearTipoEventoBase("INF-002", "Uso de celular", "Uso del teléfono mientras conduce", 4);
        crearTipoEventoBase("INF-003", "Frenada brusca", "Frenado repentino sin causa justificada", 3);
        crearTipoEventoBase("INF-004", "Invasión de carril", "Cambio de carril sin señalización", 3);
        crearTipoEventoBase("INF-005", "No respetar señal", "Pasar semáforo en rojo u otras señales", 6);
        crearTipoEventoBase("INF-006", "Conducción bajo efectos", "Alcohol u otras sustancias", 10);
    }

    private void crearTipoEventoBase(String codigo, String nombre, String descripcion, int puntos) {
        tiposEvento.add(new TipoEvento(codigo, nombre, descripcion, puntos, "Activo"));
        contadorTipo++;
    }

    public List<TipoEvento> getTiposEvento() { return tiposEvento; }

    public List<TipoEvento> getTiposEventoActivos() {
        List<TipoEvento> activos = new ArrayList<>();
        for (TipoEvento t : tiposEvento) if (t.estaActivo()) activos.add(t);
        return activos;
    }

    public TipoEvento buscarTipo(int indice) throws EventoNoValidoException {
        List<TipoEvento> activos = getTiposEventoActivos();
        if (indice >= 0 && indice < activos.size()) return activos.get(indice);
        throw new EventoNoValidoException("Tipo de evento no válido. Seleccione un número de la lista.");
    }

    public TipoEvento buscarTipoPorCodigo(String codigo) {
        for (TipoEvento t : tiposEvento) if (t.getCodigo().equalsIgnoreCase(codigo)) return t;
        return null;
    }

    public void crearTipoEvento(String codigo, String nombre, String descripcion, int puntosBase) throws SecrException {
        if (codigo == null || codigo.trim().isEmpty()) codigo = "INF-" + String.format("%03d", contadorTipo++);
        if (buscarTipoPorCodigo(codigo) != null) throw new EventoNoValidoException("Ya existe un tipo de evento con ese código.");
        if (nombre == null || nombre.trim().isEmpty()) throw new EventoNoValidoException("El nombre del tipo de evento no puede estar vacío.");
        Validaciones.validarPuntos(puntosBase);
        tiposEvento.add(new TipoEvento(codigo.toUpperCase(), nombre.trim(), descripcion == null ? "" : descripcion.trim(), puntosBase, "Activo"));
    }

    public void actualizarTipoEvento(String codigo, String nombre, String descripcion, int puntosBase, String estado) throws SecrException {
        TipoEvento t = buscarTipoPorCodigo(codigo);
        if (t == null) throw new EventoNoValidoException("No existe el tipo de evento " + codigo + ".");
        if (nombre != null && !nombre.trim().isEmpty()) t.setNombre(nombre.trim());
        if (descripcion != null) t.setDescripcion(descripcion.trim());
        Validaciones.validarPuntos(puntosBase);
        t.setPuntosBase(puntosBase);
        if (estado != null && (estado.equalsIgnoreCase("Activo") || estado.equalsIgnoreCase("Inactivo"))) t.setEstado(estado);
    }

    public void eliminarTipoEvento(String codigo, GestorConductores gestorC) throws SecrException {
        TipoEvento t = buscarTipoPorCodigo(codigo);
        if (t == null) throw new EventoNoValidoException("No existe el tipo de evento " + codigo + ".");
        for (Conductor c : gestorC.listarTodos()) {
            for (Infraccion inf : c.getInfracciones()) {
                if (inf.getTipo().getCodigo().equalsIgnoreCase(codigo)) {
                    throw new EventoNoValidoException("No se puede eliminar porque ya fue usado en una infracción. Se recomienda marcarlo Inactivo.");
                }
            }
        }
        tiposEvento.remove(t);
    }

    /**
     * Registra una infracción: valida, calcula la deducción, actualiza el puntaje, la indexa por fecha,
     * apila la operación para poder deshacerla y avisa a M3 para reubicar al conductor en el ranking.
     */
    public Infraccion registrarEvento(String licencia, int indiceTipo, LocalDate fecha, String ubicacion,
                                      int gravedad, String descripcion, GestorConductores gestorC) throws SecrException {
        Conductor c = gestorC.obtenerConductorObligatorio(licencia);
        if (!"Activo".equalsIgnoreCase(c.getEstado())) {
            throw new EstadoInvalidoException("No se pueden registrar eventos porque el conductor está inactivo.");
        }
        if (c.getPuntajeActual() <= 0) {
            throw new PuntajeNoValidoException("No se pueden registrar nuevos eventos porque el conductor ya no tiene puntos disponibles.");
        }
        TipoEvento tipo = buscarTipo(indiceTipo);
        Validaciones.validarFechaEvento(fecha);
        Validaciones.validarUbicacion(ubicacion);
        Validaciones.validarPuntos(tipo.getPuntosBase());
        if (gravedad < 1 || gravedad > 5) throw new EventoNoValidoException("La gravedad debe estar entre 1 y 5.");

        String estadoAnterior = c.getEstado();
        double puntajeAnterior = c.getPuntajeActual();
        Infraccion infraccion = new Infraccion(generarIdEvento(), fecha, ubicacion, gravedad, descripcion, tipo);
        c.agregarInfraccion(infraccion);
        gestorC.actualizarPuntaje(licencia);
        if (c.getPuntajeActual() <= 0) c.cambiarEstado("Inactivo");

        indiceFechas.agregar(infraccion);                                         // AVL por fecha: O(log d)
        pilaDeshacer.apilar(new OperacionRegistro(infraccion, c, estadoAnterior, puntajeAnterior)); // O(1)
        if (gestorRiesgo != null) gestorRiesgo.reubicar(c);                      // M3: O(log n)
        return infraccion;
    }

    /**
     * Deshace el último registro (pila LIFO): quita la infracción del conductor y del índice,
     * restaura su estado y puntaje y lo reubica en el ranking de M3.
     */
    public OperacionRegistro deshacerUltimoRegistro(GestorConductores gestorC) throws SecrException {
        if (pilaDeshacer.estaVacia()) throw new SecrException("No hay registros para deshacer en esta sesión.");
        OperacionRegistro op = pilaDeshacer.desapilar();
        Conductor c = op.conductor();
        c.removerInfraccion(op.infraccion());
        indiceFechas.eliminar(op.infraccion());
        gestorC.actualizarPuntaje(c.getLicencia().getNumero());
        if (!c.getEstado().equalsIgnoreCase(op.estadoAnterior())) c.cambiarEstado(op.estadoAnterior());
        if (gestorRiesgo != null) gestorRiesgo.reubicar(c);
        return op;
    }

    public int operacionesParaDeshacer() { return pilaDeshacer.tamanio(); }
    public OperacionRegistro verUltimaOperacion() { return pilaDeshacer.estaVacia() ? null : pilaDeshacer.cima(); }

    /** Vacía la pila (p. ej. después de cargar los datos de prueba, que no se deben poder deshacer). */
    public void limpiarHistorialDeshacer() { while (!pilaDeshacer.estaVacia()) pilaDeshacer.desapilar(); }

    /**
     * Eventos entre dos fechas (inclusive), opcionalmente de un solo conductor.
     * Consulta de rango sobre el AVL por fecha: O(log d + k).
     */
    public List<EventoConduccion> consultarPorRango(LocalDate desde, LocalDate hasta, String licencia,
                                                    GestorConductores gestorC) throws SecrException {
        if (desde == null || hasta == null) throw new EventoNoValidoException("Ingrese ambas fechas del rango.");
        if (desde.isAfter(hasta)) throw new EventoNoValidoException("La fecha inicial no puede ser posterior a la fecha final.");
        List<EventoConduccion> enRango = indiceFechas.consultarRango(desde, hasta);
        if (licencia == null || licencia.isBlank()) return enRango;
        Conductor c = gestorC.obtenerConductorObligatorio(licencia);
        List<EventoConduccion> res = new ArrayList<>();
        for (EventoConduccion e : enRango) if (c.getInfracciones().contains(e)) res.add(e);
        return res;
    }

    /** Todos los eventos en orden cronológico (recorrido en orden del AVL): O(n). */
    public List<EventoConduccion> listarCronologico() {
        return indiceFechas.consultarRango(LocalDate.MIN, LocalDate.MAX);
    }

    /** Ordena una lista de eventos con Merge Sort (recursivo y estable): O(m log m). */
    public List<EventoConduccion> ordenar(List<EventoConduccion> eventos, CriterioOrden criterio) {
        Comparator<EventoConduccion> cmp = switch (criterio) {
            case FECHA -> Comparator.comparing(EventoConduccion::getFechaHora).reversed();
            case GRAVEDAD -> Comparator.comparingInt(EventoConduccion::getGravedad).reversed()
                    .thenComparing(EventoConduccion::getFechaHora, Comparator.reverseOrder());
            case DEDUCCION -> Comparator.comparingInt(EventoConduccion::calcularPuntosDeduccion).reversed()
                    .thenComparing(EventoConduccion::getFechaHora, Comparator.reverseOrder());
        };
        return OrdenamientoMezcla.ordenar(eventos, cmp);
    }

    /** Historial de un conductor en una lista doblemente enlazada ordenada por fecha. O(m²) en el peor caso, m pequeño. */
    public ListaDoble<Infraccion> historialNavegable(String licencia, GestorConductores gestorC) throws SecrException {
        Conductor c = gestorC.obtenerConductorObligatorio(licencia);
        ListaDoble<Infraccion> lista = new ListaDoble<>();
        for (Infraccion inf : c.getInfracciones()) lista.insertarOrdenado(inf, Comparator.comparing(Infraccion::getFechaHora));
        return lista;
    }

    /** Tipo de infracción más frecuente en una lista de eventos: conteo en un árbol AVL (tipo → contador). */
    public String infraccionMasFrecuente(List<EventoConduccion> eventos) {
        if (eventos.isEmpty()) return "—";
        ArbolAVL<String, int[]> conteo = new ArbolAVL<>();
        for (EventoConduccion e : eventos) conteo.obtenerOInsertar(e.getTipo().getNombre(), () -> new int[1])[0]++;
        String mejor = null;
        int max = 0;
        for (String nombre : conteo.clavesEnOrden()) {
            int n = conteo.buscar(nombre)[0];
            if (n > max) { max = n; mejor = nombre; }
        }
        return mejor + " (" + max + ")";
    }

    public int totalEventos() { return indiceFechas.getTotal(); }

    public String generarIdEvento() { return "EVT-" + String.format("%03d", contadorEvento++); }

    public List<Infraccion> obtenerInfraccionesConductor(String licencia, GestorConductores gestorC)
            throws ConductorNoEncontradoException {
        return gestorC.obtenerConductorObligatorio(licencia).getInfracciones();
    }

    public void mostrarTodosLosEventos(GestorConductores gestorC) {
        System.out.println("\n=== LISTADO GENERAL DE TODOS LOS EVENTOS ===");
        boolean hayEventos = false;
        for (Conductor c : gestorC.listarTodos()) {
            List<Infraccion> eventos = c.getInfracciones();
            if (!eventos.isEmpty()) {
                hayEventos = true;
                System.out.println("\n Conductor: " + c.getNombreCompleto() + " (" + c.getLicencia().getNumero() + ")");
                for (Infraccion e : eventos) {
                    System.out.println(" " + e);
                    System.out.println(" Gravedad: " + e.clasificarGravedad() + " (" + e.getGravedad() + "/5)");
                    System.out.println(" ───────────────────────────────");
                }
            }
        }
        if (!hayEventos) System.out.println("No hay eventos registrados todavía.");
    }

    public List<Conductor> obtenerConductoresConEventos(GestorConductores gestorC) {
        List<Conductor> conEventos = new ArrayList<>();
        for (Conductor c : gestorC.listarTodos()) if (!c.getInfracciones().isEmpty()) conEventos.add(c);
        return conEventos;
    }
}
