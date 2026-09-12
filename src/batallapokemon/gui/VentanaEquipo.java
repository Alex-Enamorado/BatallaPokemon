package batallapokemon.gui;

import batallapokemon.datos.ProveedorPokemon;
import batallapokemon.estructuras.ListaPokemon;
import batallapokemon.logica.GestorUsuarios;
import batallapokemon.modelo.Entrenador;
import batallapokemon.modelo.Pokemon;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

/**
 * Ventana MI EQUIPO.
 *
 * En modo soloLectura solo muestra el estado (es lo que abre el boton MI EQUIPO
 * durante la batalla); en modo gestion habilita Agregar / Buscar / Eliminar /
 * Mover al primer lugar. Todo se hace con los metodos publicos de
 * ListaPokemon: aca no se importa NodoPokemon.
 */
public class VentanaEquipo extends JFrame {

    private final Entrenador entrenador;
    private final ProveedorPokemon proveedor;
    private final boolean soloLectura;

    /** Opcional: si esta, cada cambio del equipo se persiste en usuarios.txt. */
    private GestorUsuarios gestor;

    private JPanel filas;
    private JPanel panelBotones;
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

        panelBotones = construirBotones();
        add(panelBotones, BorderLayout.SOUTH);
        refrescar();
    }

    /** Si se le pasa el gestor, los cambios del equipo quedan guardados. */
    public void setGestor(GestorUsuarios gestor) { this.gestor = gestor; }

    private JPanel construirBotones() {
        JPanel p = new JPanel();
        if (!soloLectura) {
            JButton agregar  = new JButton("Agregar");
            JButton buscar   = new JButton("Buscar");
            JButton eliminar = new JButton("Eliminar");
            JButton modificar = new JButton("Modificar");
            JButton mover     = new JButton("Mover al 1°");
            agregar.addActionListener(e -> alAgregar());
            buscar.addActionListener(e -> alBuscar());
            eliminar.addActionListener(e -> alEliminar());
            modificar.addActionListener(e -> alModificar());
            mover.addActionListener(e -> alMover());
            p.add(agregar);
            p.add(buscar);
            p.add(eliminar);
            p.add(modificar);
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

    /** Los cambios del equipo se reflejan en datos/usuarios.txt. */
    private void persistir() {
        if (gestor != null) gestor.guardar();
    }

    /** Los que el proveedor garantiza incluso sin internet. */
    private String sugerencias() {
        String[] nombres = proveedor.nombresDisponibles();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nombres.length; i++) {
            sb.append(nombres[i]);
            if (i < nombres.length - 1) sb.append((i + 1) % 6 == 0 ? ",\n" : ", ");
        }
        return sb.toString();
    }

    /** Nombres actuales, para mostrarlos en los dialogos de entrada. */
    private String nombresActuales() {
        ListaPokemon equipo = entrenador.getEquipo();
        if (equipo.contar() == 0) return "(equipo vacio)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < equipo.contar(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(equipo.obtener(i).getNombre());
        }
        return sb.toString();
    }

    private String pedirNombre(String titulo, String indicacion) {
        String respuesta = JOptionPane.showInputDialog(this,
                indicacion + "\n\nEn el equipo: " + nombresActuales(),
                titulo, JOptionPane.QUESTION_MESSAGE);
        if (respuesta == null) return null;
        respuesta = respuesta.trim();
        return respuesta.isEmpty() ? null : respuesta;
    }

    // -------------------------------------------------------------- AGREGAR

    /**
     * Pide el nombre y lo trae del proveedor. La descarga puede tardar
     * (PokeAPI), asi que va en un SwingWorker: si se llamara a
     * proveedor.obtener() aca mismo, la ventana se congelaria.
     */
    private void alAgregar() {
        if (proveedor == null) {
            JOptionPane.showMessageDialog(this, "No hay proveedor de Pokemon configurado.");
            return;
        }
        ListaPokemon equipo = entrenador.getEquipo();
        if (equipo.contar() >= GestorUsuarios.MAX_EQUIPO) {
            JOptionPane.showMessageDialog(this,
                    "El equipo ya tiene " + GestorUsuarios.MAX_EQUIPO + " Pokemon.",
                    "Equipo completo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = pedirNombre("Agregar Pokemon",
                "Nombre del Pokemon (en ingles, como en PokeAPI).\n"
                + "Sin internet solo andan estos:\n" + sugerencias());
        if (nombre == null) return;

        if (equipo.buscar(nombre) != null) {
            JOptionPane.showMessageDialog(this, nombre + " ya esta en el equipo.");
            return;
        }

        ocupado(true);
        new SwingWorker<Pokemon, Void>() {
            @Override
            protected Pokemon doInBackground() {
                return proveedor.obtener(nombre);      // red + disco, fuera del hilo de Swing
            }

            @Override
            protected void done() {
                ocupado(false);
                Pokemon encontrado;
                try {
                    encontrado = get();
                } catch (Exception e) {
                    encontrado = null;
                }
                if (encontrado == null) {
                    JOptionPane.showMessageDialog(VentanaEquipo.this,
                            "No existe ningun Pokemon llamado \"" + nombre + "\".",
                            "No encontrado", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                entrenador.getEquipo().insertar(encontrado);
                persistir();
                refrescar();
            }
        }.execute();
    }

    private void ocupado(boolean esperando) {
        setCursor(Cursor.getPredefinedCursor(
                esperando ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
        panelBotones.setEnabled(!esperando);
        for (int i = 0; i < panelBotones.getComponentCount(); i++) {
            panelBotones.getComponent(i).setEnabled(!esperando);
        }
    }

    // --------------------------------------------------------------- BUSCAR

    private void alBuscar() {
        String nombre = pedirNombre("Buscar Pokemon", "Nombre a buscar en el equipo:");
        if (nombre == null) return;

        Pokemon p = entrenador.getEquipo().buscar(nombre);
        if (p == null) {
            JOptionPane.showMessageDialog(this,
                    "\"" + nombre + "\" no esta en el equipo.",
                    "Buscar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                p.getNombre()
                + "\nNivel: " + p.getNivel()
                + "\nTipo: " + p.getTipo().getEtiqueta()
                + "\nHP: " + p.getEstadoHp()
                + "\nAtaque: " + p.getAtaque() + "   Defensa: " + p.getDefensa()
                + "\nEstado: " + (p.estaDerrotado() ? "DERROTADO" : "disponible"),
                "Encontrado", JOptionPane.INFORMATION_MESSAGE);
    }

    // ------------------------------------------------------------- ELIMINAR

    private void alEliminar() {
        String nombre = pedirNombre("Eliminar Pokemon", "Nombre del Pokemon a eliminar:");
        if (nombre == null) return;

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "Seguro que queres eliminar a " + nombre + " del equipo?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        if (!entrenador.getEquipo().eliminar(nombre)) {
            JOptionPane.showMessageDialog(this,
                    "\"" + nombre + "\" no esta en el equipo.",
                    "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        persistir();
        refrescar();
    }

    // ------------------------------------------------------------ MODIFICAR

    /** Cambia el nivel de un Pokemon del equipo (tipo y hpMax quedan igual). */
    private void alModificar() {
        String nombre = pedirNombre("Modificar Pokemon", "Nombre del Pokemon a modificar:");
        if (nombre == null) return;

        Pokemon p = entrenador.getEquipo().buscar(nombre);
        if (p == null) {
            JOptionPane.showMessageDialog(this, "\"" + nombre + "\" no esta en el equipo.",
                    "Modificar", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String texto = JOptionPane.showInputDialog(this,
                "Nuevo nivel para " + p.getNombre() + " (1-100):",
                String.valueOf(p.getNivel()));
        if (texto == null) return;

        int nivel;
        try {
            nivel = Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Eso no es un numero.",
                    "Modificar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nivel < 1 || nivel > 100) {
            JOptionPane.showMessageDialog(this, "El nivel tiene que estar entre 1 y 100.",
                    "Modificar", JOptionPane.WARNING_MESSAGE);
            return;
        }

        entrenador.getEquipo().modificar(p.getNombre(), nivel, p.getHpMax(), p.getTipo());
        persistir();
        refrescar();
    }

    // ---------------------------------------------------------------- MOVER

    /** Reto adicional: el reenlazado lo hace ListaPokemon (carril Dev 1). */
    private void alMover() {
        String nombre = pedirNombre("Mover al primer lugar",
                "Nombre del Pokemon que pasa a ser el primero:");
        if (nombre == null) return;

        if (!entrenador.getEquipo().moverAlPrimerLugar(nombre)) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo mover \"" + nombre + "\".\n"
                    + "Verifica que este en el equipo.",
                    "Mover", JOptionPane.WARNING_MESSAGE);
            return;
        }
        persistir();
        refrescar();
    }
}
