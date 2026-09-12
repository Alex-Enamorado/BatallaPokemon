package batallapokemon.modelo;

public class Usuario {
    private String nombre;
    private String password;
    private Entrenador entrenador;

    public Usuario(String nombre, String password, Entrenador entrenador) {
        this.nombre = nombre;
        this.password = password;
        this.entrenador = entrenador;
    }

    public String getNombre() { return nombre; }
    public Entrenador getEntrenador() { return entrenador; }

    /** Necesario para reescribir datos/usuarios.txt en GestorUsuarios.guardar(). */
    public String getPassword() { return password; }

    public boolean passwordCorrecta(String intento) {
        return password.equals(intento);
    }

    @Override
    public String toString() { return nombre; }
}
