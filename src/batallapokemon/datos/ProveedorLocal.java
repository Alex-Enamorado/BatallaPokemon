package batallapokemon.datos;

import batallapokemon.modelo.Ataque;
import batallapokemon.modelo.Pokemon;
import batallapokemon.modelo.Tipo;

public class ProveedorLocal implements ProveedorPokemon {
    private static final String[] NOMBRES = {
        "pikachu", "charizard", "bulbasaur", "squirtle", "gengar", "blastoise",
        "venusaur", "alakazam", "machamp", "onix", "gyarados", "snorlax",
        "lapras", "arcanine", "dragonite", "pidgeot"
    };

    @Override
    public String[] nombresDisponibles() {
        return NOMBRES.clone();
    }

    @Override
    public Pokemon obtener(String nombre) {
        if (nombre == null) return null;
        switch (nombre.trim().toLowerCase()) {
            case "pikachu":   return crear("Pikachu",   25, 15, Tipo.ELECTRICO, 35,  55,  40);
            case "charizard": return crear("Charizard",  6, 18, Tipo.FUEGO,     78,  84,  78);
            case "bulbasaur": return crear("Bulbasaur",  1, 14, Tipo.PLANTA,    45,  49,  49);
            case "squirtle":  return crear("Squirtle",   7, 16, Tipo.AGUA,      44,  48,  65);
            case "gengar":    return crear("Gengar",    94, 15, Tipo.FANTASMA,  60,  65,  60);
            case "blastoise": return crear("Blastoise",  9, 18, Tipo.AGUA,      79,  83, 100);
            case "venusaur":  return crear("Venusaur",   3, 18, Tipo.PLANTA,    80,  82,  83);
            case "alakazam":  return crear("Alakazam",  65, 17, Tipo.PSIQUICO,  55,  50,  45);
            case "machamp":   return crear("Machamp",   68, 17, Tipo.LUCHA,     90, 130,  80);
            case "onix":      return crear("Onix",      95, 15, Tipo.ROCA,      35,  45, 160);
            case "gyarados":  return crear("Gyarados", 130, 19, Tipo.AGUA,      95, 125,  79);
            case "snorlax":   return crear("Snorlax",  143, 20, Tipo.NORMAL,   160, 110,  65);
            case "lapras":    return crear("Lapras",   131, 18, Tipo.AGUA,     130,  85,  80);
            case "arcanine":  return crear("Arcanine",  59, 18, Tipo.FUEGO,     90, 110,  80);
            case "dragonite": return crear("Dragonite",149, 20, Tipo.DRAGON,    91, 134,  95);
            case "pidgeot":   return crear("Pidgeot",   18, 16, Tipo.VOLADOR,   83,  80,  75);
            default:          return null;
        }
    }

    private Pokemon crear(String nombre, int id, int nivel, Tipo tipo,
                          int baseHp, int ataque, int defensa) {
        int hpMax = baseHp + nivel * 2 + 15;
        Pokemon p = new Pokemon(nombre, id, nivel, tipo, hpMax, ataque, defensa);
        p.setSpriteFrente(CacheArchivos.spriteFrente(id));
        p.setSpriteEspalda(CacheArchivos.spriteEspalda(id));
        agregarAtaques(p, tipo);
        return p;
    }

    private void agregarAtaques(Pokemon p, Tipo tipo) {
        switch (tipo) {
            case ELECTRICO:
                p.agregarAtaque(new Ataque("Impactrueno", Tipo.ELECTRICO, 40));
                p.agregarAtaque(new Ataque("Rayo",        Tipo.ELECTRICO, 90));
                break;
            case FUEGO:
                p.agregarAtaque(new Ataque("Ascuas",       Tipo.FUEGO, 40));
                p.agregarAtaque(new Ataque("Lanzallamas",  Tipo.FUEGO, 90));
                break;
            case AGUA:
                p.agregarAtaque(new Ataque("Pistola Agua", Tipo.AGUA, 40));
                p.agregarAtaque(new Ataque("Hidrobomba",   Tipo.AGUA, 90));
                break;
            case PLANTA:
                p.agregarAtaque(new Ataque("Latigo Cepa",  Tipo.PLANTA, 45));
                p.agregarAtaque(new Ataque("Hoja Afilada", Tipo.PLANTA, 85));
                break;
            case FANTASMA:
                p.agregarAtaque(new Ataque("Lenguetazo",   Tipo.FANTASMA, 30));
                p.agregarAtaque(new Ataque("Bola Sombra",  Tipo.FANTASMA, 80));
                break;
            case PSIQUICO:
                p.agregarAtaque(new Ataque("Confusion",    Tipo.PSIQUICO, 50));
                p.agregarAtaque(new Ataque("Psiquico",     Tipo.PSIQUICO, 90));
                break;
            case LUCHA:
                p.agregarAtaque(new Ataque("Golpe Karate", Tipo.LUCHA, 50));
                p.agregarAtaque(new Ataque("Sumision",     Tipo.LUCHA, 80));
                break;
            case ROCA:
                p.agregarAtaque(new Ataque("Lanzarrocas",  Tipo.ROCA, 50));
                p.agregarAtaque(new Ataque("Avalancha",    Tipo.ROCA, 75));
                break;
            case DRAGON:
                p.agregarAtaque(new Ataque("Furia Dragon", Tipo.DRAGON, 40));
                p.agregarAtaque(new Ataque("Garra Dragon", Tipo.DRAGON, 80));
                break;
            case VOLADOR:
                p.agregarAtaque(new Ataque("Tornado",      Tipo.VOLADOR, 40));
                p.agregarAtaque(new Ataque("Ala de Acero", Tipo.VOLADOR, 70));
                break;
            default:
                p.agregarAtaque(new Ataque("Ataque Rapido", Tipo.NORMAL, 40));
                p.agregarAtaque(new Ataque("Golpe Cuerpo",  Tipo.NORMAL, 85));
                break;
        }
        p.agregarAtaque(new Ataque("Placaje", Tipo.NORMAL, 35));
    }
}
