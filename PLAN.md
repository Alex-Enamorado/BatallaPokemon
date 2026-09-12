# Batalla Pokémon — Plan de Trabajo

**Equipo:** 4 integrantes · **Lenguaje:** Java 17+ · **GUI:** Swing · **Dependencias externas:** ninguna

---

## 1. Decisiones técnicas (cerradas)

| Tema | Decisión | Por qué |
|---|---|---|
| GUI | **Swing** (incluido en el JDK) | Nadie se bloquea instalando el SDK de JavaFX. Compila con `javac` en cualquier máquina. |
| Build | **Sin Maven/Gradle**: se abre la carpeta en IntelliJ y se corre `Main` | Cero configuración, cero "a mí no me compila". |
| PokeAPI | Fetch en vivo → **caché local** en `datos/` → **fallback offline** commiteado | El demo en clase no depende del WiFi del salón. |
| JSON | Parser propio de ~30 líneas (`JsonMini`) | Evita Gson/Jackson (y evita discusiones por `HashMap`). |
| HTTP | `java.net.http.HttpClient` (JDK 11+) | Ya viene en el JDK. |
| Persistencia usuarios | Archivo de texto `datos/usuarios.txt` | Sin BD, sin librerías. |

### ⚠️ Trampas con las restricciones (leer antes de codear GUI)

La consigna prohíbe `ArrayList`, `LinkedList`, `Vector`, `Stack`, `Queue`, `HashMap`, `HashSet`.
Varios componentes de Swing usan `Vector` **por dentro**. Para no dar motivo de reclamo:

- ❌ **NO usar** `JTable`, `JList`, `DefaultListModel`, `JComboBox` (`DefaultComboBoxModel` es `Vector`).
- ✅ **Sí usar** filas construidas a mano: un `JPanel` con `BoxLayout` + un `PanelFilaPokemon` (sprite + labels + `JRadioButton`) por Pokémon.
  Se ve mejor con los sprites y además es más simple.
- Arreglos (`Pokemon[]`, `double[][]`) **sí** están permitidos (no están en la lista) — se usan solo para la tabla de tipos.

---

## 2. Estructura del repositorio

```
BatallaPokemon/
├─ PLAN.md                    ← este archivo
├─ README.md                  ← cómo correrlo en IntelliJ + capturas
├─ datos/                     ← caché + fallback offline (SE COMMITEA)
│  ├─ usuarios.txt
│  ├─ pokemon/<nombre>.json   (caché local, NO se commitea: 420 KB c/u)
│  └─ sprites/<id>.gif       (32 sprites animados ya commiteados)
└─ src/batallapokemon/
   ├─ Main.java
   ├─ modelo/        Pokemon, Ataque, Objeto, Entrenador, Usuario, Tipo, Registro
   ├─ estructuras/   NodoPokemon, ListaPokemon, Nodo<T>, ListaEnlazada<T>
   ├─ logica/        MotorBatalla, TablaTipos, ResultadoTurno, GestorUsuarios, Estadisticas
   ├─ datos/         ProveedorPokemon, ProveedorLocal, ProveedorPokeApi, CacheArchivos, JsonMini
   └─ gui/           VentanaLogin, VentanaBatalla, VentanaEquipo,
                     DialogoCambiar, DialogoObjetos, DialogoHistorial, PanelFilaPokemon
```

**Regla de oro:** cada integrante toca **solo los paquetes de su carril**. Los paquetes son disjuntos ⇒ casi cero conflictos de merge.

---

## 3. División del trabajo (4 carriles sin bloqueos)

### 👤 Dev 1 — Estructuras y modelos (`estructuras/`, `modelo/`)
Es el carril **crítico**: debe terminar primero porque todos dependen de él.
- `NodoPokemon`, `ListaPokemon` con las 8 operaciones + el extra.
- `Nodo<T>` / `ListaEnlazada<T>` genérica → se reutiliza para objetos, historial, ataques y usuarios.
- `Pokemon`, `Ataque`, `Objeto`, `Entrenador`, `Usuario`, `Tipo` (enum), `Registro`.
- Reto adicional (4 integrantes): `moverAlPrimerLugar(String nombre)` manipulando solo referencias.
- `PruebasConsola.java`: un `main` de prueba para las listas (permitido: es herramienta de desarrollo, el **juego** no se juega por consola).

