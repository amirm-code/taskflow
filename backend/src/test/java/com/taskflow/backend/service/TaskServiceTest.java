package com.taskflow.backend.service;

import com.taskflow.backend.dto.request.CreateTaskRequest;
import com.taskflow.backend.dto.request.UpdateTaskRequest;
import com.taskflow.backend.dto.response.TaskResponse;
import com.taskflow.backend.model.Project;
import com.taskflow.backend.model.Task;
import com.taskflow.backend.model.TaskStatus;
import com.taskflow.backend.model.User;
import com.taskflow.backend.repository.ProjectRepository;
import com.taskflow.backend.repository.TaskRepository;
import com.taskflow.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User alice;
    private Project project;
    private Task task;

    private CreateTaskRequest validRequest;
    private UpdateTaskRequest validUpdateRequest;

    @BeforeEach
    void setup() {
        alice = new User("Alice", "alice@taskflow.com", "$2a$hashed");
        setField(alice, "id", 1L);

        project = new Project("Project A", "Description of Project A", alice);
        setField(project, "id", 1L);

        task = new Task("Task 1", "Description of Task 1", project);
        setField(task, "id", 1L);

        validRequest = new CreateTaskRequest();
        setField(validRequest, "title", "Task 1");
        setField(validRequest, "description", "Description of Task 1");

        validUpdateRequest = new UpdateTaskRequest();
        setField(validUpdateRequest, "status", TaskStatus.IN_PROGRESS);

    }

    @Test
    void createTask_should_return_task_when_valid() {
        //Given
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        //WHEN
        TaskResponse response = taskService.createTask(1L, validRequest, "alice@taskflow.com");

        //THEN
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Task 1");
        assertThat(response.getDescription()).isEqualTo("Description of Task 1");
        assertThat(response.getProjectId()).isEqualTo(1L);
    }

    @Test
    void createTask_should_throw_when_project_not_found() {
        //Given
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        //WHEN + THEN
        assertThatThrownBy(() -> taskService.createTask(1L, validRequest, "alice@taskflow.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Projet introuvable : 1");

    }

    @Test
    void updateStatus_should_update_task_status() {
        // GIVEN
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectId(anyLong())).thenReturn(List.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // WHEN
        TaskResponse response = taskService.updateStatus(
                1L, 1L, TaskStatus.IN_PROGRESS, "alice@taskflow.com"
        );

        // THEN
        assertThat(response.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }


    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set field: " + fieldName, e);
        }
    }

}
