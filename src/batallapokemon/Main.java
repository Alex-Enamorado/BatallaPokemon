package batallapokemon;

import batallapokemon.datos.CacheArchivos;
import batallapokemon.datos.ProveedorLocal;
import batallapokemon.datos.ProveedorPokeApi;
import batallapokemon.datos.ProveedorPokemon;
import batallapokemon.gui.VentanaLogin;
import batallapokemon.logica.GestorUsuarios;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada del juego.
 *
 * IntelliJ: Run > Edit Configurations > Working directory debe ser la raiz
 * del repo ($PROJECT_DIR$, que es el valor por defecto), porque las rutas de
 * datos/ son relativas.
 */
public class Main {

    public static void main(String[] args) {
        CacheArchivos.asegurarDirectorios();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // si falla, se usa el look and feel por defecto: no es critico
        }

        // El roster de datos/usuarios.txt usa Pokemon que ya estan offline:
        // con ProveedorLocal el arranque es instantaneo (si usaramos la API
        // serian 40 pedidos HTTP antes de ver la primera ventana).
        ProveedorPokemon local = new ProveedorLocal();
        GestorUsuarios gestor = new GestorUsuarios(local);
        gestor.cargar();

        // Para AGREGAR Pokemon al equipo si vale la pena ir a PokeAPI:
        // trae cualquiera de los 1300, y cae solo en ProveedorLocal sin internet.
        ProveedorPokemon api = new ProveedorPokeApi();

        SwingUtilities.invokeLater(() ->
            new VentanaLogin(gestor, api).setVisible(true));
    }
}
