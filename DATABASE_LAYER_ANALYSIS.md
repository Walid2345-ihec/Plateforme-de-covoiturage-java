# Database Layer Analysis - Covoiturage Platform

## PROJECT OVERVIEW
This is a **CSV-based database system** (no SQL) with two main service classes orchestrating all data operations:
- **CSVDatabase.java** - Low-level CSV read/write operations
- **Gestion_covoiturage.java** - Business logic & in-memory data management

---

## 📊 DATA FILES & STORAGE STRUCTURE

| CSV File | Entity | Purpose |
|----------|--------|---------|
| `conducteurs.csv` | Conducteur | Driver records with vehicle info |
| `passagers.csv` | Passager | Passenger records |
| `trajets.csv` | Trajet | Ride/journey records |
| `notifications.csv` | Notification | Passenger notifications |
| `conducteur_notifications.csv` | Notification | Driver notifications |
| `notifications_admin.csv` | Notification | Admin notifications |
| `messages.csv` | Message | Private messages between users |
| `conversations.csv` | Conversation | User-Admin conversations |
| `reclamations.csv` | Reclamation | User complaints/disputes |
| `groups.csv` | Group | Carpooling groups |
| `group_messages.csv` | GroupMessage | Group chat messages |
| `evaluations.csv` | Evaluation | Driver ratings by passengers |
| `admins.csv` | Admin | Administrator accounts |

---

## 🔧 SERVICE LAYER - CSVDatabase.java

### Class Purpose
Utility class handling all file I/O operations with CSV format.

**Configuration:**
- Delimiter: `;` (semicolon - supports French text with commas)
- Encoding: UTF-8 with BOM
- Backup System: Timestamped backups (max 5 per file)

---

### PUBLIC METHODS - CRUD OPERATIONS

#### **USER MANAGEMENT**

##### `loadConducteurs()` → List<Conducteur>
- **Operation Type:** SELECT (READ)
- **File:** `conducteurs.csv`
- **Columns Read:** CIN, Nom, Prenom, Tel, AnneeUniv, Adresse, Mail, PasswordHash, NomVoiture, MarqueVoiture, Matricule, PlacesDisponibles, WeeklySchedule, MoyenneEvaluation, Card, Banned
- **Returns:** All drivers as Conducteur objects
- **Business Rules Applied:**
  - Validates CIN format (8 digits)
  - Loads hashed passwords (no plain text)
  - Card normalization (verte/jaune/rouge)
  - Handles missing optional fields gracefully
- **Error Handling:** Logs parse errors per row, continues processing

##### `saveConducteurs(List<User> users)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `conducteurs.csv`
- **Process:**
  1. Filters User list for Conducteur instances only
  2. Escapes special characters in CSV values
  3. Writes header row
  4. Writes one row per conductor with all fields
- **Called From:** GUI when saving driver registration or modifications
- **Validation:** None here (assumes Model validation succeeded)

##### `loadPassagers()` → List<Passager>
- **Operation Type:** SELECT (READ)
- **File:** `passagers.csv`
- **Columns Read:** CIN, Nom, Prenom, Tel, AnneeUniv, Adresse, Mail, PasswordHash, ChercheCovoit, Card, Banned
- **Returns:** All passengers as Passager objects
- **Business Rules Applied:**
  - CIN validation (8 digits)
  - Loads hashed passwords
  - Card status tracking
  - Sets `chercheCovoit` boolean per user preference

##### `savePassagers(List<User> users)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `passagers.csv`
- **Process:** Same as saveConducteurs but for Passager instances

##### `loadAdmins()` → List<Admin>
- **Operation Type:** SELECT (READ)
- **File:** `admins.csv`
- **Features:** Flexible column mapping with header detection
- **Fallback:** Creates default admin if file missing
- **Columns:** CIN, Nom, Prenom, Mail, PasswordHash, Role, DateCreation

##### `saveAdmins(List<User> users)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `admins.csv`

---

#### **RIDE/JOURNEY MANAGEMENT**

##### `loadTrajets(List<User> users)` → List<Trajet>
- **Operation Type:** SELECT (READ)
- **File:** `trajets.csv`
- **Columns Read:** Depart, Arrivee, DureeMinutes, Status, Prix, ConducteurCIN, PassagerCIN, MaxPlaces, AcceptedCINs, PendingCINs, StartDateTime, EndDateTime, WeeklySchedule
- **Relationships Resolved:**
  - Finds Conducteur by CIN
  - Loads accepted passengers from comma-separated CINs
  - Loads pending passengers from comma-separated CINs
- **Date/Time Handling:** ISO 8601 format parsing
- **Status Values:** PENDING, PENDING_APPROVAL, IN_PROGRESS, FINISHED
- **Error Handling:** Validates references, skips invalid entries

##### `saveTrajets(List<Trajet> trajets)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `trajets.csv`
- **Data Serialization:**
  - Converts passager lists to CSV format (comma-separated CINs)
  - Converts Duration to minutes
  - Exports LocalDateTime to ISO 8601
  - Stores conducteur as CIN reference

##### `updateConductorWeeklySchedule(Conducteur, Gestion_covoiturage)`
- **Operation Type:** UPDATE
- **File:** `conducteurs.csv` (via saveConducteurs)
- **Process:**
  1. Finds conductor in users list
  2. Updates weeklySchedule field
  3. Saves entire conducteur list back to CSV
- **Called From:** Weekly schedule creation UI

---

#### **NOTIFICATION MANAGEMENT**

##### `loadNotifications()` → List<Notification>
- **Operation Type:** SELECT (READ)
- **File:** `notifications.csv` (passenger notifications)
- **Columns:** notificationId, passagerId, conducteurId, trajetId, type, message, dateCreation, estLue
- **Notification Types:** ACCEPTATION, REFUS, ANNULATION, etc.

