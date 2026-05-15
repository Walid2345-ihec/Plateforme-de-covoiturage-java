package com.covoiturage.service.impl;

import com.covoiturage.entity.Reclamation;
import com.covoiturage.repository.ReclamationRepository;
import com.covoiturage.service.NotificationService;
import com.covoiturage.service.ReclamationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReclamationServiceImpl implements ReclamationService {
    private final ReclamationRepository reclamations;
    private final NotificationService notifications;

    public ReclamationServiceImpl(ReclamationRepository reclamations, NotificationService notifications) {
        this.reclamations = reclamations;
        this.notifications = notifications;
    }

    public List<Reclamation> all() {
        return reclamations.findAllByOrderByCreatedAtDesc();
    }

    // Source Swing : Gestion_covoiturage.hasReclamation() + submitReclamation()
    @Transactional
    public Reclamation submit(String reservationId, String complainantId, String complainantRole,
                              String accusedId, String accusedRole, String preset, String message) {
        if (reclamations.existsByReservationIdAndComplainantIdAndAccusedId(reservationId, complainantId, accusedId)) {
            throw new IllegalStateException("Une reclamation existe deja pour cette reservation");
        }
        Reclamation rec = reclamations.save(Reclamation.builder()
                .id("RECL_" + System.currentTimeMillis() + "_" + complainantId)
                .reservationId(reservationId)
                .complainantId(complainantId)
                .complainantRole(complainantRole)
                .accusedId(accusedId)
                .accusedRole(accusedRole)
                .preset(preset)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build());
        notifications.notifyAdmin("RECLAMATION", "Nouvelle reclamation: " + message, complainantId, accusedId, reservationId);
        return rec;
    }
}
