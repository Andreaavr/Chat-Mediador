package chat.mediador;

import chat.modelo.Conversacion;

import java.util.List;
import java.util.Set;

/** Estrategia concreta: conversación de grupo (todos los miembros conectados menos el emisor). */
public class EntregaGrupo implements EstrategiaEntrega {

    @Override
    public Conversacion.Tipo tipo() {
        return Conversacion.Tipo.GRUPO;
    }

    @Override
    public List<String> destinatarios(String emisor, Conversacion c, Directorio directorio) {
        Set<String> miembros = directorio.miembros(c.id());
        if (!miembros.contains(emisor)) {
            throw new IllegalArgumentException("No perteneces al grupo «" + c.id() + "».");
        }
        return miembros.stream()
                .filter(m -> !m.equals(emisor) && directorio.estaConectado(m))
                .toList();
    }

    @Override
    public Conversacion vistaDelReceptor(String emisor, Conversacion c) {
        return c;
    }
}
