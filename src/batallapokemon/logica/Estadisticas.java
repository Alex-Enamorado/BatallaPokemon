package batallapokemon.logica;

/** Contadores de la batalla, para la ventana de estadisticas. */
public class Estadisticas {
    private int turnos;
    private int danioInfligido;
    private int danioRecibido;
    private int objetosUsados;
    private int cambiosRealizados;
    private int rivalesDerrotados;
    private int propiosDerrotados;

    public void sumarTurno()                { turnos++; }
    public void sumarDanioInfligido(int d)  { danioInfligido += d; }
    public void sumarDanioRecibido(int d)   { danioRecibido += d; }
    public void sumarObjeto()               { objetosUsados++; }
    public void sumarCambio()               { cambiosRealizados++; }
    public void sumarRivalDerrotado()       { rivalesDerrotados++; }
    public void sumarPropioDerrotado()      { propiosDerrotados++; }

    public void reiniciar() {
        turnos = 0; danioInfligido = 0; danioRecibido = 0; objetosUsados = 0;
        cambiosRealizados = 0; rivalesDerrotados = 0; propiosDerrotados = 0;
    }

    public int getTurnos() { return turnos; }

    /** Resumen listo para mostrar en la GUI. */
    public String resumen() {
        return "Turnos jugados: " + turnos
             + "\nDanio infligido: " + danioInfligido
             + "\nDanio recibido: " + danioRecibido
             + "\nObjetos usados: " + objetosUsados
             + "\nCambios de Pokemon: " + cambiosRealizados
             + "\nPokemon rivales derrotados: " + rivalesDerrotados
             + "\nPokemon propios derrotados: " + propiosDerrotados;
    }
}
