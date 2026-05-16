package com.covoiturage.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
 @Transient
 private String userName;
}