##### `loadConducteurNotifications()` → List<Notification>
- **Operation Type:** SELECT (READ)
- **File:** `conducteur_notifications.csv` (driver notifications)
- **Same structure as loadNotifications but for conductors**

##### `loadAdminNotifications()` → List<Notification>
- **Operation Type:** SELECT (READ)
- **File:** `notifications_admin.csv`
- **Flexible parsing:** Handles multiple CSV formats for backward compatibility

##### `saveNotifications(Map<String,List<Notification>>)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `notifications.csv`
- **Saves from:** notifications_par_passager map in Gestion_covoiturage

##### `saveConducteurNotifications(Map<String,List<Notification>>)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `conducteur_notifications.csv`
- **Saves from:** notifications_par_conducteur map

##### `saveAdminNotifications(List<Notification>)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `notifications_admin.csv`

---

#### **MESSAGE MANAGEMENT**

##### `loadMessages()` → List<Message>
- **Operation Type:** SELECT (READ)
- **File:** `messages.csv`
- **Columns:** messageId, senderCin, senderName, recipientCin, recipientName, content, timestamp, isDeleted, trajetId
- **Timestamp Format:** yyyy-MM-dd HH:mm:ss
- **Soft Delete:** isDeleted flag (message content replaced with "Message supprimé")

##### `saveMessages(List<Message>)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `messages.csv`
- **Called From:** Messaging UI after message send

---

#### **GROUP MANAGEMENT**

##### `loadGroups()` → List<Group>
- **Operation Type:** SELECT (READ)
- **File:** `groups.csv`
- **Columns:** groupId, groupName, conducteurCin, memberCins (comma-separated), dateCreation
- **Parsing:** Splits memberCins string to List<String>

##### `saveGroups(List<Group>)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `groups.csv`
- **Serialization:** Joins List<String> members to comma-separated string

##### `loadGroupMessages()` → List<GroupMessage>
- **Operation Type:** SELECT (READ)
- **File:** `group_messages.csv`
- **Columns:** messageId, groupId, senderCin, senderName, content, timestamp, isDeleted
- **Timestamp Format:** yyyy-MM-dd HH:mm:ss

##### `saveGroupMessages(List<GroupMessage>)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `group_messages.csv`

---

#### **CONVERSATION & DISPUTE MANAGEMENT**

##### `loadConversations()` → List<Conversation>
- **Operation Type:** SELECT (READ)
- **File:** `conversations.csv`
- **Columns:** id, userId, adminId, triggeredBy, createdAt
- **Purpose:** Track user-admin conversations for support/help requests

##### `saveConversations(List<Conversation>)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `conversations.csv`

##### `loadReclamations()` → List<Reclamation>
- **Operation Type:** SELECT (READ)
- **File:** `reclamations.csv`
- **Columns:** id, reservation_id, complainant_id, complainant_role, accused_id, accused_role, preset, message, created_at
- **Preset Reasons:** Enum ReclamationPreset with predefined complaint reasons
- **Backward Compatibility:** Handles CSV with/without preset column

##### `saveReclamations(List<Reclamation>)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `reclamations.csv`

---

#### **EVALUATION MANAGEMENT**

##### `loadEvaluations()` → List<Evaluation>
- **Operation Type:** SELECT (READ)
- **File:** `evaluations.csv`
- **Columns:** evaluationId, passagerCin, passagerName, conducteurCin, trajetId, rating (1-5), comment, dateCreation
- **Rating Validation:** Clamped to 1-5 range

##### `saveEvaluations(List<Evaluation>)`
- **Operation Type:** INSERT/UPDATE (WRITE)
- **File:** `evaluations.csv`

---

#### **ADMIN/DRIVER WEEKLY SCHEDULE**

##### `updateConductorWeeklySchedule(Conducteur, Gestion_covoiturage)`
- **Operation Type:** UPDATE
- **File:** `conducteurs.csv` (indirectly via saveConducteurs)
- **Format:** WeeklySchedule = "MON:09:00-17:00|TUE:09:00-17:00|..."
- **Business Logic:** Allows recurring rides on specific days/times

---

#### **BULK OPERATIONS**

##### `loadAllData(Gestion_covoiturage)`
- **Operation Type:** SELECT (READ - BULK)
- **Process:**
  1. Loads conducteurs → adds to users list
  2. Loads passagers → adds to users list
  3. Loads admins → adds to users list
  4. Loads trajets (with references resolved)
  5. Loads notifications (both passenger and conductor)
  6. Loads admin notifications
  7. Loads conversations, reclamations
  8. Loads groups, group messages
  9. Loads evaluations
  10. Recalculates all conductor averages
- **Called From:** Application startup
- **Error Handling:** Continues on errors, logs issues per row

##### `saveAllData(Gestion_covoiturage)`
- **Operation Type:** INSERT/UPDATE (WRITE - BULK)
- **Process:** Calls all individual save methods in sequence
- **Called From:** Application shutdown or "Save All" button

---

#### **UTILITY OPERATIONS**

##### `initializeDataFolder()`
- **Operation Type:** CREATE (directory)
- **Creates:** `data/` and `data/backups/` directories if missing
- **Called From:** Before every I/O operation

##### `createBackup()`
- **Operation Type:** CREATE (copy)
- **Backups:** conducteurs, passagers, trajets files
- **Format:** `conducteurs_20260515_143022.csv`
- **Rotation:** Keeps only 5 most recent backups per file type

##### `restoreFromBackup()` → boolean
- **Operation Type:** RESTORE
- **Process:**
  1. Finds most recent backup for each file
  2. Copies backup back to main location
  3. Overwrites current data
