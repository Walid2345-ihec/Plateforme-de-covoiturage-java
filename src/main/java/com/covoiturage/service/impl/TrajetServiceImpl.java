package com.covoiturage.service.impl;

import com.covoiturage.dto.TrajetForm;
import com.covoiturage.entity.Conducteur;
import com.covoiturage.entity.Passager;
import com.covoiturage.entity.Trajet;
import com.covoiturage.repository.ConducteurRepository;
import com.covoiturage.repository.PassagerRepository;
import com.covoiturage.repository.TrajetRepository;
import com.covoiturage.service.NotificationService;
import com.covoiturage.service.TrajetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDate;

@Service
public class TrajetServiceImpl implements TrajetService {
    private final TrajetRepository trajets;
    private final ConducteurRepository conducteurs;
    private final PassagerRepository passagers;
    private final NotificationService notifications;

    public TrajetServiceImpl(TrajetRepository trajets, ConducteurRepository conducteurs, PassagerRepository passagers, NotificationService notifications) {
        this.trajets = trajets;
        this.conducteurs = conducteurs;
        this.passagers = passagers;
        this.notifications = notifications;
    }

    public List<Trajet> getAllTrajets() {
        return trajets.findAll();
    }

    public List<Trajet> search(String depart, String arrivee) {
        return trajets.findByDepartContainingIgnoreCaseAndArriveeContainingIgnoreCase(depart == null ? "" : depart, arrivee == null ? "" : arrivee);
    }

    public List<Trajet> getTrajetsConducteur(String cin) {
        return trajets.findByConducteurCin(cin);
    }

    // Source Swing : Gestion_covoiturage.getTrajetsRecurrents(Conducteur)
    public List<Trajet> getTrajetsRecurrents(String conducteurCin) {
        return trajets.findByConducteurCinAndWeeklyScheduleIsNotNull(conducteurCin).stream()
                .filter(t -> t.getWeeklySchedule() != null && !t.getWeeklySchedule().isBlank())
                .toList();
    }

    // Source Swing : WeeklySchedulePanel.java -> filtrage par jour MON/TUE/.../SUN
    public List<Trajet> getTrajetsRecurrentsPourAujourdHui(String conducteurCin) {
        String today = LocalDate.now().getDayOfWeek().name().substring(0, 3);
        return getTrajetsRecurrents(conducteurCin).stream()
                .filter(t -> t.getWeeklySchedule().contains(today + ":"))
                .toList();
    }

    public List<Trajet> getTrajetsConducteurSansSchedule(String conducteurCin) {
        return getTrajetsConducteur(conducteurCin).stream()
                .filter(t -> t.getWeeklySchedule() == null || t.getWeeklySchedule().isBlank())
                .toList();
    }

    @Transactional
    public Trajet createTrajet(String cin, TrajetForm form) {
        String requestedSchedule = form.isRecurring()
                ? form.getWeekly().toScheduleString(
                        form.getStartDateTime() == null ? null : form.getStartDateTime().toLocalTime(),
                        form.getEndDateTime() == null ? null : form.getEndDateTime().toLocalTime())
                : form.getWeeklySchedule();
        if (requestedSchedule == null || requestedSchedule.isBlank()) {
            requestedSchedule = null;
        }
        final String schedule = requestedSchedule;
        conducteurs.findById(cin).ifPresent(c -> {
            c.setWeeklySchedule(schedule == null ? "" : schedule);
            conducteurs.save(c);
        });
        return trajets.save(Trajet.builder()
                .depart(form.getDepart())
                .arrivee(form.getArrivee())
                .dureeMinutes(form.getDureeMinutes())
                .status(Trajet.STATUS_PENDING)
                .prix(form.getPrix())
                .conducteurCin(cin)
                .maxPlaces(form.getMaxPlaces())
                .acceptedCins("")
                .pendingCins("")
                .startDateTime(form.getStartDateTime())
                .endDateTime(form.getEndDateTime())
                .weeklySchedule(schedule)
                .build());
    }

