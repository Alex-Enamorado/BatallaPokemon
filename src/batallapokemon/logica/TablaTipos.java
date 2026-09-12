package batallapokemon.logica;

import batallapokemon.modelo.Tipo;

/**
 * Ventajas y desventajas de tipo.
 * Se usa una matriz double[18][18] indexada por Tipo.ordinal().
 * Los arreglos SI estan permitidos por la consigna (no son java.util).
 */
public class TablaTipos {

    private static final int N = Tipo.values().length;
    private static final double[][] TABLA = new double[N][N];

    static {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) TABLA[i][j] = 1.0;
        }
        def(Tipo.NORMAL,    0.5, Tipo.ROCA, Tipo.ACERO);
        def(Tipo.NORMAL,    0.0, Tipo.FANTASMA);
        def(Tipo.FUEGO,     2.0, Tipo.PLANTA, Tipo.HIELO, Tipo.BICHO, Tipo.ACERO);
        def(Tipo.FUEGO,     0.5, Tipo.FUEGO, Tipo.AGUA, Tipo.ROCA, Tipo.DRAGON);
        def(Tipo.AGUA,      2.0, Tipo.FUEGO, Tipo.TIERRA, Tipo.ROCA);
        def(Tipo.AGUA,      0.5, Tipo.AGUA, Tipo.PLANTA, Tipo.DRAGON);
        def(Tipo.PLANTA,    2.0, Tipo.AGUA, Tipo.TIERRA, Tipo.ROCA);
        def(Tipo.PLANTA,    0.5, Tipo.FUEGO, Tipo.PLANTA, Tipo.VENENO, Tipo.VOLADOR,
                                 Tipo.BICHO, Tipo.DRAGON, Tipo.ACERO);
        def(Tipo.ELECTRICO, 2.0, Tipo.AGUA, Tipo.VOLADOR);
        def(Tipo.ELECTRICO, 0.5, Tipo.ELECTRICO, Tipo.PLANTA, Tipo.DRAGON);
        def(Tipo.ELECTRICO, 0.0, Tipo.TIERRA);
        def(Tipo.HIELO,     2.0, Tipo.PLANTA, Tipo.TIERRA, Tipo.VOLADOR, Tipo.DRAGON);
        def(Tipo.HIELO,     0.5, Tipo.FUEGO, Tipo.AGUA, Tipo.HIELO, Tipo.ACERO);
        def(Tipo.LUCHA,     2.0, Tipo.NORMAL, Tipo.HIELO, Tipo.ROCA, Tipo.SINIESTRO, Tipo.ACERO);
        def(Tipo.LUCHA,     0.5, Tipo.VENENO, Tipo.VOLADOR, Tipo.PSIQUICO, Tipo.BICHO, Tipo.HADA);
        def(Tipo.LUCHA,     0.0, Tipo.FANTASMA);
        def(Tipo.VENENO,    2.0, Tipo.PLANTA, Tipo.HADA);
        def(Tipo.VENENO,    0.5, Tipo.VENENO, Tipo.TIERRA, Tipo.ROCA, Tipo.FANTASMA);
        def(Tipo.VENENO,    0.0, Tipo.ACERO);
        def(Tipo.TIERRA,    2.0, Tipo.FUEGO, Tipo.ELECTRICO, Tipo.VENENO, Tipo.ROCA, Tipo.ACERO);
        def(Tipo.TIERRA,    0.5, Tipo.PLANTA, Tipo.BICHO);
        def(Tipo.TIERRA,    0.0, Tipo.VOLADOR);
        def(Tipo.VOLADOR,   2.0, Tipo.PLANTA, Tipo.LUCHA, Tipo.BICHO);
        def(Tipo.VOLADOR,   0.5, Tipo.ELECTRICO, Tipo.ROCA, Tipo.ACERO);
        def(Tipo.PSIQUICO,  2.0, Tipo.LUCHA, Tipo.VENENO);
        def(Tipo.PSIQUICO,  0.5, Tipo.PSIQUICO, Tipo.ACERO);
        def(Tipo.PSIQUICO,  0.0, Tipo.SINIESTRO);
        def(Tipo.BICHO,     2.0, Tipo.PLANTA, Tipo.PSIQUICO, Tipo.SINIESTRO);
        def(Tipo.BICHO,     0.5, Tipo.FUEGO, Tipo.LUCHA, Tipo.VENENO, Tipo.VOLADOR,
                                 Tipo.FANTASMA, Tipo.ACERO, Tipo.HADA);
        def(Tipo.ROCA,      2.0, Tipo.FUEGO, Tipo.HIELO, Tipo.VOLADOR, Tipo.BICHO);
        def(Tipo.ROCA,      0.5, Tipo.LUCHA, Tipo.TIERRA, Tipo.ACERO);
        def(Tipo.FANTASMA,  2.0, Tipo.PSIQUICO, Tipo.FANTASMA);
        def(Tipo.FANTASMA,  0.5, Tipo.SINIESTRO);
        def(Tipo.FANTASMA,  0.0, Tipo.NORMAL);
        def(Tipo.DRAGON,    2.0, Tipo.DRAGON);
        def(Tipo.DRAGON,    0.5, Tipo.ACERO);
        def(Tipo.DRAGON,    0.0, Tipo.HADA);
        def(Tipo.SINIESTRO, 2.0, Tipo.PSIQUICO, Tipo.FANTASMA);
        def(Tipo.SINIESTRO, 0.5, Tipo.LUCHA, Tipo.SINIESTRO, Tipo.HADA);
        def(Tipo.ACERO,     2.0, Tipo.HIELO, Tipo.ROCA, Tipo.HADA);
        def(Tipo.ACERO,     0.5, Tipo.FUEGO, Tipo.AGUA, Tipo.ELECTRICO, Tipo.ACERO);
        def(Tipo.HADA,      2.0, Tipo.LUCHA, Tipo.DRAGON, Tipo.SINIESTRO);
        def(Tipo.HADA,      0.5, Tipo.FUEGO, Tipo.VENENO, Tipo.ACERO);
    }

    private static void def(Tipo atacante, double mult, Tipo... defensores) {
        for (Tipo d : defensores) TABLA[atacante.ordinal()][d.ordinal()] = mult;
    }

    /** Multiplicador de danio: 2.0, 1.0, 0.5 o 0.0. */
    public static double multiplicador(Tipo atacante, Tipo defensor) {
        if (atacante == null || defensor == null) return 1.0;
        return TABLA[atacante.ordinal()][defensor.ordinal()];
    }

    /** Mensaje para el historial segun la efectividad. */
    public static String mensajeEfectividad(double mult) {
        if (mult == 0.0) return "No afecta al objetivo.";
        if (mult >= 2.0) return "Es super efectivo!";
        if (mult <= 0.5) return "No es muy eficaz...";
        return "";
    }
}
