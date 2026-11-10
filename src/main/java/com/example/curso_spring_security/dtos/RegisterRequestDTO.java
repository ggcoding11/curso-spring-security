package com.example.curso_spring_security.dtos;

import com.example.curso_spring_security.enums.UserRole;

public record RegisterRequestDTO(
        String username,
        String password,
        UserRole role
) {
}