- **Use Case:** Recovery from corruption/accidental deletion

##### `exportToExcelCSV(List<Trajet>, String)`
- **Operation Type:** CREATE (export)
- **Format:** User-friendly French labels, Excel-compatible
- **Columns:** Point de Départ, Point d'Arrivée, Durée (min), Statut, Prix (TND), Conducteur, Passager
- **Use Case:** Export for reporting/analysis

---

### HELPER METHODS

##### `escapeCSV(String)` → String
- Wraps values containing delimiter/quotes in quotes
- Escapes internal quotes with double quotes
- Returns empty string for null

##### `unescapeCSV(String)` → String
- Removes surrounding quotes
- Unescapes double quotes to single
- Strips UTF-8 BOM if present
- Trims whitespace

##### `findConducteurByCIN(List<User>, String)` → Conducteur
- Linear search through users list
- Returns first match or null

##### `findPassagerByCIN(List<User>, String)` → Passager
- Linear search through users list
- Returns first match or null

---

## 🎯 BUSINESS LOGIC LAYER - Gestion_covoiturage.java

### Class Purpose
In-memory data management with business rule enforcement.

---

### DATA STRUCTURES (IN-MEMORY)

```java
List<User> users;                                        // All users (Conducteur, Passager, Admin)
List<Trajet> trajets;                                    // All rides
List<User> passagers_acceptes;                           // History of accepted passengers
Map<String, List<String>> demandes_par_conducteur;       // Pending requests per driver
Map<String, List<Notification>> notifications_par_passager;    // Passenger notifications
Map<String, List<Notification>> notifications_par_conducteur;  // Driver notifications
List<Notification> adminNotifications;                   // Admin notifications
List<Group> groups;                                      // Carpooling groups
List<GroupMessage> groupMessages;                        // Group chat messages
List<Conversation> conversations;                        // User-admin conversations
List<Reclamation> reclamations;                          // Complaints/disputes
List<Evaluation> evaluations;                            // Driver ratings
```

---

### PUBLIC METHODS - BUSINESS LOGIC

#### **USER QUERIES**

##### `rechercher_user(String cin)` → User
- **Operation Type:** SELECT (search)
- **Validation:** CIN case-insensitive comparison
- **Returns:** First user matching CIN or null

##### `rechercher_conducteur(String cin)` → Conducteur
- **Operation Type:** SELECT (search)
- **Validation:** Casts to Conducteur type
- **Returns:** Conductor or null

##### `rechercher_passager(String cin)` → Passager
- **Operation Type:** SELECT (search)
- **Validation:** Casts to Passager type
- **Returns:** Passenger or null

##### `rechercher_admin(String cin)` → Admin
- **Operation Type:** SELECT (search)
- **Returns:** Admin or null

##### `getDefaultAdmin()` → Admin
- **Operation Type:** SELECT
- **Returns:** First Admin in users list

##### `getAllUsers()` → List<User>
- **Operation Type:** SELECT (all)
- **Returns:** Copy of users list

##### `getAllTrajets()` → List<Trajet>
- **Operation Type:** SELECT (all)
- **Returns:** Copy of trajets list

##### `getAllEvaluations()` → List<Evaluation>
- **Operation Type:** SELECT (all)
- **Returns:** Copy of evaluations list

---

#### **RIDE REQUEST WORKFLOW**

##### `ajouter_demande_pour_trajet(Trajet, String cinPassager)` → boolean
- **Operation Type:** INSERT
- **Process:**
  1. Validates trajet and passenger CIN
  2. Finds Passager object by CIN
  3. Calls trajet.addDemand(passenger)
  4. Updates demandes_par_conducteur map
  5. Creates "DEMANDE" notification for driver
- **Notification Sent:** "Nouvelle demande de [PassagerName] pour trajet [From] → [To]"
- **Returns:** true if successful

##### `accepter_passager_pour_trajet(Trajet, String cinPassager)` → boolean
- **Operation Type:** UPDATE
- **Validations:**
  - Trajet not null
  - Passenger exists
  - Available places > 0
  - Passenger not already accepted
- **Process:**
  1. Calls trajet.acceptPassenger(passenger)
  2. Sets passenger.chercheCovoit = false (no longer searching)
  3. Decrements conductor.placesDisponibles
  4. Removes passenger from demandes_par_conducteur
  5. Adds to passagers_acceptes history
  6. Sets trajet.statut = IN_PROGRESS (if first accepted)
  7. Creates "ACCEPTATION" notification for passenger
- **Notification Sent:** "Accepté par [ConductorName] pour trajet [From] → [To]"
- **Returns:** true if successful

##### `refuser_passager_pour_trajet(Trajet, String cinPassager)` → boolean
- **Operation Type:** UPDATE
- **Process:**
  1. Calls trajet.removeDemand(passenger)
  2. Updates demandes_par_conducteur map
  3. Sets passenger.chercheCovoit = true (back to searching)
  4. Creates "REFUS" notification for passenger
- **Notification Sent:** "Refusé par [ConductorName] pour trajet [From] → [To]"
- **Returns:** true if successful

##### `supprimer_passager_accepte(Trajet, String cinPassager, String cinConducteur)` → boolean
- **Operation Type:** DELETE
- **Validations:**
  - Conductor owns the trajet
  - Passenger is accepted
- **Process:**
  1. Calls trajet.removeAccepted(passenger)
  2. Increments conductor.placesDisponibles
  3. Removes from passagers_acceptes history
  4. Sets passenger.chercheCovoit = true
  5. Creates "SUPPRESSION" notification for passenger
- **Returns:** true if successful

---

#### **NOTIFICATION MANAGEMENT**

##### **Passenger Notifications**

