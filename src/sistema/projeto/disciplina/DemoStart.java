package sistema.projeto.disciplina;

import javax.swing.SwingUtilities;
import sistema.projeto.disciplina.model.Usuario;

public class DemoStart {

    public static void main(String[] args) {
        System.setProperty("EPROJ_DEMO", "true");

        SwingUtilities.invokeLater(() -> {
            Usuario demo = new Usuario();
            demo.setId(0L);
            demo.setNome("Usuário Demo");
            demo.setCargo("Administrador");

            TelaUsuarioPrincipal tela = new TelaUsuarioPrincipal(demo);
            tela.setLocationRelativeTo(null);
            tela.setVisible(true);
        });
    }
}