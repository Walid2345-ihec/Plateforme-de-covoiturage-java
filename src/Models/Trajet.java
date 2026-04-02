// src/Models/Trajet.java
package Models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.Vector;

public class Trajet {
    private String departTrajet;
    private String arriveeTrajet;
    private Duration dureeTrajet;
    private float prix;
    private boolean trajet_valide;
    private String statusTrajet; // remplacé par String
    private Conducteur conducteur;
    // Remplacement du champ unique passager par des listes pour support multi-passagers
    private Vector<Passager> passagersAcceptes = new Vector<>();
    private Vector<Passager> passagersDemandes = new Vector<>();
    private int maxPlaces = 1; // capacité par défaut
    
    // ==================== DATE AND TIME FIELDS ====================
    private LocalDateTime startDateTime;  // Date and time when the ride starts (ISO 8601)
    private LocalDateTime endDateTime;    // Date and time when the ride ends (ISO 8601)
    private String weeklySchedule = "";   // Weekly schedule: "MON:09:00-17:00|TUE:09:00-17:00|..."
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // ==================== STATUS CONSTANTS ====================
    /**
     * PENDING: Trajet available for reservation (no passenger assigned)
     * PENDING_APPROVAL: Passenger has requested reservation, waiting for driver approval
     * IN_PROGRESS: Driver accepted one or more passengers, trajet is confirmed
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
        sc.nextLine();

        System.out.println("Entrez le nombre maximal de places pour ce trajet:");
        try {
            this.maxPlaces = Integer.parseInt(sc.nextLine());
            if (this.maxPlaces < 1) this.maxPlaces = 1;
        } catch (NumberFormatException e) {
            this.maxPlaces = 1;
        }

        this.conducteur = null;
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
        this.trajet_valide = false;

        // Si on reçoit un passager dans l'ancien format, on l'ajoute aux acceptés
        if (passager != null) {
            this.passagersAcceptes.add(passager);
        }

        // Default maxPlaces: use conducteur placesDisponibles if available
        if (conducteur != null) {
            try {
                this.maxPlaces = conducteur.getPlacesDisponibles();
                if (this.maxPlaces < 1) this.maxPlaces = 1;
            } catch (Exception e) {
                this.maxPlaces = 1;
            }
        }
    }

    // Nouveau constructeur utile pour loader CSV étendu
    public Trajet(String departTrajet, String arriveeTrajet, Duration dureeTrajet, String statusTrajet, Float prix, Conducteur conducteur, int maxPlaces) {
        this(departTrajet, arriveeTrajet, dureeTrajet, statusTrajet, prix, conducteur, (Passager) null);
        if (maxPlaces > 0) this.maxPlaces = maxPlaces;
    }

    // Constructeur avec date/time pour création UI
    public Trajet(String departTrajet, String arriveeTrajet, Duration dureeTrajet, String statusTrajet, Float prix, 
                  Conducteur conducteur, LocalDateTime startDateTime, LocalDateTime endDateTime, int maxPlaces) {
        // Validation des paramètres
        if (departTrajet == null || departTrajet.trim().isEmpty()) {
            throw new IllegalArgumentException("Le point de départ ne peut pas être vide.");
        }
        if (arriveeTrajet == null || arriveeTrajet.trim().isEmpty()) {
            throw new IllegalArgumentException("Le point d'arrivée ne peut pas être vide.");
        }
        if (startDateTime == null) {
            throw new IllegalArgumentException("La date/heure de départ est requise.");
        }
        if (endDateTime == null) {
            throw new IllegalArgumentException("La date/heure d'arrivée est requise.");
        }
        if (startDateTime.isAfter(endDateTime) || startDateTime.equals(endDateTime)) {
            throw new IllegalArgumentException("La date/heure de début doit être avant la date/heure de fin.");
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
        this.trajet_valide = false;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;

        // Default maxPlaces: use conducteur placesDisponibles if available
        if (conducteur != null) {
            try {
                this.maxPlaces = conducteur.getPlacesDisponibles();
                if (this.maxPlaces < 1) this.maxPlaces = 1;
            } catch (Exception e) {
                this.maxPlaces = 1;
            }
        }
        if (maxPlaces > 0) this.maxPlaces = maxPlaces;
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
    /**
     * Compatibility getter: returns the first accepted passager or null
     */
    @Deprecated
    public Passager getPassager() { return passagersAcceptes.isEmpty() ? null : passagersAcceptes.get(0); }
    public float getPrix() { return prix; }
    public int getMaxPlaces() { return maxPlaces; }
    public Vector<Passager> getPassagersAcceptes() { return passagersAcceptes; }
    public Vector<Passager> getPassagersDemandes() { return passagersDemandes; }
    
