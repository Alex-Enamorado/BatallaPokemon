package batallapokemon.gui;

import batallapokemon.modelo.Pokemon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;

/**
 * Una fila reutilizable: [sprite] Nombre  Nv.15  [====HP====]  Tipo  (DERROTADO)
 * La usan MI EQUIPO, SELECCIONAR POKEMON y la ventana de gestion del equipo.
 *
 * IMPORTANTE: no se usa JTable ni JList porque internamente dependen de
 * Vector / DefaultListModel, prohibidos por la consigna.
 */
public class PanelFilaPokemon extends JPanel {

    private Pokemon pokemon;
    private JRadioButton seleccion;

    public PanelFilaPokemon(Pokemon pokemon, boolean seleccionable) {
        this.pokemon = pokemon;
        setLayout(new BorderLayout(8, 0));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        setPreferredSize(new Dimension(520, 60));

        if (seleccionable) {
            seleccion = new JRadioButton();
            // No se puede elegir un Pokemon derrotado (requisito de la consigna)
            seleccion.setEnabled(!pokemon.estaDerrotado());
            add(seleccion, BorderLayout.WEST);
        }

        JLabel sprite = Sprites.etiquetaSprite(pokemon.getSpriteFrente(), pokemon.getNombre());
        sprite.setPreferredSize(new Dimension(60, 50));
        add(sprite, seleccionable ? BorderLayout.CENTER : BorderLayout.WEST);

        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        JLabel nombre = new JLabel(pokemon.getNombre());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 13));
        info.add(nombre);
        info.add(new JLabel("Nv. " + pokemon.getNivel()));
        info.add(new JLabel(pokemon.getTipo().getEtiqueta()));

        JProgressBar hp = new JProgressBar(0, pokemon.getHpMax());
        hp.setValue(pokemon.getHpActual());
        hp.setStringPainted(true);
        hp.setString(pokemon.getEstadoHp());
        hp.setPreferredSize(new Dimension(120, 16));
        hp.setForeground(colorHp(pokemon));
        info.add(hp);

        if (pokemon.estaDerrotado()) {
            JLabel ko = new JLabel("DERROTADO");
            ko.setForeground(Color.RED);
            ko.setFont(new Font("SansSerif", Font.BOLD, 12));
            info.add(ko);
        }
        add(info, BorderLayout.EAST);
    }

    private static Color colorHp(Pokemon p) {
        double ratio = (double) p.getHpActual() / p.getHpMax();
        if (ratio > 0.5)  return new Color(60, 170, 70);
        if (ratio > 0.2)  return new Color(230, 180, 40);
        return new Color(200, 60, 50);
    }

    public Pokemon getPokemon() { return pokemon; }

    public boolean estaSeleccionado() {
        return seleccion != null && seleccion.isSelected();
    }

    public JRadioButton getBotonSeleccion() { return seleccion; }
}
