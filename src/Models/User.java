package Models;

import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * User - Base class for all users in the carpooling platform
 * 
 * VALIDATION RULES:
 * - CIN: exactly 8 digits (^[0-9]{8}$)
 * - Phone: exactly 8 digits (^[0-9]{8}$)
 * - Name/Prénom: letters only including French accents (^[a-zA-ZÀ-ÿ\s'-]+$)
 * - Email: @gmail.com or @*.tn domain
 */
public class User {
    protected String cin;
    protected String nom;
    protected String prenom;
    protected String tel;
    protected Year anneeUniversitaire;
    protected String adresse;
    protected String mail;
    protected String passwordHash; // Stores SHA-256 hash of password - NEVER plain text!

    // ═══════════════════════════════════════════════════════════════════════════
    // Using centralized validation from ValidationUtils class
    // ═══════════════════════════════════════════════════════════════════════════
    
    // Constructeur par défaut (interactif avec gestion des exceptions)
    public User() {
        // Utilisation d'un Scanner local. Il est préférable de ne pas le fermer
        // car cela fermerait System.in, affectant d'autres Scanners potentiels.
        Scanner sc = new Scanner(System.in);
        System.out.println("--- Saisie des informations utilisateur ---");
        
        System.out.println("Entrez votre cin :");
        this.cin = sc.nextLine();
        
        System.out.println("Entrez votre nom :");
        this.nom = sc.nextLine();

        System.out.println("Entrez votre prénom :");
        this.prenom = sc.nextLine();

        System.out.println("Entrez votre numéro de téléphone :");
        this.tel = sc.nextLine();

        // Gestion des exceptions pour l'année universitaire avec boucle de validation
        boolean anneeValide = false;
        while (!anneeValide) {
            System.out.println("Entrez l'année universitaire :");
            String yearInput = sc.nextLine();
            try {
                this.anneeUniversitaire = Year.parse(yearInput);
                anneeValide = true;
            } catch (DateTimeParseException e) {
                System.err.println("Erreur: Format d'année invalide. Veuillez entrer une année à 4 chiffres (ex: 2024).");
            }
        }

        System.out.println("Entrez votre adresse :");
        this.adresse = sc.nextLine();

        // Gestion des exceptions pour l'email avec boucle de validation
        boolean mailValide = false;
        while (!mailValide) {
            System.out.println("Entrez votre adresse mail :");
            String mailInput = sc.nextLine();
            if (ValidationUtils.isValidEmail(mailInput)) {
                this.mail = mailInput;
                mailValide = true;
            } else {
                System.err.println("Erreur: Format d'adresse mail invalide. Veuillez réessayer.");
            }
        }
    }
    
    
        public User(String cin) {
        // Utilisation d'un Scanner local. Il est préférable de ne pas le fermer
        // car cela fermerait System.in, affectant d'autres Scanners potentiels.
        Scanner sc = new Scanner(System.in);
        this.cin = cin;
        
        System.out.println("Entrez votre nom :");
        this.nom = sc.nextLine();

        System.out.println("Entrez votre prénom :");
        this.prenom = sc.nextLine();

        System.out.println("Entrez votre numéro de téléphone :");
        this.tel = sc.nextLine();

        // Gestion des exceptions pour l'année universitaire avec boucle de validation
        boolean anneeValide = false;
        while (!anneeValide) {
            System.out.println("Entrez l'année universitaire :");
            String yearInput = sc.nextLine();
            try {
                this.anneeUniversitaire = Year.parse(yearInput);
                anneeValide = true;
            } catch (DateTimeParseException e) {
                System.err.println("Erreur: Format d'année invalide. Veuillez entrer une année à 4 chiffres (ex: 2024).");
            }
        }

        System.out.println("Entrez votre adresse :");
        this.adresse = sc.nextLine();

        // Gestion des exceptions pour l'email avec boucle de validation
        boolean mailValide = false;
        while (!mailValide) {
            System.out.println("Entrez votre adresse mail :");
            String mailInput = sc.nextLine();
            if (ValidationUtils.isValidEmail(mailInput)) {
                this.mail = mailInput;
                mailValide = true;
            } else {
                System.err.println("Erreur: Format d'adresse mail invalide. Veuillez réessayer.");
            }
        }
    }

    // Constructeur paramétré (avec validation regex complète)
    public User(String cin, String nom, String prenom, String tel, Year anneeUniversitaire, String adresse, String mail, String password) {
        // ═══════════════════════════════════════════════════════════════════════════
        // VALIDATION avec expressions régulières
        // ═══════════════════════════════════════════════════════════════════════════
        
        // Validate CIN: exactly 8 digits
        ValidationUtils.validateCIN(cin);
        
        // Validate Nom: letters only (including French accents)
        ValidationUtils.validateName(nom, "Nom");
        
        // Validate Prénom: letters only (including French accents)
        ValidationUtils.validateName(prenom, "Prénom");
        
        // Validate Phone: exactly 8 digits
        ValidationUtils.validatePhone(tel);
        
        // Validate Year
        if (anneeUniversitaire == null) {
            throw new IllegalArgumentException("L'année universitaire ne peut pas être nulle.");
        }
        
        // Validate Email: @gmail.com or @*.tn
        ValidationUtils.validateEmail(mail);
        
        // Validate Password strength (if provided)
        // Note: password can be null when loading from CSV (already hashed)
        if (password != null && !password.isEmpty()) {
            ValidationUtils.validatePassword(password);
            this.passwordHash = ValidationUtils.hashPassword(password);
        } else {
            this.passwordHash = null;
        }

        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
        this.anneeUniversitaire = anneeUniversitaire;
        this.adresse = adresse;
        this.mail = mail;
    }
    
    /**
     * Constructor for loading from CSV (password already hashed).
     * This constructor accepts a pre-hashed password for database loading.
     * 
     * SECURITY NOTE: Use this constructor ONLY when loading from CSV.
     * For new user registration, use the standard constructor with plain password.
     */
    public User(String cin, String nom, String prenom, String tel, Year anneeUniversitaire, 
                String adresse, String mail, String passwordHash, boolean isHashedPassword) {
        // Minimal validation for CSV loading
        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
        this.anneeUniversitaire = anneeUniversitaire;
        this.adresse = adresse;
        this.mail = mail;
        this.passwordHash = isHashedPassword ? passwordHash : ValidationUtils.hashPassword(passwordHash);
    }

    // Getters
    public String getCin() { return cin; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getTel() { return tel; }
    public Year getAnneeUniversitaire() { return anneeUniversitaire; }
    public String getAdresse() { return adresse; }
    public String getMail() { return mail; }
    public String getPasswordHash() { return passwordHash; }
    
    /**
     * Verifies if the provided plain text password matches the stored hash.
     * Use this method for login authentication.
     * 
     * @param plainPassword The password entered by the user
     * @return true if password matches, false otherwise
     */
    public boolean verifyPassword(String plainPassword) {
        return ValidationUtils.verifyPassword(plainPassword, this.passwordHash);
    }

    // Setters
    public void setCin(String cin) { this.cin = cin; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setTel(String tel) { this.tel = tel; }
    public void setAnneeUniversitaire(Year anneeUniversitaire) { this.anneeUniversitaire = anneeUniversitaire; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setMail(String mail) { this.mail = mail; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    /**
     * Sets a new password (will be hashed automatically).
     * Use this for password changes.
     * 
     * @param newPassword The new plain text password
     */
    public void setPassword(String newPassword) {
        ValidationUtils.validatePassword(newPassword);
        this.passwordHash = ValidationUtils.hashPassword(newPassword);
    }

    @Override
    public String toString() {
        return "User{" +
                "cin='" + cin + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", tel='" + tel + '\'' +
                ", anneeUniversitaire=" + anneeUniversitaire +
                ", adresse='" + adresse + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}