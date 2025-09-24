package sistema.projeto.disciplina.model;

import java.time.LocalDate;

public class Projeto {
    private Long id;
    private String nome;
    private String descricao;
    private String status;          // "NOVO", "EM_ANDAMENTO", "CONCLUIDO"
    private LocalDate dataInicio;   
    private LocalDate dataFim;      
    private Long equipeId;         
    private String equipeNome;      // nome da equipe para exibição

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public Long getEquipeId() { return equipeId; }
    public void setEquipeId(Long equipeId) { this.equipeId = equipeId; }

    public String getEquipeNome() { return equipeNome; }
    public void setEquipeNome(String equipeNome) { this.equipeNome = equipeNome; }
}