###### `getDernieresNotifications(String cinPassager, int limite)` → List<Notification>
- **Operation Type:** SELECT (paginated)
- **Sorting:** Descending by date (newest first)
- **Returns:** Last N notifications

###### `getToutesNotifications(String cinPassager)` → List<Notification>
- **Operation Type:** SELECT (all)
- **Sorting:** Descending by date
- **Returns:** All notifications for passenger

###### `compterNotificationsNonLues(String cinPassager)` → int
- **Operation Type:** COUNT
- **Filtering:** estLue == false

###### `marquerCommelue(String cinPassager, String notificationId)`
- **Operation Type:** UPDATE
- **Sets:** estLue = true for matching notification

###### `marquerToutesCommelues(String cinPassager)`
- **Operation Type:** UPDATE
- **Sets:** estLue = true for all notifications

##### **Driver Notifications**

###### `getDernieresNotificationsConducteur(String cinConducteur, int limite)` → List<Notification>
- **Operation Type:** SELECT (paginated, newest first)

###### `getToutesNotificationsConducteur(String cinConducteur)` → List<Notification>
- **Operation Type:** SELECT (all, sorted)

###### `compterNotificationsNonLuesConducteur(String cinConducteur)` → int
- **Operation Type:** COUNT

###### `marquerCommelueConducteur(String cinConducteur, String notificationId)`
- **Operation Type:** UPDATE

###### `marquerToutesCommelueConducteur(String cinConducteur)`
- **Operation Type:** UPDATE

##### **Admin Notifications**

###### `addAdminNotification(String message, String type)`
- **Operation Type:** INSERT
- **Creates:** New Notification with type (HELP, COMPLAINT, reclamation, etc.)
- **Auto-Saves:** Calls CSVDatabase.saveAdminNotifications()

###### `markAdminNotificationAsRead(String notificationId)`
- **Operation Type:** UPDATE

###### `markAllAdminNotificationsAsRead()`
- **Operation Type:** UPDATE (batch)

###### `countUnreadAdminNotifications()` → int
- **Operation Type:** COUNT

###### `deleteAdminNotification(String notificationId)`
- **Operation Type:** DELETE

###### `requestHelp(User user)`
- **Operation Type:** INSERT
- **Message:** "Aide demandée par [UserName] (CIN: [CIN])"

###### `submitComplaint(User user, String detail)`
- **Operation Type:** INSERT
- **Message:** "Réclamation de [UserName] : [detail]"

---

#### **GROUP MANAGEMENT**

##### `creerGroupe(String groupName, String conducteurCin, List<String> passagerCins)` → Group
- **Operation Type:** INSERT
- **Validations:**
  - groupName not empty
  - conducteurCin not empty
  - At least one passenger
- **Process:**
  1. Creates Group with ID = "GRP_" + timestamp + "_" + conducteurCin
  2. Adds to groups list
  3. Creates "GROUPE" notification for each member
- **Notification Sent:** "👥 Vous avez été ajouté au groupe « [GroupName] » par [ConductorName]"
- **Returns:** Created Group or null

##### `getGroupesPourUtilisateur(String cin)` → List<Group>
- **Operation Type:** SELECT (filtered)
- **Filtering:** Groups where user is conductor or in memberCins

##### `rechercher_groupe(String groupId)` → Group
- **Operation Type:** SELECT (search)

##### `getMessagesPourGroupe(String groupId)` → List<GroupMessage>
- **Operation Type:** SELECT (filtered, sorted by timestamp)

##### `envoyerMessageDeGroupe(String groupId, String senderCin, String senderName, String content)` → GroupMessage
- **Operation Type:** INSERT
- **Validations:**
  - Group exists
  - Content not empty
- **Process:**
  1. Creates GroupMessage with ID = "GMSG_" + timestamp + "_" + senderCin
  2. Adds to groupMessages list
  3. Notifies all members (except sender) with type "MESSAGE_GROUPE"
- **Notification Format:** "💬 [GroupName] [SenderName]: [contentPreview]..."
- **Returns:** Created GroupMessage or null

##### `supprimerMessageDeGroupe(String messageId, String requesterCin)` → boolean
- **Operation Type:** DELETE (soft)
- **Authorization:** Only sender can delete
- **Process:** Marks isDeleted = true, replaces content with "message supprimée"

##### `getGroups()` → List<Group>
- **Operation Type:** SELECT (all)

##### `getGroupMessages()` → List<GroupMessage>
- **Operation Type:** SELECT (all)

##### `ajouterGroupe(Group group)`
- **Operation Type:** INSERT (during CSV load)
- **Duplicates:** Skipped by ID check

##### `ajouterGroupMessage(GroupMessage message)`
- **Operation Type:** INSERT (during CSV load)

---

#### **CONVERSATION & COMPLAINT MANAGEMENT**

##### `getOrCreateAdminConversation(String userId, String adminId, String triggeredBy)` → Conversation
- **Operation Type:** UPSERT
- **Process:**
  1. Searches for existing conversation between user and admin
  2. If found: updates triggeredBy, saves, returns
  3. If not found: creates new Conversation, saves, returns
- **ID Format:** "CONV_" + timestamp + "_" + userId
- **Returns:** Conversation or null if invalid inputs

##### `submitUserReclamation(User complainant, User accused, Trajet trajet, String reservationId, String adminId, ReclamationPreset preset, String finalMessage)` → Reclamation
- **Operation Type:** INSERT
- **Validations:**
  - Users and trajet not null
  - finalMessage not empty
  - No duplicate complaint for same trio
- **Process:**
  1. Determines complainant/accused roles
  2. Creates Reclamation with ID = "RECL_" + timestamp + "_" + complainantCin
  3. Creates admin notification
  4. Creates/updates conversation with admin
  5. Saves to CSV
