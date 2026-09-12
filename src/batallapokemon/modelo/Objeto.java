package batallapokemon.modelo;

public class Objeto {
    private String nombre;
    private String descripcion;
    private int curacion;
    private boolean revive;
    private int cantidad;

    public Objeto(String nombre, String descripcion, int curacion, boolean revive, int cantidad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.curacion = curacion;
        this.revive = revive;
        this.cantidad = cantidad;
    }

    public String  getNombre()      { return nombre; }
    public String  getDescripcion() { return descripcion; }
    public int     getCuracion()    { return curacion; }
    public boolean esRevivir()      { return revive; }
    public int     getCantidad()    { return cantidad; }

    public void descontar() { if (cantidad > 0) cantidad--; }
    public boolean disponible() { return cantidad > 0; }

    @Override
    public String toString() { return nombre + " x" + cantidad + " - " + descripcion; }
}
