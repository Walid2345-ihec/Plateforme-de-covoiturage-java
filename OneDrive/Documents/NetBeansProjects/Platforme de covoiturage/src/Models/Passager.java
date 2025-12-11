package Models;

import java.time.Year;
import java.util.Scanner;

public class Passager extends User {
    private boolean chercheCovoit;

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
    }

    // Constructeur paramétré (avec validation)
    public Passager(String cin, String nom, String prenom, String tel, Year anneeUniversitaire, String adresse, String mail,boolean chercheCovoit) {
        super(cin, nom, prenom, tel, anneeUniversitaire, adresse, mail); // La validation de User est faite ici
        this.chercheCovoit = chercheCovoit;
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

    // Setters
    public void setChercheCovoit(boolean chercheCovoit) { this.chercheCovoit = chercheCovoit; }

    @Override
    public String toString() {
        return "Passager{" +
                super.toString() +
                ", chercheCovoit=" + chercheCovoit +
                '}';
    }
}
