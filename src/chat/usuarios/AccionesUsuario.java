package chat.usuarios;

import chat.modelo.Conversacion;

import java.util.Collection;

/** Lo que la ventana puede pedirle a un usuario. La ventana depende de esto, no de {@link Usuario} (DIP). */
public interface AccionesUsuario {

    String getNombre();

    void enviar(Conversacion conversacion, String texto);

    void crearGrupo(String nombreGrupo, Collection<String> miembros);

    void desconectar();
}