### 👤 Dev 2 — Motor de combate (`logica/`)
- `MotorBatalla`: turnos, ataque del jugador, respuesta automática del rival, detección de debilitado, cambio automático al siguiente disponible, victoria/derrota, `reiniciar()`.
- `TablaTipos`: ventajas/desventajas (`double[][]` de 18×18, o un `switch` por tipo atacante).
- Cálculo de daño, uso de objetos, registro en el historial, `Estadisticas`.
- **No toca GUI**: devuelve `ResultadoTurno` con las líneas de texto ya armadas.

### 👤 Dev 3 — GUI de batalla (`gui/VentanaBatalla` + diálogos)
- Ventana principal: paneles rival/jugador con sprite, nombre, nivel, tipo, barra de HP (`JProgressBar`), botones y panel de historial.
- `DialogoCambiar` (radio buttons, bloquea derrotados), `DialogoObjetos`, `DialogoHistorial`, vista MI EQUIPO.
- `PanelFilaPokemon` reutilizable (lo usan también Dev 4 y los diálogos).
- Dueño de `Main.java` y del *wiring*.

### 👤 Dev 4 — Usuarios, PokeAPI y gestión de equipo (`datos/`, `gui/VentanaLogin`, `gui/VentanaEquipo`)
- `VentanaLogin`: crear usuario, log in, botón "usuario aleatorio" (roster de 10).
- `GestorUsuarios` + `datos/usuarios.txt`.
- `VentanaEquipo`: agregar / buscar / eliminar / mover al primer lugar (todo vía métodos públicos de `ListaPokemon`).
- `ProveedorPokeApi` + `CacheArchivos` + `JsonMini` + descarga de sprites.

**Desbloqueo clave:** Dev 1 entrega `ProveedorLocal` con ~15 Pokémon hardcodeados en el **M1**. Dev 3 y Dev 4 programan contra la interfaz `ProveedorPokemon` y nunca esperan a que PokeAPI esté listo.

---

## 4. Contratos (se congelan en el M0 — nadie cambia una firma sin avisar)

```java
// estructuras/ListaPokemon.java  — la GUI JAMÁS ve un NodoPokemon
public class ListaPokemon {
    void     insertar(Pokemon p);                 // al final
    Pokemon  buscar(String nombre);               // null si no existe
    boolean  eliminar(String nombre);
    String   recorrer();                          // "Pikachu → Charizard → null"
    int      contar();
    int      contarDisponibles();                 // HP > 0
    Pokemon  obtener(int indice);                 // iteración para la GUI, sin exponer nodos
    Pokemon  getActivo();
    boolean  setActivo(String nombre);            // false si está derrotado
    Pokemon  siguienteDisponible();
    boolean  modificar(String nombre, int nivel, int hpMax, Tipo tipo);
    boolean  todosDerrotados();
    boolean  moverAlPrimerLugar(String nombre);   // EXTRA (4 integrantes)
}

// logica/MotorBatalla.java
public class MotorBatalla {
    MotorBatalla(Entrenador jugador, Entrenador rival);
    ResultadoTurno atacar(int indiceAtaque);
    ResultadoTurno cambiarPokemon(String nombre);
    ResultadoTurno usarObjeto(String nombreObjeto, String nombrePokemonDestino);
    ListaEnlazada<Registro> getHistorial();
    Estadisticas getEstadisticas();
    boolean batallaTerminada();
    boolean jugadorGano();
    void    reiniciar();
    int     getTurno();
}

// logica/ResultadoTurno.java  — todo lo que la GUI necesita mostrar
public class ResultadoTurno {
    ListaEnlazada<String> lineas;   // "Pikachu utilizó Impactrueno.", "Gengar recibió 25 ..."
    boolean cambioForzado, batallaTerminada, jugadorGano, accionInvalida;
    String  mensajeError;
}

// datos/ProveedorPokemon.java
public interface ProveedorPokemon {
    Pokemon obtener(String nombre);        // null si no existe
    String[] nombresDisponibles();
}
```

**Fórmula de daño** (Dev 2, simple y suficiente):
```
base = ((2*nivel/5 + 2) * potencia * ataque / defensa) / 50 + 2
daño = max(1, base * multiplicadorTipo * aleatorio(0.85 .. 1.00))
```

---

## 5. PokeAPI — cómo se integra (Dev 4)

**Metadata:** `GET https://pokeapi.co/api/v2/pokemon/{nombre}`
→ se leen solo: `id`, `name`, `types[0].type.name`, `stats[]` (hp, attack, defense), `moves[0..3].move.name`.

