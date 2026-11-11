package chat.gui;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

/** Círculo (o cuadrado redondeado, para grupos) con las iniciales y el punto de "en línea". */
class Avatar extends JComponent {

    private final int tamano;
    private String nombre = "";
    private boolean cuadrado;
    private boolean enLinea;

    Avatar(int tamano) {
        this.tamano = tamano;
        setPreferredSize(new Dimension(tamano, tamano));
    }

    Avatar(String nombre, int tamano, boolean cuadrado) {
        this(tamano);
        datos(nombre, cuadrado, false);
    }

    Avatar datos(String nombre, boolean cuadrado, boolean enLinea) {
        this.nombre = nombre;
        this.cuadrado = cuadrado;
        this.enLinea = enLinea;
        repaint();
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = Estilo.suave(g);
        int s = tamano;
        g2.setColor(Estilo.colorDe(nombre));
        if (cuadrado) g2.fillRoundRect(0, 0, s, s, s / 2, s / 2);
        else g2.fillOval(0, 0, s, s);

        String ini = nombre.isEmpty() ? "?" : nombre.substring(0, 1).toUpperCase();
        g2.setColor(Color.WHITE);
        g2.setFont(Estilo.fuente(Font.BOLD, s * 0.4f));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(ini, (s - fm.stringWidth(ini)) / 2f, (s - fm.getHeight()) / 2f + fm.getAscent());

        if (enLinea) {
            int d = Math.max(10, s / 4);
            g2.setColor(Color.WHITE);
            g2.fillOval(s - d - 2, s - d - 2, d + 4, d + 4);
            g2.setColor(Estilo.EN_LINEA);
            g2.fillOval(s - d, s - d, d, d);
        }
        g2.dispose();
    }
}
