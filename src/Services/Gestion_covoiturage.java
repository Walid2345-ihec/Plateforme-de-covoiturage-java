package Services;

import Models.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

/**
 * Classe de gestion du système de covoiturage
 * @author ricko
 */
public class Gestion_covoiturage {
    private int Index_trajet_conducteur = -1;
    private int Index_conducteur = -1;
    private int Index_passager = -1;
    private Vector<User> users = new Vector<>();
    private Vector<Trajet> trajets = new Vector<>();
    private Vector<User> passagers_acceptes = new Vector<>();
    // demandes par conducteur : clé = CIN du conducteur, valeur = liste des CINs des passagers ayant demandé ce conducteur
    private Map<String, Vector<String>> demandes_par_conducteur = new HashMap<>();

    // Getters
    public Vector<User> getUsers() { return users; }
    public Vector<Trajet> getTrajets() { return trajets; }
    public Vector<User> getPassagers_acceptes() { return passagers_acceptes; }

    // Setters
    public void setUsers(Vector<User> users) { this.users = users; }
    public void setTrajets(Vector<Trajet> trajets) { this.trajets = trajets; }

    /**
     * Recherche un utilisateur par son CIN
     */
    public User rechercher_user(String ref) {
        for (User U : users) {
            if (U.getCin().equalsIgnoreCase(ref)) {
                return U;
            }
        }
        return null;
    }

    /**
     * Recherche un conducteur par son CIN
     */
    public Conducteur rechercher_conducteur(String cin) {
        User user = rechercher_user(cin);
        if (user != null && user instanceof Conducteur) {
            return (Conducteur) user;
        }
        return null;
    }

    /**
     * Recherche un passager par son CIN
     */
    public Passager rechercher_passager(String cin) {
        User user = rechercher_user(cin);
        if (user != null && user instanceof Passager) {
            return (Passager) user;
        }
        return null;
    }

    /**
     * Recherche un trajet par ses critères (depart+arrivee)
     */
    public Trajet rechercher_trajet(String depart, String arrivee) {
        for (Trajet t : trajets) {
            if (t.getDepartTrajet().equalsIgnoreCase(depart) && 
                t.getArriveeTrajet().equalsIgnoreCase(arrivee)) {
                return t;
            }
        }
        return null;
    }

    // ==================== NOUVEAUTÉS : CONNEXION ====================
    /**
     * Permet à un conducteur de se connecter en entrant un CIN existant.
     * Met à jour Index_conducteur et Index_trajet_conducteur pour pointer
     * vers le conducteur connecté et vers son (premier) trajet s'il existe.
     * Retourne true si connexion réussie, false si annulée ou impossible.
     */
    public boolean se_connecter_conducteur() {
        if (users.isEmpty()) {
            System.out.println("Aucun utilisateur inscrit. Créez d'abord un compte conducteur.");
            return false;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("\n=== CONNEXION CONDUCTEUR ===");
        String cin;
        Conducteur c = null;
        int idx = -1;

        do {
            System.out.println("Entrez votre CIN (ou tapez 'annuler' pour revenir) :");
            cin = sc.nextLine().trim();
            if (cin.equalsIgnoreCase("annuler")) {
                System.out.println("Connexion annulée.");
                return false;
            }

            // Recherche de l'utilisateur et vérification du type
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                if (u.getCin().equalsIgnoreCase(cin)) {
                    if (u instanceof Conducteur) {
                        c = (Conducteur) u;
                        idx = i;
                        break;
                    } else {
                        System.out.println("Le CIN fourni n'appartient pas à un conducteur. Réessayez.");
                        c = null;
                        break;
                    }
                }
            }

            if (c == null && idx == -1) {
                System.out.println("Aucun conducteur trouvé avec ce CIN. Réessayez.");
            }
        } while (c == null);

        // Mise à jour des index
        this.Index_conducteur = idx;

        // Chercher le premier trajet appartenant à ce conducteur
        this.Index_trajet_conducteur = -1;
        for (int i = 0; i < trajets.size(); i++) {
            Trajet t = trajets.get(i);
            if (t.getConducteur() != null && t.getConducteur().getCin().equalsIgnoreCase(c.getCin())) {
                this.Index_trajet_conducteur = i;
                break;
            }
        }

        System.out.println("✓ Connexion réussie en tant que conducteur : " + c.getNom() + " " + c.getPrenom());
        if (Index_trajet_conducteur != -1) {
            System.out.println("Votre trajet courant a été sélectionné (index trajet = " + Index_trajet_conducteur + ").");
        } else {
            System.out.println("Vous n'avez pas encore de trajet. Créez-en un depuis le menu conducteur.");
        }
        return true;
    }

