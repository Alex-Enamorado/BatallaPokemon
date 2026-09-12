package batallapokemon.gui;

import batallapokemon.datos.ProveedorPokemon;
import batallapokemon.logica.GestorUsuarios;
import batallapokemon.logica.MotorBatalla;
import batallapokemon.modelo.Entrenador;
import batallapokemon.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * ===========================================================================
 *  CARRIL DEV 4  -  LOGIN
 * ===========================================================================
 * Primera ventana del juego: log in, crear usuario o jugar con un usuario
 * aleatorio del roster de 10.
 */
public class VentanaLogin extends JFrame {

    private final GestorUsuarios gestor;
    private final ProveedorPokemon proveedor;

    private JTextField campoUsuario;
    private JPasswordField campoPassword;

    public VentanaLogin(GestorUsuarios gestor, ProveedorPokemon proveedor) {
        this.gestor = gestor;
        this.proveedor = proveedor;

        setTitle("POKEMON BATTLE - Acceso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JLabel titulo = new JLabel("POKEMON BATTLE", JLabel.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(new Color(40, 60, 110));
        titulo.setBorder(BorderFactory.createEmptyBorder(14, 0, 6, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(2, 2, 6, 8));
        formulario.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        campoUsuario = new JTextField();
        campoPassword = new JPasswordField();
        formulario.add(new JLabel("Usuario:"));
        formulario.add(campoUsuario);
        formulario.add(new JLabel("Password:"));
        formulario.add(campoPassword);
        add(formulario, BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(3, 1, 4, 4));
        botones.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));
        JButton entrar    = new JButton("INICIAR SESION");
        JButton crear     = new JButton("CREAR USUARIO");
        JButton aleatorio = new JButton("JUGAR CON USUARIO ALEATORIO");
        entrar.addActionListener(e -> alIniciarSesion());
        crear.addActionListener(e -> alCrearUsuario());
        aleatorio.addActionListener(e -> alUsuarioAleatorio());
        botones.add(entrar);
        botones.add(crear);
        botones.add(aleatorio);
        add(botones, BorderLayout.SOUTH);

        // Enter en cualquiera de los dos campos inicia sesion.
        campoUsuario.addActionListener(e -> alIniciarSesion());
        campoPassword.addActionListener(e -> alIniciarSesion());
    }

    private String usuario()  { return campoUsuario.getText().trim(); }
    private String password() { return new String(campoPassword.getPassword()); }

    private void alIniciarSesion() {
        if (usuario().isEmpty() || password().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa usuario y password.",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario u = gestor.login(usuario(), password());
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Usuario o password incorrectos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        empezar(u);
    }

    private void alCrearUsuario() {
        if (usuario().isEmpty() || password().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa usuario y password.",
                    "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario nuevo = gestor.crear(usuario(), password());
        if (nuevo == null) {
            JOptionPane.showMessageDialog(this,
                    "Ese usuario ya existe (o el nombre tiene ';' o ',').",
                    "No se pudo crear", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Usuario creado. Arma tu equipo y despues cerra esa ventana\n"
                + "para empezar la batalla.",
                "Bienvenido " + nuevo.getNombre(), JOptionPane.INFORMATION_MESSAGE);
        abrirGestionEquipo(nuevo);
    }

    private void alUsuarioAleatorio() {
        Usuario u = gestor.usuarioAleatorio();
        if (u == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay usuarios cargados. Falta el archivo "
                    + GestorUsuarios.ARCHIVO + "\n"
                    + "(o la configuracion de ejecucion no apunta a la raiz del proyecto).",
                    "Roster vacio", JOptionPane.ERROR_MESSAGE);
            return;
        }
        campoUsuario.setText(u.getNombre());
        empezar(u);
    }

    /** Ventana de gestion; al cerrarla, si ya hay equipo, arranca la batalla. */
    private void abrirGestionEquipo(Usuario u) {
        VentanaEquipo ventana = new VentanaEquipo(u.getEntrenador(), proveedor, false);
        ventana.setGestor(gestor);
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (u.getEntrenador().getEquipo().contar() > 0) empezar(u);
            }
        });
        ventana.setVisible(true);
    }

    /** Arranca la batalla. Si el equipo esta vacio, manda a armarlo primero. */
    private void empezar(Usuario u) {
        if (u.getEntrenador().getEquipo().contar() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Tu equipo esta vacio. Agrega al menos un Pokemon.",
                    "Equipo vacio", JOptionPane.WARNING_MESSAGE);
            abrirGestionEquipo(u);
            return;
        }

        Entrenador rival = gestor.generarRival(u);
        if (rival == null || rival.getEquipo().contar() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo armar el equipo rival.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MotorBatalla motor = new MotorBatalla(u.getEntrenador(), rival);
        new VentanaBatalla(motor).setVisible(true);
        dispose();
    }
}
