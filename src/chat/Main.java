package chat;

import chat.gui.VentanaUsuario;
import chat.mediador.EntregaGrupo;
import chat.mediador.EntregaPrivada;
import chat.mediador.MediadorChat;
import chat.mediador.SalaChat;
import chat.usuarios.Usuario;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 * Punto de entrada: crea el mediador y una ventana por usuario.
 *
 * Uso:  java -cp out chat.Main [usuario1 usuario2 ...]
 */
public class Main {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        List<String> nombres = args.length > 0 ? List.of(args) : List.of("Ana", "Luis", "Marta", "Pedro");

        MediadorChat sala = new SalaChat(List.of(new EntregaPrivada(), new EntregaGrupo()));

        SwingUtilities.invokeLater(() -> {
            List<Usuario> usuarios = new ArrayList<>();
            for (int i = 0; i < nombres.size(); i++) {
                Usuario usuario = new Usuario(nombres.get(i), sala);
                VentanaUsuario ventana = new VentanaUsuario(usuario);
                usuario.setOyente(ventana);
                ventana.setLocation(60 + i * 40, 60 + i * 40);
                ventana.setVisible(true);
                usuario.conectar();
                usuarios.add(usuario);
            }
            // Grupo de ejemplo para ver el apartado b) desde el principio
            if (usuarios.size() >= 3) {
                usuarios.get(0).crearGrupo("Equipo", List.of(nombres.get(1), nombres.get(2)));
            }
        });
    }
}
