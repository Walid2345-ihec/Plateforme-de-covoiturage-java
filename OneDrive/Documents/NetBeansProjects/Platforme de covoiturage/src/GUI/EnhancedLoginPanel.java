package GUI;

import Models.*;
import Services.*;
import GUI.ModernUIComponents.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.Year;

/**
 * Enhanced Modern Login Panel with beautiful animations and design
 */
public class EnhancedLoginPanel extends JPanel {
    
    private MainFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Login components
    private ModernUIComponents.ModernTextField loginCinField;
    private JRadioButton driverRadio;
    private JRadioButton passengerRadio;
    
    // Registration fields
    private ModernUIComponents.ModernTextField regCinField;
    private ModernUIComponents.ModernTextField regNomField;
    private ModernUIComponents.ModernTextField regPrenomField;
    private ModernUIComponents.ModernTextField regTelField;
    private ModernUIComponents.ModernTextField regAnneeField;
    private ModernUIComponents.ModernTextField regAdresseField;
    private ModernUIComponents.ModernTextField regMailField;
    private ModernUIComponents.ModernTextField regNomVoitureField;
    private ModernUIComponents.ModernTextField regMarqueField;
    private ModernUIComponents.ModernTextField regMatriculeField;
    private JSpinner regPlacesSpinner;
    
    // Animation
    private Timer backgroundAnimationTimer;
    private float animationOffset = 0;
    
