package Models;

import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class User {
    protected String cin;
    protected String nom;
    protected String prenom;
    protected String tel;
    protected Year anneeUniversitaire;
    protected String adresse;
    protected String mail;

    // Expression régulière simple pour la validation d'email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
    	    "^[A-Z0-9._%+-]+@((gmail\\.com)|([A-Z0-9.-]+\\.tn))$",
    	    Pattern.CASE_INSENSITIVE
    	);
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
            if (EMAIL_PATTERN.matcher(mailInput).matches()) {
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
            if (EMAIL_PATTERN.matcher(mailInput).matches()) {
                this.mail = mailInput;
                mailValide = true;
            } else {
                System.err.println("Erreur: Format d'adresse mail invalide. Veuillez réessayer.");
            }
        }
    }

    // Constructeur paramétré (avec gestion des exceptions)
    public User(String cin, String nom, String prenom, String tel, Year anneeUniversitaire, String adresse, String mail) {
        // Validation des paramètres
        if (cin == null || cin.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de carte (CIN) ne peut pas être vide.");
        }
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide.");
        }
        if (anneeUniversitaire == null) {
            throw new IllegalArgumentException("L'année universitaire ne peut pas être nulle.");
        }
        if (mail == null || !EMAIL_PATTERN.matcher(mail).matches()) {
            throw new IllegalArgumentException("L'adresse mail est invalide.");
        }

        this.cin = cin;
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
        this.anneeUniversitaire = anneeUniversitaire;
        this.adresse = adresse;
        this.mail = mail;
    }

    // Getters
    public String getCin() { return cin; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getTel() { return tel; }
    public Year getAnneeUniversitaire() { return anneeUniversitaire; }
    public String getAdresse() { return adresse; }
    public String getMail() { return mail; }

    // Setters
    public void setCin(String cin) { this.cin = cin; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setTel(String tel) { this.tel = tel; }
    public void setAnneeUniversitaire(Year anneeUniversitaire) { this.anneeUniversitaire = anneeUniversitaire; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setMail(String mail) { this.mail = mail; }

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