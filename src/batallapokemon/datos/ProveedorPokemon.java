package batallapokemon.datos;

import batallapokemon.modelo.Pokemon;

/**
 * Fuente de Pokemon. La GUI SIEMPRE programa contra esta interfaz, nunca
 * contra PokeAPI directamente: asi nadie queda bloqueado esperando la API
 * y el juego funciona sin internet.
 */
public interface ProveedorPokemon {

    /** Devuelve un Pokemon nuevo (nivel por defecto) o null si no lo conoce. */
    Pokemon obtener(String nombre);

    /** Nombres que este proveedor puede entregar (para la ventana Agregar). */
    String[] nombresDisponibles();
}
