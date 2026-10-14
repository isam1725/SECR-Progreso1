package negocio;

import static modelo.Main.*;
import modelo.*;
import estructuras.ArbolAVL;
import java.util.*;

public class GestorCursos {
    // Catálogo de cursos indexado por código en un árbol AVL (sin tablas hash).
    private final ArbolAVL<String, Curso> cursos = new ArbolAVL<>();
    private int contadorPregunta = 1;

    public GestorCursos() {
        cargarCursosBase();
    }

    private void cargarCursosBase() {
        Curso basico = new Curso("CUR-101", "Manejo Defensivo Básico",
                "Curso básico para reforzar normas de tránsito.", 20, 8, 70.0, "Activo");
        Curso avanzado = new Curso("CUR-102", "Recuperación Avanzada",
                "Curso avanzado para conductores con mayor pérdida de puntos.", 30, 12, 70.0, "Activo");

        List<Pregunta> preguntas = BancoPreguntas.getPreguntasFijas();
        for (Pregunta p : preguntas) {
            basico.agregarPregunta(new Pregunta(generarIdPregunta(), p.getTexto(), p.getOpciones(), p.getRespuestaCorrecta(), basico.getCodigo()));
            avanzado.agregarPregunta(new Pregunta(generarIdPregunta(), p.getTexto(), p.getOpciones(), p.getRespuestaCorrecta(), avanzado.getCodigo()));
        }
        cursos.insertar(basico.getCodigo(), basico);
        cursos.insertar(avanzado.getCodigo(), avanzado);
    }

    public String generarIdPregunta() {
        return "PREG-" + String.format("%03d", contadorPregunta++);
    }

    public String normalizarCodigoPregunta(String valor) throws PruebaNoValidaException {
        String codigo = valor == null ? "" : valor.trim().toUpperCase();
        if (codigo.isEmpty()) return generarIdPregunta();
        if (codigo.startsWith("PREG-")) codigo = codigo.substring(5);
        if (!codigo.matches("\\d{1,6}")) {
            throw new PruebaNoValidaException("Ingrese solo el número de la pregunta. Ejemplo: 001. El sistema agregará PREG-.");
        }
        return "PREG-" + String.format("%03d", Integer.parseInt(codigo));
    }

    public boolean existePregunta(String idPregunta) {
        if (idPregunta == null) return false;
        for (Curso curso : cursos.valoresEnOrden()) {
            for (Pregunta pregunta : curso.getPreguntas()) {
                if (pregunta.getId().equalsIgnoreCase(idPregunta.trim())) return true;
            }
        }
        return false;
    }

    public List<Curso> listarCursos() { return cursos.valoresEnOrden(); }

    public Curso buscarCurso(String codigo) { return cursos.buscar(codigo); }

    public Curso obtenerCursoObligatorio(String codigo) throws PruebaNoValidaException {
        Curso c = buscarCurso(codigo);
        if (c == null) throw new PruebaNoValidaException("No existe el curso " + codigo + ".");
        return c;
    }

    public void crearCurso(Curso curso) throws SecrException {
        if (curso == null) throw new PruebaNoValidaException("El curso no puede estar vacío.");
        if (curso.getCodigo() == null || curso.getCodigo().trim().isEmpty()) throw new PruebaNoValidaException("El código del curso no puede estar vacío.");
        if (curso.getNombre() == null || curso.getNombre().trim().isEmpty()) throw new PruebaNoValidaException("El nombre del curso no puede estar vacío.");
        Validaciones.validarPuntos(curso.getPuntosRecuperacion());
        if (curso.getDuracionHoras() <= 0) throw new PruebaNoValidaException("La duración del curso debe ser mayor a 0 horas.");
        if (curso.getPorcentajeMinimoAprobacion() < 0 || curso.getPorcentajeMinimoAprobacion() > 100) throw new PruebaNoValidaException("El porcentaje mínimo debe estar entre 0 y 100.");
        if (cursos.contiene(curso.getCodigo())) throw new PruebaNoValidaException("Ya existe un curso con ese código.");
        cursos.insertar(curso.getCodigo(), curso);
    }

    public void actualizarCurso(String codigo, String nombre, String descripcion, int puntos, int horas, double minimo, String estado) throws SecrException {
        Curso c = obtenerCursoObligatorio(codigo);
        if (nombre != null && !nombre.trim().isEmpty()) c.setNombre(nombre.trim());
        if (descripcion != null) c.setDescripcion(descripcion.trim());
        Validaciones.validarPuntos(puntos);
        c.setPuntosRecuperacion(puntos);
        if (horas <= 0) throw new PruebaNoValidaException("La duración debe ser mayor a 0 horas.");
        c.setDuracionHoras(horas);
        if (minimo < 0 || minimo > 100) throw new PruebaNoValidaException("El porcentaje mínimo debe estar entre 0 y 100.");
        c.setPorcentajeMinimoAprobacion(minimo);
        if (estado != null && (estado.equalsIgnoreCase("Activo") || estado.equalsIgnoreCase("Inactivo"))) c.setEstado(estado);
    }

