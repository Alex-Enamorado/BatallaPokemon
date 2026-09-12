package batallapokemon.gui;

import batallapokemon.datos.ProveedorPokemon;
import batallapokemon.estructuras.ListaPokemon;
import batallapokemon.modelo.Entrenador;
import batallapokemon.modelo.Pokemon;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * ===========================================================================
 *  CARRIL DEV 4  -  MI EQUIPO (gestion)
 * ===========================================================================
 * En modo soloLectura solo muestra el estado (es la vista del boton MI EQUIPO
 * durante la batalla). En modo gestion habilita Agregar / Buscar / Eliminar /
 * Mover al primer lugar.
 *
 * REGLA: todo se hace con los metodos publicos de ListaPokemon.
 * Prohibido importar NodoPokemon aca.
 */
public class VentanaEquipo extends JFrame {

    private Entrenador entrenador;
    private ProveedorPokemon proveedor;
    private boolean soloLectura;

    private JPanel filas;
    private JLabel contador;

    public VentanaEquipo(Entrenador entrenador, ProveedorPokemon proveedor, boolean soloLectura) {
        this.entrenador = entrenador;
        this.proveedor = proveedor;
        this.soloLectura = soloLectura;

        setTitle("MI EQUIPO - " + entrenador.getNombre());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(640, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(6, 6));

        filas = new JPanel();
        filas.setLayout(new BoxLayout(filas, BoxLayout.Y_AXIS));
        filas.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JScrollPane(filas), BorderLayout.CENTER);

        contador = new JLabel();
        contador.setFont(new Font("SansSerif", Font.BOLD, 13));
        contador.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 0));
        add(contador, BorderLayout.NORTH);

        add(construirBotones(), BorderLayout.SOUTH);
        refrescar();
    }

    private JPanel construirBotones() {
        JPanel p = new JPanel();
        if (!soloLectura) {
            JButton agregar  = new JButton("Agregar");
            JButton buscar   = new JButton("Buscar");
            JButton eliminar = new JButton("Eliminar");
            JButton mover    = new JButton("Mover al 1er lugar");
            agregar.addActionListener(e -> alAgregar());
            buscar.addActionListener(e -> alBuscar());
            eliminar.addActionListener(e -> alEliminar());
            mover.addActionListener(e -> alMover());
            p.add(agregar);
            p.add(buscar);
            p.add(eliminar);
            p.add(mover);
        }
        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> dispose());
        p.add(cerrar);
        return p;
    }

    /** Redibuja la lista completa recorriendo el equipo por indice. */
    public void refrescar() {
        filas.removeAll();
        ListaPokemon equipo = entrenador.getEquipo();
        if (equipo.contar() == 0) {
            filas.add(new JLabel("El equipo esta vacio. Usa Agregar."));
        }
        for (int i = 0; i < equipo.contar(); i++) {
            Pokemon p = equipo.obtener(i);
            filas.add(new PanelFilaPokemon(p, false));
        }
        contador.setText("Pokemon disponibles: " + equipo.contarDisponibles()
                       + "   |   Total: " + equipo.contar()
                       + "   |   " + equipo.recorrer());
        revalidate();
        repaint();
    }

    // ------------------------------------------------------- TODO  DEV 4

    /**
     * TODO: pedir el nombre con JOptionPane.showInputDialog, pedirlo al
     * proveedor (proveedor.obtener(nombre)), validar null ("ese Pokemon no
     * existe"), limitar el equipo a 6, insertarlo con
     * entrenador.getEquipo().insertar(p) y refrescar().
     * La descarga desde PokeAPI va dentro de un SwingWorker para que la
     * ventana no se congele.
     */
    private void alAgregar() {
        JOptionPane.showMessageDialog(this, "TODO Dev 4: implementar Agregar");
    }

    /**
     * TODO: pedir el nombre y usar entrenador.getEquipo().buscar(nombre).
     * Mostrar los datos si aparece, o "no esta en el equipo" si devuelve null.
     */
    private void alBuscar() {
        JOptionPane.showMessageDialog(this, "TODO Dev 4: implementar Buscar");
    }

    /**
     * TODO: pedir el nombre, confirmar, llamar a
     * entrenador.getEquipo().eliminar(nombre) y refrescar().
     */
    private void alEliminar() {
        JOptionPane.showMessageDialog(this, "TODO Dev 4: implementar Eliminar");
    }

    /**
     * TODO (RETO ADICIONAL): pedir el nombre y llamar a
     * entrenador.getEquipo().moverAlPrimerLugar(nombre), despues refrescar().
     * El reenlazado de nodos lo hace Dev 1 en ListaPokemon.
     */
    private void alMover() {
        JOptionPane.showMessageDialog(this, "TODO Dev 4: implementar Mover al 1er lugar");
    }
}
