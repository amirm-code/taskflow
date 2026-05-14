package com.taskflow.backend.controller;

import com.taskflow.backend.dto.response.UserResponse;
import com.taskflow.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
// ← Toutes les méthodes de ce controller nécessitent le rôle ADMIN
public class AdminController {

    private final AdminService adminService;

    // GET /api/admin/users — liste tous les utilisateurs
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    // PUT /api/admin/users/{id}/role — changer le rôle d'un user
    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable Long id,
            @RequestParam String role) {
        return ResponseEntity.ok(adminService.updateRole(id, role));
    }

    // DELETE /api/admin/projects/{id} — supprimer n'importe quel projet
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        adminService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}