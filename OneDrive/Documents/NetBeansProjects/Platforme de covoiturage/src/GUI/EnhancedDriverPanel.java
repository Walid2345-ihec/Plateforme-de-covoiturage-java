package GUI;

import Models.*;
import Services.*;
import GUI.ModernUIComponents.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.util.Vector;

/**
 * Enhanced Modern Driver Dashboard Panel
 */
public class EnhancedDriverPanel extends JPanel {
    
    private MainFrame mainFrame;
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
    
    // Sidebar buttons for selection tracking
    private java.util.List<ModernUIComponents.SidebarButton> sidebarButtons = new java.util.ArrayList<>();
    
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
        addSidebarButton(sidebar, "📊  Tableau de Bord", "DASHBOARD", true);
        addSidebarButton(sidebar, "🛣️  Mes Trajets", "TRAJETS", false);
        addSidebarButton(sidebar, "📋  Demandes Reçues", "DEMANDES", false);
        addSidebarButton(sidebar, "👥  Passagers Acceptés", "PASSAGERS", false);
        addSidebarButton(sidebar, "➕  Nouveau Trajet", "NEW_TRAJET", false);
        
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
        
        placesCard = new ModernUIComponents.StatCard("🪑", "Places Disponibles", "0", Colors.ACCENT_MINT);
        trajetsCard = new ModernUIComponents.StatCard("🛣️", "Mes Trajets", "0", Colors.PRIMARY_START);
        demandesCard = new ModernUIComponents.StatCard("📋", "Demandes", "0", Colors.ACCENT_GOLD);
        
        statsPanel.add(placesCard);
        statsPanel.add(trajetsCard);
        statsPanel.add(demandesCard);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Quick actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        ModernUIComponents.GradientButton newTrajetBtn = new ModernUIComponents.GradientButton(
            "➕ Créer un Trajet", Colors.PRIMARY_START, Colors.PRIMARY_END);
        newTrajetBtn.addActionListener(e -> {
            updateSidebarSelection(4);
            contentLayout.show(contentPanel, "NEW_TRAJET");
        });
        actionsPanel.add(newTrajetBtn);
        
