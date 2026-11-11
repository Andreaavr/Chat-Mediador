package chat.gui;

import chat.modelo.Conversacion;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/**
 * Fila de la lista de chats: avatar, nombre, detalle y mensajes sin leer.
 * Solo dibuja; los datos los pide a quien la crea.
 */
class CeldaChat extends JPanel implements ListCellRenderer<Conversacion> {

    private final Predicate<Conversacion> disponible;
    private final Function<Conversacion, String> detalle;
    private final ToIntFunction<Conversacion> noLeidos;

    private final Avatar avatar = new Avatar(42);
    private final JLabel nombre = new JLabel();
    private final JLabel textoDetalle = new JLabel();
    private final JLabel pendientes = new Insignia();

    /**
     * @param disponible si se puede escribir ahora (contacto en línea)
     * @param detalle    texto bajo el nombre
     * @param noLeidos   número de mensajes sin leer
     */
    CeldaChat(Predicate<Conversacion> disponible, Function<Conversacion, String> detalle,
              ToIntFunction<Conversacion> noLeidos) {
        super(new BorderLayout(12, 0));
        this.disponible = disponible;
        this.detalle = detalle;
        this.noLeidos = noLeidos;

        setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        nombre.setFont(Estilo.fuente(Font.BOLD, 14.5f));
        textoDetalle.setFont(Estilo.fuente(Font.PLAIN, 12.5f));
        textoDetalle.setForeground(Estilo.TEXTO_SUAVE);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(nombre);
        textos.add(textoDetalle);
        add(avatar, BorderLayout.WEST);
        add(textos, BorderLayout.CENTER);
        add(pendientes, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Conversacion> lista, Conversacion c,
                                                  int indice, boolean seleccionada, boolean foco) {
        Conversacion.Tipo tipo = c.tipo();
        boolean activa = disponible.test(c);
        setBackground(seleccionada ? Estilo.SELECCION : Estilo.FONDO_LATERAL);
        avatar.datos(c.id(), tipo.variosParticipantes(), tipo.requiereConexion() && activa);
        nombre.setText(c.id());
        nombre.setForeground(activa ? Estilo.TEXTO : Estilo.TEXTO_SUAVE);
        textoDetalle.setText(detalle.apply(c));
        int n = noLeidos.applyAsInt(c);
        pendientes.setText(n > 0 ? String.valueOf(n) : "");
        return this;
    }

    /** Círculo de color con el número de mensajes sin leer. */
    private static class Insignia extends JLabel {
        Insignia() {
            super("", SwingConstants.CENTER);
            setFont(Estilo.fuente(Font.BOLD, 11f));
            setForeground(Color.WHITE);
            setPreferredSize(new Dimension(24, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!getText().isEmpty()) {
                Graphics2D g2 = Estilo.suave(g);
                g2.setColor(Estilo.ACENTO);
                int h = 20, w = Math.max(h, getWidth());
                g2.fillRoundRect((getWidth() - w) / 2, (getHeight() - h) / 2, w, h, h, h);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
