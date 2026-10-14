package modelo;

import java.util.List;

public class Pregunta {
    private String id;
    private String texto;
    private List<String> opciones;
    private int respuestaCorrecta; // índice 0,1,2...
    private String codigoCurso;

    public Pregunta(String texto, List<String> opciones, int respuestaCorrecta) {
        this("PREG-" + System.currentTimeMillis() % 100000, texto, opciones, respuestaCorrecta, "GENERAL");
    }

    public Pregunta(String id, String texto, List<String> opciones, int respuestaCorrecta, String codigoCurso) {
        this.id = id;
        this.texto = texto;
        this.opciones = opciones;
        this.respuestaCorrecta = respuestaCorrecta;
        this.codigoCurso = codigoCurso;
    }

    public String getId() { return id; }
    public String getTexto() { return texto; }
    public List<String> getOpciones() { return opciones; }
    public int getRespuestaCorrecta() { return respuestaCorrecta; }
    public String getCodigoCurso() { return codigoCurso; }

    public void setTexto(String texto) { this.texto = texto; }
    public void setOpciones(List<String> opciones) { this.opciones = opciones; }
    public void setRespuestaCorrecta(int respuestaCorrecta) { this.respuestaCorrecta = respuestaCorrecta; }
    public void setCodigoCurso(String codigoCurso) { this.codigoCurso = codigoCurso; }

    public boolean esCorrecta(int respuesta) {
        return respuesta == respuestaCorrecta;
    }

    @Override
    public String toString() {
        return id + " | Curso: " + codigoCurso + " | " + texto;
    }
}
