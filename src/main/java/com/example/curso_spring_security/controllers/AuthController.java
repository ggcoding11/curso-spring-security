package com.example.curso_spring_security.controllers;

import com.example.curso_spring_security.dtos.CredentialsRequest;
import com.example.curso_spring_security.models.User;
import com.example.curso_spring_security.repositories.UserRepository;
import com.example.curso_spring_security.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody
            CredentialsRequest request) {

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
            CredentialsRequest request) {

        User user = new User(passwordEncoder.encode(request.password()), request.username());

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
}
