package sistema.projeto.disciplina.model;

import java.util.ArrayList;
import java.util.List;

public class Equipe {
    private Long id;
    private String nome;
    private Long gestorId;
    private String gestorNome; 
    private List<Long> membrosIds = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Long getGestorId() { return gestorId; }
    public void setGestorId(Long gestorId) { this.gestorId = gestorId; }
    public String getGestorNome() { return gestorNome; }
    public void setGestorNome(String gestorNome) { this.gestorNome = gestorNome; }
    public List<Long> getMembrosIds() { return membrosIds; }
    public void setMembrosIds(List<Long> membrosIds) { this.membrosIds = membrosIds; }
}
