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
 * CSVDatabase - Classe utilitaire pour la lecture et l'écriture de données dans des fichiers CSV.
 *
 * EXPLICATION PAS À PAS :
 * 1. Structure CSV :
 *    - Chaque fichier représente une "table" (conducteurs, passagers, trajets)
 *    - Première ligne = en-têtes de colonnes
 *    - Lignes suivantes = enregistrements de données
 *    - Valeurs séparées par des points-virgules (;) pour éviter les conflits avec la ponctuation française
 *
 * 2. Opérations clés :
 *    - LECTURE : charger les données depuis les fichiers CSV vers des objets Java
 *    - ÉCRITURE : sauvegarder les objets Java dans des fichiers CSV
 *    - SAUVEGARDE/APPEND : création de backups avant écriture, rotation des backups
 *
 */
public class CSVDatabase {
    
    // ============================================================
    // CONFIGURATION - chemins de fichiers pour chaque "table"
    // ============================================================
    
    private static final String DATA_FOLDER = "data/";
    private static final String BACKUP_FOLDER = "data/backups/";
    private static final String CONDUCTEURS_FILE = DATA_FOLDER + "conducteurs.csv";
    private static final String PASSAGERS_FILE = DATA_FOLDER + "passagers.csv";
    private static final String TRAJETS_FILE = DATA_FOLDER + "trajets.csv";
    
    // Délimiteur - utilisation du point-virgule pour la compatibilité avec les textes français
    private static final String DELIMITER = ";";
    
    // Nombre maximal de fichiers de sauvegarde à conserver
    private static final int MAX_BACKUPS = 5;
    
    // ============================================================
    // ÉTAPE 1 : Initialisation du dossier de données
    // ============================================================
    
