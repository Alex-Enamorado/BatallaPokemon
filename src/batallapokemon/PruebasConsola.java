package batallapokemon;

import batallapokemon.datos.CacheArchivos;
import batallapokemon.datos.JsonMini;
import batallapokemon.datos.ProveedorCompuesto;
import batallapokemon.datos.ProveedorLocal;
import batallapokemon.datos.ProveedorPokeApi;
import batallapokemon.datos.ProveedorPokemon;
import batallapokemon.estructuras.ListaPokemon;
import batallapokemon.logica.Estadisticas;
import batallapokemon.logica.GestorUsuarios;
import batallapokemon.logica.MotorBatalla;
import batallapokemon.logica.ResultadoTurno;
import batallapokemon.modelo.Ataque;
import batallapokemon.modelo.Entrenador;
import batallapokemon.modelo.Pokemon;
import batallapokemon.modelo.Tipo;
import batallapokemon.modelo.Usuario;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Pruebas de consola del proyecto: estructuras, datos/usuarios y motor de
 * batalla. Es una herramienta de DESARROLLO, no parte del juego (el juego se
 * juega 100% por GUI, como pide la consigna).
 *
 * Hace una copia de seguridad de datos/usuarios.txt y la restaura al final,
 * asi las pruebas no ensucian el roster.
 */
public class PruebasConsola {

    private static int ok = 0, fallo = 0;
    private static final Path ARCHIVO = Path.of(GestorUsuarios.ARCHIVO);
    private static final Path RESPALDO = Path.of(GestorUsuarios.ARCHIVO + ".bak");

