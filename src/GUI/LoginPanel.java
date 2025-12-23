package GUI;

import Models.*;
import Services.*;
import java.awt.*;
import java.time.Year;
import javax.swing.*;

/**
 * Login and Registration Panel
 */
public class LoginPanel extends JPanel {
    
    private MainFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Login components
    private JTextField loginCinField;
    
    // Registration components - User info
    private JTextField regCinField;
    private JTextField regNomField;
    private JTextField regPrenomField;
    private JTextField regTelField;
    private JTextField regAnneeField;
    private JTextField regAdresseField;
    private JTextField regMailField;
    
    // Registration components - Driver specific
    private JTextField regNomVoitureField;
    private JTextField regMarqueVoitureField;
    private JTextField regMatriculeField;
    private JSpinner regPlacesSpinner;
    
    // Registration components - Passenger specific
    private JTextField pCinField;
    private JTextField pNomField;
    private JTextField pPrenomField;
    private JTextField pTelField;
    private JTextField pAnneeField;
    private JTextField pAdresseField;
    private JTextField pMailField;

    // Registration type
    private String registrationType = "CONDUCTEUR";
    
    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(StyleUtils.BACKGROUND_COLOR);
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Card panel for login/register forms
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        
        cardPanel.add(createLoginCard(), "LOGIN");
        cardPanel.add(createDriverRegistrationCard(), "REGISTER_DRIVER");
        cardPanel.add(createPassengerRegistrationCard(), "REGISTER_PASSENGER");
        
        // Center the card panel
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(cardPanel);
        
        add(centerWrapper, BorderLayout.CENTER);
        
        // Footer
        add(createFooterPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(StyleUtils.PRIMARY_COLOR);
        panel.setPreferredSize(new Dimension(0, 120));
        panel.setLayout(new GridBagLayout());
        
        JLabel titleLabel = new JLabel("🚗 Plateforme de Covoiturage");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Partagez vos trajets, économisez ensemble");
        subtitleLabel.setFont(StyleUtils.SUBTITLE_FONT);
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);
        
        panel.add(textPanel);
        
