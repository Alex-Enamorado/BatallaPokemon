# Batalla Pokémon

Simulador de batalla por turnos entre dos entrenadores Pokémon. GUI en Swing y
estructuras de datos (listas enlazadas) implementadas a mano, sin usar las
colecciones de `java.util`.

**Integrantes:** Oscar Canahuati, Alex Enamorado, Leandro Sandoval y Marcelo Garcia

## Cómo correrlo

Requiere JDK 17 o superior. No hay dependencias externas ni Maven/Gradle.

1. `File > Open...` en IntelliJ y abrir la carpeta del repo.
2. Si avisa que falta el SDK: `File > Project Structure > Project > SDK` y elegir
   un JDK 17+.
3. Elegir la configuración **Main** y darle al ▶.

El *working directory* de la configuración de ejecución tiene que ser la raíz del
proyecto (`$PROJECT_DIR$`, el valor por defecto), porque las rutas de `datos/` son
relativas. Si no, no se ven los sprites.

La configuración **Pruebas** corre un harness de consola que verifica las
estructuras, el motor de batalla, los usuarios y PokeAPI. Es una herramienta de
desarrollo: el juego se juega solo por GUI.

## Estructura

```
datos/            usuarios.txt, sprites y caché de PokeAPI
src/batallapokemon/
  modelo/         Pokemon, Ataque, Objeto, Entrenador, Usuario, Registro, Tipo
  estructuras/    NodoPokemon, ListaPokemon, Nodo<T>, ListaEnlazada<T>
  logica/         MotorBatalla, TablaTipos, Estadisticas, GestorUsuarios
  datos/          proveedores de Pokemon, PokeAPI, caché, lector de JSON
  gui/            ventanas de login, batalla y equipo, y sus diálogos
```

## Notas

- Los sprites son los GIF animados de PokeAPI y se animan solos con
  `new ImageIcon(ruta)`. No hay que escalarlos: `getScaledInstance()` rompe la
  animación.
- `Main` usa un `ProveedorCompuesto(local, api)`. En ese orden: los 16 Pokémon
  locales responden al instante y cualquier otro nombre va a PokeAPI, que busca
  primero en la caché. Sin internet el juego funciona igual con los locales.
- `datos/sprites/` se versiona porque es el respaldo visual. `datos/pokemon/` no:
  cada JSON de PokeAPI pesa unos 420 KB y se regenera solo.
- PokeAPI no tiene animaciones de ataque ni de daño recibido, solo sprites de
  reposo.
- No se usan `JTable`, `JList` ni `JComboBox` porque dependen de `Vector` y
  `DefaultListModel`, prohibidos por la consigna. Las listas en pantalla se
  arman con `PanelFilaPokemon`.
