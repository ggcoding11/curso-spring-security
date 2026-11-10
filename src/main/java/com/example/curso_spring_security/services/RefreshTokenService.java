package com.example.curso_spring_security.services;

import com.example.curso_spring_security.models.RefreshToken;
import com.example.curso_spring_security.models.User;
import com.example.curso_spring_security.repositories.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class RefreshTokenService {
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(TokenService tokenService, RefreshTokenRepository refreshTokenRepository) {
        this.tokenService = tokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user) {

        String token = tokenService.generateRefreshToken(
                user.getUsername()
        );

        RefreshToken refreshToken = new RefreshToken(
                token, Instant.now().plus(7, ChronoUnit.DAYS), false, user
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public boolean revokeRefreshToken(String token) {

        Optional<RefreshToken> optionalToken =
                refreshTokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return false;
        }

        RefreshToken refreshToken = optionalToken.get();

        if (refreshToken.isRevoked()) {
            return false;
        }

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);

        return true;
    }
}
