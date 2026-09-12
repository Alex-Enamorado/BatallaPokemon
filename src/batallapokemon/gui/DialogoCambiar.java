package batallapokemon.gui;

import batallapokemon.estructuras.ListaPokemon;
import batallapokemon.modelo.Pokemon;
import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * SELECCIONAR POKEMON. Los derrotados aparecen pero con el radio deshabilitado
 * (PanelFilaPokemon ya se encarga de eso).
 */
public class DialogoCambiar extends JDialog {

    private String seleccionado;

    public DialogoCambiar(Frame padre, ListaPokemon equipo) {
        this(padre, equipo, false);
    }

    /**
     * @param permitirDerrotados si es true, tambien se pueden elegir Pokemon
     *                           derrotados (lo usa DialogoObjetos para elegir
     *                           el destino de un objeto que revive).
     */
    public DialogoCambiar(Frame padre, ListaPokemon equipo, boolean permitirDerrotados) {
        super(padre, "SELECCIONAR POKEMON", true);
        setSize(600, 420);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(6, 6));

        JPanel filas = new JPanel();
        filas.setLayout(new BoxLayout(filas, BoxLayout.Y_AXIS));
        filas.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        ButtonGroup grupo = new ButtonGroup();
        PanelFilaPokemon[] paneles = new PanelFilaPokemon[equipo.contar()];

        // Se recorre el equipo SOLO con metodos publicos: nunca con NodoPokemon.
        for (int i = 0; i < equipo.contar(); i++) {
            Pokemon p = equipo.obtener(i);
            PanelFilaPokemon fila = new PanelFilaPokemon(p, true, permitirDerrotados);
            grupo.add(fila.getBotonSeleccion());
            paneles[i] = fila;
            filas.add(fila);
        }
        add(new JScrollPane(filas), BorderLayout.CENTER);

        JPanel sur = new JPanel();
        JButton cambiar = new JButton("CAMBIAR");
        JButton cerrar  = new JButton("CERRAR");
        cambiar.addActionListener(e -> {
            for (PanelFilaPokemon fila : paneles) {
                if (fila.estaSeleccionado()) {
                    seleccionado = fila.getPokemon().getNombre();
                    dispose();
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Elegi un Pokemon disponible.");
        });
        cerrar.addActionListener(e -> dispose());
        sur.add(cambiar);
        sur.add(cerrar);
        add(sur, BorderLayout.SOUTH);
    }

    /** Nombre elegido, o null si se cerro sin elegir. */
    public String getSeleccionado() { return seleccionado; }
}