    public static void main(String[] args) throws Exception {
        if (Files.exists(ARCHIVO)) {
            Files.copy(ARCHIVO, RESPALDO, StandardCopyOption.REPLACE_EXISTING);
        }
        try {
            probarListaPokemon();
            probarJsonMini();
            probarGestorUsuarios();
            probarMotorBatalla();
            probarPokeApi();
        } finally {
            if (Files.exists(RESPALDO)) {
                Files.move(RESPALDO, ARCHIVO, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        System.out.println("\n================================");
        System.out.println("OK: " + ok + "   FALLARON: " + fallo);
    }

    // -------------------------------------------------------- ESTRUCTURAS

    private static void probarListaPokemon() {
        titulo("ListaPokemon");
        ProveedorPokemon prov = new ProveedorLocal();
        ListaPokemon equipo = new ListaPokemon();

        equipo.insertar(prov.obtener("pikachu"));
        equipo.insertar(prov.obtener("charizard"));
        equipo.insertar(prov.obtener("bulbasaur"));
        equipo.insertar(prov.obtener("squirtle"));

        verificar("contar() == 4", equipo.contar() == 4);
        verificar("recorrer() arranca en Pikachu", equipo.recorrer().startsWith("Pikachu"));
        verificar("obtener(1) es Charizard",
                equipo.obtener(1) != null && equipo.obtener(1).getNombre().equals("Charizard"));
        verificar("obtener() fuera de rango devuelve null", equipo.obtener(99) == null);
        verificar("buscar(\"squirtle\") lo encuentra", equipo.buscar("squirtle") != null);
        verificar("buscar(\"mewtwo\") devuelve null", equipo.buscar("mewtwo") == null);
        verificar("getActivo() es Pikachu",
                equipo.getActivo() != null && equipo.getActivo().getNombre().equals("Pikachu"));
        verificar("contarDisponibles() == 4", equipo.contarDisponibles() == 4);

        Pokemon pika = equipo.buscar("pikachu");
        pika.recibirDanio(9999);
        verificar("Pikachu quedo derrotado", pika.estaDerrotado());
        verificar("contarDisponibles() == 3", equipo.contarDisponibles() == 3);
        verificar("setActivo sobre un derrotado devuelve false", !equipo.setActivo("pikachu"));
        verificar("siguienteDisponible() no esta derrotado",
                equipo.siguienteDisponible() != null
                && !equipo.siguienteDisponible().estaDerrotado());
        verificar("el nodo derrotado NO se elimino", equipo.contar() == 4);

        verificar("setActivo(\"charizard\") == true", equipo.setActivo("charizard"));
        verificar("modificar() cambia el nivel",
                equipo.modificar("bulbasaur", 30, 120, Tipo.PLANTA)
                && equipo.buscar("bulbasaur").getNivel() == 30);

        verificar("moverAlPrimerLugar(\"bulbasaur\")", equipo.moverAlPrimerLugar("bulbasaur"));
        verificar("Bulbasaur quedo primero",
                equipo.obtener(0) != null && equipo.obtener(0).getNombre().equals("Bulbasaur"));
        verificar("el tamano no cambio al mover", equipo.contar() == 4);
        verificar("mover el que ya es primero devuelve true",
                equipo.moverAlPrimerLugar("bulbasaur"));

        // El activo es Charizard; al eliminarlo el activo tiene que reapuntar
        verificar("eliminar el Pokemon activo", equipo.eliminar("charizard"));
        verificar("el activo dejo de ser un nodo suelto",
                equipo.getActivo() != null
                && equipo.buscar(equipo.getActivo().getNombre()) != null);

        verificar("eliminar(\"squirtle\") == true", equipo.eliminar("squirtle"));
        verificar("contar() == 2 tras dos eliminaciones", equipo.contar() == 2);
        verificar("eliminar inexistente == false", !equipo.eliminar("mewtwo"));
        verificar("todosDerrotados() == false", !equipo.todosDerrotados());
        System.out.println("       lista final: " + equipo.recorrer());
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
        verificar("tipoPrincipal() == electric", "electric".equals(JsonMini.tipoPrincipal(json)));
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

        verificar("cargar() lee los usuarios del roster", gestor.getUsuarios().contar() >= 10);

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

        GestorUsuarios recargado = new GestorUsuarios(new ProveedorLocal());
        recargado.cargar();
        verificar("guardar()/cargar(): persistio el usuario nuevo",
                recargado.buscarUsuario("zzztest") != null);
        verificar("guardar()/cargar(): ash sigue con sus 4 Pokemon",
                recargado.buscarUsuario("ash") != null
                && recargado.buscarUsuario("ash").getEntrenador().getEquipo().contar() == 4);

        Entrenador rival = gestor.generarRival(ash);
        verificar("generarRival() devuelve un equipo",
                rival != null && rival.getEquipo().contar() > 0);
        verificar("el rival no es el propio jugador",
                rival != null && !rival.getNombre().equalsIgnoreCase("ash"));
        verificar("el rival trae instancias NUEVAS (HP completo)",
                rival != null && rival.getEquipo().obtener(0).getHpActual()
                              == rival.getEquipo().obtener(0).getHpMax());
        verificar("el rival tiene su propio inventario",
                rival != null && rival.getInventario().contar() == 3);
    }

    // -------------------------------------------------------- MotorBatalla

    private static void probarMotorBatalla() {
        titulo("MotorBatalla");
        ProveedorPokemon prov = new ProveedorLocal();

        // --- un turno completo
        MotorBatalla motor = armarBatalla(prov, new String[]{"pikachu", "charizard"},
                                                new String[]{"onix", "gengar"});
        Pokemon miPika = motor.getJugador().getEquipo().getActivo();
        Pokemon suOnix = motor.getRival().getEquipo().getActivo();
        int hpRivalAntes = suOnix.getHpActual();
        int hpMioAntes = miPika.getHpActual();

        ResultadoTurno r = motor.atacar(0);
        verificar("atacar() no invalida un turno normal", !r.accionInvalida());
        // Regresion: el ataque del jugador tiene que pegarle al RIVAL
        verificar("el ataque le baja HP al rival", suOnix.getHpActual() < hpRivalAntes);
        verificar("el rival contraataca y baja HP al jugador",
                miPika.getHpActual() < hpMioAntes);
        verificar("el turno avanzo a 2", motor.getTurno() == 2);
        verificar("las lineas quedaron en el historial", motor.getHistorial().contar() > 0);
        verificar("el texto del resultado no esta vacio", !r.texto().isBlank());

        Estadisticas est = motor.getEstadisticas();
        verificar("estadisticas: 1 turno jugado", est.getTurnos() == 1);
        verificar("estadisticas: danio infligido > 0", est.getDanioInfligido() > 0);
        verificar("estadisticas: danio recibido > 0", est.getDanioRecibido() > 0);

        // --- cambios
        verificar("cambiar al que ya esta activo es invalido",
                motor.cambiarPokemon(miPika.getNombre()).accionInvalida());
        ResultadoTurno cambio = motor.cambiarPokemon("charizard");
        verificar("cambiar a uno disponible es valido", !cambio.accionInvalida());
        verificar("el activo ahora es Charizard",
                motor.getJugador().getEquipo().getActivo().getNombre().equals("Charizard"));
        verificar("estadisticas: 1 cambio", motor.getEstadisticas().getCambiosRealizados() == 1);

        // --- objetos
        Pokemon activo = motor.getJugador().getEquipo().getActivo();
        activo.recibirDanio(30);
        int antesDeCurar = activo.getHpActual();
        ResultadoTurno curacion = motor.usarObjeto("Pocion", activo.getNombre());
        verificar("usar Pocion es valido", !curacion.accionInvalida());
        verificar("la Pocion curo", activo.getHpActual() > antesDeCurar);
        verificar("estadisticas: 1 objeto usado", motor.getEstadisticas().getObjetosUsados() == 1);
        verificar("Revivir sobre uno con vida es invalido",
                motor.usarObjeto("Revivir", activo.getNombre()).accionInvalida());
        verificar("usar un objeto inexistente es invalido",
                motor.usarObjeto("Masterball", activo.getNombre()).accionInvalida());

        // --- el rival cae y entra el siguiente
        // Machamp (Lucha) contra Onix (Roca) es x2; ojo con elegir Fantasma
        // como rival, porque Lucha no lo afecta y el danio seria 0.
        MotorBatalla m2 = armarBatalla(prov, new String[]{"machamp"},
                                             new String[]{"onix", "gengar"});
        Pokemon onix = m2.getRival().getEquipo().getActivo();
        onix.recibirDanio(onix.getHpMax() - 1);
        m2.atacar(0);
        verificar("el rival derrotado deja paso al siguiente",
                m2.getRival().getEquipo().getActivo() != onix);
        verificar("estadisticas: 1 rival derrotado",
                m2.getEstadisticas().getRivalesDerrotados() == 1);
        verificar("el nodo del derrotado sigue en la lista",
                m2.getRival().getEquipo().contar() == 2);

        // --- victoria
        MotorBatalla m3 = armarBatalla(prov, new String[]{"machamp"}, new String[]{"onix"});
        Pokemon ultimo = m3.getRival().getEquipo().getActivo();
        ultimo.recibirDanio(ultimo.getHpMax() - 1);
        ResultadoTurno victoria = m3.atacar(0);
        verificar("victoria detectada", victoria.batallaTerminada() && victoria.jugadorGano());
        verificar("batallaTerminada() queda en true", m3.batallaTerminada());
        verificar("atacar despues de terminar es invalido", m3.atacar(0).accionInvalida());

        // --- derrota
        MotorBatalla m4 = armarBatalla(prov, new String[]{"pikachu"},
                                             new String[]{"onix", "machamp"});
        Pokemon miUltimo = m4.getJugador().getEquipo().getActivo();
        miUltimo.recibirDanio(miUltimo.getHpMax() - 1);
        ResultadoTurno derrota = m4.atacar(0);
        verificar("derrota detectada",
                derrota.batallaTerminada() && !derrota.jugadorGano());
        verificar("estadisticas: 1 propio derrotado",
                m4.getEstadisticas().getPropiosDerrotados() == 1);

        // --- reiniciar
        m4.reiniciar();
        verificar("reiniciar(): equipo curado",
                m4.getJugador().getEquipo().contarDisponibles()
                == m4.getJugador().getEquipo().contar());
        verificar("reiniciar(): turno 1", m4.getTurno() == 1);
        verificar("reiniciar(): historial vacio", m4.getHistorial().contar() == 0);
        verificar("reiniciar(): batalla no terminada", !m4.batallaTerminada());
        verificar("reiniciar(): estadisticas en cero",
                m4.getEstadisticas().getTurnos() == 0);

        // --- tipos
        Pokemon terrestre = new Pokemon("Prueba", 0, 20, Tipo.TIERRA, 100, 50, 50);
        Pokemon atacante = prov.obtener("pikachu");
        Ataque electrico = new Ataque("Rayo", Tipo.ELECTRICO, 90);
        verificar("Electrico no afecta a Tierra: danio 0",
                motor.calcularDanio(atacante, terrestre, electrico) == 0);
        Ataque normal = new Ataque("Placaje", Tipo.NORMAL, 40);
        verificar("un ataque que si afecta hace danio > 0",
                motor.calcularDanio(atacante, terrestre, normal) > 0);
    }

    /** Dos entrenadores con equipos e inventario, listos para pelear. */
    private static MotorBatalla armarBatalla(ProveedorPokemon prov,
                                             String[] equipoJugador, String[] equipoRival) {
        Entrenador jugador = new Entrenador("Jugador");
        jugador.cargarInventarioInicial();
        for (String nombre : equipoJugador) jugador.getEquipo().insertar(prov.obtener(nombre));

        Entrenador rival = new Entrenador("Rival");
        rival.cargarInventarioInicial();
        for (String nombre : equipoRival) rival.getEquipo().insertar(prov.obtener(nombre));

        return new MotorBatalla(jugador, rival);
    }

    // ------------------------------------------------------------- PokeAPI

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
        verificar("tipo traducido a PSIQUICO", mewtwo.getTipo() == Tipo.PSIQUICO);
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

        ProveedorPokemon compuesto = new ProveedorCompuesto(new ProveedorLocal(), api);
        verificar("ProveedorCompuesto resuelve un local", compuesto.obtener("pikachu") != null);
        verificar("ProveedorCompuesto resuelve uno de la API",
                compuesto.obtener("mewtwo") != null);
        verificar("ProveedorCompuesto con nombre falso devuelve null",
                compuesto.obtener("noexistepokemon") == null);
    }

    private static void titulo(String texto) {
        System.out.println("\n--- " + texto + " ---");
    }

    private static void verificar(String descripcion, boolean condicion) {
        System.out.println((condicion ? "[ OK ] " : "[FALLA] ") + descripcion);
        if (condicion) ok++; else fallo++;
    }
}
