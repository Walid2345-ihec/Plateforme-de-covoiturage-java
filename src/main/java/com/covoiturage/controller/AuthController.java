package com.covoiturage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.covoiturage.dto.LoginRequest;
import com.covoiturage.dto.UserRegistrationForm;
import com.covoiturage.entity.Admin;
import com.covoiturage.entity.Conducteur;
import com.covoiturage.entity.Passager;
import com.covoiturage.security.SessionUser;
import com.covoiturage.service.AuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginRequest request, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) return "login";
        try {
            return auth.authenticateAny(request).map(user -> {
                String name = "";
                if (user instanceof Conducteur c) name = c.getPrenom() + " " + c.getNom();
                if (user instanceof Passager p) name = p.getPrenom() + " " + p.getNom();
                if (user instanceof Admin a) name = a.getPrenom() + " " + a.getNom();
                SessionUser.login(session, request.getCin(), request.getRole().toLowerCase(), name);
                return "redirect:/" + request.getRole().toLowerCase() + "/dashboard";
            }).orElseGet(() -> {
                model.addAttribute("error", "CIN, mot de passe ou role invalide");
                return "login";
            });
        } catch (IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            return "login";
        }
    }

    @GetMapping("/register/{role}")
    public String register(@PathVariable String role, Model model) {
        UserRegistrationForm form = new UserRegistrationForm();
        form.setRole(role);
        model.addAttribute("form", form);
        return "register";
    }

    @PostMapping("/register")
    public String registerPost(@Valid @ModelAttribute("form") UserRegistrationForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            if ("conducteur".equals(form.getRole())) auth.registerConducteur(form);
            else auth.registerPassager(form);
            model.addAttribute("success", "Compte cree. Vous pouvez vous connecter.");
            model.addAttribute("loginRequest", new LoginRequest());
            return "login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
