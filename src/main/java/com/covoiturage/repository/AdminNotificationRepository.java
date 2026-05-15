package com.covoiturage.repository;
import com.covoiturage.entity.AdminNotification;import org.springframework.data.jpa.repository.*;import org.springframework.stereotype.Repository;
@Repository public interface AdminNotificationRepository extends JpaRepository<AdminNotification,String>{java.util.List<AdminNotification> findAllByOrderByDateCreationDesc();
 java.util.List<AdminNotification> findByTypeOrderByDateCreationDesc(String type);
 long countByEstLueFalse();
 @Modifying @Query("UPDATE AdminNotification n SET n.estLue = true")
 void markAllAsRead();}
