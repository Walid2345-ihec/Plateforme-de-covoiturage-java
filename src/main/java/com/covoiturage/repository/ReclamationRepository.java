package com.covoiturage.repository;
import com.covoiturage.entity.Reclamation;import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;
@Repository public interface ReclamationRepository extends JpaRepository<Reclamation,String>{java.util.List<Reclamation> findAllByOrderByCreatedAtDesc();
 boolean existsByReservationIdAndComplainantIdAndAccusedId(String reservationId,String complainantId,String accusedId);}