package chat.modelo;

import java.time.LocalTime;

/** Mensaje inmutable que viaja a través del mediador. */
public record Mensaje(String emisor, Conversacion conversacion, String texto, LocalTime hora) {

    public static Mensaje nuevo(String emisor, Conversacion conversacion, String texto) {
        return new Mensaje(emisor, conversacion, texto, LocalTime.now());
    }

    /** El mismo mensaje tal y como lo ve el receptor (en privado, la conversación es "el emisor"). */
    public Mensaje vistoEn(Conversacion otra) {
        return new Mensaje(emisor, otra, texto, hora);
    }
}
