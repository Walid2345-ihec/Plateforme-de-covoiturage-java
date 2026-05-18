package com.covoiturage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String cin;

    @NotBlank
    private String password;

    @NotBlank
    private String role;
}