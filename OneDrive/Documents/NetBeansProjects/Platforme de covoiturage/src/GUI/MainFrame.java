package GUI;

import Models.*;
import Services.*;
import javax.swing.*;
import java.awt.*;

/**
 * Main application frame with card layout for navigation
 * Enhanced with modern UI components
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
    
    public MainFrame() {
        gestion = new Gestion_covoiturage();
        initializeFrame();
        initializePanels();
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
        driverPanel.refresh();
        cardLayout.show(mainPanel, "DRIVER");
    }
    
    public void showPassengerPanel(Passager passager) {
        this.currentUser = passager;
        this.userType = "PASSAGER";
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
