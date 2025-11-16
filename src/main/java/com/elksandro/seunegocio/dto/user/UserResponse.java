package com.elksandro.seunegocio.dto.user;

public record UserResponse(
    Long id,
    String name,
    String email,
    String whatsapp,
    String profilePictureUrl,
    String role
) {}