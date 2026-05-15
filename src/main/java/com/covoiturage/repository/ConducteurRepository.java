package com.covoiturage.repository;
import com.covoiturage.entity.Conducteur;import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;
@Repository public interface ConducteurRepository extends JpaRepository<Conducteur,String>{boolean existsByMail(String mail);}