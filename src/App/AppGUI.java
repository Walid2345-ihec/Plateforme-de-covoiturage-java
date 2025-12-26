package App;

import GUI.MainFrame;
import javax.swing.*;
import java.awt.*;

/**
 * AppGUI - Point d'entrée de l'application graphique
 */
public class AppGUI {
    
    public static void main(String[] args) {
        // Met le look and feel de Nimbus
        try {
            // Essaye de trouver Nimbus
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si Nimbus n'est pas trouvé, utilise le look and feel par défaut
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Customiser l'apparence de Nimbus
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.PLAIN, 13));

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Lancer l'application sur le thread d'événement Swing
        SwingUtilities.invokeLater(() -> {
            //Montrer l'écran de démarrage
            showSplashScreen();
            
            // Créer la fenêtre principale
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
    
    /**
     * Affiche un écran de démarrage pendant le chargement de l'application
     */
    private static void showSplashScreen() {
        JWindow splash = new JWindow();
        splash.setSize(500, 300);
        splash.setLocationRelativeTo(null);
        
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(41, 128, 185),
                    getWidth(), getHeight(), new Color(52, 152, 219)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        content.setLayout(new BorderLayout());
        
        // Logo/Title
        JLabel titleLabel = new JLabel("🚗 Plateforme de Covoiturage", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(80, 0, 0, 0));
        content.add(titleLabel, BorderLayout.NORTH);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Partagez vos trajets, économisez ensemble", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        content.add(subtitleLabel, BorderLayout.CENTER);
        
        // Indicateur de chargement
        JLabel loadingLabel = new JLabel("Chargement...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        loadingLabel.setForeground(new Color(255, 255, 255, 180));
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        content.add(loadingLabel, BorderLayout.SOUTH);
        
        splash.setContentPane(content);
        splash.setVisible(true);
        
        // Attendre 1,5 secondes
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        splash.dispose();
    }
}
