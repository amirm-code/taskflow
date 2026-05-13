package com.taskflow.backend.dto.response;

import com.taskflow.backend.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long projectId;
    private String projectName;
    private String assigneeName;  // null si pas assignée
    private String assigneeEmail; // null si pas assignée
    private LocalDateTime createdAt;
}