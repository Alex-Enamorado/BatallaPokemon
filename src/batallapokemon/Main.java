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

public class Main {
    public static void main(String[] args) {
        CacheArchivos.asegurarDirectorios();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        ProveedorPokemon proveedor = new ProveedorCompuesto(
                new ProveedorLocal(), new ProveedorPokeApi());

        GestorUsuarios gestor = new GestorUsuarios(proveedor);
        gestor.cargar();

        SwingUtilities.invokeLater(() ->
            new VentanaLogin(gestor, proveedor).setVisible(true));
    }
}
