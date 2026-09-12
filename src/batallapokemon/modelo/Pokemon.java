package batallapokemon.modelo;

import batallapokemon.estructuras.ListaEnlazada;

/**
 * Criatura del juego. Los sprites se guardan como RUTA a un archivo local
 * (datos/sprites/...), nunca como URL: la GUI solo hace new ImageIcon(ruta).
 */
public class Pokemon {
    private String nombre;
    private int idApi;
    private int nivel;
    private Tipo tipo;
    private int hpMax;
    private int hpActual;
    private int ataque;
    private int defensa;
    private ListaEnlazada<Ataque> ataques;
    private String spriteFrente;
    private String spriteEspalda;

    public Pokemon(String nombre, int idApi, int nivel, Tipo tipo,
                   int hpMax, int ataque, int defensa) {
        this.nombre = nombre;
        this.idApi = idApi;
        this.nivel = nivel;
        this.tipo = tipo;
        this.hpMax = hpMax;
        this.hpActual = hpMax;
        this.ataque = ataque;
        this.defensa = defensa;
        this.ataques = new ListaEnlazada<Ataque>();
    }

    public String getNombre()   { return nombre; }
    public int    getIdApi()    { return idApi; }
    public int    getNivel()    { return nivel; }
    public Tipo   getTipo()     { return tipo; }
    public int    getHpMax()    { return hpMax; }
    public int    getHpActual() { return hpActual; }
    public int    getAtaque()   { return ataque; }
    public int    getDefensa()  { return defensa; }
    public ListaEnlazada<Ataque> getAtaques() { return ataques; }
    public String getSpriteFrente()  { return spriteFrente; }
    public String getSpriteEspalda() { return spriteEspalda; }

    public void setNivel(int nivel) { this.nivel = nivel; }
    public void setTipo(Tipo tipo)  { this.tipo = tipo; }
    public void setSpriteFrente(String ruta)  { this.spriteFrente = ruta; }
    public void setSpriteEspalda(String ruta) { this.spriteEspalda = ruta; }

    public void setHpMax(int hpMax) {
        this.hpMax = hpMax;
        if (hpActual > hpMax) hpActual = hpMax;
    }

    public void agregarAtaque(Ataque a) { ataques.insertar(a); }

    /** Aplica danio sin bajar de 0. Devuelve el danio realmente aplicado. */
    public int recibirDanio(int danio) {
        if (danio < 0) danio = 0;
        int antes = hpActual;
        hpActual -= danio;
        if (hpActual < 0) hpActual = 0;
        return antes - hpActual;
    }

    /** Cura sin pasar de hpMax. Devuelve el HP realmente recuperado. */
    public int curar(int cantidad) {
        if (estaDerrotado()) return 0;
        int antes = hpActual;
        hpActual += cantidad;
        if (hpActual > hpMax) hpActual = hpMax;
        return hpActual - antes;
    }

    /** Revive a un Pokemon derrotado. Devuelve false si no estaba derrotado. */
    public boolean revivir(int hp) {
        if (!estaDerrotado()) return false;
        hpActual = Math.min(hp, hpMax);
        return true;
    }

    public boolean estaDerrotado() { return hpActual <= 0; }

    public String getEstadoHp() { return hpActual + "/" + hpMax; }

    @Override
    public String toString() {
        return nombre + " Nv." + nivel + " " + getEstadoHp()
             + (estaDerrotado() ? " DERROTADO" : "");
    }
}
