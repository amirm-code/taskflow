package com.taskflow.backend.service;

import com.taskflow.backend.dto.request.CreateTaskRequest;
import com.taskflow.backend.dto.request.UpdateTaskRequest;
import com.taskflow.backend.dto.response.PageResponse;
import com.taskflow.backend.dto.response.TaskResponse;
import com.taskflow.backend.exception.BadRequestException;
import com.taskflow.backend.exception.ResourceNotFoundException;
import com.taskflow.backend.model.Project;
import com.taskflow.backend.model.Task;
import com.taskflow.backend.model.TaskStatus;
import com.taskflow.backend.model.User;
import com.taskflow.backend.repository.ProjectRepository;
import com.taskflow.backend.repository.TaskRepository;
import com.taskflow.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // Récupérer toutes les tâches d'un projet
    public List<TaskResponse> getTasksByProject(Long projectId, String email) {
        Project project = getProjectOrThrow(projectId);
        checkAccess(project, email);

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Créer une tâche dans un projet
    @Transactional
    public TaskResponse createTask(Long projectId,
                                   CreateTaskRequest request,
                                   String email) {
        Project project = getProjectOrThrow(projectId);
        checkAccess(project, email);

        Task task = new Task(
                request.getTitle(),
                request.getDescription(),
                project
        );

        // Assigner si assigneeId fourni
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Utilisateur introuvable : " + request.getAssigneeId()));
            task.setAssignee(assignee);
        }

        return toResponse(taskRepository.save(task));
    }

    // Modifier une tâche
    @Transactional
    public TaskResponse updateTask(Long projectId,
                                   Long taskId,
                                   UpdateTaskRequest request,
                                   String email) {
        Project project = getProjectOrThrow(projectId);
        checkAccess(project, email);

        Task task = getTaskOrThrow(taskId, projectId);

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Utilisateur introuvable : " + request.getAssigneeId()));
            task.setAssignee(assignee);
        }

        return toResponse(taskRepository.save(task));
    }

    // Changer uniquement le statut — pour le Kanban
    @Transactional
    public TaskResponse updateStatus(Long projectId,
                                     Long taskId,
                                     TaskStatus status,
                                     String email) {
        Project project = getProjectOrThrow(projectId);
        checkAccess(project, email);

        Task task = getTaskOrThrow(taskId, projectId);
        task.setStatus(status);

        return toResponse(taskRepository.save(task));
    }

    // Supprimer une tâche
    @Transactional
    public void deleteTask(Long projectId, Long taskId, String email) {
        Project project = getProjectOrThrow(projectId);
        checkAccess(project, email);

        Task task = getTaskOrThrow(taskId, projectId);
        taskRepository.delete(task);
    }

    // Récupérer les tâches d'un projet avec pagination et filtre optionnel
    public PageResponse<TaskResponse> getTasksPaginated(
            Long projectId,
            String email,
            int page,
            int size,
            String sortBy,
            String status) {

        Project project = getProjectOrThrow(projectId);
        checkAccess(project, email);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));

        // Filtrer par statut si fourni, sinon toutes les tâches
        Page<Task> taskPage;
        if (status != null && !status.isEmpty()) {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            taskPage = taskRepository.findByProjectIdAndStatus(projectId, taskStatus, pageable);
        } else {
            taskPage = taskRepository.findByProjectId(projectId, pageable);
        }

        return new PageResponse<>(
                taskPage.getContent().stream()
                        .map(this::toResponse)
                        .toList(),
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.isFirst(),
                taskPage.isLast()
        );
    }

    // ── Méthodes privées ──────────────────────────────

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getAssignee() != null ? task.getAssignee().getName() : null,
                task.getAssignee() != null ? task.getAssignee().getEmail() : null,
                task.getCreatedAt()
        );
    }

    private Project getProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projet introuvable : " + id));
    }

    private Task getTaskOrThrow(Long taskId, Long projectId) {
        return taskRepository.findByProjectId(projectId)
                .stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tâche introuvable : " + taskId));
    }

    private void checkAccess(Project project, String email) {
        boolean isOwner = project.getOwner().getEmail().equals(email);
        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getEmail().equals(email));

        if (!isOwner && !isMember) {
            throw new BadRequestException("Accès refusé à ce projet");
        }
    }
}