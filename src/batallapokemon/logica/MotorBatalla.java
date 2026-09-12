package batallapokemon.logica;

import batallapokemon.estructuras.ListaEnlazada;
import batallapokemon.estructuras.ListaPokemon;
import batallapokemon.modelo.Ataque;
import batallapokemon.modelo.Entrenador;
import batallapokemon.modelo.Objeto;
import batallapokemon.modelo.Pokemon;
import batallapokemon.modelo.Registro;
import java.util.Random;

public class MotorBatalla {
    private final Entrenador jugador;
    private final Entrenador rival;
    private final ListaEnlazada<Registro> historial;
    private final Estadisticas estadisticas;
    private final Random azar;
    private int turno;
    private boolean terminada;
    private boolean gano;

    public MotorBatalla(Entrenador jugador, Entrenador rival) {
        this.jugador = jugador;
        this.rival = rival;
        this.historial = new ListaEnlazada<Registro>();
        this.estadisticas = new Estadisticas();
        this.azar = new Random();
        this.turno = 1;
    }

    public Entrenador getJugador() { return jugador; }
    public Entrenador getRival()   { return rival; }
    public ListaEnlazada<Registro> getHistorial() { return historial; }
    public Estadisticas getEstadisticas() { return estadisticas; }
    public int getTurno() { return turno; }
    public boolean batallaTerminada() { return terminada; }
    public boolean jugadorGano() { return gano; }

    private void registrar(ResultadoTurno r, String linea) {
        r.agregarLinea(linea);
        historial.insertar(new Registro(turno, linea));
    }

    public ResultadoTurno atacar(int indiceAtaque) {
        ResultadoTurno r = new ResultadoTurno();
        if (terminada) {
            r.invalidar("La batalla ya termino.");
            return r;
        }

        Pokemon atacante = jugador.getEquipo().getActivo();
        Pokemon defensor = rival.getEquipo().getActivo();
        if (atacante == null || defensor == null) {
            r.invalidar("No hay Pokemon en combate.");
            return r;
        }

        Ataque ataque = atacante.getAtaques().obtener(indiceAtaque);
        if (ataque == null) {
            r.invalidar("Ese ataque no existe.");
            return r;
        }

        golpear(r, atacante, defensor, ataque, true);

        if (defensor.estaDerrotado()) {
            estadisticas.sumarRivalDerrotado();
            registrar(r, defensor.getNombre() + " fue derrotado.");

            Pokemon siguiente = rival.getEquipo().siguienteDisponible();
            if (siguiente == null) {
                terminar(r, true);
                return r;
            }
            rival.getEquipo().setActivo(siguiente.getNombre());
            registrar(r, rival.getNombre() + " envia a " + siguiente.getNombre() + "!");
        }

        turnoDelRival(r);
        return r;
    }

    public ResultadoTurno cambiarPokemon(String nombre) {
        ResultadoTurno r = new ResultadoTurno();
        if (terminada) {
            r.invalidar("La batalla ya termino.");
            return r;
        }

        Pokemon activo = jugador.getEquipo().getActivo();
        if (activo != null && activo.getNombre().equalsIgnoreCase(nombre)) {
            r.invalidar(activo.getNombre() + " ya esta en combate.");
            return r;
        }
        if (!jugador.getEquipo().setActivo(nombre)) {
            r.invalidar("No podes enviar a ese Pokemon: esta derrotado.");
            return r;
        }

        estadisticas.sumarCambio();
        registrar(r, "Adelante, " + jugador.getEquipo().getActivo().getNombre() + "!");
        turnoDelRival(r);
        return r;
    }

    public ResultadoTurno usarObjeto(String nombreObjeto, String nombrePokemonDestino) {
        ResultadoTurno r = new ResultadoTurno();
        if (terminada) {
            r.invalidar("La batalla ya termino.");
            return r;
        }

        Objeto objeto = jugador.buscarObjeto(nombreObjeto);
        if (objeto == null || !objeto.disponible()) {
            r.invalidar("No te queda ese objeto.");
            return r;
        }

        Pokemon destino = jugador.getEquipo().buscar(nombrePokemonDestino);
        if (destino == null) {
            r.invalidar("Ese Pokemon no esta en tu equipo.");
            return r;
        }

        if (objeto.esRevivir()) {
            if (!destino.revivir(objeto.getCuracion())) {
                r.invalidar(destino.getNombre() + " no esta derrotado.");
                return r;
            }
            registrar(r, destino.getNombre() + " revivio con " + destino.getEstadoHp() + " HP.");
        } else {
            if (destino.estaDerrotado()) {
                r.invalidar(destino.getNombre() + " esta derrotado: usa Revivir.");
                return r;
            }
            int recuperado = destino.curar(objeto.getCuracion());
            registrar(r, destino.getNombre() + " utilizo una " + objeto.getNombre() + ".");
            registrar(r, "Recupero " + recuperado + " HP (" + destino.getEstadoHp() + ").");
        }

        objeto.descontar();
        estadisticas.sumarObjeto();
        turnoDelRival(r);
        return r;
    }

