package sistema.projeto.disciplina.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt; 
import sistema.projeto.disciplina.db.ConnectionFactory;
import sistema.projeto.disciplina.model.Usuario;

public class UsuarioDAO {

    public boolean salvar(Usuario usuario) {
        String hash = BCrypt.hashpw(usuario.getSenhaHash(), BCrypt.gensalt());

        String sql = "INSERT INTO usuarios (nome, email, usuario, cpf, telefone, cargo, senha_hash) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getUsuario());
            stmt.setString(4, usuario.getCpf());
            stmt.setString(5, usuario.getTelefone());
            stmt.setString(6, usuario.getCargo());
            stmt.setString(7, hash); 

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar usuário: " + e.getMessage(), e);
        }
    }
    
    public Usuario findByUsuarioOrEmail(String login) {
        final String sql = "SELECT id, nome, email, usuario, cpf, telefone, cargo, senha_hash " +
                           "FROM usuarios WHERE usuario = ? OR email = ? LIMIT 1";

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getLong("id"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setCpf(rs.getString("cpf"));
                    u.setTelefone(rs.getString("telefone"));
                    u.setCargo(rs.getString("cargo"));
                    u.setSenhaHash(rs.getString("senha_hash"));
                    return u;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário: " + e.getMessage(), e);
        }
        return null;
    }
   
    public Usuario autenticar(String login, String senhaDigitada) {
        Usuario u = findByUsuarioOrEmail(login);
        if (u == null) return null;

        String hash = u.getSenhaHash();
        if (hash == null || hash.isEmpty()) return null;

        boolean ok = BCrypt.checkpw(senhaDigitada, hash);
        return ok ? u : null;
    }

    public List<Usuario> listarGestores() {
        String sql = "SELECT id, nome, email, usuario, cpf, telefone, cargo " +
                     "FROM usuarios " +
                     "WHERE cargo IN ('Gestor','Administrador') " +
                     "ORDER BY nome ASC";

        List<Usuario> gestores = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getLong("id"));
                u.setNome(rs.getString("nome"));
                u.setEmail(rs.getString("email"));
                u.setUsuario(rs.getString("usuario"));
                u.setCpf(rs.getString("cpf"));
                u.setTelefone(rs.getString("telefone"));
                u.setCargo(rs.getString("cargo"));
                gestores.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar gestores: " + e.getMessage(), e);
        }
        return gestores;
    }

    public List<Usuario> listarUsuarios() {
        String sql = "SELECT id, nome FROM usuarios ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();

        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getLong("id"));
                u.setNome(rs.getString("nome"));
                lista.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }

        return lista;
    }

    public List<Usuario> listarTodos() {
        String sql = "SELECT id, nome FROM usuarios ORDER BY nome";
        List<Usuario> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getLong("id"));
                u.setNome(rs.getString("nome"));
                list.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }
        return list;
    }

    // Lista apenas colaboradores (que não são gestores/admins)
    public List<Usuario> listarColaboradores() {
        String sql = "SELECT id, nome, email, usuario, cpf, telefone, cargo " +
                     "FROM usuarios " +
                     "WHERE cargo = 'Colaborador' " +
                     "ORDER BY nome ASC";

        List<Usuario> colaboradores = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getLong("id"));
                u.setNome(rs.getString("nome"));
                u.setEmail(rs.getString("email"));
                u.setUsuario(rs.getString("usuario"));
                u.setCpf(rs.getString("cpf"));
                u.setTelefone(rs.getString("telefone"));
                u.setCargo(rs.getString("cargo"));
                colaboradores.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar colaboradores: " + e.getMessage(), e);
        }
        return colaboradores;
    }

    // Buscar usuário por ID
    public Usuario findById(Long id) {
        String sql = "SELECT id, nome, email, usuario, cpf, telefone, cargo " +
                     "FROM usuarios WHERE id = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getLong("id"));
                    u.setNome(rs.getString("nome"));
                    u.setEmail(rs.getString("email"));
                    u.setUsuario(rs.getString("usuario"));
                    u.setCpf(rs.getString("cpf"));
                    u.setTelefone(rs.getString("telefone"));
                    u.setCargo(rs.getString("cargo"));
                    return u;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por id: " + e.getMessage(), e);
        }
        return null;
    }
}
