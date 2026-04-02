package GUI;

import GUI.ModernUIComponents.Colors;
import GUI.ModernUIComponents.Fonts;
import Models.*;
import Services.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import javax.swing.*;

/**
 * Notification Panel - Affiche les notifications du passager
 * Affiche jusqu'à 10 dernières notifications (récentes)
 */
public class NotificationPanel extends JPanel {
    
    private MainFrame mainFrame;
    private Gestion_covoiturage gestion;
    private String currentPassagerCIN;
    
    private JPanel notificationsContainer;
    private JLabel noNotificationsLabel;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int MAX_NOTIFICATIONS_DISPLAY = 10;
    
    public NotificationPanel(MainFrame mainFrame, Gestion_covoiturage gestion, String passagerCIN) {
        this.mainFrame = mainFrame;
        this.gestion = gestion;
        this.currentPassagerCIN = passagerCIN;
        
        setLayout(new BorderLayout());
        setBackground(Colors.SURFACE);
        
        initializeComponents();
        refreshNotifications();
    }
    
    private void initializeComponents() {
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Scrollable notifications
        JPanel scrollablePanel = new JPanel();
        scrollablePanel.setLayout(new BorderLayout());
        scrollablePanel.setBackground(Colors.SURFACE);
        
        notificationsContainer = new JPanel();
        notificationsContainer.setLayout(new BoxLayout(notificationsContainer, BoxLayout.Y_AXIS));
        notificationsContainer.setBackground(Colors.SURFACE);
        
        noNotificationsLabel = new JLabel("Aucune notification");
        noNotificationsLabel.setFont(Fonts.BODY);
        noNotificationsLabel.setForeground(Colors.TEXT_MUTED);
        noNotificationsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        noNotificationsLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        
        notificationsContainer.add(noNotificationsLabel);
        notificationsContainer.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(notificationsContainer);
        scrollPane.setBackground(Colors.SURFACE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        scrollablePanel.add(scrollPane, BorderLayout.CENTER);
        add(scrollablePanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Green gradient for passenger
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(39, 174, 96),
                    0, getHeight(), new Color(22, 160, 133)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Back button
        JButton backButton = new JButton("← Retour");
        backButton.setFont(Fonts.BUTTON);
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(Colors.ACCENT_MINT);
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        backButton.addActionListener(e -> {
            try {
                // Marquer toutes les notifications comme lues
                gestion.marquerToutesCommelues(currentPassagerCIN);
                // Retourner au dashboard
                mainFrame.showPassengerPanel();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        // Title
        JLabel titleLabel = new JLabel("📬 Notifications");
        titleLabel.setFont(Fonts.HEADING_2);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerPanel.add(backButton);
        headerPanel.add(Box.createHorizontalStrut(20));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createHorizontalGlue());
        
        return headerPanel;
    }
    
    /**
     * Rafraîchir l'affichage des notifications
     */
    public void refreshNotifications() {
        notificationsContainer.removeAll();
        
        // Récupérer les 10 dernières notifications
        Vector<Notification> notifications = gestion.getDernieresNotifications(currentPassagerCIN, MAX_NOTIFICATIONS_DISPLAY);
        
        if (notifications.isEmpty()) {
            notificationsContainer.add(noNotificationsLabel);
            notificationsContainer.add(Box.createVerticalGlue());
        } else {
            for (Notification notif : notifications) {
                notificationsContainer.add(createNotificationCard(notif));
                notificationsContainer.add(Box.createVerticalStrut(12));
            }
            notificationsContainer.add(Box.createVerticalGlue());
        }
        
        notificationsContainer.revalidate();
        notificationsContainer.repaint();
    }
    
    /**
     * Créer une carte pour une notification
     */
    private JPanel createNotificationCard(Notification notif) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Couleur en arrière-plan selon le statut
                Color bgColor = notif.isEstLue() ? new Color(245, 245, 245) : new Color(255, 252, 242);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Border gauche colorée selon le type
                Color borderColor = Color.decode(notif.getTypeColor());
                g2.setColor(borderColor);
                g2.fillRect(0, 0, 4, getHeight());
                
                // Border grise normal
                g2.setColor(new Color(220, 220, 220));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                
                g2.dispose();
            }
        };
        
        card.setPreferredSize(new Dimension(0, 100));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        card.setOpaque(false);
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Type et date
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setOpaque(false);
        
        JLabel typeLabel = new JLabel(notif.getTypeLabel());
        typeLabel.setFont(Fonts.HEADING_3);
        typeLabel.setForeground(Color.decode(notif.getTypeColor()));
        
        JLabel dateLabel = new JLabel(notif.getDateCreation().format(DATE_FORMATTER));
        dateLabel.setFont(Fonts.CAPTION);
        dateLabel.setForeground(Colors.TEXT_MUTED);
        
        topPanel.add(typeLabel);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(dateLabel);
        
        // Message
        JTextArea messageLabel = new JTextArea(notif.getMessage());
        messageLabel.setFont(Fonts.BODY);
        messageLabel.setForeground(Colors.TEXT_DARK);
        messageLabel.setWrapStyleWord(true);
        messageLabel.setLineWrap(true);
        messageLabel.setOpaque(false);
        messageLabel.setEditable(false);
        messageLabel.setBorder(null);
        
        contentPanel.add(topPanel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(messageLabel);
        
        // Status indicator
        JPanel statusPanel = new JPanel();
        statusPanel.setOpaque(false);
        statusPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        
        if (!notif.isEstLue()) {
            JLabel luIcon = new JLabel("• Non lue");
            luIcon.setFont(Fonts.CAPTION);
            luIcon.setForeground(Colors.ACCENT_CORAL);
            statusPanel.add(luIcon);
        }
        
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(statusPanel, BorderLayout.SOUTH);
        
        // Click to mark as read
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                gestion.marquerCommelue(currentPassagerCIN, notif.getNotificationId());
                refreshNotifications();
            }
        });
        
        return card;
    }
    
    /**
     * Mettre à jour le CIN du passager
     */
    public void setCurrentPassagerCIN(String cinPassager) {
        this.currentPassagerCIN = cinPassager;
        refreshNotifications();
    }
}
