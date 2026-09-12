package batallapokemon.logica;

import batallapokemon.estructuras.ListaEnlazada;
import batallapokemon.modelo.Ataque;
import batallapokemon.modelo.Entrenador;
import batallapokemon.modelo.Objeto;
import batallapokemon.modelo.Pokemon;
import batallapokemon.modelo.Registro;

import java.util.Random;

/**
 * ===========================================================================
 * CARRIL DEV 2  -  MOTOR DE COMBATE
 * ===========================================================================
 * Reglas de este carril:
 * - Esta clase NO importa nada de javax.swing ni de batallapokemon.gui.
 * - Cada accion devuelve un ResultadoTurno con las lineas ya redactadas.
 * - Cada linea que se agrega al ResultadoTurno se agrega TAMBIEN al historial.
 */
public class MotorBatalla {

    private Entrenador jugador;
    private Entrenador rival;
    private ListaEnlazada<Registro> historial;
    private Estadisticas estadisticas;
    private int turno;
    private boolean terminada;
    private boolean gano;
    private Random azar;

    public MotorBatalla(Entrenador jugador, Entrenador rival) {
        this.jugador = jugador;
        this.rival = rival;
        this.historial = new ListaEnlazada<Registro>();
        this.estadisticas = new Estadisticas();
        this.turno = 1;
        this.azar = new Random();
    }

    // ---------------------------------------------------------------- LISTO

    public Entrenador getJugador() {
        return jugador;
    }

    public Entrenador getRival() {
        return rival;
    }

    public ListaEnlazada<Registro> getHistorial() {
        return historial;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }

    public int getTurno() {
        return turno;
    }

    public boolean batallaTerminada() {
        return terminada;
    }

    public boolean jugadorGano() {
        return gano;
    }

    /**
     * Agrega la linea al resultado y al historial de una sola vez.
     */
    private void registrar(ResultadoTurno r, String linea) {
        r.agregarLinea(linea);
        historial.insertar(new Registro(turno, linea));
    }

    // ------------------------------------------------------- TODO  DEV 2

    public ResultadoTurno atacar(int indiceAtaque) {
        ResultadoTurno r = new ResultadoTurno();
        if (batallaTerminada()) {
            r.invalidar("La batalla ya termino");
            return r;
        }
        Pokemon activoJugador = jugador.getEquipo().getActivo();
        Pokemon activoRival = jugador.getEquipo().getActivo();
        Ataque ataqueJugador = activoJugador.getAtaques().obtener(indiceAtaque);
        int danioJugador = calcularDanio(activoJugador, activoRival, ataqueJugador);
        activoRival.recibirDanio(danioJugador);

        r.agregarLinea(activoJugador.getNombre() + " utilizo " + ataqueJugador.getNombre() + ".");
        r.agregarLinea(activoRival.getNombre() + " recibio " + danioJugador + " puntos de danio.");
        r.agregarLinea(activoRival.getNombre() + ": " + activoRival.getHpActual() + "/" + activoRival.getHpMax() + " HP");

        if (activoRival.estaDerrotado()) {
            registrar(r, activoRival.getNombre() + "fue derrotado.");
            Pokemon siguienteRival = rival.getEquipo().siguienteDisponible();
            if (siguienteRival == null) {
                terminada = true;
                gano = true;
                r.setBatallaTerminada(true);
                r.setJugadorGano(true);
                return r;

            }
            rival.getEquipo().setActivo(siguienteRival.getNombre());
            activoRival = siguienteRival;
        }

        Ataque ataqueRival = activoRival.getAtaques().obtener(azar.nextInt(activoRival.getAtaques().contar()));
        int danioRival = calcularDanio(activoRival, activoJugador, ataqueRival);
        activoJugador.recibirDanio(danioRival);

        registrar(r, activoRival.getNombre() + " utilizo " + ataqueRival.getNombre() + ".");
        registrar(r, activoJugador.getNombre() + " recibio " + danioRival + " puntos de danio.");
        registrar(r, activoJugador.getNombre() + ": " + activoJugador.getHpActual() + "/" + activoJugador.getHpMax() + " HP");

        if (activoJugador.estaDerrotado()) {
            registrar(r, activoJugador.getNombre() + " fue derrotado.");
            Pokemon siguienteJugador = jugador.getEquipo().siguienteDisponible();
            if (siguienteJugador == null) {
                terminada = true;
                gano = false;
                r.setBatallaTerminada(true);
                r.setJugadorGano(false);
            } else {
                jugador.getEquipo().setActivo(siguienteJugador.getNombre());
                r.setCambioForzado(true);
            }

        }

    }


