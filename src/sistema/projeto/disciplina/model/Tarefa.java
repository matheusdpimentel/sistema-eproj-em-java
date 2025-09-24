package sistema.projeto.disciplina.model;

import java.time.LocalDate;

public class Tarefa {
    private Long id;
    private Long projetoId;
    private String titulo;
    private String descricao;
    private LocalDate dataEntrega;
    private Long responsavelId;   // pode ser null
    private String responsavelNome; // preenchido via JOIN
    private String status;        // "ABERTA", "EM_ANDAMENTO", "CONCLUIDA"

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjetoId() { return projetoId; }
    public void setProjetoId(Long projetoId) { this.projetoId = projetoId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(LocalDate dataEntrega) { this.dataEntrega = dataEntrega; }

    public Long getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Long responsavelId) { this.responsavelId = responsavelId; }

    public String getResponsavelNome() { return responsavelNome; }
    public void setResponsavelNome(String responsavelNome) { this.responsavelNome = responsavelNome; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
