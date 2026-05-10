package GUI;

import GUI.ModernUIComponents.Colors;
import GUI.ModernUIComponents.Fonts;
import Models.*;
import Services.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
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

    private final java.util.List<ModernUIComponents.SidebarButton> sidebarButtons = new java.util.ArrayList<>();

    public AdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Colors.SURFACE);
        initializeComponents();
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
        });
        sidebarButtons.add(button);
        sidebar.add(button);
    }

    private JPanel createDashboardView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Tableau de Bord Admin");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);

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

    private JPanel createUsersView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Gestion des Utilisateurs");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"CIN", "Nom", "Prénom", "Email"};

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

        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
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
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return c;
            }
        });
        return table;
    }

    public void refresh() {
        refreshDashboard();
        refreshUsersTable();
        refreshTrajetsTable();
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
    }

    private void refreshUsersTable() {
        driversModel.setRowCount(0);
        passengersModel.setRowCount(0);
        for (User u : mainFrame.getGestion().getAllUsers()) {
            Object[] row = {u.getCin(), u.getNom(), u.getPrenom(), u.getMail()};
            if (u instanceof Conducteur) {
                driversModel.addRow(row);
            } else if (u instanceof Passager) {
                passengersModel.addRow(row);
            }
        }
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
