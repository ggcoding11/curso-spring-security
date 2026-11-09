package com.example.curso_spring_security.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@NoArgsConstructor
@Table(name = "tb_refresh_tokens")
public class RefreshToken {
    public RefreshToken(String token, Instant expirationDate, boolean revoked, User user) {
        this.token = token;
        this.expirationDate = expirationDate;
        this.revoked = revoked;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    private Instant expirationDate;

    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}