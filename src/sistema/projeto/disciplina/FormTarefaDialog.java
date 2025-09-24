
package sistema.projeto.disciplina;

import sistema.projeto.disciplina.model.Tarefa;
import sistema.projeto.disciplina.dao.UsuarioDAO;
import sistema.projeto.disciplina.model.Usuario;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FormTarefaDialog extends javax.swing.JDialog {

    private final Long projetoId;   // projeto ao qual a tarefa pertence
    private Tarefa tarefa;          // usado para editar/retornar
    private boolean okPressed = false;
    private final DateTimeFormatter BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // item para o combo de responsáveis
    private static class ComboItem {
        private final Long id;
        private final String label;
        ComboItem(Long id, String label) { this.id = id; this.label = label; }
        Long getId() { return id; }
        @Override public String toString() { return label; }
    }

    public FormTarefaDialog(java.awt.Frame parent, boolean modal, Long projetoId) {
        super(parent, modal);
        this.projetoId = projetoId;
        initComponents();
        setLocationRelativeTo(parent);
        carregarStatus();
        carregarResponsaveis();
    }

    // Getters de retorno 
    public Tarefa getTarefa()     { return tarefa; }
    public boolean isOkPressed()  { return okPressed; }

    // Pré-preencher para edição 
    public void setTarefa(Tarefa t) {
        this.tarefa = t;
        setTitle("Editar Tarefa");
        jLabel1.setText("Editar Tarefa");
        btnSalvar.setText("Salvar");

        txtTitulo.setText(safe(t.getTitulo()));
        txtDescricao.setText(safe(t.getDescricao()));
        cbStatus.setSelectedItem(safe(t.getStatus()));
        txtDataEntrega.setText(t.getDataEntrega() != null ? BR.format(t.getDataEntrega()) : "");

        // selecionar responsável no combo, se houver
        Long respId = t.getResponsavelId();
        if (respId == null) {
            cbResponsavel.setSelectedIndex(0); // — Selecionar —
        } else {
            for (int i = 0; i < cbResponsavel.getItemCount(); i++) {
                Object it = cbResponsavel.getItemAt(i);
                if (it instanceof ComboItem) {
                    ComboItem ci = (ComboItem) it;
                    if (ci.getId() != null && ci.getId().equals(respId)) {
                        cbResponsavel.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    //  Utils 
    private String safe(String s) { return s == null ? "" : s; }

    private LocalDate parseDate(String s) {
        s = (s == null) ? "" : s.trim();
        if (s.isEmpty()) return null;
        try {
            return LocalDate.parse(s, BR);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida (use dd/MM/yyyy).");
        }
    }

    private void carregarStatus() {    
        cbStatus.setModel(new DefaultComboBoxModel<String>(
                new String[]{"NOVA", "EM_ANDAMENTO", "CONCLUIDA"}
        ));
    }

    private void carregarResponsaveis() {
    DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
    model.addElement("— Selecionar —");

    try {
        //Descobre a equipe do projeto
        sistema.projeto.disciplina.dao.ProjetoDAO pdao = new sistema.projeto.disciplina.dao.ProjetoDAO();
        sistema.projeto.disciplina.model.Projeto projeto = pdao.findById(projetoId);
        if (projeto == null || projeto.getEquipeId() == null) {
            cbResponsavel.setModel(model); 
            return;
        }

        Long equipeId = projeto.getEquipeId();

        // Busca a equipe 
        sistema.projeto.disciplina.dao.EquipeDAO edao = new sistema.projeto.disciplina.dao.EquipeDAO();
        sistema.projeto.disciplina.model.Equipe equipe = edao.findById(equipeId);
        if (equipe == null) {
            cbResponsavel.setModel(model);
            return;
        }

        java.util.List<Long> membrosIds = (equipe.getMembrosIds() != null)
                ? equipe.getMembrosIds()
                : java.util.Collections.emptyList();

        // permitir o gestor ser responsável:
        boolean incluirGestorTambem = true;
        java.util.Set<Long> ids = new java.util.HashSet<>(membrosIds);
        if (incluirGestorTambem && equipe.getGestorId() != null) {
            ids.add(equipe.getGestorId());
        }

        //Carrega os usuários pelos IDs
        sistema.projeto.disciplina.dao.UsuarioDAO udao = new sistema.projeto.disciplina.dao.UsuarioDAO();

              
        java.util.List<sistema.projeto.disciplina.model.Usuario> responsaveis = new java.util.ArrayList<>();
        for (Long uid : ids) {
            sistema.projeto.disciplina.model.Usuario u = udao.findById(uid);
            if (u != null) responsaveis.add(u);
        }
    
        responsaveis.sort(java.util.Comparator.comparing(sistema.projeto.disciplina.model.Usuario::getNome,
                java.text.Collator.getInstance(new java.util.Locale("pt","BR"))));

        for (sistema.projeto.disciplina.model.Usuario u : responsaveis) {
            model.addElement(new ComboItem(u.getId(), u.getNome()));
        }
    } catch (RuntimeException ex) {
        // Em caso de erro
        System.err.println("Erro ao carregar responsáveis: " + ex.getMessage());
    }

    cbResponsavel.setModel(model);
}

    // monta/atualiza objeto Tarefa a partir do form
    private Tarefa coletarTarefaDoForm() {
        String titulo = txtTitulo.getText().trim();
        if (titulo.isEmpty()) throw new IllegalArgumentException("Informe o título da tarefa.");

        String descricao = txtDescricao.getText().trim();
        String status = (String) cbStatus.getSelectedItem();
        LocalDate entrega = parseDate(txtDataEntrega.getText());

        Long responsavelId = null;
        Object sel = cbResponsavel.getSelectedItem();
        if (sel instanceof ComboItem) {
            ComboItem ci = (ComboItem) sel;
            responsavelId = ci.getId();
        }

        Tarefa t = (tarefa != null) ? tarefa : new Tarefa();
        t.setProjetoId(projetoId);
        t.setTitulo(titulo);
        t.setDescricao(descricao);
        t.setStatus(status);
        t.setDataEntrega(entrega);
        t.setResponsavelId(responsavelId);

        return t;
    }


    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtTitulo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescricao = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        txtDataEntrega = new javax.swing.JFormattedTextField();
        cbResponsavel = new javax.swing.JComboBox<>();
        cbStatus = new javax.swing.JComboBox<>();
        btnCancelar = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel1.setText("Criar Tarefa");

        jLabel3.setText("Tarefa");

        jLabel4.setText("Status");

        txtDescricao.setColumns(20);
        txtDescricao.setRows(5);
        jScrollPane1.setViewportView(txtDescricao);

        jLabel5.setText("Responsável");

        txtDataEntrega.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));

        cbResponsavel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        jLabel2.setText("Data Entrega (dd/mm/aaaa)");

        jLabel6.setText("Descrição");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(122, 122, 122)
                                .addComponent(jLabel1))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(txtDataEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(51, 51, 51)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel4)
                                        .addComponent(cbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3))
                                    .addGap(55, 55, 55)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5)
                                        .addComponent(cbResponsavel, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(51, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(btnCancelar)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnSalvar))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))
                            .addComponent(jLabel6))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbResponsavel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2))
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDataEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addGap(7, 7, 7)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCancelar)
                    .addComponent(btnSalvar))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(19, 19, 19))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        try {
            this.tarefa = coletarTarefaDoForm();
            this.okPressed = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao coletar dados: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }  
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JComboBox<Object> cbResponsavel;
    private javax.swing.JComboBox<String> cbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JFormattedTextField txtDataEntrega;
    private javax.swing.JTextArea txtDescricao;
    private javax.swing.JTextField txtTitulo;
    // End of variables declaration//GEN-END:variables
}
