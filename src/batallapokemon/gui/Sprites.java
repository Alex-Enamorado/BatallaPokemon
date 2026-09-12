package batallapokemon.gui;

import batallapokemon.datos.CacheArchivos;
import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Sprites {
    public static ImageIcon cargar(String ruta) {
        if (!CacheArchivos.existe(ruta)) return null;
        return new ImageIcon(ruta);
    }

    public static JLabel etiquetaSprite(String ruta, String nombreAlternativo) {
        ImageIcon icono = cargar(ruta);
        JLabel etiqueta;
        if (icono != null) {
            etiqueta = new JLabel(icono);
        } else {
            etiqueta = new JLabel("[ " + nombreAlternativo + " ]", JLabel.CENTER);
            etiqueta.setFont(new Font("Monospaced", Font.BOLD, 14));
            etiqueta.setForeground(Color.GRAY);
        }
        etiqueta.setHorizontalAlignment(JLabel.CENTER);
        return etiqueta;
    }
}
