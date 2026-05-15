package com.covoiturage.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="evaluations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Evaluation{
 @Id
 @Column(name="evaluation_id")
 private String evaluationId;
 @Column(name="passager_cin")
 private String passagerCin;
 @Column(name="passager_name")
 private String passagerName;
 @Column(name="conducteur_cin")
 private String conducteurCin;
 @Column(name="trajet_id")
 private String trajetId;
 @Column(name="rating")
 private Integer rating;
 @Column(name="comment", columnDefinition="TEXT")
 private String comment;
 @Column(name="date_creation")
 private LocalDateTime dateCreation;
}
