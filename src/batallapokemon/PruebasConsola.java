package batallapokemon;

import batallapokemon.datos.ProveedorLocal;
import batallapokemon.datos.ProveedorPokemon;
import batallapokemon.estructuras.ListaPokemon;
import batallapokemon.modelo.Pokemon;
import batallapokemon.modelo.Tipo;

/**
 * Herramienta de DESARROLLO para probar las estructuras (Dev 1).
 * No es parte del juego: el juego se juega 100% por GUI, como pide la
 * consigna. Esto solo sirve para verificar la lista mientras se programa.
 *
 * Se corre como otra configuracion de ejecucion en IntelliJ.
 */
public class PruebasConsola {

    private static int ok = 0, fallo = 0;

    public static void main(String[] args) {
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

        verificar("buscar(\"squirtle\") lo encuentra", equipo.buscar("squirtle") != null);
        verificar("buscar(\"mewtwo\") devuelve null", equipo.buscar("mewtwo") == null);
        verificar("getActivo() es Pikachu",
                equipo.getActivo() != null && equipo.getActivo().getNombre().equals("Pikachu"));
        verificar("contarDisponibles() == 4", equipo.contarDisponibles() == 4);

        Pokemon pika = equipo.buscar("pikachu");
        if (pika == null) {
            verificar("buscar(\"pikachu\") para derrotarlo", false);
        } else {
            pika.recibirDanio(9999);
            verificar("Pikachu quedo derrotado", pika.estaDerrotado());
            verificar("contarDisponibles() == 3", equipo.contarDisponibles() == 3);
            verificar("setActivo sobre un derrotado devuelve false",
                    !equipo.setActivo("pikachu"));
            verificar("siguienteDisponible() no esta derrotado",
                    equipo.siguienteDisponible() != null
                    && !equipo.siguienteDisponible().estaDerrotado());
            verificar("el nodo derrotado NO se elimino", equipo.contar() == 4);
        }

        verificar("setActivo(\"charizard\") == true", equipo.setActivo("charizard"));
        verificar("modificar() cambia el nivel",
                equipo.modificar("bulbasaur", 30, 120, Tipo.PLANTA)
                && equipo.buscar("bulbasaur").getNivel() == 30);

        verificar("moverAlPrimerLugar(\"bulbasaur\")", equipo.moverAlPrimerLugar("bulbasaur"));
        verificar("Bulbasaur quedo primero",
                equipo.obtener(0) != null && equipo.obtener(0).getNombre().equals("Bulbasaur"));
        verificar("el tamano no cambio al mover", equipo.contar() == 4);

        verificar("eliminar(\"squirtle\") == true", equipo.eliminar("squirtle"));
        verificar("contar() == 3 tras eliminar", equipo.contar() == 3);
        verificar("eliminar inexistente == false", !equipo.eliminar("mewtwo"));
        verificar("todosDerrotados() == false", !equipo.todosDerrotados());

        System.out.println("\nLista final: " + equipo.recorrer());
        System.out.println("OK: " + ok + "   FALLARON: " + fallo);
    }

    private static void verificar(String descripcion, boolean condicion) {
        System.out.println((condicion ? "[ OK ] " : "[FALLA] ") + descripcion);
        if (condicion) ok++; else fallo++;
    }
}
