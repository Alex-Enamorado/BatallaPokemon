package batallapokemon.gui;

import batallapokemon.datos.CacheArchivos;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Carga de imagenes para la GUI.
 * Si la ruta no existe (todavia no se descargo el sprite) devuelve null y el
 * llamador muestra el nombre como texto: la ventana nunca queda rota.
 *
 * Los GIF animados de PokeAPI se animan solos con new ImageIcon(ruta):
 * no hay que hacer nada especial.
 */
public class Sprites {

    /** Devuelve el icono de la ruta, o null si el archivo no esta. */
    public static ImageIcon cargar(String ruta) {
        if (!CacheArchivos.existe(ruta)) return null;
        return new ImageIcon(ruta);
    }

    /**
     * Escala una imagen ESTATICA (png). Ojo: no usar con gif animados,
     * getScaledInstance() les rompe la animacion.
     */
    public static ImageIcon cargarEscalado(String ruta, int ancho, int alto) {
        ImageIcon icono = cargar(ruta);
        if (icono == null) return null;
        if (ruta.toLowerCase().endsWith(".gif")) return icono;
        Image img = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    /** JLabel con el sprite, o con el nombre en texto si no hay imagen. */
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
