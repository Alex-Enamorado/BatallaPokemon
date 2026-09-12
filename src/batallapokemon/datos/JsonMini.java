package batallapokemon.datos;

/**
 * ===========================================================================
 *  CARRIL DEV 4  -  LECTOR DE JSON MINIMO
 * ===========================================================================
 * No usamos Gson/Jackson: solo necesitamos 5 campos del JSON de PokeAPI,
 * asi que alcanza con indexOf/substring. Cero dependencias externas.
 *
 * Del JSON de https://pokeapi.co/api/v2/pokemon/pikachu nos interesa:
 *   "id": 25
 *   "name": "pikachu"
 *   "types": [ { "slot":1, "type": { "name":"electric", ... } } ]
 *   "stats": [ { "base_stat":35, "stat": { "name":"hp" } }, ... ]
 *   "moves": [ { "move": { "name":"thunder-shock", ... } }, ... ]
 */
public class JsonMini {

    /**
     * TODO: devolver el valor numerico de "clave": 123
     * Pista: buscar "\"" + clave + "\":" con indexOf desde 'desde',
     * saltear espacios y leer los digitos. Devolver -1 si no esta.
     */
    public static int numero(String json, String clave, int desde) {
        return -1;
    }

    /**
     * TODO: devolver el valor de texto de "clave": "valor"
     * (la primera aparicion a partir de 'desde'), o null si no esta.
     */
    public static String texto(String json, String clave, int desde) {
        return null;
    }

    /**
     * TODO: el tipo principal. El primer "name" que aparece DESPUES de
     * "\"types\":" es el nombre del tipo (ej "electric").
     */
    public static String tipoPrincipal(String json) {
        return null;
    }

    /**
     * TODO: valor de una stat base por nombre ("hp", "attack", "defense").
     * En el JSON viene como  "base_stat": 35, ... "name": "hp"
     * Conviene buscar primero  "\"name\": \"hp\""  y desde ahi retroceder
     * hasta el "base_stat" anterior con lastIndexOf.
     */
    public static int statBase(String json, String nombreStat) {
        return -1;
    }

    /**
     * TODO: los primeros 'cantidad' nombres de movimiento.
     * Buscar "\"moves\":" y desde ahi ir tomando los "name" de a uno.
     * Devolver un arreglo de String (los arreglos SI estan permitidos).
     * Reemplazar los guiones por espacios para que se lea lindo.
     */
    public static String[] primerosMovimientos(String json, int cantidad) {
        return new String[0];
    }
}
