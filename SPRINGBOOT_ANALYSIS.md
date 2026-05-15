# Spring Boot Architecture Analysis - Plateforme de Covoiturage

**Date**: May 15, 2026  
**Analysis**: Complete Spring Boot layers with comparison to Swing implementation

---

## 1. ENTITY LAYER (JPA Entities)

### Overview
13 entity classes mapped to database tables using Jakarta Persistence and Lombok annotations.

### Entity Classes

#### **Admin.java**
- **Table**: `admins`
- **Primary Key**: `cin` (String)
- **Fields**:
  - `cin`: String (PK)
  - `nom`, `prenom`: String (name)
  - `tel`: String (phone)
  - `annee_univ`: Integer (university year)
  - `adresse`: String (TEXT) (address)
  - `mail`: String (email)
  - `passwordHash`: String (hashed password)
  - `role`: String
  - `dateCreation`: LocalDateTime
- **Annotations**: `@Entity`, `@Table`, `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`

#### **Conducteur.java** (Driver)
- **Table**: `conducteurs`
- **Primary Key**: `cin` (String)
- **Fields**:
  - `cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `passwordHash`: Standard user fields
  - `nomVoiture`: String (car name)
  - `marqueVoiture`: String (car brand)
  - `matricule`: String (license plate)
  - `placesDisponibles`: Integer (available seats)
  - `weeklySchedule`: String (TEXT) (weekly recurrence schedule)
  - `moyenneEvaluation`: Double (average rating)
  - `carte`: String (card type: green/yellow)
  - `banned`: Boolean (ban status)

#### **Passager.java** (Passenger)
- **Table**: `passagers`
- **Primary Key**: `cin` (String)
- **Fields**:
  - `cin`, `nom`, `prenom`, `tel`, `annee_univ`, `adresse`, `mail`, `passwordHash`: Standard user fields
  - `chercheCovoit`: Boolean (searching for carpooling)
  - `carte`: String (card type)
  - `banned`: Boolean (ban status)

#### **Trajet.java** (Trip)
- **Table**: `trajets`
- **Primary Key**: `id` (Long, AUTO_INCREMENT)
- **Fields**:
  - `id`: Long (PK)
  - `depart`: String (departure location)
  - `arrivee`: String (arrival location)
  - `dureeMinutes`: Integer (duration in minutes)
  - `status`: String (PENDING, PENDING_APPROVAL, IN_PROGRESS, FINISHED)
  - `prix`: Double (price)
  - `conducteurCin`: String (driver CIN)
  - `passagerCin`: String (passenger CIN)
  - `maxPlaces`: Integer (max seats)
  - `acceptedCins`: String (CSV of accepted passengers)
  - `pendingCins`: String (CSV of pending requests)
  - `startDateTime`: LocalDateTime
  - `endDateTime`: LocalDateTime
  - `weeklySchedule`: String (TEXT) (weekly recurrence)
- **Helper Methods**:
  - `getAcceptedCount()`: Count of accepted passengers
  - `getAvailablePlaces()`: Calculate remaining seats
  - `hasAccepted(String cin)`: Check if passenger accepted
  - `hasPending(String cin)`: Check if pending
  - `containsCin()`, `addCin()`, `removeCin()`: CSV parsing utilities

#### **Evaluation.java** (Rating)
- **Table**: `evaluations`
- **Primary Key**: `evaluationId` (String)
- **Fields**:
  - `evaluationId`: String (PK)
  - `passagerCin`: String (passenger)
  - `passagerName`: String (passenger name)
  - `conducteurCin`: String (driver)
  - `trajetId`: String (trip reference)
  - `rating`: Integer (1-5 stars)
  - `comment`: String (TEXT)
  - `dateCreation`: LocalDateTime

#### **Message.java** (Private Message)
- **Table**: `messages`
- **Primary Key**: `messageId` (String)
- **Fields**:
  - `messageId`: String (PK)
  - `senderCin`, `senderName`: String (sender info)
  - `recipientCin`, `recipientName`: String (recipient info)
  - `content`: String (TEXT)
  - `timestamp`: LocalDateTime
  - `isDeleted`: Boolean
  - `trajetId`: String (related trip)

#### **Notification.java** (Passenger Notification)
- **Table**: `notifications`
- **Primary Key**: `notificationId` (String)
- **Fields**:
  - `notificationId`: String (PK)
  - `passagerId`: String (passenger)
  - `conducteurId`: String (driver)
  - `trajetId`: String (trip)
  - `type`: String (ACCEPTATION, REFUS, SUPPRESSION, MESSAGE, etc.)
  - `message`: String (TEXT)
  - `dateCreation`: LocalDateTime
  - `estLue`: Boolean (read status)

#### **AdminNotification.java** (Admin Notification)
- **Table**: `notifications_admin`
- **Primary Key**: `notificationId` (String)
- **Fields**: Same structure as Notification

#### **ConducteurNotification.java** (Driver Notification)
- **Table**: `conducteur_notifications`
- **Primary Key**: `notificationId` (String)
- **Fields**: Similar to Notification but for drivers

#### **Reclamation.java** (Complaint)
- **Table**: `reclamations`
- **Primary Key**: `id` (String)
- **Fields**:
  - `id`: String (PK)
  - `reservationId`: String (trip reference)
  - `complainantId`, `complainantRole`: String (who complains)
  - `accusedId`, `accusedRole`: String (who is accused)
  - `preset`: String (preset complaint reason)
  - `message`: String (TEXT) (detailed message)
  - `createdAt`: LocalDateTime

#### **Conversation.java** (Admin Chat)
- **Table**: `conversations`
- **Primary Key**: `id` (String)
- **Fields**:
  - `id`: String (PK)
  - `userId`: String (user CIN)
  - `adminId`: String (admin CIN)
  - `triggeredBy`: String (reason: "admin", "help", "reclamation", etc.)
  - `createdAt`: LocalDateTime

#### **Group.java** (Discussion Group)
- **Table**: `groups`
- **Primary Key**: `groupId` (String)
- **Fields**:
  - `groupId`: String (PK)
  - `groupName`: String (group name)
  - `conducteurCin`: String (group creator)
  - `memberCins`: String (TEXT, CSV of member CINs)
  - `dateCreation`: LocalDateTime

#### **GroupMessage.java** (Group Chat Message)
- **Table**: `group_messages`
- **Primary Key**: `messageId` (String)
- **Fields**:
  - `messageId`: String (PK)
  - `groupId`: String (group reference)
  - `senderCin`, `senderName`: String (sender)
  - `content`: String (TEXT)
  - `timestamp`: LocalDateTime
  - `isDeleted`: Boolean

---

## 2. REPOSITORY LAYER (Data Access)

### Overview
13 Spring Data JPA repositories extending `JpaRepository<Entity, PK>`.

### Repositories

#### **AdminRepository**
```java
interface AdminRepository extends JpaRepository<Admin, String>
```
- **Methods**: Standard CRUD only

#### **ConducteurRepository**
```java
interface ConducteurRepository extends JpaRepository<Conducteur, String>
- boolean existsByMail(String mail)
```
- Check for duplicate email during registration

#### **PassagerRepository**
```java
interface PassagerRepository extends JpaRepository<Passager, String>
- boolean existsByMail(String mail)
```
- Check for duplicate email during registration

#### **TrajetRepository**
```java
interface TrajetRepository extends JpaRepository<Trajet, Long>
- List<Trajet> findByConducteurCin(String conducteurCin)
- List<Trajet> findByDepartContainingIgnoreCaseAndArriveeContainingIgnoreCase(String depart, String arrivee)
```
- Find trips by driver
- Search trips by departure and arrival (case-insensitive)

#### **EvaluationRepository**
```java
interface EvaluationRepository extends JpaRepository<Evaluation, String>
- List<Evaluation> findByConducteurCinOrderByDateCreationDesc(String conducteurCin)
```
- Get evaluations for a driver, sorted by date

#### **MessageRepository**
```java
interface MessageRepository extends JpaRepository<Message, String>
- List<Message> findBySenderCinAndRecipientCinOrRecipientCinAndSenderCinOrderByTimestampAsc(String a, String b, String c, String d)
```
- Get conversation between two users (bidirectional)

#### **NotificationRepository**
```java
interface NotificationRepository extends JpaRepository<Notification, String>
- List<Notification> findByPassagerIdOrderByDateCreationDesc(String passagerId)
- long countByPassagerIdAndEstLueFalse(String passagerId)
```
- Get notifications sorted by date
- Count unread notifications

#### **GroupRepository**
```java
interface GroupRepository extends JpaRepository<Group, String>
- List<Group> findByConducteurCin(String conducteurCin)
- List<Group> findByMemberCinsContaining(String cin)
```
- Get groups by creator
- Find groups containing a member

#### **GroupMessageRepository**
```java
interface GroupMessageRepository extends JpaRepository<GroupMessage, String>
- List<GroupMessage> findByGroupIdOrderByTimestampAsc(String groupId)
```
- Get messages in a group, sorted by timestamp

#### **ReclamationRepository**
```java
interface ReclamationRepository extends JpaRepository<Reclamation, String>
- List<Reclamation> findAllByOrderByCreatedAtDesc()
- boolean existsByReservationIdAndComplainantIdAndAccusedId(String reservationId, String complainantId, String accusedId)
```
- Get all complaints sorted by date
- Check for duplicate complaints

#### **ConversationRepository**
```java
interface ConversationRepository extends JpaRepository<Conversation, String>
- List<Conversation> findAllByOrderByCreatedAtDesc()
- Optional<Conversation> findByUserIdAndAdminId(String userId, String adminId)
```
- Get all conversations
- Find conversation between user and admin

#### **AdminNotificationRepository**
```java
interface AdminNotificationRepository extends JpaRepository<AdminNotification, String>
```
- Standard CRUD

#### **ConducteurNotificationRepository**
```java
interface ConducteurNotificationRepository extends JpaRepository<ConducteurNotification, String>
```
- Standard CRUD

---

## 3. SERVICE LAYER

### Overview
8 service interfaces + implementations handling business logic.

#### **AuthService**
**Interface Methods**:
```java
Optional<Object> authenticate(LoginRequest request)
Conducteur registerConducteur(UserRegistrationForm f)
Passager registerPassager(UserRegistrationForm f)
String hashPassword(String p)
boolean verifyPassword(String p, String h)
```
- **Purpose**: Authentication and registration
- **Repositories Used**: ConducteurRepository, PassagerRepository, AdminRepository
- **Notes**: Returns polymorphic Object (Conducteur, Passager, or Admin)

#### **TrajetService**
**Interface Methods**:
```java
List<Trajet> getAllTrajets()
List<Trajet> search(String depart, String arrivee)
List<Trajet> getTrajetsConducteur(String cin)
Trajet createTrajet(String cin, TrajetForm f)
Trajet demanderReservation(Long id, String passagerCin)
Trajet accepterPassager(Long id, String passagerCin)
Trajet refuserPassager(Long id, String passagerCin)
Trajet annulerReservation(Long id, String passagerCin)
Trajet supprimerPassagerAccepte(Long id, String passagerCin)
Trajet modifierPrix(Long id, double prix)
Trajet finishTrajet(Long id)
void deleteTrajet(Long id)
```
- **Purpose**: Complete trip management
- **Repositories Used**: TrajetRepository, ConducteurRepository, PassagerRepository, NotificationRepository

#### **UserService**
**Interface Methods**:
```java
List<Conducteur> conducteurs()
List<Passager> passagers()
List<Admin> admins()
Optional<Conducteur> conducteur(String cin)
Optional<Passager> passager(String cin)
Optional<Admin> admin(String cin)
void deleteUser(String role, String cin)
void updateCard(String role, String cin, String carte)
```
- **Purpose**: User CRUD operations
- **Repositories Used**: ConducteurRepository, PassagerRepository, AdminRepository

#### **EvaluationService**
**Interface Methods**:
```java
List<Evaluation> all()
List<Evaluation> forConducteur(String cin)
Evaluation evaluate(String passagerCin, String passagerName, String conducteurCin, String trajetId, int rating, String comment)
double moyenne(String cin)
```
- **Purpose**: Rating system
- **Repositories Used**: EvaluationRepository, ConducteurRepository

#### **NotificationService**
**Interface Methods**:
```java
void notifyPassager(String p, String c, String t, String type, String msg)
void notifyConducteur(String c, String p, String t, String type, String msg)
void notifyAdmin(String type, String msg, String p, String c, String t)
List<Notification> passagerNotifications(String cin)
List<ConducteurNotification> conducteurNotifications(String cin)
List<AdminNotification> adminNotifications()
long unreadPassager(String cin)
long unreadConducteur(String cin)
long unreadAdmin()
```
- **Purpose**: Notification management for all roles
- **Repositories Used**: NotificationRepository, ConducteurNotificationRepository, AdminNotificationRepository

#### **MessagingService**
**Interface Methods**:
```java
List<Message> conversation(String userCin, String otherCin)
Message send(String senderCin, String senderName, String recipientCin, String recipientName, String content, String trajetId)
void deleteMessage(String messageId, String requesterCin, boolean requesterIsAdmin)
List<Conversation> adminConversations()
Conversation getOrCreateAdminConversation(String userCin, String adminCin, String triggeredBy)
```
- **Purpose**: Private messaging and admin conversations
- **Repositories Used**: MessageRepository, ConversationRepository, UserService

#### **GroupService**
**Interface Methods**:
```java
List<Group> groupsFor(String role, String cin)
Group create(String conducteurCin, String groupName, List<String> memberCins)
Group group(String groupId)
List<GroupMessage> messages(String groupId)
GroupMessage sendMessage(String groupId, String senderCin, String senderName, String content)
void deleteMessage(String messageId, String requesterCin)
```
- **Purpose**: Group discussion management
- **Repositories Used**: GroupRepository, GroupMessageRepository, NotificationRepository

#### **ReclamationService**
**Interface Methods**:
```java
List<Reclamation> all()
Reclamation submit(String reservationId, String complainantId, String complainantRole, String accusedId, String accusedRole, String preset, String message)
```
- **Purpose**: Complaint submission and management
- **Repositories Used**: ReclamationRepository, NotificationRepository

---

## 4. CONTROLLER LAYER (REST/Web MVC)

### Overview
6 controllers handling HTTP requests and MVC views (Spring MVC with Thymeleaf templates).

#### **AuthController**
- **Route**: `/`
- **Methods**:
  - `GET /` → `login` (LoginRequest form)
  - `POST /login` → Authenticate and set session
  - `GET /register/{role}` → `register` (UserRegistrationForm)
  - `POST /register` → Create account
  - `POST /logout` → Invalidate session

#### **ConducteurController**
- **Route**: `/conducteur`
- **Security**: Requires `conducteur` role
- **Methods**:
  - `GET /dashboard` → Dashboard with trips and stats
  - `GET /trajets` → List driver's trips
  - `GET /trajets/new` → New trip form
  - `POST /trajets` → Create trip
  - `POST /trajets/{id}/finish` → Mark trip finished
  - `POST /trajets/{id}/price` → Update price
  - `POST /trajets/{id}/passagers/remove` → Remove passenger
  - `POST /trajets/{id}/delete` → Delete trip
  - `GET /demandes` → View pending requests
  - `GET /passagers` → View accepted passengers
  - `POST /demandes/{id}/accept` → Accept passenger
  - `POST /demandes/{id}/refuse` → Reject passenger
  - `POST /reclamations` → Submit complaint
  - `GET /notifications` → View notifications

#### **PassagerController**
- **Route**: `/passager`
- **Security**: Requires `passager` role
- **Methods**:
  - `GET /dashboard` → Dashboard with available trips
  - `GET /trajets` → Search trips (params: depart, arrivee)
  - `POST /trajets/{id}/reserve` → Request reservation
  - `GET /reservations` → View booked trips
  - `POST /reservations/{id}/cancel` → Cancel reservation
  - `POST /reclamations` → Submit complaint
  - `GET /notifications` → View notifications
  - `POST /evaluate` → Rate driver (params: conducteurCin, trajetId, rating, comment)

#### **AdminController**
- **Route**: `/admin`
- **Security**: Requires `admin` role
- **Methods**:
  - `GET /dashboard` → Statistics (users, trips, evaluations)
  - `GET /users` → List all users
  - `POST /users/{role}/{cin}/delete` → Delete user
  - `POST /users/{role}/{cin}/card` → Update card type
  - `GET /trajets` → List all trips
  - `POST /trajets/{id}/delete` → Delete trip
  - `GET /evaluations` → List all evaluations
  - `GET /reclamations` → List all complaints
  - `GET /notifications` → View admin notifications

#### **MessagingController**
- **Route**: `/messages`
- **Security**: Session required
- **Methods**:
  - `GET /?with={cin}&trajetId={id}` → Conversation view
  - `POST /admin/start` → Admin starts conversation
  - `POST /?recipientCin={cin}` → Send message
  - `POST /{id}/delete` → Delete message

#### **GroupsController**
- **Route**: `/groups`
- **Security**: Session required
- **Methods**:
  - `GET /` → List user's groups
  - `POST /` → Create group (conducteur only)
  - `GET /{id}` → View group chat
  - `POST /{id}/messages` → Send group message
  - `POST /{groupId}/messages/{messageId}/delete` → Delete message

---

## 5. DTO LAYER (Data Transfer Objects)

#### **LoginRequest**
```java
- cin: String (validated: @NotBlank)
- password: String (validated: @NotBlank)
- role: String (validated: @NotBlank)
```

#### **TrajetForm**
```java
- depart: String (@NotBlank)
- arrivee: String (@NotBlank)
- dureeMinutes: Integer (@Min(1), default: 30)
- prix: Double (@Positive, default: 0.0)
- maxPlaces: Integer (@Min(1), default: 1)
- weeklySchedule: String (default: "")
```

#### **UserRegistrationForm**
```java
- cin: String (@Pattern("^[0-9]{8}$"))
- nom: String (@NotBlank)
- prenom: String (@NotBlank)
- tel: String (@Pattern("^[0-9]{8}$"))
- anneeUniv: Integer (@Min(2000))
- adresse: String
- mail: String (@Email)
- password: String (@Size(min=4))
- role: String
- nomVoiture: String (driver only)
- marqueVoiture: String (driver only)
- matricule: String (driver only)
- placesDisponibles: Integer (default: 1)
- carte: String (default: "verte")
```

---

## 6. SECURITY & CONFIG

#### **SessionUser.java** (Security Utility)
```java
- login(HttpSession, cin, role, name) → Store session
- isLogged(HttpSession) → Check if authenticated
- hasRole(HttpSession, role) → Check role
- cin(HttpSession) → Get user CIN
- role(HttpSession) → Get user role
```
- **Purpose**: Stateless session management using HttpSession
- **Roles**: "admin", "conducteur", "passager"

#### **WebConfig.java**
```java
@Configuration
public class WebConfig implements WebMvcConfigurer
```
- **Purpose**: Basic MVC configuration (currently empty)
- **Potential**: Could add interceptors, formatters, CORS config

#### **application.properties** (Database Config)
- Located at: `src/main/resources/application.properties`
- Configured for MySQL/MariaDB persistence

---

## 7. COMPARISON TABLE: SWING vs SPRING BOOT

| Feature | Swing Service Method | Spring Boot Endpoint | Status | Notes |
|---------|---------------------|----------------------|--------|-------|
| **User Management** | | | | |
| Register Conducteur | `Gestion.ajouterUtilisateur(Conducteur)` | `POST /register` + `POST /conducteur/dashboard` | ✅ IMPLEMENTED | Both register and redirect to dashboard |
| Register Passager | `Gestion.ajouterUtilisateur(Passager)` | `POST /register` + `POST /passager/dashboard` | ✅ IMPLEMENTED | Both register and redirect to dashboard |
| Login | `Gestion.rechercher_user()` + verify | `POST /login` | ✅ IMPLEMENTED | Session-based authentication |
| Logout | `Gestion.logout()` | `POST /logout` | ✅ IMPLEMENTED | Session invalidation |
| Get All Users | `Gestion.getAllUsers()` | `GET /admin/users` | ✅ IMPLEMENTED | Admin only |
| Get User by CIN | `Gestion.rechercher_user(cin)` | `UserService.conducteur/passager/admin(cin)` | ✅ IMPLEMENTED | Internal service method |
| Delete User | `Gestion.supprimerUtilisateur(cin)` | `POST /admin/users/{role}/{cin}/delete` | ✅ IMPLEMENTED | Admin only |
| Update Card | N/A | `POST /admin/users/{role}/{cin}/card` | ✅ IMPLEMENTED | New feature (Spring Boot only) |
| **Trip Management** | | | | |
| Create Trip | `Gestion.ajouterTrajet(Trajet)` | `POST /conducteur/trajets` | ✅ IMPLEMENTED | With TrajetForm DTO |
| Get All Trips | `Gestion.getAllTrajets()` | `GET /passager/dashboard` or `GET /admin/trajets` | ✅ IMPLEMENTED | Different views per role |
| Get Driver's Trips | `Gestion.getTrajetsConducteur(cin)` | `GET /conducteur/trajets` | ✅ IMPLEMENTED | |
| Search Trips | `Gestion.searchTrajets(depart, arrivee)` | `GET /passager/trajets?depart=X&arrivee=Y` | ✅ IMPLEMENTED | Query parameters |
| Delete Trip | `Gestion.supprimerTrajet(trajet)` | `POST /admin/trajets/{id}/delete` or `POST /conducteur/trajets/{id}/delete` | ✅ IMPLEMENTED | Both roles can delete |
| Finish Trip | N/A | `POST /conducteur/trajets/{id}/finish` | ✅ IMPLEMENTED | New feature (Spring Boot) |
| Update Trip Price | N/A | `POST /conducteur/trajets/{id}/price` | ✅ IMPLEMENTED | New feature (Spring Boot) |
| **Reservation Management** | | | | |
| Request Reservation | `Gestion.ajouter_demande_pour_trajet(trajet, cin)` | `POST /passager/trajets/{id}/reserve` | ✅ IMPLEMENTED | Triggers notification |
| Accept Passenger | `Gestion.accepter_passager_pour_trajet(trajet, cin)` | `POST /conducteur/demandes/{id}/accept` | ✅ IMPLEMENTED | Triggers notification |
| Reject Passenger | `Gestion.refuser_passager_pour_trajet(trajet, cin)` | `POST /conducteur/demandes/{id}/refuse` | ✅ IMPLEMENTED | Triggers notification |
| Cancel Reservation | N/A | `POST /passager/reservations/{id}/cancel` | ✅ IMPLEMENTED | Passenger-initiated cancellation |
| Remove Accepted Passenger | `Gestion.supprimer_passager_accepte(trajet, cin)` | `POST /conducteur/trajets/{id}/passagers/remove` | ✅ IMPLEMENTED | Triggers notification |
| View Pending Requests | N/A (implicit) | `GET /conducteur/demandes` | ✅ IMPLEMENTED | Filtered view |
| View Accepted Passengers | N/A (implicit) | `GET /conducteur/passagers` | ✅ IMPLEMENTED | Filtered view |
| View My Reservations | N/A (implicit) | `GET /passager/reservations` | ✅ IMPLEMENTED | Filtered view |
| **Notifications** | | | | |
| Create Notification | `Gestion.creerNotificationAcceptation/Refus/Suppression()` | `TrajetService.accepterPassager()` etc. (internal) | ✅ IMPLEMENTED | Auto-created in service |
| Get Passenger Notifications | `Gestion.getToutesNotifications(cin)` | `GET /passager/notifications` | ✅ IMPLEMENTED | |
| Get Driver Notifications | `Gestion.getToutesNotificationsConducteur(cin)` | `GET /conducteur/notifications` | ✅ IMPLEMENTED | |
| Get Admin Notifications | `Gestion.getAdminNotifications()` | `GET /admin/notifications` | ✅ IMPLEMENTED | |
| Count Unread (Passenger) | `Gestion.compterNotificationsNonLues(cin)` | `NotificationService.unreadPassager(cin)` | ✅ IMPLEMENTED | Dashboard display |
| Count Unread (Driver) | `Gestion.compterNotificationsNonLuesConducteur(cin)` | `NotificationService.unreadConducteur(cin)` | ✅ IMPLEMENTED | Dashboard display |
| Count Unread (Admin) | `Gestion.countUnreadAdminNotifications()` | `NotificationService.unreadAdmin()` | ✅ IMPLEMENTED | Dashboard display |
| Mark as Read | `Gestion.marquerCommelue(cin, notifId)` | N/A (view-only) | ⚠️ INCOMPLETE | Auto-marked in Swing, not exposed in Spring Boot |
| Mark All as Read | `Gestion.marquerToutesCommelues(cin)` | N/A (view-only) | ⚠️ INCOMPLETE | Not implemented in Spring Boot |
| **Evaluation & Rating** | | | | |
| Evaluate Driver | `Gestion.ajouterEvaluation(Evaluation)` | `POST /passager/evaluate` | ✅ IMPLEMENTED | After trip completion |
| Get Evaluations for Driver | `Gestion.getAllEvaluations()` + filter | `EvaluationService.forConducteur(cin)` | ✅ IMPLEMENTED | |
| Get Average Rating | `Gestion.calculerMoyenne(cin)` | `EvaluationService.moyenne(cin)` | ✅ IMPLEMENTED | Displayed in driver dashboard |
| View All Evaluations | N/A | `GET /admin/evaluations` | ✅ IMPLEMENTED | Admin dashboard |
| **Messaging** | | | | |
| Send Private Message | `Gestion.ajouterMessage(Message)` | `POST /messages` | ✅ IMPLEMENTED | With optional trajetId |
| Get Conversation | `Gestion.getConversation(user1, user2)` | `GET /messages?with={cin}` | ✅ IMPLEMENTED | Bidirectional |
| Delete Message | `Gestion.deleteMessage(messageId)` | `POST /messages/{id}/delete` | ✅ IMPLEMENTED | Sender or admin only |
| **Admin Conversations** | | | | |
| Create Admin Conversation | `Gestion.getOrCreateAdminConversation(userId, adminId, trigger)` | `POST /messages/admin/start` | ✅ IMPLEMENTED | Triggered by admin |
| Get Admin Conversations | `Gestion.getAdminConversations()` | `MessagingService.adminConversations()` | ✅ IMPLEMENTED | Internal method |
| Message Admin | User initiated | `POST /messages?recipientCin=admin` | ✅ IMPLEMENTED | Auto-creates conversation |
| **Groups** | | | | |
| Create Group | `Gestion.creerGroupe(name, driverCin, passengerCins)` | `POST /groups` | ✅ IMPLEMENTED | Driver only |
| Get User's Groups | `Gestion.getGroupesPourUtilisateur(cin)` | `GET /groups` | ✅ IMPLEMENTED | Shows all user's groups |
| Get Group Details | `Gestion.rechercher_groupe(groupId)` | `GET /groups/{id}` | ✅ IMPLEMENTED | With members list |
| Send Group Message | `Gestion.envoyerMessageDeGroupe(groupId, senderCin, content)` | `POST /groups/{id}/messages` | ✅ IMPLEMENTED | |
| Get Group Messages | `Gestion.getMessagesPourGroupe(groupId)` | `GroupService.messages(groupId)` | ✅ IMPLEMENTED | Auto-sorted by timestamp |
| Delete Group Message | `Gestion.deleteGroupMessage(messageId)` | `POST /groups/{groupId}/messages/{messageId}/delete` | ✅ IMPLEMENTED | Sender or admin |
| Group Notifications | `Gestion.notifierMembresGroupeMessage()` | `GroupService.sendMessage()` (internal) | ✅ IMPLEMENTED | Auto-sent to members |
| **Complaints & Help** | | | | |
| Submit Complaint | `Gestion.submitUserReclamation(complainant, accused, trajet, ...)` | `POST /conducteur/reclamations` or `POST /passager/reclamations` | ✅ IMPLEMENTED | With preset reason |
| Get All Complaints | `Gestion.getReclamations()` | `GET /admin/reclamations` | ✅ IMPLEMENTED | Admin view |
| Check Duplicate Complaint | `Gestion.hasReclamation(reservationId, complainantId, accusedId)` | `ReclamationService.submit()` (internal check) | ✅ IMPLEMENTED | Prevents duplicates |
| Request Help | `Gestion.requestHelp(user)` | N/A (via conversation) | ⚠️ DIFFERENT | Swing: help request → notification, Spring Boot: conversation with admin |
| **Weekly Schedule** | | | | |
| Store Weekly Schedule | `Trajet.weeklySchedule` (string) | `Trajet.weeklySchedule` (string) | ✅ IMPLEMENTED | Stored in database |
| Schedule Recurrence | `Gestion.getTrajetsRecurrents(conducteur)` | N/A (view scheduling in Thymeleaf) | ⚠️ INCOMPLETE | Not queried/filtered in Spring Boot |
| **Admin Stats** | | | | |
| Total Users Count | `Gestion.getAllUsers().size()` | `GET /admin/dashboard` (counts by role) | ✅ IMPLEMENTED | |
| Total Trips Count | `Gestion.getAllTrajets().size()` | `GET /admin/dashboard` | ✅ IMPLEMENTED | |
| Total Evaluations Count | `Gestion.getAllEvaluations().size()` | `GET /admin/dashboard` | ✅ IMPLEMENTED | |
| User Management UI | Separate panels | `GET /admin/users` | ✅ IMPLEMENTED | Web interface |
| Trip Management UI | Separate panels | `GET /admin/trajets` | ✅ IMPLEMENTED | Web interface |

---

## 8. SUMMARY OF IMPLEMENTATION STATUS

### ✅ FULLY IMPLEMENTED Features
1. User authentication (login/register)
2. Role-based access control (admin, conducteur, passager)
3. Trip creation, search, and deletion
4. Reservation requests, acceptance, rejection
5. Passenger removal from trips
6. Evaluations and driver ratings
7. Notifications (creation and retrieval)
8. Private messaging
9. Admin conversations
10. Group creation and group messaging
11. Complaint submission
12. Admin dashboard with statistics
13. User management (CRUD + card updates)

### ⚠️ INCOMPLETE Features
1. **Mark Notifications as Read**: Notifications can be counted as unread but cannot be marked as read in the API/UI
2. **Weekly Schedule Filtering**: schedules are stored but not used to filter or display recurrent trips
3. **Help Request System**: Different from Swing - handled via admin conversation instead of dedicated help mechanism
4. **Trip Status Transitions**: Basic status management exists but could be more comprehensive

### ❌ MISSING Features
None identified - all major Swing features are present in Spring Boot.

---

## 9. ARCHITECTURE DIFFERENCES

### Swing (Original)
- **Data**: CSV files in `/data/` with automatic backups
- **State Management**: In-memory ArrayList + HashMap collections
- **UI**: Swing panels (JPanel, JFrame) with GUI components
- **Session**: Application-wide static state

### Spring Boot (Migration)
- **Data**: MySQL/MariaDB database with JPA ORM
- **State Management**: Persistent database + service layer
- **UI**: Web-based Thymeleaf templates with Spring MVC
- **Session**: HTTP sessions per user

### Key Improvements
1. **Scalability**: Database instead of CSV files
2. **Concurrency**: Better handling of simultaneous users
3. **Persistence**: ACID compliance with database transactions
4. **Web-Ready**: REST-like endpoints and web forms
5. **Validation**: Input validation at DTO level

---

## 10. POTENTIAL ENHANCEMENTS

1. **Mark Notifications as Read API**: Add endpoint `PUT /notifications/{id}/read`
2. **Weekly Schedule Service**: Implement recurring trip queries
3. **REST API Conversion**: Convert MVC endpoints to REST with JSON responses
4. **Real-time Notifications**: WebSocket support for live updates
5. **Payment Integration**: Payment processing for trips
6. **Map Integration**: Geolocation and route mapping
7. **Driver Banning**: Implement ban/suspend functionality
8. **Rate Limiting**: Add rate limiting for API endpoints
9. **Audit Logging**: Track all user actions
10. **Email Notifications**: Send emails for important events

