# Swing GUI Components - Detailed Inventory

## Directory: `Plateforme-de-covoiturage-java/src/GUI/`

---

## 1. **AdminPanel.java**

**Class Declaration:**
- **Class Name:** `AdminPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `contentPanel` | `JPanel` | Container for card layout switching between views |
| `contentLayout` | `CardLayout` | Layout manager for switching between dashboard, users, trajets, evaluations, conversations |
| `usersStatCard` | `StatCard` | Display total users count |
| `trajetsStatCard` | `StatCard` | Display active rides count |
| `evaluationsStatCard` | `StatCard` | Display evaluations count |
| `driversTable` | `JTable` | Display list of drivers |
| `driversModel` | `DefaultTableModel` | Table model for drivers data |
| `passengersTable` | `JTable` | Display list of passengers |
| `passengersModel` | `DefaultTableModel` | Table model for passengers data |
| `trajetsTable` | `JTable` | Display list of trips |
| `trajetsModel` | `DefaultTableModel` | Table model for trips data |
| `adminNotificationBadge` | `JLabel` | Badge for notification count |
| `adminNotificationsPopup` | `JPopupMenu` | Popup menu for notifications |
| `adminNotificationsList` | `JPanel` | Container for admin notifications list |
| `conversationsListPanel` | `JPanel` | Container for conversations list |
| `adminChatHolder` | `JPanel` | Container for chat messages |
| `sidebarButtons` | `List<SidebarButton>` | Navigation buttons collection |

**Event Handlers Present:**
- `ActionListener` on sidebar buttons - switches between card layouts (DASHBOARD, USERS, TRAJETS, EVALUATIONS, CONVERSATIONS)
- `ActionListener` on logout button - shows confirmation dialog and calls `mainFrame.showLogin()`
- `Timer` (adminNotificationsTimer) - periodic refresh of admin notifications
- `Timer` (conversationsTimer) - periodic refresh of conversations

**Service/DAO Methods Called:**
- Service method references via `Models.*` and `Services.*` imports
- Refresh methods: `refreshDashboard()`, `refreshUsersTable()`, `refreshTrajetsTable()`, `refreshConversationsList()`
- Database operations through `Gestion_covoiturage` service

**Database Operations/Business Logic:**
- Manages user administration (drivers and passengers)
- Displays trip statistics and management
- Handles evaluations overview
- Manages admin-to-user conversations
- Auto-refresh of notifications and conversations via timers

---

## 2. **MainFrame.java**

**Class Declaration:**
- **Class Name:** `MainFrame`
- **Extends:** `JFrame`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `cardLayout` | `CardLayout` | Main layout for switching between login and user panels |
| `mainPanel` | `JPanel` | Main container with card layout |
| `loginPanel` | `EnhancedLoginPanel` | Login and registration interface |
| `driverPanel` | `EnhancedDriverPanel` | Driver dashboard |
| `passengerPanel` | `EnhancedPassengerPanel` | Passenger dashboard |
| `adminPanel` | `AdminPanel` | Admin dashboard |
| `notificationPanel` | `NotificationPanel` | Passenger notifications display |
| `driverNotificationPanel` | `DriverNotificationPanel` | Driver notifications display |
| `messagingPanel` | `JPanel` | Private messaging interface |
| `groupsPanel` | `GroupsPanel` | Group management |
| `groupChatPanel` | `GroupChatPanel` | Group chat interface |
| `autoSaveTimer` | `Timer` | Auto-save every 5 minutes |

**Event Handlers Present:**
- `WindowListener` (WindowAdapter) - handles window closing with save confirmation
- `Timer` (autoSaveTimer) - periodic auto-save every 5 minutes
- Shutdown hook for emergency save on unexpected termination

**Service/DAO Methods Called:**
- `CSVDatabase.loadAllData(gestion)` - load data from CSV files on startup
- `CSVDatabase.saveAllData(gestion)` - save data to CSV files
- `CSVDatabase.createBackup()` - create backup before saving
- `gestion.submitComplaint(user, detail)` - submit complaints
- `mainFrame.openAdminConversationForCurrentUser(...)` - open admin conversations

**Database Operations/Business Logic:**
- **CSV Persistence:** Loads all data from CSV on startup, saves periodically
- **Auto-save System:** 5-minute auto-save interval with backup creation
- **Data Management:** Comprehensive save/load system with user confirmation
- **Panel Navigation:** Manages card layout switching between login, driver, passenger, admin panels
- **User Session Management:** Tracks current user and user type

---

## 3. **EnhancedLoginPanel.java**

**Class Declaration:**
- **Class Name:** `EnhancedLoginPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `cardLayout` | `CardLayout` | Switch between login and registration views |
| `cardPanel` | `JPanel` | Container for card layout |
| `loginCinField` | `ModernTextField` | Login CIN input field |
| `loginPasswordField` | `JPasswordField` | Login password input |
| `driverRadio` | `JRadioButton` | Select driver login type |
| `passengerRadio` | `JRadioButton` | Select passenger login type |
| `adminRadio` | `JRadioButton` | Select admin login type |
| `regCinField` | `ModernTextField` | Driver registration CIN |
| `regNomField` | `ModernTextField` | Driver registration name |
| `regPrenomField` | `ModernTextField` | Driver registration surname |
| `regTelField` | `ModernTextField` | Driver registration phone |
| `regAnneeField` | `ModernTextField` | Driver registration year |
| `regAdresseField` | `ModernTextField` | Driver registration address |
| `regMailField` | `ModernTextField` | Driver registration email |
| `regPasswordField` | `JPasswordField` | Driver registration password |
| `regConfirmPasswordField` | `JPasswordField` | Driver password confirmation |
| `regNomVoitureField` | `ModernTextField` | Car name |
| `regMarqueField` | `ModernTextField` | Car brand |
| `regMatriculeField` | `ModernTextField` | Car registration number |
| `regPlacesSpinner` | `JSpinner` | Number of available seats |
| `pCinField` | `ModernTextField` | Passenger registration CIN |
| `pNomField` | `ModernTextField` | Passenger registration name |
| `pPrenomField` | `ModernTextField` | Passenger registration surname |
| `pTelField` | `ModernTextField` | Passenger registration phone |
| `pAnneeField` | `ModernTextField` | Passenger registration year |
| `pMailField` | `ModernTextField` | Passenger registration email |
| `pAdresseField` | `ModernTextField` | Passenger registration address |
| `pPasswordField` | `JPasswordField` | Passenger registration password |
| `pConfirmPasswordField` | `JPasswordField` | Passenger password confirmation |
| `backgroundAnimationTimer` | `Timer` | Animates gradient background |

