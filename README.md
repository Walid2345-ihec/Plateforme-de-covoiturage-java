Projet réalisé par: Walid Saheb Ettaba / Hassen Achour / Ahmed Chermiti / Ahmed Zribi / Ela Slama
                    Etudiants en 2ème année BI à l'IHEC Carthage
# IHECovoit - Plateforme de covoiturage Spring Boot

IHECovoit est une application web de covoiturage universitaire developpee en Java avec Spring Boot. Elle permet aux passagers de rechercher et reserver des trajets, aux conducteurs de proposer et gerer leurs trajets, et a l'administrateur de superviser la plateforme.

Ce projet correspond a la version Spring Boot d'une ancienne application Java Swing. La persistance CSV et l'interface Swing ont ete remplacées par une architecture web MVC, des templates Thymeleaf, Spring Data JPA et une base MySQL.

## Fonctionnalites

### Passager

- inscription et connexion;
- recherche de trajets par depart et arrivee;
- demande de reservation;
- consultation et annulation des reservations;
- messagerie directe avec conducteur ou administrateur;
- participation aux groupes de discussion;
- reclamation contre un conducteur;
- evaluation d'un conducteur apres un trajet;
- consultation des notifications.

### Conducteur

- inscription et connexion;
- creation de trajets ponctuels;
- creation de trajets recurrents avec planning hebdomadaire;
- modification du prix d'un trajet;
- consultation des demandes de passagers;
- acceptation ou refus des demandes;
- retrait d'un passager accepte;
- terminaison ou suppression d'un trajet avec restauration des places disponibles en base;
- creation de groupes de discussion;
- messagerie directe;
- reclamation contre un passager;
- consultation des evaluations et notifications.

### Administrateur

- tableau de bord global;
- gestion des conducteurs et passagers;
- modification de carte utilisateur;
- suppression de comptes;
- suivi et suppression des trajets;
- consultation des evaluations;
- consultation des reclamations;
- notifications administrateur;
- conversations avec les utilisateurs.

## Stack technique

- Java 17
- Spring Boot 3.2.5
- Spring Web MVC
- Thymeleaf
- Spring Data JPA
- Hibernate
- MySQL
- Lombok
- HTML / CSS / JavaScript

## Structure du projet

```text
src/main/java/com/covoiturage
  CovoiturageApplication.java
  config/          Configuration Spring MVC
  controller/      Controleurs web Spring MVC
  dto/             Objets de formulaire
  entity/          Entites JPA
  repository/      Repositories Spring Data JPA
  security/        Utilitaires de session
  service/         Interfaces metier
  service/impl/    Implementations metier

src/main/resources
  application.properties
  static/css/style.css
  static/js/app.js
  templates/       Pages Thymeleaf

db/
  schema_import.sql
  README_IMPORT.md
```

## Architecture

L'application suit une architecture en couches:

```text
Navigateur
  -> Controller Spring MVC
  -> Service metier
  -> Repository Spring Data JPA
  -> MySQL
```

Les templates Thymeleaf recoivent les donnees depuis les controleurs et affichent les pages selon le role connecte.

## Notions POO utilisees

Le projet applique plusieurs notions de programmation orientee objet:

- MVC: les controleurs Spring MVC gerent les requetes, les services et entites forment le modele, et Thymeleaf represente la vue.
- Encapsulation: les entites regroupent leurs donnees et certains comportements utiles. Par exemple `Trajet` calcule ses places disponibles avec `getAvailablePlaces()`.
- Abstraction: les controleurs dependent d'interfaces comme `TrajetService`, `AuthService` ou `MessagingService`.
- Polymorphisme: Spring injecte les implementations concretes (`TrajetServiceImpl`, `AuthServiceImpl`, etc.) derriere les interfaces utilisees par les controleurs.
- Heritage: les repositories heritent de `JpaRepository`, ce qui fournit automatiquement les methodes CRUD.
- Classes abstraites: le projet n'utilise pas de classe abstraite metier explicite, mais il applique le principe d'abstraction via les interfaces. Une amelioration possible serait une classe abstraite `Utilisateur` pour factoriser les champs communs entre `Admin`, `Conducteur` et `Passager`.
- Composition: les services sont composes de repositories et d'autres services injectes par constructeur.

Cette organisation permet de passer de l'ancienne logique Swing vers une architecture web plus propre, maintenable et extensible.

