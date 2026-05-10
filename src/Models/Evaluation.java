package Models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Evaluation - Représente l'évaluation d'un conducteur par un passager.
 * Note de 1 à 5 étoiles + commentaire libre.
 */
public class Evaluation {

    private String evaluationId;
    private String passagerCin;
    private String passagerName;
    private String conducteurCin;
    private String trajetId;
    private int rating; // 1-5
    private String comment;
    private LocalDateTime dateCreation;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Evaluation(String evaluationId, String passagerCin, String passagerName,
                      String conducteurCin, String trajetId, int rating, String comment,
                      LocalDateTime dateCreation) {
        this.evaluationId = evaluationId;
        this.passagerCin = passagerCin;
        this.passagerName = passagerName;
        this.conducteurCin = conducteurCin;
        this.trajetId = (trajetId != null) ? trajetId : "";
        this.rating = clampRating(rating);
        this.comment = (comment != null) ? comment : "";
        this.dateCreation = (dateCreation != null) ? dateCreation : LocalDateTime.now();
    }

    public Evaluation(String evaluationId, String passagerCin, String passagerName,
                      String conducteurCin, String trajetId, int rating, String comment) {
        this(evaluationId, passagerCin, passagerName, conducteurCin, trajetId, rating, comment, LocalDateTime.now());
    }

    private static int clampRating(int rating) {
        if (rating < 1) return 1;
        if (rating > 5) return 5;
        return rating;
    }

    public String getEvaluationId() { return evaluationId; }
    public String getPassagerCin() { return passagerCin; }
    public String getPassagerName() { return passagerName; }
    public String getConducteurCin() { return conducteurCin; }
    public String getTrajetId() { return trajetId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getDateCreation() { return dateCreation; }

    public void setEvaluationId(String evaluationId) { this.evaluationId = evaluationId; }
    public void setPassagerCin(String passagerCin) { this.passagerCin = passagerCin; }
    public void setPassagerName(String passagerName) { this.passagerName = passagerName; }
    public void setConducteurCin(String conducteurCin) { this.conducteurCin = conducteurCin; }
    public void setTrajetId(String trajetId) { this.trajetId = trajetId != null ? trajetId : ""; }
    public void setRating(int rating) { this.rating = clampRating(rating); }
    public void setComment(String comment) { this.comment = comment != null ? comment : ""; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getDateCreationAsString() {
        return dateCreation != null ? dateCreation.format(DATE_FORMATTER) : "";
    }

    public static LocalDateTime parseDate(String dateString) {
        try {
            return LocalDateTime.parse(dateString, DATE_FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    /**
     * Représentation des étoiles (★ et ☆) selon la note.
     */
    public String getStarsDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i <= rating ? "★" : "☆");
        }
        return sb.toString();
    }
}