**Event Handlers Present:**
- `ActionListener` on login button - calls `performLogin()`
- `ActionListener` on driver registration button - clears fields and shows driver registration form
- `ActionListener` on passenger registration button - shows passenger registration form
- `Timer` (backgroundAnimationTimer) - animate background gradient (50ms interval)
- `ActionListener` on registration submit buttons - validates and registers users
- Radio button selection for user type (Driver/Passenger/Admin)

**Service/DAO Methods Called:**
- `performLogin()` - validates credentials and authenticates user
- User registration methods for drivers and passengers
- Validation of user input fields

**Database Operations/Business Logic:**
- **Authentication:** Login validation for drivers, passengers, and admins
- **User Registration:** Registration flow for both driver and passenger types
- **Form Validation:** Input validation for all registration fields
- **UI Animation:** Animated gradient background for enhanced UI

---

## 4. **EnhancedDriverPanel.java**

**Class Declaration:**
- **Class Name:** `EnhancedDriverPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `contentPanel` | `JPanel` | Container for card layout |
| `contentLayout` | `CardLayout` | Switch between dashboard, trajets, demandes, passagers, new trajet |
| `placesCard` | `StatCard` | Display available seats |
| `trajetsCard` | `StatCard` | Display total trips created |
| `demandesCard` | `StatCard` | Display pending requests count |
| `trajetsTable` | `JTable` | Display driver's trips |
| `trajetsModel` | `DefaultTableModel` | Table model for trips |
| `demandesTable` | `JTable` | Display passenger requests |
| `demandesModel` | `DefaultTableModel` | Table model for requests |
| `passagersTable` | `JTable` | Display accepted passengers |
| `passagersModel` | `DefaultTableModel` | Table model for passengers |
| `notificationBadge` | `JLabel` | Badge for pending requests count |
| `evaluationsContainer` | `JPanel` | Container for evaluations section |
| `averageStarsWidget` | `StarRating` | Display average rating with stars |
| `averageRatingLabel` | `JLabel` | Display average rating value |
| `sidebarButtons` | `List<SidebarButton>` | Navigation buttons collection |

**Event Handlers Present:**
- `ActionListener` on sidebar buttons - switches between card views (DASHBOARD, TRAJETS, DEMANDES, PASSAGERS, NEW_TRAJET, GROUPS)
- `ActionListener` on logout button - confirmation dialog and logout
- `ActionListener` on "Create Trip" button - shows new trajet form
- `ActionListener` on trips table - detail view or edit actions
- `ActionListener` on requests table - accept/reject passenger requests

**Service/DAO Methods Called:**
- `refreshDashboard()` - refresh dashboard statistics
- `refreshTrajetsTable()` - refresh trips table
- `refreshDemandesTable()` - refresh requests table
- `refreshPassagersTable()` - refresh accepted passengers table
- `mainFrame.showGroupsPanel()` - navigate to groups

**Database Operations/Business Logic:**
- **Trip Management:** Create, view, and manage trips
- **Request Management:** Display and handle passenger requests
- **Statistics:** Display available seats, trip count, and pending requests
- **Ratings:** Display average driver rating with star visualization
- **Group Management:** Navigate to group chat functionality

---

## 5. **EnhancedPassengerPanel.java**

**Class Declaration:**
- **Class Name:** `EnhancedPassengerPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `contentPanel` | `JPanel` | Container for card layout |
| `contentLayout` | `CardLayout` | Switch between dashboard, search, reservations |
| `reservationsCard` | `StatCard` | Display reservations count |
| `trajetsDispoCard` | `StatCard` | Display available trips count |
| `statusCard` | `StatCard` | Display current status |
| `trajetsTable` | `JTable` | Display available trips |
| `trajetsModel` | `DefaultTableModel` | Table model for trips |
| `mesReservationsTable` | `JTable` | Display passenger reservations |
| `reservationsModel` | `DefaultTableModel` | Table model for reservations |
| `notificationBadge` | `JLabel` | Badge for unread notifications |
| `sidebarButtons` | `List<SidebarButton>` | Navigation buttons collection |

