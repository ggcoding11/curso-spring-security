package com.example.curso_spring_security.controllers;

import com.example.curso_spring_security.dtos.LoginRequest;
import com.example.curso_spring_security.dtos.RegisterRequest;
import com.example.curso_spring_security.models.User;
import com.example.curso_spring_security.repositories.UserRepository;
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

@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository repository;

    private final TokenService tokenService;

    public AuthController(
            AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
            UserRepository repository, TokenService tokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody
            LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            String token = tokenService.generateToken(
                    request.username()
            );

            if (token == null) {
                return ResponseEntity.status(401).body("Erro ao gerar o token");
            }

            return ResponseEntity.ok(token);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Usuário ou senha inválidos");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody
            RegisterRequest request) {

        User user = new User(passwordEncoder.encode(request.password()), request.username(), request.role());

        repository.save(user);

        return ResponseEntity.ok("Usuário criado realizado com sucesso!");
    }

    @PostMapping("/validate")
    public ResponseEntity<String> register(@RequestBody String token) {

        String username = tokenService.validateToken(token);

        if (username == null) {
            return ResponseEntity.status(401).body("Token não está válido!");
        }

        return ResponseEntity.ok("Token validado!");
    }

    @GetMapping
    public ResponseEntity<String> saudacao() {
        return ResponseEntity.ok("Olá, mundo");
    }
}
