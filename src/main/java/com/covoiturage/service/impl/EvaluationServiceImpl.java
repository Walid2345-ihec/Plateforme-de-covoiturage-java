package com.covoiturage.service.impl;

import com.covoiturage.entity.Evaluation;
import com.covoiturage.repository.ConducteurRepository;
import com.covoiturage.repository.EvaluationRepository;
import com.covoiturage.service.EvaluationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvaluationServiceImpl implements EvaluationService {
    private final EvaluationRepository evaluations;
    private final ConducteurRepository conducteurs;

    public EvaluationServiceImpl(EvaluationRepository evaluations, ConducteurRepository conducteurs) {
        this.evaluations = evaluations;
        this.conducteurs = conducteurs;
    }

    public List<Evaluation> all() {
        return evaluations.findAll();
    }

    public List<Evaluation> forConducteur(String cin) {
        return evaluations.findByConducteurCinOrderByDateCreationDesc(cin);
    }

    // Source Swing : Gestion_covoiturage.creerEvaluation() -> recalculerMoyenneConducteur()
    @Transactional
    public Evaluation evaluate(String passagerCin, String passagerName, String conducteurCin, String trajetId, int rating, String comment) {
        int safeRating = Math.max(1, Math.min(5, rating));
        Evaluation ev = evaluations.save(Evaluation.builder()
                .evaluationId("EVAL_" + System.currentTimeMillis() + "_" + passagerCin)
                .passagerCin(passagerCin)
                .passagerName(passagerName)
                .conducteurCin(conducteurCin)
                .trajetId(trajetId)
                .rating(safeRating)
                .comment(comment)
                .dateCreation(LocalDateTime.now())
                .build());
        conducteurs.findById(conducteurCin).ifPresent(c -> {
            c.setMoyenneEvaluation(Math.max(0.0, Math.min(5.0, moyenne(conducteurCin))));
            conducteurs.save(c);
        });
        return ev;
    }

    public double moyenne(String cin) {
        return forConducteur(cin).stream().mapToInt(Evaluation::getRating).average().orElse(0.0);
    }
}
