package com.taskflow.backend.service;

import com.taskflow.backend.dto.request.CreateProjectRequest;
import com.taskflow.backend.dto.request.UpdateProjectRequest;
import com.taskflow.backend.dto.response.ProjectResponse;
import com.taskflow.backend.exception.BadRequestException;
import com.taskflow.backend.exception.ResourceNotFoundException;
import com.taskflow.backend.model.Project;
import com.taskflow.backend.model.User;
import com.taskflow.backend.repository.ProjectRepository;
import com.taskflow.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // Récupérer tous les projets de l'utilisateur connecté
    // (owner + membre)
    public List<ProjectResponse> getMyProjects(String email) {
        User user = getUserByEmail(email);

        // Projets dont il est owner
        List<Project> ownedProjects = projectRepository.findByOwnerId(user.getId());

        // Projets dont il est membre
        List<Project> memberProjects = projectRepository.findByMembersId(user.getId());

        // Fusionner les deux listes sans doublons
        ownedProjects.addAll(memberProjects.stream()
                .filter(p -> !ownedProjects.contains(p))
                .toList());

        return ownedProjects.stream()
                .map(this::toResponse)
                .toList();
    }

    // Créer un projet
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, String email) {
        User owner = getUserByEmail(email);

        Project project = new Project(
                request.getName(),
                request.getDescription(),
                owner
        );

        return toResponse(projectRepository.save(project));
    }

    // Récupérer un projet par ID
    public ProjectResponse getProjectById(Long id, String email) {
        Project project = getProjectOrThrow(id);
        checkAccess(project, email);
        return toResponse(project);
    }

    // Modifier un projet — owner seulement
    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request, String email) {
        Project project = getProjectOrThrow(id);
        checkOwnership(project, email);

        if (request.getName() != null) {
            project.setName(request.getName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }

        return toResponse(projectRepository.save(project));
    }

    // Supprimer un projet — owner seulement
    @Transactional
    public void deleteProject(Long id, String email) {
        Project project = getProjectOrThrow(id);
        checkOwnership(project, email);
        projectRepository.delete(project);
    }

    // Inviter un membre
    @Transactional
    public ProjectResponse addMember(Long projectId, Long userId, String email) {
        Project project = getProjectOrThrow(projectId);
        checkOwnership(project, email);

        User newMember = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable : " + userId));

        // Vérifier que l'utilisateur n'est pas déjà membre
        if (project.getMembers().contains(newMember)) {
            throw new BadRequestException("Cet utilisateur est déjà membre du projet");
        }

        // Vérifier que ce n'est pas l'owner
        if (project.getOwner().getId().equals(userId)) {
            throw new BadRequestException("L'owner ne peut pas être ajouté comme membre");
        }

        project.getMembers().add(newMember);
        return toResponse(projectRepository.save(project));
    }

    // ── Méthodes privées ──────────────────────────────────

    // Convertir Project en ProjectResponse
    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getName(),
                project.getOwner().getEmail(),
                project.getMembers().size(),
                project.getTasks().size(),
                project.getCreatedAt()
        );
    }

    // Récupérer un projet ou lancer 404
    private Project getProjectOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projet introuvable : " + id));
    }

    // Récupérer un user par email ou lancer 404
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable : " + email));
    }

    // Vérifier que l'utilisateur a accès au projet
    // (owner ou membre)
    private void checkAccess(Project project, String email) {
        boolean isOwner = project.getOwner().getEmail().equals(email);
        boolean isMember = project.getMembers().stream()
                .anyMatch(m -> m.getEmail().equals(email));

        if (!isOwner && !isMember) {
            throw new BadRequestException("Accès refusé à ce projet");
        }
    }

    // Vérifier que l'utilisateur est owner
    private void checkOwnership(Project project, String email) {
        if (!project.getOwner().getEmail().equals(email)) {
            throw new BadRequestException(
                    "Seul l'owner peut effectuer cette action");
        }
    }
}