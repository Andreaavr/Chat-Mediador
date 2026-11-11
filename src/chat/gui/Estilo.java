package chat.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.List;

/** Colores, tipografía y controles básicos con el aspecto de la aplicación. */
final class Estilo {

    static final Color FONDO_LATERAL = Color.decode("#FFFFFF");
    static final Color FONDO_CHAT    = Color.decode("#F2F3F7");
    static final Color FONDO_CAMPO   = Color.decode("#EEF0F4");
    static final Color SELECCION     = Color.decode("#ECEDFB");
    static final Color SEPARADOR     = Color.decode("#E3E5EA");
    static final Color TEXTO         = Color.decode("#14161C");
    static final Color TEXTO_SUAVE   = Color.decode("#6B7280");
    static final Color ACENTO        = Color.decode("#5B5BD6");
    static final Color ACENTO_HOVER  = Color.decode("#4848C2");
    static final Color EN_LINEA      = Color.decode("#22C55E");

    private static final List<Color> COLORES_AVATAR = List.of(
            Color.decode("#F97316"), Color.decode("#10B981"), Color.decode("#3B82F6"), Color.decode("#14B8A6"),
            Color.decode("#EC4899"), Color.decode("#8B5CF6"), Color.decode("#F59E0B"), Color.decode("#EF4444"));

    private static final String FAMILIA = elegirFuente();

    private Estilo() { }

    private static String elegirFuente() {
        List<String> disponibles = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        return List.of("Segoe UI", "SF Pro Text", "Helvetica Neue", "Inter", "Noto Sans", "Ubuntu", "DejaVu Sans")
                .stream().filter(disponibles::contains).findFirst().orElse(Font.SANS_SERIF);
    }

    static Font fuente(int estilo, float tamano) {
        return new Font(FAMILIA, estilo, Math.round(tamano)).deriveFont(tamano);
    }

    /** Cada nombre tiene siempre el mismo color. */
    static Color colorDe(String nombre) {
        int h = 0;
        for (char c : nombre.toCharArray()) h = h * 131 + c;
        h ^= h >>> 15;
        h *= 0x2c1b3c6d;
        h ^= h >>> 12;
        return COLORES_AVATAR.get(Math.floorMod(h, COLORES_AVATAR.size()));
    }

    static Graphics2D suave(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    /** Campo de texto redondeado con texto de ejemplo. */
    static JTextField campo(String ejemplo) {
        JTextField campo = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = suave(g);
                g2.setColor(FONDO_CAMPO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                if (getText().isEmpty()) {
                    FontMetrics fm = g2.getFontMetrics(getFont());
                    g2.setFont(getFont());
                    g2.setColor(TEXTO_SUAVE);
                    g2.drawString(ejemplo, getInsets().left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        campo.setOpaque(false);
        campo.setFont(fuente(Font.PLAIN, 14f));
        campo.setForeground(TEXTO);
        campo.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        return campo;
    }

    /** Botón redondeado; principal = relleno de color, secundario = contorno. */
    static JButton boton(String texto, boolean principal) {
        JButton b = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = suave(g);
                if (principal) {
                    g2.setColor(getModel().isRollover() ? ACENTO_HOVER : ACENTO);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                } else {
                    if (getModel().isRollover()) {
                        g2.setColor(SELECCION);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    }
                    g2.setColor(SEPARADOR);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(fuente(Font.BOLD, 13f));
        b.setForeground(principal ? Color.WHITE : TEXTO);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        return b;
    }

    /** Panel con desplazamiento vertical y sin bordes. */
    static JScrollPane scroll(JComponent contenido, Color fondo) {
        JScrollPane s = new JScrollPane(contenido,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        s.setBorder(BorderFactory.createEmptyBorder());
        s.getViewport().setBackground(fondo);
        s.getVerticalScrollBar().setUnitIncrement(18);
        return s;
    }
}
