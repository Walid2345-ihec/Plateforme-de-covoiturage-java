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
 * Passenger Dashboard Panel
 */
public class PassengerPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JPanel contentPanel;
    private CardLayout contentLayout;
    
    // Tables
    private JTable trajetsDisponiblesTable;
    private DefaultTableModel trajetsDisponiblesModel;
    private JTable mesReservationsTable;
    private DefaultTableModel mesReservationsModel;
    
    // Stats labels
    private JLabel trajetsCountLabel;
    private JLabel reservationsCountLabel;
    
    public PassengerPanel(MainFrame mainFrame) {
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
        contentPanel.add(createTrajetsDisponiblesView(), "TRAJETS");
        contentPanel.add(createMesReservationsView(), "RESERVATIONS");
        contentPanel.add(createRechercheTrajetView(), "RECHERCHE");
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleUtils.ACCENT_COLOR);
        panel.setPreferredSize(new Dimension(0, 70));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Left - Title
        JLabel titleLabel = new JLabel("👤 Espace Passager");
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
        logoutBtn.setForeground(StyleUtils.ACCENT_COLOR);
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
        panel.setBackground(new Color(39, 174, 96));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Menu items
        addMenuItem(panel, "📊 Tableau de Bord", "DASHBOARD");
        addMenuItem(panel, "🚗 Trajets Disponibles", "TRAJETS");
        addMenuItem(panel, "📋 Mes Réservations", "RESERVATIONS");
        addMenuItem(panel, "🔍 Rechercher un Trajet", "RECHERCHE");
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void addMenuItem(JPanel panel, String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(StyleUtils.REGULAR_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(39, 174, 96));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(220, 50));
        button.setPreferredSize(new Dimension(220, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(46, 204, 113));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(39, 174, 96));
            }
        });
        
        button.addActionListener(e -> {
            contentLayout.show(contentPanel, cardName);
            if (cardName.equals("TRAJETS")) refreshTrajetsDisponibles();
            if (cardName.equals("RESERVATIONS")) refreshMesReservations();
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
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Trajets disponibles card
        JPanel trajetsCard = createStatCard("🚗", "Trajets Disponibles", "0", StyleUtils.PRIMARY_COLOR);
        trajetsCountLabel = (JLabel) ((JPanel) trajetsCard.getComponent(0)).getComponent(1);
        statsPanel.add(trajetsCard);
        
        // Mes réservations card
        JPanel reservationsCard = createStatCard("📋", "Mes Réservations", "0", StyleUtils.ACCENT_COLOR);
        reservationsCountLabel = (JLabel) ((JPanel) reservationsCard.getComponent(0)).getComponent(1);
        statsPanel.add(reservationsCard);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Quick actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionsPanel.setOpaque(false);
        
        JButton viewTrajetsBtn = StyleUtils.createPrimaryButton("🚗 Voir les Trajets");
        viewTrajetsBtn.addActionListener(e -> {
            refreshTrajetsDisponibles();
            contentLayout.show(contentPanel, "TRAJETS");
        });
        actionsPanel.add(viewTrajetsBtn);
        
        JButton searchBtn = StyleUtils.createSecondaryButton("🔍 Rechercher");
        searchBtn.addActionListener(e -> contentLayout.show(contentPanel, "RECHERCHE"));
        actionsPanel.add(searchBtn);
        
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
    
    private JPanel createTrajetsDisponiblesView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = StyleUtils.createTitleLabel("Trajets Disponibles");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Conducteur", "Départ", "Arrivée", "Durée", "Prix (TND)", "Places"};
        trajetsDisponiblesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        trajetsDisponiblesTable = StyleUtils.createStyledTable(new Object[0][0], columns);
        trajetsDisponiblesTable.setModel(trajetsDisponiblesModel);
        
        JScrollPane scrollPane = StyleUtils.createStyledScrollPane(trajetsDisponiblesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton reserverBtn = StyleUtils.createSuccessButton("✅ Réserver ce Trajet");
        reserverBtn.addActionListener(e -> reserverTrajet());
        buttonPanel.add(reserverBtn);
        
        JButton notifierBtn = StyleUtils.createPrimaryButton("📧 Contacter Conducteur");
        notifierBtn.addActionListener(e -> notifierConducteur());
        buttonPanel.add(notifierBtn);
        
        JButton refreshBtn = StyleUtils.createSecondaryButton("🔄 Actualiser");
        refreshBtn.addActionListener(e -> refreshTrajetsDisponibles());
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createMesReservationsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = StyleUtils.createTitleLabel("Mes Réservations");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Conducteur", "Téléphone", "Départ", "Arrivée", "Prix (TND)", "Statut"};
        mesReservationsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        mesReservationsTable = StyleUtils.createStyledTable(new Object[0][0], columns);
        mesReservationsTable.setModel(mesReservationsModel);
        
        JScrollPane scrollPane = StyleUtils.createStyledScrollPane(mesReservationsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton refreshBtn = StyleUtils.createSecondaryButton("🔄 Actualiser");
        refreshBtn.addActionListener(e -> refreshMesReservations());
        buttonPanel.add(refreshBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRechercheTrajetView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(StyleUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = StyleUtils.createTitleLabel("Rechercher un Trajet");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Search form
        JPanel formCard = StyleUtils.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
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
        
        // Max Price
        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(StyleUtils.createLabel("Prix Maximum (TND) :"), gbc);
        gbc.gridx = 1;
        JSpinner maxPriceSpinner = new JSpinner(new SpinnerNumberModel(50.0, 0.0, 200.0, 5.0));
        maxPriceSpinner.setFont(StyleUtils.REGULAR_FONT);
        maxPriceSpinner.setPreferredSize(new Dimension(100, 40));
        formCard.add(maxPriceSpinner, gbc);
        
        // Search results table
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 10, 15);
        JLabel resultsLabel = StyleUtils.createHeaderLabel("Résultats de recherche:");
        formCard.add(resultsLabel, gbc);
        
        String[] columns = {"Conducteur", "Départ", "Arrivée", "Prix (TND)", "Places"};
        DefaultTableModel searchResultsModel = new DefaultTableModel(columns, 0);
        JTable searchResultsTable = StyleUtils.createStyledTable(new Object[0][0], columns);
        searchResultsTable.setModel(searchResultsModel);
        
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JScrollPane resultsScroll = StyleUtils.createStyledScrollPane(searchResultsTable);
        resultsScroll.setPreferredSize(new Dimension(500, 200));
        formCard.add(resultsScroll, gbc);
        
        // Search button
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        gbc.insets = new Insets(20, 15, 15, 15);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        
        JButton searchBtn = StyleUtils.createPrimaryButton("🔍 Rechercher");
        searchBtn.addActionListener(e -> {
            String depart = departField.getText().trim().toLowerCase();
            String arrivee = arriveeField.getText().trim().toLowerCase();
            double maxPrice = (Double) maxPriceSpinner.getValue();
            
            searchResultsModel.setRowCount(0);
            
            for (Trajet t : mainFrame.getGestion().getTrajets()) {
                if (t.getConducteur() != null && 
                    t.getStatusTrajet().equals("PENDING") &&
                    t.getConducteur().getPlacesDisponibles() > 0 &&
                    t.getPrix() <= maxPrice) {
                    
                    boolean matchDepart = depart.isEmpty() || 
                        t.getDepartTrajet().toLowerCase().contains(depart);
                    boolean matchArrivee = arrivee.isEmpty() || 
                        t.getArriveeTrajet().toLowerCase().contains(arrivee);
                    
                    if (matchDepart && matchArrivee) {
                        Conducteur c = t.getConducteur();
                        searchResultsModel.addRow(new Object[]{
                            c.getNom() + " " + c.getPrenom(),
                            t.getDepartTrajet(),
                            t.getArriveeTrajet(),
                            String.format("%.2f", t.getPrix()),
                            c.getPlacesDisponibles()
                        });
                    }
                }
            }
            
            if (searchResultsModel.getRowCount() == 0) {
                StyleUtils.showWarning(this, "Aucun trajet trouvé pour ces critères");
            }
        });
        buttonPanel.add(searchBtn);
        
        JButton clearBtn = StyleUtils.createSecondaryButton("🗑️ Effacer");
        clearBtn.addActionListener(e -> {
            departField.setText("");
            arriveeField.setText("");
            maxPriceSpinner.setValue(50.0);
            searchResultsModel.setRowCount(0);
        });
        buttonPanel.add(clearBtn);
        
        formCard.add(buttonPanel, gbc);
        
        // Center the form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(formCard);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ==================== Business Logic Methods ====================
    
    private void reserverTrajet() {
        int selectedRow = trajetsDisponiblesTable.getSelectedRow();
        if (selectedRow == -1) {
            StyleUtils.showError(this, "Veuillez sélectionner un trajet");
            return;
        }
        
        Passager passager = mainFrame.getCurrentPassager();
        if (passager == null) {
            StyleUtils.showError(this, "Erreur: Passager non connecté");
            return;
        }
        
        // Find the selected trajet
        int count = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && 
                t.getStatusTrajet().equals("PENDING") &&
                t.getConducteur().getPlacesDisponibles() > 0) {
                
                if (count == selectedRow) {
                    if (StyleUtils.showConfirm(this, 
                        "Réserver le trajet:\n" + 
                        t.getDepartTrajet() + " → " + t.getArriveeTrajet() + "\n" +
                        "Prix: " + t.getPrix() + " TND\n\n" +
                        "Confirmer la réservation ?")) {
                        
                        // Mark trajet as in progress and assign passenger
                        t.setTrajet_valide(true);
                        t.setStatusTrajet("IN_PROGRESS");
                        t.setPassager(passager);
                        
                        // Decrease driver's available seats
                        t.getConducteur().setPlacesDisponibles(
                            t.getConducteur().getPlacesDisponibles() - 1);
                        
                        StyleUtils.showSuccess(this, 
                            "Réservation confirmée !\n\n" +
                            "Conducteur: " + t.getConducteur().getNom() + " " + 
                            t.getConducteur().getPrenom() + "\n" +
                            "Téléphone: " + t.getConducteur().getTel());
                        
                        refreshTrajetsDisponibles();
                        refreshMesReservations();
                        refreshDashboard();
                    }
                    return;
                }
                count++;
            }
        }
    }
    
    private void notifierConducteur() {
        int selectedRow = trajetsDisponiblesTable.getSelectedRow();
        if (selectedRow == -1) {
            StyleUtils.showError(this, "Veuillez sélectionner un trajet");
            return;
        }
        
        // Find the selected trajet
        int count = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && 
                t.getStatusTrajet().equals("PENDING") &&
                t.getConducteur().getPlacesDisponibles() > 0) {
                
                if (count == selectedRow) {
                    Conducteur c = t.getConducteur();
                    
                    // PRIVACY: Show limited info until reservation is confirmed
                    // Mask phone and email partially
                    String maskedPhone = "****" + c.getTel().substring(Math.max(0, c.getTel().length() - 4));
                    String maskedEmail = maskEmailForDisplay(c.getMail());
                    String maskedMatricule = "***" + c.getMatricule().substring(Math.max(0, c.getMatricule().length() - 4));
                    
                    // Show contact info with privacy protection
                    JOptionPane.showMessageDialog(this,
                        "📞 Informations du Conducteur\n\n" +
                        "Nom: " + c.getNom() + " " + c.getPrenom().charAt(0) + ".\n" +
                        "Téléphone: " + maskedPhone + "\n" +
                        "Email: " + maskedEmail + "\n" +
                        "Véhicule: " + c.getMarqueVoiture() + " " + c.getNomVoiture() + "\n" +
                        "Matricule: " + maskedMatricule + "\n\n" +
                        "💡 Les coordonnées complètes seront\n" +
                        "disponibles après confirmation de réservation.",
                        "Contact Conducteur",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                count++;
            }
        }
    }
    
    // Privacy helper method
    private String maskEmailForDisplay(String email) {
        if (email == null || !email.contains("@")) return "***@***";
        int atIndex = email.indexOf("@");
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        if (local.length() <= 2) return local + "***" + domain;
        return local.substring(0, 2) + "***" + domain;
    }
    
    // ==================== Refresh Methods ====================
    
    public void refresh() {
        refreshDashboard();
        refreshTrajetsDisponibles();
        refreshMesReservations();
        contentLayout.show(contentPanel, "DASHBOARD");
    }
    
    private void refreshDashboard() {
        // Count available trajets
        int trajetsCount = 0;
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && 
                t.getStatusTrajet().equals("PENDING") &&
                t.getConducteur().getPlacesDisponibles() > 0) {
                trajetsCount++;
            }
        }
        if (trajetsCountLabel != null) {
            trajetsCountLabel.setText(String.valueOf(trajetsCount));
        }
        
        // Count my reservations
        Passager passager = mainFrame.getCurrentPassager();
        int reservationsCount = 0;
        if (passager != null) {
            for (Trajet t : mainFrame.getGestion().getTrajets()) {
                if (t.getPassager() != null && 
                    t.getPassager().getCin().equals(passager.getCin())) {
                    reservationsCount++;
                }
            }
        }
        if (reservationsCountLabel != null) {
            reservationsCountLabel.setText(String.valueOf(reservationsCount));
        }
    }
    
    private void refreshTrajetsDisponibles() {
        trajetsDisponiblesModel.setRowCount(0);
        
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getConducteur() != null && 
                t.getStatusTrajet().equals("PENDING") &&
                t.getConducteur().getPlacesDisponibles() > 0) {
                
                Conducteur c = t.getConducteur();
                trajetsDisponiblesModel.addRow(new Object[]{
                    c.getNom() + " " + c.getPrenom(),
                    t.getDepartTrajet(),
                    t.getArriveeTrajet(),
                    t.getDureeTrajet().toMinutes() + " min",
                    String.format("%.2f", t.getPrix()),
                    c.getPlacesDisponibles()
                });
            }
        }
    }
    
    private void refreshMesReservations() {
        mesReservationsModel.setRowCount(0);
        
        Passager passager = mainFrame.getCurrentPassager();
        if (passager == null) return;
        
        for (Trajet t : mainFrame.getGestion().getTrajets()) {
            if (t.getPassager() != null && 
                t.getPassager().getCin().equals(passager.getCin()) &&
                t.getConducteur() != null) {
                
                Conducteur c = t.getConducteur();
                mesReservationsModel.addRow(new Object[]{
                    c.getNom() + " " + c.getPrenom(),
                    c.getTel(),
                    t.getDepartTrajet(),
                    t.getArriveeTrajet(),
                    String.format("%.2f", t.getPrix()),
                    t.getStatusTrajet()
                });
            }
        }
    }
}
