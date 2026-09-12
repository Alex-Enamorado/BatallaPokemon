package batallapokemon.modelo;

import batallapokemon.estructuras.ListaEnlazada;
import batallapokemon.estructuras.ListaPokemon;

public class Entrenador {
    private String nombre;
    private ListaPokemon equipo;
    private ListaEnlazada<Objeto> inventario;

    public Entrenador(String nombre) {
        this.nombre = nombre;
        this.equipo = new ListaPokemon();
        this.inventario = new ListaEnlazada<Objeto>();
    }

    public String getNombre() { return nombre; }
    public ListaPokemon getEquipo() { return equipo; }
    public ListaEnlazada<Objeto> getInventario() { return inventario; }

    public void cargarInventarioInicial() {
        inventario.insertar(new Objeto("Pocion",      "Recupera 20 HP",    20, false, 3));
        inventario.insertar(new Objeto("Superpocion", "Recupera 50 HP",    50, false, 2));
        inventario.insertar(new Objeto("Revivir",     "Revive un Pokemon", 50, true,  1));
    }

    public Objeto buscarObjeto(String nombreObjeto) {
        for (int i = 0; i < inventario.contar(); i++) {
            Objeto o = inventario.obtener(i);
            if (o.getNombre().equalsIgnoreCase(nombreObjeto)) return o;
        }
        return null;
    }

    @Override
    public String toString() { return nombre; }
}
