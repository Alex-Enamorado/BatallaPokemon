package batallapokemon.gui;

import batallapokemon.logica.MotorBatalla;
import batallapokemon.logica.ResultadoTurno;
import batallapokemon.modelo.Pokemon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Ventana principal: tarjeta del rival arriba, la del jugador abajo, la barra
 * de botones y el panel de historial. Toda accion pasa por el MotorBatalla y
 * vuelve como ResultadoTurno, que se vuelca con mostrar().
 */
public class VentanaBatalla extends JFrame {

    private MotorBatalla motor;

    private JPanel tarjetaRival;
    private JPanel tarjetaJugador;
    private JTextArea historial;
    private JLabel etiquetaTurno;

    public VentanaBatalla(MotorBatalla motor) {
        this.motor = motor;
        setTitle("POKEMON BATTLE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(construirEncabezado(), BorderLayout.NORTH);
        add(construirCampo(),      BorderLayout.CENTER);
        add(construirInferior(),   BorderLayout.SOUTH);

        refrescar();
    }

    private JPanel construirEncabezado() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(40, 60, 110));
        JLabel titulo = new JLabel("POKEMON BATTLE", JLabel.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        p.add(titulo, BorderLayout.CENTER);

        etiquetaTurno = new JLabel("Turno 1  ", JLabel.RIGHT);
        etiquetaTurno.setForeground(Color.WHITE);
        p.add(etiquetaTurno, BorderLayout.EAST);
        return p;
    }

    private JPanel construirCampo() {
        JPanel campo = new JPanel();
        campo.setLayout(new BoxLayout(campo, BoxLayout.Y_AXIS));
        campo.setBackground(new Color(235, 240, 250));

        campo.add(titulo("ENTRENADOR RIVAL"));
        tarjetaRival = new JPanel(new BorderLayout());
        campo.add(tarjetaRival);

        JLabel vs = new JLabel("VS", JLabel.CENTER);
        vs.setFont(new Font("SansSerif", Font.BOLD, 26));
        vs.setAlignmentX(CENTER_ALIGNMENT);
        campo.add(vs);

        campo.add(titulo("TU ENTRENADOR"));
        tarjetaJugador = new JPanel(new BorderLayout());
        campo.add(tarjetaJugador);
        return campo;
    }

