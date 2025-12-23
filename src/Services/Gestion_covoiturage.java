package Services;

import Models.*;
import java.util.HashMap;
import java.util.Map;
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

    // ===== API utilisée par l'UI (gestion des demandes / acceptations) =====

    /**
     * Ajouter une demande pour un conducteur
     */
    public void ajouter_demande_pour_conducteur(String cinConducteur, String cinPassager) {
        Vector<String> demandes = demandes_par_conducteur.computeIfAbsent(cinConducteur, k -> new Vector<>());
        if (!demandes.contains(cinPassager)) {
            demandes.add(cinPassager);
        }
    }

    /**
     * Supprimer une demande pour un conducteur
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
            Vector<String> demandes = demandes_par_conducteur.computeIfAbsent(t.getConducteur().getCin(), k -> new Vector<>());
            if (!demandes.contains(cinPassager)) demandes.add(cinPassager);
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
        Vector<String> demandes = demandes_par_conducteur.get(conducteur.getCin());
        if (demandes != null) demandes.remove(cinPassager);

        // Ajouter à historique global
        passagers_acceptes.add(p);

        // Mise à jour statut et validité
        t.setTrajet_valide(true);
        if (!t.getPassagersAcceptes().isEmpty()) t.setStatusTrajet(Trajet.STATUS_IN_PROGRESS);

        return true;
    }
}
