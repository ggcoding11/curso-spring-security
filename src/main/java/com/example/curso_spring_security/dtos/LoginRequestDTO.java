package com.example.curso_spring_security.dtos;

public record LoginRequestDTO(
        String username,
        String password
) {
}
