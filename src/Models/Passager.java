package Models;

import java.time.Year;
import java.util.Scanner;
import java.util.Vector;

public class Passager extends User {
    private boolean chercheCovoit;
    private Conducteur conducteur;
    private Vector<String> notifications = new Vector<>();  // Liste des notifications pour ce passager

    // Constructeur par défaut (interactif)
    public Passager() {
        super(); // Appelle le constructeur par défaut interactif de User

        Scanner sc = new Scanner(System.in);
        System.out.println("--- Saisie des informations Passager ---");

        // Gestion des exceptions pour la saisie d'un booléen
        boolean saisieValide = false;
        while (!saisieValide) {
            System.out.println("Cherche covoiturage ? (oui/non) :");
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("oui") || input.equals("o")) {
                this.chercheCovoit = true;
                saisieValide = true;
            } else if (input.equals("non") || input.equals("n")) {
                this.chercheCovoit = false;
                saisieValide = true;
            } else {
                System.err.println("Erreur: Veuillez répondre par 'oui' ou 'non'.");
            }
        }
        this.conducteur = null;
    }

    // Constructeur paramétré (avec validation)
    public Passager(String cin, String nom, String prenom, String tel, Year anneeUniversitaire, String adresse, String mail, String password, boolean chercheCovoit, Conducteur conducteur) {
        super(cin, nom, prenom, tel, anneeUniversitaire, adresse, mail, password); // La validation de User est faite ici
        this.chercheCovoit = chercheCovoit;
        this.conducteur = conducteur;
    }
    
    /**
     * Constructor for loading from CSV (password already hashed).
     * This constructor accepts a pre-hashed password for database loading.
     * 
     * SECURITY NOTE: Use this constructor ONLY when loading from CSV.
     * For new passenger registration, use the standard constructor with plain password.
     */
    public Passager(String cin, String nom, String prenom, String tel, Year anneeUniversitaire, 
                    String adresse, String mail, String passwordHash, boolean isHashedPassword,
                    boolean chercheCovoit, Conducteur conducteur) {
        super(cin, nom, prenom, tel, anneeUniversitaire, adresse, mail, passwordHash, isHashedPassword);
        this.chercheCovoit = chercheCovoit;
        this.conducteur = conducteur;
    }

    // Nouveau constructeur qui accepte un objet User
    public Passager(String cin) {
        super(cin);
        Scanner sc = new Scanner(System.in);
        System.out.println("--- Saisie des informations Passager ---");

        // Gestion des exceptions pour la saisie d'un booléen
        boolean saisieValide = false;
        while (!saisieValide) {
            System.out.println("Cherche covoiturage ? (oui/non) :");
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("oui") || input.equals("o")) {
                this.chercheCovoit = true;
                saisieValide = true;
            } else if (input.equals("non") || input.equals("n")) {
                this.chercheCovoit = false;
                saisieValide = true;
            } else {
                System.err.println("Erreur: Veuillez répondre par 'oui' ou 'non'.");
            }
        }
    }
    
    
    // Getters
    public boolean isChercheCovoit() { return chercheCovoit; }
    public Vector<String> getNotifications() { return notifications; }

    // Setters
    public void setChercheCovoit(boolean chercheCovoit) { this.chercheCovoit = chercheCovoit; }

    /**
     * Ajouter une notification pour ce passager
     */
    public void addNotification(String notificationMessage) {
        if (notificationMessage != null && !notificationMessage.isEmpty()) {
            this.notifications.add(notificationMessage);
        }
    }

    /**
     * Obtenir la dernière notification et la supprimer (pop)
     */
    public String getLastNotification() {
        if (!notifications.isEmpty()) {
            return notifications.remove(notifications.size() - 1);
        }
        return null;
    }

    /**
     * Vérifier s'il y a des notifications non lues
     */
    public boolean hasNotifications() {
        return !notifications.isEmpty();
    }

    /**
     * Effacer toutes les notifications
     */
    public void clearNotifications() {
        notifications.clear();
    }

    @Override
    public String toString() {
        return "Passager{" +
                super.toString() +
                ", chercheCovoit=" + chercheCovoit +
                '}';
    }
}
