package com.covoiturage.repository;
import com.covoiturage.entity.Passager;import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;
@Repository public interface PassagerRepository extends JpaRepository<Passager,String>{boolean existsByMail(String mail);}