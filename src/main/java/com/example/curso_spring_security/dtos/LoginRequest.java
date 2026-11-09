package com.example.curso_spring_security.dtos;

public record LoginRequest(
        String username,
        String password
) {
}
