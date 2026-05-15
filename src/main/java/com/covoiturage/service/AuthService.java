package com.covoiturage.service; import java.util.Optional;

import com.covoiturage.dto.LoginRequest;
import com.covoiturage.dto.UserRegistrationForm;
import com.covoiturage.entity.Conducteur;
import com.covoiturage.entity.Passager;
 public interface AuthService{Optional<Object> authenticate(LoginRequest request);Optional<Object> authenticateAny(LoginRequest request);Conducteur registerConducteur(UserRegistrationForm f);Passager registerPassager(UserRegistrationForm f);String hashPassword(String p);boolean verifyPassword(String p,String h);}