package batallapokemon.gui;

import batallapokemon.estructuras.ListaEnlazada;
import batallapokemon.logica.MotorBatalla;
import batallapokemon.modelo.Registro;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DialogoHistorial extends JDialog {
    public DialogoHistorial(Frame padre, MotorBatalla motor) {
        super(padre, "HISTORIAL DE BATALLA", true);
        setSize(520, 480);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(6, 6));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));

        ListaEnlazada<Registro> h = motor.getHistorial();
        int turnoPrevio = -1;
        for (int i = 0; i < h.contar(); i++) {
            Registro r = h.obtener(i);
            if (r.getTurno() != turnoPrevio) {
                area.append((i == 0 ? "" : "\n") + "Turno " + r.getTurno() + "\n");
                turnoPrevio = r.getTurno();
            }
            area.append("  " + r.getTexto() + "\n");
        }
        if (h.contar() == 0) area.setText("Todavia no paso nada.\n");

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createTitledBorder("Acciones"));
        add(scroll, BorderLayout.CENTER);

        JTextArea stats = new JTextArea(motor.getEstadisticas().resumen());
        stats.setEditable(false);
        stats.setBorder(BorderFactory.createTitledBorder("ESTADISTICAS"));
        add(stats, BorderLayout.NORTH);

        JPanel sur = new JPanel();
        JButton cerrar = new JButton("CERRAR");
        cerrar.addActionListener(e -> dispose());
        sur.add(cerrar);
        add(sur, BorderLayout.SOUTH);
    }
}
