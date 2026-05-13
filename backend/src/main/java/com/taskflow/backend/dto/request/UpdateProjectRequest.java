package com.taskflow.backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateProjectRequest {

    @Size(min = 2, max = 100, message = "Le nom doit faire entre 2 et 100 caractères")
    private String name;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
}
