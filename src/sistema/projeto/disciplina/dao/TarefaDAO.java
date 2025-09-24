package sistema.projeto.disciplina.dao;

import sistema.projeto.disciplina.db.ConnectionFactory;
import sistema.projeto.disciplina.model.Tarefa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    private Tarefa map(ResultSet rs) throws SQLException {
        Tarefa t = new Tarefa();
        t.setId(rs.getLong("id"));
        t.setProjetoId(rs.getLong("projeto_id"));
        t.setTitulo(rs.getString("titulo"));
        t.setDescricao(rs.getString("descricao"));

        Date de = rs.getDate("data_entrega");
        t.setDataEntrega(de != null ? de.toLocalDate() : null);

        long resp = rs.getLong("responsavel_id");
        t.setResponsavelId(rs.wasNull() ? null : resp);

        try {
            t.setResponsavelNome(rs.getString("responsavel_nome")); // pode vir de LEFT JOIN
        } catch (SQLException ignore) {}

        t.setStatus(rs.getString("status"));
        return t;
    }

    public List<Tarefa> listarPorProjeto(Long projetoId) {
        String sql = "SELECT t.id, t.projeto_id, t.titulo, t.descricao, t.data_entrega, " +
                     "       t.responsavel_id, u.nome AS responsavel_nome, t.status " +
                     "FROM tarefas t " +
                     "LEFT JOIN usuarios u ON u.id = t.responsavel_id " +
                     "WHERE t.projeto_id = ? " +
                     "ORDER BY t.id DESC";
        List<Tarefa> list = new ArrayList<Tarefa>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, projetoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tarefas: " + e.getMessage(), e);
        }
        return list;
    }

    public Tarefa findById(Long id) {
        String sql = "SELECT t.id, t.projeto_id, t.titulo, t.descricao, t.data_entrega, " +
                     "       t.responsavel_id, t.status " +
                     "FROM tarefas t WHERE t.id = ? LIMIT 1";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tarefa: " + e.getMessage(), e);
        }
        return null;
    }

    public void insert(Tarefa t) {
        String sql = "INSERT INTO tarefas (projeto_id, titulo, descricao, data_entrega, responsavel_id, status) " +
                     "VALUES (?,?,?,?,?,?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, t.getProjetoId());
            ps.setString(2, t.getTitulo());
            ps.setString(3, t.getDescricao());

            if (t.getDataEntrega() != null) ps.setDate(4, Date.valueOf(t.getDataEntrega()));
            else ps.setNull(4, Types.DATE);

            if (t.getResponsavelId() != null) ps.setLong(5, t.getResponsavelId());
            else ps.setNull(5, Types.BIGINT);

            ps.setString(6, t.getStatus());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) t.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir tarefa: " + e.getMessage(), e);
        }
    }

    public void update(Tarefa t) {
        String sql = "UPDATE tarefas SET titulo=?, descricao=?, data_entrega=?, responsavel_id=?, status=? " +
                     "WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, t.getTitulo());
            ps.setString(2, t.getDescricao());

            if (t.getDataEntrega() != null) ps.setDate(3, Date.valueOf(t.getDataEntrega()));
            else ps.setNull(3, Types.DATE);

            if (t.getResponsavelId() != null) ps.setLong(4, t.getResponsavelId());
            else ps.setNull(4, Types.BIGINT);

            ps.setString(5, t.getStatus());
            ps.setLong(6, t.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar tarefa: " + e.getMessage(), e);
        }
    }

    public void updateStatus(Long tarefaId, String status) {
        String sql = "UPDATE tarefas SET status=? WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, tarefaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status: " + e.getMessage(), e);
        }
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM tarefas WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir tarefa: " + e.getMessage(), e);
        }
    }
    
    // Lista tarefas vinculadas ao usuário, ordenadas pela data de entrega
    public List<Tarefa> listarPorUsuario(long usuarioId) {
    String sql =
        "SELECT t.id, t.projeto_id, t.titulo, t.descricao, t.data_entrega, " +
        "       t.responsavel_id, u.nome AS responsavel_nome, t.status " +
        "FROM tarefas t " +
        "LEFT JOIN usuarios u ON u.id = t.responsavel_id " +
        "WHERE t.responsavel_id = ? " +
        "   OR t.projeto_id IN ( " +
        "       SELECT p.id " +
        "       FROM projetos p " +
        "       JOIN equipe_membros em ON em.equipe_id = p.equipe_id " +
        "       WHERE em.usuario_id = ? " +
        "   ) " +
        "ORDER BY t.data_entrega IS NULL, t.data_entrega ASC, t.id DESC";
    List<Tarefa> list = new ArrayList<>();
    try (Connection c = ConnectionFactory.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setLong(1, usuarioId);
        ps.setLong(2, usuarioId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tarefa t = new Tarefa();
                t.setId(rs.getLong("id"));
                t.setProjetoId(rs.getLong("projeto_id"));
                t.setTitulo(rs.getString("titulo"));
                t.setDescricao(rs.getString("descricao"));
                Date de = rs.getDate("data_entrega");
                t.setDataEntrega(de != null ? de.toLocalDate() : null);
                long resp = rs.getLong("responsavel_id");
                t.setResponsavelId(rs.wasNull() ? null : resp);
                try { t.setResponsavelNome(rs.getString("responsavel_nome")); } catch (SQLException ignore) {}
                t.setStatus(rs.getString("status"));
                list.add(t);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Erro ao listar tarefas do usuário: " + e.getMessage(), e);
    }
    return list;
    }

    
}
