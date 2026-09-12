package batallapokemon.datos;

import java.io.File;

/**
 * Rutas y utilidades de la cache en disco.
 * OJO: son rutas RELATIVAS a la raiz del repo. La configuracion de ejecucion
 * de IntelliJ tiene el "Working directory" en $PROJECT_DIR$, que es lo que
 * hace que estas rutas resuelvan bien.
 */
public class CacheArchivos {

    public static final String DIR_JSON    = "datos/pokemon";
    public static final String DIR_SPRITES = "datos/sprites";

    /** El JSON se cachea por NOMBRE, porque es lo unico que se sabe antes de pedirlo. */
    public static String rutaJson(String nombre) {
        return DIR_JSON + "/" + nombre.trim().toLowerCase() + ".json";
    }

    // Los sprites se cachean por id. Se prefiere el gif animado; si ese
    // Pokemon no tiene animacion en gen-V, queda el png estatico.
    public static String rutaGifFrente(int id)     { return DIR_SPRITES + "/" + id + ".gif"; }
    public static String rutaGifEspalda(int id)    { return DIR_SPRITES + "/" + id + "_back.gif"; }
    public static String rutaPngFrente(int id)     { return DIR_SPRITES + "/" + id + ".png"; }
    public static String rutaPngEspalda(int id)    { return DIR_SPRITES + "/" + id + "_back.png"; }

    /** Sprite de frente que exista en disco (gif animado primero). */
    public static String spriteFrente(int id) {
        if (existe(rutaGifFrente(id))) return rutaGifFrente(id);
        if (existe(rutaPngFrente(id))) return rutaPngFrente(id);
        return rutaGifFrente(id);          // todavia no descargado
    }

    /** Sprite de espalda que exista en disco (gif animado primero). */
    public static String spriteEspalda(int id) {
        if (existe(rutaGifEspalda(id))) return rutaGifEspalda(id);
        if (existe(rutaPngEspalda(id))) return rutaPngEspalda(id);
        return rutaGifEspalda(id);
    }

    public static boolean existe(String ruta) {
        if (ruta == null) return false;
        File f = new File(ruta);
        return f.exists() && f.length() > 0;
    }

    public static void asegurarDirectorios() {
        new File(DIR_JSON).mkdirs();
        new File(DIR_SPRITES).mkdirs();
    }
}
