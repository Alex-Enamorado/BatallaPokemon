package batallapokemon;

import batallapokemon.datos.CacheArchivos;
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

        // ProveedorPokeApi cae solo en ProveedorLocal si no hay internet.
        ProveedorPokemon proveedor = new ProveedorPokeApi();

        GestorUsuarios gestor = new GestorUsuarios(proveedor);
        gestor.cargar();

        SwingUtilities.invokeLater(() ->
            new VentanaLogin(gestor, proveedor).setVisible(true));
    }
}
