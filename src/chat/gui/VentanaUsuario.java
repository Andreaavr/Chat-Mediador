package chat.gui;

import chat.modelo.Conversacion;
import chat.modelo.Mensaje;
import chat.usuarios.AccionesUsuario;
import chat.usuarios.OyenteUsuario;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ventana de un usuario: a la izquierda sus chats (privados y grupos) y a la
 * derecha la conversación abierta. Cada conversación tiene su propio panel, así
 * que el usuario puede llevar varias a la vez.
 *
 * Recibe los avisos de su usuario porque implementa {@link OyenteUsuario}, y le
 * da órdenes a través de {@link AccionesUsuario}.
 */
public class VentanaUsuario extends JFrame implements OyenteUsuario {

    private static final String VACIO = "vacio";

    private final AccionesUsuario usuario;
    private final Map<Conversacion, PanelConversacion> paneles = new HashMap<>();
    private final Map<Conversacion, Integer> noLeidos = new HashMap<>();
    private final Map<String, List<String>> miembros = new HashMap<>();
    private final Set<String> conectados = new LinkedHashSet<>();
    private final Set<Conversacion> chats = new LinkedHashSet<>();   // todo lo que aparece en la lista

    private final DefaultListModel<Conversacion> modeloLista = new DefaultListModel<>();
    private final JList<Conversacion> lista = new JList<>(modeloLista) {
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
    };
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel derecha = new JPanel(tarjetas);
    private Conversacion abierta;
    private boolean refrescando;

    public VentanaUsuario(AccionesUsuario usuario) {
        super(usuario.getNombre() + " — Chat");
        this.usuario = usuario;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(720, 460));
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) { usuario.desconectar(); }
        });

        derecha.add(panelVacio(), VACIO);
        setLayout(new BorderLayout());
        add(panelLateral(), BorderLayout.WEST);
        add(derecha, BorderLayout.CENTER);
    }

    // ================================================================ avisos del usuario

    @Override
    public void mensajeRecibido(Mensaje m) {
        enEDT(() -> {
            Conversacion c = m.conversacion();
            chats.add(c);
            panel(c).agregar(m);
            if (!c.equals(abierta)) noLeidos.merge(c, 1, Integer::sum);
            refrescar();
        });
    }

    @Override
    public void conectadosActualizados(Set<String> nombres) {
        enEDT(() -> {
            conectados.clear();
            nombres.stream().filter(n -> !n.equals(usuario.getNombre())).forEach(conectados::add);
            conectados.forEach(n -> chats.add(Conversacion.privada(n)));
            refrescar();
        });
    }

    @Override
    public void agregadoAGrupo(String grupo, List<String> lista) {
        enEDT(() -> {
            miembros.put(grupo, lista);
            chats.add(Conversacion.grupo(grupo));
            refrescar();
        });
    }

    @Override
    public void error(String mensaje) {
        enEDT(() -> JOptionPane.showMessageDialog(this, mensaje, "Chat", JOptionPane.WARNING_MESSAGE));
    }

    // ================================================================ reglas de la ventana

    /** ¿Se puede escribir ahora? Solo depende de la característica del tipo, no de qué tipo es. */
    private boolean disponible(Conversacion c) {
        return !c.tipo().requiereConexion() || conectados.contains(c.id());
    }

    /** Texto bajo el nombre: estado de conexión o número de miembros. */
    private String detalle(Conversacion c) {
        if (c.tipo().requiereConexion()) return disponible(c) ? "En línea" : "Desconectado";
        return miembros.getOrDefault(c.id(), List.of()).size() + " miembros";
    }

    // ================================================================ conversaciones

    /** Panel de la conversación, creándolo la primera vez. */
    private PanelConversacion panel(Conversacion c) {
        return paneles.computeIfAbsent(c, k -> {
            PanelConversacion p = new PanelConversacion(k, usuario.getNombre(), texto -> usuario.enviar(k, texto));
            derecha.add(p, k.tipo() + ":" + k.id());
            return p;
        });
    }

    private void abrir(Conversacion c) {
        abierta = c;
        noLeidos.remove(c);
        PanelConversacion p = panel(c);
        tarjetas.show(derecha, c.tipo() + ":" + c.id());
        p.enfocar();
        refrescar();
    }

    private void nuevoGrupo() {
        if (conectados.isEmpty()) {
            error("No hay nadie más conectado.");
            return;
        }
        DialogoGrupo.pedir(this, conectados).ifPresent(r -> {
            usuario.crearGrupo(r.nombre(), r.miembros());
            Conversacion c = Conversacion.grupo(r.nombre());
            if (chats.contains(c)) abrir(c);
        });
    }

    /** Actualiza la lista, el estado de cada panel y el título de la ventana. */
    private void refrescar() {
        refrescando = true;
        modeloLista.clear();
        chats.forEach(modeloLista::addElement);
        if (abierta != null) lista.setSelectedValue(abierta, false);
        refrescando = false;

        paneles.forEach((c, p) -> {
            p.setSubtitulo(c.tipo().variosParticipantes()
                    ? String.join(", ", miembros.getOrDefault(c.id(), List.of())) : detalle(c));
            p.setActivo(disponible(c));
        });

        int total = noLeidos.values().stream().mapToInt(Integer::intValue).sum();
        setTitle((total > 0 ? "(" + total + ") " : "") + usuario.getNombre() + " — Chat");
    }

    // ================================================================ construcción

    private JPanel panelLateral() {
        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setBackground(Estilo.FONDO_LATERAL);
        lateral.setPreferredSize(new Dimension(300, 0));
        lateral.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Estilo.SEPARADOR));

        JPanel arriba = new JPanel(new BorderLayout(12, 0));
        arriba.setOpaque(false);
        arriba.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));
        arriba.add(new Avatar(usuario.getNombre(), 40, false), BorderLayout.WEST);
        JLabel yo = new JLabel(usuario.getNombre());
        yo.setFont(Estilo.fuente(Font.BOLD, 17f));
        yo.setForeground(Estilo.TEXTO);
        arriba.add(yo, BorderLayout.CENTER);
        JButton grupo = Estilo.boton("+ Grupo", false);
        grupo.addActionListener(e -> nuevoGrupo());
        arriba.add(grupo, BorderLayout.EAST);
        lateral.add(arriba, BorderLayout.NORTH);

        lista.setCellRenderer(new CeldaChat(this::disponible, this::detalle, c -> noLeidos.getOrDefault(c, 0)));
        lista.setBackground(Estilo.FONDO_LATERAL);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.addListSelectionListener(e -> {
            if (!refrescando && !e.getValueIsAdjusting() && lista.getSelectedValue() != null) {
                abrir(lista.getSelectedValue());
            }
        });
        lateral.add(Estilo.scroll(lista, Estilo.FONDO_LATERAL), BorderLayout.CENTER);
        return lateral;
    }

    private JPanel panelVacio() {
        JPanel vacio = new JPanel(new BorderLayout());
        vacio.setBackground(Estilo.FONDO_CHAT);
        JLabel texto = new JLabel("<html><center><b style='font-size:15px'>Hola, " + usuario.getNombre()
                + "</b><br><br>Elige un chat de la izquierda o crea un grupo.</center></html>", SwingConstants.CENTER);
        texto.setFont(Estilo.fuente(Font.PLAIN, 13.5f));
        texto.setForeground(Estilo.TEXTO_SUAVE);
        vacio.add(texto);
        return vacio;
    }

    private static void enEDT(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) r.run();
        else SwingUtilities.invokeLater(r);
    }
}
