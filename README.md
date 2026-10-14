# SECR: Sistema de Evaluación de Conducción Responsable

**ISWZ2102 · Programación III · Universidad de Las Américas (UDLA), Quito**
**Período 2027-1 · Grupo 4 · Avance del proyecto, Progreso 1**

| Integrante | Módulo a cargo |
|---|---|
| Mateo Vasquez | M1 · Conductores y licencias (tabla hash + Trie) |
| Alex Palma | M2 · Eventos e infracciones (árbol rojo-negro por fechas) |
| Martín Jurado | M3 · Puntaje y priorización de riesgo (montículo indexado) |
| Amelio Macas | M4 · Recuperación de puntos: cursos, cola de inscripción y pruebas |
| Israel Zurita | M5 · Alertas y reportes (cola de prioridad de caducidad, ordenamiento) |

## 1. Problema

En Ecuador la licencia de conducir funciona con un sistema de puntos, pero el seguimiento del
comportamiento de cada conductor está repartido entre registros de infracciones, cursos y
renovaciones. SECR centraliza esa información y, sobre todo, la **procesa**: calcula un puntaje de
conducción responsable (0 a 100), mantiene siempre actualizado el **ranking de conductores de mayor
riesgo**, consulta el historial de infracciones **por rangos de fechas**, busca conductores **por
prefijo de nombre** y organiza la **cola de inscripción** a cursos de recuperación de puntos.

El proyecto reutiliza el código del semestre anterior (*TallerEntregaFinal*, interfaz Swing + capas
modelo/negocio) y lo extiende con estructuras de datos y algoritmos seleccionados en el Progreso 1.

## 2. Alternativa seleccionada (resumen)

| Componente | Alternativa A (descartada) | **Alternativa B (seleccionada)** |
|---|---|---|
| Búsqueda por licencia | Lista ordenada + búsqueda binaria, O(log n) | **HashMap**, O(1) promedio |
| Búsqueda por prefijo de nombre | Lista ordenada de palabras, O(log n + k) | **Trie**, O(P + R) |
| Ranking de riesgo (top-k) | Reordenar todo en cada consulta, O(n log n) | **Montículo binario indexado**, O(k log k); actualización O(log n) |
| Historial por rango de fechas | Lista ordenada, inserción O(n) | **TreeMap (árbol rojo-negro)**, inserción O(log n) |

Resultados del benchmark con n = 100 000 (mediana de 5 repeticiones, OpenJDK 21, 2 vCPU):

| Operación | A | B |
|---|---:|---:|
| Registro de 100 000 conductores | 3 741 ms | 796 ms |
| 500 × (actualizar puntaje + top-10 de riesgo) | 26 139 ms | 2,3 ms |
| Registro de 100 000 eventos | 324 ms | 15 ms |
| 1 000 consultas por rango de 30 días | 38,8 ms | 7,8 ms |
| Memoria aproximada del índice | 23,1 MB | 28,4 MB |

Detalle completo en [`docs/benchmark/resultados.csv`](docs/benchmark/resultados.csv) y en el informe técnico.

## 3. Estructura del repositorio

```
.
├── README.md
├── docs/
│   ├── informe/               Informe técnico del Progreso 1 (PDF)
│   ├── uml/                   Diagramas UML en PlantUML (.puml) y exportados (.png/.svg)
│   └── benchmark/             Resultados (CSV) y gráfico de la comparación A vs B
└── src/
    ├── modelo/                Clases de dominio (reutilizadas): Conductor, Licencia, EventoConduccion…
    ├── negocio/               Gestores (lógica de negocio): conductores, eventos, cursos, reportes
    ├── interfaz/              Interfaz gráfica Swing (reutilizada)
    ├── util/                  SistemaSECR (fachada que agrupa los gestores)
    ├── estructuras/           NUEVO: Trie, MonticuloIndexado, IndiceTemporalEventos,
    │   │                      OrdenamientoMezcla, BusquedaBinaria
    │   └── comparacion/       NUEVO: implementaciones A y B con una interfaz común
    └── benchmark/             NUEVO: pruebas automáticas y benchmark A vs B
```

## 4. Cómo compilar y ejecutar

Requisitos: **JDK 17 o superior** (probado con OpenJDK 21). No se necesitan librerías externas.

```bash
# Compilar todo
javac -encoding UTF-8 -d out $(find src -name "*.java")          # Linux / macOS
# Windows (PowerShell):
# javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName

# Aplicación con interfaz gráfica
java -cp out interfaz.Interfaz

# Aplicación de consola
java -cp out modelo.Main

# Pruebas automáticas de las estructuras (16 pruebas, incluye equivalencia A = B)
java -cp out benchmark.PruebasEstructuras

# Benchmark A vs B (tarda unos minutos; escribe docs/benchmark/resultados.csv)
java -Xmx3g -cp out benchmark.BenchmarkAlternativas
```

En IntelliJ IDEA: *File → Open* sobre la carpeta, marcar `src` como *Sources Root* y ejecutar
`interfaz.Interfaz`.

## 5. Diagramas UML

Los diagramas se mantienen como código en PlantUML para que el equipo los revise y versione:

* `docs/uml/01_modelo_dominio.puml`: clases de dominio, atributos, métodos, multiplicidades,
  herencia (`EventoConduccion` → `Infraccion`) y composiciones.
* `docs/uml/02_negocio_estructuras.puml`: gestores por módulo y estructuras de datos seleccionadas.

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
| Estructuras de la alternativa B (prototipo probado) | Listo |
| Integración de las estructuras en los gestores y en la interfaz | Planificado para Progreso 2 |
| Nuevas clases `InscripcionCurso`, `GestorRiesgo` y enums de estado | Planificado para Progreso 2 |

## 8. Uso de IA

Se usó un asistente de IA generativa (Claude, de Anthropic) como apoyo para estructurar el
informe, prototipar las estructuras de datos y el benchmark, y revisar la redacción. El equipo
revisó, ejecutó y validó todo el contenido; el detalle está en la declaración de uso de IA del
informe técnico.
