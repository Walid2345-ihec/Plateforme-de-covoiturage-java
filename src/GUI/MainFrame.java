package GUI;

import Models.*;
import Services.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * Cadre principal de l'application avec CardLayout pour la navigation
 * Amélioré avec des composants UI modernes et persistance CSV.
 *
 * FONCTIONNALITÉS DE SAUVEGARDE AUTOMATIQUE :
 * - Sauvegarde périodique toutes les 5 minutes
 * - Sauvegarde à la fermeture de la fenêtre (confirmation utilisateur)
 * - Hook de shutdown pour terminaisons inattendues
 * - Création de backup avant chaque sauvegarde
 */
public class MainFrame extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Gestion_covoiturage gestion;
    
    // Panneaux améliorés (UI)
    private EnhancedLoginPanel loginPanel;
    private EnhancedDriverPanel driverPanel;
    private EnhancedPassengerPanel passengerPanel;
    
    // Informations sur l'utilisateur courant
    private User currentUser;
    private String userType; // "CONDUCTEUR" ou "PASSAGER"

    // Timer de sauvegarde automatique
    private Timer autoSaveTimer;
    private static final int AUTO_SAVE_INTERVAL = 5 * 60 * 1000; // 5 minutes en ms
    private boolean hasUnsavedChanges = false;
    
    public MainFrame() {
        gestion = new Gestion_covoiturage();
        
        // ÉTAPE : charger les données depuis les fichiers CSV au démarrage
        loadDataFromCSV();
        
        initializeFrame();
        initializePanels();
        
        // ÉTAPE : configurer le système complet de sauvegarde automatique
        setupAutoSave();
        setupPeriodicAutoSave();
        setupShutdownHook();
    }
    
    /**
     * Charge toutes les données depuis les fichiers CSV dans l'application.
     * Appelée lors du démarrage.
     */
    private void loadDataFromCSV() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Démarrage de l'application...");
        CSVDatabase.loadAllData(gestion);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Configure la sauvegarde automatique lors de la fermeture de l'application.
     */
    private void setupAutoSave() {
        // Remplace le comportement de fermeture par défaut
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleApplicationClose();
            }
        });
    }
    
    /**
     * Gère l'événement de fermeture de l'application avec nettoyage approprié.
     */
    private void handleApplicationClose() {
        // Arrête le timer d'auto-save
        if (autoSaveTimer != null && autoSaveTimer.isRunning()) {
            autoSaveTimer.stop();
        }
        
        if (hasUnsavedChanges) {
            int choice = JOptionPane.showConfirmDialog(
                MainFrame.this,
                "Vous avez des modifications non sauvegardées.\n" +
                "Voulez-vous sauvegarder avant de quitter?",
                "Sauvegarder les modifications",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                saveDataWithBackup();
                cleanupAndExit();
            } else if (choice == JOptionPane.NO_OPTION) {
                cleanupAndExit();
            }
            // Annuler -> ne fait rien, reste dans l'application
        } else {
            // Pas de modifications non sauvegardées, confirmer simplement la sortie
            int choice = JOptionPane.showConfirmDialog(
                MainFrame.this,
                "Voulez-vous quitter l'application?",
                "Quitter",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                saveDataWithBackup();
                cleanupAndExit();
            }
        }
    }
    
    /**
     * Effectue le nettoyage nécessaire puis ferme l'application.
     */
    private void cleanupAndExit() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Fermeture de l'application...");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        dispose();
        System.exit(0);
    }
    
    /**
     * Configure une sauvegarde périodique toutes les 5 minutes.
     */
    private void setupPeriodicAutoSave() {
        autoSaveTimer = new Timer(AUTO_SAVE_INTERVAL, e -> {
            if (hasUnsavedChanges) {
                System.out.println("⏰ Auto-save en cours...");
                saveDataWithBackup();
                hasUnsavedChanges = false;
            }
        });
        autoSaveTimer.setRepeats(true);
        autoSaveTimer.start();
        System.out.println("✓ Auto-save activé (toutes les 5 minutes)");
    }
    
    /**
     * Configure un shutdown hook pour les terminaisons inattendues.
     * Assure que les données sont sauvegardées si la JVM est tuée.
     */
    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n Shutdown détecté - Sauvegarde d'urgence...");
            try {
                CSVDatabase.saveAllData(gestion);
                System.out.println("✓ Sauvegarde d'urgence terminée");
            } catch (Exception ex) {
                System.err.println("✗ Erreur sauvegarde d'urgence: " + ex.getMessage());
            }
        }, "ShutdownHook-SaveData"));
    }
    
    /**
     * Marque qu'il y a des modifications non sauvegardées.
     * Appeler après toute modification des données.
     */
    public void markUnsavedChanges() {
        this.hasUnsavedChanges = true;
    }
    
    /**
     * Sauvegarde toutes les données en créant d'abord une sauvegarde (backup).
     */
    public void saveDataWithBackup() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        // Crée un backup avant la sauvegarde
        CSVDatabase.createBackup();
        // Sauvegarde des données courantes
        CSVDatabase.saveAllData(gestion);
        hasUnsavedChanges = false;
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Sauvegarde toutes les données dans les fichiers CSV.
     * Peut être appelée manuellement ou automatiquement à la fermeture.
     */
    public void saveDataToCSV() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        CSVDatabase.saveAllData(gestion);
        hasUnsavedChanges = false;
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Export des trajets vers un fichier CSV lisible par l'utilisateur.
     */
    public void exportTrajetsToCSV(String filename) {
        CSVDatabase.exportToExcelCSV(gestion.getTrajets(), filename);
    }
    
    private void initializeFrame() {
        setTitle("🚗 Plateforme de Covoiturage");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 850);
        setMinimumSize(new Dimension(1100, 750));
        setLocationRelativeTo(null);
        
        // Définir l'icône de l'application (si présente)
        try {
            setIconImage(new ImageIcon(getClass().getResource("/resources/car_icon.png")).getImage());
        } catch (Exception e) {
            // Icône non trouvée, continuer sans
        }
        
        // Paramètres d'apparence améliorés
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Défauts UI personnalisés pour une apparence moderne
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Panneau principal avec CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(ModernUIComponents.Colors.SURFACE);
        add(mainPanel);
    }
    
    private void initializePanels() {
        loginPanel = new EnhancedLoginPanel(this);
        driverPanel = new EnhancedDriverPanel(this);
        passengerPanel = new EnhancedPassengerPanel(this);
        
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(driverPanel, "DRIVER");
        mainPanel.add(passengerPanel, "PASSENGER");
        
        showLogin();
    }
    
    // Méthodes de navigation
    public void showLogin() {
        currentUser = null;
        userType = null;
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    public void showDriverPanel(Conducteur conducteur) {
        this.currentUser = conducteur;
        this.userType = "CONDUCTEUR";
        // Synchronise les index de gestion pour compatibilité console
        if (conducteur != null) {
            // cherche l'index du conducteur dans gestion.users
            int idx = -1;
            for (int i = 0; i < gestion.getUsers().size(); i++) {
                User u = gestion.getUsers().get(i);
                if (u instanceof Conducteur && u.getCin().equals(conducteur.getCin())) {
                    idx = i; break;
                }
            }
            try {
                // réflexion pour définir les champs privés Index_conducteur et Index_trajet_conducteur
                java.lang.reflect.Field f = gestion.getClass().getDeclaredField("Index_conducteur");
                f.setAccessible(true);
                f.setInt(gestion, idx);

                // définir Index_trajet_conducteur sur le premier trajet du conducteur si existe
                int trajetIndex = -1;
                for (int i = 0; i < gestion.getTrajets().size(); i++) {
                    Trajet t = gestion.getTrajets().get(i);
                    if (t.getConducteur() != null && t.getConducteur().getCin().equals(conducteur.getCin())) { trajetIndex = i; break; }
                }
                java.lang.reflect.Field ft = gestion.getClass().getDeclaredField("Index_trajet_conducteur");
                ft.setAccessible(true);
                ft.setInt(gestion, trajetIndex);
            } catch (Exception ignored) {}

        }
        driverPanel.refresh();
        cardLayout.show(mainPanel, "DRIVER");
    }
    
    public void showPassengerPanel(Passager passager) {
        this.currentUser = passager;
        this.userType = "PASSAGER";
        // Synchronise Index_passager de gestion
        if (passager != null) {
            int idx = -1;
            for (int i = 0; i < gestion.getUsers().size(); i++) {
                User u = gestion.getUsers().get(i);
                if (u instanceof Passager && u.getCin().equals(passager.getCin())) { idx = i; break; }
            }
            try {
                java.lang.reflect.Field f = gestion.getClass().getDeclaredField("Index_passager");
                f.setAccessible(true);
                f.setInt(gestion, idx);
            } catch (Exception ignored) {}
        }
        passengerPanel.refresh();
        cardLayout.show(mainPanel, "PASSENGER");
    }
    
    // Getters
    public Gestion_covoiturage getGestion() {
        return gestion;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public Conducteur getCurrentConducteur() {
        if (currentUser instanceof Conducteur) {
            return (Conducteur) currentUser;
        }
        return null;
    }
    
    public Passager getCurrentPassager() {
        if (currentUser instanceof Passager) {
            return (Passager) currentUser;
        }
        return null;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Informe tous les panneaux que les données sous-jacentes ont changé et qu'ils doivent se rafraîchir.
     */
    public void notifyDataChanged() {
        try {
            if (driverPanel != null) driverPanel.refreshModels();
        } catch (Exception ignored) {}
        try {
            if (passengerPanel != null) passengerPanel.refreshModels();
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
