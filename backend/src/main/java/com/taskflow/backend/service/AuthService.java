package com.taskflow.backend.service;

import com.taskflow.backend.dto.request.LoginRequest;
import com.taskflow.backend.dto.request.RegisterRequest;
import com.taskflow.backend.dto.response.AuthResponse;
import com.taskflow.backend.exception.BadRequestException;
import com.taskflow.backend.model.User;
import com.taskflow.backend.repository.UserRepository;
import com.taskflow.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    // @Transactional — si save échoue, rollback automatique
    public AuthResponse register(RegisterRequest request) {

        // Vérifier que l'email n'existe pas déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "Email déjà utilisé : " + request.getEmail());
        }

        // Créer l'utilisateur avec mot de passe encodé
        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
                // passwordEncoder.encode → BCrypt hash
                // jamais stocker le mot de passe en clair
        );

        userRepository.save(user);

        // Générer le token JWT
        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public AuthResponse login(LoginRequest request) {

        // Spring Security vérifie email + password
        // Lance une exception si invalide → 401 automatique
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Si authentification réussie
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadRequestException("Utilisateur introuvable"));

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}