    public EnhancedLoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        startBackgroundAnimation();
        initializeComponents();
    }
    
    private void startBackgroundAnimation() {
        backgroundAnimationTimer = new Timer(50, e -> {
            animationOffset += 0.5f;
            if (animationOffset > 360) animationOffset = 0;
            repaint();
        });
        backgroundAnimationTimer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int w = getWidth();
        int h = getHeight();
        
        // Animated gradient background
        GradientPaint gradient = new GradientPaint(
            (float)(w * Math.cos(Math.toRadians(animationOffset))), 0,
            Colors.PRIMARY_START,
            w, h,
            Colors.PRIMARY_END
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, w, h);
        
        // Animated decorative shapes
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
        g2.setColor(Color.WHITE);
        
        // Floating circles
        double offset1 = Math.sin(Math.toRadians(animationOffset)) * 30;
        double offset2 = Math.cos(Math.toRadians(animationOffset)) * 40;
        double offset3 = Math.sin(Math.toRadians(animationOffset + 45)) * 25;
        
        g2.fillOval((int)(-100 + offset1), (int)(-50 + offset2), 300, 300);
        g2.fillOval((int)(w - 200 + offset2), (int)(h - 250 + offset1), 350, 350);
        g2.fillOval((int)(w / 2 - 100 + offset3), (int)(-150 + offset1), 200, 200);
        g2.fillOval((int)(100 + offset2), (int)(h - 150 + offset3), 200, 200);
        
        // Additional smaller circles
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
        g2.fillOval((int)(w / 3 + offset1), (int)(h / 3 + offset2), 150, 150);
        g2.fillOval((int)(w * 2 / 3 + offset3), (int)(h / 2 + offset1), 100, 100);
        
        g2.dispose();
    }
    
    private void initializeComponents() {
        // Card layout for switching between login and registration
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        
        cardPanel.add(createLoginView(), "LOGIN");
        cardPanel.add(createDriverRegistrationView(), "REGISTER_DRIVER");
        cardPanel.add(createPassengerRegistrationView(), "REGISTER_PASSENGER");
        
        // Center wrapper
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(cardPanel);
        
        add(centerWrapper, BorderLayout.CENTER);
        
        // Footer
        add(createFooter(), BorderLayout.SOUTH);
    }
    
    private JPanel createLoginView() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        
        ModernUIComponents.GlassCard card = new ModernUIComponents.GlassCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 520));
        
        // Logo and Title
        JLabel logoLabel = new JLabel("🚗");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(logoLabel);
        
        card.add(Box.createVerticalStrut(10));
        
        JLabel titleLabel = new JLabel("Covoiturage");
        titleLabel.setFont(Fonts.HEADING_1);
        titleLabel.setForeground(Colors.TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Connectez-vous pour continuer");
        subtitleLabel.setFont(Fonts.BODY);
        subtitleLabel.setForeground(Colors.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitleLabel);
        
        card.add(Box.createVerticalStrut(35));
        
        // CIN Field
        loginCinField = new ModernUIComponents.ModernTextField("Entrez votre CIN");
        loginCinField.setIcon("👤");
        loginCinField.setMaximumSize(new Dimension(340, 50));
        loginCinField.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(loginCinField);
        
        card.add(Box.createVerticalStrut(20));
        
        // User type selection
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        typePanel.setOpaque(false);
        typePanel.setMaximumSize(new Dimension(340, 40));
        
        driverRadio = createStyledRadio("🚗 Conducteur", true);
        passengerRadio = createStyledRadio("👤 Passager", false);
        
        ButtonGroup group = new ButtonGroup();
        group.add(driverRadio);
        group.add(passengerRadio);
        
        typePanel.add(driverRadio);
        typePanel.add(passengerRadio);
        card.add(typePanel);
        
        card.add(Box.createVerticalStrut(30));
        
        // Login button
        ModernUIComponents.GradientButton loginBtn = new ModernUIComponents.GradientButton(
            "Se Connecter", Colors.PRIMARY_START, Colors.PRIMARY_END);
        loginBtn.setMaximumSize(new Dimension(340, 52));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> performLogin());
        card.add(loginBtn);
        
        card.add(Box.createVerticalStrut(25));
        
        // Divider
        JPanel dividerPanel = new JPanel(new GridBagLayout());
        dividerPanel.setOpaque(false);
        dividerPanel.setMaximumSize(new Dimension(340, 30));
        
        JSeparator leftSep = new JSeparator();
        leftSep.setPreferredSize(new Dimension(120, 1));
        leftSep.setForeground(Colors.BORDER);
        
        JLabel orLabel = new JLabel("ou");
        orLabel.setFont(Fonts.CAPTION);
        orLabel.setForeground(Colors.TEXT_MUTED);
        
        JSeparator rightSep = new JSeparator();
        rightSep.setPreferredSize(new Dimension(120, 1));
        rightSep.setForeground(Colors.BORDER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridy = 0;
        gbc.gridx = 0; dividerPanel.add(leftSep, gbc);
        gbc.gridx = 1; dividerPanel.add(orLabel, gbc);
        gbc.gridx = 2; dividerPanel.add(rightSep, gbc);
        
        card.add(dividerPanel);
        
        card.add(Box.createVerticalStrut(20));
        
        // Registration buttons
        JPanel regBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        regBtnPanel.setOpaque(false);
        regBtnPanel.setMaximumSize(new Dimension(360, 50));
        
        ModernUIComponents.RoundedButton regDriverBtn = new ModernUIComponents.RoundedButton(
            "Nouveau Conducteur", Colors.ACCENT_MINT);
        regDriverBtn.setPreferredSize(new Dimension(160, 44));
        regDriverBtn.addActionListener(e -> cardLayout.show(cardPanel, "REGISTER_DRIVER"));
        
        ModernUIComponents.RoundedButton regPassengerBtn = new ModernUIComponents.RoundedButton(
            "Nouveau Passager", Colors.ACCENT_SKY);
        regPassengerBtn.setPreferredSize(new Dimension(160, 44));
        regPassengerBtn.addActionListener(e -> cardLayout.show(cardPanel, "REGISTER_PASSENGER"));
        
        regBtnPanel.add(regDriverBtn);
        regBtnPanel.add(regPassengerBtn);
        card.add(regBtnPanel);
        
        wrapper.add(card);
        return wrapper;
    }
    
    private JRadioButton createStyledRadio(String text, boolean selected) {
        JRadioButton radio = new JRadioButton(text, selected) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Add subtle highlight when selected
                if (isSelected()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(Colors.PRIMARY_START.getRed(), 
                                         Colors.PRIMARY_START.getGreen(), 
                                         Colors.PRIMARY_START.getBlue(), 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                }
            }
        };
        radio.setFont(Fonts.BODY_BOLD);
        radio.setForeground(Colors.TEXT_DARK);
        radio.setOpaque(false);
        radio.setFocusPainted(false);
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radio.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        // Add hover effect
        radio.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                radio.setForeground(Colors.PRIMARY_START);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                radio.setForeground(Colors.TEXT_DARK);
            }
        });
        
        return radio;
    }
    
    private JPanel createDriverRegistrationView() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        
        ModernUIComponents.GlassCard card = new ModernUIComponents.GlassCard();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(500, 650));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel iconLabel = new JLabel("🚗");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Inscription Conducteur");
        titleLabel.setFont(Fonts.HEADING_2);
        titleLabel.setForeground(Colors.PRIMARY_START);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);
        
        headerPanel.add(Box.createVerticalStrut(15));
        card.add(headerPanel, BorderLayout.NORTH);
        
        // Form
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        // Personal info section
        formPanel.add(createSectionLabel("👤 Informations Personnelles"));
        formPanel.add(Box.createVerticalStrut(10));
        
        JPanel row1 = createFormRow();
        regCinField = addFormField(row1, "CIN", "12345678");
        regNomField = addFormField(row1, "Nom", "Votre nom");
        formPanel.add(row1);
        
        JPanel row2 = createFormRow();
        regPrenomField = addFormField(row2, "Prénom", "Votre prénom");
        regTelField = addFormField(row2, "Téléphone", "12 345 678");
        formPanel.add(row2);
        
        JPanel row3 = createFormRow();
        regAnneeField = addFormField(row3, "Année Univ.", "2024");
        regMailField = addFormField(row3, "Email", "email@gmail.com");
        formPanel.add(row3);
        
        JPanel row4 = createFormRow();
        regAdresseField = addFormField(row4, "Adresse", "Votre adresse");
        formPanel.add(row4);
        
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createSectionLabel("🚙 Informations Véhicule"));
        formPanel.add(Box.createVerticalStrut(10));
        
        JPanel row5 = createFormRow();
        regNomVoitureField = addFormField(row5, "Nom Véhicule", "Clio, Golf...");
        regMarqueField = addFormField(row5, "Marque", "Renault, VW...");
        formPanel.add(row5);
        
        JPanel row6 = createFormRow();
        regMatriculeField = addFormField(row6, "Matricule", "123TU4567");
        
        // Places spinner
        JPanel placesPanel = new JPanel();
        placesPanel.setOpaque(false);
        placesPanel.setLayout(new BoxLayout(placesPanel, BoxLayout.Y_AXIS));
        JLabel placesLabel = new JLabel("Places");
        placesLabel.setFont(Fonts.CAPTION);
        placesLabel.setForeground(Colors.TEXT_MUTED);
        regPlacesSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 8, 1));
        regPlacesSpinner.setFont(Fonts.BODY);
        regPlacesSpinner.setPreferredSize(new Dimension(80, 40));
        placesPanel.add(placesLabel);
        placesPanel.add(Box.createVerticalStrut(5));
        placesPanel.add(regPlacesSpinner);
        row6.add(placesPanel);
        
        formPanel.add(row6);
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        ModernUIComponents.applyModernScrollBar(scrollPane);
        card.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        ModernUIComponents.RoundedButton cancelBtn = new ModernUIComponents.RoundedButton(
            "Annuler", Colors.TEXT_MUTED);
        cancelBtn.setPreferredSize(new Dimension(140, 45));
        cancelBtn.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        
        ModernUIComponents.GradientButton registerBtn = new ModernUIComponents.GradientButton(
            "S'inscrire", Colors.ACCENT_MINT, Colors.GRADIENT_TEAL_START);
        registerBtn.setPreferredSize(new Dimension(160, 48));
        registerBtn.addActionListener(e -> registerDriver());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(registerBtn);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        wrapper.add(card);
        return wrapper;
    }
    
    private JPanel createPassengerRegistrationView() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        
        ModernUIComponents.GlassCard card = new ModernUIComponents.GlassCard();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(500, 520));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(iconLabel);
        
        JLabel titleLabel = new JLabel("Inscription Passager");
        titleLabel.setFont(Fonts.HEADING_2);
        titleLabel.setForeground(Colors.ACCENT_SKY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);
        
        headerPanel.add(Box.createVerticalStrut(15));
        card.add(headerPanel, BorderLayout.NORTH);
        
        // Form
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        formPanel.add(createSectionLabel("👤 Informations Personnelles"));
        formPanel.add(Box.createVerticalStrut(10));
        
        // Create fields for passenger
        ModernUIComponents.ModernTextField pCin, pNom, pPrenom, pTel, pAnnee, pMail, pAdresse;
        
        JPanel row1 = createFormRow();
        pCin = addFormField(row1, "CIN", "12345678");
        pNom = addFormField(row1, "Nom", "Votre nom");
        formPanel.add(row1);
        
        JPanel row2 = createFormRow();
        pPrenom = addFormField(row2, "Prénom", "Votre prénom");
        pTel = addFormField(row2, "Téléphone", "12 345 678");
        formPanel.add(row2);
        
        JPanel row3 = createFormRow();
        pAnnee = addFormField(row3, "Année Univ.", "2024");
        pMail = addFormField(row3, "Email", "email@gmail.com");
        formPanel.add(row3);
        
        JPanel row4 = createFormRow();
        pAdresse = addFormField(row4, "Adresse", "Votre adresse");
        formPanel.add(row4);
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        ModernUIComponents.applyModernScrollBar(scrollPane);
        card.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        ModernUIComponents.RoundedButton cancelBtn = new ModernUIComponents.RoundedButton(
            "Annuler", Colors.TEXT_MUTED);
        cancelBtn.setPreferredSize(new Dimension(140, 45));
        cancelBtn.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        
        ModernUIComponents.GradientButton registerBtn = new ModernUIComponents.GradientButton(
            "S'inscrire", Colors.ACCENT_SKY, Colors.PRIMARY_START);
        registerBtn.setPreferredSize(new Dimension(160, 48));
        registerBtn.addActionListener(e -> {
            registerPassenger(pCin.getText(), pNom.getText(), pPrenom.getText(),
                pTel.getText(), pAnnee.getText(), pAdresse.getText(), pMail.getText());
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(registerBtn);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        wrapper.add(card);
        return wrapper;
    }
    
    private JPanel createFormRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(480, 80));
        return row;
    }
    
    private ModernUIComponents.ModernTextField addFormField(JPanel row, String label, String placeholder) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setOpaque(false);
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(Fonts.CAPTION);
        labelComp.setForeground(Colors.TEXT_MUTED);
        
        ModernUIComponents.ModernTextField field = new ModernUIComponents.ModernTextField(placeholder);
        field.setPreferredSize(new Dimension(200, 42));
        
        // Add validation hints as tooltips
        String tooltip = getTooltipForField(label);
        if (tooltip != null) {
            field.setToolTipText(tooltip);
            labelComp.setToolTipText(tooltip);
        }
        
        fieldPanel.add(labelComp);
        fieldPanel.add(Box.createVerticalStrut(5));
        fieldPanel.add(field);
        
        row.add(fieldPanel);
        return field;
    }
    
    /**
     * Returns validation hint tooltip for each field type
     */
    private String getTooltipForField(String label) {
        switch (label) {
            case "CIN":
                return "<html><b>CIN:</b> Exactement 8 chiffres<br>Exemple: 12345678</html>";
            case "Téléphone":
                return "<html><b>Téléphone:</b> Exactement 8 chiffres<br>Exemple: 98765432</html>";
            case "Nom":
            case "Prénom":
                return "<html><b>" + label + ":</b> Lettres uniquement<br>(caractères français acceptés)</html>";
            case "Matricule":
                return "<html><b>Matricule:</b> Format tunisien<br>1-3 chiffres + TU + 4 chiffres<br>Exemple: 123TU4567</html>";
            case "Email":
                return "<html><b>Email:</b> Adresse valide<br>@gmail.com ou @*.tn</html>";
            case "Année Univ.":
                return "<html><b>Année:</b> 4 chiffres<br>Exemple: 2024</html>";
            default:
                return null;
        }
    }
    
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Fonts.HEADING_3);
        label.setForeground(Colors.TEXT_DARK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        return label;
    }
    
    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(0, 50));
        
        // Dynamic year for copyright
        int currentYear = java.time.Year.now().getValue();
        JLabel footerLabel = new JLabel("© " + currentYear + " Plateforme de Covoiturage - IHEC");
        footerLabel.setFont(Fonts.CAPTION);
        footerLabel.setForeground(new Color(255, 255, 255, 180));
        footer.add(footerLabel);
        
        return footer;
    }
    
    // ==================== BUSINESS LOGIC ====================
    
    private void performLogin() {
        String cin = loginCinField.getText().trim();
        
        // Validate CIN format using regex
        if (!ValidationUtils.isValidCIN(cin)) {
            showModernError(ValidationUtils.CIN_ERROR);
            return;
        }
        
        Gestion_covoiturage gestion = mainFrame.getGestion();
        
        if (driverRadio.isSelected()) {
            Conducteur conducteur = gestion.rechercher_conducteur(cin);
            if (conducteur != null) {
                showModernSuccess("Bienvenue " + conducteur.getPrenom() + " !");
                mainFrame.showDriverPanel(conducteur);
                loginCinField.setText("");
            } else {
                showModernError("Aucun conducteur trouvé avec ce CIN");
            }
        } else {
            Passager passager = gestion.rechercher_passager(cin);
            if (passager != null) {
                showModernSuccess("Bienvenue " + passager.getPrenom() + " !");
                mainFrame.showPassengerPanel(passager);
                loginCinField.setText("");
            } else {
                showModernError("Aucun passager trouvé avec ce CIN");
            }
        }
    }
    
    private void registerDriver() {
        String cin = regCinField.getText().trim();
        String nom = regNomField.getText().trim();
        String prenom = regPrenomField.getText().trim();
        String tel = regTelField.getText().trim();
        String anneeStr = regAnneeField.getText().trim();
        String adresse = regAdresseField.getText().trim();
        String mail = regMailField.getText().trim();
        String nomVoiture = regNomVoitureField.getText().trim();
        String marque = regMarqueField.getText().trim();
        String matricule = regMatriculeField.getText().trim();
        int places = (Integer) regPlacesSpinner.getValue();
        
        // ═══════════════════════════════════════════════════════════════════════════
        // VALIDATION with REGEX - Using ValidationUtils
        // ═══════════════════════════════════════════════════════════════════════════
        
        // CIN: exactly 8 digits
        if (!ValidationUtils.isValidCIN(cin)) {
            showModernError(ValidationUtils.CIN_ERROR);
            return;
        }
        
        // Nom: letters only
        if (!ValidationUtils.isValidName(nom)) {
            showModernError(ValidationUtils.NAME_ERROR);
            return;
        }
        
        // Prénom: letters only
        if (!ValidationUtils.isValidName(prenom)) {
            showModernError(ValidationUtils.PRENOM_ERROR);
            return;
        }
        
        // Téléphone: exactly 8 digits
        if (!ValidationUtils.isValidPhone(tel)) {
            showModernError(ValidationUtils.PHONE_ERROR);
            return;
        }
        
        // Email: @gmail.com or @*.tn
        if (!ValidationUtils.isValidEmail(mail)) {
            showModernError(ValidationUtils.EMAIL_ERROR);
            return;
        }
        
        // Matricule: format 123TU4567
        if (!ValidationUtils.isValidMatricule(matricule)) {
            showModernError(ValidationUtils.MATRICULE_ERROR);
            return;
        }
        
        // Nom voiture: letters only
        if (!ValidationUtils.isValidName(nomVoiture)) {
            showModernError("Le nom de voiture doit contenir uniquement des lettres");
            return;
        }
        
        // Check if user exists
        if (mainFrame.getGestion().rechercher_user(cin) != null) {
            showModernError("Un utilisateur avec ce CIN existe déjà");
            return;
        }
        
        Year annee;
        try {
            annee = Year.parse(anneeStr);
        } catch (Exception e) {
            showModernError("Format d'année invalide (ex: 2024)");
            return;
        }
        
        try {
            Conducteur conducteur = new Conducteur(cin, nom, prenom, tel, annee, adresse, mail,
                nomVoiture, marque, matricule, places);
            mainFrame.getGestion().getUsers().add(conducteur);
            showModernSuccess("Inscription réussie !");
            clearDriverFields();
            cardLayout.show(cardPanel, "LOGIN");
        } catch (Exception e) {
            showModernError("Erreur: " + e.getMessage());
        }
    }
    
    private void registerPassenger(String cin, String nom, String prenom, String tel, 
                                   String anneeStr, String adresse, String mail) {
        
        // ═══════════════════════════════════════════════════════════════════════════
        // VALIDATION with REGEX - Using ValidationUtils
        // ═══════════════════════════════════════════════════════════════════════════
        
        // CIN: exactly 8 digits
        if (!ValidationUtils.isValidCIN(cin.trim())) {
            showModernError(ValidationUtils.CIN_ERROR);
            return;
        }
        
        // Nom: letters only
        if (!ValidationUtils.isValidName(nom.trim())) {
            showModernError(ValidationUtils.NAME_ERROR);
            return;
        }
        
        // Prénom: letters only
        if (!ValidationUtils.isValidName(prenom.trim())) {
            showModernError(ValidationUtils.PRENOM_ERROR);
            return;
        }
        
        // Téléphone: exactly 8 digits
        if (!ValidationUtils.isValidPhone(tel.trim())) {
            showModernError(ValidationUtils.PHONE_ERROR);
            return;
        }
        
        // Email: @gmail.com or @*.tn
        if (!ValidationUtils.isValidEmail(mail.trim())) {
            showModernError(ValidationUtils.EMAIL_ERROR);
            return;
        }
        
        // Check if user exists
        if (mainFrame.getGestion().rechercher_user(cin.trim()) != null) {
            showModernError("Un utilisateur avec ce CIN existe déjà");
            return;
        }
        
        Year annee;
        try {
            annee = Year.parse(anneeStr.trim());
        } catch (Exception e) {
            showModernError("Format d'année invalide (ex: 2024)");
            return;
        }
        
        try {
            Passager passager = new Passager(cin.trim(), nom.trim(), prenom.trim(), tel.trim(), 
                annee, adresse.trim(), mail.trim(), true, null);
            mainFrame.getGestion().getUsers().add(passager);
            showModernSuccess("Inscription réussie !");
            cardLayout.show(cardPanel, "LOGIN");
        } catch (Exception e) {
            showModernError("Erreur: " + e.getMessage());
        }
    }
    
    private void clearDriverFields() {
        regCinField.setText("");
        regNomField.setText("");
        regPrenomField.setText("");
        regTelField.setText("");
        regAnneeField.setText("");
        regAdresseField.setText("");
        regMailField.setText("");
        regNomVoitureField.setText("");
        regMarqueField.setText("");
        regMatriculeField.setText("");
        regPlacesSpinner.setValue(4);
    }
    
    private void showModernSuccess(String message) {
        // Create a custom styled dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Succès", true);
        dialog.setLayout(new BorderLayout());
        dialog.setUndecorated(true);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(Colors.SUCCESS, 2));
        
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        
        JLabel iconLabel = new JLabel("✅");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Succès!");
        titleLabel.setFont(Fonts.HEADING_2);
        titleLabel.setForeground(Colors.SUCCESS);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel msgLabel = new JLabel("<html><center>" + message + "</center></html>");
        msgLabel.setFont(Fonts.BODY);
        msgLabel.setForeground(Colors.TEXT_DARK);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        ModernUIComponents.RoundedButton okBtn = new ModernUIComponents.RoundedButton("OK", Colors.SUCCESS);
        okBtn.setPreferredSize(new Dimension(120, 40));
        okBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        okBtn.addActionListener(e -> dialog.dispose());
        
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(msgLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(okBtn);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        
        // Auto-close after 2 seconds
        Timer autoClose = new Timer(2000, e -> dialog.dispose());
        autoClose.setRepeats(false);
        autoClose.start();
        
        dialog.setVisible(true);
    }
    
    private void showModernError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Erreur", true);
        dialog.setLayout(new BorderLayout());
        dialog.setUndecorated(true);
        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(Colors.DANGER, 2));
        
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        
        JLabel iconLabel = new JLabel("❌");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Erreur");
        titleLabel.setFont(Fonts.HEADING_2);
        titleLabel.setForeground(Colors.DANGER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel msgLabel = new JLabel("<html><center>" + message + "</center></html>");
        msgLabel.setFont(Fonts.BODY);
        msgLabel.setForeground(Colors.TEXT_DARK);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        msgLabel.setPreferredSize(new Dimension(280, 50));
        
        ModernUIComponents.RoundedButton okBtn = new ModernUIComponents.RoundedButton("Compris", Colors.DANGER);
        okBtn.setPreferredSize(new Dimension(120, 40));
        okBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        okBtn.addActionListener(e -> dialog.dispose());
        
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(msgLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(okBtn);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