    /**
     * Permet à un passager de se connecter en entrant un CIN existant.
     * Met à jour Index_passager pour pointer vers le passager connecté.
     * Retourne true si connexion réussie, false si annulée ou impossible.
     */
    public boolean se_connecter_passager() {
        if (users.isEmpty()) {
            System.out.println("Aucun utilisateur inscrit. Créez d'abord un compte passager.");
            return false;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("\n=== CONNEXION PASSAGER ===");
        String cin;
        Passager p = null;
        int idx = -1;

        do {
            System.out.println("Entrez votre CIN (ou tapez 'annuler' pour revenir) :");
            cin = sc.nextLine().trim();
            if (cin.equalsIgnoreCase("annuler")) {
                System.out.println("Connexion annulée.");
                return false;
            }

            // Recherche de l'utilisateur et vérification du type
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                if (u.getCin().equalsIgnoreCase(cin)) {
                    if (u instanceof Passager) {
                        p = (Passager) u;
                        idx = i;
                        break;
                    } else {
                        System.out.println("Le CIN fourni n'appartient pas à un passager. Réessayez.");
                        p = null;
                        break;
                    }
                }
            }

            if (p == null && idx == -1) {
                System.out.println("Aucun passager trouvé avec ce CIN. Réessayez.");
            }
        } while (p == null);

        // Mise à jour de l'index passager
        this.Index_passager = idx;

        System.out.println("✓ Connexion réussie en tant que passager : " + p.getNom() + " " + p.getPrenom());
        return true;
    }

    // ==================== MENU PRINCIPAL ====================
    
    /**
     * 1) Proposer trajet --> Saisie conducteur
     */
    public void Proposer_trajet() {
        String Cin;
        Scanner sc = new Scanner(System.in);
        System.out.println("=== PROPOSER UN TRAJET - SAISIE CONDUCTEUR ===");
        
        do {
            System.out.println("Entrez votre CIN :");
            Cin = sc.nextLine();
            if (rechercher_user(Cin) != null) {
                System.out.println("Cet utilisateur existe déjà !");
            }
        } while (rechercher_user(Cin) != null);
        
        Conducteur nouveauConducteur = new Conducteur(Cin);
        this.users.add(nouveauConducteur);
        this.Index_conducteur = users.size() - 1;
        System.out.println("✓ Conducteur ajouté avec succès !");
        System.out.println("Vous pouvez maintenant créer votre trajet dans le menu conducteur.");
    }
    
    /**
     * 2) Chercher trajet --> Saisie passager
     */
    public void Chercher_trajet() {
        String Cin;
        Scanner sc = new Scanner(System.in);
        System.out.println("=== CHERCHER UN TRAJET - SAISIE PASSAGER ===");
        
        do {
            System.out.println("Entrez votre CIN :");
            Cin = sc.nextLine();
            if (rechercher_user(Cin) != null) {
                System.out.println("Cet utilisateur existe déjà !");
            }
        } while (rechercher_user(Cin) != null);
        
        Passager nouveauPassager = new Passager(Cin);
        this.users.add(nouveauPassager);
        this.Index_passager = users.size() - 1;
        System.out.println("✓ Passager ajouté avec succès !");
        System.out.println("Vous pouvez maintenant consulter les trajets disponibles.");
    }
    
    /**
     * Afficher tous les conducteurs
     */
    public void afficher_conducteurs() {
        System.out.println("\n=== LISTE DES CONDUCTEURS ===");
        boolean trouve = false;
        for (int i = 0; i < users.size(); i++) {
            if (this.users.get(i) instanceof Conducteur) {
                System.out.println((i+1) + ") " + this.users.get(i).toString());
                trouve = true;
            }
        }
        if (!trouve) {
            System.out.println("Aucun conducteur enregistré.");
        }
    }
    
