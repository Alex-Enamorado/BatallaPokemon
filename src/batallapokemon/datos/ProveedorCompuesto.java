package batallapokemon.datos;

import batallapokemon.modelo.Pokemon;

/**
 * Encadena varios proveedores y devuelve el primer Pokemon que alguno
 * reconozca.
 *
 * Se usa con (ProveedorLocal, ProveedorPokeApi) en ese orden: los 16 locales
 * responden al instante y sin tocar el disco, y cualquier otro nombre cae en
 * la API (que a su vez busca primero en la cache). Asi un equipo armado con
 * Pokemon de PokeAPI se puede reconstruir al volver a iniciar sesion.
 */
public class ProveedorCompuesto implements ProveedorPokemon {

    private final ProveedorPokemon[] fuentes;

    public ProveedorCompuesto(ProveedorPokemon... fuentes) {
        this.fuentes = fuentes;
    }

    @Override
    public Pokemon obtener(String nombre) {
        for (ProveedorPokemon fuente : fuentes) {
            Pokemon p = fuente.obtener(nombre);
            if (p != null) return p;
        }
        return null;
    }

    /** Los nombres sugeridos son los de la primera fuente (los locales). */
    @Override
    public String[] nombresDisponibles() {
        return fuentes.length == 0 ? new String[0] : fuentes[0].nombresDisponibles();
    }
}
