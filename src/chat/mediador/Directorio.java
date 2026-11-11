package chat.mediador;

import java.util.Set;

/** Lo que una estrategia de entrega necesita consultar de la sala. */
public interface Directorio {

    boolean estaConectado(String usuario);

    /** Miembros del grupo, o un conjunto vacío si no existe. */
    Set<String> miembros(String grupo);
}