        ModernUIComponents.RoundedButton viewDemandesBtn = new ModernUIComponents.RoundedButton(
            "📋 Voir les Demandes", Colors.ACCENT_SKY);
        viewDemandesBtn.addActionListener(e -> {
            updateSidebarSelection(2);
            refreshDemandesTable();
            contentLayout.show(contentPanel, "DEMANDES");
        });
        actionsPanel.add(viewDemandesBtn);
        
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTrajetsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Header
        JLabel titleLabel = new JLabel("🛣️ Mes Trajets");
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
            "💰 Modifier Prix", Colors.ACCENT_SKY);
        editBtn.setPreferredSize(new Dimension(150, 42));
        editBtn.addActionListener(e -> modifyTrajetPrice());
        
        ModernUIComponents.RoundedButton deleteBtn = new ModernUIComponents.RoundedButton(
            "🗑️ Supprimer", Colors.ACCENT_CORAL);
        deleteBtn.setPreferredSize(new Dimension(130, 42));
        deleteBtn.addActionListener(e -> deleteTrajet());
        
        ModernUIComponents.RoundedButton refreshBtn = new ModernUIComponents.RoundedButton(
            "🔄 Actualiser", Colors.TEXT_MUTED);
        refreshBtn.setPreferredSize(new Dimension(130, 42));
        refreshBtn.addActionListener(e -> refreshTrajetsTable());
        
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createDemandesView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JLabel titleLabel = new JLabel("📋 Demandes Reçues");
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
            "✅ Accepter", Colors.ACCENT_MINT, Colors.GRADIENT_TEAL_START);
        acceptBtn.setPreferredSize(new Dimension(150, 45));
        acceptBtn.addActionListener(e -> acceptPassenger());
        
        ModernUIComponents.RoundedButton refreshBtn = new ModernUIComponents.RoundedButton(
            "🔄 Actualiser", Colors.TEXT_MUTED);
        refreshBtn.setPreferredSize(new Dimension(130, 42));
        refreshBtn.addActionListener(e -> refreshDemandesTable());
        
        buttonPanel.add(acceptBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPassagersView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JLabel titleLabel = new JLabel("👥 Passagers Acceptés");
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
        
        JScrollPane scrollPane = new JScrollPane(passagersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        ModernUIComponents.applyModernScrollBar(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        ModernUIComponents.RoundedButton refreshBtn = new ModernUIComponents.RoundedButton(
            "🔄 Actualiser", Colors.TEXT_MUTED);
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
        
        JLabel titleLabel = new JLabel("➕ Créer un Nouveau Trajet");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form card
        ModernUIComponents.GlassCard formCard = new ModernUIComponents.GlassCard();
        formCard.setLayout(new GridBagLayout());
        formCard.setPreferredSize(new Dimension(500, 350));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Departure
        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(createFormLabel("📍 Point de Départ"), gbc);
        gbc.gridx = 1;
        ModernUIComponents.ModernTextField departField = new ModernUIComponents.ModernTextField("Tunis, Sousse...");
        departField.setPreferredSize(new Dimension(280, 45));
        formCard.add(departField, gbc);
        
        // Arrival
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(createFormLabel("🎯 Point d'Arrivée"), gbc);
        gbc.gridx = 1;
        ModernUIComponents.ModernTextField arriveeField = new ModernUIComponents.ModernTextField("Destination...");
        formCard.add(arriveeField, gbc);
        
        // Duration
        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(createFormLabel("⏱️ Durée (minutes)"), gbc);
        gbc.gridx = 1;
        JSpinner dureeSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 480, 5));
        dureeSpinner.setFont(Fonts.BODY);
        dureeSpinner.setPreferredSize(new Dimension(120, 40));
        formCard.add(dureeSpinner, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(createFormLabel("💰 Prix (TND)"), gbc);
        gbc.gridx = 1;
        JSpinner prixSpinner = new JSpinner(new SpinnerNumberModel(5.0, 1.0, 100.0, 0.5));
        prixSpinner.setFont(Fonts.BODY);
        prixSpinner.setPreferredSize(new Dimension(120, 40));
        formCard.add(prixSpinner, gbc);
        
        // Create button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 12, 12, 12);
        
        ModernUIComponents.GradientButton createBtn = new ModernUIComponents.GradientButton(
            "✅ Créer le Trajet", Colors.ACCENT_MINT, Colors.GRADIENT_TEAL_START);
        createBtn.setPreferredSize(new Dimension(250, 50));
        createBtn.addActionListener(e -> {
            String depart = departField.getText().trim();
            String arrivee = arriveeField.getText().trim();
            int duree = (Integer) dureeSpinner.getValue();
            double prix = (Double) prixSpinner.getValue();
            
            if (depart.isEmpty() || arrivee.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs !", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            createTrajet(depart, arrivee, duree, (float) prix);
            departField.setText("");
            arriveeField.setText("");
            dureeSpinner.setValue(30);
            prixSpinner.setValue(5.0);
        });
        formCard.add(createBtn, gbc);
        
        // Center the form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(formCard);
        panel.add(centerPanel, BorderLayout.CENTER);
        
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
    
    private void createTrajet(String depart, String arrivee, int dureeMinutes, float prix) {
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) {
            JOptionPane.showMessageDialog(this, "Erreur: Conducteur non connecté", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Duration duree = Duration.ofMinutes(dureeMinutes);
            Trajet trajet = new Trajet(depart, arrivee, duree, "PENDING", prix, conducteur, null);
            mainFrame.getGestion().getTrajets().add(trajet);
            
            JOptionPane.showMessageDialog(this, 
                "Trajet créé avec succès !\n" + depart + " → " + arrivee + "\nPrix: " + prix + " TND",
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
                Vector<Trajet> trajets = mainFrame.getGestion().getTrajets();
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
            Vector<Trajet> trajets = mainFrame.getGestion().getTrajets();
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
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un passager", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur.getPlacesDisponibles() <= 0) {
            JOptionPane.showMessageDialog(this, "Plus de places disponibles !", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String cin = (String) demandesModel.getValueAt(row, 0);
        Passager passager = mainFrame.getGestion().rechercher_passager(cin);
        
        if (passager != null) {
            conducteur.setPlacesDisponibles(conducteur.getPlacesDisponibles() - 1);
            mainFrame.getGestion().getPassagers_acceptes().add(passager);
            
            for (Trajet t : mainFrame.getGestion().getTrajets()) {
                if (t.getConducteur() != null && t.getConducteur().getCin().equals(conducteur.getCin()) && t.getPassager() == null) {
                    t.setPassager(passager);
                    break;
                }
            }
            
            JOptionPane.showMessageDialog(this, 
                "Passager " + passager.getPrenom() + " accepté !\nPlaces restantes: " + conducteur.getPlacesDisponibles(),
                "Succès ✓", JOptionPane.INFORMATION_MESSAGE);
            
            refreshDemandesTable();
            refreshDashboard();
        }
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
        demandesCard.setValue("0");
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
                    t.getPassager() != null ? t.getPassager().getNom() : "En attente"
                });
            }
        }
    }
    
    private void refreshDemandesTable() {
        demandesModel.setRowCount(0);
        for (User u : mainFrame.getGestion().getUsers()) {
            if (u instanceof Passager) {
                Passager p = (Passager) u;
                if (p.isChercheCovoit()) {
                    demandesModel.addRow(new Object[]{
                        p.getCin(), p.getNom(), p.getPrenom(), p.getTel(), p.getMail()
                    });
                }
            }
        }
    }
    
    private void refreshPassagersTable() {
        passagersModel.setRowCount(0);
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;
        
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && t.getConducteur().getCin().equals(conducteur.getCin()) && t.getPassager() != null) {
                Passager p = t.getPassager();
                passagersModel.addRow(new Object[]{
                    p.getCin(), p.getNom(), p.getPrenom(), p.getTel(), p.getMail(), p.getAdresse()
                });
            }
        }
    }
}