**Event Handlers Present:**
- `ActionListener` on sidebar buttons - switches between dashboard, search, reservations, groups
- `ActionListener` on logout button - confirmation dialog and logout
- `ActionListener` on "Search Trip" button - shows trip search view
- `ActionListener` on "View Reservations" button - shows reservations view
- `ActionListener` on "Notifications" button - shows notifications panel
- `ActionListener` on "Help" button - opens admin conversation for help
- `ActionListener` on "Admin Discussion" button - opens admin conversation
- `ActionListener` on "Complaint" button - submit complaint dialog

**Service/DAO Methods Called:**
- `refreshDashboard()` - refresh dashboard stats
- `refreshTrajetsTable()` - refresh available trips
- `refreshReservationsTable()` - refresh reservations
- `mainFrame.openAdminConversationForCurrentUser(...)` - open admin chat for help or complaints
- `mainFrame.getGestion().submitComplaint(user, detail)` - submit complaint
- `updateSidebarSelection(int)` - update selected navigation item

**Database Operations/Business Logic:**
- **Trip Search & Reservation:** Search and make reservations for trips
- **Reservations Management:** View accepted and pending reservations
- **Notifications:** Display trip-related notifications
- **Complaints:** Submit and track complaints
- **Help System:** Open conversations with admin for assistance
- **Statistics:** Display available trips and reservation counts

