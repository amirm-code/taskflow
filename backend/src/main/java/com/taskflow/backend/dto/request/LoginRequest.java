package com.taskflow.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}