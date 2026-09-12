package batallapokemon.datos;

import batallapokemon.modelo.Pokemon;

/**
 * ===========================================================================
 *  CARRIL DEV 4  -  POKEAPI
 * ===========================================================================
 * Estrategia (para que el demo en clase nunca se rompa):
 *   1. Si ya existe datos/pokemon/<id>.json -> se usa el de la cache.
 *   2. Si no, se pide a la API y SE GUARDA en la cache.
 *   3. Si la red falla o tarda mas de TIMEOUT_MS -> se delega en respaldo
 *      (ProveedorLocal). El metodo obtener() NUNCA debe lanzar excepcion.
 *
 * Toda descarga se llama desde un SwingWorker (nunca desde el hilo de la GUI).
 */
public class ProveedorPokeApi implements ProveedorPokemon {

    public static final String API = "https://pokeapi.co/api/v2/pokemon/";
    public static final String SPRITES =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";
    /** Sprites animados de 5ta gen: Swing los anima solo con new ImageIcon(). */
    public static final String SPRITES_ANIM =
        SPRITES + "versions/generation-v/black-white/animated/";
    public static final int TIMEOUT_MS = 3000;

    private ProveedorLocal respaldo = new ProveedorLocal();

    @Override
    public String[] nombresDisponibles() {
        // La API tiene 1300+; para la ventana Agregar alcanza con los locales
        // mas lo que el usuario escriba a mano.
        return respaldo.nombresDisponibles();
    }

    /**
     * TODO: 1) intentar leer la cache (CacheArchivos.rutaJson) buscando por
     * nombre; 2) si no esta, descargarJson(nombre) y guardarlo; 3) armar el
     * Pokemon con JsonMini (id, tipoPrincipal, statBase "hp"/"attack"/"defense",
     * primerosMovimientos); 4) descargarSprites(id); 5) ante CUALQUIER fallo,
     * devolver respaldo.obtener(nombre).
     */
    @Override
    public Pokemon obtener(String nombre) {
        return respaldo.obtener(nombre);
    }

    /**
     * TODO: GET a API + nombre con java.net.http.HttpClient:
     *   HttpClient c = HttpClient.newBuilder()
     *        .connectTimeout(Duration.ofMillis(TIMEOUT_MS)).build();
     *   HttpRequest r = HttpRequest.newBuilder(URI.create(API + nombre))
     *        .timeout(Duration.ofMillis(TIMEOUT_MS)).GET().build();
     *   devolver el body si el status es 200, si no null.
     */
    public String descargarJson(String nombre) {
        return null;
    }

    /**
     * TODO: bajar el gif animado de frente (SPRITES_ANIM + id + ".gif") y el
     * de espalda (SPRITES_ANIM + "back/" + id + ".gif") a las rutas de
     * CacheArchivos, solo si todavia no existen. Bajar los bytes con
     * HttpResponse.BodyHandlers.ofFile(...) o ofByteArray() + Files.write().
     * Si un sprite animado no existe (Pokemon nuevos), probar con
     * SPRITES + id + ".png".
     */
    public void descargarSprites(int id) {
    }
}
