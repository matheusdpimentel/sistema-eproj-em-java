package sistema.projeto.disciplina.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private static final String CONFIG_FILE = "config/db.properties";

    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        } catch (IOException e) {
            throw new SQLException(
                    "Banco de dados não configurado. Crie o arquivo config/db.properties com DB_URL, DB_USER e DB_PASS.",
                    e
            );
        }

        String url = props.getProperty("DB_URL");
        String user = props.getProperty("DB_USER");
        String pass = props.getProperty("DB_PASS");

        if (url == null || user == null || pass == null) {
            throw new SQLException(
                    "config/db.properties inválido. Esperado: DB_URL, DB_USER, DB_PASS."
            );
        }

        return DriverManager.getConnection(url, user, pass);
    }
}