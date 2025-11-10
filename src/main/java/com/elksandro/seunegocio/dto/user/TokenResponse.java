package com.elksandro.seunegocio.dto.user;

public record TokenResponse(
    String token,
    long expiresIn
) {}