    @Transactional
    public Trajet demanderReservation(Long id, String passagerCin) {
        Trajet trajet = trajets.findById(id).orElseThrow();
        Passager passager = passagers.findById(passagerCin).orElseThrow();
        if (trajet.getAvailablePlaces() > 0 && !trajet.hasAccepted(passagerCin) && !trajet.hasPending(passagerCin)) {
            trajet.setPendingCins(Trajet.addCin(trajet.getPendingCins(), passagerCin));
            trajet.setStatus(Trajet.STATUS_PENDING_APPROVAL);
            notifications.notifyConducteur(trajet.getConducteurCin(), passagerCin, String.valueOf(trajet.getId()), "DEMANDE",
                    passager.getNom() + " " + passager.getPrenom() + " demande une reservation pour " + trajet.getDepart() + " -> " + trajet.getArrivee());
        }
        return trajets.save(trajet);
    }

    @Transactional
    public Trajet accepterPassager(Long id, String passagerCin) {
        Trajet trajet = trajets.findById(id).orElseThrow();
        Passager passager = passagers.findById(passagerCin).orElseThrow();
        if (trajet.getAvailablePlaces() <= 0) {
            throw new IllegalStateException("Plus de places disponibles");
        }
        if (trajet.hasAccepted(passagerCin)) {
            throw new IllegalStateException("Passager deja accepte");
        }
        trajet.setPendingCins(Trajet.removeCin(trajet.getPendingCins(), passagerCin));
        trajet.setAcceptedCins(Trajet.addCin(trajet.getAcceptedCins(), passagerCin));
        if (trajet.getPassagerCin() == null || trajet.getPassagerCin().isBlank()) {
            trajet.setPassagerCin(passagerCin);
        }
        if (Trajet.STATUS_PENDING.equals(trajet.getStatus()) || Trajet.STATUS_PENDING_APPROVAL.equals(trajet.getStatus())) {
            trajet.setStatus(Trajet.STATUS_IN_PROGRESS);
        }
        passager.setChercheCovoit(false);
        passagers.save(passager);
        conducteurs.findById(trajet.getConducteurCin()).ifPresent(c -> {
            c.setPlacesDisponibles(Math.max(0, (c.getPlacesDisponibles() == null ? 0 : c.getPlacesDisponibles()) - 1));
            conducteurs.save(c);
        });
        notifications.notifyPassager(passagerCin, trajet.getConducteurCin(), String.valueOf(trajet.getId()), "ACCEPTATION",
                "Accepte pour le trajet " + trajet.getDepart() + " -> " + trajet.getArrivee());
        return trajets.save(trajet);
    }

    @Transactional
    public Trajet refuserPassager(Long id, String passagerCin) {
        Trajet trajet = trajets.findById(id).orElseThrow();
        trajet.setPendingCins(Trajet.removeCin(trajet.getPendingCins(), passagerCin));
        passagers.findById(passagerCin).ifPresent(p -> {
            p.setChercheCovoit(true);
            passagers.save(p);
        });
        notifications.notifyPassager(passagerCin, trajet.getConducteurCin(), String.valueOf(trajet.getId()), "REFUS",
                "Demande refusee pour " + trajet.getDepart() + " -> " + trajet.getArrivee());
        return trajets.save(trajet);
    }

