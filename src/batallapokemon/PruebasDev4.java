package batallapokemon;

import batallapokemon.datos.CacheArchivos;
import batallapokemon.datos.JsonMini;
import batallapokemon.datos.ProveedorLocal;
import batallapokemon.datos.ProveedorPokeApi;
import batallapokemon.logica.GestorUsuarios;
import batallapokemon.modelo.Entrenador;
import batallapokemon.modelo.Pokemon;
import batallapokemon.modelo.Usuario;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Pruebas del CARRIL DEV 4 (herramienta de desarrollo, no es parte del juego).
 * No depende de los metodos que todavia le faltan a Dev 1 en ListaPokemon:
 * solo usa insertar / contar / obtener, que ya estan.
 *
 * Hace una copia de seguridad de datos/usuarios.txt y la restaura al final,
 * asi las pruebas no ensucian el roster.
 */
public class PruebasDev4 {

    private static int ok = 0, fallo = 0;
    private static final Path ARCHIVO = Path.of(GestorUsuarios.ARCHIVO);
    private static final Path RESPALDO = Path.of(GestorUsuarios.ARCHIVO + ".bak");

    public static void main(String[] args) throws Exception {
        if (Files.exists(ARCHIVO)) {
            Files.copy(ARCHIVO, RESPALDO, StandardCopyOption.REPLACE_EXISTING);
        }
        try {
            probarJsonMini();
            probarGestorUsuarios();
            probarPokeApi();
        } finally {
            if (Files.exists(RESPALDO)) {
                Files.move(RESPALDO, ARCHIVO, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        System.out.println("\nOK: " + ok + "   FALLARON: " + fallo);
    }

    // ------------------------------------------------------------ JsonMini

    private static void probarJsonMini() {
        titulo("JsonMini");
        // Recorte con la misma forma que devuelve PokeAPI.
        String json = "{\"height\":4,\"id\":25,\"name\":\"pikachu\","
            + "\"moves\":[{\"move\":{\"name\":\"mega-punch\",\"url\":\"x\"},"
            + "\"version_group_details\":[{\"level_learned_at\":0,"
            + "\"move_learn_method\":{\"name\":\"machine\"}}]},"
            + "{\"move\":{\"name\":\"thunder-shock\",\"url\":\"x\"}}],"
            + "\"stats\":[{\"base_stat\":35,\"effort\":0,\"stat\":{\"name\":\"hp\"}},"
            + "{\"base_stat\":55,\"effort\":0,\"stat\":{\"name\":\"attack\"}},"
            + "{\"base_stat\":40,\"effort\":0,\"stat\":{\"name\":\"defense\"}}],"
            + "\"types\":[{\"slot\":1,\"type\":{\"name\":\"electric\",\"url\":\"x\"}}]}";

        verificar("numero(id) == 25", JsonMini.numero(json, "id", 0) == 25);
        verificar("texto(name) == pikachu", "pikachu".equals(JsonMini.texto(json, "name", 0)));
        verificar("tipoPrincipal() == electric",
                "electric".equals(JsonMini.tipoPrincipal(json)));
        verificar("statBase(hp) == 35",      JsonMini.statBase(json, "hp") == 35);
        verificar("statBase(attack) == 55",  JsonMini.statBase(json, "attack") == 55);
        verificar("statBase(defense) == 40", JsonMini.statBase(json, "defense") == 40);

        String[] movs = JsonMini.primerosMovimientos(json, 3);
        verificar("primerosMovimientos() encuentra 2 (hay 2, se pidieron 3)", movs.length == 2);
        verificar("no confunde move_learn_method con move",
                movs.length == 2 && "Mega punch".equals(movs[0])
                                 && "Thunder shock".equals(movs[1]));
        verificar("clave inexistente devuelve -1", JsonMini.numero(json, "nada", 0) == -1);
        verificar("json null no explota", JsonMini.texto(null, "name", 0) == null);
    }

    // ------------------------------------------------------ GestorUsuarios

    private static void probarGestorUsuarios() {
        titulo("GestorUsuarios");
        GestorUsuarios gestor = new GestorUsuarios(new ProveedorLocal());
        gestor.cargar();

        verificar("cargar() lee los 10 del roster", gestor.getUsuarios().contar() == 10);

        Usuario ash = gestor.buscarUsuario("ash");
        verificar("buscarUsuario(\"ash\") lo encuentra", ash != null);
        verificar("a ash le cargo 4 Pokemon",
                ash != null && ash.getEntrenador().getEquipo().contar() == 4);
        verificar("el primero de ash es Pikachu",
                ash != null && ash.getEntrenador().getEquipo().obtener(0)
                        .getNombre().equals("Pikachu"));
        verificar("ash tiene los 3 objetos del inventario",
                ash != null && ash.getEntrenador().getInventario().contar() == 3);

        verificar("login correcto", gestor.login("ash", "pikachu123") != null);
        verificar("login con password mala", gestor.login("ash", "cualquiera") == null);
        verificar("login de usuario inexistente", gestor.login("nadie", "x") == null);
        verificar("usuarioAleatorio() devuelve alguno", gestor.usuarioAleatorio() != null);

        verificar("crear() usuario nuevo", gestor.crear("zzztest", "clave") != null);
        verificar("crear() duplicado devuelve null", gestor.crear("zzztest", "clave") == null);
        verificar("crear() con ';' devuelve null", gestor.crear("a;b", "clave") == null);
        verificar("el usuario nuevo arranca con equipo vacio",
                gestor.buscarUsuario("zzztest").getEntrenador().getEquipo().contar() == 0);

        // guardar() + cargar() tiene que ser ida y vuelta sin perder nada
        GestorUsuarios recargado = new GestorUsuarios(new ProveedorLocal());
        recargado.cargar();
        verificar("guardar()/cargar(): persistio el usuario nuevo",
                recargado.buscarUsuario("zzztest") != null);
        verificar("guardar()/cargar(): ash sigue con sus 4 Pokemon",
                recargado.buscarUsuario("ash") != null
                && recargado.buscarUsuario("ash").getEntrenador().getEquipo().contar() == 4);

        Entrenador rival = gestor.generarRival(ash);
        verificar("generarRival() devuelve un equipo", rival != null && rival.getEquipo().contar() > 0);
        verificar("el rival no es el propio jugador",
                rival != null && !rival.getNombre().equalsIgnoreCase("ash"));
        verificar("el rival trae instancias NUEVAS (HP completo)",
                rival != null && rival.getEquipo().obtener(0).getHpActual()
                              == rival.getEquipo().obtener(0).getHpMax());
        if (rival != null && ash != null) {
            Pokemon delRival = rival.getEquipo().obtener(0);
            Usuario plantilla = gestor.buscarUsuario(rival.getNombre());
            verificar("el rival NO comparte objetos con el roster",
                    plantilla == null || delRival != plantilla.getEntrenador()
                            .getEquipo().obtener(0));
        }
    }

    // ----------------------------------------------------------- PokeAPI

    private static void probarPokeApi() {
        titulo("ProveedorPokeApi");
        ProveedorPokeApi api = new ProveedorPokeApi();

        // Pokemon que NO esta en ProveedorLocal: si aparece, vino de la API.
        Pokemon mewtwo = api.obtener("mewtwo");
        boolean hayRed = mewtwo != null && mewtwo.getIdApi() == 150;

        if (!hayRed) {
            System.out.println("[ -- ] sin red (o PokeAPI caida): se saltean las pruebas online");
            verificar("sin red, obtener() cae en el respaldo local y no explota",
                    api.obtener("pikachu") != null);
            return;
        }

        verificar("obtener(\"mewtwo\") trae id 150", mewtwo.getIdApi() == 150);
        verificar("nombre capitalizado", "Mewtwo".equals(mewtwo.getNombre()));
        verificar("tipo traducido a PSIQUICO",
                mewtwo.getTipo() == batallapokemon.modelo.Tipo.PSIQUICO);
        verificar("stats razonables", mewtwo.getAtaque() > 0 && mewtwo.getDefensa() > 0);
        verificar("hpMax > 0", mewtwo.getHpMax() > 0);
        verificar("tiene ataques cargados", mewtwo.getAtaques().contar() >= 1);
        verificar("el JSON quedo en cache",
                CacheArchivos.existe(CacheArchivos.rutaJson("mewtwo")));
        verificar("el sprite de frente quedo en cache",
                CacheArchivos.existe(mewtwo.getSpriteFrente()));
        verificar("el sprite de espalda quedo en cache",
                CacheArchivos.existe(mewtwo.getSpriteEspalda()));
        verificar("nombre inexistente devuelve null", api.obtener("pokemonqueNoExiste") == null);

        long inicio = System.currentTimeMillis();
        Pokemon otraVez = api.obtener("mewtwo");
        long ms = System.currentTimeMillis() - inicio;
        verificar("segunda llamada sale de la cache (" + ms + " ms)",
                otraVez != null && ms < 500);
    }

    private static void titulo(String texto) {
        System.out.println("\n--- " + texto + " ---");
    }

    private static void verificar(String descripcion, boolean condicion) {
        System.out.println((condicion ? "[ OK ] " : "[FALLA] ") + descripcion);
        if (condicion) ok++; else fallo++;
    }
}
