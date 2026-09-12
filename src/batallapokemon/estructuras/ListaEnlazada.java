package batallapokemon.estructuras;

/**
 * Lista enlazada simple generica, implementada a mano (sin java.util).
 * Se usa para inventario, historial, ataques y usuarios.
 * El equipo de Pokemon usa la clase dedicada ListaPokemon.
 */
public class ListaEnlazada<T> {
    private Nodo<T> cabeza;
    private int tamano;

    public ListaEnlazada() {
        this.cabeza = null;
        this.tamano = 0;
    }

    /** Inserta al final de la lista. */
    public void insertar(T dato) {
        Nodo<T> nuevo = new Nodo<T>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
        tamano++;
    }

    /** Devuelve el dato en la posicion indicada, o null si el indice no existe. */
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamano) return null;
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    /** Elimina la primera aparicion del dato (comparado con equals). */
    public boolean eliminar(T dato) {
        if (cabeza == null) return false;
        if (cabeza.getDato().equals(dato)) {
            cabeza = cabeza.getSiguiente();
            tamano--;
            return true;
        }
        Nodo<T> actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getDato().equals(dato)) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamano--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public int contar() { return tamano; }

    public boolean estaVacia() { return cabeza == null; }

    public void vaciar() {
        cabeza = null;
        tamano = 0;
    }

    /** Recorre la lista y devuelve su contenido como texto. */
    public String recorrer() {
        StringBuilder sb = new StringBuilder();
        Nodo<T> actual = cabeza;
        while (actual != null) {
            sb.append(actual.getDato()).append(" -> ");
            actual = actual.getSiguiente();
        }
        sb.append("null");
        return sb.toString();
    }
}
