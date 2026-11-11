package chat.mediador;

import chat.modelo.Conversacion;

import java.util.List;

/**
 * Estrategia (patrón Strategy): decide a quién se entrega lo que se envía a
 * un tipo de conversación. Así el mediador no necesita ningún
 * {@code if (privada) ... else (grupo) ...}.
 */
public interface EstrategiaEntrega {

    Conversacion.Tipo tipo();

    /**
     * Destinatarios (sin incluir al emisor).
     *
     * @throws IllegalArgumentException con el motivo, si el envío no es posible
     */
    List<String> destinatarios(String emisor, Conversacion conversacion, Directorio directorio);

    /** Cómo ve la conversación cada receptor. */
    Conversacion vistaDelReceptor(String emisor, Conversacion conversacion);
}
