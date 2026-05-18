package com.covoiturage.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.covoiturage.dto.LoginRequest;
import com.covoiturage.dto.UserRegistrationForm;
import com.covoiturage.entity.Conducteur;
import com.covoiturage.entity.Passager;
import com.covoiturage.repository.AdminRepository;
import com.covoiturage.repository.ConducteurRepository;
import com.covoiturage.repository.PassagerRepository;
import com.covoiturage.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
    private final ConducteurRepository conducteurs;
    private final PassagerRepository passagers;
    private final AdminRepository admins;

    public AuthServiceImpl(ConducteurRepository conducteurs, PassagerRepository passagers, AdminRepository admins) {
        this.conducteurs = conducteurs;
        this.passagers = passagers;
        this.admins = admins;
    }

    // Source Swing : EnhancedLoginPanel.java -> conducteur banned ou carte rouge refuse
    public Optional<Object> authenticate(LoginRequest request) {
        if ("conducteur".equalsIgnoreCase(request.getRole())) {
            return conducteurs.findById(request.getCin()).filter(u -> {
                if (Boolean.TRUE.equals(u.getBanned()) || "rouge".equalsIgnoreCase(u.getCarte())) {
                    throw new IllegalStateException("Votre compte a ete suspendu");
                }
                return verifyPassword(request.getPassword(), u.getPasswordHash());
            }).map(Object.class::cast);
        }
        if ("passager".equalsIgnoreCase(request.getRole())) {
            return passagers.findById(request.getCin()).filter(u -> {
                if (Boolean.TRUE.equals(u.getBanned())) {
                    throw new IllegalStateException("Votre compte a ete suspendu");
                }
                return verifyPassword(request.getPassword(), u.getPasswordHash());
            }).map(Object.class::cast);
        }
        if ("admin".equalsIgnoreCase(request.getRole())) {
            return admins.findById(request.getCin()).filter(u -> verifyPassword(request.getPassword(), u.getPasswordHash())).map(Object.class::cast);
        }
        return Optional.empty();
    }

    public Optional<Object> authenticateAny(LoginRequest request) {
        var adminOpt = admins.findById(request.getCin()).filter(u -> verifyPassword(request.getPassword(), u.getPasswordHash()));
        if (adminOpt.isPresent()) {
            // Admin can choose passager or conducteur role
            request.setRole("admin");
            return adminOpt.map(Object.class::cast);
        }
        // Non-admin users must select the correct role matching their account type
        return authenticate(request);
    }

    @Transactional
    public Conducteur registerConducteur(UserRegistrationForm f) {
        if (conducteurs.existsById(f.getCin())) {
            throw new IllegalArgumentException("Ce CIN est deja utilise par un conducteur");
        }
        if (passagers.existsById(f.getCin())) {
            throw new IllegalArgumentException("Ce CIN est deja utilise par un passager");
        }
        return conducteurs.save(Conducteur.builder().cin(f.getCin()).nom(f.getNom()).prenom(f.getPrenom()).tel(f.getTel()).anneeUniv(f.getAnneeUniv()).adresse(f.getAdresse()).mail(f.getMail()).passwordHash(hashPassword(f.getPassword())).nomVoiture(f.getNomVoiture()).marqueVoiture(f.getMarqueVoiture()).matricule(f.getMatricule()).placesDisponibles(f.getPlacesDisponibles()).weeklySchedule("").moyenneEvaluation(0.0).carte("verte").banned(false).build());
    }

    @Transactional
    public Passager registerPassager(UserRegistrationForm f) {
        if (passagers.existsById(f.getCin())) {
            throw new IllegalArgumentException("Ce CIN est deja utilise par un passager");
        }
        if (conducteurs.existsById(f.getCin())) {
            throw new IllegalArgumentException("Ce CIN est deja utilise par un conducteur");
        }
        return passagers.save(Passager.builder().cin(f.getCin()).nom(f.getNom()).prenom(f.getPrenom()).tel(f.getTel()).anneeUniv(f.getAnneeUniv()).adresse(f.getAdresse()).mail(f.getMail()).passwordHash(hashPassword(f.getPassword())).chercheCovoit(true).carte("verte").banned(false).build());
    }

    public String hashPassword(String p) {
        try {
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            byte[] h = d.digest((p == null ? "" : p).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean verifyPassword(String p, String h) {
        return h != null && (h.equals(p) || h.equals(hashPassword(p)));
    }
}
