package com.covoiturage.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRegistrationForm {
    @NotBlank(message = "Le CIN est obligatoire")
    @Pattern(regexp = "^[0-9]{8}$", message = "Le CIN doit contenir exactement 8 chiffres")
    private String cin;

    @NotBlank(message = "Le nom est obligatoire")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le nom ne doit contenir que des lettres, espaces, apostrophes et tirets")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Le prénom ne doit contenir que des lettres, espaces, apostrophes et tirets")
    private String prenom;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^[0-9]{8}$", message = "Le téléphone doit contenir exactement 8 chiffres")
    private String tel;

    @Min(value = 2000, message = "L'année universitaire doit être à partir de 2000")
    private Integer anneeUniv;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @NotBlank(message = "L'email est obligatoire")
    @Pattern(regexp = "(?i)^[A-Z0-9._%+-]+@((gmail\\.com)|([A-Z0-9.-]+\\.tn))$", message = "Email invalide (gmail.com ou domaine.tn)")
    private String mail;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$", message = "Le mot de passe doit contenir au moins 8 caractères, 1 minuscule, 1 majuscule, 1 chiffre et 1 caractère spécial")
    private String password;

    private String role;

    @Pattern(regexp = "^$|^[a-zA-Z0-9À-ÿ\\s'-]+$", message = "Le nom du véhicule ne doit contenir que des lettres, chiffres, espaces, apostrophes et tirets")
    private String nomVoiture;

    private String marqueVoiture;

    @Pattern(regexp = "^$|^[0-9]{1,3}TU[0-9]{4}$", message = "Le matricule doit avoir le format XXXTUYYYYou être vide")
    private String matricule;

    private Integer placesDisponibles = 1;
}
