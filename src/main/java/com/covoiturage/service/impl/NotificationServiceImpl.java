package com.covoiturage.service.impl;
import com.covoiturage.entity.*;import com.covoiturage.repository.*;import com.covoiturage.service.NotificationService;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.time.LocalDateTime;import java.util.*;
@Service public class NotificationServiceImpl implements NotificationService{private final NotificationRepository n;private final ConducteurNotificationRepository cn;private final AdminNotificationRepository an;public NotificationServiceImpl(NotificationRepository n,ConducteurNotificationRepository cn,AdminNotificationRepository an){this.n=n;this.cn=cn;this.an=an;}
 @Transactional public void notifyPassager(String p,String c,String t,String type,String msg){n.save(Notification.builder().notificationId("NOTIF_"+System.currentTimeMillis()+"_"+p).passagerId(p).conducteurId(c).trajetId(t).type(type).message(msg).dateCreation(LocalDateTime.now()).estLue(false).build());}
 @Transactional public void notifyConducteur(String c,String p,String t,String type,String msg){cn.save(ConducteurNotification.builder().notificationId("NOTIF_"+System.currentTimeMillis()+"_"+c).conducteurId(c).passagerId(p).trajetId(t).type(type).message(msg).dateCreation(LocalDateTime.now()).estLue(false).build());}
 @Transactional public void notifyAdmin(String type,String msg,String p,String c,String t){an.save(AdminNotification.builder().notificationId("NOTIF_"+System.currentTimeMillis()+"_ADMIN").passagerId(p).conducteurId(c).trajetId(t).type(type).message(msg).dateCreation(LocalDateTime.now()).estLue(false).build());}
 @Transactional public void addAdminNotification(String message,String type){notifyAdmin(type,message,null,null,null);}
 public List<Notification> passagerNotifications(String cin){return n.findByPassagerIdOrderByDateCreationDesc(cin);} public List<ConducteurNotification> conducteurNotifications(String cin){return cn.findByConducteurIdOrderByDateCreationDesc(cin);} public List<AdminNotification> adminNotifications(){return an.findAllByOrderByDateCreationDesc();} public List<AdminNotification> adminNotificationsByType(String type){return an.findByTypeOrderByDateCreationDesc(type);} public long unreadPassager(String cin){return n.countByPassagerIdAndEstLueFalse(cin);} public long unreadConducteur(String cin){return cn.countByConducteurIdAndEstLueFalse(cin);} public long unreadAdmin(){return an.countByEstLueFalse();}
 // Source Swing : NotificationPanel.java -> gestion.marquerCommelue()
 @Transactional public void marquerCommelue(String cinPassager,String notificationId){n.markAsReadByIdAndPassager(notificationId,cinPassager);}
 // Source Swing : NotificationPanel.java -> bouton Back -> gestion.marquerToutesCommelues()
 @Transactional public void marquerToutesCommelues(String cinPassager){n.markAllAsReadForPassager(cinPassager);}
 // Source Swing : DriverNotificationPanel.java -> gestion.marquerCommelueConducteur()
 @Transactional public void marquerCommelueConducteur(String cinConducteur,String notificationId){cn.markAsReadByIdAndConducteur(notificationId,cinConducteur);}
 // Source Swing : DriverNotificationPanel.java -> bouton Back -> gestion.marquerToutesCommelueConducteur()
 @Transactional public void marquerToutesCommelueConducteur(String cinConducteur){cn.markAllAsReadForConducteur(cinConducteur);}
 @Transactional public void marquerToutesAdminCommelues(){an.markAllAsRead();}
}