    /**
     * Afficher tous les passagers
     */
    public void afficher_passagers() {
        System.out.println("\n=== LISTE DES PASSAGERS ===");
        boolean trouve = false;
        for (int i = 0; i < users.size(); i++) {
            if (this.users.get(i) instanceof Passager) {
                System.out.println((i+1) + ") " + this.users.get(i).toString());
                trouve = true;
            }
        }
        if (!trouve) {
            System.out.println("Aucun passager enregistré.");
        }
    }
    
    // ==================== MENU CONDUCTEUR ====================
    
    /**
     * Voir les propositions/demandes des passagers (uniquement pour le conducteur connecté)
     */
    public void voir_propositions() {
        System.out.println("\n=== DEMANDES DES PASSAGERS (POUR VOUS) ===");
        if (Index_conducteur == -1 || !(users.get(Index_conducteur) instanceof Conducteur)) {
            System.out.println("Erreur: Vous devez être connecté en tant que conducteur pour voir vos propositions.");
            return;
        }

        Conducteur conducteurCourant = (Conducteur) users.get(Index_conducteur);
        Vector<String> demandes = demandes_par_conducteur.get(conducteurCourant.getCin());
        if (demandes == null || demandes.isEmpty()) {
            System.out.println("Aucune demande de passager pour vous pour le moment.");
            return;
        }
        
        for (String cin : demandes) {
            User passager = rechercher_user(cin);
            if (passager != null) {
                System.out.println("- " + passager.getNom() + " " + passager.getPrenom() + 
                                 " (CIN: " + passager.getCin() + ", Tel: " + passager.getTel() + ")");
            }
        }
    }
    
    /**
     * Voir les demandes (trajets voulus par les passagers)
     */
    public void voir_demandes() {
        System.out.println("\n=== TRAJETS RECHERCHÉS PAR LES PASSAGERS ===");
        if (this.trajets.isEmpty()) {
            System.out.println("Aucun trajet demandé.");
            return;
        }
        
        boolean trouve = false;
        for (Trajet t : trajets) {
            // Un trajet est considéré comme "demandé" si des passagers ont fait une demande
            if (!t.getPassagersDemandes().isEmpty() && t.getConducteur() == null) {
                System.out.println(t.toString());
                trouve = true;
            }
        }
        
        if (!trouve) {
            System.out.println("Aucune demande active de passager.");
        }
    }
    
    /**
     * Créer un trajet pour le conducteur
     */
    public void creer_trajet() {
        if (Index_conducteur == -1 || !(users.get(Index_conducteur) instanceof Conducteur)) {
            System.out.println("Erreur: Vous devez être connecté en tant que conducteur.");
            return;
        }
        
        System.out.println("\n=== CRÉATION D'UN NOUVEAU TRAJET ===");
        Trajet nouveauTrajet = new Trajet();
        
        // Assigner le conducteur au trajet
        Conducteur conducteur = (Conducteur) users.get(Index_conducteur);
        nouveauTrajet.setConducteur(conducteur);
        nouveauTrajet.setStatusTrajet("PENDING");
        
        this.trajets.add(nouveauTrajet);
        this.Index_trajet_conducteur = trajets.size() - 1;
        
        System.out.println("✓ Trajet créé avec succès !");
    }
    
    /**
     * Modifier le tarif du trajet
     */
    public void modifier_tarif() {
        if (Index_trajet_conducteur == -1) {
            System.out.println("Erreur: Aucun trajet sélectionné. Créez d'abord un trajet.");
            return;
        }
        
        float Prix;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n=== MODIFICATION DU TARIF ===");
        System.out.println("Prix actuel: " + trajets.get(Index_trajet_conducteur).getPrix() + " TND");
        System.out.println("Entrez le nouveau prix du trajet (TND) :");
        Prix = sc.nextFloat();
        sc.nextLine();
        
        Trajet modification = trajets.get(Index_trajet_conducteur);
        modification.setPrix(Prix);
        System.out.println("✓ Prix modifié avec succès ! Nouveau prix: " + Prix + " TND");
    }
    
