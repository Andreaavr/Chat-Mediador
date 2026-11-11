package chat.gui;

import chat.modelo.Mensaje;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.time.format.DateTimeFormatter;

/** Burbuja de un mensaje: nombre del emisor (en grupos), texto y hora. */
class Burbuja extends JPanel {

    private static final int ANCHO_MAXIMO = 320;
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final Color fondo;

    Burbuja(Mensaje m, boolean propia, boolean mostrarEmisor) {
        super(new BorderLayout(0, 2));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(7, 12, 6, 12));
        fondo = propia ? Estilo.ACENTO : Color.WHITE;
        Color colorTexto = propia ? Color.WHITE : Estilo.TEXTO;

        if (mostrarEmisor) {
            JLabel emisor = new JLabel(m.emisor());
            emisor.setFont(Estilo.fuente(Font.BOLD, 12.5f));
            emisor.setForeground(Estilo.colorDe(m.emisor()));
            add(emisor, BorderLayout.NORTH);
        }

        JLabel texto = new JLabel();
        texto.setFont(Estilo.fuente(Font.PLAIN, 14f));
        texto.setForeground(colorTexto);
        String seguro = m.texto().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        boolean largo = texto.getFontMetrics(texto.getFont()).stringWidth(m.texto()) > ANCHO_MAXIMO;
        texto.setText("<html>" + (largo ? "<div style='width:" + ANCHO_MAXIMO + "px'>" : "") + seguro + "</html>");
        add(texto, BorderLayout.CENTER);

        JLabel hora = new JLabel(m.hora().format(HORA), SwingConstants.RIGHT);
        hora.setFont(Estilo.fuente(Font.PLAIN, 10.5f));
        hora.setForeground(propia ? new Color(255, 255, 255, 170) : Estilo.TEXTO_SUAVE);
        add(hora, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Estilo.suave(g);
        g2.setColor(new Color(15, 23, 42, 22));                    // sombra suave
        g2.fillRoundRect(0, 1, getWidth(), getHeight() - 1, 18, 18);
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth(), getHeight() - 1, 18, 18);
        g2.dispose();
    }
}
