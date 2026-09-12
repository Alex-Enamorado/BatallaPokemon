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
 *  CARRIL DEV 2  -  MOTOR DE COMBATE
 * ===========================================================================
 * Reglas de este carril:
 *  - Esta clase NO importa nada de javax.swing ni de batallapokemon.gui.
 *  - Cada accion devuelve un ResultadoTurno con las lineas ya redactadas.
 *  - Cada linea que se agrega al ResultadoTurno se agrega TAMBIEN al historial.
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

    public Entrenador getJugador() { return jugador; }
    public Entrenador getRival()   { return rival; }
    public ListaEnlazada<Registro> getHistorial() { return historial; }
    public Estadisticas getEstadisticas() { return estadisticas; }
    public int getTurno() { return turno; }
    public boolean batallaTerminada() { return terminada; }
    public boolean jugadorGano() { return gano; }

    /** Agrega la linea al resultado y al historial de una sola vez. */
    private void registrar(ResultadoTurno r, String linea) {
        r.agregarLinea(linea);
        historial.insertar(new Registro(turno, linea));
    }

    // ------------------------------------------------------- TODO  DEV 2

    /**
     * TODO: turno completo.
     *  1. Validar que la batalla no este terminada.
     *  2. Ataque del jugador: tomar el Ataque en 'indiceAtaque' del activo
     *     (jugador.getEquipo().getActivo().getAtaques().obtener(indiceAtaque)),
     *     calcular danio, aplicarlo con recibirDanio() y registrar las lineas:
     *        "Pikachu utilizo Impactrueno."
     *        "Gengar recibio 25 puntos de danio."
     *        "Gengar: 75/100 HP"
     *  3. Si el rival cayo: registrar "X fue derrotado", pasar al
     *     siguienteDisponible() del rival; si no queda ninguno -> victoria.
     *  4. Si la batalla sigue: ataque automatico del rival (mismo bloque,
     *     eligiendo un ataque al azar con azar.nextInt(...)).
     *  5. Si cae el Pokemon del jugador: cambio automatico al
     *     siguienteDisponible() y marcar r.setCambioForzado(true);
     *     si no queda ninguno -> derrota.
     *  6. Sumar los contadores de 'estadisticas' e incrementar 'turno'.
     */
    public ResultadoTurno atacar(int indiceAtaque) {
        ResultadoTurno r = new ResultadoTurno();
        r.invalidar("TODO Dev 2: implementar atacar()");
        return r;
    }

    /**
     * TODO: formula de danio.
     *   base = ((2 * nivel / 5 + 2) * potencia * ataque / defensa) / 50 + 2
     *   mult = TablaTipos.multiplicador(ataque.getTipo(), defensor.getTipo())
     *   variacion = 0.85 + azar.nextDouble() * 0.15
     *   resultado = max(1, (int)(base * mult * variacion))
     * Si mult == 0 el danio debe ser 0 (no 1).
     */
    public int calcularDanio(Pokemon atacante, Pokemon defensor, Ataque ataque) {
        return 0;
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
        r.invalidar("TODO Dev 2: implementar cambiarPokemon()");
        return r;
    }

    /**
     * TODO: usar un objeto del inventario sobre un Pokemon del equipo.
     *  - Buscar el objeto con jugador.buscarObjeto(nombreObjeto) y validar
     *    que disponible() sea true.
     *  - Si esRevivir(): solo sobre un Pokemon derrotado (usar revivir()).
     *    Si no: solo sobre un Pokemon con vida (usar curar()).
     *  - Descontar con objeto.descontar(), registrar la linea
     *    "Pikachu utilizo una Pocion." y dejar que el rival ataque.
     */
    public ResultadoTurno usarObjeto(String nombreObjeto, String nombrePokemonDestino) {
        ResultadoTurno r = new ResultadoTurno();
        r.invalidar("TODO Dev 2: implementar usarObjeto()");
        return r;
    }

    /**
     * TODO: dejar todo como al inicio.
     *  - Curar a full los dos equipos, activo = primero, historial.vaciar(),
     *    estadisticas.reiniciar(), turno = 1, terminada = false.
     */
    public void reiniciar() {
    }
}
