package Services;

import Models.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe de gestion du système de covoiturage
 * @author ricko
 */
public class Gestion_covoiturage {
    private final List<User> users = new ArrayList<>();
    private final List<Trajet> trajets = new ArrayList<>();
    private final List<User> passagers_acceptes = new ArrayList<>();
    // demandes par conducteur : clé = CIN du conducteur, valeur = liste des CINs des passagers ayant demandé ce conducteur
    private final Map<String, List<String>> demandes_par_conducteur = new HashMap<>();
    // Notifications : clé = CIN du passager, valeur = liste des notifications
    private final Map<String, List<Notification>> notifications_par_passager = new HashMap<>();
    // Notifications conducteur : clé = CIN du conducteur, valeur = liste des notifications
    private final Map<String, List<Notification>> notifications_par_conducteur = new HashMap<>();

    // Getters
    public List<User> getUsers() { return users; }
    public List<Trajet> getTrajets() { return trajets; }
    public List<User> getPassagers_acceptes() { return passagers_acceptes; }
    public Map<String, List<Notification>> getNotificationsParPassager() { return notifications_par_passager; }
    public Map<String, List<Notification>> getNotificationsParConducteur() { return notifications_par_conducteur; }

    // Setters
    public void setUsers(List<User> newUsers) { this.users.clear(); this.users.addAll(newUsers); }
    public void setTrajets(List<Trajet> newTrajets) { this.trajets.clear(); this.trajets.addAll(newTrajets); }

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

    // ===== API utilisée par l'UI (gestion des demandes / acceptations) =====

    /**
     * Ajouter une demande pour un conducteur
     */
    public void ajouter_demande_pour_conducteur(String cinConducteur, String cinPassager) {
        List<String> demandes = demandes_par_conducteur.computeIfAbsent(cinConducteur, k -> new ArrayList<>());
        if (!demandes.contains(cinPassager)) {
            demandes.add(cinPassager);
        }
    }

    /**
     * Supprimer une demande pour un conducteur
     */
    public void supprimer_demande_pour_conducteur(String cinConducteur, String cinPassager) {
        List<String> demandes = demandes_par_conducteur.get(cinConducteur);
        if (demandes != null) {
            demandes.removeIf(s -> s.equalsIgnoreCase(cinPassager));
            if (demandes.isEmpty()) {
                demandes_par_conducteur.remove(cinConducteur);
            }
        }
    }

    /**
     * Ajouter une demande pour un trajet spécifique
     */
    public boolean ajouter_demande_pour_trajet(Trajet t, String cinPassager) {
        if (t == null || cinPassager == null || cinPassager.trim().isEmpty()) return false;
        Passager p = rechercher_passager(cinPassager);
        if (p == null) return false;

        // Ajouter dans la liste du trajet
        boolean added = t.addDemand(p);
        // Mettre à jour mapping demandes_par_conducteur pour affichage rapide
        if (t.getConducteur() != null) {
            List<String> demandes = demandes_par_conducteur.computeIfAbsent(t.getConducteur().getCin(), k -> new ArrayList<>());
            if (!demandes.contains(cinPassager)) demandes.add(cinPassager);
            
            // Créer une notification pour le conducteur
            String trajetId = t.getConducteur().getCin() + "_" + t.getDepartTrajet() + "_" + t.getArriveeTrajet();
            creerNotificationNouvelleDemande(t.getConducteur().getCin(), cinPassager, trajetId, t);
        }
        return added;
    }

    /**
     * Accepter un passager pour un trajet
     * Retourne true si l'acceptation a réussi.
     */
    public boolean accepter_passager_pour_trajet(Trajet t, String cinPassager) {
        if (t == null || cinPassager == null || cinPassager.trim().isEmpty()) return false;
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
        List<String> demandes = demandes_par_conducteur.get(conducteur.getCin());
        if (demandes != null) demandes.remove(cinPassager);

        // Ajouter à historique global
        passagers_acceptes.add(p);

        // Mise à jour statut et validité
        t.setTrajet_valide(true);
        if (!t.getPassagersAcceptes().isEmpty()) t.setStatusTrajet(Trajet.STATUS_IN_PROGRESS);

        // Créer et ajouter une notification ACCEPTATION pour le passager
        String trajetId = t.getConducteur().getCin() + "_" + t.getDepartTrajet() + "_" + t.getArriveeTrajet();
        creerNotificationAcceptation(cinPassager, t.getConducteur().getCin(), trajetId, t);

        return true;
    }

