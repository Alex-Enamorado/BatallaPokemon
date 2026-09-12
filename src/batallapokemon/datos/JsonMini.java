package batallapokemon.datos;

/**
 * ===========================================================================
 *  CARRIL DEV 4  -  LECTOR DE JSON MINIMO
 * ===========================================================================
 * No usamos Gson/Jackson: solo necesitamos 5 campos del JSON de PokeAPI,
 * asi que alcanza con indexOf/substring. Cero dependencias externas.
 *
 * Limitacion conocida y aceptada: no soporta comillas escapadas (\") dentro
 * de un valor de texto. Los nombres de PokeAPI nunca las tienen.
 */
public class JsonMini {

    /**
     * Posicion donde empieza el VALOR de "clave", buscando desde 'desde'.
     * Devuelve -1 si no encuentra la clave.
     */
    private static int posicionValor(String json, String clave, int desde) {
        if (json == null || clave == null) return -1;
        if (desde < 0) desde = 0;

        String patron = "\"" + clave + "\"";
        int p = json.indexOf(patron, desde);
        if (p < 0) return -1;

        int i = p + patron.length();
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
        if (i >= json.length() || json.charAt(i) != ':') return -1;   // era otra clave
        i++;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
        return i;
    }

    /** Valor numerico de "clave": 123   (-1 si no esta). */
    public static int numero(String json, String clave, int desde) {
        int i = posicionValor(json, clave, desde);
        if (i < 0) return -1;

        int fin = i;
        if (fin < json.length() && json.charAt(fin) == '-') fin++;
        while (fin < json.length() && Character.isDigit(json.charAt(fin))) fin++;
        if (fin == i) return -1;

        return Integer.parseInt(json.substring(i, fin));
    }

    /** Valor de texto de "clave": "valor"   (null si no esta). */
    public static String texto(String json, String clave, int desde) {
        int i = posicionValor(json, clave, desde);
        if (i < 0 || i >= json.length() || json.charAt(i) != '"') return null;

        i++;
        int fin = json.indexOf('"', i);
        if (fin < 0) return null;
        return json.substring(i, fin);
    }

    /**
     * Tipo principal: el primer "name" que aparece despues de "types".
     *   "types":[{"slot":1,"type":{"name":"electric", ...
     */
    public static String tipoPrincipal(String json) {
        if (json == null) return null;
        int p = json.indexOf("\"types\"");
        if (p < 0) return null;
        return texto(json, "name", p);
    }

    /**
     * Stat base por nombre: "hp", "attack", "defense", "speed".
     *   "stats":[{"base_stat":35,"effort":0,"stat":{"name":"hp", ...
     * En cada elemento "base_stat" viene ANTES que su "name", asi que se
     * recorren los "base_stat" y se compara con el "name" que le sigue.
     */
    public static int statBase(String json, String nombreStat) {
        if (json == null || nombreStat == null) return -1;
        int inicio = json.indexOf("\"stats\"");
        if (inicio < 0) return -1;

        int i = inicio;
        while (true) {
            int p = json.indexOf("\"base_stat\"", i);
            if (p < 0) return -1;

            int valor = numero(json, "base_stat", p);
            String nombre = texto(json, "name", p);
            if (nombre != null && nombre.equalsIgnoreCase(nombreStat)) return valor;

            i = p + "\"base_stat\"".length();
        }
    }

    /**
     * Los primeros 'cantidad' nombres de movimiento, ya presentables
     * ("thunder-shock" -> "Thunder shock").
     *   "moves":[{"move":{"name":"mega-punch", ...
     * Se ancla en la clave exacta "move" (no confundir con
     * "move_learn_method", que no matchea porque incluye el guion bajo).
     */
    public static String[] primerosMovimientos(String json, int cantidad) {
        if (json == null || cantidad <= 0) return new String[0];
        int inicio = json.indexOf("\"moves\"");
        if (inicio < 0) return new String[0];

        String[] encontrados = new String[cantidad];
        int total = 0;
        int i = inicio + "\"moves\"".length();

        while (total < cantidad) {
            int p = json.indexOf("\"move\"", i);
            if (p < 0) break;
            String nombre = texto(json, "name", p);
            if (nombre != null) encontrados[total++] = capitalizar(nombre.replace('-', ' '));
            i = p + "\"move\"".length();
        }

        if (total == cantidad) return encontrados;
        String[] exacto = new String[total];          // copia manual: sin java.util
        for (int k = 0; k < total; k++) exacto[k] = encontrados[k];
        return exacto;
    }

    /** "thunder shock" -> "Thunder shock" */
    public static String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
