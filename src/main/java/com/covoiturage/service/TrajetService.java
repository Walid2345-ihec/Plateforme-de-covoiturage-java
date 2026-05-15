package com.covoiturage.service;

import com.covoiturage.dto.TrajetForm;
import com.covoiturage.entity.Trajet;
import java.util.List;

public interface TrajetService {
    List<Trajet> getAllTrajets();
    List<Trajet> search(String depart, String arrivee);
    List<Trajet> getTrajetsConducteur(String cin);
    List<Trajet> getTrajetsRecurrents(String conducteurCin);
    List<Trajet> getTrajetsRecurrentsPourAujourdHui(String conducteurCin);
    List<Trajet> getTrajetsConducteurSansSchedule(String conducteurCin);
    Trajet createTrajet(String cin, TrajetForm f);
    Trajet demanderReservation(Long id, String passagerCin);
    Trajet accepterPassager(Long id, String passagerCin);
    Trajet refuserPassager(Long id, String passagerCin);
    Trajet annulerReservation(Long id, String passagerCin);
    Trajet supprimerPassagerAccepte(Long id, String passagerCin);
    Trajet supprimerPassagerAccepte(Long id, String passagerCin, String conducteurCin);
    Trajet modifierPrix(Long id, double prix);
    Trajet finishTrajet(Long id);
    void deleteTrajet(Long id);
}
