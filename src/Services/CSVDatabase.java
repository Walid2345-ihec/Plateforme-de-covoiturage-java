package Services;

import Models.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
    public static void saveConducteurs(Vector<User> users) {
        initializeDataFolder();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(CONDUCTEURS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            // HEADER ROW - defines the columns
            writer.write("CIN;Nom;Prenom;Tel;AnneeUniv;Adresse;Mail;PasswordHash;NomVoiture;MarqueVoiture;Matricule;PlacesDisponibles");
            writer.newLine();
            
            // DATA ROWS - one per conducteur
            for (User user : users) {
                if (user instanceof Conducteur) {
                    Conducteur c = (Conducteur) user;
                    
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
                        String.valueOf(c.getPlacesDisponibles())
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
     * Saves all passagers to CSV file.
     */
    public static void savePassagers(Vector<User> users) {
        initializeDataFolder();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(PASSAGERS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            // HEADER ROW
            writer.write("CIN;Nom;Prenom;Tel;AnneeUniv;Adresse;Mail;PasswordHash;ChercheCovoit");
            writer.newLine();
            
            // DATA ROWS
            for (User user : users) {
                if (user instanceof Passager) {
                    Passager p = (Passager) user;
                    
                    String line = String.join(DELIMITER,
                        escapeCSV(p.getCin()),
                        escapeCSV(p.getNom()),
                        escapeCSV(p.getPrenom()),
                        escapeCSV(p.getTel()),
                        String.valueOf(p.getAnneeUniversitaire().getValue()),
                        escapeCSV(p.getAdresse()),
                        escapeCSV(p.getMail()),
                        escapeCSV(p.getPasswordHash() != null ? p.getPasswordHash() : ""),
                        String.valueOf(p.isChercheCovoit())
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
    public static void saveTrajets(Vector<Trajet> trajets) {
        initializeDataFolder();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(TRAJETS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            // HEADER ROW
            // Nouveau format: ajout de MaxPlaces;AcceptedCINs;PendingCINs
            writer.write("Depart;Arrivee;DureeMinutes;Status;Prix;ConducteurCIN;PassagerCIN;MaxPlaces;AcceptedCINs;PendingCINs");
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
                    escapeCSV(pending)
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
                            Integer.parseInt(values[11]) // PlacesDisponibles
                        );
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
    public static List<Trajet> loadTrajets(Vector<User> users) {
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
                        Vector<Passager> accepted = new Vector<>();
                        Vector<Passager> pending = new Vector<>();

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

                        Trajet t = new Trajet(
                            unescapeCSV(values[0]),  // Depart
                            unescapeCSV(values[1]),  // Arrivee
                            Duration.ofMinutes(Long.parseLong(values[2])), // Duree
                            unescapeCSV(values[3]),  // Status
                            Float.parseFloat(values[4]), // Prix
                            conducteur,
                            maxPlaces
                        );

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
        
        // Remove surrounding quotes
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            // Unescape internal quotes
            value = value.replace("\"\"", "\"");
        }
        return value;
    }
    
    /**
     * Finds a Conducteur by CIN in the users list.
     */
    private static Conducteur findConducteurByCIN(Vector<User> users, String cin) {
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
    private static Passager findPassagerByCIN(Vector<User> users, String cin) {
        if (cin == null || cin.trim().isEmpty()) return null;
        
        for (User u : users) {
            if (u instanceof Passager && u.getCin().equals(cin.trim())) {
                return (Passager) u;
            }
        }
        return null;
    }
    
    // ============================================================
    // STEP 5: Convenience Methods - Save/Load All
    // ============================================================
    
    /**
     * Saves all data to CSV files.
     * Call this when the application closes or after important changes.
     */
    public static void saveAllData(Gestion_covoiturage gestion) {
        System.out.println("\n📁 Sauvegarde des données...");
        saveConducteurs(gestion.getUsers());
        savePassagers(gestion.getUsers());
        saveTrajets(gestion.getTrajets());
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
        
        // Load trajets (needs users to be loaded first)
        List<Trajet> trajets = loadTrajets(gestion.getUsers());
        for (Trajet t : trajets) {
            gestion.getTrajets().add(t);
        }
        
        System.out.println("✓ Toutes les données chargées!\n");
    }
    
    // ============================================================
    // STEP 6: Export to Standard CSV (for external use)
    // ============================================================
    
    /**
     * Exports data to a user-friendly CSV file.
     * This creates a nicely formatted file that can be opened in Excel.
     */
    public static void exportToExcelCSV(Vector<Trajet> trajets, String filename) {
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
}
