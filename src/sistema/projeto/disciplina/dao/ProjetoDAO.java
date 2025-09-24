package sistema.projeto.disciplina.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import sistema.projeto.disciplina.db.ConnectionFactory;
import sistema.projeto.disciplina.model.Projeto;

public class ProjetoDAO {

    // Lista todos os projetos (com datas e nome da equipe)
    public List<Projeto> findAll() {
        String sql =
            "SELECT p.id, p.nome, p.descricao, p.status, p.data_inicio, p.data_fim, " +
            "       p.equipe_id, e.nome AS equipe_nome " +
            "FROM projetos p " +
            "LEFT JOIN equipes e ON e.id = p.equipe_id " +
            "ORDER BY p.id DESC";

        List<Projeto> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Projeto p = mapRow(rs);
                list.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar projetos: " + e.getMessage(), e);
        }
        return list;
    }

    // Busca projeto por ID
    public Projeto findById(long id) {
        String sql =
            "SELECT p.id, p.nome, p.descricao, p.status, p.data_inicio, p.data_fim, " +
            "       p.equipe_id, e.nome AS equipe_nome " +
            "FROM projetos p " +
            "LEFT JOIN equipes e ON e.id = p.equipe_id " +
            "WHERE p.id = ? " +
            "LIMIT 1";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar projeto: " + e.getMessage(), e);
        }
        return null;
    }

    // Lista por equipe para filtrar por equipe específica
    public List<Projeto> findByEquipeId(long equipeId) {
        String sql =
            "SELECT p.id, p.nome, p.descricao, p.status, p.data_inicio, p.data_fim, " +
            "       p.equipe_id, e.nome AS equipe_nome " +
            "FROM projetos p " +
            "LEFT JOIN equipes e ON e.id = p.equipe_id " +
            "WHERE p.equipe_id = ? " +
            "ORDER BY p.id DESC";

        List<Projeto> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, equipeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar por equipe: " + e.getMessage(), e);
        }
        return list;
    }

    // Insere com datas e equipe
    public void insert(Projeto p) {
        String sql = "INSERT INTO projetos (nome, descricao, status, data_inicio, data_fim, equipe_id) " +
                     "VALUES (?,?,?,?,?,?)";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescricao());
            ps.setString(3, p.getStatus());

            if (p.getDataInicio() != null) {
                ps.setDate(4, Date.valueOf(p.getDataInicio()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            if (p.getDataFim() != null) {
                ps.setDate(5, Date.valueOf(p.getDataFim()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            if (p.getEquipeId() == null) {
                ps.setNull(6, Types.BIGINT);
            } else {
                ps.setLong(6, p.getEquipeId());
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir projeto: " + e.getMessage(), e);
        }
    }

    // Lista projetos vinculados ao usuário via equipes que ele integra
    public List<Projeto> findByUsuarioId(long usuarioId) {
        String sql =
            "SELECT p.id, p.nome, p.descricao, p.status, p.data_inicio, p.data_fim, " +
            "       p.equipe_id, e.nome AS equipe_nome " +
            "FROM projetos p " +
            "JOIN equipes e           ON e.id = p.equipe_id " +
            "JOIN equipe_membros em   ON em.equipe_id = e.id " +
            "WHERE em.usuario_id = ? " +
            "GROUP BY p.id " +
            "ORDER BY p.data_fim IS NULL, p.data_fim ASC, p.id DESC";
        List<Projeto> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar projetos do usuário: " + e.getMessage(), e);
            }
            return list;
    }
    
    // Atualiza
    public void update(Projeto p) {
        String sql = "UPDATE projetos SET nome=?, descricao=?, status=?, data_inicio=?, data_fim=?, equipe_id=? " +
                     "WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescricao());
            ps.setString(3, p.getStatus());

            if (p.getDataInicio() != null) {
                ps.setDate(4, Date.valueOf(p.getDataInicio()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            if (p.getDataFim() != null) {
                ps.setDate(5, Date.valueOf(p.getDataFim()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            if (p.getEquipeId() == null) {
                ps.setNull(6, Types.BIGINT);
            } else {
                ps.setLong(6, p.getEquipeId());
            }

            ps.setLong(7, p.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar projeto: " + e.getMessage(), e);
        }
    }

    // Exclui
    public void deleteById(long id) {
        String sql = "DELETE FROM projetos WHERE id=?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir projeto: " + e.getMessage(), e);
        }
    }

    
    // mapeia uma linha
    
    private Projeto mapRow(ResultSet rs) throws SQLException {
        Projeto p = new Projeto();
        p.setId(rs.getLong("id"));
        p.setNome(rs.getString("nome"));
        p.setDescricao(rs.getString("descricao"));
        p.setStatus(rs.getString("status"));

        Date di = rs.getDate("data_inicio");
        Date df = rs.getDate("data_fim");
        p.setDataInicio(di != null ? di.toLocalDate() : null);
        p.setDataFim(df != null ? df.toLocalDate() : null);

        long equipe = rs.getLong("equipe_id");
        if (rs.wasNull()) {
            p.setEquipeId(null);
        } else {
            p.setEquipeId(equipe);
        }

        try {
            p.setEquipeNome(rs.getString("equipe_nome"));
        } catch (SQLException ignore) {
           
        }

        return p;
    }

    public List<Projeto> listarPorUsuario(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
