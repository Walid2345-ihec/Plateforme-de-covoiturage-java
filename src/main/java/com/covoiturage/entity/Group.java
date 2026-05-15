package com.covoiturage.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="`groups`")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Group{
 @Id
 @Column(name="group_id")
 private String groupId;
 @Column(name="group_name")
 private String groupName;
 @Column(name="conducteur_cin")
 private String conducteurCin;
 @Column(name="member_cins", columnDefinition="TEXT")
 private String memberCins;
 @Column(name="date_creation")
 private LocalDateTime dateCreation;
}
