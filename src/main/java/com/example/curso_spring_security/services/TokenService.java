package com.example.curso_spring_security.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.curso_spring_security.models.User;
import com.example.curso_spring_security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {
    @Value("${security.jwt.secret}")
    private String secretKey;

    @Autowired
    private UserRepository userRepository;

    public String generateToken(String username) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            String token = JWT.create()
                    .withSubject(username)
                    .withExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                    .withIssuer("curso-spring-security")
                    .sign(algorithm);

            return token;
        } catch (JWTCreationException exception) {
            return null;
        }
    }

    public String validateToken(String token) {
        DecodedJWT decodedJWT;

        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("curso-spring-security")
                    .build();

            decodedJWT = verifier.verify(token);

            return decodedJWT.getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
