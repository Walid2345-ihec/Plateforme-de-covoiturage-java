package GUI;

import GUI.ModernUIComponents.Colors;
import GUI.ModernUIComponents.Fonts;
import Models.*;
import Services.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

/**
 * AdminPanel - Administration interface for managing users and rides.
 */
public class AdminPanel extends JPanel {
    private final MainFrame mainFrame;
    private JPanel contentPanel;
    private CardLayout contentLayout;

    // Stat cards
    private ModernUIComponents.StatCard usersStatCard;
    private ModernUIComponents.StatCard trajetsStatCard;
    private ModernUIComponents.StatCard evaluationsStatCard;

    // Tables
    private JTable driversTable;
    private DefaultTableModel driversModel;
    private JTable passengersTable;
    private DefaultTableModel passengersModel;
    private JTable trajetsTable;
    private DefaultTableModel trajetsModel;

    private JLabel adminNotificationBadge;
    private JPopupMenu adminNotificationsPopup;
    private JPanel adminNotificationsList;
    private Timer adminNotificationsTimer;
    private Timer conversationsTimer;
    private JPanel conversationsListPanel;
    private JPanel adminChatHolder;
    private Conversation selectedConversation;

    private final java.util.List<ModernUIComponents.SidebarButton> sidebarButtons = new java.util.ArrayList<>();
    private static final DateTimeFormatter NOTIFICATION_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public AdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Colors.SURFACE);
        initializeComponents();
        startAdminNotificationsTimer();
    }

    private void initializeComponents() {
        // Sidebar
        add(createSidebar(), BorderLayout.WEST);

        // Content area
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(Colors.SURFACE);

        contentPanel.add(createDashboardView(), "DASHBOARD");
        contentPanel.add(createUsersView(), "USERS");
        contentPanel.add(createTrajetsView(), "TRAJETS");
        contentPanel.add(createEvaluationsView(), "EVALUATIONS");
        contentPanel.add(createConversationsView(), "CONVERSATIONS");

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(30, 40, 60),
                    0, getHeight(), new Color(40, 50, 80)
                );
                g2.setPaint(gradient);
                g2
                .fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        headerPanel.setMaximumSize(new Dimension(250, 150));

        JLabel avatarLabel = new JLabel("🛠️");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Administration");
        titleLabel.setFont(Fonts.HEADING_3);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Gestion du système");
        subtitleLabel.setFont(Fonts.CAPTION);
        subtitleLabel.setForeground(new Color(255, 255, 255, 150));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(avatarLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        sidebar.add(headerPanel);
        sidebar.add(Box.createVerticalStrut(20));

        addSidebarButton(sidebar, "Tableau de Bord", "DASHBOARD", true);
        addSidebarButton(sidebar, "Gestion Utilisateurs", "USERS", false);
        addSidebarButton(sidebar, "Gestion Trajets", "TRAJETS", false);
        addSidebarButton(sidebar, "Évaluations Globales", "EVALUATIONS", false);

        addSidebarButton(sidebar, "Conversations", "CONVERSATIONS", false);

        sidebar.add(Box.createVerticalGlue());

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setOpaque(false);
        logoutPanel.setMaximumSize(new Dimension(250, 80));
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        ModernUIComponents.RoundedButton logoutBtn = new ModernUIComponents.RoundedButton(
            "Déconnexion", Colors.ACCENT_CORAL);
        logoutBtn.setPreferredSize(new Dimension(180, 42));
        logoutBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Voulez-vous vous déconnecter ?",
                "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                mainFrame.showLogin();
            }
        });
        logoutPanel.add(logoutBtn);
        sidebar.add(logoutPanel);

        return sidebar;
    }

    private void addSidebarButton(JPanel sidebar, String text, String cardName, boolean selected) {
        ModernUIComponents.SidebarButton button = new ModernUIComponents.SidebarButton(text);
        button.setSelected(selected);
        button.setMaximumSize(new Dimension(250, 50));
        button.addActionListener(e -> {
            for (ModernUIComponents.SidebarButton btn : sidebarButtons) {
                btn.setSelected(false);
            }
            button.setSelected(true);
            contentLayout.show(contentPanel, cardName);
            if (cardName.equals("DASHBOARD")) refreshDashboard();
            if (cardName.equals("USERS")) refreshUsersTable();
            if (cardName.equals("TRAJETS")) refreshTrajetsTable();
            if (cardName.equals("CONVERSATIONS")) refreshConversationsList();
        });
        sidebarButtons.add(button);
        sidebar.add(button);
    }

    private JPanel createDashboardView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Tableau de Bord Admin");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(createAdminNotificationBell(), BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        usersStatCard = new ModernUIComponents.StatCard("", "Utilisateurs Totaux", "0", Colors.PRIMARY_START);
        trajetsStatCard = new ModernUIComponents.StatCard("", "Trajets Actifs", "0", Colors.ACCENT_MINT);
        evaluationsStatCard = new ModernUIComponents.StatCard("", "Évaluations", "0", Colors.ACCENT_GOLD);

        statsPanel.add(usersStatCard);
        statsPanel.add(trajetsStatCard);
        statsPanel.add(evaluationsStatCard);

        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAdminNotificationBell() {
        JPanel wrapper = new JPanel(null);
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(70, 50));

        JButton bellButton = new JButton("\uD83D\uDD14");
        bellButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        bellButton.setBounds(5, 5, 44, 40);
        bellButton.setFocusPainted(false);
        bellButton.setBorderPainted(false);
        bellButton.setContentAreaFilled(false);
        bellButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bellButton.setToolTipText("Notifications administrateur");
        bellButton.addActionListener(e -> showAdminNotificationsPopup(bellButton));

        adminNotificationBadge = new JLabel("0") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Colors.ACCENT_CORAL);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        adminNotificationBadge.setBounds(38, 2, 24, 24);
        adminNotificationBadge.setHorizontalAlignment(SwingConstants.CENTER);

        wrapper.add(bellButton);
        wrapper.add(adminNotificationBadge);
        updateAdminNotificationBadge();
        return wrapper;
    }

    private void showAdminNotificationsPopup(Component invoker) {
        adminNotificationsPopup = new JPopupMenu();
        adminNotificationsPopup.setBorder(BorderFactory.createLineBorder(Colors.BORDER));

        JPanel popupContent = new JPanel(new BorderLayout());
        popupContent.setBackground(Color.WHITE);
        popupContent.setPreferredSize(new Dimension(440, 520));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(14, 16, 10, 16));

        JLabel title = new JLabel("Notifications admin");
        title.setFont(Fonts.HEADING_3);
        title.setForeground(Colors.TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        JButton markAllButton = new JButton("Tout lire");
        markAllButton.setFont(Fonts.CAPTION);
        markAllButton.setFocusPainted(false);
        markAllButton.addActionListener(e -> {
            mainFrame.getGestion().markAllAdminNotificationsAsRead();
            mainFrame.markUnsavedChanges();
            refreshAdminNotificationsPopup();
            updateAdminNotificationBadge();
        });
        header.add(markAllButton, BorderLayout.EAST);

        adminNotificationsList = new JPanel();
        adminNotificationsList.setBackground(Color.WHITE);
        adminNotificationsList.setLayout(new BoxLayout(adminNotificationsList, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(adminNotificationsList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        ModernUIComponents.applyModernScrollBar(scrollPane);

        popupContent.add(header, BorderLayout.NORTH);
        popupContent.add(scrollPane, BorderLayout.CENTER);
        adminNotificationsPopup.add(popupContent);

        refreshAdminNotificationsPopup();
        adminNotificationsPopup.show(invoker, -380, invoker.getHeight() + 5);
    }

    private void refreshAdminNotificationsPopup() {
        if (adminNotificationsList == null) return;

        adminNotificationsList.removeAll();
        java.util.List<Notification> notifications = mainFrame.getGestion().getAdminNotifications();
        notifications.sort((n1, n2) -> n2.getDateCreation().compareTo(n1.getDateCreation()));

        if (notifications.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucune notification administrateur");
            emptyLabel.setFont(Fonts.BODY);
            emptyLabel.setForeground(Colors.TEXT_MUTED);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(30, 16, 30, 16));
            adminNotificationsList.add(emptyLabel);
        } else {
            for (Notification notification : notifications) {
                adminNotificationsList.add(createAdminNotificationCard(notification));
                adminNotificationsList.add(Box.createVerticalStrut(8));
            }
        }

        adminNotificationsList.revalidate();
        adminNotificationsList.repaint();
    }

    private JPanel createAdminNotificationCard(Notification notification) {
        JPanel card = new JPanel(new BorderLayout(10, 8));
        card.setBackground(notification.isEstLue() ? new Color(247, 248, 250) : new Color(255, 252, 242));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, Color.decode(notification.getTypeColor())),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 118));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel typeLabel = new JLabel(notification.getTypeLabel());
        typeLabel.setFont(Fonts.BODY_BOLD);
        typeLabel.setForeground(Color.decode(notification.getTypeColor()));

        JLabel dateLabel = new JLabel(notification.getDateCreation().format(NOTIFICATION_DATE_FORMATTER));
        dateLabel.setFont(Fonts.CAPTION);
        dateLabel.setForeground(Colors.TEXT_MUTED);

        top.add(typeLabel, BorderLayout.WEST);
        top.add(dateLabel, BorderLayout.EAST);

        JTextArea messageArea = new JTextArea(notification.getMessage());
        messageArea.setFont(Fonts.BODY);
        messageArea.setForeground(Colors.TEXT_DARK);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false);
        messageArea.setOpaque(false);
        messageArea.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        textPanel.add(top);
        textPanel.add(messageArea);

        JButton deleteButton = new JButton("x");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.setFocusPainted(false);
        deleteButton.setMargin(new Insets(2, 7, 2, 7));
        deleteButton.addActionListener(e -> {
            mainFrame.getGestion().deleteAdminNotification(notification.getNotificationId());
            mainFrame.markUnsavedChanges();
            refreshAdminNotificationsPopup();
            updateAdminNotificationBadge();
        });

        card.add(textPanel, BorderLayout.CENTER);
        card.add(deleteButton, BorderLayout.EAST);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainFrame.getGestion().markAdminNotificationAsRead(notification.getNotificationId());
                mainFrame.markUnsavedChanges();
                refreshAdminNotificationsPopup();
                updateAdminNotificationBadge();
            }
        });

        return card;
    }

    private void updateAdminNotificationBadge() {
        if (adminNotificationBadge == null) return;

        int unreadCount = mainFrame.getGestion().countUnreadAdminNotifications();
        if (unreadCount > 0) {
            adminNotificationBadge.setText(String.valueOf(Math.min(unreadCount, 99)));
            adminNotificationBadge.setVisible(true);
        } else {
            adminNotificationBadge.setVisible(false);
        }
        adminNotificationBadge.repaint();
    }

    private void startAdminNotificationsTimer() {
        adminNotificationsTimer = new Timer(2000, e -> updateAdminNotificationBadge());
        adminNotificationsTimer.start();
    }

    private JPanel createUsersView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Gestion des Utilisateurs");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"CIN", "Nom", "Prénom", "Email", "Carte"};

        // Conducteurs Section
        driversModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        driversTable = createModernTable(driversModel);
        JScrollPane driverScroll = new JScrollPane(driversTable);
        driverScroll.setBorder(BorderFactory.createTitledBorder("Conducteurs"));
        ModernUIComponents.applyModernScrollBar(driverScroll);

        // Passagers Section
        passengersModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        passengersTable = createModernTable(passengersModel);
        JScrollPane passengerScroll = new JScrollPane(passengersTable);
        passengerScroll.setBorder(BorderFactory.createTitledBorder("Passagers"));
        ModernUIComponents.applyModernScrollBar(passengerScroll);

        // Vertical container for both tables
        JPanel tablesPanel = new JPanel();
        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));
        tablesPanel.setOpaque(false);
        tablesPanel.add(new JLabel("Listes des Utilisateurs"));
        tablesPanel.add(Box.createVerticalStrut(10));
        tablesPanel.add(driverScroll);
        tablesPanel.add(Box.createVerticalStrut(20));
        tablesPanel.add(passengerScroll);

        JScrollPane mainScroll = new JScrollPane(tablesPanel);
        mainScroll.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainScroll.setOpaque(false);
        mainScroll
            .getViewport().setBackground(Colors.SURFACE);
        ModernUIComponents.applyModernScrollBar(mainScroll);
        panel.add(mainScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);

        ModernUIComponents.RoundedButton deleteBtn = new ModernUIComponents.RoundedButton("Bannir/Supprimer", Colors.ACCENT_CORAL);
        deleteBtn.setPreferredSize(new Dimension(180, 42));
        deleteBtn.addActionListener(e -> deleteUser());

        ModernUIComponents.RoundedButton refreshBtn = new ModernUIComponents.RoundedButton("Actualiser", Colors.TEXT_MUTED);
        refreshBtn.setPreferredSize(new Dimension(130, 42));
        refreshBtn.addActionListener(e -> refreshUsersTable());

        JComboBox<String> cardCombo = new JComboBox<>(new String[]{"Verte", "Jaune", "Rouge"});
        cardCombo.setPreferredSize(new Dimension(120, 42));

        ModernUIComponents.RoundedButton cardBtn = new ModernUIComponents.RoundedButton("Changer Carte", Colors.ACCENT_GOLD);
        cardBtn.setPreferredSize(new Dimension(160, 42));
        cardBtn.addActionListener(e -> changeSelectedUserCard((String) cardCombo.getSelectedItem()));

        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(cardCombo);
        buttonPanel.add(cardBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTrajetsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Surveillance des Trajets");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Départ", "Arrivée", "Prix", "Statut", "Conducteur"};
        trajetsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        trajetsTable = createModernTable(trajetsModel);

        JScrollPane scrollPane = new JScrollPane(trajetsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        ModernUIComponents.applyModernScrollBar(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setOpaque(false);

        ModernUIComponents.RoundedButton deleteBtn = new ModernUIComponents.RoundedButton("Supprimer Trajet", Colors.ACCENT_CORAL);
        deleteBtn.setPreferredSize(new Dimension(180, 42));
        deleteBtn.addActionListener(e -> deleteTrajet());

        ModernUIComponents.RoundedButton refreshBtn = new ModernUIComponents.RoundedButton("Actualiser", Colors.TEXT_MUTED);
        refreshBtn.setPreferredSize(new Dimension(130, 42));
        refreshBtn.addActionListener(e -> refreshTrajetsTable());

        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEvaluationsView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Évaluations Globales");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Passager", "Conducteur", "Note", "Commentaire"};
        DefaultTableModel evalModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable evalTable = createModernTable(evalModel);

        JScrollPane scrollPane = new JScrollPane(evalTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        ModernUIComponents.applyModernScrollBar(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Populate evaluations
        List<Evaluation> allEvals = mainFrame.getGestion().getAllEvaluations();
        for (Evaluation e : allEvals) {
            evalModel.addRow(new Object[]{e.getPassagerName(), e.getConducteurCin(), e.getRating(), e.getComment()});
        }

        return panel;
    }

    private JPanel createConversationsView() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        conversationsListPanel = new JPanel();
        conversationsListPanel.setLayout(new BoxLayout(conversationsListPanel, BoxLayout.Y_AXIS));
        conversationsListPanel.setBackground(Color.WHITE);

        JScrollPane listScroll = new JScrollPane(conversationsListPanel);
        listScroll.setPreferredSize(new Dimension(330, 0));
        listScroll.setBorder(BorderFactory.createTitledBorder("Conversations actives"));
        ModernUIComponents.applyModernScrollBar(listScroll);

        adminChatHolder = new JPanel(new BorderLayout());
        adminChatHolder.setBackground(Colors.SURFACE);
        JLabel empty = new JLabel("Selectionnez une conversation");
        empty.setFont(Fonts.BODY);
        empty.setForeground(Colors.TEXT_MUTED);
        empty.setHorizontalAlignment(SwingConstants.CENTER);
        adminChatHolder.add(empty, BorderLayout.CENTER);

        panel.add(listScroll, BorderLayout.WEST);
        panel.add(adminChatHolder, BorderLayout.CENTER);

        conversationsTimer = new Timer(5000, e -> refreshConversationsList());
        conversationsTimer.start();
        refreshConversationsList();
        return panel;
    }

    private void refreshConversationsList() {
        if (conversationsListPanel == null) return;

        conversationsListPanel.removeAll();
        java.util.List<Conversation> conversations = mainFrame.getGestion().getConversations();
        conversations.sort((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()));

        if (conversations.isEmpty()) {
            JLabel empty = new JLabel("Aucune conversation");
            empty.setFont(Fonts.BODY);
            empty.setForeground(Colors.TEXT_MUTED);
            empty.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
            conversationsListPanel.add(empty);
        } else {
            for (Conversation conversation : conversations) {
                conversationsListPanel.add(createConversationRow(conversation));
                conversationsListPanel.add(Box.createVerticalStrut(6));
            }
        }

        conversationsListPanel.revalidate();
        conversationsListPanel.repaint();
    }

    private JPanel createConversationRow(Conversation conversation) {
        User user = mainFrame.getGestion().rechercher_user(conversation.getUserId());
        String userName = user != null ? user.getNom() + " " + user.getPrenom() : conversation.getUserId();

        JPanel row = new JPanel(new BorderLayout(8, 4));
        row.setBackground(conversation == selectedConversation ? new Color(232, 240, 254) : Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colors.BORDER),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel name = new JLabel(userName);
        name.setFont(Fonts.BODY_BOLD);
        name.setForeground(Colors.TEXT_DARK);

        JLabel meta = new JLabel(conversation.getTriggeredBy() + " - " + conversation.getCreatedAtAsString());
        meta.setFont(Fonts.CAPTION);
        meta.setForeground(Colors.TEXT_MUTED);

        row.add(name, BorderLayout.NORTH);
        row.add(meta, BorderLayout.CENTER);
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openAdminConversation(conversation);
            }
        });

        return row;
    }

    private void openAdminConversation(Conversation conversation) {
        Admin admin = mainFrame.getCurrentUser() instanceof Admin
            ? (Admin) mainFrame.getCurrentUser()
            : mainFrame.getGestion().getDefaultAdmin();
        User user = mainFrame.getGestion().rechercher_user(conversation.getUserId());
        if (admin == null || user == null) {
            JOptionPane.showMessageDialog(this, "Conversation invalide");
            return;
        }

        selectedConversation = conversation;
        adminChatHolder.removeAll();
        adminChatHolder.add(new MessagingPanel(mainFrame, admin, user, this::refreshConversationsList), BorderLayout.CENTER);
        adminChatHolder.revalidate();
        adminChatHolder.repaint();
        refreshConversationsList();
    }

    private JTable createModernTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(Fonts.BODY);
        table.setRowHeight(45);
        table.setGridColor(Colors.BORDER);
        table.setSelectionBackground(ModernUIComponents.withAlpha(Colors.PRIMARY_START, 50));
        table.setSelectionForeground(Colors.TEXT_DARK);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(Fonts.BODY_BOLD);
        header.setBackground(Colors.SURFACE);
        header.setForeground(Colors.TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Colors.BORDER));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                if (value instanceof String && isCardDisplayValue((String) value)) {
                    setForeground(getCardColor((String) value));
                    setFont(Fonts.BODY_BOLD);
                } else {
                    setForeground(Colors.TEXT_DARK);
                    setFont(Fonts.BODY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return c;
            }
        });
        return table;
    }

    private boolean isCardDisplayValue(String value) {
        String lower = value.toLowerCase();
        return lower.contains("verte") || lower.contains("jaune") || lower.contains("rouge");
    }

    private Color getCardColor(String cardValue) {
        String lower = cardValue.toLowerCase();
        if (lower.contains("rouge")) return Colors.ACCENT_CORAL;
        if (lower.contains("jaune")) return Colors.ACCENT_GOLD;
        return Colors.ACCENT_MINT;
    }

    public void refresh() {
        refreshDashboard();
        refreshUsersTable();
        refreshTrajetsTable();
        refreshConversationsList();
        updateSidebarSelection(0);
        contentLayout.show(contentPanel, "DASHBOARD");
    }

    private void refreshDashboard() {
        List<User> users = mainFrame.getGestion().getAllUsers();
        List<Trajet> trajets = mainFrame.getGestion().getAllTrajets();
        List<Evaluation> evals = mainFrame.getGestion().getAllEvaluations();

        usersStatCard.setValue(String.valueOf(users.size()));
        trajetsStatCard.setValue(String.valueOf(trajets.size()));
        evaluationsStatCard.setValue(String.valueOf(evals.size()));
        updateAdminNotificationBadge();
    }

    private void refreshUsersTable() {
        driversModel.setRowCount(0);
        passengersModel.setRowCount(0);
        for (User u : mainFrame.getGestion().getAllUsers()) {
            if (u instanceof Conducteur) {
                Conducteur c = (Conducteur) u;
                Object[] row = {c.getCin(), c.getNom(), c.getPrenom(), c.getMail(), getCardDisplay(c.getCard())};
                driversModel.addRow(row);
            } else if (u instanceof Passager) {
                Passager p = (Passager) u;
                Object[] row = {p.getCin(), p.getNom(), p.getPrenom(), p.getMail(), getCardDisplay(p.getCard())};
                passengersModel.addRow(row);
            }
        }
    }

    private String getCardDisplay(String card) {
        return switch (card == null ? "verte" : card.toLowerCase()) {
            case "jaune" -> "Jaune";
            case "rouge" -> "Rouge";
            default -> "Verte";
        };
    }

    private void refreshTrajetsTable() {
        trajetsModel.setRowCount(0);
        for (Trajet t : mainFrame.getGestion().getAllTrajets()) {
            String conducteur = (t.getConducteur() != null) ? t.getConducteur().getPrenom() + " " + t.getConducteur().getNom() : "Inconnu";
            trajetsModel.addRow(new Object[]{t.getDepartTrajet(), t.getArriveeTrajet(), t.getPrix(), t.getStatusTrajet(), conducteur});
        }
    }

    private void deleteUser() {
        String cin = null;
        int driverRow = driversTable.getSelectedRow();
        int passengerRow = passengersTable.getSelectedRow();

        if (driverRow != -1) {
            cin = (String) driversModel.getValueAt(driverRow, 0);
        } else if (passengerRow != -1) {
            cin = (String) passengersModel.getValueAt(passengerRow, 0);
        }

        if (cin == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer l'utilisateur " + cin + " ?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (mainFrame.getGestion().supprimerUtilisateur(cin)) {
                JOptionPane.showMessageDialog(this, "Utilisateur supprimé avec succès !");
                refreshUsersTable();
                refreshDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression.");
            }
        }
    }

    private void changeSelectedUserCard(String selectedCardLabel) {
        String cin = null;
        boolean selectedDriver = false;
        int driverRow = driversTable.getSelectedRow();
        int passengerRow = passengersTable.getSelectedRow();

        if (driverRow != -1) {
            cin = (String) driversModel.getValueAt(driverRow, 0);
            selectedDriver = true;
        } else if (passengerRow != -1) {
            cin = (String) passengersModel.getValueAt(passengerRow, 0);
        }

        if (cin == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur");
            return;
        }

        String newCard = selectedCardLabel == null ? "verte" : selectedCardLabel.toLowerCase();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Changer la carte de l'utilisateur " + cin + " vers " + selectedCardLabel + " ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        User user = mainFrame.getGestion().rechercher_user(cin);
        if (selectedDriver && user instanceof Conducteur c) {
            c.setCard(newCard);
            CSVDatabase.saveConducteurs(mainFrame.getGestion().getUsers());
        } else if (!selectedDriver && user instanceof Passager p) {
            p.setCard(newCard);
            CSVDatabase.savePassagers(mainFrame.getGestion().getUsers());
        } else {
            JOptionPane.showMessageDialog(this, "Utilisateur introuvable");
            return;
        }

        mainFrame.saveDataToCSV();
        refreshUsersTable();
        JOptionPane.showMessageDialog(this, "Carte mise à jour avec succès.");
    }

    private void deleteTrajet() {
        int row = trajetsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un trajet");
            return;
        }
        Trajet t = mainFrame.getGestion().getAllTrajets().get(row);
        if (JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce trajet ?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (mainFrame.getGestion().supprimerTrajet(t)) {
                JOptionPane.showMessageDialog(this, "Trajet supprimé avec succès !");
                refreshTrajetsTable();
                refreshDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression.");
            }
        }
    }

    private void updateSidebarSelection(int index) {
        for (int i = 0; i < sidebarButtons.size(); i++) {
            sidebarButtons.get(i).setSelected(i == index);
        }
    }
}
