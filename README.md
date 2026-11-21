# SECR: Sistema de Evaluación de Conducción Responsable

**ISWZ2102 · Programación III · Ingeniería en Ciberseguridad · Universidad de Las Américas (UDLA), Quito**
**Período 2027-1 · Grupo 4 · Versión del Progreso 2** (el Progreso 1 está en el commit `f53f7ad`)

Repositorio: **https://github.com/isam1725/SECR-Progreso1**

| Integrante | Módulo a cargo |
|---|---|
| Jeremy Vasquez Santos | M1 · Conductores y licencias |
| Alex Palma Zambrano | **M2 · Registro y análisis de infracciones (implementado en el Progreso 2)** |
| Alan Jurado Balseca | **M3 · Puntaje y priorización de riesgo (implementado en el Progreso 2)** |
| Amelio Macas Pachar | M4 · Recuperación de puntos: cursos, cola FIFO de inscripción y pruebas |
| Matias Zurita Garcia | M5 · Alertas, reportes y operativos de control |

## 1. Descripción

SECR registra las infracciones de tránsito de cada conductor, calcula un puntaje de conducción
responsable (0 a 100) y mantiene siempre actualizado el **ranking de conductores de mayor riesgo**.
Está escrito en Java con interfaz gráfica Swing y usa estructuras de datos implementadas por el
equipo. No usa tablas hash ni Trie.

## 2. Ejecutar

Requisito: **Java 17 o superior** (`java -version`).

* **Windows:** doble clic en `ejecutar.bat`, o bien `java -jar dist\SECR.jar`.
* **Linux / macOS:** `./ejecutar.sh`, o bien `java -jar dist/SECR.jar`.
* **IntelliJ IDEA / NetBeans:** abrir la carpeta, marcar `src/main/java` como carpeta de fuentes y
  ejecutar la clase `interfaz.Interfaz`.

Para recompilar y regenerar `dist/SECR.jar` use `compilar.bat` (Windows) o `./compilar.sh`.

La aplicación arranca con 12 conductores y 16 infracciones de prueba. En el menú izquierdo:

* **M2 · Infracciones:** registrar una infracción (por ejemplo licencia `B-030405`, gravedad 2),
  deshacerla, consultar por rango de fechas, ordenar y navegar el historial de un conductor.
* **M3 · Ranking de riesgo:** top-k, filtro por semáforo, posición de un conductor (por ejemplo
  `B-050607`) y bonificación por buen comportamiento.

## 3. Módulos implementados en el Progreso 2

| Módulo | Clases | Estructuras y algoritmos |
|---|---|---|
| M2 · Infracciones | `negocio.GestorEventos`, `interfaz.PanelInfracciones` | Árbol AVL por fecha (`IndiceTemporalEventos`), pila para deshacer (`Pila`), lista doblemente enlazada (`ListaDoble`), Merge Sort (`OrdenamientoMezcla`) |
| M3 · Riesgo | `negocio.GestorRiesgo`, `interfaz.PanelRiesgo` | Ranking sobre árboles AVL (`RankingRiesgoAVL`), consulta de rango, posición en O(log n) con tamaño de subárbol (`ArbolAVL.posicion`) |

Al registrar o deshacer una infracción en M2, el conductor se reubica automáticamente en el
ranking de M3.

## 4. Verificación y evidencias (reproducibles)

```bash
javac -encoding UTF-8 -d out $(find src/main/java -name "*.java")
java -cp out verificacion.VerificacionFuncional    # 19 casos VF de M2 y M3 → 19/19 cumplen
java -cp out benchmark.PruebasEstructuras          # 32 pruebas de las estructuras → 32/32
java -Xmx3g -cp out benchmark.BenchmarkAlternativas  # benchmark A vs B del componente M3
java -cp out interfaz.CapturaDemo                  # regenera las capturas de docs/capturas
```

En Windows (PowerShell) la primera línea es:
`javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName`

| Carpeta | Contenido |
|---|---|
| `docs/verificacion/` | Salida y CSV del registro de verificación funcional y de las pruebas de estructuras |
| `docs/capturas/` | 19 capturas de la aplicación ejecutando M2 y M3 (casos normales, de error y límite) |
| `docs/benchmark/` | Datos originales, salida de consola e instrucciones del benchmark |
| `docs/uml/` | Diagramas PlantUML del código actual (`progreso1/` guarda los del Progreso 1) |
| `docs/informe/` | Informes técnicos del Progreso 1 y del Progreso 2; la versión vigente del Progreso 2 es `SECR_Informe_Progreso2_Grupo4_v4` (plantilla UDLA, alineada con los temas de clase) |

## 5. Estructura del repositorio

```
.
├── README.md · compilar.bat/.sh · ejecutar.bat/.sh
├── dist/SECR.jar                Proyecto ejecutable
├── docs/                        Informe, UML, capturas, verificación y benchmark
└── src/main/java/
    ├── modelo/                  Clases de dominio (Conductor, Licencia, EventoConduccion, NivelRiesgo…)
    ├── negocio/                 Gestores: GestorEventos (M2), GestorRiesgo (M3), GestorConductores…
    ├── interfaz/                Swing: VentanaPrincipal, PanelInfracciones (M2), PanelRiesgo (M3)…
    ├── util/                    SistemaSECR (fachada y datos de prueba)
    ├── estructuras/             ArbolAVL, Pila, Cola, ListaDoble, MonticuloBinario, Grafo,
    │   │                        IndiceTemporalEventos, OrdenamientoMezcla
    │   └── comparacion/         RankingRiesgo: alternativas A (AVL) y B (montículo)
    ├── verificacion/            VerificacionFuncional (casos VF)
    └── benchmark/               PruebasEstructuras, BenchmarkAlternativas
```

## 6. Estado

| Componente | Estado |
|---|---|
| M2 · Registro y análisis de infracciones | **Implementado (Progreso 2)** |
| M3 · Puntaje y priorización de riesgo | **Implementado (Progreso 2)** |
| M1 · Conductores y licencias (código heredado, índices AVL) | Funcional; se completará la búsqueda por prefijo |
| M4 · Cola FIFO de inscripción a cursos | Entrega final (la `Cola` ya está implementada y probada) |
| M5 · Grafo vial (BFS, DFS, Dijkstra) y cola de prioridad de caducidad | Entrega final (`Grafo` y `MonticuloBinario` ya están implementados y probados) |

## 7. Uso de IA

Se usó un asistente de IA generativa (Claude, de Anthropic) como apoyo. El equipo revisó, ejecutó
y validó todo el contenido; el detalle está en la declaración de uso de IA del informe técnico.
