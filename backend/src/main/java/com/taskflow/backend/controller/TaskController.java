package com.taskflow.backend.controller;

import com.taskflow.backend.dto.request.CreateTaskRequest;
import com.taskflow.backend.dto.request.UpdateTaskRequest;
import com.taskflow.backend.dto.response.TaskResponse;
import com.taskflow.backend.model.TaskStatus;
import com.taskflow.backend.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
// Lombok génère : TaskController(TaskService taskService)
public class TaskController {

    private final TaskService taskService;

    // GET /api/projects/{projectId}/tasks
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                taskService.getTasksByProject(projectId, userDetails.getUsername())
        );
    }

    // POST /api/projects/{projectId}/tasks
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(projectId, request, userDetails.getUsername()));
    }

    // PUT /api/projects/{projectId}/tasks/{taskId}
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                taskService.updateTask(projectId, taskId, request, userDetails.getUsername())
        );
    }

    // PATCH /api/projects/{projectId}/tasks/{taskId}/status
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam TaskStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                taskService.updateStatus(projectId, taskId, status, userDetails.getUsername())
        );
    }

    // DELETE /api/projects/{projectId}/tasks/{taskId}
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserDetails userDetails) {
        taskService.deleteTask(projectId, taskId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}