    /**
     * Crée le dossier de données s'il n'existe pas.
     * Toujours appeler cette méthode avant de lire/écrire sur le disque.
     */
    public static void initializeDataFolder() {
        try {
            Path dataPath = Paths.get(DATA_FOLDER);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                System.out.println("✓ Dossier 'data/' créé avec succès");
            }
            // Crée également le dossier de backups
            Path backupPath = Paths.get(BACKUP_FOLDER);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
            }
        } catch (IOException e) {
            System.err.println("✗ Erreur création dossier: " + e.getMessage());
        }
    }
    
    // ============================================================
    // SYSTÈME DE SAUVEGARDE ET RESTAURATION
    // ============================================================
    
    /**
     * Crée une sauvegarde (backup) de tous les fichiers CSV avant d'écrire.
     * Les backups sont horodatés et on effectue une rotation pour ne pas dépasser MAX_BACKUPS.
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
        
        // Rotation des anciens backups
        rotateBackups();
        System.out.println("✓ Backup créé: " + timestamp);
    }
    
    /**
     * Supprime les anciens fichiers de sauvegarde, en conservant uniquement les plus récents.
     */
    private static void rotateBackups() {
        try {
            Path backupDir = Paths.get(BACKUP_FOLDER);
            if (!Files.exists(backupDir)) return;
            
            // Regroupe les backups par nom de base et conserve MAX_BACKUPS par groupe
            java.util.Map<String, java.util.List<Path>> backupGroups = new java.util.HashMap<>();
            
            Files.list(backupDir)
                .filter(p -> p.toString().endsWith(".csv"))
                .forEach(p -> {
                    String name = p.getFileName().toString();
                    // Extrait le nom de base (ex: "conducteurs" depuis "conducteurs_20241217_143022.csv")
                    int underscoreIdx = name.indexOf('_');
                    if (underscoreIdx > 0) {
                        String baseName = name.substring(0, underscoreIdx);
                        backupGroups.computeIfAbsent(baseName, k -> new java.util.ArrayList<>()).add(p);
                    }
                });
            
            // Pour chaque groupe, tri par date de modification et suppression des plus anciens si > MAX_BACKUPS
            for (java.util.List<Path> group : backupGroups.values()) {
                if (group.size() > MAX_BACKUPS) {
                    group.sort((a, b) -> {
                        try {
                            return Files.getLastModifiedTime(b).compareTo(Files.getLastModifiedTime(a));
                        } catch (IOException e) {
                            return 0;
                        }
                    });
                    
                    // Supprimer les backups les plus anciens
                    for (int i = MAX_BACKUPS; i < group.size(); i++) {
                        try {
                            Files.delete(group.get(i));
                        } catch (IOException e) {
                            // Ignorer les erreurs de suppression
                        }
                    }
                }
            }
        } catch (IOException e) {
            // Ignorer les erreurs de rotation
        }
    }
    
    /**
     * Tente de restaurer les fichiers de données à partir du backup le plus récent.
     * Utiliser cette méthode si les fichiers principaux sont corrompus.
     *
     * @return true si la restauration a réussi
     */
    public static boolean restoreFromBackup() {
        try {
            Path backupDir = Paths.get(BACKUP_FOLDER);
            if (!Files.exists(backupDir)) {
                System.err.println("✗ Aucun dossier de backup trouvé");
                return false;
            }
            
            // Trouve le backup le plus récent pour chaque type de fichier
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
    // ÉTAPE 2 : Opérations D'ÉCRITURE - sauvegarde des données en CSV
    // ============================================================
    
    /**
     * Sauvegarde tous les conducteurs dans le fichier CSV correspondant.
     *
     * COMMENTAIRE DE FONCTIONNEMENT :
     * 1. Ouvre un BufferedWriter (efficace pour l'écriture de texte)
     * 2. Écrit la ligne d'en-tête
     * 3. Parcourt chaque Conducteur et écrit ses champs séparés par DELIMITER
     *
     * @param users Vecteur d'objets User (seules les instances Conducteur sont sauvegardées ici)
     */
    public static void saveConducteurs(Vector<User> users) {
        initializeDataFolder();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(CONDUCTEURS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            // LIGNE D'EN-TÊTE - définit les colonnes
            writer.write("CIN;Nom;Prenom;Tel;AnneeUniv;Adresse;Mail;PasswordHash;NomVoiture;MarqueVoiture;Matricule;PlacesDisponibles");
            writer.newLine();
            
            // LIGNES DE DONNÉES - une par conducteur
            for (User user : users) {
                if (user instanceof Conducteur) {
                    Conducteur c = (Conducteur) user;
                    
                    // Construit la ligne CSV en joignant les champs par le délimiteur
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
     * Sauvegarde tous les passagers dans le fichier CSV correspondant.
     */
    public static void savePassagers(Vector<User> users) {
        initializeDataFolder();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(PASSAGERS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            // LIGNE D'EN-TÊTE
            writer.write("CIN;Nom;Prenom;Tel;AnneeUniv;Adresse;Mail;PasswordHash;ChercheCovoit");
            writer.newLine();
            
            // LIGNES DE DONNÉES
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
     * Sauvegarde tous les trajets dans le fichier CSV correspondant.
     */
    public static void saveTrajets(Vector<Trajet> trajets) {
        initializeDataFolder();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(TRAJETS_FILE), 
                    StandardCharsets.UTF_8))) {
            
            // LIGNE D'EN-TÊTE
            // Nouveau format: ajout de MaxPlaces;AcceptedCINs;PendingCINs
            writer.write("Depart;Arrivee;DureeMinutes;Status;Prix;ConducteurCIN;PassagerCIN;MaxPlaces;AcceptedCINs;PendingCINs");
            writer.newLine();
            
            // LIGNES DE DONNÉES
            for (Trajet t : trajets) {
                String conducteurCIN = (t.getConducteur() != null) ? t.getConducteur().getCin() : "";
                // Pour compatibilité ascendante, fournir le premier CIN de passager accepté dans l'ancienne colonne
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
    // ÉTAPE 3 : Opérations DE LECTURE - chargement des données depuis CSV
    // ============================================================
    
    /**
     * Charge les conducteurs depuis le fichier CSV.
     *
     * COMMENTAIRE DE FONCTIONNEMENT :
     * 1. Ouvre un BufferedReader (efficace pour la lecture de texte)
     * 2. Saute la ligne d'en-tête
     * 3. Lit chaque ligne et split par DELIMITER
     * 4. Crée des objets Conducteur à partir des valeurs
     *
     * @return Liste d'objets Conducteur
     */
    public static List<Conducteur> loadConducteurs() {
        List<Conducteur> conducteurs = new ArrayList<>();
        Path filePath = Paths.get(CONDUCTEURS_FILE);
        
        // Vérifie si le fichier existe
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
                // Saute la ligne d'en-tête
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                // Saute les lignes vides
                if (line.trim().isEmpty()) continue;
                
                // Sépare la ligne par le délimiteur
                String[] values = line.split(DELIMITER, -1); // -1 conserve les valeurs vides

                // Valide qu'il y a suffisamment de colonnes
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
                            unescapeCSV(values[7]),  // PasswordHash (déjà haché)
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
     * Charge les passagers depuis le fichier CSV.
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
                        // Remarque : le constructeur de Passager peut nécessiter une référence vers un Conducteur
                        // Nous passons null initialement ; les relations seront recréées lors du chargement des trajets
                        Passager p = new Passager(
                            unescapeCSV(values[0]),  // CIN
                            unescapeCSV(values[1]),  // Nom
                            unescapeCSV(values[2]),  // Prenom
                            unescapeCSV(values[3]),  // Tel
                            Year.of(Integer.parseInt(values[4])), // AnneeUniv
                            unescapeCSV(values[5]),  // Adresse
                            unescapeCSV(values[6]),  // Mail
                            unescapeCSV(values[7]),  // PasswordHash (déjà haché)
                            true,                     // isHashedPassword = true
                            Boolean.parseBoolean(values[8]), // ChercheCovoit
                            null  // Conducteur - sera défini lors du chargement des trajets
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
     * Charge les trajets depuis le fichier CSV.
     * Remarque : nécessite que les conducteurs et passagers soient chargés au préalable
     * afin de reconstruire les références par CIN.
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
                        // Recherche du conducteur et du passager par CIN
                        Conducteur conducteur = findConducteurByCIN(users, values[5]);
                        Passager passager = findPassagerByCIN(users, values[6]);

                        // Si format nouveau (avec maxPlaces et listes)
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
                            // Ancien format : si passager non nul, l'ajouter aux acceptés
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

                        // Attacher les listes de passagers acceptés et en attente
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
    // ÉTAPE 4 : Méthodes d'aide
    // ============================================================
    
    /**
     * Échappe les caractères spéciaux dans les valeurs CSV.
     * Si une valeur contient le délimiteur ou des guillemets, on l'entoure de guillemets
     * et on double les guillemets internes conformément à la norme CSV.
     */
    private static String escapeCSV(String value) {
        if (value == null) return "";
        
        // Si contient le délimiteur ou des guillemets ou des retours à la ligne, entourer de guillemets et échapper
        if (value.contains(DELIMITER) || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * Retire les échappements appliqués aux valeurs CSV.
     */
    private static String unescapeCSV(String value) {
        if (value == null) return "";
        value = value.trim();
        
        // Supprime les guillemets entourants
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            // Déséchapper les guillemets internes
            value = value.replace("\"\"", "\"");
        }
        return value;
    }
    
    /**
     * Recherche un Conducteur par CIN dans la liste d'utilisateurs.
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
     * Recherche un Passager par CIN dans la liste d'utilisateurs.
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
    // ÉTAPE 5 : Méthodes de commodité - Sauvegarder/Charger tout
    // ============================================================
    
    /**
     * Sauvegarde toutes les données dans les fichiers CSV.
     * Appeler cette méthode lors de la fermeture de l'application ou après des modifications importantes.
     */
    public static void saveAllData(Gestion_covoiturage gestion) {
        System.out.println("\n📁 Sauvegarde des données...");
        saveConducteurs(gestion.getUsers());
        savePassagers(gestion.getUsers());
        saveTrajets(gestion.getTrajets());
        System.out.println("✓ Toutes les données sauvegardées!\n");
    }
    
    /**
     * Charge toutes les données depuis les fichiers CSV dans l'objet de gestion.
     * Appeler cette méthode au démarrage de l'application.
     */
    public static void loadAllData(Gestion_covoiturage gestion) {
        System.out.println("\n📂 Chargement des données...");
        
        // Charger les conducteurs
        List<Conducteur> conducteurs = loadConducteurs();
        for (Conducteur c : conducteurs) {
            gestion.getUsers().add(c);
        }
        
        // Charger les passagers
        List<Passager> passagers = loadPassagers();
        for (Passager p : passagers) {
            gestion.getUsers().add(p);
        }
        
        // Charger les trajets (nécessite que les users soient chargés en premier)
        List<Trajet> trajets = loadTrajets(gestion.getUsers());
        for (Trajet t : trajets) {
            gestion.getTrajets().add(t);
        }
        
        System.out.println("✓ Toutes les données chargées!\n");
    }
    
    // ============================================================
    // ÉTAPE 6 : Export vers CSV standard (pour usage externe)
    // ============================================================
    
    /**
     * Exporte les données vers un CSV lisible par l'utilisateur (Excel, etc.).
     * Crée un fichier bien formaté pouvant être ouvert par Excel.
     */
    public static void exportToExcelCSV(Vector<Trajet> trajets, String filename) {
        initializeDataFolder();
        String exportPath = DATA_FOLDER + filename;
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(exportPath), 
                    StandardCharsets.UTF_8))) {
            
            // BOM pour compatibilité Excel UTF-8
            writer.write('\ufeff');
            
            // En-tête avec libellés en français
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
