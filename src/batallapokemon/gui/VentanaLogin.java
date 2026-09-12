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

    private GestorUsuarios gestor;
    private ProveedorPokemon proveedor;

    private JTextField campoUsuario;
    private JPasswordField campoPassword;

    public VentanaLogin(GestorUsuarios gestor, ProveedorPokemon proveedor) {
        this.gestor = gestor;
        this.proveedor = proveedor;

        setTitle("POKEMON BATTLE - Acceso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 300);
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
    }

    private String usuario()  { return campoUsuario.getText().trim(); }
    private String password() { return new String(campoPassword.getPassword()); }

    /**
     * Abre la gestion de equipo y despues la batalla.
     * TODO Dev 4: si el equipo esta vacio, forzar a pasar por VentanaEquipo
     * antes de dejar empezar la batalla.
     */
    private void empezar(Usuario u) {
        Entrenador rival = gestor.generarRival(u);
        if (rival == null) {
            JOptionPane.showMessageDialog(this,
                "TODO Dev 4: generarRival() todavia no esta implementado.");
            return;
        }
        MotorBatalla motor = new MotorBatalla(u.getEntrenador(), rival);
        new VentanaBatalla(motor).setVisible(true);
        dispose();
    }

    /** TODO: gestor.login(usuario(), password()); si es null, avisar. */
    private void alIniciarSesion() {
        Usuario u = gestor.login(usuario(), password());
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Usuario o password incorrectos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        empezar(u);
    }

    /**
     * TODO: validar que los campos no esten vacios, gestor.crear(...) y
     * si devuelve null avisar "ese usuario ya existe". Despues abrir
     * VentanaEquipo (modo gestion) para que arme su equipo.
     */
    private void alCrearUsuario() {
        Usuario u = gestor.crear(usuario(), password());
        if (u == null) {
            JOptionPane.showMessageDialog(this,
                "No se pudo crear (ya existe o falta implementar crear()).");
            return;
        }
        new VentanaEquipo(u.getEntrenador(), proveedor, false).setVisible(true);
    }

    /** TODO: gestor.usuarioAleatorio() y empezar(u). */
    private void alUsuarioAleatorio() {
        Usuario u = gestor.usuarioAleatorio();
        if (u == null) {
            JOptionPane.showMessageDialog(this,
                "TODO Dev 4: cargar datos/usuarios.txt en GestorUsuarios.cargar()");
            return;
        }
        empezar(u);
    }
}
