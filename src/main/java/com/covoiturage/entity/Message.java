package com.covoiturage.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="messages")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Message{
 @Id
 @Column(name="message_id")
 private String messageId;
 @Column(name="sender_cin")
 private String senderCin;
 @Column(name="sender_name")
 private String senderName;
 @Column(name="recipient_cin")
 private String recipientCin;
 @Column(name="recipient_name")
 private String recipientName;
 @Column(name="content", columnDefinition="TEXT")
 private String content;
 @Column(name="timestamp")
 private LocalDateTime timestamp;
 @Column(name="is_deleted")
 private Boolean isDeleted;
 @Column(name="trajet_id")
 private String trajetId;
}
