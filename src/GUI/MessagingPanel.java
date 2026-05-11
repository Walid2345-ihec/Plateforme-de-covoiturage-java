package GUI;

import GUI.ModernUIComponents.Colors;
import GUI.ModernUIComponents.Fonts;
import Models.*;
import Services.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * Private messaging panel used for user/user and user/admin conversations.
 */
public class MessagingPanel extends JPanel {

    private MainFrame mainFrame;
    private final User currentUser;
    private final User otherUser;
    private final java.util.List<Message> messages;
    private JPanel messagesPanel;
    private JScrollPane messagesScrollPane;
    private JTextArea messageInputArea;
    private javax.swing.Timer refreshTimer;
    private final Runnable backAction;

    private final int MESSAGE_BUBBLE_WIDTH = 400;

    public MessagingPanel(MainFrame mainFrame, User currentUser, User otherUser) {
        this(mainFrame, currentUser, otherUser, null);
    }

    public MessagingPanel(MainFrame mainFrame, User currentUser, User otherUser, Runnable backAction) {
        this.mainFrame = mainFrame;
        this.currentUser = currentUser;
        this.otherUser = otherUser;
        this.backAction = backAction;
        this.messages = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(Colors.SURFACE);

        initializeComponents();
        loadMessages();
        startPolling();
    }

