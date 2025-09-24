
package sistema.projeto.disciplina;

import java.awt.CardLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import sistema.projeto.disciplina.dao.ProjetoDAO;
import sistema.projeto.disciplina.dao.TarefaDAO;
import sistema.projeto.disciplina.model.Projeto;
import sistema.projeto.disciplina.model.Tarefa;

import javax.swing.DefaultListModel;
import java.util.ArrayList;
import sistema.projeto.disciplina.dao.EquipeDAO;
import sistema.projeto.disciplina.dao.UsuarioDAO;
import sistema.projeto.disciplina.model.Equipe;
import sistema.projeto.disciplina.model.Usuario;


/**
 *  Matheus Pimentel
 */
public class TelaUsuarioPrincipal extends javax.swing.JFrame {
    
    private final ProjetoDAO projetoDAO = new ProjetoDAO();
    private final DateTimeFormatter BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");   
    private static class ComboItem {
    private final Long id;
    private final String label;
    ComboItem(Long id, String label) { this.id = id; this.label = label; }
    Long getId() { return id; }
    @Override public String toString() { return label; }
  }
    private Long equipeEditandoId = null;

    // Permissões por cargo 
    private boolean isAdmin() {
        return usuarioLogado != null && "ADMIN".equalsIgnoreCase(usuarioLogado.getCargo());
    }
    private boolean isGestor() {
        if (usuarioLogado == null) return false;
            String c = usuarioLogado.getCargo();
        return "GESTOR".equalsIgnoreCase(c) || "ADMIN".equalsIgnoreCase(c);
    }
    private boolean isColaborador() {
        return usuarioLogado != null && "COLABORADOR".equalsIgnoreCase(usuarioLogado.getCargo());
    }
    
    // Regras de autorização   
    private boolean ehGestorDaEquipe(Long equipeId) {
        if (usuarioLogado == null || equipeId == null) return false;
        try {
            Equipe e = new EquipeDAO().findById(equipeId);
            return e != null && usuarioLogado.getId().equals(e.getGestorId());
        } catch (Exception ex) { return false; }
    }

    private boolean participaDaEquipe(Long equipeId) {
        if (usuarioLogado == null || equipeId == null) return false;
        try {
            Equipe e = new EquipeDAO().findById(equipeId);
            return e != null && e.getMembrosIds() != null && e.getMembrosIds().contains(usuarioLogado.getId());
        } catch (Exception ex) { return false; }
    }

    private boolean participaDoProjeto(Projeto p) {
        if (p == null) return false;
        Long equipeId = p.getEquipeId();
        return ehGestorDaEquipe(equipeId) || participaDaEquipe(equipeId);
    }

    // Projetos
    private boolean podeVerProjeto(Projeto p) {
        if (p == null) return false;
        if (isAdmin()) return true;
        return participaDoProjeto(p);
    }
    private boolean podeGerenciarProjeto(Projeto p) {
        if (p == null) return false;
        if (isAdmin()) return true;
        if (!isGestor()) return false;
        return ehGestorDaEquipe(p.getEquipeId()); // gestor só da equipe dele
    }
    private boolean podeCriarProjeto(Projeto pProposto) {
        if (pProposto == null) return false;
        if (isAdmin()) return true;
        if (!isGestor()) return false;
        return ehGestorDaEquipe(pProposto.getEquipeId());
    }

    // Tarefas
    private boolean podeVerTarefa(Tarefa t) {
        if (t == null) return false;
        if (isAdmin()) return true;
        Projeto p = new ProjetoDAO().findById(t.getProjetoId());
        return podeVerProjeto(p);
    }
    private boolean podeGerenciarTarefa(Tarefa t) {
        if (t == null) return false;
        if (isAdmin()) return true;
        if (!isGestor()) return false;
        Projeto p = new ProjetoDAO().findById(t.getProjetoId());
        return podeGerenciarProjeto(p);
    }
    private boolean podeConcluirTarefa(Tarefa t) {
        if (t == null) return false;
        if (isAdmin()) return true;
        Projeto p = new ProjetoDAO().findById(t.getProjetoId());
        return participaDoProjeto(p);
    }

    // Equipes
    private boolean podeVerEquipe(Equipe e) {
        if (e == null) return false;
        if (isAdmin()) return true;
        return ehGestorDaEquipe(e.getId()) || participaDaEquipe(e.getId());
    }
    private boolean podeGerenciarEquipe(Equipe e) {
        if (e == null) return false;
        if (isAdmin()) return true;
        if (!isGestor()) return false;
        return ehGestorDaEquipe(e.getId());
    }

    
    public TelaUsuarioPrincipal() {
        this(null);
    }
    
   private Usuario usuarioLogado;

