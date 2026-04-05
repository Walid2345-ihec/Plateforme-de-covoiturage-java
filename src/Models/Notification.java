package Models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Notification Model - Représente une notification pour un passager
 * Contient les informations sur l'acceptation ou refus du passager par un conducteur
 */
public class Notification {
    
    private String notificationId;
    private String passagerId;  // CIN du passager
    private String conducteurId; // CIN du conducteur
    private String trajetId;    // ID du trajet
    private String type;        // "ACCEPTATION", "REFUS", "ANNULATION"
    private String message;     // Description détaillée
    private LocalDateTime dateCreation;
    private boolean estLue;     // Si la notification a été lue
    
    // Format pour la sérialisation
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Constructeur complet
     */
    public Notification(String notificationId, String passagerId, String conducteurId, 
                       String trajetId, String type, String message, LocalDateTime dateCreation, 
                       boolean estLue) {
        this.notificationId = notificationId;
        this.passagerId = passagerId;
        this.conducteurId = conducteurId;
        this.trajetId = trajetId;
        this.type = type;
        this.message = message;
        this.dateCreation = dateCreation;
        this.estLue = estLue;
    }
    
    /**
     * Constructeur pour créer une nouvelle notification
     */
    public Notification(String notificationId, String passagerId, String conducteurId, 
                       String trajetId, String type, String message) {
        this.notificationId = notificationId;
        this.passagerId = passagerId;
        this.conducteurId = conducteurId;
        this.trajetId = trajetId;
        this.type = type;
        this.message = message;
        this.dateCreation = LocalDateTime.now();
        this.estLue = false;
    }
    
    // ============================================================
    // GETTERS & SETTERS
    // ============================================================
    
    public String getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
    
    public String getPassagerId() {
        return passagerId;
    }
    
    public void setPassagerId(String passagerId) {
        this.passagerId = passagerId;
    }
    
    public String getConducteurId() {
        return conducteurId;
    }
    
    public void setConducteurId(String conducteurId) {
        this.conducteurId = conducteurId;
    }
    
    public String getTrajetId() {
        return trajetId;
    }
    
    public void setTrajetId(String trajetId) {
        this.trajetId = trajetId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public boolean isEstLue() {
        return estLue;
    }
    
    public void setEstLue(boolean estLue) {
        this.estLue = estLue;
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Convertit la date en string pour CSV
     */
    public String getDateCreationAsString() {
        return dateCreation != null ? dateCreation.format(DATE_FORMATTER) : "";
    }
    
    /**
     * Parse une date depuis string
     */
    public static LocalDateTime parseDate(String dateString) {
        try {
            return LocalDateTime.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
    
    /**
     * Retourne le label du type de notification (avec emoji)
     */
    public String getTypeLabel() {
        return switch (type) {
            case "ACCEPTATION" -> "✓ Accepté";
            case "REFUS" -> "✗ Refusé";
            case "ANNULATION" -> "⊘ Annulé";
            default -> type;
        };
    }
    
    /**
     * Retourne la couleur du type (en hex)
     */
    public String getTypeColor() {
        return switch (type) {
            case "ACCEPTATION" -> "#27AE60"; // Vert
            case "REFUS" -> "#E74C3C"; // Rouge
            case "ANNULATION" -> "#F39C12"; // Orange
            default -> "#95A5A6"; // Gris
        };
    }
    
    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", passagerId='" + passagerId + '\'' +
                ", conducteurId='" + conducteurId + '\'' +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", dateCreation=" + dateCreation +
                ", estLue=" + estLue +
                '}';
    }
}
