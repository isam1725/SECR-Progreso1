package negocio;

import static modelo.Main.*;
import modelo.*;
import java.time.LocalDate;
import estructuras.ArbolAVL;
import java.util.*;

public class GestorConductores {
    // Índices sobre árboles AVL (sin tablas hash): búsqueda, inserción y eliminación en O(log n).
    private final ArbolAVL<String, Conductor> conductores = new ArbolAVL<>();
    private final ArbolAVL<String, HistorialConduccion> historiales = new ArbolAVL<>();

    public boolean registrarConductor(Conductor c) throws SecrException {
        if (c == null) throw new ConductorNoEncontradoException("No se recibió información del conductor.");
        Validaciones.validarCedula(c.getCedula());
        Validaciones.validarNombre(c.getNombreCompleto());
        Validaciones.validarEdadMayorEdad(c.getFechaNacimiento());
        if (c.getLicencia() == null) throw new LicenciaNoValidaException("La licencia del conductor no puede estar vacía.");
        Validaciones.validarNumeroLicencia(c.getLicencia().getNumero());
        Validaciones.validarCategoria(c.getLicencia().getCategoria());
        Validaciones.validarFechaEmision(c.getLicencia().getFechaEmision(), c.getFechaNacimiento());

        String numLicencia = c.getLicencia().getNumero().toUpperCase();
        if (conductores.contiene(numLicencia)) throw new LicenciaDuplicadaException("Ya existe un conductor con la licencia " + numLicencia + ".");
        if (buscarPorCedula(c.getCedula()) != null) throw new SecrException("Ya existe un conductor con la cédula " + c.getCedula() + ".");

        conductores.insertar(numLicencia, c);
        historiales.insertar(numLicencia, c.getHistorialConduccion());
        System.out.println("✅ Conductor registrado exitosamente: " + c.getNombreCompleto() + " (" + c.getEdad() + " años)");
        return true;
    }

    public Conductor buscarPorCedula(String cedula) {
        for (Conductor c : conductores.valoresEnOrden()) if (c.getCedula().equals(cedula)) return c;
        return null;
    }

    public Conductor buscarPorLicencia(String licencia) {
        if (licencia == null) return null;
        return conductores.buscar(licencia.trim().toUpperCase());
    }

    public Conductor obtenerConductorObligatorio(String licencia) throws ConductorNoEncontradoException {
        Conductor c = buscarPorLicencia(licencia);
        if (c == null) throw new ConductorNoEncontradoException("No existe un conductor con la licencia " + licencia + ".");
        return c;
    }

    public List<Conductor> listarTodos() { return conductores.valoresEnOrden(); }

    public boolean actualizarConductor(String licencia, String nuevoNombre, LocalDate nuevaFechaNac,
                                       LocalDate nuevoVencimiento) throws SecrException {
        Conductor c = obtenerConductorObligatorio(licencia);
        if (nuevaFechaNac != null) {
            Validaciones.validarEdadMayorEdad(nuevaFechaNac);
            c.setFechaNacimiento(nuevaFechaNac);
        }
        if (nuevoNombre != null && !nuevoNombre.isBlank()) {
            Validaciones.validarNombre(nuevoNombre);
            c.setNombreCompleto(nuevoNombre.trim());
        }
        if (nuevoVencimiento != null) {
            if (!nuevoVencimiento.isAfter(c.getLicencia().getFechaEmision())) {
                throw new LicenciaNoValidaException("La fecha de vencimiento debe ser posterior a la emisión.");
            }
            c.getLicencia().setFechaVencimiento(nuevoVencimiento);
        }
        System.out.println("✅ Conductor actualizado correctamente.");
        return true;
    }

    public boolean eliminarConductor(String licencia) throws SecrException {
        Conductor c = obtenerConductorObligatorio(licencia);
        if (c.tieneHistorial()) {
            throw new SecrException("No se puede eliminar porque el conductor tiene historial de infracciones, pruebas o renovaciones.");
        }
        conductores.eliminar(c.getLicencia().getNumero().toUpperCase());
        historiales.eliminar(c.getLicencia().getNumero().toUpperCase());
        System.out.println("✅ Conductor eliminado correctamente: " + c.getNombreCompleto());
        return true;
    }

    public RenovacionLicencia renovarLicencia(String licencia) throws SecrException {
        Conductor c = obtenerConductorObligatorio(licencia);
        if (!"Activo".equalsIgnoreCase(c.getEstado())) throw new EstadoInvalidoException("No se puede renovar porque el conductor está inactivo.");
        if (c.getPuntajeActual() < 50) throw new PuntajeNoValidoException("No se puede renovar porque el puntaje es menor a 50 puntos.");
        if (!c.getLicencia().estaVencida()) {
            throw new LicenciaNoValidaException("La licencia todavía está vigente. Solo se renueva cuando la fecha de vencimiento ya pasó.");
        }
        LocalDate vencimientoAnterior = c.getLicencia().getFechaVencimiento();
        System.out.println("⚠ La licencia está vencida. Necesita renovar la licencia de forma inmediata.");
        c.getLicencia().renovarDesde(LocalDate.now());
        RenovacionLicencia renovacion = new RenovacionLicencia(c.getLicencia().getNumero(), vencimientoAnterior, c.getLicencia().getFechaVencimiento());
        c.agregarRenovacion(renovacion);
        System.out.println("✅ Licencia renovada correctamente.");
        System.out.println("Fecha anterior de vencimiento: " + vencimientoAnterior);
        System.out.println("Nueva fecha de vencimiento: " + c.getLicencia().getFechaVencimiento());
        return renovacion;
    }

