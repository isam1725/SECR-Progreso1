# Evidencia del benchmark (componente M3: ranking de riesgo)

Este directorio contiene los datos originales y la salida de consola del benchmark que compara,
para el componente de priorización de riesgo (M3), la estructura de la Alternativa A (árboles AVL)
con la de la Alternativa B (cola de prioridad con actualización perezosa).

| Archivo | Contenido |
|---|---|
| `resultados.csv` | Medianas (ms y MB) de la versión del Progreso 2 |
| `salida_benchmark.txt` | Salida de consola de esa ejecución |
| `resultados_progreso1.csv` | Datos de la versión entregada en el Progreso 1 (trazabilidad) |
| `grafico_benchmark.png` | Gráfico generado a partir de `resultados.csv` |

## Cómo reproducir

```bash
javac -encoding UTF-8 -d out $(find src/main/java -name "*.java")
java -cp out benchmark.PruebasEstructuras            # primero: A y B dan los mismos resultados
java -Xmx3g -cp out benchmark.BenchmarkAlternativas   # 5 a 8 minutos; reescribe resultados.csv
```

* Código: `src/main/java/benchmark/BenchmarkAlternativas.java` y `GeneradorDatos.java`.
* Alternativas medidas: `src/main/java/estructuras/comparacion/RankingRiesgoAVL.java` (A) y
  `RankingRiesgoMonticulo.java` (B), ambas detrás de la interfaz `RankingRiesgo`.
* Datos sintéticos con semilla fija (42), calentamiento previo de la JVM y mediana de 5 repeticiones.
* Entorno de la ejecución publicada: OpenJDK 21.0.12, Linux, 2 vCPU. Los valores absolutos cambian
  según el equipo; lo que se interpreta es la proporción entre A y B.
