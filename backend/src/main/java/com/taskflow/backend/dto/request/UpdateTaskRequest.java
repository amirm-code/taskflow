package com.taskflow.backend.dto.request;

import com.taskflow.backend.model.TaskStatus;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateTaskRequest {

    @Size(min = 2, max = 200, message = "Le titre doit faire entre 2 et 200 caractères")
    private String title;

    @Size(max = 1000)
    private String description;

    private TaskStatus status;

    private Long assigneeId;
}