package com.covoiturage.service;

import java.util.List;
import java.util.Optional;

import com.covoiturage.entity.Admin;
import com.covoiturage.entity.Conducteur;
import com.covoiturage.entity.Passager;

public interface UserService {
    List<Conducteur> conducteurs();

    List<Passager> passagers();

    List<Admin> admins();

    Optional<Conducteur> conducteur(String cin);

    Optional<Passager> passager(String cin);

    Optional<Admin> admin(String cin);

    Admin defaultAdmin();

    String displayName(String role, String cin);

    void deleteUser(String role, String cin);

    void updateCard(String role, String cin, String carte);
}
