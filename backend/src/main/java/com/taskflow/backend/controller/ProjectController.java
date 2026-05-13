package com.taskflow.backend.controller;

import com.taskflow.backend.dto.request.CreateProjectRequest;
import com.taskflow.backend.dto.request.UpdateProjectRequest;
import com.taskflow.backend.dto.response.ProjectResponse;
import com.taskflow.backend.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // GET /api/projects — mes projets
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getMyProjects(
            @AuthenticationPrincipal UserDetails userDetails) {
        // @AuthenticationPrincipal → récupère l'utilisateur connecté
        // depuis le SecurityContext — mis en place par JwtFilter
        return ResponseEntity.ok(
                projectService.getMyProjects(userDetails.getUsername())
        );
    }

    // POST /api/projects — créer un projet
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)  // ← 201 Created
                .body(projectService.createProject(request, userDetails.getUsername()));
    }

    // GET /api/projects/{id} — détail d'un projet
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                projectService.getProjectById(id, userDetails.getUsername())
        );
    }

    // PUT /api/projects/{id} — modifier un projet
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                projectService.updateProject(id, request, userDetails.getUsername())
        );
    }

    // DELETE /api/projects/{id} — supprimer un projet
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        projectService.deleteProject(id, userDetails.getUsername());
        return ResponseEntity.noContent().build(); // ← 204 No Content
    }

    // POST /api/projects/{id}/members/{userId} — inviter un membre
    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<ProjectResponse> addMember(
            @PathVariable Long id,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                projectService.addMember(id, userId, userDetails.getUsername())
        );
    }
}