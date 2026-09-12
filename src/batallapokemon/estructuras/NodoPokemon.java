package batallapokemon.estructuras;

import batallapokemon.modelo.Pokemon;

/**
 * Nodo de la lista enlazada del equipo. Encapsula un Pokemon.
 * IMPORTANTE: esta clase NO debe ser usada desde el paquete gui.
 * Toda operacion sobre el equipo pasa por los metodos publicos de ListaPokemon.
 */
public class NodoPokemon {
    private Pokemon pokemon;
    private NodoPokemon siguiente;

    public NodoPokemon(Pokemon pokemon) {
        this.pokemon = pokemon;
        this.siguiente = null;
    }

    public Pokemon getPokemon() { return pokemon; }
    public void setPokemon(Pokemon pokemon) { this.pokemon = pokemon; }

    public NodoPokemon getSiguiente() { return siguiente; }
    public void setSiguiente(NodoPokemon siguiente) { this.siguiente = siguiente; }
}
