package Models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * ValidationUtils - Contient les expréssions régulières et les méthodes de validation.
 */
public class ValidationUtils {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // EXPRESSIONS RÉGULIÈRES (Patterns compilés pour de meilleures performances)
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Numéro de téléphone : exactement 8 chiffres
     * Regex : ^[0-9]{8}$
     */
    public static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");
    
    /**
     * CIN (Carte d'Identité) : exactement 8 chiffres
     * Regex : ^[0-9]{8}$
     */
    public static final Pattern CIN_PATTERN = Pattern.compile("^[0-9]{8}$");
    
    /**
     * Nom/Prénom : uniquement des lettres (incluant les accents français), espaces, tirets et apostrophes
     * Regex : ^[a-zA-ZÀ-ÿ\s'-]+$
     */
    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ\\s'-]+$");
    
    /**
     * Nom de véhicule : lettres ET chiffres autorisés, espaces, tirets et apostrophes
     * Regex : ^[a-zA-Z0-9À-ÿ\s'-]+$
     *
     * Pourquoi ? De nombreux modèles comportent des chiffres (ex : "Peugeot 308").
     */
    public static final Pattern VEHICLE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9À-ÿ\\s'-]+$");
    
    /**
     * Matricule tunisienne : 1 à 3 chiffres + "TU" + 4 chiffres
     * Regex : ^[0-9]{1,3}TU[0-9]{4}$
     */
    public static final Pattern MATRICULE_PATTERN = Pattern.compile("^[0-9]{1,3}TU[0-9]{4}$");
    