---

## 6. **NotificationPanel.java**

**Class Declaration:**
- **Class Name:** `NotificationPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `notificationsContainer` | `JPanel` | Container for notifications list |
| `noNotificationsLabel` | `JLabel` | Placeholder when no notifications |
| `backButton` | `JButton` | Return to dashboard button |
| `scrollPane` | `JScrollPane` | Scrollable notifications area |

**Event Handlers Present:**
- `ActionListener` on back button - calls `gestion.marquerToutesCommelues(...)` and `mainFrame.showPassengerPanel()`
- `MouseListener` on each notification card - marks notification as read on click

**Service/DAO Methods Called:**
- `gestion.getToutesNotifications(cinPassager)` - retrieve all notifications for passenger
- `gestion.marquerCommelue(cinPassager, notificationId)` - mark single notification as read
- `gestion.marquerToutesCommelues(cinPassager)` - mark all notifications as read

**Database Operations/Business Logic:**
- **Notification Display:** Shows all notifications (read and unread)
- **Status Tracking:** Displays "non lue" (unread) indicator
- **Interaction:** Click notifications to mark as read
- **Color Coding:** Different colors based on notification type

---

## 7. **DriverNotificationPanel.java**

**Class Declaration:**
- **Class Name:** `DriverNotificationPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `notificationsContainer` | `JPanel` | Container for notifications list |
| `noNotificationsLabel` | `JLabel` | Placeholder when no notifications |
| `backButton` | `JButton` | Return to dashboard button |
| `countLabel` | `JLabel` | Show count of displayed notifications |

**Event Handlers Present:**
- `ActionListener` on back button - calls `gestion.marquerToutesCommelueConducteur(...)` and returns to driver panel
- Similar to NotificationPanel but specific to conducteur

**Service/DAO Methods Called:**
- `gestion.getToutesNotificationsConducteur(conducteurCIN)` - get all driver notifications
- `gestion.markerCommelue(...)` - mark notification as read
- `gestion.markerToutesCommelueConducteur(...)` - mark all as read

**Database Operations/Business Logic:**
- **Notification Display:** Shows all notifications (limits to 10 most recent)
- **Status Tracking:** Indicates read/unread status
- **Color Coding:** Type-based color scheme

---

## 8. **GroupsPanel.java**

**Class Declaration:**
- **Class Name:** `GroupsPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `groupsContainer` | `JPanel` | Container for groups list |
| `emptyLabel` | `JLabel` | Placeholder when no groups |
| `backBtn` | `JButton` | Return to previous panel |
| `refreshBtn` | `RoundedButton` | Refresh groups list |

**Event Handlers Present:**
- `ActionListener` on back button - returns to driver or passenger panel based on user type
- `ActionListener` on refresh button - calls `refreshGroups()`
- `ActionListener` on each group card - calls `openDiscussion(group)`

**Service/DAO Methods Called:**
- `gestion.getGroupesPourUtilisateur(cinUtilisateur)` - get all groups for user
- `gestion.rechercher_conducteur(cinConducteur)` - get driver info for display

**Database Operations/Business Logic:**
- **Group Display:** List all groups user belongs to
- **Member Count:** Shows number of members per group
- **Group Info:** Shows creation date and driver name
- **Chat Navigation:** Open group chat on button click

---

## 9. **GroupChatPanel.java**

**Class Declaration:**
- **Class Name:** `GroupChatPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `messagesPanel` | `JPanel` | Container for message bubbles |
| `messagesScrollPane` | `JScrollPane` | Scrollable messages area |
| `messageInputArea` | `JTextArea` | Input field for new messages |
| `sendBtn` | `RoundedButton` | Send message button |
| `cancelBtn` | `RoundedButton` | Clear input button |
| `backBtn` | `JButton` | Return to groups panel |

