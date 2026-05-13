package com.taskflow.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateTaskRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 2, max = 200, message = "Le titre doit faire entre 2 et 200 caractères")
    private String title;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    // ID de l'utilisateur assigné — optionnel
    private Long assigneeId;
}