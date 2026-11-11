package chat.mediador;

import chat.modelo.Mensaje;
import chat.usuarios.Usuario;

import java.util.Collection;

/**
 * Mediador (patrón Mediator).
 *
 * Define cómo se comunican los usuarios. Los usuarios solo conocen esta
 * interfaz: nunca tienen referencias a otros usuarios.
 */
public interface MediadorChat {

    void registrar(Usuario usuario);

    void darDeBaja(Usuario usuario);

    /** Entrega el mensaje a sus destinatarios y se lo confirma al emisor. */
    void enviar(Mensaje mensaje);

    void crearGrupo(Usuario creador, String nombreGrupo, Collection<String> miembros);
}
