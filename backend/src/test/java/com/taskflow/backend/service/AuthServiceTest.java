package com.taskflow.backend.service;

import com.taskflow.backend.dto.request.RegisterRequest;
import com.taskflow.backend.dto.response.AuthResponse;
import com.taskflow.backend.exception.BadRequestException;
import com.taskflow.backend.model.Role;
import com.taskflow.backend.model.User;
import com.taskflow.backend.repository.UserRepository;
import com.taskflow.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
// Active Mockito avec JUnit 5
// Pas besoin de Spring — test unitaire pur
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    // Mockito crée un faux UserRepository
    // aucune BDD réelle

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;
    // Mockito crée AuthService et injecte les 4 mocks dedans
    // équivalent de : new AuthService(userRepository, passwordEncoder, jwtService, authManager)

    private RegisterRequest validRequest;

    @BeforeEach
    void setUp() {
        // Préparer une requête valide réutilisable dans tous les tests
        validRequest = new RegisterRequest();
        // On utilise la reflection pour setter les champs privés
        // car RegisterRequest n'a pas de setters (Lombok @Getter uniquement)
        setField(validRequest, "name", "Alice");
        setField(validRequest, "email", "alice@taskflow.com");
        setField(validRequest, "password", "password123");
    }

    // ── Tests register ────────────────────────────────────

    @Test
    void register_should_return_token_when_email_not_exists() {
        // GIVEN
        when(userRepository.existsByEmail("alice@taskflow.com"))
                .thenReturn(false);
        // → l'email n'existe pas en BDD

        when(passwordEncoder.encode("password123"))
                .thenReturn("$2a$hashedPassword");
        // → BCrypt encode le mot de passe

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    setField(user, "id", 1L);
                    return user;
                });
        // → save retourne l'user avec un ID

        when(jwtService.generateToken("alice@taskflow.com"))
                .thenReturn("eyJhbGci.token.test");
        // → JWT généré

        // WHEN
        AuthResponse response = authService.register(validRequest);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("eyJhbGci.token.test");
        assertThat(response.getName()).isEqualTo("Alice");
        assertThat(response.getEmail()).isEqualTo("alice@taskflow.com");
        assertThat(response.getRole()).isEqualTo("USER");

        // Vérifier que save a bien été appelé une fois
        verify(userRepository, times(1)).save(any(User.class));
        // Vérifier que le mot de passe a bien été encodé
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void register_should_throw_when_email_already_exists() {
        // GIVEN
        when(userRepository.existsByEmail("alice@taskflow.com"))
                .thenReturn(true);
        // → l'email existe déjà en BDD

        // WHEN + THEN
        assertThatThrownBy(() -> authService.register(validRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email déjà utilisé");

        // Vérifier que save n'a JAMAIS été appelé
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_should_encode_password_before_saving() {
        // GIVEN
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("token");

        // WHEN
        authService.register(validRequest);

        // THEN — vérifier que encode a été appelé avec le bon mot de passe
        verify(passwordEncoder).encode("password123");
        // et que save n'a pas reçu le mot de passe en clair
        verify(userRepository).save(argThat(user ->
                        user.getPassword().equals("$2a$hashed")
                // le mot de passe sauvegardé est bien le hash, pas "password123"
        ));
    }

    // ── Tests login ───────────────────────────────────────

    @Test
    void login_should_return_token_when_credentials_valid() {
        // GIVEN
        var loginRequest = new com.taskflow.backend.dto.request.LoginRequest();
        setField(loginRequest, "email", "alice@taskflow.com");
        setField(loginRequest, "password", "password123");

        User alice = new User("Alice", "alice@taskflow.com", "$2a$hashed");

        when(userRepository.findByEmail("alice@taskflow.com"))
                .thenReturn(Optional.of(alice));
        when(jwtService.generateToken("alice@taskflow.com"))
                .thenReturn("eyJhbGci.token.test");
        // authenticationManager.authenticate → ne fait rien (mock)

        // WHEN
        AuthResponse response = authService.login(loginRequest);

        // THEN
        assertThat(response.getToken()).isEqualTo("eyJhbGci.token.test");
        assertThat(response.getEmail()).isEqualTo("alice@taskflow.com");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_should_throw_when_bad_credentials() {
        // GIVEN
        var loginRequest = new com.taskflow.backend.dto.request.LoginRequest();
        setField(loginRequest, "email", "alice@taskflow.com");
        setField(loginRequest, "password", "mauvaispassword");

        // Simuler Spring Security qui rejette les credentials
        when(authenticationManager.authenticate(any()))
                .thenThrow(new org.springframework.security.authentication
                        .BadCredentialsException("Mauvais mot de passe"));

        // WHEN + THEN
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(org.springframework.security.authentication
                        .BadCredentialsException.class);

        // findByEmail ne doit jamais être appelé
        // car l'exception arrive avant
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void login_should_throw_when_user_not_found() {
        // GIVEN
        var loginRequest = new com.taskflow.backend.dto.request.LoginRequest();
        setField(loginRequest, "email", "inconnu@taskflow.com");
        setField(loginRequest, "password", "password123");

        when(userRepository.findByEmail("inconnu@taskflow.com"))
                .thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Utilisateur introuvable");
    }

    // ── Helper — setter les champs privés par reflection ─

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