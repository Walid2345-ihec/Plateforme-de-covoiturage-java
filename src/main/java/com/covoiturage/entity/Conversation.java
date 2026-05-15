package com.covoiturage.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="conversations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Conversation{
 @Id
 @Column(name="id")
 private String id;
 @Column(name="user_id")
 private String userId;
 @Column(name="admin_id")
 private String adminId;
 @Column(name="triggered_by")
 private String triggeredBy;
 @Column(name="created_at")
 private LocalDateTime createdAt;
}
