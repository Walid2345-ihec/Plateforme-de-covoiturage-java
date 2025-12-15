package Models;

import java.util.regex.Pattern;

/**
 * ValidationUtils - Utility class containing regular expressions for data validation
 * 
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║                    REGULAR EXPRESSIONS REFERENCE GUIDE                       ║
 * ╠══════════════════════════════════════════════════════════════════════════════╣
 * ║                                                                              ║
 * ║  1. PHONE NUMBER                                                             ║
 * ║  ─────────────────────────────────────────────────────────────────────────── ║
 * ║  Description: Must be exactly 8 digits                                       ║
 * ║  Regex:       ^[0-9]{8}$                                                     ║
 * ║  Explanation:                                                                ║
 * ║    • ^        → Start of string                                              ║
 * ║    • [0-9]    → Any digit from 0 to 9                                        ║
 * ║    • {8}      → Exactly 8 occurrences                                        ║
 * ║    • $        → End of string                                                ║
 * ║  Examples:    ✓ 98765432  ✓ 12345678  ✗ 1234567  ✗ 123456789                 ║
 * ║                                                                              ║
 * ║  2. CIN NUMBER (Carte d'Identité Nationale)                                  ║
 * ║  ─────────────────────────────────────────────────────────────────────────── ║
 * ║  Description: Must be exactly 8 digits                                       ║
 * ║  Regex:       ^[0-9]{8}$                                                     ║
 * ║  Explanation:                                                                ║
 * ║    • ^        → Start of string                                              ║
 * ║    • [0-9]    → Any digit from 0 to 9                                        ║
 * ║    • {8}      → Exactly 8 occurrences                                        ║
 * ║    • $        → End of string                                                ║
 * ║  Examples:    ✓ 07654321  ✓ 12345678  ✗ 1234567  ✗ 12345678A                 ║
 * ║                                                                              ║
 * ║  3. NAME / LAST NAME                                                         ║
 * ║  ─────────────────────────────────────────────────────────────────────────── ║
 * ║  Description: Only alphabetic characters (including French accents)         ║
 * ║  Regex:       ^[a-zA-ZÀ-ÿ\\s'-]+$                                            ║
 * ║  Explanation:                                                                ║
 * ║    • ^        → Start of string                                              ║
 * ║    • [a-zA-Z] → Any letter (uppercase or lowercase)                          ║
 * ║    • À-ÿ      → French accented characters (é, è, ê, ë, à, ù, ç, etc.)      ║
 * ║    • \\s      → Whitespace (for compound names like "Ben Ali")               ║
 * ║    • '-       → Hyphen and apostrophe (for names like "O'Brien", "Al-Farsi") ║
 * ║    • +        → One or more characters                                       ║
 * ║    • $        → End of string                                                ║
 * ║  Examples:    ✓ Mohamed  ✓ Ben Ali  ✓ François  ✗ Ali123  ✗ @Ahmed           ║
 * ║                                                                              ║
 * ║  4. CAR MATRICULATION (Tunisian Format)                                      ║
 * ║  ─────────────────────────────────────────────────────────────────────────── ║
 * ║  Description: Up to 3 digits + "TU" + 4 digits                               ║
 * ║  Regex:       ^[0-9]{1,3}TU[0-9]{4}$                                         ║
 * ║  Explanation:                                                                ║
 * ║    • ^        → Start of string                                              ║
 * ║    • [0-9]    → Any digit                                                    ║
 * ║    • {1,3}    → Between 1 and 3 occurrences (e.g., 1, 12, 123)               ║
 * ║    • TU       → Literal "TU" (Tunisia code)                                  ║
 * ║    • [0-9]    → Any digit                                                    ║
 * ║    • {4}      → Exactly 4 occurrences                                        ║
 * ║    • $        → End of string                                                ║
 * ║  Examples:    ✓ 123TU4567  ✓ 1TU1234  ✓ 99TU9999  ✗ 1234TU567  ✗ TUNIS123   ║
 * ║                                                                              ║
 * ║  5. EMAIL (Gmail or .tn domain)                                              ║
 * ║  ─────────────────────────────────────────────────────────────────────────── ║
 * ║  Description: Valid email ending with @gmail.com or @*.tn                    ║
 * ║  Regex:       ^[A-Z0-9._%+-]+@((gmail\\.com)|([A-Z0-9.-]+\\.tn))$           ║
 * ║  Explanation:                                                                ║
 * ║    • ^[A-Z0-9._%+-]+  → Username (letters, numbers, dots, etc.)              ║
 * ║    • @                → Literal "@" symbol                                   ║
 * ║    • gmail\\.com      → Literal "gmail.com"                                  ║
 * ║    • |                → OR                                                   ║
 * ║    • [A-Z0-9.-]+\\.tn → Any domain ending in ".tn"                          ║
 * ║  Examples:    ✓ user@gmail.com  ✓ ali@univ.tn  ✗ test@yahoo.fr              ║
 * ║                                                                              ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 * 
 * @author Student - Java Validation Guide
 */