    @Transactional
    public Trajet annulerReservation(Long id, String passagerCin) {
        Trajet trajet = trajets.findById(id).orElseThrow();
        boolean wasAccepted = trajet.hasAccepted(passagerCin);
        trajet.setPendingCins(Trajet.removeCin(trajet.getPendingCins(), passagerCin));
        trajet.setAcceptedCins(Trajet.removeCin(trajet.getAcceptedCins(), passagerCin));
        if (passagerCin.equals(trajet.getPassagerCin())) {
            trajet.setPassagerCin(null);
        }
        passagers.findById(passagerCin).ifPresent(p -> {
            p.setChercheCovoit(true);
            passagers.save(p);
        });
        if (wasAccepted) {
            incrementPlaces(trajet.getConducteurCin());
        }
        notifications.notifyConducteur(trajet.getConducteurCin(), passagerCin, String.valueOf(trajet.getId()), "ANNULATION",
                "Le passager " + passagerCin + " a annule sa reservation pour " + trajet.getDepart() + " -> " + trajet.getArrivee());
        return trajets.save(trajet);
    }

    @Transactional
    public Trajet supprimerPassagerAccepte(Long id, String passagerCin) {
        return supprimerPassagerAccepte(id, passagerCin, null);
    }

    @Transactional
    public Trajet supprimerPassagerAccepte(Long id, String passagerCin, String conducteurCin) {
        Trajet trajet = trajets.findById(id).orElseThrow();
        if (conducteurCin != null && !conducteurCin.equals(trajet.getConducteurCin())) {
            throw new SecurityException("Seul le conducteur proprietaire peut retirer ce passager");
        }
        boolean wasAccepted = trajet.hasAccepted(passagerCin);
        trajet.setAcceptedCins(Trajet.removeCin(trajet.getAcceptedCins(), passagerCin));
        if (passagerCin.equals(trajet.getPassagerCin())) {
            trajet.setPassagerCin(null);
        }
        if (wasAccepted) {
            passagers.findById(passagerCin).ifPresent(p -> {
                p.setChercheCovoit(true);
                passagers.save(p);
            });
            incrementPlaces(trajet.getConducteurCin());
            notifications.notifyPassager(passagerCin, trajet.getConducteurCin(), String.valueOf(trajet.getId()), "SUPPRESSION",
                    "Vous avez ete retire du trajet " + trajet.getDepart() + " -> " + trajet.getArrivee());
        }
        return trajets.save(trajet);
    }

    @Transactional
    public Trajet modifierPrix(Long id, double prix) {
        Trajet trajet = trajets.findById(id).orElseThrow();
        trajet.setPrix(prix);
        return trajets.save(trajet);
    }

    @Transactional
    public Trajet finishTrajet(Long id) {
        Trajet trajet = trajets.findById(id).orElseThrow();
        boolean shouldRestorePlaces = canRestorePlaces(trajet.getStatus());
        trajet.setStatus(Trajet.STATUS_FINISHED);
        if (shouldRestorePlaces) {
            restorePlaces(trajet.getConducteurCin(), trajet.getAcceptedCount());
        }
        return trajets.save(trajet);
    }

    @Transactional
    public void deleteTrajet(Long id) {
        Trajet trajet = trajets.findById(id).orElseThrow();
        if (canRestorePlaces(trajet.getStatus())) {
            restorePlaces(trajet.getConducteurCin(), trajet.getAcceptedCount());
        }
        trajets.delete(trajet);
    }

    private boolean canRestorePlaces(String status) {
        return Trajet.STATUS_IN_PROGRESS.equals(status)
                || Trajet.STATUS_PENDING.equals(status)
                || Trajet.STATUS_PENDING_APPROVAL.equals(status);
    }

    private void incrementPlaces(String conducteurCin) {
        conducteurs.findById(conducteurCin).ifPresent(c -> {
            c.setPlacesDisponibles((c.getPlacesDisponibles() == null ? 0 : c.getPlacesDisponibles()) + 1);
            conducteurs.save(c);
        });
    }

    private void restorePlaces(String conducteurCin, int placesToRestore) {
        if (placesToRestore <= 0) {
            return;
        }
        conducteurs.findById(conducteurCin).ifPresent(c -> {
            c.setPlacesDisponibles((c.getPlacesDisponibles() == null ? 0 : c.getPlacesDisponibles()) + placesToRestore);
            conducteurs.save(c);
        });
    }
}
