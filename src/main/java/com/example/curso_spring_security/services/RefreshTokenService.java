package com.example.curso_spring_security.services;

import com.example.curso_spring_security.models.RefreshToken;
import com.example.curso_spring_security.models.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class RefreshTokenService {
    private final TokenService tokenService;

    public RefreshTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public RefreshToken createRefreshToken(User user) {

        String token = tokenService.generateRefreshToken(
                user.getUsername()
        );

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpirationDate(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        return refreshTokenRepository.save(refreshToken);
    }
}