    // ==================== DATE AND TIME GETTERS ====================
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    
    /**
     * Returns start date-time as ISO 8601 string for CSV storage
     */
    public String getStartDateTimeString() {
        return startDateTime != null ? startDateTime.format(DATE_TIME_FORMATTER) : "";
    }
    
    /**
     * Returns end date-time as ISO 8601 string for CSV storage
     */
    public String getEndDateTimeString() {
        return endDateTime != null ? endDateTime.format(DATE_TIME_FORMATTER) : "";
    }


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
    
    // ==================== DATE AND TIME SETTERS ====================
    public void setStartDateTime(LocalDateTime startDateTime) {
        if (startDateTime == null) {
            throw new IllegalArgumentException("La date/heure de départ ne peut pas être null.");
        }
        if (endDateTime != null && startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("La date/heure de début doit être avant la date/heure de fin.");
        }
        this.startDateTime = startDateTime;
    }
    
    public void setEndDateTime(LocalDateTime endDateTime) {
        if (endDateTime == null) {
            throw new IllegalArgumentException("La date/heure d'arrivée ne peut pas être null.");
        }
        if (startDateTime != null && endDateTime.isBefore(startDateTime)) {
            throw new IllegalArgumentException("La date/heure de fin doit être après la date/heure de début.");
        }
        this.endDateTime = endDateTime;
    }
    
