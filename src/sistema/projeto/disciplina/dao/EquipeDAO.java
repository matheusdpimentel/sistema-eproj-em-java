package sistema.projeto.disciplina.dao;

import sistema.projeto.disciplina.db.ConnectionFactory;
import sistema.projeto.disciplina.model.Equipe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {

    // Obtém conexão e converte SQLException em RuntimeException 
    private Connection getConn() {
        try {
            return ConnectionFactory.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter conexão com o banco.", e);
        }
    }

    // Lista equipes com nome do gestor
    public List<Equipe> findAll() {
        String sql = "SELECT e.id, e.nome, e.gestor_id, u.nome AS gestor_nome " +
                     "FROM equipes e " +
                     "JOIN usuarios u ON u.id = e.gestor_id " +
                     "ORDER BY e.id DESC";
        List<Equipe> out = new ArrayList<Equipe>();
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipe e = new Equipe();
                e.setId(rs.getLong("id"));
                e.setNome(rs.getString("nome"));
                e.setGestorId(rs.getLong("gestor_id"));
                e.setGestorNome(rs.getString("gestor_nome"));
                out.add(e);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar equipes: " + ex.getMessage(), ex);
        }
        return out;
    }

    // Busca equipe e membros
    public Equipe findById(Long id) {
        String cab = "SELECT e.id, e.nome, e.gestor_id, u.nome AS gestor_nome " +
                     "FROM equipes e JOIN usuarios u ON u.id = e.gestor_id WHERE e.id=?";
        String mem = "SELECT usuario_id FROM equipe_membros WHERE equipe_id=?";
        try (Connection c = getConn()) {
            Equipe e = null;
            try (PreparedStatement ps = c.prepareStatement(cab)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        e = new Equipe();
                        e.setId(rs.getLong("id"));
                        e.setNome(rs.getString("nome"));
                        e.setGestorId(rs.getLong("gestor_id"));
                        e.setGestorNome(rs.getString("gestor_nome"));
                    }
                }
            }
            if (e == null) return null;

            List<Long> membros = new ArrayList<Long>();
            try (PreparedStatement ps = c.prepareStatement(mem)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) membros.add(rs.getLong(1));
                }
            }
            e.setMembrosIds(membros);
            return e;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao buscar equipe: " + ex.getMessage(), ex);
        }
    }

    public void insert(Equipe e) {
        String ins  = "INSERT INTO equipes (nome, gestor_id) VALUES (?, ?)";
        String insM = "INSERT INTO equipe_membros (equipe_id, usuario_id) VALUES (?, ?)";

        Connection c = getConn();
        try {
            c.setAutoCommit(false);
            Long novoId = null;

            try (PreparedStatement ps = c.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, e.getNome());
                ps.setLong(2, e.getGestorId());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Sem ID gerado.");
                    novoId = keys.getLong(1);
                }
            }

            if (e.getMembrosIds() != null && !e.getMembrosIds().isEmpty()) {
                try (PreparedStatement ps = c.prepareStatement(insM)) {
                    for (Long uid : e.getMembrosIds()) {
                        ps.setLong(1, novoId);
                        ps.setLong(2, uid);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            c.commit();
        } catch (SQLException ex) {
            try { c.rollback(); } catch (SQLException ignore) {}
            throw new RuntimeException("Erro ao inserir equipe: " + ex.getMessage(), ex);
        } finally {
            try { c.setAutoCommit(true); } catch (SQLException ignore) {}
            try { c.close(); } catch (SQLException ignore) {}
        }
    }

    public void update(Equipe e) {
        String up   = "UPDATE equipes SET nome=?, gestor_id=? WHERE id=?";
        String delM = "DELETE FROM equipe_membros WHERE equipe_id=?";
        String insM = "INSERT INTO equipe_membros (equipe_id, usuario_id) VALUES (?, ?)";

        Connection c = getConn();
        try {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(up)) {
                ps.setString(1, e.getNome());
                ps.setLong(2, e.getGestorId());
                ps.setLong(3, e.getId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(delM)) {
                ps.setLong(1, e.getId());
                ps.executeUpdate();
            }

            if (e.getMembrosIds() != null && !e.getMembrosIds().isEmpty()) {
                try (PreparedStatement ps = c.prepareStatement(insM)) {
                    for (Long uid : e.getMembrosIds()) {
                        ps.setLong(1, e.getId());
                        ps.setLong(2, uid);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            c.commit();
        } catch (SQLException ex) {
            try { c.rollback(); } catch (SQLException ignore) {}
            throw new RuntimeException("Erro ao atualizar equipe: " + ex.getMessage(), ex);
        } finally {
            try { c.setAutoCommit(true); } catch (SQLException ignore) {}
            try { c.close(); } catch (SQLException ignore) {}
        }
    }

    public void deleteById(Long id) {
        String delM = "DELETE FROM equipe_membros WHERE equipe_id=?";
        String delE = "DELETE FROM equipes WHERE id=?";

        Connection c = getConn();
        try {
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement(delM)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(delE)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            c.commit();
        } catch (SQLException ex) {
            try { c.rollback(); } catch (SQLException ignore) {}
            throw new RuntimeException("Erro ao excluir equipe: " + ex.getMessage(), ex);
        } finally {
            try { c.setAutoCommit(true); } catch (SQLException ignore) {}
            try { c.close(); } catch (SQLException ignore) {}
        }
    }
    
    // Equipes onde o usuário está vinculado
    public List<Equipe> findByUsuarioId(long usuarioId) {
        String sql =
            "SELECT e.id, e.nome, e.gestor_id, ug.nome AS gestor_nome " +
            "FROM equipes e " +
            "JOIN equipe_membros em ON em.equipe_id = e.id " +
            "LEFT JOIN usuarios ug ON ug.id = e.gestor_id " +
            "WHERE em.usuario_id = ? " +
            "GROUP BY e.id " +
            "ORDER BY e.nome ASC";
        List<Equipe> list = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Equipe e = new Equipe();
                    e.setId(rs.getLong("id"));
                    e.setNome(rs.getString("nome"));
                    long gid = rs.getLong("gestor_id");
                    e.setGestorId(rs.wasNull() ? null : gid);
                    try { e.setGestorNome(rs.getString("gestor_nome")); } catch (SQLException ignore) {}
                    list.add(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar equipes do usuário: " + e.getMessage(), e);
        }
        return list;
    }
    
}
