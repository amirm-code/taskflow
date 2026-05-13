package com.taskflow.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private String ownerName;
    private String ownerEmail;
    private int memberCount;
    private int taskCount;
    private LocalDateTime createdAt;
}
