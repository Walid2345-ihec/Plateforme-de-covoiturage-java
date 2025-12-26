# Explication complète du projet « Plateforme de Covoiturage »

Ce document décrit l'architecture du code, le rôle des packages et une notice par classe (emplacement du fichier, attributs, méthodes importantes et comportements particuliers). Le but : offrir une lecture claire pour maintenance, extension ou revue.

---

Table des matières
- Vue d'ensemble
- Package `Models` (modèles de domaine)
  - `User.java`
  - `Conducteur.java`
  - `Passager.java`
  - `Trajet.java`
  - `ValidationUtils.java`
- Package `Services`
  - `Gestion_covoiturage.java`
  - `CSVDatabase.java`
- Package `GUI` (interfaces graphiques et composants)
  - `MainFrame.java`
  - `StyleUtils.java`
  - `ModernUIComponents.java`
  - `DialogUtils.java`
  - `LoginPanel.java`
  - `EnhancedLoginPanel.java`
  - `DriverPanel.java`
  - `EnhancedDriverPanel.java`
  - `PassengerPanel.java`
  - `EnhancedPassengerPanel.java`
- Point d'entrée `App.AppGUI`
- Flux d'exécution au démarrage / sauvegardes / backups
- Remarques et bonnes pratiques

---

Vue d'ensemble
--------------
Le projet est structuré autour de trois zones principales :
1. Models : classes représentant l'état métier (utilisateurs, trajets, validations).
2. Services : logique applicative et persistance (en mémoire + CSV).
3. GUI : panneaux Swing qui manipulent les services et modèles via `MainFrame`.

Les données persistées sont des CSV dans le dossier `data/` (conducteurs.csv, passagers.csv, trajets.csv). Avant chaque sauvegarde, un backup horodaté est créé dans `data/backups/` et une rotation limite le nombre de backups conservés.


Package Models (src/Models)
---------------------------
Fichier : `src/Models/User.java`
- Rôle : classe de base représentant un utilisateur.
- Attributs (protected):
  - String cin
  - String nom
  - String prenom
  - String tel
  - Year anneeUniversitaire
  - String adresse
  - String mail
  - String passwordHash (SHA-256)
- Constructeurs :
  - Constructeurs interactifs (utilisant Scanner) — dédié à usage console/compatibilité.
  - Constructeur paramétré avec mot de passe clair — effectue validations via ValidationUtils et stocke le hash.
  - Constructeur pour le chargement CSV acceptant un mot de passe déjà hashé.
- Méthodes importantes :
  - getters / setters standards
  - setPassword(String) : valide et stocke le hash
  - verifyPassword(String) : compare via ValidationUtils.verifyPassword
- Remarques : validations centralisées dans `ValidationUtils`.

Fichier : `src/Models/Conducteur.java`
- Rôle : étend `User` avec des informations véhicule.
- Attributs privés :
  - String nomVoiture
  - String marqueVoiture
  - String matricule
  - int placesDisponibles
- Constructeurs : interactifs, paramétrés et pour chargement CSV (avec password hash).
- Méthodes : getters/setters, toString().
- Validation : matricule, véhicule, places vérifiées via ValidationUtils.