- **Notification Message:** "[ComplainantName] ([role]) contre [AccusedName] ([role]) — Réservation #[ID] — Motif: [message]"
- **Returns:** Created Reclamation or null

##### `hasReclamation(String reservationId, String complainantId, String accusedId)` → boolean
- **Operation Type:** SELECT (existence check)

##### `buildReservationId(Trajet trajet, String passagerCin)` → String
- **Format:** "conductorCIN_departure_arrival_passengerCIN"
- **Sanitization:** Spaces → hyphens, special chars removed

##### `ajouterConversation(Conversation conversation)`
- **Operation Type:** INSERT (during CSV load)

##### `ajouterReclamation(Reclamation reclamation)`
- **Operation Type:** INSERT (during CSV load)

##### `getConversations()` → List<Conversation>
- **Operation Type:** SELECT (all)

##### `getReclamations()` → List<Reclamation>
- **Operation Type:** SELECT (all)

---

#### **EVALUATION & RATING SYSTEM**

##### `creerEvaluation(String passagerCin, String conducteurCin, String trajetId, int rating, String comment)` → Evaluation
- **Operation Type:** INSERT
- **Validations:**
  - Both users exist
  - Rating in range 1-5
- **Process:**
  1. Creates Evaluation with ID = "EVAL_" + timestamp + "_" + passagerCin
  2. Adds to evaluations list
  3. Recalculates conductor's average rating
  4. Creates "EVALUATION" notification for driver
- **Notification Format:** "⭐ [PassagerName] vous a évalué ★★★☆☆ : [commentPreview]..."
- **Returns:** Created Evaluation or null

##### `recalculerMoyenneConducteur(String conducteurCin)`
- **Operation Type:** UPDATE
- **Process:**
  1. Finds all evaluations for conductor
  2. Calculates average (sum/count)
  3. Clamps to 0.0-5.0 range
  4. Updates conductor.moyenneEvaluation
- **Called By:** creerEvaluation, loadAllData

##### `recalculerToutesMoyennes()`
- **Operation Type:** UPDATE (batch)
- **Called By:** loadAllData (after loading evaluations)

##### `getEvaluationsPourConducteur(String conducteurCin)` → List<Evaluation>
- **Operation Type:** SELECT (filtered, sorted descending by date)

##### `getEvaluations()` → List<Evaluation>
- **Operation Type:** SELECT (all)

##### `ajouterEvaluation(Evaluation evaluation)`
- **Operation Type:** INSERT (during CSV load)

---

#### **UTILITY OPERATIONS**

##### `supprimerUtilisateur(String cin)` → boolean
- **Operation Type:** DELETE
- **Removes:** User from users list

##### `supprimerTrajet(Trajet t)` → boolean
- **Operation Type:** DELETE
- **Removes:** Trajet from trajets list

##### `setUsers(List<User> newUsers)`
- **Operation Type:** REPLACE (bulk)

##### `setTrajets(List<Trajet> newTrajets)`
- **Operation Type:** REPLACE (bulk)

##### `getUsers()` → List<User>
- **Operation Type:** SELECT (reference)

##### `getTrajets()` → List<Trajet>
- **Operation Type:** SELECT (reference)

##### `getPassagers_acceptes()` → List<User>
- **Operation Type:** SELECT (reference)

##### `getNotificationsParPassager()` → Map
- **Operation Type:** SELECT (reference)

##### `getNotificationsParConducteur()` → Map
- **Operation Type:** SELECT (reference)

---

## 📋 MODEL LAYER - Entity Classes

### **User.java** (Base Class)
**Fields:**
- `String cin` - National ID (8 digits)
- `String nom` - Last name
- `String prenom` - First name
- `String tel` - Phone (8 digits)
- `Year anneeUniversitaire` - Academic year
- `String adresse` - Address
- `String mail` - Email (gmail.com or *.tn)
- `String passwordHash` - SHA-256 hashed password

**Validation Rules:**
- CIN: `^[0-9]{8}$`
- Phone: `^[0-9]{8}$`
- Name: `^[a-zA-ZÀ-ÿ\s'-]+$`
- Email: `^[A-Z0-9._%+-]+@((gmail\.com)|([A-Z0-9.-]+\.tn))$`
- Password (strength): Min 8 chars, uppercase, lowercase, digit, special char

**Constructors:**
- Interactive (Scanner input)
- Parameterized (with validation)
- CSV Loading (pre-hashed password)

---

### **Conducteur.java** (extends User)
**Additional Fields:**
- `String nomVoiture` - Vehicle name (alphanumeric allowed)
- `String marqueVoiture` - Brand
- `String matricule` - License plate format: `[0-9]{1,3}TU[0-9]{4}`
- `int placesDisponibles` - Available seats (≥1)
- `String weeklySchedule` - Format: "MON:09:00-17:00|TUE:09:00-17:00|..."
- `double moyenneEvaluation` - Average rating (0-5)
- `String card` - Warning card (verte/jaune/rouge)
- `boolean banned` - Account ban status

**Methods:**
- `setPlacesDisponibles(int)` - Update available seats
- `setWeeklySchedule(String)` - Set recurring schedule
- `setMoyenneEvaluation(double)` - Update rating (clamped 0-5)
- `setCard(String)` - Set warning card
- `setBanned(boolean)` - Ban/unban driver
- `getCardDisplayLabel()` - Returns localized card description

---

### **Passager.java** (extends User)
**Additional Fields:**
- `boolean chercheCovoit` - Currently searching for ride
- `Conducteur conducteur` - Associated driver (legacy)
- `Vector<String> notifications` - Legacy notification list
- `String card` - Warning card (verte/jaune/rouge)
- `boolean banned` - Account ban status

