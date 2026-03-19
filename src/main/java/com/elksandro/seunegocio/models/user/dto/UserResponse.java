package com.elksandro.seunegocio.models.user.dto;

public record UserResponse(
    Long id,
    String name,
    String email,
    String whatsapp,
    String profilePictureUrl,
    String role
) {}