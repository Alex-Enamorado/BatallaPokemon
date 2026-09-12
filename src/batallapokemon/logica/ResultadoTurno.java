package batallapokemon.logica;

import batallapokemon.estructuras.ListaEnlazada;

/**
 * Todo lo que la GUI necesita saber despues de una accion.
 * El motor arma las lineas de texto; la GUI solo las muestra.
 */
public class ResultadoTurno {
    private ListaEnlazada<String> lineas = new ListaEnlazada<String>();
    private boolean cambioForzado;     // el activo cayo y hubo que cambiarlo
    private boolean batallaTerminada;
    private boolean jugadorGano;
    private boolean accionInvalida;    // ej: elegir un Pokemon derrotado
    private String mensajeError;

    public void agregarLinea(String linea) { lineas.insertar(linea); }

    public ListaEnlazada<String> getLineas() { return lineas; }

    public boolean fueCambioForzado()  { return cambioForzado; }
    public boolean batallaTerminada()  { return batallaTerminada; }
    public boolean jugadorGano()       { return jugadorGano; }
    public boolean accionInvalida()    { return accionInvalida; }
    public String  getMensajeError()   { return mensajeError; }

    public void setCambioForzado(boolean v)    { this.cambioForzado = v; }
    public void setBatallaTerminada(boolean v) { this.batallaTerminada = v; }
    public void setJugadorGano(boolean v)      { this.jugadorGano = v; }

    public void invalidar(String mensaje) {
        this.accionInvalida = true;
        this.mensajeError = mensaje;
    }

    /** Las lineas concatenadas, listas para un JTextArea. */
    public String texto() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lineas.contar(); i++) {
            sb.append(lineas.obtener(i)).append("\n");
        }
        return sb.toString();
    }
}
