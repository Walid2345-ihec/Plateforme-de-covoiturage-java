package Services;

import Models.*;
import java.util.Scanner;
import java.util.Vector;

/**
 *
 * @author ricko
 */
public class Gestion_covoiturage {
    private int Index_trajet_conducteur = -1;
    private int Index_conducteur = -1;
    private Vector <User> users = new Vector<>();
    private Vector <Trajet> trajets = new Vector<>();
    private Vector <User> passagers_acceptés = new Vector<>();
    
    //getters
    public Vector<User> getUsers() { return users; }
    public Vector<Trajet> getTrajets() { return trajets; }
    
    //setters
    public void setUsers(Vector<User> users) { this.users = users; }
    public void setTrajets(Vector<Trajet> trajets) { this.trajets = trajets; }
    
    
    public User rechercher_user(String ref){
        for (User U : users){
            if(U.getCin().equalsIgnoreCase(ref)){
                return U;
            }
        }
        return null;
    }
    
    public void Proposer_trajet() {
        String Cin;
        System.out.println("--- Saisie des informations utilisateur ---");
        do{
            Scanner sc = new Scanner(System.in);
            System.out.println("Entrez votre cin :");
            Cin = sc.nextLine();
            if (rechercher_user(Cin) != null){
                System.out.println(" Cet utilisateur existe deja !");
            }
        }while (rechercher_user(Cin) != null);
        this.users.add(new Conducteur(Cin));
        System.out.println("Conducteur ajoute avec succes !");
    }
    

    public void Chercher_trajet() {
        String Cin;
        System.out.println("--- Saisie des informations utilisateur ---");
        do{
            Scanner sc = new Scanner(System.in);
            System.out.println("Entrez votre cin :");
            Cin = sc.nextLine();
            if (rechercher_user(Cin) != null){
                System.out.println(" Cet utilisateur existe deja !");
            }
        }while (rechercher_user(Cin) != null);
        this.users.add(new Passager(Cin));
        System.out.println("Passager ajoute avec succes !");
    }
    
    public void afficher_conducteurs(){
        for(int i=0;i<users.size();i++)
        {
            if(this.users.get(i) instanceof Conducteur) {
                System.out.println(this.users.get(i).toString());
            }
        }
    }
    
    public void afficher_passagers(){
        for(int i=0;i<users.size();i++)
        {
            if(this.users.get(i) instanceof Passager) {
                System.out.println(this.users.get(i).toString());
            }
        }
    }
    
    
    //MENU CONDUCTEUR
    //
    //VOIR LES TRAJETS des passagers
    public void voir_propositions(){
        if (this.trajets.isEmpty()){
            System.out.println("--- Aucun trajet disponible ---");
        }
        else{
            for(int i=0;i<trajets.size();i++)
            {
                if(this.trajets.get(i) instanceof Trajet) {
                    System.out.println(this.trajets.get(i).toString());
                }
            }
        }
    }
    //créer trajet pour le conducteur
    public void ajout_trajet(){
        this.trajets.add(new Trajet());
        Index_trajet_conducteur +=1;
    }
    //modifier le tarif
    public void modifier_tarif(){
        float Prix;
        Scanner sc = new Scanner(System.in);
        System.out.println("Entrez le nouveau prix du trajet :");
        Prix = sc.nextFloat();
        sc.nextLine();
        Trajet Modification = trajets.get(Index_trajet_conducteur);
        Modification.setPrix(Prix);
        System.out.println("Prix modifié avec succès!");
    }
    //Modifier trajet
    public void modifier_trajet(){
        trajets.set(Index_trajet_conducteur,new Trajet());
    }
    
   //Accepter passager
    public void accepter_passager() {
    String Cin;
    // Il est préférable de créer le Scanner une seule fois pour l'application
    Scanner sc = new Scanner(System.in); 
    System.out.println("Enter le cin du passager:");
    Cin = sc.nextLine();

    // 1. Trouver le passager
    for (User U : users) {
        if (U.getCin().equalsIgnoreCase(Cin)) {
            System.out.println("le passager " + U.getPrenom() + " " + U.getNom() + " a ete accepte");
            
            // 2. Récupérer le conducteur actuel (en utilisant Index_conducteur)
            User userConducteur = users.get(Index_conducteur);

            // 3. S'assurer que l'utilisateur est bien un conducteur avant de caster
            if (userConducteur instanceof Conducteur) {
                Conducteur conducteur = (Conducteur) userConducteur;
                
                // 4. Mettre à jour les places disponibles
                // On récupère l'ancienne valeur, on la décrémente et on la réassigne
                int placesActuelles = conducteur.getPlacesDisponibles();
                
                if (placesActuelles > 0) {
                    conducteur.setPlacesDisponibles(placesActuelles - 1);
                    System.out.println("Places restantes pour le conducteur : " + conducteur.getPlacesDisponibles());
                    // Optionnel: Sortir de la boucle une fois le passager trouvé et traité
                    return; 
                } else {
                    System.out.println("Le conducteur n'a plus de places disponibles.");
                    return; // Sortir si plus de places
                }
            }
            else {
                System.out.println("Erreur: L'index conducteur ne pointe pas vers un objet Conducteur.");
                return;
            }
            this.passagers_acceptés.add(U.getCin(),U.getNom(),U.getPrenom,U.getTel());
        }
    }
    // Si la boucle se termine sans trouver le passager
    System.out.println("Aucun passager trouvé avec le CIN : " + Cin);
}

//Contacts des passagers acceptés
    
    
}