        return panel;
    }
    
    private JPanel createLoginCard() {
        JPanel card = StyleUtils.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(450, 500));
        
        // Title
        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(StyleUtils.TITLE_FONT);
        titleLabel.setForeground(StyleUtils.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(30));
        
        // CIN Field
        JPanel cinPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cinPanel.setOpaque(false);
        cinPanel.add(StyleUtils.createLabel("Numéro CIN :"));
        card.add(cinPanel);
        
        loginCinField = StyleUtils.createStyledTextField();
        loginCinField.setMaximumSize(new Dimension(400, 45));
        loginCinField.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(loginCinField);
        
        card.add(Box.createVerticalStrut(20));
        
        // User type selection
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        typePanel.setOpaque(false);
        
        JRadioButton driverRadio = new JRadioButton("Conducteur", true);
        JRadioButton passengerRadio = new JRadioButton("Passager");
        
        driverRadio.setFont(StyleUtils.REGULAR_FONT);
        passengerRadio.setFont(StyleUtils.REGULAR_FONT);
        driverRadio.setOpaque(false);
        passengerRadio.setOpaque(false);
        
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(driverRadio);
        typeGroup.add(passengerRadio);
        
        typePanel.add(driverRadio);
        typePanel.add(passengerRadio);
        card.add(typePanel);
        
        card.add(Box.createVerticalStrut(30));
        
        // Login Button
        JButton loginBtn = StyleUtils.createPrimaryButton("Se Connecter");
        loginBtn.setMaximumSize(new Dimension(400, 50));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> {
            String cin = loginCinField.getText().trim();
            if (cin.isEmpty()) {
                StyleUtils.showError(this, "Veuillez entrer votre CIN");
                return;
            }
            
            if (driverRadio.isSelected()) {
                loginAsDriver(cin);
            } else {
                loginAsPassenger(cin);
            }
        });
        card.add(loginBtn);
        
        card.add(Box.createVerticalStrut(30));
        
        // Separator
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(350, 1));
        card.add(separator);
        
        card.add(Box.createVerticalStrut(20));
        
        // Register section
        JLabel noAccountLabel = new JLabel("Pas encore de compte ?");
        noAccountLabel.setFont(StyleUtils.SMALL_FONT);
        noAccountLabel.setForeground(StyleUtils.TEXT_SECONDARY);
        noAccountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(noAccountLabel);
        
        card.add(Box.createVerticalStrut(15));
        
        // Register buttons
        JPanel registerBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        registerBtnPanel.setOpaque(false);
        
        JButton regDriverBtn = StyleUtils.createSecondaryButton("S'inscrire Conducteur");
        regDriverBtn.setPreferredSize(new Dimension(180, 40));
        regDriverBtn.addActionListener(e -> {
            registrationType = "CONDUCTEUR";
            clearAllRegistrationFields();
            cardLayout.show(cardPanel, "REGISTER_DRIVER");
        });
        
        JButton regPassengerBtn = StyleUtils.createSecondaryButton("S'inscrire Passager");
        regPassengerBtn.setPreferredSize(new Dimension(180, 40));
        regPassengerBtn.addActionListener(e -> {
            registrationType = "PASSAGER";
            clearAllRegistrationFields();
            cardLayout.show(cardPanel, "REGISTER_PASSENGER");
        });
        
        registerBtnPanel.add(regDriverBtn);
        registerBtnPanel.add(regPassengerBtn);
        card.add(registerBtnPanel);
        
        return card;
    }
    
    private JPanel createDriverRegistrationCard() {
        JPanel card = StyleUtils.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(550, 650));
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Inscription Conducteur");
        titleLabel.setFont(StyleUtils.SUBTITLE_FONT);
        titleLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        card.add(titlePanel, BorderLayout.NORTH);
        
        // Form Panel with scroll
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // User info section
        formPanel.add(createSectionLabel("Informations Personnelles"));
        formPanel.add(Box.createVerticalStrut(10));
        
        regCinField = addFormField(formPanel, "CIN *");
        regNomField = addFormField(formPanel, "Nom *");
        regPrenomField = addFormField(formPanel, "Prénom *");
        regTelField = addFormField(formPanel, "Téléphone *");
        regAnneeField = addFormField(formPanel, "Année Universitaire (ex: 2024) *");
        regAdresseField = addFormField(formPanel, "Adresse");
        regMailField = addFormField(formPanel, "Email *");
        
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createSectionLabel("Informations Véhicule"));
        formPanel.add(Box.createVerticalStrut(10));
        
        regNomVoitureField = addFormField(formPanel, "Nom du Véhicule *");
        regMarqueVoitureField = addFormField(formPanel, "Marque *");
        regMatriculeField = addFormField(formPanel, "Matricule *");
        
        // Places spinner
        JPanel placesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        placesPanel.setOpaque(false);
        placesPanel.setMaximumSize(new Dimension(500, 70));
        JPanel placesContent = new JPanel(new BorderLayout(10, 0));
        placesContent.setOpaque(false);
        placesContent.add(StyleUtils.createLabel("Places Disponibles *"), BorderLayout.NORTH);
        regPlacesSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 8, 1));
        regPlacesSpinner.setFont(StyleUtils.REGULAR_FONT);
        regPlacesSpinner.setPreferredSize(new Dimension(100, 35));
        placesContent.add(regPlacesSpinner, BorderLayout.WEST);
        placesPanel.add(placesContent);
        formPanel.add(placesPanel);
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        card.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        
        JButton cancelBtn = StyleUtils.createSecondaryButton("Annuler");
        cancelBtn.setPreferredSize(new Dimension(150, 40));
        cancelBtn.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        
        JButton registerBtn = StyleUtils.createSuccessButton("S'inscrire");
        registerBtn.setPreferredSize(new Dimension(150, 40));
        registerBtn.addActionListener(e -> registerDriver());
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(registerBtn);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createPassengerRegistrationCard() {
        JPanel card = StyleUtils.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(500, 550));
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Inscription Passager");
        titleLabel.setFont(StyleUtils.SUBTITLE_FONT);
        titleLabel.setForeground(StyleUtils.PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        card.add(titlePanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        formPanel.add(createSectionLabel("Informations Personnelles"));
        formPanel.add(Box.createVerticalStrut(10));
        
        // Use passenger field variables (class members)
        pCinField = addFormField(formPanel, "CIN *");
        pNomField = addFormField(formPanel, "Nom *");
        pPrenomField = addFormField(formPanel, "Prénom *");
        pTelField = addFormField(formPanel, "Téléphone *");
        pAnneeField = addFormField(formPanel, "Année Universitaire (ex: 2024) *");
        pAdresseField = addFormField(formPanel, "Adresse");
        pMailField = addFormField(formPanel, "Email *");

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        card.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        
        JButton cancelBtn = StyleUtils.createSecondaryButton("Annuler");
        cancelBtn.setPreferredSize(new Dimension(150, 40));
        cancelBtn.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        
        JButton registerBtn = StyleUtils.createSuccessButton("S'inscrire");
        registerBtn.setPreferredSize(new Dimension(150, 40));
        registerBtn.addActionListener(e -> {
            registerPassenger(pCinField.getText(), pNomField.getText(), pPrenomField.getText(),
                pTelField.getText(), pAnneeField.getText(), pAdresseField.getText(), pMailField.getText());
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(registerBtn);
        card.add(buttonPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(StyleUtils.HEADER_FONT);
        label.setForeground(StyleUtils.PRIMARY_COLOR);
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, StyleUtils.PRIMARY_COLOR));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JTextField addFormField(JPanel parent, String labelText) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(500, 65));
        
        JLabel label = StyleUtils.createLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField field = StyleUtils.createStyledTextField();
        field.setMaximumSize(new Dimension(450, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(3));
        fieldPanel.add(field);
        fieldPanel.add(Box.createVerticalStrut(8));
        
        parent.add(fieldPanel);
        
        return field;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(StyleUtils.BACKGROUND_COLOR);
        panel.setPreferredSize(new Dimension(0, 50));
        
        JLabel footerLabel = new JLabel("© 2024 Plateforme de Covoiturage - IHEC");
        footerLabel.setFont(StyleUtils.SMALL_FONT);
        footerLabel.setForeground(StyleUtils.TEXT_SECONDARY);
        panel.add(footerLabel);
        
        return panel;
    }
    
    private void loginAsDriver(String cin) {
        Gestion_covoiturage gestion = mainFrame.getGestion();
        Conducteur conducteur = gestion.rechercher_conducteur(cin);
        
        if (conducteur != null) {
            StyleUtils.showSuccess(this, "Bienvenue " + conducteur.getPrenom() + " " + conducteur.getNom() + " !");
            mainFrame.showDriverPanel(conducteur);
            loginCinField.setText("");
        } else {
            StyleUtils.showError(this, "Aucun conducteur trouvé avec ce CIN.\nVeuillez vous inscrire d'abord.");
        }
    }
    
    private void loginAsPassenger(String cin) {
        Gestion_covoiturage gestion = mainFrame.getGestion();
        Passager passager = gestion.rechercher_passager(cin);
        
        if (passager != null) {
            StyleUtils.showSuccess(this, "Bienvenue " + passager.getPrenom() + " " + passager.getNom() + " !");
            mainFrame.showPassengerPanel(passager);
            loginCinField.setText("");
        } else {
            StyleUtils.showError(this, "Aucun passager trouvé avec ce CIN.\nVeuillez vous inscrire d'abord.");
        }
    }
    
    private void registerDriver() {
        // Validate fields
        String cin = regCinField.getText().trim();
        String nom = regNomField.getText().trim();
        String prenom = regPrenomField.getText().trim();
        String tel = regTelField.getText().trim();
        String anneeStr = regAnneeField.getText().trim();
        String adresse = regAdresseField.getText().trim();
        String mail = regMailField.getText().trim();
        String nomVoiture = regNomVoitureField.getText().trim();
        String marqueVoiture = regMarqueVoitureField.getText().trim();
        String matricule = regMatriculeField.getText().trim();
        int places = (Integer) regPlacesSpinner.getValue();
        
        // Validation
        if (cin.isEmpty() || nom.isEmpty() || prenom.isEmpty() || tel.isEmpty() || 
            anneeStr.isEmpty() || mail.isEmpty() || nomVoiture.isEmpty() || 
            marqueVoiture.isEmpty() || matricule.isEmpty()) {
            StyleUtils.showError(this, "Veuillez remplir tous les champs obligatoires (*)");
            return;
        }
        
        // Check if user exists
        if (mainFrame.getGestion().rechercher_user(cin) != null) {
            StyleUtils.showError(this, "Un utilisateur avec ce CIN existe déjà !");
            return;
        }
        
        // Validate year
        Year annee;
        try {
            annee = Year.parse(anneeStr);
        } catch (Exception e) {
            StyleUtils.showError(this, "Format d'année invalide. Utilisez le format: 2024");
            return;
        }
        
        // Validate email
        if (!mail.matches("^[A-Za-z0-9._%+-]+@(gmail\\.com|[A-Za-z0-9.-]+\\.tn)$")) {
            StyleUtils.showError(this, "Format d'email invalide.\nUtilisez une adresse Gmail ou .tn");
            return;
        }
        
        try {
            // Create conductor using parameterized constructor
            // Note: Legacy panel - using default password for backwards compatibility
            Conducteur conducteur = new Conducteur(cin, nom, prenom, tel, annee, adresse, mail,
                "TempPass@1", nomVoiture, marqueVoiture, matricule, places);
            
            mainFrame.getGestion().getUsers().add(conducteur);
            
            StyleUtils.showSuccess(this, "Inscription réussie !\nBienvenue " + prenom + " !");
            clearAllRegistrationFields();
            cardLayout.show(cardPanel, "LOGIN");
            
        } catch (Exception e) {
            StyleUtils.showError(this, "Erreur lors de l'inscription: " + e.getMessage());
        }
    }
    
    private void registerPassenger(String cin, String nom, String prenom, String tel, 
                                   String anneeStr, String adresse, String mail) {
        // Validation
        if (cin.trim().isEmpty() || nom.trim().isEmpty() || prenom.trim().isEmpty() || 
            tel.trim().isEmpty() || anneeStr.trim().isEmpty() || mail.trim().isEmpty()) {
            StyleUtils.showError(this, "Veuillez remplir tous les champs obligatoires (*)");
            return;
        }
        
        // Check if user exists
        if (mainFrame.getGestion().rechercher_user(cin.trim()) != null) {
            StyleUtils.showError(this, "Un utilisateur avec ce CIN existe déjà !");
            return;
        }
        
        // Validate year
        Year annee;
        try {
            annee = Year.parse(anneeStr.trim());
        } catch (Exception e) {
            StyleUtils.showError(this, "Format d'année invalide. Utilisez le format: 2024");
            return;
        }
        
        // Validate email
        if (!mail.trim().matches("^[A-Za-z0-9._%+-]+@(gmail\\.com|[A-Za-z0-9.-]+\\.tn)$")) {
            StyleUtils.showError(this, "Format d'email invalide.\nUtilisez une adresse Gmail ou .tn");
            return;
        }
        
        try {
            // Create passenger using parameterized constructor
            // Note: Legacy panel - using default password for backwards compatibility
            Passager passager = new Passager(cin.trim(), nom.trim(), prenom.trim(), tel.trim(), 
                annee, adresse.trim(), mail.trim(), "TempPass@1", true, null);
            
            mainFrame.getGestion().getUsers().add(passager);
            
            StyleUtils.showSuccess(this, "Inscription réussie !\nBienvenue " + prenom.trim() + " !");
            cardLayout.show(cardPanel, "LOGIN");
            
        } catch (Exception e) {
            StyleUtils.showError(this, "Erreur lors de l'inscription: " + e.getMessage());
        }
    }
    
    private void clearAllRegistrationFields() {
        // Clear driver fields
        if (regCinField != null) regCinField.setText("");
        if (regNomField != null) regNomField.setText("");
        if (regPrenomField != null) regPrenomField.setText("");
        if (regTelField != null) regTelField.setText("");
        if (regAnneeField != null) regAnneeField.setText("");
        if (regAdresseField != null) regAdresseField.setText("");
        if (regMailField != null) regMailField.setText("");
        if (regNomVoitureField != null) regNomVoitureField.setText("");
        if (regMarqueVoitureField != null) regMarqueVoitureField.setText("");
        if (regMatriculeField != null) regMatriculeField.setText("");
        if (regPlacesSpinner != null) regPlacesSpinner.setValue(4);

        // Clear passenger fields
        if (pCinField != null) pCinField.setText("");
        if (pNomField != null) pNomField.setText("");
        if (pPrenomField != null) pPrenomField.setText("");
        if (pTelField != null) pTelField.setText("");
        if (pAnneeField != null) pAnneeField.setText("");
        if (pAdresseField != null) pAdresseField.setText("");
        if (pMailField != null) pMailField.setText("");
    }

    private void clearRegistrationFields() {
        if (regCinField != null) regCinField.setText("");
        if (regNomField != null) regNomField.setText("");
        if (regPrenomField != null) regPrenomField.setText("");
        if (regTelField != null) regTelField.setText("");
        if (regAnneeField != null) regAnneeField.setText("");
        if (regAdresseField != null) regAdresseField.setText("");
        if (regMailField != null) regMailField.setText("");
        if (regNomVoitureField != null) regNomVoitureField.setText("");
        if (regMarqueVoitureField != null) regMarqueVoitureField.setText("");
        if (regMatriculeField != null) regMatriculeField.setText("");
        if (regPlacesSpinner != null) regPlacesSpinner.setValue(4);
    }
}
