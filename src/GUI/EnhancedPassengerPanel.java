package GUI;

import GUI.ModernUIComponents.Colors;
import GUI.ModernUIComponents.Fonts;
import Models.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Enhanced Modern Passenger Dashboard Panel
 */
public class EnhancedPassengerPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JPanel contentPanel;
    private CardLayout contentLayout;
    
    // Stat cards
    private ModernUIComponents.StatCard reservationsCard;
    private ModernUIComponents.StatCard trajetsDispoCard;
    private ModernUIComponents.StatCard statusCard;
    
    // Tables
    private JTable trajetsTable;
    private DefaultTableModel trajetsModel;
    private JTable mesReservationsTable;
    private DefaultTableModel reservationsModel;
    
    // Notification badge
    private JLabel notificationBadge;
    
    // Sidebar buttons
    private java.util.List<ModernUIComponents.SidebarButton> sidebarButtons = new java.util.ArrayList<>();
    
    public EnhancedPassengerPanel(MainFrame mainFrame) {
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
        contentPanel.add(createSearchTrajetsView(), "SEARCH");
        contentPanel.add(createMesReservationsView(), "RESERVATIONS");
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Green gradient for passenger
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(39, 174, 96),
                    0, getHeight(), new Color(22, 160, 133)
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
        
        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Espace Passager");
        titleLabel.setFont(Fonts.HEADING_3);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Trouvez votre trajet");
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
        addSidebarButton(sidebar, "Rechercher Trajets", "SEARCH", false);
        addSidebarButton(sidebar, "Mes Réservations", "RESERVATIONS", false);
        
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
            for (ModernUIComponents.SidebarButton btn : sidebarButtons) {
                btn.setSelected(false);
            }
            button.setSelected(true);
            contentLayout.show(contentPanel, cardName);
            
            if (cardName.equals("DASHBOARD")) refreshDashboard();
            if (cardName.equals("SEARCH")) refreshTrajetsTable();
            if (cardName.equals("RESERVATIONS")) refreshReservationsTable();
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
        
        JLabel subtitleLabel = new JLabel("Bienvenue sur votre espace passager");
        subtitleLabel.setFont(Fonts.BODY);
        subtitleLabel.setForeground(Colors.TEXT_MUTED);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Stats cards
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        trajetsDispoCard = new ModernUIComponents.StatCard("🚗", "Trajets Disponibles", "0", Colors.ACCENT_MINT);
        reservationsCard = new ModernUIComponents.StatCard("🎫", "Mes Réservations", "0", Colors.PRIMARY_START);
        statusCard = new ModernUIComponents.StatCard("📍", "Statut", "En recherche", Colors.ACCENT_SKY);
        
        statsPanel.add(trajetsDispoCard);
        statsPanel.add(reservationsCard);
        statsPanel.add(statusCard);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Quick actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        ModernUIComponents.GradientButton searchBtn = new ModernUIComponents.GradientButton(
            "Rechercher un Trajet", Colors.ACCENT_MINT, Colors.GRADIENT_TEAL_START);
        searchBtn.addActionListener(e -> {
            updateSidebarSelection(1);
            refreshTrajetsTable();
            contentLayout.show(contentPanel, "SEARCH");
        });
        actionsPanel.add(searchBtn);
        
        ModernUIComponents.RoundedButton reservationsBtn = new ModernUIComponents.RoundedButton(
            "Voir mes Réservations", Colors.ACCENT_SKY);
        reservationsBtn.addActionListener(e -> {
            updateSidebarSelection(2);
            refreshReservationsTable();
            contentLayout.show(contentPanel, "RESERVATIONS");
        });
        actionsPanel.add(reservationsBtn);
        
        // Notifications button with badge
        JPanel notificationButtonPanel = createNotificationButtonWithBadge();
        actionsPanel.add(notificationButtonPanel);
        
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Créer un bouton de notifications avec un badge
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
            "📬 Voir Notifications", Colors.ACCENT_CORAL);
        notifBtn.addActionListener(e -> {
            mainFrame.showNotificationPanel();
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
     * Mettre à jour le badge des notifications
     */
    private void updateNotificationBadge() {
        if (notificationBadge != null) {
            String passagerCIN = mainFrame.getCurrentUser() != null ? mainFrame.getCurrentUser().getCin() : "";
            if (!passagerCIN.isEmpty()) {
                int count = mainFrame.getGestion().compterNotificationsNonLues(passagerCIN);
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
    
    private JPanel createSearchTrajetsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Header with title
        JLabel titleLabel = new JLabel("Rechercher des Trajets");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Main content with search form and table
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);
        
        // Search filters card
        ModernUIComponents.GlassCard searchCard = new ModernUIComponents.GlassCard();
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));
        searchCard.setPreferredSize(new Dimension(0, 80));
        
        JLabel filterLabel = new JLabel("Filtres:");
        filterLabel.setFont(Fonts.BODY_BOLD);
        filterLabel.setForeground(Colors.TEXT_DARK);
        searchCard.add(filterLabel);
        
        ModernUIComponents.ModernTextField departFilter = new ModernUIComponents.ModernTextField("Ville de départ...");
        departFilter.setPreferredSize(new Dimension(200, 45));
        searchCard.add(departFilter);
        
        ModernUIComponents.ModernTextField arriveeFilter = new ModernUIComponents.ModernTextField("Ville d'arrivée...");
        arriveeFilter.setPreferredSize(new Dimension(200, 45));
        searchCard.add(arriveeFilter);
        
        ModernUIComponents.RoundedButton filterBtn = new ModernUIComponents.RoundedButton("🔍 Filtrer", Colors.ACCENT_SKY);
        filterBtn.setPreferredSize(new Dimension(120, 42));
        filterBtn.addActionListener(e -> filterTrajets(departFilter.getText(), arriveeFilter.getText()));
        searchCard.add(filterBtn);
        
        ModernUIComponents.RoundedButton resetBtn = new ModernUIComponents.RoundedButton("↻ Reset", Colors.TEXT_MUTED);
        resetBtn.setPreferredSize(new Dimension(100, 42));
        resetBtn.addActionListener(e -> {
            departFilter.setText("");
            arriveeFilter.setText("");
            refreshTrajetsTable();
        });
        searchCard.add(resetBtn);
        
        mainContent.add(searchCard, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Conducteur", "Départ", "Arrivée", "Durée", "Prix (TND)", "Places", "Voiture"};
        trajetsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        trajetsTable = createModernTable(trajetsModel);
        
        JScrollPane scrollPane = new JScrollPane(trajetsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        ModernUIComponents.applyModernScrollBar(scrollPane);
        mainContent.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(mainContent, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        ModernUIComponents.GradientButton reserveBtn = new ModernUIComponents.GradientButton(
            "Réserver ce Trajet", Colors.ACCENT_MINT, Colors.GRADIENT_TEAL_START);
        reserveBtn.setPreferredSize(new Dimension(200, 50));
        reserveBtn.addActionListener(e -> reserveTrajet());
        
        ModernUIComponents.RoundedButton refreshBtn = new ModernUIComponents.RoundedButton(
            "Actualiser", Colors.TEXT_MUTED);
        refreshBtn.setPreferredSize(new Dimension(130, 45));
        refreshBtn.addActionListener(e -> refreshTrajetsTable());
        
        ModernUIComponents.RoundedButton moreBtn = new ModernUIComponents.RoundedButton(
            "📋 Voir plus", Colors.ACCENT_SKY);
        moreBtn.setPreferredSize(new Dimension(140, 45));
        moreBtn.addActionListener(e -> showTrajetDetails());
        
        buttonPanel.add(reserveBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(moreBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;

    }
    
    private JPanel createMesReservationsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JLabel titleLabel = new JLabel("🎫 Mes Réservations");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Conducteur", "Téléphone", "Voiture", "Départ", "Arrivée", "Prix (TND)"};
        reservationsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mesReservationsTable = createModernTable(reservationsModel);
        
        JScrollPane scrollPane = new JScrollPane(mesReservationsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        ModernUIComponents.applyModernScrollBar(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Info panel at bottom
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        
        ModernUIComponents.RoundedButton refreshBtn = new ModernUIComponents.RoundedButton(
            "Actualiser", Colors.TEXT_MUTED);
        refreshBtn.setPreferredSize(new Dimension(130, 42));
        refreshBtn.addActionListener(e -> refreshReservationsTable());
        infoPanel.add(refreshBtn);
        
        JLabel infoLabel = new JLabel("Contactez le conducteur pour confirmer votre réservation");
        infoLabel.setFont(Fonts.BODY);
        infoLabel.setForeground(Colors.TEXT_MUTED);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        infoPanel.add(infoLabel);
        
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JTable createModernTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(Fonts.BODY);
        table.setRowHeight(50);
        table.setGridColor(Colors.BORDER);
        table.setSelectionBackground(ModernUIComponents.withAlpha(Colors.ACCENT_MINT, 50));
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
        
        // Alternating row colors with green tint
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 252, 248));
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
    
    private void filterTrajets(String depart, String arrivee) {
        trajetsModel.setRowCount(0);

        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            // Show trajet only if it has a conductor and available places
            Conducteur c = t.getConducteur();
            // Exclude finished trajets as they should no longer be available for reservation
            if (c == null || t.getAvailablePlaces() <= 0 || t.isFinished()) continue;

            boolean matchDepart = depart.isEmpty() ||
                t.getDepartTrajet().toLowerCase().contains(depart.toLowerCase());
            boolean matchArrivee = arrivee.isEmpty() || 
                t.getArriveeTrajet().toLowerCase().contains(arrivee.toLowerCase());

            if (matchDepart && matchArrivee) {
                trajetsModel.addRow(new Object[]{
                    c.getNom() + " " + c.getPrenom(),
                    t.getDepartTrajet(),
                    t.getArriveeTrajet(),
                    t.getDureeTrajet().toMinutes() + " min",
                    String.format("%.2f", t.getPrix()),
                    t.getAvailablePlaces(),
                    c.getNomVoiture() + " " + c.getMarqueVoiture()
                });
            }
        }
    }

    private void reserveTrajet() {
        int row = trajetsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un trajet", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Passager passager = mainFrame.getCurrentPassager();
        if (passager == null) {
            JOptionPane.showMessageDialog(this, "Erreur: Passager non connecté", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String conducteurName = (String) trajetsModel.getValueAt(row, 0);
        String depart = (String) trajetsModel.getValueAt(row, 1);
        String arrivee = (String) trajetsModel.getValueAt(row, 2);
        String prix = (String) trajetsModel.getValueAt(row, 4);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirmer la demande de réservation ?\n\n" +
            "Conducteur: " + conducteurName + "\n" +
            "📍 " + depart + " → " + arrivee + "\n" +
            "Prix: " + prix + " TND",
            "Demande de Réservation",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Find the trajet and add a demande (do not decrement places yet)
            for (Trajet t : mainFrame.getGestion().getTrajets()) {
                Conducteur c = t.getConducteur();
                if (c == null) continue;

                String fullName = c.getNom() + " " + c.getPrenom();
                if (fullName.equals(conducteurName) && 
                    t.getDepartTrajet().equals(depart) && 
                    t.getArriveeTrajet().equals(arrivee)) {

                    // Ignore finished trajets - shouldn't be reservable
                    if (t.isFinished()) {
                        JOptionPane.showMessageDialog(this, "Ce trajet est terminé et n'est plus disponible.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                     boolean added = mainFrame.getGestion().ajouter_demande_pour_trajet(t, passager.getCin());
                    if (added) {
                        // Also keep conductor-level mapping for notifications
                        if (t.getConducteur() != null) {
                            mainFrame.getGestion().ajouter_demande_pour_conducteur(t.getConducteur().getCin(), passager.getCin());
                        }

                        JOptionPane.showMessageDialog(this,
                            "Demande envoyée !\n\n" +
                            "Le conducteur sera notifié et pourra accepter votre demande.",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Vous avez déjà demandé ou été accepté pour ce trajet.",
                            "Info", JOptionPane.INFORMATION_MESSAGE);
                    }

                    // Refresh relevant views including reservations (pending + accepted)
                    refreshTrajetsTable();
                    refreshDashboard();
                    refreshReservationsTable();

                    // Notify main frame so driver panels / other views refresh immediately
                    if (mainFrame != null) {
                        mainFrame.notifyDataChanged();
                    }
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Trajet non disponible",
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Shows a detailed view of the selected trajet with the conductor's weekly schedule
     */
    private void showTrajetDetails() {
        int row = trajetsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un trajet", 
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // Get trajet information from selected row
            String conducteurName = (String) trajetsModel.getValueAt(row, 0);
            String depart = (String) trajetsModel.getValueAt(row, 1);
            String arrivee = (String) trajetsModel.getValueAt(row, 2);
            
            // Find the trajet object to get conductor's schedule
            Trajet selectedTrajet = null;
            for (Trajet t : mainFrame.getGestion().getTrajets()) {
                Conducteur c = t.getConducteur();
                if (c == null) continue;
                
                String fullName = c.getNom() + " " + c.getPrenom();
                if (fullName.equals(conducteurName) && 
                    t.getDepartTrajet().equals(depart) && 
                    t.getArriveeTrajet().equals(arrivee)) {
                    selectedTrajet = t;
                    break;
                }
            }

            if (selectedTrajet == null) {
                JOptionPane.showMessageDialog(this, "Trajet non trouvé", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Conducteur conductor = selectedTrajet.getConducteur();
            if (conductor == null) {
                JOptionPane.showMessageDialog(this, "Conducteur introuvable", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create detail dialog with schedule
            JDialog detailDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                "Détails du Trajet", true);
            detailDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            detailDialog.setSize(500, 550);
            detailDialog.setLocationRelativeTo(this);
            detailDialog.setResizable(false);

            // Main panel
            JPanel mainPanel = new JPanel();
            mainPanel.setBackground(Colors.SURFACE);
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Title
            JLabel titleLabel = new JLabel("📋 Horaires de la Semaine");
            titleLabel.setFont(Fonts.HEADING_2);
            titleLabel.setForeground(Colors.TEXT_DARK);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(titleLabel);
            mainPanel.add(Box.createVerticalStrut(15));

            // Conductor info
            JLabel conductorLabel = new JLabel("Conducteur: " + conductor.getNom() + " " + conductor.getPrenom());
            conductorLabel.setFont(Fonts.BODY_BOLD);
            conductorLabel.setForeground(Colors.TEXT_DARK);
            mainPanel.add(conductorLabel);
            mainPanel.add(Box.createVerticalStrut(5));

            JLabel routeLabel = new JLabel(depart + " → " + arrivee);
            routeLabel.setFont(Fonts.BODY);
            routeLabel.setForeground(Colors.TEXT_MUTED);
            mainPanel.add(routeLabel);
            mainPanel.add(Box.createVerticalStrut(20));

            // Schedule display
            ModernUIComponents.GlassCard scheduleCard = new ModernUIComponents.GlassCard();
            scheduleCard.setLayout(new BoxLayout(scheduleCard, BoxLayout.Y_AXIS));
            scheduleCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            scheduleCard.setMaximumSize(new Dimension(450, 300));

            String weeklySchedule = conductor.getWeeklySchedule();
            if (weeklySchedule == null || weeklySchedule.isEmpty()) {
                JLabel noScheduleLabel = new JLabel("Aucun horaire défini");
                noScheduleLabel.setFont(Fonts.BODY);
                noScheduleLabel.setForeground(Colors.TEXT_MUTED);
                noScheduleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                scheduleCard.add(noScheduleLabel);
            } else {
                String[] dayNames = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
                String[] dayCodes = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

                java.util.Map<String, String> scheduleMap = parseWeeklySchedule(weeklySchedule);

                for (int i = 0; i < dayCodes.length; i++) {
                    String dayCode = dayCodes[i];
                    String dayName = dayNames[i];
                    
                    JPanel dayPanel = new JPanel(new BorderLayout());
                    dayPanel.setOpaque(false);
                    dayPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

                    JLabel dayLabel = new JLabel(dayName + ":");
                    dayLabel.setFont(Fonts.BODY_BOLD);
                    dayLabel.setForeground(Colors.TEXT_DARK);
                    dayLabel.setPreferredSize(new Dimension(80, 30));

                    JLabel timeLabel = new JLabel(scheduleMap.getOrDefault(dayCode, "Fermé"));
                    timeLabel.setFont(Fonts.BODY);
                    timeLabel.setForeground(scheduleMap.containsKey(dayCode) ? 
                        new Color(39, 174, 96) : Colors.TEXT_MUTED);

                    dayPanel.add(dayLabel, BorderLayout.WEST);
                    dayPanel.add(timeLabel, BorderLayout.CENTER);

                    scheduleCard.add(dayPanel);
                }
            }

            JScrollPane scrollPane = new JScrollPane(scheduleCard);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.setBackground(Colors.SURFACE);
            scrollPane.getViewport().setBackground(Colors.SURFACE);
            ModernUIComponents.applyModernScrollBar(scrollPane);
            mainPanel.add(scrollPane);
            mainPanel.add(Box.createVerticalStrut(15));

            // Close button
            ModernUIComponents.GradientButton closeBtn = new ModernUIComponents.GradientButton(
                "Fermer", Colors.TEXT_MUTED, Colors.ACCENT_MINT);
            closeBtn.setPreferredSize(new Dimension(150, 40));
            closeBtn.setMaximumSize(new Dimension(150, 40));
            closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            closeBtn.addActionListener(e -> detailDialog.dispose());
            mainPanel.add(closeBtn);

            JScrollPane mainScroll = new JScrollPane(mainPanel);
            mainScroll.setBorder(BorderFactory.createEmptyBorder());
            detailDialog.add(mainScroll);
            detailDialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            System.err.println("Erreur lors du rechargement: " + e.getMessage());
        }
    }

    /**
     * Parses weekly schedule string into a map
     * Format: "MON:09:00-17:00|TUE:09:00-17:00|..."
     */
    private java.util.Map<String, String> parseWeeklySchedule(String scheduleString) {
        java.util.Map<String, String> scheduleMap = new java.util.HashMap<>();
        
        if (scheduleString == null || scheduleString.isEmpty()) {
            return scheduleMap;
        }

        String[] entries = scheduleString.split("\\|");
        for (String entry : entries) {
            if (entry.contains(":")) {
                String[] parts = entry.split(":", 2);
                if (parts.length == 2) {
                    String day = parts[0].trim();
                    String time = parts[1].trim();
                    scheduleMap.put(day, time);
                }
            }
        }

        return scheduleMap;
    }

    // ==================== Refresh Methods ====================

    
    public void refresh() {
        refreshDashboard();
        refreshTrajetsTable();
        refreshReservationsTable();
        updateSidebarSelection(0);
        contentLayout.show(contentPanel, "DASHBOARD");
    }

    /**
     * Refresh only the data models/tables without changing the visible card.
     */
    public void refreshModels() {
        refreshDashboard();
        refreshTrajetsTable();
        refreshReservationsTable();
    }

    private void refreshDashboard() {
        // Count available trajets
        int availableCount = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null &&
                t.getAvailablePlaces() > 0 &&
                !t.isFinished()) {
                availableCount++;
            }
        }
        trajetsDispoCard.setValue(String.valueOf(availableCount));
        
        // Count reservations for current passager
        Passager passager = mainFrame.getCurrentPassager();
        int reservationCount = 0;
        if (passager != null) {
            for (Trajet t : mainFrame.getGestion().getTrajets()) {
                for (Passager p : t.getPassagersAcceptes()) {
                    if (p.getCin().equals(passager.getCin())) {
                        reservationCount++;
                        break;
                    }
                }
            }
        }
        reservationsCard.setValue(String.valueOf(reservationCount));
        
        // Update status
        if (passager != null && passager.isChercheCovoit()) {
            statusCard.setValue("En recherche");
        } else {
            statusCard.setValue(reservationCount > 0 ? "Réservé" : "Inactif");
        }
        
        // Update notification badge
        updateNotificationBadge();
    }

    private void refreshTrajetsTable() {
        trajetsModel.setRowCount(0);

        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            Conducteur c = t.getConducteur();
            // Hide finished trajets from passenger search/list
            if (c == null || t.getAvailablePlaces() <= 0 || t.isFinished()) continue;

            trajetsModel.addRow(new Object[]{
                c.getNom() + " " + c.getPrenom(),
                t.getDepartTrajet(),
                t.getArriveeTrajet(),
                t.getDureeTrajet().toMinutes() + " min",
                String.format("%.2f", t.getPrix()),
                t.getAvailablePlaces(),
                c.getNomVoiture() + " " + c.getMarqueVoiture()
            });
        }
    }

    private void refreshReservationsTable() {
        reservationsModel.setRowCount(0);
        Passager passager = mainFrame.getCurrentPassager();
        if (passager == null) return;

        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            // First: accepted reservations
            for (Passager p : t.getPassagersAcceptes()) {
                if (p.getCin().equals(passager.getCin())) {
                    Conducteur c = t.getConducteur();
                    if (c != null) {
                        reservationsModel.addRow(new Object[]{
                            c.getNom() + " " + c.getPrenom(),
                            c.getTel(),
                            c.getNomVoiture() + " " + c.getMarqueVoiture(),
                            t.getDepartTrajet(),
                            t.getArriveeTrajet(),
                            String.format("%.2f", t.getPrix()),
                            "Accepté"
                        });
                    }
                    break;
                }
            }

            // Second: pending requests where the current passager is in demandes
            for (Passager p : t.getPassagersDemandes()) {
                if (p.getCin().equals(passager.getCin())) {
                    Conducteur c = t.getConducteur();
                    String conductorName = c != null ? (c.getNom() + " " + c.getPrenom()) : "—";
                    String phone = c != null ? c.getTel() : "—";
                    String car = c != null ? (c.getNomVoiture() + " " + c.getMarqueVoiture()) : "—";
                    reservationsModel.addRow(new Object[]{
                        conductorName,
                        phone,
                        car,
                        t.getDepartTrajet(),
                        t.getArriveeTrajet(),
                        String.format("%.2f", t.getPrix()),
                        "En attente"
                    });
                    break;
                }
            }
        }
    }
}
