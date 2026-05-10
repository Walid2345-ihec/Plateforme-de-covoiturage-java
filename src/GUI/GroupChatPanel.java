package GUI;

import GUI.ModernUIComponents.Colors;
import GUI.ModernUIComponents.Fonts;
import Models.*;
import Services.*;
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * GroupChatPanel - Page de discussion d'un groupe.
 * Style Messenger : message du locuteur courant à droite, des autres à gauche.
 * Permet d'écrire, lire et supprimer ses propres messages.
 */
public class GroupChatPanel extends JPanel {

    private final MainFrame mainFrame;
    private final Gestion_covoiturage gestion;
    private final User currentUser;
    private final Group group;

    private JPanel messagesPanel;
    private JScrollPane messagesScrollPane;
    private JTextArea messageInputArea;

    private static final int MESSAGE_BUBBLE_WIDTH = 420;

    public GroupChatPanel(MainFrame mainFrame, Gestion_covoiturage gestion,
                          User currentUser, Group group) {
        this.mainFrame = mainFrame;
        this.gestion = gestion;
        this.currentUser = currentUser;
        this.group = group;

        setLayout(new BorderLayout());
        setBackground(Colors.SURFACE);

        initializeComponents();
        loadAndDisplayMessages();
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
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("👥 " + group.getGroupName());
        nameLabel.setFont(Fonts.HEADING_3);
        nameLabel.setForeground(Colors.TEXT_DARK);

        int memberCount = group.getMemberCins().size() + 1;
        JLabel memberLabel = new JLabel(memberCount + " membres • Discussion de groupe");
        memberLabel.setFont(Fonts.CAPTION);
        memberLabel.setForeground(Colors.TEXT_MUTED);

        infoPanel.add(nameLabel);
        infoPanel.add(memberLabel);

        header.add(infoPanel, BorderLayout.WEST);

        JButton backBtn = new JButton("← Retour");
        backBtn.setFont(Fonts.BODY);
        backBtn.setBackground(Colors.SURFACE);
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> mainFrame.showGroupsPanel());

        header.add(backBtn, BorderLayout.EAST);

