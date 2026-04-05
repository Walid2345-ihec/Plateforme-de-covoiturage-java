package GUI;

import GUI.ModernUIComponents.Colors;
import GUI.ModernUIComponents.Fonts;
import Models.*;
import Services.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * MessagingPanel - Interface de messagerie privée entre passager et conducteur
 * Style Messenger de Meta avec messages à gauche/droite selon l'expéditeur
 */
public class MessagingPanel extends JPanel {
    
    private MainFrame mainFrame;
    private User currentUser;
    private User otherUser;
    private java.util.List<Message> messages;
    private JPanel messagesPanel;
    private JScrollPane messagesScrollPane;
    private JTextArea messageInputArea;
    private ModernUIComponents.RoundedButton sendBtn;
    
    private String conversationKey;
    private final int MESSAGE_BUBBLE_WIDTH = 400;
    
    public MessagingPanel(MainFrame mainFrame, User currentUser, User otherUser) {
        this.mainFrame = mainFrame;
        this.currentUser = currentUser;
        this.otherUser = otherUser;
        this.messages = new ArrayList<>();
        
        // Create conversation key for storing messages
        this.conversationKey = createConversationKey(currentUser.getCin(), otherUser.getCin());
        
        setLayout(new BorderLayout());
        setBackground(Colors.SURFACE);
        
        initializeComponents();
        loadMessages();
    }
    
    /**
     * Crée une clé unique pour la conversation
     */
    private String createConversationKey(String cin1, String cin2) {
        String[] cins = {cin1, cin2};
        Arrays.sort(cins);
        return cins[0] + "_" + cins[1];
    }
    
    /**
     * Initialiser les composants de l'interface
     */
    private void initializeComponents() {
        // Header
        add(createHeader(), BorderLayout.NORTH);
        
        // Messages panel
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(Colors.SURFACE);
        messagesPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        messagesScrollPane = new JScrollPane(messagesPanel);
        messagesScrollPane.setBackground(Colors.SURFACE);
        messagesScrollPane.getViewport().setBackground(Colors.SURFACE);
        messagesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        ModernUIComponents.applyModernScrollBar(messagesScrollPane);
        
        add(messagesScrollPane, BorderLayout.CENTER);
        
        // Input panel
        add(createInputPanel(), BorderLayout.SOUTH);
    }
    
