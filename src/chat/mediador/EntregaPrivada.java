package chat.mediador;

import chat.modelo.Conversacion;

import java.util.List;

/** Estrategia concreta: conversación uno a uno. */
public class EntregaPrivada implements EstrategiaEntrega {

    @Override
    public Conversacion.Tipo tipo() {
        return Conversacion.Tipo.PRIVADA;
    }

    @Override
    public List<String> destinatarios(String emisor, Conversacion c, Directorio directorio) {
        if (!directorio.estaConectado(c.id())) {
            throw new IllegalArgumentException(c.id() + " no está conectado.");
        }
        return List.of(c.id());
    }

    @Override
    public Conversacion vistaDelReceptor(String emisor, Conversacion c) {
        return Conversacion.privada(emisor);   // para el receptor, el chat es "con el emisor"
    }
}