Fichier : `src/Models/Passager.java`
- Rôle : étend `User` (passager).
- Attributs privés :
  - boolean chercheCovoit (indique s'il cherche activement)
  - Conducteur conducteur (relation optionnelle)
- Constructeurs : interactifs, paramétrés, CSV.
- Méthodes : isChercheCovoit(), setChercheCovoit(...), toString().

Fichier : `src/Models/Trajet.java`
- Rôle : représente un trajet.
- Attributs privés :
  - String departTrajet, arriveeTrajet
  - Duration dureeTrajet
  - float prix
  - boolean trajet_valide
  - String statusTrajet (constantes de statut)
  - Conducteur conducteur
  - Vector<Passager> passagersAcceptes
  - Vector<Passager> passagersDemandes
  - int maxPlaces
- Statuts (constantes) : PENDING, PENDING_APPROVAL, IN_PROGRESS, FINISHED
- Constructeurs : interactif et paramétrés ; constructeur optimisé pour loader CSV
- Méthodes importantes :
  - getters/setters
  - boolean addDemand(Passager)
  - boolean removeDemand(Passager)
  - boolean acceptPassenger(Passager)
  - boolean removeAccepted(Passager)
  - int getAvailablePlaces()
  - boolean isFull(), isPending(), isInProgress(), isFinished()
  - helpers de sérialisation : getPassagersAcceptesCINs(), getPassagersDemandesCINs()
- Comportement : gère la logique de demande/réponse, maintien du statut et de la capacité.

Fichier : `src/Models/ValidationUtils.java`
- Rôle : centralise expressions régulières, validations, hashing (SHA-256) et messages d'erreur.
- Attributs publics statiques : plusieurs Pattern (CIN_PATTERN, EMAIL_PATTERN...), constantes d'erreur.
- Méthodes :
  - isValidPhone/isValidCIN/isValidName/isValidEmail/isValidPassword/isValidMatricule/isValidVehicleName
  - validatePhone/validateCIN/validateName/validateEmail/validatePassword (jettent IllegalArgumentException)
  - hashPassword(String) : retourne SHA-256 hex
  - verifyPassword(plain, storedHash)
  - getPasswordStrengthDetails(String)
- Remarques : utilisé par modèles et interfaces pour valider et hasher.


Package Services (src/Services)
-------------------------------
Fichier : `src/Services/Gestion_covoiturage.java`
- Rôle : conteneur/gestionnaire en mémoire des entités (users, trajets) et API utilisée par l'UI.
- Attributs privés :
  - int Index_trajet_conducteur, Index_conducteur, Index_passager (indices internes pour compatibilité console)
  - Vector<User> users
  - Vector<Trajet> trajets
  - Vector<User> passagers_acceptes (historique)
  - Map<String, Vector<String>> demandes_par_conducteur (mapping conducteur -> CINs demandeurs)
- Méthodes publiques importantes :
  - getUsers(), getTrajets(), getPassagers_acceptes(), setUsers(), setTrajets()
  - User rechercher_user(String cin)
  - Conducteur rechercher_conducteur(String cin)
  - Passager rechercher_passager(String cin)
  - ajouter_demande_pour_conducteur(String cinConducteur, String cinPassager)
  - supprimer_demande_pour_conducteur(String, String)
  - boolean ajouter_demande_pour_trajet(Trajet t, String cinPassager)
  - boolean accepter_passager_pour_trajet(Trajet t, String cinPassager)
- Comportement : API de haut niveau pour l'UI — encapsule la logique d'acceptation et met à jour utilisateurs/trajets et mapping demandes.

Fichier : `src/Services/CSVDatabase.java`
- Rôle : lecture/écriture des données vers/depuis CSV; backup & restauration.
- Constantes : chemins (data/, data/backups/, conducteurs.csv, passagers.csv, trajets.csv), DELIMITER `;`, MAX_BACKUPS
- Méthodes principales :
  - initializeDataFolder() : crée dossiers `data/` et `data/backups/` si manquants
  - createBackup() : copie fichiers CSV vers `data/backups/` avec horodatage et appelle rotateBackups()
  - rotateBackups() : supprime anciens backups pour garder `MAX_BACKUPS`
  - restoreFromBackup() : restaure depuis les backups les plus récents
  - saveConducteurs(Vector<User>) : écrit `conducteurs.csv` (header + lignes)
  - savePassagers(Vector<User>) : écrit `passagers.csv`
  - saveTrajets(Vector<Trajet>) : écrit `trajets.csv` (nouveau format avec AcceptedCINs et PendingCINs)
  - loadConducteurs(), loadPassagers(), loadTrajets(Vector<User>) : parse CSV et recrée objets (les trajets requièrent d'abord les users pour reconstruire les références)
  - saveAllData(Gestion_covoiturage gestion) et loadAllData(Gestion_covoiturage gestion) : méthodes pratiques utilisés au démarrage/arrêt
  - exportToExcelCSV(Vector<Trajet>, String filename) : export lisible par Excel (BOM UTF-8 + en-têtes français)
- Format CSV : `;` comme séparateur. Les fichiers contiennent des entêtes. `trajets.csv` contient colonnes : Depart;Arrivee;DureeMinutes;Status;Prix;ConducteurCIN;PassagerCIN;MaxPlaces;AcceptedCINs;PendingCINs
- Backups : horodatés `base_YYYYMMDD_HHMMSS.csv`, rotation pour limiter l'espace.


Package GUI (src/GUI)
---------------------
Rôle général : panels Swing organisés et stylisés. `MainFrame` orchestre la navigation (CardLayout) et les interactions entre Services et UI. Plusieurs panels existent en version « simple » et version « enhanced » (moderne).

Fichier : `src/GUI/MainFrame.java`
- Rôle : fenêtre principale de l'application. Point central reliant gestion, panels et persistance.
- Attributs :
  - CardLayout cardLayout; JPanel mainPanel
  - Gestion_covoiturage gestion
  - EnhancedLoginPanel loginPanel; EnhancedDriverPanel driverPanel; EnhancedPassengerPanel passengerPanel
  - User currentUser; String userType
  - Timer autoSaveTimer; static final int AUTO_SAVE_INTERVAL (5 minutes)
  - boolean hasUnsavedChanges
- Constructeur :
  - instancie `Gestion_covoiturage`, charge les données via `CSVDatabase.loadAllData(gestion)`, initialise UI et panels
  - configure auto-save périodique, listener de fermeture (windowClosing) et shutdown hook
- Méthodes importantes :
  - loadDataFromCSV(), setupAutoSave(), setupPeriodicAutoSave(), setupShutdownHook()
  - handleApplicationClose() : affiche confirmation si modifications non sauvegardées, appelle saveDataWithBackup() si nécessaire
  - saveDataWithBackup() : crée backup + CSVDatabase.saveAllData(gestion)
  - saveDataToCSV() : sauvegarde sans backup
  - exportTrajetsToCSV(String filename)
  - markUnsavedChanges()
  - navigation : showLogin(), showDriverPanel(Conducteur), showPassengerPanel(Passager)
  - getters : getGestion(), getCurrentUser(), getCurrentConducteur(), getCurrentPassager()
  - notifyDataChanged() : demande aux panels de rafraîchir leurs modèles
- Particularités :
  - Utilise réflexion pour synchroniser certains indices privés de `Gestion_covoiturage` (compatibilité console/anciennes APIs) dans `showDriverPanel`.
  - Auto-save : Timer Swing qui sauvegarde toutes les 5 minutes si hasUnsavedChanges=true.
  - Shutdown hook Java qui tente de sauvegarder via `CSVDatabase.saveAllData(gestion)` lors d'un arrêt brutal.

Fichier : `src/GUI/StyleUtils.java`
- Rôle : utilitaires de style : couleurs, fontes, fonctions créant des boutons/labels/champs stylisés.
- Méthodes : createPrimaryButton, createSecondaryButton, createSuccessButton, createDangerButton, createStyledTextField, createStyledPasswordField, createLabel, createHeaderLabel, createTitleLabel, createCardPanel, createStyledComboBox, createStyledTable, createStyledScrollPane, helpers d'affichage (showSuccess/showError/showWarning/showConfirm).
- Utilisé par la majorité des panels pour cohérence graphique.

Fichier : `src/GUI/ModernUIComponents.java`
- Rôle : bibliothèque de composants graphiques modernes et réutilisables. Contient plusieurs classes internes :
  - Colors, Fonts (palettes)
  - RoundedButton, GradientButton
  - ModernTextField, ModernPasswordField
  - GlassCard, GradientHeader, StatCard, SidebarButton
  - applyModernScrollBar(JScrollPane)
- Ces composants offrent styles, animations et comportement pour les `Enhanced*Panel`.

Fichier : `src/GUI/DialogUtils.java`
- Rôle : boîtes de dialogue personnalisées : showTrajetDetails, showUserProfile, showAboutDialog, showLoadingDialog.
- Utilisé par panels pour afficher infos détaillées (profil, trajet) dans une UI cohérente.

Fichier : `src/GUI/LoginPanel.java`
- Rôle : version « classique » du panneau de connexion/inscription.
- Structure : CardLayout interne avec cartes LOGIN, REGISTER_DRIVER, REGISTER_PASSENGER.
- Composants : champs texte classiques, boutons créés via StyleUtils.
- Méthodes : loginAsDriver(), loginAsPassenger(), registerDriver(), registerPassenger(), clearAllRegistrationFields().
- Validation : validations légères (format année, email regex) avant création d'objets Conducteur/Passager (création avec mot de passe temporaire pour compatibilité legacy).

Fichier : `src/GUI/EnhancedLoginPanel.java`
- Rôle : version visuelle avancée du login + inscription. Utilise `ModernUIComponents`.
- Composants : ModernTextField, ModernPasswordField, panels animés, dialogues stylés.
- Méthodes clés : performLogin(), registerDriver(), registerPassenger(), showModernSuccess/showModernError (dialogs customisés), validations via ValidationUtils.
- Particularités : animations de fond, tooltips et validation stricte (mots de passe forts). Lors de l'inscription, les password fields sont validés et stockés hashed (via constructors des modèles qui appellent ValidationUtils.hashPassword).

Fichier : `src/GUI/DriverPanel.java` et `src/GUI/EnhancedDriverPanel.java`
- Rôle : dashboards pour conducteurs — gestion des trajets, demandes et passagers acceptés.
- Attributs : tables (trajets, demandes, passagers), labels de stats, boutons d'actions.
- Méthodes : rafraîchissement des tables (refreshTrajetsTable, refreshDemandesTable, refreshPassagersTable), actions utilisateur (createTrajet, modifyTrajetPrice, deleteTrajet, acceptPassenger, finishSelectedTrajet).
- Différence : `EnhancedDriverPanel` utilise ModernUIComponents pour un look sophistiqué et offre plus d'UX (stat cards, panneaux glass, etc.), `DriverPanel` est une version plus simple.

Fichier : `src/GUI/PassengerPanel.java` et `src/GUI/EnhancedPassengerPanel.java`
- Rôle : dashboards pour passagers — recherche de trajets, envoi de demandes, gestion des réservations.
- Composants : tableaux (trajets dispon., mes demandes, mes réservations), formulaires de recherche, boutons d'action.
- Méthodes : refreshTrajetsDisponibles(), reserverTrajet(), notifierConducteur(), annulerDemande(), refreshMesDemandes(), refreshMesReservations().
- Enhanced version : meilleure UI, composants modernes, plus d'effets et feedbacks.


Point d'entrée : `src/App/AppGUI.java`
--------------------------------------
- Rôle : lance l'interface graphique (splash + MainFrame) sur l'Event Dispatch Thread.
- Ce fichier configure LookAndFeel, options UI (fonts, arcs), active antialiasing puis crée `MainFrame`.

Flux d'exécution principal
--------------------------
1. `App.AppGUI.main` est exécuté. Affiche un splash puis instancie `MainFrame`.
2. `MainFrame()` :
   - crée `Gestion_covoiturage` en mémoire
   - appelle `CSVDatabase.loadAllData(gestion)` pour charger `data/*.csv` en mémoire
   - initialise l'UI (LookAndFeel, `mainPanel` + CardLayout)
   - initialise panels (login, driver, passenger)
   - démarre l'auto-save périodique (Timer Swing toutes les 5 minutes) et ajoute un shutdown hook
3. L'utilisateur se connecte via `LoginPanel` ou `EnhancedLoginPanel`. Les vérifications utilisent `ValidationUtils`.
4. Une fois connecté, `MainFrame.showDriverPanel` ou `showPassengerPanel` est appelé — panels rafraîchissent leurs vues depuis `gestion`.
5. Actions utilisateur (créer trajet, demander réservation, accepter) invoquent des méthodes de `Gestion_covoiturage` et modifient les modèles en mémoire.
6. Sauvegarde : l'UI appelle `CSVDatabase.saveAllData(gestion)` manuellement (bouton, fermeture) ou automatiquement via :
   - auto-save périodique (Timer) si `hasUnsavedChanges == true`
   - prompt lors de la fermeture de fenêtre
   - hook JVM au shutdown (tentative de sauvegarde d'urgence)
7. Avant chaque sauvegarde, `CSVDatabase.createBackup()` est appelée pour protéger les fichiers existants.

Remarques techniques, sécurité et bonnes pratiques
-------------------------------------------------
- Passwords : les mots de passe sont hachés avec SHA-256 (classe `ValidationUtils.hashPassword`) avant d'être stockés dans les CSV. Les fichiers CSV contiennent donc le hash, pas le mot de passe en clair. (Note: dans un contexte réel, il faudrait ajouter un salt et utiliser PBKDF2/Bcrypt/Argon2.)
- Validation centralisée : toutes les règles (CIN, email, matricule, password strength) sont regroupées dans `ValidationUtils`.
- Persistances : format CSV avec `;` comme délimiteur (facilite la compatibilité sur des contenus contenant des virgules). Le loader supporte l'ancien et le nouveau format de `trajets.csv` (compatibilité ascendante).
- Backups : `data/backups/` contient copies horodatées ; `MAX_BACKUPS` limite la quantité stockée.
- Threads et UI : l'auto-save utilise `javax.swing.Timer` (fonctionne sur EDT). Le shutdown hook est une Thread séparée qui tente de sauvegarder via `CSVDatabase.saveAllData`.
- Reflection : `MainFrame` utilise la réflexion pour modifier des champs privés de `Gestion_covoiturage` (Index_conducteur, Index_trajet_conducteur, Index_passager) — cela est fait pour compatibilité avec du code console antérieur mais est fragile ; si possible, préférer une API publique dans `Gestion_covoiturage`.
- Confidentialité : l'UI masque certaines informations sensibles (CIN partiel, email/phone masqués) jusqu'à confirmation de réservation.

Fichiers de données (dossier `data/`)
- conducteurs.csv
- passagers.csv
- trajets.csv
- backups/ (fichiers horodatés)

Emplacements clés (récapitulatif des fichiers source)
- src/Models: User.java, Conducteur.java, Passager.java, Trajet.java, ValidationUtils.java
- src/Services: Gestion_covoiturage.java, CSVDatabase.java
- src/GUI: MainFrame.java, StyleUtils.java, ModernUIComponents.java, DialogUtils.java,
  LoginPanel.java, EnhancedLoginPanel.java, DriverPanel.java, EnhancedDriverPanel.java,
  PassengerPanel.java, EnhancedPassengerPanel.java
- src/App: AppGUI.java

Conclusion et suggestions rapides
- Le code est bien organisé par responsabilités : modèles, services et UI.
- Pour production : remplacer SHA-256 par un algorithme de dérivation (PBKDF2/BCrypt/Argon2) + salt.
- Éviter la réflexion pour modifier l'état privé d'autres classes ; créer des API publiques sur `Gestion_covoiturage` pour régler les indices.
- Ajouter des tests unitaires (ValidationUtils, CSVDatabase parsing round-trip) pour prévenir régressions lors d'évolutions du format CSV.
- Considérer migrer la persistance CSV vers une petite base embarquée (SQLite) si le besoin de transactions et requêtes complexes augmente.

---

Si tu veux, je peux :
- Ajouter ce document dans un format plus synthétique (README) ou produire un diagramme UML ASCII.
- Générer des tests unitaires de base (ex. pour `ValidationUtils` et `CSVDatabase` round‑trip)
- Remplacer l'utilisation de SHA-256 par PBKDF2 (implémentation et tests).

## Encapsulation, héritage et polymorphisme

Cette section explique comment les trois grands principes de la programmation orientée objet (POO) — encapsulation, héritage et polymorphisme — sont appliqués dans ce projet et donne des exemples concrets de classes et méthodes.

1) Encapsulation

- Principe : cacher l'état interne d'un objet (attributs) et n'exposer que des méthodes publiques (getters/setters ou API) pour garantir l'invariance et contrôler l'accès.
- Comment c'est utilisé ici :
  - Les classes modèles (`User`, `Conducteur`, `Passager`, `Trajet`) déclarent la plupart de leurs attributs en `private` ou `protected` et fournissent des getters/setters publics. Exemple : `User` a `protected String cin` et des méthodes `getCin()` / `setCin(...)`.
  - `ValidationUtils` est utilisé par les constructeurs et setters pour valider les valeurs (par exemple `ValidationUtils.validateCIN(cin)`), ce qui centralise les règles et évite des affectations invalides depuis l'extérieur.
  - `CSVDatabase` encapsule la logique d'E/S (read/write, escapement CSV, backups) : l'extérieur n'accède jamais directement aux fichiers, il appelle `CSVDatabase.saveAllData(gestion)` ou `loadAllData(gestion)` — la gestion des fichiers, du format et des backups reste interne à la classe.
  - `MainFrame` gère l'état applicatif (`gestion`, `currentUser`, `hasUnsavedChanges`) via des méthodes publiques (p.ex. `markUnsavedChanges()`, `getGestion()`) plutôt que d'exposer directement les champs internes.

Bénéfices observés : protection des invariants (format CIN, validité des trajets), points uniques de modifications (validator, CSV) et réduction des effets de bord.

2) Héritage (heritage)

- Principe : spécialisation d'une classe générale (superclasse) en classes plus spécifiques (sous-classes), réutilisation de code.
- Comment c'est utilisé ici :
  - `User` est la superclasse générale qui contient les champs et comportements communs à tous les utilisateurs (CIN, nom, mail, hashing mot de passe).
  - `Conducteur` et `Passager` héritent de `User` (`public class Conducteur extends User` / `public class Passager extends User`). Ils réutilisent les attributs et méthodes de `User` et ajoutent leurs propres champs et méthodes :
    - `Conducteur` ajoute `nomVoiture`, `marqueVoiture`, `matricule`, `placesDisponibles` et des validations spécifiques.
    - `Passager` ajoute `chercheCovoit` et une référence optionnelle au `Conducteur`.
  - Exemple concret : les constructeurs paramétrés de `Conducteur` appellent `super(cin, nom, prenom, ...)` pour profiter de la validation centrale et du stockage du hash de mot de passe géré par `User`/`ValidationUtils`.

Bénéfices observés : évitement de duplication pour les champs communs, cohérence des règles (password handling) et possibilité d'étendre facilement le modèle utilisateur.

3) Polymorphisme

