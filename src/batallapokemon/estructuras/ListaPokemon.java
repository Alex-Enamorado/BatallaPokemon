package batallapokemon.estructuras;

import batallapokemon.modelo.Pokemon;
import batallapokemon.modelo.Tipo;

public class ListaPokemon {
    private NodoPokemon cabeza;
    private NodoPokemon activo;
    private int tamano;

    public ListaPokemon() {
        this.cabeza = null;
        this.activo = null;
        this.tamano = 0;
    }

    public void insertar(Pokemon p) {
        if (p == null) return;
        NodoPokemon nuevo = new NodoPokemon(p);
        if (cabeza == null) {
            cabeza = nuevo;
            activo = nuevo;
        } else {
            NodoPokemon actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
        tamano++;
    }

    public int contar() { return tamano; }

    public Pokemon obtener(int indice) {
        if (indice < 0 || indice >= tamano) return null;
        NodoPokemon actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getPokemon();
    }

    public String recorrer() {
        StringBuilder sb = new StringBuilder();
        NodoPokemon actual = cabeza;
        while (actual != null) {
            sb.append(actual.getPokemon().getNombre());
            if (actual.getPokemon().estaDerrotado()) sb.append(" (X)");
            sb.append(" -> ");
            actual = actual.getSiguiente();
        }
        sb.append("null");
        return sb.toString();
    }

    public Pokemon buscar(String nombre) {
        NodoPokemon actual = cabeza;
        while(actual != null){
            if(actual.getPokemon().getNombre().equalsIgnoreCase(nombre)){
                return actual.getPokemon();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public boolean eliminar(String nombre) {
        if(cabeza == null) return false;
        if(cabeza.getPokemon().getNombre().equalsIgnoreCase(nombre)){
            NodoPokemon eliminado = cabeza;
            cabeza = cabeza.getSiguiente();
            tamano--;
            if(activo == eliminado){
                activo = cabeza;
            }
            return true;
        }
        NodoPokemon anterior = cabeza;
        NodoPokemon actual = cabeza.getSiguiente();
        while(actual != null){
            if(actual.getPokemon().getNombre().equalsIgnoreCase(nombre)){
                anterior.setSiguiente(actual.getSiguiente());
                tamano--;
                if (activo == actual) activo = cabeza;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    public int contarDisponibles() {
        int contador = 0;
        NodoPokemon actual = cabeza;
        while(actual != null){
            if(!actual.getPokemon().estaDerrotado()){
                contador++;
            }
            actual = actual.getSiguiente();
        }
        return contador;
    }

    public Pokemon getActivo() {
        return (activo != null) ? activo.getPokemon() : null;
    }

    public boolean setActivo(String nombre) {
        NodoPokemon actual = cabeza;
        while(actual != null){
            if(actual.getPokemon().getNombre().equalsIgnoreCase(nombre)){
                if(actual.getPokemon().estaDerrotado()){
                    return false;
                }
                activo = actual;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public Pokemon siguienteDisponible() {
        NodoPokemon actual = cabeza;
        while (actual != null) {
            boolean esElActivo = (actual == activo);
            if (!actual.getPokemon().estaDerrotado() && !esElActivo) {
                return actual.getPokemon();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public boolean modificar(String nombre, int nivel, int hpMax, Tipo tipo) {
        Pokemon p = buscar(nombre);
        if (p == null) return false;
        p.setNivel(nivel);
        p.setHpMax(hpMax);
        p.setTipo(tipo);
        return true;
    }

    public boolean todosDerrotados() {
        if (cabeza == null) return false;
        NodoPokemon actual = cabeza;
        while (actual != null) {
            if (!actual.getPokemon().estaDerrotado()) {
                return false;
            }
            actual = actual.getSiguiente();
        }
        return true;
    }

    public boolean moverAlPrimerLugar(String nombre) {
        if (cabeza == null) return false;
        if (cabeza.getPokemon().getNombre().equalsIgnoreCase(nombre)) {
            return true;
        }
        NodoPokemon anterior = cabeza;
        NodoPokemon actual = cabeza.getSiguiente();
        while (actual != null) {
            if (actual.getPokemon().getNombre().equalsIgnoreCase(nombre)) {
                anterior.setSiguiente(actual.getSiguiente());
                actual.setSiguiente(cabeza);
                cabeza = actual;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }
}
