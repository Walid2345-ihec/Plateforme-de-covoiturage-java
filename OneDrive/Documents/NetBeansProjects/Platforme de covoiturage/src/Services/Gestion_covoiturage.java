package Services;

import Models.*;
import java.util.Scanner;
import java.util.Vector;

/**
 *
 * @author ricko
 */
public class Gestion_covoiturage {
    private Vector <User> users = new Vector<>();
    private Vector <Trajet> trajets = new Vector<>();
    
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
}