    private JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setBorder(BorderFactory.createEmptyBorder(6, 12, 2, 0));
        return l;
    }

    private JPanel construirInferior() {
        JPanel inferior = new JPanel(new BorderLayout(4, 4));

        JPanel botones = new JPanel(new GridLayout(1, 5, 6, 0));
        JButton atacar     = new JButton("ATACAR");
        JButton cambiar    = new JButton("CAMBIAR");
        JButton objetos    = new JButton("OBJETOS");
        JButton miEquipo   = new JButton("MI EQUIPO");
        JButton verHistorial = new JButton("HISTORIAL");

        atacar.addActionListener(e -> alAtacar());
        cambiar.addActionListener(e -> alCambiar());
        objetos.addActionListener(e -> alUsarObjeto());
        miEquipo.addActionListener(e -> alVerEquipo());
        verHistorial.addActionListener(e -> alVerHistorial());

        botones.add(atacar);
        botones.add(cambiar);
        botones.add(objetos);
        botones.add(miEquipo);
        botones.add(verHistorial);
        inferior.add(botones, BorderLayout.NORTH);

        historial = new JTextArea(8, 60);
        historial.setEditable(false);
        historial.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(historial);
        scroll.setBorder(BorderFactory.createTitledBorder("HISTORIAL"));
        inferior.add(scroll, BorderLayout.CENTER);
        return inferior;
    }

    /** Tarjeta con sprite + nombre/nivel/tipo/HP de un Pokemon. */
    private JPanel construirTarjeta(Pokemon p, boolean esRival) {
        JPanel tarjeta = new JPanel(new BorderLayout(10, 0));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 140, 180), 2),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tarjeta.setBackground(Color.WHITE);

        if (p == null) {
            tarjeta.add(new JLabel("Sin Pokemon activo", JLabel.CENTER), BorderLayout.CENTER);
            return tarjeta;
        }

        // Al rival se le ve el sprite de frente; al propio, el de espalda.
        String ruta = esRival ? p.getSpriteFrente() : p.getSpriteEspalda();
        JLabel sprite = Sprites.etiquetaSprite(ruta, p.getNombre());
        sprite.setPreferredSize(new Dimension(110, 110));
        tarjeta.add(sprite, esRival ? BorderLayout.EAST : BorderLayout.WEST);

        JPanel datos = new JPanel();
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
        datos.setOpaque(false);
        JLabel nombre = new JLabel(p.getNombre().toUpperCase());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 18));
        datos.add(nombre);
        datos.add(new JLabel("Nivel: " + p.getNivel()));
        datos.add(new JLabel("Tipo: " + p.getTipo().getEtiqueta()));

        JProgressBar barra = new JProgressBar(0, p.getHpMax());
        barra.setValue(p.getHpActual());
        barra.setStringPainted(true);
        barra.setString("HP " + p.getEstadoHp());
        barra.setPreferredSize(new Dimension(220, 20));
        datos.add(barra);

        JPanel envoltorio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        envoltorio.setOpaque(false);
        envoltorio.add(datos);
        tarjeta.add(envoltorio, BorderLayout.CENTER);
        return tarjeta;
    }

    /** Redibuja las dos tarjetas y el numero de turno. Llamar tras cada accion. */
    public void refrescar() {
        tarjetaRival.removeAll();
        tarjetaRival.add(construirTarjeta(motor.getRival().getEquipo().getActivo(), true));
        tarjetaJugador.removeAll();
        tarjetaJugador.add(construirTarjeta(motor.getJugador().getEquipo().getActivo(), false));
        etiquetaTurno.setText("Turno " + motor.getTurno() + "  ");
        revalidate();
        repaint();
    }

    /** Volca las lineas del resultado al panel de historial. */
    private void mostrar(ResultadoTurno r) {
        if (r.accionInvalida()) {
            JOptionPane.showMessageDialog(this, r.getMensajeError(),
                    "Accion no valida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        historial.append(r.texto());
        historial.setCaretPosition(historial.getDocument().getLength());
        refrescar();

        if (r.fueCambioForzado() && !r.batallaTerminada()) {
            Pokemon entra = motor.getJugador().getEquipo().getActivo();
            JOptionPane.showMessageDialog(this,
                    "Tu Pokemon fue derrotado.\nEntra " + entra.getNombre() + "!",
                    "Cambio forzado", JOptionPane.INFORMATION_MESSAGE);
        }

        if (r.batallaTerminada()) {
            String mensaje = r.jugadorGano() ? "GANASTE LA BATALLA!" : "PERDISTE LA BATALLA...";
            int opcion = JOptionPane.showConfirmDialog(this,
                    mensaje + "\n\n¿Queres jugar de nuevo?", "FIN DE LA BATALLA",
                    JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                motor.reiniciar();
                historial.setText("");
                refrescar();
            } else {
                dispose();
            }
        }
    }

    /** Muestra los ataques del Pokemon activo y ataca con el elegido. */
    private void alAtacar() {
        Pokemon activo = motor.getJugador().getEquipo().getActivo();
        if (activo == null) return;

        int cantidad = activo.getAtaques().contar();
        String[] nombres = new String[cantidad];
        for (int i = 0; i < cantidad; i++) {
            nombres[i] = activo.getAtaques().obtener(i).toString();
        }

        int elegido = JOptionPane.showOptionDialog(this, "Elegi un ataque:", "ATACAR",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, nombres, nombres[0]);

        if (elegido >= 0) {
            mostrar(motor.atacar(elegido));
        }
    }

    /** Abre la seleccion de Pokemon y cambia al elegido. */
    private void alCambiar() {
        DialogoCambiar d = new DialogoCambiar(this, motor.getJugador().getEquipo());
        d.setVisible(true);
        String elegido = d.getSeleccionado();
        if (elegido != null) mostrar(motor.cambiarPokemon(elegido));
    }

    /** Abre el inventario y aplica el objeto al Pokemon elegido. */
    private void alUsarObjeto() {
        DialogoObjetos d = new DialogoObjetos(this, motor.getJugador());
        d.setVisible(true);
        if (d.getObjetoElegido() != null) {
            mostrar(motor.usarObjeto(d.getObjetoElegido(), d.getDestinoElegido()));
        }
    }

    /** Estado de todo el equipo, en modo solo lectura. */
    private void alVerEquipo() {
        VentanaEquipo v = new VentanaEquipo(motor.getJugador(), null, true);
        v.setVisible(true);
    }

    private void alVerHistorial() {
        new DialogoHistorial(this, motor).setVisible(true);
    }
}
