package modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BancoPreguntas {
    private static final List<Pregunta> banco = new ArrayList<>();

    static {
        banco.add(new Pregunta("¿Cuál es la velocidad máxima en zona urbana residencial?",
                Arrays.asList("A) 50 km/h", "B) 30 km/h", "C) 60 km/h", "D) 40 km/h"), 1));
        banco.add(new Pregunta("¿Está permitido el uso del celular mientras se conduce?",
                Arrays.asList("A) Sí, en manos libres", "B) No está permitido", "C) Solo emergencias"), 1));
        banco.add(new Pregunta("¿Qué significa una luz roja en un semáforo?",
                Arrays.asList("A) Detenerse", "B) Precaución", "C) Avanzar"), 0));
        banco.add(new Pregunta("¿Cuál es la distancia mínima de seguimiento en carretera?",
                Arrays.asList("A) 10 metros", "B) 50 metros", "C) Depende de la velocidad"), 2));
        banco.add(new Pregunta("¿Qué debe hacer ante una señal de STOP?",
                Arrays.asList("A) Detenerse completamente", "B) Reducir velocidad", "C) Solo si hay vehículos"), 0));
    }

    /**
     * Preguntas fijas: se usan para que la prueba muestre y califique las mismas preguntas.
     * Los cursos básico y avanzado usan este mismo banco; solo cambia la cantidad de puntos recuperados.
     */
    public static List<Pregunta> getPreguntasFijas() {
        return new ArrayList<>(banco);
    }

    public static List<Pregunta> getPreguntasAleatorias(int cantidad) {
        List<Pregunta> copia = new ArrayList<>(banco);
        Collections.shuffle(copia);
        return new ArrayList<>(copia.subList(0, Math.min(cantidad, copia.size())));
    }
}
