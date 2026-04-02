README — Plateforme de covoiturage (français)


Projet réalisé par: Walid Saheb Ettaba / Hassen Achour / Ahmed Chermiti / Ahmed Zribi / Ela Slama
                    Etudiants en 2ème année BI à l'IHEC Carthage
Résumé rapide
------------
Ce dépôt contient une petite application Java Swing pour gérer une plateforme de covoiturage locale (CRUD + demandes / acceptations). Le code suit une architecture simple inspirée de MVC (Models, Views/GUI, Services) pour séparer les responsabilités.

Objectif de ce README
---------------------
Expliquer de façon lisible pour un débutant :
- la structure des dossiers et des packages ;
- le rôle des classes principales (Models, Services, GUI) ;
- comment l'interface graphique (GUI) est connectée à la logique métier ;
- comment lancer et tester l'application.

Prérequis
---------
- JDK installé (Java 8+ recommandé, Java 11+ préférable).
- Un IDE Java (NetBeans, IntelliJ IDEA ou Eclipse) ou un double-clic sur `run.bat` (Windows) pour exécuter.
- (Facultatif) Ant si vous utilisez `build.xml`.

Structure du projet
-------------------
Les fichiers sources sont dans `src/` et les données dans `data/`.
Organisation principale :
- src/App/ : point d'entrée de l'application (classes `App.java`, `AppGUI.java`).
- src/Models/ : contient les classes représentant les données (le "Modèle"). Ex : `User`, `Passager`, `Conducteur`, `Trajet`, `ValidationUtils`.
- src/Services/ : logique métier et persistance (CSV). Ex : `Gestion_covoiturage`, `CSVDatabase`.
- src/GUI/ : toutes les classes de l'interface graphique (vues). Ex : `MainFrame`, `EnhancedDriverPanel`, `EnhancedPassengerPanel`, `LoginPanel`, `ModernUIComponents`.
- data/ : fichiers CSV pour stocker utilisateurs, trajets et sauvegardes (backups/).

Architecture (MVC simplifiée)
----------------------------
- Models : objets Java qui représentent les entités (User, Trajet...). Ils contiennent getters/setters, méthodes utilitaires (ex : `Trajet.accepterPassager(...)`).
- Services : la couche qui manipule les modèles. `Gestion_covoiturage` contient la logique d'ajout de demandes, acceptation des passagers, gestion des collections en mémoire. `CSVDatabase` sauvegarde/charge ces collections dans des fichiers CSV.
- GUI (Views + contrôleurs légers) : les panels Swing affichent les données et appellent `Gestion_covoiturage` (par l'intermédiaire de `MainFrame`) pour effectuer des actions. Les panels rafraîchissent l'affichage après chaque modification.

Comment l'UI est connectée au modèle
-----------------------------------
- `MainFrame` est la classe centrale de l'UI : elle crée une instance unique de `Gestion_covoiturage` (le modèle central) et crée les panels GUI en leur passant `this` (référence au `MainFrame`).
- Chaque panneau GUI (ex : `EnhancedDriverPanel`) récupère les données via `mainFrame.getGestion()` et effectue des opérations métier (ex : `getGestion().ajouter_demande_pour_trajet(...)`).
- Après toute modification (comme accepter un passager ou finir un trajet), les panels appellent :
  - des méthodes locales de refresh (ex : `refreshTrajetsTable()`),
  - et `mainFrame.notifyDataChanged()` qui propage un appel `refreshModels()` aux panels afin d'assurer que toutes les vues sont synchronisées.
- `CSVDatabase` s'occupe de charger/sauvegarder `Gestion_covoiturage` depuis/vers les fichiers CSV (dossier `data/`).

Flux principaux (cas d'usage)
----------------------------
1) Réserver un trajet (côté passager) :
   - Le passager sélectionne un trajet dans `EnhancedPassengerPanel` ou `PassengerPanel`.
   - Le panel appelle `gestion.ajouter_demande_pour_trajet(t, passagerCin)`.
   - La demande est ajoutée dans `Trajet.passagersDemandes` et dans le mapping `demandes_par_conducteur`.
   - Le panel appelle `mainFrame.notifyDataChanged()` pour rafraîchir la vue du conducteur.

2) Accepter un passager (côté conducteur) :
   - Le conducteur voit la liste des demandes dans `EnhancedDriverPanel`.
   - En cliquant sur Accepter, `EnhancedDriverPanel` appelle `gestion.accepter_passager_pour_trajet(trajet, passagerCin)`.
   - Dans `Gestion_covoiturage.accepter_passager_pour_trajet(...)` :
     - on vérifie la place disponible ;
     - on appelle la logique d'acceptation du `Trajet` (ajoute dans `passagersAcceptes`, retire des demandes) ;
     - on décrémente les places du `Conducteur` ;
     - on marque le `Passager` comme *non* en recherche (`passager.setChercheCovoit(false)`) — ainsi il devient « Réservé » ;
     - on met à jour les mappings et l'historique.
   - L'UI notifie et rafraîchit toutes les vues pour afficher le nouveau statut.

