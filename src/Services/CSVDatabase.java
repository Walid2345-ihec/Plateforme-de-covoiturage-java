package Services;

import Models.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSVDatabase - A utility class for reading and writing data to CSV files.
 *
 * STEP-BY-STEP EXPLANATION:
 *
 * 1. CSV Structure:
 *    - Each file represents a "table"
 *    - First row = column headers
 *    - Subsequent rows = data records
 *    - Values separated by semicolons (;) to handle French text with commas
 *
 * 2. Key Operations:
 *    - READ: Load data from CSV into Java objects
 *    - WRITE: Save Java objects to CSV files
 *    - APPEND: Add new records without overwriting
 *
 * @author Student Guide
 */
public class CSVDatabase {
    
    // ============================================================
    // CONFIGURATION - File paths for each "table"
    // ============================================================
    
    private static final String DATA_FOLDER = "data/";
    private static final String BACKUP_FOLDER = "data/backups/";
    private static final String CONDUCTEURS_FILE = DATA_FOLDER + "conducteurs.csv";
    private static final String PASSAGERS_FILE = DATA_FOLDER + "passagers.csv";
    private static final String TRAJETS_FILE = DATA_FOLDER + "trajets.csv";
    private static final String NOTIFICATIONS_FILE = DATA_FOLDER + "notifications.csv";
    private static final String CONDUCTEUR_NOTIFICATIONS_FILE = DATA_FOLDER + "conducteur_notifications.csv";
    private static final String MESSAGES_FILE = DATA_FOLDER + "messages.csv";
    private static final String CONVERSATIONS_FILE = DATA_FOLDER + "conversations.csv";
    private static final String GROUPS_FILE = DATA_FOLDER + "groups.csv";
    private static final String GROUP_MESSAGES_FILE = DATA_FOLDER + "group_messages.csv";
    private static final String EVALUATIONS_FILE = DATA_FOLDER + "evaluations.csv";
    private static final String ADMINS_FILE = DATA_FOLDER + "admins.csv";
    private static final String ADMIN_NOTIFICATIONS_FILE = DATA_FOLDER + "notifications_admin.csv";

    // Delimiter - using semicolon to avoid conflicts with French text
    private static final String DELIMITER = ";";
    
    // Maximum number of backup files to keep
    private static final int MAX_BACKUPS = 5;
    
    // ============================================================
    // STEP 1: Initialize the data folder
    // ============================================================
    
