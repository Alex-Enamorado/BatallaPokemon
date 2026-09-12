package batallapokemon.logica;

import batallapokemon.datos.ProveedorPokemon;
import batallapokemon.estructuras.ListaEnlazada;
import batallapokemon.estructuras.ListaPokemon;
import batallapokemon.modelo.Entrenador;
import batallapokemon.modelo.Pokemon;
import batallapokemon.modelo.Usuario;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * ===========================================================================
 *  CARRIL DEV 4  -  USUARIOS
 * ===========================================================================
 * Carga y persiste los usuarios en datos/usuarios.txt
 * Formato de cada linea:  usuario;password;pokemon1,pokemon2,pokemon3,pokemon4
 * Las lineas vacias y las que empiezan con '#' se ignoran.
 *
 * El proveedor que se le pasa deberia ser ProveedorLocal: el roster de 10
 * usuarios usa Pokemon que ya existen offline, asi que el arranque del juego
 * es instantaneo y no dispara 40 pedidos HTTP.
 */
public class GestorUsuarios {

    public static final String ARCHIVO = "datos/usuarios.txt";
    public static final int MAX_EQUIPO = 6;

    private final ListaEnlazada<Usuario> usuarios;
    private final ProveedorPokemon proveedor;
    private final Random azar;

    public GestorUsuarios(ProveedorPokemon proveedor) {
        this.usuarios = new ListaEnlazada<Usuario>();
        this.proveedor = proveedor;
        this.azar = new Random();
    }

    public ListaEnlazada<Usuario> getUsuarios() { return usuarios; }

    /** Lee el archivo. Si no existe, la lista queda vacia (sin explotar). */
    public void cargar() {
        usuarios.vaciar();
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return;

        try (BufferedReader lector = Files.newBufferedReader(
                Path.of(ARCHIVO), StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;

                Usuario u = interpretarLinea(linea);
                if (u != null) usuarios.insertar(u);
            }
        } catch (IOException e) {
            // Archivo ilegible: se arranca sin roster. El boton "usuario
            // aleatorio" va a avisar que no hay usuarios cargados.
        }
    }

    /** "ash;pikachu123;pikachu,charizard" -> Usuario con su equipo armado. */
    private Usuario interpretarLinea(String linea) {
        String[] partes = linea.split(";");
        if (partes.length < 2) return null;

        String nombre = partes[0].trim();
        String password = partes[1].trim();
        if (nombre.isEmpty()) return null;

        Entrenador entrenador = new Entrenador(nombre);
        entrenador.cargarInventarioInicial();

        if (partes.length >= 3 && !partes[2].trim().isEmpty()) {
            String[] nombresPokemon = partes[2].split(",");
            for (String nombrePokemon : nombresPokemon) {
                Pokemon p = proveedor.obtener(nombrePokemon.trim());
                if (p != null) entrenador.getEquipo().insertar(p);
            }
        }
        return new Usuario(nombre, password, entrenador);
    }

    /** Busca el usuario y valida la password. Devuelve null si no coincide. */
    public Usuario login(String nombre, String password) {
        if (nombre == null || password == null) return null;
        Usuario u = buscarUsuario(nombre);
        if (u == null) return null;
        return u.passwordCorrecta(password) ? u : null;
    }

    /** Busqueda por nombre, sin distinguir mayusculas. */
    public Usuario buscarUsuario(String nombre) {
        if (nombre == null) return null;
        String buscado = nombre.trim();
        for (int i = 0; i < usuarios.contar(); i++) {
            Usuario u = usuarios.obtener(i);
            if (u.getNombre().equalsIgnoreCase(buscado)) return u;
        }
        return null;
    }

    /**
     * Crea un usuario con equipo vacio (lo arma desde la VentanaEquipo).
     * Devuelve null si el nombre ya existe o si falta algun dato.
     */
    public Usuario crear(String nombre, String password) {
        if (nombre == null || password == null) return null;
        String limpio = nombre.trim();
        if (limpio.isEmpty() || password.isEmpty()) return null;
        if (limpio.contains(";") || limpio.contains(",")) return null;  // romperia el archivo
        if (buscarUsuario(limpio) != null) return null;

        Entrenador entrenador = new Entrenador(limpio);
        entrenador.cargarInventarioInicial();
        Usuario nuevo = new Usuario(limpio, password, entrenador);
        usuarios.insertar(nuevo);
        guardar();
        return nuevo;
    }

    /** Reescribe el archivo completo desde la lista. */
    public void guardar() {
        new File("datos").mkdirs();
        try (PrintWriter escritor = new PrintWriter(ARCHIVO, StandardCharsets.UTF_8)) {
            escritor.println("# formato: usuario;password;pokemon1,pokemon2,pokemon3,pokemon4");
            escritor.println("# archivo generado por GestorUsuarios.guardar()");
            for (int i = 0; i < usuarios.contar(); i++) {
                Usuario u = usuarios.obtener(i);
                escritor.println(u.getNombre() + ";" + u.getPassword() + ";"
                               + nombresDelEquipo(u.getEntrenador().getEquipo()));
            }
        } catch (IOException e) {
            // no se pudo persistir: la partida en memoria sigue funcionando
        }
    }

    /** "pikachu,charizard,bulbasaur" a partir del equipo. */
    private String nombresDelEquipo(ListaPokemon equipo) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < equipo.contar(); i++) {
            if (i > 0) sb.append(",");
            sb.append(equipo.obtener(i).getNombre().toLowerCase());
        }
        return sb.toString();
    }

    /** Un usuario al azar del roster, o null si no hay ninguno cargado. */
    public Usuario usuarioAleatorio() {
        if (usuarios.contar() == 0) return null;
        return usuarios.obtener(azar.nextInt(usuarios.contar()));
    }

    /**
     * Entrenador rival para la maquina: se elige otro usuario del roster y se
     * le copia el equipo pidiendo INSTANCIAS NUEVAS al proveedor. Si se
     * devolviera su Entrenador tal cual, el rival arrancaria con el danio de
     * la partida anterior.
     */
    public Entrenador generarRival(Usuario jugador) {
        Usuario elegido = elegirRival(jugador);

        Entrenador rival = new Entrenador(elegido != null ? elegido.getNombre() : "Rival");
        rival.cargarInventarioInicial();

        if (elegido != null) {
            ListaPokemon plantilla = elegido.getEntrenador().getEquipo();
            for (int i = 0; i < plantilla.contar(); i++) {
                Pokemon fresco = proveedor.obtener(plantilla.obtener(i).getNombre());
                if (fresco != null) rival.getEquipo().insertar(fresco);
            }
        }

        // Sin roster (o roster con equipos vacios): rival de emergencia.
        if (rival.getEquipo().contar() == 0) {
            String[] respaldo = { "gengar", "onix", "arcanine" };
            for (String nombre : respaldo) {
                Pokemon p = proveedor.obtener(nombre);
                if (p != null) rival.getEquipo().insertar(p);
            }
        }
        return rival;
    }

    /** Otro usuario distinto del jugador, con equipo no vacio. */
    private Usuario elegirRival(Usuario jugador) {
        int total = usuarios.contar();
        if (total == 0) return null;

        int inicio = azar.nextInt(total);
        for (int k = 0; k < total; k++) {                 // recorrido circular
            Usuario candidato = usuarios.obtener((inicio + k) % total);
            boolean esElJugador = jugador != null
                    && candidato.getNombre().equalsIgnoreCase(jugador.getNombre());
            if (!esElJugador && candidato.getEntrenador().getEquipo().contar() > 0) {
                return candidato;
            }
        }
        return null;
    }
}
