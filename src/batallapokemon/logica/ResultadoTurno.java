package batallapokemon.logica;

import batallapokemon.estructuras.ListaEnlazada;

public class ResultadoTurno {
    private ListaEnlazada<String> lineas = new ListaEnlazada<String>();
    private boolean cambioForzado;
    private boolean batallaTerminada;
    private boolean jugadorGano;
    private boolean accionInvalida;
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

    public String texto() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lineas.contar(); i++) {
            sb.append(lineas.obtener(i)).append("\n");
        }
        return sb.toString();
    }
}