public class ValidationUtils {
    
    // ═══════════════════════════════════════════════════════════════════════════
    // REGEX PATTERNS - Compiled for better performance
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Phone number pattern: Exactly 8 digits
     * Regex: ^[0-9]{8}$
     */
    public static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");
    
    /**
     * CIN pattern: Exactly 8 digits
     * Regex: ^[0-9]{8}$
     */
    public static final Pattern CIN_PATTERN = Pattern.compile("^[0-9]{8}$");
    
    /**
     * Name pattern: Only letters (including French accents), spaces, hyphens, apostrophes
     * Regex: ^[a-zA-ZÀ-ÿ\s'-]+$
     */
    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ\\s'-]+$");
    
    /**
     * Tunisian car matriculation pattern: 1-3 digits + "TU" + 4 digits
     * Regex: ^[0-9]{1,3}TU[0-9]{4}$
     */
    public static final Pattern MATRICULE_PATTERN = Pattern.compile("^[0-9]{1,3}TU[0-9]{4}$");
    
    /**
     * Email pattern: Gmail or Tunisian domain (.tn)
     * Regex: ^[A-Z0-9._%+-]+@((gmail\.com)|([A-Z0-9.-]+\.tn))$
     */
    public static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Z0-9._%+-]+@((gmail\\.com)|([A-Z0-9.-]+\\.tn))$",
        Pattern.CASE_INSENSITIVE
    );
    
    // ═══════════════════════════════════════════════════════════════════════════
    // VALIDATION METHODS - Return boolean
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Validates a phone number (8 digits).
     * @param phone The phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Validates a CIN number (8 digits).
     * @param cin The CIN to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCIN(String cin) {
        if (cin == null) return false;
        return CIN_PATTERN.matcher(cin.trim()).matches();
    }
    
    /**
     * Validates a name (letters only, including French accents).
     * @param name The name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    /**
     * Validates a Tunisian car matriculation (1-3 digits + TU + 4 digits).
     * @param matricule The matriculation to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidMatricule(String matricule) {
        if (matricule == null) return false;
        return MATRICULE_PATTERN.matcher(matricule.trim().toUpperCase()).matches();
    }
    
    /**
     * Validates an email (Gmail or .tn domain).
     * @param email The email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // VALIDATION METHODS WITH EXCEPTIONS - For strict validation
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Validates phone and throws exception if invalid.
     * @param phone The phone number
     * @throws IllegalArgumentException if invalid
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
     * Validates CIN and throws exception if invalid.
     * @param cin The CIN number
     * @throws IllegalArgumentException if invalid
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
     * Validates name and throws exception if invalid.
     * @param name The name
     * @param fieldName The field name for error message (e.g., "Nom", "Prénom")
     * @throws IllegalArgumentException if invalid
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
     * Validates matricule and throws exception if invalid.
     * @param matricule The car matriculation
     * @throws IllegalArgumentException if invalid
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
     * Validates email and throws exception if invalid.
     * @param email The email address
     * @throws IllegalArgumentException if invalid
     */
    public static void validateEmail(String email) throws IllegalArgumentException {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException(
                "Email invalide: doit être @gmail.com ou @*.tn. " +
                "Reçu: '" + email + "'"
            );
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // ERROR MESSAGES - French error messages for UI
    // ═══════════════════════════════════════════════════════════════════════════
    
    public static final String PHONE_ERROR = "Le téléphone doit contenir exactement 8 chiffres";
    public static final String CIN_ERROR = "Le CIN doit contenir exactement 8 chiffres";
    public static final String NAME_ERROR = "Le nom doit contenir uniquement des lettres";
    public static final String PRENOM_ERROR = "Le prénom doit contenir uniquement des lettres";
    public static final String MATRICULE_ERROR = "Le matricule doit suivre le format: 123TU4567";
    public static final String EMAIL_ERROR = "L'email doit être @gmail.com ou @*.tn";
    
    // ═══════════════════════════════════════════════════════════════════════════
    // HELPER METHOD - Get all regex as documentation
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Prints all regex patterns and their explanations.
     * Useful for documentation and debugging.
     */
    public static void printRegexDocumentation() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           REGEX VALIDATION PATTERNS                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ Phone:     ^[0-9]{8}$           → 8 digits exactly         ║");
        System.out.println("║ CIN:       ^[0-9]{8}$           → 8 digits exactly         ║");
        System.out.println("║ Name:      ^[a-zA-ZÀ-ÿ\\s'-]+$  → Letters + accents        ║");
        System.out.println("║ Matricule: ^[0-9]{1,3}TU[0-9]{4}$ → 1-3 digits+TU+4 digits ║");
        System.out.println("║ Email:     ...@gmail.com or @*.tn                          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}
