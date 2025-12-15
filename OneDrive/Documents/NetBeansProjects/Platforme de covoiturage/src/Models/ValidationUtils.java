package Models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
 * ╠══════════════════════════════════════════════════════════════════════════════╣
 * ║                                                                              ║
 * ║  6. PASSWORD (Strong password policy)                                        ║
 * ║  ─────────────────────────────────────────────────────────────────────────── ║
 * ║  Description: Secure password with minimum requirements                      ║
 * ║  Regex:       ^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!]).{8,}$        ║
 * ║  Explanation:                                                                ║
 * ║    • (?=.*[a-z])      → At least one lowercase letter                        ║
 * ║    • (?=.*[A-Z])      → At least one uppercase letter                        ║
 * ║    • (?=.*\d)         → At least one digit                                   ║
 * ║    • (?=.*[@#$%^&+=!])→ At least one special character                       ║
 * ║    • .{8,}            → Minimum 8 characters                                 ║
 * ║  Examples:    ✓ MyPass@123  ✓ Secure#1  ✗ password  ✗ 12345678              ║
 * ║                                                                              ║
 * ║  SECURITY FEATURES:                                                          ║
 * ║    • Passwords are hashed using SHA-256 before storage                       ║
 * ║    • Plain text passwords are NEVER stored in CSV files                      ║
 * ║    • Hash comparison is used for authentication                              ║
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
     * Vehicle name pattern: Letters AND numbers (alphanumeric), spaces, hyphens
     * Regex: ^[a-zA-Z0-9À-ÿ\s'-]+$
     * 
     * MODIFICATION EXPLANATION:
     * ────────────────────────────────────────────────────────────────────────────
     * Original: Only allowed letters [a-zA-ZÀ-ÿ]
     * Modified: Now allows letters AND numbers [a-zA-Z0-9À-ÿ]
     * 
     * Why this change?
     * - Many vehicle models include numbers (e.g., "Peugeot 308", "Golf 7", "Clio 4")
     * - Allows realistic vehicle naming while maintaining input validation
     * 
     * Pattern breakdown:
     * • ^           → Start of string
     * • [a-zA-Z]    → Letters (uppercase and lowercase)
     * • [0-9]       → Digits (0 to 9) ← NEW ADDITION
     * • [À-ÿ]       → French accented characters
     * • [\s]        → Whitespace (for multi-word names)
     * • ['-]        → Hyphen and apostrophe
     * • +           → One or more characters
     * • $           → End of string
     * ────────────────────────────────────────────────────────────────────────────
     */
    public static final Pattern VEHICLE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9À-ÿ\\s'-]+$");
    
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
    
    /**
     * Password pattern: Strong password requirements
     * Regex: ^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!]).{8,}$
     * 
     * SECURITY EXPLANATION:
     * ────────────────────────────────────────────────────────────────────────────
     * This regex enforces a STRONG PASSWORD POLICY:
     * 
     * Pattern breakdown:
     * • ^                  → Start of string
     * • (?=.*[a-z])        → Positive lookahead: at least one lowercase letter
     * • (?=.*[A-Z])        → Positive lookahead: at least one uppercase letter
     * • (?=.*\d)           → Positive lookahead: at least one digit (0-9)
     * • (?=.*[@#$%^&+=!])  → Positive lookahead: at least one special character
     * • .{8,}              → Match any character, minimum 8 times (min length)
     * • $                  → End of string
     * 
     * Why use lookaheads?
     * - Lookaheads (?=...) check for conditions WITHOUT consuming characters
     * - This allows checking multiple conditions at any position in the string
     * - All conditions must be satisfied for the password to be valid
     * 
     * Valid examples:   Password1!  Secure#123  MyPass@99
     * Invalid examples: password (no uppercase), PASSWORD1 (no lowercase),
     *                   Password (no digit/special), Pass1! (too short)
     * ────────────────────────────────────────────────────────────────────────────
     */
    public static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$"
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
     * Validates a vehicle name (letters AND numbers allowed).
     * @param vehicleName The vehicle name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidVehicleName(String vehicleName) {
        if (vehicleName == null || vehicleName.trim().isEmpty()) return false;
        return VEHICLE_NAME_PATTERN.matcher(vehicleName.trim()).matches();
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
    
    /**
     * Validates a password for strength requirements.
     * Requirements:
     * - Minimum 8 characters
     * - At least one lowercase letter
     * - At least one uppercase letter
     * - At least one digit
     * - At least one special character (@#$%^&+=!)
     * 
     * @param password The password to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Returns a detailed password strength analysis.
     * Useful for providing feedback to users during registration.
     * 
     * @param password The password to analyze
     * @return Array of boolean: [hasLowercase, hasUppercase, hasDigit, hasSpecial, hasMinLength]
     */
    public static boolean[] getPasswordStrengthDetails(String password) {
        if (password == null) password = "";
        return new boolean[] {
            password.matches(".*[a-z].*"),           // Has lowercase
            password.matches(".*[A-Z].*"),           // Has uppercase
            password.matches(".*\\d.*"),             // Has digit
            password.matches(".*[@#$%^&+=!].*"),     // Has special char
            password.length() >= 8                    // Has min length
        };
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
     * Validates vehicle name and throws exception if invalid.
     * @param vehicleName The vehicle name (e.g., "Golf 7", "Clio 4", "308")
     * @param fieldName The field name for error message
     * @throws IllegalArgumentException if invalid
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
    
    /**
     * Validates password strength and throws exception if weak.
     * @param password The password to validate
     * @throws IllegalArgumentException if password doesn't meet requirements
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
    // ERROR MESSAGES - French error messages for UI
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
        System.out.println("║ Password:  8+ chars, upper, lower, digit, special          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // PASSWORD SECURITY - Hashing and Verification
    // ═══════════════════════════════════════════════════════════════════════════
    
    /**
     * Hashes a password using SHA-256 algorithm.
     * 
     * SECURITY EXPLANATION:
     * ────────────────────────────────────────────────────────────────────────────
     * Why hash passwords?
     * • NEVER store plain text passwords - if database is compromised, passwords are exposed
     * • SHA-256 is a one-way function - cannot reverse the hash to get original password
     * • Each password produces a unique 64-character hexadecimal hash
     * 
     * How it works:
     * 1. Get MessageDigest instance for SHA-256 algorithm
     * 2. Convert password string to bytes (UTF-8 encoding)
     * 3. Compute the hash digest
     * 4. Convert bytes to hexadecimal string for storage
     * 
     * Example:
     *   Input:  "MyPass@123"
     *   Output: "a5b9f1c3d4e5f6..." (64 hex characters)
     * ────────────────────────────────────────────────────────────────────────────
     * 
     * @param password The plain text password to hash
     * @return The SHA-256 hash as a hexadecimal string, or null if hashing fails
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        
        try {
            // Step 1: Get SHA-256 MessageDigest instance
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Step 2: Compute hash of password bytes
            byte[] hashBytes = md.digest(password.getBytes());
            
            // Step 3: Convert bytes to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                // Format each byte as 2-digit hex (with leading zero if needed)
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 should always be available in Java
            System.err.println("Erreur: Algorithme SHA-256 non disponible: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifies a plain text password against a stored hash.
     * 
     * SECURITY EXPLANATION:
     * ────────────────────────────────────────────────────────────────────────────
     * During login, we:
     * 1. Take the user's entered password
     * 2. Hash it using the same algorithm (SHA-256)
     * 3. Compare the resulting hash with the stored hash
     * 4. If they match, the password is correct
     * 
     * We NEVER decrypt the stored hash - hashing is one-way!
     * ────────────────────────────────────────────────────────────────────────────
     * 
     * @param plainPassword The plain text password entered by user
     * @param storedHash The stored SHA-256 hash from database
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        
        // Hash the entered password and compare with stored hash
        String hashedInput = hashPassword(plainPassword);
        return storedHash.equals(hashedInput);
    }
}