    /**
     * Modifier les paramètres du trajet
     */
    public void modifier_parametres_trajet() {
        if (Index_trajet_conducteur == -1) {
            System.out.println("Erreur: Aucun trajet sélectionné.");
            return;
        }
        
        System.out.println("\n=== MODIFICATION DES PARAMÈTRES DU TRAJET ===");
        System.out.println("Création d'un nouveau trajet avec vos modifications...");
        
        Trajet ancienTrajet = trajets.get(Index_trajet_conducteur);
        Conducteur conducteur = ancienTrajet.getConducteur();
        
        Trajet nouveauTrajet = new Trajet();
        nouveauTrajet.setConducteur(conducteur);
        
        trajets.set(Index_trajet_conducteur, nouveauTrajet);
        System.out.println("✓ Trajet modifié avec succès !");
    }
    
    // ==================== GESTION DES DEMANDES/ACCEPTATIONS ====================
    /**
     * Notifier le conducteur avec vérification du CIN
     * La demande est désormais associée au conducteur ciblé.
     */
    public void notifier_conducteur() {
        if (Index_passager == -1 || !(users.get(Index_passager) instanceof Passager)) {
            System.out.println("Erreur: Vous devez être connecté en tant que passager.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("\n=== NOTIFIER UN CONDUCTEUR ===");

        // Afficher la liste des conducteurs disponibles
        afficher_conducteurs();

        String cinConducteur;
        Conducteur conducteurTrouve = null;

        // Boucle de validation du CIN
        do {
            System.out.println("\nEntrez le CIN du conducteur que vous souhaitez notifier :");
            cinConducteur = sc.nextLine();

            conducteurTrouve = rechercher_conducteur(cinConducteur);

            if (conducteurTrouve == null) {
                System.out.println("❌ Aucun conducteur trouvé avec le CIN : " + cinConducteur);
                System.out.println("Veuillez réessayer.");
            }
        } while (conducteurTrouve == null);

        // Ajouter la demande pour CE conducteur
        String cinPassager = users.get(Index_passager).getCin();
        Vector<String> demandes = demandes_par_conducteur.computeIfAbsent(conducteurTrouve.getCin(), k -> new Vector<>());
        if (!demandes.contains(cinPassager)) {
            demandes.add(cinPassager);
            System.out.println("✓ Votre demande a été envoyée au conducteur " +
                             conducteurTrouve.getNom() + " " + conducteurTrouve.getPrenom() + " !");
        } else {
            System.out.println("Vous avez déjà envoyé une demande à ce conducteur.");
        }
    }

    /**
     * Ajouter une demande pour un conducteur (utilisé par l'interface GUI)
     */
    public void ajouter_demande_pour_conducteur(String cinConducteur, String cinPassager) {
        Vector<String> demandes = demandes_par_conducteur.computeIfAbsent(cinConducteur, k -> new Vector<>());
        if (!demandes.contains(cinPassager)) {
            demandes.add(cinPassager);
        }
    }

    /**
     * Supprimer une demande pour un conducteur (utilisé par GUI)
     */
    public void supprimer_demande_pour_conducteur(String cinConducteur, String cinPassager) {
        Vector<String> demandes = demandes_par_conducteur.get(cinConducteur);
        if (demandes != null) {
            demandes.removeIf(s -> s.equalsIgnoreCase(cinPassager));
            if (demandes.isEmpty()) {
                demandes_par_conducteur.remove(cinConducteur);
            }
        }
    }

    /**
     * Ajouter une demande pour un trajet spécifique (utilisé par GUI/business logic)
     */
    public boolean ajouter_demande_pour_trajet(Trajet t, String cinPassager) {
        if (t == null || cinPassager == null || cinPassager.trim().isEmpty()) return false;
        Passager p = rechercher_passager(cinPassager);
        if (p == null) return false;

        // Ajouter dans la liste du trajet
        boolean added = t.addDemand(p);
        // Mettre à jour mapping demandes_par_conducteur pour affichage rapide
        if (t.getConducteur() != null) {
            Vector<String> demandes = demandes_par_conducteur.computeIfAbsent(t.getConducteur().getCin(), k -> new Vector<>());
            if (!demandes.contains(cinPassager)) demandes.add(cinPassager);
        }
        return added;
    }

    /**
     * Accepter un passager pour un trajet (utilisé par GUI ou console)
     * Retourne true si l'acceptation a réussi.
     */
    public boolean accepter_passager_pour_trajet(Trajet t, String cinPassager) {
        if (t == null || cinPassager == null || cinPassager.trim().isEmpty()) return false;
        // Removed dependency on Index_conducteur (console-oriented). Use the conducteur assigned to the trajet.
        if (t.getConducteur() == null) return false;

        Passager p = rechercher_passager(cinPassager);
        if (p == null) return false;

        // Use the conducteur associated with the trajet
        Conducteur conducteur = t.getConducteur();

        // Vérifier places disponibles au niveau du trajet
        if (t.getAvailablePlaces() <= 0) return false;

        // Essayer d'accepter via l'objet trajet
        boolean accepted = t.acceptPassenger(p);
        if (!accepted) return false;

        // Mark passenger as no longer searching -> reserved
        try {
            p.setChercheCovoit(false);
        } catch (Exception ignored) {}

        // Mettre à jour places du conducteur global
        try {
            conducteur.setPlacesDisponibles(Math.max(0, conducteur.getPlacesDisponibles() - 1));
        } catch (Exception ignored) {}

        // Retirer de mapping demandes_par_conducteur
        Vector<String> demandes = demandes_par_conducteur.get(conducteur.getCin());
        if (demandes != null) demandes.remove(cinPassager);

        // Ajouter à historique global
        passagers_acceptes.add(p);

        // Mise à jour statut et validité
        t.setTrajet_valide(true);
        if (!t.getPassagersAcceptes().isEmpty()) t.setStatusTrajet(Trajet.STATUS_IN_PROGRESS);

        return true;
    }

    /**
     * Version console interactive: accepter un passager (ancienne API adaptée)
     */
    public void accepter_passager() {
        if (Index_conducteur == -1 || !(users.get(Index_conducteur) instanceof Conducteur)) {
            System.out.println("Erreur: Vous devez être connecté en tant que conducteur.");
            return;
        }

        if (Index_trajet_conducteur == -1) {
            System.out.println("Erreur: Vous devez d'abord créer un trajet.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("\n=== ACCEPTER UN PASSAGER ===");

        // Afficher les demandes en attente pour ce conducteur
        voir_propositions();

        Passager passagerTrouve = null;
        String Cin;

        // Boucle de validation du CIN
        do {
            System.out.println("\nEntrez le CIN du passager à accepter :");
            Cin = sc.nextLine();

            passagerTrouve = rechercher_passager(Cin);

            if (passagerTrouve == null) {
                System.out.println("❌ Aucun passager trouvé avec le CIN : " + Cin);
                System.out.println("Veuillez réessayer.");
                continue;
            }

            // Vérifier que ce passager a bien demandé CE conducteur
            Conducteur conducteur = (Conducteur) users.get(Index_conducteur);
            Vector<String> demandes = demandes_par_conducteur.get(conducteur.getCin());
            if (demandes == null || !demandes.contains(Cin)) {
                System.out.println("❌ Ce passager n'a pas demandé votre trajet. Vous ne pouvez pas l'accepter.");
                passagerTrouve = null; // force la boucle à continuer
            }

        } while (passagerTrouve == null);

        // Récupérer le conducteur actuel
        Conducteur conducteur = (Conducteur) users.get(Index_conducteur);

        // Vérifier les places disponibles (global)
        if (conducteur.getPlacesDisponibles() <= 0) {
            System.out.println("Désolé, vous n'avez plus de places disponibles.");
            return;
        }

        // Récupérer le trajet courant
        Trajet trajet = trajets.get(Index_trajet_conducteur);

        // Essayer d'accepter via l'objet trajet
        boolean accepted = accepter_passager_pour_trajet(trajet, passagerTrouve.getCin());
        if (!accepted) {
            System.out.println("Impossible d'accepter le passager (place peut-être déjà prise).");
            return;
        }

        System.out.println("✓ Le passager " + passagerTrouve.getPrenom() + " " +
                         passagerTrouve.getNom() + " a été accepté pour votre trajet !");
        System.out.println("Places restantes : " + conducteur.getPlacesDisponibles());
    }
    
    /**
     * Afficher les contacts des passagers acceptés (pour le conducteur connecté)
     * Affiche uniquement les passagers associés aux trajets de ce conducteur.
     */
    public void contacts_passagers_acceptes() {
        System.out.println("\n=== CONTACTS DES PASSAGERS ACCEPTÉS ===");
        
        if (Index_conducteur == -1 || !(users.get(Index_conducteur) instanceof Conducteur)) {
            System.out.println("Erreur: Vous devez être connecté en tant que conducteur pour voir vos passagers acceptés.");
            return;
        }

        Conducteur conducteurCourant = (Conducteur) users.get(Index_conducteur);
        boolean any = false;

        for (Trajet t : trajets) {
            if (t.getConducteur() != null && t.getConducteur().getCin().equalsIgnoreCase(conducteurCourant.getCin())) {
                // Afficher tous les passagers acceptés pour ce trajet
                for (Passager p : t.getPassagersAcceptes()) {
                    any = true;
                    System.out.println("────────────────────────────────────");
                    System.out.println("Nom: " + p.getNom() + " " + p.getPrenom());
                    System.out.println("CIN: " + p.getCin());
                    System.out.println("Tél: " + p.getTel());
                    System.out.println("Email: " + p.getMail());
                    System.out.println("Adresse: " + p.getAdresse());
                }
            }
        }

        if (!any) {
            System.out.println("Aucun passager accepté pour le moment pour ce conducteur.");
            return;
        }
        System.out.println("────────────────────────────────────");
    }
    
    // ==================== MENU PASSAGER ====================
    
    /**
     * Voir les trajets disponibles (proposés par les conducteurs)
     */
    public void voir_trajets_disponibles() {
        System.out.println("\n=== TRAJETS DISPONIBLES ===");

        if (this.trajets.isEmpty()) {
            System.out.println("Aucun trajet disponible pour le moment.");
            return;
        }

        boolean trouve = false;
        int compteur = 1;
        for (Trajet t : trajets) {
            if (t.getConducteur() != null && 
                t.isPending() &&
                t.getAvailablePlaces() > 0) {
                System.out.println("\n" + compteur + ") " + t.toString());
                compteur++;
                trouve = true;
            }
        }

        if (!trouve) {
            System.out.println("Aucun trajet avec places disponibles.");
        }
    }
    
    /**
     * Confirmer le trajet (marquer comme validé)
     * Quand un passager confirme un trajet existant (avec conducteur), on ajoute
     * la demande pour CE conducteur (au lieu d'une liste globale).
     */
    public void confirmer_trajet() {
        if (Index_passager == -1) {
            System.out.println("Erreur: Vous devez être connecté en tant que passager.");
            return;
        }
        
        Scanner sc = new Scanner(System.in);
        System.out.println("\n=== CONFIRMER UN TRAJET ===");
        
        voir_trajets_disponibles();
        
        System.out.println("\nEntrez le point de départ du trajet à confirmer :");
        String depart = sc.nextLine();
        System.out.println("Entrez le point d'arrivée :");
        String arrivee = sc.nextLine();
        
        Trajet trajetTrouve = rechercher_trajet(depart, arrivee);
        
        if (trajetTrouve == null) {
            System.out.println("Trajet introuvable.");
            return;
        }
        
        if (trajetTrouve.getConducteur() == null) {
            System.out.println("Ce trajet n'a pas encore de conducteur assigné.");
            return;
        }
        
        // Marquer la demande comme en attente d'approbation par le conducteur
        trajetTrouve.setTrajet_valide(false);
        trajetTrouve.setStatusTrajet("PENDING_APPROVAL");
        
        // Notifier le conducteur : ajouter la demande pour CE conducteur
        String cinPassager = users.get(Index_passager).getCin();
        Conducteur conducteurDuTrajet = trajetTrouve.getConducteur();
        Vector<String> demandes = demandes_par_conducteur.computeIfAbsent(conducteurDuTrajet.getCin(), k -> new Vector<>());
        if (!demandes.contains(cinPassager)) {
            demandes.add(cinPassager);
        }
        
        System.out.println("✓ Trajet confirmé ! Le conducteur a été notifié de votre demande (en attente d'approbation).");
    }
    
    /**
     * Afficher tous les passagers (admin)
     */
    public void afficher_tous_passagers() {
        afficher_passagers();
    }
    
    /**
     * Afficher tous les conducteurs (admin)
     */
    public void afficher_tous_conducteurs() {
        afficher_conducteurs();
    }
}
