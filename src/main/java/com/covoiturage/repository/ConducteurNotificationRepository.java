package com.covoiturage.repository;
import com.covoiturage.entity.ConducteurNotification;import org.springframework.data.jpa.repository.*;import org.springframework.data.repository.query.Param;import org.springframework.stereotype.Repository;
@Repository public interface ConducteurNotificationRepository extends JpaRepository<ConducteurNotification,String>{java.util.List<ConducteurNotification> findByConducteurIdOrderByDateCreationDesc(String conducteurId);
 long countByConducteurIdAndEstLueFalse(String conducteurId);
 @Modifying @Query("UPDATE ConducteurNotification n SET n.estLue = true WHERE n.notificationId = :id AND n.conducteurId = :cin")
 void markAsReadByIdAndConducteur(@Param("id")String id,@Param("cin")String cin);
 @Modifying @Query("UPDATE ConducteurNotification n SET n.estLue = true WHERE n.conducteurId = :cin")
 void markAllAsReadForConducteur(@Param("cin")String cin);}