    public void eliminarCurso(String codigo) throws SecrException {
        Curso c = obtenerCursoObligatorio(codigo);
        if (!c.getPreguntas().isEmpty()) {
            throw new PruebaNoValidaException("No se puede eliminar el curso porque tiene preguntas registradas. Primero elimine sus preguntas.");
        }
        cursos.eliminar(codigo);
    }

    public List<Pregunta> listarPreguntas(String codigoCurso) throws PruebaNoValidaException {
        return obtenerCursoObligatorio(codigoCurso).getPreguntas();
    }

    public Pregunta buscarPregunta(String codigoCurso, String idPregunta) throws PruebaNoValidaException {
        for (Pregunta p : listarPreguntas(codigoCurso)) {
            if (p.getId().equalsIgnoreCase(idPregunta)) return p;
        }
        throw new PruebaNoValidaException("No existe la pregunta " + idPregunta + " en el curso " + codigoCurso + ".");
    }

    public void crearPregunta(String codigoCurso, String texto, List<String> opciones, int respuestaCorrecta) throws SecrException {
        crearPregunta(codigoCurso, "", texto, opciones, respuestaCorrecta);
    }

    public void crearPregunta(String codigoCurso, String idPregunta, String texto, List<String> opciones, int respuestaCorrecta) throws SecrException {
        Curso c = obtenerCursoObligatorio(codigoCurso);
        String codigoPregunta = normalizarCodigoPregunta(idPregunta);
        Validaciones.validarCodigoPregunta(codigoPregunta);
        if (existePregunta(codigoPregunta)) {
            throw new PruebaNoValidaException("Ya existe una pregunta con el código " + codigoPregunta + ". Use otro número.");
        }
        if (texto == null || texto.trim().isEmpty()) throw new PruebaNoValidaException("El texto de la pregunta no puede estar vacío.");
        if (opciones == null || opciones.size() < 2) throw new PruebaNoValidaException("La pregunta debe tener mínimo 2 opciones.");
        if (respuestaCorrecta < 0 || respuestaCorrecta >= opciones.size()) throw new PruebaNoValidaException("La respuesta correcta no coincide con las opciones.");
        c.agregarPregunta(new Pregunta(codigoPregunta, texto.trim(), opciones, respuestaCorrecta, codigoCurso));
    }

    public void actualizarPregunta(String codigoCurso, String idPregunta, String texto, List<String> opciones, int respuestaCorrecta) throws SecrException {
        Pregunta p = buscarPregunta(codigoCurso, idPregunta);
        if (texto != null && !texto.trim().isEmpty()) p.setTexto(texto.trim());
        if (opciones != null && opciones.size() >= 2) p.setOpciones(opciones);
        if (respuestaCorrecta < 0 || respuestaCorrecta >= p.getOpciones().size()) throw new PruebaNoValidaException("La respuesta correcta no coincide con las opciones.");
        p.setRespuestaCorrecta(respuestaCorrecta);
    }

    public void eliminarPregunta(String codigoCurso, String idPregunta) throws PruebaNoValidaException {
        Curso c = obtenerCursoObligatorio(codigoCurso);
        Pregunta encontrada = null;
        for (Pregunta p : c.getPreguntas()) {
            if (p.getId().equalsIgnoreCase(idPregunta)) {
                encontrada = p;
                break;
            }
        }
        if (encontrada == null) throw new PruebaNoValidaException("No existe la pregunta " + idPregunta + ".");
        c.getPreguntas().remove(encontrada);
    }

    public String reporteMejorCurso(List<Conductor> conductores) {
        ArbolAVL<String, int[]> estadisticas = new ArbolAVL<>(); // código → [rendidas, aprobadas]
        for (Curso c : cursos.valoresEnOrden()) {
            estadisticas.insertar(c.getCodigo(), new int[]{0, 0});
        }
        for (Conductor conductor : conductores) {
            for (Prueba p : conductor.getHistorialPruebas()) {
                int[] datos = estadisticas.obtenerOInsertar(p.getCurso().getCodigo(), () -> new int[]{0, 0});
                datos[0]++;
                if (p.estaAprobada()) datos[1]++;
            }
        }
        String mejor = null;
        double mejorPorcentaje = -1;
        StringBuilder detalle = new StringBuilder("REPORTE DE CURSOS POR APROBACIÓN\n\n");
        for (String codigo : estadisticas.clavesEnOrden()) {
            Curso curso = buscarCurso(codigo);
            int rendidas = estadisticas.buscar(codigo)[0];
            int aprobadas = estadisticas.buscar(codigo)[1];
            double porcentaje = rendidas == 0 ? 0 : (aprobadas * 100.0 / rendidas);
            detalle.append(curso != null ? curso.getNombre() : codigo)
                    .append(" | Rendidas: ").append(rendidas)
                    .append(" | Aprobadas: ").append(aprobadas)
                    .append(" | % aprobación: ").append(String.format("%.1f", porcentaje)).append("%\n");
            if (rendidas > 0 && porcentaje > mejorPorcentaje) {
                mejorPorcentaje = porcentaje;
                mejor = curso != null ? curso.getNombre() : codigo;
            }
        }
        detalle.append("\nMejor curso: ").append(mejor == null ? "No hay pruebas rendidas" : mejor + " (" + String.format("%.1f", mejorPorcentaje) + "%)");
        return detalle.toString();
    }
}
