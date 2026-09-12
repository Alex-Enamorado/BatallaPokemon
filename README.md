# Batalla Pokémon

Simulador de batalla por turnos entre dos entrenadores Pokémon, con GUI en Swing
y estructuras de datos (listas enlazadas) implementadas a mano.

Proyecto de 4 integrantes. El reparto de tareas, los contratos entre clases y los
milestones están en **[PLAN.md](PLAN.md)** — leerlo antes de escribir código.

## Cómo correrlo (IntelliJ IDEA)

La configuración de IntelliJ está commiteada en `.idea/` (source root, language level
y configuraciones de ejecución), así que alcanza con:

1. `File > Open...` y abrir la carpeta del repo.
2. Si IntelliJ avisa que falta el SDK: `File > Project Structure > Project > SDK`
   y elegir cualquier JDK 17 o superior.
3. Elegir la configuración **Main** en el selector de arriba y darle al ▶.
   (La configuración **PruebasConsola** corre las pruebas de las estructuras.)

Ambas configuraciones ya tienen el *working directory* en `$PROJECT_DIR$`, que es lo
que hace que se encuentren los sprites de `datos/`.

> **Si `src` aparece como carpeta común y no como paquetes** (sin botón de Run, sin
> autocompletado): el source root se perdió. Click derecho en `src` →
> *Mark Directory as* → *Sources Root*. O `File > Reload Project from Disk` para
> releer el `.idea/` del repo.

Requiere JDK 17 o superior. No hay dependencias externas ni Maven/Gradle.

Para probar las estructuras hay una segunda clase con `main`:
`src/batallapokemon/PruebasConsola.java`. Es una herramienta de desarrollo
(el juego se juega 100% por GUI, como pide la consigna).

## Estado actual: M0 + carril Dev 4

Lo que **ya funciona**:

- El proyecto compila completo y la ventana de login abre.
- Modelos (`Pokemon`, `Ataque`, `Objeto`, `Entrenador`, `Usuario`, `Registro`, `Tipo`).
- `ListaEnlazada<T>` genérica completa (inventario, historial, ataques, usuarios).
- `ListaPokemon`: `insertar`, `contar`, `obtener`, `recorrer`.
- `TablaTipos` con la tabla de efectividad completa de los 18 tipos.
- `ProveedorLocal` con 16 Pokémon y sus ataques (funciona sin internet).
- 32 sprites animados en `datos/sprites/` (fallback offline listo).
- Layout de la ventana de batalla, diálogos de cambio / objetos / historial y vista de equipo.
- **Carril Dev 4 completo**: `JsonMini`, `ProveedorPokeApi` (fetch + caché + fallback),
  `GestorUsuarios` (login / crear / persistir / roster / rival), `VentanaLogin` y
  `VentanaEquipo` (agregar / buscar / eliminar / mover). Verificado con
  `PruebasDev4`: **40/40**.

Lo que **falta** está marcado con `TODO Dev N` en el código, agrupado por carril:

| Carril | Archivos | Qué falta |
|---|---|---|
| **Dev 1** | `estructuras/ListaPokemon.java` | `buscar`, `eliminar`, `contarDisponibles`, `getActivo`, `setActivo`, `siguienteDisponible`, `modificar`, `todosDerrotados`, `moverAlPrimerLugar` |
| **Dev 2** | `logica/MotorBatalla.java` | `atacar`, `calcularDanio`, `cambiarPokemon`, `usarObjeto`, `reiniciar` |
| **Dev 3** | `gui/VentanaBatalla.java`, `gui/DialogoObjetos.java` | selección de ataque, diálogo de fin de batalla, destino del objeto |
| ~~**Dev 4**~~ | — | ✅ listo |

Hay dos harness de consola (herramientas de desarrollo, el juego se juega solo por GUI):

- **`PruebasConsola`** — marcador de Dev 1. Arranca en 7 OK / 10 FALLAN; la meta es 17/17.
- **`PruebasDev4`** — carril Dev 4. Está en 40/40. Hace backup de `datos/usuarios.txt`
  y lo restaura, así que se puede correr sin ensuciar el roster.

> Los botones **Buscar**, **Eliminar** y **Mover al 1er lugar** de `VentanaEquipo` ya
> están escritos, pero van a responder "no está en el equipo" hasta que Dev 1
> implemente `buscar`, `eliminar` y `moverAlPrimerLugar` en `ListaPokemon`. No es un
> bug de la ventana.

## Reglas del equipo

- **No usar** `ArrayList`, `LinkedList`, `Vector`, `Stack`, `Queue`, `HashMap`, `HashSet`
  (restricción de la consigna). Tampoco `JTable`, `JList` ni `JComboBox`: usan `Vector`
  por dentro. Para listas en pantalla se usa `PanelFilaPokemon`.
- El paquete `gui` **nunca** importa `NodoPokemon`. Todo pasa por los métodos públicos
  de `ListaPokemon`.
- El paquete `logica` **nunca** importa `javax.swing`.
- Cada uno toca solo los archivos de su carril. Si necesitás cambiar una firma de otro
  carril, avisá en el grupo y que la cambie su dueño.