    /**
     * Créer le header avec les informations de la conversation
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Colors.TEXT_LIGHT);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER));
        header.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        
        JLabel nameLabel = new JLabel(otherUser.getNom() + " " + otherUser.getPrenom());
        nameLabel.setFont(Fonts.HEADING_3);
        nameLabel.setForeground(Colors.TEXT_DARK);
        
        JLabel typeLabel = new JLabel(otherUser instanceof Conducteur ? "🚗 Conducteur" : "👤 Passager");
        typeLabel.setFont(Fonts.BODY);
        typeLabel.setForeground(Colors.TEXT_MUTED);
        
        infoPanel.add(nameLabel);
        infoPanel.add(typeLabel);
        
        header.add(infoPanel, BorderLayout.WEST);
        
        // Back button
        JButton backBtn = new JButton("← Retour");
        backBtn.setFont(Fonts.BODY);
        backBtn.setBackground(Colors.SURFACE);
        backBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> mainFrame.refreshCurrentPanel());
        
        header.add(backBtn, BorderLayout.EAST);
        
        return header;
    }
    
    /**
     * Créer le panel d'entrée de message
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.TEXT_LIGHT);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        // Text area
        messageInputArea = new JTextArea(3, 50);
        messageInputArea.setFont(Fonts.BODY);
        messageInputArea.setLineWrap(true);
        messageInputArea.setWrapStyleWord(true);
        messageInputArea.setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));
        messageInputArea.setBackground(Color.WHITE);
        
        JScrollPane inputScroll = new JScrollPane(messageInputArea);
        inputScroll.setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));
        
        panel.add(inputScroll, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        ModernUIComponents.RoundedButton cancelBtn = new ModernUIComponents.RoundedButton(
            "Annuler", Colors.TEXT_MUTED);
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.addActionListener(e -> {
            messageInputArea.setText("");
        });
        
        sendBtn = new ModernUIComponents.RoundedButton(
            "Envoyer", Colors.ACCENT_MINT);
        sendBtn.setPreferredSize(new Dimension(120, 40));
        sendBtn.addActionListener(e -> sendMessage());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(sendBtn);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Charger les messages de la conversation
     */
    private void loadMessages() {
        messages.clear();
        
        // Load all messages from CSV
        java.util.List<Message> allMessages = CSVDatabase.loadMessages();
        
        // Filter messages for this conversation
        for (Message msg : allMessages) {
            boolean isBetweenUsers = 
                (msg.getSenderCin().equals(currentUser.getCin()) && msg.getRecipientCin().equals(otherUser.getCin())) ||
                (msg.getSenderCin().equals(otherUser.getCin()) && msg.getRecipientCin().equals(currentUser.getCin()));
            
            if (isBetweenUsers) {
                messages.add(msg);
            }
        }
        
        // Sort messages par timestamp
        messages.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));
        
        // Display messages
        displayMessages();
    }
    
    /**
     * Afficher les messages dans le panel
     */
    private void displayMessages() {
        messagesPanel.removeAll();
        
        if (messages.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucun message. Commencez la conversation!");
            emptyLabel.setFont(Fonts.BODY);
            emptyLabel.setForeground(Colors.TEXT_MUTED);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagesPanel.add(Box.createVerticalStrut(40));
            messagesPanel.add(emptyLabel);
        } else {
            for (Message msg : messages) {
                messagesPanel.add(createMessageBubble(msg));
                messagesPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        messagesPanel.add(Box.createVerticalGlue());
        messagesPanel.revalidate();
        messagesPanel.repaint();
        
        // Scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = messagesScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    /**
     * Créer une bulle de message
     */
    private JPanel createMessageBubble(Message msg) {
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.X_AXIS));
        bubblePanel.setOpaque(false);
        bubblePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        boolean isSender = msg.getSenderCin().equals(currentUser.getCin());
        
        if (isSender) {
            // Message de l'utilisateur actuel - à droite
            bubblePanel.add(Box.createHorizontalGlue());
            
            JPanel messageContent = new JPanel();
            messageContent.setLayout(new BoxLayout(messageContent, BoxLayout.Y_AXIS));
            messageContent.setOpaque(false);
            messageContent.setMaximumSize(new Dimension(MESSAGE_BUBBLE_WIDTH, 120));
            
            // Delete button for sender's messages
            JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            buttonRow.setOpaque(false);
            
            JButton deleteBtn = new JButton("Supprimer");
            deleteBtn.setFont(new Font("Arial", Font.PLAIN, 10));
            deleteBtn.setPreferredSize(new Dimension(80, 20));
            deleteBtn.setBackground(Colors.ACCENT_CORAL);
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteBtn.addActionListener(e -> deleteMessage(msg));
            
            buttonRow.add(deleteBtn);
            messageContent.add(buttonRow);
            
            // Message bubble
            JPanel bubble = createBubbleStyle(msg.getContent(), Colors.ACCENT_MINT, Color.WHITE);
            messageContent.add(bubble);
            
            // Time label
            JLabel timeLabel = new JLabel(msg.getTimeOnly());
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timeLabel.setForeground(Colors.TEXT_MUTED);
            timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            messageContent.add(timeLabel);
            
            bubblePanel.add(messageContent);
            bubblePanel.add(Box.createHorizontalStrut(10));
        } else {
            // Message de l'autre utilisateur - à gauche
            bubblePanel.add(Box.createHorizontalStrut(10));
            
            JPanel messageContent = new JPanel();
            messageContent.setLayout(new BoxLayout(messageContent, BoxLayout.Y_AXIS));
            messageContent.setOpaque(false);
            messageContent.setMaximumSize(new Dimension(MESSAGE_BUBBLE_WIDTH, 120));
            
            // Sender name
            JLabel nameLabel = new JLabel(msg.getSenderName());
            nameLabel.setFont(Fonts.BODY_BOLD);
            nameLabel.setForeground(Colors.TEXT_DARK);
            messageContent.add(nameLabel);
            
            // Message bubble
            JPanel bubble = createBubbleStyle(msg.getContent(), Color.WHITE, Colors.BORDER);
            messageContent.add(bubble);
            
            // Time label
            JLabel timeLabel = new JLabel(msg.getTimeOnly());
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timeLabel.setForeground(Colors.TEXT_MUTED);
            messageContent.add(timeLabel);
            
            bubblePanel.add(messageContent);
            bubblePanel.add(Box.createHorizontalGlue());
        }
        
        return bubblePanel;
    }
    
    /**
     * Créer le style de la bulle de message
     */
    private JPanel createBubbleStyle(String content, Color bgColor, Color borderColor) {
        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw border
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        
        bubble.setLayout(new BorderLayout());
        bubble.setOpaque(false);
        bubble.setPreferredSize(new Dimension(300, 60));
        bubble.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        
        JTextArea messageArea = new JTextArea(content);
        messageArea.setFont(Fonts.BODY);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false);
        messageArea.setOpaque(false);
        messageArea.setBorder(BorderFactory.createEmptyBorder());
        messageArea.setForeground(bgColor.equals(Color.WHITE) ? Colors.TEXT_DARK : Color.WHITE);
        
        bubble.add(messageArea, BorderLayout.CENTER);
        
        return bubble;
    }
    
    /**
     * Envoyer un message
     */
    private void sendMessage() {
        String content = messageInputArea.getText().trim();
        
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un message!", 
                "Message vide", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Generate unique message ID
        String messageId = UUID.randomUUID().toString();
        
        // Create message object
        Message msg = new Message(
            messageId,
            currentUser.getCin(),
            currentUser.getNom() + " " + currentUser.getPrenom(),
            otherUser.getCin(),
            otherUser.getNom() + " " + otherUser.getPrenom(),
            content,
            ""  // trajet ID empty for now
        );
        
        messages.add(msg);
        
        // Save to CSV
        CSVDatabase.saveMessages(messages);
        
        // Create and save notification for message
        Gestion_covoiturage gestion = mainFrame.getGestion();
        if (currentUser instanceof Conducteur) {
            // Driver sent message to passenger - notify passenger
            gestion.creerNotificationMessageDuConducteur(otherUser.getCin(), currentUser.getCin(), content);
        } else if (currentUser instanceof Passager) {
            // Passenger sent message to driver - notify driver
            gestion.creerNotificationMessageDuPassager(otherUser.getCin(), currentUser.getCin(), content);
        }
        
        // Save notifications
        CSVDatabase.saveAllNotifications(gestion);
        
        // Clear input
        messageInputArea.setText("");
        
        // Refresh display
        displayMessages();
    }
    
    /**
     * Supprimer un message
     */
    private void deleteMessage(Message msg) {
        int result = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer ce message?",
            "Confirmation de suppression", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            msg.delete();
            
            // Save to CSV
            CSVDatabase.saveMessages(messages);
            
            // Refresh display
            displayMessages();
        }
    }
    
    /**
     * Setter pour la MainFrame
     */
    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
}