    public int calcularDanio(Pokemon atacante, Pokemon defensor, Ataque ataque) {
        double mult = TablaTipos.multiplicador(ataque.getTipo(), defensor.getTipo());
        if (mult == 0) {
            return 0;
        }
        double base = ((2.0 * atacante.getNivel() / 5.0 + 2) * ataque.getPotencia()
                * atacante.getAtaque() / defensor.getDefensa()) / 50.0 + 2;
        double variacion = 0.85 + azar.nextDouble() * 0.15;
        int resultado = (int) (base * mult * variacion);
        return Math.max(1, resultado);
    }
    /**
     * TODO: cambiar el Pokemon activo del jugador.
     *  - Usar jugador.getEquipo().setActivo(nombre); si devuelve false,
     *    r.invalidar("Ese Pokemon esta derrotado") y NO gastar el turno.
     *  - Si el cambio fue valido: registrar "Adelante, X!" y dejar que el
     *    rival ataque (cambiar consume el turno).
     */
    public ResultadoTurno cambiarPokemon(String nombre) {
        ResultadoTurno r = new ResultadoTurno();

        if (batallaTerminada()) {
            r.invalidar("La batalla ya termino");
            return r;
        }

        boolean cambiado = jugador.getEquipo().setActivo(nombre);
        if (!cambiado) {
            r.invalidar("Ese Pokemon esta derrotado");
            return r;
        }

        registrar(r, "Adelante, " + nombre + "!");

        turnoDelRival(r);

        return r;
    }


    public ResultadoTurno usarObjeto(String nombreObjeto, String nombrePokemonDestino) {
        ResultadoTurno r = new ResultadoTurno();

        if (batallaTerminada()) {
            r.invalidar("La batalla ya termino");
            return r;
        }

        Objeto objeto = jugador.buscarObjeto(nombreObjeto);
        if (objeto == null || !objeto.disponible()) {
            r.invalidar("No tenes ese objeto disponible");
            return r;
        }

        Pokemon destino = jugador.getEquipo().buscar(nombrePokemonDestino);
        if (destino == null) {
            r.invalidar("Ese Pokemon no existe en tu equipo");
            return r;
        }

        if (objeto.esRevivir()) {
            boolean revivido = destino.revivir(objeto.getCuracion());
            if (!revivido) {
                r.invalidar("Ese Pokemon no esta derrotado");
                return r;
            }
        } else {
            if (destino.estaDerrotado()) {
                r.invalidar("Ese Pokemon esta derrotado, no se le puede curar");
                return r;
            }
            destino.curar(objeto.getCuracion());
        }

        objeto.descontar();
        registrar(r, destino.getNombre() + " utilizo una " + objeto.getNombre() + ".");

        turnoDelRival(r);

        return r;
    }

    private void turnoDelRival(ResultadoTurno r) {
        Pokemon activoJugador = jugador.getEquipo().getActivo();
        Pokemon activoRival = rival.getEquipo().getActivo();

        Ataque ataqueRival = activoRival.getAtaques().obtener(
                azar.nextInt(activoRival.getAtaques().contar()));
        int danioRival = calcularDanio(activoRival, activoJugador, ataqueRival);
        activoJugador.recibirDanio(danioRival);

        registrar(r, activoRival.getNombre() + " utilizo " + ataqueRival.getNombre() + ".");
        registrar(r, activoJugador.getNombre() + " recibio " + danioRival + " puntos de danio.");
        registrar(r, activoJugador.getNombre() + ": " + activoJugador.getEstadoHp() + " HP");

        if (activoJugador.estaDerrotado()) {
            registrar(r, activoJugador.getNombre() + " fue derrotado.");
            Pokemon siguienteJugador = jugador.getEquipo().siguienteDisponible();
            if (siguienteJugador == null) {
                terminada = true;
                gano = false;
                r.setBatallaTerminada(true);
                r.setJugadorGano(false);
            } else {
                jugador.getEquipo().setActivo(siguienteJugador.getNombre());
                r.setCambioForzado(true);
            }
        }

        actualizarEstadisticasYTurno();
    }



    public void reiniciar() {
        curarEquipoCompleto(jugador.getEquipo());
        curarEquipoCompleto(rival.getEquipo());

        jugador.getEquipo().setActivo(jugador.getEquipo().obtener(0).getNombre());
        rival.getEquipo().setActivo(rival.getEquipo().obtener(0).getNombre());

        historial.vaciar();
        estadisticas.reiniciar();
        turno = 1;
        terminada = false;
        gano = false;
    }


    private void curarEquipoCompleto(ListaPokemon equipo) {
        for (int i = 0; i < equipo.contar(); i++) {
            Pokemon p = equipo.obtener(i);
            if (p.estaDerrotado()) {
                p.revivir(p.getHpMax());
            } else {
                p.curar(p.getHpMax()); // curar() ya limita a hpMax, asi que esto lo deja full
            }
        }
    }

    private void curarEquipoCompleto(ListaPokemon equipo) {
        for (int i = 0; i < equipo.contar(); i++) {
            Pokemon p = equipo.obtener(i);
            p.curar(); // o revivir() + setHp(getHpMax()), segun tu clase Pokemon
        }
    }
}
