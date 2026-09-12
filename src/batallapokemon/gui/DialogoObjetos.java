package batallapokemon.gui;

import batallapokemon.estructuras.ListaEnlazada;
import batallapokemon.modelo.Entrenador;
import batallapokemon.modelo.Objeto;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Ventana OBJETOS.
 *
 * Muestra el inventario (ListaEnlazada<Objeto>) con un radio por objeto.
 * Al elegir "UTILIZAR" abre DialogoCambiar para elegir el Pokemon destino
 * (si el objeto revive, ahi tambien se pueden elegir derrotados).
 */
public class DialogoObjetos extends JDialog {

    private String objetoElegido;
    private String destinoElegido;

    public DialogoObjetos(Frame padre, Entrenador entrenador) {
        super(padre, "OBJETOS", true);
        setSize(480, 320);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(6, 6));

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        ListaEnlazada<Objeto> inv = entrenador.getInventario();
        ButtonGroup grupo = new ButtonGroup();
        JRadioButton[] radios = new JRadioButton[inv.contar()];
        Objeto[] objetos = new Objeto[inv.contar()];

        for (int i = 0; i < inv.contar(); i++) {
            Objeto o = inv.obtener(i);
            objetos[i] = o;
            JPanel fila = new JPanel(new GridLayout(1, 2, 8, 0));
            JRadioButton radio = new JRadioButton(o.getNombre() + " x" + o.getCantidad());
            radio.setEnabled(o.disponible());
            grupo.add(radio);
            radios[i] = radio;
            fila.add(radio);
            fila.add(new JLabel(o.getDescripcion()));
            lista.add(fila);
        }
        add(lista, BorderLayout.CENTER);

        JPanel sur = new JPanel();
        JButton usar   = new JButton("UTILIZAR");
        JButton cerrar = new JButton("CERRAR");
        usar.addActionListener(e -> {
            for (int i = 0; i < radios.length; i++) {
                if (radios[i].isSelected()) {
                    Objeto o = objetos[i];
                    // Si el objeto revive, tambien se pueden elegir Pokemon derrotados.
                    DialogoCambiar destino = new DialogoCambiar(padre, entrenador.getEquipo(), o.esRevivir());
                    destino.setVisible(true);
                    if (destino.getSeleccionado() != null) {
                        objetoElegido = o.getNombre();
                        destinoElegido = destino.getSeleccionado();
                        dispose();
                    }
                    return;
                }
            }
            dispose();
        });
        cerrar.addActionListener(e -> dispose());
        sur.add(usar);
        sur.add(cerrar);
        add(sur, BorderLayout.SOUTH);
    }

    public String getObjetoElegido()  { return objetoElegido; }
    public String getDestinoElegido() { return destinoElegido; }
}
