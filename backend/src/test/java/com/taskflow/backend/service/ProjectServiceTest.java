package com.taskflow.backend.service;

import com.taskflow.backend.dto.request.CreateProjectRequest;
import com.taskflow.backend.dto.response.ProjectResponse;
import com.taskflow.backend.exception.BadRequestException;
import com.taskflow.backend.exception.ResourceNotFoundException;
import com.taskflow.backend.model.Project;
import com.taskflow.backend.model.User;
import com.taskflow.backend.repository.ProjectRepository;
import com.taskflow.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    private User alice;
    private Project project;
    private CreateProjectRequest validRequest;

    @BeforeEach
    void setUp() {
        alice = new User("Alice", "alice@taskflow.com", "$2a$hashed");
        setField(alice, "id", 1L);

        project = new Project("Project A", "Description of Project A", alice);
        setField(project, "id", 1L);

        validRequest = new CreateProjectRequest();
        setField(validRequest, "name", "Project A");
        setField(validRequest, "description", "Description of Project A");
    }

    @Test
    void createProject_should_return_project_when_valid() {
        //Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(alice));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        //When
        ProjectResponse response = projectService.createProject(validRequest, "alice@taskflow.com");

        //Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Project A");
        assertThat(response.getDescription()).isEqualTo("Description of Project A");
        assertThat(response.getOwnerEmail()).isEqualTo("alice@taskflow.com");
        verify(projectRepository, times(1)).save(any(Project.class));

    }

    @Test
    void getProjectByID_should_throw_when_not_found() {
        //GIVEN
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        //WHEN + THEN
        assertThatThrownBy(() -> projectService.getProjectById(1L, "alice@taskflow.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Projet introuvable : 1");
    }

    @Test
    void getProjectById_should_throw_when_user_has_no_access() {
        //GIVEN
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        //WHEN + THEN
        assertThatThrownBy(() -> projectService.getProjectById(1L, ",noaccess@gmail.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Accès refusé à ce projet");
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