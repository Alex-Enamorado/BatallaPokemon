package batallapokemon;

import batallapokemon.datos.CacheArchivos;
import batallapokemon.datos.ProveedorCompuesto;
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
 * En IntelliJ el "Working directory" de la configuracion de ejecucion tiene
 * que ser la raiz del repo ($PROJECT_DIR$), porque las rutas de datos/ son
 * relativas.
 */
public class Main {

    public static void main(String[] args) {
        CacheArchivos.asegurarDirectorios();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // si falla, se usa el look and feel por defecto
        }

        // Local primero (los 16 offline, instantaneos), PokeAPI despues para
        // cualquier otro nombre. ProveedorPokeApi ya cae solo en el local si
        // no hay internet.
        ProveedorPokemon proveedor = new ProveedorCompuesto(
                new ProveedorLocal(), new ProveedorPokeApi());

        GestorUsuarios gestor = new GestorUsuarios(proveedor);
        gestor.cargar();

        SwingUtilities.invokeLater(() ->
            new VentanaLogin(gestor, proveedor).setVisible(true));
    }
}
