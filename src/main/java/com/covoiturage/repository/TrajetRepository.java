package com.covoiturage.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.covoiturage.entity.Trajet;
@Repository public interface TrajetRepository extends JpaRepository<Trajet,Long>{java.util.List<Trajet> findByConducteurCin(String conducteurCin);
 java.util.List<Trajet> findByConducteurCinAndWeeklyScheduleIsNotNull(String conducteurCin);
 java.util.List<Trajet> findByConducteurCinAndWeeklyScheduleIsNull(String conducteurCin);
 java.util.List<Trajet> findByWeeklyScheduleIsNotNull();
 java.util.List<Trajet> findByDepartContainingIgnoreCaseAndArriveeContainingIgnoreCase(String depart,String arrivee);}
