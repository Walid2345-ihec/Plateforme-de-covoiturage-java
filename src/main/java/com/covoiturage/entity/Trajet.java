package com.covoiturage.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="trajets")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Trajet{
 @Id
 @GeneratedValue(strategy=GenerationType.IDENTITY)
 @Column(name="id")
 private Long id;
 @Column(name="depart")
 private String depart;
 @Column(name="arrivee")
 private String arrivee;
 @Column(name="duree_minutes")
 private Integer dureeMinutes;
 @Column(name="status")
 private String status;
 @Column(name="prix")
 private Double prix;
 @Column(name="conducteur_cin")
 private String conducteurCin;
 @Column(name="passager_cin")
 private String passagerCin;
 @Column(name="max_places")
 private Integer maxPlaces;
 @Column(name="accepted_cins")
 private String acceptedCins;
 @Column(name="pending_cins")
 private String pendingCins;
 @Column(name="start_date_time")
 private LocalDateTime startDateTime;
 @Column(name="end_date_time")
 private LocalDateTime endDateTime;
 @Column(name="weekly_schedule", columnDefinition="TEXT")
 private String weeklySchedule;
 public static final String STATUS_PENDING="PENDING",STATUS_PENDING_APPROVAL="PENDING_APPROVAL",STATUS_IN_PROGRESS="IN_PROGRESS",STATUS_FINISHED="FINISHED";
 public int getAcceptedCount(){if(acceptedCins==null||acceptedCins.isBlank())return 0;return (int)java.util.Arrays.stream(acceptedCins.split(",")).filter(x->!x.isBlank()).count();}
 public int getAvailablePlaces(){return Math.max(0,(maxPlaces==null?1:maxPlaces)-getAcceptedCount());}
 public boolean hasAccepted(String cin){return containsCin(acceptedCins,cin);}
 public boolean hasPending(String cin){return containsCin(pendingCins,cin);}
 public String buildReservationId(String passagerCin){String id=(conducteurCin+"_"+depart.replace(" ","-")+"_"+arrivee.replace(" ","-")+"_"+passagerCin);return id.replaceAll("[^a-zA-Z0-9_\\-]","");}
 public static boolean containsCin(String csv,String cin){if(csv==null||cin==null)return false;return java.util.Arrays.stream(csv.split(",")).map(String::trim).anyMatch(cin::equalsIgnoreCase);}
 public static String addCin(String csv,String cin){if(cin==null||cin.isBlank()||containsCin(csv,cin))return csv==null?"":csv;return(csv==null||csv.isBlank())?cin:csv+","+cin;}
 public static String removeCin(String csv,String cin){if(csv==null||cin==null)return csv;return java.util.Arrays.stream(csv.split(",")).map(String::trim).filter(x->!x.isBlank()&&!x.equalsIgnoreCase(cin)).collect(java.util.stream.Collectors.joining(","));}
}
