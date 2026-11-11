package chat.gui;

import chat.modelo.Conversacion;
import chat.modelo.Mensaje;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.function.Consumer;

/** Una conversación abierta: cabecera, mensajes en burbujas y caja para escribir. */
class PanelConversacion extends JPanel {

    private final String yo;
    private final boolean mostrarEmisores;
    private final JLabel subtitulo = new JLabel(" ");
    private final JPanel mensajes = new ListaMensajes();
    private final JScrollPane scroll;
    private final JTextField campo = Estilo.campo("Escribe un mensaje…");
    private final JButton enviar = Estilo.boton("Enviar", true);
    private String ultimoEmisor;

    /** @param alEnviar qué hacer con el texto escrito */
    PanelConversacion(Conversacion conversacion, String yo, Consumer<String> alEnviar) {
        super(new BorderLayout());
        this.yo = yo;
        this.mostrarEmisores = conversacion.tipo().variosParticipantes();
        setBackground(Estilo.FONDO_CHAT);

        // Cabecera
        JPanel cabecera = new JPanel(new BorderLayout(12, 0));
        cabecera.setBackground(java.awt.Color.WHITE);
        cabecera.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.SEPARADOR),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)));
        cabecera.add(new Avatar(conversacion.id(), 40, conversacion.tipo().variosParticipantes()), BorderLayout.WEST);
        JPanel textos = new JPanel(new BorderLayout());
        textos.setOpaque(false);
        JLabel titulo = new JLabel(conversacion.id());
        titulo.setFont(Estilo.fuente(Font.BOLD, 16f));
        titulo.setForeground(Estilo.TEXTO);
        subtitulo.setFont(Estilo.fuente(Font.PLAIN, 12.5f));
        subtitulo.setForeground(Estilo.TEXTO_SUAVE);
        textos.add(titulo, BorderLayout.NORTH);
        textos.add(subtitulo, BorderLayout.SOUTH);
        cabecera.add(textos, BorderLayout.CENTER);
        add(cabecera, BorderLayout.NORTH);

        // Mensajes
        mensajes.setLayout(new BoxLayout(mensajes, BoxLayout.Y_AXIS));
        mensajes.setBackground(Estilo.FONDO_CHAT);
        mensajes.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        scroll = Estilo.scroll(mensajes, Estilo.FONDO_CHAT);
        add(scroll, BorderLayout.CENTER);

        // Caja de escritura: Intro o botón para enviar
        Runnable envio = () -> {
            alEnviar.accept(campo.getText());
            campo.setText("");
        };
        campo.addActionListener(e -> envio.run());
        enviar.addActionListener(e -> envio.run());
        JPanel abajo = new JPanel(new BorderLayout(10, 0));
        abajo.setBackground(java.awt.Color.WHITE);
        abajo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.SEPARADOR),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)));
        abajo.add(campo, BorderLayout.CENTER);
        abajo.add(enviar, BorderLayout.EAST);
        add(abajo, BorderLayout.SOUTH);
    }

    void agregar(Mensaje m) {
        boolean propio = yo.equals(m.emisor());
        boolean seguido = m.emisor().equals(ultimoEmisor);
        if (ultimoEmisor != null) mensajes.add(Box.createVerticalStrut(seguido ? 3 : 10));
        JPanel fila = new JPanel(new FlowLayout(propio ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0)) {
            @Override public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        fila.setOpaque(false);
        fila.add(new Burbuja(m, propio, mostrarEmisores && !propio && !seguido));
        mensajes.add(fila);
        ultimoEmisor = m.emisor();

        mensajes.revalidate();
        SwingUtilities.invokeLater(() -> SwingUtilities.invokeLater(() -> {
            JScrollBar barra = scroll.getVerticalScrollBar();
            barra.setValue(barra.getMaximum());
        }));
    }

    void setSubtitulo(String texto) {
        subtitulo.setText(texto);
    }

    /** Permite o impide escribir (por ejemplo, si el otro usuario se ha desconectado). */
    void setActivo(boolean activo) {
        campo.setEnabled(activo);
        enviar.setEnabled(activo);
    }

    void enfocar() {
        campo.requestFocusInWindow();
    }

    /** Ajusta los mensajes al ancho visible (sin scroll horizontal). */
    private static class ListaMensajes extends JPanel implements Scrollable {
        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 18; }
        public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return r.height - 40; }
        public boolean getScrollableTracksViewportWidth() { return true; }
        public boolean getScrollableTracksViewportHeight() { return false; }
    }
}
