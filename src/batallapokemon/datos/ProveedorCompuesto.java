package batallapokemon.datos;

import batallapokemon.modelo.Pokemon;

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

    @Override
    public String[] nombresDisponibles() {
        return fuentes.length == 0 ? new String[0] : fuentes[0].nombresDisponibles();
    }
}
