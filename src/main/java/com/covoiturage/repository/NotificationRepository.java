package com.covoiturage.repository;
import com.covoiturage.entity.Notification;import org.springframework.data.jpa.repository.*;import org.springframework.data.repository.query.Param;import org.springframework.stereotype.Repository;
@Repository public interface NotificationRepository extends JpaRepository<Notification,String>{java.util.List<Notification> findByPassagerIdOrderByDateCreationDesc(String passagerId);
 long countByPassagerIdAndEstLueFalse(String passagerId);
 @Modifying @Query("UPDATE Notification n SET n.estLue = true WHERE n.notificationId = :id AND n.passagerId = :cin")
 void markAsReadByIdAndPassager(@Param("id")String id,@Param("cin")String cin);
 @Modifying @Query("UPDATE Notification n SET n.estLue = true WHERE n.passagerId = :cin")
 void markAllAsReadForPassager(@Param("cin")String cin);}