    /**
     * Créer une notification d'acceptation pour un passager
     */
    public void creerNotificationAcceptation(String cinPassager, String cinConducteur, String trajetId, Trajet trajet) {
        String notificationId = "NOTIF_" + System.currentTimeMillis() + "_" + cinPassager;
        
        // Récupérer le conducteur pour obtenir son nom et prénom
        Conducteur conducteur = rechercher_conducteur(cinConducteur);
        String nomConducteur = (conducteur != null) ? conducteur.getNom() + " " + conducteur.getPrenom() : "Conducteur inconnu";
        
        String message = conducteur != null 
            ? "Accepté par " + nomConducteur + " pour le trajet " + trajet.getDepartTrajet() + " → " + trajet.getArriveeTrajet()
            : "Accepté pour le trajet " + trajet.getDepartTrajet() + " → " + trajet.getArriveeTrajet();
        
        Notification notif = new Notification(notificationId, cinPassager, cinConducteur, trajetId, "ACCEPTATION", message);
        
        // Ajouter à la map des notifications
        List<Notification> notifications = notifications_par_passager.computeIfAbsent(cinPassager, k -> new ArrayList<>());
        notifications.add(notif);
    }

    /**
     * Refuser une demande de passager pour un trajet
     * Supprime le passager de la liste des demandes et crée une notification
     * Retourne true si le refus a réussi.
     */
    public boolean refuser_passager_pour_trajet(Trajet t, String cinPassager) {
        if (t == null || cinPassager == null || cinPassager.trim().isEmpty()) return false;
        if (t.getConducteur() == null) return false;

        Passager p = rechercher_passager(cinPassager);
        if (p == null) return false;

        // Supprimer des demandes du trajet
        boolean removed = t.removeDemand(p);
        if (!removed) return false;

        // Mettre à jour mapping demandes_par_conducteur
        List<String> demandes = demandes_par_conducteur.get(t.getConducteur().getCin());
        if (demandes != null) {
            demandes.removeIf(s -> s.equalsIgnoreCase(cinPassager));
            if (demandes.isEmpty()) {
                demandes_par_conducteur.remove(t.getConducteur().getCin());
            }
        }

        // Créer et ajouter une notification REFUS pour le passager
        String trajetId = t.getConducteur().getCin() + "_" + t.getDepartTrajet() + "_" + t.getArriveeTrajet();
        creerNotificationRefus(cinPassager, t.getConducteur().getCin(), trajetId, t);

        // Remettre le passager en recherche de covoiturage s'il ne cherche plus
        p.setChercheCovoit(true);

        return true;
    }

    /**
     * Créer une notification de refus pour un passager
     */
    public void creerNotificationRefus(String cinPassager, String cinConducteur, String trajetId, Trajet trajet) {
        String notificationId = "NOTIF_" + System.currentTimeMillis() + "_" + cinPassager;
        
        // Récupérer le conducteur pour obtenir son nom et prénom
        Conducteur conducteur = rechercher_conducteur(cinConducteur);
        String nomConducteur = (conducteur != null) ? conducteur.getNom() + " " + conducteur.getPrenom() : "Conducteur inconnu";
        
        String message = conducteur != null 
            ? "Refusé par " + nomConducteur + " pour le trajet " + trajet.getDepartTrajet() + " → " + trajet.getArriveeTrajet()
            : "Refusé pour le trajet " + trajet.getDepartTrajet() + " → " + trajet.getArriveeTrajet();
        
        Notification notif = new Notification(notificationId, cinPassager, cinConducteur, trajetId, "REFUS", message);
        
        // Ajouter à la map des notifications
        List<Notification> notifications = notifications_par_passager.computeIfAbsent(cinPassager, k -> new ArrayList<>());
        notifications.add(notif);
    }

