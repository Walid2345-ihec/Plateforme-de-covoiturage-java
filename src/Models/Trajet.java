package Models;

import java.time.Duration;
import java.util.Scanner;
import java.time.format.DateTimeParseException;

public class Trajet {
    private String departTrajet;
    private String arriveeTrajet;
    private Duration dureeTrajet;
    private float prix;
    private boolean trajet_valide;
    private String statusTrajet; // remplacé par String
    private Conducteur conducteur;
    private Passager passager;

    // ==================== STATUS CONSTANTS ====================
    /**
     * PENDING: Trajet available for reservation (no passenger assigned)
     * PENDING_APPROVAL: Passenger has requested reservation, waiting for driver approval
     * IN_PROGRESS: Driver accepted the passenger, trajet is confirmed
     * FINISHED: Trajet completed
     */
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_FINISHED = "FINISHED";
    
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
            System.out.println("Entrez le statut du trajet (PENDING, PENDING_APPROVAL, IN_PROGRESS, FINISHED) :");
            String statusInput = sc.nextLine().toUpperCase();
            if (isValidStatus(statusInput)) {
                this.statusTrajet = statusInput;
                statusValide = true;
            } else {
                System.err.println("Erreur: Statut invalide. Veuillez choisir parmi PENDING, PENDING_APPROVAL, IN_PROGRESS, ou FINISHED.");
            }
        }
        
        
        System.out.println("Entrez le prix du trajet par personne:");
        this.prix = sc.nextFloat();
        
        this.conducteur = null;
        this.passager = null;
        
    }

    // Constructeur paramétré (avec validation)
    public Trajet(String departTrajet, String arriveeTrajet, Duration dureeTrajet, String statusTrajet, Float prix, Conducteur conducteur, Passager passager) {
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
        if (!isValidStatus(statusTrajet)) {
            throw new IllegalArgumentException("Le statut du trajet doit être PENDING, PENDING_APPROVAL, IN_PROGRESS ou FINISHED.");
        }

        this.departTrajet = departTrajet;
        this.arriveeTrajet = arriveeTrajet;
        this.dureeTrajet = dureeTrajet;
        this.statusTrajet = statusTrajet;
        this.prix = prix;
        this.conducteur = conducteur;
        this.passager = passager;
        this.trajet_valide = false;
    }
    
    // Helper method to validate status
    private static boolean isValidStatus(String status) {
        return status != null && (
            status.equals(STATUS_PENDING) || 
            status.equals(STATUS_PENDING_APPROVAL) || 
            status.equals(STATUS_IN_PROGRESS) || 
            status.equals(STATUS_FINISHED)
        );
    }

    // Getters
    public String getDepartTrajet() { return departTrajet; }
    public String getArriveeTrajet() { return arriveeTrajet; }
    public Duration getDureeTrajet() { return dureeTrajet; }
    public String getStatusTrajet() { return statusTrajet; }
    public boolean isTrajet_valide() { return trajet_valide; }
    public Conducteur getConducteur() { return conducteur; }
    public Passager getPassager() { return passager; }
    public float getPrix() { return prix; }
    
    

    // Setters
    public void setDepartTrajet(String departTrajet) { this.departTrajet = departTrajet; }
    public void setArriveeTrajet(String arriveeTrajet) { this.arriveeTrajet = arriveeTrajet; }
    public void setDureeTrajet(Duration dureeTrajet) { this.dureeTrajet = dureeTrajet; }
    public void setStatusTrajet(String statusTrajet) {
        if (isValidStatus(statusTrajet)) {
            this.statusTrajet = statusTrajet;
        } else {
            throw new IllegalArgumentException("Statut invalide. Utilisez PENDING, PENDING_APPROVAL, IN_PROGRESS ou FINISHED.");
        }
    }
    
    // Convenience methods for status checks
    public boolean isPending() { return STATUS_PENDING.equals(statusTrajet); }
    public boolean isPendingApproval() { return STATUS_PENDING_APPROVAL.equals(statusTrajet); }
    public boolean isInProgress() { return STATUS_IN_PROGRESS.equals(statusTrajet); }
    public boolean isFinished() { return STATUS_FINISHED.equals(statusTrajet); }

    public void setTrajet_valide(boolean trajet_valide) { this.trajet_valide = trajet_valide; }
    public void setConducteur(Conducteur conducteur) { this.conducteur = conducteur; }
    public void setPassager(Passager passager) { this.passager = passager; }
    public void setPrix(float prix) { this.prix = prix; } 
    
    

    @Override
    public String toString() {
        return "Trajet{" +
                "departTrajet='" + departTrajet + '\'' +
                ", arriveeTrajet='" + arriveeTrajet + '\'' +
                ", dureeTrajet=" + dureeTrajet.toMinutes() + " minutes" +
                ", statusTrajet=" + statusTrajet +
                ", trajet valide=" + trajet_valide +
                ", prix=" + prix +
                ", conducteur=" + (conducteur != null ? conducteur.getNom() + " " + conducteur.getPrenom() : "N/A") +
                ", passager=" + (passager != null ? passager.getNom() + " " + passager.getPrenom() : "N/A") +
                '}';
    }
}