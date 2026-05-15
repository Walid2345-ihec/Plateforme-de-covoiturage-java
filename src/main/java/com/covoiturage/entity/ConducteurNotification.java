package com.covoiturage.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="conducteur_notifications")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ConducteurNotification{
 @Id
 @Column(name="notification_id")
 private String notificationId;
 @Column(name="conducteur_id")
 private String conducteurId;
 @Column(name="passager_id")
 private String passagerId;
 @Column(name="trajet_id")
 private String trajetId;
 @Column(name="type")
 private String type;
 @Column(name="message", columnDefinition="TEXT")
 private String message;
 @Column(name="date_creation")
 private LocalDateTime dateCreation;
 @Column(name="est_lue")
 private Boolean estLue;
}
