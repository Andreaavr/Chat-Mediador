package chat.gui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** Pide el nombre y los miembros de un grupo nuevo. Cada miembro se marca con un clic. */
final class DialogoGrupo {

    record Resultado(String nombre, List<String> miembros) { }

    private DialogoGrupo() { }

    static Optional<Resultado> pedir(Component padre, Collection<String> candidatos) {
        JTextField nombre = new JTextField();
        nombre.setFont(Estilo.fuente(Font.PLAIN, 14f));

        // Una casilla por usuario conectado: un clic lo marca, otro lo desmarca
        List<JCheckBox> casillas = new ArrayList<>();
        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);
        lista.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        for (String c : candidatos.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList()) {
            JCheckBox casilla = new JCheckBox(c);
            casilla.setFont(Estilo.fuente(Font.PLAIN, 14f));
            casilla.setOpaque(false);
            casilla.setFocusPainted(false);
            casilla.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            casilla.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 2));
            casillas.add(casilla);
            lista.add(casilla);
        }
        JComponent scroll = Estilo.scroll(lista, Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(Estilo.SEPARADOR));
        scroll.setPreferredSize(new Dimension(280, Math.min(200, 34 * candidatos.size() + 12)));

        JPanel formulario = new JPanel(new BorderLayout(0, 12));
        formulario.add(conTitulo("Nombre del grupo:", nombre), BorderLayout.NORTH);
        formulario.add(conTitulo("Miembros (haz clic en los que quieras añadir):", scroll), BorderLayout.CENTER);

        int r = JOptionPane.showConfirmDialog(padre, formulario, "Nuevo grupo",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return Optional.empty();

        List<String> elegidos = casillas.stream().filter(JCheckBox::isSelected).map(JCheckBox::getText).toList();
        return Optional.of(new Resultado(nombre.getText().trim(), elegidos));
    }

    private static JPanel conTitulo(String titulo, Component c) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(Estilo.fuente(Font.BOLD, 12.5f));
        p.add(etiqueta, BorderLayout.NORTH);
        p.add(c, BorderLayout.CENTER);
        return p;
    }
}
