package GUI;

import Models.*;
import Services.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.util.Vector;

/**
 * Driver Dashboard Panel
 */
public class DriverPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JPanel contentPanel;
    private CardLayout contentLayout;
    
    // Tables
    private JTable trajetsTable;
    private DefaultTableModel trajetsTableModel;
    private JTable demandesTable;
    private DefaultTableModel demandesTableModel;
    private JTable passagersAcceptesTable;
    private DefaultTableModel passagersAcceptesTableModel;
    
    // Labels for stats
    private JLabel placesLabel;
    private JLabel trajetsCountLabel;
    private JLabel demandesCountLabel;
    
    public DriverPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(StyleUtils.BACKGROUND_COLOR);
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Sidebar
        add(createSidebarPanel(), BorderLayout.WEST);
        
        // Content area with card layout
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(StyleUtils.BACKGROUND_COLOR);
        
        contentPanel.add(createDashboardView(), "DASHBOARD");
        contentPanel.add(createTrajetsView(), "TRAJETS");
        contentPanel.add(createDemandesView(), "DEMANDES");
        contentPanel.add(createPassagersView(), "PASSAGERS");
        contentPanel.add(createNewTrajetView(), "NEW_TRAJET");
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleUtils.PRIMARY_COLOR);
        panel.setPreferredSize(new Dimension(0, 70));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Left - Title
        JLabel titleLabel = new JLabel("🚗 Espace Conducteur");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        // Right - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Bienvenue !");
        userLabel.setFont(StyleUtils.REGULAR_FONT);
        userLabel.setForeground(Color.WHITE);
        rightPanel.add(userLabel);
        
        rightPanel.add(Box.createHorizontalStrut(20));
        
        JButton logoutBtn = new JButton("Déconnexion");
        logoutBtn.setFont(StyleUtils.REGULAR_FONT);
        logoutBtn.setForeground(StyleUtils.PRIMARY_COLOR);
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            if (StyleUtils.showConfirm(this, "Voulez-vous vraiment vous déconnecter ?")) {
                mainFrame.showLogin();
            }
        });
        rightPanel.add(logoutBtn);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(44, 62, 80));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Menu items
        addMenuItem(panel, "📊 Tableau de Bord", "DASHBOARD");
        addMenuItem(panel, "🛣️ Mes Trajets", "TRAJETS");
        addMenuItem(panel, "📋 Demandes Reçues", "DEMANDES");
        addMenuItem(panel, "👥 Passagers Acceptés", "PASSAGERS");
        addMenuItem(panel, "➕ Nouveau Trajet", "NEW_TRAJET");
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void addMenuItem(JPanel panel, String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(StyleUtils.REGULAR_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(44, 62, 80));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(220, 50));
        button.setPreferredSize(new Dimension(220, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(52, 73, 94));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(44, 62, 80));
            }
        });
        
        button.addActionListener(e -> {
            contentLayout.show(contentPanel, cardName);
            if (cardName.equals("TRAJETS")) refreshTrajetsTable();
            if (cardName.equals("DEMANDES")) refreshDemandesTable();
            if (cardName.equals("PASSAGERS")) refreshPassagersAcceptesTable();
            if (cardName.equals("DASHBOARD")) refreshDashboard();
        });
        
        panel.add(button);
    }
    
    private JPanel createDashboardView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = StyleUtils.createTitleLabel("Tableau de Bord");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        // Places card
        JPanel placesCard = createStatCard("🪑", "Places Disponibles", "0", StyleUtils.ACCENT_COLOR);
        placesLabel = (JLabel) ((JPanel) placesCard.getComponent(0)).getComponent(1);
        statsPanel.add(placesCard);
        
        // Trajets card
        JPanel trajetsCard = createStatCard("🛣️", "Mes Trajets", "0", StyleUtils.PRIMARY_COLOR);
        trajetsCountLabel = (JLabel) ((JPanel) trajetsCard.getComponent(0)).getComponent(1);
        statsPanel.add(trajetsCard);
        
        // Demandes card
        JPanel demandesCard = createStatCard("📋", "Demandes Reçues", "0", StyleUtils.WARNING_COLOR);
        demandesCountLabel = (JLabel) ((JPanel) demandesCard.getComponent(0)).getComponent(1);
        statsPanel.add(demandesCard);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Quick actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionsPanel.setOpaque(false);
        
        JButton newTrajetBtn = StyleUtils.createPrimaryButton("➕ Créer un Trajet");
        newTrajetBtn.addActionListener(e -> contentLayout.show(contentPanel, "NEW_TRAJET"));
        actionsPanel.add(newTrajetBtn);
        
        JButton viewDemandesBtn = StyleUtils.createSecondaryButton("📋 Voir les Demandes");
        viewDemandesBtn.addActionListener(e -> {
            refreshDemandesTable();
            contentLayout.show(contentPanel, "DEMANDES");
        });
        actionsPanel.add(viewDemandesBtn);
        
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatCard(String icon, String title, String value, Color color) {
        JPanel card = StyleUtils.createCardPanel();
        card.setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        JLabel iconLabel = new JLabel(icon + " " + title);
        iconLabel.setFont(StyleUtils.REGULAR_FONT);
        iconLabel.setForeground(StyleUtils.TEXT_SECONDARY);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createVerticalGlue());
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createTrajetsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = StyleUtils.createTitleLabel("Mes Trajets");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Départ", "Arrivée", "Durée", "Prix (TND)", "Statut", "Passager"};
        trajetsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        trajetsTable = StyleUtils.createStyledTable(new Object[0][0], columns);
        trajetsTable.setModel(trajetsTableModel);
        
        JScrollPane scrollPane = StyleUtils.createStyledScrollPane(trajetsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton editPriceBtn = StyleUtils.createSecondaryButton("💰 Modifier Prix");
        editPriceBtn.addActionListener(e -> modifyTrajetPrice());
        buttonPanel.add(editPriceBtn);
        
        JButton deleteBtn = StyleUtils.createDangerButton("🗑️ Supprimer");
        deleteBtn.addActionListener(e -> deleteTrajet());
        buttonPanel.add(deleteBtn);
        
        JButton refreshBtn = StyleUtils.createSecondaryButton("🔄 Actualiser");
        refreshBtn.addActionListener(e -> refreshTrajetsTable());
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createDemandesView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = StyleUtils.createTitleLabel("Demandes Reçues");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"CIN", "Nom", "Prénom", "Téléphone", "Email"};
        demandesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        demandesTable = StyleUtils.createStyledTable(new Object[0][0], columns);
        demandesTable.setModel(demandesTableModel);
        
        JScrollPane scrollPane = StyleUtils.createStyledScrollPane(demandesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton acceptBtn = StyleUtils.createSuccessButton("✅ Accepter");
        acceptBtn.addActionListener(e -> acceptPassenger());
        buttonPanel.add(acceptBtn);
        
        JButton refreshBtn = StyleUtils.createSecondaryButton("🔄 Actualiser");
        refreshBtn.addActionListener(e -> refreshDemandesTable());
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPassagersView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = StyleUtils.createTitleLabel("Passagers Acceptés");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"CIN", "Nom", "Prénom", "Téléphone", "Email", "Adresse"};
        passagersAcceptesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        passagersAcceptesTable = StyleUtils.createStyledTable(new Object[0][0], columns);
        passagersAcceptesTable.setModel(passagersAcceptesTableModel);
        
        JScrollPane scrollPane = StyleUtils.createStyledScrollPane(passagersAcceptesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        JButton refreshBtn = StyleUtils.createSecondaryButton("🔄 Actualiser");
        refreshBtn.addActionListener(e -> refreshPassagersAcceptesTable());
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createNewTrajetView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = StyleUtils.createTitleLabel("Créer un Nouveau Trajet");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form
        JPanel formCard = StyleUtils.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Departure
        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(StyleUtils.createLabel("Point de Départ :"), gbc);
        gbc.gridx = 1;
        JTextField departField = StyleUtils.createStyledTextField();
        departField.setPreferredSize(new Dimension(300, 40));
        formCard.add(departField, gbc);
        
        // Arrival
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(StyleUtils.createLabel("Point d'Arrivée :"), gbc);
        gbc.gridx = 1;
        JTextField arriveeField = StyleUtils.createStyledTextField();
        formCard.add(arriveeField, gbc);
        
        // Duration
        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(StyleUtils.createLabel("Durée (minutes) :"), gbc);
        gbc.gridx = 1;
        JSpinner dureeSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 480, 5));
        dureeSpinner.setFont(StyleUtils.REGULAR_FONT);
        dureeSpinner.setPreferredSize(new Dimension(100, 40));
        formCard.add(dureeSpinner, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(StyleUtils.createLabel("Prix par personne (TND) :"), gbc);
        gbc.gridx = 1;
        JSpinner prixSpinner = new JSpinner(new SpinnerNumberModel(5.0, 1.0, 100.0, 0.5));
        prixSpinner.setFont(StyleUtils.REGULAR_FONT);
        prixSpinner.setPreferredSize(new Dimension(100, 40));
        formCard.add(prixSpinner, gbc);
        
        // Create button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        JButton createBtn = StyleUtils.createSuccessButton("✅ Créer le Trajet");
        createBtn.addActionListener(e -> {
            String depart = departField.getText().trim();
            String arrivee = arriveeField.getText().trim();
            int duree = (Integer) dureeSpinner.getValue();
            double prix = (Double) prixSpinner.getValue();
            
            if (depart.isEmpty() || arrivee.isEmpty()) {
                StyleUtils.showError(this, "Veuillez remplir tous les champs !");
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
    
    // ==================== Business Logic Methods ====================
    
    private void createTrajet(String depart, String arrivee, int dureeMinutes, float prix) {
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) {
            StyleUtils.showError(this, "Erreur: Conducteur non connecté");
            return;
        }
        
        try {
            Duration duree = Duration.ofMinutes(dureeMinutes);
            Trajet trajet = new Trajet(depart, arrivee, duree, "PENDING", prix, conducteur, null);
            mainFrame.getGestion().getTrajets().add(trajet);
            
            StyleUtils.showSuccess(this, "Trajet créé avec succès !\n" + 
                depart + " → " + arrivee + "\nPrix: " + prix + " TND");
            
            refreshDashboard();
            contentLayout.show(contentPanel, "TRAJETS");
            refreshTrajetsTable();
            
        } catch (Exception e) {
            StyleUtils.showError(this, "Erreur lors de la création: " + e.getMessage());
        }
    }
    
    private void modifyTrajetPrice() {
        int selectedRow = trajetsTable.getSelectedRow();
        if (selectedRow == -1) {
            StyleUtils.showError(this, "Veuillez sélectionner un trajet");
            return;
        }
        
        String newPriceStr = JOptionPane.showInputDialog(this, 
            "Nouveau prix (TND):", "Modifier le Prix", JOptionPane.PLAIN_MESSAGE);
        
        if (newPriceStr != null && !newPriceStr.isEmpty()) {
            try {
                float newPrice = Float.parseFloat(newPriceStr);
                
                // Find the trajet
                Conducteur conducteur = mainFrame.getCurrentConducteur();
                Vector<Trajet> trajets = mainFrame.getGestion().getTrajets();
                int count = 0;
                for (Trajet t : trajets) {
                    if (t.getConducteur() != null && 
                        t.getConducteur().getCin().equals(conducteur.getCin())) {
                        if (count == selectedRow) {
                            t.setPrix(newPrice);
                            StyleUtils.showSuccess(this, "Prix modifié avec succès !");
                            refreshTrajetsTable();
                            return;
                        }
                        count++;
                    }
                }
            } catch (NumberFormatException e) {
                StyleUtils.showError(this, "Prix invalide !");
            }
        }
    }
    
    private void deleteTrajet() {
        int selectedRow = trajetsTable.getSelectedRow();
        if (selectedRow == -1) {
            StyleUtils.showError(this, "Veuillez sélectionner un trajet");
            return;
        }
        
        if (StyleUtils.showConfirm(this, "Voulez-vous vraiment supprimer ce trajet ?")) {
            Conducteur conducteur = mainFrame.getCurrentConducteur();
            Vector<Trajet> trajets = mainFrame.getGestion().getTrajets();
            int count = 0;
            for (int i = 0; i < trajets.size(); i++) {
                Trajet t = trajets.get(i);
                if (t.getConducteur() != null && 
                    t.getConducteur().getCin().equals(conducteur.getCin())) {
                    if (count == selectedRow) {
                        trajets.remove(i);
                        StyleUtils.showSuccess(this, "Trajet supprimé !");
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
        int selectedRow = demandesTable.getSelectedRow();
        if (selectedRow == -1) {
            StyleUtils.showError(this, "Veuillez sélectionner une demande");
            return;
        }

        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;

        if (conducteur.getPlacesDisponibles() <= 0) {
            StyleUtils.showError(this, "Vous n'avez plus de places disponibles !");
            return;
        }

        // Find the corresponding trajet and the corresponding pending passenger
        int count = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null &&
                t.getConducteur().getCin().equals(conducteur.getCin())) {

                // iterate demandes for this trajet
                for (Passager p : t.getPassagersDemandes()) {
                    if (count == selectedRow) {
                        Passager passager = p;

                        if (StyleUtils.showConfirm(this,
                            "Accepter la demande de " + passager.getPrenom() + " " + passager.getNom() + " ?\n\n" +
                            "Trajet: " + t.getDepartTrajet() + " → " + t.getArriveeTrajet() + "\n" +
                            "Prix: " + String.format("%.2f", t.getPrix()) + " TND")) {

                            boolean accepted = mainFrame.getGestion().accepter_passager_pour_trajet(t, passager.getCin());
                            if (accepted) {
                                StyleUtils.showSuccess(this,
                                    "✅ Passager accepté !\n\n" +
                                    passager.getPrenom() + " " + passager.getNom() + "\n" +
                                    "Téléphone: " + passager.getTel() + "\n\n" +
                                    "Places restantes: " + conducteur.getPlacesDisponibles());

                                refreshDemandesTable();
                                refreshPassagersAcceptesTable();
                                refreshDashboard();

                                // Notify main frame to refresh other panels (passenger view)
                                if (mainFrame != null) {
                                    mainFrame.notifyDataChanged();
                                }
                            } else {
                                StyleUtils.showError(this, "Impossible d'accepter le passager (place peut-être déjà prise).");
                            }
                        }
                        return;
                    }
                    count++;
                }
            }
        }

        StyleUtils.showError(this, "Erreur: Demande non trouvée");
    }
    
    // ==================== Refresh Methods ====================
    
    public void refresh() {
        refreshDashboard();
        refreshTrajetsTable();
        refreshDemandesTable();
        refreshPassagersAcceptesTable();
        contentLayout.show(contentPanel, "DASHBOARD");
    }

    /**
     * Refresh only the data models/tables without changing the visible card.
     * Used when external events (other users) modify data and we want to update UI in-place.
     */
    public void refreshModels() {
        refreshDashboard();
        refreshTrajetsTable();
        refreshDemandesTable();
        refreshPassagersAcceptesTable();
    }

    private void refreshDashboard() {
        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;
        
        // Update places
        if (placesLabel != null) {
            placesLabel.setText(String.valueOf(conducteur.getPlacesDisponibles()));
        }
        
        // Count trajets
        int trajetCount = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && 
                t.getConducteur().getCin().equals(conducteur.getCin())) {
                trajetCount++;
            }
        }
        if (trajetsCountLabel != null) {
            trajetsCountLabel.setText(String.valueOf(trajetCount));
        }
        
        // Count pending demandes (PENDING_APPROVAL status)
        int demandesCount = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && 
                t.getConducteur().getCin().equals(conducteur.getCin()) &&
                t.isPendingApproval() &&
                !t.getPassagersDemandes().isEmpty()) {
                // count all pending requests for this trajet
                demandesCount += t.getPassagersDemandes().size();
            }
        }
        if (demandesCountLabel != null) {
            demandesCountLabel.setText(String.valueOf(demandesCount));
        }
    }
    
    private void refreshTrajetsTable() {
        trajetsTableModel.setRowCount(0);

        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;

        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && 
                t.getConducteur().getCin().equals(conducteur.getCin())) {
                String passagerInfo = "En attente";
                if (!t.getPassagersAcceptes().isEmpty()) passagerInfo = t.getPassagersAcceptes().size() + " accepté(s)";
                trajetsTableModel.addRow(new Object[]{
                    t.getDepartTrajet(),
                    t.getArriveeTrajet(),
                    t.getDureeTrajet().toMinutes() + " min",
                    String.format("%.2f", t.getPrix()),
                    t.getStatusTrajet(),
                    passagerInfo
                });
            }
        }
    }

    private void refreshDemandesTable() {
        demandesTableModel.setRowCount(0);

        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;

        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null &&
                t.getConducteur().getCin().equals(conducteur.getCin())) {

                for (Passager p : t.getPassagersDemandes()) {
                    // PRIVACY: Mask sensitive data until accepted
                    String maskedCin = p.getCin().length() > 3 ? ("*****" + p.getCin().substring(p.getCin().length() - 3)) : p.getCin();
                    String maskedPhone = p.getTel().length() > 4 ? ("****" + p.getTel().substring(p.getTel().length() - 4)) : p.getTel();
                    String maskedEmail = maskEmail(p.getMail());

                    demandesTableModel.addRow(new Object[]{
                        maskedCin,
                        p.getNom(),
                        p.getPrenom(),
                        maskedPhone,
                        maskedEmail
                    });
                }
            }
        }
    }

    private void refreshPassagersAcceptesTable() {
        passagersAcceptesTableModel.setRowCount(0);

        Conducteur conducteur = mainFrame.getCurrentConducteur();
        if (conducteur == null) return;

        // Find passengers assigned to this conductor's trajets with IN_PROGRESS or FINISHED status
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && 
                t.getConducteur().getCin().equals(conducteur.getCin()) &&
                (t.isInProgress() || t.isFinished())) {

                for (Passager p : t.getPassagersAcceptes()) {
                    // PRIVACY: Mask CIN even for accepted passengers
                    String maskedCin = p.getCin().length() > 3 ? ("*****" + p.getCin().substring(p.getCin().length() - 3)) : p.getCin();
                    passagersAcceptesTableModel.addRow(new Object[]{
                        maskedCin,
                        p.getNom(),
                        p.getPrenom(),
                        p.getTel(),  // Full phone for accepted passengers (they need to contact)
                        p.getMail(), // Full email for accepted passengers
                        p.getAdresse()
                    });
                }
            }
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***@***";
        int atIndex = email.indexOf("@");
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        if (local.length() <= 2) return local + "***" + domain;
        return local.substring(0, 2) + "***" + domain;
    }
}
