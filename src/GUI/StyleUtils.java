package GUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Classe utilitaire pour garantir une apparence cohérente dans toute l'application.
 */
public class StyleUtils {
    
    // Palette de couleurs - Thème bleu moderne
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Bleu principal
    public static final Color PRIMARY_DARK = new Color(31, 97, 141);        // Bleu foncé (hover)
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219);    // Bleu clair
    public static final Color ACCENT_COLOR = new Color(46, 204, 113);       // Vert accent (success)
    public static final Color WARNING_COLOR = new Color(241, 196, 15);      // Jaune (warning)
    public static final Color DANGER_COLOR = new Color(231, 76, 60);        // Rouge (erreur/danger)
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241);  // Gris clair de fond
    public static final Color CARD_COLOR = Color.WHITE;                     // Couleur des cartes
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);         // Texte principal (gris foncé)
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);    // Texte secondaire (gris moyen)

    // Polices standardisées
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    /**
     * Crée un bouton primaire stylé (fond primaire, texte blanc).
     * Utilisé pour les actions principales.
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 45));
        
        // Effet au survol : couleur légèrement plus foncée
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }
    
    /**
     * Crée un bouton secondaire stylé (fond blanc, bordure colorée).
     * Utilisé pour les actions secondaires ou alternatives.
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setForeground(PRIMARY_COLOR);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 45));
        
        // Effet au survol : léger fond bleu pâle
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(235, 245, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    /**
     * Crée un bouton de succès stylé (vert accent, texte blanc).
     * Utilisé pour les actions positives (valider, confirmer, enregistrer).
     */
    public static JButton createSuccessButton(String text) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 45));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_COLOR);
            }
        });
        
        return button;
    }
    
    /**
     * Crée un bouton danger stylé (rouge, texte blanc).
     * Utilisé pour les actions destructrices (suppression, annulation définitive).
     */
    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(DANGER_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 45));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(192, 57, 43));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(DANGER_COLOR);
            }
        });
        
        return button;
    }
    
    /**
     * Crée un champ de texte stylé (bordure douce, padding interne).
     * Utilisé pour les formulaires principaux.
     */
    public static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(REGULAR_FONT);
        field.setPreferredSize(new Dimension(250, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    /**
     * Crée un champ de mot de passe stylé (même apparence que le champ texte).
     */
    public static JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(REGULAR_FONT);
        field.setPreferredSize(new Dimension(250, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    /**
     * Crée un JLabel stylé (texte standard pour les formulaires).
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(REGULAR_FONT);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Crée un JLabel pour les en-têtes de section.
     */
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Crée un JLabel pour les titres de pages ou écrans.
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_COLOR);
        return label;
    }
    
    /**
     * Crée un panneau "carte" avec un effet d'ombre et un padding interne.
     * Idéal pour contenir des formulaires ou des blocs d'informations.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }
    
    /**
     * Crée un JComboBox stylé avec les dimensions et la police standard.
     */
    public static <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(REGULAR_FONT);
        comboBox.setPreferredSize(new Dimension(250, 40));
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }
    
    /**
     * Crée un JTable stylé (hauteur des lignes, entête colorée, sélection).
     */
    public static JTable createStyledTable(Object[][] data, String[] columns) {
        JTable table = new JTable(data, columns);
        table.setFont(REGULAR_FONT);
        table.setRowHeight(35);
        table.setGridColor(new Color(236, 240, 241));
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        return table;
    }
    
    /**
     * Crée un JScrollPane stylé (bordure et fond du viewport).
     */
    public static JScrollPane createStyledScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }
    
    /**
     * Bordure personnalisée avec ombre pour les cartes.
     */
    static class ShadowBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Dessine l'ombre
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(x + 3, y + 3, width - 3, height - 3, 10, 10);
            
            // Dessine le contour
            g2.setColor(new Color(189, 195, 199));
            g2.drawRoundRect(x, y, width - 4, height - 4, 10, 10);
            
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 5, 8, 8);
        }
    }
    
    /**
     * Affiche une boîte de dialogue d'information (succès) avec un style simple.
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Affiche une boîte de dialogue d'erreur.
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Affiche une boîte de dialogue d'avertissement.
     */
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Affiche une boîte de dialogue de confirmation et retourne true si l'utilisateur a choisi "Oui".
     */
    public static boolean showConfirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Confirmation", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
