package chat.usuarios;

import chat.mediador.MediadorChat;
import chat.modelo.Conversacion;
import chat.modelo.Mensaje;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Colega del patrón Mediator.
 *
 * Solo conoce a su mediador. No sabe nada de ventanas: lo que recibe se lo
 * pasa a su {@link OyenteUsuario}.
 */
public class Usuario implements AccionesUsuario {

    private final String nombre;
    private final MediadorChat mediador;
    private OyenteUsuario oyente;

    public Usuario(String nombre, MediadorChat mediador) {
        this.nombre = nombre;
        this.mediador = mediador;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setOyente(OyenteUsuario oyente) {
        this.oyente = oyente;
    }

    // ---------------------------------------------- acciones (se delegan en el mediador)

    public void conectar()    { mediador.registrar(this); }

    @Override
    public void desconectar() { mediador.darDeBaja(this); }

    @Override
    public void enviar(Conversacion conversacion, String texto) {
        if (texto != null && !texto.isBlank()) {
            mediador.enviar(Mensaje.nuevo(nombre, conversacion, texto.trim()));
        }
    }

    @Override
    public void crearGrupo(String nombreGrupo, Collection<String> miembros) {
        mediador.crearGrupo(this, nombreGrupo, miembros);
    }

    // ---------------------------------------------- avisos (los invoca el mediador)

    public void notificarMensaje(Mensaje m)                      { if (oyente != null) oyente.mensajeRecibido(m); }
    public void notificarConectados(Set<String> conectados)      { if (oyente != null) oyente.conectadosActualizados(conectados); }
    public void notificarGrupo(String grupo, List<String> miembros) { if (oyente != null) oyente.agregadoAGrupo(grupo, miembros); }
    public void notificarError(String mensaje)                   { if (oyente != null) oyente.error(mensaje); }
}
