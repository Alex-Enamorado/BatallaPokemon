# Batalla Pokémon

Simulador de batalla por turnos entre dos entrenadores Pokémon, con GUI en Swing
y estructuras de datos (listas enlazadas) implementadas a mano.

**Integrantes:** Oscar Canahuati, Alex Enamorado, Leandro Sandoval y Marcelo Garcia

El reparto de tareas y los contratos entre clases están en **[PLAN.md](PLAN.md)**.

## Cómo correrlo (IntelliJ IDEA)

El repo versiona lo justo de `.idea/` para que el proyecto abra y compile en cualquier
máquina (el módulo con el *source root* y las configuraciones de ejecución), así que
alcanza con:

1. `File > Open...` y abrir la carpeta del repo.
2. Si IntelliJ avisa que falta el SDK: `File > Project Structure > Project > SDK`
   y elegir cualquier JDK 17 o superior.
3. Elegir la configuración **Main** en el selector de arriba y darle al ▶.
   (La configuración **Pruebas** corre el harness de consola.)

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

## Estado

Los cuatro carriles están completos y el juego es jugable de punta a punta:
login → equipo → batalla por turnos → victoria/derrota → reiniciar.

| Área | Estado |
|---|---|
| `estructuras/` — `ListaPokemon` (9 operaciones + mover al 1er lugar) | ✅ |
| `logica/` — `MotorBatalla`, `TablaTipos` (18 tipos), `Estadisticas` | ✅ |
| `gui/` — batalla, equipo, login y los 4 diálogos | ✅ |
| `datos/` — PokeAPI + caché + fallback offline, usuarios | ✅ |

Verificación: `PruebasConsola` (configuración **Pruebas**) cubre estructuras, JsonMini,
usuarios, motor de batalla y PokeAPI. Está en **101/101**. Hace backup de
`datos/usuarios.txt` y lo restaura, así que se puede correr sin ensuciar el roster.

### Detalles que conviene saber

- **Sprites animados**: los GIF de gen-V se animan solos con `new ImageIcon(ruta)`.
  Al rival se le ve el sprite de frente y al propio el de espalda. No hay que escalarlos:
  `getScaledInstance()` rompe la animación.
- **PokeAPI no tiene animaciones de ataque ni de daño.** El endpoint `/move/` es solo
  metadata y el repo de sprites no tiene carpeta de movimientos. Los GIF son animaciones
  de reposo. Para que los golpes se vean, habría que animarlos con `javax.swing.Timer`
  (sacudida, destello, drenaje de la barra de HP).
- **Proveedores**: `Main` arma un `ProveedorCompuesto(local, api)`. Los 16 locales
  responden al instante y cualquier otro nombre va a PokeAPI (que busca primero en
  la caché). Eso es lo que permite que un equipo armado con Pokémon de la API se
  reconstruya al volver a iniciar sesión.
- **Caché**: `datos/sprites/` se commitea (es el fallback visual). `datos/pokemon/`
  no: cada JSON de PokeAPI pesa ~420 KB y se regenera solo.

## Reglas del equipo

- **No usar** `ArrayList`, `LinkedList`, `Vector`, `Stack`, `Queue`, `HashMap`, `HashSet`
  (restricción de la consigna). Tampoco `JTable`, `JList` ni `JComboBox`: usan `Vector`
  por dentro. Para listas en pantalla se usa `PanelFilaPokemon`.
- El paquete `gui` **nunca** importa `NodoPokemon`. Todo pasa por los métodos públicos
  de `ListaPokemon`.
- El paquete `logica` **nunca** importa `javax.swing`.
- Cada uno toca solo los archivos de su carril. Si necesitás cambiar una firma de otro
  carril, avisá en el grupo y que la cambie su dueño.
