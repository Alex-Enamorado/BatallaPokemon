package batallapokemon.datos;

import batallapokemon.modelo.Ataque;
import batallapokemon.modelo.Pokemon;
import batallapokemon.modelo.Tipo;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Proveedor que trae los Pokemon de PokeAPI.
 *
 * Estrategia (para que el demo en clase nunca se rompa):
 *   1. Si ya existe datos/pokemon/<nombre>.json -> se usa el de la cache.
 *   2. Si no, se pide a la API y SE GUARDA en la cache.
 *   3. Si la red falla o tarda mas de TIMEOUT_MS -> se delega en ProveedorLocal.
 *
 * obtener() NUNCA lanza excepcion: en el peor caso devuelve el respaldo local
 * (o null si el Pokemon tampoco esta ahi).
 *
 * IMPORTANTE: obtener() hace I/O de red. Llamarlo SIEMPRE desde un
 * SwingWorker, nunca desde el hilo de eventos de Swing.
 */
public class ProveedorPokeApi implements ProveedorPokemon {

    public static final String API = "https://pokeapi.co/api/v2/pokemon/";
    public static final String SPRITES =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/";
    /** Sprites animados de 5ta gen: Swing los anima solo con new ImageIcon(). */
    public static final String SPRITES_ANIM =
        SPRITES + "versions/generation-v/black-white/animated/";

    public static final int TIMEOUT_MS = 3000;
    /** La API no tiene "nivel": se lo asigna el juego. */
    public static final int NIVEL_POR_DEFECTO = 16;
    private static final int ATAQUES = 3;
    private static final int[] POTENCIAS = { 45, 65, 85 };

    private final ProveedorLocal respaldo = new ProveedorLocal();
    private final HttpClient cliente = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(TIMEOUT_MS))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Override
    public String[] nombresDisponibles() {
        // La API tiene 1300+; para la ventana Agregar alcanza con los locales
        // mas lo que el usuario escriba a mano.
        return respaldo.nombresDisponibles();
    }

    @Override
    public Pokemon obtener(String nombre) {
        if (nombre == null) return null;
        String clave = nombre.trim().toLowerCase();
        if (clave.isEmpty()) return null;

        try {
            String json = leerCache(clave);
            if (json == null) {
                json = descargarJson(clave);
                if (json != null) guardarCache(clave, json);
            }
            if (json == null) return respaldo.obtener(clave);

            Pokemon p = armar(clave, json);
            return p != null ? p : respaldo.obtener(clave);
        } catch (Exception e) {
            // Sin internet, JSON raro, disco lleno: el juego sigue andando.
            return respaldo.obtener(clave);
        }
    }

    /** Construye el Pokemon a partir del JSON de la API. */
    private Pokemon armar(String clave, String json) {
        int id = JsonMini.numero(json, "id", 0);
        if (id <= 0) return null;

        String nombreApi = JsonMini.texto(json, "name", 0);
        String nombre = JsonMini.capitalizar(nombreApi == null ? clave : nombreApi);
        Tipo tipo = Tipo.desdeApi(JsonMini.tipoPrincipal(json));

        int baseHp  = JsonMini.statBase(json, "hp");
        int ataque  = JsonMini.statBase(json, "attack");
        int defensa = JsonMini.statBase(json, "defense");
        if (baseHp  <= 0) baseHp  = 50;      // por si la API cambia el formato
        if (ataque  <= 0) ataque  = 50;
        if (defensa <= 0) defensa = 50;

        // Misma formula que ProveedorLocal: los dos proveedores tienen que
        // dar Pokemon comparables o la batalla queda desbalanceada.
        int nivel = NIVEL_POR_DEFECTO;
        int hpMax = baseHp + nivel * 2 + 15;

        Pokemon p = new Pokemon(nombre, id, nivel, tipo, hpMax, ataque, defensa);

        descargarSprites(id);
        p.setSpriteFrente(CacheArchivos.spriteFrente(id));
        p.setSpriteEspalda(CacheArchivos.spriteEspalda(id));

        // La POTENCIA no se pide a la API (serian 3 requests extra por Pokemon):
        // el nombre sale de la API y la potencia de una tabla fija por slot.
        String[] movimientos = JsonMini.primerosMovimientos(json, ATAQUES);
        for (int i = 0; i < movimientos.length; i++) {
            p.agregarAtaque(new Ataque(movimientos[i], tipo, POTENCIAS[i]));
        }
        p.agregarAtaque(new Ataque("Placaje", Tipo.NORMAL, 35));
        return p;
    }

    // ------------------------------------------------------------- CACHE

    private String leerCache(String nombre) {
        String ruta = CacheArchivos.rutaJson(nombre);
        if (!CacheArchivos.existe(ruta)) return null;
        try {
            return Files.readString(Path.of(ruta), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    private void guardarCache(String nombre, String json) {
        try {
            CacheArchivos.asegurarDirectorios();
            Files.writeString(Path.of(CacheArchivos.rutaJson(nombre)), json,
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            // si no se puede cachear no importa: la proxima se vuelve a pedir
        }
    }

    // ------------------------------------------------------------- RED

    /** GET a la API. Devuelve el body si el status es 200, null si no. */
    public String descargarJson(String nombre) {
        try {
            HttpRequest peticion = HttpRequest.newBuilder(URI.create(API + nombre))
                    .timeout(Duration.ofMillis(TIMEOUT_MS))
                    .GET()
                    .build();
            HttpResponse<String> respuesta =
                    cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            return respuesta.statusCode() == 200 ? respuesta.body() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Baja los sprites de frente y espalda a la cache, solo si faltan.
     * Primero prueba el gif animado de gen-V; si ese Pokemon no tiene
     * animacion, cae en el png estatico.
     */
    public void descargarSprites(int id) {
        CacheArchivos.asegurarDirectorios();
        bajarSprite(SPRITES_ANIM + id + ".gif",
                    SPRITES + id + ".png",
                    CacheArchivos.rutaGifFrente(id),
                    CacheArchivos.rutaPngFrente(id));
        bajarSprite(SPRITES_ANIM + "back/" + id + ".gif",
                    SPRITES + "back/" + id + ".png",
                    CacheArchivos.rutaGifEspalda(id),
                    CacheArchivos.rutaPngEspalda(id));
    }

    private void bajarSprite(String urlGif, String urlPng, String destinoGif, String destinoPng) {
        if (CacheArchivos.existe(destinoGif) || CacheArchivos.existe(destinoPng)) return;
        if (bajarArchivo(urlGif, destinoGif)) return;
        bajarArchivo(urlPng, destinoPng);
    }

    private boolean bajarArchivo(String url, String destino) {
        try {
            HttpRequest peticion = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofMillis(TIMEOUT_MS))
                    .GET()
                    .build();
            HttpResponse<byte[]> respuesta =
                    cliente.send(peticion, HttpResponse.BodyHandlers.ofByteArray());
            if (respuesta.statusCode() != 200 || respuesta.body().length == 0) return false;
            Files.write(Path.of(destino), respuesta.body());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