**Sprites (URL predecible, sin necesidad de parsear JSON):**
```
Animado (GEN V):  https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white/animated/{id}.gif
Frente estático:  https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/{id}.png
Espalda:          https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/{id}.png
```
💡 Swing anima los **GIF** automáticamente con `new ImageIcon(ruta)`. Cero código extra y la batalla se ve viva: sprite de espalda para tu Pokémon, de frente para el rival.

**Flujo:**
1. ¿Existe `datos/pokemon/{id}.json`? → se usa. Si no, se pide a la API y **se guarda**.
2. Igual con `datos/sprites/{id}.gif`.
3. Timeout de 3 s. Si falla la red → `ProveedorLocal` (los 15 commiteados). La app nunca se cae ni se queda sin imagen.
4. Toda descarga va dentro de un `SwingWorker` para no congelar la ventana.
5. La **potencia** de los ataques NO se pide a la API (serían 4 requests extra por Pokémon): se toma el *nombre* del movimiento de la API y la potencia de una tabla fija por slot (40 / 60 / 80 / 90).

**Qué se commitea y qué no:** los sprites sí (`datos/sprites/`, ~1.8 MB, son el fallback
visual). El JSON de la API **no**: pesa ~420 KB por Pokémon y se regenera solo. El modo
offline lo garantizan `ProveedorLocal` (16 Pokémon) + esos sprites.

---

## 6. Milestones

| # | Qué | Quién | Estado |
|---|---|---|---|
| **M0** | Repo + `PLAN.md` + esqueleto que **compila** + sprites de respaldo + `.gitignore`. Se sube el link. | Todos | ☑ |
| **M1** | `ListaPokemon` + modelos + `ProveedorLocal` con 16 Pokémon | Dev 1 | ☑ |
| **M2** | Ventana de batalla con sprites y barras de HP + `MotorBatalla.atacar()` de punta a punta | Dev 2 + Dev 3 | ☑ |
| **M3** | Login/usuarios, `VentanaEquipo`, PokeAPI en vivo, diálogos CAMBIAR / OBJETOS / MI EQUIPO / HISTORIAL | Dev 3 + Dev 4 | ☑ |
| **M4** | Extra: mover al primer lugar · reiniciar partida · estadísticas · victoria/derrota | Dev 1 + Dev 2 | ☑ |
| **M5** | Día de integración: harness en 101/101, README al día. Faltan las capturas. | Todos | ◐ |

---

## 7. Flujo de Git (para no pisarse)

- `main` siempre compila. Nadie hace push directo a `main` después del M0.
- Ramas: `estructuras`, `combate`, `gui-batalla`, `datos-equipo`. Pull Request → revisa cualquier otro integrante → merge.
- `git pull origin main` **antes** de empezar a trabajar, todos los días.
- Si necesitás cambiar una firma de otro carril: **no la cambies**, avisá en el grupo y que el dueño del archivo la cambie. Así el contrato se mantiene.
- `datos/` se commitea (es el fallback offline). `out/`, `*.class`, `.idea/` van al `.gitignore`.

## 8. Checklist de la consigna

**Listas:** insertar ☑ buscar ☑ eliminar ☑ recorrer ☑ contar ☑ activo ☑ siguiente disponible ☑ modificar ☑ mover al primer lugar ☑
**Usuario:** crear ☑ login ☑ 10 usuarios aleatorios con equipo ☑
**Equipo:** consultar ☑ agregar ☑ buscar ☑ eliminar ☑ cantidad disponibles ☑
**Combate:** atacar ☑ recibir daño ☑ calcular daño ☑ ventajas de tipo ☑ detectar derrota ☑ cambiar ☑ continuar con el siguiente ☑
**Inventario:** consultar ☑ usar ☑ actualizar (lista enlazada propia) ☑
**Sistema:** historial ☑ estadísticas ☑ victoria ☑ derrota ☑ reiniciar ☑
**GUI:** todo por ventanas, sprites visibles, nada de consola ☑

Donde se demuestra cada operación de la lista: las 9 son alcanzables desde la GUI —
`insertar`/`buscar`/`eliminar`/`modificar`/`moverAlPrimerLugar` desde MI EQUIPO,
`getActivo`/`contar`/`contarDisponibles`/`recorrer` en las tarjetas y el encabezado, y
`siguienteDisponible` cuando cae el Pokémon activo en combate.