    public void actualizarPuntaje(String licencia) throws SecrException {
        Conductor c = obtenerConductorObligatorio(licencia);
        c.setPuntajeActual(c.getHistorialConduccion().calcularPuntajeFinal());
    }

    public int aplicarBonificacionBuenComportamiento(String licencia) throws SecrException {
        Conductor c = obtenerConductorObligatorio(licencia);
        long anios = c.calcularAniosSinInfracciones();
        int puntos = (int) Math.min(100, anios * 5);
        c.getHistorialConduccion().setBonificacionBuenComportamiento(puntos);
        actualizarPuntaje(licencia);
        return puntos;
    }

    public void aplicarBonificacionATodos() throws SecrException {
        for (Conductor c : listarTodos()) aplicarBonificacionBuenComportamiento(c.getLicencia().getNumero());
    }

    public List<Conductor> obtenerAlertasCaducidad(int diasAnticipacion) {
        List<Conductor> alertas = new ArrayList<>();
        for (Conductor c : conductores.valoresEnOrden()) {
            Licencia lic = c.getLicencia();
            if (lic.estaVencida() || lic.estaCercaDeCaducar(diasAnticipacion)) alertas.add(c);
        }
        return alertas;
    }

    public String estadoPuntosConductor(String licencia) throws ConductorNoEncontradoException {
        Conductor c = obtenerConductorObligatorio(licencia);
        return "Conductor: " + c.getNombreCompleto() + "\nPuntaje actual: " + String.format("%.1f", c.getPuntajeActual()) + " pts";
    }

    public Prueba realizarPruebaRecuperacion(String licencia, Curso curso, List<Integer> respuestas) throws SecrException {
        List<Pregunta> preguntas = curso != null && !curso.getPreguntas().isEmpty() ? curso.getPreguntas() : BancoPreguntas.getPreguntasFijas();
        return realizarPruebaRecuperacion(licencia, curso, preguntas, respuestas);
    }

    public Prueba realizarPruebaRecuperacion(String licencia, Curso curso, List<Pregunta> preguntas,
                                             List<Integer> respuestas) throws SecrException {
        Conductor c = obtenerConductorObligatorio(licencia);
        if (c.getPuntajeActual() >= 100) throw new PuntajeNoValidoException("El conductor ya tiene el puntaje máximo de 100 puntos.");
        if (curso == null) throw new PruebaNoValidaException("Debe seleccionar un curso válido para recuperar puntos.");
        if (!curso.estaActivo()) throw new PruebaNoValidaException("El curso seleccionado está inactivo.");
        if (preguntas == null || preguntas.isEmpty()) throw new PruebaNoValidaException("No hay preguntas disponibles para la prueba.");
        if (respuestas == null || respuestas.size() != preguntas.size()) throw new PruebaNoValidaException("Debe responder todas las preguntas de la prueba.");

        double calificacion = evaluarPrueba(preguntas, respuestas);
        String idPrueba = "PRU-" + System.currentTimeMillis() % 10000;
        Prueba prueba = new Prueba(idPrueba, c, curso, "Recuperación de puntos por bajo puntaje");
        prueba.setCalificacion(calificacion);
        c.agregarPrueba(prueba);
        actualizarPuntaje(licencia);
        System.out.println("✅ Prueba realizada. Calificación: " + String.format("%.1f", calificacion) + "%");
        return prueba;
    }

    private double evaluarPrueba(List<Pregunta> preguntas, List<Integer> respuestas) {
        int correctas = 0;
        for (int i = 0; i < preguntas.size() && i < respuestas.size(); i++) if (preguntas.get(i).esCorrecta(respuestas.get(i))) correctas++;
        return (correctas * 100.0) / preguntas.size();
    }

    public String estadisticaInfraccionMasComun() {
        // Conteo por tipo con un árbol AVL (nombre → contador): O(I log T), I = infracciones, T = tipos.
        ArbolAVL<String, int[]> conteo = new ArbolAVL<>();
        int totalInfracciones = 0;
        for (Conductor c : conductores.valoresEnOrden()) {
            for (Infraccion inf : c.getInfracciones()) {
                conteo.obtenerOInsertar(inf.getTipo().getNombre(), () -> new int[1])[0]++;
                totalInfracciones++;
            }
        }
        if (totalInfracciones == 0) return "No hay infracciones registradas.";
        String[] masComun = {null};
        int[] max = {0};
        conteo.recorrerEnOrden((nombre, cont) -> {
            if (cont[0] > max[0]) { max[0] = cont[0]; masComun[0] = nombre; }
        });
        double porcentaje = (max[0] * 100.0) / totalInfracciones;
        return "Infracción más cometida: " + masComun[0] + "\nCantidad: " + max[0] + " veces (" + String.format("%.1f", porcentaje) + "% del total)";
    }
}
