package com.covoiturage.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="conducteurs")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Conducteur{
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
 @Column(name="nom_voiture")
 private String nomVoiture;
 @Column(name="marque_voiture")
 private String marqueVoiture;
 @Column(name="matricule")
 private String matricule;
 @Column(name="places_disponibles")
 private Integer placesDisponibles;
 @Column(name="weekly_schedule", columnDefinition="TEXT")
 private String weeklySchedule;
 @Column(name="moyenne_evaluation")
 private Double moyenneEvaluation;
 @Column(name="carte")
 private String carte;
 @Column(name="banned")
 private Boolean banned;
}