    private void initializeComponents() {
        add(createHeader(), BorderLayout.NORTH);

        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(Colors.SURFACE);
        messagesPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        messagesScrollPane = new JScrollPane(messagesPanel);
        messagesScrollPane.setBackground(Colors.SURFACE);
        messagesScrollPane.getViewport().setBackground(Colors.SURFACE);
        messagesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        messagesScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        ModernUIComponents.applyModernScrollBar(messagesScrollPane);

        add(messagesScrollPane, BorderLayout.CENTER);
        add(createInputPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Colors.TEXT_LIGHT);
        header.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(otherUser.getNom() + " " + otherUser.getPrenom());
        nameLabel.setFont(Fonts.HEADING_3);
        nameLabel.setForeground(Colors.TEXT_DARK);

        String typeText = otherUser instanceof Admin ? "Administrateur"
            : otherUser instanceof Conducteur ? "Conducteur" : "Passager";
        JLabel typeLabel = new JLabel(typeText);
        typeLabel.setFont(Fonts.BODY);
        typeLabel.setForeground(Colors.TEXT_MUTED);

        infoPanel.add(nameLabel);
        infoPanel.add(typeLabel);
        header.add(infoPanel, BorderLayout.WEST);

        JButton backBtn = new JButton("Retour");
        backBtn.setFont(Fonts.BODY);
        backBtn.setBackground(Colors.SURFACE);
        backBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            stopPolling();
            if (backAction != null) {
                backAction.run();
            } else {
                mainFrame.refreshCurrentPanel();
            }
        });
        header.add(backBtn, BorderLayout.EAST);

        return header;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.TEXT_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        messageInputArea = new JTextArea(3, 50);
        messageInputArea.setFont(Fonts.BODY);
        messageInputArea.setLineWrap(true);
        messageInputArea.setWrapStyleWord(true);
        messageInputArea.setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));
        messageInputArea.setBackground(Color.WHITE);

        JScrollPane inputScroll = new JScrollPane(messageInputArea);
        inputScroll.setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));
        panel.add(inputScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        ModernUIComponents.RoundedButton cancelBtn = new ModernUIComponents.RoundedButton("Annuler", Colors.TEXT_MUTED);
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.addActionListener(e -> messageInputArea.setText(""));

        ModernUIComponents.RoundedButton sendBtn = new ModernUIComponents.RoundedButton("Envoyer", Colors.ACCENT_MINT);
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

    private void loadMessages() {
        messages.clear();
        messages.addAll(filterConversationMessages(CSVDatabase.loadMessages()));
        displayMessages();
    }

    private java.util.List<Message> filterConversationMessages(java.util.List<Message> allMessages) {
        java.util.List<Message> filtered = new ArrayList<>();
        for (Message msg : allMessages) {
            boolean isBetweenUsers =
                (msg.getSenderCin().equals(currentUser.getCin()) && msg.getRecipientCin().equals(otherUser.getCin())) ||
                (msg.getSenderCin().equals(otherUser.getCin()) && msg.getRecipientCin().equals(currentUser.getCin()));

            if (isBetweenUsers) {
                filtered.add(msg);
            }
        }
        filtered.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));
        return filtered;
    }

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

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = messagesScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private JPanel createMessageBubble(Message msg) {
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.X_AXIS));
        bubblePanel.setOpaque(false);
        bubblePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        boolean isSender = msg.getSenderCin().equals(currentUser.getCin());

        if (isSender) {
            bubblePanel.add(Box.createHorizontalGlue());
            bubblePanel.add(createMessageContent(msg, true));
            bubblePanel.add(Box.createHorizontalStrut(10));
        } else {
            bubblePanel.add(Box.createHorizontalStrut(10));
            bubblePanel.add(createMessageContent(msg, false));
            bubblePanel.add(Box.createHorizontalGlue());
        }

        return bubblePanel;
    }

    private JPanel createMessageContent(Message msg, boolean isSender) {
        JPanel messageContent = new JPanel();
        messageContent.setLayout(new BoxLayout(messageContent, BoxLayout.Y_AXIS));
        messageContent.setOpaque(false);
        messageContent.setMaximumSize(new Dimension(MESSAGE_BUBBLE_WIDTH, 130));

        if (isSender && !msg.isDeleted()) {
            JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            buttonRow.setOpaque(false);
            JButton deleteBtn = new JButton("Supprimer");
            deleteBtn.setFont(new Font("Arial", Font.PLAIN, 10));
            deleteBtn.setPreferredSize(new Dimension(90, 20));
            deleteBtn.setBackground(Colors.ACCENT_CORAL);
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteBtn.addActionListener(e -> deleteMessage(msg));
            buttonRow.add(deleteBtn);
            messageContent.add(buttonRow);
        } else if (!isSender) {
            JLabel nameLabel = new JLabel(msg.getSenderName());
            nameLabel.setFont(Fonts.BODY_BOLD);
            nameLabel.setForeground(Colors.TEXT_DARK);
            messageContent.add(nameLabel);
        }

        Color bubbleColor = isSender ? Colors.ACCENT_MINT : Color.WHITE;
        Color borderColor = isSender ? Color.WHITE : Colors.BORDER;
        messageContent.add(createBubbleStyle(msg.getContent(), bubbleColor, borderColor));

        JLabel timeLabel = new JLabel(msg.getTimeOnly());
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        timeLabel.setForeground(Colors.TEXT_MUTED);
        timeLabel.setAlignmentX(isSender ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        messageContent.add(timeLabel);

        return messageContent;
    }

    private JPanel createBubbleStyle(String content, Color bgColor, Color borderColor) {
        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
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
        if ("Message supprimé".equals(content)) {
            messageArea.setForeground(Colors.TEXT_MUTED);
            messageArea.setFont(Fonts.CAPTION);
        }

        bubble.add(messageArea, BorderLayout.CENTER);
        return bubble;
    }

    private void sendMessage() {
        String content = messageInputArea.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un message!",
                "Message vide", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Message msg = new Message(
            UUID.randomUUID().toString(),
            currentUser.getCin(),
            currentUser.getNom() + " " + currentUser.getPrenom(),
            otherUser.getCin(),
            otherUser.getNom() + " " + otherUser.getPrenom(),
            content,
            ""
        );

        java.util.List<Message> allMessages = CSVDatabase.loadMessages();
        allMessages.add(msg);
        CSVDatabase.saveMessages(allMessages);

        Gestion_covoiturage gestion = mainFrame.getGestion();
        if (currentUser instanceof Conducteur && otherUser instanceof Passager) {
            gestion.creerNotificationMessageDuConducteur(otherUser.getCin(), currentUser.getCin(), content);
            CSVDatabase.saveAllNotifications(gestion);
        } else if (currentUser instanceof Passager && otherUser instanceof Conducteur) {
            gestion.creerNotificationMessageDuPassager(otherUser.getCin(), currentUser.getCin(), content);
            CSVDatabase.saveAllNotifications(gestion);
        } else if (currentUser instanceof Admin) {
            gestion.creerNotificationMessageAdmin(otherUser.getCin(), currentUser.getCin(), content);
            CSVDatabase.saveAllNotifications(gestion);
        }

        mainFrame.saveDataToCSV();
        messageInputArea.setText("");
        loadMessages();
    }

    private void deleteMessage(Message msg) {
        int result = JOptionPane.showConfirmDialog(this,
            "Etes-vous sur de vouloir supprimer ce message?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            java.util.List<Message> allMessages = CSVDatabase.loadMessages();
            for (Message stored : allMessages) {
                if (stored.getMessageId().equals(msg.getMessageId())
                        && stored.getSenderCin().equals(currentUser.getCin())) {
                    stored.delete();
                    break;
                }
            }
            CSVDatabase.saveMessages(allMessages);
            mainFrame.saveDataToCSV();
            loadMessages();
        }
    }

    private void startPolling() {
        refreshTimer = new javax.swing.Timer(5000, e -> loadMessages());
        refreshTimer.start();
    }

    private void stopPolling() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }

    @Override
    public void removeNotify() {
        stopPolling();
        super.removeNotify();
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }
}
