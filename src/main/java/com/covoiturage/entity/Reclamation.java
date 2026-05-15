package com.covoiturage.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="reclamations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Reclamation{
 @Id
 @Column(name="id")
 private String id;
 @Column(name="reservation_id")
 private String reservationId;
 @Column(name="complainant_id")
 private String complainantId;
 @Column(name="complainant_role")
 private String complainantRole;
 @Column(name="accused_id")
 private String accusedId;
 @Column(name="accused_role")
 private String accusedRole;
 @Column(name="preset")
 private String preset;
 @Column(name="message", columnDefinition="TEXT")
 private String message;
 @Column(name="created_at")
 private LocalDateTime createdAt;
}
