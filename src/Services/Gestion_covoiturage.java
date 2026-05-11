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
    // Notifications administrateur
    private final List<Notification> adminNotifications = new ArrayList<>();
    // Groupes de discussion
    private final List<Group> groups = new ArrayList<>();
    // Messages de groupe (tous groupes confondus, filtrés par groupId à l'usage)
    private final List<GroupMessage> groupMessages = new ArrayList<>();
    // Conversations utilisateur <-> administrateur
    private final List<Conversation> conversations = new ArrayList<>();
    // Évaluations passager → conducteur
    private final List<Evaluation> evaluations = new ArrayList<>();

    // Getters
    public List<User> getUsers() { return users; }
    public List<Trajet> getTrajets() { return trajets; }
    public List<User> getPassagers_acceptes() { return passagers_acceptes; }
    public Map<String, List<Notification>> getNotificationsParPassager() { return notifications_par_passager; }
    public Map<String, List<Notification>> getNotificationsParConducteur() { return notifications_par_conducteur; }

    public List<Notification> getAdminNotifications() {
        return new ArrayList<>(adminNotifications);
    }

    public List<Conversation> getConversations() {
        return new ArrayList<>(conversations);
    }

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

    /**
     * Recherche un administrateur par son CIN
     */
    public Admin rechercher_admin(String cin) {
        User user = rechercher_user(cin);
        if (user != null && user instanceof Admin) {
            return (Admin) user;
        }
        return null;
    }

    public Admin getDefaultAdmin() {
        for (User user : users) {
            if (user instanceof Admin) {
                return (Admin) user;
            }
        }
        return null;
    }

    /**
     * Récupère tous les utilisateurs du système
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Supprime un utilisateur du système par son CIN
     */
    public boolean supprimerUtilisateur(String cin) {
        return users.removeIf(u -> u.getCin().equalsIgnoreCase(cin));
    }

    /**
     * Récupère tous les trajets du système
     */
    public List<Trajet> getAllTrajets() {
        return new ArrayList<>(trajets);
    }

    /**
     * Supprime un trajet spécifique
     */
    public boolean supprimerTrajet(Trajet t) {
        if (t == null) return false;
        return trajets.remove(t);
    }

    /**
     * Récupère toutes les évaluations du système
     */
    public List<Evaluation> getAllEvaluations() {
        return new ArrayList<>(evaluations);
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
     * Obtenir TOUTES les notifications pour un passager (sans limite)
     */
    public List<Notification> getToutesNotifications(String cinPassager) {
        List<Notification> allNotifs = new ArrayList<>(notifications_par_passager.getOrDefault(cinPassager, new ArrayList<>()));
        
        // Trier par date décroissante (les plus récentes en premier)
        allNotifs.sort((n1, n2) -> n2.getDateCreation().compareTo(n1.getDateCreation()));
        
        return allNotifs;
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

    /**
     * Ajoute une notification pour l'administrateur.
     */
    public void addAdminNotification(String message, String type) {
        String notificationId = "ADMIN_NOTIF_" + System.currentTimeMillis();
        Notification notif = new Notification(notificationId, "ADMIN", "ADMIN", "", type, message);

        adminNotifications.add(notif);
        CSVDatabase.saveAdminNotifications(adminNotifications);
    }

    /**
     * Demande d'aide envoyée à l'administrateur.
     */
    public void requestHelp(User user) {
        if (user == null) return;
        addAdminNotification("Aide demandée par " + user.getNom() + " " + user.getPrenom() + " (CIN: " + user.getCin() + ")", "HELP");
    }

    /**
     * Réclamation soumise à l'administrateur.
     */
    public void submitComplaint(User user, String detail) {
        if (user == null) return;
        addAdminNotification("Réclamation de " + user.getNom() + " " + user.getPrenom() + " : " + detail, "COMPLAINT");
    }

    /**
     * Ajouter une notification admin existante (chargement CSV).
     */
    public void ajouterNotificationAdmin(Notification notification) {
        adminNotifications.add(notification);
    }

    /**
     * Marquer une notification admin comme lue.
     */
    public void markAdminNotificationAsRead(String notificationId) {
        for (Notification n : adminNotifications) {
            if (n.getNotificationId().equals(notificationId)) {
                n.setEstLue(true);
                break;
            }
        }
        CSVDatabase.saveAdminNotifications(adminNotifications);
    }

    /**
     * Marquer toutes les notifications admin comme lues.
     */
    public void markAllAdminNotificationsAsRead() {
        for (Notification n : adminNotifications) {
            n.setEstLue(true);
        }
        CSVDatabase.saveAdminNotifications(adminNotifications);
    }

    /**
     * Compter les notifications admin non lues.
     */
    public int countUnreadAdminNotifications() {
        return (int) adminNotifications.stream().filter(n -> !n.isEstLue()).count();
    }

    /**
     * Supprimer de la liste la notification admin.
     */
    public void deleteAdminNotification(String notificationId) {
        adminNotifications.removeIf(n -> n.getNotificationId().equals(notificationId));
        CSVDatabase.saveAdminNotifications(adminNotifications);
    }

    // ==================== CONVERSATIONS ADMIN ====================

    public void ajouterConversation(Conversation conversation) {
        if (conversation == null) return;
        for (Conversation existing : conversations) {
            if (existing.getId().equals(conversation.getId())) return;
        }
        conversations.add(conversation);
    }

    public Conversation getOrCreateAdminConversation(String userId, String adminId, String triggeredBy) {
        if (userId == null || adminId == null || userId.isBlank() || adminId.isBlank()) {
            return null;
        }

        for (Conversation conversation : conversations) {
            if (conversation.getUserId().equals(userId) && conversation.getAdminId().equals(adminId)) {
                if (triggeredBy != null && !triggeredBy.isBlank()) {
                    conversation.setTriggeredBy(triggeredBy);
                }
                CSVDatabase.saveConversations(conversations);
                return conversation;
            }
        }

        String normalizedTrigger = (triggeredBy == null || triggeredBy.isBlank()) ? "help" : triggeredBy;
        Conversation created = new Conversation(
            "CONV_" + System.currentTimeMillis() + "_" + userId,
            userId,
            adminId,
            normalizedTrigger
        );
        conversations.add(created);
        CSVDatabase.saveConversations(conversations);
        return created;
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
     * Obtenir TOUTES les notifications pour un conducteur (sans limite)
     */
    public List<Notification> getToutesNotificationsConducteur(String cinConducteur) {
        List<Notification> allNotifs = new ArrayList<>(notifications_par_conducteur.getOrDefault(cinConducteur, new ArrayList<>()));
        
        // Trier par date décroissante (les plus récentes en premier)
        allNotifs.sort((n1, n2) -> n2.getDateCreation().compareTo(n1.getDateCreation()));
        
        return allNotifs;
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
     * Créer une notification d'annulation de réservation pour le conducteur
     * Envoyée lorsqu'un passager annule sa réservation
     */
    public void creerNotificationAnnulationReservation(String cinConducteur, String cinPassager, String trajetId, Trajet trajet) {
        String notificationId = "NOTIF_" + System.currentTimeMillis() + "_" + cinConducteur;
        
        // Récupérer le passager pour obtenir son nom et prénom
        Passager passager = rechercher_passager(cinPassager);
        String nomPassager = (passager != null) ? passager.getNom() + " " + passager.getPrenom() : "Passager inconnu";
        
        String message = passager != null 
            ? "Annulation de réservation par " + nomPassager + " pour le trajet " + trajet.getDepartTrajet() + " → " + trajet.getArriveeTrajet()
            : "Annulation de réservation pour le trajet " + trajet.getDepartTrajet() + " → " + trajet.getArriveeTrajet();
        
        Notification notif = new Notification(notificationId, cinConducteur, cinPassager, trajetId, "ANNULATION", message);
        
        // Ajouter à la map des notifications
        List<Notification> notifications = notifications_par_conducteur.computeIfAbsent(cinConducteur, k -> new ArrayList<>());
        notifications.add(notif);
    }

    /**
     * Ajouter une notification existante pour un conducteur (utilisé lors du chargement du CSV)
     */
    public void ajouterNotificationConducteur(Notification notification) {
        String conducteurCin = notification.getConducteurId();
        if (rechercher_conducteur(conducteurCin) == null
                && rechercher_conducteur(notification.getPassagerId()) != null) {
            conducteurCin = notification.getPassagerId();
        }
        List<Notification> notifs = notifications_par_conducteur.computeIfAbsent(conducteurCin, k -> new ArrayList<>());
        notifs.add(notification);
    }

    /**
     * Créer une notification de message provenant d'un conducteur pour un passager
     */
    public void creerNotificationMessageDuConducteur(String cinPassager, String cinConducteur, String messageContent) {
        String notificationId = "NOTIF_" + System.currentTimeMillis() + "_" + cinPassager;
        
        // Récupérer le conducteur pour obtenir son nom et prénom
        Conducteur conducteur = rechercher_conducteur(cinConducteur);
        String nomConducteur = (conducteur != null) ? conducteur.getNom() + " " + conducteur.getPrenom() : "Conducteur";
        
        String message = "📨 Message de " + nomConducteur + ": " + (messageContent.length() > 50 ? messageContent.substring(0, 50) + "..." : messageContent);
        
        Notification notif = new Notification(notificationId, cinPassager, cinConducteur, "", "MESSAGE", message);
        
        // Ajouter à la map des notifications du passager
        List<Notification> notifications = notifications_par_passager.computeIfAbsent(cinPassager, k -> new ArrayList<>());
        notifications.add(notif);
    }

    /**
     * Créer une notification de message provenant d'un passager pour un conducteur
     */
    public void creerNotificationMessageDuPassager(String cinConducteur, String cinPassager, String messageContent) {
        String notificationId = "NOTIF_" + System.currentTimeMillis() + "_" + cinConducteur;

        // Récupérer le passager pour obtenir son nom et prénom
        Passager passager = rechercher_passager(cinPassager);
        String nomPassager = (passager != null) ? passager.getNom() + " " + passager.getPrenom() : "Passager";

        String message = "📨 Message de " + nomPassager + ": " + (messageContent.length() > 50 ? messageContent.substring(0, 50) + "..." : messageContent);

        Notification notif = new Notification(notificationId, cinConducteur, cinPassager, "", "MESSAGE", message);

        // Ajouter à la map des notifications du conducteur
        List<Notification> notifications = notifications_par_conducteur.computeIfAbsent(cinConducteur, k -> new ArrayList<>());
        notifications.add(notif);
    }

    /**
     * Créer une notification lorsqu'un administrateur répond à un utilisateur.
     */
    public void creerNotificationMessageAdmin(String userCin, String adminCin, String messageContent) {
        if (userCin == null || userCin.isBlank()) return;

        String notificationId = "NOTIF_ADMIN_MSG_" + System.currentTimeMillis() + "_" + userCin;
        String preview = messageContent != null && messageContent.length() > 50
            ? messageContent.substring(0, 50) + "..."
            : (messageContent == null ? "" : messageContent);
        String message = "Message de l'administrateur: " + preview;

        Notification notif = new Notification(notificationId, userCin, adminCin, "", "MESSAGE", message);

        if (rechercher_passager(userCin) != null) {
            List<Notification> notifications = notifications_par_passager.computeIfAbsent(userCin, k -> new ArrayList<>());
            notifications.add(notif);
        } else if (rechercher_conducteur(userCin) != null) {
            List<Notification> notifications = notifications_par_conducteur.computeIfAbsent(userCin, k -> new ArrayList<>());
            notifications.add(notif);
        }
    }

    // ==================== GROUPES ====================

    public List<Group> getGroups() { return groups; }
    public List<GroupMessage> getGroupMessages() { return groupMessages; }

    /**
     * Ajoute un groupe existant (utilisé lors du chargement CSV).
     */
    public void ajouterGroupe(Group group) {
        if (group == null) return;
        for (Group g : groups) {
            if (g.getGroupId().equals(group.getGroupId())) return;
        }
        groups.add(group);
    }

    /**
     * Ajoute un message de groupe existant (utilisé lors du chargement CSV).
     */
    public void ajouterGroupMessage(GroupMessage message) {
        if (message == null) return;
        groupMessages.add(message);
    }

    /**
     * Crée un nouveau groupe de covoiturage. Le conducteur sélectionne les passagers.
     * Envoie automatiquement une notification d'appartenance à chaque passager membre.
     * Retourne le groupe créé, ou null en cas d'erreur.
     */
    public Group creerGroupe(String groupName, String conducteurCin, List<String> passagerCins) {
        if (groupName == null || groupName.trim().isEmpty()) return null;
        if (conducteurCin == null || conducteurCin.trim().isEmpty()) return null;
        if (passagerCins == null || passagerCins.isEmpty()) return null;

        String groupId = "GRP_" + System.currentTimeMillis() + "_" + conducteurCin;
        Group group = new Group(groupId, groupName.trim(), conducteurCin, passagerCins);
        groups.add(group);

        // Notifier chaque passager membre du groupe
        for (String passagerCin : passagerCins) {
            creerNotificationAppartenanceGroupe(passagerCin, conducteurCin, groupId, groupName.trim());
        }

        return group;
    }

    /**
     * Notification d'appartenance à un nouveau groupe pour un passager.
     */
    public void creerNotificationAppartenanceGroupe(String cinPassager, String cinConducteur,
                                                    String groupId, String groupName) {
        String notificationId = "NOTIF_" + System.currentTimeMillis() + "_" + cinPassager + "_GRP";
        Conducteur conducteur = rechercher_conducteur(cinConducteur);
        String nomConducteur = (conducteur != null)
                ? conducteur.getNom() + " " + conducteur.getPrenom()
                : "Conducteur inconnu";

        String message = "👥 Vous avez été ajouté au groupe « " + groupName + " » par " + nomConducteur;
        Notification notif = new Notification(notificationId, cinPassager, cinConducteur,
                groupId, "GROUPE", message);

        List<Notification> notifications = notifications_par_passager
                .computeIfAbsent(cinPassager, k -> new ArrayList<>());
        notifications.add(notif);
    }

    /**
     * Récupère tous les groupes auxquels un utilisateur appartient (en tant que conducteur ou passager).
     */
    public List<Group> getGroupesPourUtilisateur(String cin) {
        List<Group> result = new ArrayList<>();
        if (cin == null) return result;
        for (Group g : groups) {
            if (g.containsUser(cin)) result.add(g);
        }
        return result;
    }

    /**
     * Recherche un groupe par son identifiant.
     */
    public Group rechercher_groupe(String groupId) {
        if (groupId == null) return null;
        for (Group g : groups) {
            if (g.getGroupId().equals(groupId)) return g;
        }
        return null;
    }

    /**
     * Récupère tous les messages d'un groupe, triés par date croissante.
     */
    public List<GroupMessage> getMessagesPourGroupe(String groupId) {
        List<GroupMessage> result = new ArrayList<>();
        if (groupId == null) return result;
        for (GroupMessage m : groupMessages) {
            if (groupId.equals(m.getGroupId())) result.add(m);
        }
        result.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));
        return result;
    }

    /**
     * Envoie un message dans un groupe et notifie tous les autres membres.
     * Retourne le message créé.
     */
    public GroupMessage envoyerMessageDeGroupe(String groupId, String senderCin,
                                               String senderName, String content) {
        if (groupId == null || senderCin == null || content == null || content.trim().isEmpty()) {
            return null;
        }
        Group group = rechercher_groupe(groupId);
        if (group == null) return null;

        String messageId = "GMSG_" + System.currentTimeMillis() + "_" + senderCin;
        GroupMessage message = new GroupMessage(messageId, groupId, senderCin, senderName, content.trim());
        groupMessages.add(message);

        // Notifier les autres membres (passagers et conducteur)
        String previewContent = content.length() > 50 ? content.substring(0, 50) + "..." : content;
        notifierMembresGroupeMessage(group, senderCin, senderName, previewContent);

        return message;
    }

    /**
     * Notifie tous les membres d'un groupe (sauf l'expéditeur) qu'un message a été envoyé.
     */
    private void notifierMembresGroupeMessage(Group group, String senderCin, String senderName,
                                              String previewContent) {
        // Notifier le conducteur si ce n'est pas lui qui a envoyé
        if (group.getConducteurCin() != null && !group.getConducteurCin().equals(senderCin)) {
            String notifId = "NOTIF_" + System.currentTimeMillis() + "_" + group.getConducteurCin() + "_GRPMSG";
            String message = "💬 [" + group.getGroupName() + "] " + senderName + ": " + previewContent;
            Notification notif = new Notification(notifId, group.getConducteurCin(), senderCin,
                    group.getGroupId(), "MESSAGE_GROUPE", message);
            List<Notification> notifs = notifications_par_conducteur
                    .computeIfAbsent(group.getConducteurCin(), k -> new ArrayList<>());
            notifs.add(notif);
        }

        // Notifier chaque passager (sauf l'expéditeur)
        for (String memberCin : group.getMemberCins()) {
            if (memberCin.equals(senderCin)) continue;
            String notifId = "NOTIF_" + System.currentTimeMillis() + "_" + memberCin + "_GRPMSG";
            String message = "💬 [" + group.getGroupName() + "] " + senderName + ": " + previewContent;
            Notification notif = new Notification(notifId, memberCin, senderCin,
                    group.getGroupId(), "MESSAGE_GROUPE", message);
            List<Notification> notifs = notifications_par_passager
                    .computeIfAbsent(memberCin, k -> new ArrayList<>());
            notifs.add(notif);
        }
    }

    /**
     * Supprime (marque comme supprimé) un message de groupe.
     * Seul l'expéditeur peut supprimer son message. Le contenu est remplacé par "message supprimée".
     * Retourne true en cas de succès.
     */
    public boolean supprimerMessageDeGroupe(String messageId, String requesterCin) {
        if (messageId == null || requesterCin == null) return false;
        for (GroupMessage m : groupMessages) {
            if (m.getMessageId().equals(messageId)) {
                if (!m.getSenderCin().equals(requesterCin)) return false;
                m.delete();
                return true;
            }
        }
        return false;
    }

    // ==================== ÉVALUATIONS ====================

    public List<Evaluation> getEvaluations() { return evaluations; }

    /**
     * Ajoute une évaluation existante (utilisé lors du chargement CSV).
     */
    public void ajouterEvaluation(Evaluation evaluation) {
        if (evaluation == null) return;
        evaluations.add(evaluation);
    }

    /**
     * Crée une évaluation pour un conducteur. Met à jour la moyenne du conducteur
     * et envoie une notification au conducteur.
     * Retourne l'évaluation créée, ou null en cas d'erreur.
     */
    public Evaluation creerEvaluation(String passagerCin, String conducteurCin, String trajetId,
                                      int rating, String comment) {
        if (passagerCin == null || conducteurCin == null) return null;
        if (rating < 1 || rating > 5) return null;

        Passager passager = rechercher_passager(passagerCin);
        Conducteur conducteur = rechercher_conducteur(conducteurCin);
        if (passager == null || conducteur == null) return null;

        String passagerName = passager.getNom() + " " + passager.getPrenom();
        String evaluationId = "EVAL_" + System.currentTimeMillis() + "_" + passagerCin;

        Evaluation eval = new Evaluation(evaluationId, passagerCin, passagerName,
                conducteurCin, trajetId != null ? trajetId : "", rating,
                comment != null ? comment : "");
        evaluations.add(eval);

        // Mettre à jour la moyenne du conducteur
        recalculerMoyenneConducteur(conducteurCin);

        // Notification au conducteur
        creerNotificationEvaluation(conducteurCin, passagerCin, passagerName, rating, comment);

        return eval;
    }

    /**
     * Recalcule la moyenne d'évaluation d'un conducteur à partir de ses évaluations.
     */
    public void recalculerMoyenneConducteur(String conducteurCin) {
        Conducteur c = rechercher_conducteur(conducteurCin);
        if (c == null) return;

        double sum = 0;
        int count = 0;
        for (Evaluation e : evaluations) {
            if (e.getConducteurCin().equals(conducteurCin)) {
                sum += e.getRating();
                count++;
            }
        }
        c.setMoyenneEvaluation(count > 0 ? (sum / count) : 0.0);
    }

    /**
     * Recalcule la moyenne pour tous les conducteurs (appelé après chargement CSV).
     */
    public void recalculerToutesMoyennes() {
        for (User u : users) {
            if (u instanceof Conducteur c) {
                recalculerMoyenneConducteur(c.getCin());
            }
        }
    }

    /**
     * Récupère toutes les évaluations reçues par un conducteur, triées par date décroissante.
     */
    public List<Evaluation> getEvaluationsPourConducteur(String conducteurCin) {
        List<Evaluation> result = new ArrayList<>();
        if (conducteurCin == null) return result;
        for (Evaluation e : evaluations) {
            if (conducteurCin.equals(e.getConducteurCin())) result.add(e);
        }
        result.sort((a, b) -> b.getDateCreation().compareTo(a.getDateCreation()));
        return result;
    }

    /**
     * Notification au conducteur quand un passager l'évalue.
     */
    public void creerNotificationEvaluation(String conducteurCin, String passagerCin,
                                            String passagerName, int rating, String comment) {
        String notifId = "NOTIF_" + System.currentTimeMillis() + "_" + conducteurCin + "_EVAL";
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) stars.append(i <= rating ? "★" : "☆");

        String preview = (comment != null && !comment.isEmpty())
                ? (comment.length() > 50 ? comment.substring(0, 50) + "..." : comment)
                : "(sans commentaire)";
        String message = "⭐ " + passagerName + " vous a évalué " + stars + " : " + preview;

        Notification notif = new Notification(notifId, conducteurCin, passagerCin,
                "", "EVALUATION", message);

        List<Notification> notifs = notifications_par_conducteur
                .computeIfAbsent(conducteurCin, k -> new ArrayList<>());
        notifs.add(notif);
    }
}
