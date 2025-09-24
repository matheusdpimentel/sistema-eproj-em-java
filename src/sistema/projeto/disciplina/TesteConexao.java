package sistema.projeto.disciplina;

import java.sql.Connection;
import java.sql.DriverManager;

/*
* classe criada somente para teste de conexao com a base de dados 
* neste caso conectada ao meu PC
*/

public class TesteConexao {
  public static void main(String[] args) {
    try (Connection c = DriverManager.getConnection(
        "jdbc:mysql://127.0.0.1:3306/eproj?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
        "eproj_user",
        "SenhaFort3!"
    )) {
      System.out.println("Conectou com a minha base MySQL!");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}


