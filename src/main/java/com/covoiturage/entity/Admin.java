package com.covoiturage.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="admins")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Admin{
 @Id
 @Column(name="cin")
 private String cin;
 @Column(name="nom")
 private String nom;
 @Column(name="prenom")
 private String prenom;
 @Column(name="tel")
 private String tel;
 @Column(name="annee_univ")
 private Integer anneeUniv;
 @Column(name="adresse", columnDefinition="TEXT")
 private String adresse;
 @Column(name="mail")
 private String mail;
 @Column(name="password_hash")
 private String passwordHash;
 @Column(name="role")
 private String role;
 @Column(name="date_creation")
 private LocalDateTime dateCreation;
}