3) Finir un trajet (nouveau bouton) :
   - Le conducteur sélectionne un trajet et clique sur "Finir Trajet".
   - `EnhancedDriverPanel.finishSelectedTrajet()` : met le trajet en `STATUS_FINISHED`, remet `chercheCovoit = true` pour tous les passagers acceptés et restaure les places du conducteur (par `t.getMaxPlaces()` ou par calcul), puis appelle `mainFrame.notifyDataChanged()`.
   - Les trajets finis sont exclus des listes de recherche côté passager (logique `!t.isFinished()` dans les panels passagers).

Sauvegarde et auto-save
-----------------------
- `MainFrame` instancie le `CSVDatabase` via `loadDataFromCSV()` au démarrage.
- L'application effectue un auto-save périodique (toutes les 5 minutes) si `hasUnsavedChanges` est vrai.
- A la fermeture l'application propose de sauvegarder (via une boîte de dialogue) et crée une sauvegarde dans `data/backups/`.

Fichiers de données (CSV)
-------------------------
- `data/conducteurs.csv` : conducteurs
- `data/passagers.csv` : passagers
- `data/trajets.csv` : trajets
- `data/backups/` : copies horodatées de ces fichiers créées avant chaque sauvegarde

Comment lancer l'application
---------------------------
1) Sur Windows : double-cliquez `run.bat` à la racine du projet (ou exécutez-le depuis PowerShell) :

   .\run.bat

2) Depuis un IDE :
   - Ouvrez le projet dans NetBeans / IntelliJ / Eclipse.
   - Définissez la classe de démarrage `MainFrame` ou `App.App` selon votre configuration et lancez l'application.

3) Build avec Ant :
   - `ant` (si vous avez Ant installé) utilise `build.xml`. Le plus simple reste `run.bat`.

Conseils de debug pour débutant
-------------------------------
- Erreur de compilation "illegal start of expression": souvent due à un caractère en trop (ex : un '+' isolé) ou à des accolades mal appariées. Regardez la ligne indiquée et quelques lignes au‑dessus/au‑dessous.
- Si une vue n’affiche pas les données attendues : vérifiez que la méthode `refresh...()` du panel est appelée après la modification du modèle. Recherchez les appels `notifyDataChanged()` dans `MainFrame`.
- Pour tracer le flux, ajoutez des `System.out.println(...)` temporaires dans `Gestion_covoiturage` et dans les panels GUI pour vérifier que les méthodes sont exécutées.

Bonnes pratiques et petites améliorations possibles
-------------------------------------------------
- Centraliser l'initialisation de `maxPlaces` pour chaque trajet afin que la restauration des places soit fiable.
- Remplacer l'utilisation de reflection (qui synchronise les index dans `Gestion_covoiturage`) par des setters publics dans `Gestion_covoiturage` (ex : `setIndex_passager(int)`), plus propre.
- Ajouter des tests unitaires pour la logique métier (accepter passager, finir trajet, ajout de demande) afin d'éviter les régressions lors des modifications.
- Remonter les warnings (imports inutilisés, variables finales) pour garder le code propre et éviter la confusion.

Points d'entrée utiles (liste de classes/clés)
----------------------------------------------
- `MainFrame` : fenêtre principale, crée `Gestion_covoiturage`, panels et coordonne la navigation/refresh.
- `Gestion_covoiturage` : logique métier et stockage en mémoire des Users/Trajets/demandes.
- `CSVDatabase` : lecture / écriture CSV + backups.
- `Models/*.java` : `User`, `Passager`, `Conducteur`, `Trajet` (les entités centrales).
- `GUI/EnhancedDriverPanel.java` : vue conducteur (gestion des trajets, acceptation, fin de trajet).
- `GUI/EnhancedPassengerPanel.java` : vue passager moderne (recherche, réserver, mes réservations).
- `App/App.java` ou `AppGUI.java` : utilitaires d'exécution (selon votre configuration).

Remerciements et suite
----------------------
Ce README vous donne une vue d'ensemble pour commencer à lire et modifier le code. Si vous voulez, je peux :
- ajouter un diagramme simple (texte) des classes principales ;
- générer un petit guide pas-à-pas pour ajouter une nouvelle fonctionnalité (ex : ajout d'un champ "note du conducteur");
- nettoyer les warnings et faire un petit refactor (remplacer reflection par setters publics dans `Gestion_covoiturage`).

Dites-moi quelle suite vous voulez et je l'implémente (par exemple, nettoyage + tests unitaires basiques ou ajout d'une notification pour le passager quand il devient "Réservé").

Bonne lecture et bon développement !

