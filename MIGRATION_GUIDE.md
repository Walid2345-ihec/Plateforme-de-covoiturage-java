# Migration Java Swing vers Spring Boot

## Résumé
Le dossier contient une migration MVC Spring Boot du projet Swing avec MySQL, JPA, Thymeleaf, HTML/CSS et services métier.

## Architecture
- controller/: routes MVC.
- service/ et service/impl/: logique métier transactionnelle.
- repository/: Spring Data JPA.
- entity/: tables issues des CSV.
- dto/: formulaires.
- config/ et security/: configuration et session simple.
- templates/ et static/: frontend web équivalent aux écrans Swing.

## Base de données
Importer db/schema_import.sql dans MySQL. Le script crée la base covoiturage, les tables issues de data/*.csv et insère les lignes existantes.

## Lancement
1. Installer Java 17, Maven et MySQL.
2. Exécuter: mysql -u root -p < db/schema_import.sql
3. Ajuster src/main/resources/application.properties si votre mot de passe MySQL n'est pas vide.
4. Lancer: mvn spring-boot:run
5. Ouvrir http://localhost:8080

## Routes principales
- / login
- /passager/dashboard, /passager/trajets, /passager/reservations
- /conducteur/dashboard, /conducteur/trajets, /conducteur/demandes, /conducteur/trajets/new
- /admin/dashboard, /admin/users, /admin/trajets, /admin/evaluations, /admin/reclamations

## Adaptations
Les statuts PENDING, PENDING_APPROVAL, IN_PROGRESS et FINISHED sont conservés. Les colonnes AcceptedCINs et PendingCINs restent stockées au format CSV compatible avec le projet initial. Les mots de passe SHA-256 existants sont supportés, avec fallback texte brut comme dans le modèle Swing.
