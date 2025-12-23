package GUI;

import Models.*;
import Services.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 * Main application frame with card layout for navigation
 * Enhanced with modern UI components and CSV persistence
 * 
 * AUTO-SAVE FEATURES:
 * - Periodic auto-save every 5 minutes
 * - Save on window close with user confirmation
 * - Shutdown hook for unexpected terminations
 * - Backup creation before each save
 */
public class MainFrame extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Gestion_covoiturage gestion;
    
    // Enhanced Panels
    private EnhancedLoginPanel loginPanel;
    private EnhancedDriverPanel driverPanel;
    private EnhancedPassengerPanel passengerPanel;
    
    // Current user info
    private User currentUser;
    private String userType; // "CONDUCTEUR" or "PASSAGER"
    
    // Auto-save timer
    private Timer autoSaveTimer;
    private static final int AUTO_SAVE_INTERVAL = 5 * 60 * 1000; // 5 minutes in ms
    private boolean hasUnsavedChanges = false;
    
    public MainFrame() {
        gestion = new Gestion_covoiturage();
        
        // STEP: Load data from CSV files on startup
        loadDataFromCSV();
        
        initializeFrame();
        initializePanels();
        
        // STEP: Setup comprehensive auto-save system
        setupAutoSave();
        setupPeriodicAutoSave();
        setupShutdownHook();
    }
    
    /**
     * Loads all data from CSV files into the application.
     * Called when the application starts.
     */
    private void loadDataFromCSV() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Démarrage de l'application...");
        CSVDatabase.loadAllData(gestion);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Sets up automatic saving when the application closes.
     */
    private void setupAutoSave() {
        // Override default close operation
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleApplicationClose();
            }
        });
    }
    
    /**
     * Handles the application close event with proper cleanup.
     */
    private void handleApplicationClose() {
        // Stop auto-save timer
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
            // Cancel - do nothing, stay in app
        } else {
            // No unsaved changes, just confirm exit
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
     * Performs cleanup and exits the application.
     */
    private void cleanupAndExit() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Fermeture de l'application...");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        dispose();
        System.exit(0);
    }
    
    /**
     * Sets up periodic auto-save every 5 minutes.
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
     * Sets up a shutdown hook for unexpected terminations.
     * This ensures data is saved even if the JVM is killed unexpectedly.
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
     * Marks that there are unsaved changes.
     * Call this after any data modification.
     */
    public void markUnsavedChanges() {
        this.hasUnsavedChanges = true;
    }
    
    /**
     * Saves all data with backup creation.
     */
    public void saveDataWithBackup() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        // Create backup before saving
        CSVDatabase.createBackup();
        // Save current data
        CSVDatabase.saveAllData(gestion);
        hasUnsavedChanges = false;
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Saves all data to CSV files.
     * Can be called manually or automatically on close.
     */
    public void saveDataToCSV() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        CSVDatabase.saveAllData(gestion);
        hasUnsavedChanges = false;
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Exports trajets to a user-friendly CSV file.
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
        
        // Set application icon
        try {
            setIconImage(new ImageIcon(getClass().getResource("/resources/car_icon.png")).getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        
        // Enhanced Look and Feel settings
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Custom UI defaults for modern appearance
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Main panel with card layout
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
    
    // Navigation methods
    public void showLogin() {
        currentUser = null;
        userType = null;
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    public void showDriverPanel(Conducteur conducteur) {
        this.currentUser = conducteur;
        this.userType = "CONDUCTEUR";
        // Synchronize gestion indices for console compatibility
        if (conducteur != null) {
            // find index of conducteur in gestion.users
            int idx = -1;
            for (int i = 0; i < gestion.getUsers().size(); i++) {
                User u = gestion.getUsers().get(i);
                if (u instanceof Conducteur && u.getCin().equals(conducteur.getCin())) {
                    idx = i; break;
                }
            }
            try {
                // reflect to set private Index_conducteur and Index_trajet_conducteur
                java.lang.reflect.Field f = gestion.getClass().getDeclaredField("Index_conducteur");
                f.setAccessible(true);
                f.setInt(gestion, idx);

                // set Index_trajet_conducteur to first trajet of conducteur if any
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
        // Synchronize gestion Index_passager
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
     * Notify all panels that underlying data changed and they should refresh.
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
