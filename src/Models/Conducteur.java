package Models;

import java.time.Year;
import java.util.Scanner;

public class Conducteur extends User {
    private String nomVoiture;
    private String marqueVoiture;
    private String matricule; // id
    private int placesDisponibles;
    
    // Constructeur par défaut (interactif)
    public Conducteur() {
        super();
        Scanner sc = new Scanner(System.in);
        System.out.println("--- Saisie des informations Conducteur ---");

        System.out.println("Entrez le nom de la voiture :");
        this.nomVoiture = sc.nextLine();

        System.out.println("Entrez la marque de la voiture :");
        this.marqueVoiture = sc.nextLine();

        System.out.println("Entrez le matricule (ID) de la voiture :");
        this.matricule = sc.nextLine();

        // Gestion des exceptions pour les places disponibles
        boolean placesValides = false;
        while (!placesValides) {
            System.out.println("Entrez le nombre de places disponibles :");
            try {
                this.placesDisponibles = Integer.parseInt(sc.nextLine());
                if (this.placesDisponibles < 1) {
                    throw new IllegalArgumentException("Le nombre de places doit être au moins 1.");
                }
                placesValides = true;
            } catch (NumberFormatException e) {
                System.err.println("Erreur: Veuillez entrer un nombre entier valide.");
            } catch (IllegalArgumentException e) {
                System.err.println("Erreur: " + e.getMessage());
            }
        }
    }
    
    
    // Constructeur paramétré (avec validation regex)
    public Conducteur(String cin, String nom, String prenom, String tel, Year anneeUniversitaire, String adresse, String mail, String password, String nomVoiture, String marqueVoiture, String matricule, int placesDisponibles) {
        super(cin, nom, prenom, tel, anneeUniversitaire, adresse, mail, password); // La validation de User est faite ici

        // ═══════════════════════════════════════════════════════════════════════════
        // VALIDATION spécifique au Conducteur
        // ═══════════════════════════════════════════════════════════════════════════
        
        // Valider nom de voiture: lettres et chiffres autorisés
        ValidationUtils.validateVehicleName(nomVoiture, "Nom de voiture");
        
        // Valider marque de voiture: lettres et chiffres autorisés
        if (marqueVoiture != null && !marqueVoiture.trim().isEmpty()) {
            ValidationUtils.validateVehicleName(marqueVoiture, "Marque de voiture");
        }
        
        // Valider matricule: format XXXTUXXXX (1-3 digits + TU + 4 digits)
        ValidationUtils.validateMatricule(matricule);
        
        // Valider places disponibles
        if (placesDisponibles < 1) {
            throw new IllegalArgumentException("Le nombre de places disponibles doit être au moins 1.");
        }

        this.nomVoiture = nomVoiture;
        this.marqueVoiture = marqueVoiture;
        this.matricule = matricule.toUpperCase();
        this.placesDisponibles = placesDisponibles;
    }
    
    /**
     * Constructeur pour le chargement des données à partir du CSV
     */
    public Conducteur(String cin, String nom, String prenom, String tel, Year anneeUniversitaire, 
                      String adresse, String mail, String passwordHash, boolean isHashedPassword,
                      String nomVoiture, String marqueVoiture, String matricule, int placesDisponibles) {
        super(cin, nom, prenom, tel, anneeUniversitaire, adresse, mail, passwordHash, isHashedPassword);
        this.nomVoiture = nomVoiture;
        this.marqueVoiture = marqueVoiture;
        this.matricule = matricule;
        this.placesDisponibles = placesDisponibles;
    }

    // Nouveau constructeur qui accepte un objet User et demande uniquement
    // les informations spécifiques au Conducteur.
    public Conducteur(String cin) {
        super(cin);
        Scanner sc = new Scanner(System.in);
        System.out.println("--- Saisie des informations Conducteur ---");

        System.out.println("Entrez le nom de la voiture :");
        this.nomVoiture = sc.nextLine();

        System.out.println("Entrez la marque de la voiture :");
        this.marqueVoiture = sc.nextLine();

        System.out.println("Entrez le matricule (ID) de la voiture :");
        this.matricule = sc.nextLine();

        // Gestion des exceptions pour les places disponibles
        boolean placesValides = false;
        while (!placesValides) {
            System.out.println("Entrez le nombre de places disponibles :");
            try {
                this.placesDisponibles = Integer.parseInt(sc.nextLine());
                if (this.placesDisponibles < 1) {
                    throw new IllegalArgumentException("Le nombre de places doit être au moins 1.");
                }
                placesValides = true;
            } catch (NumberFormatException e) {
                System.err.println("Erreur: Veuillez entrer un nombre entier valide.");
            } catch (IllegalArgumentException e) {
                System.err.println("Erreur: " + e.getMessage());
            }
        }
    }

    // Getters
    public String getNomVoiture() { return nomVoiture; }
    public String getMarqueVoiture() { return marqueVoiture; }
    public String getMatricule() { return matricule; }
    public int getPlacesDisponibles() { return placesDisponibles; }

    // Setters
    public void setNomVoiture(String nomVoiture) { this.nomVoiture = nomVoiture; }
    public void setMarqueVoiture(String marqueVoiture) { this.marqueVoiture = marqueVoiture; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public void setPlacesDisponibles(int placesDisponibles) { this.placesDisponibles = placesDisponibles; }

    @Override
    public String toString() {
        return "Conducteur{" +
                super.toString() +
                ", nomVoiture='" + nomVoiture + '\'' +
                ", marqueVoiture='" + marqueVoiture + '\'' +
                ", matricule='" + matricule + '\'' +
                ", placesDisponibles=" + placesDisponibles +
                '}';
    }
}