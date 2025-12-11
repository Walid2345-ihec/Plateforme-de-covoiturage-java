package Models;

import java.time.Duration;
import java.util.Scanner;
import java.time.format.DateTimeParseException;

public class Trajet {
    private String departTrajet;
    private String arriveeTrajet;
    private Duration dureeTrajet;
    private boolean trajet_valide;
    private String statusTrajet; // remplacé par String
    private Conducteur conducteur;
    private Passager passager;

    // Constructeur par défaut (interactif)
    public Trajet() {
        Scanner sc = new Scanner(System.in);
        this.trajet_valide = false;
        System.out.println("--- Saisie des informations Trajet ---");

        System.out.println("Entrez le point de départ du trajet :");
        this.departTrajet = sc.nextLine();

        System.out.println("Entrez le point d'arrivée du trajet :");
        this.arriveeTrajet = sc.nextLine();

        // Gestion des exceptions pour la durée
        boolean dureeValide = false;
        while (!dureeValide) {
            System.out.println("Entrez la durée du trajet :");
            String dureeInput = sc.nextLine();
            try {
                this.dureeTrajet = Duration.parse(dureeInput);
                dureeValide = true;
            } catch (DateTimeParseException e) {
                System.err.println("Erreur: Format de durée invalide. Veuillez utiliser le format ISO-8601 (ex: PT1H30M).");
            }
        }

        // Saisie du statut (simplifiée pour l'interactivité)
        boolean statusValide = false;
        while (!statusValide) {
            System.out.println("Entrez le statut du trajet (PENDING, IN_PROGRESS, FINISHED) :");
            String statusInput = sc.nextLine().toUpperCase();
            if (statusInput.equals("PENDING") || statusInput.equals("IN_PROGRESS") || statusInput.equals("FINISHED")) {
                this.statusTrajet = statusInput;
                statusValide = true;
            } else {
                System.err.println("Erreur: Statut invalide. Veuillez choisir parmi PENDING, IN_PROGRESS, ou FINISHED.");
            }
        }
        // Initialisation des relations (peut être null ou interactif si nécessaire)
        System.out.println("Voulez-vous créer un conducteur pour ce trajet ? (oui/non)");
        if (sc.nextLine().trim().equalsIgnoreCase("oui")) {
            this.conducteur = new Conducteur();
        }

        System.out.println("Voulez-vous créer un passager pour ce trajet ? (oui/non)");
        if (sc.nextLine().trim().equalsIgnoreCase("oui")) {
            this.passager = new Passager();
        }
    }

    // Constructeur paramétré (avec validation)
    public Trajet(String departTrajet, String arriveeTrajet, Duration dureeTrajet, String statusTrajet, Conducteur conducteur, Passager passager) {
        // Validation des paramètres
        if (departTrajet == null || departTrajet.trim().isEmpty()) {
            throw new IllegalArgumentException("Le point de départ ne peut pas être vide.");
        }
        if (arriveeTrajet == null || arriveeTrajet.trim().isEmpty()) {
            throw new IllegalArgumentException("Le point d'arrivée ne peut pas être vide.");
        }
        if (dureeTrajet == null || dureeTrajet.isNegative() || dureeTrajet.isZero()) {
            throw new IllegalArgumentException("La durée du trajet doit être positive.");
        }
        if (statusTrajet == null || 
            !(statusTrajet.equals("PENDING") || statusTrajet.equals("IN_PROGRESS") || statusTrajet.equals("FINISHED"))) {
            throw new IllegalArgumentException("Le statut du trajet doit être PENDING, IN_PROGRESS ou FINISHED.");
        }

        this.departTrajet = departTrajet;
        this.arriveeTrajet = arriveeTrajet;
        this.dureeTrajet = dureeTrajet;
        this.statusTrajet = statusTrajet;
        this.conducteur = conducteur;
        this.passager = passager;
        this.trajet_valide = false;
    }

    // Getters
    public String getDepartTrajet() { return departTrajet; }
    public String getArriveeTrajet() { return arriveeTrajet; }
    public Duration getDureeTrajet() { return dureeTrajet; }
    public String getStatusTrajet() { return statusTrajet; }
    public boolean isTrajet_valide() { return trajet_valide; }
    public Conducteur getConducteur() { return conducteur; }
    public Passager getPassager() { return passager; }
    

    // Setters
    public void setDepartTrajet(String departTrajet) { this.departTrajet = departTrajet; }
    public void setArriveeTrajet(String arriveeTrajet) { this.arriveeTrajet = arriveeTrajet; }
    public void setDureeTrajet(Duration dureeTrajet) { this.dureeTrajet = dureeTrajet; }
    public void setStatusTrajet(String statusTrajet) {
        if (statusTrajet != null && 
            (statusTrajet.equals("PENDING") || statusTrajet.equals("IN_PROGRESS") || statusTrajet.equals("FINISHED"))) {
            this.statusTrajet = statusTrajet;
        } else {
            throw new IllegalArgumentException("Statut invalide. Utilisez PENDING, IN_PROGRESS ou FINISHED.");
        }
    }

    public void setTrajet_valide(boolean trajet_valide) { this.trajet_valide = trajet_valide; }
    public void setConducteur(Conducteur conducteur) { this.conducteur = conducteur; }
    public void setPassager(Passager passager) { this.passager = passager; }
    

    @Override
    public String toString() {
        return "Trajet{" +
                "departTrajet='" + departTrajet + '\'' +
                ", arriveeTrajet='" + arriveeTrajet + '\'' +
                ", dureeTrajet=" + dureeTrajet.toMinutes() + " minutes" +
                ", statusTrajet=" + statusTrajet +
                ", trajet valide=" + trajet_valide +
                ", conducteur=" + (conducteur != null ? conducteur.getNom() + " " + conducteur.getPrenom() : "N/A") +
                ", passager=" + (passager != null ? passager.getNom() + " " + passager.getPrenom() : "N/A") +
                '}';
    }
}