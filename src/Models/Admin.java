package Models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Admin - Représente un administrateur de la plateforme.
 * Hérite de User pour pouvoir être utilisé dans le système de messagerie commun,
 * mais ses constructeurs n'appliquent PAS les mêmes validations strictes que les
 * utilisateurs ordinaires (CIN potentiellement non numérique, etc.).
 */
public class Admin extends User {

    private LocalDateTime dateCreation;
    private String role; // ex: "SUPER_ADMIN", "MODERATOR"

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructeur pour création depuis le CSV. Les champs CIN et password sont
     * pris tels quels (le mot de passe est déjà hashé).
     */
    public Admin(String cin, String nom, String prenom, String mail, String passwordHash,
                 String role, LocalDateTime dateCreation) {
        // Réutilise le constructeur "loading from CSV" de User pour éviter les
        // validations strictes (CIN regex, email, etc.).
        super(cin, nom, prenom, "00000000", java.time.Year.of(2024),
                "—", mail != null ? mail : "admin@plateforme.tn",
                passwordHash != null ? passwordHash : "", true);
        this.role = (role != null) ? role : "ADMIN";
        this.dateCreation = (dateCreation != null) ? dateCreation : LocalDateTime.now();
    }

    public Admin(String cin, String nom, String prenom, String mail, String passwordHash,
                 String role) {
        this(cin, nom, prenom, mail, passwordHash, role, LocalDateTime.now());
    }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

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
}
