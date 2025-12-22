package App;

import Models.*;
import Services.*;
import java.util.Scanner;

/**
 * Application principale de gestion de covoiturage
 * @author ricko
 */
public class App {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Gestion_covoiturage gestion = new Gestion_covoiturage();
        int choix;
        
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║  BIENVENUE SUR LA PLATEFORME COVOITURAGE  ║");
        System.out.println("╚════════════════════════════════════════════╝");
        
        do {
            System.out.println("\n┌─────────────────────────────────────┐");
            System.out.println("│     MENU PRINCIPAL                  │");
            System.out.println("├─────────────────────────────────────┤");
            System.out.println("│ 1. Proposer un trajet (Conducteur)  │");
            System.out.println("│ 2. Chercher un trajet (Passager)    │");
            System.out.println("│ 3. Se connecter (Conducteur)        │");
            System.out.println("│ 4. Se connecter (Passager)          │");
            System.out.println("│ 0. Quitter                          │");
            System.out.println("└─────────────────────────────────────┘");
            System.out.print("Votre choix : ");

            while (!sc.hasNextInt()) {
                System.out.print("⚠ Veuillez entrer un nombre : ");
                sc.next();
            }
            choix = sc.nextInt();
            sc.nextLine();

            switch (choix) {
                case 1:
                    gestion.Proposer_trajet();
                    // Après création on accède au menu conducteur automatiquement
                    menuConducteur(sc, gestion);
                    break;

                case 2:
                    gestion.Chercher_trajet();
                    // Après création on accède au menu passager automatiquement
                    menuPassager(sc, gestion);
                    break;
                    
                case 3:
                    // Connexion conducteur existant
                    if (gestion.se_connecter_conducteur()) {
                        menuConducteur(sc, gestion);
                    }
                    break;

                case 4:
                    // Connexion passager existant
                    if (gestion.se_connecter_passager()) {
                        menuPassager(sc, gestion);
                    }
                    break;

                case 0:
                    System.out.println("\n════════════════════════════════════════");
                    System.out.println("   Merci d'avoir utilisé notre service!");
                    System.out.println("   À bientôt! 🚗");
                    System.out.println("════════════════════════════════════════");
                    break;

                default:
                    System.out.println("❌ Choix invalide !");
            }

        } while (choix != 0);
        
        sc.close();
    }
    
    /**
     * Menu pour le conducteur
     */
    private static void menuConducteur(Scanner sc, Gestion_covoiturage gestion) {
        int choix;
        
        do {
            System.out.println("\n┌──────────────────────────────────────────────┐");
            System.out.println("│          MENU CONDUCTEUR                     │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ 1. Voir les propositions (demandes)          │");
            System.out.println("│ 2. Voir les demandes (trajets recherchés)    │");
            System.out.println("│ 3. Créer un trajet                           │");
            System.out.println("│ 4. Modifier le tarif                         │");
            System.out.println("│ 5. Modifier les paramètres du trajet         │");
            System.out.println("│ 6. Accepter un passager                      │");
            System.out.println("│ 7. Contacts des passagers acceptés           │");
            System.out.println("│ 8. Afficher tous les conducteurs             │");
            System.out.println("│ 0. Retour au menu principal                  │");
            System.out.println("└──────────────────────────────────────────────┘");
            System.out.print("Votre choix : ");
            
            while (!sc.hasNextInt()) {
                System.out.print("⚠ Veuillez entrer un nombre : ");
                sc.next();
            }
            choix = sc.nextInt();
            sc.nextLine();
            
            switch(choix) {
                case 1:
                    gestion.voir_propositions();
                    break;
                case 2:
                    gestion.voir_demandes();
                    break;
                case 3:
                    gestion.creer_trajet();
                    break;
                case 4:
                    gestion.modifier_tarif();
                    break;
                case 5:
                    gestion.modifier_parametres_trajet();
                    break;
                case 6:
                    gestion.accepter_passager();
                    break;
                case 7:
                    gestion.contacts_passagers_acceptes();
                    break;
                case 8:
                    gestion.afficher_conducteurs();
                    break;
                case 0:
                    System.out.println("↩ Retour au menu principal...");
                    break;
                default:
                    System.out.println("❌ Choix invalide !");
            }
            
            if (choix != 0) {
                System.out.println("\nAppuyez sur Entrée pour continuer...");
                sc.nextLine();
            }
            
        } while (choix != 0);
    }
    
    /**
     * Menu pour le passager
     */
    private static void menuPassager(Scanner sc, Gestion_covoiturage gestion) {
        int choix;
        
        do {
            System.out.println("\n┌──────────────────────────────────────────────┐");
            System.out.println("│          MENU PASSAGER                       │");
            System.out.println("├──────────────────────────────────────────────┤");
            System.out.println("│ 1. Voir les trajets disponibles              │");
            System.out.println("│ 2. Notifier le conducteur                    │");
            System.out.println("│ 3. Ajouter un trajet voulu                   │");
            System.out.println("│ 4. Confirmer le trajet                       │");
            System.out.println("│ 5. Afficher tous les passagers               │");
            System.out.println("│ 0. Retour au menu principal                  │");
            System.out.println("└──────────────────────────────────────────────┘");
            System.out.print("Votre choix : ");
            
            while (!sc.hasNextInt()) {
                System.out.print("⚠ Veuillez entrer un nombre : ");
                sc.next();
            }
            choix = sc.nextInt();
            sc.nextLine();
            
            switch(choix) {
                case 1:
                    gestion.voir_trajets_disponibles();
                    break;
                case 2:
                    gestion.notifier_conducteur();
                    break;
                case 4:
                    gestion.confirmer_trajet();
                    break;
                case 5:
                    gestion.afficher_passagers();
                    break;
                case 0:
                    System.out.println("↩ Retour au menu principal...");
                    break;
                default:
                    System.out.println("❌ Choix invalide !");
            }
            
            if (choix != 0) {
                System.out.println("\nAppuyez sur Entrée pour continuer...");
                sc.nextLine();
            }
            
        } while (choix != 0);
    }
}
