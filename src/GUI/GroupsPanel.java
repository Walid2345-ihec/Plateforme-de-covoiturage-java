package GUI;

import GUI.ModernUIComponents.Colors;
import GUI.ModernUIComponents.Fonts;
import Models.*;
import Services.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

/**
 * GroupsPanel - Affiche la liste des groupes de covoiturage auxquels appartient
 * l'utilisateur courant (conducteur ou passager). Chaque groupe possède un bouton
 * « Ouvrir la discussion » qui mène à GroupChatPanel.
 */
public class GroupsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final Gestion_covoiturage gestion;
    private final User currentUser;

    private JPanel groupsContainer;
    private JLabel emptyLabel;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public GroupsPanel(MainFrame mainFrame, Gestion_covoiturage gestion, User currentUser) {
        this.mainFrame = mainFrame;
        this.gestion = gestion;
        this.currentUser = currentUser;

        setLayout(new BorderLayout());
        setBackground(Colors.SURFACE);

        initializeComponents();
        refreshGroups();
    }

    private void initializeComponents() {
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Container with scroll
        groupsContainer = new JPanel();
        groupsContainer.setLayout(new BoxLayout(groupsContainer, BoxLayout.Y_AXIS));
        groupsContainer.setBackground(Colors.SURFACE);
        groupsContainer.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        emptyLabel = new JLabel("Aucun groupe pour le moment.");
        emptyLabel.setFont(Fonts.BODY);
        emptyLabel.setForeground(Colors.TEXT_MUTED);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane scrollPane = new JScrollPane(groupsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Colors.SURFACE);
        scrollPane.getViewport().setBackground(Colors.SURFACE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        ModernUIComponents.applyModernScrollBar(scrollPane);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Colors.SURFACE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        // Left: back button + title
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));

        JButton backBtn = new JButton("← Retour");
        backBtn.setFont(Fonts.BODY);
        backBtn.setBackground(Color.WHITE);
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> {
            if (currentUser instanceof Conducteur) {
                mainFrame.showDriverPanel();
            } else if (currentUser instanceof Passager) {
                mainFrame.showPassengerPanel();
            }
        });
        leftPanel.add(backBtn);
        leftPanel.add(Box.createHorizontalStrut(20));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("👥 Mes Groupes");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);

        JLabel subtitleLabel = new JLabel("Discussions de covoiturage");
        subtitleLabel.setFont(Fonts.BODY);
        subtitleLabel.setForeground(Colors.TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        leftPanel.add(titlePanel);

        headerPanel.add(leftPanel, BorderLayout.WEST);

        ModernUIComponents.RoundedButton refreshBtn =
                new ModernUIComponents.RoundedButton("Actualiser", Colors.TEXT_MUTED);
        refreshBtn.setPreferredSize(new Dimension(130, 42));
        refreshBtn.addActionListener(e -> refreshGroups());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(refreshBtn);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Rafraîchit la liste des groupes.
     */
    public void refreshGroups() {
        groupsContainer.removeAll();

        List<Group> groups = gestion.getGroupesPourUtilisateur(currentUser.getCin());

        if (groups.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);
            emptyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyPanel.add(emptyLabel);
            groupsContainer.add(emptyPanel);
        } else {
            for (Group g : groups) {
                groupsContainer.add(createGroupCard(g));
                groupsContainer.add(Box.createVerticalStrut(15));
            }
        }
        groupsContainer.add(Box.createVerticalGlue());

        groupsContainer.revalidate();
        groupsContainer.repaint();
    }

    private JPanel createGroupCard(Group group) {
        ModernUIComponents.GlassCard card = new ModernUIComponents.GlassCard();
        card.setLayout(new BorderLayout(20, 0));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setPreferredSize(new Dimension(0, 130));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left: group info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("👥 " + group.getGroupName());
        nameLabel.setFont(Fonts.HEADING_3);
        nameLabel.setForeground(Colors.TEXT_DARK);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(6));

        // Membres count
        int membersCount = group.getMemberCins().size() + 1; // +1 conducteur
        JLabel membersLabel = new JLabel("• " + membersCount + " membres");
        membersLabel.setFont(Fonts.BODY);
        membersLabel.setForeground(Colors.TEXT_MUTED);
        infoPanel.add(membersLabel);
        infoPanel.add(Box.createVerticalStrut(4));

        // Date de création
        String dateText = group.getDateCreation() != null
                ? "Créé le " + group.getDateCreation().format(DATE_FORMATTER)
                : "";
        JLabel dateLabel = new JLabel(dateText);
        dateLabel.setFont(Fonts.CAPTION);
        dateLabel.setForeground(Colors.TEXT_MUTED);
        infoPanel.add(dateLabel);
        infoPanel.add(Box.createVerticalStrut(4));

        // Conducteur
        Conducteur c = gestion.rechercher_conducteur(group.getConducteurCin());
        String driverName = (c != null) ? c.getNom() + " " + c.getPrenom() : "Conducteur";
        JLabel driverLabel = new JLabel("Conducteur: " + driverName);
        driverLabel.setFont(Fonts.CAPTION);
        driverLabel.setForeground(Colors.TEXT_MUTED);
        infoPanel.add(driverLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Right: open discussion button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 30));
        buttonPanel.setOpaque(false);

        ModernUIComponents.GradientButton openBtn = new ModernUIComponents.GradientButton(
                "Ouvrir la discussion", Colors.PRIMARY_START, Colors.PRIMARY_END);
        openBtn.setPreferredSize(new Dimension(220, 45));
        openBtn.addActionListener(e -> openDiscussion(group));

        buttonPanel.add(openBtn);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private void openDiscussion(Group group) {
        GroupChatPanel chatPanel = new GroupChatPanel(mainFrame, gestion, currentUser, group);
        mainFrame.showGroupChatPanel(chatPanel);
    }
}