    /**
     * Obtenir les 10 dernières notifications pour un passager
     */
    public List<Notification> getDernieresNotifications(String cinPassager, int limite) {
        List<Notification> allNotifs = new ArrayList<>(notifications_par_passager.getOrDefault(cinPassager, new ArrayList<>()));
        
        // Trier par date décroissante (les plus récentes en premier)
        allNotifs.sort((n1, n2) -> n2.getDateCreation().compareTo(n1.getDateCreation()));
        
        // Retourner les N dernières
        List<Notification> derniers = new ArrayList<>();
        int count = Math.min(limite, allNotifs.size());
        for (int i = 0; i < count; i++) {
            derniers.add(allNotifs.get(i));
        }
        return derniers;
    }

    /**
     * Compter les notifications non lues pour un passager
     */
    public int compterNotificationsNonLues(String cinPassager) {
        List<Notification> notifs = notifications_par_passager.getOrDefault(cinPassager, new ArrayList<>());
        return (int) notifs.stream().filter(n -> !n.isEstLue()).count();
    }

    /**
     * Marquer une notification comme lue
     */
    public void marquerCommelue(String cinPassager, String notificationId) {
        List<Notification> notifs = notifications_par_passager.getOrDefault(cinPassager, new ArrayList<>());
        for (Notification n : notifs) {
            if (n.getNotificationId().equals(notificationId)) {
                n.setEstLue(true);
                break;
            }
        }
    }

    /**
     * Marquer toutes les notifications comme lues pour un passager
     */
    public void marquerToutesCommelues(String cinPassager) {
        List<Notification> notifs = notifications_par_passager.getOrDefault(cinPassager, new ArrayList<>());
        for (Notification n : notifs) {
            n.setEstLue(true);
        }
    }

    /**
     * Ajouter une notification existante (utilisé lors du chargement du CSV)
     */
    public void ajouterNotification(Notification notification) {
        List<Notification> notifs = notifications_par_passager.computeIfAbsent(notification.getPassagerId(), k -> new ArrayList<>());
        notifs.add(notification);
    }

    /**
     * Supprimer un passager accepté d'un trajet
     * - Retire le passager de la liste des acceptés
     * - Augmente les places disponibles du conducteur
     * - Remet le passager en recherche de covoiturage
     * - Crée une notification de suppression
     * - Met à jour l'historique des passagers acceptés
     * Retourne true si la suppression a réussi
     */
    public boolean supprimer_passager_accepte(Trajet t, String cinPassager, String cinConducteur) {
        if (t == null || cinPassager == null || cinPassager.trim().isEmpty()) return false;
        if (cinConducteur == null || cinConducteur.trim().isEmpty()) return false;

        Passager p = rechercher_passager(cinPassager);
        if (p == null) return false;

        // Vérifier que le conducteur est le propriétaire du trajet
        if (t.getConducteur() == null || !t.getConducteur().getCin().equals(cinConducteur)) {
            return false;
        }

        // Retirer le passager de la liste des acceptés du trajet
        boolean removed = t.removeAccepted(p);
        if (!removed) return false;

        // Augmenter les places disponibles du conducteur
        try {
            Conducteur conducteur = t.getConducteur();
            if (conducteur != null) {
                conducteur.setPlacesDisponibles(conducteur.getPlacesDisponibles() + 1);
            }
        } catch (Exception ignored) {}

        // Retirer l'historique global des passagers acceptés (si applicable)
        passagers_acceptes.removeIf(user -> 
            user instanceof Passager && ((Passager) user).getCin().equals(cinPassager)
        );

        // Remettre le passager en recherche de covoiturage
        try {
            p.setChercheCovoit(true);
        } catch (Exception ignored) {}

        // Créer et ajouter une notification SUPPRESSION pour le passager
        String trajetId = t.getConducteur().getCin() + "_" + t.getDepartTrajet() + "_" + t.getArriveeTrajet();
        creerNotificationSuppression(cinPassager, cinConducteur, trajetId, t);

        return true;
    }

