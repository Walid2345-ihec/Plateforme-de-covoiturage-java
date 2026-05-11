package GUI;

import GUI.ModernUIComponents.Colors;
import GUI.ModernUIComponents.Fonts;
import Models.*;
import Services.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Enhanced Modern Driver Dashboard Panel
 */
public class EnhancedDriverPanel extends JPanel {
    
    private final MainFrame mainFrame;
    private JPanel contentPanel;
    private CardLayout contentLayout;
    
    // Stat cards
    private ModernUIComponents.StatCard placesCard;
    private ModernUIComponents.StatCard trajetsCard;
    private ModernUIComponents.StatCard demandesCard;
    
    // Tables
    private JTable trajetsTable;
    private DefaultTableModel trajetsModel;
    private JTable demandesTable;
    private DefaultTableModel demandesModel;
    private JTable passagersTable;
    private DefaultTableModel passagersModel;
    
    // Passenger mapping for accepted passengers
    private java.util.Map<Integer, Passager> passagersMap = new java.util.HashMap<>();
    
    // Sidebar buttons for selection tracking
    private final java.util.List<ModernUIComponents.SidebarButton> sidebarButtons = new java.util.ArrayList<>();
    
    // Notification badge
    private JLabel notificationBadge;

    // Evaluations dashboard section
    private JPanel evaluationsContainer;
    private ModernUIComponents.StarRating averageStarsWidget;
    private JLabel averageRatingLabel;
    
