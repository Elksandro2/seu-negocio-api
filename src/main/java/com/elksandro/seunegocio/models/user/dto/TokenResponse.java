package com.elksandro.seunegocio.models.user.dto;

public record TokenResponse(
    String token,
    long expiresIn
) {}