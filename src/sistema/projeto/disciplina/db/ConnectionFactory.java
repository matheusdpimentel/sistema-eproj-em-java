
package sistema.projeto.disciplina.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Classe criada para conectar com meu banco de dados - Usuario, Equipe, Projeto, Tarefa

public class ConnectionFactory {
    // URL de conexão 
    private static final String URL  = "jdbc:mysql://127.0.0.1:3306/eproj?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "eproj_user";   // usuário 
    private static final String PASS = "SenhaFort3!";  // senha 

    // Método público que entrega uma conexão pronta
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

