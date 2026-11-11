package chat.mediador;

import chat.modelo.Conversacion;
import chat.modelo.Mensaje;
import chat.usuarios.Usuario;

import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mediador concreto: conoce a todos los usuarios y grupos y coordina la
 * comunicación. Para decidir los destinatarios delega en una
 * {@link EstrategiaEntrega} según el tipo de conversación.
 */
public class SalaChat implements MediadorChat, Directorio {

    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();
    private final Map<String, Set<String>> grupos = new LinkedHashMap<>();
    private final Map<Conversacion.Tipo, EstrategiaEntrega> estrategias = new EnumMap<>(Conversacion.Tipo.class);

    public SalaChat(List<EstrategiaEntrega> estrategias) {
        estrategias.forEach(e -> this.estrategias.put(e.tipo(), e));
    }

    @Override
    public synchronized void registrar(Usuario usuario) {
        if (usuarios.putIfAbsent(usuario.getNombre(), usuario) != null) {
            usuario.notificarError("Ya hay un usuario llamado «" + usuario.getNombre() + "».");
            return;
        }
        avisarConectados();
    }

    @Override
    public synchronized void darDeBaja(Usuario usuario) {
        if (usuarios.remove(usuario.getNombre(), usuario)) avisarConectados();
    }

    @Override
    public synchronized void enviar(Mensaje mensaje) {
        Usuario emisor = usuarios.get(mensaje.emisor());
        if (emisor == null) return;
        EstrategiaEntrega estrategia = estrategias.get(mensaje.conversacion().tipo());
        try {
            List<String> destinatarios = estrategia.destinatarios(emisor.getNombre(), mensaje.conversacion(), this);
            Mensaje recibido = mensaje.vistoEn(estrategia.vistaDelReceptor(emisor.getNombre(), mensaje.conversacion()));
            emisor.notificarMensaje(mensaje);                       // confirmación al emisor
            destinatarios.forEach(d -> usuarios.get(d).notificarMensaje(recibido));
        } catch (IllegalArgumentException e) {
            emisor.notificarError(e.getMessage());
        }
    }

    @Override
    public synchronized void crearGrupo(Usuario creador, String nombreGrupo, Collection<String> miembros) {
        String nombre = nombreGrupo == null ? "" : nombreGrupo.trim();
        if (nombre.isEmpty() || grupos.containsKey(nombre)) {
            creador.notificarError(nombre.isEmpty() ? "El grupo necesita un nombre." : "Ese grupo ya existe.");
            return;
        }
        Set<String> grupo = new LinkedHashSet<>();
        grupo.add(creador.getNombre());
        miembros.stream().filter(usuarios::containsKey).forEach(grupo::add);
        if (grupo.size() < 2) {
            creador.notificarError("Elige al menos otro miembro.");
            return;
        }
        grupos.put(nombre, grupo);
        List<String> lista = List.copyOf(grupo);
        lista.forEach(m -> usuarios.get(m).notificarGrupo(nombre, lista));
    }

    // ---------------------------------------------------------------- Directorio

    @Override
    public boolean estaConectado(String usuario) {
        return usuarios.containsKey(usuario);
    }

    @Override
    public Set<String> miembros(String grupo) {
        return grupos.getOrDefault(grupo, Set.of());
    }

    private void avisarConectados() {
        Set<String> conectados = new LinkedHashSet<>(usuarios.keySet());   // en orden de conexión
        usuarios.values().forEach(u -> u.notificarConectados(conectados));
    }
}