**Event Handlers Present:**
- `ActionListener` on send button - sends message and refreshes display
- `ActionListener` on cancel button - clears message input
- `ActionListener` on back button - returns to groups panel
- `ActionListener` on delete button (own messages only) - deletes message via `deleteMessage(msg)`

**Service/DAO Methods Called:**
- `gestion.getMessagesPourGroupe(groupId)` - load all group messages
- `gestion.sendGroupMessage(...)` - send new message to group
- `gestion.deleteGroupMessage(...)` - delete user's own message

**Database Operations/Business Logic:**
- **Message Display:** Messenger-style conversation with bubbles
- **Message Ownership:** Own messages on right, others on left
- **Message Deletion:** Users can delete their own messages
- **Auto-scroll:** Scrolls to newest message
- **Timestamp Display:** Shows time for each message

---

## 10. **MessagingPanel.java**

**Class Declaration:**
- **Class Name:** `MessagingPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `messagesPanel` | `JPanel` | Container for message bubbles |
| `messagesScrollPane` | `JScrollPane` | Scrollable messages area |
| `messageInputArea` | `JTextArea` | Input field for messages |
| `sendBtn` | `RoundedButton` | Send message button |
| `cancelBtn` | `RoundedButton` | Clear input button |
| `backBtn` | `JButton` | Return button |

**Event Handlers Present:**
- `ActionListener` on send button - sends message
- `ActionListener` on cancel button - clears input
- `ActionListener` on back button - stops polling and executes back action or refreshes panel
- `Timer` (refreshTimer) - periodic message polling

**Service/DAO Methods Called:**
- `CSVDatabase.loadMessages()` - load all messages
- `loadMessages()` - filter and display conversation messages
- `sendMessage()` - send new message

**Database Operations/Business Logic:**
- **Private Messaging:** One-on-one conversations
- **Message Polling:** Regular refresh of messages
- **Conversation Filter:** Shows only messages between two users
- **Message Display:** Bubble-style layout

---

## 11. **DriverPanel.java** (Legacy)

**Class Declaration:**
- **Class Name:** `DriverPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `contentPanel` | `JPanel` | Card layout container |
| `contentLayout` | `CardLayout` | Layout for switching views |
| `trajetsTable` | `JTable` | Display trips |
| `demandesTable` | `JTable` | Display requests |
| `passagersAcceptesTable` | `JTable` | Display accepted passengers |
| `placesLabel`, `trajetsCountLabel`, `demandesCountLabel` | `JLabel` | Stats labels |

**Event Handlers Present:**
- Menu button listeners for sidebar navigation
- Mouse listener for hover effects on menu items
- Table selection listeners
- Logout button with confirmation

**Service/DAO Methods Called:**
- `refreshTrajetsTable()`, `refreshDemandesTable()`, `refreshPassagersAcceptesTable()`

**Database Operations/Business Logic:**
- Same as EnhancedDriverPanel but with simpler UI

---

## 12. **PassengerPanel.java** (Legacy)

**Class Declaration:**
- **Class Name:** `PassengerPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `contentPanel` | `JPanel` | Card layout container |
| `contentLayout` | `CardLayout` | Layout switching |
| `trajetsDisponiblesTable` | `JTable` | Available trips |
| `mesDemandesTable` | `JTable` | User's requests |
| `mesReservationsTable` | `JTable` | User's reservations |

**Event Handlers Present:**
- Sidebar menu button listeners
- Mouse listeners for hover effects
- Logout with confirmation

**Service/DAO Methods Called:**
- `refreshTrajetsDisponibles()`, `refreshMesDemandes()`, `refreshMesReservations()`

**Database Operations/Business Logic:**
- Similar to EnhancedPassengerPanel with legacy styling

---

## 13. **LoginPanel.java** (Legacy)

**Class Declaration:**
- **Class Name:** `LoginPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `cardLayout` | `CardLayout` | Switch login/register |
| `cardPanel` | `JPanel` | Card container |
| `loginCinField` | `JTextField` | Login CIN |
| Various registration fields | `JTextField`, `JSpinner`, `JRadioButton` | Registration form fields |

