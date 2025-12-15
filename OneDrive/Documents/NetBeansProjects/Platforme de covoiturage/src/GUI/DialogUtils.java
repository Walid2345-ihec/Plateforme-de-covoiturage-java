package GUI;

import Models.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.Year;

/**
 * Utility class for creating custom dialogs
 */
public class DialogUtils {
    
    /**
     * Shows a detailed trip information dialog
     */
    public static void showTrajetDetails(Component parent, Trajet trajet) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), 
            "Détails du Trajet", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("🛣️ Détails du Trajet");
        headerLabel.setFont(StyleUtils.SUBTITLE_FONT);
        headerLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        addDetailRow(contentPanel, "Départ", trajet.getDepartTrajet());
        addDetailRow(contentPanel, "Arrivée", trajet.getArriveeTrajet());
        addDetailRow(contentPanel, "Durée", trajet.getDureeTrajet().toMinutes() + " minutes");
        addDetailRow(contentPanel, "Prix", String.format("%.2f TND", trajet.getPrix()));
        addDetailRow(contentPanel, "Statut", trajet.getStatusTrajet());
        addDetailRow(contentPanel, "Validé", trajet.isTrajet_valide() ? "Oui" : "Non");
        
        if (trajet.getConducteur() != null) {
            contentPanel.add(Box.createVerticalStrut(15));
            addSectionHeader(contentPanel, "Conducteur");
            addDetailRow(contentPanel, "Nom", trajet.getConducteur().getNom() + " " + 
                trajet.getConducteur().getPrenom());
            addDetailRow(contentPanel, "Téléphone", trajet.getConducteur().getTel());
            addDetailRow(contentPanel, "Véhicule", trajet.getConducteur().getMarqueVoiture() + 
                " " + trajet.getConducteur().getNomVoiture());
        }
        
        if (trajet.getPassager() != null) {
            contentPanel.add(Box.createVerticalStrut(15));
            addSectionHeader(contentPanel, "Passager");
            addDetailRow(contentPanel, "Nom", trajet.getPassager().getNom() + " " + 
                trajet.getPassager().getPrenom());
            addDetailRow(contentPanel, "Téléphone", trajet.getPassager().getTel());
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JButton closeBtn = StyleUtils.createPrimaryButton("Fermer");
        closeBtn.setPreferredSize(new Dimension(120, 40));
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Shows a user profile dialog
     */
    public static void showUserProfile(Component parent, User user) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), 
            "Profil Utilisateur", true);
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with avatar
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel avatarLabel = new JLabel(user instanceof Conducteur ? "🚗" : "👤");
        avatarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel nameLabel = new JLabel(user.getPrenom() + " " + user.getNom());
        nameLabel.setFont(StyleUtils.SUBTITLE_FONT);
        nameLabel.setForeground(StyleUtils.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel typeLabel = new JLabel(user instanceof Conducteur ? "Conducteur" : "Passager");
        typeLabel.setFont(StyleUtils.SMALL_FONT);
        typeLabel.setForeground(StyleUtils.TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(avatarLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(nameLabel);
        headerPanel.add(typeLabel);
        headerPanel.add(Box.createVerticalStrut(20));
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        addDetailRow(contentPanel, "CIN", user.getCin());
        addDetailRow(contentPanel, "Téléphone", user.getTel());
        addDetailRow(contentPanel, "Email", user.getMail());
        addDetailRow(contentPanel, "Adresse", user.getAdresse() != null ? user.getAdresse() : "Non spécifiée");
        addDetailRow(contentPanel, "Année Universitaire", 
            user.getAnneeUniversitaire() != null ? user.getAnneeUniversitaire().toString() : "N/A");
        
        if (user instanceof Conducteur) {
            Conducteur c = (Conducteur) user;
            contentPanel.add(Box.createVerticalStrut(15));
            addSectionHeader(contentPanel, "Informations Véhicule");
            addDetailRow(contentPanel, "Véhicule", c.getMarqueVoiture() + " " + c.getNomVoiture());
            addDetailRow(contentPanel, "Matricule", c.getMatricule());
            addDetailRow(contentPanel, "Places Disponibles", String.valueOf(c.getPlacesDisponibles()));
        }
        
        if (user instanceof Passager) {
            Passager p = (Passager) user;
            contentPanel.add(Box.createVerticalStrut(15));
            addDetailRow(contentPanel, "Cherche Covoiturage", p.isChercheCovoit() ? "Oui" : "Non");
        }
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JButton closeBtn = StyleUtils.createPrimaryButton("Fermer");
        closeBtn.setPreferredSize(new Dimension(120, 40));
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Shows an about dialog
     */
    public static void showAboutDialog(Component parent) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), 
            "À Propos", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Logo
        JLabel logoLabel = new JLabel("🚗");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(logoLabel);
        
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Title
        JLabel titleLabel = new JLabel("Plateforme de Covoiturage");
        titleLabel.setFont(StyleUtils.SUBTITLE_FONT);
        titleLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        
        mainPanel.add(Box.createVerticalStrut(5));
        
        // Version
        JLabel versionLabel = new JLabel("Version 1.0.0");
        versionLabel.setFont(StyleUtils.SMALL_FONT);
        versionLabel.setForeground(StyleUtils.TEXT_SECONDARY);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(versionLabel);
        
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Description
        JLabel descLabel = new JLabel("<html><center>Une application de covoiturage<br>" +
            "pour les étudiants universitaires.<br><br>" +
            "Partagez vos trajets et économisez<br>sur vos frais de transport.</center></html>");
        descLabel.setFont(StyleUtils.REGULAR_FONT);
        descLabel.setForeground(StyleUtils.TEXT_PRIMARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(descLabel);
        
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Copyright
        JLabel copyrightLabel = new JLabel("© 2024 IHEC - Tous droits réservés");
        copyrightLabel.setFont(StyleUtils.SMALL_FONT);
        copyrightLabel.setForeground(StyleUtils.TEXT_SECONDARY);
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(copyrightLabel);
        
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Close button
        JButton closeBtn = StyleUtils.createPrimaryButton("Fermer");
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dialog.dispose());
        mainPanel.add(closeBtn);
        
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }
    
    // Helper methods
    private static void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setOpaque(false);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        rowPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        
        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(StyleUtils.REGULAR_FONT);
        labelComp.setForeground(StyleUtils.TEXT_SECONDARY);
        labelComp.setPreferredSize(new Dimension(140, 25));
        
        JLabel valueComp = new JLabel(value != null ? value : "N/A");
        valueComp.setFont(StyleUtils.REGULAR_FONT);
        valueComp.setForeground(StyleUtils.TEXT_PRIMARY);
        
        rowPanel.add(labelComp, BorderLayout.WEST);
        rowPanel.add(valueComp, BorderLayout.CENTER);
        
        panel.add(rowPanel);
    }
    
    private static void addSectionHeader(JPanel panel, String title) {
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(StyleUtils.HEADER_FONT);
        headerLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        headerLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, StyleUtils.PRIMARY_COLOR));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(10));
    }
    
    /**
     * Creates an input dialog with custom styling
     */
    public static String showInputDialog(Component parent, String title, String message) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Shows a loading dialog (returns the dialog so it can be disposed later)
     */
    public static JDialog showLoadingDialog(Component parent, String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "", true);
        dialog.setSize(250, 100);
        dialog.setLocationRelativeTo(parent);
        dialog.setUndecorated(true);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(StyleUtils.PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel loadingLabel = new JLabel(message);
        loadingLabel.setFont(StyleUtils.REGULAR_FONT);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setForeground(StyleUtils.PRIMARY_COLOR);
        
        panel.add(loadingLabel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        
        dialog.setContentPane(panel);
        
        return dialog;
    }
}
