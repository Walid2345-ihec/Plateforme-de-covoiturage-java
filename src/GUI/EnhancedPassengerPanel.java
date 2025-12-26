package GUI;

import GUI.ModernUIComponents.Colors;
import GUI.ModernUIComponents.Fonts;
import Models.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Panneau passager moderne amélioré
 * Fournit un tableau de bord moderne pour le rôle passager avec recherche et gestion des réservations
 */
public class EnhancedPassengerPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JPanel contentPanel;
    private CardLayout contentLayout;
    
    // Cartes de statistiques
    private ModernUIComponents.StatCard reservationsCard;
    private ModernUIComponents.StatCard trajetsDispoCard;
    private ModernUIComponents.StatCard statusCard;
    
    // Tables
    private JTable trajetsTable;
    private DefaultTableModel trajetsModel;
    private JTable mesReservationsTable;
    private DefaultTableModel reservationsModel;
    
    // Boutons de la barre latérale
    private java.util.List<ModernUIComponents.SidebarButton> sidebarButtons = new java.util.ArrayList<>();
    
    public EnhancedPassengerPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Colors.SURFACE);
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Barre latérale
        add(createSidebar(), BorderLayout.WEST);
        
        // Zone de contenu
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
                
                // Dégradé vert pour l'espace passager
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
        
        // En-tête avec infos utilisateur
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
        
        // Éléments du menu
        addSidebarButton(sidebar, "Tableau de Bord", "DASHBOARD", true);
        addSidebarButton(sidebar, "Rechercher Trajets", "SEARCH", false);
        addSidebarButton(sidebar, "Mes Réservations", "RESERVATIONS", false);
        
        sidebar.add(Box.createVerticalGlue());
        
        // Bouton de déconexion
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
        
        // En-tête
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
        
        // Cartes de statistiques
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
        
        // Actions rapides
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
        
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSearchTrajetsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // En-tête avec titre
        JLabel titleLabel = new JLabel("Rechercher des Trajets");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Contenu principal avec formulaire de recherche et table
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);
        
        // Carte des filtres de recherche
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
        
        // Boutons d'action
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
        
        buttonPanel.add(reserveBtn);
        buttonPanel.add(refreshBtn);
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
        
        // Panneau d'information en bas
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
        
        // Style de l'entête
        JTableHeader header = table.getTableHeader();
        header.setFont(Fonts.BODY_BOLD);
        header.setBackground(Colors.SURFACE);
        header.setForeground(Colors.TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Colors.BORDER));
        
        // Alternance de couleur des lignes avec une teinte verte
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
    
    // ==================== Logique Métier ====================

    private void filterTrajets(String depart, String arrivee) {
        trajetsModel.setRowCount(0);

        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            // Montrer le trajet uniquement s'il a un conducteur et des places disponibles
            Conducteur c = t.getConducteur();
            // Exclure les trajets terminés car ils ne doivent plus être réservables
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
            // Trouver le trajet et ajouter une demande (ne pas décrémenter les places encore)
            for (Trajet t : mainFrame.getGestion().getTrajets()) {
                Conducteur c = t.getConducteur();
                if (c == null) continue;

                String fullName = c.getNom() + " " + c.getPrenom();
                if (fullName.equals(conducteurName) && 
                    t.getDepartTrajet().equals(depart) && 
                    t.getArriveeTrajet().equals(arrivee)) {

                    // Ignorer les trajets terminés - ne doivent pas être réservés
                    if (t.isFinished()) {
                        JOptionPane.showMessageDialog(this, "Ce trajet est terminé et n'est plus disponible.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                     boolean added = mainFrame.getGestion().ajouter_demande_pour_trajet(t, passager.getCin());
                    if (added) {
                        // Mise à jour mapping conducteur->demandes pour notifications
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

                    // Rafraîchir les vues pertinentes
                    refreshTrajetsTable();
                    refreshDashboard();
                    refreshReservationsTable();

                    // Notifier le main frame pour mise à jour globale
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

    // ==================== Méthodes de rafraîchissement ====================

    public void refresh() {
        refreshDashboard();
        refreshTrajetsTable();
        refreshReservationsTable();
        updateSidebarSelection(0);
        contentLayout.show(contentPanel, "DASHBOARD");
    }

    /**
     * Rafraîchit uniquement les modèles de données / tables sans changer la carte visible.
     */
    public void refreshModels() {
        refreshDashboard();
        refreshTrajetsTable();
        refreshReservationsTable();
    }

    private void refreshDashboard() {
        // Compte des trajets disponibles
        int availableCount = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null &&
                t.getAvailablePlaces() > 0 &&
                !t.isFinished()) {
                availableCount++;
            }
        }
        trajetsDispoCard.setValue(String.valueOf(availableCount));
        
        // Compte des réservations pour le passager courant
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
        
        // Met à jour le statut
        if (passager != null && passager.isChercheCovoit()) {
            statusCard.setValue("En recherche");
        } else {
            statusCard.setValue(reservationCount > 0 ? "Réservé" : "Inactif");
        }
    }

    private void refreshTrajetsTable() {
        trajetsModel.setRowCount(0);

        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            Conducteur c = t.getConducteur();
            // Masquer les trajets terminés de la recherche passager
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
            // D'abord : réservations acceptées
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

            // Ensuite : demandes en attente où le passager courant est dans les demandes
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
