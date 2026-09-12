package batallapokemon.logica;

import batallapokemon.datos.ProveedorPokemon;
import batallapokemon.estructuras.ListaEnlazada;
import batallapokemon.modelo.Entrenador;
import batallapokemon.modelo.Pokemon;
import batallapokemon.modelo.Usuario;
import java.util.Random;

/**
 * ===========================================================================
 *  CARRIL DEV 4  -  USUARIOS
 * ===========================================================================
 * Carga y persiste los usuarios en datos/usuarios.txt
 * Formato de cada linea:  usuario;password;pokemon1,pokemon2,pokemon3,pokemon4
 * Las lineas que empiezan con '#' son comentarios.
 */
public class GestorUsuarios {

    public static final String ARCHIVO = "datos/usuarios.txt";

    private ListaEnlazada<Usuario> usuarios;
    private ProveedorPokemon proveedor;
    private Random azar;

    public GestorUsuarios(ProveedorPokemon proveedor) {
        this.usuarios = new ListaEnlazada<Usuario>();
        this.proveedor = proveedor;
        this.azar = new Random();
    }

    public ListaEnlazada<Usuario> getUsuarios() { return usuarios; }

    /**
     * TODO: leer ARCHIVO con BufferedReader linea por linea.
     * Saltear vacias y las que empiezan con '#'. Partir con split(";"),
     * los nombres de Pokemon con split(","), pedir cada Pokemon al
     * proveedor y armar el Entrenador con cargarInventarioInicial().
     * Si el archivo no existe, dejar la lista vacia (sin explotar).
     */
    public void cargar() {
    }

    /**
     * TODO: buscar el usuario por nombre y validar la password con
     * passwordCorrecta(). Devolver el Usuario o null.
     */
    public Usuario login(String nombre, String password) {
        return null;
    }

    /**
     * TODO: crear un usuario nuevo.
     *  - Rechazar (devolver null) si el nombre ya existe.
     *  - Crear el Entrenador con inventario inicial y equipo vacio
     *    (el usuario los agrega desde la VentanaEquipo).
     *  - Insertar en la lista y llamar a guardar().
     */
    public Usuario crear(String nombre, String password) {
        return null;
    }

    /** TODO: reescribir ARCHIVO completo desde la lista (PrintWriter). */
    public void guardar() {
    }

    /** TODO: devolver un usuario al azar del roster (azar.nextInt(contar())). */
    public Usuario usuarioAleatorio() {
        return null;
    }

    /**
     * TODO: armar el entrenador rival de la maquina: tomar un usuario
     * aleatorio distinto del jugador y devolver su Entrenador.
     * Si no hay ninguno, armar uno a mano con 3 Pokemon del proveedor.
     */
    public Entrenador generarRival(Usuario jugador) {
        return null;
    }
}
