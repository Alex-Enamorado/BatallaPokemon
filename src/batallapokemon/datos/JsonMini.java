package batallapokemon.datos;

public class JsonMini {
    private static int posicionValor(String json, String clave, int desde) {
        if (json == null || clave == null) return -1;
        if (desde < 0) desde = 0;

        String patron = "\"" + clave + "\"";
        int p = json.indexOf(patron, desde);
        if (p < 0) return -1;

        int i = p + patron.length();
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
        if (i >= json.length() || json.charAt(i) != ':') return -1;
        i++;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
        return i;
    }

    public static int numero(String json, String clave, int desde) {
        int i = posicionValor(json, clave, desde);
        if (i < 0) return -1;

        int fin = i;
        if (fin < json.length() && json.charAt(fin) == '-') fin++;
        while (fin < json.length() && Character.isDigit(json.charAt(fin))) fin++;
        if (fin == i) return -1;

        return Integer.parseInt(json.substring(i, fin));
    }

    public static String texto(String json, String clave, int desde) {
        int i = posicionValor(json, clave, desde);
        if (i < 0 || i >= json.length() || json.charAt(i) != '"') return null;

        i++;
        int fin = json.indexOf('"', i);
        if (fin < 0) return null;
        return json.substring(i, fin);
    }

    public static String tipoPrincipal(String json) {
        if (json == null) return null;
        int p = json.indexOf("\"types\"");
        if (p < 0) return null;
        return texto(json, "name", p);
    }

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
        String[] exacto = new String[total];
        for (int k = 0; k < total; k++) exacto[k] = encontrados[k];
        return exacto;
    }

    public static String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
