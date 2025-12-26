package GUI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Bibliothèque de composants UI modernisés
 * Fournit des composants animés et visuellement améliorés pour une interface moderne
 */
public class ModernUIComponents {
    
    // ==================== PALETTE DE COULEURS MODERNE ====================
    public static class Colors {
        // Couleurs du gradient principal
        public static final Color PRIMARY_START = new Color(102, 126, 234);    // Violet-bleu
        public static final Color PRIMARY_END = new Color(118, 75, 162);       // Violet

        // Gradient alternatif (teal -> vert)
        public static final Color GRADIENT_TEAL_START = new Color(17, 153, 142);
        public static final Color GRADIENT_TEAL_END = new Color(56, 239, 125);
        
        // Couleurs d'accentuation
        public static final Color ACCENT_CORAL = new Color(255, 107, 107);
        public static final Color ACCENT_MINT = new Color(46, 213, 115);
        public static final Color ACCENT_SKY = new Color(30, 144, 255);
        public static final Color ACCENT_GOLD = new Color(255, 193, 7);
        
        // Couleurs neutres
        public static final Color DARK_BG = new Color(30, 30, 46);
        public static final Color CARD_BG = new Color(255, 255, 255);
        public static final Color SURFACE = new Color(248, 249, 250);
        public static final Color BORDER = new Color(222, 226, 230);
        
        // Couleurs de texte
        public static final Color TEXT_DARK = new Color(33, 37, 41);
        public static final Color TEXT_MUTED = new Color(108, 117, 125);
        public static final Color TEXT_LIGHT = new Color(173, 181, 189);
        
        // Couleurs de statut
        public static final Color SUCCESS = new Color(40, 167, 69);
        public static final Color WARNING = new Color(255, 193, 7);
        public static final Color DANGER = new Color(220, 53, 69);
        public static final Color INFO = new Color(23, 162, 184);
    }
    
