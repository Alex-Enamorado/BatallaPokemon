package batallapokemon.estructuras;

import batallapokemon.modelo.Pokemon;
import batallapokemon.modelo.Tipo;

/**
 * ===========================================================================
 *  CARRIL DEV 1  -  NUCLEO DEL PROYECTO
 * ===========================================================================
 * Lista enlazada simple del equipo de un entrenador.
 *
 * Reglas:
 *  - Nadie fuera de este paquete ve un NodoPokemon (encapsulamiento).
 *  - Cuando un Pokemon es derrotado el nodo NO se elimina: solo cambia
 *    el puntero 'activo'.
 *
 * Lo que ya esta implementado (plomeria basica, igual que ListaEnlazada):
 *    insertar, contar, obtener, recorrer
 * Lo que falta implementar (TODO Dev 1):
 *    buscar, eliminar, contarDisponibles, getActivo, setActivo,
 *    siguienteDisponible, modificar, todosDerrotados, moverAlPrimerLugar
 */
public class ListaPokemon {
    private NodoPokemon cabeza;
    private NodoPokemon activo;
    private int tamano;

    public ListaPokemon() {
        this.cabeza = null;
        this.activo = null;
        this.tamano = 0;
    }

    // ---------------------------------------------------------------- LISTO

    /** Inserta al final. El primer Pokemon insertado queda como activo. */
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

    /** Cantidad total de Pokemon del equipo (incluye derrotados). */
    public int contar() { return tamano; }

    /** Acceso por indice para que la GUI pueda iterar sin ver los nodos. */
    public Pokemon obtener(int indice) {
        if (indice < 0 || indice >= tamano) return null;
        NodoPokemon actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getPokemon();
    }

    /** Recorre la lista: "Pikachu -> Charizard -> null". */
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

    public boolean estaVacia() { return cabeza == null; }
    
    public Pokemon buscar(String nombre) {
        NodoPokemon actual = cabeza;
        while(actual != null){
            if(acutal.getPokemon().getNombre().equalsIgnoreCase(nombre)){
                return actual.getPokemon();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * TODO: eliminar el nodo cuyo Pokemon se llame 'nombre'.
     * Casos a cubrir: lista vacia, es la cabeza, esta en el medio/final,
     * y que pasa si el nodo eliminado era el activo (reasignar activo).
     * Acordarse de decrementar 'tamano'.
     */
    public boolean eliminar(String nombre) {
        return false;
    }

    /** TODO: contar los Pokemon con HP > 0 (recorrido + contador). */
    public int contarDisponibles() {
        return 0;
    }

    /** TODO: devolver el Pokemon del nodo 'activo' (o null si no hay). */
    public Pokemon getActivo() {
        return null;
    }

    /**
     * TODO: buscar el nodo con ese nombre y dejarlo como activo.
     * Debe devolver false (y NO cambiar nada) si el Pokemon esta derrotado
     * o si no existe. Requisito de la consigna: no se puede seleccionar
     * un Pokemon derrotado.
     */
    public boolean setActivo(String nombre) {
        return false;
    }

    /**
     * TODO: devolver el primer Pokemon de la lista con HP > 0 que no sea
     * el activo actual. Se usa para continuar la batalla automaticamente
     * cuando el activo es derrotado. Devuelve null si no queda ninguno.
     */
    public Pokemon siguienteDisponible() {
        return null;
    }

    /**
     * TODO: buscar por nombre y actualizar nivel / hpMax / tipo.
     * Usar los setters de Pokemon. Devolver false si no existe.
     */
    public boolean modificar(String nombre, int nivel, int hpMax, Tipo tipo) {
        return false;
    }

    /** TODO: true si TODOS los Pokemon del equipo estan derrotados. */
    public boolean todosDerrotados() {
        return false;
    }

    /**
     * TODO (RETO ADICIONAL - 4 integrantes):
     * Mover el Pokemon 'nombre' al primer lugar de la lista.
     *   Antes:   Pikachu -> Charizard -> Bulbasaur -> Squirtle
     *   Despues: Bulbasaur -> Pikachu -> Charizard -> Squirtle
     * Se debe hacer SOLO reenlazando referencias: hay que guardar el nodo
     * ANTERIOR al que se mueve, saltearlo (anterior.setSiguiente(nodo.getSiguiente()))
     * y poner el nodo como nueva cabeza. Prohibido usar otra estructura de datos.
     * Ojo: si ya es la cabeza, devolver true sin tocar nada.
     */
    public boolean moverAlPrimerLugar(String nombre) {
        return false;
    }
}
