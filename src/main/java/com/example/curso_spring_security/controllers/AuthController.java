package com.example.curso_spring_security.controllers;

import com.example.curso_spring_security.dtos.*;
import com.example.curso_spring_security.models.RefreshToken;
import com.example.curso_spring_security.models.User;
import com.example.curso_spring_security.repositories.RefreshTokenRepository;
import com.example.curso_spring_security.repositories.UserRepository;
import com.example.curso_spring_security.services.RefreshTokenService;
import com.example.curso_spring_security.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository repository;

    private final TokenService tokenService;

    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(
            AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
            UserRepository repository, TokenService tokenService, RefreshTokenService refreshTokenService,
            RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody
            LoginRequestDTO request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            User user = repository.findByUsername(request.username());

            String accessToken = tokenService.generateAccessToken(
                    user.getUsername()
            );

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return ResponseEntity.ok(new LoginResponseDTO(accessToken, refreshToken.getToken()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody
            RegisterRequestDTO request) {

        User user = new User(passwordEncoder.encode(request.password()), request.username(), request.role());

        repository.save(user);

        return ResponseEntity.ok("Usuário criado realizado com sucesso!");
    }

    @PostMapping("/validate")
    public ResponseEntity<String> register(@RequestBody String token) {

        String username = tokenService.validateAccessToken(token);

        if (username == null) {
            return ResponseEntity.status(401).body("Token não está válido!");
        }

        return ResponseEntity.ok("Token validado!");
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String token = refreshTokenRequest.refreshToken();

        String username = tokenService.validateRefreshToken(token);

        if (username == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByToken(token);

        if (optionalRefreshToken.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        RefreshToken refreshToken = optionalRefreshToken.get();

        if (refreshToken.isRevoked()) {
            return ResponseEntity.status(401).build();
        }

        String accessToken = tokenService.generateAccessToken(username);

        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDTO logoutRequestDTO) {
        String token = logoutRequestDTO.refreshToken();

        String username = tokenService.validateRefreshToken(token);

        if (username == null) {
            return ResponseEntity.status(401).build();
        }

        boolean revoked = refreshTokenService.revokeRefreshToken(token);

        if (!revoked) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<String> saudacao() {
        return ResponseEntity.ok("Olá, mundo");
    }
}
