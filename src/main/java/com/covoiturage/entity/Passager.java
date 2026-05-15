package com.covoiturage.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="passagers")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Passager{
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
 @Column(name="cherche_covoit")
 private Boolean chercheCovoit;
 @Column(name="carte")
 private String carte;
 @Column(name="banned")
 private Boolean banned;
}