## Architecture Spring Boot realisee

Le navigateur envoie les requetes aux controleurs. Les controleurs verifient la session et appellent les services. Les services appliquent les regles metier, puis utilisent les repositories pour lire ou modifier MySQL. Les resultats sont renvoyes aux templates Thymeleaf.

Les couches principales sont:

- `controller/`: routes web et redirections;
- `service/`: contrats metier;
- `service/impl/`: logique metier concrete;
- `repository/`: acces aux donnees avec Spring Data JPA;
- `entity/`: mapping objet/table MySQL;
- `dto/`: donnees de formulaire;
- `templates/`: vues HTML Thymeleaf.

## Base de donnees MySQL

La base MySQL remplace les fichiers CSV de la version Swing. Elle stocke les utilisateurs, trajets, reservations, messages, groupes, evaluations, reclamations et notifications.

Spring Boot est relie a MySQL via `application.properties`. Les entites utilisent JPA:

- `@Entity` pour declarer une classe persistante;
- `@Table` pour choisir la table;
- `@Id` pour la cle primaire;
- `@Column` pour les colonnes;
- `@GeneratedValue` pour les identifiants auto-generes.

Les repositories heritent de `JpaRepository`, donc un appel Java comme `save(trajet)` devient une operation SQL executee par Hibernate.

## Configuration

Le fichier principal de configuration est:

```text
src/main/resources/application.properties
```

Configuration par defaut:

```properties
server.port=9000
spring.datasource.url=jdbc:mysql://localhost:3306/covoiturage?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.thymeleaf.cache=false
```

Adaptez `username`, `password` et l'URL MySQL selon votre environnement.

## Prerequis

- JDK 17 ou plus
- Maven
- MySQL
- Une base de donnees nommee `covoiturage`

## Installation de la base

Creer la base:

```sql
CREATE DATABASE covoiturage CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Importer le jeu de donnees fourni:

```bash
mysql -u root -p covoiturage < db/schema_import.sql
```

Selon le contenu exact du script, vous pouvez aussi utiliser:

```bash
mysql -u root -p < db/schema_import.sql
```

## Lancement du projet

Depuis la racine:

```bash
mvn spring-boot:run
```

Puis ouvrir:

```text
http://localhost:9000
```

## Build

```bash
mvn clean package
```

Execution du JAR:

```bash
java -jar target/covoiturage-projet-springboot-1.0.0.jar
```

## Pages principales

```text
/                         Connexion
/register/passager        Inscription passager
/register/conducteur      Inscription conducteur
/passager/dashboard       Dashboard passager
/passager/trajets         Recherche trajets
/passager/reservations    Reservations passager
/conducteur/dashboard     Dashboard conducteur
/conducteur/trajets       Trajets conducteur
/conducteur/demandes      Demandes recues
/admin/dashboard          Dashboard admin
/admin/users              Gestion utilisateurs
/admin/trajets            Gestion trajets
/messages                 Messagerie
/groups                   Groupes
```

## Fonctionnement metier important

### Reservation

1. Le passager demande une reservation.
2. Son CIN est ajoute dans `pendingCins`.
3. Le conducteur accepte ou refuse.
4. En cas d'acceptation, le CIN passe dans `acceptedCins`.
5. Les places disponibles du conducteur sont diminuees en base.
6. Une notification est envoyee au passager.

### Fin ou suppression de trajet

Lorsqu'un conducteur termine ou supprime un trajet, le service restaure dans la base les places correspondant au nombre de passagers acceptes. Cela met a jour `conducteurs.places_disponibles`, donc le dashboard conducteur affiche ensuite la bonne valeur.

### Messages

Les messages sont supprimes logiquement: ils restent en base mais leur contenu devient `Message supprime`.

## Documentation supplementaire

Le fichier suivant explique le projet en detail:

```text
explication.md
```

Un rapport Spring Boot est aussi fourni:

```text
rapport_springboot.md
rapport_springboot.pdf
```

## Ameliorations possibles

- integration de Spring Security;
- hashage BCrypt;
- modelisation relationnelle des reservations au lieu de champs CSV;
- tests unitaires et tests d'integration;
- pagination admin;
- recherche par date;
- API REST;
- suivi GPS et bouton SOS;
- CI/CD.

## Auteurs

Projet universitaire IHEC - Plateforme de covoiturage.

Version actuelle: migration Spring Boot de l'application Java Swing initiale.

