/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package App;

import Models.*;
import Services.*;
import java.util.Scanner;

/**
 *
 * @author ricko
 */
public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        Gestion_covoiturage users = new Gestion_covoiturage();
        int choix;
        int choix2;
        
         do {
            System.out.println("\n===== Plateforme de covoiturage =====");
            System.out.println("1. Proposer un trajet");
            System.out.println("2. Chercher un trajet");
            System.out.println("0. Quitter");
            System.out.print("Votre choix : ");

            while (!sc.hasNextInt()) {
                System.out.print("Veuillez entrer un nombre : ");
                sc.next();
            }
            choix = sc.nextInt();
            sc.nextLine(); // consommer le retour à la ligne

            switch (choix) {
                case 1:
                    users.Proposer_trajet();
                    do{
                        
                        System.out.println("\n===== Plateforme de covoiturage =====");
                        System.out.println("1. Afficher tous les conducteurs");
                        System.out.println("0. retour");
                        System.out.print("Votre choix : ");
                        while (!sc.hasNextInt()) {
                            System.out.print("Veuillez entrer un nombre : ");
                            sc.next();
                        }
                        choix2 = sc.nextInt();
                        sc.nextLine(); // consommer le retour à la ligne
                        
                        switch(choix2) {
                            case 1:
                                users.afficher_conducteurs();
                                break;
                            case 0:
                                break;
                            default:
                                System.out.println("Choix invalide !");
                        }
                        
                    }while (choix2 != 0);
                    
                    
                    break;

                case 2:
                    users.Chercher_trajet();
                    do{
                        
                        System.out.println("\n===== Plateforme de covoiturage =====");
                        System.out.println("1. Afficher tous les passagers");
                        System.out.println("0. retour");
                        System.out.print("Votre choix : ");
                        while (!sc.hasNextInt()) {
                            System.out.print("Veuillez entrer un nombre : ");
                            sc.next();
                        }
                        choix2 = sc.nextInt();
                        sc.nextLine(); // consommer le retour à la ligne
                        
                        switch(choix2) {
                            case 1:
                                users.afficher_passagers();
                                break;
                            case 0:
                                break;
                            default:
                                System.out.println("Choix invalide !");
                        }
                        
                    }while (choix2 != 0);
                    break;
                case 0:
                    System.out.println("Au revoir !");
                    break;

                default:
                    System.out.println("Choix invalide !");
            }

        } while (choix != 0);
    }
    
}