    // ==================== POLICES MODERNES ====================
    public static class Fonts {
        public static final Font DISPLAY = new Font("Segoe UI", Font.BOLD, 42);
        public static final Font HEADING_1 = new Font("Segoe UI", Font.BOLD, 32);
        public static final Font HEADING_2 = new Font("Segoe UI", Font.BOLD, 24);
        public static final Font HEADING_3 = new Font("Segoe UI Semibold", Font.PLAIN, 18);
        public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);
        public static final Font BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
        public static final Font CAPTION = new Font("Segoe UI", Font.PLAIN, 12);
        public static final Font BUTTON = new Font("Segoe UI Semibold", Font.PLAIN, 14);
    }
    
    // ==================== BOUTON ARRONDI (RoundedButton) ====================
    public static class RoundedButton extends JButton {
        private Color normalColor;
        private Color hoverColor;
        private Color pressedColor;
        private int radius = 25;
        private boolean isHovered = false;
        private boolean isPressed = false;
        private Timer animationTimer;
        private float animationProgress = 0f;
        
        public RoundedButton(String text, Color bgColor) {
            super(text);
            this.normalColor = bgColor;
            this.hoverColor = brighten(bgColor, 0.1f);
            this.pressedColor = darken(bgColor, 0.1f);
            
            setFont(Fonts.BUTTON);
            setForeground(Color.WHITE);
            setBackground(normalColor);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(180, 48));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    animateHover(true);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    isPressed = false;
                    animateHover(false);
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }
        
        private void animateHover(boolean entering) {
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }
            
            animationTimer = new Timer(16, e -> {
                if (entering) {
                    animationProgress = Math.min(1f, animationProgress + 0.15f);
                } else {
                    animationProgress = Math.max(0f, animationProgress - 0.15f);
                }
                
                if ((entering && animationProgress >= 1f) || (!entering && animationProgress <= 0f)) {
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });
            animationTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Calcul de la couleur animée
            Color currentColor;
            if (isPressed) {
                currentColor = pressedColor;
            } else {
                currentColor = interpolateColor(normalColor, hoverColor, animationProgress);
            }
            
            // Dessine une ombre lorsque le bouton est survolé
            if (animationProgress > 0) {
                int shadowOffset = (int) (4 * animationProgress);
                g2.setColor(new Color(0, 0, 0, (int) (30 * animationProgress)));
                g2.fillRoundRect(2, shadowOffset, width - 4, height - 2, radius, radius);
            }
            
            // Dessine le fond du bouton
            g2.setColor(currentColor);
            g2.fillRoundRect(0, 0, width - 1, height - 1 - (int)(2 * animationProgress), radius, radius);
            
            // Dessine le texte avec une meilleure lisibilité
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Font buttonFont = getFont().deriveFont(Font.BOLD, 14f);
            g2.setFont(buttonFont);
            FontMetrics fm = g2.getFontMetrics();
            int textX = (width - fm.stringWidth(getText())) / 2;
            int textY = (height - fm.getHeight()) / 2 + fm.getAscent() - (int)(animationProgress);
            
            // Ombre du texte pour profondeur et lisibilité
            g2.setColor(new Color(0, 0, 0, 100));
            g2.drawString(getText(), textX + 1, textY + 1);
            
            // Texte principal en blanc
            g2.setColor(Color.WHITE);
            g2.drawString(getText(), textX, textY);
            
            g2.dispose();
        }
    }
    
    // ==================== BOUTON GRADIENT (GradientButton) ====================
    public static class GradientButton extends JButton {
        private Color startColor;
        private Color endColor;
        private int radius = 25;
        private float animationProgress = 0f;
        private Timer animationTimer;
        
        public GradientButton(String text, Color start, Color end) {
            super(text);
            this.startColor = start;
            this.endColor = end;
            
            setFont(Fonts.BUTTON);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(200, 50));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    animateHover(true);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    animateHover(false);
                }
            });
        }
        
        private void animateHover(boolean entering) {
            if (animationTimer != null) animationTimer.stop();
            
            animationTimer = new Timer(16, e -> {
                if (entering) {
                    animationProgress = Math.min(1f, animationProgress + 0.12f);
                } else {
                    animationProgress = Math.max(0f, animationProgress - 0.12f);
                }
                if ((entering && animationProgress >= 1f) || (!entering && animationProgress <= 0f)) {
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });
            animationTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Ombre animée
            int shadowY = (int) (5 * animationProgress);
            g2.setColor(new Color(0, 0, 0, (int) (40 * animationProgress)));
            g2.fillRoundRect(3, 3 + shadowY, w - 6, h - 4, radius, radius);
            
            // Fond en gradient
            GradientPaint gradient = new GradientPaint(
                0, 0, startColor,
                w, h, endColor
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(0, (int)(-2 * animationProgress), w - 1, h - 1, radius, radius);
            
            // Effet de brillance au survol
            if (animationProgress > 0) {
                g2.setColor(new Color(255, 255, 255, (int) (40 * animationProgress)));
                g2.fillRoundRect(0, 0, w - 1, h / 2, radius, radius);
            }
            
            // Texte avec ombre
            FontMetrics fm = g2.getFontMetrics(getFont());
            int textX = (w - fm.stringWidth(getText())) / 2;
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent() - (int)(2 * animationProgress);
            
            g2.setFont(getFont());
            g2.setColor(new Color(0, 0, 0, 50));
            g2.drawString(getText(), textX + 1, textY + 1);
            g2.setColor(getForeground());
            g2.drawString(getText(), textX, textY);
            
            g2.dispose();
        }
    }
    
    // ==================== CHAMP DE TEXTE MODERNE (ModernTextField) ====================
    public static class ModernTextField extends JTextField {
        private String placeholder;
        private Color borderColor = Colors.BORDER;
        private Color focusBorderColor = Colors.PRIMARY_START;
        private boolean isFocused = false;
        private int radius = 12;
        private String iconText = null;
        
        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setFont(Fonts.BODY);
            setForeground(Colors.TEXT_DARK);
            setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
            setPreferredSize(new Dimension(280, 48));
            
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    isFocused = true;
                    repaint();
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    isFocused = false;
                    repaint();
                }
            });
        }
        
        public void setIcon(String icon) {
            this.iconText = icon;
            setBorder(BorderFactory.createEmptyBorder(12, 45, 12, 15));
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Fond
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);
            
            // Bordure
            g2.setColor(isFocused ? focusBorderColor : borderColor);
            g2.setStroke(new BasicStroke(isFocused ? 2f : 1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
            
            // Icône
            if (iconText != null) {
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                g2.setColor(Colors.TEXT_MUTED);
                g2.drawString(iconText, 15, h / 2 + 6);
            }
            
            g2.dispose();
            
            super.paintComponent(g);
            
            // Placeholder - fix : dessin dans le contexte paintComponent
            if (getText().isEmpty() && !isFocused && placeholder != null) {
                Graphics2D g2p = (Graphics2D) g.create();
                g2p.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2p.setColor(Colors.TEXT_LIGHT);
                g2p.setFont(getFont());
                int x = iconText != null ? 45 : 15;
                FontMetrics fm = g2p.getFontMetrics();
                int y = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2p.drawString(placeholder, x, y);
                g2p.dispose();
            }
        }
        
        /**
         * Retourne le texte courant (conserve le comportement hérité)
         */
        @Override
        public String getText() {
            return super.getText();
        }
    }
    
    // ==================== CHAMP DE MOT DE PASSE MODERNE (ModernPasswordField) ====================
    public static class ModernPasswordField extends JPasswordField {
        private String placeholder;
        private Color borderColor = Colors.BORDER;
        private Color focusBorderColor = Colors.PRIMARY_START;
        private boolean isFocused = false;
        private int radius = 12;
        
        public ModernPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setFont(Fonts.BODY);
            setForeground(Colors.TEXT_DARK);
            setBorder(BorderFactory.createEmptyBorder(12, 45, 12, 15));
            setPreferredSize(new Dimension(280, 48));
            
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    isFocused = true;
                    repaint();
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    isFocused = false;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Fond
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);
            
            // Bordure
            g2.setColor(isFocused ? focusBorderColor : borderColor);
            g2.setStroke(new BasicStroke(isFocused ? 2f : 1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
            
            // Icône cadenas
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            g2.setColor(Colors.TEXT_MUTED);
            g2.drawString("🔒", 15, h / 2 + 6);
            
            g2.dispose();
            
            super.paintComponent(g);
        }
    }
    
    // ==================== PANNEAU GLASS CARD (GlassCard) ====================
    public static class GlassCard extends JPanel {
        private int radius = 20;
        private Color bgColor;
        private float opacity = 0.95f;
        
        public GlassCard() {
            this(Colors.CARD_BG);
        }
        
        public GlassCard(Color backgroundColor) {
            this.bgColor = backgroundColor;
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Ombre externe
            for (int i = 0; i < 8; i++) {
                g2.setColor(new Color(0, 0, 0, 5 - i / 2));
                g2.fillRoundRect(i, i + 2, w - i * 2, h - i * 2, radius + i, radius + i);
            }
            
            // Fond de la carte
            g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(),
                (int)(255 * opacity)));
            g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);
            
            // Bordure subtile
            g2.setColor(new Color(255, 255, 255, 100));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
            
            g2.dispose();
        }
    }
    
    // ==================== EN-TETE GRADIENT (GradientHeader) ====================
    public static class GradientHeader extends JPanel {
        private Color startColor;
        private Color endColor;
        
        public GradientHeader(Color start, Color end) {
            this.startColor = start;
            this.endColor = end;
            setLayout(new GridBagLayout());
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            int w = getWidth();
            int h = getHeight();
            
            // Gradient principal
            GradientPaint gradient = new GradientPaint(
                0, 0, startColor,
                w, h, endColor
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, w, h);
            
            // Cercles décoratifs translucides
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2.setColor(Color.WHITE);
            g2.fillOval(-50, -50, 200, 200);
            g2.fillOval(w - 150, h - 100, 200, 200);
            g2.fillOval(w / 2 - 75, -100, 150, 150);
            
            g2.dispose();
        }
    }
    
    // ==================== CARTE DE STATISTIQUES (StatCard) ====================
    public static class StatCard extends JPanel {
        private String title;
        private String value;
        private String icon;
        private Color accentColor;
        private int radius = 16;
        
        public StatCard(String icon, String title, String value, Color accent) {
            this.icon = icon;
            this.title = title;
            this.value = value;
            this.accentColor = accent;
            
            setOpaque(false);
            setPreferredSize(new Dimension(180, 120));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        public void setValue(String newValue) {
            this.value = newValue;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Ombre
            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRoundRect(3, 5, w - 6, h - 5, radius, radius);
            
            // Fond
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w - 1, h - 5, radius, radius);
            
            // Barre d'accent à gauche
            g2.setColor(accentColor);
            g2.fillRoundRect(0, 0, 5, h - 5, radius, radius);
            g2.fillRect(3, 0, 5, h - 5);
            
            // Icône
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            g2.setColor(accentColor);
            g2.drawString(icon, 20, 40);
            
            // Valeur
            g2.setFont(Fonts.HEADING_1);
            g2.setColor(Colors.TEXT_DARK);
            g2.drawString(value, 20, 80);
            
            // Titre
            g2.setFont(Fonts.CAPTION);
            g2.setColor(Colors.TEXT_MUTED);
            g2.drawString(title, 20, 100);
            
            g2.dispose();
        }
    }
    
    // ==================== BOUTON DE LA BARRE LATÉRALE (SidebarButton) ====================
    public static class SidebarButton extends JButton {
        private boolean isSelected = false;
        private Color normalBg = new Color(0, 0, 0, 0);
        private Color hoverBg = new Color(255, 255, 255, 20);
        private Color selectedBg = new Color(255, 255, 255, 30);
        private float hoverProgress = 0f;
        private Timer hoverTimer;
        
        public SidebarButton(String text) {
            super(text);
            setFont(Fonts.BODY);
            setForeground(new Color(255, 255, 255, 200));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(220, 50));
            setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 15));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    animateHover(true);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    animateHover(false);
                }
            });
        }
        
        private void animateHover(boolean entering) {
            if (hoverTimer != null) hoverTimer.stop();
            hoverTimer = new Timer(16, e -> {
                if (entering) hoverProgress = Math.min(1f, hoverProgress + 0.15f);
                else hoverProgress = Math.max(0f, hoverProgress - 0.15f);
                
                if ((entering && hoverProgress >= 1f) || (!entering && hoverProgress <= 0f)) {
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });
            hoverTimer.start();
        }
        
        public void setSelected(boolean selected) {
            this.isSelected = selected;
            setForeground(selected ? Color.WHITE : new Color(255, 255, 255, 200));
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Fond selon l'état (sélection / survol)
            if (isSelected) {
                g2.setColor(selectedBg);
                g2.fillRoundRect(10, 5, w - 20, h - 10, 10, 10);
                
                // Indicateur gauche
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, h / 2 - 10, 4, 20, 2, 2);
            } else if (hoverProgress > 0) {
                g2.setColor(new Color(255, 255, 255, (int)(20 * hoverProgress)));
                g2.fillRoundRect(10, 5, w - 20, h - 10, 10, 10);
            }
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================

    public static Color brighten(Color color, float factor) {
        int r = Math.min(255, (int) (color.getRed() * (1 + factor)));
        int g = Math.min(255, (int) (color.getGreen() * (1 + factor)));
        int b = Math.min(255, (int) (color.getBlue() * (1 + factor)));
        return new Color(r, g, b);
    }
    
    public static Color darken(Color color, float factor) {
        int r = Math.max(0, (int) (color.getRed() * (1 - factor)));
        int g = Math.max(0, (int) (color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int) (color.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }
    
    public static Color interpolateColor(Color c1, Color c2, float ratio) {
        int r = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
        int g = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
        int b = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
        return new Color(r, g, b);
    }
    
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    
    // Applique un style moderne à la scrollbar d'un JScrollPane
    public static void applyModernScrollBar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Colors.TEXT_LIGHT;
                this.trackColor = Colors.SURFACE;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, thumbBounds.width - 4, thumbBounds.height, 8, 8);
                g2.dispose();
            }
            
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }
        });
        
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
    }
}