    public void reiniciar() {
        curarEquipo(jugador.getEquipo());
        curarEquipo(rival.getEquipo());

        activarPrimero(jugador.getEquipo());
        activarPrimero(rival.getEquipo());

        historial.vaciar();
        estadisticas.reiniciar();
        turno = 1;
        terminada = false;
        gano = false;
    }

    public int calcularDanio(Pokemon atacante, Pokemon defensor, Ataque ataque) {
        double multiplicador = TablaTipos.multiplicador(ataque.getTipo(), defensor.getTipo());
        if (multiplicador == 0) return 0;

        double base = ((2.0 * atacante.getNivel() / 5.0 + 2) * ataque.getPotencia()
                * atacante.getAtaque() / defensor.getDefensa()) / 50.0 + 2;
        double variacion = 0.85 + azar.nextDouble() * 0.15;
        return Math.max(1, (int) (base * multiplicador * variacion));
    }

    private void golpear(ResultadoTurno r, Pokemon atacante, Pokemon defensor,
                         Ataque ataque, boolean esDelJugador) {
        int danio = defensor.recibirDanio(calcularDanio(atacante, defensor, ataque));

        registrar(r, atacante.getNombre() + " utilizo " + ataque.getNombre() + ".");
        String efecto = TablaTipos.mensajeEfectividad(
                TablaTipos.multiplicador(ataque.getTipo(), defensor.getTipo()));
        if (!efecto.isEmpty()) registrar(r, efecto);
        registrar(r, defensor.getNombre() + " recibio " + danio + " puntos de danio.");
        registrar(r, defensor.getNombre() + ": " + defensor.getEstadoHp() + " HP");

        if (esDelJugador) estadisticas.sumarDanioInfligido(danio);
        else estadisticas.sumarDanioRecibido(danio);
    }

    private void turnoDelRival(ResultadoTurno r) {
        Pokemon atacante = rival.getEquipo().getActivo();
        Pokemon defensor = jugador.getEquipo().getActivo();
        Ataque ataque = ataqueAlAzar(atacante);

        if (atacante == null || defensor == null || ataque == null) {
            cerrarTurno();
            return;
        }

        golpear(r, atacante, defensor, ataque, false);

        if (defensor.estaDerrotado()) {
            estadisticas.sumarPropioDerrotado();
            registrar(r, defensor.getNombre() + " fue derrotado.");

            Pokemon siguiente = jugador.getEquipo().siguienteDisponible();
            if (siguiente == null) {
                terminar(r, false);
                return;
            }
            jugador.getEquipo().setActivo(siguiente.getNombre());
            registrar(r, "Adelante, " + siguiente.getNombre() + "!");
            r.setCambioForzado(true);
        }

        cerrarTurno();
    }

    private Ataque ataqueAlAzar(Pokemon p) {
        if (p == null || p.getAtaques().contar() == 0) return null;
        return p.getAtaques().obtener(azar.nextInt(p.getAtaques().contar()));
    }

    private void terminar(ResultadoTurno r, boolean ganoJugador) {
        terminada = true;
        gano = ganoJugador;
        r.setBatallaTerminada(true);
        r.setJugadorGano(ganoJugador);
        registrar(r, ganoJugador
                ? "Ganaste la batalla!"
                : "Te quedaste sin Pokemon disponibles. Perdiste...");
        estadisticas.sumarTurno();
    }

    private void cerrarTurno() {
        estadisticas.sumarTurno();
        turno++;
    }

    private void curarEquipo(ListaPokemon equipo) {
        for (int i = 0; i < equipo.contar(); i++) {
            Pokemon p = equipo.obtener(i);
            if (p.estaDerrotado()) p.revivir(p.getHpMax());
            else p.curar(p.getHpMax());
        }
    }

    private void activarPrimero(ListaPokemon equipo) {
        if (equipo.contar() > 0) equipo.setActivo(equipo.obtener(0).getNombre());
    }
}
