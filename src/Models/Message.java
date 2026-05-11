package Models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Message - Représente un message privé entre un passager et un conducteur
 * @author Application
 */
public class Message {
    private String messageId;           // Identifiant unique du message
    private String senderCin;          // CIN de l'expéditeur
    private String senderName;         // Nom de l'expéditeur
    private String recipientCin;       // CIN du destinataire
    private String recipientName;      // Nom du destinataire
    private String content;            // Contenu du message
    private LocalDateTime timestamp;   // Horodatage du message
    private boolean isDeleted;         // Marque si le message a été supprimé
    private String trajetId;           // ID du trajet associé (optionnel)
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Constructeur paramétré
     */
    public Message(String messageId, String senderCin, String senderName, String recipientCin, 
                   String recipientName, String content, LocalDateTime timestamp, boolean isDeleted, String trajetId) {
        this.messageId = messageId;
        this.senderCin = senderCin;
        this.senderName = senderName;
        this.recipientCin = recipientCin;
        this.recipientName = recipientName;
        this.content = isDeleted ? "Message supprimé" : content;
        this.timestamp = timestamp;
        this.isDeleted = isDeleted;
        this.trajetId = trajetId;
    }
    
    /**
     * Constructeur pour créer un nouveau message
     */
    public Message(String messageId, String senderCin, String senderName, String recipientCin, 
                   String recipientName, String content, String trajetId) {
        this.messageId = messageId;
        this.senderCin = senderCin;
        this.senderName = senderName;
        this.recipientCin = recipientCin;
        this.recipientName = recipientName;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isDeleted = false;
        this.trajetId = trajetId;
    }
    
    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getSenderCin() {
        return senderCin;
    }
    
    public void setSenderCin(String senderCin) {
        this.senderCin = senderCin;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getRecipientCin() {
        return recipientCin;
    }
    
    public void setRecipientCin(String recipientCin) {
        this.recipientCin = recipientCin;
    }
    
    public String getRecipientName() {
        return recipientName;
    }
    
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.isDeleted = false;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void delete() {
        this.isDeleted = true;
        this.content = "Message supprimé";
    }
    
    public String getTrajetId() {
        return trajetId;
    }
    
    public void setTrajetId(String trajetId) {
        this.trajetId = trajetId;
    }
    
    /**
     * Formate le timestamp pour l'affichage
     */
    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }
    
    /**
     * Retourne l'heure au format HH:mm
     */
    public String getTimeOnly() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    /**
     * Retourne la date au format yyyy-MM-dd
     */
    public String getDateOnly() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", senderCin='" + senderCin + '\'' +
                ", senderName='" + senderName + '\'' +
                ", recipientCin='" + recipientCin + '\'' +
                ", recipientName='" + recipientName + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", isDeleted=" + isDeleted +
                ", trajetId='" + trajetId + '\'' +
                '}';
    }
}