- Principe : traiter des objets de différentes classes dérivées comme des instances de leur supertype commun et invoquer des comportements spécifiques via liaisons dynamiques.
- Comment c'est utilisé ici :
  - Collections hétérogènes : `Gestion_covoiturage` stocke tous les utilisateurs dans `Vector<User> users`. Grâce au polymorphisme, on peut stocker `Conducteur` et `Passager` dans le même vecteur et traiter chaque élément selon son type réel.
  - Opérations basées sur instanceof / cast : quand un comportement spécifique est nécessaire, le code utilise `instanceof` puis un cast (p.ex. dans `rechercher_conducteur` : si `user instanceof Conducteur` alors cast en `Conducteur`). Cela permet d'identifier et d'utiliser des méthodes/attributs propres à la sous-classe.
  - Méthodes communes et surcharges : certaines méthodes (par ex. `toString()`) sont redéfinies (`@Override`) dans `Conducteur` et `Passager` pour afficher les informations spécifiques — appelées via la référence de type `User` elles exécutent la version la plus spécifique (liaison dynamique).
  - Polymorphisme de comportement dans `Trajet` : `Trajet` manipule des listes de `Passager` (type concret) mais les tableaux/collections et l'UI considèrent souvent des `User` ou `Passager` de façon interopérable.

