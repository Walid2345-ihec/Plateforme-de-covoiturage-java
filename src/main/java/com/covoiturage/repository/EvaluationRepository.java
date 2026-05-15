package com.covoiturage.repository;
import com.covoiturage.entity.Evaluation;import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;
@Repository public interface EvaluationRepository extends JpaRepository<Evaluation,String>{java.util.List<Evaluation> findByConducteurCinOrderByDateCreationDesc(String conducteurCin);}