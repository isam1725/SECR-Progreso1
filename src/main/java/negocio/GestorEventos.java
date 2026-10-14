package negocio;

import static modelo.Main.*;
import modelo.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorEventos {
    private List<TipoEvento> tiposEvento = new ArrayList<>();
    private int contadorEvento = 1;
    private int contadorTipo = 1;

    public GestorEventos() { cargarTiposEvento(); }

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

    public boolean registrarEvento(String licencia, int indiceTipo, LocalDate fecha, String ubicacion,
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

        String idEvento = generarIdEvento();
        Infraccion infraccion = new Infraccion(idEvento, fecha, ubicacion, gravedad, descripcion, tipo);
        c.agregarInfraccion(infraccion);
        gestorC.actualizarPuntaje(licencia);
        if (c.getPuntajeActual() <= 0) {
            c.cambiarEstado("Inactivo");
            System.out.println("⚠ El conductor quedó inactivo porque su puntaje llegó a 0.");
        }

        System.out.println("✅ Evento registrado exitosamente.");
        System.out.println(" Deducción: -" + infraccion.calcularPuntosDeduccion() + " puntos");
        System.out.println(" Puntaje actual: " + String.format("%.1f", c.getPuntajeActual()) + " pts");
        return true;
    }

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