    /**
     * Creates the data folder if it doesn't exist.
     * Always call this before reading/writing files!
     */
    public static void initializeDataFolder() {
        try {
            Path dataPath = Paths.get(DATA_FOLDER);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                System.out.println("✓ Dossier 'data/' créé avec succès");
            }
            // Also create backup folder
            Path backupPath = Paths.get(BACKUP_FOLDER);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }
        } catch (IOException e) {
            System.err.println("✗ Erreur création dossier: " + e.getMessage());
        }
    }
    
    // ============================================================
    // BACKUP & RECOVERY SYSTEM
    // ============================================================
    
    /**
     * Creates a backup of all CSV files before saving.
     * Backups are timestamped and rotated (max 5 kept).
     */
    public static void createBackup() {
        initializeDataFolder();
        
        String timestamp = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        String[] files = {CONDUCTEURS_FILE, PASSAGERS_FILE, TRAJETS_FILE};
        
        for (String file : files) {
            Path source = Paths.get(file);
            if (Files.exists(source)) {
                try {
                    String fileName = source.getFileName().toString();
                    String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                    String backupName = BACKUP_FOLDER + baseName + "_" + timestamp + ".csv";
                    Files.copy(source, Paths.get(backupName), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    System.err.println("⚠️ Backup échoué pour " + file + ": " + e.getMessage());
                }
            }
        }
        
        // Rotate old backups
        rotateBackups();
        System.out.println("✓ Backup créé: " + timestamp);
    }
    
    /**
     * Removes old backup files, keeping only the most recent ones.
     */
    private static void rotateBackups() {
        try {
            Path backupDir = Paths.get(BACKUP_FOLDER);
            if (!Files.exists(backupDir)) return;
            
            // Group backups by base name and keep only MAX_BACKUPS of each
            java.util.Map<String, java.util.List<Path>> backupGroups = new java.util.HashMap<>();
            
            Files.list(backupDir)
                .filter(p -> p.toString().endsWith(".csv"))
                .forEach(p -> {
                    String name = p.getFileName().toString();
                    // Extract base name (e.g., "conducteurs" from "conducteurs_20241217_143022.csv")
                    int underscoreIdx = name.indexOf('_');
                    if (underscoreIdx > 0) {
                        String baseName = name.substring(0, underscoreIdx);
                        backupGroups.computeIfAbsent(baseName, k -> new java.util.ArrayList<>()).add(p);
                    }
                });
            
            // For each group, sort by modification time and delete oldest if > MAX_BACKUPS
            for (java.util.List<Path> group : backupGroups.values()) {
                if (group.size() > MAX_BACKUPS) {
                    group.sort((a, b) -> {
                        try {
                            return Files.getLastModifiedTime(b).compareTo(Files.getLastModifiedTime(a));
                        } catch (IOException e) {
                            return 0;
                        }
                    });
                    
                    // Delete oldest backups
                    for (int i = MAX_BACKUPS; i < group.size(); i++) {
                        try {
                            Files.delete(group.get(i));
                        } catch (IOException e) {
                            // Ignore deletion errors
                        }
                    }
                }
            }
        } catch (IOException e) {
            // Ignore rotation errors
        }
    }
    
    /**
     * Attempts to restore data from the most recent backup.
     * Use this if the main data files are corrupted.
     * 
     * @return true if restoration was successful
     */
    public static boolean restoreFromBackup() {
        try {
            Path backupDir = Paths.get(BACKUP_FOLDER);
            if (!Files.exists(backupDir)) {
                System.err.println("✗ Aucun dossier de backup trouvé");
                return false;
            }
            
            // Find most recent backup for each file type
            String[] baseNames = {"conducteurs", "passagers", "trajets"};
            String[] targetFiles = {CONDUCTEURS_FILE, PASSAGERS_FILE, TRAJETS_FILE};
            
            for (int i = 0; i < baseNames.length; i++) {
                final String baseName = baseNames[i];
                final String targetFile = targetFiles[i];
                
                java.util.Optional<Path> latestBackup = Files.list(backupDir)
                    .filter(p -> p.getFileName().toString().startsWith(baseName + "_"))
                    .max((a, b) -> {
                        try {
                            return Files.getLastModifiedTime(a).compareTo(Files.getLastModifiedTime(b));
                        } catch (IOException e) {
                            return 0;
                        }
                    });
                
                if (latestBackup.isPresent()) {
                    Files.copy(latestBackup.get(), Paths.get(targetFile), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("✓ Restauré: " + targetFile + " depuis " + latestBackup.get().getFileName());
                }
            }
            
            System.out.println("✓ Restauration depuis backup terminée");
            return true;
            
        } catch (IOException e) {
            System.err.println("✗ Erreur lors de la restauration: " + e.getMessage());
            return false;
        }
    }
    
    // ============================================================
    // STEP 2: WRITE Operations - Saving data to CSV
    // ============================================================
    
    /**
     * Saves all conducteurs to CSV file.
     *
     * HOW IT WORKS:
     * 1. Open a BufferedWriter (efficient for writing text)
     * 2. Write the header row first
     * 3. Loop through each Conducteur and write their data
     *  * Each field is separated by our DELIMITER
     *
     * @param users Vector of User objects (only Conducteur instances are saved by this method)
     */
    public static void saveConducteurs(List<User> users) {
        initializeDataFolder();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(CONDUCTEURS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            // HEADER ROW - defines the columns
            writer.write("CIN;Nom;Prenom;Tel;AnneeUniv;Adresse;Mail;PasswordHash;NomVoiture;MarqueVoiture;Matricule;PlacesDisponibles;WeeklySchedule;MoyenneEvaluation;carte;Banned");
            writer.newLine();

            // DATA ROWS - one per conducteur
            for (User user : users) {
                if (user instanceof Conducteur c) {

                    // Build the CSV line by joining fields with delimiter
                    String line = String.join(DELIMITER,
                        escapeCSV(c.getCin()),
                        escapeCSV(c.getNom()),
                        escapeCSV(c.getPrenom()),
                        escapeCSV(c.getTel()),
                        String.valueOf(c.getAnneeUniversitaire().getValue()),
                        escapeCSV(c.getAdresse()),
                        escapeCSV(c.getMail()),
                        escapeCSV(c.getPasswordHash() != null ? c.getPasswordHash() : ""),
                        escapeCSV(c.getNomVoiture()),
                        escapeCSV(c.getMarqueVoiture()),
                        escapeCSV(c.getMatricule()),
                        String.valueOf(c.getPlacesDisponibles()),
                        escapeCSV(c.getWeeklySchedule()),
                        String.format(java.util.Locale.US, "%.2f", c.getMoyenneEvaluation()),
                        escapeCSV(c.getCard()),
                        String.valueOf(c.isBanned())
                    );

                    writer.write(line);
                    writer.newLine();
                }
            }
            
            System.out.println("✓ Conducteurs sauvegardés: " + CONDUCTEURS_FILE);
            
        } catch (IOException e) {
            System.err.println("✗ Erreur sauvegarde conducteurs: " + e.getMessage());
        }
    }
    
    /**
     * Updates a specific conductor's weekly schedule and saves to CSV.
     * This method is used when a conductor creates a trajectory with a weekly schedule.
     * 
     * @param conducteur The conductor object with updated schedule
     * @param gestion The Gestion_covoiturage object containing all users
     */
    public static void updateConductorWeeklySchedule(Conducteur conducteur, Gestion_covoiturage gestion) {
        if (conducteur == null || gestion == null) {
            System.err.println("✗ Erreur: Conducteur ou Gestion non valide");
            return;
        }
        
        try {
            // Find and update the conductor in the users list
            List<User> users = gestion.getUsers();
            boolean found = false;
            
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                if (user instanceof Conducteur c) {
                    if (c.getCin().equals(conducteur.getCin())) {
                        // Update the weekly schedule
                        c.setWeeklySchedule(conducteur.getWeeklySchedule());
                        found = true;
                        System.out.println("✓ Horaire de semaine du conducteur mis à jour: " + c.getCin());
                        break;
                    }
                }
            }
            
            if (!found) {
                System.err.println("✗ Conducteur non trouvé: " + conducteur.getCin());
                return;
            }
            
            // Save all conductors to CSV
            saveConducteurs(users);
            
        } catch (Exception e) {
            System.err.println("✗ Erreur mise à jour horaire conducteur: " + e.getMessage());
        }
    }
    
    /**
     * Saves all passagers to CSV file.
     */
    public static void savePassagers(List<User> users) {
        initializeDataFolder();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(PASSAGERS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            // HEADER ROW
            writer.write("CIN;Nom;Prenom;Tel;AnneeUniv;Adresse;Mail;PasswordHash;ChercheCovoit;carte;Banned");
            writer.newLine();

            // DATA ROWS
            for (User user : users) {
                if (user instanceof Passager p) {

                    String line = String.join(DELIMITER,
                        escapeCSV(p.getCin()),
                        escapeCSV(p.getNom()),
                        escapeCSV(p.getPrenom()),
                        escapeCSV(p.getTel()),
                        String.valueOf(p.getAnneeUniversitaire().getValue()),
                        escapeCSV(p.getAdresse()),
                        escapeCSV(p.getMail()),
                        escapeCSV(p.getPasswordHash() != null ? p.getPasswordHash() : ""),
                        String.valueOf(p.isChercheCovoit()),
                        escapeCSV(p.getCard()),
                        String.valueOf(p.isBanned())
                    );

                    writer.write(line);
                    writer.newLine();
                }
            }
            
            System.out.println("✓ Passagers sauvegardés: " + PASSAGERS_FILE);
            
        } catch (IOException e) {
            System.err.println("✗ Erreur sauvegarde passagers: " + e.getMessage());
        }
    }
    
    /**
     * Saves all trajets to CSV file.
     */
    public static void saveTrajets(List<Trajet> trajets) {
        initializeDataFolder();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(TRAJETS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            // HEADER ROW
            // Format: Depart;Arrivee;DureeMinutes;Status;Prix;ConducteurCIN;PassagerCIN;MaxPlaces;AcceptedCINs;PendingCINs;StartDateTime;EndDateTime;WeeklySchedule
            writer.write("Depart;Arrivee;DureeMinutes;Status;Prix;ConducteurCIN;PassagerCIN;MaxPlaces;AcceptedCINs;PendingCINs;StartDateTime;EndDateTime;WeeklySchedule");
            writer.newLine();
            
            // DATA ROWS
            for (Trajet t : trajets) {
                String conducteurCIN = (t.getConducteur() != null) ? t.getConducteur().getCin() : "";
                // For backward compatibility provide first accepted passager CIN in the old column
                String passagerCIN = "";
                if (!t.getPassagersAcceptes().isEmpty()) {
                    passagerCIN = t.getPassagersAcceptes().get(0).getCin();
                }
                String maxPlaces = String.valueOf(t.getMaxPlaces());
                String accepted = t.getPassagersAcceptesCINs();
                String pending = t.getPassagersDemandesCINs();
                String startDateTime = t.getStartDateTimeString();
                String endDateTime = t.getEndDateTimeString();
                String weeklySchedule = t.getWeeklySchedule();

                String line = String.join(DELIMITER,
                    escapeCSV(t.getDepartTrajet()),
                    escapeCSV(t.getArriveeTrajet()),
                    String.valueOf(t.getDureeTrajet().toMinutes()),
                    escapeCSV(t.getStatusTrajet()),
                    String.valueOf(t.getPrix()),
                    escapeCSV(conducteurCIN),
                    escapeCSV(passagerCIN),
                    escapeCSV(maxPlaces),
                    escapeCSV(accepted),
                    escapeCSV(pending),
                    escapeCSV(startDateTime),
                    escapeCSV(endDateTime),
                    escapeCSV(weeklySchedule)
                );
                
                writer.write(line);
                writer.newLine();
            }
            
            System.out.println("✓ Trajets sauvegardés: " + TRAJETS_FILE);
            
        } catch (IOException e) {
            System.err.println("✗ Erreur sauvegarde trajets: " + e.getMessage());
        }
    }
    
    // ============================================================
    // STEP 3: READ Operations - Loading data from CSV
    // ============================================================
    
    /**
     * Loads conducteurs from CSV file.
     * 
     * HOW IT WORKS:
     * 1. Open a BufferedReader (efficient for reading text)
     * 2. Skip the header row
     * 3. Read each line and split by DELIMITER
     * 4. Create Conducteur objects from the values
     * 
     * @return List of Conducteur objects
     */
    public static List<Conducteur> loadConducteurs() {
        List<Conducteur> conducteurs = new ArrayList<>();
        Path filePath = Paths.get(CONDUCTEURS_FILE);
        
        // Check if file exists
        if (!Files.exists(filePath)) {
            System.out.println("ℹ Fichier conducteurs non trouvé, liste vide retournée");
            return conducteurs;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(CONDUCTEURS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            String line;
            boolean isHeader = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip header row
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                // Skip empty lines
                if (line.trim().isEmpty()) continue;
                
                // Split the line by delimiter
                String[] values = line.split(DELIMITER, -1); // -1 keeps empty values
                
                // Validate we have enough columns
                if (values.length >= 12) {
                    try {
                        // Get weeklySchedule if available (new format with 13 columns)
                        String weeklySchedule = (values.length >= 13) ? unescapeCSV(values[12]) : "";

                        // Get average rating if available (new format with 14 columns)
                        double moyenneEvaluation = 0.0;
                        if (values.length >= 14) {
                            String rawAvg = unescapeCSV(values[13]).trim();
                            if (!rawAvg.isEmpty()) {
                                try {
                                    moyenneEvaluation = Double.parseDouble(rawAvg.replace(',', '.'));
                                } catch (NumberFormatException nfe) {
                                    moyenneEvaluation = 0.0;
                                }
                            }
                        }

                        // Card and banned status (new format with 16 columns)
                        String card = (values.length >= 15) ? unescapeCSV(values[14]) : "GREEN";
                        boolean banned = false;
                        if (values.length >= 16) {
                            banned = Boolean.parseBoolean(values[15].trim());
                        }

                        Conducteur c = new Conducteur(
                            unescapeCSV(values[0]),  // CIN
                            unescapeCSV(values[1]),  // Nom
                            unescapeCSV(values[2]),  // Prenom
                            unescapeCSV(values[3]),  // Tel
                            Year.of(Integer.parseInt(values[4])), // AnneeUniv
                            unescapeCSV(values[5]),  // Adresse
                            unescapeCSV(values[6]),  // Mail
                            unescapeCSV(values[7]),  // PasswordHash (already hashed)
                            true,                     // isHashedPassword = true
                            unescapeCSV(values[8]),  // NomVoiture
                            unescapeCSV(values[9]),  // MarqueVoiture
                            unescapeCSV(values[10]), // Matricule
                            Integer.parseInt(values[11]), // PlacesDisponibles
                            weeklySchedule          // WeeklySchedule (new format)
                        );
                        c.setMoyenneEvaluation(moyenneEvaluation);
                        c.setCard(card);
                        c.setBanned(banned);
                        conducteurs.add(c);
                    } catch (Exception e) {
                        System.err.println("⚠ Erreur parsing conducteur: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("✓ " + conducteurs.size() + " conducteurs chargés");
            
        } catch (IOException e) {
            System.err.println("✗ Erreur lecture conducteurs: " + e.getMessage());
        }
        
        return conducteurs;
    }
    
    /**
     * Loads passagers from CSV file.
     */
    public static List<Passager> loadPassagers() {
        List<Passager> passagers = new ArrayList<>();
        Path filePath = Paths.get(PASSAGERS_FILE);
        
        if (!Files.exists(filePath)) {
            System.out.println("ℹ Fichier passagers non trouvé, liste vide retournée");
            return passagers;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(PASSAGERS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                if (line.trim().isEmpty()) continue;
                
                String[] values = line.split(DELIMITER, -1);
                
                if (values.length >= 9) {
                    try {
                        // Note: Passager constructor requires a Conducteur reference
                        // We pass null initially; relationships are rebuilt when loading trajets
                        Passager p = new Passager(
                            unescapeCSV(values[0]),  // CIN
                            unescapeCSV(values[1]),  // Nom
                            unescapeCSV(values[2]),  // Prenom
                            unescapeCSV(values[3]),  // Tel
                            Year.of(Integer.parseInt(values[4])), // AnneeUniv
                            unescapeCSV(values[5]),  // Adresse
                            unescapeCSV(values[6]),  // Mail
                            unescapeCSV(values[7]),  // PasswordHash (already hashed)
                            true,                     // isHashedPassword = true
                            Boolean.parseBoolean(values[8]), // ChercheCovoit
                            null  // Conducteur - will be set when loading trajets
                        );
                        // Card and banned status (new format)
                        if (values.length >= 10) {
                            p.setCard(unescapeCSV(values[9]));
                        }
                        if (values.length >= 11) {
                            p.setBanned(Boolean.parseBoolean(values[10].trim()));
                        }
                        passagers.add(p);
                    } catch (Exception e) {
                        System.err.println("⚠ Erreur parsing passager: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("✓ " + passagers.size() + " passagers chargés");
            
        } catch (IOException e) {
            System.err.println("✗ Erreur lecture passagers: " + e.getMessage());
        }
        
        return passagers;
    }
    
    /**
     * Loads trajets from CSV file.
     * Note: Requires conducteurs and passagers to be loaded first for references.
     */
    public static List<Trajet> loadTrajets(List<User> users) {
        List<Trajet> trajets = new ArrayList<>();
        Path filePath = Paths.get(TRAJETS_FILE);
        
        if (!Files.exists(filePath)) {
            System.out.println("ℹ Fichier trajets non trouvé, liste vide retournée");
            return trajets;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(TRAJETS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            String line;
            boolean isHeader = true;
            
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                if (line.trim().isEmpty()) continue;
                
                String[] values = line.split(DELIMITER, -1);
                
                if (values.length >= 7) {
                    try {
                        // Find conductor and passenger by CIN
                        Conducteur conducteur = findConducteurByCIN(users, values[5]);
                        Passager passager = findPassagerByCIN(users, values[6]);

                        // If newer format (with maxPlaces and lists)
                        int maxPlaces = 1;
                        ArrayList<Passager> accepted = new ArrayList<>();
                        ArrayList<Passager> pending = new ArrayList<>();
                        LocalDateTime startDateTime = null;
                        LocalDateTime endDateTime = null;

                        if (values.length >= 10) {
                            try {
                                maxPlaces = Integer.parseInt(values[7].trim().isEmpty() ? "1" : values[7].trim());
                            } catch (NumberFormatException e) {
                                maxPlaces = (conducteur != null) ? conducteur.getPlacesDisponibles() : 1;
                            }
                            // parse accepted CINs
                            String acceptedStr = unescapeCSV(values[8]);
                            if (!acceptedStr.isEmpty()) {
                                String[] ac = acceptedStr.split(",");
                                for (String cin : ac) {
                                    Passager p = findPassagerByCIN(users, cin.trim());
                                    if (p != null) accepted.add(p);
                                }
                            }
                            // parse pending CINs
                            String pendingStr = unescapeCSV(values[9]);
                            if (!pendingStr.isEmpty()) {
                                String[] pc = pendingStr.split(",");
                                for (String cin : pc) {
                                    Passager p = findPassagerByCIN(users, cin.trim());
                                    if (p != null) pending.add(p);
                                }
                            }
                        } else {
                            // Old format: if passager not null, add as accepted
                            if (passager != null) accepted.add(passager);
                            maxPlaces = (conducteur != null) ? conducteur.getPlacesDisponibles() : 1;
                        }

                        // Parse date/time fields if present (new format)
                        if (values.length >= 12) {
                            try {
                                startDateTime = Trajet.parseDateTime(values[10]);
                            } catch (DateTimeParseException e) {
                                System.err.println("⚠ Erreur parsing startDateTime: " + values[10]);
                            }
                            try {
                                endDateTime = Trajet.parseDateTime(values[11]);
                            } catch (DateTimeParseException e) {
                                System.err.println("⚠ Erreur parsing endDateTime: " + values[11]);
                            }
                        }

                        // Create trajet with or without date/time info
                        Trajet t;
                        if (startDateTime != null && endDateTime != null) {
                            // New format with date/time
                            t = new Trajet(
                                unescapeCSV(values[0]),  // Depart
                                unescapeCSV(values[1]),  // Arrivee
                                Duration.ofMinutes(Long.parseLong(values[2])), // Duree
                                unescapeCSV(values[3]),  // Status
                                Float.parseFloat(values[4]), // Prix
                                conducteur,
                                startDateTime,
                                endDateTime,
                                maxPlaces
                            );
                        } else {
                            // Old format without date/time
                            t = new Trajet(
                                unescapeCSV(values[0]),  // Depart
                                unescapeCSV(values[1]),  // Arrivee
                                Duration.ofMinutes(Long.parseLong(values[2])), // Duree
                                unescapeCSV(values[3]),  // Status
                                Float.parseFloat(values[4]), // Prix
                                conducteur,
                                maxPlaces
                            );
                        }

                        // Load weekly schedule if present (new format with 13 columns)
                        if (values.length >= 13) {
                            String weeklySchedule = unescapeCSV(values[12]);
                            if (!weeklySchedule.isEmpty()) {
                                t.setWeeklySchedule(weeklySchedule);
                            }
                        }

                        // Attach accepted and pending lists
                        for (Passager p : accepted) t.getPassagersAcceptes().add(p);
                        for (Passager p : pending) t.getPassagersDemandes().add(p);

                        trajets.add(t);
                    } catch (Exception e) {
                        System.err.println("⚠ Erreur parsing trajet: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("✓ " + trajets.size() + " trajets chargés");
            
        } catch (IOException e) {
            System.err.println("✗ Erreur lecture trajets: " + e.getMessage());
        }
        
        return trajets;
    }
    
    // ============================================================
    // STEP 4: Helper Methods
    // ============================================================
    
    /**
     * Escapes special characters in CSV values.
     * If a value contains the delimiter or quotes, wrap it in quotes.
     */
    private static String escapeCSV(String value) {
        if (value == null) return "";
        
        // If contains delimiter or quotes, wrap in quotes and escape internal quotes
        if (value.contains(DELIMITER) || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * Removes escape characters from CSV values.
     */
    private static String unescapeCSV(String value) {
        if (value == null) return "";
        value = value.trim();
        value = stripBom(value);
        
        // Remove surrounding quotes
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            // Unescape internal quotes
            value = value.replace("\"\"", "\"");
        }
        return value;
    }

    private static String stripBom(String value) {
        if (value != null && value.startsWith("\ufeff")) {
            return value.substring(1);
        }
        return value;
    }
    
    /**
     * Finds a Conducteur by CIN in the users list.
     */
    private static Conducteur findConducteurByCIN(List<User> users, String cin) {
        if (cin == null || cin.trim().isEmpty()) return null;
        
        for (User u : users) {
            if (u instanceof Conducteur && u.getCin().equals(cin.trim())) {
                return (Conducteur) u;
            }
        }
        return null;
    }
    
    /**
     * Finds a Passager by CIN in the users list.
     */
    private static Passager findPassagerByCIN(List<User> users, String cin) {
        if (cin == null || cin.trim().isEmpty()) return null;
        
        for (User u : users) {
            if (u instanceof Passager && u.getCin().equals(cin.trim())) {
                return (Passager) u;
            }
        }
        return null;
    }
    
    /**
     * Loads admins from CSV file.
     */
    public static List<Admin> loadAdmins() {
        List<Admin> admins = new ArrayList<>();
        Path filePath = Paths.get(ADMINS_FILE);

        if (!Files.exists(filePath)) {
            System.out.println("ℹ Fichier admins non trouvé, liste vide retournée");
            return admins;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(ADMINS_FILE),
                    StandardCharsets.UTF_8))) {

            String line;
            boolean isHeader = true;
            Map<String, Integer> headerIndexes = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    String[] headers = line.split(DELIMITER, -1);
                    for (int i = 0; i < headers.length; i++) {
                        headerIndexes.put(stripBom(headers[i]).trim().toLowerCase(), i);
                    }
                    isHeader = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(DELIMITER, -1);
                if (values.length >= 4) {
                    try {
                        String cin = getCsvValue(values, headerIndexes, "cin", 0);
                        String nom = getCsvValue(values, headerIndexes, "nom", 1);
                        String prenom = getCsvValue(values, headerIndexes, "prenom", 2);
                        String mail = getCsvValue(values, headerIndexes, "mail", 6);
                        String passwordHash = getCsvValue(values, headerIndexes, "passwordhash", 7);
                        String role = getCsvValue(values, headerIndexes, "role", 8);
                        String dateCreation = getCsvValue(values, headerIndexes, "datecreation", 9);

                        if (mail.isEmpty() && values.length >= 4) {
                            mail = unescapeCSV(values[3]);
                        }
                        if (passwordHash.isEmpty() && values.length >= 5) {
                            passwordHash = unescapeCSV(values[4]);
                        }
                        if (role.isEmpty() && values.length >= 6) {
                            role = unescapeCSV(values[5]);
                        }

                        Admin a = new Admin(
                            cin,
                            nom,
                            prenom,
                            mail,
                            passwordHash,
                            role.isEmpty() ? "ADMIN" : role,
                            Admin.parseDate(dateCreation)
                        );
                        admins.add(a);
                    } catch (Exception e) {
                        System.err.println("⚠ Erreur parsing admin: " + e.getMessage());
                    }
                }
            }
            System.out.println("✓ " + admins.size() + " admins chargés");
        } catch (IOException e) {
            System.err.println("✗ Erreur lecture admins: " + e.getMessage());
        }
        if (admins.isEmpty()) {
            admins.add(createDefaultAdmin());
        }
        return admins;
    }

    private static Admin createDefaultAdmin() {
        return new Admin(
            "99999999",
            "Admin",
            "Principal",
            "admin@gmail.com",
            "admin123",
            "SUPER_ADMIN",
            Admin.parseDate("2026-05-10 14:30:00")
        );
    }

    private static String getCsvValue(String[] values, Map<String, Integer> headerIndexes, String headerName, int fallbackIndex) {
        Integer index = headerIndexes.get(headerName);
        if (index != null && index >= 0 && index < values.length) {
            return unescapeCSV(values[index]);
        }
        if (fallbackIndex >= 0 && fallbackIndex < values.length) {
            return unescapeCSV(values[fallbackIndex]);
        }
        return "";
    }

    /**
     * Saves all admins to CSV file.
     */
    public static void saveAdmins(List<User> users) {
        initializeDataFolder();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(ADMINS_FILE),
                    StandardCharsets.UTF_8))) {

            writer.write("CIN;Nom;Prenom;Tel;AnneeUniv;Adresse;Mail;PasswordHash;Role;DateCreation");
            writer.newLine();

            int count = 0;
            for (User user : users) {
                if (user instanceof Admin a) {
                    String line = String.join(DELIMITER,
                        escapeCSV(a.getCin()),
                        escapeCSV(a.getNom()),
                        escapeCSV(a.getPrenom()),
                        escapeCSV(a.getTel()),
                        String.valueOf(a.getAnneeUniversitaire().getValue()),
                        escapeCSV(a.getAdresse()),
                        escapeCSV(a.getMail()),
                        escapeCSV(a.getPasswordHash()),
                        escapeCSV(a.getRole()),
                        escapeCSV(a.getDateCreationAsString())
                    );
                    writer.write(line);
                    writer.newLine();
                    count++;
                }
            }
            System.out.println("✓ " + count + " admins sauvegardés");
        } catch (IOException e) {
            System.err.println("✗ Erreur sauvegarde admins: " + e.getMessage());
        }
    }

    /**
     * Saves all data to CSV files.
     * Call this when the application closes or after important changes.
     */
    public static void saveAllData(Gestion_covoiturage gestion) {
        System.out.println("\n📁 Sauvegarde des données...");
        saveConducteurs(gestion.getUsers());
        savePassagers(gestion.getUsers());
        saveAdmins(gestion.getUsers());
        saveTrajets(gestion.getTrajets());
        saveAllNotifications(gestion);
        saveAdminNotifications(gestion.getAdminNotifications());
        saveConversations(gestion.getConversations());
        saveGroups(gestion.getGroups());
        saveGroupMessages(gestion.getGroupMessages());
        saveEvaluations(gestion.getEvaluations());
        System.out.println("✓ Toutes les données sauvegardées!\n");
    }

    /**
     * Loads all data from CSV files into the gestion object.
     * Call this when the application starts.
     */
    public static void loadAllData(Gestion_covoiturage gestion) {
        System.out.println("\n📂 Chargement des données...");
        
        // Load conducteurs
        List<Conducteur> conducteurs = loadConducteurs();
        for (Conducteur c : conducteurs) {
            gestion.getUsers().add(c);
        }
        
        // Load passagers
        List<Passager> passagers = loadPassagers();
        for (Passager p : passagers) {
            gestion.getUsers().add(p);
        }

        // Load admins
        List<Admin> admins = loadAdmins();
        for (Admin a : admins) {
            gestion.getUsers().add(a);
        }
        
        // Load trajets (needs users to be loaded first)
        List<Trajet> trajets = loadTrajets(gestion.getUsers());
        for (Trajet t : trajets) {
            gestion.getTrajets().add(t);
        }
        
        // Load notifications for passagers
        List<Notification> notifications = loadNotifications();
        for (Notification n : notifications) {
            gestion.ajouterNotification(n);
        }
        
        // Load notifications for conducteurs
        List<Notification> conducteurNotifications = loadConducteurNotifications();
        for (Notification n : conducteurNotifications) {
            gestion.ajouterNotificationConducteur(n);
        }

        // Load notifications for admin
        List<Notification> adminNotifications = loadAdminNotifications();
        for (Notification n : adminNotifications) {
            gestion.ajouterNotificationAdmin(n);
        }

        List<Conversation> conversations = loadConversations();
        for (Conversation conversation : conversations) {
            gestion.ajouterConversation(conversation);
        }

        // Load groups and group messages
        List<Group> groups = loadGroups();
        for (Group g : groups) {
            gestion.ajouterGroupe(g);
        }
        List<GroupMessage> groupMessages = loadGroupMessages();
        for (GroupMessage m : groupMessages) {
            gestion.ajouterGroupMessage(m);
        }

        // Load evaluations
        List<Evaluation> evaluations = loadEvaluations();
        for (Evaluation e : evaluations) {
            gestion.ajouterEvaluation(e);
        }
        // Recalculer la moyenne pour chaque conducteur (au cas où le CSV serait désynchronisé)
        gestion.recalculerToutesMoyennes();

        System.out.println("✓ Toutes les données chargées!\n");
    }
    
    // ============================================================
    // STEP 6: Export to Standard CSV (for external use)
    // ============================================================
    
    /**
     * Exports data to a user-friendly CSV file.
     * This creates a nicely formatted file that can be opened in Excel.
     */
    public static void exportToExcelCSV(List<Trajet> trajets, String filename) {
        initializeDataFolder();
        String exportPath = DATA_FOLDER + filename;
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(exportPath), 
                    StandardCharsets.UTF_8))) {
            
            // BOM for Excel UTF-8 compatibility
            writer.write('\ufeff');
            
            // Header with French labels
            writer.write("Point de Départ;Point d'Arrivée;Durée (min);Statut;Prix (TND);Conducteur;Passager");
            writer.newLine();
            
            for (Trajet t : trajets) {
                String conducteur = (t.getConducteur() != null) 
                    ? t.getConducteur().getNom() + " " + t.getConducteur().getPrenom() 
                    : "Non assigné";
                String passager;
                if (!t.getPassagersAcceptes().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < t.getPassagersAcceptes().size(); i++) {
                        Passager p = t.getPassagersAcceptes().get(i);
                        if (i > 0) sb.append(", ");
                        sb.append(p.getNom()).append(" ").append(p.getPrenom());
                    }
                    passager = sb.toString();
                } else {
                    passager = "En attente";
                }

                String line = String.join(";",
                    t.getDepartTrajet(),
                    t.getArriveeTrajet(),
                    String.valueOf(t.getDureeTrajet().toMinutes()),
                    t.getStatusTrajet(),
                    String.format("%.2f", t.getPrix()),
                    conducteur,
                    passager
                );
                
                writer.write(line);
                writer.newLine();
            }
            
            System.out.println("✓ Export réussi: " + exportPath);
            
        } catch (IOException e) {
            System.err.println("✗ Erreur export: " + e.getMessage());
        }
    }
    
    // ============================================================
    // NOTIFICATIONS - Load/Save Methods
    // ============================================================
    
    /**
     * Load all notifications from CSV
     */
    public static List<Notification> loadNotifications() {
        initializeDataFolder();
        List<Notification> notifications = new ArrayList<>();
        
        try {
            Path path = Paths.get(NOTIFICATIONS_FILE);
            if (!Files.exists(path)) {
                System.out.println("✓ Aucun fichier de notifications (nouveau)");
                return notifications;
            }
            
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.isEmpty()) return notifications;
            
            // Skip header
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(DELIMITER, -1);
                if (parts.length < 8) continue;
                
                try {
                    String notificationId = parts[0].trim();
                    String passagerId = parts[1].trim();
                    String conducteurId = parts[2].trim();
                    String trajetId = parts[3].trim();
                    String type = parts[4].trim();
                    String message = parts[5].trim();
                    LocalDateTime dateCreation = Notification.parseDate(parts[6].trim());
                    boolean estLue = Boolean.parseBoolean(parts[7].trim());
                    
                    Notification notif = new Notification(notificationId, passagerId, conducteurId, 
                                                         trajetId, type, message, dateCreation, estLue);
                    notifications.add(notif);
                    
                } catch (Exception e) {
                    System.err.println("✗ Erreur parsing notification ligne " + (i+1) + ": " + e.getMessage());
                }
            }
            
            System.out.println("✓ " + notifications.size() + " notifications chargées");
            
        } catch (IOException e) {
            System.err.println("✗ Erreur lecture notifications: " + e.getMessage());
        }
        
        return notifications;
    }
    
    /**
     * Save all notifications for passengers and conducteurs
     */
    public static void saveAllNotifications(Gestion_covoiturage gestion) {
        saveNotifications(gestion.getNotificationsParPassager());
        saveConducteurNotifications(gestion.getNotificationsParConducteur());
    }
    
    /**
     * Save all notifications to CSV
     */
    public static void saveNotifications(Map<String, List<Notification>> notificationsParPassager) {
        initializeDataFolder();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(NOTIFICATIONS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            // BOM for UTF-8 compatibility
            writer.write('\ufeff');
            
            // Header
            writer.write("notificationId;passagerId;conducteurId;trajetId;type;message;dateCreation;estLue");
            writer.newLine();
            
            // Write all notifications
            int count = 0;
            for (List<Notification> notifs : notificationsParPassager.values()) {
                for (Notification n : notifs) {
                    String line = String.join(DELIMITER,
                        n.getNotificationId(),
                        n.getPassagerId(),
                        n.getConducteurId(),
                        n.getTrajetId(),
                        n.getType(),
                        n.getMessage(),
                        n.getDateCreationAsString(),
                        String.valueOf(n.isEstLue())
                    );
                    writer.write(line);
                    writer.newLine();
                    count++;
                }
            }
            
            System.out.println("✓ " + count + " notifications sauvegardées");
            
        } catch (IOException e) {
            System.err.println("✗ Erreur sauvegarde notifications: " + e.getMessage());
        }
    }
    
    // ============================================================
    // CONDUCTEUR NOTIFICATIONS - Load/Save Methods
    // ============================================================
    
    /**
     * Load all notifications for conducteurs from dedicated CSV file
     */
    public static List<Notification> loadConducteurNotifications() {
        initializeDataFolder();
        List<Notification> notifications = new ArrayList<>();
        
        try {
            Path path = Paths.get(CONDUCTEUR_NOTIFICATIONS_FILE);
            if (!Files.exists(path)) {
                System.out.println("✓ Aucun fichier de notifications conducteur (nouveau)");
                return notifications;
            }
            
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.isEmpty()) return notifications;
            
            // Skip header
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(DELIMITER, -1);
                if (parts.length < 8) continue;
                
                try {
                    String notificationId = parts[0].trim();
                    String conducteurId = parts[1].trim();
                    String passagerId = parts[2].trim();
                    String trajetId = parts[3].trim();
                    String type = parts[4].trim();
                    String message = parts[5].trim();
                    LocalDateTime dateCreation = Notification.parseDate(parts[6].trim());
                    boolean estLue = Boolean.parseBoolean(parts[7].trim());
                    
                    Notification notif = new Notification(notificationId, passagerId, conducteurId, 
                                                         trajetId, type, message, dateCreation, estLue);
                    notifications.add(notif);
                    
                } catch (Exception e) {
                    System.err.println("✗ Erreur parsing notification conducteur ligne " + (i+1) + ": " + e.getMessage());
                }
            }
            
            System.out.println("✓ " + notifications.size() + " notifications conducteur chargées");
            
        } catch (IOException e) {
            System.err.println("✗ Erreur lecture notifications conducteur: " + e.getMessage());
        }
        
        return notifications;
    }
    
    /**
     * Save all notifications for conducteurs from gestion object
     */
    public static void saveConducteurNotifications(Map<String, List<Notification>> notificationsParConducteur) {
        initializeDataFolder();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(CONDUCTEUR_NOTIFICATIONS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            // BOM for UTF-8 compatibility
            writer.write('\ufeff');
            
            // Header
            writer.write("notificationId;conducteurId;passagerId;trajetId;type;message;dateCreation;estLue");
            writer.newLine();
            
            // Write all notifications
            int count = 0;
            for (List<Notification> notifs : notificationsParConducteur.values()) {
                for (Notification n : notifs) {
                    String line = String.join(DELIMITER,
                        n.getNotificationId(),
                        n.getConducteurId(),
                        n.getPassagerId(),
                        n.getTrajetId(),
                        n.getType(),
                        n.getMessage(),
                        n.getDateCreationAsString(),
                        String.valueOf(n.isEstLue())
                    );
                    writer.write(line);
                    writer.newLine();
                    count++;
                }
            }
            
            System.out.println("✓ " + count + " notifications conducteur sauvegardées");
            
        } catch (IOException e) {
            System.err.println("✗ Erreur sauvegarde notifications conducteur: " + e.getMessage());
        }
    }

    /**
     * Load all notifications for admin from dedicated CSV file
     */
    public static List<Notification> loadAdminNotifications() {
        initializeDataFolder();
        List<Notification> notifications = new ArrayList<>();

        try {
            Path path = Paths.get(ADMIN_NOTIFICATIONS_FILE);
            if (!Files.exists(path)) {
                System.out.println("✓ Aucun fichier de notifications admin (nouveau)");
                return notifications;
            }

            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.isEmpty()) return notifications;

            // Skip header
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(DELIMITER, -1);

                try {
                    String notificationId;
                    String passagerId;
                    String conducteurId;
                    String trajetId;
                    String type;
                    String message;
                    LocalDateTime dateCreation;
                    boolean estLue;

                    if (parts.length >= 8) {
                        notificationId = stripBom(parts[0]).trim();
                        passagerId = parts[1].trim();
                        conducteurId = parts[2].trim();
                        trajetId = parts[3].trim();
                        type = parts[4].trim();
                        message = unescapeCSV(parts[5].trim());
                        dateCreation = Notification.parseDate(parts[6].trim());
                        estLue = Boolean.parseBoolean(parts[7].trim());
                    } else if (parts.length >= 5) {
                        notificationId = stripBom(parts[0]).trim();
                        passagerId = "ADMIN";
                        conducteurId = "ADMIN";
                        trajetId = "";
                        message = unescapeCSV(parts[1].trim());
                        type = parts[2].trim();
                        estLue = Boolean.parseBoolean(parts[3].trim());
                        dateCreation = Notification.parseDate(parts[4].trim());
                    } else {
                        continue;
                    }

                    Notification notif = new Notification(notificationId, passagerId, conducteurId,
                                                         trajetId, type, message, dateCreation, estLue);
                    notifications.add(notif);

                } catch (Exception e) {
                    System.err.println("✗ Erreur parsing notification admin ligne " + (i+1) + ": " + e.getMessage());
                }
            }

            System.out.println("✓ " + notifications.size() + " notifications admin chargées");

        } catch (IOException e) {
            System.err.println("✗ Erreur lecture notifications admin: " + e.getMessage());
        }

        return notifications;
    }

    /**
     * Save all notifications for admin from list
     */
    public static void saveAdminNotifications(List<Notification> adminNotifications) {
        initializeDataFolder();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(ADMIN_NOTIFICATIONS_FILE),
                    StandardCharsets.UTF_8))) {

            // BOM for UTF-8 compatibility
            writer.write('﻿');

            // Header
            writer.write("notificationId;passagerId;conducteurId;trajetId;type;message;dateCreation;estLue");
            writer.newLine();

            int count = 0;
            for (Notification n : adminNotifications) {
                String line = String.join(DELIMITER,
                    n.getNotificationId(),
                    n.getPassagerId(),
                    n.getConducteurId(),
                    n.getTrajetId(),
                    n.getType(),
                    escapeCSV(n.getMessage()),
                    n.getDateCreationAsString(),
                    String.valueOf(n.isEstLue())
                );
                writer.write(line);
                writer.newLine();
                count++;
            }

            System.out.println("✓ " + count + " notifications admin sauvegardées");

        } catch (IOException e) {
            System.err.println("✗ Erreur sauvegarde notifications admin: " + e.getMessage());
        }
    }

    // ============================================================
    // MESSAGES - Load/Save Methods
    // ============================================================
    
    /**
     * Load all messages from CSV
     */
    public static List<Message> loadMessages() {
        initializeDataFolder();
        List<Message> messages = new ArrayList<>();
        
        try {
            Path path = Paths.get(MESSAGES_FILE);
            if (!Files.exists(path)) {
                System.out.println("✓ Aucun fichier de messages (nouveau)");
                return messages;
            }
            
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.isEmpty()) return messages;
            
            // Skip header
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(DELIMITER, -1);
                if (parts.length < 9) continue;
                
                try {
                    String messageId = parts[0].trim();
                    String senderCin = parts[1].trim();
                    String senderName = unescapeCSV(parts[2]);
                    String recipientCin = parts[3].trim();
                    String recipientName = unescapeCSV(parts[4]);
                    String content = unescapeCSV(parts[5]);
                    LocalDateTime timestamp = LocalDateTime.parse(parts[6].trim(), 
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    boolean isDeleted = Boolean.parseBoolean(parts[7].trim());
                    String trajetId = parts[8].trim();
                    
                    Message msg = new Message(messageId, senderCin, senderName, recipientCin, 
                                             recipientName, content, timestamp, isDeleted, trajetId);
                    messages.add(msg);
                    
                } catch (Exception e) {
                    System.err.println("✗ Erreur parsing message ligne " + (i+1) + ": " + e.getMessage());
                }
            }
            
            System.out.println("✓ " + messages.size() + " messages chargés");
            
        } catch (IOException e) {
            System.err.println("✗ Erreur lecture messages: " + e.getMessage());
        }
        
        return messages;
    }

    /**
     * Load admin conversations from CSV.
     */
    public static List<Conversation> loadConversations() {
        initializeDataFolder();
        List<Conversation> conversations = new ArrayList<>();

        try {
            Path path = Paths.get(CONVERSATIONS_FILE);
            if (!Files.exists(path)) {
                return conversations;
            }

            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.isEmpty()) return conversations;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(DELIMITER, -1);
                if (parts.length < 5) continue;

                try {
                    Conversation conversation = new Conversation(
                        stripBom(unescapeCSV(parts[0])),
                        unescapeCSV(parts[1]),
                        unescapeCSV(parts[2]),
                        unescapeCSV(parts[3]),
                        Conversation.parseDate(parts[4].trim())
                    );
                    conversations.add(conversation);
                } catch (Exception e) {
                    System.err.println("✗ Erreur parsing conversation ligne " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("✗ Erreur lecture conversations: " + e.getMessage());
        }

        return conversations;
    }

    /**
     * Save all admin conversations to CSV.
     */
    public static void saveConversations(List<Conversation> conversations) {
        initializeDataFolder();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(CONVERSATIONS_FILE),
                    StandardCharsets.UTF_8))) {

            writer.write('\ufeff');
            writer.write("id;user_id;admin_id;triggered_by;created_at");
            writer.newLine();

            for (Conversation conversation : conversations) {
                String line = String.join(DELIMITER,
                    escapeCSV(conversation.getId()),
                    escapeCSV(conversation.getUserId()),
                    escapeCSV(conversation.getAdminId()),
                    escapeCSV(conversation.getTriggeredBy()),
                    escapeCSV(conversation.getCreatedAtAsString())
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("✗ Erreur sauvegarde conversations: " + e.getMessage());
        }
    }
    
    // ============================================================
    // GROUPS - Load/Save Methods
    // ============================================================

    /**
     * Load all groups from CSV
     */
    public static List<Group> loadGroups() {
        initializeDataFolder();
        List<Group> groups = new ArrayList<>();

        try {
            Path path = Paths.get(GROUPS_FILE);
            if (!Files.exists(path)) {
                System.out.println("✓ Aucun fichier de groupes (nouveau)");
                return groups;
            }

            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.isEmpty()) return groups;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("﻿")) line = line.substring(1);

                String[] parts = line.split(DELIMITER, -1);
                if (parts.length < 5) continue;

                try {
                    String groupId = unescapeCSV(parts[0]);
                    String groupName = unescapeCSV(parts[1]);
                    String conducteurCin = unescapeCSV(parts[2]);
                    String memberCinsStr = unescapeCSV(parts[3]);
                    LocalDateTime dateCreation = Group.parseDate(parts[4].trim());

                    List<String> memberCins = new ArrayList<>();
                    if (!memberCinsStr.isEmpty()) {
                        for (String c : memberCinsStr.split(",")) {
                            String trimmed = c.trim();
                            if (!trimmed.isEmpty()) memberCins.add(trimmed);
                        }
                    }

                    Group g = new Group(groupId, groupName, conducteurCin, memberCins, dateCreation);
                    groups.add(g);
                } catch (Exception e) {
                    System.err.println("✗ Erreur parsing groupe ligne " + (i + 1) + ": " + e.getMessage());
                }
            }

            System.out.println("✓ " + groups.size() + " groupes chargés");
        } catch (IOException e) {
            System.err.println("✗ Erreur lecture groupes: " + e.getMessage());
        }

        return groups;
    }

    /**
     * Save all groups to CSV
     */
    public static void saveGroups(List<Group> groups) {
        initializeDataFolder();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(GROUPS_FILE),
                    StandardCharsets.UTF_8))) {

            writer.write('﻿');
            writer.write("groupId;groupName;conducteurCin;memberCins;dateCreation");
            writer.newLine();

            int count = 0;
            for (Group g : groups) {
                String line = String.join(DELIMITER,
                        escapeCSV(g.getGroupId()),
                        escapeCSV(g.getGroupName()),
                        escapeCSV(g.getConducteurCin()),
                        escapeCSV(g.getMemberCinsAsString()),
                        escapeCSV(g.getDateCreationAsString())
                );
                writer.write(line);
                writer.newLine();
                count++;
            }

            System.out.println("✓ " + count + " groupes sauvegardés");
        } catch (IOException e) {
            System.err.println("✗ Erreur sauvegarde groupes: " + e.getMessage());
        }
    }

    // ============================================================
    // GROUP MESSAGES - Load/Save Methods
    // ============================================================

    /**
     * Load all group messages from CSV
     */
    public static List<GroupMessage> loadGroupMessages() {
        initializeDataFolder();
        List<GroupMessage> messages = new ArrayList<>();

        try {
            Path path = Paths.get(GROUP_MESSAGES_FILE);
            if (!Files.exists(path)) {
                System.out.println("✓ Aucun fichier de messages de groupe (nouveau)");
                return messages;
            }

            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.isEmpty()) return messages;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("﻿")) line = line.substring(1);

                String[] parts = line.split(DELIMITER, -1);
                if (parts.length < 7) continue;

                try {
                    String messageId = unescapeCSV(parts[0]);
                    String groupId = unescapeCSV(parts[1]);
                    String senderCin = unescapeCSV(parts[2]);
                    String senderName = unescapeCSV(parts[3]);
                    String content = unescapeCSV(parts[4]);
                    LocalDateTime timestamp = GroupMessage.parseTimestamp(parts[5].trim());
                    boolean isDeleted = Boolean.parseBoolean(parts[6].trim());

                    GroupMessage m = new GroupMessage(messageId, groupId, senderCin, senderName,
                            content, timestamp, isDeleted);
                    messages.add(m);
                } catch (Exception e) {
                    System.err.println("✗ Erreur parsing message groupe ligne " + (i + 1) + ": " + e.getMessage());
                }
            }

            System.out.println("✓ " + messages.size() + " messages de groupe chargés");
        } catch (IOException e) {
            System.err.println("✗ Erreur lecture messages de groupe: " + e.getMessage());
        }

        return messages;
    }

    /**
     * Save all group messages to CSV
     */
    public static void saveGroupMessages(List<GroupMessage> messages) {
        initializeDataFolder();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(GROUP_MESSAGES_FILE),
                    StandardCharsets.UTF_8))) {

            writer.write('﻿');
            writer.write("messageId;groupId;senderCin;senderName;content;timestamp;isDeleted");
            writer.newLine();

            int count = 0;
            for (GroupMessage m : messages) {
                String line = String.join(DELIMITER,
                        escapeCSV(m.getMessageId()),
                        escapeCSV(m.getGroupId()),
                        escapeCSV(m.getSenderCin()),
                        escapeCSV(m.getSenderName()),
                        escapeCSV(m.getContent()),
                        escapeCSV(m.getFormattedTimestamp()),
                        String.valueOf(m.isDeleted())
                );
                writer.write(line);
                writer.newLine();
                count++;
            }

            System.out.println("✓ " + count + " messages de groupe sauvegardés");
        } catch (IOException e) {
            System.err.println("✗ Erreur sauvegarde messages de groupe: " + e.getMessage());
        }
    }

    // ============================================================
    // EVALUATIONS - Load/Save Methods
    // ============================================================

    /**
     * Load all evaluations from CSV
     */
    public static List<Evaluation> loadEvaluations() {
        initializeDataFolder();
        List<Evaluation> evaluations = new ArrayList<>();

        try {
            Path path = Paths.get(EVALUATIONS_FILE);
            if (!Files.exists(path)) {
                System.out.println("✓ Aucun fichier d'évaluations (nouveau)");
                return evaluations;
            }

            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            if (lines.isEmpty()) return evaluations;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("﻿")) line = line.substring(1);

                String[] parts = line.split(DELIMITER, -1);
                if (parts.length < 8) continue;

                try {
                    String evaluationId = unescapeCSV(parts[0]);
                    String passagerCin = unescapeCSV(parts[1]);
                    String passagerName = unescapeCSV(parts[2]);
                    String conducteurCin = unescapeCSV(parts[3]);
                    String trajetId = unescapeCSV(parts[4]);
                    int rating = Integer.parseInt(parts[5].trim());
                    String comment = unescapeCSV(parts[6]);
                    LocalDateTime dateCreation = Evaluation.parseDate(parts[7].trim());

                    Evaluation eval = new Evaluation(evaluationId, passagerCin, passagerName,
                            conducteurCin, trajetId, rating, comment, dateCreation);
                    evaluations.add(eval);
                } catch (Exception e) {
                    System.err.println("✗ Erreur parsing évaluation ligne " + (i + 1) + ": " + e.getMessage());
                }
            }

            System.out.println("✓ " + evaluations.size() + " évaluations chargées");
        } catch (IOException e) {
            System.err.println("✗ Erreur lecture évaluations: " + e.getMessage());
        }

        return evaluations;
    }

    /**
     * Save all evaluations to CSV
     */
    public static void saveEvaluations(List<Evaluation> evaluations) {
        initializeDataFolder();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(EVALUATIONS_FILE),
                    StandardCharsets.UTF_8))) {

            writer.write('﻿');
            writer.write("evaluationId;passagerCin;passagerName;conducteurCin;trajetId;rating;comment;dateCreation");
            writer.newLine();

            int count = 0;
            for (Evaluation e : evaluations) {
                String line = String.join(DELIMITER,
                        escapeCSV(e.getEvaluationId()),
                        escapeCSV(e.getPassagerCin()),
                        escapeCSV(e.getPassagerName()),
                        escapeCSV(e.getConducteurCin()),
                        escapeCSV(e.getTrajetId()),
                        String.valueOf(e.getRating()),
                        escapeCSV(e.getComment()),
                        escapeCSV(e.getDateCreationAsString())
                );
                writer.write(line);
                writer.newLine();
                count++;
            }

            System.out.println("✓ " + count + " évaluations sauvegardées");
        } catch (IOException ioe) {
            System.err.println("✗ Erreur sauvegarde évaluations: " + ioe.getMessage());
        }
    }

    /**
     * Save all messages to CSV
     */
    public static void saveMessages(List<Message> messages) {
        initializeDataFolder();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(MESSAGES_FILE), 
                    StandardCharsets.UTF_8))) {
            
            // BOM for UTF-8 compatibility
            writer.write('\ufeff');
            
            // Header
            writer.write("messageId;senderCin;senderName;recipientCin;recipientName;content;timestamp;isDeleted;trajetId");
            writer.newLine();
            
            // Write all messages
            int count = 0;
            for (Message msg : messages) {
                String line = String.join(DELIMITER,
                    escapeCSV(msg.getMessageId()),
                    escapeCSV(msg.getSenderCin()),
                    escapeCSV(msg.getSenderName()),
                    escapeCSV(msg.getRecipientCin()),
                    escapeCSV(msg.getRecipientName()),
                    escapeCSV(msg.getContent()),
                    escapeCSV(msg.getFormattedTimestamp()),
                    String.valueOf(msg.isDeleted()),
                    escapeCSV(msg.getTrajetId())
                );
                writer.write(line);
                writer.newLine();
                count++;
            }
            
            System.out.println("✓ " + count + " messages sauvegardés");
            
        } catch (IOException e) {
            System.err.println("✗ Erreur sauvegarde messages: " + e.getMessage());
        }
    }
}