    public EnhancedDriverPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Colors.SURFACE);
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Sidebar
        add(createSidebar(), BorderLayout.WEST);
        
        // Content area
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(Colors.SURFACE);
        
        contentPanel.add(createDashboardView(), "DASHBOARD");
        contentPanel.add(createTrajetsView(), "TRAJETS");
        contentPanel.add(createDemandesView(), "DEMANDES");
        contentPanel.add(createPassagersView(), "PASSAGERS");
        contentPanel.add(createNewTrajetView(), "NEW_TRAJET");
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(44, 62, 80),
                    0, getHeight(), new Color(52, 73, 94)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        
        // Header with user info
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        headerPanel.setMaximumSize(new Dimension(250, 150));
        
        JLabel avatarLabel = new JLabel("🚗");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Espace Conducteur");
        titleLabel.setFont(Fonts.HEADING_3);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Gérez vos trajets");
        subtitleLabel.setFont(Fonts.CAPTION);
        subtitleLabel.setForeground(new Color(255, 255, 255, 150));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(avatarLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        sidebar.add(headerPanel);
        sidebar.add(Box.createVerticalStrut(20));
        
        // Menu items
        addSidebarButton(sidebar, "Tableau de Bord", "DASHBOARD", true);
        addSidebarButton(sidebar, "Mes Trajets", "TRAJETS", false);
        addSidebarButton(sidebar, "Demandes Reçues", "DEMANDES", false);
        addSidebarButton(sidebar, "Passagers Acceptés", "PASSAGERS", false);
        addSidebarButton(sidebar, "Nouveau Trajet", "NEW_TRAJET", false);
        addSidebarButton(sidebar, "Mes Groupes", "GROUPS", false);
        
        sidebar.add(Box.createVerticalGlue());
        
        // Logout button
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setOpaque(false);
        logoutPanel.setMaximumSize(new Dimension(250, 80));
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        ModernUIComponents.RoundedButton logoutBtn = new ModernUIComponents.RoundedButton(
            "Déconnexion", Colors.ACCENT_CORAL);
        logoutBtn.setPreferredSize(new Dimension(180, 42));
        logoutBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Voulez-vous vous déconnecter ?", 
                "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                mainFrame.showLogin();
            }
        });
        logoutPanel.add(logoutBtn);
        sidebar.add(logoutPanel);
        
        return sidebar;
    }
    
    private void addSidebarButton(JPanel sidebar, String text, String cardName, boolean selected) {
        ModernUIComponents.SidebarButton button = new ModernUIComponents.SidebarButton(text);
        button.setSelected(selected);
        button.setMaximumSize(new Dimension(250, 50));
        button.addActionListener(e -> {
            // Update selection
            for (ModernUIComponents.SidebarButton btn : sidebarButtons) {
                btn.setSelected(false);
            }
            button.setSelected(true);
            
            // For groups, navigate to a top-level page (different layout)
            if (cardName.equals("GROUPS")) {
                mainFrame.showGroupsPanel();
                return;
            }

            // Show content and refresh
            contentLayout.show(contentPanel, cardName);
            if (cardName.equals("DASHBOARD")) refreshDashboard();
            if (cardName.equals("TRAJETS")) refreshTrajetsTable();
            if (cardName.equals("DEMANDES")) refreshDemandesTable();
            if (cardName.equals("PASSAGERS")) refreshPassagersTable();
        });
        sidebarButtons.add(button);
        sidebar.add(button);
    }
    
    private JPanel createDashboardView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Tableau de Bord");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        
        JLabel dateLabel = new JLabel("Bienvenue sur votre espace conducteur");
        dateLabel.setFont(Fonts.BODY);
        dateLabel.setForeground(Colors.TEXT_MUTED);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(dateLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Stats cards
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        placesCard = new ModernUIComponents.StatCard("", "Places Disponibles", "0", Colors.ACCENT_MINT);
        trajetsCard = new ModernUIComponents.StatCard("", "Mes Trajets", "0", Colors.PRIMARY_START);
        demandesCard = new ModernUIComponents.StatCard("", "Demandes", "0", Colors.ACCENT_GOLD);
        
        statsPanel.add(placesCard);
        statsPanel.add(trajetsCard);
        statsPanel.add(demandesCard);

        // ─── Center: stats + evaluations stacked vertically ───
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);

        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerContent.add(statsPanel);

        JPanel evalSection = buildEvaluationsSection();
        evalSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerContent.add(evalSection);
        centerContent.add(Box.createVerticalGlue());

        JScrollPane centerScroll = new JScrollPane(centerContent);
        centerScroll.setBorder(BorderFactory.createEmptyBorder());
        centerScroll.setBackground(Colors.SURFACE);
        centerScroll.getViewport().setBackground(Colors.SURFACE);
        centerScroll.getVerticalScrollBar().setUnitIncrement(16);
        ModernUIComponents.applyModernScrollBar(centerScroll);

        panel.add(centerScroll, BorderLayout.CENTER);

        // Quick actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        ModernUIComponents.GradientButton newTrajetBtn = new ModernUIComponents.GradientButton(
            "Créer un Trajet", Colors.PRIMARY_START, Colors.PRIMARY_END);
        newTrajetBtn.addActionListener(e -> {
            updateSidebarSelection(4);
            contentLayout.show(contentPanel, "NEW_TRAJET");
        });
        actionsPanel.add(newTrajetBtn);
        
        ModernUIComponents.RoundedButton viewDemandesBtn = new ModernUIComponents.RoundedButton(
            "Voir les Demandes", Colors.ACCENT_SKY);
        viewDemandesBtn.addActionListener(e -> {
            updateSidebarSelection(2);
            refreshDemandesTable();
            contentLayout.show(contentPanel, "DEMANDES");
        });
        actionsPanel.add(viewDemandesBtn);

        // Notifications button with badge
        JPanel notificationButtonPanel = createNotificationButtonWithBadge();
        actionsPanel.add(notificationButtonPanel);

        ModernUIComponents.RoundedButton helpBtn = new ModernUIComponents.RoundedButton(
            "Help", Colors.ACCENT_SKY);
        helpBtn.addActionListener(e -> sendHelpRequest());
        actionsPanel.add(helpBtn);

        ModernUIComponents.RoundedButton adminChatBtn = new ModernUIComponents.RoundedButton(
            "Discussion Admin", Colors.ACCENT_MINT);
        adminChatBtn.addActionListener(e -> mainFrame.openAdminConversationForCurrentUser("admin"));
        actionsPanel.add(adminChatBtn);

        ModernUIComponents.RoundedButton complaintBtn = new ModernUIComponents.RoundedButton(
            "Reclamation", Colors.ACCENT_CORAL);
        complaintBtn.addActionListener(e -> submitComplaint());
        actionsPanel.add(complaintBtn);
        
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void sendHelpRequest() {
        mainFrame.openAdminConversationForCurrentUser("help");
    }

    private void submitComplaint() {
        User user = mainFrame.getCurrentUser();
        if (user == null) return;

        String detail = JOptionPane.showInputDialog(this,
            "Decrivez votre reclamation :",
            "Nouvelle reclamation",
            JOptionPane.PLAIN_MESSAGE);
        if (detail == null) return;

        detail = detail.trim();
        if (detail.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "La reclamation ne peut pas etre vide.",
                "Reclamation",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        mainFrame.getGestion().submitComplaint(user, detail);
        mainFrame.openAdminConversationForCurrentUser("reclamation", detail);
    }
    
    /**
     * Construit la section « Mes Évaluations » du tableau de bord :
     * - Moyenne (étoiles + valeur numérique + nombre d'évaluations)
     * - Liste défilante des évaluations reçues : passager, étoiles, date, commentaire
     */
    private JPanel buildEvaluationsSection() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Title
        JLabel sectionTitle = new JLabel("⭐ Mes Évaluations");
        sectionTitle.setFont(Fonts.HEADING_2);
        sectionTitle.setForeground(Colors.TEXT_DARK);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Summary (average) card
        ModernUIComponents.GlassCard summaryCard = new ModernUIComponents.GlassCard();
        summaryCard.setLayout(new BoxLayout(summaryCard, BoxLayout.X_AXIS));
        summaryCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        summaryCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel summaryLeft = new JPanel();
        summaryLeft.setOpaque(false);
        summaryLeft.setLayout(new BoxLayout(summaryLeft, BoxLayout.Y_AXIS));

        JLabel avgTitleLabel = new JLabel("Note moyenne");
        avgTitleLabel.setFont(Fonts.BODY_BOLD);
        avgTitleLabel.setForeground(Colors.TEXT_DARK);

        averageStarsWidget = new ModernUIComponents.StarRating(false, 0, 28);

        averageRatingLabel = new JLabel("Aucune évaluation pour le moment.");
        averageRatingLabel.setFont(Fonts.BODY);
        averageRatingLabel.setForeground(Colors.TEXT_MUTED);

        summaryLeft.add(avgTitleLabel);
        summaryLeft.add(Box.createVerticalStrut(8));
        summaryLeft.add(averageStarsWidget);
        summaryLeft.add(Box.createVerticalStrut(6));
        summaryLeft.add(averageRatingLabel);

        summaryCard.add(summaryLeft);
        summaryCard.add(Box.createHorizontalGlue());

        // Comments container
        evaluationsContainer = new JPanel();
        evaluationsContainer.setOpaque(false);
        evaluationsContainer.setLayout(new BoxLayout(evaluationsContainer, BoxLayout.Y_AXIS));
        evaluationsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane commentsScroll = new JScrollPane(evaluationsContainer);
        commentsScroll.setBorder(BorderFactory.createEmptyBorder());
        commentsScroll.setBackground(Colors.SURFACE);
        commentsScroll.getViewport().setBackground(Colors.SURFACE);
        commentsScroll.setPreferredSize(new Dimension(0, 280));
        commentsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        commentsScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        ModernUIComponents.applyModernScrollBar(commentsScroll);

        section.add(sectionTitle);
        section.add(Box.createVerticalStrut(12));
        section.add(summaryCard);
        section.add(Box.createVerticalStrut(15));
        section.add(commentsScroll);

        return section;
    }

    /**
     * Met à jour la section Évaluations (moyenne + commentaires).
     */
    private void refreshEvaluationsSection() {
        if (evaluationsContainer == null) return;
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;

        java.util.List<Evaluation> evals = mainFrame.getGestion()
                .getEvaluationsPourConducteur(conducteur.getCin());

        // Update summary
        double moyenne = conducteur.getMoyenneEvaluation();
        int rounded = (int) Math.round(moyenne);
        if (averageStarsWidget != null) averageStarsWidget.setRating(rounded);
        if (averageRatingLabel != null) {
            int count = evals.size();
            averageRatingLabel.setText(count == 0
                    ? "Aucune évaluation pour le moment."
                    : String.format("%.1f / 5  •  %d évaluation%s",
                            moyenne, count, count > 1 ? "s" : ""));
        }

        // Update comments list
        evaluationsContainer.removeAll();

        if (evals.isEmpty()) {
            JLabel empty = new JLabel("Vos passagers pourront vous évaluer après leur trajet.");
            empty.setFont(Fonts.BODY);
            empty.setForeground(Colors.TEXT_MUTED);
            empty.setBorder(BorderFactory.createEmptyBorder(20, 5, 5, 5));
            evaluationsContainer.add(empty);
        } else {
            for (Evaluation e : evals) {
                evaluationsContainer.add(buildEvaluationRow(e));
                evaluationsContainer.add(Box.createVerticalStrut(10));
            }
        }
        evaluationsContainer.add(Box.createVerticalGlue());
        evaluationsContainer.revalidate();
        evaluationsContainer.repaint();
    }

    /**
     * Construit une ligne d'évaluation (carte avec passager + étoiles + commentaire + date).
     */
    private JPanel buildEvaluationRow(Evaluation e) {
        ModernUIComponents.GlassCard row = new ModernUIComponents.GlassCard();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Top: passenger + stars + date
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        top.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel name = new JLabel(e.getPassagerName());
        name.setFont(Fonts.BODY_BOLD);
        name.setForeground(Colors.TEXT_DARK);

        ModernUIComponents.StarRating stars = new ModernUIComponents.StarRating(false, e.getRating(), 18);

        java.time.format.DateTimeFormatter fmt =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        JLabel date = new JLabel(e.getDateCreation().format(fmt));
        date.setFont(Fonts.CAPTION);
        date.setForeground(Colors.TEXT_MUTED);

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftTop.setOpaque(false);
        leftTop.add(name);
        leftTop.add(stars);

        top.add(leftTop, BorderLayout.WEST);
        top.add(date, BorderLayout.EAST);

        // Comment
        String commentText = e.getComment().isEmpty() ? "(Sans commentaire)" : e.getComment();
        JTextArea commentArea = new JTextArea(commentText);
        commentArea.setEditable(false);
        commentArea.setFont(Fonts.BODY);
        commentArea.setForeground(e.getComment().isEmpty() ? Colors.TEXT_MUTED : Colors.TEXT_DARK);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setOpaque(false);
        commentArea.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        commentArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        row.add(top);
        row.add(Box.createVerticalStrut(4));
        row.add(commentArea);

        return row;
    }

    private JPanel createTrajetsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Header
        JLabel titleLabel = new JLabel("Mes Trajets");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Départ", "Arrivée", "Durée", "Prix (TND)", "Statut", "Passager"};
        trajetsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        trajetsTable = createModernTable(trajetsModel);
        
        JScrollPane scrollPane = new JScrollPane(trajetsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        ModernUIComponents.applyModernScrollBar(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);
        
        ModernUIComponents.RoundedButton editBtn = new ModernUIComponents.RoundedButton(
            "Modifier Prix", Colors.ACCENT_SKY);
        editBtn.setPreferredSize(new Dimension(150, 42));
        editBtn.addActionListener(e -> modifyTrajetPrice());
        
        ModernUIComponents.RoundedButton deleteBtn = new ModernUIComponents.RoundedButton(
            "Supprimer", Colors.ACCENT_CORAL);
        deleteBtn.setPreferredSize(new Dimension(130, 42));
        deleteBtn.addActionListener(e -> deleteTrajet());
        
        ModernUIComponents.RoundedButton refreshBtn = new ModernUIComponents.RoundedButton(
            "Actualiser", Colors.TEXT_MUTED);
        refreshBtn.setPreferredSize(new Dimension(130, 42));
        refreshBtn.addActionListener(e -> refreshTrajetsTable());

        ModernUIComponents.RoundedButton finishBtn = new ModernUIComponents.RoundedButton(
            "Finir Trajet", Colors.ACCENT_CORAL);
        finishBtn.setPreferredSize(new Dimension(150, 42));
        finishBtn.addActionListener(e -> finishSelectedTrajet());

        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(finishBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createDemandesView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JLabel titleLabel = new JLabel("Demandes Reçues");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"CIN", "Nom", "Prénom", "Téléphone", "Email"};
        demandesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        demandesTable = createModernTable(demandesModel);
        
        JScrollPane scrollPane = new JScrollPane(demandesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        ModernUIComponents.applyModernScrollBar(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);
        
        ModernUIComponents.GradientButton acceptBtn = new ModernUIComponents.GradientButton(
            "Accepter", Colors.ACCENT_MINT, Colors.GRADIENT_TEAL_START);
        acceptBtn.setPreferredSize(new Dimension(150, 45));
        acceptBtn.addActionListener(e -> acceptPassenger());
        
        ModernUIComponents.RoundedButton viewMoreBtn = new ModernUIComponents.RoundedButton(
            "Voir Plus", Colors.ACCENT_SKY);
        viewMoreBtn.setPreferredSize(new Dimension(130, 42));
        viewMoreBtn.addActionListener(e -> showPassengerDetailsFromDemandes());
        
        ModernUIComponents.RoundedButton refreshBtn = new ModernUIComponents.RoundedButton(
            "Actualiser", Colors.TEXT_MUTED);
        refreshBtn.setPreferredSize(new Dimension(130, 42));
        refreshBtn.addActionListener(e -> refreshDemandesTable());
        
        ModernUIComponents.RoundedButton refuseBtn = new ModernUIComponents.RoundedButton(
            "Refuse", Colors.ACCENT_CORAL);
        refuseBtn.setPreferredSize(new Dimension(130, 42));
        refuseBtn.addActionListener(e -> refusePassenger());
        
        buttonPanel.add(acceptBtn);
        buttonPanel.add(viewMoreBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(refuseBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPassagersView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JLabel titleLabel = new JLabel("Passagers Acceptés");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"CIN", "Nom", "Prénom", "Téléphone", "Email", "Adresse"};
        passagersModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        passagersTable = createModernTable(passagersModel);
        // Allow multi-selection so the driver can pick multiple passengers for a group
        passagersTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollPane = new JScrollPane(passagersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        ModernUIComponents.applyModernScrollBar(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);

        ModernUIComponents.RoundedButton deleteBtn = new ModernUIComponents.RoundedButton(
            "Supprimer", Colors.ACCENT_CORAL);
        deleteBtn.setPreferredSize(new Dimension(130, 42));
        deleteBtn.addActionListener(e -> deletePassengerFromAccepted());
        buttonPanel.add(deleteBtn);

        ModernUIComponents.RoundedButton messagingBtn = new ModernUIComponents.RoundedButton(
            "💬 Messagerie", Colors.ACCENT_MINT);
        messagingBtn.setPreferredSize(new Dimension(130, 42));
        messagingBtn.addActionListener(e -> openMessagingWithPassenger());
        buttonPanel.add(messagingBtn);

        ModernUIComponents.GradientButton createGroupBtn = new ModernUIComponents.GradientButton(
            "👥 Créer Groupe", Colors.PRIMARY_START, Colors.PRIMARY_END);
        createGroupBtn.setPreferredSize(new Dimension(170, 42));
        createGroupBtn.addActionListener(e -> createGroupFromSelection());
        buttonPanel.add(createGroupBtn);

        ModernUIComponents.RoundedButton refreshBtn = new ModernUIComponents.RoundedButton(
            "Actualiser", Colors.TEXT_MUTED);
        refreshBtn.setPreferredSize(new Dimension(130, 42));
        refreshBtn.addActionListener(e -> refreshPassagersTable());
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private JPanel createNewTrajetView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JLabel titleLabel = new JLabel("Créer un Nouveau Trajet");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Scroll pane for the form (since it's now longer)
        JPanel mainFormPanel = new JPanel();
        mainFormPanel.setLayout(new BoxLayout(mainFormPanel, BoxLayout.Y_AXIS));
        mainFormPanel.setBackground(Colors.SURFACE);
        mainFormPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Form card part 1 - Basic info
        ModernUIComponents.GlassCard formCard = new ModernUIComponents.GlassCard();
        formCard.setLayout(new GridBagLayout());
        formCard.setMaximumSize(new Dimension(800, 500));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        
        // Departure
        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(createFormLabel("Point de Départ"), gbc);
        gbc.gridx = 1;
        ModernUIComponents.ModernTextField departField = new ModernUIComponents.ModernTextField("Tunis, Sousse...");
        departField.setPreferredSize(new Dimension(280, 45));
        formCard.add(departField, gbc);
        
        // Arrival
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(createFormLabel("Point d'Arrivée"), gbc);
        gbc.gridx = 1;
        ModernUIComponents.ModernTextField arriveeField = new ModernUIComponents.ModernTextField("Destination...");
        formCard.add(arriveeField, gbc);
        
        // Duration
        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(createFormLabel("Durée (minutes)"), gbc);
        gbc.gridx = 1;
        JSpinner dureeSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 480, 5));
        dureeSpinner.setFont(Fonts.BODY);
        dureeSpinner.setPreferredSize(new Dimension(120, 40));
        formCard.add(dureeSpinner, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(createFormLabel("Prix (TND)"), gbc);
        gbc.gridx = 1;
        JSpinner prixSpinner = new JSpinner(new SpinnerNumberModel(5.0, 1.0, 100.0, 0.5));
        prixSpinner.setFont(Fonts.BODY);
        prixSpinner.setPreferredSize(new Dimension(120, 40));
        formCard.add(prixSpinner, gbc);
        
        mainFormPanel.add(formCard);
        mainFormPanel.add(Box.createVerticalStrut(20));
        
        // Weekly Schedule Panel
        WeeklySchedulePanel weeklySchedulePanel = new WeeklySchedulePanel();
        weeklySchedulePanel.setMaximumSize(new Dimension(800, 400));
        mainFormPanel.add(weeklySchedulePanel);
        mainFormPanel.add(Box.createVerticalStrut(20));
        
        // Create button
        ModernUIComponents.GradientButton createBtn = new ModernUIComponents.GradientButton(
            "Créer le Trajet", Colors.ACCENT_MINT, Colors.GRADIENT_TEAL_START);
        createBtn.setPreferredSize(new Dimension(250, 50));
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createBtn.addActionListener(e -> {
            String depart = departField.getText().trim();
            String arrivee = arriveeField.getText().trim();
            int duree = (Integer) dureeSpinner.getValue();
            double prix = (Double) prixSpinner.getValue();
            String weeklySchedule = weeklySchedulePanel.getScheduleAsString();
            
            if (depart.isEmpty() || arrivee.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs !", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (weeklySchedule.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins un jour de la semaine !", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            createTrajet(depart, arrivee, duree, (float) prix, weeklySchedule);
            departField.setText("");
            arriveeField.setText("");
            dureeSpinner.setValue(30);
            prixSpinner.setValue(5.0);
            weeklySchedulePanel.clearSchedule();
        });
        mainFormPanel.add(createBtn);
        mainFormPanel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(mainFormPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Colors.SURFACE);
        scrollPane.getViewport().setBackground(Colors.SURFACE);
        ModernUIComponents.applyModernScrollBar(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Fonts.BODY_BOLD);
        label.setForeground(Colors.TEXT_DARK);
        return label;
    }
    
    private JTable createModernTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(Fonts.BODY);
        table.setRowHeight(45);
        table.setGridColor(Colors.BORDER);
        table.setSelectionBackground(ModernUIComponents.withAlpha(Colors.PRIMARY_START, 50));
        table.setSelectionForeground(Colors.TEXT_DARK);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(Fonts.BODY_BOLD);
        header.setBackground(Colors.SURFACE);
        header.setForeground(Colors.TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Colors.BORDER));
        
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return c;
            }
        });
        
        return table;
    }
    
    private void updateSidebarSelection(int index) {
        for (int i = 0; i < sidebarButtons.size(); i++) {
            sidebarButtons.get(i).setSelected(i == index);
        }
    }
    
    // ==================== Business Logic ====================
    
    private void createTrajet(String depart, String arrivee, int dureeMinutes, float prix, String weeklySchedule) {
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) {
            JOptionPane.showMessageDialog(this, "Erreur: Conducteur non connecté", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            java.time.Duration duree = java.time.Duration.ofMinutes(dureeMinutes);
            Trajet trajet = new Trajet(depart, arrivee, duree, "PENDING", prix, conducteur, 
                                       conducteur.getPlacesDisponibles());
            // Set the weekly schedule on the trajet
            trajet.setWeeklySchedule(weeklySchedule);
            mainFrame.getGestion().getTrajets().add(trajet);
            
            // ═══ Save the conductor's weekly schedule to CSV ═══
            conducteur.setWeeklySchedule(weeklySchedule);
            CSVDatabase.updateConductorWeeklySchedule(conducteur, mainFrame.getGestion());
            
            JOptionPane.showMessageDialog(this, 
                "Trajet créé avec succès !\n" + depart + " → " + arrivee + "\nPrix: " + prix + " TND" +
                "\nJours: " + weeklySchedule,
                "Succès ✓", JOptionPane.INFORMATION_MESSAGE);
            
            refreshDashboard();
            updateSidebarSelection(1);
            contentLayout.show(contentPanel, "TRAJETS");
            refreshTrajetsTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void modifyTrajetPrice() {
        int row = trajetsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un trajet", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String input = JOptionPane.showInputDialog(this, "Nouveau prix (TND):", "Modifier le Prix", JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.isEmpty()) {
            try {
                float newPrice = Float.parseFloat(input);
                Conducteur conducteur = mainFrame.getCurrentConducteur();
                List<Trajet> trajets = mainFrame.getGestion().getTrajets();
                int count = 0;
                
                for (Trajet t : trajets) {
                    if (t.getConducteur() != null && t.getConducteur().getCin().equals(conducteur.getCin())) {
                        if (count == row) {
                            t.setPrix(newPrice);
                            JOptionPane.showMessageDialog(this, "Prix modifié avec succès !", "Succès ✓", JOptionPane.INFORMATION_MESSAGE);
                            refreshTrajetsTable();
                            return;
                        }
                        count++;
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Prix invalide !", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteTrajet() {
        int row = trajetsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un trajet", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        if (JOptionPane.showConfirmDialog(this, "Supprimer ce trajet ?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Conducteur conducteur = mainFrame.getCurrentConducteur();
            List<Trajet> trajets = mainFrame.getGestion().getTrajets();
            int count = 0;
            
            for (int i = 0; i < trajets.size(); i++) {
                Trajet t = trajets.get(i);
                if (t.getConducteur() != null && t.getConducteur().getCin().equals(conducteur.getCin())) {
                    if (count == row) {
                        trajets.remove(i);
                        JOptionPane.showMessageDialog(this, "Trajet supprimé !", "Succès ✓", JOptionPane.INFORMATION_MESSAGE);
                        refreshTrajetsTable();
                        refreshDashboard();
                        return;
                    }
                    count++;
                }
            }
        }
    }
    
    private void acceptPassenger() {
        int row = demandesTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une demande", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;
        
        if (conducteur.getPlacesDisponibles() <= 0) {
            JOptionPane.showMessageDialog(this, "Plus de places disponibles !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Find the corresponding trajet with PENDING_APPROVAL status
        int count = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && 
                t.getConducteur().getCin().equals(conducteur.getCin()) &&
                // show demandes irrespective of the trajet status, as long as there are demandes and places
                !t.getPassagersDemandes().isEmpty() &&
                t.getAvailablePlaces() > 0) {

                for (Passager passager : t.getPassagersDemandes()) {
                    if (count == row) {
                        int confirm = JOptionPane.showConfirmDialog(this,
                            "Accepter la demande de " + passager.getPrenom() + " " + passager.getNom() + " ?\n\n" +
                            "Trajet: " + t.getDepartTrajet() + " → " + t.getArriveeTrajet() + "\n" +
                            "Prix: " + String.format("%.2f", t.getPrix()) + " TND",
                            "Confirmer", JOptionPane.YES_NO_OPTION);

                        if (confirm == JOptionPane.YES_OPTION) {
                            boolean accepted = mainFrame.getGestion().accepter_passager_pour_trajet(t, passager.getCin());
                            if (accepted) {
                                JOptionPane.showMessageDialog(this,
                                    "Passager accepté !\n\n" +
                                    passager.getPrenom() + " " + passager.getNom() + "\n" +
                                    "Téléphone: " + passager.getTel() + "\n\n" +
                                    "Places restantes: " + conducteur.getPlacesDisponibles(),
                                    "Succès", JOptionPane.INFORMATION_MESSAGE);

                                refreshDemandesTable();
                                refreshPassagersTable();
                                refreshDashboard();

                                // Notify other panels (passenger) to refresh their models/views
                                if (mainFrame != null) mainFrame.notifyDataChanged();

                            } else {
                                JOptionPane.showMessageDialog(this, "Impossible d'accepter le passager (place peut-être déjà prise).", "Erreur", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        return;
                    }
                    count++;
                }
            }
        }
        
        JOptionPane.showMessageDialog(this, "Erreur: Demande non trouvée", "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Refuser une demande de passager avec confirmation
     */
    private void refusePassenger() {
        int row = demandesTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une demande", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;
        
        // Find the corresponding passenger
        int count = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && 
                t.getConducteur().getCin().equals(conducteur.getCin()) &&
                !t.getPassagersDemandes().isEmpty() &&
                t.getAvailablePlaces() > 0) {

                for (Passager passager : t.getPassagersDemandes()) {
                    if (count == row) {
                        // Found the passenger - show refusal confirmation dialog
                        showRefuseConfirmationDialog(passager, t);
                        return;
                    }
                    count++;
                }
            }
        }
        
        JOptionPane.showMessageDialog(this, "Erreur: Demande non trouvée", "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Afficher le dialogue de confirmation de refus
     */
    private void showRefuseConfirmationDialog(Passager passager, Trajet trajet) {
        // Create modal dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Confirmation de Refus", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Colors.SURFACE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Message panel
        JPanel messagePanel = new JPanel();
        messagePanel.setOpaque(false);
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        
        // Icon/Title
        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.add(iconLabel);
        
        messagePanel.add(Box.createVerticalStrut(15));
        
        // Question
        JLabel questionLabel = new JLabel("<html><div style='text-align: center;'>Êtes-vous sûr de refuser ce passager ?</div></html>");
        questionLabel.setFont(Fonts.HEADING_3);
        questionLabel.setForeground(Colors.TEXT_DARK);
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.add(questionLabel);
        
        messagePanel.add(Box.createVerticalStrut(20));
        
        // Passenger info
        JLabel passengerLabel = new JLabel("Passager: " + passager.getPrenom() + " " + passager.getNom());
        passengerLabel.setFont(Fonts.BODY);
        passengerLabel.setForeground(Colors.TEXT_MUTED);
        passengerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.add(passengerLabel);
        
        // Trajet info
        JLabel trajetLabel = new JLabel("Trajet: " + trajet.getDepartTrajet() + " → " + trajet.getArriveeTrajet());
        trajetLabel.setFont(Fonts.BODY);
        trajetLabel.setForeground(Colors.TEXT_MUTED);
        trajetLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.add(trajetLabel);
        
        messagePanel.add(Box.createVerticalStrut(20));
        
        // Note
        JLabel noteLabel = new JLabel("<html><div style='text-align: center;'><i>Une notification d'inacceptation sera envoyée au passager</i></div></html>");
        noteLabel.setFont(Fonts.CAPTION);
        noteLabel.setForeground(Colors.ACCENT_CORAL);
        noteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.add(noteLabel);
        
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Delete button
        ModernUIComponents.RoundedButton deleteBtn = new ModernUIComponents.RoundedButton(
            "Supprimer", Colors.ACCENT_CORAL);
        deleteBtn.setPreferredSize(new Dimension(140, 45));
        deleteBtn.addActionListener(e -> {
            // Refuser le passager
            boolean refused = mainFrame.getGestion().refuser_passager_pour_trajet(trajet, passager.getCin());
            if (refused) {
                JOptionPane.showMessageDialog(EnhancedDriverPanel.this, 
                    "Demande refusée.\nNotification envoyée au passager.",
                    "Succès ✓", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh tables
                refreshDemandesTable();
                refreshDashboard();
                
                // Notify other panels to refresh
                if (mainFrame != null) mainFrame.notifyDataChanged();
            } else {
                JOptionPane.showMessageDialog(EnhancedDriverPanel.this, 
                    "Erreur lors du refus de la demande.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
            dialog.dispose();
        });
        
        // Cancel button
        ModernUIComponents.RoundedButton cancelBtn = new ModernUIComponents.RoundedButton(
            "Annuler", Colors.TEXT_MUTED);
        cancelBtn.setPreferredSize(new Dimension(140, 45));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonsPanel.add(deleteBtn);
        buttonsPanel.add(cancelBtn);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Finish the selected trajet: mark FINISHED, restore conducteur places,
     * set chercheCovoit=true for accepted passengers, and refresh views.
     */
    private void finishSelectedTrajet() {
        int row = trajetsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un trajet", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) {
            JOptionPane.showMessageDialog(this, "Erreur: Conducteur non connecté", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Trajet> trajets = mainFrame.getGestion().getTrajets();
        int count = 0;
        for (Trajet t : trajets) {
            if (t.getConducteur() != null && t.getConducteur().getCin().equals(conducteur.getCin())) {
                if (count == row) {
                    int acceptedCount = (t.getPassagersAcceptes() != null) ? t.getPassagersAcceptes().size() : 0;

                    // Restore places: prefer trajet.maxPlaces if defined, otherwise add acceptedCount to current
                    int restoredPlaces = -1;
                    try {
                        restoredPlaces = t.getMaxPlaces();
                    } catch (Exception ignored) {}
                    if (restoredPlaces <= 0) {
                        restoredPlaces = conducteur.getPlacesDisponibles() + acceptedCount;
                    }

                    // Set status FINISHED
                    try {
                        t.setStatusTrajet(Trajet.STATUS_FINISHED);
                    } catch (Exception ex) {
                        // fallback to direct assignment if setter rejects but constant valid
                        // (shouldn't happen)
                        // ignore
                    }
                    t.setTrajet_valide(false);

                    // Reset passagers chercheCovoit to true
                    if (t.getPassagersAcceptes() != null) {
                        for (Passager p : t.getPassagersAcceptes()) {
                            if (p != null) p.setChercheCovoit(true);
                        }
                    }

                    // Restore conducteur places
                    conducteur.setPlacesDisponibles(restoredPlaces);

                    JOptionPane.showMessageDialog(this, "Trajet terminé. Places restaurées et passagers remis en recherche.", "Succès", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh local views and notify other panels
                    refreshTrajetsTable();
                    refreshPassagersTable();
                    refreshDashboard();
                    if (mainFrame != null) mainFrame.notifyDataChanged();
                    return;
                }
                count++;
            }
        }

        JOptionPane.showMessageDialog(this, "Trajet non trouvé", "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // ==================== Refresh Methods ====================
    
    public void refresh() {
        refreshDashboard();
        refreshTrajetsTable();
        refreshDemandesTable();
        refreshPassagersTable();
        updateSidebarSelection(0);
        contentLayout.show(contentPanel, "DASHBOARD");
    }

    /**
     * Refresh only data models/tables without changing the visible card.
     */
    public void refreshModels() {
        refreshDashboard();
        refreshTrajetsTable();
        refreshDemandesTable();
        refreshPassagersTable();
    }

    private void refreshDashboard() {
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;
        
        placesCard.setValue(String.valueOf(conducteur.getPlacesDisponibles()));
        
        int trajetCount = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && t.getConducteur().getCin().equals(conducteur.getCin())) {
                trajetCount++;
            }
        }
        trajetsCard.setValue(String.valueOf(trajetCount));
        // Count total demandes for this conducteur (across all their trajets)
        int demandesCount = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && t.getConducteur().getCin().equals(conducteur.getCin())) {
                if (t.getPassagersDemandes() != null) demandesCount += t.getPassagersDemandes().size();
            }
        }
        demandesCard.setValue(String.valueOf(demandesCount));

        // Update notification badge
        updateNotificationBadge();

        // Update evaluations section
        refreshEvaluationsSection();
    }
    
    private void refreshTrajetsTable() {
        trajetsModel.setRowCount(0);
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;
        
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && t.getConducteur().getCin().equals(conducteur.getCin())) {
                trajetsModel.addRow(new Object[]{
                    t.getDepartTrajet(),
                    t.getArriveeTrajet(),
                    t.getDureeTrajet().toMinutes() + " min",
                    String.format("%.2f", t.getPrix()),
                    t.getStatusTrajet(),
                    t.getPassagersAcceptes().isEmpty() ? "En attente" : t.getPassagersAcceptes().size() + " accepté(s)"
                });
            }
        }
    }
    
    private void refreshDemandesTable() {
        demandesModel.setRowCount(0);
        
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;
        
        // FIXED: Only show passengers who have requested THIS driver's trajets
        // and are awaiting approval (PENDING_APPROVAL status)
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            // Only show demandes for:
            // 1. Trajets belonging to this conductor
            // 2. With PENDING_APPROVAL status (passenger has requested)
            // 3. With one or more passengers in demandes list
            if (t.getConducteur() != null &&
                t.getConducteur().getCin().equals(conducteur.getCin()) &&
                !t.getPassagersDemandes().isEmpty() &&
                t.getAvailablePlaces() > 0) {

                for (Passager p : t.getPassagersDemandes()) {
                    // PRIVACY: Mask CIN - only show last 3 digits
                    String maskedCin = "*****" + p.getCin().substring(Math.max(0, p.getCin().length() - 3));
                    demandesModel.addRow(new Object[]{
                        maskedCin,
                        p.getNom(),
                        p.getPrenom(),
                        maskPhone(p.getTel()),
                        maskEmail(p.getMail())
                    });
                }
            }
        }
    }
    
    // Privacy helper methods
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "****" + phone.substring(phone.length() - 4);
    }
    
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***@***";
        int atIndex = email.indexOf("@");
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        if (local.length() <= 2) return local + "***" + domain;
        return local.substring(0, 2) + "***" + domain;
    }
    
    private void refreshPassagersTable() {
        passagersModel.setRowCount(0);
        passagersMap.clear();
        int rowIndex = 0;
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;
        
        // Only show accepted passengers (IN_PROGRESS or FINISHED status)
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && 
                t.getConducteur().getCin().equals(conducteur.getCin()) && 
                (t.isInProgress() || t.isFinished()) &&
                !t.getPassagersAcceptes().isEmpty()) {

                for (Passager p : t.getPassagersAcceptes()) {
                    // For accepted passengers, show full contact info (they have a confirmed booking)
                    // But still mask CIN for privacy
                    String maskedCin = "*****" + p.getCin().substring(Math.max(0, p.getCin().length() - 3));
                    passagersModel.addRow(new Object[]{
                        maskedCin, p.getNom(), p.getPrenom(), p.getTel(), p.getMail(), p.getAdresse()
                    });
                    passagersMap.put(rowIndex, p);
                    rowIndex++;
                }
            }
        }
    }
    
    /**
     * Show passenger details in a modal dialog from the Demandes table
     */
    private void showPassengerDetailsFromDemandes() {
        int row = demandesTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une demande", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;
        
        // Find the corresponding passenger
        int count = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && 
                t.getConducteur().getCin().equals(conducteur.getCin()) &&
                !t.getPassagersDemandes().isEmpty() &&
                t.getAvailablePlaces() > 0) {

                for (Passager passenger : t.getPassagersDemandes()) {
                    if (count == row) {
                        // Found the passenger - display details modal
                        displayPassengerDetailsModal(passenger, t);
                        return;
                    }
                    count++;
                }
            }
        }
    }
    
    /**
     * Display complete passenger details in a modal dialog
     */
    private void displayPassengerDetailsModal(Passager passenger, Trajet trajet) {
        // Create modal dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Détails du Passager", true);
        dialog.setSize(600, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Colors.SURFACE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Title
        JLabel titleLabel = new JLabel("👤 Informations du Passager");
        titleLabel.setFont(Fonts.HEADING_2);
        titleLabel.setForeground(Colors.TEXT_DARK);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Trajet info
        JLabel trajetLabel = new JLabel(
            "Trajet: " + trajet.getDepartTrajet() + " → " + trajet.getArriveeTrajet()
        );
        trajetLabel.setFont(Fonts.BODY);
        trajetLabel.setForeground(Colors.TEXT_MUTED);
        trajetLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        headerPanel.add(trajetLabel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Add details with labels and values
        detailsPanel.add(createDetailRow("CIN:", passenger.getCin()));
        detailsPanel.add(createDetailRow("Nom:", passenger.getNom()));
        detailsPanel.add(createDetailRow("Prénom:", passenger.getPrenom()));
        detailsPanel.add(createDetailRow("Téléphone:", passenger.getTel()));
        detailsPanel.add(createDetailRow("Email:", passenger.getMail()));
        detailsPanel.add(createDetailRow("Adresse:", passenger.getAdresse()));
        detailsPanel.add(createDetailRow("Année Universitaire:", passenger.getAnneeUniversitaire() != null ? 
            passenger.getAnneeUniversitaire().toString() : "N/A"));
        detailsPanel.add(createDetailRow("Cherche Covoiturage:", passenger.isChercheCovoit() ? "Oui" : "Non"));
        detailsPanel.add(createCardDetailRow("Carte:", passenger.getCardDisplayLabel(), passenger.getCard()));
        
        // Add spacer
        detailsPanel.add(Box.createVerticalStrut(10));
        
        // Trajet details
        JLabel trajetDetailsTitle = new JLabel("📋 Détails du Trajet");
        trajetDetailsTitle.setFont(Fonts.BODY_BOLD);
        trajetDetailsTitle.setForeground(Colors.TEXT_DARK);
        detailsPanel.add(trajetDetailsTitle);
        
        detailsPanel.add(createDetailRow("Départ:", trajet.getDepartTrajet()));
        detailsPanel.add(createDetailRow("Arrivée:", trajet.getArriveeTrajet()));
        detailsPanel.add(createDetailRow("Prix:", String.format("%.2f TND", trajet.getPrix())));
        detailsPanel.add(createDetailRow("Durée:", trajet.getDureeTrajet().toMinutes() + " minutes"));
        detailsPanel.add(createDetailRow("Places disponibles:", String.valueOf(trajet.getAvailablePlaces())));
        
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Colors.SURFACE);
        scrollPane.getViewport().setBackground(Colors.SURFACE);
        ModernUIComponents.applyModernScrollBar(scrollPane);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer panel with close button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        ModernUIComponents.RoundedButton closeBtn = new ModernUIComponents.RoundedButton(
            "✕ Fermer", Colors.TEXT_MUTED);
        closeBtn.setPreferredSize(new Dimension(150, 42));
        closeBtn.addActionListener(e -> dialog.dispose());
        
        footerPanel.add(closeBtn);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Create a detail row with label and value
     */
    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(Fonts.BODY_BOLD);
        labelComponent.setForeground(Colors.TEXT_DARK);
        labelComponent.setPreferredSize(new Dimension(150, 30));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(Fonts.BODY);
        valueComponent.setForeground(Colors.TEXT_MUTED);
        
        row.add(labelComponent, BorderLayout.WEST);
        row.add(valueComponent, BorderLayout.CENTER);
        
        return row;
    }

    private JPanel createCardDetailRow(String label, String value, String cardValue) {
        JPanel row = createDetailRow(label, "● " + value);
        Component component = row.getComponent(1);
        if (component instanceof JLabel valueComponent) {
            valueComponent.setForeground(getCardColor(cardValue));
            valueComponent.setFont(Fonts.BODY_BOLD);
        }
        return row;
    }

    private Color getCardColor(String cardValue) {
        return switch (cardValue == null ? "verte" : cardValue.toLowerCase()) {
            case "jaune" -> Colors.ACCENT_GOLD;
            case "rouge" -> Colors.ACCENT_CORAL;
            default -> Colors.ACCENT_MINT;
        };
    }
    
    /**
     * Delete a passenger from the accepted passengers list
     */
    private void deletePassengerFromAccepted() {
        int selectedRow = passagersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un passager à supprimer");
            return;
        }

        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) {
            JOptionPane.showMessageDialog(this, "Erreur: Conducteur non connecté");
            return;
        }

        // Find the corresponding trajet and passager
        int count = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null &&
                t.getConducteur().getCin().equals(conducteur.getCin()) &&
                (t.isInProgress() || t.isFinished())) {

                for (Passager p : t.getPassagersAcceptes()) {
                    if (count == selectedRow) {
                        // Display confirmation dialog with passenger details
                        String passagerFullName = p.getPrenom() + " " + p.getNom();
                        int confirm = JOptionPane.showConfirmDialog(this,
                            "Êtes-vous sûr de supprimer ce passager ?\n\n" +
                            "Passager: " + passagerFullName + "\n" +
                            "Téléphone: " + p.getTel() + "\n" +
                            "Email: " + p.getMail(),
                            "Confirmer la suppression",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);

                        if (confirm == JOptionPane.YES_OPTION) {
                            // Call the deletion logic from Gestion_covoiturage
                            boolean deleted = mainFrame.getGestion().supprimer_passager_accepte(
                                t, 
                                p.getCin(), 
                                conducteur.getCin()
                            );

                            if (deleted) {
                                JOptionPane.showMessageDialog(this,
                                    "Passager supprimé avec succès !\n\n" +
                                    passagerFullName + " a été retiré du trajet.\n" +
                                    "Une notification a été envoyée au passager.\n" +
                                    "Places restantes: " + conducteur.getPlacesDisponibles());

                                refreshPassagersTable();
                                refreshDashboard();
                                refreshDemandesTable();

                                // Notify main frame to refresh other panels (passenger view)
                                if (mainFrame != null) {
                                    mainFrame.notifyDataChanged();
                                }
                            } else {
                                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression du passager.");
                            }
                        }
                        return;
                    }
                    count++;
                }
            }
        }
    }
    
    /**
     * Créer un bouton de notifications avec un badge pour le conducteur
     */
    private JPanel createNotificationButtonWithBadge() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(200, 42));
        
        ModernUIComponents.RoundedButton notifBtn = new ModernUIComponents.RoundedButton(
            "📬 Voir Notifications", Colors.ACCENT_MINT);
        notifBtn.addActionListener(e -> {
            mainFrame.showDriverNotificationPanel();
        });
        
        panel.add(notifBtn);
        
        // Badge avec le nombre de notifications non lues
        notificationBadge = new JLabel("0") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw red circle
                g2.setColor(Color.RED);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // Draw text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        notificationBadge.setPreferredSize(new Dimension(24, 24));
        notificationBadge.setMaximumSize(new Dimension(24, 24));
        notificationBadge.setHorizontalAlignment(SwingConstants.CENTER);
        notificationBadge.setVerticalAlignment(SwingConstants.CENTER);
        notificationBadge.setOpaque(true);
        notificationBadge.setBackground(Color.RED);
        notificationBadge.setForeground(Color.WHITE);
        notificationBadge.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Position le badge en haut à droite
        JPanel badgeContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        badgeContainer.setLayout(new BorderLayout());
        badgeContainer.setOpaque(false);
        badgeContainer.setPreferredSize(new Dimension(30, 30));
        badgeContainer.add(notificationBadge, BorderLayout.CENTER);
        
        panel.add(Box.createHorizontalStrut(8));
        panel.add(badgeContainer);
        
        // Mettre à jour le badge avec le nombre de notifications
        updateNotificationBadge();
        
        return panel;
    }
    
    /**
     * Mettre à jour le badge des notifications du conducteur
     */
    private void updateNotificationBadge() {
        if (notificationBadge != null) {
            String conducteurCIN = mainFrame.getCurrentUser() != null ? mainFrame.getCurrentUser().getCin() : "";
            if (!conducteurCIN.isEmpty()) {
                int count = mainFrame.getGestion().compterNotificationsNonLuesConducteur(conducteurCIN);
                if (count > 0) {
                    notificationBadge.setText(String.valueOf(Math.min(count, 99))); // Max 99 affichées
                    notificationBadge.setVisible(true);
                } else {
                    notificationBadge.setVisible(false);
                }
            } else {
                notificationBadge.setVisible(false);
            }
        }
    }
    
    /**
     * Crée un groupe à partir des passagers sélectionnés dans le tableau.
     * Affiche une boîte de dialogue pour saisir le nom du groupe et envoie
     * une notification d'appartenance à chaque passager membre.
     */
    private void createGroupFromSelection() {
        int[] selectedRows = passagersTable.getSelectedRows();
        if (selectedRows == null || selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner au moins un passager.\n" +
                "Astuce: maintenez Ctrl pour sélectionner plusieurs lignes.",
                "Sélection requise", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) {
            JOptionPane.showMessageDialog(this, "Erreur: Conducteur non connecté",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Collecter les passagers uniques (un même passager peut apparaître dans plusieurs lignes
        // s'il est accepté sur plusieurs trajets)
        java.util.LinkedHashMap<String, Passager> uniqueByCin = new java.util.LinkedHashMap<>();
        for (int row : selectedRows) {
            Passager p = passagersMap.get(row);
            if (p != null) uniqueByCin.put(p.getCin(), p);
        }
        if (uniqueByCin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun passager valide sélectionné.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Demander le nom du groupe
        String groupName = JOptionPane.showInputDialog(this,
            "Nom du groupe (" + uniqueByCin.size() + " passager(s) sélectionné(s)):",
            "Créer un groupe", JOptionPane.PLAIN_MESSAGE);

        if (groupName == null) return; // Annulé
        groupName = groupName.trim();
        if (groupName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom du groupe ne peut pas être vide.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.util.List<String> memberCins = new java.util.ArrayList<>(uniqueByCin.keySet());
        Models.Group created = mainFrame.getGestion().creerGroupe(
            groupName, conducteur.getCin(), memberCins);

        if (created == null) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la création du groupe.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Persister
        Services.CSVDatabase.saveGroups(mainFrame.getGestion().getGroups());
        Services.CSVDatabase.saveAllNotifications(mainFrame.getGestion());
        mainFrame.markUnsavedChanges();

        StringBuilder sb = new StringBuilder();
        sb.append("Groupe « ").append(groupName).append(" » créé avec succès !\n\n");
        sb.append("Membres (").append(uniqueByCin.size()).append(") :\n");
        for (Passager p : uniqueByCin.values()) {
            sb.append("• ").append(p.getPrenom()).append(" ").append(p.getNom()).append("\n");
        }
        sb.append("\nUne notification d'appartenance a été envoyée à chaque passager.");

        JOptionPane.showMessageDialog(this, sb.toString(),
            "Groupe créé ✓", JOptionPane.INFORMATION_MESSAGE);

        if (mainFrame != null) mainFrame.notifyDataChanged();
    }

    /**
     * Ouvrir la messagerie avec le passager sélectionné
     */
    private void openMessagingWithPassenger() {
        int selectedRow = passagersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un passager", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Passager passenger = passagersMap.get(selectedRow);
        if (passenger == null) {
            JOptionPane.showMessageDialog(this, "Passager non trouvé", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Conducteur conductor = mainFrame.getCurrentConducteur();
        if (conductor == null) return;
        
        // Ouvrir la MessagingPanel
        MessagingPanel messagingPanel = new MessagingPanel(mainFrame, conductor, passenger);
        mainFrame.showMessagingPanel(messagingPanel);
    }
}
