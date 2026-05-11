package com.taskflow.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Plusieurs tâches appartiennent à un seul projet
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    public Task(String title, String description, Project project) {
        this.title = title;
        this.description = description;
        this.project = project;
        this.status = TaskStatus.TODO;
        this.createdAt = LocalDateTime.now();
    }
}