# SECR: Sistema de Evaluación de Conducción Responsable

**ISWZ2102 · Programación III · Ingeniería en Ciberseguridad · Universidad de Las Américas (UDLA), Quito**
**Período 2027-1 · Grupo 4 · Avance del proyecto, Progreso 1**

| Integrante | Módulo a cargo |
|---|---|
| Mateo Vasquez | M1 · Conductores y licencias (árboles AVL por licencia y por nombre) |
| Alex Palma | M2 · Eventos e infracciones (AVL por fecha + pila para deshacer) |
| Martín Jurado | M3 · Puntaje y priorización de riesgo (ranking sobre árboles AVL) |
| Amelio Macas | M4 · Recuperación de puntos: cursos, cola FIFO de inscripción y pruebas |
| Israel Zurita | M5 · Alertas, reportes y operativos (cola de prioridad + grafo vial con BFS/DFS/Dijkstra) |

## 1. Problema

En Ecuador la licencia de conducir funciona con un sistema de puntos, pero el seguimiento del
comportamiento de cada conductor está repartido entre registros de infracciones, cursos y
renovaciones. SECR centraliza esa información y, sobre todo, la **procesa**: calcula un puntaje de
conducción responsable (0 a 100), mantiene siempre actualizado el **ranking de conductores de mayor
riesgo**, consulta el historial de infracciones **por rangos de fechas**, organiza la **cola de
inscripción** a cursos de recuperación y planifica **operativos de control** sobre un mapa vial.

**Restricción de diseño:** solo se usan **pilas, colas, árboles y grafos**, implementados por el
equipo. No se usan tablas hash (HashMap, HashSet, LinkedHashMap) ni Trie; las del código heredado
fueron reemplazadas por `ArbolAVL`.

## 2. Estructuras y alternativa seleccionada

| Estructura (propia) | Uso en SECR |
|---|---|
| `ArbolAVL<K,V>` | Índices por licencia, nombre, fecha y código de curso; conteos; ranking de riesgo |
| `Pila<T>` | Deshacer el último registro (M2); recorridos iterativos (in-order, DFS) |
| `Cola<T>` | Cola FIFO de inscripción a cursos (M4); BFS |
| `MonticuloBinario<T>` | Cola de prioridad de caducidad de licencias (M5); Dijkstra |
| `Grafo` | Mapa vial: zonas de influencia (BFS), ruta de operativos (Dijkstra), zonas aisladas (DFS) |

Para el componente crítico (ranking de riesgo, M3) se compararon dos alternativas competitivas:
**A** (dos árboles AVL) y **B** (cola de prioridad con actualización perezosa).
Resultados con n = 100 000 (mediana de 5 repeticiones, OpenJDK 21, 2 vCPU):

| Operación | A · AVL | B · montículo |
|---|---:|---:|
| 20 000 actualizaciones de puntaje | 116,0 ms | 42,2 ms |
| 1 000 consultas top-10 | 0,18 ms | 1,53 ms |
| 50 consultas semáforo rojo | 64,8 ms | 220,2 ms |
| Memoria tras n registros y 2n cambios | 8,4 MB | 12,4 MB |

**Seleccionada: Alternativa A** (consultas ordenadas y más rápidas, memoria fija, una sola estructura
reutilizada en todos los módulos). Detalle en [`docs/benchmark/resultados.csv`](docs/benchmark/resultados.csv)
y en el informe técnico.

## 3. Estructura del repositorio

```
.
├── README.md
├── docs/
│   ├── informe/               Informe técnico del Progreso 1 (PDF)
│   ├── uml/                   Diagramas UML en PlantUML (.puml) y exportados (.png/.svg)
│   └── benchmark/             Resultados (CSV) y gráfico de la comparación A vs B
└── src/main/java/
    ├── modelo/                Clases de dominio (reutilizadas): Conductor, Licencia, EventoConduccion…
    ├── negocio/               Gestores de cada módulo (ahora sobre ArbolAVL, sin tablas hash)
    ├── interfaz/              Interfaz gráfica Swing (reutilizada)
    ├── util/                  SistemaSECR (fachada que agrupa los gestores)
    ├── estructuras/           NUEVO: ArbolAVL, Pila, Cola, MonticuloBinario, Grafo,
    │   │                      IndiceTemporalEventos, OrdenamientoMezcla
    │   └── comparacion/       NUEVO: RankingRiesgo, alternativas A (AVL) y B (montículo)
    └── benchmark/             NUEVO: pruebas automáticas y benchmark A vs B
```

## 4. Cómo compilar y ejecutar

Requisitos: **JDK 17 o superior** (probado con OpenJDK 21). No se necesitan librerías externas.

```bash
# Compilar todo
javac -encoding UTF-8 -d out $(find src/main/java -name "*.java")   # Linux / macOS
# Windows (PowerShell):
# javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName

# Aplicación con interfaz gráfica
java -cp out interfaz.Interfaz

# Aplicación de consola
java -cp out modelo.Main

# Pruebas automáticas (29 pruebas: AVL, pila, cola, montículo, grafo, A = B y gestores)
java -cp out benchmark.PruebasEstructuras

# Benchmark A vs B (tarda unos minutos; escribe docs/benchmark/resultados.csv)
java -Xmx3g -cp out benchmark.BenchmarkAlternativas
```

En IntelliJ IDEA: *File → Open* sobre la carpeta, marcar `src/main/java` como *Sources Root* y ejecutar
`interfaz.Interfaz`.

## 5. Diagramas UML

Los diagramas se mantienen como código en PlantUML para que el equipo los revise y versione:

* `docs/uml/01_modelo_dominio.puml`: clases de dominio, atributos, métodos, multiplicidades,
  herencia (`EventoConduccion` → `Infraccion`) y composiciones.
* `docs/uml/02_negocio_estructuras.puml`: gestores por módulo y estructuras propias (pilas, colas, árboles y grafo).

Se pueden abrir con la extensión *PlantUML* de IntelliJ/VS Code o en <https://www.plantuml.com/plantuml>.

## 6. Flujo de trabajo con Git

* `main`: versión estable que se entrega en cada progreso.
* `develop`: integración del equipo.
* `feature/m1-conductores`, `feature/m2-eventos`, `feature/m3-riesgo`, `feature/m4-cursos`,
  `feature/m5-reportes`: una rama por módulo y responsable.
* Cada cambio entra por *pull request* revisado por otro integrante y con
  `benchmark.PruebasEstructuras` en verde.

## 7. Estado

| Entregable | Estado |
|---|---|
| Código base del semestre anterior (Swing, modelo, negocio) | Reutilizado y compilando |
| Estructuras propias (AVL, pila, cola, montículo, grafo) y alternativas A/B probadas | Listo |
| Tablas hash del código heredado reemplazadas por ArbolAVL | Listo |
| Integración de las estructuras en los gestores y en la interfaz | Planificado para Progreso 2 |
| Nuevas clases `InscripcionCurso`, `Zona`, `TramoVial`, `GestorRiesgo` y enums de estado | Planificado para Progreso 2 |

## 8. Uso de IA

Se usó un asistente de IA generativa (Claude, de Anthropic) como apoyo para estructurar el
informe, prototipar las estructuras de datos y el benchmark, y revisar la redacción. El equipo
revisó, ejecutó y validó todo el contenido; el detalle está en la declaración de uso de IA del
informe técnico.