        return header;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));

        messageInputArea = new JTextArea(3, 50);
        messageInputArea.setFont(Fonts.BODY);
        messageInputArea.setLineWrap(true);
        messageInputArea.setWrapStyleWord(true);

        JScrollPane inputScroll = new JScrollPane(messageInputArea);
        inputScroll.setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));

        panel.add(inputScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        ModernUIComponents.RoundedButton cancelBtn = new ModernUIComponents.RoundedButton(
                "Effacer", Colors.TEXT_MUTED);
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.addActionListener(e -> messageInputArea.setText(""));

        ModernUIComponents.RoundedButton sendBtn = new ModernUIComponents.RoundedButton(
                "Envoyer", Colors.ACCENT_MINT);
        sendBtn.setPreferredSize(new Dimension(120, 40));
        sendBtn.addActionListener(e -> sendMessage());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(sendBtn);

        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private void loadAndDisplayMessages() {
        messagesPanel.removeAll();

        List<GroupMessage> messages = gestion.getMessagesPourGroupe(group.getGroupId());

        if (messages.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucun message. Soyez le premier à écrire !");
            emptyLabel.setFont(Fonts.BODY);
            emptyLabel.setForeground(Colors.TEXT_MUTED);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagesPanel.add(Box.createVerticalStrut(40));
            messagesPanel.add(emptyLabel);
        } else {
            for (GroupMessage m : messages) {
                messagesPanel.add(createMessageBubble(m));
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

    private JPanel createMessageBubble(GroupMessage msg) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        boolean isSender = msg.getSenderCin().equals(currentUser.getCin());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setMaximumSize(new Dimension(MESSAGE_BUBBLE_WIDTH, 150));

        if (isSender) {
            // À droite : messages de l'utilisateur courant
            row.add(Box.createHorizontalGlue());

            // Bouton Supprimer (uniquement sur ses propres messages, et si pas déjà supprimé)
            if (!msg.isDeleted()) {
                JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                actionRow.setOpaque(false);
                JButton deleteBtn = new JButton("Supprimer");
                deleteBtn.setFont(new Font("Arial", Font.PLAIN, 10));
                deleteBtn.setPreferredSize(new Dimension(85, 22));
                deleteBtn.setBackground(Colors.ACCENT_CORAL);
                deleteBtn.setForeground(Color.WHITE);
                deleteBtn.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                deleteBtn.setFocusPainted(false);
                deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                deleteBtn.addActionListener(e -> deleteMessage(msg));
                actionRow.add(deleteBtn);
                content.add(actionRow);
            }

            JPanel bubble = createBubbleStyle(msg.getContent(), Colors.ACCENT_MINT, true,
                    msg.isDeleted());
            content.add(bubble);

            JLabel timeLabel = new JLabel(msg.getTimeOnly());
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timeLabel.setForeground(Colors.TEXT_MUTED);
            timeLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            content.add(timeLabel);

            row.add(content);
            row.add(Box.createHorizontalStrut(10));
        } else {
            // À gauche : messages des autres membres
            row.add(Box.createHorizontalStrut(10));

            JLabel nameLabel = new JLabel(msg.getSenderName());
            nameLabel.setFont(Fonts.BODY_BOLD);
            nameLabel.setForeground(Colors.TEXT_DARK);
            content.add(nameLabel);

            JPanel bubble = createBubbleStyle(msg.getContent(), Color.WHITE, false, msg.isDeleted());
            content.add(bubble);

            JLabel timeLabel = new JLabel(msg.getTimeOnly());
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timeLabel.setForeground(Colors.TEXT_MUTED);
            content.add(timeLabel);

            row.add(content);
            row.add(Box.createHorizontalGlue());
        }

        return row;
    }

    private JPanel createBubbleStyle(String content, Color bgColor, boolean isOwn, boolean isDeleted) {
        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isDeleted ? new Color(220, 220, 220) : bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Colors.BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        bubble.setLayout(new BorderLayout());
        bubble.setOpaque(false);
        bubble.setPreferredSize(new Dimension(320, 60));
        bubble.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JTextArea area = new JTextArea(content);
        area.setFont(isDeleted ? Fonts.CAPTION : Fonts.BODY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setOpaque(false);
        area.setBorder(BorderFactory.createEmptyBorder());
        if (isDeleted) {
            area.setForeground(Colors.TEXT_MUTED);
        } else {
            area.setForeground(isOwn ? Color.WHITE : Colors.TEXT_DARK);
        }

        bubble.add(area, BorderLayout.CENTER);
        return bubble;
    }

    private void sendMessage() {
        String content = messageInputArea.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un message.",
                    "Message vide", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String senderName = currentUser.getNom() + " " + currentUser.getPrenom();
        GroupMessage created = gestion.envoyerMessageDeGroupe(
                group.getGroupId(),
                currentUser.getCin(),
                senderName,
                content);

        if (created == null) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'envoi du message.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Persister sur disque
        CSVDatabase.saveGroupMessages(gestion.getGroupMessages());
        CSVDatabase.saveAllNotifications(gestion);

        messageInputArea.setText("");
        loadAndDisplayMessages();
        mainFrame.markUnsavedChanges();
    }

    private void deleteMessage(GroupMessage msg) {
        int result = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer ce message ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            boolean ok = gestion.supprimerMessageDeGroupe(msg.getMessageId(), currentUser.getCin());
            if (ok) {
                CSVDatabase.saveGroupMessages(gestion.getGroupMessages());
                loadAndDisplayMessages();
                mainFrame.markUnsavedChanges();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vous ne pouvez supprimer que vos propres messages.",
                        "Action refusée", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