Exemple d'usage concret :
- Lors du chargement CSV, `CSVDatabase.loadConducteurs()` crée `Conducteur` et `loadPassagers()` crée `Passager` ; toutes ces instances sont ajoutées au `Vector<User>` de `Gestion_covoiturage`.
- Lorsqu'on recherche un conducteur dans `Gestion_covoiturage.rechercher_conducteur(cin)`, on traverse `users` (type `User`), on teste `instanceof Conducteur` et on cast pour retourner l'objet `Conducteur` avec ses attributs spécifiques (matricule, placesDisponibles).
- Dans l'UI, lorsqu'on affiche la liste des trajets, on peut appeler `t.getConducteur()` qui renvoie un `Conducteur` ou `null` ; la vue utilise `Conducteur.getNom()` ou `getTel()` (méthodes héritées) et `getNomVoiture()` (spécifique).

Remarques pratiques et suggestions
- Le projet illustre un usage classique et pédagogique de l'OOP : encapsulation pour protéger les données, héritage pour structurer les types d'utilisateurs, polymorphisme pour manipuler des collections hétérogènes d'utilisateurs.
- Amélioration recommandée : remplacer certains `instanceof` + cast par des méthodes polymorphes ou patterns (ex. `UserType` enum, méthodes `isConducteur()`/`asConducteur()` centralisées) pour réduire la dispersion du test de type à travers le code.
- Pour aller plus loin : envisager l'introduction d'interfaces (par ex. `Reservable`, `Contactable`) que `Conducteur` et/ou `Passager` implémenteraient pour rendre le polymorphisme plus expressif et éviter des casts explicites.
