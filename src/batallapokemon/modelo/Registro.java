package batallapokemon.modelo;

public class Registro {
    private int turno;
    private String texto;

    public Registro(int turno, String texto) {
        this.turno = turno;
        this.texto = texto;
    }

    public int getTurno()   { return turno; }
    public String getTexto(){ return texto; }

    @Override
    public String toString() { return "Turno " + turno + ": " + texto; }
}
