package com.covoiturage.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="group_messages")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupMessage{
 @Id
 @Column(name="message_id")
 private String messageId;
 @Column(name="group_id")
 private String groupId;
 @Column(name="sender_cin")
 private String senderCin;
 @Column(name="sender_name")
 private String senderName;
 @Column(name="content", columnDefinition="TEXT")
 private String content;
 @Column(name="timestamp")
 private LocalDateTime timestamp;
 @Column(name="is_deleted")
 private Boolean isDeleted;
}