    /**
     * Sets date/time from ISO 8601 strings (used for CSV loading)
     */
    public static LocalDateTime parseDateTime(String dateTimeString) throws DateTimeParseException {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString.trim(), DATE_TIME_FORMATTER);
    }

    // Convenience methods for status checks
    public boolean isPending() { return STATUS_PENDING.equals(statusTrajet); }
    public boolean isPendingApproval() { return STATUS_PENDING_APPROVAL.equals(statusTrajet); }
    public boolean isInProgress() { return STATUS_IN_PROGRESS.equals(statusTrajet); }
    public boolean isFinished() { return STATUS_FINISHED.equals(statusTrajet); }

    public void setTrajet_valide(boolean trajet_valide) { this.trajet_valide = trajet_valide; }

    // Mise à jour: affecte le conducteur et met à jour le statut
    public void setConducteur(Conducteur conducteur) {
        this.conducteur = conducteur;
        // Si un conducteur est assigné et le trajet était PENDING, on passe en PENDING_APPROVAL
        if (conducteur != null && isPending()) {
            setStatusTrajet(STATUS_PENDING_APPROVAL);
        }
        // Si le conducteur est retiré et que le statut était PENDING_APPROVAL, on remet en PENDING
        else if (conducteur == null && isPendingApproval()) {
            setStatusTrajet(STATUS_PENDING);
        }

        // Synchroniser capacité par défaut avec conducteur
        if (conducteur != null) {
            try {
                this.maxPlaces = conducteur.getPlacesDisponibles();
                if (this.maxPlaces < 1) this.maxPlaces = 1;
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Ajoute une demande de réservation par un passager.
     * Retourne true si la demande a été ajoutée, false si déjà présente.
     */
    public boolean addDemand(Passager p) {
        if (p == null) return false;
        // Ne pas ajouter si déjà accepté
        for (Passager ap : passagersAcceptes) {
            if (ap.getCin().equals(p.getCin())) return false;
        }
        for (Passager dp : passagersDemandes) {
            if (dp.getCin().equals(p.getCin())) return false;
        }
        passagersDemandes.add(p);
        // Mettre le statut en attente d'approbation
        if (isPending()) setStatusTrajet(STATUS_PENDING_APPROVAL);
        return true;
    }

    /**
     * Retire une demande en attente.
     */
    public boolean removeDemand(Passager p) {
        if (p == null) return false;
        return passagersDemandes.removeIf(pp -> pp.getCin().equals(p.getCin()));
    }

    /**
     * Accepte un passager si des places sont disponibles.
     * Retourne true si accepté, false si plein ou passager non en attente.
     */
    public boolean acceptPassenger(Passager p) {
        if (p == null) return false;
        // Vérifier si déjà accepté
        for (Passager ap : passagersAcceptes) {
            if (ap.getCin().equals(p.getCin())) return false;
        }
        // Vérifier présence en demandes (si applicable) - on autorise acceptation même sans demande si besoin
        boolean wasInDemande = false;
        for (Passager dp : passagersDemandes) {
            if (dp.getCin().equals(p.getCin())) { wasInDemande = true; break; }
        }
        // Vérifier capacité
        if (getAvailablePlaces() <= 0) {
            return false; // complet
        }
        // Ajouter aux acceptés
        passagersAcceptes.add(p);
        // Retirer des demandes si présent
        if (wasInDemande) removeDemand(p);
        // Mettre à jour statut
        if (!passagersAcceptes.isEmpty()) setStatusTrajet(STATUS_IN_PROGRESS);
        return true;
    }

    /**
     * Refuse une demande (retire de la file d'attente)
     */
    public boolean refusePassenger(Passager p) {
        return removeDemand(p);
    }

    /**
     * Annule une acceptation (libère une place)
     */
    public boolean removeAccepted(Passager p) {
        boolean removed = passagersAcceptes.removeIf(pp -> pp.getCin().equals(p.getCin()));
        if (passagersAcceptes.isEmpty() && conducteur == null) {
            setStatusTrajet(STATUS_PENDING);
        }
        return removed;
    }

    /**
     * Retourne le nombre de places disponibles restantes pour ce trajet
     */
    public int getAvailablePlaces() {
        return Math.max(0, maxPlaces - passagersAcceptes.size());
    }

    public boolean isFull() { return getAvailablePlaces() <= 0; }

    public void setPrix(float prix) { this.prix = prix; }
    public void setMaxPlaces(int maxPlaces) { if (maxPlaces > 0) this.maxPlaces = maxPlaces; }

    /**
     * Sérialisation helper : renvoie CINs des acceptés séparés par des virgules
     */
    public String getPassagersAcceptesCINs() {
        if (passagersAcceptes.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < passagersAcceptes.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(passagersAcceptes.get(i).getCin());
        }
        return sb.toString();
    }

    /**
     * Sérialisation helper : renvoie CINs des demandes séparés par des virgules
     */
    public String getPassagersDemandesCINs() {
        if (passagersDemandes.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < passagersDemandes.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(passagersDemandes.get(i).getCin());
        }
        return sb.toString();
    }

    // ==================== WEEKLY SCHEDULE GETTERS/SETTERS ====================
    /**
     * Get the weekly schedule as a formatted string
     * Format: "MON:09:00-17:00|TUE:09:00-17:00|..."
     */
    public String getWeeklySchedule() {
        return weeklySchedule != null ? weeklySchedule : "";
    }

    /**
     * Set the weekly schedule
     * Format: "MON:09:00-17:00|TUE:09:00-17:00|..."
     */
    public void setWeeklySchedule(String weeklySchedule) {
        this.weeklySchedule = weeklySchedule != null ? weeklySchedule : "";
    }

    @Override
    public String toString() {
        return "Trajet{" +
                "departTrajet='" + departTrajet + '\'' +
                ", arriveeTrajet='" + arriveeTrajet + '\'' +
                ", dureeTrajet=" + (dureeTrajet != null ? dureeTrajet.toMinutes() + " minutes" : "N/A") +
                ", statusTrajet=" + statusTrajet +
                ", trajet valide=" + trajet_valide +
                ", prix=" + prix +
                ", conducteur=" + (conducteur != null ? conducteur.getNom() + " " + conducteur.getPrenom() : "N/A") +
                ", places=" + passagersAcceptes.size() + "/" + maxPlaces +
                ", demandes=" + passagersDemandes.size() +
                ", départ=" + (startDateTime != null ? startDateTime.format(DATE_TIME_FORMATTER) : "N/A") +
                ", arrivée=" + (endDateTime != null ? endDateTime.format(DATE_TIME_FORMATTER) : "N/A") +
                '}';
    }
}
