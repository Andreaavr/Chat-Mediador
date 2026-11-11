package chat.usuarios;

import chat.modelo.Mensaje;

import java.util.List;
import java.util.Set;

/** Quien quiere enterarse de lo que le llega a un usuario (normalmente, su ventana). */
public interface OyenteUsuario {

    void mensajeRecibido(Mensaje mensaje);

    void conectadosActualizados(Set<String> conectados);

    void agregadoAGrupo(String grupo, List<String> miembros);

    void error(String mensaje);
}
