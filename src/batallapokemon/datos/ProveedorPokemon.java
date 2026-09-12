package batallapokemon.datos;

import batallapokemon.modelo.Pokemon;

public interface ProveedorPokemon {
    Pokemon obtener(String nombre);

    String[] nombresDisponibles();
}
