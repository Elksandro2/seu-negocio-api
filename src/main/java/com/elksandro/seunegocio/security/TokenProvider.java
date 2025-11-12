package com.elksandro.seunegocio.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.elksandro.seunegocio.model.User;
import com.elksandro.seunegocio.security.exception.TokenException;

import jakarta.annotation.PostConstruct;

@Service
public class TokenProvider {

    @Value("${api.secret}")
    private String secret;

    private final long EXPIRATION_TIME_IN_SECONDS = 259200; // 3 dias

    private Algorithm algorithm;

    @PostConstruct
    public void setUp() {
        Base64.getEncoder().encode(secret.getBytes());
        algorithm = Algorithm.HMAC256(secret.getBytes());
    }

    public String generateToken(User user) {
        try {
            return JWT.create()
                .withSubject(String.valueOf(user.getId()))
                .withIssuedAt(Instant.now())
                .withExpiresAt(expirationToken())
                .sign(algorithm)
                .strip();
        } catch (JWTCreationException e){
            throw new TokenException(e.getMessage());
        }
    }

    private Instant expirationToken() {
        return LocalDateTime.now().plusSeconds(EXPIRATION_TIME_IN_SECONDS).toInstant(ZoneOffset.of("-03:00"));
    }

    public String getSubjectByToken(String token) {
        try {
            return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
        } catch (JWTVerificationException e){
            return null;
        }
    }

    public long getExpirationTimeInSeconds() {
        return EXPIRATION_TIME_IN_SECONDS;
    }
}
