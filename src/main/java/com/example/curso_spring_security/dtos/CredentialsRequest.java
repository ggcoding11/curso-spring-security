package com.example.curso_spring_security.dtos;

public record CredentialsRequest(
        String username,
        String password
) {}