**Event Handlers Present:**
- Login button action
- Registration button actions
- Radio button selection for user type

**Service/DAO Methods Called:**
- `loginAsDriver()`, `loginAsPassenger()`

---

## 14. **DateTimePickerPanel.java**

**Class Declaration:**
- **Class Name:** `DateTimePickerPanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `dateSpinner` | `JSpinner` | Select date |
| `hourSpinner` | `JSpinner` | Select hour (0-23) |
| `minuteSpinner` | `JSpinner` | Select minutes (0-59) |
| `calendar` | `Calendar` | Internal calendar state |

**Event Handlers Present:**
- `ChangeListener` on each spinner - calls `updateDateTime()` on change

**Service/DAO Methods Called:**
- Date/time selection and validation

**Database Operations/Business Logic:**
- Provides LocalDateTime selection interface
- Validates time ranges

---

## 15. **WeeklySchedulePanel.java**

**Class Declaration:**
- **Class Name:** `WeeklySchedulePanel`
- **Extends:** `JPanel`

**Interactive Components:**

| Component Name | Type | Purpose |
|---|---|---|
| `daysPanel` | `JPanel` | Container for day schedules |
| `daySchedules` | `Map<String, DaySchedule>` | Map of day schedules |

**Inner Class: `DaySchedule`**
- Contains enable checkbox and departure/return time spinners

**Event Handlers Present:**
- Checkbox for each day (enable/disable)
- Spinners for departure and return times

**Service/DAO Methods Called:**
- `getSchedule()` - returns Map of schedules
- `setSchedule()` - sets schedules from map
- `getScheduleAsString()` - serializes schedule

**Database Operations/Business Logic:**
- Weekly recurring schedule management
- Days: MON, TUE, WED, THU, FRI, SAT, SUN
- Time format: "HH:mm-HH:mm"

---

## 16. **DialogUtils.java**

**Class Declaration:**
- **Class Name:** `DialogUtils`
- **Type:** Utility class with static methods

**Static Methods:**

| Method | Components | Purpose |
|---|---|---|
| `showTrajetDetails()` | `JDialog`, `JLabel`, `JScrollPane` | Display trip details dialog |
| `showUserProfile()` | `JDialog`, `JLabel`, `JPanel` | Display user profile dialog |
| Additional detail methods | Various | Show various details in dialogs |

**Database Operations/Business Logic:**
- Displays trip information with driver and passenger details
- Shows user profile information
- Modal dialogs for detailed views

---

## 17. **ModernUIComponents.java**

**Class Declaration:**
- **Class Name:** `ModernUIComponents`
- **Type:** Utility class with static inner classes

**Inner Classes & Components:**

| Component | Type | Purpose |
|---|---|---|
| `Colors` | Static class | Color palette constants |
| `Fonts` | Static class | Font definitions |
| `RoundedButton` | JButton subclass | Custom rounded button with hover animation |
| `GradientButton` | JButton subclass | Button with gradient fill |
| `ModernTextField` | JTextField subclass | Styled text input field |
| `ModernPasswordField` | JPasswordField subclass | Styled password field |
| `SidebarButton` | JButton subclass | Navigation sidebar button |
| `StatCard` | JPanel subclass | Statistics card display |
| `GlassCard` | JPanel subclass | Semi-transparent card container |
| `GradientHeader` | JPanel subclass | Header with gradient background |
| `StarRating` | JPanel subclass | Star rating display widget |

**Event Handlers Present:**
- Mouse listeners on buttons for hover/press effects
- Animation timers for smooth transitions
- Focus and paint listeners for custom rendering

**Database Operations/Business Logic:**
- Pure UI components - no database operations
- Provides consistent styling across application
- Animation framework for smooth interactions

---

## 18. **StyleUtils.java**

**Class Declaration:**
- **Class Name:** `StyleUtils`
- **Type:** Utility class with static methods

**Static Methods & Utilities:**

| Method | Purpose |
|---|---|
| `createPrimaryButton(String)` | Creates blue primary button |
| `createSecondaryButton(String)` | Creates secondary button |
| `createSuccessButton(String)` | Creates green success button |
| `createWarningButton(String)` | Creates yellow warning button |
| `createDangerButton(String)` | Creates red danger button |
| `createTitleLabel()` | Creates title label |
| `createLabel()` | Creates styled label |
| `createStyledTextField()` | Creates styled text field |
| `createCardPanel()` | Creates white card container |
| `createSeparator()` | Creates styled separator |
| `showConfirm()` | Shows confirmation dialog |
| `showError()` | Shows error dialog |
| `showSuccess()` | Shows success dialog |

**Color Constants:**
- PRIMARY_COLOR, PRIMARY_DARK
- SECONDARY_COLOR, ACCENT_COLOR
- WARNING_COLOR, DANGER_COLOR
- BACKGROUND_COLOR, CARD_COLOR
- TEXT_PRIMARY, TEXT_SECONDARY

**Database Operations/Business Logic:**
- Pure styling utility - no database operations
- Dialog helper methods
- Consistent button and component styling

---

## **Summary Statistics**

| Category | Count |
|---|---|
| **Main GUI Classes** | 10 |
| **Legacy Panels** | 3 |
| **Utility Classes** | 3 |
| **Total GUI Files** | 18 |
| **Total Interactive Components** | 150+ |
| **Event Handler Types** | 6+ |
| **Service Integration Points** | 50+ |
| **Database Operations** | CSV-based persistence |

---

## **Key Service/DAO Integration Points**

### Core Services Referenced:
1. **Gestion_covoiturage** - Main business logic service
2. **CSVDatabase** - Persistence layer
3. **Models** - Data classes (Conducteur, Passager, Admin, Trajet, etc.)

### Common DAO Patterns:
- User authentication (login/registration)
- Trip management (CRUD)
- Passenger request handling
- Notification management
- Group and message handling
- Complaint submission

### Database Operations:
- **Load:** `CSVDatabase.loadAllData()`, `loadMessages()`
- **Save:** `CSVDatabase.saveAllData()`, `createBackup()`
- **Queries:** Various `gestion` methods for filtering and retrieval
- **Updates:** State changes marked via `markUnsavedChanges()`

---

## **Event Flow Architecture**

```
MainFrame (JFrame)
├── LoginPanel / EnhancedLoginPanel (Auth)
├── DriverPanel / EnhancedDriverPanel
│   ├── Dashboard (Stats Cards + Charts)
│   ├── Trips Management (JTable)
│   ├── Requests (JTable)
│   ├── Passengers (JTable)
│   └── Groups → GroupsPanel → GroupChatPanel
├── PassengerPanel / EnhancedPassengerPanel
│   ├── Dashboard (Stats Cards)
│   ├── Search Trips (JTable)
│   ├── Reservations (JTable)
│   ├── Notifications → NotificationPanel
│   └── Groups → GroupsPanel → GroupChatPanel
├── AdminPanel
│   ├── Dashboard (Stats)
│   ├── Users Management (JTable)
│   ├── Trips Management (JTable)
│   ├── Evaluations View
│   └── Conversations
└── Utility Panels
    ├── MessagingPanel (1:1 Chat)
    ├── NotificationPanel / DriverNotificationPanel
    ├── DateTimePickerPanel
    └── WeeklySchedulePanel
```

---

**End of GUI Inventory**