    /**
     * Email : domaine Gmail ou domaine tunisien (.tn)
     * Regex : ^[A-Z0-9._%+-]+@((gmail\.com)|([A-Z0-9.-]+\.tn))$
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Z0-9._%+-]+@((gmail\\.com)|([A-Z0-9.-]+\\.tn))$",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Mot de passe : politique de sécurité forte
     * Regex : ^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!]).{8,}$
     *
     * Explication :
     * - Au moins une minuscule, une majuscule, un chiffre et un caractère spécial
     * - Longueur minimale : 8 caractères
     */
    public static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$"
    );
    
    // ═══════════════════════════════════════════════════════════════════════════
    // MÉTHODES DE VALIDATION SIMPLES (retournent boolean)
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Valide un numéro de téléphone (8 chiffres).
     * @param phone Le numéro de téléphone à valider
     * @return true si valide, false sinon
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Valide un CIN (8 chiffres).
     * @param cin Le CIN à valider
     * @return true si valide, false sinon
     */
    public static boolean isValidCIN(String cin) {
        if (cin == null) return false;
        return CIN_PATTERN.matcher(cin.trim()).matches();
    }
    
    /**
     * Valide un nom ou prénom (lettres et accents).
     * @param name Le nom à valider
     * @return true si valide, false sinon
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    /**
     * Valide un nom de véhicule (lettres et chiffres autorisés).
     * @param vehicleName Le nom du véhicule
     * @return true si valide, false sinon
     */
    public static boolean isValidVehicleName(String vehicleName) {
        if (vehicleName == null || vehicleName.trim().isEmpty()) return false;
        return VEHICLE_NAME_PATTERN.matcher(vehicleName.trim()).matches();
    }
    
    /**
     * Valide une immatriculation tunisienne.
     * @param matricule L'immatriculation à valider
     * @return true si valide, false sinon
     */
    public static boolean isValidMatricule(String matricule) {
        if (matricule == null) return false;
        return MATRICULE_PATTERN.matcher(matricule.trim().toUpperCase()).matches();
    }
    
    /**
     * Valide un email (gmail ou domaine en .tn).
     * @param email L'email à valider
     * @return true si valide, false sinon
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Valide la force d'un mot de passe selon la politique définie.
     * @param password Le mot de passe à valider
     * @return true si respecte la politique, false sinon
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Retourne une analyse détaillée de la force du mot de passe.
     * Utile pour fournir un retour utilisateur lors de l'inscription.
     *
     * @param password Le mot de passe à analyser
     * @return Tableau de booléens : [hasLowercase, hasUppercase, hasDigit, hasSpecial, hasMinLength]
     */
    public static boolean[] getPasswordStrengthDetails(String password) {
        if (password == null) password = "";
        return new boolean[] {
            password.matches(".*[a-z].*"),           // contient une minuscule
            password.matches(".*[A-Z].*"),           // contient une majuscule
            password.matches(".*\\d.*"),             // contient un chiffre
            password.matches(".*[@#$%^&+=!].*"),     // contient un caractère spécial
            password.length() >= 8                    // longueur minimale
        };
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // MÉTHODES DE VALIDATION STRICTES (lancent IllegalArgumentException)
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Valide le téléphone et lance IllegalArgumentException en cas d'erreur.
     * @param phone Le numéro de téléphone
     * @throws IllegalArgumentException si invalide
     */
    public static void validatePhone(String phone) throws IllegalArgumentException {
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException(
                "Numéro de téléphone invalide: doit contenir exactement 8 chiffres. " +
                "Reçu: '" + phone + "'"
            );
        }
    }
    
    /**
     * Valide le CIN et lance IllegalArgumentException en cas d'erreur.
     * @param cin Le CIN
     * @throws IllegalArgumentException si invalide
     */
    public static void validateCIN(String cin) throws IllegalArgumentException {
        if (!isValidCIN(cin)) {
            throw new IllegalArgumentException(
                "CIN invalide: doit contenir exactement 8 chiffres. " +
                "Reçu: '" + cin + "'"
            );
        }
    }
    
    /**
     * Valide un nom et lance IllegalArgumentException si invalide.
     * @param name Le nom
     * @param fieldName Nom du champ pour le message d'erreur (ex: "Nom", "Prénom")
     * @throws IllegalArgumentException si invalide
     */
    public static void validateName(String name, String fieldName) throws IllegalArgumentException {
        if (!isValidName(name)) {
            throw new IllegalArgumentException(
                fieldName + " invalide: doit contenir uniquement des lettres. " +
                "Reçu: '" + name + "'"
            );
        }
    }
    
    /**
     * Valide le nom du véhicule et lance IllegalArgumentException si invalide.
     * @param vehicleName Le nom du véhicule (ex: "Golf 7", "Clio 4")
     * @param fieldName Nom du champ pour le message d'erreur
     * @throws IllegalArgumentException si invalide
     */
    public static void validateVehicleName(String vehicleName, String fieldName) throws IllegalArgumentException {
        if (!isValidVehicleName(vehicleName)) {
            throw new IllegalArgumentException(
                fieldName + " invalide: doit contenir uniquement des lettres et des chiffres. " +
                "Reçu: '" + vehicleName + "'"
            );
        }
    }
    
    /**
     * Valide l'immatriculation et lance IllegalArgumentException si invalide.
     * @param matricule L'immatriculation
     * @throws IllegalArgumentException si invalide
     */
    public static void validateMatricule(String matricule) throws IllegalArgumentException {
        if (!isValidMatricule(matricule)) {
            throw new IllegalArgumentException(
                "Matricule invalide: format attendu 'XXXTUXXXX' (ex: 123TU4567). " +
                "Reçu: '" + matricule + "'"
            );
        }
    }
    
    /**
     * Valide l'email et lance IllegalArgumentException si invalide.
     * @param email L'adresse email
     * @throws IllegalArgumentException si invalide
     */
    public static void validateEmail(String email) throws IllegalArgumentException {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException(
                "Email invalide: doit être @gmail.com ou @*.tn. " +
                "Reçu: '" + email + "'"
            );
        }
    }
    
    /**
     * Valide la force du mot de passe et lance IllegalArgumentException si non conforme.
     * @param password Le mot de passe à valider
     * @throws IllegalArgumentException si le mot de passe ne respecte pas la politique
     */
    public static void validatePassword(String password) throws IllegalArgumentException {
        if (!isValidPassword(password)) {
            boolean[] details = getPasswordStrengthDetails(password);
            StringBuilder sb = new StringBuilder("Mot de passe invalide. Requis: ");
            if (!details[4]) sb.append("min 8 caractères, ");
            if (!details[0]) sb.append("une minuscule, ");
            if (!details[1]) sb.append("une majuscule, ");
            if (!details[2]) sb.append("un chiffre, ");
            if (!details[3]) sb.append("un caractère spécial (@#$%^&+=!), ");
            throw new IllegalArgumentException(sb.toString().replaceAll(", $", ""));
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // MESSAGES D'ERREUR - Messages en français utilisés par l'UI
    // ═══════════════════════════════════════════════════════════════════════════
    
    public static final String PHONE_ERROR = "Le téléphone doit contenir exactement 8 chiffres";
    public static final String CIN_ERROR = "Le CIN doit contenir exactement 8 chiffres";
    public static final String NAME_ERROR = "Le nom doit contenir uniquement des lettres";
    public static final String PRENOM_ERROR = "Le prénom doit contenir uniquement des lettres";
    public static final String MATRICULE_ERROR = "Le matricule doit suivre le format: 123TU4567";
    public static final String EMAIL_ERROR = "L'email doit être @gmail.com ou @*.tn";
    public static final String PASSWORD_ERROR = "Le mot de passe doit contenir: 8+ caractères, majuscule, minuscule, chiffre, caractère spécial (@#$%^&+=!)";
    public static final String PASSWORD_MISMATCH_ERROR = "Les mots de passe ne correspondent pas";
    
    // ═══════════════════════════════════════════════════════════════════════════
    // MÉTHODES D'AIDE - Documentation et sécurité des mots de passe
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Affiche les patterns et leurs explications (utile pour la documentation ou le debug).
     */
    public static void printRegexDocumentation() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           PATTERNS DE VALIDATION (REGEX)                   ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ Téléphone:  ^[0-9]{8}$           → 8 chiffres exactement    ║");
        System.out.println("║ CIN:        ^[0-9]{8}$           → 8 chiffres exactement    ║");
        System.out.println("║ Nom:        ^[a-zA-ZÀ-ÿ\\s'-]+$  → Lettres + accents        ║");
        System.out.println("║ Matricule:  ^[0-9]{1,3}TU[0-9]{4}$ → 1-3 chiffres+TU+4 chiffres ║");
        System.out.println("║ Email:      ...@gmail.com ou @*.tn                         ║");
        System.out.println("║ Mot de passe: 8+ chars, maj, min, chiffre, spécial          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Hache un mot de passe avec SHA-256.
     *
     * EXPLICATION SÉCURITÉ :
     * - Il ne faut jamais stocker de mot de passe en clair.
     * - Le hachage est une fonction à sens unique : on ne peut pas retrouver
     *   le mot de passe d'origine à partir du haché.
     * - SHA-256 produit une chaîne hexadécimale d'une longueur fixe.
     *
     * @param password Mot de passe en clair
     * @return Haché SHA-256 sous forme hexadécimale, ou null si échec
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        
        try {
            // Obtenir l'instance MessageDigest pour SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Calcul du haché des octets du mot de passe
            byte[] hashBytes = md.digest(password.getBytes());
            
            // Conversion des octets en chaîne hexadécimale
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 devrait toujours être disponible
            System.err.println("Erreur: Algorithme SHA-256 non disponible: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Vérifie un mot de passe en clair contre un haché stocké.
     *
     * MÉTHODE : on hache le mot de passe fourni et on compare les deux hachés.
     *
     * @param plainPassword Mot de passe entré par l'utilisateur
     * @param storedHash Haché SHA-256 stocké
     * @return true si la comparaison réussit, false sinon
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        
        // Hacher l'entrée et comparer
        String hashedInput = hashPassword(plainPassword);
        return storedHash.equals(hashedInput);
    }
}
