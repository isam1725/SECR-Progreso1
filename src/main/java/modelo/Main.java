package modelo;

import negocio.*;
import util.SistemaSECR;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    static SistemaSECR sistema = new SistemaSECR();
    static GestorConductores gestorC = sistema.getGestorConductores();
    static GestorEventos gestorE = sistema.getGestorEventos();
    static GestorCursos gestorCursos = sistema.getGestorCursos();
    static GestorReportes gestorReportes = sistema.getGestorReportes();

    /*
     * EXCEPCIONES DEL PROYECTO
     * La carpeta "excepciones" fue eliminada. Ahora las excepciones están
     * dentro del Main, como pidió la docente. Las demás clases las lanzan con
     * throw/throws y este Main las controla con try/catch.
     */
    public static class SecrException extends Exception {
        public SecrException(String mensaje) {
            super(mensaje);
        }
    }

    public static class ConductorNoEncontradoException extends SecrException {
        public ConductorNoEncontradoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class EdadNoValidaException extends SecrException {
        public EdadNoValidaException(String mensaje) {
            super(mensaje);
        }
    }

    public static class EstadoInvalidoException extends SecrException {
        public EstadoInvalidoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class EventoNoValidoException extends SecrException {
        public EventoNoValidoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class LicenciaDuplicadaException extends SecrException {
        public LicenciaDuplicadaException(String mensaje) {
            super(mensaje);
        }
    }

    public static class LicenciaNoValidaException extends SecrException {
        public LicenciaNoValidaException(String mensaje) {
            super(mensaje);
        }
    }

    public static class PruebaNoValidaException extends SecrException {
        public PruebaNoValidaException(String mensaje) {
            super(mensaje);
        }
    }

    public static class PuntajeNoValidoException extends SecrException {
        public PuntajeNoValidoException(String mensaje) {
            super(mensaje);
        }
    }

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println(" SISTEMA DE EVALUACIÓN DE CONDUCCIÓN (SECR) ");
        System.out.println("==============================================");
        System.out.println("El sistema inicia sin datos de prueba. Registre los conductores desde el módulo 1.");

        int opcion;
        do {
            try {
                mostrarMenuPrincipal();
                opcion = leerEntero("Seleccione una opción: ");

                switch (opcion) {
                    case 1 -> moduloConductores();
                    case 2 -> moduloEventos();
                    case 3 -> moduloReportesYAlertas();
                    case 4 -> moduloEmisionLicencias();
                    case 5 -> moduloRecuperacionPuntos();
                    case 0 -> System.out.println("\n¡Gracias por usar SECR! Hasta pronto.");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (ConductorNoEncontradoException e) {
                System.out.println("❌ Conductor no encontrado: " + e.getMessage());
                opcion = -1;
            } catch (EstadoInvalidoException e) {
                System.out.println("❌ Error de estado: " + e.getMessage());
                opcion = -1;
            } catch (PuntajeNoValidoException e) {
                System.out.println("❌ Error de puntaje: " + e.getMessage());
                opcion = -1;
            } catch (PruebaNoValidaException e) {
                System.out.println("❌ Error en la prueba: " + e.getMessage());
                opcion = -1;
            } catch (EventoNoValidoException e) {
                System.out.println("❌ Error en el evento: " + e.getMessage());
                opcion = -1;
            } catch (SecrException e) {
                System.out.println("❌ Error SECR: " + e.getMessage());
                opcion = -1;
            } catch (Exception e) {
                System.out.println("❌ Error inesperado: " + e.getMessage());
                opcion = -1;
            }
        } while (opcion != 0);
    }

    static void mostrarMenuPrincipal() {
        System.out.println("\n------------- MENÚ PRINCIPAL ---------------");
        System.out.println("1. Módulo de Conductores");
        System.out.println("2. Módulo de Eventos / Infracciones (CRUD)");
        System.out.println("3. Reportes y Alertas");
        System.out.println("4. Emisión / Renovación de Licencias");
        System.out.println("5. Recuperación de Puntos (Cursos + Pruebas)");
        System.out.println("0. Salir");
        System.out.println("--------------------------------------------");
    }

    // ==================== MÓDULO 1: CONDUCTORES ====================

    static void moduloConductores() throws SecrException {
        int op;
        do {
            System.out.println("\n--- MÓDULO CONDUCTORES ---");
            System.out.println("1. Registrar nuevo conductor");
            System.out.println("2. Consultar conductor");
            System.out.println("3. Actualizar conductor");
            System.out.println("4. Cambiar estado del conductor (A/I)");
            System.out.println("5. Listar todos los conductores");
            System.out.println("6. Eliminar conductor");
            System.out.println("0. Volver");
            op = leerEntero("Opción: ");

            switch (op) {
                case 1 -> registrarConductor();
                case 2 -> consultarConductor();
                case 3 -> actualizarConductor();
                case 4 -> cambiarEstadoConductor();
                case 5 -> listarConductores();
                case 6 -> eliminarConductor();
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (op != 0);
    }

    static void registrarConductor() throws SecrException {
        System.out.println("\n--- REGISTRAR NUEVO CONDUCTOR ---");
        System.out.print("Cédula: ");
        String cedula = sc.nextLine().trim();

        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine().trim();

        LocalDate fechaNac = leerFecha("Fecha de nacimiento (dd/MM/yyyy): ");

        System.out.print("Número de licencia (Ej: B-001 o solo 001): ");
        String numLicIngresada = sc.nextLine().trim();

        System.out.print("Categoría (B/C/E): ");
        String cat = sc.nextLine().trim();
        String numLic = Validaciones.normalizarNumeroLicencia(numLicIngresada, cat);

        LocalDate emision = leerFecha("Fecha de emisión (dd/MM/yyyy): ");

        Licencia lic = new Licencia(numLic, cat, emision);
        Conductor c = new Conductor(cedula, nombre, fechaNac, lic);

        gestorC.registrarConductor(c);
        System.out.println("✅ Conductor registrado correctamente.");
        System.out.println("Fecha de vencimiento calculada automáticamente: " + lic.getFechaVencimiento());
    }

    static void consultarConductor() throws ConductorNoEncontradoException {
        System.out.print("Número de licencia: ");
        String lic = sc.nextLine().trim();
        Conductor c = gestorC.obtenerConductorObligatorio(lic);
        System.out.println(c);
    }

    static void actualizarConductor() throws SecrException {
        System.out.println("\n--- ACTUALIZAR CONDUCTOR ---");
        System.out.print("Número de licencia del conductor: ");
        String lic = sc.nextLine().trim();

        Conductor c = gestorC.obtenerConductorObligatorio(lic);
        System.out.println("Datos actuales:");
        System.out.println(c);

        System.out.println("\n¿Qué desea actualizar?");
        System.out.println("1. Nombre completo");
        System.out.println("2. Fecha de nacimiento");
        System.out.println("3. Estado (Activo/Inactivo)");
        System.out.println("Nota: la renovación de licencia se realiza en el módulo 4.");
        int op = leerEntero("Opción: ");

        switch (op) {
            case 1 -> {
                System.out.print("Nuevo nombre completo: ");
                String nuevoNombre = sc.nextLine().trim();
                gestorC.actualizarConductor(lic, nuevoNombre, null, null);
            }
            case 2 -> {
                LocalDate nuevaFechaNac = leerFecha("Nueva fecha de nacimiento (dd/MM/yyyy): ");
                gestorC.actualizarConductor(lic, null, nuevaFechaNac, null);
                System.out.println("Nueva edad: " + c.getEdad() + " años");
            }
            case 3 -> {
                System.out.print("Nuevo estado (Activo / Inactivo): ");
                c.cambiarEstado(sc.nextLine().trim());
            }
            default -> System.out.println("Opción no válida.");
        }
    }

    static void listarConductores() {
        List<Conductor> lista = gestorC.listarTodos();
        System.out.println(lista.isEmpty() ? "No hay conductores." : sistema.mostrarColeccion(lista));
    }

    static void eliminarConductor() throws SecrException {
        System.out.print("Número de licencia del conductor a eliminar: ");
        String lic = sc.nextLine().trim();
        Conductor c = gestorC.obtenerConductorObligatorio(lic);
        System.out.println("Conductor encontrado: " + c.getNombreCompleto());
        int confirmar = leerEntero("¿Seguro que desea eliminarlo? 1. Sí / 2. No: ");
        if (confirmar == 1) {
            gestorC.eliminarConductor(lic);
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    static void cambiarEstadoConductor() throws SecrException {
        System.out.print("Número de licencia: ");
        String lic = sc.nextLine().trim();
        Conductor c = gestorC.obtenerConductorObligatorio(lic);
        System.out.print("Nuevo estado (Activo / Inactivo): ");
        c.cambiarEstado(sc.nextLine().trim());
    }

    // ==================== MÓDULO 2: EVENTOS ====================

    static void moduloEventos() throws SecrException {
        int op;
        do {
            System.out.println("\n--- CRUD EVENTOS / INFRACCIONES ---");
            System.out.println("1. Registrar nuevo evento");
            System.out.println("2. Ver infracciones de un conductor");
            System.out.println("3. Ver TODOS los eventos registrados");
            System.out.println("0. Volver");
            op = leerEntero("Opción: ");

            switch (op) {
                case 1 -> registrarEvento();
                case 2 -> verInfraccionesConductor();
                case 3 -> gestorE.mostrarTodosLosEventos(gestorC);
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (op != 0);
    }

    static void registrarEvento() throws SecrException {
        System.out.print("Licencia del conductor: ");
        String lic = sc.nextLine().trim();

        List<TipoEvento> tipos = gestorE.getTiposEvento();
        for (int i = 0; i < tipos.size(); i++) {
            System.out.println((i + 1) + ". " + tipos.get(i));
        }
        int idx = leerEntero("Tipo de evento (número): ") - 1;
        LocalDate fecha = leerFecha("Fecha (dd/MM/yyyy): ");

        System.out.print("Ubicación: ");
        String ubi = sc.nextLine().trim();

        int gravedad = leerEntero("Gravedad (1-5): ");
        System.out.print("Descripción: ");
        String desc = sc.nextLine().trim();

        gestorE.registrarEvento(lic, idx, fecha, ubi, gravedad, desc, gestorC);
    }

    static void verInfraccionesConductor() throws ConductorNoEncontradoException {
        System.out.print("Número de licencia: ");
        String lic = sc.nextLine().trim();
        Conductor c = gestorC.obtenerConductorObligatorio(lic);

        List<Infraccion> lista = c.getInfracciones();
        if (lista.isEmpty()) {
            System.out.println("Este conductor no tiene eventos registrados.");
            return;
        }

        System.out.println("\n--- INFRACCIONES DE " + c.getNombreCompleto() + " ---");
        for (Infraccion e : lista) {
            System.out.println(e);
            System.out.println(" Gravedad: " + e.clasificarGravedad() + " (" + e.getGravedad() + "/5)");
            System.out.println(" ───────────────────────────────");
        }
    }

    // ==================== MÓDULO 3: REPORTES Y ALERTAS ====================

    static void moduloReportesYAlertas() throws ConductorNoEncontradoException {
        int op;
        do {
            System.out.println("\n--- REPORTES Y ALERTAS ---");
            System.out.println("1. Reporte Individual");
            System.out.println("2. Reporte Estadístico General");
            System.out.println("3. Alertas de Caducidad de Licencia");
            System.out.println("4. Infracción más cometida");
            System.out.println("0. Volver");
            op = leerEntero("Opción: ");

            switch (op) {
                case 1 -> verReporteIndividual();
                case 2 -> verReporteEstadistico();
                case 3 -> alertasCaducidad();
                case 4 -> System.out.println(gestorC.estadisticaInfraccionMasComun());
                case 0 -> { }
                default -> System.out.println("Opción no válida.");
            }
        } while (op != 0);
    }

    static void verReporteIndividual() throws ConductorNoEncontradoException {
        System.out.print("Licencia: ");
        String lic = sc.nextLine().trim();
        Conductor c = gestorC.obtenerConductorObligatorio(lic);
        System.out.println(gestorReportes.generarReporteIndividual(c));
    }

    static void verReporteEstadistico() {
        System.out.println(gestorReportes.generarReporteGeneral(gestorC.listarTodos()));
    }

    static void alertasCaducidad() {
        List<Conductor> alertas = gestorC.obtenerAlertasCaducidad(30);
        if (alertas.isEmpty()) {
            System.out.println("No hay alertas de caducidad.");
        } else {
            alertas.forEach(c -> System.out.println(c.getNombreCompleto() + " - " + c.getLicencia()));
        }
    }

    // ==================== MÓDULO 4: EMISIÓN LICENCIAS ====================

    static void moduloEmisionLicencias() throws SecrException {
        System.out.print("Licencia del conductor: ");
        String lic = sc.nextLine().trim();
        Conductor c = gestorC.obtenerConductorObligatorio(lic);

        System.out.println("\n--- DATOS ACTUALES DE LA LICENCIA ---");
        System.out.println(c.getLicencia());
        System.out.println("Puntaje actual: " + String.format("%.1f", c.getPuntajeActual()) + " pts");
        System.out.println("Estado del conductor: " + c.getEstado());

        if (c.puedeRenovarLicencia()) {
            System.out.println("✅ Puede renovar la licencia.");
            gestorC.renovarLicencia(lic);
        } else {
            System.out.println("❌ NO puede renovar. Revise si la licencia aún está vigente, si tiene menos de 50 puntos o si está inactivo.");
        }
        Alerta.generarAlerta(c);
    }

    // ==================== MÓDULO 5: RECUPERACIÓN DE PUNTOS ====================

    static void moduloRecuperacionPuntos() throws SecrException {
        System.out.print("Licencia: ");
        String lic = sc.nextLine().trim();
        Conductor c = gestorC.obtenerConductorObligatorio(lic);

        if (c.getPuntajeActual() >= 100) {
            throw new PuntajeNoValidoException("Ya tiene puntaje máximo (100 pts).");
        }

        List<Curso> cursos = List.of(
                new Curso("CUR-101", "Manejo Defensivo Básico", 20, 8),
                new Curso("CUR-102", "Recuperación Avanzada", 30, 12)
        );

        System.out.println("Cursos disponibles:");
        for (int i = 0; i < cursos.size(); i++) {
            System.out.println((i + 1) + ". " + cursos.get(i));
        }

        int idx = leerEntero("Seleccione curso (número): ") - 1;
        if (idx < 0 || idx >= cursos.size()) {
            throw new PruebaNoValidaException("Curso inválido. Seleccione una opción existente.");
        }

        Curso curso = cursos.get(idx);
        List<Pregunta> preguntas = BancoPreguntas.getPreguntasFijas();
        List<Integer> respuestas = new ArrayList<>();

        System.out.println("\nLos cursos básico y avanzado usan las mismas preguntas.");
        System.out.println("La diferencia es que el básico recupera 20 puntos y el avanzado recupera 30 puntos.");
        System.out.println("\n--- PRUEBA TEÓRICA (" + preguntas.size() + " preguntas) ---");
        for (int i = 0; i < preguntas.size(); i++) {
            Pregunta p = preguntas.get(i);
            System.out.println("\n" + (i + 1) + ". " + p.getTexto());
            for (String opcion : p.getOpciones()) {
                System.out.println(" " + opcion);
            }

            int resp = leerEntero("Su respuesta (1-" + p.getOpciones().size() + "): ") - 1;
            if (resp < 0 || resp >= p.getOpciones().size()) {
                throw new PruebaNoValidaException("Respuesta fuera de rango en la pregunta " + (i + 1) + ".");
            }
            respuestas.add(resp);
        }

        Prueba prueba = gestorC.realizarPruebaRecuperacion(lic, curso, preguntas, respuestas);
        System.out.println(prueba);
        System.out.println("Nuevo puntaje: " + String.format("%.1f", c.getPuntajeActual()) + " pts");
    }

    // ==================== UTILIDADES ====================

    static int leerEntero(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }

    static LocalDate leerFecha(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return LocalDate.parse(sc.nextLine().trim(), fmt);
            } catch (DateTimeParseException e) {
                System.out.println("Formato incorrecto. Use: dd/MM/yyyy");
            }
        }
    }
}