**Methods:**
- `setChercheCovoit(boolean)` - Update search status
- `addNotification(String)` - Add notification (legacy)
- `getLastNotification()` → String - Pop last notification
- `hasNotifications()` → boolean - Check for unread
- `clearNotifications()` - Clear all (legacy)

---

### **Admin.java** (extends User)
**Additional Fields:**
- `LocalDateTime dateCreation` - Account creation date
- `String role` - Admin role (SUPER_ADMIN, MODERATOR, ADMIN)

**Special Notes:**
- Bypasses strict CIN/email validation (allows non-numeric CIN)
- Used for support/complaint handling

---

### **Trajet.java**
**Fields:**
- `String departTrajet` - Start location
- `String arriveeTrajet` - End location
- `Duration dureeTrajet` - Journey duration
- `float prix` - Price per person
- `String statusTrajet` - PENDING, PENDING_APPROVAL, IN_PROGRESS, FINISHED
- `Conducteur conducteur` - Driver reference
- `Vector<Passager> passagersAcceptes` - Accepted passengers
- `Vector<Passager> passagersDemandes` - Passengers requesting ride
- `int maxPlaces` - Max capacity (≥1)
- `LocalDateTime startDateTime` - Start date/time (ISO 8601)
- `LocalDateTime endDateTime` - End date/time (ISO 8601)
- `String weeklySchedule` - Recurring schedule (if applicable)
- `boolean trajet_valide` - Validation flag

**Status Values:**
- `PENDING` - No passengers
- `PENDING_APPROVAL` - Conductor assigned
- `IN_PROGRESS` - At least one passenger accepted
- `FINISHED` - Completed

**Methods:**
- `addDemand(Passager)` → boolean - Add passenger request
- `removeDemand(Passager)` → boolean - Reject passenger
- `acceptPassenger(Passager)` → boolean - Accept passenger
- `removeAccepted(Passager)` → boolean - Remove accepted passenger
- `getAvailablePlaces()` → int - Remaining capacity
- `getPassagersAcceptesCINs()` → String - Comma-separated accepted CINs
- `getPassagersDemandesCINs()` → String - Comma-separated pending CINs
- `setWeeklySchedule(String)` - Set recurring schedule

---

### **Notification.java**
**Fields:**
- `String notificationId` - Unique ID (NOTIF_timestamp_cin)
- `String passagerId` - Recipient passenger CIN
- `String conducteurId` - Actor/sender driver CIN
- `String trajetId` - Associated ride ID
- `String type` - ACCEPTATION, REFUS, SUPPRESSION, DEMANDE, ANNULATION, GROUPE, MESSAGE_GROUPE, EVALUATION, etc.
- `String message` - Display message
- `LocalDateTime dateCreation` - Timestamp
- `boolean estLue` - Read status