   public TelaUsuarioPrincipal(Usuario usuarioLogado) {
       this.usuarioLogado = usuarioLogado;
       
        initComponents();
        carregarTabelaProjetos();
        setupCards();
        
        panelEditar.setVisible(false);
        carregarTabelaEquipes();
        carregarGestoresCombo();
        carregarTabelaProjetos();

        // seleção simples + listener para carregar os detalhes (somente visualização)
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) carregarDetalheSomenteVisualizacao();
        });
       
        tabelaProjetos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabelaProjetos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarTabelaTarefasDoProjetoSelecionado();
        });
       
        jTable3.setDefaultRenderer(Object.class, new PrazoRenderer());
        carregarDashboard();
        aplicarRegrasDeAcessoUI();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidePanel = new javax.swing.JPanel();
        navPanel = new javax.swing.JPanel();
        btnDashboard = new javax.swing.JButton();
        btnProjetos = new javax.swing.JButton();
        btnEquipes = new javax.swing.JButton();
        btnPerfil = new javax.swing.JButton();
        footerPanel = new javax.swing.JPanel();
        btnSair = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        mainContent = new javax.swing.JPanel();
        dashboardPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        projetosPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        tabelaProjetos = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnEditarTarefa = new javax.swing.JButton();
        btnExcluirTarefa = new javax.swing.JButton();
        btnCriarTarefa = new javax.swing.JButton();
        btnConcluirTarefa = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelaTarefas = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btnNovoProjeto = new javax.swing.JButton();
        btnEditarProjeto = new javax.swing.JButton();
        btnExcluirProjeto = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        equipesPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnNovaEquipe = new javax.swing.JButton();
        btnEditarEquipe = new javax.swing.JButton();
        btnAtualizarEquipes = new javax.swing.JButton();
        btnExcluirEquipe = new javax.swing.JButton();
        detalheEquipePanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtNomeEquipe = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cbGestor = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        listaMembros = new javax.swing.JList<>();
        panelEditar = new javax.swing.JPanel();
        btnSalvarEquipe = new javax.swing.JButton();
        btnCancelarEquipe = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        listaDisponiveis = new javax.swing.JList<>();
        jLabel9 = new javax.swing.JLabel();
        btnRemoverMembro = new javax.swing.JButton();
        btnAddMembro = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        perfilPanel = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtPerfilNome = new javax.swing.JTextField();
        txtPerfilUsuario = new javax.swing.JTextField();
        txtPerfilCpf = new javax.swing.JTextField();
        txtPerfilEmail = new javax.swing.JTextField();
        txtPerfilCargo = new javax.swing.JTextField();
        btnEditarDados = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Área do Usuário – eProj");

        sidePanel.setBackground(new java.awt.Color(204, 204, 204));
        sidePanel.setToolTipText("");
        sidePanel.setPreferredSize(new java.awt.Dimension(220, 700));

        navPanel.setOpaque(false);
        navPanel.setLayout(new java.awt.GridLayout(0, 1, 8, 8));

        btnDashboard.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnDashboard.setText("Dashboard");
        btnDashboard.setFocusPainted(false);
        btnDashboard.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashboardActionPerformed(evt);
            }
        });
        navPanel.add(btnDashboard);

        btnProjetos.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnProjetos.setText("Projetos");
        btnProjetos.setFocusPainted(false);
        btnProjetos.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnProjetos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProjetosActionPerformed(evt);
            }
        });
        navPanel.add(btnProjetos);

        btnEquipes.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnEquipes.setText("Equipes");
        btnEquipes.setFocusPainted(false);
        btnEquipes.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnEquipes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEquipesActionPerformed(evt);
            }
        });
        navPanel.add(btnEquipes);

        btnPerfil.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnPerfil.setText("Meu Perfil");
        btnPerfil.setFocusPainted(false);
        btnPerfil.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnPerfil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPerfilActionPerformed(evt);
            }
        });
        navPanel.add(btnPerfil);

        footerPanel.setOpaque(false);

        btnSair.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnSair.setText("Sair");
        btnSair.setFocusPainted(false);
        btnSair.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout footerPanelLayout = new javax.swing.GroupLayout(footerPanel);
        footerPanel.setLayout(footerPanelLayout);
        footerPanelLayout.setHorizontalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
            .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(footerPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(btnSair)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        footerPanelLayout.setVerticalGroup(
            footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
            .addGroup(footerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(footerPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(btnSair)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jLabel4.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("e-PROJ");

        javax.swing.GroupLayout sidePanelLayout = new javax.swing.GroupLayout(sidePanel);
        sidePanel.setLayout(sidePanelLayout);
        sidePanelLayout.setHorizontalGroup(
            sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidePanelLayout.createSequentialGroup()
                .addGroup(sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sidePanelLayout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(footerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(navPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(sidePanelLayout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(jLabel4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sidePanelLayout.setVerticalGroup(
            sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidePanelLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel4)
                .addGap(61, 61, 61)
                .addComponent(navPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 271, Short.MAX_VALUE)
                .addComponent(footerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
        );

        getContentPane().add(sidePanel, java.awt.BorderLayout.WEST);

        mainContent.setForeground(new java.awt.Color(255, 255, 255));

        dashboardPanel.setName("dashboardPanel"); // NOI18N
        dashboardPanel.setLayout(new java.awt.BorderLayout());

        jLabel11.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("MEUS PROJETOS");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Projeto", "Status", "Data Entrega", "Equipe"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
            jTable2.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(410, 410, 410)
                        .addComponent(jLabel11))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 850, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel12.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("MINHAS TAREFAS");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tarefa", "Status", "Data Entrega", "Projeto"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(0).setResizable(false);
            jTable3.getColumnModel().getColumn(1).setResizable(false);
            jTable3.getColumnModel().getColumn(2).setResizable(false);
            jTable3.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(399, 399, 399)
                        .addComponent(jLabel12))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 854, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addGap(27, 27, 27)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel13.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("MINHAS EQUIPES");

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Equipe", "Gestor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(jTable4);
        if (jTable4.getColumnModel().getColumnCount() > 0) {
            jTable4.getColumnModel().getColumn(0).setResizable(false);
            jTable4.getColumnModel().getColumn(1).setResizable(false);
        }

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(411, 411, 411)
                        .addComponent(jLabel13))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 850, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel13)
                .addGap(45, 45, 45)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        dashboardPanel.add(jPanel6, java.awt.BorderLayout.CENTER);

        projetosPanel.setName("projetosPanel"); // NOI18N
        projetosPanel.setLayout(new java.awt.BorderLayout());

        tabelaProjetos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "Descrição", "Data Início", "Data Fim", "Status", "Equipe"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane.setViewportView(tabelaProjetos);
        if (tabelaProjetos.getColumnModel().getColumnCount() > 0) {
            tabelaProjetos.getColumnModel().getColumn(0).setResizable(false);
            tabelaProjetos.getColumnModel().getColumn(0).setPreferredWidth(1);
            tabelaProjetos.getColumnModel().getColumn(1).setResizable(false);
            tabelaProjetos.getColumnModel().getColumn(2).setResizable(false);
            tabelaProjetos.getColumnModel().getColumn(3).setResizable(false);
            tabelaProjetos.getColumnModel().getColumn(4).setResizable(false);
            tabelaProjetos.getColumnModel().getColumn(5).setResizable(false);
            tabelaProjetos.getColumnModel().getColumn(6).setResizable(false);
        }

        btnEditarTarefa.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnEditarTarefa.setText("Editar Tarefa");
        btnEditarTarefa.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnEditarTarefa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarTarefaActionPerformed(evt);
            }
        });

        btnExcluirTarefa.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnExcluirTarefa.setText("Excluir Tarefa");
        btnExcluirTarefa.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnExcluirTarefa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirTarefaActionPerformed(evt);
            }
        });

        btnCriarTarefa.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnCriarTarefa.setText("Criar Tarefa");
        btnCriarTarefa.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnCriarTarefa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCriarTarefaActionPerformed(evt);
            }
        });

        btnConcluirTarefa.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnConcluirTarefa.setText("Concluir Tarefa");
        btnConcluirTarefa.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnConcluirTarefa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConcluirTarefaActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Tarefas");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(btnCriarTarefa)
                        .addGap(40, 40, 40)
                        .addComponent(btnEditarTarefa)
                        .addGap(30, 30, 30)
                        .addComponent(btnConcluirTarefa)
                        .addGap(33, 33, 33)
                        .addComponent(btnExcluirTarefa))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(317, 317, 317)
                        .addComponent(jLabel2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEditarTarefa)
                    .addComponent(btnExcluirTarefa)
                    .addComponent(btnCriarTarefa)
                    .addComponent(btnConcluirTarefa))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabelaTarefas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Projeto", "Tarefa", "Descrição", "Data Entrega", "Responsável", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tabelaTarefas);
        if (tabelaTarefas.getColumnModel().getColumnCount() > 0) {
            tabelaTarefas.getColumnModel().getColumn(0).setResizable(false);
            tabelaTarefas.getColumnModel().getColumn(0).setPreferredWidth(1);
            tabelaTarefas.getColumnModel().getColumn(1).setResizable(false);
            tabelaTarefas.getColumnModel().getColumn(2).setResizable(false);
            tabelaTarefas.getColumnModel().getColumn(3).setResizable(false);
            tabelaTarefas.getColumnModel().getColumn(4).setResizable(false);
            tabelaTarefas.getColumnModel().getColumn(5).setResizable(false);
            tabelaTarefas.getColumnModel().getColumn(6).setResizable(false);
        }

        jLabel5.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Projetos");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnNovoProjeto.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnNovoProjeto.setText("Cadastrar Projeto");
        btnNovoProjeto.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnNovoProjeto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoProjetoActionPerformed(evt);
            }
        });

        btnEditarProjeto.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnEditarProjeto.setText("Editar Projeto");
        btnEditarProjeto.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnEditarProjeto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarProjetoActionPerformed(evt);
            }
        });

        btnExcluirProjeto.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnExcluirProjeto.setText("Excluir Projeto");
        btnExcluirProjeto.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnExcluirProjeto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirProjetoActionPerformed(evt);
            }
        });

        btnAtualizar.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnAtualizar.setText("Atualizar Projetos");
        btnAtualizar.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(btnNovoProjeto)
                        .addGap(39, 39, 39)
                        .addComponent(btnEditarProjeto)
                        .addGap(18, 18, 18)
                        .addComponent(btnAtualizar)
                        .addGap(18, 18, 18)
                        .addComponent(btnExcluirProjeto))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(295, 295, 295)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(123, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovoProjeto)
                    .addComponent(btnEditarProjeto)
                    .addComponent(btnExcluirProjeto)
                    .addComponent(btnAtualizar))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        projetosPanel.add(jPanel1, java.awt.BorderLayout.CENTER);

        equipesPanel.setName("equipesPanel"); // NOI18N
        equipesPanel.setLayout(new java.awt.BorderLayout());

        jLabel6.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("EQUIPES");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Nome", "Gestor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
        }

        btnNovaEquipe.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnNovaEquipe.setText("Cadastrar Equipe");
        btnNovaEquipe.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnNovaEquipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovaEquipeActionPerformed(evt);
            }
        });

        btnEditarEquipe.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnEditarEquipe.setText("Editar Equipe");
        btnEditarEquipe.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnEditarEquipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarEquipeActionPerformed(evt);
            }
        });

        btnAtualizarEquipes.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnAtualizarEquipes.setText("Atualizar Equipe");
        btnAtualizarEquipes.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnAtualizarEquipes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarEquipesActionPerformed(evt);
            }
        });

        btnExcluirEquipe.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnExcluirEquipe.setText("Excluir Equipe");
        btnExcluirEquipe.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnExcluirEquipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirEquipeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 903, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(btnNovaEquipe)
                        .addGap(29, 29, 29)
                        .addComponent(btnEditarEquipe)
                        .addGap(18, 18, 18)
                        .addComponent(btnAtualizarEquipes)
                        .addGap(18, 18, 18)
                        .addComponent(btnExcluirEquipe)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovaEquipe)
                    .addComponent(btnEditarEquipe)
                    .addComponent(btnAtualizarEquipes)
                    .addComponent(btnExcluirEquipe))
                .addGap(18, 18, 18))
        );

        jLabel7.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("DETALHES");

        txtNomeEquipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeEquipeActionPerformed(evt);
            }
        });

        jLabel3.setText("Nome da Equipe");

        cbGestor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel8.setText("Gestor");

        jScrollPane4.setViewportView(listaMembros);

        btnSalvarEquipe.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnSalvarEquipe.setText("Salvar");
        btnSalvarEquipe.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnSalvarEquipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarEquipeActionPerformed(evt);
            }
        });

        btnCancelarEquipe.setFont(new java.awt.Font("Helvetica Neue", 0, 14)); // NOI18N
        btnCancelarEquipe.setText("Cancelar");
        btnCancelarEquipe.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnCancelarEquipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarEquipeActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(listaDisponiveis);

        jLabel9.setFont(new java.awt.Font("Helvetica Neue", 0, 10)); // NOI18N
        jLabel9.setText("Colaboradores disponíveis");

        btnRemoverMembro.setText("Remover");
        btnRemoverMembro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverMembroActionPerformed(evt);
            }
        });

        btnAddMembro.setText("Adicionar");
        btnAddMembro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddMembroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelEditarLayout = new javax.swing.GroupLayout(panelEditar);
        panelEditar.setLayout(panelEditarLayout);
        panelEditarLayout.setHorizontalGroup(
            panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEditarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnRemoverMembro)
                    .addComponent(btnAddMembro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEditarLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addGroup(panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnCancelarEquipe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSalvarEquipe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel9))
                .addContainerGap())
        );
        panelEditarLayout.setVerticalGroup(
            panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEditarLayout.createSequentialGroup()
                .addGroup(panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEditarLayout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(btnSalvarEquipe)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCancelarEquipe))
                    .addGroup(panelEditarLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelEditarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEditarLayout.createSequentialGroup()
                                .addComponent(btnRemoverMembro)
                                .addGap(18, 18, 18)
                                .addComponent(btnAddMembro)
                                .addGap(42, 42, 42)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel10.setFont(new java.awt.Font("Helvetica Neue", 0, 10)); // NOI18N
        jLabel10.setText("Membros da Equipe");

        javax.swing.GroupLayout detalheEquipePanelLayout = new javax.swing.GroupLayout(detalheEquipePanel);
        detalheEquipePanel.setLayout(detalheEquipePanelLayout);
        detalheEquipePanelLayout.setHorizontalGroup(
            detalheEquipePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detalheEquipePanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(detalheEquipePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(detalheEquipePanelLayout.createSequentialGroup()
                        .addGroup(detalheEquipePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(detalheEquipePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtNomeEquipe)
                                .addComponent(jLabel3)
                                .addComponent(cbGestor, 0, 192, Short.MAX_VALUE)))
                        .addGap(63, 63, 63)
                        .addGroup(detalheEquipePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );
        detalheEquipePanelLayout.setVerticalGroup(
            detalheEquipePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detalheEquipePanelLayout.createSequentialGroup()
                .addGroup(detalheEquipePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detalheEquipePanelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel7)
                        .addGroup(detalheEquipePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(detalheEquipePanelLayout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNomeEquipe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(17, 17, 17)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbGestor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(detalheEquipePanelLayout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(detalheEquipePanelLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(panelEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(detalheEquipePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(detalheEquipePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        equipesPanel.add(jPanel4, java.awt.BorderLayout.CENTER);

        perfilPanel.setName("perfilPanel"); // NOI18N
        perfilPanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Nome:");

        jLabel14.setText("CPF:");

        jLabel16.setText("E-mail:");

        jLabel17.setText("Cargo:");

        jLabel18.setText("Usuário:");

        txtPerfilNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPerfilNomeActionPerformed(evt);
            }
        });

        txtPerfilCpf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPerfilCpfActionPerformed(evt);
            }
        });

        btnEditarDados.setText("Editar Dados");

        btnSalvar.setText("Salvar");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(183, 183, 183)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(btnEditarDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel17))
                                .addGap(3, 3, 3)))
                        .addGap(41, 41, 41)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPerfilUsuario)
                            .addComponent(txtPerfilNome)
                            .addComponent(txtPerfilCpf)
                            .addComponent(txtPerfilEmail)
                            .addComponent(txtPerfilCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(270, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap(82, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(61, 61, 61))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPerfilNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPerfilUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addGap(18, 18, 18)
                        .addComponent(txtPerfilCpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPerfilEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addGap(20, 20, 20)))
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPerfilCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(49, 49, 49)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEditarDados)
                    .addComponent(btnSalvar))
                .addContainerGap(303, Short.MAX_VALUE))
        );

        perfilPanel.add(jPanel10, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout mainContentLayout = new javax.swing.GroupLayout(mainContent);
        mainContent.setLayout(mainContentLayout);
        mainContentLayout.setHorizontalGroup(
            mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
            .addGroup(mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainContentLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(dashboardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainContentLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(projetosPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainContentLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(equipesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainContentLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(perfilPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        mainContentLayout.setVerticalGroup(
            mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 729, Short.MAX_VALUE)
            .addGroup(mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainContentLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(dashboardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainContentLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(projetosPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainContentLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(equipesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(mainContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainContentLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(perfilPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        getContentPane().add(mainContent, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private String nvl(String s) { return s == null ? "" : s; }

    private String formatCpf(String cpf) {
        if (cpf == null) return "";
        String d = cpf.replaceAll("\\D", "");
            if (d.length() != 11) return cpf; // deixa como veio se não tiver 11 dígitos
                return d.substring(0,3)+"."+d.substring(3,6)+"."+d.substring(6,9)+"-"+d.substring(9);
    }
    
    private void setupCards() {
        // troca o layout do container central para CardLayout
        mainContent.removeAll();
        mainContent.setLayout(new CardLayout());

        // registra cada painel com uma CHAVE
        mainContent.add(dashboardPanel, "DASHBOARD");
        mainContent.add(projetosPanel,  "PROJETOS");
        mainContent.add(equipesPanel,   "EQUIPES");
        mainContent.add(perfilPanel,    "PERFIL");

        // mostra o card inicial
        ((CardLayout) mainContent.getLayout()).show(mainContent, "DASHBOARD");

        // atualiza a UI
        mainContent.revalidate();
        mainContent.repaint();
    }

    // Habilita/oculta botões/abas conforme o cargo do usuário logado
    private void aplicarRegrasDeAcessoUI() {
        // PROJETOS 
        btnNovoProjeto.setVisible(isGestor());     
        btnEditarProjeto.setVisible(isGestor());
        btnExcluirProjeto.setVisible(isGestor());

        // TAREFAS (edições)
        btnCriarTarefa.setVisible(isGestor());    
        btnEditarTarefa.setVisible(isGestor());
        btnExcluirTarefa.setVisible(isGestor());
        // Concluir tarefa pode ser feito por colaborador
        btnConcluirTarefa.setVisible(true);

        // EQUIPES (somente gestor/admin)
        btnNovaEquipe.setVisible(isGestor());
        btnEditarEquipe.setVisible(isGestor());
        btnExcluirEquipe.setVisible(isGestor());
        panelEditar.setVisible(false); 

        // Equipes- colaborador pode ver (somente leitura)
        btnEquipes.setVisible(true);

        // esconder PROJETOS para colaborador
        btnProjetos.setVisible(true);

        // para colaborador somente leitura nas equipes
        boolean soLeituraEquipes = isColaborador();
        btnNovaEquipe.setEnabled(!soLeituraEquipes);
        btnEditarEquipe.setEnabled(!soLeituraEquipes);
        btnExcluirEquipe.setEnabled(!soLeituraEquipes);
        }

    
    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        int option = javax.swing.JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente sair?",
            "Confirmar saída",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.QUESTION_MESSAGE
    );

    if (option == javax.swing.JOptionPane.YES_OPTION) {
        System.exit(0); // encerra o sistema
    }
    }//GEN-LAST:event_btnSairActionPerformed

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        ((CardLayout) mainContent.getLayout()).show(mainContent, "DASHBOARD");
        carregarDashboard();
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnProjetosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProjetosActionPerformed
        ((CardLayout) mainContent.getLayout()).show(mainContent, "PROJETOS");
    }//GEN-LAST:event_btnProjetosActionPerformed

    private void btnEquipesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEquipesActionPerformed
        ((CardLayout) mainContent.getLayout()).show(mainContent, "EQUIPES");
        carregarTabelaEquipes();
        carregarGestoresCombo();
        carregarColaboradoresDisponiveis();
        panelEditar.setVisible(false);
        
        if (isColaborador()) {
            panelEditar.setVisible(false);
            btnNovaEquipe.setEnabled(false);
            btnEditarEquipe.setEnabled(false);
            btnExcluirEquipe.setEnabled(false);
}
        
    }//GEN-LAST:event_btnEquipesActionPerformed

    private void btnPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPerfilActionPerformed
        ((CardLayout) mainContent.getLayout()).show(mainContent, "PERFIL");
        preencherPerfilComUsuarioLogado();
    }//GEN-LAST:event_btnPerfilActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        carregarTabelaProjetos();
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnNovoProjetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoProjetoActionPerformed
        if (!isGestor()) { JOptionPane.showMessageDialog(this, "Acesso restrito a gestores/administradores."); return; }
        FormProjetoDialog dlg = new FormProjetoDialog(this, true);
        dlg.setVisible(true);

    
    if (dlg.isOkPressed()) {
        Projeto novo = dlg.getProjeto();
        if (!podeCriarProjeto(novo)) {
            JOptionPane.showMessageDialog(this,
            "Você não tem permissão para criar projeto nessa equipe.",
            "Acesso negado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            new ProjetoDAO().insert(novo);
            javax.swing.JOptionPane.showMessageDialog(this, "Projeto cadastrado com sucesso!");
            carregarTabelaProjetos(); // atualiza a tabela
        } catch (RuntimeException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Erro ao salvar: " + ex.getMessage(), "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_btnNovoProjetoActionPerformed

    private void btnEditarProjetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarProjetoActionPerformed
        if (!isGestor()) { JOptionPane.showMessageDialog(this, "Acesso restrito a gestores/administradores."); return; }
        int row = tabelaProjetos.getSelectedRow();
        if (row < 0) {
        javax.swing.JOptionPane.showMessageDialog(this, "Selecione um projeto na tabela.");
            return;
        }
        Long id = (Long) tabelaProjetos.getValueAt(row, 0);

        ProjetoDAO dao = new ProjetoDAO();
        Projeto existente = dao.findById(id);
        if (existente == null) {
            javax.swing.JOptionPane.showMessageDialog(this, "Projeto não encontrado.");
            return;
        }

        if (!podeGerenciarProjeto(existente)) {
            JOptionPane.showMessageDialog(this,
            "Você não pode editar este projeto.",
            "Acesso negado", JOptionPane.WARNING_MESSAGE);
          return;
        }

        
    FormProjetoDialog dlg = new FormProjetoDialog(this, true);
    dlg.setProjeto(existente);   // preenche o form
    dlg.setVisible(true);

    if (dlg.isOkPressed()) {
        try {
            dao.update(dlg.getProjeto());
            javax.swing.JOptionPane.showMessageDialog(this, "Projeto atualizado!");
            carregarTabelaProjetos();
        } catch (RuntimeException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage(),
                                                     "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    }//GEN-LAST:event_btnEditarProjetoActionPerformed

    private void btnExcluirProjetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirProjetoActionPerformed
        if (!isGestor()) { JOptionPane.showMessageDialog(this, "Acesso restrito a gestores/administradores."); return; }
        int row = tabelaProjetos.getSelectedRow();
    if (row < 0) {
        javax.swing.JOptionPane.showMessageDialog(this, "Selecione um projeto na tabela.");
        return;
    }

    Long id = (Long) tabelaProjetos.getValueAt(row, 0);
    String nome = (String) tabelaProjetos.getValueAt(row, 1);

    int resp = javax.swing.JOptionPane.showConfirmDialog(
            this,
            "Você quer mesmo excluir \"" + nome + "\" (ID " + id + ")?",
            "Confirmar exclusão",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE
    );
    if (resp != javax.swing.JOptionPane.YES_OPTION) return;

    try {
        ProjetoDAO dao = new ProjetoDAO();
        Projeto existente = new ProjetoDAO().findById(id);
            if (!podeGerenciarProjeto(existente)) {
                JOptionPane.showMessageDialog(this,
                "Você não pode excluir este projeto.",
                "Acesso negado", JOptionPane.WARNING_MESSAGE);
               return;
            }
            
        dao.deleteById(id);
        javax.swing.JOptionPane.showMessageDialog(this, "Projeto excluído com sucesso!");
        carregarTabelaProjetos(); // atualiza a lista
    } catch (RuntimeException ex) {
        //  tarefas vinculadas
        Throwable cause = ex.getCause();
        String msg = ex.getMessage();

        // Mensagem padrão
        String amigavel = "Não foi possível excluir o projeto.";

        // erro de integridade
        if ((msg != null && msg.toLowerCase().contains("foreign key"))
            || (cause != null && cause.getClass().getSimpleName().contains("SQLIntegrityConstraintViolationException"))) {
            amigavel = "Este projeto possui vínculos (ex.: tarefas) e não pode ser excluído.";
        }

        javax.swing.JOptionPane.showMessageDialog(
                this, amigavel + "\nDetalhes: " + ex.getMessage(),
                "Erro", javax.swing.JOptionPane.ERROR_MESSAGE
        );
    }
    }//GEN-LAST:event_btnExcluirProjetoActionPerformed

    private void btnCriarTarefaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCriarTarefaActionPerformed
        if (!isGestor()) { JOptionPane.showMessageDialog(this, "Acesso restrito a gestores/administradores."); return; } 
        Long projetoId = getProjetoSelecionadoId();
        if (projetoId == null) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto antes de criar a tarefa.");
            return;
        }

        FormTarefaDialog dlg = new FormTarefaDialog(this, true, projetoId);
        dlg.setVisible(true);
        if (!dlg.isOkPressed()) return;

        try {
            TarefaDAO dao = new TarefaDAO();
            dao.insert(dlg.getTarefa());
            JOptionPane.showMessageDialog(this, "Tarefa criada com sucesso!");
            carregarTabelaTarefasDoProjetoSelecionado();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao criar tarefa: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCriarTarefaActionPerformed
  
    
    private void btnEditarTarefaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarTarefaActionPerformed
        if (!isGestor()) { JOptionPane.showMessageDialog(this, "Acesso restrito a gestores/administradores."); return; }
        Long projetoId = getProjetoSelecionadoId();
        if (projetoId == null) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto.");
            return;
        }
        Long tarefaId = getTarefaSelecionadaId();
        if (tarefaId == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na tabela.");
            return;
        }

        TarefaDAO dao = new TarefaDAO();
        Tarefa existente = dao.findById(tarefaId);
        if (!podeGerenciarTarefa(existente)) {
            JOptionPane.showMessageDialog(this,
            "Você não pode editar esta tarefa.",
            "Acesso negado", JOptionPane.WARNING_MESSAGE);
        return;
        }
        
        if (existente == null) {
            JOptionPane.showMessageDialog(this, "Tarefa não encontrada.");
            return;
        }

        FormTarefaDialog dlg = new FormTarefaDialog(this, true, projetoId);
        dlg.setTarefa(existente);
        dlg.setVisible(true);
        if (!dlg.isOkPressed()) return;

        try {
            dao.update(dlg.getTarefa());
            JOptionPane.showMessageDialog(this, "Tarefa atualizada!");
            carregarTabelaTarefasDoProjetoSelecionado();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar tarefa: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEditarTarefaActionPerformed

    private void btnConcluirTarefaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConcluirTarefaActionPerformed
          Long tarefaId = getTarefaSelecionadaId();
        if (tarefaId == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na tabela.");
            return;
        }
        int resp = JOptionPane.showConfirmDialog(
            this,
            "Marcar a tarefa como CONCLUÍDA?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (resp != JOptionPane.YES_OPTION) return;

        try {
            TarefaDAO dao = new TarefaDAO();
        Tarefa t = new TarefaDAO().findById(tarefaId);
            if (!podeConcluirTarefa(t)) {
                JOptionPane.showMessageDialog(this,
                "Você não pode concluir esta tarefa.",
                "Acesso negado", JOptionPane.WARNING_MESSAGE);
              return;
            }
            
            dao.updateStatus(tarefaId, "CONCLUIDA");
            carregarTabelaTarefasDoProjetoSelecionado();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao concluir tarefa: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnConcluirTarefaActionPerformed

    private void btnExcluirTarefaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirTarefaActionPerformed
        if (!isGestor()) { JOptionPane.showMessageDialog(this, "Acesso restrito a gestores/administradores."); return; } 
        Long tarefaId = getTarefaSelecionadaId();
        if (tarefaId == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na tabela.");
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
            this,
            "Excluir a tarefa selecionada?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (resp != JOptionPane.YES_OPTION) return;

        try {
            TarefaDAO dao = new TarefaDAO();
            
        Tarefa t = new TarefaDAO().findById(tarefaId);
        if (!podeGerenciarTarefa(t)) {
            JOptionPane.showMessageDialog(this,
                "Você não pode excluir esta tarefa.",
                "Acesso negado", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
            dao.deleteById(tarefaId);
            JOptionPane.showMessageDialog(this, "Tarefa excluída com sucesso!");
            carregarTabelaTarefasDoProjetoSelecionado();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir tarefa: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnExcluirTarefaActionPerformed

    private void txtNomeEquipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeEquipeActionPerformed
        // A fazer
    }//GEN-LAST:event_txtNomeEquipeActionPerformed

    private void btnNovaEquipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovaEquipeActionPerformed
        if (!isGestor()) { JOptionPane.showMessageDialog(this, "Apenas gestores/administradores podem criar equipes."); return; }
        abrirCriarEquipe();
    }//GEN-LAST:event_btnNovaEquipeActionPerformed

    private void btnCancelarEquipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarEquipeActionPerformed
        panelEditar.setVisible(false);
    }//GEN-LAST:event_btnCancelarEquipeActionPerformed

    private void btnEditarEquipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarEquipeActionPerformed

        if (!isGestor()) { JOptionPane.showMessageDialog(this, "Apenas gestores/administradores podem editar equipes."); return; }
        abrirEditarEquipe();
    }//GEN-LAST:event_btnEditarEquipeActionPerformed

    private void btnAtualizarEquipesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarEquipesActionPerformed
        carregarTabelaEquipes();
        panelEditar.setVisible(false);
        equipeEditandoId = null;
        limparFormularioEquipe();
        // se houver seleção, mostra detalhes só visualização
        carregarDetalheSomenteVisualizacao();
    }//GEN-LAST:event_btnAtualizarEquipesActionPerformed

    private void btnExcluirEquipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirEquipeActionPerformed
        if (!isGestor()) { JOptionPane.showMessageDialog(this, "Apenas gestores/administradores podem excluir equipes."); return; }        
        excluirEquipe();
    }//GEN-LAST:event_btnExcluirEquipeActionPerformed

    private void btnRemoverMembroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverMembroActionPerformed
        moverSelecionados((javax.swing.JList) listaMembros, (javax.swing.JList) listaDisponiveis);
    }//GEN-LAST:event_btnRemoverMembroActionPerformed

    private void btnAddMembroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddMembroActionPerformed
        moverSelecionados((javax.swing.JList) listaDisponiveis, (javax.swing.JList) listaMembros);
    }//GEN-LAST:event_btnAddMembroActionPerformed

    private void btnSalvarEquipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarEquipeActionPerformed
        salvarEquipe();
    }//GEN-LAST:event_btnSalvarEquipeActionPerformed

    private void txtPerfilNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPerfilNomeActionPerformed
        // A fazer
    }//GEN-LAST:event_txtPerfilNomeActionPerformed

    private void txtPerfilCpfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPerfilCpfActionPerformed
        // A fazer
    }//GEN-LAST:event_txtPerfilCpfActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaUsuarioPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaUsuarioPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaUsuarioPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaUsuarioPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaUsuarioPrincipal().setVisible(true);
            }
        });
    }

    private void carregarTabelaProjetos() {
     List<Projeto> projetos;
    ProjetoDAO pdao = new ProjetoDAO();

    if (usuarioLogado == null) {
        projetos = pdao.findAll(); 
    } else if (isAdmin()) {
        projetos = pdao.findAll();
    } else {
        // pega todos e filtra pelo helper
        projetos = pdao.findAll();
        List<Projeto> visiveis = new java.util.ArrayList<>();
        for (Projeto p : projetos) if (podeVerProjeto(p)) visiveis.add(p);
        projetos = visiveis;
    }

    DefaultTableModel model = (DefaultTableModel) tabelaProjetos.getModel();
    model.setRowCount(0);

    for (Projeto p : projetos) {
        model.addRow(new Object[]{
            p.getId(),
            p.getNome(),
            p.getDescricao(),
            (p.getDataInicio() != null ? p.getDataInicio().format(BR) : ""),
            (p.getDataFim() != null ? p.getDataFim().format(BR) : ""),
            p.getStatus(),
            (p.getEquipeNome() != null ? p.getEquipeNome() : p.getEquipeId())
        });
    }
    carregarTabelaTarefasDoProjetoSelecionado();
    }

    private Long getProjetoSelecionadoId() {
     int row = tabelaProjetos.getSelectedRow();
        if (row < 0) return null;
        Object val = tabelaProjetos.getValueAt(row, 0);
        return (val instanceof Long) ? (Long) val : Long.valueOf(val.toString());
    }

    private void carregarTabelaTarefasDoProjetoSelecionado() {
     Long projetoId = getProjetoSelecionadoId();
        DefaultTableModel model = (DefaultTableModel) tabelaTarefas.getModel();
        model.setRowCount(0);
        if (projetoId == null) return;

        TarefaDAO dao = new TarefaDAO();
        java.util.List<Tarefa> tarefas = dao.listarPorProjeto(projetoId);

        for (Tarefa t : tarefas) {
           if (!podeVerTarefa(t)) continue; 
            
            model.addRow(new Object[]{
                t.getId(),
                projetoId,
                t.getTitulo(),
                t.getDescricao(),
                t.getDataEntrega() != null ? t.getDataEntrega().format(BR) : "",
                t.getResponsavelNome() != null ? t.getResponsavelNome() : t.getResponsavelId(),
                t.getStatus()
            });
        }
}

    private Long getTarefaSelecionadaId() {
        int row = tabelaTarefas.getSelectedRow();
        if (row < 0) return null;
        Object val = tabelaTarefas.getValueAt(row, 0);
        return (val instanceof Long) ? (Long) val : Long.valueOf(val.toString());
  }
    
    //EQUIPES: carregar tabela
    private void carregarTabelaEquipes() {
        EquipeDAO dao = new EquipeDAO();
        List<Equipe> todas = dao.findAll();
        List<Equipe> equipes = new java.util.ArrayList<>();
        for (Equipe e : todas) if (podeVerEquipe(e)) equipes.add(e);

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        for (Equipe e : equipes) {
            model.addRow(new Object[]{
            e.getId(),
            e.getNome(),
            (e.getGestorNome() != null ? e.getGestorNome() : e.getGestorId())
            });
        }
    }

    // obtém ID da equipe selecionada na tabela 
    private Long getEquipeSelecionadaId() {
        int row = jTable1.getSelectedRow();
        if (row < 0) return null;
        Object val = jTable1.getValueAt(row, 0);
        return (val instanceof Long) ? (Long) val : Long.valueOf(val.toString());
    }

    // carrega combo de gestores (admins/gestores)
    private void carregarGestoresCombo() {
        UsuarioDAO udao = new UsuarioDAO();
    java.util.List<Usuario> candidatos = new java.util.ArrayList<>();
    java.util.HashSet<Long> seen = new java.util.HashSet<>();

    // Gestores
    try {
        java.util.List<Usuario> gestores = udao.listarGestores();
        if (gestores != null) {
            for (Usuario u : gestores) {
                if (u != null && u.getId() != null && seen.add(u.getId())) {
                    candidatos.add(u);
                }
            }
        }
    } catch (Throwable ignore) { }

    // Colaboradores  para quando não houver gestores
    try {
        java.util.List<Usuario> colabs = udao.listarColaboradores();
        if (colabs != null) {
            for (Usuario u : colabs) {
                if (u != null && u.getId() != null && seen.add(u.getId())) {
                    candidatos.add(u);
                }
            }
        }
    } catch (Throwable ignore) { }

    // Admin logado também como opção de "gestor" 
    if (usuarioLogado != null && "ADMIN".equalsIgnoreCase(usuarioLogado.getCargo())) {
        if (usuarioLogado.getId() != null && seen.add(usuarioLogado.getId())) {
            candidatos.add(usuarioLogado);
        }
    }

    // Monta o combo
    javax.swing.DefaultComboBoxModel<Object> model = new javax.swing.DefaultComboBoxModel<>();
    for (Usuario u : candidatos) {
        String cargo = (u.getCargo() != null ? u.getCargo() : "");
        model.addElement(new ComboItem(u.getId(), u.getNome() + (cargo.isEmpty() ? "" : " (" + cargo + ")")));
    }
    cbGestor.setModel(model);

    
    if (model.getSize() == 0) {
        model.addElement(new ComboItem(-1L, "— sem usuários disponíveis —"));
        cbGestor.setModel(model);
        cbGestor.setSelectedIndex(0);
    }
}


    // carrega lista de colaboradores disponíveis (apenas perfil COLABORADOR)
    private void carregarColaboradoresDisponiveis() {
        UsuarioDAO udao = new UsuarioDAO();
        // ajuste para o método que retorna só colaboradores 
        java.util.List<Usuario> colabs = udao.listarColaboradores();

        DefaultListModel<ComboItem> model = new DefaultListModel<ComboItem>();
        for (Usuario u : colabs) {
        model.addElement(new ComboItem(u.getId(), u.getNome()));
    }
    
    @SuppressWarnings("unchecked")
    javax.swing.JList list = (javax.swing.JList) listaDisponiveis;
    list.setModel(model);
    }

    // limpa formulário/estado do editor
    private void limparFormularioEquipe() {
        txtNomeEquipe.setText("");
        if (cbGestor.getItemCount() > 0) cbGestor.setSelectedIndex(0);

        DefaultListModel<ComboItem> membros = new DefaultListModel<ComboItem>();
        @SuppressWarnings("unchecked")
        javax.swing.JList list = (javax.swing.JList) listaMembros;
        list.setModel(membros);
    }   

    // move item selecionado de uma lista para outra (add/remove)
    private void moverSelecionados(javax.swing.JList origem, javax.swing.JList destino) {
        Object[] selecionados = origem.getSelectedValuesList().toArray();
        DefaultListModel origemModel = (DefaultListModel) origem.getModel();
        DefaultListModel destinoModel = (DefaultListModel) destino.getModel();

        for (int i = 0; i < selecionados.length; i++) {
        Object item = selecionados[i];
        destinoModel.addElement(item);
        }
        for (int i = 0; i < selecionados.length; i++) {
        origemModel.removeElement(selecionados[i]);
        }
    }

    // preenche o editor com a equipe existente
    private void preencherEditor(Equipe e) {
        txtNomeEquipe.setText(e.getNome());

        // selecionar gestor no combo
        for (int i = 0; i < cbGestor.getItemCount(); i++) {
            Object it = cbGestor.getItemAt(i);
            if (it instanceof ComboItem) {
                ComboItem ci = (ComboItem) it;
                if (ci.getId().equals(e.getGestorId())) {
                    cbGestor.setSelectedIndex(i);
                    break;
                }
            }
        }

        // membros selecionados
        DefaultListModel<ComboItem> membrosModel = new DefaultListModel<ComboItem>();
        if (e.getMembrosIds() != null) {
            UsuarioDAO udao = new UsuarioDAO();
            // carrega nomes para cada id (faça um método melhor no DAO se quiser performance)
            for (Long uid : e.getMembrosIds()) {
                Usuario u = udao.findById(uid);
                if (u != null) membrosModel.addElement(new ComboItem(u.getId(), u.getNome()));
            }
        }
        @SuppressWarnings("unchecked")
        javax.swing.JList listM = (javax.swing.JList) listaMembros;
        listM.setModel(membrosModel);
    }

    // coleta os dados do editor e monta um objeto Equipe
    private Equipe coletarEquipeDoEditor(Long idEdicao) {
        String nome = txtNomeEquipe.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da equipe.");
            return null;
        }

        Object sel = cbGestor.getSelectedItem();
        if (!(sel instanceof ComboItem)) {
            JOptionPane.showMessageDialog(this, "Selecione o gestor.");
            return null;
        }
        Long gestorId = ((ComboItem) sel).getId();

        // membros = itens da listaMembros
        @SuppressWarnings("unchecked")
        javax.swing.JList listM = (javax.swing.JList) listaMembros;
        DefaultListModel model = (DefaultListModel) listM.getModel();

        java.util.List<Long> membrosIds = new ArrayList<Long>();
        for (int i = 0; i < model.size(); i++) {
         Object it = model.getElementAt(i);
         if (it instanceof ComboItem) {
            membrosIds.add(((ComboItem) it).getId());
            }
        }

        Equipe e = new Equipe();
        if (idEdicao != null) e.setId(idEdicao);
        e.setNome(nome);
        e.setGestorId(gestorId);
        e.setMembrosIds(membrosIds);
        return e;
    }

    // carregar detalhes somente visualização (ao clicar na tabela de equipes)
    private void carregarDetalheSomenteVisualizacao() {
        Long id = getEquipeSelecionadaId();
        if (id == null) {
            limparFormularioEquipe();
            panelEditar.setVisible(false);
            equipeEditandoId = null;
            return;
    }

        EquipeDAO dao = new EquipeDAO();
        Equipe e = dao.findById(id);
        if (e == null) {
            limparFormularioEquipe();
            panelEditar.setVisible(false);
            equipeEditandoId = null;
            return;
        }

        // nome
        txtNomeEquipe.setText(e.getNome());

        // repopula gestores e seleciona o gestor da equipe
        carregarGestoresCombo();
        for (int i = 0; i < cbGestor.getItemCount(); i++) {
            Object it = cbGestor.getItemAt(i);
            if (it instanceof ComboItem) {
                ComboItem ci = (ComboItem) it;
                if (ci.getId().equals(e.getGestorId())) {
                    cbGestor.setSelectedIndex(i);
                    break;
                }
            }
        }

        // monta listas: membros e disponíveis
        UsuarioDAO udao = new UsuarioDAO();
        java.util.List<Usuario> colaboradores = udao.listarColaboradores();

        java.util.HashSet<Long> setMembros = new java.util.HashSet<Long>(
            e.getMembrosIds() == null ? java.util.Collections.<Long>emptyList() : e.getMembrosIds()
        );

        DefaultListModel<ComboItem> modelM = new DefaultListModel<ComboItem>();
        DefaultListModel<ComboItem> modelD = new DefaultListModel<ComboItem>();

        for (Usuario u : colaboradores) {
            ComboItem item = new ComboItem(u.getId(), u.getNome());
            if (setMembros.contains(u.getId())) modelM.addElement(item);
            else modelD.addElement(item);
        }

        ((javax.swing.JList) listaMembros).setModel(modelM);
        ((javax.swing.JList) listaDisponiveis).setModel(modelD);

        // visualização (editor oculto)
        panelEditar.setVisible(false);
        equipeEditandoId = null;
    }

        
    // abrir criação de equipe (editor em branco)
    private void abrirCriarEquipe() {
        if (!isGestor()) { JOptionPane.showMessageDialog(this, "Apenas gestores/administradores podem criar equipes."); return; }
        equipeEditandoId = null;
        txtNomeEquipe.setText("");
        carregarGestoresCombo();

        // membros vazios
        DefaultListModel<ComboItem> membros = new DefaultListModel<ComboItem>();
        ((javax.swing.JList) listaMembros).setModel(membros);

        // disponíveis = todos colaboradores
        carregarColaboradoresDisponiveis();

        panelEditar.setVisible(true);
    }

    // abrir edição de equipe (carrega dados + divide listas)
    private void abrirEditarEquipe() {
        if (!isGestor()) {
        JOptionPane.showMessageDialog(this, "Apenas gestores/administradores podem editar equipes.");
        return;
    }

    Long id = getEquipeSelecionadaId();
    if (id == null) {
        JOptionPane.showMessageDialog(this, "Selecione uma equipe.");
        return;
    }

    EquipeDAO dao = new EquipeDAO();
    Equipe e = dao.findById(id);
    if (e == null) {
        JOptionPane.showMessageDialog(this, "Equipe não encontrada.");
        return;
    }

    
    if (!podeGerenciarEquipe(e)) {
        JOptionPane.showMessageDialog(this, "Você não pode editar esta equipe.");
        return;
    }
        equipeEditandoId = e.getId();

        // nome/gestor/membros
        preencherEditor(e);

        // disponíveis = colaboradores que NÃO estão nos membros
        UsuarioDAO udao = new UsuarioDAO();
        java.util.List<Usuario> colaboradores = udao.listarColaboradores();
        java.util.HashSet<Long> setMembros = new java.util.HashSet<Long>(
        e.getMembrosIds() == null ? java.util.Collections.<Long>emptyList() : e.getMembrosIds()
        );

        DefaultListModel<ComboItem> disp = new DefaultListModel<ComboItem>();
        for (Usuario u : colaboradores) {
            if (!setMembros.contains(u.getId())) {
                disp.addElement(new ComboItem(u.getId(), u.getNome()));
            }
        }
        ((javax.swing.JList) listaDisponiveis).setModel(disp);

        panelEditar.setVisible(true);
    }

    
    // salvar (insert ou update) a equipe
    private void salvarEquipe() {
        if (!isGestor()) {
        JOptionPane.showMessageDialog(this, "Apenas gestores/administradores podem salvar equipes.");
        return;
    }

    // Coleta dados do formulário
    Equipe e = coletarEquipeDoEditor(equipeEditandoId);
    if (e == null) return;

    EquipeDAO dao = new EquipeDAO();

    try {
        if (equipeEditandoId == null) {
            // CRIAR
            if (!(isAdmin() || isGestor())) {
                JOptionPane.showMessageDialog(this, "Sem permissão para criar equipe.");
                return;
            }
            // Se for gestor (não admin), só pode criar equipe em que ele mesmo é o gestor selecionado
            if (!isAdmin()) {
                Object sel = cbGestor.getSelectedItem();
                if (sel instanceof ComboItem) {
                    Long gestorSelecionado = ((ComboItem) sel).getId();
                    if (!usuarioLogado.getId().equals(gestorSelecionado)) {
                        JOptionPane.showMessageDialog(this,
                                "Como gestor, você só pode criar equipe onde você é o gestor.",
                                "Acesso negado", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            dao.insert(e);
            JOptionPane.showMessageDialog(this, "Equipe criada com sucesso!");

        } else {
            // EDITAR
            Equipe atual = dao.findById(equipeEditandoId);
            if (atual == null) {
                JOptionPane.showMessageDialog(this, "Equipe não encontrada.");
                return;
            }
            if (!podeGerenciarEquipe(atual)) {
                JOptionPane.showMessageDialog(this, "Você não pode salvar alterações nesta equipe.");
                return;
            }

            dao.update(e);
            JOptionPane.showMessageDialog(this, "Equipe atualizada!");
        }

        carregarTabelaEquipes();
        panelEditar.setVisible(false);
        equipeEditandoId = null;
        carregarDetalheSomenteVisualizacao();

        } catch (RuntimeException ex) {
        JOptionPane.showMessageDialog(this, "Erro ao salvar equipe: " + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // excluir equipe selecionada
    private void excluirEquipe() {
        Long id = getEquipeSelecionadaId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma equipe.");
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "Excluir a equipe selecionada?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (resp != JOptionPane.YES_OPTION) return;

        try {
            Equipe e = new EquipeDAO().findById(id);
        if (e == null) {
            JOptionPane.showMessageDialog(this, "Equipe não encontrada.");
            return;
        }
        if (!podeGerenciarEquipe(e)) {
            JOptionPane.showMessageDialog(this, "Você não pode excluir esta equipe.");
            return;
        }
            new EquipeDAO().deleteById(id);
            JOptionPane.showMessageDialog(this, "Equipe excluída com sucesso!");
            panelEditar.setVisible(false);
            equipeEditandoId = null;
            carregarTabelaEquipes();
            limparFormularioEquipe();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir equipe: " + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void prepararModelosDashboard() {
    // MEUS PROJETOS 
    jTable2.setModel(new javax.swing.table.DefaultTableModel(
        new Object[][]{}, new String[]{"Projeto","Status","Data Entrega","Equipe"}
    ) {
        @Override public boolean isCellEditable(int r,int c){ return false; }
        @Override public Class<?> getColumnClass(int c){
            return (c==2) ? java.time.LocalDate.class : Object.class; // Entrega como LocalDate
        }
      });

    // MINHAS TAREFAS 
    jTable3.setModel(new javax.swing.table.DefaultTableModel(
        new Object[][]{}, new String[]{"Tarefa","Status","Data Entrega","Projeto"}
    ) {
        @Override public boolean isCellEditable(int r,int c){ return false; }
        @Override public Class<?> getColumnClass(int c){
            return (c==2) ? java.time.LocalDate.class : Object.class; // Entrega como LocalDate
        }
    });

    // MINHAS EQUIPES 
    jTable4.setModel(new javax.swing.table.DefaultTableModel(
        new Object[][]{}, new String[]{"Equipe","Gestor"}
    ) {
        @Override public boolean isCellEditable(int r,int c){ return false; }
    });
    }

    private static class PrazoRowRenderer extends javax.swing.table.DefaultTableCellRenderer {
        private final int colData;
        private final java.time.format.DateTimeFormatter br =
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        PrazoRowRenderer(int colData) { this.colData = colData; }

        @Override
        public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) c.setBackground(java.awt.Color.WHITE);

                Object val = table.getModel().getValueAt(row, colData);
                java.time.LocalDate entrega = null;
                    if (val instanceof java.time.LocalDate) {
                        entrega = (java.time.LocalDate) val;
                    } else if (val instanceof String) {
                    String s = ((String) val).trim();
                        if (!s.isEmpty()) {
                            try { entrega = java.time.LocalDate.parse(s, br); } catch (Exception ignore) {}
                        }
                    }

        if (entrega != null) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), entrega);
            java.awt.Color cor;
            if (dias < 0)        cor = new java.awt.Color(255, 102, 102);  // vermelho
            else if (dias <= 7)  cor = new java.awt.Color(255, 178, 102);  // laranja
            else if (dias <= 15) cor = new java.awt.Color(255, 236, 153);  // amarelo
            else                 cor = new java.awt.Color(204, 255, 204);  // verde
            if (!isSelected) c.setBackground(cor);
        }
        return c;
            }
    }

    private void aplicarRendererPrazo(javax.swing.JTable tabela, int colData) {
        PrazoRowRenderer rr = new PrazoRowRenderer(colData);
        for (int i = 0; i < tabela.getColumnModel().getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(rr);
        }
    }

 
    // DASHBOARD
    private void carregarDashboard() {
        carregarProjetosDoUsuario();
        carregarTarefasDoUsuario();
        carregarEquipesDoUsuario();
    }

    private void carregarProjetosDoUsuario() {
        DefaultTableModel m = (DefaultTableModel) jTable2.getModel();
        m.setRowCount(0);
        List<Projeto> todos = new ProjetoDAO().findAll();
        for (Projeto p : todos) {
            if (!podeVerProjeto(p)) continue;
            m.addRow(new Object[] {
                p.getNome(),
                p.getStatus(),
                (p.getDataFim() != null ? p.getDataFim().format(BR) : ""),
                (p.getEquipeNome() != null ? p.getEquipeNome() : p.getEquipeId())
            });
        }
    }

    private void carregarTarefasDoUsuario() {
        DefaultTableModel m = (DefaultTableModel) jTable3.getModel();
        m.setRowCount(0);

        TarefaDAO tdao = new TarefaDAO();
        ProjetoDAO pdao = new ProjetoDAO();
        List<Tarefa> tarefas = new java.util.ArrayList<>();


        for (Projeto p : pdao.findAll()) {
            List<Tarefa> doProjeto = tdao.listarPorProjeto(p.getId());
            if (doProjeto != null) tarefas.addAll(doProjeto);
        }

        // ordena por dataEntrega (nulos por último)
        tarefas.sort((a,b) -> {
            java.time.LocalDate da = a.getDataEntrega();
            java.time.LocalDate db = b.getDataEntrega();
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            return da.compareTo(db);
        });

        for (Tarefa t : tarefas) {
            if (!podeVerTarefa(t)) continue;
            String projetoRef;
            Projeto proj = (t.getProjetoId() != null) ? new ProjetoDAO().findById(t.getProjetoId()) : null;
            if (proj != null && proj.getNome() != null) {
                projetoRef = proj.getNome();
            } else {
                projetoRef = String.valueOf(t.getProjetoId());
            }

            m.addRow(new Object[] {
                t.getTitulo(),
                t.getStatus(),
                (t.getDataEntrega() != null ? t.getDataEntrega().format(BR) : ""),
                projetoRef
            });
        }
    }

    private void carregarEquipesDoUsuario() {
        DefaultTableModel m = (DefaultTableModel) jTable4.getModel();
        m.setRowCount(0);
        List<Equipe> todas = new EquipeDAO().findAll();
        for (Equipe e : todas) {
            if (!podeVerEquipe(e)) continue;
            m.addRow(new Object[] {
                e.getNome(),
                (e.getGestorNome() != null ? e.getGestorNome() : e.getGestorId())
            });
        }
    }
    
    // Renderer para colorir as linhas da jTable3 conforme prazo de entrega
    private class PrazoRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(
            javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // coluna 2 = "Data Entrega" no dashboard
            int colData = 2;
            String dataStr = (String) table.getValueAt(row, colData);
            java.awt.Color bg = java.awt.Color.WHITE;

            if (dataStr != null && !dataStr.trim().isEmpty()) {
                try {
                    java.time.LocalDate entrega = java.time.LocalDate.parse(dataStr, BR);
                    java.time.LocalDate hoje = java.time.LocalDate.now();
                    long dias = java.time.temporal.ChronoUnit.DAYS.between(hoje, entrega);

                    if (entrega.isBefore(hoje)) {
                        // vermelho: atrasadas
                        bg = new java.awt.Color(255, 102, 102);
                    } else if (dias <= 7) {
                        // laranja: <= 7 dias
                        bg = new java.awt.Color(255, 178, 102);
                    } else if (dias <= 15) {
                        // amarelo: <= 15 dias
                        bg = new java.awt.Color(255, 255, 153);
                    } else {
                        // verde: > 15 dias
                        bg = new java.awt.Color(153, 255, 153);
                    }
                } catch (Exception ignore) {
                // data inválida -> mantém nrmal
                }
            }

        if (!isSelected) {
            c.setBackground(bg);
        }
        return c;
        }
    }   


    private void preencherPerfilComUsuarioLogado() {
        if (usuarioLogado == null) {      
            try {
                txtPerfilNome.setText("");
                txtPerfilUsuario.setText("");
                txtPerfilEmail.setText("");
                txtPerfilCargo.setText("");       
                txtPerfilCpf.setText("");
            } catch (Throwable ignore) {}
        return;
        }   

        try {
            txtPerfilNome.setText(nvl(usuarioLogado.getNome()));
            txtPerfilUsuario.setText(nvl(usuarioLogado.getUsuario())); // se o campo for “login/username”
            txtPerfilEmail.setText(nvl(usuarioLogado.getEmail()));
            txtPerfilCargo.setText(nvl(usuarioLogado.getCargo()));
            txtPerfilCpf.setText(formatCpf(usuarioLogado.getCpf()));

            // deixa os fields não editáveis
            javax.swing.JTextField[] campos = {
                txtPerfilNome, txtPerfilUsuario, txtPerfilEmail,
                txtPerfilCargo, txtPerfilCpf
            };
            for (javax.swing.JTextField tf : campos) {
                if (tf == null) continue;
                tf.setEditable(false);
                tf.setFocusable(false);
                // aparência de desabilitado mantendo legível
                try {
                    tf.setBackground(javax.swing.UIManager.getColor("TextField.inactiveBackground"));
                } catch (Throwable ignore) {}
            }
        } catch (Throwable ex) {
            System.err.println("Erro ao preencher perfil: " + ex.getMessage());
        }
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddMembro;
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnAtualizarEquipes;
    private javax.swing.JButton btnCancelarEquipe;
    private javax.swing.JButton btnConcluirTarefa;
    private javax.swing.JButton btnCriarTarefa;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnEditarDados;
    private javax.swing.JButton btnEditarEquipe;
    private javax.swing.JButton btnEditarProjeto;
    private javax.swing.JButton btnEditarTarefa;
    private javax.swing.JButton btnEquipes;
    private javax.swing.JButton btnExcluirEquipe;
    private javax.swing.JButton btnExcluirProjeto;
    private javax.swing.JButton btnExcluirTarefa;
    private javax.swing.JButton btnNovaEquipe;
    private javax.swing.JButton btnNovoProjeto;
    private javax.swing.JButton btnPerfil;
    private javax.swing.JButton btnProjetos;
    private javax.swing.JButton btnRemoverMembro;
    private javax.swing.JButton btnSair;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSalvarEquipe;
    private javax.swing.JComboBox<Object> cbGestor;
    private javax.swing.JPanel dashboardPanel;
    private javax.swing.JPanel detalheEquipePanel;
    private javax.swing.JPanel equipesPanel;
    private javax.swing.JPanel footerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JList<String> listaDisponiveis;
    private javax.swing.JList<String> listaMembros;
    private javax.swing.JPanel mainContent;
    private javax.swing.JPanel navPanel;
    private javax.swing.JPanel panelEditar;
    private javax.swing.JPanel perfilPanel;
    private javax.swing.JPanel projetosPanel;
    private javax.swing.JPanel sidePanel;
    private javax.swing.JTable tabelaProjetos;
    private javax.swing.JTable tabelaTarefas;
    private javax.swing.JTextField txtNomeEquipe;
    private javax.swing.JTextField txtPerfilCargo;
    private javax.swing.JTextField txtPerfilCpf;
    private javax.swing.JTextField txtPerfilEmail;
    private javax.swing.JTextField txtPerfilNome;
    private javax.swing.JTextField txtPerfilUsuario;
    // End of variables declaration//GEN-END:variables
}
