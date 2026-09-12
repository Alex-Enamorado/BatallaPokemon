package batallapokemon.datos;

import java.io.File;

/**
 * Rutas y utilidades de la cache en disco.
 * OJO: son rutas RELATIVAS a la raiz del repo. En IntelliJ, el
 * "Working directory" de la configuracion de ejecucion debe ser la raiz
 * del proyecto ($PROJECT_DIR$, que es el valor por defecto).
 */
public class CacheArchivos {

    public static final String DIR_JSON    = "datos/pokemon";
    public static final String DIR_SPRITES = "datos/sprites";

    public static String rutaJson(int id) {
        return DIR_JSON + "/" + id + ".json";
    }

    public static String rutaSpriteFrente(int id) {
        return DIR_SPRITES + "/" + id + ".gif";
    }

    public static String rutaSpriteEspalda(int id) {
        return DIR_SPRITES + "/" + id + "_back.gif";
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