    /**
     * Créer une notification de suppression pour un passager
     * La notification contient le nom et prénom du conducteur
     */
    public void creerNotificationSuppression(String cinPassager, String cinConducteur, String trajetId, Trajet trajet) {
        String notificationId = "NOTIF_" + System.currentTimeMillis() + "_" + cinPassager;
        
        // Récupérer le conducteur pour obtenir son nom et prénom
        Conducteur conducteur = rechercher_conducteur(cinConducteur);
        String nomConducteur = (conducteur != null) ? conducteur.getNom() + " " + conducteur.getPrenom() : "Conducteur inconnu";
        
        String message = conducteur != null 
            ? "Supprimé du trajet par " + nomConducteur + " (" + trajet.getDepartTrajet() + " → " + trajet.getArriveeTrajet() + ")"
            : "Supprimé du trajet " + trajet.getDepartTrajet() + " → " + trajet.getArriveeTrajet();
        
        Notification notif = new Notification(notificationId, cinPassager, cinConducteur, trajetId, "SUPPRESSION", message);
        
        // Ajouter à la map des notifications
        List<Notification> notifications = notifications_par_passager.computeIfAbsent(cinPassager, k -> new ArrayList<>());
        notifications.add(notif);
    }

    // ==================== NOTIFICATIONS CONDUCTEUR ====================

    /**
     * Obtenir les dernières notifications pour un conducteur
     */
    public List<Notification> getDernieresNotificationsConducteur(String cinConducteur, int limite) {
        List<Notification> allNotifs = new ArrayList<>(notifications_par_conducteur.getOrDefault(cinConducteur, new ArrayList<>()));
        
        // Trier par date décroissante (les plus récentes en premier)
        allNotifs.sort((n1, n2) -> n2.getDateCreation().compareTo(n1.getDateCreation()));
        
        // Retourner les N dernières
        List<Notification> derniers = new ArrayList<>();
        int count = Math.min(limite, allNotifs.size());
        for (int i = 0; i < count; i++) {
            derniers.add(allNotifs.get(i));
        }
        return derniers;
    }

    /**
     * Compter les notifications non lues pour un conducteur
     */
    public int compterNotificationsNonLuesConducteur(String cinConducteur) {
        List<Notification> notifs = notifications_par_conducteur.getOrDefault(cinConducteur, new ArrayList<>());
        return (int) notifs.stream().filter(n -> !n.isEstLue()).count();
    }

    /**
     * Marquer une notification comme lue pour un conducteur
     */
    public void marquerCommelueConducteur(String cinConducteur, String notificationId) {
        List<Notification> notifs = notifications_par_conducteur.getOrDefault(cinConducteur, new ArrayList<>());
        for (Notification n : notifs) {
            if (n.getNotificationId().equals(notificationId)) {
                n.setEstLue(true);
                break;
            }
        }
    }

    /**
     * Marquer toutes les notifications comme lues pour un conducteur
     */
    public void marquerToutesCommelueConducteur(String cinConducteur) {
        List<Notification> notifs = notifications_par_conducteur.getOrDefault(cinConducteur, new ArrayList<>());
        for (Notification n : notifs) {
            n.setEstLue(true);
        }
    }

    /**
     * Créer une notification de nouvelle demande pour le conducteur
     */
    public void creerNotificationNouvelleDemande(String cinConducteur, String cinPassager, String trajetId, Trajet trajet) {
        String notificationId = "NOTIF_" + System.currentTimeMillis() + "_" + cinConducteur;
        
        // Récupérer le passager pour obtenir son nom et prénom
        Passager passager = rechercher_passager(cinPassager);
        String nomPassager = (passager != null) ? passager.getNom() + " " + passager.getPrenom() : "Passager inconnu";
        
        String message = passager != null 
            ? "Nouvelle demande de " + nomPassager + " pour le trajet " + trajet.getDepartTrajet() + " → " + trajet.getArriveeTrajet()
            : "Nouvelle demande pour le trajet " + trajet.getDepartTrajet() + " → " + trajet.getArriveeTrajet();
        
        Notification notif = new Notification(notificationId, cinConducteur, cinPassager, trajetId, "DEMANDE", message);
        
        // Ajouter à la map des notifications
        List<Notification> notifications = notifications_par_conducteur.computeIfAbsent(cinConducteur, k -> new ArrayList<>());
        notifications.add(notif);
    }

    /**
     * Ajouter une notification existante pour un conducteur (utilisé lors du chargement du CSV)
     */
    public void ajouterNotificationConducteur(Notification notification) {
        List<Notification> notifs = notifications_par_conducteur.computeIfAbsent(notification.getPassagerId(), k -> new ArrayList<>());
        notifs.add(notification);
    }
}