**Types & Colors:**
| Type | Label | Color |
|------|-------|-------|
| ACCEPTATION | ✓ Accepté | Green (#27AE60) |
| REFUS | ✗ Refusé | Red (#E74C3C) |
| SUPPRESSION | Suppression | Orange (#E67E22) |
| DEMANDE | Nouvelle demande | - |
| GROUPE | 👥 Groupe | Purple (#8E44AD) |
| MESSAGE_GROUPE | 💬 Message Groupe | Blue (#3498DB) |
| EVALUATION | ⭐ Évaluation | Gold (#F1C40F) |
| HELP | Aide | Blue (#3498DB) |
| COMPLAINT | Reclamation | Red (#E74C3C) |

---

### **Message.java** (Private Messages)
**Fields:**
- `String messageId` - Unique ID
- `String senderCin` - Sender CIN
- `String senderName` - Sender name
- `String recipientCin` - Recipient CIN
- `String recipientName` - Recipient name
- `String content` - Message text
- `LocalDateTime timestamp` - Sent time
- `boolean isDeleted` - Soft delete flag
- `String trajetId` - Associated ride (optional)

**Soft Delete:** When deleted, content becomes "Message supprimé"

---

### **GroupMessage.java**
**Fields:**
- `String messageId` - Unique ID
- `String groupId` - Associated group
- `String senderCin` - Sender CIN
- `String senderName` - Sender name
- `String content` - Message text
- `LocalDateTime timestamp` - Sent time
- `boolean isDeleted` - Soft delete flag

---

### **Group.java**
**Fields:**
- `String groupId` - Unique ID (GRP_timestamp_conductorCin)
- `String groupName` - Display name
- `String conducteurCin` - Group creator (driver)
- `List<String> memberCins` - Member passenger CINs
- `LocalDateTime dateCreation` - Created timestamp

**Methods:**
- `containsUser(String cin)` → boolean - Check membership
- `getMemberCinsAsString()` → String - Comma-separated members

---

### **Evaluation.java**
**Fields:**
- `String evaluationId` - Unique ID
- `String passagerCin` - Evaluator (passenger) CIN
- `String passagerName` - Evaluator name
- `String conducteurCin` - Evaluated driver CIN
- `String trajetId` - Associated ride
- `int rating` - 1-5 stars
- `String comment` - Optional review text
- `LocalDateTime dateCreation` - Evaluation timestamp

**Methods:**
- `getStarsDisplay()` → String - Star representation (★☆)

---

### **Conversation.java** (User-Admin)
**Fields:**
- `String id` - Unique ID
- `String userId` - User CIN
- `String adminId` - Admin CIN
- `String triggeredBy` - Reason (help, reclamation, etc.)
- `LocalDateTime createdAt` - Created timestamp

---

### **Reclamation.java** (Disputes)
**Fields:**
- `String id` - Unique ID (RECL_timestamp_complainantCin)
- `String reservationId` - Associated ride ID
- `String complainantId` - Complainer CIN
- `String complainantRole` - "conducteur" or "passager"
- `String accusedId` - Accused CIN
- `String accusedRole` - "conducteur" or "passager"
- `ReclamationPreset preset` - Predefined reason (if applicable)
- `String message` - Custom complaint text
- `LocalDateTime createdAt` - Submission timestamp

**ReclamationPreset Enum:**
| Against Passenger | Against Driver |
|-------------------|----------------|
| PASSAGER_ABSENT | CONDUCTEUR_ABSENT |
| ANNULATION_TARDIVE | ANNULATION_SANS_PREAVIS |
| COMPORTEMENT_IRRESPECTUEUX_PASSAGER | CONDUITE_DANGEREUSE |
| DEGATS_VEHICULE | VEHICULE_NON_CONFORME |
| RETARD_PASSAGER | COMPORTEMENT_IRRESPECTUEUX_CONDUCTEUR |

---

### **ValidationUtils.java**
**Static Regex Patterns:**
- `PHONE_PATTERN`: `^[0-9]{8}$`
- `CIN_PATTERN`: `^[0-9]{8}$`
- `NAME_PATTERN`: `^[a-zA-ZÀ-ÿ\s'-]+$`
- `VEHICLE_NAME_PATTERN`: `^[a-zA-Z0-9À-ÿ\s'-]+$` (alphanumeric)
- `MATRICULE_PATTERN`: `^[0-9]{1,3}TU[0-9]{4}$`
- `EMAIL_PATTERN`: `^[A-Z0-9._%+-]+@((gmail\.com)|([A-Z0-9.-]+\.tn))$`
- `PASSWORD_PATTERN`: Min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char

**Static Methods:**
- `validateCIN(String)` - Throws if invalid
- `validatePhone(String)` - Throws if invalid
- `validateName(String, String fieldName)` - Throws if invalid
- `validateEmail(String)` - Throws if invalid
- `validatePassword(String)` - Throws if weak
- `validateMatricule(String)` - Throws if invalid
- `validateVehicleName(String, String fieldName)` - Throws if invalid
- `hashPassword(String)` → String - SHA-256 hash
- `isValidEmail(String)` → boolean - Regex check only

---

## 🔄 DATABASE OPERATION PATTERNS

### CRUD MATRIX

| Entity | Create | Read | Update | Delete | Search |
|--------|--------|------|--------|--------|--------|
| Conducteur | saveConducteurs | loadConducteurs | saveConducteurs | - | rechercher_conducteur |
| Passager | savePassagers | loadPassagers | savePassagers | supprimerUtilisateur | rechercher_passager |
| Admin | saveAdmins | loadAdmins | saveAdmins | - | rechercher_admin |
| Trajet | saveTrajets | loadTrajets | saveTrajets | supprimerTrajet | - |
| Notification | ajouterNotification | getDernieresNotifications | marquerCommelue | deleteAdminNotification | - |
| Message | saveMessages | loadMessages | - | supprimerMessageDeGroupe | - |
| Group | saveGroups | loadGroups | - | - | rechercher_groupe |
| GroupMessage | saveGroupMessages | loadGroupMessages | - | supprimerMessageDeGroupe | getMessagesPourGroupe |
| Conversation | saveConversations | loadConversations | getOrCreateAdminConversation | - | - |
| Reclamation | saveReclamations | loadReclamations | - | - | hasReclamation |
| Evaluation | saveEvaluations | loadEvaluations | recalculerMoyenneConducteur | - | getEvaluationsPourConducteur |

### TRANSACTION-LIKE OPERATIONS

**Request Acceptance (Multi-Step):**
1. accepter_passager_pour_trajet() calls trajet.acceptPassenger()
2. Updates conductor's placesDisponibles
3. Sets passenger.chercheCovoit = false
4. Removes from demandes_par_conducteur
5. Updates trajet status to IN_PROGRESS
6. Creates ACCEPTATION notification
7. No explicit save() - relies on UI calling saveAllData()

**Reclamation Submission (Multi-Step):**
1. submitUserReclamation() validates all inputs
2. Creates Reclamation object
3. Calls addAdminNotification() (saves to CSV)
4. Calls getOrCreateAdminConversation() (saves to CSV)
5. Calls CSVDatabase.saveReclamations()
6. Calls CSVDatabase.saveConversations()

**Evaluation Creation (Multi-Step):**
1. creerEvaluation() creates Evaluation
2. Calls recalculerMoyenneConducteur() (updates conductor.moyenneEvaluation)
3. Creates EVALUATION notification for driver
4. No explicit save - relies on UI calling saveAllData()

### BATCH OPERATIONS

**Load All Data (Startup):**
- CSVDatabase.loadAllData(gestion)
- Loads 13 CSV files in dependency order
- Resolves object references by CIN
- Recalculates conductor averages

**Save All Data (Shutdown):**
- CSVDatabase.saveAllData(gestion)
- Saves all 13 CSV files
- Creates backup before overwriting

### SEARCH/FILTER OPERATIONS

| Operation | Method | Index | Performance |
|-----------|--------|-------|-------------|
| Find user by CIN | rechercher_user | Linear | O(n) |
| Find conductor by CIN | rechercher_conducteur | Linear | O(n) |
| Find group by ID | rechercher_groupe | Linear | O(n) |
| Get passenger notifications | getToutesNotifications | Map | O(1) lookup, O(n) within |
| Get group messages | getMessagesPourGroupe | Linear | O(n) |
| Get evaluations for conductor | getEvaluationsPourConducteur | Linear | O(n) |
| Check if reclamation exists | hasReclamation | Linear | O(n) |

### ERROR HANDLING PATTERNS

**CSV Parsing (Gestion_covoiturage.java):**
- Try-catch per row
- Logs error with row number
- Continues processing remaining rows
- Returns partially loaded data

**File Operations (CSVDatabase.java):**
- Checks file existence before reading
- Returns empty list/map if file missing
- Catches IOException globally
- Logs system errors

**Validation (Model constructors):**
- Throws IllegalArgumentException on invalid data
- Used during CSV loading (skips invalid rows)
- Used during user input (interactive constructors)

**Backup System:**
- Auto-backup before saving
- Timestamp format: yyyyMMdd_HHmmss
- Keeps 5 most recent per file
- Restores with restoreFromBackup()

---

## 🎯 GUI-DATABASE MAPPING

| GUI Action | Service Method | CSV Operation | File |
|-----------|-----------------|----------------|------|
| Register driver | saveConducteurs | INSERT | conducteurs.csv |
| Register passenger | savePassagers | INSERT | passagers.csv |
| Request ride | ajouter_demande_pour_trajet | INSERT demande | trajets.csv |
| Accept passenger | accepter_passager_pour_trajet | UPDATE trajet status | trajets.csv |
| Reject passenger | refuser_passager_pour_trajet | DELETE demande | trajets.csv |
| Remove passenger | supprimer_passager_accepte | UPDATE trajet | trajets.csv |
| Send message | saveMessages | INSERT | messages.csv |
| Create group | creerGroupe | INSERT | groups.csv |
| Send group message | envoyerMessageDeGroupe | INSERT | group_messages.csv |
| Request help | requestHelp | INSERT notification | notifications_admin.csv |
| File complaint | submitComplaint | INSERT notification | notifications_admin.csv |
| Submit reclamation | submitUserReclamation | INSERT reclamation | reclamations.csv |
| Evaluate driver | creerEvaluation | INSERT evaluation | evaluations.csv |
| Mark notification read | marquerCommelue | UPDATE notification | notifications.csv |
| Create weekly schedule | updateConductorWeeklySchedule | UPDATE conducteur | conducteurs.csv |

---

## ⚠️ CRITICAL BUSINESS RULES

1. **Password Security:** Never stored in plain text, always hashed with SHA-256
2. **CIN Uniqueness:** Should be enforced but currently not (no database constraints)
3. **Available Places:** Cannot go below 0 (clamped by methods)
4. **Rating Range:** Clamped to 1.0-5.0
5. **Soft Delete:** Messages and group messages marked isDeleted, not physically removed
6. **Notification Read Status:** Not persisted to CSV after marking read (in-memory only)
7. **Passenger Status:** chercheCovoit updated atomically with acceptance/rejection
8. **Trajet Status:** IN_PROGRESS only set when first passenger accepted
9. **Backup Rotation:** Old backups auto-deleted, keeping only 5 newest
10. **Weekly Schedule:** Optional, format validated at save time

---

## 📝 COLUMN DEFINITIONS

### conducteurs.csv
```
CIN;Nom;Prenom;Tel;AnneeUniv;Adresse;Mail;PasswordHash;
NomVoiture;MarqueVoiture;Matricule;PlacesDisponibles;
WeeklySchedule;MoyenneEvaluation;Carte;Banned
```

### passagers.csv
```
CIN;Nom;Prenom;Tel;AnneeUniv;Adresse;Mail;PasswordHash;
ChercheCovoit;Carte;Banned
```

### trajets.csv
```
Depart;Arrivee;DureeMinutes;Status;Prix;ConducteurCIN;PassagerCIN;
MaxPlaces;AcceptedCINs;PendingCINs;StartDateTime;EndDateTime;WeeklySchedule
```

### notifications.csv
```
notificationId;passagerId;conducteurId;trajetId;type;message;dateCreation;estLue
```

### messages.csv
```
messageId;senderCin;senderName;recipientCin;recipientName;
content;timestamp;isDeleted;trajetId
```

### groups.csv
```
groupId;groupName;conducteurCin;memberCins;dateCreation
```

### group_messages.csv
```
messageId;groupId;senderCin;senderName;content;timestamp;isDeleted
```

### evaluations.csv
```
evaluationId;passagerCin;passagerName;conducteurCin;trajetId;
rating;comment;dateCreation
```

### conversations.csv
```
id;user_id;admin_id;triggered_by;created_at
```

### reclamations.csv
```
id;reservation_id;complainant_id;complainant_role;accused_id;
accused_role;preset;message;created_at
```

---

## 🚀 PERFORMANCE CONSIDERATIONS

### Linear Searches (O(n))
- User lookups by CIN require full users list scan
- No indexing implemented
- **Optimization:** Could use HashMap<CIN, User>

### In-Memory State
- All data loaded into RAM at startup
- No connection pooling (single-file system)
- No lazy loading
- **Optimization:** Consider streaming for large datasets

### CSV I/O
- Full file rewrite on each save (no delta updates)
- Backup system creates copies (storage intensive)
- No transaction rollback
- **Optimization:** Could implement append-only logs

### Notification Management
- Stored in HashMap<CIN, List<Notification>>
- Read status not persisted (lost on restart)
- **Optimization:** Save read status to CSV if needed

---

## 🔐 DATA INTEGRITY

### What's Protected:
✅ Passwords (hashed)
✅ CIN validation (regex)
✅ Email validation (domain-specific)
✅ Monetary values (float precision)
✅ Date ranges (startDateTime < endDateTime)

### What's NOT Protected:
❌ CIN uniqueness (no constraints)
❌ Email uniqueness (could have duplicates)
❌ Trajet capacity overflow (relies on business logic)
❌ Notification orphans (if user deleted, notifications remain)
❌ Circular references (user A evaluates B, B evaluates A possible)
❌ Concurrency (no locking mechanism)

