package chat.modelo;

/**
 * Identifica una conversación desde el punto de vista de un usuario:
 * - PRIVADA: {@code id} es el nombre del otro usuario.
 * - GRUPO:   {@code id} es el nombre del grupo.
 */
public record Conversacion(Tipo tipo, String id) {

    /**
     * Cada tipo declara sus características. Quien necesite comportarse distinto
     * pregunta por la característica, no por el tipo: así un tipo nuevo solo
     * tiene que declarar las suyas y nadie más cambia (OCP).
     */
    public enum Tipo {
        PRIVADA(false, true),
        GRUPO(true, false);

        private final boolean variosParticipantes;
        private final boolean requiereConexion;

        Tipo(boolean variosParticipantes, boolean requiereConexion) {
            this.variosParticipantes = variosParticipantes;
            this.requiereConexion = requiereConexion;
        }

        /** ¿Participan más de dos personas? (hay que indicar quién envía cada mensaje). */
        public boolean variosParticipantes() { return variosParticipantes; }

        /** ¿Solo se puede escribir si el otro usuario está conectado? */
        public boolean requiereConexion()    { return requiereConexion; }
    }

    public static Conversacion privada(String otroUsuario) {
        return new Conversacion(Tipo.PRIVADA, otroUsuario);
    }

    public static Conversacion grupo(String nombreGrupo) {
        return new Conversacion(Tipo.GRUPO, nombreGrupo);
    }
}
